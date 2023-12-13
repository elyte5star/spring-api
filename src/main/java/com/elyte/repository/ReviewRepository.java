package com.elyte.repository;
import com.elyte.domain.Review;
import org.springframework.data.repository.CrudRepository;
import java.util.List;
import jakarta.transaction.Transactional;

public interface ReviewRepository extends CrudRepository<Review,String> {

    List<Review> findByProductPid(String pid);

    @Transactional
    void deleteByProductPid(String pid);

}
