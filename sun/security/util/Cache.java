package sun.security.util;

import java.util.Arrays;
import java.util.Map;

public abstract class Cache<K, V>
{
  protected Cache() {}
  
  public abstract int size();
  
  public abstract void clear();
  
  public abstract void put(K paramK, V paramV);
  
  public abstract V get(Object paramObject);
  
  public abstract void remove(Object paramObject);
  
  public abstract void setCapacity(int paramInt);
  
  public abstract void setTimeout(int paramInt);
  
  public abstract void accept(CacheVisitor<K, V> paramCacheVisitor);
  
  public static <K, V> Cache<K, V> newSoftMemoryCache(int paramInt)
  {
    return new MemoryCache(true, paramInt);
  }
  
  public static <K, V> Cache<K, V> newSoftMemoryCache(int paramInt1, int paramInt2)
  {
    return new MemoryCache(true, paramInt1, paramInt2);
  }
  
  public static <K, V> Cache<K, V> newHardMemoryCache(int paramInt)
  {
    return new MemoryCache(false, paramInt);
  }
  
  public static <K, V> Cache<K, V> newNullCache()
  {
    return NullCache.INSTANCE;
  }
  
  public static <K, V> Cache<K, V> newHardMemoryCache(int paramInt1, int paramInt2)
  {
    return new MemoryCache(false, paramInt1, paramInt2);
  }
  
  public static abstract interface CacheVisitor<K, V>
  {
    public abstract void visit(Map<K, V> paramMap);
  }
  
  public static class EqualByteArray
  {
    private final byte[] b;
    private volatile int hash;
    
    public EqualByteArray(byte[] paramArrayOfByte)
    {
      b = paramArrayOfByte;
    }
    
    public int hashCode()
    {
      int i = hash;
      if (i == 0)
      {
        i = b.length + 1;
        for (int j = 0; j < b.length; j++) {
          i += (b[j] & 0xFF) * 37;
        }
        hash = i;
      }
      return i;
    }
    
    public boolean equals(Object paramObject)
    {
      if (this == paramObject) {
        return true;
      }
      if (!(paramObject instanceof EqualByteArray)) {
        return false;
      }
      EqualByteArray localEqualByteArray = (EqualByteArray)paramObject;
      return Arrays.equals(b, b);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\util\Cache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */