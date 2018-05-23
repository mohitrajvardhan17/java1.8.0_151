package com.sun.xml.internal.ws.server;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;
import com.sun.xml.internal.ws.api.pipe.helper.AbstractTubeImpl;
import com.sun.xml.internal.ws.api.server.AsyncProviderCallback;
import com.sun.xml.internal.ws.api.server.Invoker;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.resources.ServerMessages;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.WebServiceException;

public abstract class InvokerTube<T>
  extends com.sun.xml.internal.ws.server.sei.InvokerTube<Invoker>
  implements EndpointAwareTube
{
  private WSEndpoint endpoint;
  private static final ThreadLocal<Packet> packets = new ThreadLocal();
  private final Invoker wrapper = new Invoker()
  {
    public Object invoke(Packet paramAnonymousPacket, Method paramAnonymousMethod, Object... paramAnonymousVarArgs)
      throws InvocationTargetException, IllegalAccessException
    {
      Packet localPacket = set(paramAnonymousPacket);
      try
      {
        Object localObject1 = ((Invoker)invoker).invoke(paramAnonymousPacket, paramAnonymousMethod, paramAnonymousVarArgs);
        return localObject1;
      }
      finally
      {
        set(localPacket);
      }
    }
    
    public <T> T invokeProvider(Packet paramAnonymousPacket, T paramAnonymousT)
      throws IllegalAccessException, InvocationTargetException
    {
      Packet localPacket = set(paramAnonymousPacket);
      try
      {
        Object localObject1 = ((Invoker)invoker).invokeProvider(paramAnonymousPacket, paramAnonymousT);
        return (T)localObject1;
      }
      finally
      {
        set(localPacket);
      }
    }
    
    public <T> void invokeAsyncProvider(Packet paramAnonymousPacket, T paramAnonymousT, AsyncProviderCallback paramAnonymousAsyncProviderCallback, WebServiceContext paramAnonymousWebServiceContext)
      throws IllegalAccessException, InvocationTargetException
    {
      Packet localPacket = set(paramAnonymousPacket);
      try
      {
        ((Invoker)invoker).invokeAsyncProvider(paramAnonymousPacket, paramAnonymousT, paramAnonymousAsyncProviderCallback, paramAnonymousWebServiceContext);
      }
      finally
      {
        set(localPacket);
      }
    }
    
    private Packet set(Packet paramAnonymousPacket)
    {
      Packet localPacket = (Packet)InvokerTube.packets.get();
      InvokerTube.packets.set(paramAnonymousPacket);
      return localPacket;
    }
  };
  
  protected InvokerTube(Invoker paramInvoker)
  {
    super(paramInvoker);
  }
  
  public void setEndpoint(WSEndpoint paramWSEndpoint)
  {
    endpoint = paramWSEndpoint;
    AbstractWebServiceContext local1 = new AbstractWebServiceContext(paramWSEndpoint)
    {
      @Nullable
      public Packet getRequestPacket()
      {
        Packet localPacket = (Packet)InvokerTube.packets.get();
        return localPacket;
      }
    };
    ((Invoker)invoker).start(local1, paramWSEndpoint);
  }
  
  protected WSEndpoint getEndpoint()
  {
    return endpoint;
  }
  
  @NotNull
  public final Invoker getInvoker(Packet paramPacket)
  {
    return wrapper;
  }
  
  public final AbstractTubeImpl copy(TubeCloner paramTubeCloner)
  {
    paramTubeCloner.add(this, this);
    return this;
  }
  
  public void preDestroy()
  {
    ((Invoker)invoker).dispose();
  }
  
  @NotNull
  public static Packet getCurrentPacket()
  {
    Packet localPacket = (Packet)packets.get();
    if (localPacket == null) {
      throw new WebServiceException(ServerMessages.NO_CURRENT_PACKET());
    }
    return localPacket;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\server\InvokerTube.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */