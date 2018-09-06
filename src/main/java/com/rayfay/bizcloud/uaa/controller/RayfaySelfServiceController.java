package com.rayfay.bizcloud.uaa.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.rayfay.bizcloud.uaa.config.EnvInfo;

@Configuration
@Controller
class RayfaySelfServiceController
{
  
  @Autowired
  AuthenticationTrustResolver authenticationTrustResolver;
  
  @Autowired
  private EnvInfo envinfo;
  
  @RequestMapping("/user")
  @ResponseBody
  public Object user()
  {
    return SecurityContextHolder.getContext().getAuthentication().getPrincipal();
  }
  
  @RequestMapping(value = { "/", "/home" }, method = RequestMethod.GET)
  public String home(Model model) throws Exception
  {
    model.addAttribute("idSelfserviceUrl", envinfo.getIdSelfserviceUrl());
    return "home";
  }
  
  @RequestMapping(value = "/login", method = RequestMethod.GET)
  public String login(Model model)
  {
    model.addAttribute("resetPasswordUrl", envinfo.getResetPasswordUrl());
    model.addAttribute("caLicenseDownLoadUrl", envinfo.getCaLicenseDownLoadUrl());
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null && !authenticationTrustResolver.isAnonymous(authentication))
    {
      return "redirect:home";
    }
    else
    {
      return "login";
    }
  }
}
