package com.elyte.repository;

import com.elyte.domain.Booking;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BookingRepository extends CrudRepository<Booking, String> {

    List<Booking> findByUserUserid(String userid);


}