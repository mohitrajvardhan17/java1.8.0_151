package com.sun.xml.internal.ws.api.server;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import javax.xml.namespace.QName;

public abstract class PortAddressResolver
{
  public PortAddressResolver() {}
  
  @Nullable
  public abstract String getAddressFor(@NotNull QName paramQName, @NotNull String paramString);
  
  @Nullable
  public String getAddressFor(@NotNull QName paramQName, @NotNull String paramString1, String paramString2)
  {
    return getAddressFor(paramQName, paramString1);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\server\PortAddressResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */