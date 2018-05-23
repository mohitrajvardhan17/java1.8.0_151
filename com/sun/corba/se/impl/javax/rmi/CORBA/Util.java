package com.sun.corba.se.impl.javax.rmi.CORBA;

import com.sun.corba.se.impl.corba.AnyImpl;
import com.sun.corba.se.impl.io.ValueHandlerImpl;
import com.sun.corba.se.impl.logging.UtilSystemException;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.impl.util.IdentityHashtable;
import com.sun.corba.se.impl.util.JDKBridge;
import com.sun.corba.se.impl.util.Utility;
import com.sun.corba.se.pept.transport.ContactInfoList;
import com.sun.corba.se.spi.copyobject.CopierManager;
import com.sun.corba.se.spi.copyobject.ObjectCopier;
import com.sun.corba.se.spi.copyobject.ObjectCopierFactory;
import com.sun.corba.se.spi.copyobject.ReflectiveCopyException;
import com.sun.corba.se.spi.oa.OAInvocationInfo;
import com.sun.corba.se.spi.orb.ORBVersion;
import com.sun.corba.se.spi.orb.ORBVersionFactory;
import com.sun.corba.se.spi.protocol.CorbaClientDelegate;
import com.sun.corba.se.spi.protocol.LocalClientRequestDispatcher;
import com.sun.corba.se.spi.transport.CorbaContactInfoList;
import java.io.NotSerializableException;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.rmi.AccessException;
import java.rmi.MarshalException;
import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.ServerError;
import java.rmi.ServerException;
import java.rmi.UnexpectedException;
import java.rmi.server.RMIClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.EmptyStackException;
import java.util.Enumeration;
import javax.rmi.CORBA.Stub;
import javax.rmi.CORBA.Tie;
import javax.rmi.CORBA.UtilDelegate;
import javax.rmi.CORBA.ValueHandler;
import javax.transaction.InvalidTransactionException;
import javax.transaction.TransactionRequiredException;
import javax.transaction.TransactionRolledbackException;
import org.omg.CORBA.ACTIVITY_COMPLETED;
import org.omg.CORBA.ACTIVITY_REQUIRED;
import org.omg.CORBA.Any;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.COMM_FAILURE;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.INVALID_ACTIVITY;
import org.omg.CORBA.INVALID_TRANSACTION;
import org.omg.CORBA.INV_OBJREF;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.NO_PERMISSION;
import org.omg.CORBA.OBJECT_NOT_EXIST;
import org.omg.CORBA.OBJ_ADAPTER;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TRANSACTION_REQUIRED;
import org.omg.CORBA.TRANSACTION_ROLLEDBACK;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.UnknownException;
import sun.corba.JavaCorbaAccess;
import sun.corba.SharedSecrets;

public class Util
  implements UtilDelegate
{
  private static KeepAlive keepAlive = null;
  private static IdentityHashtable exportedServants = new IdentityHashtable();
  private static final ValueHandlerImpl valueHandlerSingleton = SharedSecrets.getJavaCorbaAccess().newValueHandlerImpl();
  private UtilSystemException utilWrapper = UtilSystemException.get("rpc.encoding");
  private static Util instance = null;
  
  public Util()
  {
    setInstance(this);
  }
  
  private static void setInstance(Util paramUtil)
  {
    assert (instance == null) : "Instance already defined";
    instance = paramUtil;
  }
  
  public static Util getInstance()
  {
    return instance;
  }
  
  public static boolean isInstanceDefined()
  {
    return instance != null;
  }
  
  public void unregisterTargetsForORB(org.omg.CORBA.ORB paramORB)
  {
    Enumeration localEnumeration = exportedServants.keys();
    while (localEnumeration.hasMoreElements())
    {
      Object localObject = localEnumeration.nextElement();
      Remote localRemote = (Remote)((localObject instanceof Tie) ? ((Tie)localObject).getTarget() : localObject);
      try
      {
        if (paramORB == getTie(localRemote).orb()) {
          try
          {
            unexportObject(localRemote);
          }
          catch (NoSuchObjectException localNoSuchObjectException) {}
        }
      }
      catch (BAD_OPERATION localBAD_OPERATION) {}
    }
  }
  
  public RemoteException mapSystemException(SystemException paramSystemException)
  {
    if ((paramSystemException instanceof UnknownException))
    {
      localObject1 = originalEx;
      if ((localObject1 instanceof Error)) {
        return new ServerError("Error occurred in server thread", (Error)localObject1);
      }
      if ((localObject1 instanceof RemoteException)) {
        return new ServerException("RemoteException occurred in server thread", (Exception)localObject1);
      }
      if ((localObject1 instanceof RuntimeException)) {
        throw ((RuntimeException)localObject1);
      }
    }
    Object localObject1 = paramSystemException.getClass().getName();
    String str1 = ((String)localObject1).substring(((String)localObject1).lastIndexOf('.') + 1);
    String str2;
    switch (completed.value())
    {
    case 0: 
      str2 = "Yes";
      break;
    case 1: 
      str2 = "No";
      break;
    case 2: 
    default: 
      str2 = "Maybe";
    }
    String str3 = "CORBA " + str1 + " " + minor + " " + str2;
    if ((paramSystemException instanceof COMM_FAILURE)) {
      return new MarshalException(str3, paramSystemException);
    }
    Object localObject2;
    if ((paramSystemException instanceof INV_OBJREF))
    {
      localObject2 = new NoSuchObjectException(str3);
      detail = paramSystemException;
      return (RemoteException)localObject2;
    }
    if ((paramSystemException instanceof NO_PERMISSION)) {
      return new AccessException(str3, paramSystemException);
    }
    if ((paramSystemException instanceof MARSHAL)) {
      return new MarshalException(str3, paramSystemException);
    }
    if ((paramSystemException instanceof OBJECT_NOT_EXIST))
    {
      localObject2 = new NoSuchObjectException(str3);
      detail = paramSystemException;
      return (RemoteException)localObject2;
    }
    if ((paramSystemException instanceof TRANSACTION_REQUIRED))
    {
      localObject2 = new TransactionRequiredException(str3);
      detail = paramSystemException;
      return (RemoteException)localObject2;
    }
    if ((paramSystemException instanceof TRANSACTION_ROLLEDBACK))
    {
      localObject2 = new TransactionRolledbackException(str3);
      detail = paramSystemException;
      return (RemoteException)localObject2;
    }
    if ((paramSystemException instanceof INVALID_TRANSACTION))
    {
      localObject2 = new InvalidTransactionException(str3);
      detail = paramSystemException;
      return (RemoteException)localObject2;
    }
    if ((paramSystemException instanceof BAD_PARAM))
    {
      localObject2 = paramSystemException;
      if ((minor == 1398079489) || (minor == 1330446342))
      {
        if (paramSystemException.getMessage() != null) {
          localObject2 = new NotSerializableException(paramSystemException.getMessage());
        } else {
          localObject2 = new NotSerializableException();
        }
        ((Exception)localObject2).initCause(paramSystemException);
      }
      return new MarshalException(str3, (Exception)localObject2);
    }
    Class[] arrayOfClass;
    Constructor localConstructor;
    Object[] arrayOfObject;
    if ((paramSystemException instanceof ACTIVITY_REQUIRED)) {
      try
      {
        localObject2 = SharedSecrets.getJavaCorbaAccess().loadClass("javax.activity.ActivityRequiredException");
        arrayOfClass = new Class[2];
        arrayOfClass[0] = String.class;
        arrayOfClass[1] = Throwable.class;
        localConstructor = ((Class)localObject2).getConstructor(arrayOfClass);
        arrayOfObject = new Object[2];
        arrayOfObject[0] = str3;
        arrayOfObject[1] = paramSystemException;
        return (RemoteException)localConstructor.newInstance(arrayOfObject);
      }
      catch (Throwable localThrowable1)
      {
        utilWrapper.classNotFound(localThrowable1, "javax.activity.ActivityRequiredException");
      }
    } else if ((paramSystemException instanceof ACTIVITY_COMPLETED)) {
      try
      {
        Class localClass1 = SharedSecrets.getJavaCorbaAccess().loadClass("javax.activity.ActivityCompletedException");
        arrayOfClass = new Class[2];
        arrayOfClass[0] = String.class;
        arrayOfClass[1] = Throwable.class;
        localConstructor = localClass1.getConstructor(arrayOfClass);
        arrayOfObject = new Object[2];
        arrayOfObject[0] = str3;
        arrayOfObject[1] = paramSystemException;
        return (RemoteException)localConstructor.newInstance(arrayOfObject);
      }
      catch (Throwable localThrowable2)
      {
        utilWrapper.classNotFound(localThrowable2, "javax.activity.ActivityCompletedException");
      }
    } else if ((paramSystemException instanceof INVALID_ACTIVITY)) {
      try
      {
        Class localClass2 = SharedSecrets.getJavaCorbaAccess().loadClass("javax.activity.InvalidActivityException");
        arrayOfClass = new Class[2];
        arrayOfClass[0] = String.class;
        arrayOfClass[1] = Throwable.class;
        localConstructor = localClass2.getConstructor(arrayOfClass);
        arrayOfObject = new Object[2];
        arrayOfObject[0] = str3;
        arrayOfObject[1] = paramSystemException;
        return (RemoteException)localConstructor.newInstance(arrayOfObject);
      }
      catch (Throwable localThrowable3)
      {
        utilWrapper.classNotFound(localThrowable3, "javax.activity.InvalidActivityException");
      }
    }
    return new RemoteException(str3, paramSystemException);
  }
  
  public void writeAny(org.omg.CORBA.portable.OutputStream paramOutputStream, Object paramObject)
  {
    org.omg.CORBA.ORB localORB = paramOutputStream.orb();
    Any localAny = localORB.create_any();
    Object localObject = Utility.autoConnect(paramObject, localORB, false);
    if ((localObject instanceof org.omg.CORBA.Object))
    {
      localAny.insert_Object((org.omg.CORBA.Object)localObject);
    }
    else if (localObject == null)
    {
      localAny.insert_Value(null, createTypeCodeForNull(localORB));
    }
    else if ((localObject instanceof Serializable))
    {
      TypeCode localTypeCode = createTypeCode((Serializable)localObject, localAny, localORB);
      if (localTypeCode == null) {
        localAny.insert_Value((Serializable)localObject);
      } else {
        localAny.insert_Value((Serializable)localObject, localTypeCode);
      }
    }
    else if ((localObject instanceof Remote))
    {
      ORBUtility.throwNotSerializableForCorba(localObject.getClass().getName());
    }
    else
    {
      ORBUtility.throwNotSerializableForCorba(localObject.getClass().getName());
    }
    paramOutputStream.write_any(localAny);
  }
  
  private TypeCode createTypeCode(Serializable paramSerializable, Any paramAny, org.omg.CORBA.ORB paramORB)
  {
    if (((paramAny instanceof AnyImpl)) && ((paramORB instanceof com.sun.corba.se.spi.orb.ORB)))
    {
      AnyImpl localAnyImpl = (AnyImpl)paramAny;
      com.sun.corba.se.spi.orb.ORB localORB = (com.sun.corba.se.spi.orb.ORB)paramORB;
      return localAnyImpl.createTypeCodeForClass(paramSerializable.getClass(), localORB);
    }
    return null;
  }
  
  private TypeCode createTypeCodeForNull(org.omg.CORBA.ORB paramORB)
  {
    if ((paramORB instanceof com.sun.corba.se.spi.orb.ORB))
    {
      localObject = (com.sun.corba.se.spi.orb.ORB)paramORB;
      if ((!ORBVersionFactory.getFOREIGN().equals(((com.sun.corba.se.spi.orb.ORB)localObject).getORBVersion())) && (ORBVersionFactory.getNEWER().compareTo(((com.sun.corba.se.spi.orb.ORB)localObject).getORBVersion()) > 0)) {
        return paramORB.get_primitive_tc(TCKind.tk_value);
      }
    }
    Object localObject = "IDL:omg.org/CORBA/AbstractBase:1.0";
    return paramORB.create_abstract_interface_tc((String)localObject, "");
  }
  
  public Object readAny(org.omg.CORBA.portable.InputStream paramInputStream)
  {
    Any localAny = paramInputStream.read_any();
    if (localAny.type().kind().value() == 14) {
      return localAny.extract_Object();
    }
    return localAny.extract_Value();
  }
  
  public void writeRemoteObject(org.omg.CORBA.portable.OutputStream paramOutputStream, Object paramObject)
  {
    Object localObject = Utility.autoConnect(paramObject, paramOutputStream.orb(), false);
    paramOutputStream.write_Object((org.omg.CORBA.Object)localObject);
  }
  
  public void writeAbstractObject(org.omg.CORBA.portable.OutputStream paramOutputStream, Object paramObject)
  {
    Object localObject = Utility.autoConnect(paramObject, paramOutputStream.orb(), false);
    ((org.omg.CORBA_2_3.portable.OutputStream)paramOutputStream).write_abstract_interface(localObject);
  }
  
  public void registerTarget(Tie paramTie, Remote paramRemote)
  {
    synchronized (exportedServants)
    {
      if (lookupTie(paramRemote) == null)
      {
        exportedServants.put(paramRemote, paramTie);
        paramTie.setTarget(paramRemote);
        if (keepAlive == null)
        {
          keepAlive = (KeepAlive)AccessController.doPrivileged(new PrivilegedAction()
          {
            public Object run()
            {
              return new KeepAlive();
            }
          });
          keepAlive.start();
        }
      }
    }
  }
  
  public void unexportObject(Remote paramRemote)
    throws NoSuchObjectException
  {
    synchronized (exportedServants)
    {
      Tie localTie = lookupTie(paramRemote);
      if (localTie != null)
      {
        exportedServants.remove(paramRemote);
        Utility.purgeStubForTie(localTie);
        Utility.purgeTieAndServant(localTie);
        try
        {
          cleanUpTie(localTie);
        }
        catch (BAD_OPERATION localBAD_OPERATION) {}catch (OBJ_ADAPTER localOBJ_ADAPTER) {}
        if (exportedServants.isEmpty())
        {
          keepAlive.quit();
          keepAlive = null;
        }
      }
      else
      {
        throw new NoSuchObjectException("Tie not found");
      }
    }
  }
  
  protected void cleanUpTie(Tie paramTie)
    throws NoSuchObjectException
  {
    paramTie.setTarget(null);
    paramTie.deactivate();
  }
  
  /* Error */
  public Tie getTie(Remote paramRemote)
  {
    // Byte code:
    //   0: getstatic 510	com/sun/corba/se/impl/javax/rmi/CORBA/Util:exportedServants	Lcom/sun/corba/se/impl/util/IdentityHashtable;
    //   3: dup
    //   4: astore_2
    //   5: monitorenter
    //   6: aload_1
    //   7: invokestatic 524	com/sun/corba/se/impl/javax/rmi/CORBA/Util:lookupTie	(Ljava/rmi/Remote;)Ljavax/rmi/CORBA/Tie;
    //   10: aload_2
    //   11: monitorexit
    //   12: areturn
    //   13: astore_3
    //   14: aload_2
    //   15: monitorexit
    //   16: aload_3
    //   17: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	18	0	this	Util
    //   0	18	1	paramRemote	Remote
    //   4	11	2	Ljava/lang/Object;	Object
    //   13	4	3	localObject1	Object
    // Exception table:
    //   from	to	target	type
    //   6	12	13	finally
    //   13	16	13	finally
  }
  
  private static Tie lookupTie(Remote paramRemote)
  {
    Tie localTie = (Tie)exportedServants.get(paramRemote);
    if ((localTie == null) && ((paramRemote instanceof Tie)) && (exportedServants.contains(paramRemote))) {
      localTie = (Tie)paramRemote;
    }
    return localTie;
  }
  
  public ValueHandler createValueHandler()
  {
    return valueHandlerSingleton;
  }
  
  public String getCodebase(Class paramClass)
  {
    return RMIClassLoader.getClassAnnotation(paramClass);
  }
  
  public Class loadClass(String paramString1, String paramString2, ClassLoader paramClassLoader)
    throws ClassNotFoundException
  {
    return JDKBridge.loadClass(paramString1, paramString2, paramClassLoader);
  }
  
  public boolean isLocal(Stub paramStub)
    throws RemoteException
  {
    boolean bool = false;
    try
    {
      Delegate localDelegate = paramStub._get_delegate();
      if ((localDelegate instanceof CorbaClientDelegate))
      {
        CorbaClientDelegate localCorbaClientDelegate = (CorbaClientDelegate)localDelegate;
        ContactInfoList localContactInfoList = localCorbaClientDelegate.getContactInfoList();
        if ((localContactInfoList instanceof CorbaContactInfoList))
        {
          CorbaContactInfoList localCorbaContactInfoList = (CorbaContactInfoList)localContactInfoList;
          LocalClientRequestDispatcher localLocalClientRequestDispatcher = localCorbaContactInfoList.getLocalClientRequestDispatcher();
          bool = localLocalClientRequestDispatcher.useLocalInvocation(null);
        }
      }
      else
      {
        bool = localDelegate.is_local(paramStub);
      }
    }
    catch (SystemException localSystemException)
    {
      throw javax.rmi.CORBA.Util.mapSystemException(localSystemException);
    }
    return bool;
  }
  
  public RemoteException wrapException(Throwable paramThrowable)
  {
    if ((paramThrowable instanceof SystemException)) {
      return mapSystemException((SystemException)paramThrowable);
    }
    if ((paramThrowable instanceof Error)) {
      return new ServerError("Error occurred in server thread", (Error)paramThrowable);
    }
    if ((paramThrowable instanceof RemoteException)) {
      return new ServerException("RemoteException occurred in server thread", (Exception)paramThrowable);
    }
    if ((paramThrowable instanceof RuntimeException)) {
      throw ((RuntimeException)paramThrowable);
    }
    if ((paramThrowable instanceof Exception)) {
      return new UnexpectedException(paramThrowable.toString(), (Exception)paramThrowable);
    }
    return new UnexpectedException(paramThrowable.toString());
  }
  
  public Object[] copyObjects(Object[] paramArrayOfObject, org.omg.CORBA.ORB paramORB)
    throws RemoteException
  {
    if (paramArrayOfObject == null) {
      throw new NullPointerException();
    }
    Class localClass = paramArrayOfObject.getClass().getComponentType();
    if ((Remote.class.isAssignableFrom(localClass)) && (!localClass.isInterface()))
    {
      Remote[] arrayOfRemote = new Remote[paramArrayOfObject.length];
      System.arraycopy(paramArrayOfObject, 0, arrayOfRemote, 0, paramArrayOfObject.length);
      return (Object[])copyObject(arrayOfRemote, paramORB);
    }
    return (Object[])copyObject(paramArrayOfObject, paramORB);
  }
  
  public Object copyObject(Object paramObject, org.omg.CORBA.ORB paramORB)
    throws RemoteException
  {
    if ((paramORB instanceof com.sun.corba.se.spi.orb.ORB))
    {
      localObject1 = (com.sun.corba.se.spi.orb.ORB)paramORB;
      try
      {
        return ((com.sun.corba.se.spi.orb.ORB)localObject1).peekInvocationInfo().getCopierFactory().make().copy(paramObject);
      }
      catch (EmptyStackException localEmptyStackException)
      {
        localObject2 = ((com.sun.corba.se.spi.orb.ORB)localObject1).getCopierManager();
        ObjectCopier localObjectCopier = ((CopierManager)localObject2).getDefaultObjectCopierFactory().make();
        return localObjectCopier.copy(paramObject);
      }
      catch (ReflectiveCopyException localReflectiveCopyException)
      {
        Object localObject2 = new RemoteException();
        ((RemoteException)localObject2).initCause(localReflectiveCopyException);
        throw ((Throwable)localObject2);
      }
    }
    Object localObject1 = (org.omg.CORBA_2_3.portable.OutputStream)paramORB.create_output_stream();
    ((org.omg.CORBA_2_3.portable.OutputStream)localObject1).write_value((Serializable)paramObject);
    org.omg.CORBA_2_3.portable.InputStream localInputStream = (org.omg.CORBA_2_3.portable.InputStream)((org.omg.CORBA_2_3.portable.OutputStream)localObject1).create_input_stream();
    return localInputStream.read_value();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\javax\rmi\CORBA\Util.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */