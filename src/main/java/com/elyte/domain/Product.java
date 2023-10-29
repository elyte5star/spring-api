package com.elyte.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.FetchType;
import jakarta.persistence.GenerationType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.CascadeType;
import java.sql.Timestamp;
import java.util.List;

@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PRODUCT_ID")
    private Long pid;

    @Column(name = "CREATED_AT")
    private Timestamp createdAt;

    @Column(name = "NAME")
    private String name;

    @Column(name = "IMAGE")
    private String image;

    @Column(name = "DETAILS")
    private String details;

    @Column(name = "CATEGORY")
    private String category;

    @Column(name = "PRICE")
    private Double price;

    public void setPid(Long pid) {
        this.pid = pid;
    }

    @Column(name = "STOCK_QUANTITY")
    private Double stockQuantity;

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Double stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "REVIEW_ID")
    @OrderBy("CREATED_ON")
    private List<Review> reviews;

    public void addReview(Review review) {
        this.reviews.add(review);
    }

    public List<Review> getReviews() {
        return reviews;
    }
}
