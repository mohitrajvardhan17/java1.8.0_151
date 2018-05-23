package sun.util.resources;

import java.util.AbstractSet;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicMarkableReference;

public abstract class ParallelListResourceBundle
  extends ResourceBundle
{
  private volatile ConcurrentMap<String, Object> lookup;
  private volatile Set<String> keyset;
  private final AtomicMarkableReference<Object[][]> parallelContents = new AtomicMarkableReference(null, false);
  
  protected ParallelListResourceBundle() {}
  
  protected abstract Object[][] getContents();
  
  ResourceBundle getParent()
  {
    return parent;
  }
  
  public void setParallelContents(OpenListResourceBundle paramOpenListResourceBundle)
  {
    if (paramOpenListResourceBundle == null) {
      parallelContents.compareAndSet(null, null, false, true);
    } else {
      parallelContents.compareAndSet(null, paramOpenListResourceBundle.getContents(), false, false);
    }
  }
  
  boolean areParallelContentsComplete()
  {
    if (parallelContents.isMarked()) {
      return true;
    }
    boolean[] arrayOfBoolean = new boolean[1];
    Object[][] arrayOfObject = (Object[][])parallelContents.get(arrayOfBoolean);
    return (arrayOfObject != null) || (arrayOfBoolean[0] != 0);
  }
  
  protected Object handleGetObject(String paramString)
  {
    if (paramString == null) {
      throw new NullPointerException();
    }
    loadLookupTablesIfNecessary();
    return lookup.get(paramString);
  }
  
  public Enumeration<String> getKeys()
  {
    return Collections.enumeration(keySet());
  }
  
  public boolean containsKey(String paramString)
  {
    return keySet().contains(paramString);
  }
  
  protected Set<String> handleKeySet()
  {
    loadLookupTablesIfNecessary();
    return lookup.keySet();
  }
  
  public Set<String> keySet()
  {
    Object localObject1;
    while ((localObject1 = keyset) == null)
    {
      localObject1 = new KeySet(handleKeySet(), parent, null);
      synchronized (this)
      {
        if (keyset == null) {
          keyset = ((Set)localObject1);
        }
      }
    }
    return (Set<String>)localObject1;
  }
  
  synchronized void resetKeySet()
  {
    keyset = null;
  }
  
  void loadLookupTablesIfNecessary()
  {
    Object localObject1 = lookup;
    if (localObject1 == null)
    {
      localObject1 = new ConcurrentHashMap();
      for (Object[] arrayOfObject3 : getContents()) {
        ((ConcurrentMap)localObject1).put((String)arrayOfObject3[0], arrayOfObject3[1]);
      }
    }
    ??? = (Object[][])parallelContents.getReference();
    if (??? != null)
    {
      for (Object[] arrayOfObject4 : ???) {
        ((ConcurrentMap)localObject1).putIfAbsent((String)arrayOfObject4[0], arrayOfObject4[1]);
      }
      parallelContents.set(null, true);
    }
    if (lookup == null) {
      synchronized (this)
      {
        if (lookup == null) {
          lookup = ((ConcurrentMap)localObject1);
        }
      }
    }
  }
  
  private static class KeySet
    extends AbstractSet<String>
  {
    private final Set<String> set;
    private final ResourceBundle parent;
    
    private KeySet(Set<String> paramSet, ResourceBundle paramResourceBundle)
    {
      set = paramSet;
      parent = paramResourceBundle;
    }
    
    public boolean contains(Object paramObject)
    {
      if (set.contains(paramObject)) {
        return true;
      }
      return parent != null ? parent.containsKey((String)paramObject) : false;
    }
    
    public Iterator<String> iterator()
    {
      if (parent == null) {
        return set.iterator();
      }
      new Iterator()
      {
        private Iterator<String> itr = set.iterator();
        private boolean usingParent;
        
        public boolean hasNext()
        {
          if (itr.hasNext()) {
            return true;
          }
          if (!usingParent)
          {
            HashSet localHashSet = new HashSet(parent.keySet());
            localHashSet.removeAll(set);
            itr = localHashSet.iterator();
            usingParent = true;
          }
          return itr.hasNext();
        }
        
        public String next()
        {
          if (hasNext()) {
            return (String)itr.next();
          }
          throw new NoSuchElementException();
        }
        
        public void remove()
        {
          throw new UnsupportedOperationException();
        }
      };
    }
    
    public int size()
    {
      if (parent == null) {
        return set.size();
      }
      HashSet localHashSet = new HashSet(set);
      localHashSet.addAll(parent.keySet());
      return localHashSet.size();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\util\resources\ParallelListResourceBundle.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */