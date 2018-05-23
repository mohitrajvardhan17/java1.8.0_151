package com.sun.xml.internal.ws.handler;

import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;
import com.sun.xml.internal.ws.api.pipe.helper.AbstractFilterTubeImpl;
import com.sun.xml.internal.ws.binding.BindingImpl;
import com.sun.xml.internal.ws.client.HandlerConfiguration;
import com.sun.xml.internal.ws.message.DataHandlerAttachment;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.activation.DataHandler;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.handler.MessageContext;

public class ServerMessageHandlerTube
  extends HandlerTube
{
  private SEIModel seiModel;
  private Set<String> roles;
  
  public ServerMessageHandlerTube(SEIModel paramSEIModel, WSBinding paramWSBinding, Tube paramTube, HandlerTube paramHandlerTube)
  {
    super(paramTube, paramHandlerTube, paramWSBinding);
    seiModel = paramSEIModel;
    setUpHandlersOnce();
  }
  
  private ServerMessageHandlerTube(ServerMessageHandlerTube paramServerMessageHandlerTube, TubeCloner paramTubeCloner)
  {
    super(paramServerMessageHandlerTube, paramTubeCloner);
    seiModel = seiModel;
    handlers = handlers;
    roles = roles;
  }
  
  private void setUpHandlersOnce()
  {
    handlers = new ArrayList();
    HandlerConfiguration localHandlerConfiguration = ((BindingImpl)getBinding()).getHandlerConfig();
    List localList = localHandlerConfiguration.getMessageHandlers();
    if (!localList.isEmpty())
    {
      handlers.addAll(localList);
      roles = new HashSet();
      roles.addAll(localHandlerConfiguration.getRoles());
    }
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
      if (localAttachmentSet.get(str) == null)
      {
        DataHandlerAttachment localDataHandlerAttachment = new DataHandlerAttachment(str, (DataHandler)localMap.get(str));
        localAttachmentSet.add(localDataHandlerAttachment);
      }
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
  
  protected void resetProcessor()
  {
    processor = null;
  }
  
  void setUpProcessor()
  {
    if ((!handlers.isEmpty()) && (processor == null)) {
      processor = new SOAPHandlerProcessor(false, this, getBinding(), handlers);
    }
  }
  
  void closeHandlers(MessageContext paramMessageContext)
  {
    closeServersideHandlers(paramMessageContext);
  }
  
  MessageUpdatableContext getContext(Packet paramPacket)
  {
    MessageHandlerContextImpl localMessageHandlerContextImpl = new MessageHandlerContextImpl(seiModel, getBinding(), port, paramPacket, roles);
    return localMessageHandlerContextImpl;
  }
  
  protected void initiateClosing(MessageContext paramMessageContext)
  {
    close(paramMessageContext);
    super.initiateClosing(paramMessageContext);
  }
  
  public AbstractFilterTubeImpl copy(TubeCloner paramTubeCloner)
  {
    return new ServerMessageHandlerTube(this, paramTubeCloner);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\handler\ServerMessageHandlerTube.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */