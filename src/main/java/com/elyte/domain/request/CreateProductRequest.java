package com.elyte.domain.request;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;





@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateProductRequest implements Serializable{

    @Serial
    private static final long serialVersionUID = 1234567L;

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

    @NotNull(message = "quantity is required")
    private Integer stock_quantity;

    
}
