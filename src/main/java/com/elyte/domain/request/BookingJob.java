package com.elyte.domain.request;
import java.math.BigDecimal;
import com.elyte.domain.Payment.BillingAddress;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BookingJob {

    @NotNull(message = "userid is required")
    private String userid;
    
    @NotNull(message = "total price is required")
    private BigDecimal totalPrice;


    @NotNull(message = "total price is required")
    private Cart cart;


    @NotNull(message = "Please provide shipping details.")
    private BillingAddress shippingAddress;
    
}
