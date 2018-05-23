package com.sun.corba.se.spi.activation;

import org.omg.CORBA.UserException;

public final class ServerNotRegistered
  extends UserException
{
  public int serverId = 0;
  
  public ServerNotRegistered()
  {
    super(ServerNotRegisteredHelper.id());
  }
  
  public ServerNotRegistered(int paramInt)
  {
    super(ServerNotRegisteredHelper.id());
    serverId = paramInt;
  }
  
  public ServerNotRegistered(String paramString, int paramInt)
  {
    super(ServerNotRegisteredHelper.id() + "  " + paramString);
    serverId = paramInt;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\activation\ServerNotRegistered.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */