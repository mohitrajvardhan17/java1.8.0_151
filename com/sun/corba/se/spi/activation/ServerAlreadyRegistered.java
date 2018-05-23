package com.sun.corba.se.spi.activation;

import org.omg.CORBA.UserException;

public final class ServerAlreadyRegistered
  extends UserException
{
  public int serverId = 0;
  
  public ServerAlreadyRegistered()
  {
    super(ServerAlreadyRegisteredHelper.id());
  }
  
  public ServerAlreadyRegistered(int paramInt)
  {
    super(ServerAlreadyRegisteredHelper.id());
    serverId = paramInt;
  }
  
  public ServerAlreadyRegistered(String paramString, int paramInt)
  {
    super(ServerAlreadyRegisteredHelper.id() + "  " + paramString);
    serverId = paramInt;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\activation\ServerAlreadyRegistered.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */