package com.sun.corba.se.impl.protocol.giopmsgheaders;

import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.portable.IDLEntity;
import org.omg.IOP.TaggedProfile;

public final class TargetAddress
  implements IDLEntity
{
  private byte[] ___object_key;
  private TaggedProfile ___profile;
  private IORAddressingInfo ___ior;
  private short __discriminator;
  private boolean __uninitialized = true;
  
  public TargetAddress() {}
  
  public short discriminator()
  {
    if (__uninitialized) {
      throw new BAD_OPERATION();
    }
    return __discriminator;
  }
  
  public byte[] object_key()
  {
    if (__uninitialized) {
      throw new BAD_OPERATION();
    }
    verifyobject_key(__discriminator);
    return ___object_key;
  }
  
  public void object_key(byte[] paramArrayOfByte)
  {
    __discriminator = 0;
    ___object_key = paramArrayOfByte;
    __uninitialized = false;
  }
  
  private void verifyobject_key(short paramShort)
  {
    if (paramShort != 0) {
      throw new BAD_OPERATION();
    }
  }
  
  public TaggedProfile profile()
  {
    if (__uninitialized) {
      throw new BAD_OPERATION();
    }
    verifyprofile(__discriminator);
    return ___profile;
  }
  
  public void profile(TaggedProfile paramTaggedProfile)
  {
    __discriminator = 1;
    ___profile = paramTaggedProfile;
    __uninitialized = false;
  }
  
  private void verifyprofile(short paramShort)
  {
    if (paramShort != 1) {
      throw new BAD_OPERATION();
    }
  }
  
  public IORAddressingInfo ior()
  {
    if (__uninitialized) {
      throw new BAD_OPERATION();
    }
    verifyior(__discriminator);
    return ___ior;
  }
  
  public void ior(IORAddressingInfo paramIORAddressingInfo)
  {
    __discriminator = 2;
    ___ior = paramIORAddressingInfo;
    __uninitialized = false;
  }
  
  private void verifyior(short paramShort)
  {
    if (paramShort != 2) {
      throw new BAD_OPERATION();
    }
  }
  
  public void _default()
  {
    __discriminator = Short.MIN_VALUE;
    __uninitialized = false;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\protocol\giopmsgheaders\TargetAddress.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */