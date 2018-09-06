package com.rayfay.bizcloud.uaa.config;

import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * Created by shenfu on 2017/6/6.
 */
@Configuration
public class EnvInfo implements EnvironmentAware
{
  private String idSelfserviceUrl;
  private String resetPasswordUrl;
  private String caLicenseDownLoadUrl;
  
  public String getResetPasswordUrl()
  {
    return resetPasswordUrl;
  }
  
  public String getIdSelfserviceUrl()
  {
    return idSelfserviceUrl;
  }

  public String getCaLicenseDownLoadUrl()
  {
    return caLicenseDownLoadUrl;
  }
  
  @Override
  public void setEnvironment(Environment environment)
  {
    idSelfserviceUrl = environment.getProperty("ID_SELFSERVICE_URL");
    resetPasswordUrl = environment.getProperty("RESET_PASSWORD_URL");
    caLicenseDownLoadUrl = environment.getProperty("CA_LICENSE_DOWNLOAD_URL");
  }
  
}
