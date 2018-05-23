package com.sun.xml.internal.ws.api.server;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.message.Packet;
import java.security.Principal;

public abstract interface WebServiceContextDelegate
{
  public abstract Principal getUserPrincipal(@NotNull Packet paramPacket);
  
  public abstract boolean isUserInRole(@NotNull Packet paramPacket, String paramString);
  
  @NotNull
  public abstract String getEPRAddress(@NotNull Packet paramPacket, @NotNull WSEndpoint paramWSEndpoint);
  
  @Nullable
  public abstract String getWSDLAddress(@NotNull Packet paramPacket, @NotNull WSEndpoint paramWSEndpoint);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\server\WebServiceContextDelegate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */