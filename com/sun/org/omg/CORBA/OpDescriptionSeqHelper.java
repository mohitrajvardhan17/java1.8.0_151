package com.sun.org.omg.CORBA;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public final class OpDescriptionSeqHelper
{
  private static String _id = "IDL:omg.org/CORBA/OpDescriptionSeq:1.0";
  private static TypeCode __typeCode = null;
  
  public OpDescriptionSeqHelper() {}
  
  public static void insert(Any paramAny, OperationDescription[] paramArrayOfOperationDescription)
  {
    OutputStream localOutputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(localOutputStream, paramArrayOfOperationDescription);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static OperationDescription[] extract(Any paramAny)
  {
    return read(paramAny.create_input_stream());
  }
  
  public static synchronized TypeCode type()
  {
    if (__typeCode == null)
    {
      __typeCode = OperationDescriptionHelper.type();
      __typeCode = ORB.init().create_sequence_tc(0, __typeCode);
      __typeCode = ORB.init().create_alias_tc(id(), "OpDescriptionSeq", __typeCode);
    }
    return __typeCode;
  }
  
  public static String id()
  {
    return _id;
  }
  
  public static OperationDescription[] read(InputStream paramInputStream)
  {
    OperationDescription[] arrayOfOperationDescription = null;
    int i = paramInputStream.read_long();
    arrayOfOperationDescription = new OperationDescription[i];
    for (int j = 0; j < arrayOfOperationDescription.length; j++) {
      arrayOfOperationDescription[j] = OperationDescriptionHelper.read(paramInputStream);
    }
    return arrayOfOperationDescription;
  }
  
  public static void write(OutputStream paramOutputStream, OperationDescription[] paramArrayOfOperationDescription)
  {
    paramOutputStream.write_long(paramArrayOfOperationDescription.length);
    for (int i = 0; i < paramArrayOfOperationDescription.length; i++) {
      OperationDescriptionHelper.write(paramOutputStream, paramArrayOfOperationDescription[i]);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\omg\CORBA\OpDescriptionSeqHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */