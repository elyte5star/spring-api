package com.elyte.domain.request;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.io.Serial;
import java.io.Serializable;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;





@AllArgsConstructor
@NoArgsConstructor
@Data
public class ModifyEntityRequest  implements Serializable{

    @Serial
    private static final long serialVersionUID = 1234567L;

    @Email(message = "invalid email address")
    private String email;

    @Digits(fraction = 0, integer = 10)
    private String telephone;

    @NotBlank(message = "fullname is required")
    private String fullName;

    @NotBlank(message = "address is required")
    private String streetAddress;

    @NotBlank(message = "Country is required")
    private String country;

    @NotBlank(message = "city name is required")
    private String city;

    @NotBlank(message = "State is required")
    private String state;

    @NotBlank(message = "Zip code is required")
    private String zip;

  
}
