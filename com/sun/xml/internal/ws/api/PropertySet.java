package com.sun.xml.internal.ws.api;

import com.oracle.webservices.internal.api.message.BasePropertySet;
import com.oracle.webservices.internal.api.message.BasePropertySet.Accessor;
import com.oracle.webservices.internal.api.message.BasePropertySet.PropertyMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

/**
 * @deprecated
 */
public abstract class PropertySet
  extends BasePropertySet
{
  public PropertySet() {}
  
  /**
   * @deprecated
   */
  protected static PropertyMap parse(Class paramClass)
  {
    BasePropertySet.PropertyMap localPropertyMap = BasePropertySet.parse(paramClass);
    PropertyMap localPropertyMap1 = new PropertyMap();
    localPropertyMap1.putAll(localPropertyMap);
    return localPropertyMap1;
  }
  
  public Object get(Object paramObject)
  {
    BasePropertySet.Accessor localAccessor = (BasePropertySet.Accessor)getPropertyMap().get(paramObject);
    if (localAccessor != null) {
      return localAccessor.get(this);
    }
    throw new IllegalArgumentException("Undefined property " + paramObject);
  }
  
  public Object put(String paramString, Object paramObject)
  {
    BasePropertySet.Accessor localAccessor = (BasePropertySet.Accessor)getPropertyMap().get(paramString);
    if (localAccessor != null)
    {
      Object localObject = localAccessor.get(this);
      localAccessor.set(this, paramObject);
      return localObject;
    }
    throw new IllegalArgumentException("Undefined property " + paramString);
  }
  
  public boolean supports(Object paramObject)
  {
    return getPropertyMap().containsKey(paramObject);
  }
  
  public Object remove(Object paramObject)
  {
    BasePropertySet.Accessor localAccessor = (BasePropertySet.Accessor)getPropertyMap().get(paramObject);
    if (localAccessor != null)
    {
      Object localObject = localAccessor.get(this);
      localAccessor.set(this, null);
      return localObject;
    }
    throw new IllegalArgumentException("Undefined property " + paramObject);
  }
  
  protected void createEntrySet(Set<Map.Entry<String, Object>> paramSet)
  {
    Iterator localIterator = getPropertyMap().entrySet().iterator();
    while (localIterator.hasNext())
    {
      final Map.Entry localEntry = (Map.Entry)localIterator.next();
      paramSet.add(new Map.Entry()
      {
        public String getKey()
        {
          return (String)localEntry.getKey();
        }
        
        public Object getValue()
        {
          return ((BasePropertySet.Accessor)localEntry.getValue()).get(PropertySet.this);
        }
        
        public Object setValue(Object paramAnonymousObject)
        {
          BasePropertySet.Accessor localAccessor = (BasePropertySet.Accessor)localEntry.getValue();
          Object localObject = localAccessor.get(PropertySet.this);
          localAccessor.set(PropertySet.this, paramAnonymousObject);
          return localObject;
        }
      });
    }
  }
  
  protected abstract PropertyMap getPropertyMap();
  
  /**
   * @deprecated
   */
  protected static class PropertyMap
    extends BasePropertySet.PropertyMap
  {
    protected PropertyMap() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\PropertySet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */