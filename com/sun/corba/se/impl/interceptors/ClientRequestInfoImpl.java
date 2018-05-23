package com.sun.corba.se.impl.interceptors;

import com.sun.corba.se.impl.logging.InterceptorsSystemException;
import com.sun.corba.se.impl.logging.OMGSystemException;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.impl.protocol.CorbaInvocationInfo;
import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.corba.se.pept.transport.ContactInfo;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.iiop.IIOPProfile;
import com.sun.corba.se.spi.ior.iiop.IIOPProfileTemplate;
import com.sun.corba.se.spi.legacy.connection.Connection;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import com.sun.corba.se.spi.protocol.RetryType;
import com.sun.corba.se.spi.servicecontext.ServiceContexts;
import com.sun.corba.se.spi.transport.CorbaContactInfo;
import com.sun.corba.se.spi.transport.CorbaContactInfoList;
import com.sun.corba.se.spi.transport.CorbaContactInfoListIterator;
import java.util.HashMap;
import org.omg.CORBA.Any;
import org.omg.CORBA.BAD_INV_ORDER;
import org.omg.CORBA.Context;
import org.omg.CORBA.ContextList;
import org.omg.CORBA.ExceptionList;
import org.omg.CORBA.NVList;
import org.omg.CORBA.NamedValue;
import org.omg.CORBA.Policy;
import org.omg.CORBA.Request;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.Dynamic.Parameter;
import org.omg.IOP.ServiceContext;
import org.omg.IOP.TaggedComponent;
import org.omg.IOP.TaggedProfile;
import org.omg.PortableInterceptor.ClientRequestInfo;

public final class ClientRequestInfoImpl
  extends RequestInfoImpl
  implements ClientRequestInfo
{
  static final int CALL_SEND_REQUEST = 0;
  static final int CALL_SEND_POLL = 1;
  static final int CALL_RECEIVE_REPLY = 0;
  static final int CALL_RECEIVE_EXCEPTION = 1;
  static final int CALL_RECEIVE_OTHER = 2;
  private RetryType retryRequest;
  private int entryCount = 0;
  private Request request;
  private boolean diiInitiate;
  private CorbaMessageMediator messageMediator;
  private org.omg.CORBA.Object cachedTargetObject;
  private org.omg.CORBA.Object cachedEffectiveTargetObject;
  private Parameter[] cachedArguments;
  private TypeCode[] cachedExceptions;
  private String[] cachedContexts;
  private String[] cachedOperationContext;
  private String cachedReceivedExceptionId;
  private Any cachedResult;
  private Any cachedReceivedException;
  private TaggedProfile cachedEffectiveProfile;
  private HashMap cachedRequestServiceContexts;
  private HashMap cachedReplyServiceContexts;
  private HashMap cachedEffectiveComponents;
  protected boolean piCurrentPushed;
  protected static final int MID_TARGET = 14;
  protected static final int MID_EFFECTIVE_TARGET = 15;
  protected static final int MID_EFFECTIVE_PROFILE = 16;
  protected static final int MID_RECEIVED_EXCEPTION = 17;
  protected static final int MID_RECEIVED_EXCEPTION_ID = 18;
  protected static final int MID_GET_EFFECTIVE_COMPONENT = 19;
  protected static final int MID_GET_EFFECTIVE_COMPONENTS = 20;
  protected static final int MID_GET_REQUEST_POLICY = 21;
  protected static final int MID_ADD_REQUEST_SERVICE_CONTEXT = 22;
  private static final boolean[][] validCall = { { true, true, true, true, true }, { true, true, true, true, true }, { true, false, true, false, false }, { true, false, true, true, true }, { true, false, true, true, true }, { true, false, true, true, true }, { false, false, true, false, false }, { true, true, true, true, true }, { true, false, true, true, true }, { false, false, true, true, true }, { false, false, false, false, true }, { true, true, true, true, true }, { true, false, true, true, true }, { false, false, true, true, true }, { true, true, true, true, true }, { true, true, true, true, true }, { true, true, true, true, true }, { false, false, false, true, false }, { false, false, false, true, false }, { true, false, true, true, true }, { true, false, true, true, true }, { true, false, true, true, true }, { true, false, false, false, false } };
  
  void reset()
  {
    super.reset();
    retryRequest = RetryType.NONE;
    request = null;
    diiInitiate = false;
    messageMediator = null;
    cachedTargetObject = null;
    cachedEffectiveTargetObject = null;
    cachedArguments = null;
    cachedExceptions = null;
    cachedContexts = null;
    cachedOperationContext = null;
    cachedReceivedExceptionId = null;
    cachedResult = null;
    cachedReceivedException = null;
    cachedEffectiveProfile = null;
    cachedRequestServiceContexts = null;
    cachedReplyServiceContexts = null;
    cachedEffectiveComponents = null;
    piCurrentPushed = false;
    startingPointCall = 0;
    endingPointCall = 0;
  }
  
  protected ClientRequestInfoImpl(ORB paramORB)
  {
    super(paramORB);
    startingPointCall = 0;
    endingPointCall = 0;
  }
  
  public org.omg.CORBA.Object target()
  {
    if (cachedTargetObject == null)
    {
      CorbaContactInfo localCorbaContactInfo = (CorbaContactInfo)messageMediator.getContactInfo();
      cachedTargetObject = iorToObject(localCorbaContactInfo.getTargetIOR());
    }
    return cachedTargetObject;
  }
  
  public org.omg.CORBA.Object effective_target()
  {
    if (cachedEffectiveTargetObject == null)
    {
      CorbaContactInfo localCorbaContactInfo = (CorbaContactInfo)messageMediator.getContactInfo();
      cachedEffectiveTargetObject = iorToObject(localCorbaContactInfo.getEffectiveTargetIOR());
    }
    return cachedEffectiveTargetObject;
  }
  
  public TaggedProfile effective_profile()
  {
    if (cachedEffectiveProfile == null)
    {
      CorbaContactInfo localCorbaContactInfo = (CorbaContactInfo)messageMediator.getContactInfo();
      cachedEffectiveProfile = localCorbaContactInfo.getEffectiveProfile().getIOPProfile();
    }
    return cachedEffectiveProfile;
  }
  
  public Any received_exception()
  {
    checkAccess(17);
    if (cachedReceivedException == null) {
      cachedReceivedException = exceptionToAny(exception);
    }
    return cachedReceivedException;
  }
  
  public String received_exception_id()
  {
    checkAccess(18);
    if (cachedReceivedExceptionId == null)
    {
      String str1 = null;
      if (exception == null) {
        throw wrapper.exceptionWasNull();
      }
      if ((exception instanceof SystemException))
      {
        String str2 = exception.getClass().getName();
        str1 = ORBUtility.repositoryIdOf(str2);
      }
      else if ((exception instanceof ApplicationException))
      {
        str1 = ((ApplicationException)exception).getId();
      }
      cachedReceivedExceptionId = str1;
    }
    return cachedReceivedExceptionId;
  }
  
  public TaggedComponent get_effective_component(int paramInt)
  {
    checkAccess(19);
    return get_effective_components(paramInt)[0];
  }
  
  public TaggedComponent[] get_effective_components(int paramInt)
  {
    checkAccess(20);
    Integer localInteger = new Integer(paramInt);
    TaggedComponent[] arrayOfTaggedComponent = null;
    int i = 0;
    if (cachedEffectiveComponents == null)
    {
      cachedEffectiveComponents = new HashMap();
      i = 1;
    }
    else
    {
      arrayOfTaggedComponent = (TaggedComponent[])cachedEffectiveComponents.get(localInteger);
    }
    if ((arrayOfTaggedComponent == null) && ((i != 0) || (!cachedEffectiveComponents.containsKey(localInteger))))
    {
      CorbaContactInfo localCorbaContactInfo = (CorbaContactInfo)messageMediator.getContactInfo();
      IIOPProfileTemplate localIIOPProfileTemplate = (IIOPProfileTemplate)localCorbaContactInfo.getEffectiveProfile().getTaggedProfileTemplate();
      arrayOfTaggedComponent = localIIOPProfileTemplate.getIOPComponents(myORB, paramInt);
      cachedEffectiveComponents.put(localInteger, arrayOfTaggedComponent);
    }
    if ((arrayOfTaggedComponent == null) || (arrayOfTaggedComponent.length == 0)) {
      throw stdWrapper.invalidComponentId(localInteger);
    }
    return arrayOfTaggedComponent;
  }
  
  public Policy get_request_policy(int paramInt)
  {
    checkAccess(21);
    throw wrapper.piOrbNotPolicyBased();
  }
  
  public void add_request_service_context(ServiceContext paramServiceContext, boolean paramBoolean)
  {
    checkAccess(22);
    if (cachedRequestServiceContexts == null) {
      cachedRequestServiceContexts = new HashMap();
    }
    addServiceContext(cachedRequestServiceContexts, messageMediator.getRequestServiceContexts(), paramServiceContext, paramBoolean);
  }
  
  public int request_id()
  {
    return messageMediator.getRequestId();
  }
  
  public String operation()
  {
    return messageMediator.getOperationName();
  }
  
  public Parameter[] arguments()
  {
    checkAccess(2);
    if (cachedArguments == null)
    {
      if (request == null) {
        throw stdWrapper.piOperationNotSupported1();
      }
      cachedArguments = nvListToParameterArray(request.arguments());
    }
    return cachedArguments;
  }
  
  public TypeCode[] exceptions()
  {
    checkAccess(3);
    if (cachedExceptions == null)
    {
      if (request == null) {
        throw stdWrapper.piOperationNotSupported2();
      }
      ExceptionList localExceptionList = request.exceptions();
      int i = localExceptionList.count();
      TypeCode[] arrayOfTypeCode = new TypeCode[i];
      try
      {
        for (int j = 0; j < i; j++) {
          arrayOfTypeCode[j] = localExceptionList.item(j);
        }
      }
      catch (Exception localException)
      {
        throw wrapper.exceptionInExceptions(localException);
      }
      cachedExceptions = arrayOfTypeCode;
    }
    return cachedExceptions;
  }
  
  public String[] contexts()
  {
    checkAccess(4);
    if (cachedContexts == null)
    {
      if (request == null) {
        throw stdWrapper.piOperationNotSupported3();
      }
      ContextList localContextList = request.contexts();
      int i = localContextList.count();
      String[] arrayOfString = new String[i];
      try
      {
        for (int j = 0; j < i; j++) {
          arrayOfString[j] = localContextList.item(j);
        }
      }
      catch (Exception localException)
      {
        throw wrapper.exceptionInContexts(localException);
      }
      cachedContexts = arrayOfString;
    }
    return cachedContexts;
  }
  
  public String[] operation_context()
  {
    checkAccess(5);
    if (cachedOperationContext == null)
    {
      if (request == null) {
        throw stdWrapper.piOperationNotSupported4();
      }
      Context localContext = request.ctx();
      NVList localNVList = localContext.get_values("", 15, "*");
      String[] arrayOfString = new String[localNVList.count() * 2];
      if ((localNVList != null) && (localNVList.count() != 0))
      {
        int i = 0;
        for (int j = 0; j < localNVList.count(); j++)
        {
          NamedValue localNamedValue;
          try
          {
            localNamedValue = localNVList.item(j);
          }
          catch (Exception localException)
          {
            return (String[])null;
          }
          arrayOfString[i] = localNamedValue.name();
          i++;
          arrayOfString[i] = localNamedValue.value().extract_string();
          i++;
        }
      }
      cachedOperationContext = arrayOfString;
    }
    return cachedOperationContext;
  }
  
  public Any result()
  {
    checkAccess(6);
    if (cachedResult == null)
    {
      if (request == null) {
        throw stdWrapper.piOperationNotSupported5();
      }
      NamedValue localNamedValue = request.result();
      if (localNamedValue == null) {
        throw wrapper.piDiiResultIsNull();
      }
      cachedResult = localNamedValue.value();
    }
    return cachedResult;
  }
  
  public boolean response_expected()
  {
    return !messageMediator.isOneWay();
  }
  
  public org.omg.CORBA.Object forward_reference()
  {
    checkAccess(10);
    if (replyStatus != 3) {
      throw stdWrapper.invalidPiCall1();
    }
    IOR localIOR = getLocatedIOR();
    return iorToObject(localIOR);
  }
  
  private IOR getLocatedIOR()
  {
    CorbaContactInfoList localCorbaContactInfoList = (CorbaContactInfoList)messageMediator.getContactInfo().getContactInfoList();
    IOR localIOR = localCorbaContactInfoList.getEffectiveTargetIOR();
    return localIOR;
  }
  
  protected void setLocatedIOR(IOR paramIOR)
  {
    ORB localORB = (ORB)messageMediator.getBroker();
    CorbaContactInfoListIterator localCorbaContactInfoListIterator = (CorbaContactInfoListIterator)((CorbaInvocationInfo)localORB.getInvocationInfo()).getContactInfoListIterator();
    localCorbaContactInfoListIterator.reportRedirect((CorbaContactInfo)messageMediator.getContactInfo(), paramIOR);
  }
  
  public ServiceContext get_request_service_context(int paramInt)
  {
    checkAccess(12);
    if (cachedRequestServiceContexts == null) {
      cachedRequestServiceContexts = new HashMap();
    }
    return getServiceContext(cachedRequestServiceContexts, messageMediator.getRequestServiceContexts(), paramInt);
  }
  
  public ServiceContext get_reply_service_context(int paramInt)
  {
    checkAccess(13);
    if (cachedReplyServiceContexts == null) {
      cachedReplyServiceContexts = new HashMap();
    }
    try
    {
      ServiceContexts localServiceContexts = messageMediator.getReplyServiceContexts();
      if (localServiceContexts == null) {
        throw new NullPointerException();
      }
      return getServiceContext(cachedReplyServiceContexts, localServiceContexts, paramInt);
    }
    catch (NullPointerException localNullPointerException)
    {
      throw stdWrapper.invalidServiceContextId(localNullPointerException);
    }
  }
  
  public Connection connection()
  {
    return (Connection)messageMediator.getConnection();
  }
  
  protected void setInfo(MessageMediator paramMessageMediator)
  {
    messageMediator = ((CorbaMessageMediator)paramMessageMediator);
    messageMediator.setDIIInfo(request);
  }
  
  void setRetryRequest(RetryType paramRetryType)
  {
    retryRequest = paramRetryType;
  }
  
  RetryType getRetryRequest()
  {
    return retryRequest;
  }
  
  void incrementEntryCount()
  {
    entryCount += 1;
  }
  
  void decrementEntryCount()
  {
    entryCount -= 1;
  }
  
  int getEntryCount()
  {
    return entryCount;
  }
  
  protected void setReplyStatus(short paramShort)
  {
    super.setReplyStatus(paramShort);
    switch (paramShort)
    {
    case 0: 
      endingPointCall = 0;
      break;
    case 1: 
    case 2: 
      endingPointCall = 1;
      break;
    case 3: 
    case 4: 
      endingPointCall = 2;
    }
  }
  
  protected void setDIIRequest(Request paramRequest)
  {
    request = paramRequest;
  }
  
  protected void setDIIInitiate(boolean paramBoolean)
  {
    diiInitiate = paramBoolean;
  }
  
  protected boolean isDIIInitiate()
  {
    return diiInitiate;
  }
  
  protected void setPICurrentPushed(boolean paramBoolean)
  {
    piCurrentPushed = paramBoolean;
  }
  
  protected boolean isPICurrentPushed()
  {
    return piCurrentPushed;
  }
  
  protected void setException(Exception paramException)
  {
    super.setException(paramException);
    cachedReceivedException = null;
    cachedReceivedExceptionId = null;
  }
  
  protected boolean getIsOneWay()
  {
    return !response_expected();
  }
  
  protected void checkAccess(int paramInt)
    throws BAD_INV_ORDER
  {
    int i = 0;
    switch (currentExecutionPoint)
    {
    case 0: 
      switch (startingPointCall)
      {
      case 0: 
        i = 0;
        break;
      case 1: 
        i = 1;
      }
      break;
    case 2: 
      switch (endingPointCall)
      {
      case 0: 
        i = 2;
        break;
      case 1: 
        i = 3;
        break;
      case 2: 
        i = 4;
      }
      break;
    }
    if (validCall[paramInt][i] == 0) {
      throw stdWrapper.invalidPiCall2();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\interceptors\ClientRequestInfoImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */