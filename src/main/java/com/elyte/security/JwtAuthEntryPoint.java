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
import java.util.Map;
import java.io.IOException;
import com.elyte.domain.response.CustomResponseStatus;
import com.elyte.utils.UtilityFunctions;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;

@Component
public class JwtAuthEntryPoint extends UtilityFunctions implements AuthenticationEntryPoint, Serializable {

	private static final long serialVersionUID = -7858869558953243875L;

	private static final Logger log = LoggerFactory.getLogger(JwtAuthEntryPoint.class);

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException {
		CustomResponseStatus status = new CustomResponseStatus(HttpServletResponse.SC_UNAUTHORIZED,
				authException.getMessage(), this.FAILURE, authException.getClass().getName(),
				this.timeNow(), this.ARC_MSG);
		response.setContentType(MediaType.ALL_VALUE);
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> map = mapper.convertValue(status, new TypeReference<Map<String, String>>() {
		});
		for (String key : map.keySet()) {
			response.addHeader(key, map.get(key));
		}
		log.error("[X] Unauthorized error: ", authException.getMessage());
		response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());

	}

}
