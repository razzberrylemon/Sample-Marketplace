package ie.ucd.market.entities;

import jakarta.persistence.*;

/* 
 * This class defines a WebOrderQuantity object to be saved as a table in the database.
 */

@Entity
@Table(name = "quantities")
public class WebOrderQuantity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable=false)
    private long id; 

    @ManyToOne
    @JoinColumn(name = "productId")
    private Product product;

    @Column(nullable = false)
    private int quantity;

    @ManyToOne
    @JoinColumn(name = "orderId") 
    private WebOrder order;

    // Getters
    public long getId() {
        return this.id;
    }

    public Product getProduct() {
        return this.product;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public WebOrder getOrder() {
        return this.order;
    }
 
    // Setters
    public void setId(long id) {
        this.id = id;
    }
    
    public void setProduct(Product product) {
        this.product = product;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setOrder(WebOrder order) {
        this.order = order;
    }
}
