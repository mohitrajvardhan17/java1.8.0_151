package sun.util;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;

public abstract class PreHashedMap<V>
  extends AbstractMap<String, V>
{
  private final int rows;
  private final int size;
  private final int shift;
  private final int mask;
  private final Object[] ht;
  
  protected PreHashedMap(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    rows = paramInt1;
    size = paramInt2;
    shift = paramInt3;
    mask = paramInt4;
    ht = new Object[paramInt1];
    init(ht);
  }
  
  protected abstract void init(Object[] paramArrayOfObject);
  
  private V toV(Object paramObject)
  {
    return (V)paramObject;
  }
  
  public V get(Object paramObject)
  {
    int i = paramObject.hashCode() >> shift & mask;
    Object[] arrayOfObject = (Object[])ht[i];
    if (arrayOfObject == null) {
      return null;
    }
    for (;;)
    {
      if (arrayOfObject[0].equals(paramObject)) {
        return (V)toV(arrayOfObject[1]);
      }
      if (arrayOfObject.length < 3) {
        return null;
      }
      arrayOfObject = (Object[])arrayOfObject[2];
    }
  }
  
  public V put(String paramString, V paramV)
  {
    int i = paramString.hashCode() >> shift & mask;
    Object[] arrayOfObject = (Object[])ht[i];
    if (arrayOfObject == null) {
      throw new UnsupportedOperationException(paramString);
    }
    for (;;)
    {
      if (arrayOfObject[0].equals(paramString))
      {
        Object localObject = toV(arrayOfObject[1]);
        arrayOfObject[1] = paramV;
        return (V)localObject;
      }
      if (arrayOfObject.length < 3) {
        throw new UnsupportedOperationException(paramString);
      }
      arrayOfObject = (Object[])arrayOfObject[2];
    }
  }
  
  public Set<String> keySet()
  {
    new AbstractSet()
    {
      public int size()
      {
        return size;
      }
      
      public Iterator<String> iterator()
      {
        new Iterator()
        {
          private int i = -1;
          Object[] a = null;
          String cur = null;
          
          private boolean findNext()
          {
            if (a != null)
            {
              if (a.length == 3)
              {
                a = ((Object[])a[2]);
                cur = ((String)a[0]);
                return true;
              }
              i += 1;
              a = null;
            }
            cur = null;
            if (i >= rows) {
              return false;
            }
            if ((i < 0) || (ht[i] == null)) {
              do
              {
                if (++i >= rows) {
                  return false;
                }
              } while (ht[i] == null);
            }
            a = ((Object[])ht[i]);
            cur = ((String)a[0]);
            return true;
          }
          
          public boolean hasNext()
          {
            if (cur != null) {
              return true;
            }
            return findNext();
          }
          
          public String next()
          {
            if ((cur == null) && (!findNext())) {
              throw new NoSuchElementException();
            }
            String str = cur;
            cur = null;
            return str;
          }
          
          public void remove()
          {
            throw new UnsupportedOperationException();
          }
        };
      }
    };
  }
  
  public Set<Map.Entry<String, V>> entrySet()
  {
    new AbstractSet()
    {
      public int size()
      {
        return size;
      }
      
      public Iterator<Map.Entry<String, V>> iterator()
      {
        new Iterator()
        {
          final Iterator<String> i = keySet().iterator();
          
          public boolean hasNext()
          {
            return i.hasNext();
          }
          
          public Map.Entry<String, V> next()
          {
            new Map.Entry()
            {
              String k = (String)i.next();
              
              public String getKey()
              {
                return k;
              }
              
              public V getValue()
              {
                return (V)get(k);
              }
              
              public int hashCode()
              {
                Object localObject = get(k);
                return k.hashCode() + (localObject == null ? 0 : localObject.hashCode());
              }
              
              public boolean equals(Object paramAnonymous3Object)
              {
                if (paramAnonymous3Object == this) {
                  return true;
                }
                if (!(paramAnonymous3Object instanceof Map.Entry)) {
                  return false;
                }
                Map.Entry localEntry = (Map.Entry)paramAnonymous3Object;
                return (getKey() == null ? localEntry.getKey() == null : getKey().equals(localEntry.getKey())) && (getValue() == null ? localEntry.getValue() == null : getValue().equals(localEntry.getValue()));
              }
              
              public V setValue(V paramAnonymous3V)
              {
                throw new UnsupportedOperationException();
              }
            };
          }
          
          public void remove()
          {
            throw new UnsupportedOperationException();
          }
        };
      }
    };
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\util\PreHashedMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */