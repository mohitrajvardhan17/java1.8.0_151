package com.sun.corba.se.impl.orb;

import com.sun.corba.se.spi.orb.DataCollector;
import java.applet.Applet;
import java.net.URL;
import java.util.Properties;

public abstract class DataCollectorFactory
{
  private DataCollectorFactory() {}
  
  public static DataCollector create(Applet paramApplet, Properties paramProperties, String paramString)
  {
    String str = paramString;
    if (paramApplet != null)
    {
      URL localURL = paramApplet.getCodeBase();
      if (localURL != null) {
        str = localURL.getHost();
      }
    }
    return new AppletDataCollector(paramApplet, paramProperties, paramString, str);
  }
  
  public static DataCollector create(String[] paramArrayOfString, Properties paramProperties, String paramString)
  {
    return new NormalDataCollector(paramArrayOfString, paramProperties, paramString, paramString);
  }
  
  public static DataCollector create(Properties paramProperties, String paramString)
  {
    return new PropertyOnlyDataCollector(paramProperties, paramString, paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\corba\se\impl\orb\DataCollectorFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */