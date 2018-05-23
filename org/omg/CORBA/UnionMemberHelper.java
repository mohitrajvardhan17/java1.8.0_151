package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class UnionMemberHelper
{
  private static String _id = "IDL:omg.org/CORBA/UnionMember:1.0";
  private static TypeCode __typeCode = null;
  private static boolean __active = false;
  
  public UnionMemberHelper() {}
  
  public static void insert(Any paramAny, UnionMember paramUnionMember)
  {
    OutputStream localOutputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(localOutputStream, paramUnionMember);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static UnionMember extract(Any paramAny)
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
          StructMember[] arrayOfStructMember = new StructMember[4];
          TypeCode localTypeCode = null;
          localTypeCode = ORB.init().create_string_tc(0);
          localTypeCode = ORB.init().create_alias_tc(IdentifierHelper.id(), "Identifier", localTypeCode);
          arrayOfStructMember[0] = new StructMember("name", localTypeCode, null);
          localTypeCode = ORB.init().get_primitive_tc(TCKind.tk_any);
          arrayOfStructMember[1] = new StructMember("label", localTypeCode, null);
          localTypeCode = ORB.init().get_primitive_tc(TCKind.tk_TypeCode);
          arrayOfStructMember[2] = new StructMember("type", localTypeCode, null);
          localTypeCode = IDLTypeHelper.type();
          arrayOfStructMember[3] = new StructMember("type_def", localTypeCode, null);
          __typeCode = ORB.init().create_struct_tc(id(), "UnionMember", arrayOfStructMember);
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
  
  public static UnionMember read(InputStream paramInputStream)
  {
    UnionMember localUnionMember = new UnionMember();
    name = paramInputStream.read_string();
    label = paramInputStream.read_any();
    type = paramInputStream.read_TypeCode();
    type_def = IDLTypeHelper.read(paramInputStream);
    return localUnionMember;
  }
  
  public static void write(OutputStream paramOutputStream, UnionMember paramUnionMember)
  {
    paramOutputStream.write_string(name);
    paramOutputStream.write_any(label);
    paramOutputStream.write_TypeCode(type);
    IDLTypeHelper.write(paramOutputStream, type_def);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\CORBA\UnionMemberHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */