package com.sun.xml.internal.ws.handler;

import com.sun.istack.internal.Nullable;
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

public class ClientMessageHandlerTube
  extends HandlerTube
{
  private SEIModel seiModel;
  private Set<String> roles;
  
  public ClientMessageHandlerTube(@Nullable SEIModel paramSEIModel, WSBinding paramWSBinding, WSDLPort paramWSDLPort, Tube paramTube)
  {
    super(paramTube, paramWSDLPort, paramWSBinding);
    seiModel = paramSEIModel;
  }
  
  private ClientMessageHandlerTube(ClientMessageHandlerTube paramClientMessageHandlerTube, TubeCloner paramTubeCloner)
  {
    super(paramClientMessageHandlerTube, paramTubeCloner);
    seiModel = seiModel;
  }
  
  public AbstractFilterTubeImpl copy(TubeCloner paramTubeCloner)
  {
    return new ClientMessageHandlerTube(this, paramTubeCloner);
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
  
  boolean callHandlersOnRequest(MessageUpdatableContext paramMessageUpdatableContext, boolean paramBoolean)
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
  
  void closeHandlers(MessageContext paramMessageContext)
  {
    closeClientsideHandlers(paramMessageContext);
  }
  
  void setUpProcessor()
  {
    if (handlers == null)
    {
      handlers = new ArrayList();
      HandlerConfiguration localHandlerConfiguration = ((BindingImpl)getBinding()).getHandlerConfig();
      List localList = localHandlerConfiguration.getMessageHandlers();
      if (!localList.isEmpty())
      {
        handlers.addAll(localList);
        roles = new HashSet();
        roles.addAll(localHandlerConfiguration.getRoles());
        processor = new SOAPHandlerProcessor(true, this, getBinding(), handlers);
      }
    }
  }
  
  MessageUpdatableContext getContext(Packet paramPacket)
  {
    MessageHandlerContextImpl localMessageHandlerContextImpl = new MessageHandlerContextImpl(seiModel, getBinding(), port, paramPacket, roles);
    return localMessageHandlerContextImpl;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\handler\ClientMessageHandlerTube.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */