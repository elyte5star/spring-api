package com.elyte.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@Entity
@AllArgsConstructor(staticName = "build")
@NoArgsConstructor
@Data
@Table(name="PRODUCTS")
public class Product extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "PRODUCT_ID")
    private UUID pid;

    @Column(name = "NAME")
    @NotBlank(message = "name is required")
    private String name;

    @Column(name = "IMAGE_NAME")
    @NotBlank(message = "image name is required")
    private String image;

    @Column(name = "DETAILS",length = 3000)
    @NotBlank(message = "product description name is required")
    private String details;

    @Column(name = "CATEGORY")
    @NotBlank(message = "category name is required")
    private String category;

    @Column(name = "PRICE")
    @NotNull(message = "price is required")
    private Double price;

    @Column(name = "STOCK_QUANTITY")
    @NotNull(message = "quantity is required")
    private Integer stock_quantity;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "REVIEW_ID")
    @OrderBy("CREATED_AT")
    private List<Review> reviews;

    

}
