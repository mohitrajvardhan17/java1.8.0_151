package com.sun.corba.se.spi.activation;

import java.util.Hashtable;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.InvokeHandler;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.ResponseHandler;

public abstract class _ActivatorImplBase
  extends ObjectImpl
  implements Activator, InvokeHandler
{
  private static Hashtable _methods = new Hashtable();
  private static String[] __ids = { "IDL:activation/Activator:1.0" };
  
  public _ActivatorImplBase() {}
  
  public OutputStream _invoke(String paramString, InputStream paramInputStream, ResponseHandler paramResponseHandler)
  {
    OutputStream localOutputStream = null;
    Integer localInteger = (Integer)_methods.get(paramString);
    if (localInteger == null) {
      throw new BAD_OPERATION(0, CompletionStatus.COMPLETED_MAYBE);
    }
    Object localObject;
    switch (localInteger.intValue())
    {
    case 0: 
      try
      {
        int i = ServerIdHelper.read(paramInputStream);
        localObject = ServerHelper.read(paramInputStream);
        active(i, (Server)localObject);
        localOutputStream = paramResponseHandler.createReply();
      }
      catch (ServerNotRegistered localServerNotRegistered1)
      {
        localOutputStream = paramResponseHandler.createExceptionReply();
        ServerNotRegisteredHelper.write(localOutputStream, localServerNotRegistered1);
      }
    case 1: 
      try
      {
        int j = ServerIdHelper.read(paramInputStream);
        localObject = ORBidHelper.read(paramInputStream);
        EndPointInfo[] arrayOfEndPointInfo = EndpointInfoListHelper.read(paramInputStream);
        registerEndpoints(j, (String)localObject, arrayOfEndPointInfo);
        localOutputStream = paramResponseHandler.createReply();
      }
      catch (ServerNotRegistered localServerNotRegistered2)
      {
        localOutputStream = paramResponseHandler.createExceptionReply();
        ServerNotRegisteredHelper.write(localOutputStream, localServerNotRegistered2);
      }
      catch (NoSuchEndPoint localNoSuchEndPoint)
      {
        localOutputStream = paramResponseHandler.createExceptionReply();
        NoSuchEndPointHelper.write(localOutputStream, localNoSuchEndPoint);
      }
      catch (ORBAlreadyRegistered localORBAlreadyRegistered)
      {
        localOutputStream = paramResponseHandler.createExceptionReply();
        ORBAlreadyRegisteredHelper.write(localOutputStream, localORBAlreadyRegistered);
      }
    case 2: 
      int[] arrayOfInt = null;
      arrayOfInt = getActiveServers();
      localOutputStream = paramResponseHandler.createReply();
      ServerIdsHelper.write(localOutputStream, arrayOfInt);
      break;
    case 3: 
      try
      {
        int k = ServerIdHelper.read(paramInputStream);
        activate(k);
        localOutputStream = paramResponseHandler.createReply();
      }
      catch (ServerAlreadyActive localServerAlreadyActive)
      {
        localOutputStream = paramResponseHandler.createExceptionReply();
        ServerAlreadyActiveHelper.write(localOutputStream, localServerAlreadyActive);
      }
      catch (ServerNotRegistered localServerNotRegistered3)
      {
        localOutputStream = paramResponseHandler.createExceptionReply();
        ServerNotRegisteredHelper.write(localOutputStream, localServerNotRegistered3);
      }
      catch (ServerHeldDown localServerHeldDown1)
      {
        localOutputStream = paramResponseHandler.createExceptionReply();
        ServerHeldDownHelper.write(localOutputStream, localServerHeldDown1);
      }
    case 4: 
      try
      {
        int m = ServerIdHelper.read(paramInputStream);
        shutdown(m);
        localOutputStream = paramResponseHandler.createReply();
      }
      catch (ServerNotActive localServerNotActive)
      {
        localOutputStream = paramResponseHandler.createExceptionReply();
        ServerNotActiveHelper.write(localOutputStream, localServerNotActive);
      }
      catch (ServerNotRegistered localServerNotRegistered4)
      {
        localOutputStream = paramResponseHandler.createExceptionReply();
        ServerNotRegisteredHelper.write(localOutputStream, localServerNotRegistered4);
      }
    case 5: 
      try
      {
        int n = ServerIdHelper.read(paramInputStream);
        install(n);
        localOutputStream = paramResponseHandler.createReply();
      }
      catch (ServerNotRegistered localServerNotRegistered5)
      {
        localOutputStream = paramResponseHandler.createExceptionReply();
        ServerNotRegisteredHelper.write(localOutputStream, localServerNotRegistered5);
      }
      catch (ServerHeldDown localServerHeldDown2)
      {
        localOutputStream = paramResponseHandler.createExceptionReply();
        ServerHeldDownHelper.write(localOutputStream, localServerHeldDown2);
      }
      catch (ServerAlreadyInstalled localServerAlreadyInstalled)
      {
        localOutputStream = paramResponseHandler.createExceptionReply();
        ServerAlreadyInstalledHelper.write(localOutputStream, localServerAlreadyInstalled);
      }
    case 6: 
      try
      {
        int i1 = ServerIdHelper.read(paramInputStream);
        localObject = null;
        localObject = getORBNames(i1);
        localOutputStream = paramResponseHandler.createReply();
        ORBidListHelper.write(localOutputStream, (String[])localObject);
      }
      catch (ServerNotRegistered localServerNotRegistered6)
      {
        localOutputStream = paramResponseHandler.createExceptionReply();
        ServerNotRegisteredHelper.write(localOutputStream, localServerNotRegistered6);
      }
    case 7: 
      try
      {
        int i2 = ServerIdHelper.read(paramInputStream);
        uninstall(i2);
        localOutputStream = paramResponseHandler.createReply();
      }
      catch (ServerNotRegistered localServerNotRegistered7)
      {
        localOutputStream = paramResponseHandler.createExceptionReply();
        ServerNotRegisteredHelper.write(localOutputStream, localServerNotRegistered7);
      }
      catch (ServerHeldDown localServerHeldDown3)
      {
        localOutputStream = paramResponseHandler.createExceptionReply();
        ServerHeldDownHelper.write(localOutputStream, localServerHeldDown3);
      }
      catch (ServerAlreadyUninstalled localServerAlreadyUninstalled)
      {
        localOutputStream = paramResponseHandler.createExceptionReply();
        ServerAlreadyUninstalledHelper.write(localOutputStream, localServerAlreadyUninstalled);
      }
    default: 
      throw new BAD_OPERATION(0, CompletionStatus.COMPLETED_MAYBE);
    }
    return localOutputStream;
  }
  
  public String[] _ids()
  {
    return (String[])__ids.clone();
  }
  
  static
  {
    _methods.put("active", new Integer(0));
    _methods.put("registerEndpoints", new Integer(1));
    _methods.put("getActiveServers", new Integer(2));
    _methods.put("activate", new Integer(3));
    _methods.put("shutdown", new Integer(4));
    _methods.put("install", new Integer(5));
    _methods.put("getORBNames", new Integer(6));
    _methods.put("uninstall", new Integer(7));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\activation\_ActivatorImplBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */