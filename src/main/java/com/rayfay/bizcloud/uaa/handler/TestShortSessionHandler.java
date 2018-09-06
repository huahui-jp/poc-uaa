package com.rayfay.bizcloud.uaa.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

/**
 * @author maxiang
 *
 */
public class TestShortSessionHandler extends SimpleUrlAuthenticationSuccessHandler
{
  public final Integer SESSION_TIMEOUT_IN_SECONDS = 60;
  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException
  {
    System.out.println("setting session timeout to " + SESSION_TIMEOUT_IN_SECONDS + " seconds");
    request.getSession().setMaxInactiveInterval(SESSION_TIMEOUT_IN_SECONDS);
    super.onAuthenticationSuccess(request, response, authentication);
  }
  
}
