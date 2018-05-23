package org.omg.CosNaming.NamingContextPackage;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CosNaming.NameComponentHelper;
import org.omg.CosNaming.NameHelper;

public abstract class NotFoundHelper
{
  private static String _id = "IDL:omg.org/CosNaming/NamingContext/NotFound:1.0";
  private static TypeCode __typeCode = null;
  private static boolean __active = false;
  
  public NotFoundHelper() {}
  
  public static void insert(Any paramAny, NotFound paramNotFound)
  {
    OutputStream localOutputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(localOutputStream, paramNotFound);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static NotFound extract(Any paramAny)
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
          StructMember[] arrayOfStructMember = new StructMember[2];
          TypeCode localTypeCode = null;
          localTypeCode = NotFoundReasonHelper.type();
          arrayOfStructMember[0] = new StructMember("why", localTypeCode, null);
          localTypeCode = NameComponentHelper.type();
          localTypeCode = ORB.init().create_sequence_tc(0, localTypeCode);
          localTypeCode = ORB.init().create_alias_tc(NameHelper.id(), "Name", localTypeCode);
          arrayOfStructMember[1] = new StructMember("rest_of_name", localTypeCode, null);
          __typeCode = ORB.init().create_exception_tc(id(), "NotFound", arrayOfStructMember);
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
  
  public static NotFound read(InputStream paramInputStream)
  {
    NotFound localNotFound = new NotFound();
    paramInputStream.read_string();
    why = NotFoundReasonHelper.read(paramInputStream);
    rest_of_name = NameHelper.read(paramInputStream);
    return localNotFound;
  }
  
  public static void write(OutputStream paramOutputStream, NotFound paramNotFound)
  {
    paramOutputStream.write_string(id());
    NotFoundReasonHelper.write(paramOutputStream, why);
    NameHelper.write(paramOutputStream, rest_of_name);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CosNaming\NamingContextPackage\NotFoundHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */