package org.omg.CosNaming;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class BindingHelper
{
  private static String _id = "IDL:omg.org/CosNaming/Binding:1.0";
  private static TypeCode __typeCode = null;
  private static boolean __active = false;
  
  public BindingHelper() {}
  
  public static void insert(Any paramAny, Binding paramBinding)
  {
    OutputStream localOutputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(localOutputStream, paramBinding);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static Binding extract(Any paramAny)
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
          localTypeCode = NameComponentHelper.type();
          localTypeCode = ORB.init().create_sequence_tc(0, localTypeCode);
          localTypeCode = ORB.init().create_alias_tc(NameHelper.id(), "Name", localTypeCode);
          arrayOfStructMember[0] = new StructMember("binding_name", localTypeCode, null);
          localTypeCode = BindingTypeHelper.type();
          arrayOfStructMember[1] = new StructMember("binding_type", localTypeCode, null);
          __typeCode = ORB.init().create_struct_tc(id(), "Binding", arrayOfStructMember);
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
  
  public static Binding read(InputStream paramInputStream)
  {
    Binding localBinding = new Binding();
    binding_name = NameHelper.read(paramInputStream);
    binding_type = BindingTypeHelper.read(paramInputStream);
    return localBinding;
  }
  
  public static void write(OutputStream paramOutputStream, Binding paramBinding)
  {
    NameHelper.write(paramOutputStream, binding_name);
    BindingTypeHelper.write(paramOutputStream, binding_type);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CosNaming\BindingHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */