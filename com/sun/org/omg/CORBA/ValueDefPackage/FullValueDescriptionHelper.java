package com.sun.org.omg.CORBA.ValueDefPackage;

import com.sun.org.omg.CORBA.AttrDescriptionSeqHelper;
import com.sun.org.omg.CORBA.AttributeDescriptionHelper;
import com.sun.org.omg.CORBA.IdentifierHelper;
import com.sun.org.omg.CORBA.InitializerHelper;
import com.sun.org.omg.CORBA.InitializerSeqHelper;
import com.sun.org.omg.CORBA.OpDescriptionSeqHelper;
import com.sun.org.omg.CORBA.OperationDescriptionHelper;
import com.sun.org.omg.CORBA.RepositoryIdHelper;
import com.sun.org.omg.CORBA.RepositoryIdSeqHelper;
import com.sun.org.omg.CORBA.ValueMemberHelper;
import com.sun.org.omg.CORBA.ValueMemberSeqHelper;
import com.sun.org.omg.CORBA.VersionSpecHelper;
import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public final class FullValueDescriptionHelper
{
  private static String _id = "IDL:omg.org/CORBA/ValueDef/FullValueDescription:1.0";
  private static TypeCode __typeCode = null;
  private static boolean __active = false;
  
  public FullValueDescriptionHelper() {}
  
  public static void insert(Any paramAny, FullValueDescription paramFullValueDescription)
  {
    OutputStream localOutputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(localOutputStream, paramFullValueDescription);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static FullValueDescription extract(Any paramAny)
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
          StructMember[] arrayOfStructMember = new StructMember[15];
          TypeCode localTypeCode = null;
          localTypeCode = ORB.init().create_string_tc(0);
          localTypeCode = ORB.init().create_alias_tc(IdentifierHelper.id(), "Identifier", localTypeCode);
          arrayOfStructMember[0] = new StructMember("name", localTypeCode, null);
          localTypeCode = ORB.init().create_string_tc(0);
          localTypeCode = ORB.init().create_alias_tc(RepositoryIdHelper.id(), "RepositoryId", localTypeCode);
          arrayOfStructMember[1] = new StructMember("id", localTypeCode, null);
          localTypeCode = ORB.init().get_primitive_tc(TCKind.tk_boolean);
          arrayOfStructMember[2] = new StructMember("is_abstract", localTypeCode, null);
          localTypeCode = ORB.init().get_primitive_tc(TCKind.tk_boolean);
          arrayOfStructMember[3] = new StructMember("is_custom", localTypeCode, null);
          localTypeCode = ORB.init().create_string_tc(0);
          localTypeCode = ORB.init().create_alias_tc(RepositoryIdHelper.id(), "RepositoryId", localTypeCode);
          arrayOfStructMember[4] = new StructMember("defined_in", localTypeCode, null);
          localTypeCode = ORB.init().create_string_tc(0);
          localTypeCode = ORB.init().create_alias_tc(VersionSpecHelper.id(), "VersionSpec", localTypeCode);
          arrayOfStructMember[5] = new StructMember("version", localTypeCode, null);
          localTypeCode = OperationDescriptionHelper.type();
          localTypeCode = ORB.init().create_sequence_tc(0, localTypeCode);
          localTypeCode = ORB.init().create_alias_tc(OpDescriptionSeqHelper.id(), "OpDescriptionSeq", localTypeCode);
          arrayOfStructMember[6] = new StructMember("operations", localTypeCode, null);
          localTypeCode = AttributeDescriptionHelper.type();
          localTypeCode = ORB.init().create_sequence_tc(0, localTypeCode);
          localTypeCode = ORB.init().create_alias_tc(AttrDescriptionSeqHelper.id(), "AttrDescriptionSeq", localTypeCode);
          arrayOfStructMember[7] = new StructMember("attributes", localTypeCode, null);
          localTypeCode = ValueMemberHelper.type();
          localTypeCode = ORB.init().create_sequence_tc(0, localTypeCode);
          localTypeCode = ORB.init().create_alias_tc(ValueMemberSeqHelper.id(), "ValueMemberSeq", localTypeCode);
          arrayOfStructMember[8] = new StructMember("members", localTypeCode, null);
          localTypeCode = InitializerHelper.type();
          localTypeCode = ORB.init().create_sequence_tc(0, localTypeCode);
          localTypeCode = ORB.init().create_alias_tc(InitializerSeqHelper.id(), "InitializerSeq", localTypeCode);
          arrayOfStructMember[9] = new StructMember("initializers", localTypeCode, null);
          localTypeCode = ORB.init().create_string_tc(0);
          localTypeCode = ORB.init().create_alias_tc(RepositoryIdHelper.id(), "RepositoryId", localTypeCode);
          localTypeCode = ORB.init().create_sequence_tc(0, localTypeCode);
          localTypeCode = ORB.init().create_alias_tc(RepositoryIdSeqHelper.id(), "RepositoryIdSeq", localTypeCode);
          arrayOfStructMember[10] = new StructMember("supported_interfaces", localTypeCode, null);
          localTypeCode = ORB.init().create_string_tc(0);
          localTypeCode = ORB.init().create_alias_tc(RepositoryIdHelper.id(), "RepositoryId", localTypeCode);
          localTypeCode = ORB.init().create_sequence_tc(0, localTypeCode);
          localTypeCode = ORB.init().create_alias_tc(RepositoryIdSeqHelper.id(), "RepositoryIdSeq", localTypeCode);
          arrayOfStructMember[11] = new StructMember("abstract_base_values", localTypeCode, null);
          localTypeCode = ORB.init().get_primitive_tc(TCKind.tk_boolean);
          arrayOfStructMember[12] = new StructMember("is_truncatable", localTypeCode, null);
          localTypeCode = ORB.init().create_string_tc(0);
          localTypeCode = ORB.init().create_alias_tc(RepositoryIdHelper.id(), "RepositoryId", localTypeCode);
          arrayOfStructMember[13] = new StructMember("base_value", localTypeCode, null);
          localTypeCode = ORB.init().get_primitive_tc(TCKind.tk_TypeCode);
          arrayOfStructMember[14] = new StructMember("type", localTypeCode, null);
          __typeCode = ORB.init().create_struct_tc(id(), "FullValueDescription", arrayOfStructMember);
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
  
  public static FullValueDescription read(InputStream paramInputStream)
  {
    FullValueDescription localFullValueDescription = new FullValueDescription();
    name = paramInputStream.read_string();
    id = paramInputStream.read_string();
    is_abstract = paramInputStream.read_boolean();
    is_custom = paramInputStream.read_boolean();
    defined_in = paramInputStream.read_string();
    version = paramInputStream.read_string();
    operations = OpDescriptionSeqHelper.read(paramInputStream);
    attributes = AttrDescriptionSeqHelper.read(paramInputStream);
    members = ValueMemberSeqHelper.read(paramInputStream);
    initializers = InitializerSeqHelper.read(paramInputStream);
    supported_interfaces = RepositoryIdSeqHelper.read(paramInputStream);
    abstract_base_values = RepositoryIdSeqHelper.read(paramInputStream);
    is_truncatable = paramInputStream.read_boolean();
    base_value = paramInputStream.read_string();
    type = paramInputStream.read_TypeCode();
    return localFullValueDescription;
  }
  
  public static void write(OutputStream paramOutputStream, FullValueDescription paramFullValueDescription)
  {
    paramOutputStream.write_string(name);
    paramOutputStream.write_string(id);
    paramOutputStream.write_boolean(is_abstract);
    paramOutputStream.write_boolean(is_custom);
    paramOutputStream.write_string(defined_in);
    paramOutputStream.write_string(version);
    OpDescriptionSeqHelper.write(paramOutputStream, operations);
    AttrDescriptionSeqHelper.write(paramOutputStream, attributes);
    ValueMemberSeqHelper.write(paramOutputStream, members);
    InitializerSeqHelper.write(paramOutputStream, initializers);
    RepositoryIdSeqHelper.write(paramOutputStream, supported_interfaces);
    RepositoryIdSeqHelper.write(paramOutputStream, abstract_base_values);
    paramOutputStream.write_boolean(is_truncatable);
    paramOutputStream.write_string(base_value);
    paramOutputStream.write_TypeCode(type);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\omg\CORBA\ValueDefPackage\FullValueDescriptionHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */