package javax.script;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class SimpleBindings
  implements Bindings
{
  private Map<String, Object> map;
  
  public SimpleBindings(Map<String, Object> paramMap)
  {
    if (paramMap == null) {
      throw new NullPointerException();
    }
    map = paramMap;
  }
  
  public SimpleBindings()
  {
    this(new HashMap());
  }
  
  public Object put(String paramString, Object paramObject)
  {
    checkKey(paramString);
    return map.put(paramString, paramObject);
  }
  
  public void putAll(Map<? extends String, ? extends Object> paramMap)
  {
    if (paramMap == null) {
      throw new NullPointerException("toMerge map is null");
    }
    Iterator localIterator = paramMap.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      String str = (String)localEntry.getKey();
      checkKey(str);
      put(str, localEntry.getValue());
    }
  }
  
  public void clear()
  {
    map.clear();
  }
  
  public boolean containsKey(Object paramObject)
  {
    checkKey(paramObject);
    return map.containsKey(paramObject);
  }
  
  public boolean containsValue(Object paramObject)
  {
    return map.containsValue(paramObject);
  }
  
  public Set<Map.Entry<String, Object>> entrySet()
  {
    return map.entrySet();
  }
  
  public Object get(Object paramObject)
  {
    checkKey(paramObject);
    return map.get(paramObject);
  }
  
  public boolean isEmpty()
  {
    return map.isEmpty();
  }
  
  public Set<String> keySet()
  {
    return map.keySet();
  }
  
  public Object remove(Object paramObject)
  {
    checkKey(paramObject);
    return map.remove(paramObject);
  }
  
  public int size()
  {
    return map.size();
  }
  
  public Collection<Object> values()
  {
    return map.values();
  }
  
  private void checkKey(Object paramObject)
  {
    if (paramObject == null) {
      throw new NullPointerException("key can not be null");
    }
    if (!(paramObject instanceof String)) {
      throw new ClassCastException("key should be a String");
    }
    if (paramObject.equals("")) {
      throw new IllegalArgumentException("key can not be empty");
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\script\SimpleBindings.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */