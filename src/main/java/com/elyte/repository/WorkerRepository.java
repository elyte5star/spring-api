package com.elyte.repository;
import org.springframework.stereotype.Repository;
import com.elyte.domain.Worker;
import org.springframework.data.repository.CrudRepository;

@Repository
public interface WorkerRepository  extends CrudRepository<Worker, String>{
    
}
