package com.sun.org.apache.xml.internal.serializer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

final class WriterToUTF8Buffered
  extends Writer
  implements WriterChain
{
  private static final int BYTES_MAX = 16384;
  private static final int CHARS_MAX = 5461;
  private final OutputStream m_os;
  private final byte[] m_outputBytes;
  private final char[] m_inputChars;
  private int count;
  
  public WriterToUTF8Buffered(OutputStream paramOutputStream)
    throws UnsupportedEncodingException
  {
    m_os = paramOutputStream;
    m_outputBytes = new byte['䀃'];
    m_inputChars = new char['ᕗ'];
    count = 0;
  }
  
  public void write(int paramInt)
    throws IOException
  {
    if (count >= 16384) {
      flushBuffer();
    }
    if (paramInt < 128)
    {
      m_outputBytes[(count++)] = ((byte)paramInt);
    }
    else if (paramInt < 2048)
    {
      m_outputBytes[(count++)] = ((byte)(192 + (paramInt >> 6)));
      m_outputBytes[(count++)] = ((byte)(128 + (paramInt & 0x3F)));
    }
    else if (paramInt < 65536)
    {
      m_outputBytes[(count++)] = ((byte)(224 + (paramInt >> 12)));
      m_outputBytes[(count++)] = ((byte)(128 + (paramInt >> 6 & 0x3F)));
      m_outputBytes[(count++)] = ((byte)(128 + (paramInt & 0x3F)));
    }
    else
    {
      m_outputBytes[(count++)] = ((byte)(240 + (paramInt >> 18)));
      m_outputBytes[(count++)] = ((byte)(128 + (paramInt >> 12 & 0x3F)));
      m_outputBytes[(count++)] = ((byte)(128 + (paramInt >> 6 & 0x3F)));
      m_outputBytes[(count++)] = ((byte)(128 + (paramInt & 0x3F)));
    }
  }
  
  public void write(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws IOException
  {
    int i = 3 * paramInt2;
    int i1;
    int i2;
    int i3;
    if (i >= 16384 - count)
    {
      flushBuffer();
      if (i > 16384)
      {
        j = paramInt2 / 5461;
        int k;
        if (paramInt2 % 5461 > 0) {
          k = j + 1;
        } else {
          k = j;
        }
        m = paramInt1;
        for (n = 1; n <= k; n++)
        {
          i1 = m;
          m = paramInt1 + (int)(paramInt2 * n / k);
          i2 = paramArrayOfChar[(m - 1)];
          i3 = paramArrayOfChar[(m - 1)];
          if ((i2 >= 55296) && (i2 <= 56319)) {
            if (m < paramInt1 + paramInt2) {
              m++;
            } else {
              m--;
            }
          }
          int i4 = m - i1;
          write(paramArrayOfChar, i1, i4);
        }
        return;
      }
    }
    int j = paramInt2 + paramInt1;
    byte[] arrayOfByte = m_outputBytes;
    int m = count;
    for (int n = paramInt1; (n < j) && ((i1 = paramArrayOfChar[n]) < ''); n++) {
      arrayOfByte[(m++)] = ((byte)i1);
    }
    while (n < j)
    {
      i1 = paramArrayOfChar[n];
      if (i1 < 128)
      {
        arrayOfByte[(m++)] = ((byte)i1);
      }
      else if (i1 < 2048)
      {
        arrayOfByte[(m++)] = ((byte)(192 + (i1 >> 6)));
        arrayOfByte[(m++)] = ((byte)(128 + (i1 & 0x3F)));
      }
      else if ((i1 >= 55296) && (i1 <= 56319))
      {
        i2 = i1;
        n++;
        i3 = paramArrayOfChar[n];
        arrayOfByte[(m++)] = ((byte)(0xF0 | i2 + 64 >> 8 & 0xF0));
        arrayOfByte[(m++)] = ((byte)(0x80 | i2 + 64 >> 2 & 0x3F));
        arrayOfByte[(m++)] = ((byte)(0x80 | (i3 >> 6 & 0xF) + (i2 << 4 & 0x30)));
        arrayOfByte[(m++)] = ((byte)(0x80 | i3 & 0x3F));
      }
      else
      {
        arrayOfByte[(m++)] = ((byte)(224 + (i1 >> 12)));
        arrayOfByte[(m++)] = ((byte)(128 + (i1 >> 6 & 0x3F)));
        arrayOfByte[(m++)] = ((byte)(128 + (i1 & 0x3F)));
      }
      n++;
    }
    count = m;
  }
  
  public void write(String paramString)
    throws IOException
  {
    int i = paramString.length();
    int j = 3 * i;
    int i3;
    int i4;
    int i5;
    if (j >= 16384 - count)
    {
      flushBuffer();
      if (j > 16384)
      {
        int k = 0;
        m = i / 5461;
        int n;
        if (i % 5461 > 0) {
          n = m + 1;
        } else {
          n = m;
        }
        i1 = 0;
        for (i2 = 1; i2 <= n; i2++)
        {
          i3 = i1;
          i1 = 0 + (int)(i * i2 / n);
          paramString.getChars(i3, i1, m_inputChars, 0);
          i4 = i1 - i3;
          i5 = m_inputChars[(i4 - 1)];
          if ((i5 >= 55296) && (i5 <= 56319))
          {
            i1--;
            i4--;
            if (i2 != n) {}
          }
          write(m_inputChars, 0, i4);
        }
        return;
      }
    }
    paramString.getChars(0, i, m_inputChars, 0);
    char[] arrayOfChar = m_inputChars;
    int m = i;
    byte[] arrayOfByte = m_outputBytes;
    int i1 = count;
    for (int i2 = 0; (i2 < m) && ((i3 = arrayOfChar[i2]) < ''); i2++) {
      arrayOfByte[(i1++)] = ((byte)i3);
    }
    while (i2 < m)
    {
      i3 = arrayOfChar[i2];
      if (i3 < 128)
      {
        arrayOfByte[(i1++)] = ((byte)i3);
      }
      else if (i3 < 2048)
      {
        arrayOfByte[(i1++)] = ((byte)(192 + (i3 >> 6)));
        arrayOfByte[(i1++)] = ((byte)(128 + (i3 & 0x3F)));
      }
      else if ((i3 >= 55296) && (i3 <= 56319))
      {
        i4 = i3;
        i2++;
        i5 = arrayOfChar[i2];
        arrayOfByte[(i1++)] = ((byte)(0xF0 | i4 + 64 >> 8 & 0xF0));
        arrayOfByte[(i1++)] = ((byte)(0x80 | i4 + 64 >> 2 & 0x3F));
        arrayOfByte[(i1++)] = ((byte)(0x80 | (i5 >> 6 & 0xF) + (i4 << 4 & 0x30)));
        arrayOfByte[(i1++)] = ((byte)(0x80 | i5 & 0x3F));
      }
      else
      {
        arrayOfByte[(i1++)] = ((byte)(224 + (i3 >> 12)));
        arrayOfByte[(i1++)] = ((byte)(128 + (i3 >> 6 & 0x3F)));
        arrayOfByte[(i1++)] = ((byte)(128 + (i3 & 0x3F)));
      }
      i2++;
    }
    count = i1;
  }
  
  public void flushBuffer()
    throws IOException
  {
    if (count > 0)
    {
      m_os.write(m_outputBytes, 0, count);
      count = 0;
    }
  }
  
  public void flush()
    throws IOException
  {
    flushBuffer();
    m_os.flush();
  }
  
  public void close()
    throws IOException
  {
    flushBuffer();
    m_os.close();
  }
  
  public OutputStream getOutputStream()
  {
    return m_os;
  }
  
  public Writer getWriter()
  {
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\serializer\WriterToUTF8Buffered.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */