package com.elyte.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import com.elyte.domain.response.CustomResponseStatus;
import com.elyte.utils.ApplicationConsts;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;

@Component
public class BasicAuthEntryPoint extends BasicAuthenticationEntryPoint implements Serializable {

	private static final long serialVersionUID = -1L;

	private static final Logger log = LoggerFactory.getLogger(BasicAuthEntryPoint.class);

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException {
		CustomResponseStatus status = new CustomResponseStatus(HttpServletResponse.SC_UNAUTHORIZED,
				authException.getMessage(), ApplicationConsts.FAILURE, authException.getClass().getName(),
				ApplicationConsts.timeNow(), ApplicationConsts.ARC_MSG);
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.addHeader("WWW-Authenticate", "Basic realm=" + getRealmName() + "");
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		OutputStream responseStream = response.getOutputStream();
		ObjectMapper mapper = new ObjectMapper();
		mapper.writeValue(responseStream, status);
		log.error("[+] Unauthorized Basic Auth error: {}", authException.getMessage());
		responseStream.flush();

	}

	@Override
	public void afterPropertiesSet() {
		setRealmName("Elyte");
		super.afterPropertiesSet();
	}

}
