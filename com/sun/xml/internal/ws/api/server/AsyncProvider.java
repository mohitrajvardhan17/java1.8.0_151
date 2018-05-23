package com.sun.xml.internal.ws.api.server;

import com.sun.istack.internal.NotNull;
import javax.xml.ws.WebServiceContext;

public abstract interface AsyncProvider<T>
{
  public abstract void invoke(@NotNull T paramT, @NotNull AsyncProviderCallback<T> paramAsyncProviderCallback, @NotNull WebServiceContext paramWebServiceContext);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\server\AsyncProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */