package com.elyte.repository;
import org.springframework.data.repository.CrudRepository;
import com.elyte.domain.Booking;
import com.elyte.domain.ShippingAddress;



public interface ShippingRepository  extends CrudRepository<ShippingAddress,String>{
    
    ShippingAddress findByBooking(Booking booking);

    ShippingAddress findByShippingId(String shippingId);
    
}
