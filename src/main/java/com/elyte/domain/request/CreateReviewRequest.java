package com.elyte.domain.request;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class CreateReviewRequest {

    @NotNull(message = "product id is required")
    private String pid;

    @Email(message = "invalid email address")
    private String email;


    @NotBlank(message = "comment name is required")
    private String comment;

    @NotNull(message = "rating is required")
    private Integer rating;
  
}
