package com.elyte.domain.request;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serial;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ItemInCart implements Serializable{

    @Serial
    private static final long serialVersionUID = 1234567L;

    @NotBlank(message = "pid is required")
    private String pid;

    @NotBlank(message = "name is required")
    private String name;

    @NotBlank(message = "image name is required")
    private String image;

    @NotBlank(message = "product details name is required")
    private String details;

    @NotBlank(message = "product description name is required")
    private String description;

    @NotBlank(message = "category name is required")
    private String category;

    @NotNull(message = "price is required")
    private Double price;

    @NotNull(message = "stock quantity is required")
    private Integer stockQuantity;

    @Digits(integer = 10, fraction = 2)
    private String productDiscount=null;

    @NotNull(message = "quantity is required")
    private int quantity;

    private Double calculatedPrice;
    
}
