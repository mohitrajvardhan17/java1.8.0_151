package com.sun.corba.se.impl.ior;

import org.omg.CORBA.ORB;
import org.omg.CORBA_2_3.portable.InputStream;

public class GenericTaggedComponent
  extends GenericIdentifiable
  implements com.sun.corba.se.spi.ior.TaggedComponent
{
  public GenericTaggedComponent(int paramInt, InputStream paramInputStream)
  {
    super(paramInt, paramInputStream);
  }
  
  public GenericTaggedComponent(int paramInt, byte[] paramArrayOfByte)
  {
    super(paramInt, paramArrayOfByte);
  }
  
  public org.omg.IOP.TaggedComponent getIOPComponent(ORB paramORB)
  {
    return new org.omg.IOP.TaggedComponent(getId(), getData());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\ior\GenericTaggedComponent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */