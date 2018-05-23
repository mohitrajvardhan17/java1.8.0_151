package com.sun.corba.se.impl.protocol.giopmsgheaders;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.IOP.IORHelper;

public abstract class IORAddressingInfoHelper
{
  private static String _id = "IDL:messages/IORAddressingInfo:1.0";
  private static TypeCode __typeCode = null;
  private static boolean __active = false;
  
  public IORAddressingInfoHelper() {}
  
  public static void insert(Any paramAny, IORAddressingInfo paramIORAddressingInfo)
  {
    OutputStream localOutputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(localOutputStream, paramIORAddressingInfo);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static IORAddressingInfo extract(Any paramAny)
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
          arrayOfStructMember[0] = new StructMember("selected_profile_index", localTypeCode, null);
          localTypeCode = IORHelper.type();
          arrayOfStructMember[1] = new StructMember("ior", localTypeCode, null);
          __typeCode = ORB.init().create_struct_tc(id(), "IORAddressingInfo", arrayOfStructMember);
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
  
  public static IORAddressingInfo read(InputStream paramInputStream)
  {
    IORAddressingInfo localIORAddressingInfo = new IORAddressingInfo();
    selected_profile_index = paramInputStream.read_ulong();
    ior = IORHelper.read(paramInputStream);
    return localIORAddressingInfo;
  }
  
  public static void write(OutputStream paramOutputStream, IORAddressingInfo paramIORAddressingInfo)
  {
    paramOutputStream.write_ulong(selected_profile_index);
    IORHelper.write(paramOutputStream, ior);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\protocol\giopmsgheaders\IORAddressingInfoHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */