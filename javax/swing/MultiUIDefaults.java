package javax.swing;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;

class MultiUIDefaults
  extends UIDefaults
{
  private UIDefaults[] tables;
  
  public MultiUIDefaults(UIDefaults[] paramArrayOfUIDefaults)
  {
    tables = paramArrayOfUIDefaults;
  }
  
  public MultiUIDefaults()
  {
    tables = new UIDefaults[0];
  }
  
  public Object get(Object paramObject)
  {
    Object localObject = super.get(paramObject);
    if (localObject != null) {
      return localObject;
    }
    for (UIDefaults localUIDefaults : tables)
    {
      localObject = localUIDefaults != null ? localUIDefaults.get(paramObject) : null;
      if (localObject != null) {
        return localObject;
      }
    }
    return null;
  }
  
  public Object get(Object paramObject, Locale paramLocale)
  {
    Object localObject = super.get(paramObject, paramLocale);
    if (localObject != null) {
      return localObject;
    }
    for (UIDefaults localUIDefaults : tables)
    {
      localObject = localUIDefaults != null ? localUIDefaults.get(paramObject, paramLocale) : null;
      if (localObject != null) {
        return localObject;
      }
    }
    return null;
  }
  
  public int size()
  {
    return entrySet().size();
  }
  
  public boolean isEmpty()
  {
    return size() == 0;
  }
  
  public Enumeration<Object> keys()
  {
    return new MultiUIDefaultsEnumerator(MultiUIDefaults.MultiUIDefaultsEnumerator.Type.KEYS, entrySet());
  }
  
  public Enumeration<Object> elements()
  {
    return new MultiUIDefaultsEnumerator(MultiUIDefaults.MultiUIDefaultsEnumerator.Type.ELEMENTS, entrySet());
  }
  
  public Set<Map.Entry<Object, Object>> entrySet()
  {
    HashSet localHashSet = new HashSet();
    for (int i = tables.length - 1; i >= 0; i--) {
      if (tables[i] != null) {
        localHashSet.addAll(tables[i].entrySet());
      }
    }
    localHashSet.addAll(super.entrySet());
    return localHashSet;
  }
  
  protected void getUIError(String paramString)
  {
    if (tables.length > 0) {
      tables[0].getUIError(paramString);
    } else {
      super.getUIError(paramString);
    }
  }
  
  public Object remove(Object paramObject)
  {
    Object localObject1 = null;
    for (int i = tables.length - 1; i >= 0; i--) {
      if (tables[i] != null)
      {
        Object localObject3 = tables[i].remove(paramObject);
        if (localObject3 != null) {
          localObject1 = localObject3;
        }
      }
    }
    Object localObject2 = super.remove(paramObject);
    if (localObject2 != null) {
      localObject1 = localObject2;
    }
    return localObject1;
  }
  
  public void clear()
  {
    super.clear();
    for (UIDefaults localUIDefaults : tables) {
      if (localUIDefaults != null) {
        localUIDefaults.clear();
      }
    }
  }
  
  public synchronized String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append("{");
    Enumeration localEnumeration = keys();
    while (localEnumeration.hasMoreElements())
    {
      Object localObject = localEnumeration.nextElement();
      localStringBuffer.append(localObject + "=" + get(localObject) + ", ");
    }
    int i = localStringBuffer.length();
    if (i > 1) {
      localStringBuffer.delete(i - 2, i);
    }
    localStringBuffer.append("}");
    return localStringBuffer.toString();
  }
  
  private static class MultiUIDefaultsEnumerator
    implements Enumeration<Object>
  {
    private Iterator<Map.Entry<Object, Object>> iterator;
    private Type type;
    
    MultiUIDefaultsEnumerator(Type paramType, Set<Map.Entry<Object, Object>> paramSet)
    {
      type = paramType;
      iterator = paramSet.iterator();
    }
    
    public boolean hasMoreElements()
    {
      return iterator.hasNext();
    }
    
    public Object nextElement()
    {
      switch (MultiUIDefaults.1.$SwitchMap$javax$swing$MultiUIDefaults$MultiUIDefaultsEnumerator$Type[type.ordinal()])
      {
      case 1: 
        return ((Map.Entry)iterator.next()).getKey();
      case 2: 
        return ((Map.Entry)iterator.next()).getValue();
      }
      return null;
    }
    
    public static enum Type
    {
      KEYS,  ELEMENTS;
      
      private Type() {}
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\MultiUIDefaults.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */