package com.elyte.queue;

import java.util.ArrayList;
import java.util.Collections;
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
import com.elyte.domain.response.JobAndTasksResult;
import com.elyte.domain.response.JobResponse;
import com.elyte.domain.enums.JobType;
import com.elyte.exception.ResourceNotFoundException;
import com.elyte.repository.JobRepository;
import com.elyte.repository.TaskRepository;
import com.elyte.repository.UserRepository;
import com.elyte.utils.UtilityFunctions;

import org.springframework.stereotype.Service;

@Service
public class RabbitMqHandler extends UtilityFunctions{

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

    private static final Logger log = LoggerFactory.getLogger(RabbitMqHandler.class);

    public Job createJob(JobType jobType) throws Exception {
        Job job = new Job();
        job.setJobType(jobType);
        job.setCreated(this.timeNow());
        job.setJobStatus(new Status(State.PENDING, false, false));
        return job;
    }

    public Map<String, Object> jobWithOneTask(Job job, String routingkey) throws Exception {
        Task task = new Task();
        Status jobStatus = new Status(State.RECEIVED, false, false);
        task.setCreated(this.timeNow());
        task.setTaskStatus(jobStatus);
        task.setJob(job);
        return addJobAndTasksToDbAndQueue(job, List.of(task), List.of(new QueueItem(job, task)), routingkey);

    }

    public Map<String, Object> addJobAndTasksToDbAndQueue(Job job, List<Task> tasks, List<QueueItem> queueItems,
            String routingkey)
            throws Exception {
        try {

            job.setNumberOfTasks(tasks.size());
            job = jobRepository.save(job);
            taskRepository.saveAll(tasks);
            queueItems.forEach((queueItem) -> rabbitTemplate.convertAndSend(exchange, routingkey, queueItem));
            return Map.of("success", true, "message", "Job with id : " + job.getJid() + " was created!");

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Map.of("success", false, "message", e.getMessage());

        }

    }

    public List<Task> getTasksByJobId(String jid) throws ResourceNotFoundException {
        Optional<Job> job = jobRepository.findById(jid);
        if (job.isPresent()) {
            return taskRepository.findByJobJid(jid);
        }
        throw new ResourceNotFoundException("Job with id : " + jid + " not found!");

    }

    public Job getJob(String jid) {
        Optional<Job> job = jobRepository.findById(jid);
        if (job.isPresent())
            return job.get();
        throw new ResourceNotFoundException("Job with id : " + jid + " not found!");

    }

    public JobResponse getJobResponse(String jid) {
        Optional<Job> job = jobRepository.findById(jid);
        if (job.isPresent()) {
            JobAndTasksResult jobAndTasksResult = this.checkJobAndTasks(job.get());
            if (this.resultAvailable(jobAndTasksResult.getJob())) {
                return this.createJobResponse(jobAndTasksResult.getJob(), jobAndTasksResult.getLastTaskEndedAt());
            } else {
                JobResponse jobResponse = new JobResponse(jobAndTasksResult.getJob().getUser().getUserid(),
                        jobAndTasksResult.getJob().getCreated(), jobAndTasksResult.getJob().getJid(),
                        jobAndTasksResult.getJob().getJobStatus());
                return jobResponse;
            }
        }
        throw new ResourceNotFoundException("Job with id : " + jid + " not found!");
    }

    public Task getTask(String tid) {
        Optional<Task> task = taskRepository.findById(tid);
        if (task.isPresent()) {
            return task.get();
        }
        throw new ResourceNotFoundException("Task with id : " + tid + " not found!");
    }

    public List<JobResponse> getJobsByUserid(String userid) {
        Optional<User> user = userRepository.findById(userid);
        List<JobResponse> jobResponses = new ArrayList<>();
        if (user.isPresent()) {
            List<Job> jobs = jobRepository.findByUserUserid(userid);
            for (Job job : jobs) {
                jobResponses.add(this.getJobResponse(job.getJid()));
            }
            return jobResponses;
        }
        throw new ResourceNotFoundException("User with id : " + userid + " not found!");

    }

    public JobAndTasksResult checkJobAndTasks(Job job) {
        List<Task> tasks = this.getTasksByJobId(job.getJid());
        if (tasks.isEmpty()) {
            job.setJobStatus(new Status(State.NOTASK, true, false));
            job = jobRepository.save(job);
            return new JobAndTasksResult(job, null, null);
        }
        List<State> states = new ArrayList<State>();
        List<String> endedAt = new ArrayList<String>();
        List<Boolean> successful = new ArrayList<Boolean>();

        for (Task task : tasks) {
            states.add(task.getTaskStatus().getState());
            endedAt.add(task.getEndedAt());
            successful.add(task.getTaskStatus().isSuccessful());
        }
        Collections.sort(endedAt);
        Status currentStatus = new Status(State.FINISHED, true, true);
        if (states.contains(State.TIMEOUT)) {
            currentStatus = new Status(State.TIMEOUT, true, false);

        } else if (states.contains(State.NOTSET)) {
            currentStatus = new Status(State.NOTSET, false, false);

        } else if (states.contains(State.RECEIVED)) {

            currentStatus = new Status(State.PENDING, false, false);

        } else if (states.contains(State.PENDING)) {
            currentStatus = new Status(State.PENDING, false, false);

        }

        job.setJobStatus(currentStatus);
        job = jobRepository.save(job);
        return new JobAndTasksResult(job, tasks, endedAt.get(endedAt.size() - 1));

    }

    public boolean resultAvailable(Job job) {
        if (Boolean.FALSE.equals(job.getJobStatus().isFinished()))
            return false;
        if (job.getJobStatus().getState() != State.FINISHED)
            return false;
        return job.getJobStatus().isSuccessful();
    }

    public JobResponse createJobResponse(Job job, String endedAt) {
        return new JobResponse(job.getUser().getUserid(), job.getCreated(), endedAt,
                this.diff(job.getCreated(), endedAt), job.getJid(), job.getJobStatus());
    }

}
