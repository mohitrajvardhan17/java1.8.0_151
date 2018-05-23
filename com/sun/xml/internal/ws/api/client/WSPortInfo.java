package com.sun.xml.internal.ws.api.client;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.BindingID;
import com.sun.xml.internal.ws.api.EndpointAddress;
import com.sun.xml.internal.ws.api.WSService;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.policy.PolicyMap;
import javax.xml.ws.handler.PortInfo;

public abstract interface WSPortInfo
  extends PortInfo
{
  @NotNull
  public abstract WSService getOwner();
  
  @NotNull
  public abstract BindingID getBindingId();
  
  @NotNull
  public abstract EndpointAddress getEndpointAddress();
  
  @Nullable
  public abstract WSDLPort getPort();
  
  /**
   * @deprecated
   */
  public abstract PolicyMap getPolicyMap();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\client\WSPortInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */