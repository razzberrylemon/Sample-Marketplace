package ie.ucd.market.api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ie.ucd.market.entities.Product;
import ie.ucd.market.service.ProductService;

@Controller
public class ProductController {
    @Autowired
    private ProductService productService;

    // this returns the admin home page
    @GetMapping("/adminHome")
    public String getAdminHome(Model model) {
        productService.getAllProducts(model);

        return "admin/adminHome";
    }

    // this returns the customer home page
    @GetMapping("/customerHome")
    public String getCustomerHome(Model model) {
        productService.getVisibleProducts(model);
        return "customer/customerHome";
    }

    /*
     * This function creates a new product from the product creation form input. It uses createProduct,
     * addProduct, and getAllProducts from the productService before returning the admin home page.
     */
    @GetMapping("/product_creation")
    public String createProduct(@RequestParam String name, @RequestParam String price, @RequestParam String description, @RequestParam String visible, Model model) {
        Product p = productService.createProduct(name, price, description, visible);
        productService.addProduct(p);
        productService.getAllProducts(model);
        return "admin/adminHome";
    }

    /*
     * This function finds the product requested for details and adds it to the model before returning the details page.
     */
    @GetMapping("product_details")
    public String displayDetails(@RequestParam long product_id, Model model) {
        Product p = productService.getProductById(product_id);
        model.addAttribute("displayProduct", p);
        return "customer/productDetails";
    }

    /*
     * This function finds the product requested for editing and adds it to the model before returning the edit page.
     */
    @GetMapping("/edit_product")
    public String editProduct(@RequestParam long product_id, Model model){
        Product p = productService.getProductById(product_id);
        model.addAttribute("editingProduct", p);
        return "admin/editProduct";
    }

    /*
     * This function is called when a product is edited and "Save" is clicked. It takes the price value and formats 
     * it to be money, then sets all the values and saves it. A list of all products is added to the model before returning
     * the admin home page. This will display the saved updates.
     */
    @PostMapping("/save_product")
    public String saveEdits(@RequestParam long product_id, @RequestParam String price, @RequestParam String description, @RequestParam String visible, Model model) {
        Product p = productService.getProductById(product_id);

        Double d = Double.valueOf(price);
        String priceString = String.format("%.2f", d);

        p.setPrice(priceString);
        p.setDescription(description);
        boolean visibleValue = true;
        if (visible.equals("false")) {
            visibleValue = false;
        }
        p.setVisible(visibleValue);

        productService.addProduct(p);
        productService.getAllProducts(model);

        return "admin/adminHome";
    }
}
