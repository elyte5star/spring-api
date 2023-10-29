package com.elyte.domain;

import jakarta.persistence.GenerationType;
import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import java.sql.Timestamp;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.validation.constraints.NotEmpty;
import jakarta.persistence.FetchType;

@Entity
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REVIEW_ID")
    private Long rid;

    @Column(name = "CREATED_AT")
    private Timestamp createdAt;

    @Column(name = "EMAIL")
    @NotEmpty
    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ITEM_ID", referencedColumnName = "PRODUCT_ID", unique = true, nullable = false, updatable = false)
    private Product product;

    @Column(name = "COMMENT")
    @NotEmpty
    private String comment;

    @Column(name = "RATING")
    @NotEmpty
    private Integer rating;

}
