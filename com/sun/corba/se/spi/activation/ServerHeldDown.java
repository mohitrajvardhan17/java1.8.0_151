package com.sun.corba.se.spi.activation;

import org.omg.CORBA.UserException;

public final class ServerHeldDown
  extends UserException
{
  public int serverId = 0;
  
  public ServerHeldDown()
  {
    super(ServerHeldDownHelper.id());
  }
  
  public ServerHeldDown(int paramInt)
  {
    super(ServerHeldDownHelper.id());
    serverId = paramInt;
  }
  
  public ServerHeldDown(String paramString, int paramInt)
  {
    super(ServerHeldDownHelper.id() + "  " + paramString);
    serverId = paramInt;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\activation\ServerHeldDown.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */