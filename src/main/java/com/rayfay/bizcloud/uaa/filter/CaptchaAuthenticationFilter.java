package com.rayfay.bizcloud.uaa.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import com.google.code.kaptcha.Constants;
import org.springframework.stereotype.Component;

public class CaptchaAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

	private String servletPath;

	public CaptchaAuthenticationFilter(String servletPath, String failureUrl) {
		super(servletPath);
		this.servletPath = servletPath;
		setAuthenticationFailureHandler(new SimpleUrlAuthenticationFailureHandler(failureUrl));
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		if ("POST".equalsIgnoreCase(req.getMethod()) && servletPath.equals(req.getServletPath())) {
			String expect = (String) req.getSession().getAttribute(Constants.KAPTCHA_SESSION_KEY);
			Object needCaptcha = req.getSession().getAttribute("needCaptcha"); 
			if (expect != null && !expect.equalsIgnoreCase(req.getParameter("kaptcha"))) {
				unsuccessfulAuthentication(req, res, new InsufficientAuthenticationException("输入的验证码不正确"));
				return;
			}
		}

        chain.doFilter(request, response);
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException, IOException, ServletException {
		return null;

	}
}