package ie.ucd.market.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ie.ucd.market.entities.Customer;
import ie.ucd.market.entities.WebOrder;
import ie.ucd.market.entities.WebOrderQuantity;
import ie.ucd.market.repositories.CustomerRepository;
import ie.ucd.market.repositories.WebOrderQuantityRepository;
import ie.ucd.market.repositories.WebOrderRepository;

@Service
public class OrderService {
    @Autowired 
    private CustomerRepository customerRepo;

    @Autowired
    private WebOrderQuantityRepository quantityRepo;

    @Autowired
    private WebOrderRepository orderRepo;

    // make new WebOrder and save it in the database
    public void createWebOrder(Customer customer) {
        WebOrder newOrder = new WebOrder();

        newOrder.setCustomer(customer);

        List<WebOrderQuantity> quantities = new ArrayList<>();
        newOrder.setQuantities(quantities);

        newOrder.setStatus("unordered");

        newOrder.setTotalCost("0.00");

        // save the new order... 
        orderRepo.save(newOrder);

        List<WebOrder> orders = customer.getOrders();
        orders.add(newOrder);
        customerRepo.save(customer);
    }

    // add a new order to the given list
    public List<WebOrder> addWebOrder(List<WebOrder> orders, WebOrder order) {
        orders.add(order);
        return orders;
    }

    // save the given order in the database
    public void saveWebOrder(WebOrder order) {
        orderRepo.save(order);
    }

    // save the given quantity in the database
    public void saveQuantity(WebOrderQuantity quantity) {
        quantityRepo.save(quantity);
    }

    // calculate the total cost of an order and format it to two decimal places (money)
    public String calculateTotalCost(List<WebOrderQuantity> quantities) {
        double total = 0;

        for (WebOrderQuantity quantity : quantities) {
            Double price = Double.valueOf(quantity.getProduct().getPrice());
            total += (quantity.getQuantity() * price);
        }

        String totalString = String.format("%.2f", total);
        return totalString;
    }

    // increase the quantity of the given quantity
    public void incrementQuantity(WebOrderQuantity quantity) {
        quantity.setQuantity(quantity.getQuantity() + 1);
        quantityRepo.save(quantity);
    }

    // decrease the quantity of the given quantity
    public void decrementQuantity(WebOrderQuantity quantity) {
        quantity.setQuantity(quantity.getQuantity() - 1);
        quantityRepo.save(quantity);
    }

    // return the list of orders for a given customer 
    public List<WebOrder> getOrdersList(Customer customer) {
        return customer.getOrders();
    }

    // return the current order for a given customer
    public WebOrder getCurrentOrder(Customer customer) {
        List<WebOrder> ordersList = getOrdersList(customer);
        return ordersList.get(ordersList.size() - 1);
    }

    // return the list of quantities for a given customer
    public List<WebOrderQuantity> getOrderQuantities(Customer customer) {
        WebOrder order = getCurrentOrder(customer);
        return order.getQuantities();
    }

    // delete a given order from the database 
    public void deleteOrder(WebOrder order) {
        orderRepo.delete(order);
    }

    // delete a given quantity from the database
    public void deleteQuantity(WebOrderQuantity quantity) {
        quantityRepo.delete(quantity);
    }
}
