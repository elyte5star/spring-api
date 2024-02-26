package com.elyte.controllers;
import com.elyte.domain.request.CreateBooking;
import com.elyte.domain.response.CustomResponseStatus;
import com.elyte.exception.ResourceNotFoundException;
import com.elyte.service.BookingService;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/qbooking")
public class BookingController {


    @Autowired
    private BookingService bookingService;


    @PostMapping("/create")
    @Operation(summary = "Create booking ", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CustomResponseStatus> createUser(@RequestBody @Valid CreateBooking createBooking,
            final Locale locale) throws Exception {
        return bookingService.createBookingQ(createBooking);
    }

    @GetMapping("/{userid}/bookings")
    @Operation(summary = "Get user bookings of by userid", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CustomResponseStatus> getAllBookingsByUserId(@PathVariable  @Valid String userid) throws ResourceNotFoundException {
        return bookingService.bookingsByUserid(userid);

    }

    @GetMapping("/result/{jid}")
    @Operation(summary = "Get  booking result by jid", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CustomResponseStatus> getBookingResultByJib(@PathVariable  @Valid String jid) throws ResourceNotFoundException, JsonParseException, JsonMappingException, IOException {
        return bookingService.bookingResultByJid(jid);

    }

}
