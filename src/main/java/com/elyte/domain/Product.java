package com.elyte.domain;

import lombok.Data;
import jakarta.persistence.*;
import java.util.List;
import com.elyte.utils.Auditable;

@Entity
@Data
public class Product extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PRODUCT_ID")
    private Long pid;


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

    @Column(name = "STOCK_QUANTITY")
    private Double stockQuantity;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "REVIEW_ID")
    @OrderBy("CREATED_AT")
    private List<Review> reviews;

    

}
