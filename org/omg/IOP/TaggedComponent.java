package org.omg.IOP;

import org.omg.CORBA.portable.IDLEntity;

public final class TaggedComponent
  implements IDLEntity
{
  public int tag = 0;
  public byte[] component_data = null;
  
  public TaggedComponent() {}
  
  public TaggedComponent(int paramInt, byte[] paramArrayOfByte)
  {
    tag = paramInt;
    component_data = paramArrayOfByte;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\IOP\TaggedComponent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */