package com.elyte.repository;
import org.springframework.data.repository.CrudRepository;
import com.elyte.domain.Task;
import java.util.List;


public interface TaskRepository extends CrudRepository<Task, String>{
    List<Task> findByJobJid(String jid);
    
}
