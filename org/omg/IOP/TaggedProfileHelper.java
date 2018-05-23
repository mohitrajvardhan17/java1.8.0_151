package org.omg.IOP;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class TaggedProfileHelper
{
  private static String _id = "IDL:omg.org/IOP/TaggedProfile:1.0";
  private static TypeCode __typeCode = null;
  private static boolean __active = false;
  
  public TaggedProfileHelper() {}
  
  public static void insert(Any paramAny, TaggedProfile paramTaggedProfile)
  {
    OutputStream localOutputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(localOutputStream, paramTaggedProfile);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static TaggedProfile extract(Any paramAny)
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
          localTypeCode = ORB.init().get_primitive_tc(TCKind.tk_ulong);
          localTypeCode = ORB.init().create_alias_tc(ProfileIdHelper.id(), "ProfileId", localTypeCode);
          arrayOfStructMember[0] = new StructMember("tag", localTypeCode, null);
          localTypeCode = ORB.init().get_primitive_tc(TCKind.tk_octet);
          localTypeCode = ORB.init().create_sequence_tc(0, localTypeCode);
          arrayOfStructMember[1] = new StructMember("profile_data", localTypeCode, null);
          __typeCode = ORB.init().create_struct_tc(id(), "TaggedProfile", arrayOfStructMember);
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
  
  public static TaggedProfile read(InputStream paramInputStream)
  {
    TaggedProfile localTaggedProfile = new TaggedProfile();
    tag = paramInputStream.read_ulong();
    int i = paramInputStream.read_long();
    profile_data = new byte[i];
    paramInputStream.read_octet_array(profile_data, 0, i);
    return localTaggedProfile;
  }
  
  public static void write(OutputStream paramOutputStream, TaggedProfile paramTaggedProfile)
  {
    paramOutputStream.write_ulong(tag);
    paramOutputStream.write_long(profile_data.length);
    paramOutputStream.write_octet_array(profile_data, 0, profile_data.length);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\IOP\TaggedProfileHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */