package com.sun.xml.internal.ws.api.server;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.pipe.Codec;

public abstract interface EndpointAwareCodec
  extends Codec
{
  public abstract void setEndpoint(@NotNull WSEndpoint paramWSEndpoint);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\server\EndpointAwareCodec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */