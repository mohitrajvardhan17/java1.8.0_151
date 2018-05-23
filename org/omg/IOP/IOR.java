package org.omg.IOP;

import org.omg.CORBA.portable.IDLEntity;

public final class IOR
  implements IDLEntity
{
  public String type_id = null;
  public TaggedProfile[] profiles = null;
  
  public IOR() {}
  
  public IOR(String paramString, TaggedProfile[] paramArrayOfTaggedProfile)
  {
    type_id = paramString;
    profiles = paramArrayOfTaggedProfile;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\IOP\IOR.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */