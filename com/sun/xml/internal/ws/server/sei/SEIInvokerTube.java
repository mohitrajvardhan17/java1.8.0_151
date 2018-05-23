package com.sun.xml.internal.ws.server.sei;

import com.oracle.webservices.internal.api.databinding.JavaCallInfo;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.databinding.Databinding;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.NextAction;
import com.sun.xml.internal.ws.api.server.Invoker;
import com.sun.xml.internal.ws.api.server.TransportBackChannel;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.model.AbstractSEIModelImpl;
import com.sun.xml.internal.ws.server.InvokerTube;
import com.sun.xml.internal.ws.wsdl.DispatchException;
import java.lang.reflect.InvocationTargetException;

public class SEIInvokerTube
  extends InvokerTube
{
  private final WSBinding binding;
  private final AbstractSEIModelImpl model;
  
  public SEIInvokerTube(AbstractSEIModelImpl paramAbstractSEIModelImpl, Invoker paramInvoker, WSBinding paramWSBinding)
  {
    super(paramInvoker);
    binding = paramWSBinding;
    model = paramAbstractSEIModelImpl;
  }
  
  @NotNull
  public NextAction processRequest(@NotNull Packet paramPacket)
  {
    JavaCallInfo localJavaCallInfo = model.getDatabinding().deserializeRequest(paramPacket);
    if (localJavaCallInfo.getException() == null)
    {
      try
      {
        if ((paramPacket.getMessage().isOneWay(model.getPort())) && (transportBackChannel != null)) {
          transportBackChannel.close();
        }
        Object localObject1 = getInvoker(paramPacket).invoke(paramPacket, localJavaCallInfo.getMethod(), localJavaCallInfo.getParameters());
        localJavaCallInfo.setReturnValue(localObject1);
      }
      catch (InvocationTargetException localInvocationTargetException)
      {
        localJavaCallInfo.setException(localInvocationTargetException);
      }
      catch (Exception localException)
      {
        localJavaCallInfo.setException(localException);
      }
    }
    else if ((localJavaCallInfo.getException() instanceof DispatchException))
    {
      localObject2 = (DispatchException)localJavaCallInfo.getException();
      return doReturnWith(paramPacket.createServerResponse(fault, model.getPort(), null, binding));
    }
    Object localObject2 = (Packet)model.getDatabinding().serializeResponse(localJavaCallInfo);
    localObject2 = paramPacket.relateServerResponse((Packet)localObject2, endpoint.getPort(), model, endpoint.getBinding());
    assert (localObject2 != null);
    return doReturnWith((Packet)localObject2);
  }
  
  @NotNull
  public NextAction processResponse(@NotNull Packet paramPacket)
  {
    return doReturnWith(paramPacket);
  }
  
  @NotNull
  public NextAction processException(@NotNull Throwable paramThrowable)
  {
    return doThrow(paramThrowable);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\server\sei\SEIInvokerTube.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */