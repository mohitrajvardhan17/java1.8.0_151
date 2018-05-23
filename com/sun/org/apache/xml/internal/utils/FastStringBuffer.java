package com.sun.org.apache.xml.internal.utils;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;

public class FastStringBuffer
{
  static final int DEBUG_FORCE_INIT_BITS = 0;
  static final boolean DEBUG_FORCE_FIXED_CHUNKSIZE = true;
  public static final int SUPPRESS_LEADING_WS = 1;
  public static final int SUPPRESS_TRAILING_WS = 2;
  public static final int SUPPRESS_BOTH = 3;
  private static final int CARRY_WS = 4;
  int m_chunkBits = 15;
  int m_maxChunkBits = 15;
  int m_rebundleBits = 2;
  int m_chunkSize;
  int m_chunkMask;
  char[][] m_array;
  int m_lastChunk = 0;
  int m_firstFree = 0;
  FastStringBuffer m_innerFSB = null;
  static final char[] SINGLE_SPACE = { ' ' };
  
  public FastStringBuffer(int paramInt1, int paramInt2, int paramInt3)
  {
    paramInt2 = paramInt1;
    m_array = new char[16][];
    if (paramInt1 > paramInt2) {
      paramInt1 = paramInt2;
    }
    m_chunkBits = paramInt1;
    m_maxChunkBits = paramInt2;
    m_rebundleBits = paramInt3;
    m_chunkSize = (1 << paramInt1);
    m_chunkMask = (m_chunkSize - 1);
    m_array[0] = new char[m_chunkSize];
  }
  
  public FastStringBuffer(int paramInt1, int paramInt2)
  {
    this(paramInt1, paramInt2, 2);
  }
  
  public FastStringBuffer(int paramInt)
  {
    this(paramInt, 15, 2);
  }
  
  public FastStringBuffer()
  {
    this(10, 15, 2);
  }
  
  public final int size()
  {
    return (m_lastChunk << m_chunkBits) + m_firstFree;
  }
  
  public final int length()
  {
    return (m_lastChunk << m_chunkBits) + m_firstFree;
  }
  
  public final void reset()
  {
    m_lastChunk = 0;
    m_firstFree = 0;
    for (FastStringBuffer localFastStringBuffer = this; m_innerFSB != null; localFastStringBuffer = m_innerFSB) {}
    m_chunkBits = m_chunkBits;
    m_chunkSize = m_chunkSize;
    m_chunkMask = m_chunkMask;
    m_innerFSB = null;
    m_array = new char[16][0];
    m_array[0] = new char[m_chunkSize];
  }
  
  public final void setLength(int paramInt)
  {
    m_lastChunk = (paramInt >>> m_chunkBits);
    if ((m_lastChunk == 0) && (m_innerFSB != null))
    {
      m_innerFSB.setLength(paramInt, this);
    }
    else
    {
      m_firstFree = (paramInt & m_chunkMask);
      if ((m_firstFree == 0) && (m_lastChunk > 0))
      {
        m_lastChunk -= 1;
        m_firstFree = m_chunkSize;
      }
    }
  }
  
  private final void setLength(int paramInt, FastStringBuffer paramFastStringBuffer)
  {
    m_lastChunk = (paramInt >>> m_chunkBits);
    if ((m_lastChunk == 0) && (m_innerFSB != null))
    {
      m_innerFSB.setLength(paramInt, paramFastStringBuffer);
    }
    else
    {
      m_chunkBits = m_chunkBits;
      m_maxChunkBits = m_maxChunkBits;
      m_rebundleBits = m_rebundleBits;
      m_chunkSize = m_chunkSize;
      m_chunkMask = m_chunkMask;
      m_array = m_array;
      m_innerFSB = m_innerFSB;
      m_lastChunk = m_lastChunk;
      m_firstFree = (paramInt & m_chunkMask);
    }
  }
  
  public final String toString()
  {
    int i = (m_lastChunk << m_chunkBits) + m_firstFree;
    return getString(new StringBuffer(i), 0, 0, i).toString();
  }
  
  public final void append(char paramChar)
  {
    int i = m_lastChunk + 1 == m_array.length ? 1 : 0;
    char[] arrayOfChar;
    if (m_firstFree < m_chunkSize)
    {
      arrayOfChar = m_array[m_lastChunk];
    }
    else
    {
      int j = m_array.length;
      if (m_lastChunk + 1 == j)
      {
        char[][] arrayOfChar1 = new char[j + 16][];
        System.arraycopy(m_array, 0, arrayOfChar1, 0, j);
        m_array = arrayOfChar1;
      }
      arrayOfChar = m_array[(++m_lastChunk)];
      if (arrayOfChar == null)
      {
        if ((m_lastChunk == 1 << m_rebundleBits) && (m_chunkBits < m_maxChunkBits)) {
          m_innerFSB = new FastStringBuffer(this);
        }
        arrayOfChar = m_array[m_lastChunk] = new char[m_chunkSize];
      }
      m_firstFree = 0;
    }
    arrayOfChar[(m_firstFree++)] = paramChar;
  }
  
  public final void append(String paramString)
  {
    if (paramString == null) {
      return;
    }
    int i = paramString.length();
    if (0 == i) {
      return;
    }
    int j = 0;
    char[] arrayOfChar = m_array[m_lastChunk];
    int k = m_chunkSize - m_firstFree;
    while (i > 0)
    {
      if (k > i) {
        k = i;
      }
      paramString.getChars(j, j + k, m_array[m_lastChunk], m_firstFree);
      i -= k;
      j += k;
      if (i > 0)
      {
        int m = m_array.length;
        if (m_lastChunk + 1 == m)
        {
          char[][] arrayOfChar1 = new char[m + 16][];
          System.arraycopy(m_array, 0, arrayOfChar1, 0, m);
          m_array = arrayOfChar1;
        }
        arrayOfChar = m_array[(++m_lastChunk)];
        if (arrayOfChar == null)
        {
          if ((m_lastChunk == 1 << m_rebundleBits) && (m_chunkBits < m_maxChunkBits)) {
            m_innerFSB = new FastStringBuffer(this);
          }
          arrayOfChar = m_array[m_lastChunk] = new char[m_chunkSize];
        }
        k = m_chunkSize;
        m_firstFree = 0;
      }
    }
    m_firstFree += k;
  }
  
  public final void append(StringBuffer paramStringBuffer)
  {
    if (paramStringBuffer == null) {
      return;
    }
    int i = paramStringBuffer.length();
    if (0 == i) {
      return;
    }
    int j = 0;
    char[] arrayOfChar = m_array[m_lastChunk];
    int k = m_chunkSize - m_firstFree;
    while (i > 0)
    {
      if (k > i) {
        k = i;
      }
      paramStringBuffer.getChars(j, j + k, m_array[m_lastChunk], m_firstFree);
      i -= k;
      j += k;
      if (i > 0)
      {
        int m = m_array.length;
        if (m_lastChunk + 1 == m)
        {
          char[][] arrayOfChar1 = new char[m + 16][];
          System.arraycopy(m_array, 0, arrayOfChar1, 0, m);
          m_array = arrayOfChar1;
        }
        arrayOfChar = m_array[(++m_lastChunk)];
        if (arrayOfChar == null)
        {
          if ((m_lastChunk == 1 << m_rebundleBits) && (m_chunkBits < m_maxChunkBits)) {
            m_innerFSB = new FastStringBuffer(this);
          }
          arrayOfChar = m_array[m_lastChunk] = new char[m_chunkSize];
        }
        k = m_chunkSize;
        m_firstFree = 0;
      }
    }
    m_firstFree += k;
  }
  
  public final void append(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    int i = paramInt2;
    if (0 == i) {
      return;
    }
    int j = paramInt1;
    char[] arrayOfChar = m_array[m_lastChunk];
    int k = m_chunkSize - m_firstFree;
    while (i > 0)
    {
      if (k > i) {
        k = i;
      }
      System.arraycopy(paramArrayOfChar, j, m_array[m_lastChunk], m_firstFree, k);
      i -= k;
      j += k;
      if (i > 0)
      {
        int m = m_array.length;
        if (m_lastChunk + 1 == m)
        {
          char[][] arrayOfChar1 = new char[m + 16][];
          System.arraycopy(m_array, 0, arrayOfChar1, 0, m);
          m_array = arrayOfChar1;
        }
        arrayOfChar = m_array[(++m_lastChunk)];
        if (arrayOfChar == null)
        {
          if ((m_lastChunk == 1 << m_rebundleBits) && (m_chunkBits < m_maxChunkBits)) {
            m_innerFSB = new FastStringBuffer(this);
          }
          arrayOfChar = m_array[m_lastChunk] = new char[m_chunkSize];
        }
        k = m_chunkSize;
        m_firstFree = 0;
      }
    }
    m_firstFree += k;
  }
  
  public final void append(FastStringBuffer paramFastStringBuffer)
  {
    if (paramFastStringBuffer == null) {
      return;
    }
    int i = paramFastStringBuffer.length();
    if (0 == i) {
      return;
    }
    int j = 0;
    char[] arrayOfChar = m_array[m_lastChunk];
    int k = m_chunkSize - m_firstFree;
    while (i > 0)
    {
      if (k > i) {
        k = i;
      }
      int m = j + m_chunkSize - 1 >>> m_chunkBits;
      int n = j & m_chunkMask;
      int i1 = m_chunkSize - n;
      if (i1 > k) {
        i1 = k;
      }
      System.arraycopy(m_array[m], n, m_array[m_lastChunk], m_firstFree, i1);
      if (i1 != k) {
        System.arraycopy(m_array[(m + 1)], 0, m_array[m_lastChunk], m_firstFree + i1, k - i1);
      }
      i -= k;
      j += k;
      if (i > 0)
      {
        int i2 = m_array.length;
        if (m_lastChunk + 1 == i2)
        {
          char[][] arrayOfChar1 = new char[i2 + 16][];
          System.arraycopy(m_array, 0, arrayOfChar1, 0, i2);
          m_array = arrayOfChar1;
        }
        arrayOfChar = m_array[(++m_lastChunk)];
        if (arrayOfChar == null)
        {
          if ((m_lastChunk == 1 << m_rebundleBits) && (m_chunkBits < m_maxChunkBits)) {
            m_innerFSB = new FastStringBuffer(this);
          }
          arrayOfChar = m_array[m_lastChunk] = new char[m_chunkSize];
        }
        k = m_chunkSize;
        m_firstFree = 0;
      }
    }
    m_firstFree += k;
  }
  
  public boolean isWhitespace(int paramInt1, int paramInt2)
  {
    int i = paramInt1 >>> m_chunkBits;
    int j = paramInt1 & m_chunkMask;
    for (int k = m_chunkSize - j; paramInt2 > 0; k = m_chunkSize)
    {
      int m = paramInt2 <= k ? paramInt2 : k;
      boolean bool;
      if ((i == 0) && (m_innerFSB != null)) {
        bool = m_innerFSB.isWhitespace(j, m);
      } else {
        bool = XMLCharacterRecognizer.isWhiteSpace(m_array[i], j, m);
      }
      if (!bool) {
        return false;
      }
      paramInt2 -= m;
      i++;
      j = 0;
    }
    return true;
  }
  
  public String getString(int paramInt1, int paramInt2)
  {
    int i = paramInt1 & m_chunkMask;
    int j = paramInt1 >>> m_chunkBits;
    if ((i + paramInt2 < m_chunkMask) && (m_innerFSB == null)) {
      return getOneChunkString(j, i, paramInt2);
    }
    return getString(new StringBuffer(paramInt2), j, i, paramInt2).toString();
  }
  
  protected String getOneChunkString(int paramInt1, int paramInt2, int paramInt3)
  {
    return new String(m_array[paramInt1], paramInt2, paramInt3);
  }
  
  StringBuffer getString(StringBuffer paramStringBuffer, int paramInt1, int paramInt2)
  {
    return getString(paramStringBuffer, paramInt1 >>> m_chunkBits, paramInt1 & m_chunkMask, paramInt2);
  }
  
  StringBuffer getString(StringBuffer paramStringBuffer, int paramInt1, int paramInt2, int paramInt3)
  {
    int i = (paramInt1 << m_chunkBits) + paramInt2 + paramInt3;
    int j = i >>> m_chunkBits;
    int k = i & m_chunkMask;
    for (int m = paramInt1; m < j; m++)
    {
      if ((m == 0) && (m_innerFSB != null)) {
        m_innerFSB.getString(paramStringBuffer, paramInt2, m_chunkSize - paramInt2);
      } else {
        paramStringBuffer.append(m_array[m], paramInt2, m_chunkSize - paramInt2);
      }
      paramInt2 = 0;
    }
    if ((j == 0) && (m_innerFSB != null)) {
      m_innerFSB.getString(paramStringBuffer, paramInt2, k - paramInt2);
    } else if (k > paramInt2) {
      paramStringBuffer.append(m_array[j], paramInt2, k - paramInt2);
    }
    return paramStringBuffer;
  }
  
  public char charAt(int paramInt)
  {
    int i = paramInt >>> m_chunkBits;
    if ((i == 0) && (m_innerFSB != null)) {
      return m_innerFSB.charAt(paramInt & m_chunkMask);
    }
    return m_array[i][(paramInt & m_chunkMask)];
  }
  
  public void sendSAXcharacters(ContentHandler paramContentHandler, int paramInt1, int paramInt2)
    throws SAXException
  {
    int i = paramInt1 >>> m_chunkBits;
    int j = paramInt1 & m_chunkMask;
    if ((j + paramInt2 < m_chunkMask) && (m_innerFSB == null))
    {
      paramContentHandler.characters(m_array[i], j, paramInt2);
      return;
    }
    int k = paramInt1 + paramInt2;
    int m = k >>> m_chunkBits;
    int n = k & m_chunkMask;
    for (int i1 = i; i1 < m; i1++)
    {
      if ((i1 == 0) && (m_innerFSB != null)) {
        m_innerFSB.sendSAXcharacters(paramContentHandler, j, m_chunkSize - j);
      } else {
        paramContentHandler.characters(m_array[i1], j, m_chunkSize - j);
      }
      j = 0;
    }
    if ((m == 0) && (m_innerFSB != null)) {
      m_innerFSB.sendSAXcharacters(paramContentHandler, j, n - j);
    } else if (n > j) {
      paramContentHandler.characters(m_array[m], j, n - j);
    }
  }
  
