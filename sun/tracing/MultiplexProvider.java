package sun.tracing;

import com.sun.tracing.Provider;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Set;

class MultiplexProvider
  extends ProviderSkeleton
{
  private Set<Provider> providers;
  
  protected ProbeSkeleton createProbe(Method paramMethod)
  {
    return new MultiplexProbe(paramMethod, providers);
  }
  
  MultiplexProvider(Class<? extends Provider> paramClass, Set<Provider> paramSet)
  {
    super(paramClass);
    providers = paramSet;
  }
  
  public void dispose()
  {
    Iterator localIterator = providers.iterator();
    while (localIterator.hasNext())
    {
      Provider localProvider = (Provider)localIterator.next();
      localProvider.dispose();
    }
    super.dispose();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\tracing\MultiplexProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */