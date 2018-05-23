package com.sun.corba.se.spi.activation;

import org.omg.CORBA.Any;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.portable.OutputStream;

public abstract class ActivatorHelper
{
  private static String _id = "IDL:activation/Activator:1.0";
  private static TypeCode __typeCode = null;
  
  public ActivatorHelper() {}
  
  public static void insert(Any paramAny, Activator paramActivator)
  {
    OutputStream localOutputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(localOutputStream, paramActivator);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static Activator extract(Any paramAny)
  {
    return read(paramAny.create_input_stream());
  }
  
  public static synchronized TypeCode type()
  {
    if (__typeCode == null) {
      __typeCode = ORB.init().create_interface_tc(id(), "Activator");
    }
    return __typeCode;
  }
  
  public static String id()
  {
    return _id;
  }
  
  public static Activator read(InputStream paramInputStream)
  {
    return narrow(paramInputStream.read_Object(_ActivatorStub.class));
  }
  
  public static void write(OutputStream paramOutputStream, Activator paramActivator)
  {
    paramOutputStream.write_Object(paramActivator);
  }
  
  public static Activator narrow(org.omg.CORBA.Object paramObject)
  {
    if (paramObject == null) {
      return null;
    }
    if ((paramObject instanceof Activator)) {
      return (Activator)paramObject;
    }
    if (!paramObject._is_a(id())) {
      throw new BAD_PARAM();
    }
    Delegate localDelegate = ((ObjectImpl)paramObject)._get_delegate();
    _ActivatorStub local_ActivatorStub = new _ActivatorStub();
    local_ActivatorStub._set_delegate(localDelegate);
    return local_ActivatorStub;
  }
  
  public static Activator unchecked_narrow(org.omg.CORBA.Object paramObject)
  {
    if (paramObject == null) {
      return null;
    }
    if ((paramObject instanceof Activator)) {
      return (Activator)paramObject;
    }
    Delegate localDelegate = ((ObjectImpl)paramObject)._get_delegate();
    _ActivatorStub local_ActivatorStub = new _ActivatorStub();
    local_ActivatorStub._set_delegate(localDelegate);
    return local_ActivatorStub;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\activation\ActivatorHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */