package com.elyte.domain.response;

import java.util.List;
import com.elyte.domain.Review;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductResponse {
    private String pid;
    private String name;
    private String description;
    private String category;
    private Double price;
    private Integer stockQuantity;
    private List<Review> reviews;
    private String image;
    private String details;
    
}
