package sun.net.httpserver;

import com.sun.net.httpserver.Headers;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

class UnmodifiableHeaders
  extends Headers
{
  Headers map;
  
  UnmodifiableHeaders(Headers paramHeaders)
  {
    map = paramHeaders;
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
    return map.containsKey(paramObject);
  }
  
  public boolean containsValue(Object paramObject)
  {
    return map.containsValue(paramObject);
  }
  
  public List<String> get(Object paramObject)
  {
    return map.get(paramObject);
  }
  
  public String getFirst(String paramString)
  {
    return map.getFirst(paramString);
  }
  
  public List<String> put(String paramString, List<String> paramList)
  {
    return map.put(paramString, paramList);
  }
  
  public void add(String paramString1, String paramString2)
  {
    throw new UnsupportedOperationException("unsupported operation");
  }
  
  public void set(String paramString1, String paramString2)
  {
    throw new UnsupportedOperationException("unsupported operation");
  }
  
  public List<String> remove(Object paramObject)
  {
    throw new UnsupportedOperationException("unsupported operation");
  }
  
  public void putAll(Map<? extends String, ? extends List<String>> paramMap)
  {
    throw new UnsupportedOperationException("unsupported operation");
  }
  
  public void clear()
  {
    throw new UnsupportedOperationException("unsupported operation");
  }
  
  public Set<String> keySet()
  {
    return Collections.unmodifiableSet(map.keySet());
  }
  
  public Collection<List<String>> values()
  {
    return Collections.unmodifiableCollection(map.values());
  }
  
  public Set<Map.Entry<String, List<String>>> entrySet()
  {
    return Collections.unmodifiableSet(map.entrySet());
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\httpserver\UnmodifiableHeaders.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */