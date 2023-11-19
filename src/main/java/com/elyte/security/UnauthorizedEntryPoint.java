package com.elyte.security;

//JwtAuthenticationEntryPoint extends Spring’s AuthenticationEntryPoint class and overrides its method commence. 
//It rejects every unauthenticated request and sends error code 401.


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import java.io.Serializable;
import java.io.IOException;
import java.io.OutputStream;
import com.elyte.domain.response.ErrorResponse;
import com.elyte.domain.response.Status;
import com.elyte.utils.ApplicationConsts;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import org.springframework.http.MediaType;


@Component
public class UnauthorizedEntryPoint implements AuthenticationEntryPoint, Serializable {
    private static final long serialVersionUID = -7858869558953243875L;
    private static final Logger log = LoggerFactory.getLogger(UnauthorizedEntryPoint.class);

	LocalDateTime current = LocalDateTime.now();

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
		Status status = Status.build(HttpServletResponse.SC_UNAUTHORIZED,authException.getMessage(), ApplicationConsts.FAILURE,authException.getClass().getName(),current.format(ApplicationConsts.dtf));
		ErrorResponse errorResponse = ErrorResponse.build(status,ApplicationConsts.ARC_MSG);
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		OutputStream responseStream = response.getOutputStream();
		ObjectMapper mapper = new ObjectMapper();
		mapper.writeValue(responseStream, errorResponse);
        log.error("Unauthorized error: {}", authException.getMessage());
		responseStream.flush();
		
	}
    

}
