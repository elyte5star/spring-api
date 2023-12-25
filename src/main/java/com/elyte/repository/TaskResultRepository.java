package com.elyte.repository;
import org.springframework.data.repository.CrudRepository;
import com.elyte.queue.TaskResult;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskResultRepository extends CrudRepository<TaskResult,String>{
    
}
