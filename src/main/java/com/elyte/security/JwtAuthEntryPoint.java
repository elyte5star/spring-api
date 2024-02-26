package com.elyte.security;


//JwtAuthenticationEntryPoint extends Spring’s AuthenticationEntryPoint class and overrides its method commence. 
//It rejects every unauthenticated request and sends error code 401.

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import java.io.Serial;
import java.io.Serializable;
import java.io.IOException;
import com.elyte.utils.UtilityFunctions;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.HashMap;

@Component
public class JwtAuthEntryPoint extends UtilityFunctions implements AuthenticationEntryPoint, Serializable {

	@Serial
	private static final long serialVersionUID = -7858869558953243875L;

	private static final Logger log = LoggerFactory.getLogger(JwtAuthEntryPoint.class);

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException,ServletException  {
		log.error("[X] Unauthorized : " + authException.getLocalizedMessage());
		response.setContentType(MediaType.ALL_VALUE);
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		final Map<String, Object> body = new HashMap<>();
		body.put("code", HttpServletResponse.SC_UNAUTHORIZED);
		body.put("result", "Unauthorized");
		body.put("message", authException.getMessage());
		body.put("path", request.getServletPath());
		body.put("timeStamp", this.timeNow());
		body.put("success", false);
		final ObjectMapper mapper = new ObjectMapper();
		mapper.writeValue(response.getOutputStream(), body);
	}

}
