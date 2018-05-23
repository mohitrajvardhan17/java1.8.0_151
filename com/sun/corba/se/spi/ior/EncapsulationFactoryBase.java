package com.sun.corba.se.spi.ior;

import com.sun.corba.se.impl.ior.EncapsulationUtility;
import org.omg.CORBA_2_3.portable.InputStream;

public abstract class EncapsulationFactoryBase
  implements IdentifiableFactory
{
  private int id;
  
  public int getId()
  {
    return id;
  }
  
  public EncapsulationFactoryBase(int paramInt)
  {
    id = paramInt;
  }
  
  public final Identifiable create(InputStream paramInputStream)
  {
    InputStream localInputStream = EncapsulationUtility.getEncapsulationStream(paramInputStream);
    return readContents(localInputStream);
  }
  
  protected abstract Identifiable readContents(InputStream paramInputStream);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\ior\EncapsulationFactoryBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */