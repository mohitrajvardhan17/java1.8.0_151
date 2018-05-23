package com.sun.xml.internal.ws.api.server;

import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.message.Packet;
import javax.xml.ws.WebServiceContext;

public abstract interface WSWebServiceContext
  extends WebServiceContext
{
  @Nullable
  public abstract Packet getRequestPacket();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\server\WSWebServiceContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */