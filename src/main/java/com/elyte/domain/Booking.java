package com.elyte.domain;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.elyte.domain.request.Cart;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "BOOKINGS")
public class Booking extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "BOOKING_ID")
    private String oid;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "OWNER_ID")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private User user;

    @Column(name = "TOTAL_PRICE")
    @NotNull(message = "total price is required")
    private Double totalPrice;

    @Column(name = "SHIPPING_DETAILS", columnDefinition = "json")
    private String shippingDetails;

    @Column(name = "CART", columnDefinition = "json")
    private Cart cart;

}
