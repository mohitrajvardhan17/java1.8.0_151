package com.sun.corba.se.spi.ior;

import com.sun.corba.se.impl.ior.EncapsulationUtility;
import org.omg.CORBA_2_3.portable.OutputStream;

public abstract class IdentifiableBase
  implements Identifiable, WriteContents
{
  public IdentifiableBase() {}
  
  public final void write(OutputStream paramOutputStream)
  {
    EncapsulationUtility.writeEncapsulation(this, paramOutputStream);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\ior\IdentifiableBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */