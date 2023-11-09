package com.elyte.domain;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

import com.elyte.utils.Auditable;

@Entity
@Data
@Table(name="BOOKINGS")
public class Booking extends Auditable{
   
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "BOOKING_ID")
    private UUID oid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OWNER_ID", referencedColumnName = "USER_ID")
    private User owner;

    
    @Column(name = "TOTAL_PRICE")
    private Double totalPrice;


    @Column(name = "SHIPPING_DETAILS", columnDefinition = "json")
    private String shippingDetails;

    @Column(name = "CART", columnDefinition = "json")
    private String cart;


    
    
}
