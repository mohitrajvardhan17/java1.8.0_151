package com.sun.corba.se.spi.activation;

import org.omg.CORBA.Any;
import org.omg.CORBA.BAD_PARAM;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.CORBA.portable.OutputStream;

public abstract class LocatorHelper
{
  private static String _id = "IDL:activation/Locator:1.0";
  private static TypeCode __typeCode = null;
  
  public LocatorHelper() {}
  
  public static void insert(Any paramAny, Locator paramLocator)
  {
    OutputStream localOutputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(localOutputStream, paramLocator);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static Locator extract(Any paramAny)
  {
    return read(paramAny.create_input_stream());
  }
  
  public static synchronized TypeCode type()
  {
    if (__typeCode == null) {
      __typeCode = ORB.init().create_interface_tc(id(), "Locator");
    }
    return __typeCode;
  }
  
  public static String id()
  {
    return _id;
  }
  
  public static Locator read(InputStream paramInputStream)
  {
    return narrow(paramInputStream.read_Object(_LocatorStub.class));
  }
  
  public static void write(OutputStream paramOutputStream, Locator paramLocator)
  {
    paramOutputStream.write_Object(paramLocator);
  }
  
  public static Locator narrow(org.omg.CORBA.Object paramObject)
  {
    if (paramObject == null) {
      return null;
    }
    if ((paramObject instanceof Locator)) {
      return (Locator)paramObject;
    }
    if (!paramObject._is_a(id())) {
      throw new BAD_PARAM();
    }
    Delegate localDelegate = ((ObjectImpl)paramObject)._get_delegate();
    _LocatorStub local_LocatorStub = new _LocatorStub();
    local_LocatorStub._set_delegate(localDelegate);
    return local_LocatorStub;
  }
  
  public static Locator unchecked_narrow(org.omg.CORBA.Object paramObject)
  {
    if (paramObject == null) {
      return null;
    }
    if ((paramObject instanceof Locator)) {
      return (Locator)paramObject;
    }
    Delegate localDelegate = ((ObjectImpl)paramObject)._get_delegate();
    _LocatorStub local_LocatorStub = new _LocatorStub();
    local_LocatorStub._set_delegate(localDelegate);
    return local_LocatorStub;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\activation\LocatorHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */