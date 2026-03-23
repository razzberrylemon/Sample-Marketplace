package ie.ucd.market.entities;

import java.util.List;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import java.util.ArrayList;

import jakarta.persistence.*;

/* 
 * This class defines an WebOrder object to be saved as a table in the database.
 */

@Entity
@Table(name = "orders")
public class WebOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable=false)
    private long id;

    @Column(nullable = false)
    private String totalCost;

    @Column
    private String status;

    @ManyToOne
    @JoinColumn(name = "customerId")
    private Customer customer;

    @OneToMany(mappedBy = "order")
    @Cascade(CascadeType.ALL)
    private List<WebOrderQuantity> quantities = new ArrayList<>();


    // Getters
    public long getId() {
        return this.id;
    }

    public String getTotalCost() {
        return this.totalCost;
    }

    public String getStatus() {
        return this.status;
    }

    public Customer getCustomer() {
        return this.customer;
    }

    public List<WebOrderQuantity> getQuantities() {
        return this.quantities;
    }

    // Setters
    public void setId(long id) {
        this.id = id;
    }

    public void setTotalCost(String totalCost) {
        this.totalCost = totalCost;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void setQuantities(List<WebOrderQuantity> quantities) {
        this.quantities = quantities;
    } 
}
