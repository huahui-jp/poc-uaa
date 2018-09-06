package com.rayfay.bizcloud.uaa.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.messaging.Message;

import com.rayfay.bizcloud.uaa.message.LoginInfoChannelSender;

/**
 * @author maxiang
 *
 */
@EnableBinding(LoginInfoChannelSender.class)
public class AuthLogWriter
{
  private final LoginInfoChannelSender loginInfoChannelSender;

  @Autowired
  public AuthLogWriter(LoginInfoChannelSender loginInfoChannelSender) {
    this.loginInfoChannelSender = loginInfoChannelSender;
  }
  
  public boolean send(Message<?> paramMessage)
  {
    return loginInfoChannelSender.sendLoginInfo().send(paramMessage);
  }

}
