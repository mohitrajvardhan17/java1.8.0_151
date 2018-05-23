package com.sun.corba.se.impl.protocol;

import com.sun.corba.se.impl.corba.RequestImpl;
import com.sun.corba.se.impl.encoding.BufferManagerRead;
import com.sun.corba.se.impl.encoding.BufferManagerReadStream;
import com.sun.corba.se.impl.encoding.BufferManagerWrite;
import com.sun.corba.se.impl.encoding.CDRInputObject;
import com.sun.corba.se.impl.encoding.CDROutputObject;
import com.sun.corba.se.impl.encoding.EncapsOutputStream;
import com.sun.corba.se.impl.logging.InterceptorsSystemException;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.impl.protocol.giopmsgheaders.AddressingDispositionHelper;
import com.sun.corba.se.impl.protocol.giopmsgheaders.FragmentMessage_1_1;
import com.sun.corba.se.impl.protocol.giopmsgheaders.FragmentMessage_1_2;
import com.sun.corba.se.impl.protocol.giopmsgheaders.LocateReplyMessage;
import com.sun.corba.se.impl.protocol.giopmsgheaders.LocateReplyOrReplyMessage;
import com.sun.corba.se.impl.protocol.giopmsgheaders.LocateRequestMessage;
import com.sun.corba.se.impl.protocol.giopmsgheaders.Message;
import com.sun.corba.se.impl.protocol.giopmsgheaders.MessageBase;
import com.sun.corba.se.impl.protocol.giopmsgheaders.MessageHandler;
import com.sun.corba.se.impl.protocol.giopmsgheaders.ReplyMessage;
import com.sun.corba.se.impl.protocol.giopmsgheaders.ReplyMessage_1_1;
import com.sun.corba.se.impl.protocol.giopmsgheaders.RequestMessage;
import com.sun.corba.se.pept.broker.Broker;
import com.sun.corba.se.pept.encoding.InputObject;
import com.sun.corba.se.pept.encoding.OutputObject;
import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.corba.se.pept.protocol.ProtocolHandler;
import com.sun.corba.se.pept.transport.Acceptor;
import com.sun.corba.se.pept.transport.ByteBufferPool;
import com.sun.corba.se.pept.transport.Connection;
import com.sun.corba.se.pept.transport.ContactInfo;
import com.sun.corba.se.pept.transport.EventHandler;
import com.sun.corba.se.pept.transport.ResponseWaitingRoom;
import com.sun.corba.se.pept.transport.Selector;
import com.sun.corba.se.pept.transport.TransportManager;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.ObjectKey;
import com.sun.corba.se.spi.ior.ObjectKeyTemplate;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.ior.iiop.IIOPProfile;
import com.sun.corba.se.spi.ior.iiop.IIOPProfileTemplate;
import com.sun.corba.se.spi.ior.iiop.MaxStreamFormatVersionComponent;
import com.sun.corba.se.spi.oa.OAInvocationInfo;
import com.sun.corba.se.spi.oa.ObjectAdapter;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.ORBVersionFactory;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import com.sun.corba.se.spi.protocol.CorbaProtocolHandler;
import com.sun.corba.se.spi.protocol.CorbaServerRequestDispatcher;
import com.sun.corba.se.spi.protocol.ForwardException;
import com.sun.corba.se.spi.protocol.PIHandler;
import com.sun.corba.se.spi.servicecontext.MaxStreamFormatVersionServiceContext;
import com.sun.corba.se.spi.servicecontext.ORBVersionServiceContext;
import com.sun.corba.se.spi.servicecontext.SendingContextServiceContext;
import com.sun.corba.se.spi.servicecontext.ServiceContext;
import com.sun.corba.se.spi.servicecontext.ServiceContexts;
import com.sun.corba.se.spi.servicecontext.UEInfoServiceContext;
import com.sun.corba.se.spi.servicecontext.UnknownServiceContext;
import com.sun.corba.se.spi.transport.CorbaConnection;
import com.sun.corba.se.spi.transport.CorbaContactInfo;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.util.EmptyStackException;
import java.util.Iterator;
import org.omg.CORBA.Any;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.Environment;
import org.omg.CORBA.ExceptionList;
import org.omg.CORBA.Request;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.UNKNOWN;
import org.omg.CORBA.UnknownUserException;
import org.omg.CORBA.portable.UnknownException;
import org.omg.CORBA_2_3.portable.InputStream;
import sun.corba.OutputStreamFactory;

public class CorbaMessageMediatorImpl
  implements CorbaMessageMediator, CorbaProtocolHandler, MessageHandler
{
  protected ORB orb;
  protected ORBUtilSystemException wrapper;
  protected InterceptorsSystemException interceptorWrapper;
  protected CorbaContactInfo contactInfo;
  protected CorbaConnection connection;
  protected short addrDisposition;
  protected CDROutputObject outputObject;
  protected CDRInputObject inputObject;
  protected Message messageHeader;
  protected RequestMessage requestHeader;
  protected LocateReplyOrReplyMessage replyHeader;
  protected String replyExceptionDetailMessage;
  protected IOR replyIOR;
  protected Integer requestIdInteger;
  protected Message dispatchHeader;
  protected ByteBuffer dispatchByteBuffer;
  protected byte streamFormatVersion;
  protected boolean streamFormatVersionSet = false;
  protected Request diiRequest;
  protected boolean cancelRequestAlreadySent = false;
  protected ProtocolHandler protocolHandler;
  protected boolean _executeReturnServantInResponseConstructor = false;
  protected boolean _executeRemoveThreadInfoInResponseConstructor = false;
  protected boolean _executePIInResponseConstructor = false;
  protected boolean isThreadDone = false;
  
  public CorbaMessageMediatorImpl(ORB paramORB, ContactInfo paramContactInfo, Connection paramConnection, GIOPVersion paramGIOPVersion, IOR paramIOR, int paramInt, short paramShort, String paramString, boolean paramBoolean)
  {
    this(paramORB, paramConnection);
    contactInfo = ((CorbaContactInfo)paramContactInfo);
    addrDisposition = paramShort;
    streamFormatVersion = getStreamFormatVersionForThisRequest(contactInfo.getEffectiveTargetIOR(), paramGIOPVersion);
    requestHeader = MessageBase.createRequest(orb, paramGIOPVersion, ORBUtility.getEncodingVersion(paramORB, paramIOR), paramInt, !paramBoolean, contactInfo.getEffectiveTargetIOR(), addrDisposition, paramString, new ServiceContexts(paramORB), null);
  }
  
  public CorbaMessageMediatorImpl(ORB paramORB, Connection paramConnection)
  {
    orb = paramORB;
    connection = ((CorbaConnection)paramConnection);
    wrapper = ORBUtilSystemException.get(paramORB, "rpc.protocol");
    interceptorWrapper = InterceptorsSystemException.get(paramORB, "rpc.protocol");
  }
  
  public CorbaMessageMediatorImpl(ORB paramORB, CorbaConnection paramCorbaConnection, Message paramMessage, ByteBuffer paramByteBuffer)
  {
    this(paramORB, paramCorbaConnection);
    dispatchHeader = paramMessage;
    dispatchByteBuffer = paramByteBuffer;
  }
  
  public Broker getBroker()
  {
    return orb;
  }
  
  public ContactInfo getContactInfo()
  {
    return contactInfo;
  }
  
  public Connection getConnection()
  {
    return connection;
  }
  
  public void initializeMessage()
  {
    getRequestHeader().write(outputObject);
  }
  
  public void finishSendingRequest()
  {
    outputObject.finishSendingMessage();
  }
  
  public InputObject waitForResponse()
  {
    if (getRequestHeader().isResponseExpected()) {
      return connection.waitForResponse(this);
    }
    return null;
  }
  
  public void setOutputObject(OutputObject paramOutputObject)
  {
    outputObject = ((CDROutputObject)paramOutputObject);
  }
  
  public OutputObject getOutputObject()
  {
    return outputObject;
  }
  
  public void setInputObject(InputObject paramInputObject)
  {
    inputObject = ((CDRInputObject)paramInputObject);
  }
  
  public InputObject getInputObject()
  {
    return inputObject;
  }
  
  public void setReplyHeader(LocateReplyOrReplyMessage paramLocateReplyOrReplyMessage)
  {
    replyHeader = paramLocateReplyOrReplyMessage;
    replyIOR = paramLocateReplyOrReplyMessage.getIOR();
  }
  
  public LocateReplyMessage getLocateReplyHeader()
  {
    return (LocateReplyMessage)replyHeader;
  }
  
  public ReplyMessage getReplyHeader()
  {
    return (ReplyMessage)replyHeader;
  }
  
  public void setReplyExceptionDetailMessage(String paramString)
  {
    replyExceptionDetailMessage = paramString;
  }
  
  public RequestMessage getRequestHeader()
  {
    return requestHeader;
  }
  
  public GIOPVersion getGIOPVersion()
  {
    if (messageHeader != null) {
      return messageHeader.getGIOPVersion();
    }
    return getRequestHeader().getGIOPVersion();
  }
  
  public byte getEncodingVersion()
  {
    if (messageHeader != null) {
      return messageHeader.getEncodingVersion();
    }
    return getRequestHeader().getEncodingVersion();
  }
  
  public int getRequestId()
  {
    return getRequestHeader().getRequestId();
  }
  
  public Integer getRequestIdInteger()
  {
    if (requestIdInteger == null) {
      requestIdInteger = new Integer(getRequestHeader().getRequestId());
    }
    return requestIdInteger;
  }
  
  public boolean isOneWay()
  {
    return !getRequestHeader().isResponseExpected();
  }
  
  public short getAddrDisposition()
  {
    return addrDisposition;
  }
  
  public String getOperationName()
  {
    return getRequestHeader().getOperation();
  }
  
  public ServiceContexts getRequestServiceContexts()
  {
    return getRequestHeader().getServiceContexts();
  }
  
  public ServiceContexts getReplyServiceContexts()
  {
    return getReplyHeader().getServiceContexts();
  }
  
  public void sendCancelRequestIfFinalFragmentNotSent()
  {
    if ((!sentFullMessage()) && (sentFragment()) && (!cancelRequestAlreadySent)) {
      try
      {
        if (orb.subcontractDebugFlag) {
          dprint(".sendCancelRequestIfFinalFragmentNotSent->: " + opAndId(this));
        }
        connection.sendCancelRequestWithLock(getGIOPVersion(), getRequestId());
        cancelRequestAlreadySent = true;
      }
      catch (IOException localIOException)
      {
        if (orb.subcontractDebugFlag) {
          dprint(".sendCancelRequestIfFinalFragmentNotSent: !ERROR : " + opAndId(this), localIOException);
        }
        throw interceptorWrapper.ioexceptionDuringCancelRequest(CompletionStatus.COMPLETED_MAYBE, localIOException);
      }
      finally
      {
        if (orb.subcontractDebugFlag) {
          dprint(".sendCancelRequestIfFinalFragmentNotSent<-: " + opAndId(this));
        }
      }
    }
  }
  
  public boolean sentFullMessage()
  {
    return outputObject.getBufferManager().sentFullMessage();
  }
  
  public boolean sentFragment()
  {
    return outputObject.getBufferManager().sentFragment();
  }
  
  public void setDIIInfo(Request paramRequest)
  {
    diiRequest = paramRequest;
  }
  
  public boolean isDIIRequest()
  {
    return diiRequest != null;
  }
  
  public Exception unmarshalDIIUserException(String paramString, InputStream paramInputStream)
  {
    if (!isDIIRequest()) {
      return null;
    }
    ExceptionList localExceptionList = diiRequest.exceptions();
    try
    {
      for (int i = 0; i < localExceptionList.count(); i++)
      {
        TypeCode localTypeCode = localExceptionList.item(i);
        if (localTypeCode.id().equals(paramString))
        {
          Any localAny = orb.create_any();
          localAny.read_value(paramInputStream, localTypeCode);
          return new UnknownUserException(localAny);
        }
      }
    }
    catch (Exception localException)
    {
      throw wrapper.unexpectedDiiException(localException);
    }
    return wrapper.unknownCorbaExc(CompletionStatus.COMPLETED_MAYBE);
  }
  
  public void setDIIException(Exception paramException)
  {
    diiRequest.env().exception(paramException);
  }
  
  public void handleDIIReply(InputStream paramInputStream)
  {
    if (!isDIIRequest()) {
      return;
    }
    ((RequestImpl)diiRequest).unmarshalReply(paramInputStream);
  }
  
  public Message getDispatchHeader()
  {
    return dispatchHeader;
  }
  
  public void setDispatchHeader(Message paramMessage)
  {
    dispatchHeader = paramMessage;
  }
  
  public ByteBuffer getDispatchBuffer()
  {
    return dispatchByteBuffer;
  }
  
  public void setDispatchBuffer(ByteBuffer paramByteBuffer)
  {
    dispatchByteBuffer = paramByteBuffer;
  }
  
  public int getThreadPoolToUse()
  {
    int i = 0;
    Message localMessage = getDispatchHeader();
    if (localMessage != null) {
      i = localMessage.getThreadPoolToUse();
    }
    return i;
  }
  
  public byte getStreamFormatVersion()
  {
    if (streamFormatVersionSet) {
      return streamFormatVersion;
    }
    return getStreamFormatVersionForReply();
  }
  
  public byte getStreamFormatVersionForReply()
  {
    ServiceContexts localServiceContexts = getRequestServiceContexts();
    MaxStreamFormatVersionServiceContext localMaxStreamFormatVersionServiceContext = (MaxStreamFormatVersionServiceContext)localServiceContexts.get(17);
    if (localMaxStreamFormatVersionServiceContext != null)
    {
      int i = ORBUtility.getMaxStreamFormatVersion();
      int j = localMaxStreamFormatVersionServiceContext.getMaximumStreamFormatVersion();
      return (byte)Math.min(i, j);
    }
    if (getGIOPVersion().lessThan(GIOPVersion.V1_3)) {
      return 1;
    }
    return 2;
  }
  
  public boolean isSystemExceptionReply()
  {
    return replyHeader.getReplyStatus() == 2;
  }
  
  public boolean isUserExceptionReply()
  {
    return replyHeader.getReplyStatus() == 1;
  }
  
  public boolean isLocationForwardReply()
  {
    return (replyHeader.getReplyStatus() == 3) || (replyHeader.getReplyStatus() == 4);
  }
  
  public boolean isDifferentAddrDispositionRequestedReply()
  {
    return replyHeader.getReplyStatus() == 5;
  }
  
  public short getAddrDispositionReply()
  {
    return replyHeader.getAddrDisposition();
  }
  
  public IOR getForwardedIOR()
  {
    return replyHeader.getIOR();
  }
  
  public SystemException getSystemExceptionReply()
  {
    return replyHeader.getSystemException(replyExceptionDetailMessage);
  }
  
  public ObjectKey getObjectKey()
  {
    return getRequestHeader().getObjectKey();
  }
  
  public void setProtocolHandler(CorbaProtocolHandler paramCorbaProtocolHandler)
  {
    throw wrapper.methodShouldNotBeCalled();
  }
  
  public CorbaProtocolHandler getProtocolHandler()
  {
    return this;
  }
  
  public org.omg.CORBA.portable.OutputStream createReply()
  {
    getProtocolHandler().createResponse(this, (ServiceContexts)null);
    return (org.omg.CORBA_2_3.portable.OutputStream)getOutputObject();
  }
  
  public org.omg.CORBA.portable.OutputStream createExceptionReply()
  {
    getProtocolHandler().createUserExceptionResponse(this, (ServiceContexts)null);
    return (org.omg.CORBA_2_3.portable.OutputStream)getOutputObject();
  }
  
  public boolean executeReturnServantInResponseConstructor()
  {
    return _executeReturnServantInResponseConstructor;
  }
  
  public void setExecuteReturnServantInResponseConstructor(boolean paramBoolean)
  {
    _executeReturnServantInResponseConstructor = paramBoolean;
  }
  
  public boolean executeRemoveThreadInfoInResponseConstructor()
  {
    return _executeRemoveThreadInfoInResponseConstructor;
  }
  
  public void setExecuteRemoveThreadInfoInResponseConstructor(boolean paramBoolean)
  {
    _executeRemoveThreadInfoInResponseConstructor = paramBoolean;
  }
  
  public boolean executePIInResponseConstructor()
  {
    return _executePIInResponseConstructor;
  }
  
  public void setExecutePIInResponseConstructor(boolean paramBoolean)
  {
    _executePIInResponseConstructor = paramBoolean;
  }
  
  private byte getStreamFormatVersionForThisRequest(IOR paramIOR, GIOPVersion paramGIOPVersion)
  {
    int i = ORBUtility.getMaxStreamFormatVersion();
    IOR localIOR = contactInfo.getEffectiveTargetIOR();
    IIOPProfileTemplate localIIOPProfileTemplate = (IIOPProfileTemplate)localIOR.getProfile().getTaggedProfileTemplate();
    Iterator localIterator = localIIOPProfileTemplate.iteratorById(38);
    if (!localIterator.hasNext())
    {
      if (paramGIOPVersion.lessThan(GIOPVersion.V1_3)) {
        return 1;
      }
      return 2;
    }
    int j = ((MaxStreamFormatVersionComponent)localIterator.next()).getMaxStreamFormatVersion();
    return (byte)Math.min(i, j);
  }
  
  public boolean handleRequest(MessageMediator paramMessageMediator)
  {
    try
    {
      dispatchHeader.callback(this);
    }
    catch (IOException localIOException) {}
    return isThreadDone;
  }
  
  private void setWorkThenPoolOrResumeSelect(Message paramMessage)
  {
    if (getConnection().getEventHandler().shouldUseSelectThreadToWait())
    {
      resumeSelect(paramMessage);
    }
    else
    {
      isThreadDone = true;
      orb.getTransportManager().getSelector(0).unregisterForEvent(getConnection().getEventHandler());
      orb.getTransportManager().getSelector(0).registerForEvent(getConnection().getEventHandler());
    }
  }
  
  private void setWorkThenReadOrResumeSelect(Message paramMessage)
  {
    if (getConnection().getEventHandler().shouldUseSelectThreadToWait()) {
      resumeSelect(paramMessage);
    } else {
      isThreadDone = false;
    }
  }
  
  private void resumeSelect(Message paramMessage)
  {
    if (transportDebug())
    {
      dprint(".resumeSelect:->");
      localObject = "?";
      if ((paramMessage instanceof RequestMessage)) {
        localObject = new Integer(((RequestMessage)paramMessage).getRequestId()).toString();
      } else if ((paramMessage instanceof ReplyMessage)) {
        localObject = new Integer(((ReplyMessage)paramMessage).getRequestId()).toString();
      } else if ((paramMessage instanceof FragmentMessage_1_2)) {
        localObject = new Integer(((FragmentMessage_1_2)paramMessage).getRequestId()).toString();
      }
      dprint(".resumeSelect: id/" + (String)localObject + " " + getConnection());
    }
    Object localObject = getConnection().getEventHandler();
    orb.getTransportManager().getSelector(0).registerInterestOps((EventHandler)localObject);
    if (transportDebug()) {
      dprint(".resumeSelect:<-");
    }
  }
  
  private void setInputObject()
  {
    if (getConnection().getContactInfo() != null) {
      inputObject = ((CDRInputObject)getConnection().getContactInfo().createInputObject(orb, this));
    } else if (getConnection().getAcceptor() != null) {
      inputObject = ((CDRInputObject)getConnection().getAcceptor().createInputObject(orb, this));
    } else {
      throw new RuntimeException("CorbaMessageMediatorImpl.setInputObject");
    }
    inputObject.setMessageMediator(this);
    setInputObject(inputObject);
  }
  
  private void signalResponseReceived()
  {
    connection.getResponseWaitingRoom().responseReceived(inputObject);
  }
  
  /* Error */
  public void handleInput(Message paramMessage)
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: aload_1
    //   2: putfield 1062	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:messageHeader	Lcom/sun/corba/se/impl/protocol/giopmsgheaders/Message;
    //   5: aload_0
    //   6: invokespecial 1123	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:transportDebug	()Z
    //   9: ifeq +34 -> 43
    //   12: aload_0
    //   13: new 618	java/lang/StringBuilder
    //   16: dup
    //   17: invokespecial 1223	java/lang/StringBuilder:<init>	()V
    //   20: ldc 43
    //   22: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   25: aload_1
    //   26: invokeinterface 1259 1 0
    //   31: invokestatic 1174	com/sun/corba/se/impl/protocol/giopmsgheaders/MessageBase:typeToString	(I)Ljava/lang/String;
    //   34: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   37: invokevirtual 1224	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   40: invokespecial 1144	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:dprint	(Ljava/lang/String;)V
    //   43: aload_0
    //   44: aload_1
    //   45: invokespecial 1128	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:setWorkThenReadOrResumeSelect	(Lcom/sun/corba/se/impl/protocol/giopmsgheaders/Message;)V
    //   48: aload_1
    //   49: invokeinterface 1259 1 0
    //   54: lookupswitch	default:+94->148, 5:+26->80, 6:+60->114
    //   80: aload_0
    //   81: invokespecial 1123	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:transportDebug	()Z
    //   84: ifeq +9 -> 93
    //   87: aload_0
    //   88: ldc 44
    //   90: invokespecial 1144	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:dprint	(Ljava/lang/String;)V
    //   93: aload_0
    //   94: getfield 1066	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:connection	Lcom/sun/corba/se/spi/transport/CorbaConnection;
    //   97: aload_0
    //   98: getfield 1059	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:wrapper	Lcom/sun/corba/se/impl/logging/ORBUtilSystemException;
    //   101: invokevirtual 1094	com/sun/corba/se/impl/logging/ORBUtilSystemException:connectionRebind	()Lorg/omg/CORBA/COMM_FAILURE;
    //   104: iconst_1
    //   105: iconst_0
    //   106: invokeinterface 1349 4 0
    //   111: goto +83 -> 194
    //   114: aload_0
    //   115: invokespecial 1123	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:transportDebug	()Z
    //   118: ifeq +9 -> 127
    //   121: aload_0
    //   122: ldc 46
    //   124: invokespecial 1144	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:dprint	(Ljava/lang/String;)V
    //   127: aload_0
    //   128: getfield 1066	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:connection	Lcom/sun/corba/se/spi/transport/CorbaConnection;
    //   131: aload_0
    //   132: getfield 1059	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:wrapper	Lcom/sun/corba/se/impl/logging/ORBUtilSystemException;
    //   135: invokevirtual 1095	com/sun/corba/se/impl/logging/ORBUtilSystemException:recvMsgError	()Lorg/omg/CORBA/COMM_FAILURE;
    //   138: iconst_1
    //   139: iconst_0
    //   140: invokeinterface 1349 4 0
    //   145: goto +49 -> 194
    //   148: aload_0
    //   149: invokespecial 1123	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:transportDebug	()Z
    //   152: ifeq +34 -> 186
    //   155: aload_0
    //   156: new 618	java/lang/StringBuilder
    //   159: dup
    //   160: invokespecial 1223	java/lang/StringBuilder:<init>	()V
    //   163: ldc 45
    //   165: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   168: aload_1
    //   169: invokeinterface 1259 1 0
    //   174: invokestatic 1174	com/sun/corba/se/impl/protocol/giopmsgheaders/MessageBase:typeToString	(I)Ljava/lang/String;
    //   177: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   180: invokevirtual 1224	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   183: invokespecial 1144	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:dprint	(Ljava/lang/String;)V
    //   186: aload_0
    //   187: getfield 1059	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:wrapper	Lcom/sun/corba/se/impl/logging/ORBUtilSystemException;
    //   190: invokevirtual 1096	com/sun/corba/se/impl/logging/ORBUtilSystemException:badGiopRequestType	()Lorg/omg/CORBA/INTERNAL;
    //   193: athrow
    //   194: aload_0
    //   195: invokespecial 1117	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:releaseByteBufferToPool	()V
    //   198: aload_0
    //   199: invokespecial 1123	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:transportDebug	()Z
    //   202: ifeq +78 -> 280
    //   205: aload_0
    //   206: new 618	java/lang/StringBuilder
    //   209: dup
    //   210: invokespecial 1223	java/lang/StringBuilder:<init>	()V
    //   213: ldc 47
    //   215: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   218: aload_1
    //   219: invokeinterface 1259 1 0
    //   224: invokestatic 1174	com/sun/corba/se/impl/protocol/giopmsgheaders/MessageBase:typeToString	(I)Ljava/lang/String;
    //   227: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   230: invokevirtual 1224	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   233: invokespecial 1144	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:dprint	(Ljava/lang/String;)V
    //   236: goto +44 -> 280
    //   239: astore_2
    //   240: aload_0
    //   241: invokespecial 1123	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:transportDebug	()Z
    //   244: ifeq +34 -> 278
    //   247: aload_0
    //   248: new 618	java/lang/StringBuilder
    //   251: dup
    //   252: invokespecial 1223	java/lang/StringBuilder:<init>	()V
    //   255: ldc 47
    //   257: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   260: aload_1
    //   261: invokeinterface 1259 1 0
    //   266: invokestatic 1174	com/sun/corba/se/impl/protocol/giopmsgheaders/MessageBase:typeToString	(I)Ljava/lang/String;
    //   269: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   272: invokevirtual 1224	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   275: invokespecial 1144	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:dprint	(Ljava/lang/String;)V
    //   278: aload_2
    //   279: athrow
    //   280: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	281	0	this	CorbaMessageMediatorImpl
    //   0	281	1	paramMessage	Message
    //   239	40	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   0	198	239	finally
  }
  
  /* Error */
  public void handleInput(com.sun.corba.se.impl.protocol.giopmsgheaders.RequestMessage_1_0 paramRequestMessage_1_0)
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial 1123	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:transportDebug	()Z
    //   4: ifeq +26 -> 30
    //   7: aload_0
    //   8: new 618	java/lang/StringBuilder
    //   11: dup
    //   12: invokespecial 1223	java/lang/StringBuilder:<init>	()V
    //   15: ldc 34
    //   17: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   20: aload_1
    //   21: invokevirtual 1227	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   24: invokevirtual 1224	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   27: invokespecial 1144	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:dprint	(Ljava/lang/String;)V
    //   30: aload_0
    //   31: aload_0
    //   32: aload_1
    //   33: dup_x1
    //   34: putfield 1063	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:requestHeader	Lcom/sun/corba/se/impl/protocol/giopmsgheaders/RequestMessage;
    //   37: putfield 1062	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:messageHeader	Lcom/sun/corba/se/impl/protocol/giopmsgheaders/Message;
    //   40: aload_0
    //   41: invokespecial 1118	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:setInputObject	()V
    //   44: aload_0
    //   45: aload_1
    //   46: invokespecial 1127	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:setWorkThenPoolOrResumeSelect	(Lcom/sun/corba/se/impl/protocol/giopmsgheaders/Message;)V
    //   49: goto +11 -> 60
    //   52: astore_2
    //   53: aload_0
    //   54: aload_1
    //   55: invokespecial 1127	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:setWorkThenPoolOrResumeSelect	(Lcom/sun/corba/se/impl/protocol/giopmsgheaders/Message;)V
    //   58: aload_2
    //   59: athrow
    //   60: aload_0
    //   61: invokevirtual 1142	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:getProtocolHandler	()Lcom/sun/corba/se/spi/protocol/CorbaProtocolHandler;
    //   64: aload_1
    //   65: aload_0
    //   66: invokeinterface 1324 3 0
    //   71: aload_0
    //   72: invokespecial 1123	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:transportDebug	()Z
    //   75: ifeq +127 -> 202
    //   78: aload_0
    //   79: new 618	java/lang/StringBuilder
    //   82: dup
    //   83: invokespecial 1223	java/lang/StringBuilder:<init>	()V
    //   86: ldc 36
    //   88: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   91: aload_1
    //   92: invokevirtual 1227	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   95: invokevirtual 1224	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   98: invokespecial 1144	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:dprint	(Ljava/lang/String;)V
    //   101: goto +101 -> 202
    //   104: astore_2
    //   105: aload_0
    //   106: invokespecial 1123	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:transportDebug	()Z
    //   109: ifeq +27 -> 136
    //   112: aload_0
    //   113: new 618	java/lang/StringBuilder
    //   116: dup
    //   117: invokespecial 1223	java/lang/StringBuilder:<init>	()V
    //   120: ldc 35
    //   122: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   125: aload_1
    //   126: invokevirtual 1227	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   129: invokevirtual 1224	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   132: aload_2
    //   133: invokespecial 1151	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:dprint	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   136: aload_0
    //   137: invokespecial 1123	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:transportDebug	()Z
    //   140: ifeq +62 -> 202
    //   143: aload_0
    //   144: new 618	java/lang/StringBuilder
    //   147: dup
    //   148: invokespecial 1223	java/lang/StringBuilder:<init>	()V
    //   151: ldc 36
    //   153: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   156: aload_1
    //   157: invokevirtual 1227	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   160: invokevirtual 1224	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   163: invokespecial 1144	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:dprint	(Ljava/lang/String;)V
    //   166: goto +36 -> 202
    //   169: astore_3
    //   170: aload_0
    //   171: invokespecial 1123	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:transportDebug	()Z
    //   174: ifeq +26 -> 200
    //   177: aload_0
    //   178: new 618	java/lang/StringBuilder
    //   181: dup
    //   182: invokespecial 1223	java/lang/StringBuilder:<init>	()V
    //   185: ldc 36
    //   187: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   190: aload_1
    //   191: invokevirtual 1227	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   194: invokevirtual 1224	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   197: invokespecial 1144	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:dprint	(Ljava/lang/String;)V
    //   200: aload_3
    //   201: athrow
    //   202: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	203	0	this	CorbaMessageMediatorImpl
    //   0	203	1	paramRequestMessage_1_0	com.sun.corba.se.impl.protocol.giopmsgheaders.RequestMessage_1_0
    //   52	7	2	localObject1	Object
    //   104	29	2	localThrowable	Throwable
    //   169	32	3	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   30	44	52	finally
    //   0	71	104	java/lang/Throwable
    //   0	71	169	finally
    //   104	136	169	finally
  }
  
  /* Error */
  public void handleInput(com.sun.corba.se.impl.protocol.giopmsgheaders.RequestMessage_1_1 paramRequestMessage_1_1)
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial 1123	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:transportDebug	()Z
    //   4: ifeq +26 -> 30
    //   7: aload_0
    //   8: new 618	java/lang/StringBuilder
    //   11: dup
    //   12: invokespecial 1223	java/lang/StringBuilder:<init>	()V
    //   15: ldc 37
    //   17: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   20: aload_1
    //   21: invokevirtual 1227	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   24: invokevirtual 1224	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   27: invokespecial 1144	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:dprint	(Ljava/lang/String;)V
    //   30: aload_0
    //   31: aload_0
    //   32: aload_1
    //   33: dup_x1
    //   34: putfield 1063	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:requestHeader	Lcom/sun/corba/se/impl/protocol/giopmsgheaders/RequestMessage;
    //   37: putfield 1062	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:messageHeader	Lcom/sun/corba/se/impl/protocol/giopmsgheaders/Message;
    //   40: aload_0
    //   41: invokespecial 1118	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:setInputObject	()V
    //   44: aload_0
    //   45: getfield 1066	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:connection	Lcom/sun/corba/se/spi/transport/CorbaConnection;
    //   48: aload_0
    //   49: invokeinterface 1344 2 0
    //   54: aload_0
    //   55: aload_1
    //   56: invokespecial 1127	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:setWorkThenPoolOrResumeSelect	(Lcom/sun/corba/se/impl/protocol/giopmsgheaders/Message;)V
    //   59: goto +11 -> 70
    //   62: astore_2
    //   63: aload_0
    //   64: aload_1
    //   65: invokespecial 1127	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:setWorkThenPoolOrResumeSelect	(Lcom/sun/corba/se/impl/protocol/giopmsgheaders/Message;)V
    //   68: aload_2
    //   69: athrow
    //   70: aload_0
    //   71: invokevirtual 1142	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:getProtocolHandler	()Lcom/sun/corba/se/spi/protocol/CorbaProtocolHandler;
    //   74: aload_1
    //   75: aload_0
    //   76: invokeinterface 1324 3 0
    //   81: aload_0
    //   82: invokespecial 1123	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:transportDebug	()Z
    //   85: ifeq +127 -> 212
    //   88: aload_0
    //   89: new 618	java/lang/StringBuilder
    //   92: dup
    //   93: invokespecial 1223	java/lang/StringBuilder:<init>	()V
    //   96: ldc 39
    //   98: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   101: aload_1
    //   102: invokevirtual 1227	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   105: invokevirtual 1224	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   108: invokespecial 1144	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:dprint	(Ljava/lang/String;)V
    //   111: goto +101 -> 212
    //   114: astore_2
    //   115: aload_0
    //   116: invokespecial 1123	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:transportDebug	()Z
    //   119: ifeq +27 -> 146
    //   122: aload_0
    //   123: new 618	java/lang/StringBuilder
    //   126: dup
    //   127: invokespecial 1223	java/lang/StringBuilder:<init>	()V
    //   130: ldc 38
    //   132: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   135: aload_1
    //   136: invokevirtual 1227	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   139: invokevirtual 1224	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   142: aload_2
    //   143: invokespecial 1151	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:dprint	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   146: aload_0
    //   147: invokespecial 1123	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:transportDebug	()Z
    //   150: ifeq +62 -> 212
    //   153: aload_0
    //   154: new 618	java/lang/StringBuilder
    //   157: dup
    //   158: invokespecial 1223	java/lang/StringBuilder:<init>	()V
    //   161: ldc 39
    //   163: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   166: aload_1
    //   167: invokevirtual 1227	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   170: invokevirtual 1224	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   173: invokespecial 1144	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:dprint	(Ljava/lang/String;)V
    //   176: goto +36 -> 212
    //   179: astore_3
    //   180: aload_0
    //   181: invokespecial 1123	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:transportDebug	()Z
    //   184: ifeq +26 -> 210
    //   187: aload_0
    //   188: new 618	java/lang/StringBuilder
    //   191: dup
    //   192: invokespecial 1223	java/lang/StringBuilder:<init>	()V
    //   195: ldc 39
    //   197: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   200: aload_1
    //   201: invokevirtual 1227	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   204: invokevirtual 1224	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   207: invokespecial 1144	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:dprint	(Ljava/lang/String;)V
    //   210: aload_3
    //   211: athrow
    //   212: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	213	0	this	CorbaMessageMediatorImpl
    //   0	213	1	paramRequestMessage_1_1	com.sun.corba.se.impl.protocol.giopmsgheaders.RequestMessage_1_1
    //   62	7	2	localObject1	Object
    //   114	29	2	localThrowable	Throwable
    //   179	32	3	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   30	54	62	finally
    //   0	81	114	java/lang/Throwable
    //   0	81	179	finally
    //   114	146	179	finally
  }
  
  /* Error */
  public void handleInput(com.sun.corba.se.impl.protocol.giopmsgheaders.RequestMessage_1_2 paramRequestMessage_1_2)
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: aload_0
    //   2: aload_1
    //   3: dup_x1
    //   4: putfield 1063	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:requestHeader	Lcom/sun/corba/se/impl/protocol/giopmsgheaders/RequestMessage;
    //   7: putfield 1062	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:messageHeader	Lcom/sun/corba/se/impl/protocol/giopmsgheaders/Message;
    //   10: aload_1
    //   11: aload_0
    //   12: getfield 1070	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:dispatchByteBuffer	Ljava/nio/ByteBuffer;
    //   15: invokevirtual 1183	com/sun/corba/se/impl/protocol/giopmsgheaders/RequestMessage_1_2:unmarshalRequestID	(Ljava/nio/ByteBuffer;)V
    //   18: aload_0
    //   19: invokespecial 1118	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:setInputObject	()V
    //   22: aload_0
    //   23: invokespecial 1123	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:transportDebug	()Z
    //   26: ifeq +38 -> 64
    //   29: aload_0
    //   30: new 618	java/lang/StringBuilder
    //   33: dup
    //   34: invokespecial 1223	java/lang/StringBuilder:<init>	()V
    //   37: ldc 40
    //   39: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   42: aload_1
    //   43: invokevirtual 1182	com/sun/corba/se/impl/protocol/giopmsgheaders/RequestMessage_1_2:getRequestId	()I
    //   46: invokevirtual 1225	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   49: ldc 54
    //   51: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   54: aload_1
    //   55: invokevirtual 1227	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   58: invokevirtual 1224	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   61: invokespecial 1144	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:dprint	(Ljava/lang/String;)V
    //   64: aload_0
    //   65: getfield 1066	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:connection	Lcom/sun/corba/se/spi/transport/CorbaConnection;
    //   68: aload_1
    //   69: invokevirtual 1182	com/sun/corba/se/impl/protocol/giopmsgheaders/RequestMessage_1_2:getRequestId	()I
    //   72: aload_0
    //   73: invokeinterface 1348 3 0
    //   78: aload_0
    //   79: aload_1
    //   80: invokespecial 1127	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:setWorkThenPoolOrResumeSelect	(Lcom/sun/corba/se/impl/protocol/giopmsgheaders/Message;)V
    //   83: goto +11 -> 94
    //   86: astore_2
    //   87: aload_0
    //   88: aload_1
    //   89: invokespecial 1127	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:setWorkThenPoolOrResumeSelect	(Lcom/sun/corba/se/impl/protocol/giopmsgheaders/Message;)V
    //   92: aload_2
    //   93: athrow
    //   94: aload_0
    //   95: invokevirtual 1142	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:getProtocolHandler	()Lcom/sun/corba/se/spi/protocol/CorbaProtocolHandler;
    //   98: aload_1
    //   99: aload_0
    //   100: invokeinterface 1324 3 0
    //   105: aload_0
    //   106: getfield 1066	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:connection	Lcom/sun/corba/se/spi/transport/CorbaConnection;
    //   109: aload_1
    //   110: invokevirtual 1182	com/sun/corba/se/impl/protocol/giopmsgheaders/RequestMessage_1_2:getRequestId	()I
    //   113: invokeinterface 1339 2 0
    //   118: aload_0
    //   119: invokespecial 1123	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:transportDebug	()Z
    //   122: ifeq +201 -> 323
    //   125: aload_0
    //   126: new 618	java/lang/StringBuilder
    //   129: dup
    //   130: invokespecial 1223	java/lang/StringBuilder:<init>	()V
    //   133: ldc 42
    //   135: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   138: aload_1
    //   139: invokevirtual 1182	com/sun/corba/se/impl/protocol/giopmsgheaders/RequestMessage_1_2:getRequestId	()I
    //   142: invokevirtual 1225	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   145: ldc 54
    //   147: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   150: aload_1
    //   151: invokevirtual 1227	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   154: invokevirtual 1224	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   157: invokespecial 1144	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:dprint	(Ljava/lang/String;)V
    //   160: goto +163 -> 323
    //   163: astore_2
    //   164: aload_0
    //   165: invokespecial 1123	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:transportDebug	()Z
    //   168: ifeq +39 -> 207
    //   171: aload_0
    //   172: new 618	java/lang/StringBuilder
    //   175: dup
    //   176: invokespecial 1223	java/lang/StringBuilder:<init>	()V
    //   179: ldc 41
    //   181: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   184: aload_1
    //   185: invokevirtual 1182	com/sun/corba/se/impl/protocol/giopmsgheaders/RequestMessage_1_2:getRequestId	()I
    //   188: invokevirtual 1225	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   191: ldc 55
    //   193: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   196: aload_1
    //   197: invokevirtual 1227	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   200: invokevirtual 1224	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   203: aload_2
    //   204: invokespecial 1151	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:dprint	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   207: aload_0
    //   208: getfield 1066	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:connection	Lcom/sun/corba/se/spi/transport/CorbaConnection;
    //   211: aload_1
    //   212: invokevirtual 1182	com/sun/corba/se/impl/protocol/giopmsgheaders/RequestMessage_1_2:getRequestId	()I
    //   215: invokeinterface 1339 2 0
    //   220: aload_0
    //   221: invokespecial 1123	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:transportDebug	()Z
    //   224: ifeq +99 -> 323
    //   227: aload_0
    //   228: new 618	java/lang/StringBuilder
    //   231: dup
    //   232: invokespecial 1223	java/lang/StringBuilder:<init>	()V
    //   235: ldc 42
    //   237: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   240: aload_1
    //   241: invokevirtual 1182	com/sun/corba/se/impl/protocol/giopmsgheaders/RequestMessage_1_2:getRequestId	()I
    //   244: invokevirtual 1225	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   247: ldc 54
    //   249: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   252: aload_1
    //   253: invokevirtual 1227	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   256: invokevirtual 1224	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   259: invokespecial 1144	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:dprint	(Ljava/lang/String;)V
    //   262: goto +61 -> 323
    //   265: astore_3
    //   266: aload_0
    //   267: getfield 1066	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:connection	Lcom/sun/corba/se/spi/transport/CorbaConnection;
    //   270: aload_1
    //   271: invokevirtual 1182	com/sun/corba/se/impl/protocol/giopmsgheaders/RequestMessage_1_2:getRequestId	()I
    //   274: invokeinterface 1339 2 0
    //   279: aload_0
    //   280: invokespecial 1123	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:transportDebug	()Z
    //   283: ifeq +38 -> 321
    //   286: aload_0
    //   287: new 618	java/lang/StringBuilder
    //   290: dup
    //   291: invokespecial 1223	java/lang/StringBuilder:<init>	()V
    //   294: ldc 42
    //   296: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   299: aload_1
    //   300: invokevirtual 1182	com/sun/corba/se/impl/protocol/giopmsgheaders/RequestMessage_1_2:getRequestId	()I
    //   303: invokevirtual 1225	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   306: ldc 54
    //   308: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   311: aload_1
    //   312: invokevirtual 1227	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   315: invokevirtual 1224	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   318: invokespecial 1144	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:dprint	(Ljava/lang/String;)V
    //   321: aload_3
    //   322: athrow
    //   323: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	324	0	this	CorbaMessageMediatorImpl
    //   0	324	1	paramRequestMessage_1_2	com.sun.corba.se.impl.protocol.giopmsgheaders.RequestMessage_1_2
    //   86	7	2	localObject1	Object
    //   163	41	2	localThrowable	Throwable
    //   265	57	3	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   0	78	86	finally
    //   0	105	163	java/lang/Throwable
    //   0	105	265	finally
    //   163	207	265	finally
  }
  
  /* Error */
  public void handleInput(com.sun.corba.se.impl.protocol.giopmsgheaders.ReplyMessage_1_0 paramReplyMessage_1_0)
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial 1123	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:transportDebug	()Z
    //   4: ifeq +26 -> 30
    //   7: aload_0
    //   8: new 618	java/lang/StringBuilder
    //   11: dup
    //   12: invokespecial 1223	java/lang/StringBuilder:<init>	()V
    //   15: ldc 25
    //   17: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   20: aload_1
    //   21: invokevirtual 1227	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   24: invokevirtual 1224	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   27: invokespecial 1144	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:dprint	(Ljava/lang/String;)V
    //   30: aload_0
    //   31: aload_0
    //   32: aload_1
    //   33: dup_x1
    //   34: putfield 1060	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:replyHeader	Lcom/sun/corba/se/impl/protocol/giopmsgheaders/LocateReplyOrReplyMessage;
    //   37: putfield 1062	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:messageHeader	Lcom/sun/corba/se/impl/protocol/giopmsgheaders/Message;
    //   40: aload_0
    //   41: invokespecial 1118	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:setInputObject	()V
    //   44: aload_0
    //   45: getfield 1056	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:inputObject	Lcom/sun/corba/se/impl/encoding/CDRInputObject;
    //   48: invokevirtual 1083	com/sun/corba/se/impl/encoding/CDRInputObject:unmarshalHeader	()V
    //   51: aload_0
    //   52: invokespecial 1119	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:signalResponseReceived	()V
    //   55: aload_0
    //   56: aload_1
    //   57: invokespecial 1128	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:setWorkThenReadOrResumeSelect	(Lcom/sun/corba/se/impl/protocol/giopmsgheaders/Message;)V
    //   60: goto +11 -> 71
    //   63: astore_2
    //   64: aload_0
    //   65: aload_1
    //   66: invokespecial 1128	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:setWorkThenReadOrResumeSelect	(Lcom/sun/corba/se/impl/protocol/giopmsgheaders/Message;)V
    //   69: aload_2
    //   70: athrow
    //   71: aload_0
    //   72: invokespecial 1123	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:transportDebug	()Z
    //   75: ifeq +127 -> 202
    //   78: aload_0
    //   79: new 618	java/lang/StringBuilder
    //   82: dup
    //   83: invokespecial 1223	java/lang/StringBuilder:<init>	()V
    //   86: ldc 27
    //   88: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   91: aload_1
    //   92: invokevirtual 1227	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   95: invokevirtual 1224	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   98: invokespecial 1144	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:dprint	(Ljava/lang/String;)V
    //   101: goto +101 -> 202
    //   104: astore_2
    //   105: aload_0
    //   106: invokespecial 1123	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:transportDebug	()Z
    //   109: ifeq +27 -> 136
    //   112: aload_0
    //   113: new 618	java/lang/StringBuilder
    //   116: dup
    //   117: invokespecial 1223	java/lang/StringBuilder:<init>	()V
    //   120: ldc 26
    //   122: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   125: aload_1
    //   126: invokevirtual 1227	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   129: invokevirtual 1224	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   132: aload_2
    //   133: invokespecial 1151	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:dprint	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   136: aload_0
    //   137: invokespecial 1123	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:transportDebug	()Z
    //   140: ifeq +62 -> 202
    //   143: aload_0
    //   144: new 618	java/lang/StringBuilder
    //   147: dup
    //   148: invokespecial 1223	java/lang/StringBuilder:<init>	()V
    //   151: ldc 27
    //   153: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   156: aload_1
    //   157: invokevirtual 1227	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   160: invokevirtual 1224	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   163: invokespecial 1144	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:dprint	(Ljava/lang/String;)V
    //   166: goto +36 -> 202
    //   169: astore_3
    //   170: aload_0
    //   171: invokespecial 1123	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:transportDebug	()Z
    //   174: ifeq +26 -> 200
    //   177: aload_0
    //   178: new 618	java/lang/StringBuilder
    //   181: dup
    //   182: invokespecial 1223	java/lang/StringBuilder:<init>	()V
    //   185: ldc 27
    //   187: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   190: aload_1
    //   191: invokevirtual 1227	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   194: invokevirtual 1224	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   197: invokespecial 1144	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:dprint	(Ljava/lang/String;)V
    //   200: aload_3
    //   201: athrow
    //   202: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	203	0	this	CorbaMessageMediatorImpl
    //   0	203	1	paramReplyMessage_1_0	com.sun.corba.se.impl.protocol.giopmsgheaders.ReplyMessage_1_0
    //   63	7	2	localObject1	Object
    //   104	29	2	localThrowable	Throwable
    //   169	32	3	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   0	55	63	finally
    //   0	71	104	java/lang/Throwable
    //   0	71	169	finally
    //   104	136	169	finally
  }
  
  public void handleInput(ReplyMessage_1_1 paramReplyMessage_1_1)
    throws IOException
  {
    try
    {
      if (transportDebug()) {
        dprint(".REPLY 1.1->: " + paramReplyMessage_1_1);
      }
      messageHeader = (replyHeader = paramReplyMessage_1_1);
      setInputObject();
      if (paramReplyMessage_1_1.moreFragmentsToFollow())
      {
        connection.clientReply_1_1_Put(this);
        setWorkThenPoolOrResumeSelect(paramReplyMessage_1_1);
        inputObject.unmarshalHeader();
        signalResponseReceived();
      }
      else
      {
        inputObject.unmarshalHeader();
        signalResponseReceived();
        setWorkThenReadOrResumeSelect(paramReplyMessage_1_1);
      }
    }
    catch (Throwable localThrowable)
    {
      if (transportDebug()) {
        dprint(".REPLY 1.1: !!ERROR!!: " + paramReplyMessage_1_1);
      }
    }
    finally
    {
      if (transportDebug()) {
        dprint(".REPLY 1.1<-: " + paramReplyMessage_1_1);
      }
    }
  }
  
  /* Error */
  public void handleInput(com.sun.corba.se.impl.protocol.giopmsgheaders.ReplyMessage_1_2 paramReplyMessage_1_2)
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: aload_0
    //   2: aload_1
    //   3: dup_x1
    //   4: putfield 1060	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:replyHeader	Lcom/sun/corba/se/impl/protocol/giopmsgheaders/LocateReplyOrReplyMessage;
    //   7: putfield 1062	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:messageHeader	Lcom/sun/corba/se/impl/protocol/giopmsgheaders/Message;
    //   10: aload_1
    //   11: aload_0
    //   12: getfield 1070	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:dispatchByteBuffer	Ljava/nio/ByteBuffer;
    //   15: invokevirtual 1181	com/sun/corba/se/impl/protocol/giopmsgheaders/ReplyMessage_1_2:unmarshalRequestID	(Ljava/nio/ByteBuffer;)V
    //   18: aload_0
    //   19: invokespecial 1123	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:transportDebug	()Z
    //   22: ifeq +50 -> 72
    //   25: aload_0
    //   26: new 618	java/lang/StringBuilder
    //   29: dup
    //   30: invokespecial 1223	java/lang/StringBuilder:<init>	()V
    //   33: ldc 31
    //   35: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   38: aload_1
    //   39: invokevirtual 1179	com/sun/corba/se/impl/protocol/giopmsgheaders/ReplyMessage_1_2:getRequestId	()I
    //   42: invokevirtual 1225	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   45: ldc 56
    //   47: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   50: aload_1
    //   51: invokevirtual 1180	com/sun/corba/se/impl/protocol/giopmsgheaders/ReplyMessage_1_2:moreFragmentsToFollow	()Z
    //   54: invokevirtual 1226	java/lang/StringBuilder:append	(Z)Ljava/lang/StringBuilder;
    //   57: ldc 54
    //   59: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   62: aload_1
    //   63: invokevirtual 1227	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   66: invokevirtual 1224	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   69: invokespecial 1144	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:dprint	(Ljava/lang/String;)V
    //   72: aload_0
    //   73: invokespecial 1118	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:setInputObject	()V
    //   76: aload_0
    //   77: invokespecial 1119	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:signalResponseReceived	()V
    //   80: aload_0
    //   81: aload_1
    //   82: invokespecial 1128	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:setWorkThenReadOrResumeSelect	(Lcom/sun/corba/se/impl/protocol/giopmsgheaders/Message;)V
    //   85: goto +11 -> 96
    //   88: astore_2
    //   89: aload_0
    //   90: aload_1
    //   91: invokespecial 1128	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:setWorkThenReadOrResumeSelect	(Lcom/sun/corba/se/impl/protocol/giopmsgheaders/Message;)V
    //   94: aload_2
    //   95: athrow
    //   96: aload_0
    //   97: invokespecial 1123	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:transportDebug	()Z
    //   100: ifeq +175 -> 275
    //   103: aload_0
    //   104: new 618	java/lang/StringBuilder
    //   107: dup
    //   108: invokespecial 1223	java/lang/StringBuilder:<init>	()V
    //   111: ldc 33
    //   113: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   116: aload_1
    //   117: invokevirtual 1179	com/sun/corba/se/impl/protocol/giopmsgheaders/ReplyMessage_1_2:getRequestId	()I
    //   120: invokevirtual 1225	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   123: ldc 54
    //   125: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   128: aload_1
    //   129: invokevirtual 1227	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   132: invokevirtual 1224	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   135: invokespecial 1144	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:dprint	(Ljava/lang/String;)V
    //   138: goto +137 -> 275
    //   141: astore_2
    //   142: aload_0
    //   143: invokespecial 1123	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:transportDebug	()Z
    //   146: ifeq +39 -> 185
    //   149: aload_0
    //   150: new 618	java/lang/StringBuilder
    //   153: dup
    //   154: invokespecial 1223	java/lang/StringBuilder:<init>	()V
    //   157: ldc 32
    //   159: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   162: aload_1
    //   163: invokevirtual 1179	com/sun/corba/se/impl/protocol/giopmsgheaders/ReplyMessage_1_2:getRequestId	()I
    //   166: invokevirtual 1225	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   169: ldc 55
    //   171: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   174: aload_1
    //   175: invokevirtual 1227	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   178: invokevirtual 1224	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   181: aload_2
    //   182: invokespecial 1151	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:dprint	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   185: aload_0
    //   186: invokespecial 1123	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:transportDebug	()Z
    //   189: ifeq +86 -> 275
    //   192: aload_0
    //   193: new 618	java/lang/StringBuilder
    //   196: dup
    //   197: invokespecial 1223	java/lang/StringBuilder:<init>	()V
    //   200: ldc 33
    //   202: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   205: aload_1
    //   206: invokevirtual 1179	com/sun/corba/se/impl/protocol/giopmsgheaders/ReplyMessage_1_2:getRequestId	()I
    //   209: invokevirtual 1225	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   212: ldc 54
    //   214: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   217: aload_1
    //   218: invokevirtual 1227	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   221: invokevirtual 1224	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   224: invokespecial 1144	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:dprint	(Ljava/lang/String;)V
    //   227: goto +48 -> 275
    //   230: astore_3
    //   231: aload_0
    //   232: invokespecial 1123	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:transportDebug	()Z
    //   235: ifeq +38 -> 273
    //   238: aload_0
    //   239: new 618	java/lang/StringBuilder
    //   242: dup
    //   243: invokespecial 1223	java/lang/StringBuilder:<init>	()V
    //   246: ldc 33
    //   248: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   251: aload_1
    //   252: invokevirtual 1179	com/sun/corba/se/impl/protocol/giopmsgheaders/ReplyMessage_1_2:getRequestId	()I
    //   255: invokevirtual 1225	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   258: ldc 54
    //   260: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   263: aload_1
    //   264: invokevirtual 1227	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   267: invokevirtual 1224	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   270: invokespecial 1144	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:dprint	(Ljava/lang/String;)V
    //   273: aload_3
    //   274: athrow
    //   275: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	276	0	this	CorbaMessageMediatorImpl
    //   0	276	1	paramReplyMessage_1_2	com.sun.corba.se.impl.protocol.giopmsgheaders.ReplyMessage_1_2
    //   88	7	2	localObject1	Object
    //   141	41	2	localThrowable	Throwable
    //   230	44	3	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   0	80	88	finally
    //   0	96	141	java/lang/Throwable
    //   0	96	230	finally
    //   141	185	230	finally
  }
  
  /* Error */
  public void handleInput(com.sun.corba.se.impl.protocol.giopmsgheaders.LocateRequestMessage_1_0 paramLocateRequestMessage_1_0)
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial 1123	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:transportDebug	()Z
    //   4: ifeq +26 -> 30
    //   7: aload_0
    //   8: new 618	java/lang/StringBuilder
    //   11: dup
    //   12: invokespecial 1223	java/lang/StringBuilder:<init>	()V
    //   15: ldc 16
    //   17: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   20: aload_1
    //   21: invokevirtual 1227	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   24: invokevirtual 1224	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   27: invokespecial 1144	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:dprint	(Ljava/lang/String;)V
    //   30: aload_0
    //   31: aload_1
    //   32: putfield 1062	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:messageHeader	Lcom/sun/corba/se/impl/protocol/giopmsgheaders/Message;
    //   35: aload_0
    //   36: invokespecial 1118	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:setInputObject	()V
    //   39: aload_0
    //   40: aload_1
    //   41: invokespecial 1127	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:setWorkThenPoolOrResumeSelect	(Lcom/sun/corba/se/impl/protocol/giopmsgheaders/Message;)V
    //   44: goto +11 -> 55
    //   47: astore_2
    //   48: aload_0
    //   49: aload_1
    //   50: invokespecial 1127	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:setWorkThenPoolOrResumeSelect	(Lcom/sun/corba/se/impl/protocol/giopmsgheaders/Message;)V
    //   53: aload_2
    //   54: athrow
    //   55: aload_0
    //   56: invokevirtual 1142	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:getProtocolHandler	()Lcom/sun/corba/se/spi/protocol/CorbaProtocolHandler;
    //   59: aload_1
    //   60: aload_0
    //   61: invokeinterface 1323 3 0
    //   66: aload_0
    //   67: invokespecial 1123	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:transportDebug	()Z
    //   70: ifeq +127 -> 197
    //   73: aload_0
    //   74: new 618	java/lang/StringBuilder
    //   77: dup
    //   78: invokespecial 1223	java/lang/StringBuilder:<init>	()V
    //   81: ldc 18
    //   83: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   86: aload_1
    //   87: invokevirtual 1227	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   90: invokevirtual 1224	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   93: invokespecial 1144	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:dprint	(Ljava/lang/String;)V
    //   96: goto +101 -> 197
    //   99: astore_2
    //   100: aload_0
    //   101: invokespecial 1123	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:transportDebug	()Z
    //   104: ifeq +27 -> 131
    //   107: aload_0
    //   108: new 618	java/lang/StringBuilder
    //   111: dup
    //   112: invokespecial 1223	java/lang/StringBuilder:<init>	()V
    //   115: ldc 17
    //   117: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   120: aload_1
    //   121: invokevirtual 1227	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   124: invokevirtual 1224	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   127: aload_2
    //   128: invokespecial 1151	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:dprint	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   131: aload_0
    //   132: invokespecial 1123	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:transportDebug	()Z
    //   135: ifeq +62 -> 197
    //   138: aload_0
    //   139: new 618	java/lang/StringBuilder
    //   142: dup
    //   143: invokespecial 1223	java/lang/StringBuilder:<init>	()V
    //   146: ldc 18
    //   148: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   151: aload_1
    //   152: invokevirtual 1227	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   155: invokevirtual 1224	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   158: invokespecial 1144	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:dprint	(Ljava/lang/String;)V
    //   161: goto +36 -> 197
    //   164: astore_3
    //   165: aload_0
    //   166: invokespecial 1123	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:transportDebug	()Z
    //   169: ifeq +26 -> 195
    //   172: aload_0
    //   173: new 618	java/lang/StringBuilder
    //   176: dup
    //   177: invokespecial 1223	java/lang/StringBuilder:<init>	()V
    //   180: ldc 18
    //   182: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   185: aload_1
    //   186: invokevirtual 1227	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   189: invokevirtual 1224	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   192: invokespecial 1144	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:dprint	(Ljava/lang/String;)V
    //   195: aload_3
    //   196: athrow
    //   197: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	198	0	this	CorbaMessageMediatorImpl
    //   0	198	1	paramLocateRequestMessage_1_0	com.sun.corba.se.impl.protocol.giopmsgheaders.LocateRequestMessage_1_0
    //   47	7	2	localObject1	Object
    //   99	29	2	localThrowable	Throwable
    //   164	32	3	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   30	39	47	finally
    //   0	66	99	java/lang/Throwable
    //   0	66	164	finally
    //   99	131	164	finally
  }
  
  /* Error */
  public void handleInput(com.sun.corba.se.impl.protocol.giopmsgheaders.LocateRequestMessage_1_1 paramLocateRequestMessage_1_1)
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial 1123	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:transportDebug	()Z
    //   4: ifeq +26 -> 30
    //   7: aload_0
    //   8: new 618	java/lang/StringBuilder
    //   11: dup
    //   12: invokespecial 1223	java/lang/StringBuilder:<init>	()V
    //   15: ldc 19
    //   17: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   20: aload_1
    //   21: invokevirtual 1227	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   24: invokevirtual 1224	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   27: invokespecial 1144	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:dprint	(Ljava/lang/String;)V
    //   30: aload_0
    //   31: aload_1
    //   32: putfield 1062	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:messageHeader	Lcom/sun/corba/se/impl/protocol/giopmsgheaders/Message;
    //   35: aload_0
    //   36: invokespecial 1118	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:setInputObject	()V
    //   39: aload_0
    //   40: aload_1
    //   41: invokespecial 1127	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:setWorkThenPoolOrResumeSelect	(Lcom/sun/corba/se/impl/protocol/giopmsgheaders/Message;)V
    //   44: goto +11 -> 55
    //   47: astore_2
    //   48: aload_0
    //   49: aload_1
    //   50: invokespecial 1127	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:setWorkThenPoolOrResumeSelect	(Lcom/sun/corba/se/impl/protocol/giopmsgheaders/Message;)V
    //   53: aload_2
    //   54: athrow
    //   55: aload_0
    //   56: invokevirtual 1142	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:getProtocolHandler	()Lcom/sun/corba/se/spi/protocol/CorbaProtocolHandler;
    //   59: aload_1
    //   60: aload_0
    //   61: invokeinterface 1323 3 0
    //   66: aload_0
    //   67: invokespecial 1123	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:transportDebug	()Z
    //   70: ifeq +127 -> 197
    //   73: aload_0
    //   74: new 618	java/lang/StringBuilder
    //   77: dup
    //   78: invokespecial 1223	java/lang/StringBuilder:<init>	()V
    //   81: ldc 21
    //   83: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   86: aload_1
    //   87: invokevirtual 1227	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   90: invokevirtual 1224	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   93: invokespecial 1144	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:dprint	(Ljava/lang/String;)V
    //   96: goto +101 -> 197
    //   99: astore_2
    //   100: aload_0
    //   101: invokespecial 1123	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:transportDebug	()Z
    //   104: ifeq +27 -> 131
    //   107: aload_0
    //   108: new 618	java/lang/StringBuilder
    //   111: dup
    //   112: invokespecial 1223	java/lang/StringBuilder:<init>	()V
    //   115: ldc 20
    //   117: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   120: aload_1
    //   121: invokevirtual 1227	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   124: invokevirtual 1224	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   127: aload_2
    //   128: invokespecial 1151	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:dprint	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   131: aload_0
    //   132: invokespecial 1123	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:transportDebug	()Z
    //   135: ifeq +62 -> 197
    //   138: aload_0
    //   139: new 618	java/lang/StringBuilder
    //   142: dup
    //   143: invokespecial 1223	java/lang/StringBuilder:<init>	()V
    //   146: ldc 21
    //   148: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   151: aload_1
    //   152: invokevirtual 1227	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   155: invokevirtual 1224	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   158: invokespecial 1144	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:dprint	(Ljava/lang/String;)V
    //   161: goto +36 -> 197
    //   164: astore_3
    //   165: aload_0
    //   166: invokespecial 1123	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:transportDebug	()Z
    //   169: ifeq +26 -> 195
    //   172: aload_0
    //   173: new 618	java/lang/StringBuilder
    //   176: dup
    //   177: invokespecial 1223	java/lang/StringBuilder:<init>	()V
    //   180: ldc 21
    //   182: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   185: aload_1
    //   186: invokevirtual 1227	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   189: invokevirtual 1224	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   192: invokespecial 1144	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:dprint	(Ljava/lang/String;)V
    //   195: aload_3
    //   196: athrow
    //   197: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	198	0	this	CorbaMessageMediatorImpl
    //   0	198	1	paramLocateRequestMessage_1_1	com.sun.corba.se.impl.protocol.giopmsgheaders.LocateRequestMessage_1_1
    //   47	7	2	localObject1	Object
    //   99	29	2	localThrowable	Throwable
    //   164	32	3	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   30	39	47	finally
    //   0	66	99	java/lang/Throwable
    //   0	66	164	finally
    //   99	131	164	finally
  }
  
  /* Error */
  public void handleInput(com.sun.corba.se.impl.protocol.giopmsgheaders.LocateRequestMessage_1_2 paramLocateRequestMessage_1_2)
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: aload_1
    //   2: putfield 1062	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:messageHeader	Lcom/sun/corba/se/impl/protocol/giopmsgheaders/Message;
    //   5: aload_1
    //   6: aload_0
    //   7: getfield 1070	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:dispatchByteBuffer	Ljava/nio/ByteBuffer;
    //   10: invokevirtual 1173	com/sun/corba/se/impl/protocol/giopmsgheaders/LocateRequestMessage_1_2:unmarshalRequestID	(Ljava/nio/ByteBuffer;)V
    //   13: aload_0
    //   14: invokespecial 1118	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:setInputObject	()V
    //   17: aload_0
    //   18: invokespecial 1123	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:transportDebug	()Z
    //   21: ifeq +38 -> 59
    //   24: aload_0
    //   25: new 618	java/lang/StringBuilder
    //   28: dup
    //   29: invokespecial 1223	java/lang/StringBuilder:<init>	()V
    //   32: ldc 22
    //   34: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   37: aload_1
    //   38: invokevirtual 1171	com/sun/corba/se/impl/protocol/giopmsgheaders/LocateRequestMessage_1_2:getRequestId	()I
    //   41: invokevirtual 1225	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   44: ldc 54
    //   46: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   49: aload_1
    //   50: invokevirtual 1227	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   53: invokevirtual 1224	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   56: invokespecial 1144	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:dprint	(Ljava/lang/String;)V
    //   59: aload_1
    //   60: invokevirtual 1172	com/sun/corba/se/impl/protocol/giopmsgheaders/LocateRequestMessage_1_2:moreFragmentsToFollow	()Z
    //   63: ifeq +17 -> 80
    //   66: aload_0
    //   67: getfield 1066	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:connection	Lcom/sun/corba/se/spi/transport/CorbaConnection;
    //   70: aload_1
    //   71: invokevirtual 1171	com/sun/corba/se/impl/protocol/giopmsgheaders/LocateRequestMessage_1_2:getRequestId	()I
    //   74: aload_0
    //   75: invokeinterface 1348 3 0
    //   80: aload_0
    //   81: aload_1
    //   82: invokespecial 1127	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:setWorkThenPoolOrResumeSelect	(Lcom/sun/corba/se/impl/protocol/giopmsgheaders/Message;)V
    //   85: goto +11 -> 96
    //   88: astore_2
    //   89: aload_0
    //   90: aload_1
    //   91: invokespecial 1127	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:setWorkThenPoolOrResumeSelect	(Lcom/sun/corba/se/impl/protocol/giopmsgheaders/Message;)V
    //   94: aload_2
    //   95: athrow
    //   96: aload_0
    //   97: invokevirtual 1142	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:getProtocolHandler	()Lcom/sun/corba/se/spi/protocol/CorbaProtocolHandler;
    //   100: aload_1
    //   101: aload_0
    //   102: invokeinterface 1323 3 0
    //   107: aload_0
    //   108: invokespecial 1123	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:transportDebug	()Z
    //   111: ifeq +175 -> 286
    //   114: aload_0
    //   115: new 618	java/lang/StringBuilder
    //   118: dup
    //   119: invokespecial 1223	java/lang/StringBuilder:<init>	()V
    //   122: ldc 24
    //   124: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   127: aload_1
    //   128: invokevirtual 1171	com/sun/corba/se/impl/protocol/giopmsgheaders/LocateRequestMessage_1_2:getRequestId	()I
    //   131: invokevirtual 1225	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   134: ldc 54
    //   136: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   139: aload_1
    //   140: invokevirtual 1227	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   143: invokevirtual 1224	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   146: invokespecial 1144	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:dprint	(Ljava/lang/String;)V
    //   149: goto +137 -> 286
    //   152: astore_2
    //   153: aload_0
    //   154: invokespecial 1123	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:transportDebug	()Z
    //   157: ifeq +39 -> 196
    //   160: aload_0
    //   161: new 618	java/lang/StringBuilder
    //   164: dup
    //   165: invokespecial 1223	java/lang/StringBuilder:<init>	()V
    //   168: ldc 23
    //   170: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   173: aload_1
    //   174: invokevirtual 1171	com/sun/corba/se/impl/protocol/giopmsgheaders/LocateRequestMessage_1_2:getRequestId	()I
    //   177: invokevirtual 1225	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   180: ldc 55
    //   182: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   185: aload_1
    //   186: invokevirtual 1227	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   189: invokevirtual 1224	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   192: aload_2
    //   193: invokespecial 1151	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:dprint	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   196: aload_0
    //   197: invokespecial 1123	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:transportDebug	()Z
    //   200: ifeq +86 -> 286
    //   203: aload_0
    //   204: new 618	java/lang/StringBuilder
    //   207: dup
    //   208: invokespecial 1223	java/lang/StringBuilder:<init>	()V
    //   211: ldc 24
    //   213: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   216: aload_1
    //   217: invokevirtual 1171	com/sun/corba/se/impl/protocol/giopmsgheaders/LocateRequestMessage_1_2:getRequestId	()I
    //   220: invokevirtual 1225	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   223: ldc 54
    //   225: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   228: aload_1
    //   229: invokevirtual 1227	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   232: invokevirtual 1224	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   235: invokespecial 1144	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:dprint	(Ljava/lang/String;)V
    //   238: goto +48 -> 286
    //   241: astore_3
    //   242: aload_0
    //   243: invokespecial 1123	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:transportDebug	()Z
    //   246: ifeq +38 -> 284
    //   249: aload_0
    //   250: new 618	java/lang/StringBuilder
    //   253: dup
    //   254: invokespecial 1223	java/lang/StringBuilder:<init>	()V
    //   257: ldc 24
    //   259: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   262: aload_1
    //   263: invokevirtual 1171	com/sun/corba/se/impl/protocol/giopmsgheaders/LocateRequestMessage_1_2:getRequestId	()I
    //   266: invokevirtual 1225	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   269: ldc 54
    //   271: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   274: aload_1
    //   275: invokevirtual 1227	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   278: invokevirtual 1224	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   281: invokespecial 1144	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:dprint	(Ljava/lang/String;)V
    //   284: aload_3
    //   285: athrow
    //   286: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	287	0	this	CorbaMessageMediatorImpl
    //   0	287	1	paramLocateRequestMessage_1_2	com.sun.corba.se.impl.protocol.giopmsgheaders.LocateRequestMessage_1_2
    //   88	7	2	localObject1	Object
    //   152	41	2	localThrowable	Throwable
    //   241	44	3	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   0	80	88	finally
    //   0	107	152	java/lang/Throwable
    //   0	107	241	finally
    //   152	196	241	finally
  }
  
  /* Error */
  public void handleInput(com.sun.corba.se.impl.protocol.giopmsgheaders.LocateReplyMessage_1_0 paramLocateReplyMessage_1_0)
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial 1123	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:transportDebug	()Z
    //   4: ifeq +26 -> 30
    //   7: aload_0
    //   8: new 618	java/lang/StringBuilder
    //   11: dup
    //   12: invokespecial 1223	java/lang/StringBuilder:<init>	()V
    //   15: ldc 7
    //   17: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   20: aload_1
    //   21: invokevirtual 1227	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   24: invokevirtual 1224	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   27: invokespecial 1144	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:dprint	(Ljava/lang/String;)V
    //   30: aload_0
    //   31: aload_1
    //   32: putfield 1062	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:messageHeader	Lcom/sun/corba/se/impl/protocol/giopmsgheaders/Message;
    //   35: aload_0
    //   36: invokespecial 1118	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:setInputObject	()V
    //   39: aload_0
    //   40: getfield 1056	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:inputObject	Lcom/sun/corba/se/impl/encoding/CDRInputObject;
    //   43: invokevirtual 1083	com/sun/corba/se/impl/encoding/CDRInputObject:unmarshalHeader	()V
    //   46: aload_0
    //   47: invokespecial 1119	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:signalResponseReceived	()V
    //   50: aload_0
    //   51: aload_1
    //   52: invokespecial 1128	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:setWorkThenReadOrResumeSelect	(Lcom/sun/corba/se/impl/protocol/giopmsgheaders/Message;)V
    //   55: goto +11 -> 66
    //   58: astore_2
    //   59: aload_0
    //   60: aload_1
    //   61: invokespecial 1128	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:setWorkThenReadOrResumeSelect	(Lcom/sun/corba/se/impl/protocol/giopmsgheaders/Message;)V
    //   64: aload_2
    //   65: athrow
    //   66: aload_0
    //   67: invokespecial 1123	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:transportDebug	()Z
    //   70: ifeq +127 -> 197
    //   73: aload_0
    //   74: new 618	java/lang/StringBuilder
    //   77: dup
    //   78: invokespecial 1223	java/lang/StringBuilder:<init>	()V
    //   81: ldc 9
    //   83: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   86: aload_1
    //   87: invokevirtual 1227	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   90: invokevirtual 1224	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   93: invokespecial 1144	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:dprint	(Ljava/lang/String;)V
    //   96: goto +101 -> 197
    //   99: astore_2
    //   100: aload_0
    //   101: invokespecial 1123	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:transportDebug	()Z
    //   104: ifeq +27 -> 131
    //   107: aload_0
    //   108: new 618	java/lang/StringBuilder
    //   111: dup
    //   112: invokespecial 1223	java/lang/StringBuilder:<init>	()V
    //   115: ldc 8
    //   117: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   120: aload_1
    //   121: invokevirtual 1227	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   124: invokevirtual 1224	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   127: aload_2
    //   128: invokespecial 1151	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:dprint	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   131: aload_0
    //   132: invokespecial 1123	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:transportDebug	()Z
    //   135: ifeq +62 -> 197
    //   138: aload_0
    //   139: new 618	java/lang/StringBuilder
    //   142: dup
    //   143: invokespecial 1223	java/lang/StringBuilder:<init>	()V
    //   146: ldc 9
    //   148: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   151: aload_1
    //   152: invokevirtual 1227	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   155: invokevirtual 1224	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   158: invokespecial 1144	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:dprint	(Ljava/lang/String;)V
    //   161: goto +36 -> 197
    //   164: astore_3
    //   165: aload_0
    //   166: invokespecial 1123	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:transportDebug	()Z
    //   169: ifeq +26 -> 195
    //   172: aload_0
    //   173: new 618	java/lang/StringBuilder
    //   176: dup
    //   177: invokespecial 1223	java/lang/StringBuilder:<init>	()V
    //   180: ldc 9
    //   182: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   185: aload_1
    //   186: invokevirtual 1227	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   189: invokevirtual 1224	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   192: invokespecial 1144	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:dprint	(Ljava/lang/String;)V
    //   195: aload_3
    //   196: athrow
    //   197: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	198	0	this	CorbaMessageMediatorImpl
    //   0	198	1	paramLocateReplyMessage_1_0	com.sun.corba.se.impl.protocol.giopmsgheaders.LocateReplyMessage_1_0
    //   58	7	2	localObject1	Object
    //   99	29	2	localThrowable	Throwable
    //   164	32	3	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   30	50	58	finally
    //   0	66	99	java/lang/Throwable
    //   0	66	164	finally
    //   99	131	164	finally
  }
  
  /* Error */
  public void handleInput(com.sun.corba.se.impl.protocol.giopmsgheaders.LocateReplyMessage_1_1 paramLocateReplyMessage_1_1)
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial 1123	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:transportDebug	()Z
    //   4: ifeq +26 -> 30
    //   7: aload_0
    //   8: new 618	java/lang/StringBuilder
    //   11: dup
    //   12: invokespecial 1223	java/lang/StringBuilder:<init>	()V
    //   15: ldc 10
    //   17: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   20: aload_1
    //   21: invokevirtual 1227	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   24: invokevirtual 1224	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   27: invokespecial 1144	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:dprint	(Ljava/lang/String;)V
    //   30: aload_0
    //   31: aload_1
    //   32: putfield 1062	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:messageHeader	Lcom/sun/corba/se/impl/protocol/giopmsgheaders/Message;
    //   35: aload_0
    //   36: invokespecial 1118	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:setInputObject	()V
    //   39: aload_0
    //   40: getfield 1056	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:inputObject	Lcom/sun/corba/se/impl/encoding/CDRInputObject;
    //   43: invokevirtual 1083	com/sun/corba/se/impl/encoding/CDRInputObject:unmarshalHeader	()V
    //   46: aload_0
    //   47: invokespecial 1119	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:signalResponseReceived	()V
    //   50: aload_0
    //   51: aload_1
    //   52: invokespecial 1128	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:setWorkThenReadOrResumeSelect	(Lcom/sun/corba/se/impl/protocol/giopmsgheaders/Message;)V
    //   55: goto +11 -> 66
    //   58: astore_2
    //   59: aload_0
    //   60: aload_1
    //   61: invokespecial 1128	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:setWorkThenReadOrResumeSelect	(Lcom/sun/corba/se/impl/protocol/giopmsgheaders/Message;)V
    //   64: aload_2
    //   65: athrow
    //   66: aload_0
    //   67: invokespecial 1123	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:transportDebug	()Z
    //   70: ifeq +127 -> 197
    //   73: aload_0
    //   74: new 618	java/lang/StringBuilder
    //   77: dup
    //   78: invokespecial 1223	java/lang/StringBuilder:<init>	()V
    //   81: ldc 12
    //   83: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   86: aload_1
    //   87: invokevirtual 1227	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   90: invokevirtual 1224	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   93: invokespecial 1144	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:dprint	(Ljava/lang/String;)V
    //   96: goto +101 -> 197
    //   99: astore_2
    //   100: aload_0
    //   101: invokespecial 1123	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:transportDebug	()Z
    //   104: ifeq +27 -> 131
    //   107: aload_0
    //   108: new 618	java/lang/StringBuilder
    //   111: dup
    //   112: invokespecial 1223	java/lang/StringBuilder:<init>	()V
    //   115: ldc 11
    //   117: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   120: aload_1
    //   121: invokevirtual 1227	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   124: invokevirtual 1224	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   127: aload_2
    //   128: invokespecial 1151	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:dprint	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   131: aload_0
    //   132: invokespecial 1123	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:transportDebug	()Z
    //   135: ifeq +62 -> 197
    //   138: aload_0
    //   139: new 618	java/lang/StringBuilder
    //   142: dup
    //   143: invokespecial 1223	java/lang/StringBuilder:<init>	()V
    //   146: ldc 12
    //   148: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   151: aload_1
    //   152: invokevirtual 1227	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   155: invokevirtual 1224	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   158: invokespecial 1144	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:dprint	(Ljava/lang/String;)V
    //   161: goto +36 -> 197
    //   164: astore_3
    //   165: aload_0
    //   166: invokespecial 1123	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:transportDebug	()Z
    //   169: ifeq +26 -> 195
    //   172: aload_0
    //   173: new 618	java/lang/StringBuilder
    //   176: dup
    //   177: invokespecial 1223	java/lang/StringBuilder:<init>	()V
    //   180: ldc 12
    //   182: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   185: aload_1
    //   186: invokevirtual 1227	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   189: invokevirtual 1224	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   192: invokespecial 1144	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:dprint	(Ljava/lang/String;)V
    //   195: aload_3
    //   196: athrow
    //   197: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	198	0	this	CorbaMessageMediatorImpl
    //   0	198	1	paramLocateReplyMessage_1_1	com.sun.corba.se.impl.protocol.giopmsgheaders.LocateReplyMessage_1_1
    //   58	7	2	localObject1	Object
    //   99	29	2	localThrowable	Throwable
    //   164	32	3	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   30	50	58	finally
    //   0	66	99	java/lang/Throwable
    //   0	66	164	finally
    //   99	131	164	finally
  }
  
  /* Error */
  public void handleInput(com.sun.corba.se.impl.protocol.giopmsgheaders.LocateReplyMessage_1_2 paramLocateReplyMessage_1_2)
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: aload_1
    //   2: putfield 1062	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:messageHeader	Lcom/sun/corba/se/impl/protocol/giopmsgheaders/Message;
    //   5: aload_1
    //   6: aload_0
    //   7: getfield 1070	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:dispatchByteBuffer	Ljava/nio/ByteBuffer;
    //   10: invokevirtual 1170	com/sun/corba/se/impl/protocol/giopmsgheaders/LocateReplyMessage_1_2:unmarshalRequestID	(Ljava/nio/ByteBuffer;)V
    //   13: aload_0
    //   14: invokespecial 1118	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:setInputObject	()V
    //   17: aload_0
    //   18: invokespecial 1123	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:transportDebug	()Z
    //   21: ifeq +38 -> 59
    //   24: aload_0
    //   25: new 618	java/lang/StringBuilder
    //   28: dup
    //   29: invokespecial 1223	java/lang/StringBuilder:<init>	()V
    //   32: ldc 13
    //   34: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   37: aload_1
    //   38: invokevirtual 1169	com/sun/corba/se/impl/protocol/giopmsgheaders/LocateReplyMessage_1_2:getRequestId	()I
    //   41: invokevirtual 1225	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   44: ldc 54
    //   46: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   49: aload_1
    //   50: invokevirtual 1227	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   53: invokevirtual 1224	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   56: invokespecial 1144	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:dprint	(Ljava/lang/String;)V
    //   59: aload_0
    //   60: invokespecial 1119	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:signalResponseReceived	()V
    //   63: aload_0
    //   64: aload_1
    //   65: invokespecial 1127	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:setWorkThenPoolOrResumeSelect	(Lcom/sun/corba/se/impl/protocol/giopmsgheaders/Message;)V
    //   68: goto +11 -> 79
    //   71: astore_2
    //   72: aload_0
    //   73: aload_1
    //   74: invokespecial 1127	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:setWorkThenPoolOrResumeSelect	(Lcom/sun/corba/se/impl/protocol/giopmsgheaders/Message;)V
    //   77: aload_2
    //   78: athrow
    //   79: aload_0
    //   80: invokespecial 1123	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:transportDebug	()Z
    //   83: ifeq +175 -> 258
    //   86: aload_0
    //   87: new 618	java/lang/StringBuilder
    //   90: dup
    //   91: invokespecial 1223	java/lang/StringBuilder:<init>	()V
    //   94: ldc 15
    //   96: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   99: aload_1
    //   100: invokevirtual 1169	com/sun/corba/se/impl/protocol/giopmsgheaders/LocateReplyMessage_1_2:getRequestId	()I
    //   103: invokevirtual 1225	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   106: ldc 54
    //   108: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   111: aload_1
    //   112: invokevirtual 1227	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   115: invokevirtual 1224	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   118: invokespecial 1144	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:dprint	(Ljava/lang/String;)V
    //   121: goto +137 -> 258
    //   124: astore_2
    //   125: aload_0
    //   126: invokespecial 1123	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:transportDebug	()Z
    //   129: ifeq +39 -> 168
    //   132: aload_0
    //   133: new 618	java/lang/StringBuilder
    //   136: dup
    //   137: invokespecial 1223	java/lang/StringBuilder:<init>	()V
    //   140: ldc 14
    //   142: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   145: aload_1
    //   146: invokevirtual 1169	com/sun/corba/se/impl/protocol/giopmsgheaders/LocateReplyMessage_1_2:getRequestId	()I
    //   149: invokevirtual 1225	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   152: ldc 55
    //   154: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   157: aload_1
    //   158: invokevirtual 1227	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   161: invokevirtual 1224	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   164: aload_2
    //   165: invokespecial 1151	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:dprint	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   168: aload_0
    //   169: invokespecial 1123	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:transportDebug	()Z
    //   172: ifeq +86 -> 258
    //   175: aload_0
    //   176: new 618	java/lang/StringBuilder
    //   179: dup
    //   180: invokespecial 1223	java/lang/StringBuilder:<init>	()V
    //   183: ldc 15
    //   185: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   188: aload_1
    //   189: invokevirtual 1169	com/sun/corba/se/impl/protocol/giopmsgheaders/LocateReplyMessage_1_2:getRequestId	()I
    //   192: invokevirtual 1225	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   195: ldc 54
    //   197: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   200: aload_1
    //   201: invokevirtual 1227	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   204: invokevirtual 1224	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   207: invokespecial 1144	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:dprint	(Ljava/lang/String;)V
    //   210: goto +48 -> 258
    //   213: astore_3
    //   214: aload_0
    //   215: invokespecial 1123	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:transportDebug	()Z
    //   218: ifeq +38 -> 256
    //   221: aload_0
    //   222: new 618	java/lang/StringBuilder
    //   225: dup
    //   226: invokespecial 1223	java/lang/StringBuilder:<init>	()V
    //   229: ldc 15
    //   231: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   234: aload_1
    //   235: invokevirtual 1169	com/sun/corba/se/impl/protocol/giopmsgheaders/LocateReplyMessage_1_2:getRequestId	()I
    //   238: invokevirtual 1225	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   241: ldc 54
    //   243: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   246: aload_1
    //   247: invokevirtual 1227	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   250: invokevirtual 1224	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   253: invokespecial 1144	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:dprint	(Ljava/lang/String;)V
    //   256: aload_3
    //   257: athrow
    //   258: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	259	0	this	CorbaMessageMediatorImpl
    //   0	259	1	paramLocateReplyMessage_1_2	com.sun.corba.se.impl.protocol.giopmsgheaders.LocateReplyMessage_1_2
    //   71	7	2	localObject1	Object
    //   124	41	2	localThrowable	Throwable
    //   213	44	3	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   0	63	71	finally
    //   0	79	124	java/lang/Throwable
    //   0	79	213	finally
    //   124	168	213	finally
  }
  
  public void handleInput(FragmentMessage_1_1 paramFragmentMessage_1_1)
    throws IOException
  {
    try
    {
      if (transportDebug()) {
        dprint(".FRAGMENT 1.1->: more?: " + paramFragmentMessage_1_1.moreFragmentsToFollow() + ": " + paramFragmentMessage_1_1);
      }
      try
      {
        messageHeader = paramFragmentMessage_1_1;
        MessageMediator localMessageMediator = null;
        CDRInputObject localCDRInputObject = null;
        if (connection.isServer()) {
          localMessageMediator = connection.serverRequest_1_1_Get();
        } else {
          localMessageMediator = connection.clientReply_1_1_Get();
        }
        if (localMessageMediator != null) {
          localCDRInputObject = (CDRInputObject)localMessageMediator.getInputObject();
        }
        if (localCDRInputObject == null)
        {
          if (transportDebug()) {
            dprint(".FRAGMENT 1.1: ++++DISCARDING++++: " + paramFragmentMessage_1_1);
          }
          releaseByteBufferToPool();
          setWorkThenReadOrResumeSelect(paramFragmentMessage_1_1);
          return;
        }
        localCDRInputObject.getBufferManager().processFragment(dispatchByteBuffer, paramFragmentMessage_1_1);
        if (!paramFragmentMessage_1_1.moreFragmentsToFollow()) {
          if (connection.isServer()) {
            connection.serverRequest_1_1_Remove();
          } else {
            connection.clientReply_1_1_Remove();
          }
        }
      }
      finally
      {
        setWorkThenReadOrResumeSelect(paramFragmentMessage_1_1);
      }
    }
    catch (Throwable localThrowable)
    {
      if (transportDebug()) {
        dprint(".FRAGMENT 1.1: !!ERROR!!: " + paramFragmentMessage_1_1, localThrowable);
      }
    }
    finally
    {
      if (transportDebug()) {
        dprint(".FRAGMENT 1.1<-: " + paramFragmentMessage_1_1);
      }
    }
  }
  
  public void handleInput(FragmentMessage_1_2 paramFragmentMessage_1_2)
    throws IOException
  {
    try
    {
      try
      {
        messageHeader = paramFragmentMessage_1_2;
        paramFragmentMessage_1_2.unmarshalRequestID(dispatchByteBuffer);
        if (transportDebug()) {
          dprint(".FRAGMENT 1.2->: id/" + paramFragmentMessage_1_2.getRequestId() + ": more?: " + paramFragmentMessage_1_2.moreFragmentsToFollow() + ": " + paramFragmentMessage_1_2);
        }
        Object localObject1 = null;
        InputObject localInputObject = null;
        if (connection.isServer()) {
          localObject1 = connection.serverRequestMapGet(paramFragmentMessage_1_2.getRequestId());
        } else {
          localObject1 = connection.clientRequestMapGet(paramFragmentMessage_1_2.getRequestId());
        }
        if (localObject1 != null) {
          localInputObject = ((MessageMediator)localObject1).getInputObject();
        }
        if (localInputObject == null)
        {
          if (transportDebug()) {
            dprint(".FRAGMENT 1.2: id/" + paramFragmentMessage_1_2.getRequestId() + ": ++++DISCARDING++++: " + paramFragmentMessage_1_2);
          }
          releaseByteBufferToPool();
          setWorkThenReadOrResumeSelect(paramFragmentMessage_1_2);
          return;
        }
        ((CDRInputObject)localInputObject).getBufferManager().processFragment(dispatchByteBuffer, paramFragmentMessage_1_2);
        if (connection.isServer()) {}
      }
      finally
      {
        setWorkThenReadOrResumeSelect(paramFragmentMessage_1_2);
      }
    }
    catch (Throwable localThrowable)
    {
      if (transportDebug()) {
        dprint(".FRAGMENT 1.2: id/" + paramFragmentMessage_1_2.getRequestId() + ": !!ERROR!!: " + paramFragmentMessage_1_2, localThrowable);
      }
    }
    finally
    {
      if (transportDebug()) {
        dprint(".FRAGMENT 1.2<-: id/" + paramFragmentMessage_1_2.getRequestId() + ": " + paramFragmentMessage_1_2);
      }
    }
  }
  
  /* Error */
  public void handleInput(com.sun.corba.se.impl.protocol.giopmsgheaders.CancelRequestMessage paramCancelRequestMessage)
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: aload_1
    //   2: putfield 1062	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:messageHeader	Lcom/sun/corba/se/impl/protocol/giopmsgheaders/Message;
    //   5: aload_0
    //   6: invokespecial 1118	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:setInputObject	()V
    //   9: aload_0
    //   10: getfield 1056	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:inputObject	Lcom/sun/corba/se/impl/encoding/CDRInputObject;
    //   13: invokevirtual 1083	com/sun/corba/se/impl/encoding/CDRInputObject:unmarshalHeader	()V
    //   16: aload_0
    //   17: invokespecial 1123	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:transportDebug	()Z
    //   20: ifeq +55 -> 75
    //   23: aload_0
    //   24: new 618	java/lang/StringBuilder
    //   27: dup
    //   28: invokespecial 1223	java/lang/StringBuilder:<init>	()V
    //   31: ldc_w 516
    //   34: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   37: aload_1
    //   38: invokeinterface 1246 1 0
    //   43: invokevirtual 1225	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   46: ldc 54
    //   48: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   51: aload_1
    //   52: invokeinterface 1247 1 0
    //   57: invokevirtual 1227	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   60: ldc 54
    //   62: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   65: aload_1
    //   66: invokevirtual 1227	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   69: invokevirtual 1224	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   72: invokespecial 1144	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:dprint	(Ljava/lang/String;)V
    //   75: aload_0
    //   76: aload_1
    //   77: invokeinterface 1246 1 0
    //   82: invokespecial 1124	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:processCancelRequest	(I)V
    //   85: aload_0
    //   86: invokespecial 1117	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:releaseByteBufferToPool	()V
    //   89: aload_0
    //   90: aload_1
    //   91: invokespecial 1128	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:setWorkThenReadOrResumeSelect	(Lcom/sun/corba/se/impl/protocol/giopmsgheaders/Message;)V
    //   94: goto +11 -> 105
    //   97: astore_2
    //   98: aload_0
    //   99: aload_1
    //   100: invokespecial 1128	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:setWorkThenReadOrResumeSelect	(Lcom/sun/corba/se/impl/protocol/giopmsgheaders/Message;)V
    //   103: aload_2
    //   104: athrow
    //   105: aload_0
    //   106: invokespecial 1123	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:transportDebug	()Z
    //   109: ifeq +229 -> 338
    //   112: aload_0
    //   113: new 618	java/lang/StringBuilder
    //   116: dup
    //   117: invokespecial 1223	java/lang/StringBuilder:<init>	()V
    //   120: ldc_w 518
    //   123: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   126: aload_1
    //   127: invokeinterface 1246 1 0
    //   132: invokevirtual 1225	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   135: ldc 54
    //   137: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   140: aload_1
    //   141: invokeinterface 1247 1 0
    //   146: invokevirtual 1227	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   149: ldc 54
    //   151: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   154: aload_1
    //   155: invokevirtual 1227	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   158: invokevirtual 1224	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   161: invokespecial 1144	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:dprint	(Ljava/lang/String;)V
    //   164: goto +174 -> 338
    //   167: astore_2
    //   168: aload_0
    //   169: invokespecial 1123	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:transportDebug	()Z
    //   172: ifeq +42 -> 214
    //   175: aload_0
    //   176: new 618	java/lang/StringBuilder
    //   179: dup
    //   180: invokespecial 1223	java/lang/StringBuilder:<init>	()V
    //   183: ldc_w 517
    //   186: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   189: aload_1
    //   190: invokeinterface 1246 1 0
    //   195: invokevirtual 1225	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   198: ldc 55
    //   200: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   203: aload_1
    //   204: invokevirtual 1227	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   207: invokevirtual 1224	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   210: aload_2
    //   211: invokespecial 1151	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:dprint	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   214: aload_0
    //   215: invokespecial 1123	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:transportDebug	()Z
    //   218: ifeq +120 -> 338
    //   221: aload_0
    //   222: new 618	java/lang/StringBuilder
    //   225: dup
    //   226: invokespecial 1223	java/lang/StringBuilder:<init>	()V
    //   229: ldc_w 518
    //   232: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   235: aload_1
    //   236: invokeinterface 1246 1 0
    //   241: invokevirtual 1225	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   244: ldc 54
    //   246: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   249: aload_1
    //   250: invokeinterface 1247 1 0
    //   255: invokevirtual 1227	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   258: ldc 54
    //   260: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   263: aload_1
    //   264: invokevirtual 1227	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   267: invokevirtual 1224	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   270: invokespecial 1144	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:dprint	(Ljava/lang/String;)V
    //   273: goto +65 -> 338
    //   276: astore_3
    //   277: aload_0
    //   278: invokespecial 1123	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:transportDebug	()Z
    //   281: ifeq +55 -> 336
    //   284: aload_0
    //   285: new 618	java/lang/StringBuilder
    //   288: dup
    //   289: invokespecial 1223	java/lang/StringBuilder:<init>	()V
    //   292: ldc_w 518
    //   295: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   298: aload_1
    //   299: invokeinterface 1246 1 0
    //   304: invokevirtual 1225	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   307: ldc 54
    //   309: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   312: aload_1
    //   313: invokeinterface 1247 1 0
    //   318: invokevirtual 1227	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   321: ldc 54
    //   323: invokevirtual 1228	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   326: aload_1
    //   327: invokevirtual 1227	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   330: invokevirtual 1224	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   333: invokespecial 1144	com/sun/corba/se/impl/protocol/CorbaMessageMediatorImpl:dprint	(Ljava/lang/String;)V
    //   336: aload_3
    //   337: athrow
    //   338: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	339	0	this	CorbaMessageMediatorImpl
    //   0	339	1	paramCancelRequestMessage	com.sun.corba.se.impl.protocol.giopmsgheaders.CancelRequestMessage
    //   97	7	2	localObject1	Object
    //   167	44	2	localThrowable	Throwable
    //   276	61	3	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   0	89	97	finally
    //   0	105	167	java/lang/Throwable
    //   0	105	276	finally
    //   167	214	276	finally
  }
  
  private void throwNotImplemented()
  {
    isThreadDone = false;
    throwNotImplemented("");
  }
  
  private void throwNotImplemented(String paramString)
  {
    throw new RuntimeException("CorbaMessageMediatorImpl: not implemented " + paramString);
  }
  
  private void dprint(String paramString, Throwable paramThrowable)
  {
    dprint(paramString);
    paramThrowable.printStackTrace(System.out);
  }
  
  private void dprint(String paramString)
  {
    ORBUtility.dprint("CorbaMessageMediatorImpl", paramString);
  }
  
  protected String opAndId(CorbaMessageMediator paramCorbaMessageMediator)
  {
    return ORBUtility.operationNameAndRequestId(paramCorbaMessageMediator);
  }
  
  private boolean transportDebug()
  {
    return orb.transportDebugFlag;
  }
  
  private final void processCancelRequest(int paramInt)
  {
    if (!connection.isServer()) {
      return;
    }
    Object localObject = connection.serverRequestMapGet(paramInt);
    int i;
    if (localObject == null)
    {
      localObject = connection.serverRequest_1_1_Get();
      if (localObject == null) {
        return;
      }
      i = ((CorbaMessageMediator)localObject).getRequestId();
      if (i != paramInt) {
        return;
      }
      if (i != 0) {}
    }
    else
    {
      i = ((CorbaMessageMediator)localObject).getRequestId();
    }
    RequestMessage localRequestMessage = ((CorbaMessageMediator)localObject).getRequestHeader();
    if (localRequestMessage.getType() != 0) {
      wrapper.badMessageTypeForCancel();
    }
    BufferManagerReadStream localBufferManagerReadStream = (BufferManagerReadStream)((CDRInputObject)((MessageMediator)localObject).getInputObject()).getBufferManager();
    localBufferManagerReadStream.cancelProcessing(paramInt);
  }
  
  public void handleRequest(RequestMessage paramRequestMessage, CorbaMessageMediator paramCorbaMessageMediator)
  {
    try
    {
      beginRequest(paramCorbaMessageMediator);
      try
      {
        handleRequestRequest(paramCorbaMessageMediator);
        if (paramCorbaMessageMediator.isOneWay()) {
          return;
        }
      }
      catch (Throwable localThrowable1)
      {
        if (paramCorbaMessageMediator.isOneWay()) {
          return;
        }
        handleThrowableDuringServerDispatch(paramCorbaMessageMediator, localThrowable1, CompletionStatus.COMPLETED_MAYBE);
      }
      sendResponse(paramCorbaMessageMediator);
    }
    catch (Throwable localThrowable2)
    {
      dispatchError(paramCorbaMessageMediator, "RequestMessage", localThrowable2);
    }
    finally
    {
      endRequest(paramCorbaMessageMediator);
    }
  }
  
  public void handleRequest(LocateRequestMessage paramLocateRequestMessage, CorbaMessageMediator paramCorbaMessageMediator)
  {
    try
    {
      beginRequest(paramCorbaMessageMediator);
      try
      {
        handleLocateRequest(paramCorbaMessageMediator);
      }
      catch (Throwable localThrowable1)
      {
        handleThrowableDuringServerDispatch(paramCorbaMessageMediator, localThrowable1, CompletionStatus.COMPLETED_MAYBE);
      }
      sendResponse(paramCorbaMessageMediator);
    }
    catch (Throwable localThrowable2)
    {
      dispatchError(paramCorbaMessageMediator, "LocateRequestMessage", localThrowable2);
    }
    finally
    {
      endRequest(paramCorbaMessageMediator);
    }
  }
  
  private void beginRequest(CorbaMessageMediator paramCorbaMessageMediator)
  {
    ORB localORB = (ORB)paramCorbaMessageMediator.getBroker();
    if (subcontractDebugFlag) {
      dprint(".handleRequest->:");
    }
    connection.serverRequestProcessingBegins();
  }
  
  private void dispatchError(CorbaMessageMediator paramCorbaMessageMediator, String paramString, Throwable paramThrowable)
  {
    if (orb.subcontractDebugFlag) {
      dprint(".handleRequest: " + opAndId(paramCorbaMessageMediator) + ": !!ERROR!!: " + paramString, paramThrowable);
    }
  }
  
  private void sendResponse(CorbaMessageMediator paramCorbaMessageMediator)
  {
    if (orb.subcontractDebugFlag) {
      dprint(".handleRequest: " + opAndId(paramCorbaMessageMediator) + ": sending response");
    }
    CDROutputObject localCDROutputObject = (CDROutputObject)paramCorbaMessageMediator.getOutputObject();
    if (localCDROutputObject != null) {
      localCDROutputObject.finishSendingMessage();
    }
  }
  
  private void endRequest(CorbaMessageMediator paramCorbaMessageMediator)
  {
    ORB localORB = (ORB)paramCorbaMessageMediator.getBroker();
    if (subcontractDebugFlag) {
      dprint(".handleRequest<-: " + opAndId(paramCorbaMessageMediator));
    }
    try
    {
      OutputObject localOutputObject = paramCorbaMessageMediator.getOutputObject();
      if (localOutputObject != null) {
        localOutputObject.close();
      }
      InputObject localInputObject = paramCorbaMessageMediator.getInputObject();
      if (localInputObject != null) {
        localInputObject.close();
      }
    }
    catch (IOException localIOException)
    {
      if (subcontractDebugFlag) {
        dprint(".endRequest: IOException:" + localIOException.getMessage(), localIOException);
      }
    }
    finally
    {
      ((CorbaConnection)paramCorbaMessageMediator.getConnection()).serverRequestProcessingEnds();
    }
  }
  
  protected void handleRequestRequest(CorbaMessageMediator paramCorbaMessageMediator)
  {
    ((CDRInputObject)paramCorbaMessageMediator.getInputObject()).unmarshalHeader();
    ORB localORB = (ORB)paramCorbaMessageMediator.getBroker();
    synchronized (localORB)
    {
      localORB.checkShutdownState();
    }
    ??? = paramCorbaMessageMediator.getObjectKey();
    if (subcontractDebugFlag)
    {
      localObject2 = ((ObjectKey)???).getTemplate();
      dprint(".handleRequest: " + opAndId(paramCorbaMessageMediator) + ": dispatching to scid: " + ((ObjectKeyTemplate)localObject2).getSubcontractId());
    }
    Object localObject2 = ((ObjectKey)???).getServerRequestDispatcher(localORB);
    if (subcontractDebugFlag) {
      dprint(".handleRequest: " + opAndId(paramCorbaMessageMediator) + ": dispatching to sc: " + localObject2);
    }
    if (localObject2 == null) {
      throw wrapper.noServerScInDispatch();
    }
    try
    {
      localORB.startingDispatch();
      ((CorbaServerRequestDispatcher)localObject2).dispatch(paramCorbaMessageMediator);
    }
    finally
    {
      localORB.finishedDispatch();
    }
  }
  
  protected void handleLocateRequest(CorbaMessageMediator paramCorbaMessageMediator)
  {
    ORB localORB = (ORB)paramCorbaMessageMediator.getBroker();
    LocateRequestMessage localLocateRequestMessage = (LocateRequestMessage)paramCorbaMessageMediator.getDispatchHeader();
    IOR localIOR = null;
    LocateReplyMessage localLocateReplyMessage = null;
    short s = -1;
    try
    {
      ((CDRInputObject)paramCorbaMessageMediator.getInputObject()).unmarshalHeader();
      CorbaServerRequestDispatcher localCorbaServerRequestDispatcher = localLocateRequestMessage.getObjectKey().getServerRequestDispatcher(localORB);
      if (localCorbaServerRequestDispatcher == null) {
        return;
      }
      localIOR = localCorbaServerRequestDispatcher.locate(localLocateRequestMessage.getObjectKey());
      if (localIOR == null) {
        localLocateReplyMessage = MessageBase.createLocateReply(localORB, localLocateRequestMessage.getGIOPVersion(), localLocateRequestMessage.getEncodingVersion(), localLocateRequestMessage.getRequestId(), 1, null);
      } else {
        localLocateReplyMessage = MessageBase.createLocateReply(localORB, localLocateRequestMessage.getGIOPVersion(), localLocateRequestMessage.getEncodingVersion(), localLocateRequestMessage.getRequestId(), 2, localIOR);
      }
    }
    catch (AddressingDispositionException localAddressingDispositionException)
    {
      localLocateReplyMessage = MessageBase.createLocateReply(localORB, localLocateRequestMessage.getGIOPVersion(), localLocateRequestMessage.getEncodingVersion(), localLocateRequestMessage.getRequestId(), 5, null);
      s = localAddressingDispositionException.expectedAddrDisp();
    }
    catch (RequestCanceledException localRequestCanceledException)
    {
      return;
    }
    catch (Exception localException)
    {
      localLocateReplyMessage = MessageBase.createLocateReply(localORB, localLocateRequestMessage.getGIOPVersion(), localLocateRequestMessage.getEncodingVersion(), localLocateRequestMessage.getRequestId(), 0, null);
    }
    CDROutputObject localCDROutputObject = createAppropriateOutputObject(paramCorbaMessageMediator, localLocateRequestMessage, localLocateReplyMessage);
    paramCorbaMessageMediator.setOutputObject(localCDROutputObject);
    localCDROutputObject.setMessageMediator(paramCorbaMessageMediator);
    localLocateReplyMessage.write(localCDROutputObject);
    if (localIOR != null) {
      localIOR.write(localCDROutputObject);
    }
    if (s != -1) {
      AddressingDispositionHelper.write(localCDROutputObject, s);
    }
  }
  
  private CDROutputObject createAppropriateOutputObject(CorbaMessageMediator paramCorbaMessageMediator, Message paramMessage, LocateReplyMessage paramLocateReplyMessage)
  {
    CDROutputObject localCDROutputObject;
    if (paramMessage.getGIOPVersion().lessThan(GIOPVersion.V1_2)) {
      localCDROutputObject = OutputStreamFactory.newCDROutputObject((ORB)paramCorbaMessageMediator.getBroker(), this, GIOPVersion.V1_0, (CorbaConnection)paramCorbaMessageMediator.getConnection(), paramLocateReplyMessage, (byte)1);
    } else {
      localCDROutputObject = OutputStreamFactory.newCDROutputObject((ORB)paramCorbaMessageMediator.getBroker(), paramCorbaMessageMediator, paramLocateReplyMessage, (byte)1);
    }
    return localCDROutputObject;
  }
  
  public void handleThrowableDuringServerDispatch(CorbaMessageMediator paramCorbaMessageMediator, Throwable paramThrowable, CompletionStatus paramCompletionStatus)
  {
    if (getBrokersubcontractDebugFlag) {
      dprint(".handleThrowableDuringServerDispatch: " + opAndId(paramCorbaMessageMediator) + ": " + paramThrowable);
    }
    handleThrowableDuringServerDispatch(paramCorbaMessageMediator, paramThrowable, paramCompletionStatus, 1);
  }
  
  protected void handleThrowableDuringServerDispatch(CorbaMessageMediator paramCorbaMessageMediator, Throwable paramThrowable, CompletionStatus paramCompletionStatus, int paramInt)
  {
    Object localObject;
    if (paramInt > 10)
    {
      if (getBrokersubcontractDebugFlag) {
        dprint(".handleThrowableDuringServerDispatch: " + opAndId(paramCorbaMessageMediator) + ": cannot handle: " + paramThrowable);
      }
      localObject = new RuntimeException("handleThrowableDuringServerDispatch: cannot create response.");
      ((RuntimeException)localObject).initCause(paramThrowable);
      throw ((Throwable)localObject);
    }
    try
    {
      if ((paramThrowable instanceof ForwardException))
      {
        localObject = (ForwardException)paramThrowable;
        createLocationForward(paramCorbaMessageMediator, ((ForwardException)localObject).getIOR(), null);
        return;
      }
      if ((paramThrowable instanceof AddressingDispositionException))
      {
        handleAddressingDisposition(paramCorbaMessageMediator, (AddressingDispositionException)paramThrowable);
        return;
      }
      localObject = convertThrowableToSystemException(paramThrowable, paramCompletionStatus);
      createSystemExceptionResponse(paramCorbaMessageMediator, (SystemException)localObject, null);
      return;
    }
    catch (Throwable localThrowable)
    {
      handleThrowableDuringServerDispatch(paramCorbaMessageMediator, localThrowable, paramCompletionStatus, paramInt + 1);
    }
  }
  
  protected SystemException convertThrowableToSystemException(Throwable paramThrowable, CompletionStatus paramCompletionStatus)
  {
    if ((paramThrowable instanceof SystemException)) {
      return (SystemException)paramThrowable;
    }
    if ((paramThrowable instanceof RequestCanceledException)) {
      return wrapper.requestCanceled(paramThrowable);
    }
    return wrapper.runtimeexception(CompletionStatus.COMPLETED_MAYBE, paramThrowable);
  }
  
  protected void handleAddressingDisposition(CorbaMessageMediator paramCorbaMessageMediator, AddressingDispositionException paramAddressingDispositionException)
  {
    short s = -1;
    CDROutputObject localCDROutputObject;
    switch (paramCorbaMessageMediator.getRequestHeader().getType())
    {
    case 0: 
      ReplyMessage localReplyMessage = MessageBase.createReply((ORB)paramCorbaMessageMediator.getBroker(), paramCorbaMessageMediator.getGIOPVersion(), paramCorbaMessageMediator.getEncodingVersion(), paramCorbaMessageMediator.getRequestId(), 5, null, null);
      localCDROutputObject = OutputStreamFactory.newCDROutputObject((ORB)paramCorbaMessageMediator.getBroker(), this, paramCorbaMessageMediator.getGIOPVersion(), (CorbaConnection)paramCorbaMessageMediator.getConnection(), localReplyMessage, (byte)1);
      paramCorbaMessageMediator.setOutputObject(localCDROutputObject);
      localCDROutputObject.setMessageMediator(paramCorbaMessageMediator);
      localReplyMessage.write(localCDROutputObject);
      AddressingDispositionHelper.write(localCDROutputObject, paramAddressingDispositionException.expectedAddrDisp());
      return;
    case 3: 
      LocateReplyMessage localLocateReplyMessage = MessageBase.createLocateReply((ORB)paramCorbaMessageMediator.getBroker(), paramCorbaMessageMediator.getGIOPVersion(), paramCorbaMessageMediator.getEncodingVersion(), paramCorbaMessageMediator.getRequestId(), 5, null);
      s = paramAddressingDispositionException.expectedAddrDisp();
      localCDROutputObject = createAppropriateOutputObject(paramCorbaMessageMediator, paramCorbaMessageMediator.getRequestHeader(), localLocateReplyMessage);
      paramCorbaMessageMediator.setOutputObject(localCDROutputObject);
      localCDROutputObject.setMessageMediator(paramCorbaMessageMediator);
      localLocateReplyMessage.write(localCDROutputObject);
      Object localObject = null;
      if (localObject != null) {
        ((IOR)localObject).write(localCDROutputObject);
      }
      if (s != -1) {
        AddressingDispositionHelper.write(localCDROutputObject, s);
      }
      return;
    }
  }
  
  public CorbaMessageMediator createResponse(CorbaMessageMediator paramCorbaMessageMediator, ServiceContexts paramServiceContexts)
  {
    return createResponseHelper(paramCorbaMessageMediator, getServiceContextsForReply(paramCorbaMessageMediator, null));
  }
  
  public CorbaMessageMediator createUserExceptionResponse(CorbaMessageMediator paramCorbaMessageMediator, ServiceContexts paramServiceContexts)
  {
    return createResponseHelper(paramCorbaMessageMediator, getServiceContextsForReply(paramCorbaMessageMediator, null), true);
  }
  
  public CorbaMessageMediator createUnknownExceptionResponse(CorbaMessageMediator paramCorbaMessageMediator, UnknownException paramUnknownException)
  {
    ServiceContexts localServiceContexts = null;
    UNKNOWN localUNKNOWN = new UNKNOWN(0, CompletionStatus.COMPLETED_MAYBE);
    localServiceContexts = new ServiceContexts((ORB)paramCorbaMessageMediator.getBroker());
    UEInfoServiceContext localUEInfoServiceContext = new UEInfoServiceContext(localUNKNOWN);
    localServiceContexts.put(localUEInfoServiceContext);
    return createSystemExceptionResponse(paramCorbaMessageMediator, localUNKNOWN, localServiceContexts);
  }
  
  public CorbaMessageMediator createSystemExceptionResponse(CorbaMessageMediator paramCorbaMessageMediator, SystemException paramSystemException, ServiceContexts paramServiceContexts)
  {
    if (paramCorbaMessageMediator.getConnection() != null)
    {
      localObject1 = (CorbaMessageMediatorImpl)((CorbaConnection)paramCorbaMessageMediator.getConnection()).serverRequestMapGet(paramCorbaMessageMediator.getRequestId());
      localObject2 = null;
      if (localObject1 != null) {
        localObject2 = ((CorbaMessageMediatorImpl)localObject1).getOutputObject();
      }
      if ((localObject2 != null) && (((CorbaMessageMediatorImpl)localObject1).sentFragment()) && (!((CorbaMessageMediatorImpl)localObject1).sentFullMessage())) {
        return (CorbaMessageMediator)localObject1;
      }
    }
    if (paramCorbaMessageMediator.executePIInResponseConstructor()) {
      ((ORB)paramCorbaMessageMediator.getBroker()).getPIHandler().setServerPIInfo(paramSystemException);
    }
    if ((getBrokersubcontractDebugFlag) && (paramSystemException != null)) {
      dprint(".createSystemExceptionResponse: " + opAndId(paramCorbaMessageMediator), paramSystemException);
    }
    Object localObject1 = getServiceContextsForReply(paramCorbaMessageMediator, paramServiceContexts);
    addExceptionDetailMessage(paramCorbaMessageMediator, paramSystemException, (ServiceContexts)localObject1);
    Object localObject2 = createResponseHelper(paramCorbaMessageMediator, (ServiceContexts)localObject1, false);
    ORBUtility.writeSystemException(paramSystemException, (org.omg.CORBA_2_3.portable.OutputStream)((CorbaMessageMediator)localObject2).getOutputObject());
    return (CorbaMessageMediator)localObject2;
  }
  
  private void addExceptionDetailMessage(CorbaMessageMediator paramCorbaMessageMediator, SystemException paramSystemException, ServiceContexts paramServiceContexts)
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    PrintWriter localPrintWriter = new PrintWriter(localByteArrayOutputStream);
    paramSystemException.printStackTrace(localPrintWriter);
    localPrintWriter.flush();
    EncapsOutputStream localEncapsOutputStream = OutputStreamFactory.newEncapsOutputStream((ORB)paramCorbaMessageMediator.getBroker());
    localEncapsOutputStream.putEndian();
    localEncapsOutputStream.write_wstring(localByteArrayOutputStream.toString());
    UnknownServiceContext localUnknownServiceContext = new UnknownServiceContext(14, localEncapsOutputStream.toByteArray());
    paramServiceContexts.put(localUnknownServiceContext);
  }
  
  public CorbaMessageMediator createLocationForward(CorbaMessageMediator paramCorbaMessageMediator, IOR paramIOR, ServiceContexts paramServiceContexts)
  {
    ReplyMessage localReplyMessage = MessageBase.createReply((ORB)paramCorbaMessageMediator.getBroker(), paramCorbaMessageMediator.getGIOPVersion(), paramCorbaMessageMediator.getEncodingVersion(), paramCorbaMessageMediator.getRequestId(), 3, getServiceContextsForReply(paramCorbaMessageMediator, paramServiceContexts), paramIOR);
    return createResponseHelper(paramCorbaMessageMediator, localReplyMessage, paramIOR);
  }
  
  protected CorbaMessageMediator createResponseHelper(CorbaMessageMediator paramCorbaMessageMediator, ServiceContexts paramServiceContexts)
  {
    ReplyMessage localReplyMessage = MessageBase.createReply((ORB)paramCorbaMessageMediator.getBroker(), paramCorbaMessageMediator.getGIOPVersion(), paramCorbaMessageMediator.getEncodingVersion(), paramCorbaMessageMediator.getRequestId(), 0, paramServiceContexts, null);
    return createResponseHelper(paramCorbaMessageMediator, localReplyMessage, null);
  }
  
  protected CorbaMessageMediator createResponseHelper(CorbaMessageMediator paramCorbaMessageMediator, ServiceContexts paramServiceContexts, boolean paramBoolean)
  {
    ReplyMessage localReplyMessage = MessageBase.createReply((ORB)paramCorbaMessageMediator.getBroker(), paramCorbaMessageMediator.getGIOPVersion(), paramCorbaMessageMediator.getEncodingVersion(), paramCorbaMessageMediator.getRequestId(), paramBoolean ? 1 : 2, paramServiceContexts, null);
    return createResponseHelper(paramCorbaMessageMediator, localReplyMessage, null);
  }
  
  protected CorbaMessageMediator createResponseHelper(CorbaMessageMediator paramCorbaMessageMediator, ReplyMessage paramReplyMessage, IOR paramIOR)
  {
    runServantPostInvoke(paramCorbaMessageMediator);
    runInterceptors(paramCorbaMessageMediator, paramReplyMessage);
    runRemoveThreadInfo(paramCorbaMessageMediator);
    if (getBrokersubcontractDebugFlag) {
      dprint(".createResponseHelper: " + opAndId(paramCorbaMessageMediator) + ": " + paramReplyMessage);
    }
    paramCorbaMessageMediator.setReplyHeader(paramReplyMessage);
    Object localObject;
    if (paramCorbaMessageMediator.getConnection() == null) {
      localObject = OutputStreamFactory.newCDROutputObject(orb, paramCorbaMessageMediator, paramCorbaMessageMediator.getReplyHeader(), paramCorbaMessageMediator.getStreamFormatVersion(), 0);
    } else {
      localObject = paramCorbaMessageMediator.getConnection().getAcceptor().createOutputObject(paramCorbaMessageMediator.getBroker(), paramCorbaMessageMediator);
    }
    paramCorbaMessageMediator.setOutputObject((OutputObject)localObject);
    paramCorbaMessageMediator.getOutputObject().setMessageMediator(paramCorbaMessageMediator);
    paramReplyMessage.write((org.omg.CORBA_2_3.portable.OutputStream)paramCorbaMessageMediator.getOutputObject());
    if (paramReplyMessage.getIOR() != null) {
      paramReplyMessage.getIOR().write((org.omg.CORBA_2_3.portable.OutputStream)paramCorbaMessageMediator.getOutputObject());
    }
    return paramCorbaMessageMediator;
  }
  
  protected void runServantPostInvoke(CorbaMessageMediator paramCorbaMessageMediator)
  {
    ORB localORB = null;
    if (paramCorbaMessageMediator.executeReturnServantInResponseConstructor())
    {
      paramCorbaMessageMediator.setExecuteReturnServantInResponseConstructor(false);
      paramCorbaMessageMediator.setExecuteRemoveThreadInfoInResponseConstructor(true);
      try
      {
        localORB = (ORB)paramCorbaMessageMediator.getBroker();
        OAInvocationInfo localOAInvocationInfo = localORB.peekInvocationInfo();
        ObjectAdapter localObjectAdapter = localOAInvocationInfo.oa();
        try
        {
          localObjectAdapter.returnServant();
        }
        catch (Throwable localThrowable)
        {
          wrapper.unexpectedException(localThrowable);
          if ((localThrowable instanceof Error)) {
            throw ((Error)localThrowable);
          }
          if ((localThrowable instanceof RuntimeException)) {
            throw ((RuntimeException)localThrowable);
          }
        }
        finally
        {
          localObjectAdapter.exit();
        }
      }
      catch (EmptyStackException localEmptyStackException)
      {
        throw wrapper.emptyStackRunServantPostInvoke(localEmptyStackException);
      }
    }
  }
  
  protected void runInterceptors(CorbaMessageMediator paramCorbaMessageMediator, ReplyMessage paramReplyMessage)
  {
    if (paramCorbaMessageMediator.executePIInResponseConstructor())
    {
      ((ORB)paramCorbaMessageMediator.getBroker()).getPIHandler().invokeServerPIEndingPoint(paramReplyMessage);
      ((ORB)paramCorbaMessageMediator.getBroker()).getPIHandler().cleanupServerPIRequest();
      paramCorbaMessageMediator.setExecutePIInResponseConstructor(false);
    }
  }
  
  protected void runRemoveThreadInfo(CorbaMessageMediator paramCorbaMessageMediator)
  {
    if (paramCorbaMessageMediator.executeRemoveThreadInfoInResponseConstructor())
    {
      paramCorbaMessageMediator.setExecuteRemoveThreadInfoInResponseConstructor(false);
      ((ORB)paramCorbaMessageMediator.getBroker()).popInvocationInfo();
    }
  }
  
  protected ServiceContexts getServiceContextsForReply(CorbaMessageMediator paramCorbaMessageMediator, ServiceContexts paramServiceContexts)
  {
    CorbaConnection localCorbaConnection = (CorbaConnection)paramCorbaMessageMediator.getConnection();
    if (getBrokersubcontractDebugFlag) {
      dprint(".getServiceContextsForReply: " + opAndId(paramCorbaMessageMediator) + ": " + localCorbaConnection);
    }
    if (paramServiceContexts == null) {
      paramServiceContexts = new ServiceContexts((ORB)paramCorbaMessageMediator.getBroker());
    }
    if ((localCorbaConnection != null) && (!localCorbaConnection.isPostInitialContexts()))
    {
      localCorbaConnection.setPostInitialContexts();
      localObject = new SendingContextServiceContext(((ORB)paramCorbaMessageMediator.getBroker()).getFVDCodeBaseIOR());
      if (paramServiceContexts.get(((SendingContextServiceContext)localObject).getId()) != null) {
        throw wrapper.duplicateSendingContextServiceContext();
      }
      paramServiceContexts.put((ServiceContext)localObject);
      if (getBrokersubcontractDebugFlag) {
        dprint(".getServiceContextsForReply: " + opAndId(paramCorbaMessageMediator) + ": added SendingContextServiceContext");
      }
    }
    Object localObject = new ORBVersionServiceContext(ORBVersionFactory.getORBVersion());
    if (paramServiceContexts.get(((ORBVersionServiceContext)localObject).getId()) != null) {
      throw wrapper.duplicateOrbVersionServiceContext();
    }
    paramServiceContexts.put((ServiceContext)localObject);
    if (getBrokersubcontractDebugFlag) {
      dprint(".getServiceContextsForReply: " + opAndId(paramCorbaMessageMediator) + ": added ORB version service context");
    }
    return paramServiceContexts;
  }
  
  private void releaseByteBufferToPool()
  {
    if (dispatchByteBuffer != null)
    {
      orb.getByteBufferPool().releaseByteBuffer(dispatchByteBuffer);
      if (transportDebug())
      {
        int i = System.identityHashCode(dispatchByteBuffer);
        StringBuffer localStringBuffer = new StringBuffer();
        localStringBuffer.append(".handleInput: releasing ByteBuffer (" + i + ") to ByteBufferPool");
        dprint(localStringBuffer.toString());
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\protocol\CorbaMessageMediatorImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */