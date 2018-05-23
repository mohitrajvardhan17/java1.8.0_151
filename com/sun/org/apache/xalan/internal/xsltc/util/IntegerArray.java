package com.sun.org.apache.xalan.internal.xsltc.util;

import java.io.PrintStream;

public final class IntegerArray
{
  private static final int InitialSize = 32;
  private int[] _array;
  private int _size;
  private int _free = 0;
  
  public IntegerArray()
  {
    this(32);
  }
  
  public IntegerArray(int paramInt)
  {
    _array = new int[_size = paramInt];
  }
  
  public IntegerArray(int[] paramArrayOfInt)
  {
    this(paramArrayOfInt.length);
    System.arraycopy(paramArrayOfInt, 0, _array, 0, _free = _size);
  }
  
  public void clear()
  {
    _free = 0;
  }
  
  public Object clone()
  {
    IntegerArray localIntegerArray = new IntegerArray(_free > 0 ? _free : 1);
    System.arraycopy(_array, 0, _array, 0, _free);
    _free = _free;
    return localIntegerArray;
  }
  
  public int[] toIntArray()
  {
    int[] arrayOfInt = new int[cardinality()];
    System.arraycopy(_array, 0, arrayOfInt, 0, cardinality());
    return arrayOfInt;
  }
  
  public final int at(int paramInt)
  {
    return _array[paramInt];
  }
  
  public final void set(int paramInt1, int paramInt2)
  {
    _array[paramInt1] = paramInt2;
  }
  
  public int indexOf(int paramInt)
  {
    for (int i = 0; i < _free; i++) {
      if (paramInt == _array[i]) {
        return i;
      }
    }
    return -1;
  }
  
  public final void add(int paramInt)
  {
    if (_free == _size) {
      growArray(_size * 2);
    }
    _array[(_free++)] = paramInt;
  }
  
  public void addNew(int paramInt)
  {
    for (int i = 0; i < _free; i++) {
      if (_array[i] == paramInt) {
        return;
      }
    }
    add(paramInt);
  }
  
  public void reverse()
  {
    int i = 0;
    int j = _free - 1;
    while (i < j)
    {
      int k = _array[i];
      _array[(i++)] = _array[j];
      _array[(j--)] = k;
    }
  }
  
  public void merge(IntegerArray paramIntegerArray)
  {
    int i = _free + _free;
    int[] arrayOfInt = new int[i];
    int j = 0;
    int k = 0;
    for (int m = 0; (j < _free) && (k < _free); m++)
    {
      int n = _array[j];
      int i1 = _array[k];
      if (n < i1)
      {
        arrayOfInt[m] = n;
        j++;
      }
      else if (n > i1)
      {
        arrayOfInt[m] = i1;
        k++;
      }
      else
      {
        arrayOfInt[m] = n;
        j++;
        k++;
      }
    }
    if (j >= _free) {
      while (k < _free) {
        arrayOfInt[(m++)] = _array[(k++)];
      }
    }
    while (j < _free) {
      arrayOfInt[(m++)] = _array[(j++)];
    }
    _array = arrayOfInt;
    _free = (_size = i);
  }
  
  public void sort()
  {
    quicksort(_array, 0, _free - 1);
  }
  
  private static void quicksort(int[] paramArrayOfInt, int paramInt1, int paramInt2)
  {
    if (paramInt1 < paramInt2)
    {
      int i = partition(paramArrayOfInt, paramInt1, paramInt2);
      quicksort(paramArrayOfInt, paramInt1, i);
      quicksort(paramArrayOfInt, i + 1, paramInt2);
    }
  }
  
  private static int partition(int[] paramArrayOfInt, int paramInt1, int paramInt2)
  {
    int i = paramArrayOfInt[(paramInt1 + paramInt2 >>> 1)];
    int j = paramInt1 - 1;
    int k = paramInt2 + 1;
    for (;;)
    {
      if (i >= paramArrayOfInt[(--k)])
      {
        while (i > paramArrayOfInt[(++j)]) {}
        if (j >= k) {
          break;
        }
        int m = paramArrayOfInt[j];
        paramArrayOfInt[j] = paramArrayOfInt[k];
        paramArrayOfInt[k] = m;
      }
    }
    return k;
  }
  
  private void growArray(int paramInt)
  {
    int[] arrayOfInt = new int[_size = paramInt];
    System.arraycopy(_array, 0, arrayOfInt, 0, _free);
    _array = arrayOfInt;
  }
  
  public int popLast()
  {
    return _array[(--_free)];
  }
  
  public int last()
  {
    return _array[(_free - 1)];
  }
  
  public void setLast(int paramInt)
  {
    _array[(_free - 1)] = paramInt;
  }
  
  public void pop()
  {
    _free -= 1;
  }
  
  public void pop(int paramInt)
  {
    _free -= paramInt;
  }
  
  public final int cardinality()
  {
    return _free;
  }
  
  public void print(PrintStream paramPrintStream)
  {
    if (_free > 0)
    {
      for (int i = 0; i < _free - 1; i++)
      {
        paramPrintStream.print(_array[i]);
        paramPrintStream.print(' ');
      }
      paramPrintStream.println(_array[(_free - 1)]);
    }
    else
    {
      paramPrintStream.println("IntegerArray: empty");
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\util\IntegerArray.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */