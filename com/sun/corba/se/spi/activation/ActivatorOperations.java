package com.sun.corba.se.spi.activation;

public abstract interface ActivatorOperations
{
  public abstract void active(int paramInt, Server paramServer)
    throws ServerNotRegistered;
  
  public abstract void registerEndpoints(int paramInt, String paramString, EndPointInfo[] paramArrayOfEndPointInfo)
    throws ServerNotRegistered, NoSuchEndPoint, ORBAlreadyRegistered;
  
  public abstract int[] getActiveServers();
  
  public abstract void activate(int paramInt)
    throws ServerAlreadyActive, ServerNotRegistered, ServerHeldDown;
  
  public abstract void shutdown(int paramInt)
    throws ServerNotActive, ServerNotRegistered;
  
  public abstract void install(int paramInt)
    throws ServerNotRegistered, ServerHeldDown, ServerAlreadyInstalled;
  
  public abstract String[] getORBNames(int paramInt)
    throws ServerNotRegistered;
  
  public abstract void uninstall(int paramInt)
    throws ServerNotRegistered, ServerHeldDown, ServerAlreadyUninstalled;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\activation\ActivatorOperations.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */