package com.sun.corba.se.spi.legacy.connection;

public abstract interface LegacyServerSocketManager
{
  public abstract int legacyGetTransientServerPort(String paramString);
  
  public abstract int legacyGetPersistentServerPort(String paramString);
  
  public abstract int legacyGetTransientOrPersistentServerPort(String paramString);
  
  public abstract LegacyServerSocketEndPointInfo legacyGetEndpoint(String paramString);
  
  public abstract boolean legacyIsLocalServerPort(int paramInt);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\legacy\connection\LegacyServerSocketManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */