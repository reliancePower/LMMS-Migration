package com.lawsuittracker.util;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lawsuittracker.modal.AuthenticationService;

public class RestAuthenticationFilter implements javax.servlet.Filter {
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