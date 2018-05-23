package com.sun.corba.se.spi.activation;

import org.omg.CORBA.UserException;

public final class ServerNotActive
  extends UserException
{
  public int serverId = 0;
  
  public ServerNotActive()
  {
    super(ServerNotActiveHelper.id());
  }
  
  public ServerNotActive(int paramInt)
  {
    super(ServerNotActiveHelper.id());
    serverId = paramInt;
  }
  
  public ServerNotActive(String paramString, int paramInt)
  {
    super(ServerNotActiveHelper.id() + "  " + paramString);
    serverId = paramInt;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\activation\ServerNotActive.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */