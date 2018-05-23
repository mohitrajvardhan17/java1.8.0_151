package com.sun.corba.se.impl.oa;

import com.sun.corba.se.spi.oa.NullServant;
import org.omg.CORBA.SystemException;

public class NullServantImpl
  implements NullServant
{
  private SystemException sysex;
  
  public NullServantImpl(SystemException paramSystemException)
  {
    sysex = paramSystemException;
  }
  
  public SystemException getException()
  {
    return sysex;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\oa\NullServantImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */