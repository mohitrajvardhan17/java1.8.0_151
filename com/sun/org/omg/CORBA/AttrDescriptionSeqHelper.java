package com.sun.org.omg.CORBA;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public final class AttrDescriptionSeqHelper
{
  private static String _id = "IDL:omg.org/CORBA/AttrDescriptionSeq:1.0";
  private static TypeCode __typeCode = null;
  
  public AttrDescriptionSeqHelper() {}
  
  public static void insert(Any paramAny, AttributeDescription[] paramArrayOfAttributeDescription)
  {
    OutputStream localOutputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(localOutputStream, paramArrayOfAttributeDescription);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static AttributeDescription[] extract(Any paramAny)
  {
    return read(paramAny.create_input_stream());
  }
  
  public static synchronized TypeCode type()
  {
    if (__typeCode == null)
    {
      __typeCode = AttributeDescriptionHelper.type();
      __typeCode = ORB.init().create_sequence_tc(0, __typeCode);
      __typeCode = ORB.init().create_alias_tc(id(), "AttrDescriptionSeq", __typeCode);
    }
    return __typeCode;
  }
  
  public static String id()
  {
    return _id;
  }
  
  public static AttributeDescription[] read(InputStream paramInputStream)
  {
    AttributeDescription[] arrayOfAttributeDescription = null;
    int i = paramInputStream.read_long();
    arrayOfAttributeDescription = new AttributeDescription[i];
    for (int j = 0; j < arrayOfAttributeDescription.length; j++) {
      arrayOfAttributeDescription[j] = AttributeDescriptionHelper.read(paramInputStream);
    }
    return arrayOfAttributeDescription;
  }
  
  public static void write(OutputStream paramOutputStream, AttributeDescription[] paramArrayOfAttributeDescription)
  {
    paramOutputStream.write_long(paramArrayOfAttributeDescription.length);
    for (int i = 0; i < paramArrayOfAttributeDescription.length; i++) {
      AttributeDescriptionHelper.write(paramOutputStream, paramArrayOfAttributeDescription[i]);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\omg\CORBA\AttrDescriptionSeqHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */