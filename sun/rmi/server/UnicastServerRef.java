package sun.rmi.server;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectStreamClass;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.AccessException;
import java.rmi.MarshalException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.ServerError;
import java.rmi.ServerException;
import java.rmi.UnmarshalException;
import java.rmi.server.ExportException;
import java.rmi.server.ObjID;
import java.rmi.server.RemoteCall;
import java.rmi.server.RemoteRef;
import java.rmi.server.RemoteStub;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.ServerRef;
import java.rmi.server.Skeleton;
import java.rmi.server.SkeletonNotFoundException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import sun.misc.ObjectInputFilter;
import sun.misc.ObjectInputFilter.Config;
import sun.rmi.runtime.Log;
import sun.rmi.transport.LiveRef;
import sun.rmi.transport.StreamRemoteCall;
import sun.rmi.transport.Target;
import sun.rmi.transport.tcp.TCPTransport;
import sun.security.action.GetBooleanAction;

public class UnicastServerRef
  extends UnicastRef
  implements ServerRef, Dispatcher
{
  public static final boolean logCalls = ((Boolean)AccessController.doPrivileged(new GetBooleanAction("java.rmi.server.logCalls"))).booleanValue();
  public static final Log callLog = Log.getLog("sun.rmi.server.call", "RMI", logCalls);
  private static final long serialVersionUID = -7384275867073752268L;
  private static final boolean wantExceptionLog = ((Boolean)AccessController.doPrivileged(new GetBooleanAction("sun.rmi.server.exceptionTrace"))).booleanValue();
  private boolean forceStubUse = false;
  private static final boolean suppressStackTraces = ((Boolean)AccessController.doPrivileged(new GetBooleanAction("sun.rmi.server.suppressStackTraces"))).booleanValue();
  private transient Skeleton skel;
  private final transient ObjectInputFilter filter;
  private transient Map<Long, Method> hashToMethod_Map = null;
  private static final WeakClassHashMap<Map<Long, Method>> hashToMethod_Maps = new HashToMethod_Maps();
  private static final Map<Class<?>, ?> withoutSkeletons = Collections.synchronizedMap(new WeakHashMap());
  private final AtomicInteger methodCallIDCount = new AtomicInteger(0);
  
  public UnicastServerRef()
  {
    filter = null;
  }
  
  public UnicastServerRef(LiveRef paramLiveRef)
  {
    super(paramLiveRef);
    filter = null;
  }
  
  public UnicastServerRef(LiveRef paramLiveRef, ObjectInputFilter paramObjectInputFilter)
  {
    super(paramLiveRef);
    filter = paramObjectInputFilter;
  }
  
  public UnicastServerRef(int paramInt)
  {
    super(new LiveRef(paramInt));
    filter = null;
  }
  
  public UnicastServerRef(boolean paramBoolean)
  {
    this(0);
  }
  
  public RemoteStub exportObject(Remote paramRemote, Object paramObject)
    throws RemoteException
  {
    forceStubUse = true;
    return (RemoteStub)exportObject(paramRemote, paramObject, false);
  }
  
  public Remote exportObject(Remote paramRemote, Object paramObject, boolean paramBoolean)
    throws RemoteException
  {
    Class localClass = paramRemote.getClass();
    Remote localRemote;
    try
    {
      localRemote = Util.createProxy(localClass, getClientRef(), forceStubUse);
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      throw new ExportException("remote object implements illegal remote interface", localIllegalArgumentException);
    }
    if ((localRemote instanceof RemoteStub)) {
      setSkeleton(paramRemote);
    }
    Target localTarget = new Target(paramRemote, this, localRemote, ref.getObjID(), paramBoolean);
    ref.exportObject(localTarget);
    hashToMethod_Map = ((Map)hashToMethod_Maps.get(localClass));
    return localRemote;
  }
  
  public String getClientHost()
    throws ServerNotActiveException
  {
    return TCPTransport.getClientHost();
  }
  
  public void setSkeleton(Remote paramRemote)
    throws RemoteException
  {
    if (!withoutSkeletons.containsKey(paramRemote.getClass())) {
      try
      {
        skel = Util.createSkeleton(paramRemote);
      }
      catch (SkeletonNotFoundException localSkeletonNotFoundException)
      {
        withoutSkeletons.put(paramRemote.getClass(), null);
      }
    }
  }
  
  public void dispatch(Remote paramRemote, RemoteCall paramRemoteCall)
    throws IOException
  {
    try
    {
      ObjectInput localObjectInput;
      int i;
      try
      {
        localObjectInput = paramRemoteCall.getInputStream();
        i = localObjectInput.readInt();
      }
      catch (Exception localException1)
      {
        throw new UnmarshalException("error unmarshalling call header", localException1);
      }
      if (i >= 0)
      {
        if (skel != null)
        {
          oldDispatch(paramRemote, paramRemoteCall, i);
          return;
        }
        throw new UnmarshalException("skeleton class not found but required for client version");
      }
      long l;
      try
      {
        l = localObjectInput.readLong();
      }
      catch (Exception localException2)
      {
        throw new UnmarshalException("error unmarshalling call header", localException2);
      }
      localObject2 = (MarshalInputStream)localObjectInput;
      ((MarshalInputStream)localObject2).skipDefaultResolveClass();
      localObject3 = (Method)hashToMethod_Map.get(Long.valueOf(l));
      if (localObject3 == null) {
        throw new UnmarshalException("unrecognized method hash: method not supported by remote object");
      }
      logCall(paramRemote, localObject3);
      Object[] arrayOfObject = null;
      try
      {
        unmarshalCustomCallData(localObjectInput);
        arrayOfObject = unmarshalParameters(paramRemote, (Method)localObject3, (MarshalInputStream)localObject2);
      }
      catch (AccessException localAccessException)
      {
        ((StreamRemoteCall)paramRemoteCall).discardPendingRefs();
        throw localAccessException;
      }
      catch (IOException|ClassNotFoundException localIOException1)
      {
        ((StreamRemoteCall)paramRemoteCall).discardPendingRefs();
        throw new UnmarshalException("error unmarshalling arguments", localIOException1);
      }
      finally
      {
        paramRemoteCall.releaseInputStream();
      }
      Object localObject4;
      try
      {
        localObject4 = ((Method)localObject3).invoke(paramRemote, arrayOfObject);
      }
      catch (InvocationTargetException localInvocationTargetException)
      {
        throw localInvocationTargetException.getTargetException();
      }
      try
      {
        ObjectOutput localObjectOutput = paramRemoteCall.getResultStream(true);
        Class localClass = ((Method)localObject3).getReturnType();
        if (localClass != Void.TYPE) {
          marshalValue(localClass, localObject4, localObjectOutput);
        }
      }
      catch (IOException localIOException2)
      {
        throw new MarshalException("error marshalling return", localIOException2);
      }
    }
    catch (Throwable localThrowable)
    {
      Object localObject2 = localThrowable;
      logCallException(localThrowable);
      Object localObject3 = paramRemoteCall.getResultStream(false);
      Object localObject1;
      if ((localThrowable instanceof Error)) {
        localObject1 = new ServerError("Error occurred in server thread", (Error)localThrowable);
      } else if ((localObject1 instanceof RemoteException)) {
        localObject1 = new ServerException("RemoteException occurred in server thread", (Exception)localObject1);
      }
      if (suppressStackTraces) {
        clearStackTraces((Throwable)localObject1);
      }
      ((ObjectOutput)localObject3).writeObject(localObject1);
      if ((localObject2 instanceof AccessException)) {
        throw new IOException("Connection is not reusable", (Throwable)localObject2);
      }
    }
    finally
    {
      paramRemoteCall.releaseInputStream();
      paramRemoteCall.releaseOutputStream();
    }
  }
  
  protected void unmarshalCustomCallData(ObjectInput paramObjectInput)
    throws IOException, ClassNotFoundException
  {
    if ((filter != null) && ((paramObjectInput instanceof ObjectInputStream)))
    {
      final ObjectInputStream localObjectInputStream = (ObjectInputStream)paramObjectInput;
      AccessController.doPrivileged(new PrivilegedAction()
      {
        public Void run()
        {
          ObjectInputFilter.Config.setObjectInputFilter(localObjectInputStream, filter);
          return null;
        }
      });
    }
  }
  
  private void oldDispatch(Remote paramRemote, RemoteCall paramRemoteCall, int paramInt)
    throws Exception
  {
    ObjectInput localObjectInput = paramRemoteCall.getInputStream();
    try
    {
      Class localClass = Class.forName("sun.rmi.transport.DGCImpl_Skel");
      if (localClass.isAssignableFrom(skel.getClass())) {
        ((MarshalInputStream)localObjectInput).useCodebaseOnly();
      }
    }
    catch (ClassNotFoundException localClassNotFoundException) {}
    long l;
    try
    {
      l = localObjectInput.readLong();
    }
    catch (Exception localException)
    {
      throw new UnmarshalException("error unmarshalling call header", localException);
    }
    logCall(paramRemote, skel.getOperations()[paramInt]);
    unmarshalCustomCallData(localObjectInput);
    skel.dispatch(paramRemote, paramRemoteCall, paramInt, l);
  }
  
  public static void clearStackTraces(Throwable paramThrowable)
  {
    StackTraceElement[] arrayOfStackTraceElement = new StackTraceElement[0];
    while (paramThrowable != null)
    {
      paramThrowable.setStackTrace(arrayOfStackTraceElement);
      paramThrowable = paramThrowable.getCause();
    }
  }
  
  private void logCall(Remote paramRemote, Object paramObject)
  {
    if (callLog.isLoggable(Log.VERBOSE))
    {
      String str;
      try
      {
        str = getClientHost();
      }
      catch (ServerNotActiveException localServerNotActiveException)
      {
        str = "(local)";
      }
      callLog.log(Log.VERBOSE, "[" + str + ": " + paramRemote.getClass().getName() + ref.getObjID().toString() + ": " + paramObject + "]");
    }
  }
  
  private void logCallException(Throwable paramThrowable)
  {
    Object localObject1;
    if (callLog.isLoggable(Log.BRIEF))
    {
      localObject1 = "";
      try
      {
        localObject1 = "[" + getClientHost() + "] ";
      }
      catch (ServerNotActiveException localServerNotActiveException) {}
      callLog.log(Log.BRIEF, (String)localObject1 + "exception: ", paramThrowable);
    }
    if (wantExceptionLog)
    {
      localObject1 = System.err;
      synchronized (localObject1)
      {
        ((PrintStream)localObject1).println();
        ((PrintStream)localObject1).println("Exception dispatching call to " + ref.getObjID() + " in thread \"" + Thread.currentThread().getName() + "\" at " + new Date() + ":");
        paramThrowable.printStackTrace((PrintStream)localObject1);
      }
    }
  }
  
  public String getRefClass(ObjectOutput paramObjectOutput)
  {
    return "UnicastServerRef";
  }
  
  protected RemoteRef getClientRef()
  {
    return new UnicastRef(ref);
  }
  
  public void writeExternal(ObjectOutput paramObjectOutput)
    throws IOException
  {}
  
  public void readExternal(ObjectInput paramObjectInput)
    throws IOException, ClassNotFoundException
  {
    ref = null;
    skel = null;
  }
  
  private Object[] unmarshalParameters(Object paramObject, Method paramMethod, MarshalInputStream paramMarshalInputStream)
    throws IOException, ClassNotFoundException
  {
    return (paramObject instanceof DeserializationChecker) ? unmarshalParametersChecked((DeserializationChecker)paramObject, paramMethod, paramMarshalInputStream) : unmarshalParametersUnchecked(paramMethod, paramMarshalInputStream);
  }
  
  private Object[] unmarshalParametersUnchecked(Method paramMethod, ObjectInput paramObjectInput)
    throws IOException, ClassNotFoundException
  {
    Class[] arrayOfClass = paramMethod.getParameterTypes();
    Object[] arrayOfObject = new Object[arrayOfClass.length];
    for (int i = 0; i < arrayOfClass.length; i++) {
      arrayOfObject[i] = unmarshalValue(arrayOfClass[i], paramObjectInput);
    }
    return arrayOfObject;
  }
  
  private Object[] unmarshalParametersChecked(DeserializationChecker paramDeserializationChecker, Method paramMethod, MarshalInputStream paramMarshalInputStream)
    throws IOException, ClassNotFoundException
  {
    int i = methodCallIDCount.getAndIncrement();
    MyChecker localMyChecker = new MyChecker(paramDeserializationChecker, paramMethod, i);
    paramMarshalInputStream.setStreamChecker(localMyChecker);
    try
    {
      Class[] arrayOfClass = paramMethod.getParameterTypes();
      Object[] arrayOfObject1 = new Object[arrayOfClass.length];
      for (int j = 0; j < arrayOfClass.length; j++)
      {
        localMyChecker.setIndex(j);
        arrayOfObject1[j] = unmarshalValue(arrayOfClass[j], paramMarshalInputStream);
      }
      localMyChecker.end(i);
      Object[] arrayOfObject2 = arrayOfObject1;
      return arrayOfObject2;
    }
    finally
    {
      paramMarshalInputStream.setStreamChecker(null);
    }
  }
  
  private static class HashToMethod_Maps
    extends WeakClassHashMap<Map<Long, Method>>
  {
    HashToMethod_Maps() {}
    
    protected Map<Long, Method> computeValue(Class<?> paramClass)
    {
      HashMap localHashMap = new HashMap();
      for (Object localObject = paramClass; localObject != null; localObject = ((Class)localObject).getSuperclass()) {
        for (Class localClass : ((Class)localObject).getInterfaces()) {
          if (Remote.class.isAssignableFrom(localClass)) {
            for (Method localMethod1 : localClass.getMethods())
            {
              final Method localMethod2 = localMethod1;
              AccessController.doPrivileged(new PrivilegedAction()
              {
                public Void run()
                {
                  localMethod2.setAccessible(true);
                  return null;
                }
              });
              localHashMap.put(Long.valueOf(Util.computeMethodHash(localMethod2)), localMethod2);
            }
          }
        }
      }
      return localHashMap;
    }
  }
  
  private static class MyChecker
    implements MarshalInputStream.StreamChecker
  {
    private final DeserializationChecker descriptorCheck;
    private final Method method;
    private final int callID;
    private int parameterIndex;
    
    MyChecker(DeserializationChecker paramDeserializationChecker, Method paramMethod, int paramInt)
    {
      descriptorCheck = paramDeserializationChecker;
      method = paramMethod;
      callID = paramInt;
    }
    
    public void validateDescriptor(ObjectStreamClass paramObjectStreamClass)
    {
      descriptorCheck.check(method, paramObjectStreamClass, parameterIndex, callID);
    }
    
    public void checkProxyInterfaceNames(String[] paramArrayOfString)
    {
      descriptorCheck.checkProxyClass(method, paramArrayOfString, parameterIndex, callID);
    }
    
    void setIndex(int paramInt)
    {
      parameterIndex = paramInt;
    }
    
    void end(int paramInt)
    {
      descriptorCheck.end(paramInt);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\rmi\server\UnicastServerRef.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */