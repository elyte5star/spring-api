package com.elyte.repository;
import com.elyte.domain.Job;
import org.springframework.data.repository.CrudRepository;

public interface JobRepository extends CrudRepository<Job, String>{
    
}
