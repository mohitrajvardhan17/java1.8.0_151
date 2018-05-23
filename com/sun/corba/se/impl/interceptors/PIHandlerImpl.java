package com.sun.corba.se.impl.interceptors;

import com.sun.corba.se.impl.corba.RequestImpl;
import com.sun.corba.se.impl.logging.InterceptorsSystemException;
import com.sun.corba.se.impl.logging.OMGSystemException;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.protocol.giopmsgheaders.ReplyMessage;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.ObjectKeyTemplate;
import com.sun.corba.se.spi.oa.ObjectAdapter;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.ORBData;
import com.sun.corba.se.spi.orbutil.closure.ClosureFactory;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import com.sun.corba.se.spi.protocol.ForwardException;
import com.sun.corba.se.spi.protocol.PIHandler;
import com.sun.corba.se.spi.protocol.RetryType;
import com.sun.corba.se.spi.resolver.LocalResolver;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Stack;
import org.omg.CORBA.Any;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.NVList;
import org.omg.CORBA.Policy;
import org.omg.CORBA.PolicyError;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.UserException;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA.portable.RemarshalException;
import org.omg.IOP.CodecFactory;
import org.omg.PortableInterceptor.Current;
import org.omg.PortableInterceptor.Interceptor;
import org.omg.PortableInterceptor.ORBInitInfoPackage.DuplicateName;
import org.omg.PortableInterceptor.ORBInitializer;
import org.omg.PortableInterceptor.ObjectReferenceTemplate;
import org.omg.PortableInterceptor.PolicyFactory;

public class PIHandlerImpl
  implements PIHandler
{
  boolean printPushPopEnabled = false;
  int pushLevel = 0;
  private ORB orb;
  InterceptorsSystemException wrapper;
  ORBUtilSystemException orbutilWrapper;
  OMGSystemException omgWrapper;
  private int serverRequestIdCounter = 0;
  CodecFactory codecFactory = null;
  String[] arguments = null;
  private InterceptorList interceptorList;
  private boolean hasIORInterceptors;
  private boolean hasClientInterceptors;
  private boolean hasServerInterceptors;
  private InterceptorInvoker interceptorInvoker;
  private PICurrent current;
  private HashMap policyFactoryTable;
  private static final short[] REPLY_MESSAGE_TO_PI_REPLY_STATUS = { 0, 2, 1, 3, 3, 4 };
  private ThreadLocal threadLocalClientRequestInfoStack = new ThreadLocal()
  {
    protected Object initialValue()
    {
      return new PIHandlerImpl.RequestInfoStack(PIHandlerImpl.this, null);
    }
  };
  private ThreadLocal threadLocalServerRequestInfoStack = new ThreadLocal()
  {
    protected Object initialValue()
    {
      return new PIHandlerImpl.RequestInfoStack(PIHandlerImpl.this, null);
    }
  };
  
  private void printPush()
  {
    if (!printPushPopEnabled) {
      return;
    }
    printSpaces(pushLevel);
    pushLevel += 1;
    System.out.println("PUSH");
  }
  
  private void printPop()
  {
    if (!printPushPopEnabled) {
      return;
    }
    pushLevel -= 1;
    printSpaces(pushLevel);
    System.out.println("POP");
  }
  
  private void printSpaces(int paramInt)
  {
    for (int i = 0; i < paramInt; i++) {
      System.out.print(" ");
    }
  }
  
  public void close()
  {
    orb = null;
    wrapper = null;
    orbutilWrapper = null;
    omgWrapper = null;
    codecFactory = null;
    arguments = null;
    interceptorList = null;
    interceptorInvoker = null;
    current = null;
    policyFactoryTable = null;
    threadLocalClientRequestInfoStack = null;
    threadLocalServerRequestInfoStack = null;
  }
  
  public PIHandlerImpl(ORB paramORB, String[] paramArrayOfString)
  {
    orb = paramORB;
    wrapper = InterceptorsSystemException.get(paramORB, "rpc.protocol");
    orbutilWrapper = ORBUtilSystemException.get(paramORB, "rpc.protocol");
    omgWrapper = OMGSystemException.get(paramORB, "rpc.protocol");
    arguments = paramArrayOfString;
    codecFactory = new CodecFactoryImpl(paramORB);
    interceptorList = new InterceptorList(wrapper);
    current = new PICurrent(paramORB);
    interceptorInvoker = new InterceptorInvoker(paramORB, interceptorList, current);
    paramORB.getLocalResolver().register("PICurrent", ClosureFactory.makeConstant(current));
    paramORB.getLocalResolver().register("CodecFactory", ClosureFactory.makeConstant(codecFactory));
  }
  
  public void initialize()
  {
    if (orb.getORBData().getORBInitializers() != null)
    {
      ORBInitInfoImpl localORBInitInfoImpl = createORBInitInfo();
      current.setORBInitializing(true);
      preInitORBInitializers(localORBInitInfoImpl);
      postInitORBInitializers(localORBInitInfoImpl);
      interceptorList.sortInterceptors();
      current.setORBInitializing(false);
      localORBInitInfoImpl.setStage(2);
      hasIORInterceptors = interceptorList.hasInterceptorsOfType(2);
      hasClientInterceptors = true;
      hasServerInterceptors = interceptorList.hasInterceptorsOfType(1);
      interceptorInvoker.setEnabled(true);
    }
  }
  
  public void destroyInterceptors()
  {
    interceptorList.destroyAll();
  }
  
  public void objectAdapterCreated(ObjectAdapter paramObjectAdapter)
  {
    if (!hasIORInterceptors) {
      return;
    }
    interceptorInvoker.objectAdapterCreated(paramObjectAdapter);
  }
  
  public void adapterManagerStateChanged(int paramInt, short paramShort)
  {
    if (!hasIORInterceptors) {
      return;
    }
    interceptorInvoker.adapterManagerStateChanged(paramInt, paramShort);
  }
  
  public void adapterStateChanged(ObjectReferenceTemplate[] paramArrayOfObjectReferenceTemplate, short paramShort)
  {
    if (!hasIORInterceptors) {
      return;
    }
    interceptorInvoker.adapterStateChanged(paramArrayOfObjectReferenceTemplate, paramShort);
  }
  
  public void disableInterceptorsThisThread()
  {
    if (!hasClientInterceptors) {
      return;
    }
    RequestInfoStack localRequestInfoStack = (RequestInfoStack)threadLocalClientRequestInfoStack.get();
    disableCount += 1;
  }
  
  public void enableInterceptorsThisThread()
  {
    if (!hasClientInterceptors) {
      return;
    }
    RequestInfoStack localRequestInfoStack = (RequestInfoStack)threadLocalClientRequestInfoStack.get();
    disableCount -= 1;
  }
  
  public void invokeClientPIStartingPoint()
    throws RemarshalException
  {
    if (!hasClientInterceptors) {
      return;
    }
    if (!isClientPIEnabledForThisThread()) {
      return;
    }
    ClientRequestInfoImpl localClientRequestInfoImpl = peekClientRequestInfoImplStack();
    interceptorInvoker.invokeClientInterceptorStartingPoint(localClientRequestInfoImpl);
    short s = localClientRequestInfoImpl.getReplyStatus();
    if ((s == 1) || (s == 3))
    {
      Exception localException = invokeClientPIEndingPoint(convertPIReplyStatusToReplyMessage(s), localClientRequestInfoImpl.getException());
      if ((localException != null) || ((localException instanceof SystemException))) {
        throw ((SystemException)localException);
      }
      if ((localException instanceof RemarshalException)) {
        throw ((RemarshalException)localException);
      }
      if (((localException instanceof UserException)) || ((localException instanceof ApplicationException))) {
        throw wrapper.exceptionInvalid();
      }
    }
    else if (s != -1)
    {
      throw wrapper.replyStatusNotInit();
    }
  }
  
  public Exception makeCompletedClientRequest(int paramInt, Exception paramException)
  {
    return handleClientPIEndingPoint(paramInt, paramException, false);
  }
  
  public Exception invokeClientPIEndingPoint(int paramInt, Exception paramException)
  {
    return handleClientPIEndingPoint(paramInt, paramException, true);
  }
  
  public Exception handleClientPIEndingPoint(int paramInt, Exception paramException, boolean paramBoolean)
  {
    if (!hasClientInterceptors) {
      return paramException;
    }
    if (!isClientPIEnabledForThisThread()) {
      return paramException;
    }
    short s = REPLY_MESSAGE_TO_PI_REPLY_STATUS[paramInt];
    ClientRequestInfoImpl localClientRequestInfoImpl = peekClientRequestInfoImplStack();
    localClientRequestInfoImpl.setReplyStatus(s);
    localClientRequestInfoImpl.setException(paramException);
    if (paramBoolean)
    {
      interceptorInvoker.invokeClientInterceptorEndingPoint(localClientRequestInfoImpl);
      s = localClientRequestInfoImpl.getReplyStatus();
    }
    if ((s == 3) || (s == 4))
    {
      localClientRequestInfoImpl.reset();
      if (paramBoolean) {
        localClientRequestInfoImpl.setRetryRequest(RetryType.AFTER_RESPONSE);
      } else {
        localClientRequestInfoImpl.setRetryRequest(RetryType.BEFORE_RESPONSE);
      }
      paramException = new RemarshalException();
    }
    else if ((s == 1) || (s == 2))
    {
      paramException = localClientRequestInfoImpl.getException();
    }
    return paramException;
  }
  
  public void initiateClientPIRequest(boolean paramBoolean)
  {
    if (!hasClientInterceptors) {
      return;
    }
    if (!isClientPIEnabledForThisThread()) {
      return;
    }
    RequestInfoStack localRequestInfoStack = (RequestInfoStack)threadLocalClientRequestInfoStack.get();
    ClientRequestInfoImpl localClientRequestInfoImpl = null;
    if (!localRequestInfoStack.empty()) {
      localClientRequestInfoImpl = (ClientRequestInfoImpl)localRequestInfoStack.peek();
    }
    if ((!paramBoolean) && (localClientRequestInfoImpl != null) && (localClientRequestInfoImpl.isDIIInitiate()))
    {
      localClientRequestInfoImpl.setDIIInitiate(false);
    }
    else
    {
      if ((localClientRequestInfoImpl == null) || (!localClientRequestInfoImpl.getRetryRequest().isRetry()))
      {
        localClientRequestInfoImpl = new ClientRequestInfoImpl(orb);
        localRequestInfoStack.push(localClientRequestInfoImpl);
        printPush();
      }
      localClientRequestInfoImpl.setRetryRequest(RetryType.NONE);
      localClientRequestInfoImpl.incrementEntryCount();
      localClientRequestInfoImpl.setReplyStatus((short)-1);
      if (paramBoolean) {
        localClientRequestInfoImpl.setDIIInitiate(true);
      }
    }
  }
  
  public void cleanupClientPIRequest()
  {
    if (!hasClientInterceptors) {
      return;
    }
    if (!isClientPIEnabledForThisThread()) {
      return;
    }
    ClientRequestInfoImpl localClientRequestInfoImpl = peekClientRequestInfoImplStack();
    RetryType localRetryType = localClientRequestInfoImpl.getRetryRequest();
    if (!localRetryType.equals(RetryType.BEFORE_RESPONSE))
    {
      int i = localClientRequestInfoImpl.getReplyStatus();
      if (i == -1) {
        invokeClientPIEndingPoint(2, wrapper.unknownRequestInvoke(CompletionStatus.COMPLETED_MAYBE));
      }
    }
    localClientRequestInfoImpl.decrementEntryCount();
    if ((localClientRequestInfoImpl.getEntryCount() == 0) && (!localClientRequestInfoImpl.getRetryRequest().isRetry()))
    {
      RequestInfoStack localRequestInfoStack = (RequestInfoStack)threadLocalClientRequestInfoStack.get();
      localRequestInfoStack.pop();
      printPop();
    }
  }
  
  public void setClientPIInfo(CorbaMessageMediator paramCorbaMessageMediator)
  {
    if (!hasClientInterceptors) {
      return;
    }
    if (!isClientPIEnabledForThisThread()) {
      return;
    }
    peekClientRequestInfoImplStack().setInfo(paramCorbaMessageMediator);
  }
  
  public void setClientPIInfo(RequestImpl paramRequestImpl)
  {
    if (!hasClientInterceptors) {
      return;
    }
    if (!isClientPIEnabledForThisThread()) {
      return;
    }
    peekClientRequestInfoImplStack().setDIIRequest(paramRequestImpl);
  }
  
  public void invokeServerPIStartingPoint()
  {
    if (!hasServerInterceptors) {
      return;
    }
    ServerRequestInfoImpl localServerRequestInfoImpl = peekServerRequestInfoImplStack();
    interceptorInvoker.invokeServerInterceptorStartingPoint(localServerRequestInfoImpl);
    serverPIHandleExceptions(localServerRequestInfoImpl);
  }
  
  public void invokeServerPIIntermediatePoint()
  {
    if (!hasServerInterceptors) {
      return;
    }
    ServerRequestInfoImpl localServerRequestInfoImpl = peekServerRequestInfoImplStack();
    interceptorInvoker.invokeServerInterceptorIntermediatePoint(localServerRequestInfoImpl);
    localServerRequestInfoImpl.releaseServant();
    serverPIHandleExceptions(localServerRequestInfoImpl);
  }
  
  public void invokeServerPIEndingPoint(ReplyMessage paramReplyMessage)
  {
    if (!hasServerInterceptors) {
      return;
    }
    ServerRequestInfoImpl localServerRequestInfoImpl = peekServerRequestInfoImplStack();
    localServerRequestInfoImpl.setReplyMessage(paramReplyMessage);
    localServerRequestInfoImpl.setCurrentExecutionPoint(2);
    if (!localServerRequestInfoImpl.getAlreadyExecuted())
    {
      int i = paramReplyMessage.getReplyStatus();
      short s = REPLY_MESSAGE_TO_PI_REPLY_STATUS[i];
      if ((s == 3) || (s == 4)) {
        localServerRequestInfoImpl.setForwardRequest(paramReplyMessage.getIOR());
      }
      Exception localException1 = localServerRequestInfoImpl.getException();
      if ((!localServerRequestInfoImpl.isDynamic()) && (s == 2)) {
        localServerRequestInfoImpl.setException(omgWrapper.unknownUserException(CompletionStatus.COMPLETED_MAYBE));
      }
      localServerRequestInfoImpl.setReplyStatus(s);
      interceptorInvoker.invokeServerInterceptorEndingPoint(localServerRequestInfoImpl);
      int j = localServerRequestInfoImpl.getReplyStatus();
      Exception localException2 = localServerRequestInfoImpl.getException();
      if ((j == 1) && (localException2 != localException1)) {
        throw ((SystemException)localException2);
      }
      if (j == 3)
      {
        if (s != 3)
        {
          IOR localIOR = localServerRequestInfoImpl.getForwardRequestIOR();
          throw new ForwardException(orb, localIOR);
        }
        if (localServerRequestInfoImpl.isForwardRequestRaisedInEnding()) {
          paramReplyMessage.setIOR(localServerRequestInfoImpl.getForwardRequestIOR());
        }
      }
    }
  }
  
  public void setServerPIInfo(Exception paramException)
  {
    if (!hasServerInterceptors) {
      return;
    }
    ServerRequestInfoImpl localServerRequestInfoImpl = peekServerRequestInfoImplStack();
    localServerRequestInfoImpl.setException(paramException);
  }
  
  public void setServerPIInfo(NVList paramNVList)
  {
    if (!hasServerInterceptors) {
      return;
    }
    ServerRequestInfoImpl localServerRequestInfoImpl = peekServerRequestInfoImplStack();
    localServerRequestInfoImpl.setDSIArguments(paramNVList);
  }
  
  public void setServerPIExceptionInfo(Any paramAny)
  {
    if (!hasServerInterceptors) {
      return;
    }
    ServerRequestInfoImpl localServerRequestInfoImpl = peekServerRequestInfoImplStack();
    localServerRequestInfoImpl.setDSIException(paramAny);
  }
  
  public void setServerPIInfo(Any paramAny)
  {
    if (!hasServerInterceptors) {
      return;
    }
    ServerRequestInfoImpl localServerRequestInfoImpl = peekServerRequestInfoImplStack();
    localServerRequestInfoImpl.setDSIResult(paramAny);
  }
  
  public void initializeServerPIInfo(CorbaMessageMediator paramCorbaMessageMediator, ObjectAdapter paramObjectAdapter, byte[] paramArrayOfByte, ObjectKeyTemplate paramObjectKeyTemplate)
  {
    if (!hasServerInterceptors) {
      return;
    }
    RequestInfoStack localRequestInfoStack = (RequestInfoStack)threadLocalServerRequestInfoStack.get();
    ServerRequestInfoImpl localServerRequestInfoImpl = new ServerRequestInfoImpl(orb);
    localRequestInfoStack.push(localServerRequestInfoImpl);
    printPush();
    paramCorbaMessageMediator.setExecutePIInResponseConstructor(true);
    localServerRequestInfoImpl.setInfo(paramCorbaMessageMediator, paramObjectAdapter, paramArrayOfByte, paramObjectKeyTemplate);
  }
  
  public void setServerPIInfo(Object paramObject, String paramString)
  {
    if (!hasServerInterceptors) {
      return;
    }
    ServerRequestInfoImpl localServerRequestInfoImpl = peekServerRequestInfoImplStack();
    localServerRequestInfoImpl.setInfo(paramObject, paramString);
  }
  
  public void cleanupServerPIRequest()
  {
    if (!hasServerInterceptors) {
      return;
    }
    RequestInfoStack localRequestInfoStack = (RequestInfoStack)threadLocalServerRequestInfoStack.get();
    localRequestInfoStack.pop();
    printPop();
  }
  
  private void serverPIHandleExceptions(ServerRequestInfoImpl paramServerRequestInfoImpl)
  {
    int i = paramServerRequestInfoImpl.getEndingPointCall();
    if (i == 1) {
      throw ((SystemException)paramServerRequestInfoImpl.getException());
    }
    if ((i == 2) && (paramServerRequestInfoImpl.getForwardRequestException() != null))
    {
      IOR localIOR = paramServerRequestInfoImpl.getForwardRequestIOR();
      throw new ForwardException(orb, localIOR);
    }
  }
  
  private int convertPIReplyStatusToReplyMessage(short paramShort)
  {
    int i = 0;
    for (int j = 0; j < REPLY_MESSAGE_TO_PI_REPLY_STATUS.length; j++) {
      if (REPLY_MESSAGE_TO_PI_REPLY_STATUS[j] == paramShort)
      {
        i = j;
        break;
      }
    }
    return i;
  }
  
  private ClientRequestInfoImpl peekClientRequestInfoImplStack()
  {
    RequestInfoStack localRequestInfoStack = (RequestInfoStack)threadLocalClientRequestInfoStack.get();
    ClientRequestInfoImpl localClientRequestInfoImpl = null;
    if (!localRequestInfoStack.empty()) {
      localClientRequestInfoImpl = (ClientRequestInfoImpl)localRequestInfoStack.peek();
    } else {
      throw wrapper.clientInfoStackNull();
    }
    return localClientRequestInfoImpl;
  }
  
  private ServerRequestInfoImpl peekServerRequestInfoImplStack()
  {
    RequestInfoStack localRequestInfoStack = (RequestInfoStack)threadLocalServerRequestInfoStack.get();
    ServerRequestInfoImpl localServerRequestInfoImpl = null;
    if (!localRequestInfoStack.empty()) {
      localServerRequestInfoImpl = (ServerRequestInfoImpl)localRequestInfoStack.peek();
    } else {
      throw wrapper.serverInfoStackNull();
    }
    return localServerRequestInfoImpl;
  }
  
  private boolean isClientPIEnabledForThisThread()
  {
    RequestInfoStack localRequestInfoStack = (RequestInfoStack)threadLocalClientRequestInfoStack.get();
    return disableCount == 0;
  }
  
  private void preInitORBInitializers(ORBInitInfoImpl paramORBInitInfoImpl)
  {
    paramORBInitInfoImpl.setStage(0);
    for (int i = 0; i < orb.getORBData().getORBInitializers().length; i++)
    {
      ORBInitializer localORBInitializer = orb.getORBData().getORBInitializers()[i];
      if (localORBInitializer != null) {
        try
        {
          localORBInitializer.pre_init(paramORBInitInfoImpl);
        }
        catch (Exception localException) {}
      }
    }
  }
  
  private void postInitORBInitializers(ORBInitInfoImpl paramORBInitInfoImpl)
  {
    paramORBInitInfoImpl.setStage(1);
    for (int i = 0; i < orb.getORBData().getORBInitializers().length; i++)
    {
      ORBInitializer localORBInitializer = orb.getORBData().getORBInitializers()[i];
      if (localORBInitializer != null) {
        try
        {
          localORBInitializer.post_init(paramORBInitInfoImpl);
        }
        catch (Exception localException) {}
      }
    }
  }
  
  private ORBInitInfoImpl createORBInitInfo()
  {
    ORBInitInfoImpl localORBInitInfoImpl = null;
    String str = orb.getORBData().getORBId();
    localORBInitInfoImpl = new ORBInitInfoImpl(orb, arguments, str, codecFactory);
    return localORBInitInfoImpl;
  }
  
  public void register_interceptor(Interceptor paramInterceptor, int paramInt)
    throws DuplicateName
  {
    if ((paramInt >= 3) || (paramInt < 0)) {
      throw wrapper.typeOutOfRange(new Integer(paramInt));
    }
    String str = paramInterceptor.name();
    if (str == null) {
      throw wrapper.nameNull();
    }
    interceptorList.register_interceptor(paramInterceptor, paramInt);
  }
  
  public Current getPICurrent()
  {
    return current;
  }
  
  private void nullParam()
    throws BAD_PARAM
  {
    throw orbutilWrapper.nullParam();
  }
  
  public Policy create_policy(int paramInt, Any paramAny)
    throws PolicyError
  {
    if (paramAny == null) {
      nullParam();
    }
    if (policyFactoryTable == null) {
      throw new PolicyError("There is no PolicyFactory Registered for type " + paramInt, (short)0);
    }
    PolicyFactory localPolicyFactory = (PolicyFactory)policyFactoryTable.get(new Integer(paramInt));
    if (localPolicyFactory == null) {
      throw new PolicyError(" Could Not Find PolicyFactory for the Type " + paramInt, (short)0);
    }
    Policy localPolicy = localPolicyFactory.create_policy(paramInt, paramAny);
    return localPolicy;
  }
  
  public void registerPolicyFactory(int paramInt, PolicyFactory paramPolicyFactory)
  {
    if (policyFactoryTable == null) {
      policyFactoryTable = new HashMap();
    }
    Integer localInteger = new Integer(paramInt);
    Object localObject = policyFactoryTable.get(localInteger);
    if (localObject == null) {
      policyFactoryTable.put(localInteger, paramPolicyFactory);
    } else {
      throw omgWrapper.policyFactoryRegFailed(new Integer(paramInt));
    }
  }
  
  public synchronized int allocateServerRequestId()
  {
    return serverRequestIdCounter++;
  }
  
  private final class RequestInfoStack
    extends Stack
  {
    public int disableCount = 0;
    
    private RequestInfoStack() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\interceptors\PIHandlerImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */