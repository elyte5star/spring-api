package com.elyte.domain;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.*;


@Entity
@AllArgsConstructor(staticName = "build")
@NoArgsConstructor
@Getter
@Setter
@Table(name="REVIEWS")
public class Review extends AuditEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "REVIEW_ID", unique = true)
    private String rid;

    @Column(name = "EMAIL")
    @Email(message = "invalid email address")
    private String email;

   
    @Column(name ="PRODUCT_ID", updatable = false)
    @NotNull(message = "product id is required")
    private String product_id;

    @Column(name = "COMMENT", length = 5000)
    @NotBlank(message = "comment name is required")
    private String comment;

    @Column(name = "RATING")
    @NotNull(message = "rating is required")
    private Integer rating;

    
}
