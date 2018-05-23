package com.sun.corba.se.spi.activation;

import com.sun.corba.se.spi.activation.RepositoryPackage.ServerDef;
import com.sun.corba.se.spi.activation.RepositoryPackage.ServerDefHelper;
import com.sun.corba.se.spi.activation.RepositoryPackage.StringSeqHelper;
import java.util.Hashtable;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.InvokeHandler;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.ResponseHandler;

public abstract class _RepositoryImplBase
  extends ObjectImpl
  implements Repository, InvokeHandler
{
  private static Hashtable _methods = new Hashtable();
  private static String[] __ids = { "IDL:activation/Repository:1.0" };
  
  public _RepositoryImplBase() {}
  
  public OutputStream _invoke(String paramString, InputStream paramInputStream, ResponseHandler paramResponseHandler)
  {
    OutputStream localOutputStream = null;
    Integer localInteger = (Integer)_methods.get(paramString);
    if (localInteger == null) {
      throw new BAD_OPERATION(0, CompletionStatus.COMPLETED_MAYBE);
    }
    boolean bool;
    Object localObject;
    switch (localInteger.intValue())
    {
    case 0: 
      try
      {
        ServerDef localServerDef1 = ServerDefHelper.read(paramInputStream);
        int i1 = 0;
        i1 = registerServer(localServerDef1);
        localOutputStream = paramResponseHandler.createReply();
        localOutputStream.write_long(i1);
      }
      catch (ServerAlreadyRegistered localServerAlreadyRegistered)
      {
        localOutputStream = paramResponseHandler.createExceptionReply();
        ServerAlreadyRegisteredHelper.write(localOutputStream, localServerAlreadyRegistered);
      }
      catch (BadServerDefinition localBadServerDefinition)
      {
        localOutputStream = paramResponseHandler.createExceptionReply();
        BadServerDefinitionHelper.write(localOutputStream, localBadServerDefinition);
      }
    case 1: 
      try
      {
        int i = ServerIdHelper.read(paramInputStream);
        unregisterServer(i);
        localOutputStream = paramResponseHandler.createReply();
      }
      catch (ServerNotRegistered localServerNotRegistered1)
      {
        localOutputStream = paramResponseHandler.createExceptionReply();
        ServerNotRegisteredHelper.write(localOutputStream, localServerNotRegistered1);
      }
    case 2: 
      try
      {
        int j = ServerIdHelper.read(paramInputStream);
        ServerDef localServerDef2 = null;
        localServerDef2 = getServer(j);
        localOutputStream = paramResponseHandler.createReply();
        ServerDefHelper.write(localOutputStream, localServerDef2);
      }
      catch (ServerNotRegistered localServerNotRegistered2)
      {
        localOutputStream = paramResponseHandler.createExceptionReply();
        ServerNotRegisteredHelper.write(localOutputStream, localServerNotRegistered2);
      }
    case 3: 
      try
      {
        int k = ServerIdHelper.read(paramInputStream);
        bool = false;
        bool = isInstalled(k);
        localOutputStream = paramResponseHandler.createReply();
        localOutputStream.write_boolean(bool);
      }
      catch (ServerNotRegistered localServerNotRegistered3)
      {
        localOutputStream = paramResponseHandler.createExceptionReply();
        ServerNotRegisteredHelper.write(localOutputStream, localServerNotRegistered3);
      }
    case 4: 
      try
      {
        int m = ServerIdHelper.read(paramInputStream);
        install(m);
        localOutputStream = paramResponseHandler.createReply();
      }
      catch (ServerNotRegistered localServerNotRegistered4)
      {
        localOutputStream = paramResponseHandler.createExceptionReply();
        ServerNotRegisteredHelper.write(localOutputStream, localServerNotRegistered4);
      }
      catch (ServerAlreadyInstalled localServerAlreadyInstalled)
      {
        localOutputStream = paramResponseHandler.createExceptionReply();
        ServerAlreadyInstalledHelper.write(localOutputStream, localServerAlreadyInstalled);
      }
    case 5: 
      try
      {
        int n = ServerIdHelper.read(paramInputStream);
        uninstall(n);
        localOutputStream = paramResponseHandler.createReply();
      }
      catch (ServerNotRegistered localServerNotRegistered5)
      {
        localOutputStream = paramResponseHandler.createExceptionReply();
        ServerNotRegisteredHelper.write(localOutputStream, localServerNotRegistered5);
      }
      catch (ServerAlreadyUninstalled localServerAlreadyUninstalled)
      {
        localOutputStream = paramResponseHandler.createExceptionReply();
        ServerAlreadyUninstalledHelper.write(localOutputStream, localServerAlreadyUninstalled);
      }
    case 6: 
      localObject = null;
      localObject = listRegisteredServers();
      localOutputStream = paramResponseHandler.createReply();
      ServerIdsHelper.write(localOutputStream, (int[])localObject);
      break;
    case 7: 
      localObject = null;
      localObject = getApplicationNames();
      localOutputStream = paramResponseHandler.createReply();
      StringSeqHelper.write(localOutputStream, (String[])localObject);
      break;
    case 8: 
      try
      {
        localObject = paramInputStream.read_string();
        bool = false;
        int i2 = getServerID((String)localObject);
        localOutputStream = paramResponseHandler.createReply();
        localOutputStream.write_long(i2);
      }
      catch (ServerNotRegistered localServerNotRegistered6)
      {
        localOutputStream = paramResponseHandler.createExceptionReply();
        ServerNotRegisteredHelper.write(localOutputStream, localServerNotRegistered6);
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
    _methods.put("registerServer", new Integer(0));
    _methods.put("unregisterServer", new Integer(1));
    _methods.put("getServer", new Integer(2));
    _methods.put("isInstalled", new Integer(3));
    _methods.put("install", new Integer(4));
    _methods.put("uninstall", new Integer(5));
    _methods.put("listRegisteredServers", new Integer(6));
    _methods.put("getApplicationNames", new Integer(7));
    _methods.put("getServerID", new Integer(8));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\activation\_RepositoryImplBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */