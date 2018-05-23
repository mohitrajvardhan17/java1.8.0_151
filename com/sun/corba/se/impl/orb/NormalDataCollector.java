package com.sun.corba.se.impl.orb;

import java.util.Properties;

public class NormalDataCollector
  extends DataCollectorBase
{
  private String[] args;
  
  public NormalDataCollector(String[] paramArrayOfString, Properties paramProperties, String paramString1, String paramString2)
  {
    super(paramProperties, paramString1, paramString2);
    args = paramArrayOfString;
  }
  
  public boolean isApplet()
  {
    return false;
  }
  
  protected void collect()
  {
    checkPropertyDefaults();
    findPropertiesFromFile();
    findPropertiesFromSystem();
    findPropertiesFromProperties();
    findPropertiesFromArgs(args);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\orb\NormalDataCollector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */