package com.sun.security.auth.login;

import java.net.URI;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;
import jdk.Exported;
import sun.security.provider.ConfigFile.Spi;

@Exported
public class ConfigFile
  extends Configuration
{
  private final ConfigFile.Spi spi;
  
  public ConfigFile()
  {
    spi = new ConfigFile.Spi();
  }
  
  public ConfigFile(URI paramURI)
  {
    spi = new ConfigFile.Spi(paramURI);
  }
  
  public AppConfigurationEntry[] getAppConfigurationEntry(String paramString)
  {
    return spi.engineGetAppConfigurationEntry(paramString);
  }
  
  public void refresh()
  {
    spi.engineRefresh();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\security\auth\login\ConfigFile.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */