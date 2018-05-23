package com.sun.org.apache.xml.internal.dtm.ref;

import com.sun.org.apache.xml.internal.res.XMLMessages;
import java.io.PrintStream;

final class ChunkedIntArray
{
  final int slotsize = 4;
  static final int lowbits = 10;
  static final int chunkalloc = 1024;
  static final int lowmask = 1023;
  ChunksVector chunks = new ChunksVector();
  final int[] fastArray = new int['Ѐ'];
  int lastUsed = 0;
  
  ChunkedIntArray(int paramInt)
  {
    getClass();
    if (4 < paramInt) {
      throw new ArrayIndexOutOfBoundsException(XMLMessages.createXMLMessage("ER_CHUNKEDINTARRAY_NOT_SUPPORTED", new Object[] { Integer.toString(paramInt) }));
    }
    getClass();
    if (4 > paramInt)
    {
      getClass();
      System.out.println("*****WARNING: ChunkedIntArray(" + paramInt + ") wasting " + (4 - paramInt) + " words per slot");
    }
    chunks.addElement(fastArray);
  }
  
  int appendSlot(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int i = 4;
    int j = (lastUsed + 1) * 4;
    int k = j >> 10;
    int m = j & 0x3FF;
    if (k > chunks.size() - 1) {
      chunks.addElement(new int['Ѐ']);
    }
    int[] arrayOfInt = chunks.elementAt(k);
    arrayOfInt[m] = paramInt1;
    arrayOfInt[(m + 1)] = paramInt2;
    arrayOfInt[(m + 2)] = paramInt3;
    arrayOfInt[(m + 3)] = paramInt4;
    return ++lastUsed;
  }
  
  int readEntry(int paramInt1, int paramInt2)
    throws ArrayIndexOutOfBoundsException
  {
    if (paramInt2 >= 4) {
      throw new ArrayIndexOutOfBoundsException(XMLMessages.createXMLMessage("ER_OFFSET_BIGGER_THAN_SLOT", null));
    }
    paramInt1 *= 4;
    int i = paramInt1 >> 10;
    int j = paramInt1 & 0x3FF;
    int[] arrayOfInt = chunks.elementAt(i);
    return arrayOfInt[(j + paramInt2)];
  }
  
  int specialFind(int paramInt1, int paramInt2)
  {
    int i = paramInt1;
    while (i > 0)
    {
      i *= 4;
      int j = i >> 10;
      int k = i & 0x3FF;
      int[] arrayOfInt = chunks.elementAt(j);
      i = arrayOfInt[(k + 1)];
      if (i == paramInt2) {
        break;
      }
    }
    if (i <= 0) {
      return paramInt2;
    }
    return -1;
  }
  
  int slotsUsed()
  {
    return lastUsed;
  }
  
  void discardLast()
  {
    lastUsed -= 1;
  }
  
  void writeEntry(int paramInt1, int paramInt2, int paramInt3)
    throws ArrayIndexOutOfBoundsException
  {
    if (paramInt2 >= 4) {
      throw new ArrayIndexOutOfBoundsException(XMLMessages.createXMLMessage("ER_OFFSET_BIGGER_THAN_SLOT", null));
    }
    paramInt1 *= 4;
    int i = paramInt1 >> 10;
    int j = paramInt1 & 0x3FF;
    int[] arrayOfInt = chunks.elementAt(i);
    arrayOfInt[(j + paramInt2)] = paramInt3;
  }
  
  void writeSlot(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    paramInt1 *= 4;
    int i = paramInt1 >> 10;
    int j = paramInt1 & 0x3FF;
    if (i > chunks.size() - 1) {
      chunks.addElement(new int['Ѐ']);
    }
    int[] arrayOfInt = chunks.elementAt(i);
    arrayOfInt[j] = paramInt2;
    arrayOfInt[(j + 1)] = paramInt3;
    arrayOfInt[(j + 2)] = paramInt4;
    arrayOfInt[(j + 3)] = paramInt5;
  }
  
  void readSlot(int paramInt, int[] paramArrayOfInt)
  {
    paramInt *= 4;
    int i = paramInt >> 10;
    int j = paramInt & 0x3FF;
    if (i > chunks.size() - 1) {
      chunks.addElement(new int['Ѐ']);
    }
    int[] arrayOfInt = chunks.elementAt(i);
    System.arraycopy(arrayOfInt, j, paramArrayOfInt, 0, 4);
  }
  
  class ChunksVector
  {
    final int BLOCKSIZE = 64;
    int[][] m_map = new int[64][];
    int m_mapSize = 64;
    int pos = 0;
    
    ChunksVector() {}
    
    final int size()
    {
      return pos;
    }
    
    void addElement(int[] paramArrayOfInt)
    {
      if (pos >= m_mapSize)
      {
        int i = m_mapSize;
        while (pos >= m_mapSize) {
          m_mapSize += 64;
        }
        int[][] arrayOfInt = new int[m_mapSize][];
        System.arraycopy(m_map, 0, arrayOfInt, 0, i);
        m_map = arrayOfInt;
      }
      m_map[pos] = paramArrayOfInt;
      pos += 1;
    }
    
    final int[] elementAt(int paramInt)
    {
      return m_map[paramInt];
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\dtm\ref\ChunkedIntArray.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */