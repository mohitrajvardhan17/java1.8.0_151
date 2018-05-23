package org.omg.IOP.CodecPackage;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class InvalidTypeForEncodingHelper
{
  private static String _id = "IDL:omg.org/IOP/Codec/InvalidTypeForEncoding:1.0";
  private static TypeCode __typeCode = null;
  private static boolean __active = false;
  
  public InvalidTypeForEncodingHelper() {}
  
  public static void insert(Any paramAny, InvalidTypeForEncoding paramInvalidTypeForEncoding)
  {
    OutputStream localOutputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(localOutputStream, paramInvalidTypeForEncoding);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static InvalidTypeForEncoding extract(Any paramAny)
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
          __typeCode = ORB.init().create_exception_tc(id(), "InvalidTypeForEncoding", arrayOfStructMember);
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
  
  public static InvalidTypeForEncoding read(InputStream paramInputStream)
  {
    InvalidTypeForEncoding localInvalidTypeForEncoding = new InvalidTypeForEncoding();
    paramInputStream.read_string();
    return localInvalidTypeForEncoding;
  }
  
  public static void write(OutputStream paramOutputStream, InvalidTypeForEncoding paramInvalidTypeForEncoding)
  {
    paramOutputStream.write_string(id());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\IOP\CodecPackage\InvalidTypeForEncodingHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */