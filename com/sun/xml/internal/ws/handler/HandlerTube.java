package com.sun.xml.internal.ws.handler;

import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.pipe.Fiber;
import com.sun.xml.internal.ws.api.pipe.NextAction;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;
import com.sun.xml.internal.ws.api.pipe.helper.AbstractFilterTubeImpl;
import com.sun.xml.internal.ws.api.server.TransportBackChannel;
import com.sun.xml.internal.ws.binding.BindingImpl;
import com.sun.xml.internal.ws.client.HandlerConfiguration;
import java.util.List;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.MessageContext;

public abstract class HandlerTube
  extends AbstractFilterTubeImpl
{
  HandlerTube cousinTube;
  protected List<Handler> handlers;
  HandlerProcessor processor;
  boolean remedyActionTaken = false;
  @Nullable
  protected final WSDLPort port;
  boolean requestProcessingSucessful = false;
  private WSBinding binding;
  private HandlerConfiguration hc;
  private HandlerTubeExchange exchange;
  
  public HandlerTube(Tube paramTube, WSDLPort paramWSDLPort, WSBinding paramWSBinding)
  {
    super(paramTube);
    port = paramWSDLPort;
    binding = paramWSBinding;
  }
  
  public HandlerTube(Tube paramTube, HandlerTube paramHandlerTube, WSBinding paramWSBinding)
  {
    super(paramTube);
    cousinTube = paramHandlerTube;
    binding = paramWSBinding;
    if (paramHandlerTube != null) {
      port = port;
    } else {
      port = null;
    }
  }
  
  protected HandlerTube(HandlerTube paramHandlerTube, TubeCloner paramTubeCloner)
  {
    super(paramHandlerTube, paramTubeCloner);
    if (cousinTube != null) {
      cousinTube = ((HandlerTube)paramTubeCloner.copy(cousinTube));
    }
    port = port;
    binding = binding;
  }
  
  protected WSBinding getBinding()
  {
    return binding;
  }
  
  public NextAction processRequest(Packet paramPacket)
  {
    setupExchange();
    if (isHandleFalse())
    {
      remedyActionTaken = true;
      return doInvoke(next, paramPacket);
    }
    setUpProcessorInternal();
    MessageUpdatableContext localMessageUpdatableContext = getContext(paramPacket);
    boolean bool1 = checkOneWay(paramPacket);
    try
    {
      if (!isHandlerChainEmpty())
      {
        boolean bool2 = callHandlersOnRequest(localMessageUpdatableContext, bool1);
        localMessageUpdatableContext.updatePacket();
        if ((!bool1) && (!bool2))
        {
          localNextAction2 = doReturnWith(paramPacket);
          return localNextAction2;
        }
      }
      requestProcessingSucessful = true;
      NextAction localNextAction1 = doInvoke(next, paramPacket);
      return localNextAction1;
    }
    catch (RuntimeException localRuntimeException)
    {
      NextAction localNextAction2;
      if (bool1)
      {
        if (transportBackChannel != null) {
          transportBackChannel.close();
        }
        paramPacket.setMessage(null);
        localNextAction2 = doReturnWith(paramPacket);
        return localNextAction2;
      }
      throw localRuntimeException;
    }
    finally
    {
      if (!requestProcessingSucessful) {
        initiateClosing(localMessageUpdatableContext.getMessageContext());
      }
    }
  }
  
  public NextAction processResponse(Packet paramPacket)
  {
    setupExchange();
    MessageUpdatableContext localMessageUpdatableContext = getContext(paramPacket);
    try
    {
      if ((isHandleFalse()) || (paramPacket.getMessage() == null))
      {
        NextAction localNextAction = doReturnWith(paramPacket);
        return localNextAction;
      }
      setUpProcessorInternal();
      boolean bool = isHandleFault(paramPacket);
      if (!isHandlerChainEmpty()) {
        callHandlersOnResponse(localMessageUpdatableContext, bool);
      }
    }
    finally
    {
      initiateClosing(localMessageUpdatableContext.getMessageContext());
    }
    localMessageUpdatableContext.updatePacket();
    return doReturnWith(paramPacket);
  }
  
  public NextAction processException(Throwable paramThrowable)
  {
    try
    {
      NextAction localNextAction = doThrow(paramThrowable);
      Packet localPacket1;
      MessageUpdatableContext localMessageUpdatableContext1;
      return localNextAction;
    }
    finally
    {
      Packet localPacket2 = Fiber.current().getPacket();
      MessageUpdatableContext localMessageUpdatableContext2 = getContext(localPacket2);
      initiateClosing(localMessageUpdatableContext2.getMessageContext());
    }
  }
  
  protected void initiateClosing(MessageContext paramMessageContext) {}
  
  public final void close(MessageContext paramMessageContext)
  {
    if ((requestProcessingSucessful) && (cousinTube != null)) {
      cousinTube.close(paramMessageContext);
    }
    if (processor != null) {
      closeHandlers(paramMessageContext);
    }
    exchange = null;
    requestProcessingSucessful = false;
  }
  
  abstract void closeHandlers(MessageContext paramMessageContext);
  
  protected void closeClientsideHandlers(MessageContext paramMessageContext)
  {
    if (processor == null) {
      return;
    }
    if (remedyActionTaken)
    {
      processor.closeHandlers(paramMessageContext, processor.getIndex(), 0);
      processor.setIndex(-1);
      remedyActionTaken = false;
    }
    else
    {
      processor.closeHandlers(paramMessageContext, handlers.size() - 1, 0);
    }
  }
  
  protected void closeServersideHandlers(MessageContext paramMessageContext)
  {
    if (processor == null) {
      return;
    }
    if (remedyActionTaken)
    {
      processor.closeHandlers(paramMessageContext, processor.getIndex(), handlers.size() - 1);
      processor.setIndex(-1);
      remedyActionTaken = false;
    }
    else
    {
      processor.closeHandlers(paramMessageContext, 0, handlers.size() - 1);
    }
  }
  
  abstract void callHandlersOnResponse(MessageUpdatableContext paramMessageUpdatableContext, boolean paramBoolean);
  
  abstract boolean callHandlersOnRequest(MessageUpdatableContext paramMessageUpdatableContext, boolean paramBoolean);
  
  private boolean checkOneWay(Packet paramPacket)
  {
    if (port != null) {
      return paramPacket.getMessage().isOneWay(port);
    }
    return (expectReply == null) || (!expectReply.booleanValue());
  }
  
  private void setUpProcessorInternal()
  {
    HandlerConfiguration localHandlerConfiguration = ((BindingImpl)binding).getHandlerConfig();
    if (localHandlerConfiguration != hc) {
      resetProcessor();
    }
    hc = localHandlerConfiguration;
    setUpProcessor();
  }
  
  abstract void setUpProcessor();
  
  protected void resetProcessor()
  {
    handlers = null;
  }
  
  public final boolean isHandlerChainEmpty()
  {
    return handlers.isEmpty();
  }
  
  abstract MessageUpdatableContext getContext(Packet paramPacket);
  
  private boolean isHandleFault(Packet paramPacket)
  {
    if (cousinTube != null) {
      return exchange.isHandleFault();
    }
    boolean bool = paramPacket.getMessage().isFault();
    exchange.setHandleFault(bool);
    return bool;
  }
  
  final void setHandleFault()
  {
    exchange.setHandleFault(true);
  }
  
  private boolean isHandleFalse()
  {
    return exchange.isHandleFalse();
  }
  
  final void setHandleFalse()
  {
    exchange.setHandleFalse();
  }
  
  private void setupExchange()
  {
    if (exchange == null)
    {
      exchange = new HandlerTubeExchange();
      if (cousinTube != null) {
        cousinTube.exchange = exchange;
      }
    }
    else if (cousinTube != null)
    {
      cousinTube.exchange = exchange;
    }
  }
  
  static final class HandlerTubeExchange
  {
    private boolean handleFalse;
    private boolean handleFault;
    
    HandlerTubeExchange() {}
    
    boolean isHandleFault()
    {
      return handleFault;
    }
    
    void setHandleFault(boolean paramBoolean)
    {
      handleFault = paramBoolean;
    }
    
    public boolean isHandleFalse()
    {
      return handleFalse;
    }
    
    void setHandleFalse()
    {
      handleFalse = true;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\handler\HandlerTube.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */