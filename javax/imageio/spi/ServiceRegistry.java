package javax.imageio.spi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;

public class ServiceRegistry
{
  private Map categoryMap = new HashMap();
  
  public ServiceRegistry(Iterator<Class<?>> paramIterator)
  {
    if (paramIterator == null) {
      throw new IllegalArgumentException("categories == null!");
    }
    while (paramIterator.hasNext())
    {
      Class localClass = (Class)paramIterator.next();
      SubRegistry localSubRegistry = new SubRegistry(this, localClass);
      categoryMap.put(localClass, localSubRegistry);
    }
  }
  
  public static <T> Iterator<T> lookupProviders(Class<T> paramClass, ClassLoader paramClassLoader)
  {
    if (paramClass == null) {
      throw new IllegalArgumentException("providerClass == null!");
    }
    return ServiceLoader.load(paramClass, paramClassLoader).iterator();
  }
  
  public static <T> Iterator<T> lookupProviders(Class<T> paramClass)
  {
    if (paramClass == null) {
      throw new IllegalArgumentException("providerClass == null!");
    }
    return ServiceLoader.load(paramClass).iterator();
  }
  
  public Iterator<Class<?>> getCategories()
  {
    Set localSet = categoryMap.keySet();
    return localSet.iterator();
  }
  
  private Iterator getSubRegistries(Object paramObject)
  {
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = categoryMap.keySet().iterator();
    while (localIterator.hasNext())
    {
      Class localClass = (Class)localIterator.next();
      if (localClass.isAssignableFrom(paramObject.getClass())) {
        localArrayList.add((SubRegistry)categoryMap.get(localClass));
      }
    }
    return localArrayList.iterator();
  }
  
  public <T> boolean registerServiceProvider(T paramT, Class<T> paramClass)
  {
    if (paramT == null) {
      throw new IllegalArgumentException("provider == null!");
    }
    SubRegistry localSubRegistry = (SubRegistry)categoryMap.get(paramClass);
    if (localSubRegistry == null) {
      throw new IllegalArgumentException("category unknown!");
    }
    if (!paramClass.isAssignableFrom(paramT.getClass())) {
      throw new ClassCastException();
    }
    return localSubRegistry.registerServiceProvider(paramT);
  }
  
  public void registerServiceProvider(Object paramObject)
  {
    if (paramObject == null) {
      throw new IllegalArgumentException("provider == null!");
    }
    Iterator localIterator = getSubRegistries(paramObject);
    while (localIterator.hasNext())
    {
      SubRegistry localSubRegistry = (SubRegistry)localIterator.next();
      localSubRegistry.registerServiceProvider(paramObject);
    }
  }
  
  public void registerServiceProviders(Iterator<?> paramIterator)
  {
    if (paramIterator == null) {
      throw new IllegalArgumentException("provider == null!");
    }
    while (paramIterator.hasNext()) {
      registerServiceProvider(paramIterator.next());
    }
  }
  
  public <T> boolean deregisterServiceProvider(T paramT, Class<T> paramClass)
  {
    if (paramT == null) {
      throw new IllegalArgumentException("provider == null!");
    }
    SubRegistry localSubRegistry = (SubRegistry)categoryMap.get(paramClass);
    if (localSubRegistry == null) {
      throw new IllegalArgumentException("category unknown!");
    }
    if (!paramClass.isAssignableFrom(paramT.getClass())) {
      throw new ClassCastException();
    }
    return localSubRegistry.deregisterServiceProvider(paramT);
  }
  
  public void deregisterServiceProvider(Object paramObject)
  {
    if (paramObject == null) {
      throw new IllegalArgumentException("provider == null!");
    }
    Iterator localIterator = getSubRegistries(paramObject);
    while (localIterator.hasNext())
    {
      SubRegistry localSubRegistry = (SubRegistry)localIterator.next();
      localSubRegistry.deregisterServiceProvider(paramObject);
    }
  }
  
  public boolean contains(Object paramObject)
  {
    if (paramObject == null) {
      throw new IllegalArgumentException("provider == null!");
    }
    Iterator localIterator = getSubRegistries(paramObject);
    while (localIterator.hasNext())
    {
      SubRegistry localSubRegistry = (SubRegistry)localIterator.next();
      if (localSubRegistry.contains(paramObject)) {
        return true;
      }
    }
    return false;
  }
  
  public <T> Iterator<T> getServiceProviders(Class<T> paramClass, boolean paramBoolean)
  {
    SubRegistry localSubRegistry = (SubRegistry)categoryMap.get(paramClass);
    if (localSubRegistry == null) {
      throw new IllegalArgumentException("category unknown!");
    }
    return localSubRegistry.getServiceProviders(paramBoolean);
  }
  
  public <T> Iterator<T> getServiceProviders(Class<T> paramClass, Filter paramFilter, boolean paramBoolean)
  {
    SubRegistry localSubRegistry = (SubRegistry)categoryMap.get(paramClass);
    if (localSubRegistry == null) {
      throw new IllegalArgumentException("category unknown!");
    }
    Iterator localIterator = getServiceProviders(paramClass, paramBoolean);
    return new FilterIterator(localIterator, paramFilter);
  }
  
  public <T> T getServiceProviderByClass(Class<T> paramClass)
  {
    if (paramClass == null) {
      throw new IllegalArgumentException("providerClass == null!");
    }
    Iterator localIterator = categoryMap.keySet().iterator();
    while (localIterator.hasNext())
    {
      Class localClass = (Class)localIterator.next();
      if (localClass.isAssignableFrom(paramClass))
      {
        SubRegistry localSubRegistry = (SubRegistry)categoryMap.get(localClass);
        Object localObject = localSubRegistry.getServiceProviderByClass(paramClass);
        if (localObject != null) {
          return (T)localObject;
        }
      }
    }
    return null;
  }
  
  public <T> boolean setOrdering(Class<T> paramClass, T paramT1, T paramT2)
  {
    if ((paramT1 == null) || (paramT2 == null)) {
      throw new IllegalArgumentException("provider is null!");
    }
    if (paramT1 == paramT2) {
      throw new IllegalArgumentException("providers are the same!");
    }
    SubRegistry localSubRegistry = (SubRegistry)categoryMap.get(paramClass);
    if (localSubRegistry == null) {
      throw new IllegalArgumentException("category unknown!");
    }
    if ((localSubRegistry.contains(paramT1)) && (localSubRegistry.contains(paramT2))) {
      return localSubRegistry.setOrdering(paramT1, paramT2);
    }
    return false;
  }
  
  public <T> boolean unsetOrdering(Class<T> paramClass, T paramT1, T paramT2)
  {
    if ((paramT1 == null) || (paramT2 == null)) {
      throw new IllegalArgumentException("provider is null!");
    }
    if (paramT1 == paramT2) {
      throw new IllegalArgumentException("providers are the same!");
    }
    SubRegistry localSubRegistry = (SubRegistry)categoryMap.get(paramClass);
    if (localSubRegistry == null) {
      throw new IllegalArgumentException("category unknown!");
    }
    if ((localSubRegistry.contains(paramT1)) && (localSubRegistry.contains(paramT2))) {
      return localSubRegistry.unsetOrdering(paramT1, paramT2);
    }
    return false;
  }
  
  public void deregisterAll(Class<?> paramClass)
  {
    SubRegistry localSubRegistry = (SubRegistry)categoryMap.get(paramClass);
    if (localSubRegistry == null) {
      throw new IllegalArgumentException("category unknown!");
    }
    localSubRegistry.clear();
  }
  
  public void deregisterAll()
  {
    Iterator localIterator = categoryMap.values().iterator();
    while (localIterator.hasNext())
    {
      SubRegistry localSubRegistry = (SubRegistry)localIterator.next();
      localSubRegistry.clear();
    }
  }
  
  public void finalize()
    throws Throwable
  {
    deregisterAll();
    super.finalize();
  }
  
  public static abstract interface Filter
  {
    public abstract boolean filter(Object paramObject);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\imageio\spi\ServiceRegistry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */