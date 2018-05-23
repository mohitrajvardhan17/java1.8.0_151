package sun.tracing;

import com.sun.tracing.Provider;
import com.sun.tracing.ProviderFactory;

public class NullProviderFactory
  extends ProviderFactory
{
  public NullProviderFactory() {}
  
  public <T extends Provider> T createProvider(Class<T> paramClass)
  {
    NullProvider localNullProvider = new NullProvider(paramClass);
    localNullProvider.init();
    return localNullProvider.newProxyInstance();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\tracing\NullProviderFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */