package com.sun.corba.se.spi.legacy.connection;

public abstract interface LegacyServerSocketEndPointInfo
{
  public static final String DEFAULT_ENDPOINT = "DEFAULT_ENDPOINT";
  public static final String BOOT_NAMING = "BOOT_NAMING";
  public static final String NO_NAME = "NO_NAME";
  
  public abstract String getType();
  
  public abstract String getHostName();
  
  public abstract int getPort();
  
  public abstract int getLocatorPort();
  
  public abstract void setLocatorPort(int paramInt);
  
  public abstract String getName();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\spi\legacy\connection\LegacyServerSocketEndPointInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */