package ie.ucd.market.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import ie.ucd.market.entities.WebOrderQuantity;

public interface WebOrderQuantityRepository extends JpaRepository<WebOrderQuantity, Long> {
}
