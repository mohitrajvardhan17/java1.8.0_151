package com.sun.corba.se.impl.orb;

import java.applet.Applet;
import java.util.Properties;

public class AppletDataCollector
  extends DataCollectorBase
{
  private Applet applet;
  
  AppletDataCollector(Applet paramApplet, Properties paramProperties, String paramString1, String paramString2)
  {
    super(paramProperties, paramString1, paramString2);
    applet = paramApplet;
  }
  
  public boolean isApplet()
  {
    return true;
  }
  
  protected void collect()
  {
    checkPropertyDefaults();
    findPropertiesFromFile();
    findPropertiesFromProperties();
    findPropertiesFromApplet(applet);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\orb\AppletDataCollector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */