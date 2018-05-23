package com.sun.org.omg.CORBA;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public final class ExceptionDescriptionHelper
{
  private static String _id = "IDL:omg.org/CORBA/ExceptionDescription:1.0";
  private static TypeCode __typeCode = null;
  private static boolean __active = false;
  
  public ExceptionDescriptionHelper() {}
  
  public static void insert(Any paramAny, ExceptionDescription paramExceptionDescription)
  {
    OutputStream localOutputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(localOutputStream, paramExceptionDescription);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static ExceptionDescription extract(Any paramAny)
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
          StructMember[] arrayOfStructMember = new StructMember[5];
          TypeCode localTypeCode = null;
          localTypeCode = ORB.init().create_string_tc(0);
          localTypeCode = ORB.init().create_alias_tc(IdentifierHelper.id(), "Identifier", localTypeCode);
          arrayOfStructMember[0] = new StructMember("name", localTypeCode, null);
          localTypeCode = ORB.init().create_string_tc(0);
          localTypeCode = ORB.init().create_alias_tc(RepositoryIdHelper.id(), "RepositoryId", localTypeCode);
          arrayOfStructMember[1] = new StructMember("id", localTypeCode, null);
          localTypeCode = ORB.init().create_string_tc(0);
          localTypeCode = ORB.init().create_alias_tc(RepositoryIdHelper.id(), "RepositoryId", localTypeCode);
          arrayOfStructMember[2] = new StructMember("defined_in", localTypeCode, null);
          localTypeCode = ORB.init().create_string_tc(0);
          localTypeCode = ORB.init().create_alias_tc(VersionSpecHelper.id(), "VersionSpec", localTypeCode);
          arrayOfStructMember[3] = new StructMember("version", localTypeCode, null);
          localTypeCode = ORB.init().get_primitive_tc(TCKind.tk_TypeCode);
          arrayOfStructMember[4] = new StructMember("type", localTypeCode, null);
          __typeCode = ORB.init().create_struct_tc(id(), "ExceptionDescription", arrayOfStructMember);
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
  
  public static ExceptionDescription read(InputStream paramInputStream)
  {
    ExceptionDescription localExceptionDescription = new ExceptionDescription();
    name = paramInputStream.read_string();
    id = paramInputStream.read_string();
    defined_in = paramInputStream.read_string();
    version = paramInputStream.read_string();
    type = paramInputStream.read_TypeCode();
    return localExceptionDescription;
  }
  
  public static void write(OutputStream paramOutputStream, ExceptionDescription paramExceptionDescription)
  {
    paramOutputStream.write_string(name);
    paramOutputStream.write_string(id);
    paramOutputStream.write_string(defined_in);
    paramOutputStream.write_string(version);
    paramOutputStream.write_TypeCode(type);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\omg\CORBA\ExceptionDescriptionHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */