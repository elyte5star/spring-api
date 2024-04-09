package com.elyte.domain.request;
import java.math.BigDecimal;
import com.elyte.domain.Payment.BillingAddress;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BookingJob {

    private String userid;
    
    private BigDecimal totalPrice;

    private Cart cart;

    private BillingAddress shippingAddress;
    
}
