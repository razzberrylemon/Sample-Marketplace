package ie.ucd.market.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ie.ucd.market.entities.Product;
import ie.ucd.market.repositories.ProductRepository;
import org.springframework.ui.Model;


@Service
public class ProductService {
    // this is where we will store code to get VisibleProducts and AllProducts
    @Autowired
    private ProductRepository repo;

    // get all products and add them to the model
    public void getAllProducts(Model model) {
        List<Product> productList = repo.findAll();
        model.addAttribute("allProducts", productList);
    }

    // get visible products and add them to the model
    public void getVisibleProducts(Model model) {
        List<Product> productList = repo.findByVisible(true);
        model.addAttribute("visibleProducts", productList);
    }

    // get a product by its id and unwrap it
    public Product getProductById(long id) {
        Product p = repo.findById(id).get();
        return p;
    }

    // add a product to the database
    public void addProduct(Product p) {
        repo.save(p);
    }

    // create a new product with the price formated to two decimal places (money)
    public Product createProduct(String name, String price, String description, String visible) {
        Double d = Double.valueOf(price);
        String priceValue = String.format("%.2f", d);

        boolean visibleValue = true;
        if (visible.equals("false")) {
            visibleValue = false;
        }

        Product p = new Product();
        p.setName(name);
        p.setPrice(priceValue);
        p.setDescription(description);
        p.setVisible(visibleValue);

        return p;
    }

}
