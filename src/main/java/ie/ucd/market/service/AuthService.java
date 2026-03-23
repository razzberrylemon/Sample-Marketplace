package ie.ucd.market.service;

import java.util.List;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ie.ucd.market.entities.Admin;
import ie.ucd.market.entities.Customer;
import ie.ucd.market.entities.WebOrder;
import ie.ucd.market.repositories.AdminRepository;
import ie.ucd.market.repositories.CustomerRepository;

@Service
public class AuthService {

    @Autowired
    private OrderService orderService;
    
    // Admin Services 

    @Autowired
    private AdminRepository adminRepo;

    // save admin in the database
    public void saveAdmin(Admin admin) {
        adminRepo.save(admin);
    }

    // create new admin object
    public Admin createAdmin(String username, String password) {
        Admin admin = new Admin();
        admin.setUsername(username);
        admin.setPassword(password);
        return admin;
    }

    // Customer Services

    @Autowired
    private CustomerRepository customerRepo;


    // save customer in the database
    public void saveCustomer(Customer customer) {
        customerRepo.save(customer);
    }

    // create new customer object
    public Customer createCustomer(String username, String password) {
        Customer c = new Customer();
        c.setUsername(username);
        c.setPassword(password);

        List<WebOrder> orders = new ArrayList<>();
        c.setOrders(orders);

        return c;
    }

    // find customer by username
    public Customer findCustomer(String username) {
        return customerRepo.findByUsername(username);
    }

    // logout a specific customer by removing the last order in the list and deleting it from the database
    public void customerLogout(Customer customer) {
        List<WebOrder> orders = customer.getOrders();
        WebOrder garbage = orders.get(orders.size() - 1);
        orders.remove(garbage);
        orderService.deleteOrder(garbage);
        saveCustomer(customer);
    }
}
