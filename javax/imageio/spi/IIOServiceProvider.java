package javax.imageio.spi;

import java.util.Locale;

public abstract class IIOServiceProvider
  implements RegisterableService
{
  protected String vendorName;
  protected String version;
  
  public IIOServiceProvider(String paramString1, String paramString2)
  {
    if (paramString1 == null) {
      throw new IllegalArgumentException("vendorName == null!");
    }
    if (paramString2 == null) {
      throw new IllegalArgumentException("version == null!");
    }
    vendorName = paramString1;
    version = paramString2;
  }
  
  public IIOServiceProvider() {}
  
  public void onRegistration(ServiceRegistry paramServiceRegistry, Class<?> paramClass) {}
  
  public void onDeregistration(ServiceRegistry paramServiceRegistry, Class<?> paramClass) {}
  
  public String getVendorName()
  {
    return vendorName;
  }
  
  public String getVersion()
  {
    return version;
  }
  
  public abstract String getDescription(Locale paramLocale);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\imageio\spi\IIOServiceProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */