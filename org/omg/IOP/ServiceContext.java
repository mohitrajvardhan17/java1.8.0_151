package org.omg.IOP;

import org.omg.CORBA.portable.IDLEntity;

public final class ServiceContext
  implements IDLEntity
{
  public int context_id = 0;
  public byte[] context_data = null;
  
  public ServiceContext() {}
  
  public ServiceContext(int paramInt, byte[] paramArrayOfByte)
  {
    context_id = paramInt;
    context_data = paramArrayOfByte;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\omg\IOP\ServiceContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */