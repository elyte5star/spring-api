package com.elyte.domain.Payment;
import lombok.Data;
import java.io.Serializable;

import com.elyte.domain.enums.Currency;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class CardDetails implements Serializable{
    private static final long serialVersionUID = 1234567L;  
    private String cardType;
    private String cardNumber;
    private String expiryDate;
    private String cardCvv;
    private String nameOnCard;
    private Currency currency;
    
}
