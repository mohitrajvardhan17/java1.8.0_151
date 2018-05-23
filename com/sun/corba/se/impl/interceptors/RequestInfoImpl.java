package com.sun.corba.se.impl.interceptors;

import com.sun.corba.se.impl.encoding.EncapsOutputStream;
import com.sun.corba.se.impl.logging.InterceptorsSystemException;
import com.sun.corba.se.impl.logging.OMGSystemException;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.impl.util.RepositoryId;
import com.sun.corba.se.impl.util.RepositoryIdCache;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.legacy.connection.Connection;
import com.sun.corba.se.spi.legacy.interceptor.RequestInfoExt;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.protocol.PIHandler;
import com.sun.corba.se.spi.servicecontext.ServiceContexts;
import com.sun.corba.se.spi.servicecontext.UnknownServiceContext;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import org.omg.CORBA.Any;
import org.omg.CORBA.BAD_INV_ORDER;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.LocalObject;
import org.omg.CORBA.NVList;
import org.omg.CORBA.NamedValue;
import org.omg.CORBA.ParameterMode;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.UNKNOWN;
import org.omg.CORBA.UserException;
import org.omg.CORBA.portable.ApplicationException;
import org.omg.Dynamic.Parameter;
import org.omg.IOP.ServiceContextHelper;
import org.omg.PortableInterceptor.ForwardRequest;
import org.omg.PortableInterceptor.InvalidSlot;
import org.omg.PortableInterceptor.RequestInfo;
import sun.corba.JavaCorbaAccess;
import sun.corba.OutputStreamFactory;
import sun.corba.SharedSecrets;

public abstract class RequestInfoImpl
  extends LocalObject
  implements RequestInfo, RequestInfoExt
{
  protected ORB myORB;
  protected InterceptorsSystemException wrapper;
  protected OMGSystemException stdWrapper;
  protected int flowStackIndex = 0;
  protected int startingPointCall;
  protected int intermediatePointCall;
  protected int endingPointCall;
  protected short replyStatus = -1;
  protected static final short UNINITIALIZED = -1;
  protected int currentExecutionPoint;
  protected static final int EXECUTION_POINT_STARTING = 0;
  protected static final int EXECUTION_POINT_INTERMEDIATE = 1;
  protected static final int EXECUTION_POINT_ENDING = 2;
  protected boolean alreadyExecuted;
  protected Connection connection;
  protected ServiceContexts serviceContexts;
  protected ForwardRequest forwardRequest;
  protected IOR forwardRequestIOR;
  protected SlotTable slotTable;
  protected Exception exception;
  protected static final int MID_REQUEST_ID = 0;
  protected static final int MID_OPERATION = 1;
  protected static final int MID_ARGUMENTS = 2;
  protected static final int MID_EXCEPTIONS = 3;
  protected static final int MID_CONTEXTS = 4;
  protected static final int MID_OPERATION_CONTEXT = 5;
  protected static final int MID_RESULT = 6;
  protected static final int MID_RESPONSE_EXPECTED = 7;
  protected static final int MID_SYNC_SCOPE = 8;
  protected static final int MID_REPLY_STATUS = 9;
  protected static final int MID_FORWARD_REFERENCE = 10;
  protected static final int MID_GET_SLOT = 11;
  protected static final int MID_GET_REQUEST_SERVICE_CONTEXT = 12;
  protected static final int MID_GET_REPLY_SERVICE_CONTEXT = 13;
  protected static final int MID_RI_LAST = 13;
  
  void reset()
  {
    flowStackIndex = 0;
    startingPointCall = 0;
    intermediatePointCall = 0;
    endingPointCall = 0;
    setReplyStatus((short)-1);
    currentExecutionPoint = 0;
    alreadyExecuted = false;
    connection = null;
    serviceContexts = null;
    forwardRequest = null;
    forwardRequestIOR = null;
    exception = null;
  }
  
  public RequestInfoImpl(ORB paramORB)
  {
    myORB = paramORB;
    wrapper = InterceptorsSystemException.get(paramORB, "rpc.protocol");
    stdWrapper = OMGSystemException.get(paramORB, "rpc.protocol");
    PICurrent localPICurrent = (PICurrent)paramORB.getPIHandler().getPICurrent();
    slotTable = localPICurrent.getSlotTable();
  }
  
  public abstract int request_id();
  
  public abstract String operation();
  
  public abstract Parameter[] arguments();
  
  public abstract TypeCode[] exceptions();
  
  public abstract String[] contexts();
  
  public abstract String[] operation_context();
  
  public abstract Any result();
  
  public abstract boolean response_expected();
  
  public short sync_scope()
  {
    checkAccess(8);
    return 1;
  }
  
  public short reply_status()
  {
    checkAccess(9);
    return replyStatus;
  }
  
  public abstract org.omg.CORBA.Object forward_reference();
  
  public Any get_slot(int paramInt)
    throws InvalidSlot
  {
    return slotTable.get_slot(paramInt);
  }
  
  public abstract org.omg.IOP.ServiceContext get_request_service_context(int paramInt);
  
  public abstract org.omg.IOP.ServiceContext get_reply_service_context(int paramInt);
  
  public Connection connection()
  {
    return connection;
  }
  
  private void insertApplicationException(ApplicationException paramApplicationException, Any paramAny)
    throws UNKNOWN
  {
    try
    {
      RepositoryId localRepositoryId = RepositoryId.cache.getId(paramApplicationException.getId());
      String str1 = localRepositoryId.getClassName();
      String str2 = str1 + "Helper";
      Class localClass = SharedSecrets.getJavaCorbaAccess().loadClass(str2);
      Class[] arrayOfClass = new Class[1];
      arrayOfClass[0] = org.omg.CORBA.portable.InputStream.class;
      Method localMethod = localClass.getMethod("read", arrayOfClass);
      org.omg.CORBA.portable.InputStream localInputStream = paramApplicationException.getInputStream();
      localInputStream.mark(0);
      localUserException = null;
      try
      {
        Object[] arrayOfObject = new Object[1];
        arrayOfObject[0] = localInputStream;
        localUserException = (UserException)localMethod.invoke(null, arrayOfObject);
        try
        {
          localInputStream.reset();
        }
        catch (IOException localIOException1)
        {
          throw wrapper.markAndResetFailed(localIOException1);
        }
        insertUserException(localUserException, paramAny);
      }
      finally
      {
        try
        {
          localInputStream.reset();
        }
        catch (IOException localIOException2)
        {
          throw wrapper.markAndResetFailed(localIOException2);
        }
      }
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      UserException localUserException;
      throw stdWrapper.unknownUserException(CompletionStatus.COMPLETED_MAYBE, localClassNotFoundException);
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      throw stdWrapper.unknownUserException(CompletionStatus.COMPLETED_MAYBE, localNoSuchMethodException);
    }
    catch (SecurityException localSecurityException)
    {
      throw stdWrapper.unknownUserException(CompletionStatus.COMPLETED_MAYBE, localSecurityException);
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      throw stdWrapper.unknownUserException(CompletionStatus.COMPLETED_MAYBE, localIllegalAccessException);
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      throw stdWrapper.unknownUserException(CompletionStatus.COMPLETED_MAYBE, localIllegalArgumentException);
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      throw stdWrapper.unknownUserException(CompletionStatus.COMPLETED_MAYBE, localInvocationTargetException);
    }
  }
  
  private void insertUserException(UserException paramUserException, Any paramAny)
    throws UNKNOWN
  {
    try
    {
      if (paramUserException != null)
      {
        Class localClass1 = paramUserException.getClass();
        String str1 = localClass1.getName();
        String str2 = str1 + "Helper";
        Class localClass2 = SharedSecrets.getJavaCorbaAccess().loadClass(str2);
        Class[] arrayOfClass = new Class[2];
        arrayOfClass[0] = Any.class;
        arrayOfClass[1] = localClass1;
        Method localMethod = localClass2.getMethod("insert", arrayOfClass);
        Object[] arrayOfObject = new Object[2];
        arrayOfObject[0] = paramAny;
        arrayOfObject[1] = paramUserException;
        localMethod.invoke(null, arrayOfObject);
      }
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      throw stdWrapper.unknownUserException(CompletionStatus.COMPLETED_MAYBE, localClassNotFoundException);
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      throw stdWrapper.unknownUserException(CompletionStatus.COMPLETED_MAYBE, localNoSuchMethodException);
    }
    catch (SecurityException localSecurityException)
    {
      throw stdWrapper.unknownUserException(CompletionStatus.COMPLETED_MAYBE, localSecurityException);
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      throw stdWrapper.unknownUserException(CompletionStatus.COMPLETED_MAYBE, localIllegalAccessException);
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      throw stdWrapper.unknownUserException(CompletionStatus.COMPLETED_MAYBE, localIllegalArgumentException);
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      throw stdWrapper.unknownUserException(CompletionStatus.COMPLETED_MAYBE, localInvocationTargetException);
    }
  }
  
  protected Parameter[] nvListToParameterArray(NVList paramNVList)
  {
    int i = paramNVList.count();
    Parameter[] arrayOfParameter = new Parameter[i];
    try
    {
      for (int j = 0; j < i; j++)
      {
        Parameter localParameter = new Parameter();
        arrayOfParameter[j] = localParameter;
        NamedValue localNamedValue = paramNVList.item(j);
        argument = localNamedValue.value();
        mode = ParameterMode.from_int(localNamedValue.flags() - 1);
      }
    }
    catch (Exception localException)
    {
      throw wrapper.exceptionInArguments(localException);
    }
    return arrayOfParameter;
  }
  
  protected Any exceptionToAny(Exception paramException)
  {
    Any localAny = myORB.create_any();
    if (paramException == null) {
      throw wrapper.exceptionWasNull2();
    }
    if ((paramException instanceof SystemException)) {
      ORBUtility.insertSystemException((SystemException)paramException, localAny);
    } else if ((paramException instanceof ApplicationException)) {
      try
      {
        ApplicationException localApplicationException = (ApplicationException)paramException;
        insertApplicationException(localApplicationException, localAny);
      }
      catch (UNKNOWN localUNKNOWN1)
      {
        ORBUtility.insertSystemException(localUNKNOWN1, localAny);
      }
    } else if ((paramException instanceof UserException)) {
      try
      {
        UserException localUserException = (UserException)paramException;
        insertUserException(localUserException, localAny);
      }
      catch (UNKNOWN localUNKNOWN2)
      {
        ORBUtility.insertSystemException(localUNKNOWN2, localAny);
      }
    }
    return localAny;
  }
  
  protected org.omg.IOP.ServiceContext getServiceContext(HashMap paramHashMap, ServiceContexts paramServiceContexts, int paramInt)
  {
    org.omg.IOP.ServiceContext localServiceContext = null;
    Integer localInteger = new Integer(paramInt);
    localServiceContext = (org.omg.IOP.ServiceContext)paramHashMap.get(localInteger);
    if (localServiceContext == null)
    {
      com.sun.corba.se.spi.servicecontext.ServiceContext localServiceContext1 = paramServiceContexts.get(paramInt);
      if (localServiceContext1 == null) {
        throw stdWrapper.invalidServiceContextId();
      }
      EncapsOutputStream localEncapsOutputStream = OutputStreamFactory.newEncapsOutputStream(myORB);
      localServiceContext1.write(localEncapsOutputStream, GIOPVersion.V1_2);
      org.omg.CORBA.portable.InputStream localInputStream = localEncapsOutputStream.create_input_stream();
      localServiceContext = ServiceContextHelper.read(localInputStream);
      paramHashMap.put(localInteger, localServiceContext);
    }
    return localServiceContext;
  }
  
  protected void addServiceContext(HashMap paramHashMap, ServiceContexts paramServiceContexts, org.omg.IOP.ServiceContext paramServiceContext, boolean paramBoolean)
  {
    int i = 0;
    EncapsOutputStream localEncapsOutputStream = OutputStreamFactory.newEncapsOutputStream(myORB);
    org.omg.CORBA.portable.InputStream localInputStream = null;
    UnknownServiceContext localUnknownServiceContext = null;
    ServiceContextHelper.write(localEncapsOutputStream, paramServiceContext);
    localInputStream = localEncapsOutputStream.create_input_stream();
    localUnknownServiceContext = new UnknownServiceContext(localInputStream.read_long(), (org.omg.CORBA_2_3.portable.InputStream)localInputStream);
    i = localUnknownServiceContext.getId();
    if (paramServiceContexts.get(i) != null) {
      if (paramBoolean) {
        paramServiceContexts.delete(i);
      } else {
        throw stdWrapper.serviceContextAddFailed(new Integer(i));
      }
    }
    paramServiceContexts.put(localUnknownServiceContext);
    paramHashMap.put(new Integer(i), paramServiceContext);
  }
  
  protected void setFlowStackIndex(int paramInt)
  {
    flowStackIndex = paramInt;
  }
  
  protected int getFlowStackIndex()
  {
    return flowStackIndex;
  }
  
  protected void setEndingPointCall(int paramInt)
  {
    endingPointCall = paramInt;
  }
  
  protected int getEndingPointCall()
  {
    return endingPointCall;
  }
  
  protected void setIntermediatePointCall(int paramInt)
  {
    intermediatePointCall = paramInt;
  }
  
  protected int getIntermediatePointCall()
  {
    return intermediatePointCall;
  }
  
  protected void setStartingPointCall(int paramInt)
  {
    startingPointCall = paramInt;
  }
  
  protected int getStartingPointCall()
  {
    return startingPointCall;
  }
  
  protected boolean getAlreadyExecuted()
  {
    return alreadyExecuted;
  }
  
  protected void setAlreadyExecuted(boolean paramBoolean)
  {
    alreadyExecuted = paramBoolean;
  }
  
  protected void setReplyStatus(short paramShort)
  {
    replyStatus = paramShort;
  }
  
  protected short getReplyStatus()
  {
    return replyStatus;
  }
  
  protected void setForwardRequest(ForwardRequest paramForwardRequest)
  {
    forwardRequest = paramForwardRequest;
    forwardRequestIOR = null;
  }
  
  protected void setForwardRequest(IOR paramIOR)
  {
    forwardRequestIOR = paramIOR;
    forwardRequest = null;
  }
  
  protected ForwardRequest getForwardRequestException()
  {
    if ((forwardRequest == null) && (forwardRequestIOR != null))
    {
      org.omg.CORBA.Object localObject = iorToObject(forwardRequestIOR);
      forwardRequest = new ForwardRequest(localObject);
    }
    return forwardRequest;
  }
  
  protected IOR getForwardRequestIOR()
  {
    if ((forwardRequestIOR == null) && (forwardRequest != null)) {
      forwardRequestIOR = ORBUtility.getIOR(forwardRequest.forward);
    }
    return forwardRequestIOR;
  }
  
  protected void setException(Exception paramException)
  {
    exception = paramException;
  }
  
  Exception getException()
  {
    return exception;
  }
  
  protected void setCurrentExecutionPoint(int paramInt)
  {
    currentExecutionPoint = paramInt;
  }
  
  protected abstract void checkAccess(int paramInt)
    throws BAD_INV_ORDER;
  
  void setSlotTable(SlotTable paramSlotTable)
  {
    slotTable = paramSlotTable;
  }
  
  protected org.omg.CORBA.Object iorToObject(IOR paramIOR)
  {
    return ORBUtility.makeObjectReference(paramIOR);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\interceptors\RequestInfoImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */