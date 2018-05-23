package com.sun.org.omg.CORBA;

import org.omg.CORBA.Any;
import org.omg.CORBA.DefinitionKind;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public final class DefinitionKindHelper
{
  private static String _id = "IDL:omg.org/CORBA/DefinitionKind:1.0";
  private static TypeCode __typeCode = null;
  
  public DefinitionKindHelper() {}
  
  public static void insert(Any paramAny, DefinitionKind paramDefinitionKind)
  {
    OutputStream localOutputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(localOutputStream, paramDefinitionKind);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static DefinitionKind extract(Any paramAny)
  {
    return read(paramAny.create_input_stream());
  }
  
  public static synchronized TypeCode type()
  {
    if (__typeCode == null) {
      __typeCode = ORB.init().create_enum_tc(id(), "DefinitionKind", new String[] { "dk_none", "dk_all", "dk_Attribute", "dk_Constant", "dk_Exception", "dk_Interface", "dk_Module", "dk_Operation", "dk_Typedef", "dk_Alias", "dk_Struct", "dk_Union", "dk_Enum", "dk_Primitive", "dk_String", "dk_Sequence", "dk_Array", "dk_Repository", "dk_Wstring", "dk_Fixed", "dk_Value", "dk_ValueBox", "dk_ValueMember", "dk_Native" });
    }
    return __typeCode;
  }
  
  public static String id()
  {
    return _id;
  }
  
  public static DefinitionKind read(InputStream paramInputStream)
  {
    return DefinitionKind.from_int(paramInputStream.read_long());
  }
  
  public static void write(OutputStream paramOutputStream, DefinitionKind paramDefinitionKind)
  {
    paramOutputStream.write_long(paramDefinitionKind.value());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\omg\CORBA\DefinitionKindHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */