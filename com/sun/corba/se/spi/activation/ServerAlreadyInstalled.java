package com.sun.corba.se.spi.activation;

import org.omg.CORBA.UserException;

public final class ServerAlreadyInstalled
  extends UserException
{
  public int serverId = 0;
  
  public ServerAlreadyInstalled()
  {
    super(ServerAlreadyInstalledHelper.id());
  }
  
  public ServerAlreadyInstalled(int paramInt)
  {
    super(ServerAlreadyInstalledHelper.id());
    serverId = paramInt;
  }
  
  public ServerAlreadyInstalled(String paramString, int paramInt)
  {
    super(ServerAlreadyInstalledHelper.id() + "  " + paramString);
    serverId = paramInt;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\activation\ServerAlreadyInstalled.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */