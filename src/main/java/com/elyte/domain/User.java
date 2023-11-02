package com.elyte.domain;

import jakarta.persistence.*;
import java.sql.Timestamp;
import jakarta.validation.constraints.Digits;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    public User(String username, String password, String email, boolean active, boolean admin, Timestamp createdAt, String telephone) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.active = active;
        this.admin = admin;
        this.createdAt = createdAt;
        this.telephone = telephone;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "USER_ID")
    private Long id;

    @Column(name = "USERNAME")
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Column(name = "PASSWORD")
    private String password;

    public String getPassword() {
        return password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Column(name = "EMAIL")
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Column(name = "ACTIVE", columnDefinition = "BOOLEAN DEFAULT false")
    private boolean active;

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Column(name = "ADMIN", columnDefinition = "BOOLEAN DEFAULT false")
    private boolean admin;

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    @Column(name = "CREATED_AT")
    private Timestamp createdAt;

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Column(name = "TELEPHONE")
    @Digits(fraction = 0, integer = 10)
    private String telephone;

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "BOOKING_ID")
    @OrderBy
    private List<Booking> bookings;

    public List<Booking> getBookings() {
        return bookings;
    }

    public void addBooking(Booking booking) {
        this.bookings.add(booking);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", active=" + active +
                ", admin=" + admin +
                ", createdOn=" + createdAt +
                ", telephone='" + telephone + '\'' +
                ", bookings=" + bookings +
                '}';
    }
}
