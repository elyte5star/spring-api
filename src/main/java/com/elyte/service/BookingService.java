package com.elyte.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.Optional;
import com.stripe.Stripe;
import com.stripe.exception.ApiConnectionException;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.CardException;
import com.stripe.exception.InvalidRequestException;
import com.stripe.exception.RateLimitException;
import com.stripe.exception.StripeException;
//import com.stripe.model.Charge;
import com.elyte.domain.Booking;
import com.elyte.domain.Job;
import com.elyte.domain.Task;
import com.elyte.domain.User;
import com.elyte.domain.Payment.BillingAddress;
import com.elyte.domain.Payment.CardDetails;
import com.elyte.domain.enums.JobType;
import com.elyte.domain.request.BookingJob;
import com.elyte.domain.request.CreateBooking;
import com.elyte.domain.response.CustomResponseStatus;
import com.elyte.domain.response.JobAndTasksResult;
import com.elyte.domain.response.JobResponse;
import com.elyte.domain.response.WorkResult;
import com.elyte.exception.ResourceNotFoundException;
import com.elyte.queue.RabbitMqHandler;
import com.elyte.repository.BookingRepository;
import com.elyte.repository.UserRepository;
import com.elyte.utils.UtilityFunctions;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import java.util.Map;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

@Service
public class BookingService extends UtilityFunctions{

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private RabbitMqHandler rabbitMqHandler;

    @Value("${spring.rabbitmq.auto-config.bindings.binding-two.routing-key}")
    private String bookingRoutingkey;

    @Value("${payment.STRIPE_SECRET_KEY}")
    private String secretKey;

    @Value("${payment.STRIPE_PUBLIC_KEY}")
    private String stripePublicKey;

    @Autowired
    private UserRepository userRepository;

    @PostConstruct
    public void init() {

        Stripe.apiKey = secretKey;
    }

    public ResponseEntity<CustomResponseStatus> bookingsByUserid(String userid) throws ResourceNotFoundException {
        Optional<User> user = userRepository.findById(userid);
        if (user.isPresent()) {
            List<Booking> bookings = bookingRepository.findByUserUserid(userid);
            CustomResponseStatus resp = new CustomResponseStatus(HttpStatus.OK.value(), this.I200_MSG,
                    this.SUCCESS,
                    this.SRC, this.timeNow(), bookings);
            return new ResponseEntity<>(resp, HttpStatus.OK);

        }
        throw new ResourceNotFoundException("User with id :" + userid + " not found!");

    }

    public ResponseEntity<CustomResponseStatus> createBookingQ(CreateBooking createBooking) throws Exception {
        Optional<User> user = userRepository.findById(createBooking.getUserid());
        if (user.isPresent()) {
            // confirm payment before creating job
            boolean paymentConfirmation = handlePayment(createBooking.getPaymentDetails().getCardDetails(),
                    createBooking.getPaymentDetails().getBilling_address(), createBooking.getTotalPrice());
            if (!paymentConfirmation)
                throw new Exception("Payment not Successful!");
            Job job = rabbitMqHandler.createJob(JobType.BOOKING);
            BookingJob bookingJob = new BookingJob(createBooking.getUserid(), createBooking.getTotalPrice(),
                    createBooking.getCart(), createBooking.getPaymentDetails().getShippingAddress());
            job.setJobRequest(this.convertObjectToJson(bookingJob));
            job.setUser(user.get());
            Map<String, Object> result = rabbitMqHandler.jobWithOneTask(job, bookingRoutingkey);
            if (Boolean.TRUE.equals(result.get("success"))) {
                CustomResponseStatus resp = new CustomResponseStatus(HttpStatus.CREATED.value(),
                        this.I200_MSG,
                        this.SUCCESS,
                        this.SRC, this.timeNow(), result.get("message"));
                return new ResponseEntity<>(resp, HttpStatus.CREATED);
            }
            CustomResponseStatus status = new CustomResponseStatus(HttpStatus.BAD_REQUEST.value(),
                    (String) result.get("message"), this.FAILURE, this.SRC,
                    this.timeNow(), null);
            return new ResponseEntity<>(status, new HttpHeaders(), HttpStatus.BAD_REQUEST);
        }
        throw new ResourceNotFoundException("[+] REQUEST FROM UNKNOWN USER WITH ID :" + createBooking.getUserid());
    }

    private boolean handlePayment(CardDetails cardDetails, BillingAddress billingAddress, BigDecimal totalAmount)
            throws CardException, RateLimitException, InvalidRequestException, AuthenticationException,
            ApiConnectionException, StripeException, Exception {
        if (totalAmount.compareTo(BigDecimal.ZERO) > 0) {
            Map<String, Object> chargeParams = new HashMap<>();

            try {
                // Use Stripe's library to make requests..
                // chargeParams.put("amount", totalAmount);
                // chargeParams.put("currency", cardDetails.getCurrency());
                // chargeParams.put("source", stripePublicKey);
                // Charge.create(chargeParams);
                chargeParams.put("description", "Product payment");
                return true;
            } catch (Exception e) {
                // Something else happened, completely unrelated to Stripe
            }
            return false;
        }

        throw new Exception("Amount not specified");

    }

    public ResponseEntity<CustomResponseStatus> bookingResultByJid(@Valid String jid) throws JsonParseException, JsonMappingException, IOException {
        Job job = rabbitMqHandler.getJob(jid);
        if (job.getJobType() != JobType.BOOKING)
            throw new ResourceNotFoundException("Wrong job type");
        JobAndTasksResult results = rabbitMqHandler.checkJobAndTasks(job);
        boolean resultIsAvailable = rabbitMqHandler.resultAvailable(results.getJob());
        if (resultIsAvailable) {
            JobResponse jobResponse = rabbitMqHandler.createJobResponse(job, results.getLastTaskEndedAt());
            List<WorkResult> bookingResult = createBookingResult(results.getTasks());
            CustomResponseStatus resp = new CustomResponseStatus(HttpStatus.OK.value(), this.I200_MSG,
                    this.SUCCESS,
                    this.SRC, this.timeNow(),
                    Map.of("job-result", jobResponse, "tasks", bookingResult));
            return new ResponseEntity<>(resp, HttpStatus.OK);

        }
        throw new ResourceNotFoundException("Result from job is not available.");
    }

    public List<WorkResult> createBookingResult(List<Task> tasks) throws JsonParseException, JsonMappingException, IOException {
        List<WorkResult> taskResults = new ArrayList<WorkResult>();
        for (Task task : tasks) {
            String result = this.mapper.readValue(task.getResult(), String.class);
            taskResults.add(new WorkResult(task.getTid(),task.getTaskStatus().isSuccessful(),result));
        }
        return taskResults;
    }
}