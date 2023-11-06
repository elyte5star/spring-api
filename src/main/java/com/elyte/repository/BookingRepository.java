package com.elyte.repository;

import com.elyte.domain.Booking;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface BookingRepository extends CrudRepository<Booking, UUID> {
}