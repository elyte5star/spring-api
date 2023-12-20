package com.elyte.domain.request;
import java.io.Serializable;

//import com.elyte.domain.Payment.Payment;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateBooking implements Serializable{

    private static final long serialVersionUID = 1234567L;

    @NotNull(message = "userid is required")
    private String userid;

    @NotNull(message = "total price is required")
    private Double totalPrice;

    //private Cart cart;

    //private Payment paymentDetails;
    
}
