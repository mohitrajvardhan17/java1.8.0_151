package com.sun.xml.internal.ws.transport;

import java.io.Serializable;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

public class Headers
  extends TreeMap<String, List<String>>
{
  private static final InsensitiveComparator INSTANCE = new InsensitiveComparator(null);
  
  public Headers()
  {
    super(INSTANCE);
  }
  
  public void add(String paramString1, String paramString2)
  {
    Object localObject = (List)get(paramString1);
    if (localObject == null)
    {
      localObject = new LinkedList();
      put(paramString1, localObject);
    }
    ((List)localObject).add(paramString2);
  }
  
  public String getFirst(String paramString)
  {
    List localList = (List)get(paramString);
    return localList == null ? null : (String)localList.get(0);
  }
  
  public void set(String paramString1, String paramString2)
  {
    LinkedList localLinkedList = new LinkedList();
    localLinkedList.add(paramString2);
    put(paramString1, localLinkedList);
  }
  
  private static final class InsensitiveComparator
    implements Comparator<String>, Serializable
  {
    private InsensitiveComparator() {}
    
    public int compare(String paramString1, String paramString2)
    {
      if ((paramString1 == null) && (paramString2 == null)) {
        return 0;
      }
      if (paramString1 == null) {
        return -1;
      }
      if (paramString2 == null) {
        return 1;
      }
      return paramString1.compareToIgnoreCase(paramString2);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\transport\Headers.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */