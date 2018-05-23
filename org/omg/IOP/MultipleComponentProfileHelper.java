package org.omg.IOP;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class MultipleComponentProfileHelper
{
  private static String _id = "IDL:omg.org/IOP/MultipleComponentProfile:1.0";
  private static TypeCode __typeCode = null;
  
  public MultipleComponentProfileHelper() {}
  
  public static void insert(Any paramAny, TaggedComponent[] paramArrayOfTaggedComponent)
  {
    OutputStream localOutputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(localOutputStream, paramArrayOfTaggedComponent);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static TaggedComponent[] extract(Any paramAny)
  {
    return read(paramAny.create_input_stream());
  }
  
  public static synchronized TypeCode type()
  {
    if (__typeCode == null)
    {
      __typeCode = TaggedComponentHelper.type();
      __typeCode = ORB.init().create_sequence_tc(0, __typeCode);
      __typeCode = ORB.init().create_alias_tc(id(), "MultipleComponentProfile", __typeCode);
    }
    return __typeCode;
  }
  
  public static String id()
  {
    return _id;
  }
  
  public static TaggedComponent[] read(InputStream paramInputStream)
  {
    TaggedComponent[] arrayOfTaggedComponent = null;
    int i = paramInputStream.read_long();
    arrayOfTaggedComponent = new TaggedComponent[i];
    for (int j = 0; j < arrayOfTaggedComponent.length; j++) {
      arrayOfTaggedComponent[j] = TaggedComponentHelper.read(paramInputStream);
    }
    return arrayOfTaggedComponent;
  }
  
  public static void write(OutputStream paramOutputStream, TaggedComponent[] paramArrayOfTaggedComponent)
  {
    paramOutputStream.write_long(paramArrayOfTaggedComponent.length);
    for (int i = 0; i < paramArrayOfTaggedComponent.length; i++) {
      TaggedComponentHelper.write(paramOutputStream, paramArrayOfTaggedComponent[i]);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\IOP\MultipleComponentProfileHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */