package com.sun.xml.internal.ws.developer;

import com.sun.xml.internal.ws.api.FeatureConstructor;
import javax.xml.ws.WebServiceFeature;

public class SerializationFeature
  extends WebServiceFeature
{
  public static final String ID = "http://jax-ws.java.net/features/serialization";
  private final String encoding;
  
  public SerializationFeature()
  {
    this("");
  }
  
  @FeatureConstructor({"encoding"})
  public SerializationFeature(String paramString)
  {
    encoding = paramString;
  }
  
  public String getID()
  {
    return "http://jax-ws.java.net/features/serialization";
  }
  
  public String getEncoding()
  {
    return encoding;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\developer\SerializationFeature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */