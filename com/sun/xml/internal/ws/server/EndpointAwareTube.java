package com.sun.xml.internal.ws.server;

import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.server.WSEndpoint;

public abstract interface EndpointAwareTube
  extends Tube
{
  public abstract void setEndpoint(WSEndpoint<?> paramWSEndpoint);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\server\EndpointAwareTube.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */