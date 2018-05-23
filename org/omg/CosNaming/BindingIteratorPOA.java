package org.omg.CosNaming;

import java.util.Hashtable;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.InvokeHandler;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.ResponseHandler;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.Servant;

public abstract class BindingIteratorPOA
  extends Servant
  implements BindingIteratorOperations, InvokeHandler
{
  private static Hashtable _methods = new Hashtable();
  private static String[] __ids = { "IDL:omg.org/CosNaming/BindingIterator:1.0" };
  
  public BindingIteratorPOA() {}
  
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
      BindingHolder localBindingHolder = new BindingHolder();
      boolean bool1 = false;
      bool1 = next_one(localBindingHolder);
      localOutputStream = paramResponseHandler.createReply();
      localOutputStream.write_boolean(bool1);
      BindingHelper.write(localOutputStream, value);
      break;
    case 1: 
      int i = paramInputStream.read_ulong();
      BindingListHolder localBindingListHolder = new BindingListHolder();
      boolean bool2 = false;
      bool2 = next_n(i, localBindingListHolder);
      localOutputStream = paramResponseHandler.createReply();
      localOutputStream.write_boolean(bool2);
      BindingListHelper.write(localOutputStream, value);
      break;
    case 2: 
      destroy();
      localOutputStream = paramResponseHandler.createReply();
      break;
    default: 
      throw new BAD_OPERATION(0, CompletionStatus.COMPLETED_MAYBE);
    }
    return localOutputStream;
  }
  
  public String[] _all_interfaces(POA paramPOA, byte[] paramArrayOfByte)
  {
    return (String[])__ids.clone();
  }
  
  public BindingIterator _this()
  {
    return BindingIteratorHelper.narrow(super._this_object());
  }
  
  public BindingIterator _this(ORB paramORB)
  {
    return BindingIteratorHelper.narrow(super._this_object(paramORB));
  }
  
  static
  {
    _methods.put("next_one", new Integer(0));
    _methods.put("next_n", new Integer(1));
    _methods.put("destroy", new Integer(2));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CosNaming\BindingIteratorPOA.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */