package com.sun.xml.internal.ws.api.server;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.server.DefaultResourceInjector;

public abstract class ResourceInjector
{
  public static final ResourceInjector STANDALONE = new DefaultResourceInjector();
  
  public ResourceInjector() {}
  
  public abstract void inject(@NotNull WSWebServiceContext paramWSWebServiceContext, @NotNull Object paramObject);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\server\ResourceInjector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */