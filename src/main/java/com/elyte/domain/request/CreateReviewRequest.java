package com.elyte.domain.request;
import java.io.Serial;
import java.io.Serializable;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class CreateReviewRequest implements Serializable{

    @Serial
    private static final long serialVersionUID = 1234567L;

    @NotNull(message = "product id is required")
    private String pid;

    @Email(message = "invalid email address")
    private String email;


    @NotBlank(message = "comment name is required")
    private String comment;

    @NotBlank(message = "comment name is required")
    private String reviewerName;

    @NotNull(message = "rating is required")
    @Min(1)
    @Max(5)
    private Integer rating;
  
}
