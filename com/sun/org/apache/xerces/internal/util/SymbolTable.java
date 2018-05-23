package com.sun.org.apache.xerces.internal.util;

public class SymbolTable
{
  protected static final int TABLE_SIZE = 101;
  protected static final int MAX_HASH_COLLISIONS = 40;
  protected static final int MULTIPLIERS_SIZE = 32;
  protected static final int MULTIPLIERS_MASK = 31;
  protected Entry[] fBuckets = null;
  protected int fTableSize;
  protected transient int fCount;
  protected int fThreshold;
  protected float fLoadFactor;
  protected final int fCollisionThreshold;
  protected int[] fHashMultipliers;
  
  public SymbolTable(int paramInt, float paramFloat)
  {
    if (paramInt < 0) {
      throw new IllegalArgumentException("Illegal Capacity: " + paramInt);
    }
    if ((paramFloat <= 0.0F) || (Float.isNaN(paramFloat))) {
      throw new IllegalArgumentException("Illegal Load: " + paramFloat);
    }
    if (paramInt == 0) {
      paramInt = 1;
    }
    fLoadFactor = paramFloat;
    fTableSize = paramInt;
    fBuckets = new Entry[fTableSize];
    fThreshold = ((int)(fTableSize * paramFloat));
    fCollisionThreshold = ((int)(40.0F * paramFloat));
    fCount = 0;
  }
  
  public SymbolTable(int paramInt)
  {
    this(paramInt, 0.75F);
  }
  
  public SymbolTable()
  {
    this(101, 0.75F);
  }
  
  public String addSymbol(String paramString)
  {
    int i = 0;
    int j = hash(paramString) % fTableSize;
    for (Entry localEntry = fBuckets[j]; localEntry != null; localEntry = next)
    {
      if (symbol.equals(paramString)) {
        return symbol;
      }
      i++;
    }
    return addSymbol0(paramString, j, i);
  }
  
  private String addSymbol0(String paramString, int paramInt1, int paramInt2)
  {
    if (fCount >= fThreshold)
    {
      rehash();
      paramInt1 = hash(paramString) % fTableSize;
    }
    else if (paramInt2 >= fCollisionThreshold)
    {
      rebalance();
      paramInt1 = hash(paramString) % fTableSize;
    }
    Entry localEntry = new Entry(paramString, fBuckets[paramInt1]);
    fBuckets[paramInt1] = localEntry;
    fCount += 1;
    return symbol;
  }
  
  public String addSymbol(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    int i = 0;
    int j = hash(paramArrayOfChar, paramInt1, paramInt2) % fTableSize;
    label88:
    for (Entry localEntry = fBuckets[j]; localEntry != null; localEntry = next)
    {
      if (paramInt2 == characters.length)
      {
        for (int k = 0; k < paramInt2; k++) {
          if (paramArrayOfChar[(paramInt1 + k)] != characters[k])
          {
            i++;
            break label88;
          }
        }
        return symbol;
      }
      i++;
    }
    return addSymbol0(paramArrayOfChar, paramInt1, paramInt2, j, i);
  }
  
  private String addSymbol0(char[] paramArrayOfChar, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (fCount >= fThreshold)
    {
      rehash();
      paramInt3 = hash(paramArrayOfChar, paramInt1, paramInt2) % fTableSize;
    }
    else if (paramInt4 >= fCollisionThreshold)
    {
      rebalance();
      paramInt3 = hash(paramArrayOfChar, paramInt1, paramInt2) % fTableSize;
    }
    Entry localEntry = new Entry(paramArrayOfChar, paramInt1, paramInt2, fBuckets[paramInt3]);
    fBuckets[paramInt3] = localEntry;
    fCount += 1;
    return symbol;
  }
  
  public int hash(String paramString)
  {
    if (fHashMultipliers == null) {
      return paramString.hashCode() & 0x7FFFFFFF;
    }
    return hash0(paramString);
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
  
  public int hash(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    if (fHashMultipliers == null)
    {
      int i = 0;
      for (int j = 0; j < paramInt2; j++) {
        i = i * 31 + paramArrayOfChar[(paramInt1 + j)];
      }
      return i & 0x7FFFFFFF;
    }
    return hash0(paramArrayOfChar, paramInt1, paramInt2);
  }
  
  private int hash0(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    int i = 0;
    int[] arrayOfInt = fHashMultipliers;
    for (int j = 0; j < paramInt2; j++) {
      i = i * arrayOfInt[(j & 0x1F)] + paramArrayOfChar[(paramInt1 + j)];
    }
    return i & 0x7FFFFFFF;
  }
  
  protected void rehash()
  {
    rehashCommon(fBuckets.length * 2 + 1);
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
    fThreshold = ((int)(paramInt * fLoadFactor));
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
        int k = hash(symbol) % paramInt;
        next = arrayOfEntry2[k];
        arrayOfEntry2[k] = localEntry2;
      }
    }
  }
  
  public boolean containsSymbol(String paramString)
  {
    int i = hash(paramString) % fTableSize;
    int j = paramString.length();
    label76:
    for (Entry localEntry = fBuckets[i]; localEntry != null; localEntry = next) {
      if (j == characters.length)
      {
        for (int k = 0; k < j; k++) {
          if (paramString.charAt(k) != characters[k]) {
            break label76;
          }
        }
        return true;
      }
    }
    return false;
  }
  
  public boolean containsSymbol(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    int i = hash(paramArrayOfChar, paramInt1, paramInt2) % fTableSize;
    label75:
    for (Entry localEntry = fBuckets[i]; localEntry != null; localEntry = next) {
      if (paramInt2 == characters.length)
      {
        for (int j = 0; j < paramInt2; j++) {
          if (paramArrayOfChar[(paramInt1 + j)] != characters[j]) {
            break label75;
          }
        }
        return true;
      }
    }
    return false;
  }
  
  protected static final class Entry
  {
    public final String symbol;
    public final char[] characters;
    public Entry next;
    
    public Entry(String paramString, Entry paramEntry)
    {
      symbol = paramString.intern();
      characters = new char[paramString.length()];
      paramString.getChars(0, characters.length, characters, 0);
      next = paramEntry;
    }
    
    public Entry(char[] paramArrayOfChar, int paramInt1, int paramInt2, Entry paramEntry)
    {
      characters = new char[paramInt2];
      System.arraycopy(paramArrayOfChar, paramInt1, characters, 0, paramInt2);
      symbol = new String(characters).intern();
      next = paramEntry;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\util\SymbolTable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */