package ie.ucd.market.api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ie.ucd.market.entities.Customer;
import ie.ucd.market.entities.Product;
import ie.ucd.market.entities.WebOrder;
import ie.ucd.market.entities.WebOrderQuantity;
import ie.ucd.market.repositories.WebOrderRepository;
import ie.ucd.market.service.ProductService;
import ie.ucd.market.service.AuthService;
import ie.ucd.market.service.OrderService;

import java.util.List;

@Controller
public class PurchaseController {
    @Autowired
    private ProductService productService;

    @Autowired
    private AuthService authService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private WebOrderRepository orderRepo;

    /*
     * This function finds the current customer with the cookie value, then adds the selected item to their current Order
     * which will always be the last in the list. If the item is already in the cart, the amount of that item is 
     * incremented by 1 in the "WebOrderQuantity" for that product, and if not then a new Quantity is created and stored for it.
     * Then a list of visible products is gotten and added to the model, and the customer home page is returned.
     */
    @GetMapping("/add_to_cart")
    public String addToCart(@CookieValue(value="currUsername", defaultValue = "customerNotFound") String currUsername, @RequestParam long product_id, Model model) {
        if (currUsername.equals("customerNotFound")) {
            return "failure";
        }
        
        Customer customer = authService.findCustomer(currUsername);

        List<WebOrder> orders = customer.getOrders();
        WebOrder currOrder = orders.get(orders.size() - 1);

        List<WebOrderQuantity> quantities = currOrder.getQuantities();

        Product product = productService.getProductById(product_id);

        boolean added = false; 
        for (int i = 0; i < quantities.size() && !added; i++) {
            WebOrderQuantity curr = quantities.get(i);
            // if the product already exists in the list, just increment the quantity
            if (curr.getProduct().getId() == product_id) {
                added = true;
                orderService.incrementQuantity(curr);
                orderService.saveQuantity(curr);
            }
        }

        if (!added) {
            WebOrderQuantity newQuantity = new WebOrderQuantity();
            newQuantity.setProduct(product);
            newQuantity.setQuantity(1);
            newQuantity.setOrder(currOrder);

            orderService.saveQuantity(newQuantity);

            quantities.add(newQuantity);
        }

        productService.getVisibleProducts(model);
        return "customer/customerHome";
    }

    /*
     * This function finds the current customer, gets their current order and the quantities of the items in
     * the order. Then the total cost is calculated and set for the current order before the cart is returned.
     */
    @GetMapping("/cart")
    public String viewCart(@CookieValue(value="currUsername", defaultValue = "customerNotFound") String currUsername, Model model) {
        if (currUsername.equals("customerNotFound")) {
            return "failure";
        }
        
        Customer customer = authService.findCustomer(currUsername);
        WebOrder order = orderService.getCurrentOrder(customer);

        List<WebOrderQuantity> quantities = order.getQuantities();

        String totalCost = orderService.calculateTotalCost(quantities);
        order.setTotalCost(totalCost);
        orderService.saveWebOrder(order);

        model.addAttribute("order", order);

        return "customer/cart";
    }

    /*
     * This function is called when the customer incremented or decrements a product from the cart.
     * First the quantity of the product being changed is gotten using the product_id, while finding the index of 
     * this product in the list. If the quantity ever gets to 0 the product is automatically removed.
     */
    @GetMapping("/cartUpdate")
    public String cartUpdate(@RequestParam boolean inc, @RequestParam long product_id, @CookieValue(value = "currUsername", defaultValue="customerNotFound") String currUsername, Model model) {
        if(currUsername.equals("customerNotFound")) {
            return "failure";
        }

        Customer customer = authService.findCustomer(currUsername);
        List<WebOrderQuantity> quantities = orderService.getOrderQuantities(customer);

        boolean incremented = false;
        int i = -1;
        while (i < quantities.size() && !incremented) {
            i++;
            WebOrderQuantity curr = quantities.get(i);
            if (curr.getProduct().getId() == product_id) {
                if (inc) {
                    orderService.incrementQuantity(curr);
                } else {
                    orderService.decrementQuantity(curr);
                }
                incremented = true;
            }
        }

        WebOrderQuantity changed = quantities.get(i);
        if (changed.getQuantity() <= 0) {
            // remove the quantity from the list
            quantities.remove(i);
            orderService.deleteQuantity(changed);
        }

        WebOrder order = orderService.getCurrentOrder(customer);
        order.setTotalCost(orderService.calculateTotalCost(quantities));
        orderService.saveWebOrder(order);

        model.addAttribute("order", order);

        return "customer/cart";
    }

    /*
     * This function finds the current user and order. It also gets the current cost of the order. If the cost is 0 
     * then return an error message saying that you must add things to the order to make a purchase. Otherwise, 
     * set the status of the order to "Processing", save it, and add it to the model before returning the purchase page.
     */
    @GetMapping("/purchase")
    public String purchase(@CookieValue(value="currUsername", defaultValue = "customerNotFound") String currUsername, Model model) {
        if (currUsername.equals("customerNotFound")) {
            return "failure";
        }

        Customer customer = authService.findCustomer(currUsername);
        WebOrder order = orderService.getCurrentOrder(customer);
        Double cost = Double.valueOf(order.getTotalCost());

        if (cost <= 0) {
            model.addAttribute("errorMessage", "Please Add Products To Your Cart to Make a Purchase");
            model.addAttribute("order", order);
            return "customer/cart";
        }

        order.setStatus("Processing");
        orderService.saveWebOrder(order);

        model.addAttribute("order", order);

        return "customer/purchase";
    }

    /*
     * This function is called if an item is being removed from the cart. First, the current customer and their
     * current order is found, then the product being removed is found via its id while also finding its index in the 
     * list of quantities. Then this product is removed from the list and deleted from the quantities in the database. 
     * Everything is saved, the order is added to the model, and the cart is returned.
     */
    @GetMapping("/remove")
    public String removeFromCart(@CookieValue(value = "currUsername", defaultValue = "customerNotFound") String currUsername, @RequestParam long product_id, Model model) {
         if (currUsername.equals("customerNotFound")) {
            return "failure";
         }

         Customer customer = authService.findCustomer(currUsername);
         WebOrder order = orderService.getCurrentOrder(customer);
         List<WebOrderQuantity> quantities = orderService.getOrderQuantities(customer);

         int i = -1;
         boolean found = false;
         while (i < quantities.size() && !found) {
            i++;
            WebOrderQuantity curr = quantities.get(i);
            if (curr.getProduct().getId() == product_id) {
                found = true;
            }
         }
         WebOrderQuantity toDelete = quantities.get(i);
         quantities.remove(i);
         order.setTotalCost(orderService.calculateTotalCost(quantities));
         orderService.saveWebOrder(order);

         orderService.deleteQuantity(toDelete);

         model.addAttribute("order", order);
         return "customer/cart";
    }

    /*
     * This function is called when the customer views their orders. First the current customer is found, then
     * their list of orders is found. The current order is removed from the list so that it is not displayed,
     * then the orders list is added to the model, and the viewOrders page is displayed.
     */
    @GetMapping("/view_customer_orders")
    public String viewCustomerOrders(@CookieValue(value="currUsername", defaultValue = "customerNotFound") String currUsername, Model model) {
        if (currUsername.equals("customerNotFound")) {
            return "failure";
        }

        Customer customer = authService.findCustomer(currUsername);
        List<WebOrder> orders = orderRepo.findByCustomer(customer);
        orders.remove(orders.size() - 1);

        model.addAttribute("orders", orders);

        return "customer/viewOrders";
    }

    // This function finds all the orders and returns the displayOrders page
    @GetMapping("/display_orders")
    public String displayOrders(Model model) {
        List<WebOrder> orders = orderRepo.findAll();
        model.addAttribute("orders", orders);
        return "admin/displayOrders";

    }

    // This function finds the order being edited and returns the editOrder page
    @GetMapping("/edit_order")
    public String editOrder(@RequestParam long order_id, Model model) {
        WebOrder order = orderRepo.findById(order_id).get();
        model.addAttribute("order", order);
        return "admin/editOrder";
    } 

    /*
     * This function finds the current order by its id, then sets its status, saves it, adds it to the
     * model and returns the displayOrders page.
     */
    @PostMapping("/save_order")
    public String saveOrder(@RequestParam long order_id, @RequestParam String status, Model model) {
        WebOrder order = orderRepo.findById(order_id).get();
        order.setStatus(status);
        orderService.saveWebOrder(order);

        List<WebOrder> orders = orderRepo.findAll();

        model.addAttribute("orders", orders);
        return "admin/displayOrders";
    }

    // This function allows customers to continue shopping after they make a purchase or view their orders.
    @GetMapping("/continue_shopping")
    public String continueShopping(@CookieValue(value="currUsername", defaultValue="customerNotFound") String currUsername, Model model) {
        if (currUsername.equals("customerNotFound")) {
            return "failure";
        }
        Customer customer = authService.findCustomer(currUsername);
        orderService.createWebOrder(customer);

        productService.getVisibleProducts(model);

        return "customer/customerHome";
    }
}

