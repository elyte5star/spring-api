package com.elyte.domain.request;
import java.math.BigDecimal;
import com.elyte.domain.Payment.BillingAddress;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BookingJob {

    private String userid;
    
    private BigDecimal totalPrice;

    private List<ItemInCart> cart;

    private BillingAddress shippingAddress;
    
}
