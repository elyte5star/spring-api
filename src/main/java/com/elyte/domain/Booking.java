package com.elyte.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Entity
@AllArgsConstructor(staticName = "build")
@NoArgsConstructor
@Table(name = "BOOKINGS")
public class Booking extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "BOOKING_ID")
    private String oid;

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
