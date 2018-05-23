package java.util;

import java.io.Serializable;

public class LinkedHashSet<E>
  extends HashSet<E>
  implements Set<E>, Cloneable, Serializable
{
  private static final long serialVersionUID = -2851667679971038690L;
  
  public LinkedHashSet(int paramInt, float paramFloat)
  {
    super(paramInt, paramFloat, true);
  }
  
  public LinkedHashSet(int paramInt)
  {
    super(paramInt, 0.75F, true);
  }
  
  public LinkedHashSet()
  {
    super(16, 0.75F, true);
  }
  
  public LinkedHashSet(Collection<? extends E> paramCollection)
  {
    super(Math.max(2 * paramCollection.size(), 11), 0.75F, true);
    addAll(paramCollection);
  }
  
  public Spliterator<E> spliterator()
  {
    return Spliterators.spliterator(this, 17);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\LinkedHashSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */