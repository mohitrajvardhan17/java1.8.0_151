package com.sun.org.apache.xerces.internal.util;

public class SymbolHash
{
  protected static final int TABLE_SIZE = 101;
  protected static final int MAX_HASH_COLLISIONS = 40;
  protected static final int MULTIPLIERS_SIZE = 32;
  protected static final int MULTIPLIERS_MASK = 31;
  protected int fTableSize;
  protected Entry[] fBuckets;
  protected int fNum = 0;
  protected int[] fHashMultipliers;
  
  public SymbolHash()
  {
    this(101);
  }
  
  public SymbolHash(int paramInt)
  {
    fTableSize = paramInt;
    fBuckets = new Entry[fTableSize];
  }
  
  public void put(Object paramObject1, Object paramObject2)
  {
    int i = 0;
    int j = hash(paramObject1);
    int k = j % fTableSize;
    for (Entry localEntry = fBuckets[k]; localEntry != null; localEntry = next)
    {
      if (paramObject1.equals(key))
      {
        value = paramObject2;
        return;
      }
      i++;
    }
    if (fNum >= fTableSize)
    {
      rehash();
      k = j % fTableSize;
    }
    else if ((i >= 40) && ((paramObject1 instanceof String)))
    {
      rebalance();
      k = hash(paramObject1) % fTableSize;
    }
    localEntry = new Entry(paramObject1, paramObject2, fBuckets[k]);
    fBuckets[k] = localEntry;
    fNum += 1;
  }
  
  public Object get(Object paramObject)
  {
    int i = hash(paramObject) % fTableSize;
    Entry localEntry = search(paramObject, i);
    if (localEntry != null) {
      return value;
    }
    return null;
  }
  
  public int getLength()
  {
    return fNum;
  }
  
  public int getValues(Object[] paramArrayOfObject, int paramInt)
  {
    int i = 0;
    int j = 0;
    while ((i < fTableSize) && (j < fNum))
    {
      for (Entry localEntry = fBuckets[i]; localEntry != null; localEntry = next)
      {
        paramArrayOfObject[(paramInt + j)] = value;
        j++;
      }
      i++;
    }
    return fNum;
  }
  
  public Object[] getEntries()
  {
    Object[] arrayOfObject = new Object[fNum << 1];
    int i = 0;
    int j = 0;
    while ((i < fTableSize) && (j < fNum << 1))
    {
      for (Entry localEntry = fBuckets[i]; localEntry != null; localEntry = next)
      {
        arrayOfObject[j] = key;
        arrayOfObject[(++j)] = value;
        j++;
      }
      i++;
    }
    return arrayOfObject;
  }
  
  public SymbolHash makeClone()
  {
    SymbolHash localSymbolHash = new SymbolHash(fTableSize);
    fNum = fNum;
    fHashMultipliers = (fHashMultipliers != null ? (int[])fHashMultipliers.clone() : null);
    for (int i = 0; i < fTableSize; i++) {
      if (fBuckets[i] != null) {
        fBuckets[i] = fBuckets[i].makeClone();
      }
    }
    return localSymbolHash;
  }
  
  public void clear()
  {
    for (int i = 0; i < fTableSize; i++) {
      fBuckets[i] = null;
    }
    fNum = 0;
    fHashMultipliers = null;
  }
  
  protected Entry search(Object paramObject, int paramInt)
  {
    for (Entry localEntry = fBuckets[paramInt]; localEntry != null; localEntry = next) {
      if (paramObject.equals(key)) {
        return localEntry;
      }
    }
    return null;
  }
  
  protected int hash(Object paramObject)
  {
    if ((fHashMultipliers == null) || (!(paramObject instanceof String))) {
      return paramObject.hashCode() & 0x7FFFFFFF;
    }
    return hash0((String)paramObject);
  }
  
  private int hash0(String paramString)
  {
    int i = 0;
    int j = paramString.length();
    int[] arrayOfInt = fHashMultipliers;
    for (int k = 0; k < j; k++) {
      i = i * arrayOfInt[(k & 0x1F)] + paramString.charAt(k);
    }
    return i & 0x7FFFFFFF;
  }
  
  protected void rehash()
  {
    rehashCommon((fBuckets.length << 1) + 1);
  }
  
  protected void rebalance()
  {
    if (fHashMultipliers == null) {
      fHashMultipliers = new int[32];
    }
    PrimeNumberSequenceGenerator.generateSequence(fHashMultipliers);
    rehashCommon(fBuckets.length);
  }
  
  private void rehashCommon(int paramInt)
  {
    int i = fBuckets.length;
    Entry[] arrayOfEntry1 = fBuckets;
    Entry[] arrayOfEntry2 = new Entry[paramInt];
    fBuckets = arrayOfEntry2;
    fTableSize = fBuckets.length;
    int j = i;
    while (j-- > 0)
    {
      Entry localEntry1 = arrayOfEntry1[j];
      while (localEntry1 != null)
      {
        Entry localEntry2 = localEntry1;
        localEntry1 = next;
        int k = hash(key) % paramInt;
        next = arrayOfEntry2[k];
        arrayOfEntry2[k] = localEntry2;
      }
    }
  }
  
  protected static final class Entry
  {
    public Object key;
    public Object value;
    public Entry next;
    
    public Entry()
    {
      key = null;
      value = null;
      next = null;
    }
    
    public Entry(Object paramObject1, Object paramObject2, Entry paramEntry)
    {
      key = paramObject1;
      value = paramObject2;
      next = paramEntry;
    }
    
    public Entry makeClone()
    {
      Entry localEntry = new Entry();
      key = key;
      value = value;
      if (next != null) {
        next = next.makeClone();
      }
      return localEntry;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\util\SymbolHash.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */