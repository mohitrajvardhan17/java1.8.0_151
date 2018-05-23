package sun.text;

import java.io.PrintStream;

public final class IntHashtable
{
  private int defaultValue = 0;
  private int primeIndex;
  private static final float HIGH_WATER_FACTOR = 0.4F;
  private int highWaterMark;
  private static final float LOW_WATER_FACTOR = 0.0F;
  private int lowWaterMark;
  private int count;
  private int[] values;
  private int[] keyList;
  private static final int EMPTY = Integer.MIN_VALUE;
  private static final int DELETED = -2147483647;
  private static final int MAX_UNUSED = -2147483647;
  private static final int[] PRIMES = { 17, 37, 67, 131, 257, 521, 1031, 2053, 4099, 8209, 16411, 32771, 65537, 131101, 262147, 524309, 1048583, 2097169, 4194319, 8388617, 16777259, 33554467, 67108879, 134217757, 268435459, 536870923, 1073741827, Integer.MAX_VALUE };
  
  public IntHashtable()
  {
    initialize(3);
  }
  
  public IntHashtable(int paramInt)
  {
    initialize(leastGreaterPrimeIndex((int)(paramInt / 0.4F)));
  }
  
  public int size()
  {
    return count;
  }
  
  public boolean isEmpty()
  {
    return count == 0;
  }
  
  public void put(int paramInt1, int paramInt2)
  {
    if (count > highWaterMark) {
      rehash();
    }
    int i = find(paramInt1);
    if (keyList[i] <= -2147483647)
    {
      keyList[i] = paramInt1;
      count += 1;
    }
    values[i] = paramInt2;
  }
  
  public int get(int paramInt)
  {
    return values[find(paramInt)];
  }
  
  public void remove(int paramInt)
  {
    int i = find(paramInt);
    if (keyList[i] > -2147483647)
    {
      keyList[i] = -2147483647;
      values[i] = defaultValue;
      count -= 1;
      if (count < lowWaterMark) {
        rehash();
      }
    }
  }
  
  public int getDefaultValue()
  {
    return defaultValue;
  }
  
  public void setDefaultValue(int paramInt)
  {
    defaultValue = paramInt;
    rehash();
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject.getClass() != getClass()) {
      return false;
    }
    IntHashtable localIntHashtable = (IntHashtable)paramObject;
    if ((localIntHashtable.size() != count) || (defaultValue != defaultValue)) {
      return false;
    }
    for (int i = 0; i < keyList.length; i++)
    {
      int j = keyList[i];
      if ((j > -2147483647) && (localIntHashtable.get(j) != values[i])) {
        return false;
      }
    }
    return true;
  }
  
  public int hashCode()
  {
    int i = 465;
    int j = 1362796821;
    for (int k = 0; k < keyList.length; k++)
    {
      i = i * j + 1;
      i += keyList[k];
    }
    for (k = 0; k < values.length; k++)
    {
      i = i * j + 1;
      i += values[k];
    }
    return i;
  }
  
  public Object clone()
    throws CloneNotSupportedException
  {
    IntHashtable localIntHashtable = (IntHashtable)super.clone();
    values = ((int[])values.clone());
    keyList = ((int[])keyList.clone());
    return localIntHashtable;
  }
  
  private void initialize(int paramInt)
  {
    if (paramInt < 0)
    {
      paramInt = 0;
    }
    else if (paramInt >= PRIMES.length)
    {
      System.out.println("TOO BIG");
      paramInt = PRIMES.length - 1;
    }
    primeIndex = paramInt;
    int i = PRIMES[paramInt];
    values = new int[i];
    keyList = new int[i];
    for (int j = 0; j < i; j++)
    {
      keyList[j] = Integer.MIN_VALUE;
      values[j] = defaultValue;
    }
    count = 0;
    lowWaterMark = ((int)(i * 0.0F));
    highWaterMark = ((int)(i * 0.4F));
  }
  
  private void rehash()
  {
    int[] arrayOfInt1 = values;
    int[] arrayOfInt2 = keyList;
    int i = primeIndex;
    if (count > highWaterMark) {
      i++;
    } else if (count < lowWaterMark) {
      i -= 2;
    }
    initialize(i);
    for (int j = arrayOfInt1.length - 1; j >= 0; j--)
    {
      int k = arrayOfInt2[j];
      if (k > -2147483647) {
        putInternal(k, arrayOfInt1[j]);
      }
    }
  }
  
  public void putInternal(int paramInt1, int paramInt2)
  {
    int i = find(paramInt1);
    if (keyList[i] < -2147483647)
    {
      keyList[i] = paramInt1;
      count += 1;
    }
    values[i] = paramInt2;
  }
  
  private int find(int paramInt)
  {
    if (paramInt <= -2147483647) {
      throw new IllegalArgumentException("key can't be less than 0xFFFFFFFE");
    }
    int i = -1;
    int j = (paramInt ^ 0x4000000) % keyList.length;
    if (j < 0) {
      j = -j;
    }
    int k = 0;
    for (;;)
    {
      int m = keyList[j];
      if (m == paramInt) {
        return j;
      }
      if (m <= -2147483647)
      {
        if (m == Integer.MIN_VALUE)
        {
          if (i >= 0) {
            j = i;
          }
          return j;
        }
        if (i < 0) {
          i = j;
        }
      }
      if (k == 0)
      {
        k = paramInt % (keyList.length - 1);
        if (k < 0) {
          k = -k;
        }
        k++;
      }
      j = (j + k) % keyList.length;
      if (j == i) {
        return j;
      }
    }
  }
  
  private static int leastGreaterPrimeIndex(int paramInt)
  {
    for (int i = 0; (i < PRIMES.length) && (paramInt >= PRIMES[i]); i++) {}
    return i == 0 ? 0 : i - 1;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\text\IntHashtable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */