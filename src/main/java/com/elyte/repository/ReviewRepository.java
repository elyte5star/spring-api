package com.elyte.repository;
import com.elyte.domain.Review;
import org.springframework.data.repository.CrudRepository;
import java.util.UUID;


public interface ReviewRepository extends CrudRepository<Review,UUID> {

}
