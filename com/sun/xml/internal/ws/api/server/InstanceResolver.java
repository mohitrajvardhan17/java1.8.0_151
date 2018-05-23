package com.sun.xml.internal.ws.api.server;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.resources.ServerMessages;
import com.sun.xml.internal.ws.resources.WsservletMessages;
import com.sun.xml.internal.ws.server.ServerRtException;
import com.sun.xml.internal.ws.server.SingletonResolver;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.ws.Provider;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.WebServiceException;

public abstract class InstanceResolver<T>
{
  private static final Logger logger = Logger.getLogger("com.sun.xml.internal.ws.server");
  
  public InstanceResolver() {}
  
  @NotNull
  public abstract T resolve(@NotNull Packet paramPacket);
  
  public void postInvoke(@NotNull Packet paramPacket, @NotNull T paramT) {}
  
  public void start(@NotNull WSWebServiceContext paramWSWebServiceContext, @NotNull WSEndpoint paramWSEndpoint)
  {
    start(paramWSWebServiceContext);
  }
  
  /**
   * @deprecated
   */
  public void start(@NotNull WebServiceContext paramWebServiceContext) {}
  
  public void dispose() {}
  
  public static <T> InstanceResolver<T> createSingleton(T paramT)
  {
    assert (paramT != null);
    Object localObject = createFromInstanceResolverAnnotation(paramT.getClass());
    if (localObject == null) {
      localObject = new SingletonResolver(paramT);
    }
    return (InstanceResolver<T>)localObject;
  }
  
  /**
   * @deprecated
   */
  public static <T> InstanceResolver<T> createDefault(@NotNull Class<T> paramClass, boolean paramBoolean)
  {
    return createDefault(paramClass);
  }
  
  public static <T> InstanceResolver<T> createDefault(@NotNull Class<T> paramClass)
  {
    Object localObject = createFromInstanceResolverAnnotation(paramClass);
    if (localObject == null) {
      localObject = new SingletonResolver(createNewInstance(paramClass));
    }
    return (InstanceResolver<T>)localObject;
  }
  
  public static <T> InstanceResolver<T> createFromInstanceResolverAnnotation(@NotNull Class<T> paramClass)
  {
    for (Annotation localAnnotation : paramClass.getAnnotations())
    {
      InstanceResolverAnnotation localInstanceResolverAnnotation = (InstanceResolverAnnotation)localAnnotation.annotationType().getAnnotation(InstanceResolverAnnotation.class);
      if (localInstanceResolverAnnotation != null)
      {
        Class localClass = localInstanceResolverAnnotation.value();
        try
        {
          return (InstanceResolver)localClass.getConstructor(new Class[] { Class.class }).newInstance(new Object[] { paramClass });
        }
        catch (InstantiationException localInstantiationException)
        {
          throw new WebServiceException(ServerMessages.FAILED_TO_INSTANTIATE_INSTANCE_RESOLVER(localClass.getName(), localAnnotation.annotationType(), paramClass.getName()));
        }
        catch (IllegalAccessException localIllegalAccessException)
        {
          throw new WebServiceException(ServerMessages.FAILED_TO_INSTANTIATE_INSTANCE_RESOLVER(localClass.getName(), localAnnotation.annotationType(), paramClass.getName()));
        }
        catch (InvocationTargetException localInvocationTargetException)
        {
          throw new WebServiceException(ServerMessages.FAILED_TO_INSTANTIATE_INSTANCE_RESOLVER(localClass.getName(), localAnnotation.annotationType(), paramClass.getName()));
        }
        catch (NoSuchMethodException localNoSuchMethodException)
        {
          throw new WebServiceException(ServerMessages.FAILED_TO_INSTANTIATE_INSTANCE_RESOLVER(localClass.getName(), localAnnotation.annotationType(), paramClass.getName()));
        }
      }
    }
    return null;
  }
  
  protected static <T> T createNewInstance(Class<T> paramClass)
  {
    try
    {
      return (T)paramClass.newInstance();
    }
    catch (InstantiationException localInstantiationException)
    {
      logger.log(Level.SEVERE, localInstantiationException.getMessage(), localInstantiationException);
      throw new ServerRtException(WsservletMessages.ERROR_IMPLEMENTOR_FACTORY_NEW_INSTANCE_FAILED(paramClass), new Object[0]);
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      logger.log(Level.SEVERE, localIllegalAccessException.getMessage(), localIllegalAccessException);
      throw new ServerRtException(WsservletMessages.ERROR_IMPLEMENTOR_FACTORY_NEW_INSTANCE_FAILED(paramClass), new Object[0]);
    }
  }
  
  @NotNull
  public Invoker createInvoker()
  {
    new Invoker()
    {
      public void start(@NotNull WSWebServiceContext paramAnonymousWSWebServiceContext, @NotNull WSEndpoint paramAnonymousWSEndpoint)
      {
        InstanceResolver.this.start(paramAnonymousWSWebServiceContext, paramAnonymousWSEndpoint);
      }
      
      public void dispose()
      {
        InstanceResolver.this.dispose();
      }
      
      public Object invoke(Packet paramAnonymousPacket, Method paramAnonymousMethod, Object... paramAnonymousVarArgs)
        throws InvocationTargetException, IllegalAccessException
      {
        Object localObject1 = resolve(paramAnonymousPacket);
        try
        {
          Object localObject2 = MethodUtil.invoke(localObject1, paramAnonymousMethod, paramAnonymousVarArgs);
          return localObject2;
        }
        finally
        {
          postInvoke(paramAnonymousPacket, localObject1);
        }
      }
      
      public <U> U invokeProvider(@NotNull Packet paramAnonymousPacket, U paramAnonymousU)
      {
        Object localObject1 = resolve(paramAnonymousPacket);
        try
        {
          Object localObject2 = ((Provider)localObject1).invoke(paramAnonymousU);
          return (U)localObject2;
        }
        finally
        {
          postInvoke(paramAnonymousPacket, localObject1);
        }
      }
      
      public String toString()
      {
        return "Default Invoker over " + InstanceResolver.this.toString();
      }
    };
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\server\InstanceResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */