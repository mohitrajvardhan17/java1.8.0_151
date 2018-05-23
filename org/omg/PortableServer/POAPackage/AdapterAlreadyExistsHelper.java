package org.omg.PortableServer.POAPackage;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class AdapterAlreadyExistsHelper
{
  private static String _id = "IDL:omg.org/PortableServer/POA/AdapterAlreadyExists:1.0";
  private static TypeCode __typeCode = null;
  private static boolean __active = false;
  
  public AdapterAlreadyExistsHelper() {}
  
  public static void insert(Any paramAny, AdapterAlreadyExists paramAdapterAlreadyExists)
  {
    OutputStream localOutputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(localOutputStream, paramAdapterAlreadyExists);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static AdapterAlreadyExists extract(Any paramAny)
  {
    return read(paramAny.create_input_stream());
  }
  
  public static synchronized TypeCode type()
  {
    if (__typeCode == null) {
      synchronized (TypeCode.class)
      {
        if (__typeCode == null)
        {
          if (__active) {
            return ORB.init().create_recursive_tc(_id);
          }
          __active = true;
          StructMember[] arrayOfStructMember = new StructMember[0];
          Object localObject1 = null;
          __typeCode = ORB.init().create_exception_tc(id(), "AdapterAlreadyExists", arrayOfStructMember);
          __active = false;
        }
      }
    }
    return __typeCode;
  }
  
  public static String id()
  {
    return _id;
  }
  
  public static AdapterAlreadyExists read(InputStream paramInputStream)
  {
    AdapterAlreadyExists localAdapterAlreadyExists = new AdapterAlreadyExists();
    paramInputStream.read_string();
    return localAdapterAlreadyExists;
  }
  
  public static void write(OutputStream paramOutputStream, AdapterAlreadyExists paramAdapterAlreadyExists)
  {
    paramOutputStream.write_string(id());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\PortableServer\POAPackage\AdapterAlreadyExistsHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */