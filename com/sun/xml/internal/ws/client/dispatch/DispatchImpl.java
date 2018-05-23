package com.sun.xml.internal.ws.client.dispatch;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.BindingID;
import com.sun.xml.internal.ws.api.EndpointAddress;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.api.client.WSPortInfo;
import com.sun.xml.internal.ws.api.message.AddressingUtils;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.message.Packet.State;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLService;
import com.sun.xml.internal.ws.api.pipe.Fiber.CompletionCallback;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.api.server.ContainerResolver;
import com.sun.xml.internal.ws.api.server.ThreadLocalContainerResolver;
import com.sun.xml.internal.ws.binding.BindingImpl;
import com.sun.xml.internal.ws.client.AsyncInvoker;
import com.sun.xml.internal.ws.client.AsyncResponseImpl;
import com.sun.xml.internal.ws.client.RequestContext;
import com.sun.xml.internal.ws.client.ResponseContext;
import com.sun.xml.internal.ws.client.ResponseContextReceiver;
import com.sun.xml.internal.ws.client.Stub;
import com.sun.xml.internal.ws.client.WSServiceDelegate;
import com.sun.xml.internal.ws.encoding.soap.DeserializationException;
import com.sun.xml.internal.ws.fault.SOAPFaultBuilder;
import com.sun.xml.internal.ws.message.AttachmentSetImpl;
import com.sun.xml.internal.ws.message.DataHandlerAttachment;
import com.sun.xml.internal.ws.resources.DispatchMessages;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.DataHandler;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Response;
import javax.xml.ws.Service.Mode;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;

public abstract class DispatchImpl<T>
  extends Stub
  implements Dispatch<T>
{
  private static final Logger LOGGER = Logger.getLogger(DispatchImpl.class.getName());
  final Service.Mode mode;
  final SOAPVersion soapVersion;
  final boolean allowFaultResponseMsg;
  static final long AWAIT_TERMINATION_TIME = 800L;
  static final String HTTP_REQUEST_METHOD_GET = "GET";
  static final String HTTP_REQUEST_METHOD_POST = "POST";
  static final String HTTP_REQUEST_METHOD_PUT = "PUT";
  
  @Deprecated
  protected DispatchImpl(QName paramQName, Service.Mode paramMode, WSServiceDelegate paramWSServiceDelegate, Tube paramTube, BindingImpl paramBindingImpl, @Nullable WSEndpointReference paramWSEndpointReference)
  {
    super(paramQName, paramWSServiceDelegate, paramTube, paramBindingImpl, paramWSServiceDelegate.getWsdlService() != null ? paramWSServiceDelegate.getWsdlService().get(paramQName) : null, paramWSServiceDelegate.getEndpointAddress(paramQName), paramWSEndpointReference);
    mode = paramMode;
    soapVersion = paramBindingImpl.getSOAPVersion();
    allowFaultResponseMsg = false;
  }
  
  protected DispatchImpl(WSPortInfo paramWSPortInfo, Service.Mode paramMode, BindingImpl paramBindingImpl, @Nullable WSEndpointReference paramWSEndpointReference)
  {
    this(paramWSPortInfo, paramMode, paramBindingImpl, paramWSEndpointReference, false);
  }
  
  protected DispatchImpl(WSPortInfo paramWSPortInfo, Service.Mode paramMode, BindingImpl paramBindingImpl, @Nullable WSEndpointReference paramWSEndpointReference, boolean paramBoolean)
  {
    this(paramWSPortInfo, paramMode, paramBindingImpl, null, paramWSEndpointReference, paramBoolean);
  }
  
  protected DispatchImpl(WSPortInfo paramWSPortInfo, Service.Mode paramMode, BindingImpl paramBindingImpl, Tube paramTube, @Nullable WSEndpointReference paramWSEndpointReference, boolean paramBoolean)
  {
    super(paramWSPortInfo, paramBindingImpl, paramTube, paramWSPortInfo.getEndpointAddress(), paramWSEndpointReference);
    mode = paramMode;
    soapVersion = paramBindingImpl.getSOAPVersion();
    allowFaultResponseMsg = paramBoolean;
  }
  
  protected DispatchImpl(WSPortInfo paramWSPortInfo, Service.Mode paramMode, Tube paramTube, BindingImpl paramBindingImpl, @Nullable WSEndpointReference paramWSEndpointReference, boolean paramBoolean)
  {
    super(paramWSPortInfo, paramBindingImpl, paramTube, paramWSPortInfo.getEndpointAddress(), paramWSEndpointReference);
    mode = paramMode;
    soapVersion = paramBindingImpl.getSOAPVersion();
    allowFaultResponseMsg = paramBoolean;
  }
  
  abstract Packet createPacket(T paramT);
  
  abstract T toReturnValue(Packet paramPacket);
  
  public final Response<T> invokeAsync(T paramT)
  {
    Container localContainer = ContainerResolver.getDefault().enterContainer(owner.getContainer());
    try
    {
      if (LOGGER.isLoggable(Level.FINE)) {
        dumpParam(paramT, "invokeAsync(T)");
      }
      DispatchAsyncInvoker localDispatchAsyncInvoker = new DispatchAsyncInvoker(paramT);
      AsyncResponseImpl localAsyncResponseImpl1 = new AsyncResponseImpl(localDispatchAsyncInvoker, null);
      localDispatchAsyncInvoker.setReceiver(localAsyncResponseImpl1);
      localAsyncResponseImpl1.run();
      AsyncResponseImpl localAsyncResponseImpl2 = localAsyncResponseImpl1;
      return localAsyncResponseImpl2;
    }
    finally
    {
      ContainerResolver.getDefault().exitContainer(localContainer);
    }
  }
  
  private void dumpParam(T paramT, String paramString)
  {
    if ((paramT instanceof Packet))
    {
      Packet localPacket = (Packet)paramT;
      if (LOGGER.isLoggable(Level.FINE))
      {
        AddressingVersion localAddressingVersion = getBinding().getAddressingVersion();
        SOAPVersion localSOAPVersion = getBinding().getSOAPVersion();
        String str1 = (localAddressingVersion != null) && (localPacket.getMessage() != null) ? AddressingUtils.getAction(localPacket.getMessage().getHeaders(), localAddressingVersion, localSOAPVersion) : null;
        String str2 = (localAddressingVersion != null) && (localPacket.getMessage() != null) ? AddressingUtils.getMessageID(localPacket.getMessage().getHeaders(), localAddressingVersion, localSOAPVersion) : null;
        LOGGER.fine("In DispatchImpl." + paramString + " for message with action: " + str1 + " and msg ID: " + str2 + " msg: " + localPacket.getMessage());
        if (localPacket.getMessage() == null) {
          LOGGER.fine("Dispatching null message for action: " + str1 + " and msg ID: " + str2);
        }
      }
    }
  }
  
  public final Future<?> invokeAsync(T paramT, AsyncHandler<T> paramAsyncHandler)
  {
    Container localContainer = ContainerResolver.getDefault().enterContainer(owner.getContainer());
    try
    {
      if (LOGGER.isLoggable(Level.FINE)) {
        dumpParam(paramT, "invokeAsync(T, AsyncHandler<T>)");
      }
      DispatchAsyncInvoker localDispatchAsyncInvoker = new DispatchAsyncInvoker(paramT);
      AsyncResponseImpl localAsyncResponseImpl1 = new AsyncResponseImpl(localDispatchAsyncInvoker, paramAsyncHandler);
      localDispatchAsyncInvoker.setReceiver(localAsyncResponseImpl1);
      localDispatchAsyncInvoker.setNonNullAsyncHandlerGiven(paramAsyncHandler != null);
      localAsyncResponseImpl1.run();
      AsyncResponseImpl localAsyncResponseImpl2 = localAsyncResponseImpl1;
      return localAsyncResponseImpl2;
    }
    finally
    {
      ContainerResolver.getDefault().exitContainer(localContainer);
    }
  }
  
  /* Error */
  public final T doInvoke(T paramT, RequestContext paramRequestContext, ResponseContextReceiver paramResponseContextReceiver)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 4
    //   3: aload_1
    //   4: aload_2
    //   5: aload_0
    //   6: getfield 544	com/sun/xml/internal/ws/client/dispatch/DispatchImpl:binding	Lcom/sun/xml/internal/ws/binding/BindingImpl;
    //   9: aload_0
    //   10: getfield 549	com/sun/xml/internal/ws/client/dispatch/DispatchImpl:mode	Ljavax/xml/ws/Service$Mode;
    //   13: invokestatic 593	com/sun/xml/internal/ws/client/dispatch/DispatchImpl:checkNullAllowed	(Ljava/lang/Object;Lcom/sun/xml/internal/ws/client/RequestContext;Lcom/sun/xml/internal/ws/api/WSBinding;Ljavax/xml/ws/Service$Mode;)V
    //   16: aload_0
    //   17: aload_1
    //   18: invokevirtual 582	com/sun/xml/internal/ws/client/dispatch/DispatchImpl:createPacket	(Ljava/lang/Object;)Lcom/sun/xml/internal/ws/api/message/Packet;
    //   21: astore 5
    //   23: aload 5
    //   25: getstatic 541	com/sun/xml/internal/ws/api/message/Packet$State:ClientRequest	Lcom/sun/xml/internal/ws/api/message/Packet$State;
    //   28: invokevirtual 560	com/sun/xml/internal/ws/api/message/Packet:setState	(Lcom/sun/xml/internal/ws/api/message/Packet$State;)V
    //   31: aload_0
    //   32: aload 5
    //   34: aload_2
    //   35: invokevirtual 583	com/sun/xml/internal/ws/client/dispatch/DispatchImpl:resolveEndpointAddress	(Lcom/sun/xml/internal/ws/api/message/Packet;Lcom/sun/xml/internal/ws/client/RequestContext;)V
    //   38: aload_0
    //   39: aload 5
    //   41: iconst_1
    //   42: invokevirtual 578	com/sun/xml/internal/ws/client/dispatch/DispatchImpl:setProperties	(Lcom/sun/xml/internal/ws/api/message/Packet;Z)V
    //   45: aload_0
    //   46: aload 5
    //   48: aload_2
    //   49: aload_3
    //   50: invokevirtual 589	com/sun/xml/internal/ws/client/dispatch/DispatchImpl:process	(Lcom/sun/xml/internal/ws/api/message/Packet;Lcom/sun/xml/internal/ws/client/RequestContext;Lcom/sun/xml/internal/ws/client/ResponseContextReceiver;)Lcom/sun/xml/internal/ws/api/message/Packet;
    //   53: astore 4
    //   55: aload 4
    //   57: invokevirtual 559	com/sun/xml/internal/ws/api/message/Packet:getMessage	()Lcom/sun/xml/internal/ws/api/message/Message;
    //   60: astore 6
    //   62: aload 6
    //   64: ifnull +35 -> 99
    //   67: aload 6
    //   69: invokevirtual 557	com/sun/xml/internal/ws/api/message/Message:isFault	()Z
    //   72: ifeq +27 -> 99
    //   75: aload_0
    //   76: getfield 542	com/sun/xml/internal/ws/client/dispatch/DispatchImpl:allowFaultResponseMsg	Z
    //   79: ifne +20 -> 99
    //   82: aload 6
    //   84: invokestatic 601	com/sun/xml/internal/ws/fault/SOAPFaultBuilder:create	(Lcom/sun/xml/internal/ws/api/message/Message;)Lcom/sun/xml/internal/ws/fault/SOAPFaultBuilder;
    //   87: astore 7
    //   89: aload 7
    //   91: aconst_null
    //   92: invokevirtual 602	com/sun/xml/internal/ws/fault/SOAPFaultBuilder:createException	(Ljava/util/Map;)Ljava/lang/Throwable;
    //   95: checkcast 322	javax/xml/ws/soap/SOAPFaultException
    //   98: athrow
    //   99: goto +42 -> 141
    //   102: astore 5
    //   104: new 289	com/sun/xml/internal/ws/encoding/soap/DeserializationException
    //   107: dup
    //   108: invokestatic 607	com/sun/xml/internal/ws/resources/DispatchMessages:INVALID_RESPONSE_DESERIALIZATION	()Ljava/lang/String;
    //   111: iconst_1
    //   112: anewarray 296	java/lang/Object
    //   115: dup
    //   116: iconst_0
    //   117: aload 5
    //   119: aastore
    //   120: invokespecial 600	com/sun/xml/internal/ws/encoding/soap/DeserializationException:<init>	(Ljava/lang/String;[Ljava/lang/Object;)V
    //   123: athrow
    //   124: astore 5
    //   126: aload 5
    //   128: athrow
    //   129: astore 5
    //   131: new 321	javax/xml/ws/WebServiceException
    //   134: dup
    //   135: aload 5
    //   137: invokespecial 645	javax/xml/ws/WebServiceException:<init>	(Ljava/lang/Throwable;)V
    //   140: athrow
    //   141: aload_0
    //   142: aload 4
    //   144: invokevirtual 584	com/sun/xml/internal/ws/client/dispatch/DispatchImpl:toReturnValue	(Lcom/sun/xml/internal/ws/api/message/Packet;)Ljava/lang/Object;
    //   147: astore 5
    //   149: aload 4
    //   151: ifnull +21 -> 172
    //   154: aload 4
    //   156: getfield 538	com/sun/xml/internal/ws/api/message/Packet:transportBackChannel	Lcom/sun/xml/internal/ws/api/server/TransportBackChannel;
    //   159: ifnull +13 -> 172
    //   162: aload 4
    //   164: getfield 538	com/sun/xml/internal/ws/api/message/Packet:transportBackChannel	Lcom/sun/xml/internal/ws/api/server/TransportBackChannel;
    //   167: invokeinterface 651 1 0
    //   172: aload 5
    //   174: areturn
    //   175: astore 8
    //   177: aload 4
    //   179: ifnull +21 -> 200
    //   182: aload 4
    //   184: getfield 538	com/sun/xml/internal/ws/api/message/Packet:transportBackChannel	Lcom/sun/xml/internal/ws/api/server/TransportBackChannel;
    //   187: ifnull +13 -> 200
    //   190: aload 4
    //   192: getfield 538	com/sun/xml/internal/ws/api/message/Packet:transportBackChannel	Lcom/sun/xml/internal/ws/api/server/TransportBackChannel;
    //   195: invokeinterface 651 1 0
    //   200: aload 8
    //   202: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	203	0	this	DispatchImpl
    //   0	203	1	paramT	T
    //   0	203	2	paramRequestContext	RequestContext
    //   0	203	3	paramResponseContextReceiver	ResponseContextReceiver
    //   1	190	4	localPacket1	Packet
    //   21	26	5	localPacket2	Packet
    //   102	16	5	localJAXBException	JAXBException
    //   124	3	5	localWebServiceException	WebServiceException
    //   129	7	5	localThrowable	Throwable
    //   147	26	5	localObject1	Object
    //   60	23	6	localMessage	Message
    //   87	3	7	localSOAPFaultBuilder	SOAPFaultBuilder
    //   175	26	8	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   3	99	102	javax/xml/bind/JAXBException
    //   3	99	124	javax/xml/ws/WebServiceException
    //   3	99	129	java/lang/Throwable
    //   3	149	175	finally
    //   175	177	175	finally
  }
  
  public final T invoke(T paramT)
  {
    Container localContainer = ContainerResolver.getDefault().enterContainer(owner.getContainer());
    try
    {
      if (LOGGER.isLoggable(Level.FINE)) {
        dumpParam(paramT, "invoke(T)");
      }
      Object localObject1 = doInvoke(paramT, requestContext, this);
      return (T)localObject1;
    }
    finally
    {
      ContainerResolver.getDefault().exitContainer(localContainer);
    }
  }
  
  public final void invokeOneWay(T paramT)
  {
    Container localContainer = ContainerResolver.getDefault().enterContainer(owner.getContainer());
    try
    {
      if (LOGGER.isLoggable(Level.FINE)) {
        dumpParam(paramT, "invokeOneWay(T)");
      }
      try
      {
        checkNullAllowed(paramT, requestContext, binding, mode);
        Packet localPacket = createPacket(paramT);
        localPacket.setState(Packet.State.ClientRequest);
        setProperties(localPacket, false);
        process(localPacket, requestContext, this);
      }
      catch (WebServiceException localWebServiceException)
      {
        throw localWebServiceException;
      }
      catch (Throwable localThrowable)
      {
        throw new WebServiceException(localThrowable);
      }
    }
    finally
    {
      ContainerResolver.getDefault().exitContainer(localContainer);
    }
  }
  
  void setProperties(Packet paramPacket, boolean paramBoolean)
  {
    expectReply = Boolean.valueOf(paramBoolean);
  }
  
  static boolean isXMLHttp(@NotNull WSBinding paramWSBinding)
  {
    return paramWSBinding.getBindingId().equals(BindingID.XML_HTTP);
  }
  
  static boolean isPAYLOADMode(@NotNull Service.Mode paramMode)
  {
    return paramMode == Service.Mode.PAYLOAD;
  }
  
  static void checkNullAllowed(@Nullable Object paramObject, RequestContext paramRequestContext, WSBinding paramWSBinding, Service.Mode paramMode)
  {
    if (paramObject != null) {
      return;
    }
    if (isXMLHttp(paramWSBinding))
    {
      if (methodNotOk(paramRequestContext)) {
        throw new WebServiceException(DispatchMessages.INVALID_NULLARG_XMLHTTP_REQUEST_METHOD("POST", "GET"));
      }
    }
    else if (paramMode == Service.Mode.MESSAGE) {
      throw new WebServiceException(DispatchMessages.INVALID_NULLARG_SOAP_MSGMODE(paramMode.name(), Service.Mode.PAYLOAD.toString()));
    }
  }
  
  static boolean methodNotOk(@NotNull RequestContext paramRequestContext)
  {
    String str1 = (String)paramRequestContext.get("javax.xml.ws.http.request.method");
    String str2 = str1 == null ? "POST" : str1;
    return ("POST".equalsIgnoreCase(str2)) || ("PUT".equalsIgnoreCase(str2));
  }
  
  public static void checkValidSOAPMessageDispatch(WSBinding paramWSBinding, Service.Mode paramMode)
  {
    if (isXMLHttp(paramWSBinding)) {
      throw new WebServiceException(DispatchMessages.INVALID_SOAPMESSAGE_DISPATCH_BINDING("http://www.w3.org/2004/08/wsdl/http", "http://schemas.xmlsoap.org/wsdl/soap/http or http://www.w3.org/2003/05/soap/bindings/HTTP/"));
    }
    if (isPAYLOADMode(paramMode)) {
      throw new WebServiceException(DispatchMessages.INVALID_SOAPMESSAGE_DISPATCH_MSGMODE(paramMode.name(), Service.Mode.MESSAGE.toString()));
    }
  }
  
  public static void checkValidDataSourceDispatch(WSBinding paramWSBinding, Service.Mode paramMode)
  {
    if (!isXMLHttp(paramWSBinding)) {
      throw new WebServiceException(DispatchMessages.INVALID_DATASOURCE_DISPATCH_BINDING("SOAP/HTTP", "http://www.w3.org/2004/08/wsdl/http"));
    }
    if (isPAYLOADMode(paramMode)) {
      throw new WebServiceException(DispatchMessages.INVALID_DATASOURCE_DISPATCH_MSGMODE(paramMode.name(), Service.Mode.MESSAGE.toString()));
    }
  }
  
  @NotNull
  public final QName getPortName()
  {
    return portname;
  }
  
  void resolveEndpointAddress(@NotNull Packet paramPacket, @NotNull RequestContext paramRequestContext)
  {
    boolean bool = packetTakesPriorityOverRequestContext;
    String str1;
    if ((bool) && (endpointAddress != null)) {
      str1 = endpointAddress.toString();
    } else {
      str1 = (String)paramRequestContext.get("javax.xml.ws.service.endpoint.address");
    }
    if (str1 == null)
    {
      if (endpointAddress == null) {
        throw new WebServiceException(DispatchMessages.INVALID_NULLARG_URI());
      }
      str1 = endpointAddress.toString();
    }
    String str2 = null;
    String str3 = null;
    if ((bool) && (invocationProperties.get("javax.xml.ws.http.request.pathinfo") != null)) {
      str2 = (String)invocationProperties.get("javax.xml.ws.http.request.pathinfo");
    } else if (paramRequestContext.get("javax.xml.ws.http.request.pathinfo") != null) {
      str2 = (String)paramRequestContext.get("javax.xml.ws.http.request.pathinfo");
    }
    if ((bool) && (invocationProperties.get("javax.xml.ws.http.request.querystring") != null)) {
      str3 = (String)invocationProperties.get("javax.xml.ws.http.request.querystring");
    } else if (paramRequestContext.get("javax.xml.ws.http.request.querystring") != null) {
      str3 = (String)paramRequestContext.get("javax.xml.ws.http.request.querystring");
    }
    if ((str2 != null) || (str3 != null))
    {
      str2 = checkPath(str2);
      str3 = checkQuery(str3);
      if (str1 != null) {
        try
        {
          URI localURI = new URI(str1);
          str1 = resolveURI(localURI, str2, str3);
        }
        catch (URISyntaxException localURISyntaxException)
        {
          throw new WebServiceException(DispatchMessages.INVALID_URI(str1));
        }
      }
    }
    paramRequestContext.put("javax.xml.ws.service.endpoint.address", str1);
  }
  
  @NotNull
  protected String resolveURI(@NotNull URI paramURI, @Nullable String paramString1, @Nullable String paramString2)
  {
    String str1 = null;
    String str2 = null;
    if (paramString2 != null)
    {
      try
      {
        URI localURI = new URI(null, null, paramURI.getPath(), paramString2, null);
        localObject = paramURI.resolve(localURI);
      }
      catch (URISyntaxException localURISyntaxException)
      {
        throw new WebServiceException(DispatchMessages.INVALID_QUERY_STRING(paramString2));
      }
      str1 = ((URI)localObject).getQuery();
      str2 = ((URI)localObject).getFragment();
    }
    Object localObject = paramString1 != null ? paramString1 : paramURI.getPath();
    try
    {
      StringBuilder localStringBuilder = new StringBuilder();
      if (localObject != null) {
        localStringBuilder.append((String)localObject);
      }
      if (str1 != null)
      {
        localStringBuilder.append("?");
        localStringBuilder.append(str1);
      }
      if (str2 != null)
      {
        localStringBuilder.append("#");
        localStringBuilder.append(str2);
      }
      return new URL(paramURI.toURL(), localStringBuilder.toString()).toExternalForm();
    }
    catch (MalformedURLException localMalformedURLException)
    {
      throw new WebServiceException(DispatchMessages.INVALID_URI_RESOLUTION(localObject));
    }
  }
  
  private static String checkPath(@Nullable String paramString)
  {
    return "/" + paramString;
  }
  
  private static String checkQuery(@Nullable String paramString)
  {
    if (paramString == null) {
      return null;
    }
    if (paramString.indexOf('?') == 0) {
      throw new WebServiceException(DispatchMessages.INVALID_QUERY_LEADING_CHAR(paramString));
    }
    return paramString;
  }
  
  protected AttachmentSet setOutboundAttachments()
  {
    HashMap localHashMap = (HashMap)getRequestContext().get("javax.xml.ws.binding.attachments.outbound");
    if (localHashMap != null)
    {
      ArrayList localArrayList = new ArrayList();
      Iterator localIterator = localHashMap.entrySet().iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        DataHandlerAttachment localDataHandlerAttachment = new DataHandlerAttachment((String)localEntry.getKey(), (DataHandler)localEntry.getValue());
        localArrayList.add(localDataHandlerAttachment);
      }
      return new AttachmentSetImpl(localArrayList);
    }
    return new AttachmentSetImpl();
  }
  
  public void setOutboundHeaders(Object... paramVarArgs)
  {
    throw new UnsupportedOperationException();
  }
  
  @Deprecated
  public static Dispatch<Source> createSourceDispatch(QName paramQName, Service.Mode paramMode, WSServiceDelegate paramWSServiceDelegate, Tube paramTube, BindingImpl paramBindingImpl, WSEndpointReference paramWSEndpointReference)
  {
    if (isXMLHttp(paramBindingImpl)) {
      return new RESTSourceDispatch(paramQName, paramMode, paramWSServiceDelegate, paramTube, paramBindingImpl, paramWSEndpointReference);
    }
    return new SOAPSourceDispatch(paramQName, paramMode, paramWSServiceDelegate, paramTube, paramBindingImpl, paramWSEndpointReference);
  }
  
  public static Dispatch<Source> createSourceDispatch(WSPortInfo paramWSPortInfo, Service.Mode paramMode, BindingImpl paramBindingImpl, WSEndpointReference paramWSEndpointReference)
  {
    if (isXMLHttp(paramBindingImpl)) {
      return new RESTSourceDispatch(paramWSPortInfo, paramMode, paramBindingImpl, paramWSEndpointReference);
    }
    return new SOAPSourceDispatch(paramWSPortInfo, paramMode, paramBindingImpl, paramWSEndpointReference);
  }
  
  private class DispatchAsyncInvoker
    extends AsyncInvoker
  {
    private final T param;
    private final RequestContext rc = requestContext.copy();
    
    DispatchAsyncInvoker()
    {
      Object localObject;
      param = localObject;
    }
    
    public void do_run()
    {
      DispatchImpl.checkNullAllowed(param, rc, binding, mode);
      Packet localPacket = createPacket(param);
      localPacket.setState(Packet.State.ClientRequest);
      nonNullAsyncHandlerGiven = Boolean.valueOf(nonNullAsyncHandlerGiven);
      resolveEndpointAddress(localPacket, rc);
      setProperties(localPacket, true);
      String str1 = null;
      String str2 = null;
      if (DispatchImpl.LOGGER.isLoggable(Level.FINE))
      {
        localObject1 = getBinding().getAddressingVersion();
        localObject2 = getBinding().getSOAPVersion();
        str1 = (localObject1 != null) && (localPacket.getMessage() != null) ? AddressingUtils.getAction(localPacket.getMessage().getHeaders(), (AddressingVersion)localObject1, (SOAPVersion)localObject2) : null;
        str2 = (localObject1 != null) && (localPacket.getMessage() != null) ? AddressingUtils.getMessageID(localPacket.getMessage().getHeaders(), (AddressingVersion)localObject1, (SOAPVersion)localObject2) : null;
        DispatchImpl.LOGGER.fine("In DispatchAsyncInvoker.do_run for async message with action: " + str1 + " and msg ID: " + str2);
      }
      final Object localObject1 = str1;
      final Object localObject2 = str2;
      Fiber.CompletionCallback local1 = new Fiber.CompletionCallback()
      {
        public void onCompletion(@NotNull Packet paramAnonymousPacket)
        {
          if (DispatchImpl.LOGGER.isLoggable(Level.FINE)) {
            DispatchImpl.LOGGER.fine("Done with processAsync in DispatchAsyncInvoker.do_run, and setting response for async message with action: " + localObject1 + " and msg ID: " + localObject2);
          }
          Message localMessage = paramAnonymousPacket.getMessage();
          if (DispatchImpl.LOGGER.isLoggable(Level.FINE)) {
            DispatchImpl.LOGGER.fine("Done with processAsync in DispatchAsyncInvoker.do_run, and setting response for async message with action: " + localObject1 + " and msg ID: " + localObject2 + " msg: " + localMessage);
          }
          try
          {
            if ((localMessage != null) && (localMessage.isFault()) && (!allowFaultResponseMsg))
            {
              SOAPFaultBuilder localSOAPFaultBuilder = SOAPFaultBuilder.create(localMessage);
              throw ((SOAPFaultException)localSOAPFaultBuilder.createException(null));
            }
            responseImpl.setResponseContext(new ResponseContext(paramAnonymousPacket));
            responseImpl.set(toReturnValue(paramAnonymousPacket), null);
          }
          catch (JAXBException localJAXBException)
          {
            responseImpl.set(null, new DeserializationException(DispatchMessages.INVALID_RESPONSE_DESERIALIZATION(), new Object[] { localJAXBException }));
          }
          catch (WebServiceException localWebServiceException)
          {
            responseImpl.set(null, localWebServiceException);
          }
          catch (Throwable localThrowable)
          {
            responseImpl.set(null, new WebServiceException(localThrowable));
          }
        }
        
        public void onCompletion(@NotNull Throwable paramAnonymousThrowable)
        {
          if (DispatchImpl.LOGGER.isLoggable(Level.FINE)) {
            DispatchImpl.LOGGER.fine("Done with processAsync in DispatchAsyncInvoker.do_run, and setting response for async message with action: " + localObject1 + " and msg ID: " + localObject2 + " Throwable: " + paramAnonymousThrowable.toString());
          }
          if ((paramAnonymousThrowable instanceof WebServiceException)) {
            responseImpl.set(null, paramAnonymousThrowable);
          } else {
            responseImpl.set(null, new WebServiceException(paramAnonymousThrowable));
          }
        }
      };
      processAsync(responseImpl, localPacket, rc, local1);
    }
  }
  
  private class Invoker
    implements Callable
  {
    private final T param;
    private final RequestContext rc = requestContext.copy();
    private ResponseContextReceiver receiver;
    
    Invoker()
    {
      Object localObject;
      param = localObject;
    }
    
    public T call()
      throws Exception
    {
      if (DispatchImpl.LOGGER.isLoggable(Level.FINE)) {
        DispatchImpl.this.dumpParam(param, "call()");
      }
      return (T)doInvoke(param, rc, receiver);
    }
    
    void setReceiver(ResponseContextReceiver paramResponseContextReceiver)
    {
      receiver = paramResponseContextReceiver;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\client\dispatch\DispatchImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */