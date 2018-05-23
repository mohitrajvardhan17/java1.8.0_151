package com.sun.xml.internal.ws.api.message;

import com.oracle.webservices.internal.api.message.BaseDistributedPropertySet;
import com.oracle.webservices.internal.api.message.BasePropertySet.PropertyMap;
import com.oracle.webservices.internal.api.message.ContentType;
import com.oracle.webservices.internal.api.message.MessageContext;
import com.oracle.webservices.internal.api.message.PropertySet.Property;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.bind.marshaller.SAX2DOMEx;
import com.sun.xml.internal.ws.addressing.WsaPropertyBag;
import com.sun.xml.internal.ws.addressing.WsaTubeHelper;
import com.sun.xml.internal.ws.api.BindingID;
import com.sun.xml.internal.ws.api.Component;
import com.sun.xml.internal.ws.api.DistributedPropertySet;
import com.sun.xml.internal.ws.api.EndpointAddress;
import com.sun.xml.internal.ws.api.PropertySet;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.WSDLOperationMapping;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.pipe.Codec;
import com.sun.xml.internal.ws.api.server.TransportBackChannel;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.api.server.WebServiceContextDelegate;
import com.sun.xml.internal.ws.api.streaming.XMLStreamWriterFactory;
import com.sun.xml.internal.ws.client.ContentNegotiation;
import com.sun.xml.internal.ws.client.HandlerConfiguration;
import com.sun.xml.internal.ws.client.Stub;
import com.sun.xml.internal.ws.message.RelatesToHeader;
import com.sun.xml.internal.ws.message.StringHeader;
import com.sun.xml.internal.ws.resources.AddressingMessages;
import com.sun.xml.internal.ws.util.DOMUtil;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import com.sun.xml.internal.ws.wsdl.DispatchException;
import com.sun.xml.internal.ws.wsdl.OperationDispatcher;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.WritableByteChannel;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.MTOMFeature;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public final class Packet
  extends BaseDistributedPropertySet
  implements MessageContext, MessageMetadata
{
  private Message message;
  private WSDLOperationMapping wsdlOperationMapping = null;
  private QName wsdlOperation;
  public boolean wasTransportSecure;
  public static final String INBOUND_TRANSPORT_HEADERS = "com.sun.xml.internal.ws.api.message.packet.inbound.transport.headers";
  public static final String OUTBOUND_TRANSPORT_HEADERS = "com.sun.xml.internal.ws.api.message.packet.outbound.transport.headers";
  public static final String HA_INFO = "com.sun.xml.internal.ws.api.message.packet.hainfo";
  @PropertySet.Property({"com.sun.xml.internal.ws.handler.config"})
  public HandlerConfiguration handlerConfig;
  @PropertySet.Property({"com.sun.xml.internal.ws.client.handle"})
  public BindingProvider proxy;
  public boolean isAdapterDeliversNonAnonymousResponse;
  public boolean packetTakesPriorityOverRequestContext = false;
  public EndpointAddress endpointAddress;
  public ContentNegotiation contentNegotiation;
  public String acceptableMimeTypes;
  public WebServiceContextDelegate webServiceContextDelegate;
  @Nullable
  public TransportBackChannel transportBackChannel;
  public Component component;
  @PropertySet.Property({"com.sun.xml.internal.ws.api.server.WSEndpoint"})
  public WSEndpoint endpoint;
  @PropertySet.Property({"javax.xml.ws.soap.http.soapaction.uri"})
  public String soapAction;
  @PropertySet.Property({"com.sun.xml.internal.ws.server.OneWayOperation"})
  public Boolean expectReply;
  @Deprecated
  public Boolean isOneWay;
  public Boolean isSynchronousMEP;
  public Boolean nonNullAsyncHandlerGiven;
  private Boolean isRequestReplyMEP;
  private Set<String> handlerScopePropertyNames;
  public final Map<String, Object> invocationProperties;
  private static final BasePropertySet.PropertyMap model = parse(Packet.class);
  private static final Logger LOGGER = Logger.getLogger(Packet.class.getName());
  public Codec codec = null;
  private ContentType contentType;
  private Boolean mtomRequest;
  private Boolean mtomAcceptable;
  private MTOMFeature mtomFeature;
  Boolean checkMtomAcceptable;
  private Boolean fastInfosetAcceptable;
  private State state = State.ServerRequest;
  private boolean isFastInfosetDisabled;
  
  public Packet(Message paramMessage)
  {
    this();
    message = paramMessage;
    if (message != null) {
      message.setMessageMedadata(this);
    }
  }
  
  public Packet()
  {
    invocationProperties = new HashMap();
  }
  
  private Packet(Packet paramPacket)
  {
    relatePackets(paramPacket, true);
    invocationProperties = invocationProperties;
  }
  
  public Packet copy(boolean paramBoolean)
  {
    Packet localPacket = new Packet(this);
    if ((paramBoolean) && (message != null)) {
      message = message.copy();
    }
    if (message != null) {
      message.setMessageMedadata(localPacket);
    }
    return localPacket;
  }
  
  public Message getMessage()
  {
    if ((message != null) && (!(message instanceof MessageWrapper))) {
      message = new MessageWrapper(this, message);
    }
    return message;
  }
  
  public Message getInternalMessage()
  {
    return (message instanceof MessageWrapper) ? message).delegate : message;
  }
  
  public WSBinding getBinding()
  {
    if (endpoint != null) {
      return endpoint.getBinding();
    }
    if (proxy != null) {
      return (WSBinding)proxy.getBinding();
    }
    return null;
  }
  
  public void setMessage(Message paramMessage)
  {
    message = paramMessage;
    if (paramMessage != null) {
      message.setMessageMedadata(this);
    }
  }
  
  @PropertySet.Property({"javax.xml.ws.wsdl.operation"})
  @Nullable
  public final QName getWSDLOperation()
  {
    if (wsdlOperation != null) {
      return wsdlOperation;
    }
    if (wsdlOperationMapping == null) {
      wsdlOperationMapping = getWSDLOperationMapping();
    }
    if (wsdlOperationMapping != null) {
      wsdlOperation = wsdlOperationMapping.getOperationName();
    }
    return wsdlOperation;
  }
  
  public WSDLOperationMapping getWSDLOperationMapping()
  {
    if (wsdlOperationMapping != null) {
      return wsdlOperationMapping;
    }
    OperationDispatcher localOperationDispatcher = null;
    if (endpoint != null) {
      localOperationDispatcher = endpoint.getOperationDispatcher();
    } else if (proxy != null) {
      localOperationDispatcher = ((Stub)proxy).getOperationDispatcher();
    }
    if (localOperationDispatcher != null) {
      try
      {
        wsdlOperationMapping = localOperationDispatcher.getWSDLOperationMapping(this);
      }
      catch (DispatchException localDispatchException) {}
    }
    return wsdlOperationMapping;
  }
  
  public void setWSDLOperation(QName paramQName)
  {
    wsdlOperation = paramQName;
  }
  
  /**
   * @deprecated
   */
  @PropertySet.Property({"javax.xml.ws.service.endpoint.address"})
  public String getEndPointAddressString()
  {
    if (endpointAddress == null) {
      return null;
    }
    return endpointAddress.toString();
  }
  
  public void setEndPointAddressString(String paramString)
  {
    if (paramString == null) {
      endpointAddress = null;
    } else {
      endpointAddress = EndpointAddress.create(paramString);
    }
  }
  
  @PropertySet.Property({"com.sun.xml.internal.ws.client.ContentNegotiation"})
  public String getContentNegotiationString()
  {
    return contentNegotiation != null ? contentNegotiation.toString() : null;
  }
  
  public void setContentNegotiationString(String paramString)
  {
    if (paramString == null) {
      contentNegotiation = null;
    } else {
      try
      {
        contentNegotiation = ContentNegotiation.valueOf(paramString);
      }
      catch (IllegalArgumentException localIllegalArgumentException)
      {
        contentNegotiation = ContentNegotiation.none;
      }
    }
  }
  
  @PropertySet.Property({"javax.xml.ws.reference.parameters"})
  @NotNull
  public List<Element> getReferenceParameters()
  {
    Message localMessage = getMessage();
    ArrayList localArrayList = new ArrayList();
    if (localMessage == null) {
      return localArrayList;
    }
    MessageHeaders localMessageHeaders = localMessage.getHeaders();
    Iterator localIterator = localMessageHeaders.asList().iterator();
    while (localIterator.hasNext())
    {
      Header localHeader = (Header)localIterator.next();
      String str = localHeader.getAttribute(W3CnsUri, "IsReferenceParameter");
      if ((str != null) && ((str.equals("true")) || (str.equals("1"))))
      {
        Document localDocument = DOMUtil.createDom();
        SAX2DOMEx localSAX2DOMEx = new SAX2DOMEx(localDocument);
        try
        {
          localHeader.writeTo(localSAX2DOMEx, XmlUtil.DRACONIAN_ERROR_HANDLER);
          localArrayList.add((Element)localDocument.getLastChild());
        }
        catch (SAXException localSAXException)
        {
          throw new WebServiceException(localSAXException);
        }
      }
    }
    return localArrayList;
  }
  
  @PropertySet.Property({"com.sun.xml.internal.ws.api.message.HeaderList"})
  MessageHeaders getHeaderList()
  {
    Message localMessage = getMessage();
    if (localMessage == null) {
      return null;
    }
    return localMessage.getHeaders();
  }
  
  public TransportBackChannel keepTransportBackChannelOpen()
  {
    TransportBackChannel localTransportBackChannel = transportBackChannel;
    transportBackChannel = null;
    return localTransportBackChannel;
  }
  
  public Boolean isRequestReplyMEP()
  {
    return isRequestReplyMEP;
  }
  
  public void setRequestReplyMEP(Boolean paramBoolean)
  {
    isRequestReplyMEP = paramBoolean;
  }
  
  public final Set<String> getHandlerScopePropertyNames(boolean paramBoolean)
  {
    Object localObject = handlerScopePropertyNames;
    if (localObject == null)
    {
      if (paramBoolean) {
        return Collections.emptySet();
      }
      localObject = new HashSet();
      handlerScopePropertyNames = ((Set)localObject);
    }
    return (Set<String>)localObject;
  }
  
  /**
   * @deprecated
   */
  public final Set<String> getApplicationScopePropertyNames(boolean paramBoolean)
  {
    if (!$assertionsDisabled) {
      throw new AssertionError();
    }
    return new HashSet();
  }
  
  @Deprecated
  public Packet createResponse(Message paramMessage)
  {
    Packet localPacket = new Packet(this);
    localPacket.setMessage(paramMessage);
    return localPacket;
  }
  
  public Packet createClientResponse(Message paramMessage)
  {
    Packet localPacket = new Packet(this);
    localPacket.setMessage(paramMessage);
    finishCreateRelateClientResponse(localPacket);
    return localPacket;
  }
  
  public Packet relateClientResponse(Packet paramPacket)
  {
    paramPacket.relatePackets(this, true);
    finishCreateRelateClientResponse(paramPacket);
    return paramPacket;
  }
  
  private void finishCreateRelateClientResponse(Packet paramPacket)
  {
    soapAction = null;
    paramPacket.setState(State.ClientResponse);
  }
  
  public Packet createServerResponse(@Nullable Message paramMessage, @Nullable WSDLPort paramWSDLPort, @Nullable SEIModel paramSEIModel, @NotNull WSBinding paramWSBinding)
  {
    Packet localPacket = createClientResponse(paramMessage);
    return relateServerResponse(localPacket, paramWSDLPort, paramSEIModel, paramWSBinding);
  }
  
  public void copyPropertiesTo(@Nullable Packet paramPacket)
  {
    relatePackets(paramPacket, false);
  }
  
  private void relatePackets(@Nullable Packet paramPacket, boolean paramBoolean)
  {
    Packet localPacket1;
    Packet localPacket2;
    if (!paramBoolean)
    {
      localPacket1 = this;
      localPacket2 = paramPacket;
      soapAction = null;
      invocationProperties.putAll(invocationProperties);
      if (getState().equals(State.ServerRequest)) {
        localPacket2.setState(State.ServerResponse);
      }
    }
    else
    {
      localPacket1 = paramPacket;
      localPacket2 = this;
      soapAction = soapAction;
      localPacket2.setState(localPacket1.getState());
    }
    localPacket1.copySatelliteInto(localPacket2);
    isAdapterDeliversNonAnonymousResponse = isAdapterDeliversNonAnonymousResponse;
    handlerConfig = handlerConfig;
    handlerScopePropertyNames = handlerScopePropertyNames;
    contentNegotiation = contentNegotiation;
    wasTransportSecure = wasTransportSecure;
    transportBackChannel = transportBackChannel;
    endpointAddress = endpointAddress;
    wsdlOperation = wsdlOperation;
    wsdlOperationMapping = wsdlOperationMapping;
    acceptableMimeTypes = acceptableMimeTypes;
    endpoint = endpoint;
    proxy = proxy;
    webServiceContextDelegate = webServiceContextDelegate;
    expectReply = expectReply;
    component = component;
    mtomAcceptable = mtomAcceptable;
    mtomRequest = mtomRequest;
  }
  
  public Packet relateServerResponse(@Nullable Packet paramPacket, @Nullable WSDLPort paramWSDLPort, @Nullable SEIModel paramSEIModel, @NotNull WSBinding paramWSBinding)
  {
    relatePackets(paramPacket, false);
    paramPacket.setState(State.ServerResponse);
    AddressingVersion localAddressingVersion = paramWSBinding.getAddressingVersion();
    if (localAddressingVersion == null) {
      return paramPacket;
    }
    if (getMessage() == null) {
      return paramPacket;
    }
    String str = AddressingUtils.getAction(getMessage().getHeaders(), localAddressingVersion, paramWSBinding.getSOAPVersion());
    if (str == null) {
      return paramPacket;
    }
    if ((paramPacket.getMessage() == null) || ((paramWSDLPort != null) && (getMessage().isOneWay(paramWSDLPort)))) {
      return paramPacket;
    }
    populateAddressingHeaders(paramWSBinding, paramPacket, paramWSDLPort, paramSEIModel);
    return paramPacket;
  }
  
  public Packet createServerResponse(@Nullable Message paramMessage, @NotNull AddressingVersion paramAddressingVersion, @NotNull SOAPVersion paramSOAPVersion, @NotNull String paramString)
  {
    Packet localPacket = createClientResponse(paramMessage);
    localPacket.setState(State.ServerResponse);
    if (paramAddressingVersion == null) {
      return localPacket;
    }
    String str = AddressingUtils.getAction(getMessage().getHeaders(), paramAddressingVersion, paramSOAPVersion);
    if (str == null) {
      return localPacket;
    }
    populateAddressingHeaders(localPacket, paramAddressingVersion, paramSOAPVersion, paramString, false);
    return localPacket;
  }
  
  public void setResponseMessage(@NotNull Packet paramPacket, @Nullable Message paramMessage, @NotNull AddressingVersion paramAddressingVersion, @NotNull SOAPVersion paramSOAPVersion, @NotNull String paramString)
  {
    Packet localPacket = paramPacket.createServerResponse(paramMessage, paramAddressingVersion, paramSOAPVersion, paramString);
    setMessage(localPacket.getMessage());
  }
  
  private void populateAddressingHeaders(Packet paramPacket, AddressingVersion paramAddressingVersion, SOAPVersion paramSOAPVersion, String paramString, boolean paramBoolean)
  {
    if (paramAddressingVersion == null) {
      return;
    }
    if (paramPacket.getMessage() == null) {
      return;
    }
    MessageHeaders localMessageHeaders = paramPacket.getMessage().getHeaders();
    WsaPropertyBag localWsaPropertyBag = (WsaPropertyBag)getSatellite(WsaPropertyBag.class);
    Message localMessage = getMessage();
    WSEndpointReference localWSEndpointReference1 = null;
    Header localHeader1 = AddressingUtils.getFirstHeader(localMessage.getHeaders(), replyToTag, true, paramSOAPVersion);
    Header localHeader2 = localMessageHeaders.get(toTag, false);
    int i = 1;
    try
    {
      if (localHeader1 != null) {
        localWSEndpointReference1 = localHeader1.readAsEPR(paramAddressingVersion);
      }
      if ((localHeader2 != null) && (localWSEndpointReference1 == null)) {
        i = 0;
      }
    }
    catch (XMLStreamException localXMLStreamException)
    {
      throw new WebServiceException(AddressingMessages.REPLY_TO_CANNOT_PARSE(), localXMLStreamException);
    }
    if (localWSEndpointReference1 == null) {
      localWSEndpointReference1 = AddressingUtils.getReplyTo(localMessage.getHeaders(), paramAddressingVersion, paramSOAPVersion);
    }
    if (AddressingUtils.getAction(paramPacket.getMessage().getHeaders(), paramAddressingVersion, paramSOAPVersion) == null) {
      localMessageHeaders.add(new StringHeader(actionTag, paramString, paramSOAPVersion, paramBoolean));
    }
    if (paramPacket.getMessage().getHeaders().get(messageIDTag, false) == null)
    {
      str = Message.generateMessageID();
      localMessageHeaders.add(new StringHeader(messageIDTag, str));
    }
    String str = null;
    if (localWsaPropertyBag != null) {
      str = localWsaPropertyBag.getMessageID();
    }
    if (str == null) {
      str = AddressingUtils.getMessageID(localMessage.getHeaders(), paramAddressingVersion, paramSOAPVersion);
    }
    if (str != null) {
      localMessageHeaders.addOrReplace(new RelatesToHeader(relatesToTag, str));
    }
    WSEndpointReference localWSEndpointReference2 = null;
    if (paramPacket.getMessage().isFault())
    {
      if (localWsaPropertyBag != null) {
        localWSEndpointReference2 = localWsaPropertyBag.getFaultToFromRequest();
      }
      if (localWSEndpointReference2 == null) {
        localWSEndpointReference2 = AddressingUtils.getFaultTo(localMessage.getHeaders(), paramAddressingVersion, paramSOAPVersion);
      }
      if (localWSEndpointReference2 == null) {
        localWSEndpointReference2 = localWSEndpointReference1;
      }
    }
    else
    {
      localWSEndpointReference2 = localWSEndpointReference1;
    }
    if ((i != 0) && (localWSEndpointReference2 != null))
    {
      localMessageHeaders.addOrReplace(new StringHeader(toTag, localWSEndpointReference2.getAddress()));
      localWSEndpointReference2.addReferenceParametersToList(localMessageHeaders);
    }
  }
  
  private void populateAddressingHeaders(WSBinding paramWSBinding, Packet paramPacket, WSDLPort paramWSDLPort, SEIModel paramSEIModel)
  {
    AddressingVersion localAddressingVersion = paramWSBinding.getAddressingVersion();
    if (localAddressingVersion == null) {
      return;
    }
    WsaTubeHelper localWsaTubeHelper = localAddressingVersion.getWsaHelper(paramWSDLPort, paramSEIModel, paramWSBinding);
    String str = paramPacket.getMessage().isFault() ? localWsaTubeHelper.getFaultAction(this, paramPacket) : localWsaTubeHelper.getOutputAction(this);
    if (str == null)
    {
      LOGGER.info("WSA headers are not added as value for wsa:Action cannot be resolved for this message");
      return;
    }
    populateAddressingHeaders(paramPacket, localAddressingVersion, paramWSBinding.getSOAPVersion(), str, AddressingVersion.isRequired(paramWSBinding));
  }
  
  public String toShortString()
  {
    return super.toString();
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(super.toString());
    String str;
    try
    {
      Message localMessage = getMessage();
      if (localMessage != null)
      {
        ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
        XMLStreamWriter localXMLStreamWriter = XMLStreamWriterFactory.create(localByteArrayOutputStream, "UTF-8");
        localMessage.copy().writeTo(localXMLStreamWriter);
        localXMLStreamWriter.flush();
        localXMLStreamWriter.close();
        localByteArrayOutputStream.flush();
        XMLStreamWriterFactory.recycle(localXMLStreamWriter);
        byte[] arrayOfByte = localByteArrayOutputStream.toByteArray();
        str = new String(arrayOfByte, "UTF-8");
      }
      else
      {
        str = "<none>";
      }
    }
    catch (Throwable localThrowable)
    {
      throw new WebServiceException(localThrowable);
    }
    localStringBuilder.append(" Content: ").append(str);
    return localStringBuilder.toString();
  }
  
  protected BasePropertySet.PropertyMap getPropertyMap()
  {
    return model;
  }
  
  public Map<String, Object> asMapIncludingInvocationProperties()
  {
    final Map localMap = asMap();
    new AbstractMap()
    {
      public Object get(Object paramAnonymousObject)
      {
        Object localObject = localMap.get(paramAnonymousObject);
        if (localObject != null) {
          return localObject;
        }
        return invocationProperties.get(paramAnonymousObject);
      }
      
      public int size()
      {
        return localMap.size() + invocationProperties.size();
      }
      
      public boolean containsKey(Object paramAnonymousObject)
      {
        if (localMap.containsKey(paramAnonymousObject)) {
          return true;
        }
        return invocationProperties.containsKey(paramAnonymousObject);
      }
      
      public Set<Map.Entry<String, Object>> entrySet()
      {
        final Set localSet1 = localMap.entrySet();
        final Set localSet2 = invocationProperties.entrySet();
        new AbstractSet()
        {
          public Iterator<Map.Entry<String, Object>> iterator()
          {
            final Iterator localIterator1 = localSet1.iterator();
            final Iterator localIterator2 = localSet2.iterator();
            new Iterator()
            {
              public boolean hasNext()
              {
                return (localIterator1.hasNext()) || (localIterator2.hasNext());
              }
              
              public Map.Entry<String, Object> next()
              {
                if (localIterator1.hasNext()) {
                  return (Map.Entry)localIterator1.next();
                }
                return (Map.Entry)localIterator2.next();
              }
              
              public void remove()
              {
                throw new UnsupportedOperationException();
              }
            };
          }
          
          public int size()
          {
            return val$asMap.size() + invocationProperties.size();
          }
        };
      }
      
      public Object put(String paramAnonymousString, Object paramAnonymousObject)
      {
        if (supports(paramAnonymousString)) {
          return localMap.put(paramAnonymousString, paramAnonymousObject);
        }
        return invocationProperties.put(paramAnonymousString, paramAnonymousObject);
      }
      
      public void clear()
      {
        localMap.clear();
        invocationProperties.clear();
      }
      
      public Object remove(Object paramAnonymousObject)
      {
        if (supports(paramAnonymousObject)) {
          return localMap.remove(paramAnonymousObject);
        }
        return invocationProperties.remove(paramAnonymousObject);
      }
    };
  }
  
  public SOAPMessage getSOAPMessage()
    throws SOAPException
  {
    return getAsSOAPMessage();
  }
  
  public SOAPMessage getAsSOAPMessage()
    throws SOAPException
  {
    Message localMessage = getMessage();
    if (localMessage == null) {
      return null;
    }
    if ((localMessage instanceof MessageWritable)) {
      ((MessageWritable)localMessage).setMTOMConfiguration(mtomFeature);
    }
    return localMessage.readAsSOAPMessage(this, getState().isInbound());
  }
  
  public Codec getCodec()
  {
    if (codec != null) {
      return codec;
    }
    if (endpoint != null) {
      codec = endpoint.createCodec();
    }
    WSBinding localWSBinding = getBinding();
    if (localWSBinding != null) {
      codec = localWSBinding.getBindingId().createEncoder(localWSBinding);
    }
    return codec;
  }
  
  public ContentType writeTo(OutputStream paramOutputStream)
    throws IOException
  {
    Message localMessage = getInternalMessage();
    if ((localMessage instanceof MessageWritable))
    {
      ((MessageWritable)localMessage).setMTOMConfiguration(mtomFeature);
      return ((MessageWritable)localMessage).writeTo(paramOutputStream);
    }
    return getCodec().encode(this, paramOutputStream);
  }
  
  public ContentType writeTo(WritableByteChannel paramWritableByteChannel)
  {
    return getCodec().encode(this, paramWritableByteChannel);
  }
  
  public Boolean getMtomRequest()
  {
    return mtomRequest;
  }
  
  public void setMtomRequest(Boolean paramBoolean)
  {
    mtomRequest = paramBoolean;
  }
  
  public Boolean getMtomAcceptable()
  {
    return mtomAcceptable;
  }
  
  public void checkMtomAcceptable()
  {
    if (checkMtomAcceptable == null) {
      if ((acceptableMimeTypes == null) || (isFastInfosetDisabled)) {
        checkMtomAcceptable = Boolean.valueOf(false);
      } else {
        checkMtomAcceptable = Boolean.valueOf(acceptableMimeTypes.indexOf("application/xop+xml") != -1);
      }
    }
    mtomAcceptable = checkMtomAcceptable;
  }
  
  public Boolean getFastInfosetAcceptable(String paramString)
  {
    if (fastInfosetAcceptable == null) {
      if ((acceptableMimeTypes == null) || (isFastInfosetDisabled)) {
        fastInfosetAcceptable = Boolean.valueOf(false);
      } else {
        fastInfosetAcceptable = Boolean.valueOf(acceptableMimeTypes.indexOf(paramString) != -1);
      }
    }
    return fastInfosetAcceptable;
  }
  
  public void setMtomFeature(MTOMFeature paramMTOMFeature)
  {
    mtomFeature = paramMTOMFeature;
  }
  
  public MTOMFeature getMtomFeature()
  {
    WSBinding localWSBinding = getBinding();
    if (localWSBinding != null) {
      return (MTOMFeature)localWSBinding.getFeature(MTOMFeature.class);
    }
    return mtomFeature;
  }
  
  public ContentType getContentType()
  {
    if (contentType == null) {
      contentType = getInternalContentType();
    }
    if (contentType == null) {
      contentType = getCodec().getStaticContentType(this);
    }
    if (contentType == null) {}
    return contentType;
  }
  
  public ContentType getInternalContentType()
  {
    Message localMessage = getInternalMessage();
    if ((localMessage instanceof MessageWritable)) {
      return ((MessageWritable)localMessage).getContentType();
    }
    return contentType;
  }
  
  public void setContentType(ContentType paramContentType)
  {
    contentType = paramContentType;
  }
  
  public State getState()
  {
    return state;
  }
  
  public void setState(State paramState)
  {
    state = paramState;
  }
  
  public boolean shouldUseMtom()
  {
    if (getState().isInbound()) {
      return isMtomContentType();
    }
    return shouldUseMtomOutbound();
  }
  
  private boolean shouldUseMtomOutbound()
  {
    MTOMFeature localMTOMFeature = getMtomFeature();
    if ((localMTOMFeature != null) && (localMTOMFeature.isEnabled()))
    {
      if ((getMtomAcceptable() == null) && (getMtomRequest() == null)) {
        return true;
      }
      if ((getMtomAcceptable() != null) && (getMtomAcceptable().booleanValue()) && (getState().equals(State.ServerResponse))) {
        return true;
      }
      if ((getMtomRequest() != null) && (getMtomRequest().booleanValue()) && (getState().equals(State.ServerResponse))) {
        return true;
      }
      if ((getMtomRequest() != null) && (getMtomRequest().booleanValue()) && (getState().equals(State.ClientRequest))) {
        return true;
      }
    }
    return false;
  }
  
  private boolean isMtomContentType()
  {
    return (getInternalContentType() != null) && (getInternalContentType().getContentType().contains("application/xop+xml"));
  }
  
  /**
   * @deprecated
   */
  public void addSatellite(@NotNull PropertySet paramPropertySet)
  {
    super.addSatellite(paramPropertySet);
  }
  
  /**
   * @deprecated
   */
  public void addSatellite(@NotNull Class paramClass, @NotNull PropertySet paramPropertySet)
  {
    super.addSatellite(paramClass, paramPropertySet);
  }
  
  /**
   * @deprecated
   */
  public void copySatelliteInto(@NotNull DistributedPropertySet paramDistributedPropertySet)
  {
    super.copySatelliteInto(paramDistributedPropertySet);
  }
  
  /**
   * @deprecated
   */
  public void removeSatellite(PropertySet paramPropertySet)
  {
    super.removeSatellite(paramPropertySet);
  }
  
  public void setFastInfosetDisabled(boolean paramBoolean)
  {
    isFastInfosetDisabled = paramBoolean;
  }
  
  public static enum State
  {
    ServerRequest(true),  ClientRequest(false),  ServerResponse(false),  ClientResponse(true);
    
    private boolean inbound;
    
    private State(boolean paramBoolean)
    {
      inbound = paramBoolean;
    }
    
    public boolean isInbound()
    {
      return inbound;
    }
  }
  
  public static enum Status
  {
    Request,  Response,  Unknown;
    
    private Status() {}
    
    public boolean isRequest()
    {
      return Request.equals(this);
    }
    
    public boolean isResponse()
    {
      return Response.equals(this);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\message\Packet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */