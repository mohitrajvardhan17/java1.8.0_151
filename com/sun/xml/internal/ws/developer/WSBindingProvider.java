package com.sun.xml.internal.ws.developer;

import com.sun.istack.internal.NotNull;
import com.sun.org.glassfish.gmbal.ManagedObjectManager;
import com.sun.xml.internal.ws.api.ComponentRegistry;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.api.client.WSPortInfo;
import com.sun.xml.internal.ws.api.message.Header;
import java.io.Closeable;
import java.util.List;
import javax.xml.ws.BindingProvider;

public abstract interface WSBindingProvider
  extends BindingProvider, Closeable, ComponentRegistry
{
  public abstract void setOutboundHeaders(List<Header> paramList);
  
  public abstract void setOutboundHeaders(Header... paramVarArgs);
  
  public abstract void setOutboundHeaders(Object... paramVarArgs);
  
  public abstract List<Header> getInboundHeaders();
  
  public abstract void setAddress(String paramString);
  
  public abstract WSEndpointReference getWSEndpointReference();
  
  public abstract WSPortInfo getPortInfo();
  
  @NotNull
  public abstract ManagedObjectManager getManagedObjectManager();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\developer\WSBindingProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */