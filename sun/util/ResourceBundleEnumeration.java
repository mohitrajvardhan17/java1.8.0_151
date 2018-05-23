package sun.util;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

public class ResourceBundleEnumeration
  implements Enumeration<String>
{
  Set<String> set;
  Iterator<String> iterator;
  Enumeration<String> enumeration;
  String next = null;
  
  public ResourceBundleEnumeration(Set<String> paramSet, Enumeration<String> paramEnumeration)
  {
    set = paramSet;
    iterator = paramSet.iterator();
    enumeration = paramEnumeration;
  }
  
  public boolean hasMoreElements()
  {
    if (next == null) {
      if (iterator.hasNext()) {
        next = ((String)iterator.next());
      } else if (enumeration != null) {
        while ((next == null) && (enumeration.hasMoreElements()))
        {
          next = ((String)enumeration.nextElement());
          if (set.contains(next)) {
            next = null;
          }
        }
      }
    }
    return next != null;
  }
  
  public String nextElement()
  {
    if (hasMoreElements())
    {
      String str = next;
      next = null;
      return str;
    }
    throw new NoSuchElementException();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\util\ResourceBundleEnumeration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */