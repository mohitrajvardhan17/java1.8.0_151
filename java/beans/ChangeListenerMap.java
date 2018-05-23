package java.beans;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

abstract class ChangeListenerMap<L extends EventListener>
{
  private Map<String, L[]> map;
  
  ChangeListenerMap() {}
  
  protected abstract L[] newArray(int paramInt);
  
  protected abstract L newProxy(String paramString, L paramL);
  
  public final synchronized void add(String paramString, L paramL)
  {
    if (map == null) {
      map = new HashMap();
    }
    EventListener[] arrayOfEventListener1 = (EventListener[])map.get(paramString);
    int i = arrayOfEventListener1 != null ? arrayOfEventListener1.length : 0;
    EventListener[] arrayOfEventListener2 = newArray(i + 1);
    arrayOfEventListener2[i] = paramL;
    if (arrayOfEventListener1 != null) {
      System.arraycopy(arrayOfEventListener1, 0, arrayOfEventListener2, 0, i);
    }
    map.put(paramString, arrayOfEventListener2);
  }
  
  public final synchronized void remove(String paramString, L paramL)
  {
    if (map != null)
    {
      EventListener[] arrayOfEventListener1 = (EventListener[])map.get(paramString);
      if (arrayOfEventListener1 != null) {
        for (int i = 0; i < arrayOfEventListener1.length; i++) {
          if (paramL.equals(arrayOfEventListener1[i]))
          {
            int j = arrayOfEventListener1.length - 1;
            if (j > 0)
            {
              EventListener[] arrayOfEventListener2 = newArray(j);
              System.arraycopy(arrayOfEventListener1, 0, arrayOfEventListener2, 0, i);
              System.arraycopy(arrayOfEventListener1, i + 1, arrayOfEventListener2, i, j - i);
              map.put(paramString, arrayOfEventListener2);
              break;
            }
            map.remove(paramString);
            if (!map.isEmpty()) {
              break;
            }
            map = null;
            break;
          }
        }
      }
    }
  }
  
  public final synchronized L[] get(String paramString)
  {
    return map != null ? (EventListener[])map.get(paramString) : null;
  }
  
  public final void set(String paramString, L[] paramArrayOfL)
  {
    if (paramArrayOfL != null)
    {
      if (map == null) {
        map = new HashMap();
      }
      map.put(paramString, paramArrayOfL);
    }
    else if (map != null)
    {
      map.remove(paramString);
      if (map.isEmpty()) {
        map = null;
      }
    }
  }
  
  public final synchronized L[] getListeners()
  {
    if (map == null) {
      return newArray(0);
    }
    ArrayList localArrayList = new ArrayList();
    EventListener[] arrayOfEventListener = (EventListener[])map.get(null);
    Object localObject2;
    if (arrayOfEventListener != null) {
      for (localObject2 : arrayOfEventListener) {
        localArrayList.add(localObject2);
      }
    }
    ??? = map.entrySet().iterator();
    while (((Iterator)???).hasNext())
    {
      Map.Entry localEntry = (Map.Entry)((Iterator)???).next();
      String str = (String)localEntry.getKey();
      if (str != null) {
        for (EventListener localEventListener : (EventListener[])localEntry.getValue()) {
          localArrayList.add(newProxy(str, localEventListener));
        }
      }
    }
    return (EventListener[])localArrayList.toArray(newArray(localArrayList.size()));
  }
  
  public final L[] getListeners(String paramString)
  {
    if (paramString != null)
    {
      EventListener[] arrayOfEventListener = get(paramString);
      if (arrayOfEventListener != null) {
        return (EventListener[])arrayOfEventListener.clone();
      }
    }
    return newArray(0);
  }
  
  public final synchronized boolean hasListeners(String paramString)
  {
    if (map == null) {
      return false;
    }
    EventListener[] arrayOfEventListener = (EventListener[])map.get(null);
    return (arrayOfEventListener != null) || ((paramString != null) && (null != map.get(paramString)));
  }
  
  public final Set<Map.Entry<String, L[]>> getEntries()
  {
    return map != null ? map.entrySet() : Collections.emptySet();
  }
  
  public abstract L extract(L paramL);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\beans\ChangeListenerMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */