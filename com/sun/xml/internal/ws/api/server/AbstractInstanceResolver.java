package com.sun.xml.internal.ws.api.server;

import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.resources.ServerMessages;
import com.sun.xml.internal.ws.server.ServerRtException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;

public abstract class AbstractInstanceResolver<T>
  extends InstanceResolver<T>
{
  public AbstractInstanceResolver() {}
  
  protected static ResourceInjector getResourceInjector(WSEndpoint paramWSEndpoint)
  {
    ResourceInjector localResourceInjector = (ResourceInjector)paramWSEndpoint.getContainer().getSPI(ResourceInjector.class);
    if (localResourceInjector == null) {
      localResourceInjector = ResourceInjector.STANDALONE;
    }
    return localResourceInjector;
  }
  
  protected static void invokeMethod(@Nullable Method paramMethod, final Object paramObject, final Object... paramVarArgs)
  {
    if (paramMethod == null) {
      return;
    }
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Void run()
      {
        try
        {
          if (!val$method.isAccessible()) {
            val$method.setAccessible(true);
          }
          MethodUtil.invoke(paramObject, val$method, paramVarArgs);
        }
        catch (IllegalAccessException localIllegalAccessException)
        {
          throw new ServerRtException("server.rt.err", new Object[] { localIllegalAccessException });
        }
        catch (InvocationTargetException localInvocationTargetException)
        {
          throw new ServerRtException("server.rt.err", new Object[] { localInvocationTargetException });
        }
        return null;
      }
    });
  }
  
  @Nullable
  protected final Method findAnnotatedMethod(Class paramClass, Class<? extends Annotation> paramClass1)
  {
    int i = 0;
    Object localObject = null;
    for (Method localMethod : paramClass.getDeclaredMethods()) {
      if (localMethod.getAnnotation(paramClass1) != null)
      {
        if (i != 0) {
          throw new ServerRtException(ServerMessages.ANNOTATION_ONLY_ONCE(paramClass1), new Object[0]);
        }
        if (localMethod.getParameterTypes().length != 0) {
          throw new ServerRtException(ServerMessages.NOT_ZERO_PARAMETERS(localMethod), new Object[0]);
        }
        localObject = localMethod;
        i = 1;
      }
    }
    return (Method)localObject;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\server\AbstractInstanceResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */