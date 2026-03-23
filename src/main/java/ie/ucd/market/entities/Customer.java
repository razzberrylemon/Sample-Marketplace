package ie.ucd.market.entities;

import java.util.List;
import java.util.ArrayList;

import jakarta.persistence.*;

/* 
 * This class defines a Customer object to be saved as a table in the database.
 */

@Entity
@Table(name = "customers")
public class Customer {
        
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable=false)
    private long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    // CascadeType.ALL means anything that happens to a Customer will happen to all associated WebOrders too
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<WebOrder> orders = new ArrayList<>();

    // Getters
    public long getId() {
        return this.id;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public List<WebOrder> getOrders() {
        return this.orders;
    }

    // Setters
    public void setId(long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setOrders(List<WebOrder> orders) {
        this.orders = orders;
    }
}
