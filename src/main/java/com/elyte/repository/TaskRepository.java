package com.elyte.repository;
import org.springframework.data.repository.CrudRepository;
import com.elyte.domain.Task;

public interface TaskRepository extends CrudRepository<Task, String>{
    
}
