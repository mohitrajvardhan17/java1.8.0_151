package org.omg.IOP;

import org.omg.CORBA.portable.IDLEntity;

public final class TaggedProfile
  implements IDLEntity
{
  public int tag = 0;
  public byte[] profile_data = null;
  
  public TaggedProfile() {}
  
  public TaggedProfile(int paramInt, byte[] paramArrayOfByte)
  {
    tag = paramInt;
    profile_data = paramArrayOfByte;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\IOP\TaggedProfile.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */