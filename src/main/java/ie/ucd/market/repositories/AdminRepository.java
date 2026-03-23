package ie.ucd.market.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ie.ucd.market.entities.Admin;

public interface AdminRepository extends JpaRepository<Admin, Long> { 
}