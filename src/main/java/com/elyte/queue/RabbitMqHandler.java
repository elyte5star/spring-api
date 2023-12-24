package com.elyte.queue;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import com.elyte.domain.Job;
import com.elyte.domain.Task;
import com.elyte.domain.User;
import com.elyte.domain.enums.State;
import com.elyte.domain.enums.Status;
import com.elyte.domain.enums.JobType;
import com.elyte.exception.ResourceNotFoundException;
import com.elyte.repository.JobRepository;
import com.elyte.repository.TaskRepository;
import com.elyte.repository.UserRepository;
import com.elyte.utils.ApplicationConsts;

import org.springframework.stereotype.Service;

@Service
public class RabbitMqHandler {

    @Autowired
    private JobRepository jobRepository;
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${spring.rabbitmq.exchange}")
    private String exchange;

    @Value("${spring.rabbitmq.auto-config.bindings.binding-two.routing-key}")
    private String bookingRoutingkey;

    private static final Logger log = LoggerFactory.getLogger(RabbitMqHandler.class);

    public Job createJob(JobType jobType) throws Exception{
        Job job = new Job();
        job.setJobType(jobType);
        job.setCreated(ApplicationConsts.timeNow());
        job.setJobStatus(new Status(State.PENDING, false));
        return job;
    }

    public Map<String, Object> jobWithOneTask(Job job, String queueName) throws Exception {
        Task task = new Task();
        Status jobStatus = new Status(State.RECEIVED, false);
        task.setCreated(ApplicationConsts.timeNow());
        task.setTaskStatus(jobStatus);
        job.setJobStatus(jobStatus);
        task.setJob(job);
        return addJobAndTasksToDbAndQueue(job, List.of(task),List.of(new QueueItem(job,task)), queueName);

    }

    public Map<String, Object> addJobAndTasksToDbAndQueue(Job job, List<Task> tasks,List<QueueItem> queueItems, String queueName)
            throws Exception {
        try {
           
            job.setNumberOfTasks(tasks.size());
            job = jobRepository.save(job);
            taskRepository.saveAll(tasks);
            queueItems.forEach((queueItem) -> rabbitTemplate.convertAndSend(exchange, bookingRoutingkey, queueItem));
            return Map.of("success", true, "message", "Job with id : " + job.getJid() + " was created!");

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Map.of("success", false, "message", e.getMessage());

        }

    }

    public List<Task> getTasksByJobId(String jid) throws ResourceNotFoundException{
        Optional<Job> job = jobRepository.findById(jid);
        if (job.isPresent()){
            return taskRepository.findByJobJid(jid);
        }
        throw new ResourceNotFoundException("Job with id : " + jid +" not found!");

    }

    public Job getJob(String jid){
        Optional<Job> job = jobRepository.findById(jid);
         if (job.isPresent()){
            return job.get();
         }
         throw new ResourceNotFoundException("Job with id : " + jid +" not found!");
    }

    public Task getTask(String tid){
        Optional<Task> task = taskRepository.findById(tid);
         if (task.isPresent()){
            return task.get();
         }
         throw new ResourceNotFoundException("Task with id : " + tid +" not found!");
    }



    public List<Job> getJobsByUserid(String userid){
        Optional<User> user = userRepository.findById(userid);
        if(user.isPresent()){
            return jobRepository.findByUserUserid(userid);
        }
        throw new ResourceNotFoundException("User with id : " + userid +" not found!");

    }
}
