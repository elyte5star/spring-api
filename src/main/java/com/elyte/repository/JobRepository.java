package com.elyte.repository;
import com.elyte.domain.Job;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface JobRepository extends CrudRepository<Job, String>{

    List<Job> findByUserUserid(String userid);
    
}
