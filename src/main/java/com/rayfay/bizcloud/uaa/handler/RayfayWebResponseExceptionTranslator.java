//package com.rayfay.bizcloud.uaa.handler;
//
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.oauth2.common.DefaultThrowableAnalyzer;
//import org.springframework.security.oauth2.common.exceptions.ClientAuthenticationException;
//import org.springframework.security.oauth2.common.exceptions.InvalidScopeException;
//import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
//import org.springframework.security.oauth2.provider.error.DefaultWebResponseExceptionTranslator;
//import org.springframework.security.web.util.ThrowableAnalyzer;
//
///**
// * Created by July on 2018/7/17.
// */
//public class RayfayWebResponseExceptionTranslator extends DefaultWebResponseExceptionTranslator {
//    private ThrowableAnalyzer throwableAnalyzer = new DefaultThrowableAnalyzer();
//    @Override
//    public ResponseEntity<OAuth2Exception> translate(Exception e) throws Exception {
//        Throwable[] causeChain = this.throwableAnalyzer.determineCauseChain(e);
//        Exception ase = (OAuth2Exception)this.throwableAnalyzer.getFirstThrowableOfType(OAuth2Exception.class, causeChain);
//        if(ase instanceof InvalidScopeException){
//            ClientAuthenticationException cae = new RayfayClientAuthenticationException(((InvalidScopeException) ase).getOAuth2ErrorCode(), ase.getMessage());
//            return super.translate(cae);
//        }
//        return super.translate(e);
//    }
//
//
//    public class RayfayClientAuthenticationException extends ClientAuthenticationException{
//        private String errorCode = "";
//        RayfayClientAuthenticationException(String errorCode, String errorMessage){
//          super(errorCode);
//          this.errorCode = errorCode;
//        }
//
//        @Override
//        public String getOAuth2ErrorCode() {
//            return this.errorCode;
//        }
//    }
//}
