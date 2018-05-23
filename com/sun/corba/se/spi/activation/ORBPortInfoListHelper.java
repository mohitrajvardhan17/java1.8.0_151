package com.sun.corba.se.spi.activation;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class ORBPortInfoListHelper
{
  private static String _id = "IDL:activation/ORBPortInfoList:1.0";
  private static TypeCode __typeCode = null;
  
  public ORBPortInfoListHelper() {}
  
  public static void insert(Any paramAny, ORBPortInfo[] paramArrayOfORBPortInfo)
  {
    OutputStream localOutputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(localOutputStream, paramArrayOfORBPortInfo);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static ORBPortInfo[] extract(Any paramAny)
  {
    return read(paramAny.create_input_stream());
  }
  
  public static synchronized TypeCode type()
  {
    if (__typeCode == null)
    {
      __typeCode = ORBPortInfoHelper.type();
      __typeCode = ORB.init().create_sequence_tc(0, __typeCode);
      __typeCode = ORB.init().create_alias_tc(id(), "ORBPortInfoList", __typeCode);
    }
    return __typeCode;
  }
  
  public static String id()
  {
    return _id;
  }
  
  public static ORBPortInfo[] read(InputStream paramInputStream)
  {
    ORBPortInfo[] arrayOfORBPortInfo = null;
    int i = paramInputStream.read_long();
    arrayOfORBPortInfo = new ORBPortInfo[i];
    for (int j = 0; j < arrayOfORBPortInfo.length; j++) {
      arrayOfORBPortInfo[j] = ORBPortInfoHelper.read(paramInputStream);
    }
    return arrayOfORBPortInfo;
  }
  
  public static void write(OutputStream paramOutputStream, ORBPortInfo[] paramArrayOfORBPortInfo)
  {
    paramOutputStream.write_long(paramArrayOfORBPortInfo.length);
    for (int i = 0; i < paramArrayOfORBPortInfo.length; i++) {
      ORBPortInfoHelper.write(paramOutputStream, paramArrayOfORBPortInfo[i]);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\activation\ORBPortInfoListHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */