package com.lawsuittracker.util;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class FilterAction implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		HttpServletResponse response = (HttpServletResponse) res;
		HttpServletRequest req = (HttpServletRequest) request;
		HttpSession session = req.getSession(false);

		if (session != null && req.getParameter("JSESSIONID") != null) {
			Cookie userCookie = new Cookie("JSESSIONID", req.getParameter("JSESSIONID"));
			response.addCookie(userCookie);
		} else {
			session = req.getSession(true);
			String sessionId = session.getId();
			Cookie userCookie = new Cookie("JSESSIONID", sessionId);
			response.addCookie(userCookie);
		}
		
		

		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT");
		response.setHeader("Access-Control-Max-Age", "28800");
		//response.setHeader("Access-Control-Max-Age", "600");
		response.setHeader("Access-Control-Allow-Credentials", "true");
		// response.setHeader("Access-Control-Allow-Headers", "x-requested-with");
		chain.doFilter(request, response);		

	}

	public void destroy() {
	}
}