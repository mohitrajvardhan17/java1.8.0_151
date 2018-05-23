package com.sun.beans.finder;

import java.beans.PersistenceDelegate;
import java.util.HashMap;
import java.util.Map;

public final class PersistenceDelegateFinder
  extends InstanceFinder<PersistenceDelegate>
{
  private final Map<Class<?>, PersistenceDelegate> registry = new HashMap();
  
  public PersistenceDelegateFinder()
  {
    super(PersistenceDelegate.class, true, "PersistenceDelegate", new String[0]);
  }
  
  public void register(Class<?> paramClass, PersistenceDelegate paramPersistenceDelegate)
  {
    synchronized (registry)
    {
      if (paramPersistenceDelegate != null) {
        registry.put(paramClass, paramPersistenceDelegate);
      } else {
        registry.remove(paramClass);
      }
    }
  }
  
  public PersistenceDelegate find(Class<?> paramClass)
  {
    PersistenceDelegate localPersistenceDelegate;
    synchronized (registry)
    {
      localPersistenceDelegate = (PersistenceDelegate)registry.get(paramClass);
    }
    return localPersistenceDelegate != null ? localPersistenceDelegate : (PersistenceDelegate)super.find(paramClass);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\beans\finder\PersistenceDelegateFinder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */