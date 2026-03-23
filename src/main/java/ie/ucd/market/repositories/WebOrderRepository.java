package ie.ucd.market.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import ie.ucd.market.entities.WebOrder;
import ie.ucd.market.entities.Customer;

public interface WebOrderRepository extends JpaRepository<WebOrder, Long> {
    // defines a function to find a web order by a specific customer
    public List<WebOrder> findByCustomer(Customer customer);
}
