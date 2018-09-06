package com.rayfay.bizcloud.uaa.filter;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;

import com.alibaba.fastjson.JSONObject;
import com.rayfay.bizcloud.uaa.message.LoginInfoChannelSender;

/**
 * @author maxiang
 *
 */
//@EnableBinding(LoginInfoChannelSender.class)
public class AuthLogFilter implements Filter // extends
												// AbstractAuthenticationProcessingFilter
{

  AuthLogWriter authLogWriter;
//	private final LoginInfoChannelSender loginInfoChannelSender;
//
	public AuthLogFilter(AuthLogWriter authLogWriter) {
		this.authLogWriter = authLogWriter;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;

		chain.doFilter(request, response);

		if ("POST".equalsIgnoreCase(req.getMethod()) && "/login".equals(req.getServletPath())) {
			try {
				System.out.println("----- AFTER POST /login -----");
				AuthenticationException ex = (AuthenticationException) req.getSession()
						.getAttribute("SPRING_SECURITY_LAST_EXCEPTION");
				DefaultSavedRequest savreq = (DefaultSavedRequest) req.getSession()
						.getAttribute("SPRING_SECURITY_SAVED_REQUEST");
				String username = req.getParameter("username");
				String host = req.getRemoteHost();
				String ip = req.getRemoteAddr();
				String clientId = "";
				System.out.println("username=" + username);
				System.out.println("Host=" + host);
				System.out.println("IP=" + ip);

				if (savreq == null) {
					System.out.println("NO saved request!");
				} else {
					Collection<String> pnames = savreq.getParameterNames();
					clientId = pnames.contains("client_id") ? savreq.getParameterValues("client_id")[0] : null;
					String redirectUrl = pnames.contains("redirect_uri") ? savreq.getParameterValues("redirect_uri")[0]
							: null;
					System.out.println("client_id=" + clientId);
					System.out.println("redirect_uri=" + redirectUrl);

					JSONObject loginInfo = new JSONObject();
					loginInfo.put("userName", username);
					loginInfo.put("clientId", clientId);
					loginInfo.put("IP", ip);
					loginInfo.put("loginTime", new Date().getTime());
					if (ex == null) {
						loginInfo.put("logType", "AuthorizeSuccess");
						loginInfo.put("message", "");
						System.out.println("AUTH=OK");
					} else {
						loginInfo.put("logType", "AuthorizeFailed");
						loginInfo.put("message", ex.getMessage());
						System.out.println("AUTH=NG, exception=" + ex.getMessage());
					}

					authLogWriter.send(MessageBuilder.withPayload(loginInfo.toJSONString()).build());
				}
			} catch (Exception e) {
				// logger.error("", e);
				e.printStackTrace();
			}
		} else if ("/oauth/authorize".equals(req.getServletPath())) {
			try {
				System.out.println("----- AFTER /oauth/authorize -----");

				System.out.println("response header names:" + res.getHeaderNames());
				SecurityContext sc = (SecurityContext) req.getSession().getAttribute("SPRING_SECURITY_CONTEXT");

				String username = sc == null ? null : sc.getAuthentication().getName();
				if (username != null) {
					String host = req.getRemoteHost();
					String ip = req.getRemoteAddr();
					String clientId = req.getParameter("client_id");
					String redirectURL = res.getHeader("Location");
					int sts = res.getStatus();

					System.out.println("username=" + username);
					System.out.println("Host=" + host);
					System.out.println("IP=" + ip);
					System.out.println("clientId=" + clientId);
					System.out.println("Location=" + redirectURL);
					System.out.println("Status=" + sts);
					String errCode = null;
					String errMsg = null;
					if (redirectURL != null) {
						List<NameValuePair> params = URLEncodedUtils.parse(new URI(redirectURL),
								Charset.forName("UTF-8"));

						for (NameValuePair nv : params) {
							if ("error".equals(nv.getName())) {
								System.out.println("error=" + nv.getValue());
								errCode = nv.getValue();
							} else if ("error_description".equals(nv.getName())) {
								System.out.println("error_description=" + nv.getValue());
								errMsg = nv.getValue();

							}
						}
					}
					if (errCode != null) {
						JSONObject loginInfo = new JSONObject();
						loginInfo.put("userName", username);
						loginInfo.put("clientId", clientId);
						loginInfo.put("IP", ip);
						loginInfo.put("loginTime", new Date().getTime());
						loginInfo.put("logType", "AuthorizeFailed");
						loginInfo.put("message", errCode + ":" + errMsg);
						authLogWriter.send(MessageBuilder.withPayload(loginInfo.toJSONString()).build());
					}

				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#destroy()
	 */
	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

}
