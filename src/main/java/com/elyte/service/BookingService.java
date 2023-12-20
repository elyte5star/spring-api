package com.elyte.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.Optional;
import com.elyte.domain.Booking;
import com.elyte.domain.Job;
import com.elyte.domain.User;
import com.elyte.domain.enums.JobType;
import com.elyte.domain.request.CreateBooking;
import com.elyte.domain.response.CustomResponseStatus;
import com.elyte.exception.ResourceNotFoundException;
import com.elyte.queue.RabbitMqHandler;
import com.elyte.repository.BookingRepository;
import com.elyte.repository.UserRepository;
import com.elyte.utils.ApplicationConsts;
import java.util.Map;
import java.util.List;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private RabbitMqHandler rabbitMqHandler;

    @Autowired
    private UserRepository userRepository;

    public ResponseEntity<CustomResponseStatus> bookingsByUserid(String userid) throws ResourceNotFoundException{
        Optional<User> user =userRepository.findById(userid);
        if(user.isPresent()){
            List<Booking> bookings = bookingRepository.findByUserUserid(userid);
            CustomResponseStatus resp = new CustomResponseStatus(HttpStatus.OK.value(), ApplicationConsts.I200_MSG,
                    ApplicationConsts.SUCCESS,
                    ApplicationConsts.SRC, ApplicationConsts.timeNow(), bookings);
            return new ResponseEntity<>(resp, HttpStatus.OK);

        }
        throw new ResourceNotFoundException("User with id :" + userid + " not found!");

    }

    public ResponseEntity<CustomResponseStatus> createBookingQ(CreateBooking createBooking) throws Exception {
        Optional<User> user = userRepository.findById(createBooking.getUserid());
        if (user.isPresent()) {
            Job job = rabbitMqHandler.createJob(JobType.BOOKING);
            job.setJobRequest(ApplicationConsts.convertObjectToJson(createBooking));
            job.setUser(user.get());
            Map<String, Object> result = rabbitMqHandler.jobWithOneTask(job, "BOOKING");
            if (Boolean.TRUE.equals(result.get("success"))) {
                CustomResponseStatus resp = new CustomResponseStatus(HttpStatus.CREATED.value(),
                        ApplicationConsts.I200_MSG,
                        ApplicationConsts.SUCCESS,
                        ApplicationConsts.SRC, ApplicationConsts.timeNow(), result.get("message"));
                return new ResponseEntity<>(resp, HttpStatus.CREATED);
            }
            CustomResponseStatus status = new CustomResponseStatus(HttpStatus.BAD_REQUEST.value(),
                    (String) result.get("message"), ApplicationConsts.FAILURE, ApplicationConsts.SRC,
                    ApplicationConsts.timeNow(), null);
            return new ResponseEntity<>(status, new HttpHeaders(), HttpStatus.BAD_REQUEST);
        }
        throw new ResourceNotFoundException("[+] REQUEST FROM UNKNOWN USER WITH ID :" + createBooking.getUserid());
    }



}
