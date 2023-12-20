package com.elyte.domain.Payment;

import java.io.Serializable;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;



@AllArgsConstructor
@NoArgsConstructor
@Data
public class Payment implements Serializable{

    private static final long serialVersionUID = 1234567L;

    private String cardType;
    private String cardNumber;
    private String expiryDate;
    private String cardCvv;
    private String nameOnCard;
    
    private BillingAddress billing_address;

    private  BillingAddress shippingAddress;
}
