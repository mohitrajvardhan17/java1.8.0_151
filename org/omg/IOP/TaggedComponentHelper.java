package org.omg.IOP;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class TaggedComponentHelper
{
  private static String _id = "IDL:omg.org/IOP/TaggedComponent:1.0";
  private static TypeCode __typeCode = null;
  private static boolean __active = false;
  
  public TaggedComponentHelper() {}
  
  public static void insert(Any paramAny, TaggedComponent paramTaggedComponent)
  {
    OutputStream localOutputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(localOutputStream, paramTaggedComponent);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static TaggedComponent extract(Any paramAny)
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
          localTypeCode = ORB.init().create_alias_tc(ComponentIdHelper.id(), "ComponentId", localTypeCode);
          arrayOfStructMember[0] = new StructMember("tag", localTypeCode, null);
          localTypeCode = ORB.init().get_primitive_tc(TCKind.tk_octet);
          localTypeCode = ORB.init().create_sequence_tc(0, localTypeCode);
          arrayOfStructMember[1] = new StructMember("component_data", localTypeCode, null);
          __typeCode = ORB.init().create_struct_tc(id(), "TaggedComponent", arrayOfStructMember);
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
  
  public static TaggedComponent read(InputStream paramInputStream)
  {
    TaggedComponent localTaggedComponent = new TaggedComponent();
    tag = paramInputStream.read_ulong();
    int i = paramInputStream.read_long();
    component_data = new byte[i];
    paramInputStream.read_octet_array(component_data, 0, i);
    return localTaggedComponent;
  }
  
  public static void write(OutputStream paramOutputStream, TaggedComponent paramTaggedComponent)
  {
    paramOutputStream.write_ulong(tag);
    paramOutputStream.write_long(component_data.length);
    paramOutputStream.write_octet_array(component_data, 0, component_data.length);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\IOP\TaggedComponentHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */