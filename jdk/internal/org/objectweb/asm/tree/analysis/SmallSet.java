package jdk.internal.org.objectweb.asm.tree.analysis;

import java.util.AbstractSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

class SmallSet<E>
  extends AbstractSet<E>
  implements Iterator<E>
{
  E e1;
  E e2;
  
  static final <T> Set<T> emptySet()
  {
    return new SmallSet(null, null);
  }
  
  SmallSet(E paramE1, E paramE2)
  {
    e1 = paramE1;
    e2 = paramE2;
  }
  
  public Iterator<E> iterator()
  {
    return new SmallSet(e1, e2);
  }
  
  public int size()
  {
    return e2 == null ? 1 : e1 == null ? 0 : 2;
  }
  
  public boolean hasNext()
  {
    return e1 != null;
  }
  
  public E next()
  {
    if (e1 == null) {
      throw new NoSuchElementException();
    }
    Object localObject = e1;
    e1 = e2;
    e2 = null;
    return (E)localObject;
  }
  
  public void remove() {}
  
  Set<E> union(SmallSet<E> paramSmallSet)
  {
    if (((e1 == e1) && (e2 == e2)) || ((e1 == e2) && (e2 == e1))) {
      return this;
    }
    if (e1 == null) {
      return this;
    }
    if (e1 == null) {
      return paramSmallSet;
    }
    if (e2 == null)
    {
      if (e2 == null) {
        return new SmallSet(e1, e1);
      }
      if ((e1 == e1) || (e1 == e2)) {
        return this;
      }
    }
    if ((e2 == null) && ((e1 == e1) || (e1 == e2))) {
      return paramSmallSet;
    }
    HashSet localHashSet = new HashSet(4);
    localHashSet.add(e1);
    if (e2 != null) {
      localHashSet.add(e2);
    }
    localHashSet.add(e1);
    if (e2 != null) {
      localHashSet.add(e2);
    }
    return localHashSet;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\jdk\internal\org\objectweb\asm\tree\analysis\SmallSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */