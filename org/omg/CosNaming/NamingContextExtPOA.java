package org.omg.CosNaming;

import java.util.Hashtable;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.ORB;
import org.omg.CORBA.ObjectHelper;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.InvokeHandler;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.ResponseHandler;
import org.omg.CosNaming.NamingContextExtPackage.AddressHelper;
import org.omg.CosNaming.NamingContextExtPackage.InvalidAddress;
import org.omg.CosNaming.NamingContextExtPackage.InvalidAddressHelper;
import org.omg.CosNaming.NamingContextExtPackage.StringNameHelper;
import org.omg.CosNaming.NamingContextPackage.AlreadyBound;
import org.omg.CosNaming.NamingContextPackage.AlreadyBoundHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.CannotProceedHelper;
import org.omg.CosNaming.NamingContextPackage.InvalidName;
import org.omg.CosNaming.NamingContextPackage.InvalidNameHelper;
import org.omg.CosNaming.NamingContextPackage.NotEmpty;
import org.omg.CosNaming.NamingContextPackage.NotEmptyHelper;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.omg.CosNaming.NamingContextPackage.NotFoundHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.Servant;

public abstract class NamingContextExtPOA
  extends Servant
  implements NamingContextExtOperations, InvokeHandler
{
  private static Hashtable _methods = new Hashtable();
  private static String[] __ids = { "IDL:omg.org/CosNaming/NamingContextExt:1.0", "IDL:omg.org/CosNaming/NamingContext:1.0" };
  
  public NamingContextExtPOA() {}
  
  public OutputStream _invoke(String paramString, InputStream paramInputStream, ResponseHandler paramResponseHandler)
  {
    OutputStream localOutputStream = null;
    Integer localInteger = (Integer)_methods.get(paramString);
    if (localInteger == null) {
      throw new BAD_OPERATION(0, CompletionStatus.COMPLETED_MAYBE);
    }
    Object localObject2;
    Object localObject3;
    Object localObject1;
    switch (localInteger.intValue())
    {
    case 0: 
      try
      {
        NameComponent[] arrayOfNameComponent1 = NameHelper.read(paramInputStream);
        localObject2 = null;
        localObject2 = to_string(arrayOfNameComponent1);
        localOutputStream = paramResponseHandler.createReply();
        localOutputStream.write_string((String)localObject2);
      }
      catch (InvalidName localInvalidName1)
      {
        localOutputStream = paramResponseHandler.createExceptionReply();
        InvalidNameHelper.write(localOutputStream, localInvalidName1);
      }
    case 1: 
      try
      {
        String str1 = StringNameHelper.read(paramInputStream);
        localObject2 = null;
        localObject2 = to_name(str1);
        localOutputStream = paramResponseHandler.createReply();
        NameHelper.write(localOutputStream, (NameComponent[])localObject2);
      }
      catch (InvalidName localInvalidName2)
      {
        localOutputStream = paramResponseHandler.createExceptionReply();
        InvalidNameHelper.write(localOutputStream, localInvalidName2);
      }
    case 2: 
      try
      {
        String str2 = AddressHelper.read(paramInputStream);
        localObject2 = StringNameHelper.read(paramInputStream);
        localObject3 = null;
        localObject3 = to_url(str2, (String)localObject2);
        localOutputStream = paramResponseHandler.createReply();
        localOutputStream.write_string((String)localObject3);
      }
      catch (InvalidAddress localInvalidAddress)
      {
        localOutputStream = paramResponseHandler.createExceptionReply();
        InvalidAddressHelper.write(localOutputStream, localInvalidAddress);
      }
      catch (InvalidName localInvalidName3)
      {
        localOutputStream = paramResponseHandler.createExceptionReply();
        InvalidNameHelper.write(localOutputStream, localInvalidName3);
      }
    case 3: 
      try
      {
        String str3 = StringNameHelper.read(paramInputStream);
        localObject2 = null;
        localObject2 = resolve_str(str3);
        localOutputStream = paramResponseHandler.createReply();
        ObjectHelper.write(localOutputStream, (org.omg.CORBA.Object)localObject2);
      }
      catch (NotFound localNotFound1)
      {
        localOutputStream = paramResponseHandler.createExceptionReply();
        NotFoundHelper.write(localOutputStream, localNotFound1);
      }
      catch (CannotProceed localCannotProceed1)
      {
        localOutputStream = paramResponseHandler.createExceptionReply();
        CannotProceedHelper.write(localOutputStream, localCannotProceed1);
      }
      catch (InvalidName localInvalidName4)
      {
        localOutputStream = paramResponseHandler.createExceptionReply();
        InvalidNameHelper.write(localOutputStream, localInvalidName4);
      }
    case 4: 
      try
      {
        NameComponent[] arrayOfNameComponent2 = NameHelper.read(paramInputStream);
        localObject2 = ObjectHelper.read(paramInputStream);
        bind(arrayOfNameComponent2, (org.omg.CORBA.Object)localObject2);
        localOutputStream = paramResponseHandler.createReply();
      }
      catch (NotFound localNotFound2)
      {
        localOutputStream = paramResponseHandler.createExceptionReply();
        NotFoundHelper.write(localOutputStream, localNotFound2);
      }
      catch (CannotProceed localCannotProceed2)
      {
        localOutputStream = paramResponseHandler.createExceptionReply();
        CannotProceedHelper.write(localOutputStream, localCannotProceed2);
      }
      catch (InvalidName localInvalidName5)
      {
        localOutputStream = paramResponseHandler.createExceptionReply();
        InvalidNameHelper.write(localOutputStream, localInvalidName5);
      }
      catch (AlreadyBound localAlreadyBound1)
      {
        localOutputStream = paramResponseHandler.createExceptionReply();
        AlreadyBoundHelper.write(localOutputStream, localAlreadyBound1);
      }
    case 5: 
      try
      {
        NameComponent[] arrayOfNameComponent3 = NameHelper.read(paramInputStream);
        localObject2 = NamingContextHelper.read(paramInputStream);
        bind_context(arrayOfNameComponent3, (NamingContext)localObject2);
        localOutputStream = paramResponseHandler.createReply();
      }
      catch (NotFound localNotFound3)
      {
        localOutputStream = paramResponseHandler.createExceptionReply();
        NotFoundHelper.write(localOutputStream, localNotFound3);
      }
      catch (CannotProceed localCannotProceed3)
      {
        localOutputStream = paramResponseHandler.createExceptionReply();
        CannotProceedHelper.write(localOutputStream, localCannotProceed3);
      }
      catch (InvalidName localInvalidName6)
      {
        localOutputStream = paramResponseHandler.createExceptionReply();
        InvalidNameHelper.write(localOutputStream, localInvalidName6);
      }
      catch (AlreadyBound localAlreadyBound2)
      {
        localOutputStream = paramResponseHandler.createExceptionReply();
        AlreadyBoundHelper.write(localOutputStream, localAlreadyBound2);
      }
    case 6: 
      try
      {
        NameComponent[] arrayOfNameComponent4 = NameHelper.read(paramInputStream);
        localObject2 = ObjectHelper.read(paramInputStream);
        rebind(arrayOfNameComponent4, (org.omg.CORBA.Object)localObject2);
        localOutputStream = paramResponseHandler.createReply();
      }
      catch (NotFound localNotFound4)
      {
        localOutputStream = paramResponseHandler.createExceptionReply();
        NotFoundHelper.write(localOutputStream, localNotFound4);
      }
      catch (CannotProceed localCannotProceed4)
      {
        localOutputStream = paramResponseHandler.createExceptionReply();
        CannotProceedHelper.write(localOutputStream, localCannotProceed4);
      }
      catch (InvalidName localInvalidName7)
      {
        localOutputStream = paramResponseHandler.createExceptionReply();
        InvalidNameHelper.write(localOutputStream, localInvalidName7);
      }
    case 7: 
      try
      {
        NameComponent[] arrayOfNameComponent5 = NameHelper.read(paramInputStream);
        localObject2 = NamingContextHelper.read(paramInputStream);
        rebind_context(arrayOfNameComponent5, (NamingContext)localObject2);
        localOutputStream = paramResponseHandler.createReply();
      }
      catch (NotFound localNotFound5)
      {
        localOutputStream = paramResponseHandler.createExceptionReply();
        NotFoundHelper.write(localOutputStream, localNotFound5);
      }
      catch (CannotProceed localCannotProceed5)
      {
        localOutputStream = paramResponseHandler.createExceptionReply();
        CannotProceedHelper.write(localOutputStream, localCannotProceed5);
      }
      catch (InvalidName localInvalidName8)
      {
        localOutputStream = paramResponseHandler.createExceptionReply();
        InvalidNameHelper.write(localOutputStream, localInvalidName8);
      }
    case 8: 
      try
      {
        NameComponent[] arrayOfNameComponent6 = NameHelper.read(paramInputStream);
        localObject2 = null;
        localObject2 = resolve(arrayOfNameComponent6);
        localOutputStream = paramResponseHandler.createReply();
        ObjectHelper.write(localOutputStream, (org.omg.CORBA.Object)localObject2);
      }
      catch (NotFound localNotFound6)
      {
        localOutputStream = paramResponseHandler.createExceptionReply();
        NotFoundHelper.write(localOutputStream, localNotFound6);
      }
      catch (CannotProceed localCannotProceed6)
      {
        localOutputStream = paramResponseHandler.createExceptionReply();
        CannotProceedHelper.write(localOutputStream, localCannotProceed6);
      }
      catch (InvalidName localInvalidName9)
      {
        localOutputStream = paramResponseHandler.createExceptionReply();
        InvalidNameHelper.write(localOutputStream, localInvalidName9);
      }
    case 9: 
      try
      {
        NameComponent[] arrayOfNameComponent7 = NameHelper.read(paramInputStream);
        unbind(arrayOfNameComponent7);
        localOutputStream = paramResponseHandler.createReply();
      }
      catch (NotFound localNotFound7)
      {
        localOutputStream = paramResponseHandler.createExceptionReply();
        NotFoundHelper.write(localOutputStream, localNotFound7);
      }
      catch (CannotProceed localCannotProceed7)
      {
        localOutputStream = paramResponseHandler.createExceptionReply();
        CannotProceedHelper.write(localOutputStream, localCannotProceed7);
      }
      catch (InvalidName localInvalidName10)
      {
        localOutputStream = paramResponseHandler.createExceptionReply();
        InvalidNameHelper.write(localOutputStream, localInvalidName10);
      }
    case 10: 
      int i = paramInputStream.read_ulong();
      localObject2 = new BindingListHolder();
      localObject3 = new BindingIteratorHolder();
      list(i, (BindingListHolder)localObject2, (BindingIteratorHolder)localObject3);
      localOutputStream = paramResponseHandler.createReply();
      BindingListHelper.write(localOutputStream, value);
      BindingIteratorHelper.write(localOutputStream, value);
      break;
    case 11: 
      localObject1 = null;
      localObject1 = new_context();
      localOutputStream = paramResponseHandler.createReply();
      NamingContextHelper.write(localOutputStream, (NamingContext)localObject1);
      break;
    case 12: 
      try
      {
        localObject1 = NameHelper.read(paramInputStream);
        localObject2 = null;
        localObject2 = bind_new_context((NameComponent[])localObject1);
        localOutputStream = paramResponseHandler.createReply();
        NamingContextHelper.write(localOutputStream, (NamingContext)localObject2);
      }
      catch (NotFound localNotFound8)
      {
        localOutputStream = paramResponseHandler.createExceptionReply();
        NotFoundHelper.write(localOutputStream, localNotFound8);
      }
      catch (AlreadyBound localAlreadyBound3)
      {
        localOutputStream = paramResponseHandler.createExceptionReply();
        AlreadyBoundHelper.write(localOutputStream, localAlreadyBound3);
      }
      catch (CannotProceed localCannotProceed8)
      {
        localOutputStream = paramResponseHandler.createExceptionReply();
        CannotProceedHelper.write(localOutputStream, localCannotProceed8);
      }
      catch (InvalidName localInvalidName11)
      {
        localOutputStream = paramResponseHandler.createExceptionReply();
        InvalidNameHelper.write(localOutputStream, localInvalidName11);
      }
    case 13: 
      try
      {
        destroy();
        localOutputStream = paramResponseHandler.createReply();
      }
      catch (NotEmpty localNotEmpty)
      {
        localOutputStream = paramResponseHandler.createExceptionReply();
        NotEmptyHelper.write(localOutputStream, localNotEmpty);
      }
    default: 
      throw new BAD_OPERATION(0, CompletionStatus.COMPLETED_MAYBE);
    }
    return localOutputStream;
  }
  
  public String[] _all_interfaces(POA paramPOA, byte[] paramArrayOfByte)
  {
    return (String[])__ids.clone();
  }
  
  public NamingContextExt _this()
  {
    return NamingContextExtHelper.narrow(super._this_object());
  }
  
  public NamingContextExt _this(ORB paramORB)
  {
    return NamingContextExtHelper.narrow(super._this_object(paramORB));
  }
  
  static
  {
    _methods.put("to_string", new Integer(0));
    _methods.put("to_name", new Integer(1));
    _methods.put("to_url", new Integer(2));
    _methods.put("resolve_str", new Integer(3));
    _methods.put("bind", new Integer(4));
    _methods.put("bind_context", new Integer(5));
    _methods.put("rebind", new Integer(6));
    _methods.put("rebind_context", new Integer(7));
    _methods.put("resolve", new Integer(8));
    _methods.put("unbind", new Integer(9));
    _methods.put("list", new Integer(10));
    _methods.put("new_context", new Integer(11));
    _methods.put("bind_new_context", new Integer(12));
    _methods.put("destroy", new Integer(13));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CosNaming\NamingContextExtPOA.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */