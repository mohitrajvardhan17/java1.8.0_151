package com.sun.xml.internal.ws.handler;

import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;
import com.sun.xml.internal.ws.api.pipe.helper.AbstractFilterTubeImpl;
import com.sun.xml.internal.ws.binding.BindingImpl;
import com.sun.xml.internal.ws.client.HandlerConfiguration;
import com.sun.xml.internal.ws.model.AbstractSEIModelImpl;
import com.sun.xml.internal.ws.spi.db.BindingContext;
import java.util.ArrayList;
import java.util.List;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.handler.MessageContext;

public class ClientLogicalHandlerTube
  extends HandlerTube
{
  private SEIModel seiModel;
  
  public ClientLogicalHandlerTube(WSBinding paramWSBinding, SEIModel paramSEIModel, WSDLPort paramWSDLPort, Tube paramTube)
  {
    super(paramTube, paramWSDLPort, paramWSBinding);
    seiModel = paramSEIModel;
  }
  
  public ClientLogicalHandlerTube(WSBinding paramWSBinding, SEIModel paramSEIModel, Tube paramTube, HandlerTube paramHandlerTube)
  {
    super(paramTube, paramHandlerTube, paramWSBinding);
    seiModel = paramSEIModel;
  }
  
  private ClientLogicalHandlerTube(ClientLogicalHandlerTube paramClientLogicalHandlerTube, TubeCloner paramTubeCloner)
  {
    super(paramClientLogicalHandlerTube, paramTubeCloner);
    seiModel = seiModel;
  }
  
  protected void initiateClosing(MessageContext paramMessageContext)
  {
    close(paramMessageContext);
    super.initiateClosing(paramMessageContext);
  }
  
  public AbstractFilterTubeImpl copy(TubeCloner paramTubeCloner)
  {
    return new ClientLogicalHandlerTube(this, paramTubeCloner);
  }
  
  void setUpProcessor()
  {
    if (handlers == null)
    {
      handlers = new ArrayList();
      WSBinding localWSBinding = getBinding();
      List localList = ((BindingImpl)localWSBinding).getHandlerConfig().getLogicalHandlers();
      if (!localList.isEmpty())
      {
        handlers.addAll(localList);
        if (localWSBinding.getSOAPVersion() == null) {
          processor = new XMLHandlerProcessor(this, localWSBinding, handlers);
        } else {
          processor = new SOAPHandlerProcessor(true, this, localWSBinding, handlers);
        }
      }
    }
  }
  
  MessageUpdatableContext getContext(Packet paramPacket)
  {
    return new LogicalMessageContextImpl(getBinding(), getBindingContext(), paramPacket);
  }
  
  private BindingContext getBindingContext()
  {
    return (seiModel != null) && ((seiModel instanceof AbstractSEIModelImpl)) ? ((AbstractSEIModelImpl)seiModel).getBindingContext() : null;
  }
  
  boolean callHandlersOnRequest(MessageUpdatableContext paramMessageUpdatableContext, boolean paramBoolean)
  {
    boolean bool;
    try
    {
      bool = processor.callHandlersRequest(HandlerProcessor.Direction.OUTBOUND, paramMessageUpdatableContext, !paramBoolean);
    }
    catch (WebServiceException localWebServiceException)
    {
      remedyActionTaken = true;
      throw localWebServiceException;
    }
    catch (RuntimeException localRuntimeException)
    {
      remedyActionTaken = true;
      throw new WebServiceException(localRuntimeException);
    }
    if (!bool) {
      remedyActionTaken = true;
    }
    return bool;
  }
  
  void callHandlersOnResponse(MessageUpdatableContext paramMessageUpdatableContext, boolean paramBoolean)
  {
    try
    {
      processor.callHandlersResponse(HandlerProcessor.Direction.INBOUND, paramMessageUpdatableContext, paramBoolean);
    }
    catch (WebServiceException localWebServiceException)
    {
      throw localWebServiceException;
    }
    catch (RuntimeException localRuntimeException)
    {
      throw new WebServiceException(localRuntimeException);
    }
  }
  
  void closeHandlers(MessageContext paramMessageContext)
  {
    closeClientsideHandlers(paramMessageContext);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\handler\ClientLogicalHandlerTube.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */