package com.elyte.domain;
import com.elyte.utils.Auditable;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@AllArgsConstructor(staticName = "build")
@NoArgsConstructor
@Data
public class Review extends Auditable{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "REVIEW_ID")
    private UUID rid;

    @Column(name = "EMAIL")
    @Email(message = "invalid email address")
    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ITEM_ID", referencedColumnName = "PRODUCT_ID", unique = true, nullable = false, updatable = false)
    private Product product;

    @Column(name = "COMMENT", length = 5000)
    @NotBlank(message = "comment name is required")
    private String comment;

    @Column(name = "RATING")
    @NotNull(message = "rating is required")
    private Integer rating;

    
}
