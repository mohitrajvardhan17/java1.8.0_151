package sun.misc;

public abstract class LRUCache<N, V>
{
  private V[] oa = null;
  private final int size;
  
  public LRUCache(int paramInt)
  {
    size = paramInt;
  }
  
  protected abstract V create(N paramN);
  
  protected abstract boolean hasName(V paramV, N paramN);
  
  public static void moveToFront(Object[] paramArrayOfObject, int paramInt)
  {
    Object localObject = paramArrayOfObject[paramInt];
    for (int i = paramInt; i > 0; i--) {
      paramArrayOfObject[i] = paramArrayOfObject[(i - 1)];
    }
    paramArrayOfObject[0] = localObject;
  }
  
  public V forName(N paramN)
  {
    if (oa == null)
    {
      Object[] arrayOfObject = (Object[])new Object[size];
      oa = arrayOfObject;
    }
    else
    {
      for (int i = 0; i < oa.length; i++)
      {
        Object localObject2 = oa[i];
        if ((localObject2 != null) && (hasName(localObject2, paramN)))
        {
          if (i > 0) {
            moveToFront(oa, i);
          }
          return (V)localObject2;
        }
      }
    }
    Object localObject1 = create(paramN);
    oa[(oa.length - 1)] = localObject1;
    moveToFront(oa, oa.length - 1);
    return (V)localObject1;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\misc\LRUCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */