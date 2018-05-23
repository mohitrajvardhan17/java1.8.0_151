package com.sun.corba.se.impl.protocol.giopmsgheaders;

import org.omg.CORBA.Any;
import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.UnionMember;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.IOP.TaggedProfile;
import org.omg.IOP.TaggedProfileHelper;

public abstract class TargetAddressHelper
{
  private static String _id = "IDL:messages/TargetAddress:1.0";
  private static TypeCode __typeCode = null;
  
  public TargetAddressHelper() {}
  
  public static void insert(Any paramAny, TargetAddress paramTargetAddress)
  {
    OutputStream localOutputStream = paramAny.create_output_stream();
    paramAny.type(type());
    write(localOutputStream, paramTargetAddress);
    paramAny.read_value(localOutputStream.create_input_stream(), type());
  }
  
  public static TargetAddress extract(Any paramAny)
  {
    return read(paramAny.create_input_stream());
  }
  
  public static synchronized TypeCode type()
  {
    if (__typeCode == null)
    {
      TypeCode localTypeCode1 = ORB.init().get_primitive_tc(TCKind.tk_short);
      localTypeCode1 = ORB.init().create_alias_tc(AddressingDispositionHelper.id(), "AddressingDisposition", localTypeCode1);
      UnionMember[] arrayOfUnionMember = new UnionMember[3];
      Any localAny = ORB.init().create_any();
      localAny.insert_short((short)0);
      TypeCode localTypeCode2 = ORB.init().get_primitive_tc(TCKind.tk_octet);
      localTypeCode2 = ORB.init().create_sequence_tc(0, localTypeCode2);
      arrayOfUnionMember[0] = new UnionMember("object_key", localAny, localTypeCode2, null);
      localAny = ORB.init().create_any();
      localAny.insert_short((short)1);
      localTypeCode2 = TaggedProfileHelper.type();
      arrayOfUnionMember[1] = new UnionMember("profile", localAny, localTypeCode2, null);
      localAny = ORB.init().create_any();
      localAny.insert_short((short)2);
      localTypeCode2 = IORAddressingInfoHelper.type();
      arrayOfUnionMember[2] = new UnionMember("ior", localAny, localTypeCode2, null);
      __typeCode = ORB.init().create_union_tc(id(), "TargetAddress", localTypeCode1, arrayOfUnionMember);
    }
    return __typeCode;
  }
  
  public static String id()
  {
    return _id;
  }
  
  public static TargetAddress read(InputStream paramInputStream)
  {
    TargetAddress localTargetAddress = new TargetAddress();
    int i = 0;
    i = paramInputStream.read_short();
    switch (i)
    {
    case 0: 
      byte[] arrayOfByte = null;
      int j = paramInputStream.read_long();
      arrayOfByte = new byte[j];
      paramInputStream.read_octet_array(arrayOfByte, 0, j);
      localTargetAddress.object_key(arrayOfByte);
      break;
    case 1: 
      TaggedProfile localTaggedProfile = null;
      localTaggedProfile = TaggedProfileHelper.read(paramInputStream);
      localTargetAddress.profile(localTaggedProfile);
      break;
    case 2: 
      IORAddressingInfo localIORAddressingInfo = null;
      localIORAddressingInfo = IORAddressingInfoHelper.read(paramInputStream);
      localTargetAddress.ior(localIORAddressingInfo);
      break;
    default: 
      throw new BAD_OPERATION();
    }
    return localTargetAddress;
  }
  
  public static void write(OutputStream paramOutputStream, TargetAddress paramTargetAddress)
  {
    paramOutputStream.write_short(paramTargetAddress.discriminator());
    switch (paramTargetAddress.discriminator())
    {
    case 0: 
      paramOutputStream.write_long(paramTargetAddress.object_key().length);
      paramOutputStream.write_octet_array(paramTargetAddress.object_key(), 0, paramTargetAddress.object_key().length);
      break;
    case 1: 
      TaggedProfileHelper.write(paramOutputStream, paramTargetAddress.profile());
      break;
    case 2: 
      IORAddressingInfoHelper.write(paramOutputStream, paramTargetAddress.ior());
      break;
    default: 
      throw new BAD_OPERATION();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\protocol\giopmsgheaders\TargetAddressHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */