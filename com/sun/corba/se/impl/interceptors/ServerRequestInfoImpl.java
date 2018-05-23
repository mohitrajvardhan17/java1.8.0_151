package com.sun.corba.se.impl.interceptors;

import com.sun.corba.se.impl.logging.InterceptorsSystemException;
import com.sun.corba.se.impl.logging.OMGSystemException;
import com.sun.corba.se.impl.protocol.giopmsgheaders.ReplyMessage;
import com.sun.corba.se.spi.ior.ObjectAdapterId;
import com.sun.corba.se.spi.ior.ObjectKeyTemplate;
import com.sun.corba.se.spi.legacy.connection.Connection;
import com.sun.corba.se.spi.oa.ObjectAdapter;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.ORBData;
import com.sun.corba.se.spi.presentation.rmi.StubAdapter;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import com.sun.corba.se.spi.protocol.PIHandler;
import com.sun.corba.se.spi.servicecontext.ServiceContexts;
import java.util.ArrayList;
import java.util.HashMap;
import org.omg.CORBA.Any;
import org.omg.CORBA.BAD_INV_ORDER;
import org.omg.CORBA.NVList;
import org.omg.CORBA.Policy;
import org.omg.CORBA.TypeCode;
import org.omg.Dynamic.Parameter;
import org.omg.IOP.ServiceContext;
import org.omg.PortableInterceptor.ForwardRequest;
import org.omg.PortableInterceptor.InvalidSlot;
import org.omg.PortableInterceptor.ServerRequestInfo;
import org.omg.PortableServer.Servant;

public final class ServerRequestInfoImpl
  extends RequestInfoImpl
  implements ServerRequestInfo
{
  static final int CALL_RECEIVE_REQUEST_SERVICE_CONTEXT = 0;
  static final int CALL_RECEIVE_REQUEST = 0;
  static final int CALL_INTERMEDIATE_NONE = 1;
  static final int CALL_SEND_REPLY = 0;
  static final int CALL_SEND_EXCEPTION = 1;
  static final int CALL_SEND_OTHER = 2;
  private boolean forwardRequestRaisedInEnding;
  private CorbaMessageMediator request;
  private Object servant;
  private byte[] objectId;
  private ObjectKeyTemplate oktemp;
  private byte[] adapterId;
  private String[] adapterName;
  private ArrayList addReplyServiceContextQueue;
  private ReplyMessage replyMessage;
  private String targetMostDerivedInterface;
  private NVList dsiArguments;
  private Any dsiResult;
  private Any dsiException;
  private boolean isDynamic;
  private ObjectAdapter objectAdapter;
  private int serverRequestId;
  private Parameter[] cachedArguments;
  private Any cachedSendingException;
  private HashMap cachedRequestServiceContexts;
  private HashMap cachedReplyServiceContexts;
  protected static final int MID_SENDING_EXCEPTION = 14;
  protected static final int MID_OBJECT_ID = 15;
  protected static final int MID_ADAPTER_ID = 16;
  protected static final int MID_TARGET_MOST_DERIVED_INTERFACE = 17;
  protected static final int MID_GET_SERVER_POLICY = 18;
  protected static final int MID_SET_SLOT = 19;
  protected static final int MID_TARGET_IS_A = 20;
  protected static final int MID_ADD_REPLY_SERVICE_CONTEXT = 21;
  protected static final int MID_SERVER_ID = 22;
  protected static final int MID_ORB_ID = 23;
  protected static final int MID_ADAPTER_NAME = 24;
  private static final boolean[][] validCall = { { true, true, true, true, true }, { true, true, true, true, true }, { false, true, true, false, false }, { false, true, true, true, true }, { false, true, true, true, true }, { false, true, true, false, false }, { false, false, true, false, false }, { true, true, true, true, true }, { true, true, true, true, true }, { false, false, true, true, true }, { false, false, false, false, true }, { true, true, true, true, true }, { true, true, true, true, true }, { false, false, true, true, true }, { false, false, false, true, false }, { false, true, true, true, true }, { false, true, true, true, true }, { false, true, false, false, false }, { true, true, true, true, true }, { true, true, true, true, true }, { false, true, false, false, false }, { true, true, true, true, true }, { false, true, true, true, true }, { false, true, true, true, true }, { false, true, true, true, true } };
  
  void reset()
  {
    super.reset();
    forwardRequestRaisedInEnding = false;
    request = null;
    servant = null;
    objectId = null;
    oktemp = null;
    adapterId = null;
    adapterName = null;
    addReplyServiceContextQueue = null;
    replyMessage = null;
    targetMostDerivedInterface = null;
    dsiArguments = null;
    dsiResult = null;
    dsiException = null;
    isDynamic = false;
    objectAdapter = null;
    serverRequestId = myORB.getPIHandler().allocateServerRequestId();
    cachedArguments = null;
    cachedSendingException = null;
    cachedRequestServiceContexts = null;
    cachedReplyServiceContexts = null;
    startingPointCall = 0;
    intermediatePointCall = 0;
    endingPointCall = 0;
  }
  
  ServerRequestInfoImpl(ORB paramORB)
  {
    super(paramORB);
    startingPointCall = 0;
    intermediatePointCall = 0;
    endingPointCall = 0;
    serverRequestId = paramORB.getPIHandler().allocateServerRequestId();
  }
  
  public Any sending_exception()
  {
    checkAccess(14);
    if (cachedSendingException == null)
    {
      Any localAny = null;
      if (dsiException != null) {
        localAny = dsiException;
      } else if (exception != null) {
        localAny = exceptionToAny(exception);
      } else {
        throw wrapper.exceptionUnavailable();
      }
      cachedSendingException = localAny;
    }
    return cachedSendingException;
  }
  
  public byte[] object_id()
  {
    checkAccess(15);
    if (objectId == null) {
      throw stdWrapper.piOperationNotSupported6();
    }
    return objectId;
  }
  
  private void checkForNullTemplate()
  {
    if (oktemp == null) {
      throw stdWrapper.piOperationNotSupported7();
    }
  }
  
  public String server_id()
  {
    checkAccess(22);
    checkForNullTemplate();
    return Integer.toString(oktemp.getServerId());
  }
  
  public String orb_id()
  {
    checkAccess(23);
    return myORB.getORBData().getORBId();
  }
  
  public synchronized String[] adapter_name()
  {
    checkAccess(24);
    if (adapterName == null)
    {
      checkForNullTemplate();
      ObjectAdapterId localObjectAdapterId = oktemp.getObjectAdapterId();
      adapterName = localObjectAdapterId.getAdapterName();
    }
    return adapterName;
  }
  
  public synchronized byte[] adapter_id()
  {
    checkAccess(16);
    if (adapterId == null)
    {
      checkForNullTemplate();
      adapterId = oktemp.getAdapterId();
    }
    return adapterId;
  }
  
  public String target_most_derived_interface()
  {
    checkAccess(17);
    return targetMostDerivedInterface;
  }
  
  public Policy get_server_policy(int paramInt)
  {
    Policy localPolicy = null;
    if (objectAdapter != null) {
      localPolicy = objectAdapter.getEffectivePolicy(paramInt);
    }
    return localPolicy;
  }
  
  public void set_slot(int paramInt, Any paramAny)
    throws InvalidSlot
  {
    slotTable.set_slot(paramInt, paramAny);
  }
  
  public boolean target_is_a(String paramString)
  {
    checkAccess(20);
    boolean bool = false;
    if ((servant instanceof Servant)) {
      bool = ((Servant)servant)._is_a(paramString);
    } else if (StubAdapter.isStub(servant)) {
      bool = ((org.omg.CORBA.Object)servant)._is_a(paramString);
    } else {
      throw wrapper.servantInvalid();
    }
    return bool;
  }
  
  public void add_reply_service_context(ServiceContext paramServiceContext, boolean paramBoolean)
  {
    if (currentExecutionPoint == 2)
    {
      localObject = replyMessage.getServiceContexts();
      if (localObject == null)
      {
        localObject = new ServiceContexts(myORB);
        replyMessage.setServiceContexts((ServiceContexts)localObject);
      }
      if (cachedReplyServiceContexts == null) {
        cachedReplyServiceContexts = new HashMap();
      }
      addServiceContext(cachedReplyServiceContexts, (ServiceContexts)localObject, paramServiceContext, paramBoolean);
    }
    Object localObject = new AddReplyServiceContextCommand(null);
    service_context = paramServiceContext;
    replace = paramBoolean;
    if (addReplyServiceContextQueue == null) {
      addReplyServiceContextQueue = new ArrayList();
    }
    enqueue((AddReplyServiceContextCommand)localObject);
  }
  
  public int request_id()
  {
    return serverRequestId;
  }
  
  public String operation()
  {
    return request.getOperationName();
  }
  
  public Parameter[] arguments()
  {
    checkAccess(2);
    if (cachedArguments == null)
    {
      if (!isDynamic) {
        throw stdWrapper.piOperationNotSupported1();
      }
      if (dsiArguments == null) {
        throw stdWrapper.piOperationNotSupported8();
      }
      cachedArguments = nvListToParameterArray(dsiArguments);
    }
    return cachedArguments;
  }
  
  public TypeCode[] exceptions()
  {
    checkAccess(3);
    throw stdWrapper.piOperationNotSupported2();
  }
  
  public String[] contexts()
  {
    checkAccess(4);
    throw stdWrapper.piOperationNotSupported3();
  }
  
  public String[] operation_context()
  {
    checkAccess(5);
    throw stdWrapper.piOperationNotSupported4();
  }
  
  public Any result()
  {
    checkAccess(6);
    if (!isDynamic) {
      throw stdWrapper.piOperationNotSupported5();
    }
    if (dsiResult == null) {
      throw wrapper.piDsiResultIsNull();
    }
    return dsiResult;
  }
  
  public boolean response_expected()
  {
    return !request.isOneWay();
  }
  
  public org.omg.CORBA.Object forward_reference()
  {
    checkAccess(10);
    if (replyStatus != 3) {
      throw stdWrapper.invalidPiCall1();
    }
    return getForwardRequestExceptionforward;
  }
  
  public ServiceContext get_request_service_context(int paramInt)
  {
    checkAccess(12);
    if (cachedRequestServiceContexts == null) {
      cachedRequestServiceContexts = new HashMap();
    }
    return getServiceContext(cachedRequestServiceContexts, request.getRequestServiceContexts(), paramInt);
  }
  
  public ServiceContext get_reply_service_context(int paramInt)
  {
    checkAccess(13);
    if (cachedReplyServiceContexts == null) {
      cachedReplyServiceContexts = new HashMap();
    }
    return getServiceContext(cachedReplyServiceContexts, replyMessage.getServiceContexts(), paramInt);
  }
  
  private void enqueue(AddReplyServiceContextCommand paramAddReplyServiceContextCommand)
  {
    int i = addReplyServiceContextQueue.size();
    int j = 0;
    for (int k = 0; k < i; k++)
    {
      AddReplyServiceContextCommand localAddReplyServiceContextCommand = (AddReplyServiceContextCommand)addReplyServiceContextQueue.get(k);
      if (service_context.context_id == service_context.context_id)
      {
        j = 1;
        if (replace)
        {
          addReplyServiceContextQueue.set(k, paramAddReplyServiceContextCommand);
          break;
        }
        throw stdWrapper.serviceContextAddFailed(new Integer(service_context.context_id));
      }
    }
    if (j == 0) {
      addReplyServiceContextQueue.add(paramAddReplyServiceContextCommand);
    }
  }
  
  protected void setCurrentExecutionPoint(int paramInt)
  {
    super.setCurrentExecutionPoint(paramInt);
    if ((paramInt == 2) && (addReplyServiceContextQueue != null))
    {
      int i = addReplyServiceContextQueue.size();
      for (int j = 0; j < i; j++)
      {
        AddReplyServiceContextCommand localAddReplyServiceContextCommand = (AddReplyServiceContextCommand)addReplyServiceContextQueue.get(j);
        try
        {
          add_reply_service_context(service_context, replace);
        }
        catch (BAD_INV_ORDER localBAD_INV_ORDER) {}
      }
    }
  }
  
  protected void setInfo(CorbaMessageMediator paramCorbaMessageMediator, ObjectAdapter paramObjectAdapter, byte[] paramArrayOfByte, ObjectKeyTemplate paramObjectKeyTemplate)
  {
    request = paramCorbaMessageMediator;
    objectId = paramArrayOfByte;
    oktemp = paramObjectKeyTemplate;
    objectAdapter = paramObjectAdapter;
    connection = ((Connection)paramCorbaMessageMediator.getConnection());
  }
  
  protected void setDSIArguments(NVList paramNVList)
  {
    dsiArguments = paramNVList;
  }
  
  protected void setDSIException(Any paramAny)
  {
    dsiException = paramAny;
    cachedSendingException = null;
  }
  
  protected void setDSIResult(Any paramAny)
  {
    dsiResult = paramAny;
  }
  
  protected void setException(Exception paramException)
  {
    super.setException(paramException);
    dsiException = null;
    cachedSendingException = null;
  }
  
  protected void setInfo(Object paramObject, String paramString)
  {
    servant = paramObject;
    targetMostDerivedInterface = paramString;
    isDynamic = (((paramObject instanceof org.omg.PortableServer.DynamicImplementation)) || ((paramObject instanceof org.omg.CORBA.DynamicImplementation)));
  }
  
  void setReplyMessage(ReplyMessage paramReplyMessage)
  {
    replyMessage = paramReplyMessage;
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
  
  void releaseServant()
  {
    servant = null;
  }
  
  void setForwardRequestRaisedInEnding()
  {
    forwardRequestRaisedInEnding = true;
  }
  
  boolean isForwardRequestRaisedInEnding()
  {
    return forwardRequestRaisedInEnding;
  }
  
  boolean isDynamic()
  {
    return isDynamic;
  }
  
  protected void checkAccess(int paramInt)
  {
    int i = 0;
    switch (currentExecutionPoint)
    {
    case 0: 
      i = 0;
      break;
    case 1: 
      i = 1;
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
  
  private class AddReplyServiceContextCommand
  {
    ServiceContext service_context;
    boolean replace;
    
    private AddReplyServiceContextCommand() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\interceptors\ServerRequestInfoImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */