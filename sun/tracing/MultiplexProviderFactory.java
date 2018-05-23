package sun.tracing;

import com.sun.tracing.Provider;
import com.sun.tracing.ProviderFactory;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class MultiplexProviderFactory
  extends ProviderFactory
{
  private Set<ProviderFactory> factories;
  
  public MultiplexProviderFactory(Set<ProviderFactory> paramSet)
  {
    factories = paramSet;
  }
  
  public <T extends Provider> T createProvider(Class<T> paramClass)
  {
    HashSet localHashSet = new HashSet();
    Object localObject = factories.iterator();
    while (((Iterator)localObject).hasNext())
    {
      ProviderFactory localProviderFactory = (ProviderFactory)((Iterator)localObject).next();
      localHashSet.add(localProviderFactory.createProvider(paramClass));
    }
    localObject = new MultiplexProvider(paramClass, localHashSet);
    ((MultiplexProvider)localObject).init();
    return ((MultiplexProvider)localObject).newProxyInstance();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\tracing\MultiplexProviderFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */