package com.sun.xml.internal.ws.developer;

import com.sun.org.glassfish.gmbal.ManagedAttribute;
import com.sun.org.glassfish.gmbal.ManagedData;
import javax.xml.ws.WebServiceFeature;

@ManagedData
public final class BindingTypeFeature
  extends WebServiceFeature
{
  public static final String ID = "http://jax-ws.dev.java.net/features/binding";
  private final String bindingId;
  
  public BindingTypeFeature(String paramString)
  {
    bindingId = paramString;
  }
  
  @ManagedAttribute
  public String getID()
  {
    return "http://jax-ws.dev.java.net/features/binding";
  }
  
  @ManagedAttribute
  public String getBindingId()
  {
    return bindingId;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\developer\BindingTypeFeature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */