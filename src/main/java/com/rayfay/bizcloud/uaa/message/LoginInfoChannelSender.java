package com.rayfay.bizcloud.uaa.message;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

/**
 * Created by shenfu on 2017/7/19.
 */
public interface LoginInfoChannelSender {
	String LOGIN_INFO_SENDER = "_loginInfoChannel";

	@Output(LoginInfoChannelSender.LOGIN_INFO_SENDER)
	MessageChannel sendLoginInfo();

}