  public int sendNormalizedSAXcharacters(ContentHandler paramContentHandler, int paramInt1, int paramInt2)
    throws SAXException
  {
    int i = 1;
    int j = paramInt1 + paramInt2;
    int k = paramInt1 >>> m_chunkBits;
    int m = paramInt1 & m_chunkMask;
    int n = j >>> m_chunkBits;
    int i1 = j & m_chunkMask;
    for (int i2 = k; i2 < n; i2++)
    {
      if ((i2 == 0) && (m_innerFSB != null)) {
        i = m_innerFSB.sendNormalizedSAXcharacters(paramContentHandler, m, m_chunkSize - m);
      } else {
        i = sendNormalizedSAXcharacters(m_array[i2], m, m_chunkSize - m, paramContentHandler, i);
      }
      m = 0;
    }
    if ((n == 0) && (m_innerFSB != null)) {
      i = m_innerFSB.sendNormalizedSAXcharacters(paramContentHandler, m, i1 - m);
    } else if (i1 > m) {
      i = sendNormalizedSAXcharacters(m_array[n], m, i1 - m, paramContentHandler, i | 0x2);
    }
    return i;
  }
  
  static int sendNormalizedSAXcharacters(char[] paramArrayOfChar, int paramInt1, int paramInt2, ContentHandler paramContentHandler, int paramInt3)
    throws SAXException
  {
    int i = (paramInt3 & 0x1) != 0 ? 1 : 0;
    int j = (paramInt3 & 0x4) != 0 ? 1 : 0;
    int k = (paramInt3 & 0x2) != 0 ? 1 : 0;
    int m = paramInt1;
    int n = paramInt1 + paramInt2;
    if (i != 0)
    {
      while ((m < n) && (XMLCharacterRecognizer.isWhiteSpace(paramArrayOfChar[m]))) {
        m++;
      }
      if (m == n) {
        return paramInt3;
      }
    }
    while (m < n)
    {
      int i1 = m;
      while ((m < n) && (!XMLCharacterRecognizer.isWhiteSpace(paramArrayOfChar[m]))) {
        m++;
      }
      if (i1 != m)
      {
        if (j != 0)
        {
          paramContentHandler.characters(SINGLE_SPACE, 0, 1);
          j = 0;
        }
        paramContentHandler.characters(paramArrayOfChar, i1, m - i1);
      }
      int i2 = m;
      while ((m < n) && (XMLCharacterRecognizer.isWhiteSpace(paramArrayOfChar[m]))) {
        m++;
      }
      if (i2 != m) {
        j = 1;
      }
    }
    return (j != 0 ? 4 : 0) | paramInt3 & 0x2;
  }
  
  public static void sendNormalizedSAXcharacters(char[] paramArrayOfChar, int paramInt1, int paramInt2, ContentHandler paramContentHandler)
    throws SAXException
  {
    sendNormalizedSAXcharacters(paramArrayOfChar, paramInt1, paramInt2, paramContentHandler, 3);
  }
  
  public void sendSAXComment(LexicalHandler paramLexicalHandler, int paramInt1, int paramInt2)
    throws SAXException
  {
    String str = getString(paramInt1, paramInt2);
    paramLexicalHandler.comment(str.toCharArray(), 0, paramInt2);
  }
  
  private void getChars(int paramInt1, int paramInt2, char[] paramArrayOfChar, int paramInt3) {}
  
  private FastStringBuffer(FastStringBuffer paramFastStringBuffer)
  {
    m_chunkBits = m_chunkBits;
    m_maxChunkBits = m_maxChunkBits;
    m_rebundleBits = m_rebundleBits;
    m_chunkSize = m_chunkSize;
    m_chunkMask = m_chunkMask;
    m_array = m_array;
    m_innerFSB = m_innerFSB;
    m_lastChunk -= 1;
    m_firstFree = m_chunkSize;
    m_array = new char[16][];
    m_innerFSB = this;
    m_lastChunk = 1;
    m_firstFree = 0;
    m_chunkBits += m_rebundleBits;
    m_chunkSize = (1 << m_chunkBits);
    m_chunkMask = (m_chunkSize - 1);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\utils\FastStringBuffer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */