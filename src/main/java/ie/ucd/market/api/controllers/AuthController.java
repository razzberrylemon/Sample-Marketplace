package ie.ucd.market.api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ie.ucd.market.entities.Admin;
import ie.ucd.market.entities.Customer;
import ie.ucd.market.repositories.AdminRepository;
import ie.ucd.market.repositories.CustomerRepository;
import org.springframework.ui.Model;

import ie.ucd.market.service.AuthService;
import ie.ucd.market.service.OrderService;
import ie.ucd.market.service.ProductService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class AuthController {

    @Autowired
    private CustomerRepository repoC;

    @Autowired
    private AdminRepository repoA;

    @Autowired
    private ProductService productService;

    @Autowired
    private AuthService authService;

    @Autowired 
    private OrderService orderService;

    /* 
     * Defines the login functionality. 
     * A cookie value is used to save a reference to which user is logged in. Then an example matcher is defined that 
     * will check for username and password in the database. If it is a customer login, a new empty Order is made and 
     * the home page is displayed.
     * 
     * If an Admin logs in they will be authenticated through the database with the matcher and then the admin home page
     * is displayed.
     */
    @PostMapping("/login")
    public String login(@RequestParam String username, 
                        @RequestParam String password, 
                        @RequestParam String signtype, 
                        Model model, HttpServletResponse response) {

        Cookie cookie = new Cookie("currUsername", username);
        response.addCookie(cookie);

        // create matcher with criteria about what to look for
        // the criteria is the same for both Customer and Admin, so they can use the same one
        ExampleMatcher match = ExampleMatcher.matching().withIgnorePaths("id")
                             .withMatcher("username", ExampleMatcher.GenericPropertyMatchers.exact())
                             .withMatcher("password", ExampleMatcher.GenericPropertyMatchers.exact());


        if (signtype.equals("customer")) {
            // create instance of the Customer that we are searching for
            Customer c = authService.createCustomer(username, password);

            // create an Example object from the Customer instance
            Example<Customer> ex = Example.of(c, match);

            if (!repoC.exists(ex)) {
              model.addAttribute("error_message", "Incorrect Username or Password. Try again, or click Register to create an account.");
              return "auth/login";
            }

            Customer customer = authService.findCustomer(username);
            orderService.createWebOrder(customer);

            // display visible products!
            productService.getVisibleProducts(model);
                        
            // if the Example Customer exists, return success, if not return failure
            return "customer/customerHome";

        } else if (signtype.equals("admin")) {
            // create instance of the Admin that we are searching for
            Admin a = authService.createAdmin(username, password);

            // create an Example object from the Admin instance
            Example<Admin> ex = Example.of(a, match);

            if (!repoA.exists(ex)) {
              model.addAttribute("error_message", "Incorrect Username or Password. Try again, or click Register to create an account.");
              return "auth/login";
            }


            // display all products!
            productService.getAllProducts(model);

            // if the Example Admin exists, return success, if not return failure
            return "admin/adminHome";

        }

        return "failure";
    }

    // This returns the login page
    @GetMapping("/login")
    public String showLogin() {
        return "auth/login";
    }

    /*
     * This function is for user registration. It will only use the Matcher to match the username, as this 
     * should be a unique value, but passwords can be repeated. Again, a cookie value will be used 
     * to save the current user. If the username is not already registered, the customer or admin will be saved
     * in the database and the home pages will be saved.
     */
    @PostMapping("/register")
    public String register(@RequestParam String username, 
                           @RequestParam String password, 
                           @RequestParam String signtype, Model model, HttpServletResponse response) {

        ExampleMatcher match = ExampleMatcher.matching().withIgnorePaths("id", "password")
        .withMatcher("username", ExampleMatcher.GenericPropertyMatchers.exact());

        if (signtype.equals("customer")) {
          // make new Customer and set username and password according to the form
          Customer c = authService.createCustomer(username, password);

          Example<Customer> ex = Example.of(c, match);
          if (repoC.exists(ex) || username.equals("customerNotFound")) {
            model.addAttribute("error_message", "A user with this username already exists. Please try again.");
            return "auth/register";
          } else {
            Cookie cookie = new Cookie("currUsername", username);
            response.addCookie(cookie);

            // save the Customer in the database 
            authService.saveCustomer(c);

            orderService.createWebOrder(c);

            // display visible products!
            productService.getVisibleProducts(model);

            // redirect the user to the Customer Home page
            return "customer/customerHome";
          }

        } else if (signtype.equals("admin")) {
          // make new Admin and set username and password according to the form
          Admin a = authService.createAdmin(username, password);

          // save the Admin in the database
          authService.saveAdmin(a);

          // display all products!
          productService.getAllProducts(model);

          // redirect the user to the Admin Home page
          return "admin/adminHome";
        }

        return "failure";
    }

    // this returns the Register page
    @GetMapping("/register")
    public String showRegister() {
      return "auth/register";
    }

    // ---------- LOGOUT ----------

    /* 
     * This function defines what happens when a customer logs out. It finds the current customer and 
     * calls the function customerLogout from authService.
     */
    @GetMapping("/customer_logout")
    public String customerLogout(@CookieValue(value="currUsername", defaultValue = "customerNotFound") String currUsername) {
      if (currUsername.equals("customerNotFound")) {
        return "failure";
      }

      Customer customer = authService.findCustomer(currUsername);
      authService.customerLogout(customer);

      return "index";
    }

    // This returns the welcome page
    @GetMapping(value = {"/purchase_logout", "/index", "/admin_logout"}) 
    public String purchaseLogout() {
      return "index";
    }
}
