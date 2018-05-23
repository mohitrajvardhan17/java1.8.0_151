package com.sun.xml.internal.ws.api.server;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.message.Packet;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.xml.ws.Provider;
import javax.xml.ws.WebServiceContext;

public abstract class Invoker
  extends com.sun.xml.internal.ws.server.sei.Invoker
{
  private static final Method invokeMethod;
  private static final Method asyncInvokeMethod;
  
  public Invoker() {}
  
  public void start(@NotNull WSWebServiceContext paramWSWebServiceContext, @NotNull WSEndpoint paramWSEndpoint)
  {
    start(paramWSWebServiceContext);
  }
  
  /**
   * @deprecated
   */
  public void start(@NotNull WebServiceContext paramWebServiceContext)
  {
    throw new IllegalStateException("deprecated version called");
  }
  
  public void dispose() {}
  
  public <T> T invokeProvider(@NotNull Packet paramPacket, T paramT)
    throws IllegalAccessException, InvocationTargetException
  {
    return (T)invoke(paramPacket, invokeMethod, new Object[] { paramT });
  }
  
  public <T> void invokeAsyncProvider(@NotNull Packet paramPacket, T paramT, AsyncProviderCallback paramAsyncProviderCallback, WebServiceContext paramWebServiceContext)
    throws IllegalAccessException, InvocationTargetException
  {
    invoke(paramPacket, asyncInvokeMethod, new Object[] { paramT, paramAsyncProviderCallback, paramWebServiceContext });
  }
  
  static
  {
    try
    {
      invokeMethod = Provider.class.getMethod("invoke", new Class[] { Object.class });
    }
    catch (NoSuchMethodException localNoSuchMethodException1)
    {
      throw new AssertionError(localNoSuchMethodException1);
    }
    try
    {
      asyncInvokeMethod = AsyncProvider.class.getMethod("invoke", new Class[] { Object.class, AsyncProviderCallback.class, WebServiceContext.class });
    }
    catch (NoSuchMethodException localNoSuchMethodException2)
    {
      throw new AssertionError(localNoSuchMethodException2);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\server\Invoker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */