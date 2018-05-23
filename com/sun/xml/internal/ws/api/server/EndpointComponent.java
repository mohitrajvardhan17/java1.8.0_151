package com.sun.xml.internal.ws.api.server;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

/**
 * @deprecated
 */
public abstract interface EndpointComponent
{
  @Nullable
  public abstract <T> T getSPI(@NotNull Class<T> paramClass);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\server\EndpointComponent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */