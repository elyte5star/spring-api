package com.elyte.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OrderBy;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "PRODUCTS")
public class Product extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "PRODUCT_ID")
    private String pid;

    @Column(name = "NAME", unique = true)
    @NotBlank(message = "name is required")
    private String name;

    @Column(name = "IMAGE_NAME")
    @NotBlank(message = "image name is required")
    private String image;

    @Column(name = "DETAILS", length = 3000)
    @NotBlank(message = "product details name is required")
    private String details;


    @Column(name = "DESCRIPTION", length = 3000)
    @NotBlank(message = "product description name is required")
    private String description;

    @Column(name = "CATEGORY")
    @NotBlank(message = "category name is required")
    private String category;

    @Column(name = "PRICE")
    @NotNull(message = "price is required")
    private Double price;

    @Column(name = "STOCK_QUANTITY")
    @NotNull(message = "quantity is required")
    private Integer stock_quantity;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "product")
    @OrderBy
    private List<Review> reviews;

}
