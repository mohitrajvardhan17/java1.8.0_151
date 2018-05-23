package com.oracle.webservices.internal.api;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.xml.ws.spi.WebServiceFeatureAnnotation;

@WebServiceFeatureAnnotation(id="", bean=EnvelopeStyleFeature.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface EnvelopeStyle
{
  Style[] style() default {Style.SOAP11};
  
  public static enum Style
  {
    SOAP11("http://schemas.xmlsoap.org/wsdl/soap/http"),  SOAP12("http://www.w3.org/2003/05/soap/bindings/HTTP/"),  XML("http://www.w3.org/2004/08/wsdl/http");
    
    public final String bindingId;
    
    private Style(String paramString)
    {
      bindingId = paramString;
    }
    
    public boolean isSOAP11()
    {
      return equals(SOAP11);
    }
    
    public boolean isSOAP12()
    {
      return equals(SOAP12);
    }
    
    public boolean isXML()
    {
      return equals(XML);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\oracle\webservices\internal\api\EnvelopeStyle.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */