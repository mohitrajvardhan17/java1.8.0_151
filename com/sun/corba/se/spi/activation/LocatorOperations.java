package com.sun.corba.se.spi.activation;

import com.sun.corba.se.spi.activation.LocatorPackage.ServerLocation;
import com.sun.corba.se.spi.activation.LocatorPackage.ServerLocationPerORB;

public abstract interface LocatorOperations
{
  public abstract ServerLocation locateServer(int paramInt, String paramString)
    throws NoSuchEndPoint, ServerNotRegistered, ServerHeldDown;
  
  public abstract ServerLocationPerORB locateServerForORB(int paramInt, String paramString)
    throws InvalidORBid, ServerNotRegistered, ServerHeldDown;
  
  public abstract int getEndpoint(String paramString)
    throws NoSuchEndPoint;
  
  public abstract int getServerPortForType(ServerLocationPerORB paramServerLocationPerORB, String paramString)
    throws NoSuchEndPoint;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\activation\LocatorOperations.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */