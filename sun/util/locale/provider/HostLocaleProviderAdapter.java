package sun.util.locale.provider;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.spi.LocaleServiceProvider;

public class HostLocaleProviderAdapter
  extends AuxLocaleProviderAdapter
{
  public HostLocaleProviderAdapter() {}
  
  public LocaleProviderAdapter.Type getAdapterType()
  {
    return LocaleProviderAdapter.Type.HOST;
  }
  
  protected <P extends LocaleServiceProvider> P findInstalledProvider(Class<P> paramClass)
  {
    try
    {
      Method localMethod = HostLocaleProviderAdapterImpl.class.getMethod("get" + paramClass.getSimpleName(), (Class[])null);
      return (LocaleServiceProvider)localMethod.invoke(null, (Object[])null);
    }
    catch (NoSuchMethodException|IllegalAccessException|IllegalArgumentException|InvocationTargetException localNoSuchMethodException)
    {
      LocaleServiceProviderPool.config(HostLocaleProviderAdapter.class, localNoSuchMethodException.toString());
    }
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\util\locale\provider\HostLocaleProviderAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */