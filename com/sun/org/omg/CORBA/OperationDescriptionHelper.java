package com.sun.org.omg.CORBA;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public final class OperationDescriptionHelper
{
  private static String _id = "IDL:omg.org/CORBA/OperationDescription:1.0";
  private static TypeCode __typeCode = null;
  private static boolean __active = false;
  
  public OperationDescriptionHelper() {}
  
  public static void insert(Any paramAny, OperationDescription paramOperationDescription)
  {
    OutputStream localOutputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(localOutputStream, paramOperationDescription);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static OperationDescription extract(Any paramAny)
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
          StructMember[] arrayOfStructMember = new StructMember[9];
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
          arrayOfStructMember[4] = new StructMember("result", localTypeCode, null);
          localTypeCode = OperationModeHelper.type();
          arrayOfStructMember[5] = new StructMember("mode", localTypeCode, null);
          localTypeCode = ORB.init().create_string_tc(0);
          localTypeCode = ORB.init().create_alias_tc(IdentifierHelper.id(), "Identifier", localTypeCode);
          localTypeCode = ORB.init().create_alias_tc(ContextIdentifierHelper.id(), "ContextIdentifier", localTypeCode);
          localTypeCode = ORB.init().create_sequence_tc(0, localTypeCode);
          localTypeCode = ORB.init().create_alias_tc(ContextIdSeqHelper.id(), "ContextIdSeq", localTypeCode);
          arrayOfStructMember[6] = new StructMember("contexts", localTypeCode, null);
          localTypeCode = ParameterDescriptionHelper.type();
          localTypeCode = ORB.init().create_sequence_tc(0, localTypeCode);
          localTypeCode = ORB.init().create_alias_tc(ParDescriptionSeqHelper.id(), "ParDescriptionSeq", localTypeCode);
          arrayOfStructMember[7] = new StructMember("parameters", localTypeCode, null);
          localTypeCode = ExceptionDescriptionHelper.type();
          localTypeCode = ORB.init().create_sequence_tc(0, localTypeCode);
          localTypeCode = ORB.init().create_alias_tc(ExcDescriptionSeqHelper.id(), "ExcDescriptionSeq", localTypeCode);
          arrayOfStructMember[8] = new StructMember("exceptions", localTypeCode, null);
          __typeCode = ORB.init().create_struct_tc(id(), "OperationDescription", arrayOfStructMember);
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
  
  public static OperationDescription read(InputStream paramInputStream)
  {
    OperationDescription localOperationDescription = new OperationDescription();
    name = paramInputStream.read_string();
    id = paramInputStream.read_string();
    defined_in = paramInputStream.read_string();
    version = paramInputStream.read_string();
    result = paramInputStream.read_TypeCode();
    mode = OperationModeHelper.read(paramInputStream);
    contexts = ContextIdSeqHelper.read(paramInputStream);
    parameters = ParDescriptionSeqHelper.read(paramInputStream);
    exceptions = ExcDescriptionSeqHelper.read(paramInputStream);
    return localOperationDescription;
  }
  
  public static void write(OutputStream paramOutputStream, OperationDescription paramOperationDescription)
  {
    paramOutputStream.write_string(name);
    paramOutputStream.write_string(id);
    paramOutputStream.write_string(defined_in);
    paramOutputStream.write_string(version);
    paramOutputStream.write_TypeCode(result);
    OperationModeHelper.write(paramOutputStream, mode);
    ContextIdSeqHelper.write(paramOutputStream, contexts);
    ParDescriptionSeqHelper.write(paramOutputStream, parameters);
    ExcDescriptionSeqHelper.write(paramOutputStream, exceptions);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\omg\CORBA\OperationDescriptionHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */