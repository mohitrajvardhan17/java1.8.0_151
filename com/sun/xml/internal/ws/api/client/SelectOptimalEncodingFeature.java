package com.sun.xml.internal.ws.api.client;

import com.sun.org.glassfish.gmbal.ManagedAttribute;
import com.sun.org.glassfish.gmbal.ManagedData;
import com.sun.xml.internal.ws.api.FeatureConstructor;
import javax.xml.ws.WebServiceFeature;

@ManagedData
public class SelectOptimalEncodingFeature
  extends WebServiceFeature
{
  public static final String ID = "http://java.sun.com/xml/ns/jaxws/client/selectOptimalEncoding";
  
  public SelectOptimalEncodingFeature()
  {
    enabled = true;
  }
  
  @FeatureConstructor({"enabled"})
  public SelectOptimalEncodingFeature(boolean paramBoolean)
  {
    enabled = paramBoolean;
  }
  
  @ManagedAttribute
  public String getID()
  {
    return "http://java.sun.com/xml/ns/jaxws/client/selectOptimalEncoding";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\client\SelectOptimalEncodingFeature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */