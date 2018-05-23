package com.sun.corba.se.spi.activation;

import org.omg.CORBA.UserException;

public final class ServerAlreadyActive
  extends UserException
{
  public int serverId = 0;
  
  public ServerAlreadyActive()
  {
    super(ServerAlreadyActiveHelper.id());
  }
  
  public ServerAlreadyActive(int paramInt)
  {
    super(ServerAlreadyActiveHelper.id());
    serverId = paramInt;
  }
  
  public ServerAlreadyActive(String paramString, int paramInt)
  {
    super(ServerAlreadyActiveHelper.id() + "  " + paramString);
    serverId = paramInt;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\activation\ServerAlreadyActive.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */