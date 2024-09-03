package ru.fsdstudio.product.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Book extends Product {
    @Column(name = "author")
    private String author;
    
    public Book() {}
    
    public Book(Stock stock, String author) {
        super(stock);
        this.author = author;
    }
    
    @Override
    public String getProductName() {
        return this.getName();
    }

    @Override
    public String getProductDescription() {
        return this.getDescription();
    }
}
