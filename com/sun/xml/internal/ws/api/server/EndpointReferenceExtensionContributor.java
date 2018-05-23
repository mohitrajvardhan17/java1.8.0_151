package com.sun.xml.internal.ws.api.server;

import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference.EPRExtension;
import javax.xml.namespace.QName;

public abstract class EndpointReferenceExtensionContributor
{
  public EndpointReferenceExtensionContributor() {}
  
  public abstract WSEndpointReference.EPRExtension getEPRExtension(WSEndpoint paramWSEndpoint, @Nullable WSEndpointReference.EPRExtension paramEPRExtension);
  
  public abstract QName getQName();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\server\EndpointReferenceExtensionContributor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */