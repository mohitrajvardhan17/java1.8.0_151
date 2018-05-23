package com.sun.xml.internal.ws.server;

import com.sun.xml.internal.ws.api.server.AbstractInstanceResolver;
import com.sun.xml.internal.ws.api.server.ResourceInjector;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.api.server.WSWebServiceContext;
import java.lang.reflect.Method;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

public abstract class AbstractMultiInstanceResolver<T>
  extends AbstractInstanceResolver<T>
{
  protected final Class<T> clazz;
  private WSWebServiceContext webServiceContext;
  protected WSEndpoint owner;
  private final Method postConstructMethod;
  private final Method preDestroyMethod;
  private ResourceInjector resourceInjector;
  
  public AbstractMultiInstanceResolver(Class<T> paramClass)
  {
    clazz = paramClass;
    postConstructMethod = findAnnotatedMethod(paramClass, PostConstruct.class);
    preDestroyMethod = findAnnotatedMethod(paramClass, PreDestroy.class);
  }
  
  protected final void prepare(T paramT)
  {
    assert (webServiceContext != null);
    resourceInjector.inject(webServiceContext, paramT);
    invokeMethod(postConstructMethod, paramT, new Object[0]);
  }
  
  protected final T create()
  {
    Object localObject = createNewInstance(clazz);
    prepare(localObject);
    return (T)localObject;
  }
  
  public void start(WSWebServiceContext paramWSWebServiceContext, WSEndpoint paramWSEndpoint)
  {
    resourceInjector = getResourceInjector(paramWSEndpoint);
    webServiceContext = paramWSWebServiceContext;
    owner = paramWSEndpoint;
  }
  
  protected final void dispose(T paramT)
  {
    invokeMethod(preDestroyMethod, paramT, new Object[0]);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\server\AbstractMultiInstanceResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */