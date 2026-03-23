package ie.ucd.market.entities;

import jakarta.persistence.*;

/* 
 * This class defines a Product object to be saved as a table in the database.
 */

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable=false)
    private long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false) 
    private String price;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private boolean visible;


    // Getters
    public long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getPrice() {
        return this.price;
    }

    public String getDescription() {
        return this.description;
    }

    public boolean getVisible() {
        return this.visible;
    }

    // Setters
    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
