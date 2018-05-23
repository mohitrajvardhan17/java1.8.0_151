package com.sun.xml.internal.ws.handler;

import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.MessageHeaders;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.message.DOMMessage;
import com.sun.xml.internal.ws.message.EmptyMessageImpl;
import com.sun.xml.internal.ws.message.jaxb.JAXBMessage;
import com.sun.xml.internal.ws.message.source.PayloadSourceMessage;
import com.sun.xml.internal.ws.spi.db.BindingContext;
import com.sun.xml.internal.ws.spi.db.BindingContextFactory;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.util.JAXBSource;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.ws.LogicalMessage;
import javax.xml.ws.WebServiceException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

class LogicalMessageImpl
  implements LogicalMessage
{
  private Packet packet;
  protected BindingContext defaultJaxbContext;
  private ImmutableLM lm = null;
  
  public LogicalMessageImpl(BindingContext paramBindingContext, Packet paramPacket)
  {
    packet = paramPacket;
    defaultJaxbContext = paramBindingContext;
  }
  
  public Source getPayload()
  {
    if (lm == null)
    {
      Source localSource = packet.getMessage().copy().readPayloadAsSource();
      if ((localSource instanceof DOMSource)) {
        lm = createLogicalMessageImpl(localSource);
      }
      return localSource;
    }
    return lm.getPayload();
  }
  
  public void setPayload(Source paramSource)
  {
    lm = createLogicalMessageImpl(paramSource);
  }
  
  private ImmutableLM createLogicalMessageImpl(Source paramSource)
  {
    if (paramSource == null) {
      lm = new EmptyLogicalMessageImpl();
    } else if ((paramSource instanceof DOMSource)) {
      lm = new DOMLogicalMessageImpl((DOMSource)paramSource);
    } else {
      lm = new SourceLogicalMessageImpl(paramSource);
    }
    return lm;
  }
  
  public Object getPayload(BindingContext paramBindingContext)
  {
    if (paramBindingContext == null) {
      paramBindingContext = defaultJaxbContext;
    }
    if (paramBindingContext == null) {
      throw new WebServiceException("JAXBContext parameter cannot be null");
    }
    Object localObject;
    if (lm == null)
    {
      try
      {
        localObject = packet.getMessage().copy().readPayloadAsJAXB(paramBindingContext.createUnmarshaller());
      }
      catch (JAXBException localJAXBException)
      {
        throw new WebServiceException(localJAXBException);
      }
    }
    else
    {
      localObject = lm.getPayload(paramBindingContext);
      lm = new JAXBLogicalMessageImpl(paramBindingContext.getJAXBContext(), localObject);
    }
    return localObject;
  }
  
  public Object getPayload(JAXBContext paramJAXBContext)
  {
    if (paramJAXBContext == null) {
      return getPayload(defaultJaxbContext);
    }
    if (paramJAXBContext == null) {
      throw new WebServiceException("JAXBContext parameter cannot be null");
    }
    Object localObject;
    if (lm == null)
    {
      try
      {
        localObject = packet.getMessage().copy().readPayloadAsJAXB(paramJAXBContext.createUnmarshaller());
      }
      catch (JAXBException localJAXBException)
      {
        throw new WebServiceException(localJAXBException);
      }
    }
    else
    {
      localObject = lm.getPayload(paramJAXBContext);
      lm = new JAXBLogicalMessageImpl(paramJAXBContext, localObject);
    }
    return localObject;
  }
  
  public void setPayload(Object paramObject, BindingContext paramBindingContext)
  {
    if (paramBindingContext == null) {
      paramBindingContext = defaultJaxbContext;
    }
    if (paramObject == null) {
      lm = new EmptyLogicalMessageImpl();
    } else {
      lm = new JAXBLogicalMessageImpl(paramBindingContext.getJAXBContext(), paramObject);
    }
  }
  
  public void setPayload(Object paramObject, JAXBContext paramJAXBContext)
  {
    if (paramJAXBContext == null) {
      setPayload(paramObject, defaultJaxbContext);
    }
    if (paramObject == null) {
      lm = new EmptyLogicalMessageImpl();
    } else {
      lm = new JAXBLogicalMessageImpl(paramJAXBContext, paramObject);
    }
  }
  
  public boolean isPayloadModifed()
  {
    return lm != null;
  }
  
  public Message getMessage(MessageHeaders paramMessageHeaders, AttachmentSet paramAttachmentSet, WSBinding paramWSBinding)
  {
    assert (isPayloadModifed());
    if (isPayloadModifed()) {
      return lm.getMessage(paramMessageHeaders, paramAttachmentSet, paramWSBinding);
    }
    return packet.getMessage();
  }
  
  private class DOMLogicalMessageImpl
    extends LogicalMessageImpl.SourceLogicalMessageImpl
  {
    private DOMSource dom;
    
    public DOMLogicalMessageImpl(DOMSource paramDOMSource)
    {
      super(paramDOMSource);
      dom = paramDOMSource;
    }
    
    public Source getPayload()
    {
      return dom;
    }
    
    public Message getMessage(MessageHeaders paramMessageHeaders, AttachmentSet paramAttachmentSet, WSBinding paramWSBinding)
    {
      Object localObject = dom.getNode();
      if (((Node)localObject).getNodeType() == 9) {
        localObject = ((Document)localObject).getDocumentElement();
      }
      return new DOMMessage(paramWSBinding.getSOAPVersion(), paramMessageHeaders, (Element)localObject, paramAttachmentSet);
    }
  }
  
  private class EmptyLogicalMessageImpl
    extends LogicalMessageImpl.ImmutableLM
  {
    public EmptyLogicalMessageImpl()
    {
      super(null);
    }
    
    public Source getPayload()
    {
      return null;
    }
    
    public Object getPayload(JAXBContext paramJAXBContext)
    {
      return null;
    }
    
    public Object getPayload(BindingContext paramBindingContext)
    {
      return null;
    }
    
    public Message getMessage(MessageHeaders paramMessageHeaders, AttachmentSet paramAttachmentSet, WSBinding paramWSBinding)
    {
      return new EmptyMessageImpl(paramMessageHeaders, paramAttachmentSet, paramWSBinding.getSOAPVersion());
    }
  }
  
  private abstract class ImmutableLM
  {
    private ImmutableLM() {}
    
    public abstract Source getPayload();
    
    public abstract Object getPayload(BindingContext paramBindingContext);
    
    public abstract Object getPayload(JAXBContext paramJAXBContext);
    
    public abstract Message getMessage(MessageHeaders paramMessageHeaders, AttachmentSet paramAttachmentSet, WSBinding paramWSBinding);
  }
  
  private class JAXBLogicalMessageImpl
    extends LogicalMessageImpl.ImmutableLM
  {
    private JAXBContext ctxt;
    private Object o;
    
    public JAXBLogicalMessageImpl(JAXBContext paramJAXBContext, Object paramObject)
    {
      super(null);
      ctxt = paramJAXBContext;
      o = paramObject;
    }
    
    public Source getPayload()
    {
      JAXBContext localJAXBContext = ctxt;
      if (localJAXBContext == null) {
        localJAXBContext = defaultJaxbContext.getJAXBContext();
      }
      try
      {
        return new JAXBSource(localJAXBContext, o);
      }
      catch (JAXBException localJAXBException)
      {
        throw new WebServiceException(localJAXBException);
      }
    }
    
    public Object getPayload(JAXBContext paramJAXBContext)
    {
      try
      {
        Source localSource = getPayload();
        if (localSource == null) {
          return null;
        }
        Unmarshaller localUnmarshaller = paramJAXBContext.createUnmarshaller();
        return localUnmarshaller.unmarshal(localSource);
      }
      catch (JAXBException localJAXBException)
      {
        throw new WebServiceException(localJAXBException);
      }
    }
    
    public Object getPayload(BindingContext paramBindingContext)
    {
      try
      {
        Source localSource = getPayload();
        if (localSource == null) {
          return null;
        }
        Unmarshaller localUnmarshaller = paramBindingContext.createUnmarshaller();
        return localUnmarshaller.unmarshal(localSource);
      }
      catch (JAXBException localJAXBException)
      {
        throw new WebServiceException(localJAXBException);
      }
    }
    
    public Message getMessage(MessageHeaders paramMessageHeaders, AttachmentSet paramAttachmentSet, WSBinding paramWSBinding)
    {
      return JAXBMessage.create(BindingContextFactory.create(ctxt), o, paramWSBinding.getSOAPVersion(), paramMessageHeaders, paramAttachmentSet);
    }
  }
  
  private class SourceLogicalMessageImpl
    extends LogicalMessageImpl.ImmutableLM
  {
    private Source payloadSrc;
    
    public SourceLogicalMessageImpl(Source paramSource)
    {
      super(null);
      payloadSrc = paramSource;
    }
    
    public Source getPayload()
    {
      assert (!(payloadSrc instanceof DOMSource));
      try
      {
        Transformer localTransformer = XmlUtil.newTransformer();
        DOMResult localDOMResult = new DOMResult();
        localTransformer.transform(payloadSrc, localDOMResult);
        DOMSource localDOMSource = new DOMSource(localDOMResult.getNode());
        lm = new LogicalMessageImpl.DOMLogicalMessageImpl(LogicalMessageImpl.this, localDOMSource);
        payloadSrc = null;
        return localDOMSource;
      }
      catch (TransformerException localTransformerException)
      {
        throw new WebServiceException(localTransformerException);
      }
    }
    
    public Object getPayload(JAXBContext paramJAXBContext)
    {
      try
      {
        Source localSource = getPayload();
        if (localSource == null) {
          return null;
        }
        Unmarshaller localUnmarshaller = paramJAXBContext.createUnmarshaller();
        return localUnmarshaller.unmarshal(localSource);
      }
      catch (JAXBException localJAXBException)
      {
        throw new WebServiceException(localJAXBException);
      }
    }
    
    public Object getPayload(BindingContext paramBindingContext)
    {
      try
      {
        Source localSource = getPayload();
        if (localSource == null) {
          return null;
        }
        Unmarshaller localUnmarshaller = paramBindingContext.createUnmarshaller();
        return localUnmarshaller.unmarshal(localSource);
      }
      catch (JAXBException localJAXBException)
      {
        throw new WebServiceException(localJAXBException);
      }
    }
    
    public Message getMessage(MessageHeaders paramMessageHeaders, AttachmentSet paramAttachmentSet, WSBinding paramWSBinding)
    {
      assert (payloadSrc != null);
      return new PayloadSourceMessage(paramMessageHeaders, payloadSrc, paramAttachmentSet, paramWSBinding.getSOAPVersion());
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\handler\LogicalMessageImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */