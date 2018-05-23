package com.sun.xml.internal.ws.client;

import com.sun.xml.internal.ws.developer.JAXWSProperties;

public abstract interface BindingProviderProperties
  extends JAXWSProperties
{
  @Deprecated
  public static final String HOSTNAME_VERIFICATION_PROPERTY = "com.sun.xml.internal.ws.client.http.HostnameVerificationProperty";
  public static final String HTTP_COOKIE_JAR = "com.sun.xml.internal.ws.client.http.CookieJar";
  public static final String REDIRECT_REQUEST_PROPERTY = "com.sun.xml.internal.ws.client.http.RedirectRequestProperty";
  public static final String ONE_WAY_OPERATION = "com.sun.xml.internal.ws.server.OneWayOperation";
  public static final String JAXWS_HANDLER_CONFIG = "com.sun.xml.internal.ws.handler.config";
  public static final String JAXWS_CLIENT_HANDLE_PROPERTY = "com.sun.xml.internal.ws.client.handle";
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\client\BindingProviderProperties.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */