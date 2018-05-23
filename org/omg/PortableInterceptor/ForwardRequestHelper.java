package org.omg.PortableInterceptor;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.ObjectHelper;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class ForwardRequestHelper
{
  private static String _id = "IDL:omg.org/PortableInterceptor/ForwardRequest:1.0";
  private static TypeCode __typeCode = null;
  private static boolean __active = false;
  
  public ForwardRequestHelper() {}
  
  public static void insert(Any paramAny, ForwardRequest paramForwardRequest)
  {
    OutputStream localOutputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(localOutputStream, paramForwardRequest);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static ForwardRequest extract(Any paramAny)
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
          StructMember[] arrayOfStructMember = new StructMember[1];
          TypeCode localTypeCode = null;
          localTypeCode = ObjectHelper.type();
          arrayOfStructMember[0] = new StructMember("forward", localTypeCode, null);
          __typeCode = ORB.init().create_exception_tc(id(), "ForwardRequest", arrayOfStructMember);
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
  
  public static ForwardRequest read(InputStream paramInputStream)
  {
    ForwardRequest localForwardRequest = new ForwardRequest();
    paramInputStream.read_string();
    forward = ObjectHelper.read(paramInputStream);
    return localForwardRequest;
  }
  
  public static void write(OutputStream paramOutputStream, ForwardRequest paramForwardRequest)
  {
    paramOutputStream.write_string(id());
    ObjectHelper.write(paramOutputStream, forward);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\PortableInterceptor\ForwardRequestHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */