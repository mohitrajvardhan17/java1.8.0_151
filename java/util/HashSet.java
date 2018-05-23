package java.util;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import sun.misc.JavaOISAccess;
import sun.misc.SharedSecrets;

public class HashSet<E>
  extends AbstractSet<E>
  implements Set<E>, Cloneable, Serializable
{
  static final long serialVersionUID = -5024744406713321676L;
  private transient HashMap<E, Object> map;
  private static final Object PRESENT = new Object();
  
  public HashSet()
  {
    map = new HashMap();
  }
  
  public HashSet(Collection<? extends E> paramCollection)
  {
    map = new HashMap(Math.max((int)(paramCollection.size() / 0.75F) + 1, 16));
    addAll(paramCollection);
  }
  
  public HashSet(int paramInt, float paramFloat)
  {
    map = new HashMap(paramInt, paramFloat);
  }
  
  public HashSet(int paramInt)
  {
    map = new HashMap(paramInt);
  }
  
  HashSet(int paramInt, float paramFloat, boolean paramBoolean)
  {
    map = new LinkedHashMap(paramInt, paramFloat);
  }
  
  public Iterator<E> iterator()
  {
    return map.keySet().iterator();
  }
  
  public int size()
  {
    return map.size();
  }
  
  public boolean isEmpty()
  {
    return map.isEmpty();
  }
  
  public boolean contains(Object paramObject)
  {
    return map.containsKey(paramObject);
  }
  
  public boolean add(E paramE)
  {
    return map.put(paramE, PRESENT) == null;
  }
  
  public boolean remove(Object paramObject)
  {
    return map.remove(paramObject) == PRESENT;
  }
  
  public void clear()
  {
    map.clear();
  }
  
  public Object clone()
  {
    try
    {
      HashSet localHashSet = (HashSet)super.clone();
      map = ((HashMap)map.clone());
      return localHashSet;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new InternalError(localCloneNotSupportedException);
    }
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.defaultWriteObject();
    paramObjectOutputStream.writeInt(map.capacity());
    paramObjectOutputStream.writeFloat(map.loadFactor());
    paramObjectOutputStream.writeInt(map.size());
    Iterator localIterator = map.keySet().iterator();
    while (localIterator.hasNext())
    {
      Object localObject = localIterator.next();
      paramObjectOutputStream.writeObject(localObject);
    }
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    int i = paramObjectInputStream.readInt();
    if (i < 0) {
      throw new InvalidObjectException("Illegal capacity: " + i);
    }
    float f = paramObjectInputStream.readFloat();
    if ((f <= 0.0F) || (Float.isNaN(f))) {
      throw new InvalidObjectException("Illegal load factor: " + f);
    }
    int j = paramObjectInputStream.readInt();
    if (j < 0) {
      throw new InvalidObjectException("Illegal size: " + j);
    }
    i = (int)Math.min(j * Math.min(1.0F / f, 4.0F), 1.07374182E9F);
    SharedSecrets.getJavaOISAccess().checkArray(paramObjectInputStream, Map.Entry[].class, HashMap.tableSizeFor(i));
    map = ((this instanceof LinkedHashSet) ? new LinkedHashMap(i, f) : new HashMap(i, f));
    for (int k = 0; k < j; k++)
    {
      Object localObject = paramObjectInputStream.readObject();
      map.put(localObject, PRESENT);
    }
  }
  
  public Spliterator<E> spliterator()
  {
    return new HashMap.KeySpliterator(map, 0, -1, 0, 0);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\HashSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */