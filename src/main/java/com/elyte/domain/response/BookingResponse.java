package com.elyte.domain.response;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.io.Serial;
import com.elyte.domain.Payment.BillingAddress;
import com.elyte.domain.request.ItemInCart;
import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BookingResponse {
    
    @Serial
    private static final long serialVersionUID = 1234567L;

    private String bid;
    private String userid;
    private BigDecimal totalPrice;
    private String createdAt; 
    private List<ItemInCart> cart;
    private BillingAddress shippingAddress;
    
}
