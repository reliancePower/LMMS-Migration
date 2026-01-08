package com.lawsuittracker.util;

import java.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.lawsuittracker.modal.AuthenticationService;

public class RestAuthenticationFilter implements jakarta.servlet.Filter {
	public static final String AUTHENTICATION_HEADER = "Authorization";

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filter)
	        throws IOException, ServletException {

	    if (request instanceof HttpServletRequest) {
	        HttpServletRequest httpServletRequest = (HttpServletRequest) request;

	        // ✅ Skip auth for downloadPdf endpoint
	        String uri = httpServletRequest.getRequestURI();
	        if (uri != null && uri.contains("/rest/V2/downloadPdf")) {
	            filter.doFilter(request, response); // Bypass authentication
	            return;
	        }

	        String authCredentials = httpServletRequest.getHeader(AUTHENTICATION_HEADER);

	        // better injected
	        AuthenticationService authenticationService = new AuthenticationService();
	        boolean authenticationStatus = authenticationService.authenticate(authCredentials, "rest");

	        if (authenticationStatus) {
	            filter.doFilter(request, response);
	        } else {
	            if (response instanceof HttpServletResponse) {
	                HttpServletResponse httpServletResponse = (HttpServletResponse) response;
	                httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	            }
	        }
	    }
	}


	@Override
	public void destroy() {
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
	}
}