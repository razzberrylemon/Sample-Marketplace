package ie.ucd.market.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ie.ucd.market.entities.Product;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long>{
    // defines a function to find a product by its visibility
    List<Product> findByVisible(boolean visible);
}