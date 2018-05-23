package com.sun.xml.internal.ws.transport.http.client;

import com.oracle.webservices.internal.api.message.BasePropertySet;
import com.oracle.webservices.internal.api.message.BasePropertySet.PropertyMap;
import com.oracle.webservices.internal.api.message.PropertySet.Property;
import com.sun.istack.internal.NotNull;
import java.util.List;
import java.util.Map;

final class HttpResponseProperties
  extends BasePropertySet
{
  private final HttpClientTransport deferedCon;
  private static final BasePropertySet.PropertyMap model = parse(HttpResponseProperties.class);
  
  public HttpResponseProperties(@NotNull HttpClientTransport paramHttpClientTransport)
  {
    deferedCon = paramHttpClientTransport;
  }
  
  @PropertySet.Property({"javax.xml.ws.http.response.headers"})
  public Map<String, List<String>> getResponseHeaders()
  {
    return deferedCon.getHeaders();
  }
  
  @PropertySet.Property({"javax.xml.ws.http.response.code"})
  public int getResponseCode()
  {
    return deferedCon.statusCode;
  }
  
  protected BasePropertySet.PropertyMap getPropertyMap()
  {
    return model;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\transport\http\client\HttpResponseProperties.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */