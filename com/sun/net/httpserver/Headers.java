package com.sun.net.httpserver;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import jdk.Exported;

@Exported
public class Headers
  implements Map<String, List<String>>
{
  HashMap<String, List<String>> map = new HashMap(32);
  
  public Headers() {}
  
  private String normalize(String paramString)
  {
    if (paramString == null) {
      return null;
    }
    int i = paramString.length();
    if (i == 0) {
      return paramString;
    }
    char[] arrayOfChar = paramString.toCharArray();
    if ((arrayOfChar[0] >= 'a') && (arrayOfChar[0] <= 'z')) {
      arrayOfChar[0] = ((char)(arrayOfChar[0] - ' '));
    }
    for (int j = 1; j < i; j++) {
      if ((arrayOfChar[j] >= 'A') && (arrayOfChar[j] <= 'Z')) {
        arrayOfChar[j] = ((char)(arrayOfChar[j] + ' '));
      }
    }
    return new String(arrayOfChar);
  }
  
  public int size()
  {
    return map.size();
  }
  
  public boolean isEmpty()
  {
    return map.isEmpty();
  }
  
  public boolean containsKey(Object paramObject)
  {
    if (paramObject == null) {
      return false;
    }
    if (!(paramObject instanceof String)) {
      return false;
    }
    return map.containsKey(normalize((String)paramObject));
  }
  
  public boolean containsValue(Object paramObject)
  {
    return map.containsValue(paramObject);
  }
  
  public List<String> get(Object paramObject)
  {
    return (List)map.get(normalize((String)paramObject));
  }
  
  public String getFirst(String paramString)
  {
    List localList = (List)map.get(normalize(paramString));
    if (localList == null) {
      return null;
    }
    return (String)localList.get(0);
  }
  
  public List<String> put(String paramString, List<String> paramList)
  {
    return (List)map.put(normalize(paramString), paramList);
  }
  
  public void add(String paramString1, String paramString2)
  {
    String str = normalize(paramString1);
    Object localObject = (List)map.get(str);
    if (localObject == null)
    {
      localObject = new LinkedList();
      map.put(str, localObject);
    }
    ((List)localObject).add(paramString2);
  }
  
  public void set(String paramString1, String paramString2)
  {
    LinkedList localLinkedList = new LinkedList();
    localLinkedList.add(paramString2);
    put(paramString1, localLinkedList);
  }
  
  public List<String> remove(Object paramObject)
  {
    return (List)map.remove(normalize((String)paramObject));
  }
  
  public void putAll(Map<? extends String, ? extends List<String>> paramMap)
  {
    map.putAll(paramMap);
  }
  
  public void clear()
  {
    map.clear();
  }
  
  public Set<String> keySet()
  {
    return map.keySet();
  }
  
  public Collection<List<String>> values()
  {
    return map.values();
  }
  
  public Set<Map.Entry<String, List<String>>> entrySet()
  {
    return map.entrySet();
  }
  
  public boolean equals(Object paramObject)
  {
    return map.equals(paramObject);
  }
  
  public int hashCode()
  {
    return map.hashCode();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\net\httpserver\Headers.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */