package com.sun.corba.se.spi.activation;

import com.sun.corba.se.spi.activation.InitialNameServicePackage.NameAlreadyBound;
import com.sun.corba.se.spi.activation.InitialNameServicePackage.NameAlreadyBoundHelper;
import java.util.Hashtable;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.Object;
import org.omg.CORBA.ObjectHelper;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.InvokeHandler;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.ResponseHandler;

public abstract class _InitialNameServiceImplBase
  extends ObjectImpl
  implements InitialNameService, InvokeHandler
{
  private static Hashtable _methods = new Hashtable();
  private static String[] __ids = { "IDL:activation/InitialNameService:1.0" };
  
  public _InitialNameServiceImplBase() {}
  
  public OutputStream _invoke(String paramString, InputStream paramInputStream, ResponseHandler paramResponseHandler)
  {
    OutputStream localOutputStream = null;
    Integer localInteger = (Integer)_methods.get(paramString);
    if (localInteger == null) {
      throw new BAD_OPERATION(0, CompletionStatus.COMPLETED_MAYBE);
    }
    switch (localInteger.intValue())
    {
    case 0: 
      try
      {
        String str = paramInputStream.read_string();
        Object localObject = ObjectHelper.read(paramInputStream);
        boolean bool = paramInputStream.read_boolean();
        bind(str, localObject, bool);
        localOutputStream = paramResponseHandler.createReply();
      }
      catch (NameAlreadyBound localNameAlreadyBound)
      {
        localOutputStream = paramResponseHandler.createExceptionReply();
        NameAlreadyBoundHelper.write(localOutputStream, localNameAlreadyBound);
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
    _methods.put("bind", new Integer(0));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\activation\_InitialNameServiceImplBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */