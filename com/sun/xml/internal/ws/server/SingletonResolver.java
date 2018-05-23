package com.sun.xml.internal.ws.server;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.server.AbstractInstanceResolver;
import com.sun.xml.internal.ws.api.server.ResourceInjector;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.api.server.WSWebServiceContext;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

public final class SingletonResolver<T>
  extends AbstractInstanceResolver<T>
{
  @NotNull
  private final T singleton;
  
  public SingletonResolver(@NotNull T paramT)
  {
    singleton = paramT;
  }
  
  @NotNull
  public T resolve(Packet paramPacket)
  {
    return (T)singleton;
  }
  
  public void start(WSWebServiceContext paramWSWebServiceContext, WSEndpoint paramWSEndpoint)
  {
    getResourceInjector(paramWSEndpoint).inject(paramWSWebServiceContext, singleton);
    invokeMethod(findAnnotatedMethod(singleton.getClass(), PostConstruct.class), singleton, new Object[0]);
  }
  
  public void dispose()
  {
    invokeMethod(findAnnotatedMethod(singleton.getClass(), PreDestroy.class), singleton, new Object[0]);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\server\SingletonResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */