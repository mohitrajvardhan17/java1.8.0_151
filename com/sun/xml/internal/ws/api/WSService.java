package com.sun.xml.internal.ws.api;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.client.WSServiceDelegate;
import java.lang.reflect.Field;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import javax.xml.ws.Service.Mode;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.spi.ServiceDelegate;

public abstract class WSService
  extends ServiceDelegate
  implements ComponentRegistry
{
  private final Set<Component> components = new CopyOnWriteArraySet();
  protected static final ThreadLocal<InitParams> INIT_PARAMS = new ThreadLocal();
  protected static final InitParams EMPTY_PARAMS = new InitParams();
  
  protected WSService() {}
  
  public abstract <T> T getPort(WSEndpointReference paramWSEndpointReference, Class<T> paramClass, WebServiceFeature... paramVarArgs);
  
  public abstract <T> Dispatch<T> createDispatch(QName paramQName, WSEndpointReference paramWSEndpointReference, Class<T> paramClass, Service.Mode paramMode, WebServiceFeature... paramVarArgs);
  
  public abstract Dispatch<Object> createDispatch(QName paramQName, WSEndpointReference paramWSEndpointReference, JAXBContext paramJAXBContext, Service.Mode paramMode, WebServiceFeature... paramVarArgs);
  
  @NotNull
  public abstract Container getContainer();
  
  @Nullable
  public <S> S getSPI(@NotNull Class<S> paramClass)
  {
    Iterator localIterator = components.iterator();
    while (localIterator.hasNext())
    {
      Component localComponent = (Component)localIterator.next();
      Object localObject = localComponent.getSPI(paramClass);
      if (localObject != null) {
        return (S)localObject;
      }
    }
    return (S)getContainer().getSPI(paramClass);
  }
  
  @NotNull
  public Set<Component> getComponents()
  {
    return components;
  }
  
  public static WSService create(URL paramURL, QName paramQName)
  {
    return new WSServiceDelegate(paramURL, paramQName, Service.class, new WebServiceFeature[0]);
  }
  
  public static WSService create(QName paramQName)
  {
    return create(null, paramQName);
  }
  
  public static WSService create()
  {
    return create(null, new QName(WSService.class.getName(), "dummy"));
  }
  
  public static Service create(URL paramURL, QName paramQName, InitParams paramInitParams)
  {
    if (INIT_PARAMS.get() != null) {
      throw new IllegalStateException("someone left non-null InitParams");
    }
    INIT_PARAMS.set(paramInitParams);
    try
    {
      Service localService1 = Service.create(paramURL, paramQName);
      if (INIT_PARAMS.get() != null) {
        throw new IllegalStateException("Service " + localService1 + " didn't recognize InitParams");
      }
      Service localService2 = localService1;
      return localService2;
    }
    finally
    {
      INIT_PARAMS.set(null);
    }
  }
  
  public static WSService unwrap(Service paramService)
  {
    (WSService)AccessController.doPrivileged(new PrivilegedAction()
    {
      public WSService run()
      {
        try
        {
          Field localField = val$svc.getClass().getField("delegate");
          localField.setAccessible(true);
          localObject = localField.get(val$svc);
          if (!(localObject instanceof WSService)) {
            throw new IllegalArgumentException();
          }
          return (WSService)localObject;
        }
        catch (NoSuchFieldException localNoSuchFieldException)
        {
          localObject = new AssertionError("Unexpected service API implementation");
          ((AssertionError)localObject).initCause(localNoSuchFieldException);
          throw ((Throwable)localObject);
        }
        catch (IllegalAccessException localIllegalAccessException)
        {
          Object localObject = new IllegalAccessError(localIllegalAccessException.getMessage());
          ((IllegalAccessError)localObject).initCause(localIllegalAccessException);
          throw ((Throwable)localObject);
        }
      }
    });
  }
  
  public static final class InitParams
  {
    private Container container;
    
    public InitParams() {}
    
    public void setContainer(Container paramContainer)
    {
      container = paramContainer;
    }
    
    public Container getContainer()
    {
      return container;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\WSService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */