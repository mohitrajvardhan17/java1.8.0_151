package com.sun.xml.internal.ws.handler;

import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;
import com.sun.xml.internal.ws.api.pipe.helper.AbstractFilterTubeImpl;
import com.sun.xml.internal.ws.binding.BindingImpl;
import com.sun.xml.internal.ws.client.HandlerConfiguration;
import com.sun.xml.internal.ws.message.DataHandlerAttachment;
import com.sun.xml.internal.ws.model.AbstractSEIModelImpl;
import com.sun.xml.internal.ws.spi.db.BindingContext;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.activation.DataHandler;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.handler.MessageContext;

public class ServerLogicalHandlerTube
  extends HandlerTube
{
  private SEIModel seiModel;
  
  public ServerLogicalHandlerTube(WSBinding paramWSBinding, SEIModel paramSEIModel, WSDLPort paramWSDLPort, Tube paramTube)
  {
    super(paramTube, paramWSDLPort, paramWSBinding);
    seiModel = paramSEIModel;
    setUpHandlersOnce();
  }
  
  public ServerLogicalHandlerTube(WSBinding paramWSBinding, SEIModel paramSEIModel, Tube paramTube, HandlerTube paramHandlerTube)
  {
    super(paramTube, paramHandlerTube, paramWSBinding);
    seiModel = paramSEIModel;
    setUpHandlersOnce();
  }
  
  private ServerLogicalHandlerTube(ServerLogicalHandlerTube paramServerLogicalHandlerTube, TubeCloner paramTubeCloner)
  {
    super(paramServerLogicalHandlerTube, paramTubeCloner);
    seiModel = seiModel;
    handlers = handlers;
  }
  
  protected void initiateClosing(MessageContext paramMessageContext)
  {
    if (getBinding().getSOAPVersion() != null)
    {
      super.initiateClosing(paramMessageContext);
    }
    else
    {
      close(paramMessageContext);
      super.initiateClosing(paramMessageContext);
    }
  }
  
  public AbstractFilterTubeImpl copy(TubeCloner paramTubeCloner)
  {
    return new ServerLogicalHandlerTube(this, paramTubeCloner);
  }
  
  private void setUpHandlersOnce()
  {
    handlers = new ArrayList();
    List localList = ((BindingImpl)getBinding()).getHandlerConfig().getLogicalHandlers();
    if (!localList.isEmpty()) {
      handlers.addAll(localList);
    }
  }
  
  protected void resetProcessor()
  {
    processor = null;
  }
  
  void setUpProcessor()
  {
    if ((!handlers.isEmpty()) && (processor == null)) {
      if (getBinding().getSOAPVersion() == null) {
        processor = new XMLHandlerProcessor(this, getBinding(), handlers);
      } else {
        processor = new SOAPHandlerProcessor(false, this, getBinding(), handlers);
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
      bool = processor.callHandlersRequest(HandlerProcessor.Direction.INBOUND, paramMessageUpdatableContext, !paramBoolean);
    }
    catch (RuntimeException localRuntimeException)
    {
      remedyActionTaken = true;
      throw localRuntimeException;
    }
    if (!bool) {
      remedyActionTaken = true;
    }
    return bool;
  }
  
  void callHandlersOnResponse(MessageUpdatableContext paramMessageUpdatableContext, boolean paramBoolean)
  {
    Map localMap = (Map)paramMessageUpdatableContext.get("javax.xml.ws.binding.attachments.outbound");
    AttachmentSet localAttachmentSet = packet.getMessage().getAttachments();
    Iterator localIterator = localMap.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      String str = (String)localEntry.getKey();
      DataHandlerAttachment localDataHandlerAttachment = new DataHandlerAttachment(str, (DataHandler)localMap.get(str));
      localAttachmentSet.add(localDataHandlerAttachment);
    }
    try
    {
      processor.callHandlersResponse(HandlerProcessor.Direction.OUTBOUND, paramMessageUpdatableContext, paramBoolean);
    }
    catch (WebServiceException localWebServiceException)
    {
      throw localWebServiceException;
    }
    catch (RuntimeException localRuntimeException)
    {
      throw localRuntimeException;
    }
  }
  
  void closeHandlers(MessageContext paramMessageContext)
  {
    closeServersideHandlers(paramMessageContext);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\handler\ServerLogicalHandlerTube.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */