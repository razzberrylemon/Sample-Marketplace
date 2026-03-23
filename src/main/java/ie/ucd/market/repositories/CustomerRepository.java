package ie.ucd.market.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ie.ucd.market.entities.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    // defines a function to find a customer by their username
    public Customer findByUsername(String username);
}
