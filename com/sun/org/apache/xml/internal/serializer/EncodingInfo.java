package com.sun.org.apache.xml.internal.serializer;

public final class EncodingInfo
{
  final String name;
  final String javaName;
  private InEncoding m_encoding;
  
  public boolean isInEncoding(char paramChar)
  {
    if (m_encoding == null) {
      m_encoding = new EncodingImpl(null);
    }
    return m_encoding.isInEncoding(paramChar);
  }
  
  public boolean isInEncoding(char paramChar1, char paramChar2)
  {
    if (m_encoding == null) {
      m_encoding = new EncodingImpl(null);
    }
    return m_encoding.isInEncoding(paramChar1, paramChar2);
  }
  
  public EncodingInfo(String paramString1, String paramString2)
  {
    name = paramString1;
    javaName = paramString2;
  }
  
  private static boolean inEncoding(char paramChar, String paramString)
  {
    boolean bool;
    try
    {
      char[] arrayOfChar = new char[1];
      arrayOfChar[0] = paramChar;
      String str = new String(arrayOfChar);
      byte[] arrayOfByte = str.getBytes(paramString);
      bool = inEncoding(paramChar, arrayOfByte);
    }
    catch (Exception localException)
    {
      bool = false;
      if (paramString == null) {
        bool = true;
      }
    }
    return bool;
  }
  
  private static boolean inEncoding(char paramChar1, char paramChar2, String paramString)
  {
    boolean bool;
    try
    {
      char[] arrayOfChar = new char[2];
      arrayOfChar[0] = paramChar1;
      arrayOfChar[1] = paramChar2;
      String str = new String(arrayOfChar);
      byte[] arrayOfByte = str.getBytes(paramString);
      bool = inEncoding(paramChar1, arrayOfByte);
    }
    catch (Exception localException)
    {
      bool = false;
    }
    return bool;
  }
  
  private static boolean inEncoding(char paramChar, byte[] paramArrayOfByte)
  {
    boolean bool;
    if ((paramArrayOfByte == null) || (paramArrayOfByte.length == 0)) {
      bool = false;
    } else if (paramArrayOfByte[0] == 0) {
      bool = false;
    } else if ((paramArrayOfByte[0] == 63) && (paramChar != '?')) {
      bool = false;
    } else {
      bool = true;
    }
    return bool;
  }
  
  private class EncodingImpl
    implements EncodingInfo.InEncoding
  {
    private final String m_encoding;
    private final int m_first;
    private final int m_explFirst;
    private final int m_explLast;
    private final int m_last;
    private EncodingInfo.InEncoding m_before;
    private EncodingInfo.InEncoding m_after;
    private static final int RANGE = 128;
    private final boolean[] m_alreadyKnown = new boolean[''];
    private final boolean[] m_isInEncoding = new boolean[''];
    
    public boolean isInEncoding(char paramChar)
    {
      int j = Encodings.toCodePoint(paramChar);
      boolean bool1;
      boolean bool2;
      if (j < m_explFirst)
      {
        if (m_before == null) {
          m_before = new EncodingImpl(EncodingInfo.this, m_encoding, m_first, m_explFirst - 1, j);
        }
        bool1 = m_before.isInEncoding(paramChar);
      }
      else if (m_explLast < j)
      {
        if (m_after == null) {
          m_after = new EncodingImpl(EncodingInfo.this, m_encoding, m_explLast + 1, m_last, j);
        }
        bool1 = m_after.isInEncoding(paramChar);
      }
      else
      {
        int k = j - m_explFirst;
        if (m_alreadyKnown[k] != 0)
        {
          int i = m_isInEncoding[k];
        }
        else
        {
          bool2 = EncodingInfo.inEncoding(paramChar, m_encoding);
          m_alreadyKnown[k] = true;
          m_isInEncoding[k] = bool2;
        }
      }
      return bool2;
    }
    
    public boolean isInEncoding(char paramChar1, char paramChar2)
    {
      int j = Encodings.toCodePoint(paramChar1, paramChar2);
      boolean bool1;
      boolean bool2;
      if (j < m_explFirst)
      {
        if (m_before == null) {
          m_before = new EncodingImpl(EncodingInfo.this, m_encoding, m_first, m_explFirst - 1, j);
        }
        bool1 = m_before.isInEncoding(paramChar1, paramChar2);
      }
      else if (m_explLast < j)
      {
        if (m_after == null) {
          m_after = new EncodingImpl(EncodingInfo.this, m_encoding, m_explLast + 1, m_last, j);
        }
        bool1 = m_after.isInEncoding(paramChar1, paramChar2);
      }
      else
      {
        int k = j - m_explFirst;
        if (m_alreadyKnown[k] != 0)
        {
          int i = m_isInEncoding[k];
        }
        else
        {
          bool2 = EncodingInfo.inEncoding(paramChar1, paramChar2, m_encoding);
          m_alreadyKnown[k] = true;
          m_isInEncoding[k] = bool2;
        }
      }
      return bool2;
    }
    
    private EncodingImpl()
    {
      this(javaName, 0, Integer.MAX_VALUE, 0);
    }
    
    private EncodingImpl(String paramString, int paramInt1, int paramInt2, int paramInt3)
    {
      m_first = paramInt1;
      m_last = paramInt2;
      m_explFirst = (paramInt3 / 128 * 128);
      m_explLast = (m_explFirst + 127);
      m_encoding = paramString;
      if (javaName != null)
      {
        int i;
        if ((0 <= m_explFirst) && (m_explFirst <= 127) && (("UTF8".equals(javaName)) || ("UTF-16".equals(javaName)) || ("ASCII".equals(javaName)) || ("US-ASCII".equals(javaName)) || ("Unicode".equals(javaName)) || ("UNICODE".equals(javaName)) || (javaName.startsWith("ISO8859")))) {
          for (i = 1; i < 127; i++)
          {
            int j = i - m_explFirst;
            if ((0 <= j) && (j < 128))
            {
              m_alreadyKnown[j] = true;
              m_isInEncoding[j] = true;
            }
          }
        }
        if (javaName == null) {
          for (i = 0; i < m_alreadyKnown.length; i++)
          {
            m_alreadyKnown[i] = true;
            m_isInEncoding[i] = true;
          }
        }
      }
    }
  }
  
  private static abstract interface InEncoding
  {
    public abstract boolean isInEncoding(char paramChar);
    
    public abstract boolean isInEncoding(char paramChar1, char paramChar2);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\serializer\EncodingInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */