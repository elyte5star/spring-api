package com.elyte.domain;
import com.elyte.utils.Auditable;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import jakarta.persistence.*;

@Entity
@Data
public class Review extends Auditable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REVIEW_ID")
    private Long rid;

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
