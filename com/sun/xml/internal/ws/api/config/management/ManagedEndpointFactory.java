package com.sun.xml.internal.ws.api.config.management;

import com.sun.xml.internal.ws.api.server.WSEndpoint;

public abstract interface ManagedEndpointFactory
{
  public abstract <T> WSEndpoint<T> createEndpoint(WSEndpoint<T> paramWSEndpoint, EndpointCreationAttributes paramEndpointCreationAttributes);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\config\management\ManagedEndpointFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */