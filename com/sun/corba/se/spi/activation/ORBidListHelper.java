package com.sun.corba.se.spi.activation;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class ORBidListHelper
{
  private static String _id = "IDL:activation/ORBidList:1.0";
  private static TypeCode __typeCode = null;
  
  public ORBidListHelper() {}
  
  public static void insert(Any paramAny, String[] paramArrayOfString)
  {
    OutputStream localOutputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(localOutputStream, paramArrayOfString);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static String[] extract(Any paramAny)
  {
    return read(paramAny.create_input_stream());
  }
  
  public static synchronized TypeCode type()
  {
    if (__typeCode == null)
    {
      __typeCode = ORB.init().create_string_tc(0);
      __typeCode = ORB.init().create_alias_tc(ORBidHelper.id(), "ORBid", __typeCode);
      __typeCode = ORB.init().create_sequence_tc(0, __typeCode);
      __typeCode = ORB.init().create_alias_tc(id(), "ORBidList", __typeCode);
    }
    return __typeCode;
  }
  
  public static String id()
  {
    return _id;
  }
  
  public static String[] read(InputStream paramInputStream)
  {
    String[] arrayOfString = null;
    int i = paramInputStream.read_long();
    arrayOfString = new String[i];
    for (int j = 0; j < arrayOfString.length; j++) {
      arrayOfString[j] = ORBidHelper.read(paramInputStream);
    }
    return arrayOfString;
  }
  
  public static void write(OutputStream paramOutputStream, String[] paramArrayOfString)
  {
    paramOutputStream.write_long(paramArrayOfString.length);
    for (int i = 0; i < paramArrayOfString.length; i++) {
      ORBidHelper.write(paramOutputStream, paramArrayOfString[i]);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\activation\ORBidListHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */