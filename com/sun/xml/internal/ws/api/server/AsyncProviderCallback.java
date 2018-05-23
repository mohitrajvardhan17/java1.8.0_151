package com.sun.xml.internal.ws.api.server;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

public abstract interface AsyncProviderCallback<T>
{
  public abstract void send(@Nullable T paramT);
  
  public abstract void sendError(@NotNull Throwable paramThrowable);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\server\AsyncProviderCallback.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */