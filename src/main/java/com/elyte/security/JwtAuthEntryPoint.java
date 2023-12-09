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
import com.elyte.domain.response.CustomResponseStatus;
import com.elyte.utils.ApplicationConsts;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

@Component
public class JwtAuthEntryPoint implements AuthenticationEntryPoint, Serializable {

	private static final long serialVersionUID = -7858869558953243875L;

	private static final Logger log = LoggerFactory.getLogger(JwtAuthEntryPoint.class);

	@Autowired
    private LoginAttemptService loginAttemptService;

	

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException {
		CustomResponseStatus status = new CustomResponseStatus(HttpServletResponse.SC_UNAUTHORIZED,
				authException.getMessage(), ApplicationConsts.FAILURE, authException.getClass().getName(),
				ApplicationConsts.timeNow(), ApplicationConsts.ARC_MSG);
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		if(loginAttemptService.isBlocked()) status.setResult("User blocked");
		OutputStream responseStream = response.getOutputStream();
		ObjectMapper mapper = new ObjectMapper();
		mapper.writeValue(responseStream, status);
		log.error("[+] Unauthorized error: {}", authException.getMessage());
		responseStream.flush();

	}

}
