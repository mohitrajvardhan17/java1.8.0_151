package javax.swing.text;

import java.io.Serializable;

abstract class GapVector
  implements Serializable
{
  private Object array;
  private int g0;
  private int g1;
  
  public GapVector()
  {
    this(10);
  }
  
  public GapVector(int paramInt)
  {
    array = allocateArray(paramInt);
    g0 = 0;
    g1 = paramInt;
  }
  
  protected abstract Object allocateArray(int paramInt);
  
  protected abstract int getArrayLength();
  
  protected final Object getArray()
  {
    return array;
  }
  
  protected final int getGapStart()
  {
    return g0;
  }
  
  protected final int getGapEnd()
  {
    return g1;
  }
  
  protected void replace(int paramInt1, int paramInt2, Object paramObject, int paramInt3)
  {
    int i = 0;
    if (paramInt3 == 0)
    {
      close(paramInt1, paramInt2);
      return;
    }
    if (paramInt2 > paramInt3)
    {
      close(paramInt1 + paramInt3, paramInt2 - paramInt3);
    }
    else
    {
      int j = paramInt3 - paramInt2;
      int k = open(paramInt1 + paramInt2, j);
      System.arraycopy(paramObject, paramInt2, array, k, j);
      paramInt3 = paramInt2;
    }
    System.arraycopy(paramObject, i, array, paramInt1, paramInt3);
  }
  
  void close(int paramInt1, int paramInt2)
  {
    if (paramInt2 == 0) {
      return;
    }
    int i = paramInt1 + paramInt2;
    int j = g1 - g0 + paramInt2;
    if (i <= g0)
    {
      if (g0 != i) {
        shiftGap(i);
      }
      shiftGapStartDown(g0 - paramInt2);
    }
    else if (paramInt1 >= g0)
    {
      if (g0 != paramInt1) {
        shiftGap(paramInt1);
      }
      shiftGapEndUp(g0 + j);
    }
    else
    {
      shiftGapStartDown(paramInt1);
      shiftGapEndUp(g0 + j);
    }
  }
  
  int open(int paramInt1, int paramInt2)
  {
    int i = g1 - g0;
    if (paramInt2 == 0)
    {
      if (paramInt1 > g0) {
        paramInt1 += i;
      }
      return paramInt1;
    }
    shiftGap(paramInt1);
    if (paramInt2 >= i)
    {
      shiftEnd(getArrayLength() - i + paramInt2);
      i = g1 - g0;
    }
    g0 += paramInt2;
    return paramInt1;
  }
  
  void resize(int paramInt)
  {
    Object localObject = allocateArray(paramInt);
    System.arraycopy(array, 0, localObject, 0, Math.min(paramInt, getArrayLength()));
    array = localObject;
  }
  
  protected void shiftEnd(int paramInt)
  {
    int i = getArrayLength();
    int j = g1;
    int k = i - j;
    int m = getNewArraySize(paramInt);
    int n = m - k;
    resize(m);
    g1 = n;
    if (k != 0) {
      System.arraycopy(array, j, array, n, k);
    }
  }
  
  int getNewArraySize(int paramInt)
  {
    return (paramInt + 1) * 2;
  }
  
  protected void shiftGap(int paramInt)
  {
    if (paramInt == g0) {
      return;
    }
    int i = g0;
    int j = paramInt - i;
    int k = g1;
    int m = k + j;
    int n = k - i;
    g0 = paramInt;
    g1 = m;
    if (j > 0) {
      System.arraycopy(array, k, array, i, j);
    } else if (j < 0) {
      System.arraycopy(array, paramInt, array, m, -j);
    }
  }
  
  protected void shiftGapStartDown(int paramInt)
  {
    g0 = paramInt;
  }
  
  protected void shiftGapEndUp(int paramInt)
  {
    g1 = paramInt;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\text\GapVector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */