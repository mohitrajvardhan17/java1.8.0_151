package com.sun.corba.se.spi.activation;

import com.sun.corba.se.spi.activation.LocatorPackage.ServerLocation;
import com.sun.corba.se.spi.activation.LocatorPackage.ServerLocationHelper;
import com.sun.corba.se.spi.activation.LocatorPackage.ServerLocationPerORB;
import com.sun.corba.se.spi.activation.LocatorPackage.ServerLocationPerORBHelper;
import java.util.Hashtable;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.InvokeHandler;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.ResponseHandler;

public abstract class _LocatorImplBase
  extends ObjectImpl
  implements Locator, InvokeHandler
{
  private static Hashtable _methods = new Hashtable();
  private static String[] __ids = { "IDL:activation/Locator:1.0" };
  
  public _LocatorImplBase() {}
  
  public OutputStream _invoke(String paramString, InputStream paramInputStream, ResponseHandler paramResponseHandler)
  {
    OutputStream localOutputStream = null;
    Integer localInteger = (Integer)_methods.get(paramString);
    if (localInteger == null) {
      throw new BAD_OPERATION(0, CompletionStatus.COMPLETED_MAYBE);
    }
    String str2;
    Object localObject;
    switch (localInteger.intValue())
    {
    case 0: 
      try
      {
        int i = ServerIdHelper.read(paramInputStream);
        str2 = paramInputStream.read_string();
        localObject = null;
        localObject = locateServer(i, str2);
        localOutputStream = paramResponseHandler.createReply();
        ServerLocationHelper.write(localOutputStream, (ServerLocation)localObject);
      }
      catch (NoSuchEndPoint localNoSuchEndPoint1)
      {
        localOutputStream = paramResponseHandler.createExceptionReply();
        NoSuchEndPointHelper.write(localOutputStream, localNoSuchEndPoint1);
      }
      catch (ServerNotRegistered localServerNotRegistered1)
      {
        localOutputStream = paramResponseHandler.createExceptionReply();
        ServerNotRegisteredHelper.write(localOutputStream, localServerNotRegistered1);
      }
      catch (ServerHeldDown localServerHeldDown1)
      {
        localOutputStream = paramResponseHandler.createExceptionReply();
        ServerHeldDownHelper.write(localOutputStream, localServerHeldDown1);
      }
    case 1: 
      try
      {
        int j = ServerIdHelper.read(paramInputStream);
        str2 = ORBidHelper.read(paramInputStream);
        localObject = null;
        localObject = locateServerForORB(j, str2);
        localOutputStream = paramResponseHandler.createReply();
        ServerLocationPerORBHelper.write(localOutputStream, (ServerLocationPerORB)localObject);
      }
      catch (InvalidORBid localInvalidORBid)
      {
        localOutputStream = paramResponseHandler.createExceptionReply();
        InvalidORBidHelper.write(localOutputStream, localInvalidORBid);
      }
      catch (ServerNotRegistered localServerNotRegistered2)
      {
        localOutputStream = paramResponseHandler.createExceptionReply();
        ServerNotRegisteredHelper.write(localOutputStream, localServerNotRegistered2);
      }
      catch (ServerHeldDown localServerHeldDown2)
      {
        localOutputStream = paramResponseHandler.createExceptionReply();
        ServerHeldDownHelper.write(localOutputStream, localServerHeldDown2);
      }
    case 2: 
      try
      {
        String str1 = paramInputStream.read_string();
        int k = 0;
        k = getEndpoint(str1);
        localOutputStream = paramResponseHandler.createReply();
        localOutputStream.write_long(k);
      }
      catch (NoSuchEndPoint localNoSuchEndPoint2)
      {
        localOutputStream = paramResponseHandler.createExceptionReply();
        NoSuchEndPointHelper.write(localOutputStream, localNoSuchEndPoint2);
      }
    case 3: 
      try
      {
        ServerLocationPerORB localServerLocationPerORB = ServerLocationPerORBHelper.read(paramInputStream);
        String str3 = paramInputStream.read_string();
        int m = 0;
        m = getServerPortForType(localServerLocationPerORB, str3);
        localOutputStream = paramResponseHandler.createReply();
        localOutputStream.write_long(m);
      }
      catch (NoSuchEndPoint localNoSuchEndPoint3)
      {
        localOutputStream = paramResponseHandler.createExceptionReply();
        NoSuchEndPointHelper.write(localOutputStream, localNoSuchEndPoint3);
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
    _methods.put("locateServer", new Integer(0));
    _methods.put("locateServerForORB", new Integer(1));
    _methods.put("getEndpoint", new Integer(2));
    _methods.put("getServerPortForType", new Integer(3));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\activation\_LocatorImplBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */