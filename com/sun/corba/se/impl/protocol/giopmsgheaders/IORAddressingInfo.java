package com.sun.corba.se.impl.protocol.giopmsgheaders;

import org.omg.CORBA.portable.IDLEntity;
import org.omg.IOP.IOR;

public final class IORAddressingInfo
  implements IDLEntity
{
  public int selected_profile_index = 0;
  public IOR ior = null;
  
  public IORAddressingInfo() {}
  
  public IORAddressingInfo(int paramInt, IOR paramIOR)
  {
    selected_profile_index = paramInt;
    ior = paramIOR;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\protocol\giopmsgheaders\IORAddressingInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */