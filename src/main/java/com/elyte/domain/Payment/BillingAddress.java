package com.elyte.domain.Payment;

import java.io.Serializable;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class BillingAddress implements Serializable{

    private static final long serialVersionUID = 1234567L;

    private String fullName;
    private String streetAddress;
    private String country;
    private String email;
    private String state;
    private String zip;

    
}
