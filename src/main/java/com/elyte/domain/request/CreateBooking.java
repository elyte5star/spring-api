package com.elyte.domain.request;
import java.io.Serializable;
import java.math.BigDecimal;

import com.elyte.domain.Payment.Payment;

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
    private BigDecimal totalPrice;

    @NotNull(message = "cart cant be empty")
    private Cart cart;

    @NotNull(message = "Please provide payment details.")
    private Payment paymentDetails;
    
}
