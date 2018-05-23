package org.omg.PortableServer.POAPackage;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class ObjectNotActiveHelper
{
  private static String _id = "IDL:omg.org/PortableServer/POA/ObjectNotActive:1.0";
  private static TypeCode __typeCode = null;
  private static boolean __active = false;
  
  public ObjectNotActiveHelper() {}
  
  public static void insert(Any paramAny, ObjectNotActive paramObjectNotActive)
  {
    OutputStream localOutputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(localOutputStream, paramObjectNotActive);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static ObjectNotActive extract(Any paramAny)
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
          __typeCode = ORB.init().create_exception_tc(id(), "ObjectNotActive", arrayOfStructMember);
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
  
  public static ObjectNotActive read(InputStream paramInputStream)
  {
    ObjectNotActive localObjectNotActive = new ObjectNotActive();
    paramInputStream.read_string();
    return localObjectNotActive;
  }
  
  public static void write(OutputStream paramOutputStream, ObjectNotActive paramObjectNotActive)
  {
    paramOutputStream.write_string(id());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\PortableServer\POAPackage\ObjectNotActiveHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */