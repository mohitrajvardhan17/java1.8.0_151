package javax.security.auth.login;

public abstract class ConfigurationSpi
{
  public ConfigurationSpi() {}
  
  protected abstract AppConfigurationEntry[] engineGetAppConfigurationEntry(String paramString);
  
  protected void engineRefresh() {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\security\auth\login\ConfigurationSpi.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */