package com.oracle.webservices.internal.api.message;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import java.util.AbstractMap;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Collection;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public abstract class BaseDistributedPropertySet
  extends BasePropertySet
  implements DistributedPropertySet
{
  private final Map<Class<? extends PropertySet>, PropertySet> satellites = new IdentityHashMap();
  private final Map<String, Object> viewthis = super.createView();
  
  public BaseDistributedPropertySet() {}
  
  public void addSatellite(@NotNull PropertySet paramPropertySet)
  {
    addSatellite(paramPropertySet.getClass(), paramPropertySet);
  }
  
  public void addSatellite(@NotNull Class<? extends PropertySet> paramClass, @NotNull PropertySet paramPropertySet)
  {
    satellites.put(paramClass, paramPropertySet);
  }
  
  public void removeSatellite(PropertySet paramPropertySet)
  {
    satellites.remove(paramPropertySet.getClass());
  }
  
  public void copySatelliteInto(@NotNull DistributedPropertySet paramDistributedPropertySet)
  {
    Iterator localIterator = satellites.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      paramDistributedPropertySet.addSatellite((Class)localEntry.getKey(), (PropertySet)localEntry.getValue());
    }
  }
  
  public void copySatelliteInto(MessageContext paramMessageContext)
  {
    copySatelliteInto(paramMessageContext);
  }
  
  @Nullable
  public <T extends PropertySet> T getSatellite(Class<T> paramClass)
  {
    PropertySet localPropertySet1 = (PropertySet)satellites.get(paramClass);
    if (localPropertySet1 != null) {
      return localPropertySet1;
    }
    Iterator localIterator = satellites.values().iterator();
    while (localIterator.hasNext())
    {
      PropertySet localPropertySet2 = (PropertySet)localIterator.next();
      if (paramClass.isInstance(localPropertySet2)) {
        return (PropertySet)paramClass.cast(localPropertySet2);
      }
      if (DistributedPropertySet.class.isInstance(localPropertySet2))
      {
        localPropertySet1 = ((DistributedPropertySet)DistributedPropertySet.class.cast(localPropertySet2)).getSatellite(paramClass);
        if (localPropertySet1 != null) {
          return localPropertySet1;
        }
      }
    }
    return null;
  }
  
  public Map<Class<? extends PropertySet>, PropertySet> getSatellites()
  {
    return satellites;
  }
  
  public Object get(Object paramObject)
  {
    Iterator localIterator = satellites.values().iterator();
    while (localIterator.hasNext())
    {
      PropertySet localPropertySet = (PropertySet)localIterator.next();
      if (localPropertySet.supports(paramObject)) {
        return localPropertySet.get(paramObject);
      }
    }
    return super.get(paramObject);
  }
  
  public Object put(String paramString, Object paramObject)
  {
    Iterator localIterator = satellites.values().iterator();
    while (localIterator.hasNext())
    {
      PropertySet localPropertySet = (PropertySet)localIterator.next();
      if (localPropertySet.supports(paramString)) {
        return localPropertySet.put(paramString, paramObject);
      }
    }
    return super.put(paramString, paramObject);
  }
  
  public boolean containsKey(Object paramObject)
  {
    if (viewthis.containsKey(paramObject)) {
      return true;
    }
    Iterator localIterator = satellites.values().iterator();
    while (localIterator.hasNext())
    {
      PropertySet localPropertySet = (PropertySet)localIterator.next();
      if (localPropertySet.containsKey(paramObject)) {
        return true;
      }
    }
    return false;
  }
  
  public boolean supports(Object paramObject)
  {
    Iterator localIterator = satellites.values().iterator();
    while (localIterator.hasNext())
    {
      PropertySet localPropertySet = (PropertySet)localIterator.next();
      if (localPropertySet.supports(paramObject)) {
        return true;
      }
    }
    return super.supports(paramObject);
  }
  
  public Object remove(Object paramObject)
  {
    Iterator localIterator = satellites.values().iterator();
    while (localIterator.hasNext())
    {
      PropertySet localPropertySet = (PropertySet)localIterator.next();
      if (localPropertySet.supports(paramObject)) {
        return localPropertySet.remove(paramObject);
      }
    }
    return super.remove(paramObject);
  }
  
  protected void createEntrySet(Set<Map.Entry<String, Object>> paramSet)
  {
    super.createEntrySet(paramSet);
    Iterator localIterator = satellites.values().iterator();
    while (localIterator.hasNext())
    {
      PropertySet localPropertySet = (PropertySet)localIterator.next();
      ((BasePropertySet)localPropertySet).createEntrySet(paramSet);
    }
  }
  
  protected Map<String, Object> asMapLocal()
  {
    return viewthis;
  }
  
  protected boolean supportsLocal(Object paramObject)
  {
    return super.supports(paramObject);
  }
  
  protected Map<String, Object> createView()
  {
    return new DistributedMapView();
  }
  
  class DistributedMapView
    extends AbstractMap<String, Object>
  {
    DistributedMapView() {}
    
    public Object get(Object paramObject)
    {
      Iterator localIterator = satellites.values().iterator();
      while (localIterator.hasNext())
      {
        PropertySet localPropertySet = (PropertySet)localIterator.next();
        if (localPropertySet.supports(paramObject)) {
          return localPropertySet.get(paramObject);
        }
      }
      return viewthis.get(paramObject);
    }
    
    public int size()
    {
      int i = viewthis.size();
      Iterator localIterator = satellites.values().iterator();
      while (localIterator.hasNext())
      {
        PropertySet localPropertySet = (PropertySet)localIterator.next();
        i += localPropertySet.asMap().size();
      }
      return i;
    }
    
    public boolean containsKey(Object paramObject)
    {
      if (viewthis.containsKey(paramObject)) {
        return true;
      }
      Iterator localIterator = satellites.values().iterator();
      while (localIterator.hasNext())
      {
        PropertySet localPropertySet = (PropertySet)localIterator.next();
        if (localPropertySet.containsKey(paramObject)) {
          return true;
        }
      }
      return false;
    }
    
    public Set<Map.Entry<String, Object>> entrySet()
    {
      HashSet localHashSet = new HashSet();
      Iterator localIterator1 = satellites.values().iterator();
      Object localObject;
      while (localIterator1.hasNext())
      {
        localObject = (PropertySet)localIterator1.next();
        Iterator localIterator2 = ((PropertySet)localObject).asMap().entrySet().iterator();
        while (localIterator2.hasNext())
        {
          Map.Entry localEntry = (Map.Entry)localIterator2.next();
          localHashSet.add(new AbstractMap.SimpleImmutableEntry(localEntry.getKey(), localEntry.getValue()));
        }
      }
      localIterator1 = viewthis.entrySet().iterator();
      while (localIterator1.hasNext())
      {
        localObject = (Map.Entry)localIterator1.next();
        localHashSet.add(new AbstractMap.SimpleImmutableEntry(((Map.Entry)localObject).getKey(), ((Map.Entry)localObject).getValue()));
      }
      return localHashSet;
    }
    
    public Object put(String paramString, Object paramObject)
    {
      Iterator localIterator = satellites.values().iterator();
      while (localIterator.hasNext())
      {
        PropertySet localPropertySet = (PropertySet)localIterator.next();
        if (localPropertySet.supports(paramString)) {
          return localPropertySet.put(paramString, paramObject);
        }
      }
      return viewthis.put(paramString, paramObject);
    }
    
    public void clear()
    {
      satellites.clear();
      viewthis.clear();
    }
    
    public Object remove(Object paramObject)
    {
      Iterator localIterator = satellites.values().iterator();
      while (localIterator.hasNext())
      {
        PropertySet localPropertySet = (PropertySet)localIterator.next();
        if (localPropertySet.supports(paramObject)) {
          return localPropertySet.remove(paramObject);
        }
      }
      return viewthis.remove(paramObject);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\oracle\webservices\internal\api\message\BaseDistributedPropertySet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */