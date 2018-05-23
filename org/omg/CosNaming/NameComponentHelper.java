package org.omg.CosNaming;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class NameComponentHelper
{
  private static String _id = "IDL:omg.org/CosNaming/NameComponent:1.0";
  private static TypeCode __typeCode = null;
  private static boolean __active = false;
  
  public NameComponentHelper() {}
  
  public static void insert(Any paramAny, NameComponent paramNameComponent)
  {
    OutputStream localOutputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(localOutputStream, paramNameComponent);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static NameComponent extract(Any paramAny)
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
          localTypeCode = ORB.init().create_string_tc(0);
          localTypeCode = ORB.init().create_alias_tc(IstringHelper.id(), "Istring", localTypeCode);
          arrayOfStructMember[0] = new StructMember("id", localTypeCode, null);
          localTypeCode = ORB.init().create_string_tc(0);
          localTypeCode = ORB.init().create_alias_tc(IstringHelper.id(), "Istring", localTypeCode);
          arrayOfStructMember[1] = new StructMember("kind", localTypeCode, null);
          __typeCode = ORB.init().create_struct_tc(id(), "NameComponent", arrayOfStructMember);
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
  
  public static NameComponent read(InputStream paramInputStream)
  {
    NameComponent localNameComponent = new NameComponent();
    id = paramInputStream.read_string();
    kind = paramInputStream.read_string();
    return localNameComponent;
  }
  
  public static void write(OutputStream paramOutputStream, NameComponent paramNameComponent)
  {
    paramOutputStream.write_string(id);
    paramOutputStream.write_string(kind);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CosNaming\NameComponentHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */