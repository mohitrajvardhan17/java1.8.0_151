package org.omg.IOP;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class IORHelper
{
  private static String _id = "IDL:omg.org/IOP/IOR:1.0";
  private static TypeCode __typeCode = null;
  private static boolean __active = false;
  
  public IORHelper() {}
  
  public static void insert(Any paramAny, IOR paramIOR)
  {
    OutputStream localOutputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(localOutputStream, paramIOR);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static IOR extract(Any paramAny)
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
          arrayOfStructMember[0] = new StructMember("type_id", localTypeCode, null);
          localTypeCode = TaggedProfileHelper.type();
          localTypeCode = ORB.init().create_sequence_tc(0, localTypeCode);
          arrayOfStructMember[1] = new StructMember("profiles", localTypeCode, null);
          __typeCode = ORB.init().create_struct_tc(id(), "IOR", arrayOfStructMember);
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
  
  public static IOR read(InputStream paramInputStream)
  {
    IOR localIOR = new IOR();
    type_id = paramInputStream.read_string();
    int i = paramInputStream.read_long();
    profiles = new TaggedProfile[i];
    for (int j = 0; j < profiles.length; j++) {
      profiles[j] = TaggedProfileHelper.read(paramInputStream);
    }
    return localIOR;
  }
  
  public static void write(OutputStream paramOutputStream, IOR paramIOR)
  {
    paramOutputStream.write_string(type_id);
    paramOutputStream.write_long(profiles.length);
    for (int i = 0; i < profiles.length; i++) {
      TaggedProfileHelper.write(paramOutputStream, profiles[i]);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\IOP\IORHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */