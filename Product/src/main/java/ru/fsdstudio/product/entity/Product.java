package ru.fsdstudio.product.entity;

import jakarta.persistence.*;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import ru.fsdstudio.order.entity.OrderItem;

@Getter
@Setter
@Entity
@Table(name = "products")
public class Product implements ProductType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    
    @Column(name = "name")
    private String name;
    
    @Column(name = "description")
    private String description;
    
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<OrderItem> orderItems;
    
    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private Stock stock;
    
    public Product() {}
    
    public Product(Stock stock) {
        this.stock = stock;
    }
    
    @Override
    public String getProductName() {
        return this.name;
    }
    
    @Override
    public String getProductDescription() {
        return this.description;
    }
}
