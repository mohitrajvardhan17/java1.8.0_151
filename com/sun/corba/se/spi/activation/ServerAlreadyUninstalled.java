package com.sun.corba.se.spi.activation;

import org.omg.CORBA.UserException;

public final class ServerAlreadyUninstalled
  extends UserException
{
  public int serverId = 0;
  
  public ServerAlreadyUninstalled()
  {
    super(ServerAlreadyUninstalledHelper.id());
  }
  
  public ServerAlreadyUninstalled(int paramInt)
  {
    super(ServerAlreadyUninstalledHelper.id());
    serverId = paramInt;
  }
  
  public ServerAlreadyUninstalled(String paramString, int paramInt)
  {
    super(ServerAlreadyUninstalledHelper.id() + "  " + paramString);
    serverId = paramInt;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\activation\ServerAlreadyUninstalled.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */