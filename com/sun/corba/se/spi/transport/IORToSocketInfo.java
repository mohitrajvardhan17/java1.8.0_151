package com.sun.corba.se.spi.transport;

import com.sun.corba.se.spi.ior.IOR;
import java.util.List;

public abstract interface IORToSocketInfo
{
  public abstract List getSocketInfo(IOR paramIOR);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\transport\IORToSocketInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */