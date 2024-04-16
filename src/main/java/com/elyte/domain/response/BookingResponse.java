package com.elyte.domain.response;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.io.Serial;
import com.elyte.domain.Payment.ShippingDetails;
import com.elyte.domain.request.ItemInCart;
import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BookingResponse {
    
    @Serial
    private static final long serialVersionUID = 1234567L;

    private String oid;
    private String userid;
    private BigDecimal totalPrice;
    private String created; 
    private List<ItemInCart> cart;
    private ShippingDetails shippingAddress;
    
}
