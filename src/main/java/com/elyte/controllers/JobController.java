package com.elyte.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.elyte.domain.response.CustomResponseStatus;
import com.elyte.domain.response.JobResponse;
import com.elyte.exception.ResourceNotFoundException;
import com.elyte.queue.RabbitMqHandler;
import com.elyte.utils.UtilityFunctions;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api/job")
public class JobController extends UtilityFunctions{

    @Autowired
    private RabbitMqHandler rabbitMqHandler;

    @GetMapping("/{jid}")
    @Operation(summary = "Get a job status by jid", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CustomResponseStatus> findJobById(@PathVariable @Valid String jid)
            throws ResourceNotFoundException {
        JobResponse jobResponse = rabbitMqHandler.getJobResponse(jid);
        CustomResponseStatus status = new CustomResponseStatus(HttpStatus.OK.value(), this.I200_MSG,
                this.SUCCESS, this.SRC, this.timeNow(), jobResponse);
        return new ResponseEntity<>(status, HttpStatus.OK);

    }




    
}
