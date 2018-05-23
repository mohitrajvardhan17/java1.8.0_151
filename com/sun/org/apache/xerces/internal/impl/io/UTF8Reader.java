package com.sun.org.apache.xerces.internal.impl.io;

import com.sun.org.apache.xerces.internal.impl.msg.XMLMessageFormatter;
import com.sun.org.apache.xerces.internal.util.MessageFormatter;
import com.sun.xml.internal.stream.util.BufferAllocator;
import com.sun.xml.internal.stream.util.ThreadLocalBufferAllocator;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Locale;

public class UTF8Reader
  extends Reader
{
  public static final int DEFAULT_BUFFER_SIZE = 2048;
  private static final boolean DEBUG_READ = false;
  protected InputStream fInputStream;
  protected byte[] fBuffer;
  protected int fOffset;
  private int fSurrogate = -1;
  private MessageFormatter fFormatter = null;
  private Locale fLocale = null;
  
  public UTF8Reader(InputStream paramInputStream)
  {
    this(paramInputStream, 2048, new XMLMessageFormatter(), Locale.getDefault());
  }
  
  public UTF8Reader(InputStream paramInputStream, MessageFormatter paramMessageFormatter, Locale paramLocale)
  {
    this(paramInputStream, 2048, paramMessageFormatter, paramLocale);
  }
  
  public UTF8Reader(InputStream paramInputStream, int paramInt, MessageFormatter paramMessageFormatter, Locale paramLocale)
  {
    fInputStream = paramInputStream;
    BufferAllocator localBufferAllocator = ThreadLocalBufferAllocator.getBufferAllocator();
    fBuffer = localBufferAllocator.getByteBuffer(paramInt);
    if (fBuffer == null) {
      fBuffer = new byte[paramInt];
    }
    fFormatter = paramMessageFormatter;
    fLocale = paramLocale;
  }
  
  public int read()
    throws IOException
  {
    int i = fSurrogate;
    if (fSurrogate == -1)
    {
      int j = 0;
      int k = j == fOffset ? fInputStream.read() : fBuffer[(j++)] & 0xFF;
      if (k == -1) {
        return -1;
      }
      if (k < 128)
      {
        i = (char)k;
      }
      else
      {
        int m;
        if (((k & 0xE0) == 192) && ((k & 0x1E) != 0))
        {
          m = j == fOffset ? fInputStream.read() : fBuffer[(j++)] & 0xFF;
          if (m == -1) {
            expectedByte(2, 2);
          }
          if ((m & 0xC0) != 128) {
            invalidByte(2, 2, m);
          }
          i = k << 6 & 0x7C0 | m & 0x3F;
        }
        else
        {
          int n;
          if ((k & 0xF0) == 224)
          {
            m = j == fOffset ? fInputStream.read() : fBuffer[(j++)] & 0xFF;
            if (m == -1) {
              expectedByte(2, 3);
            }
            if (((m & 0xC0) != 128) || ((k == 237) && (m >= 160)) || (((k & 0xF) == 0) && ((m & 0x20) == 0))) {
              invalidByte(2, 3, m);
            }
            n = j == fOffset ? fInputStream.read() : fBuffer[(j++)] & 0xFF;
            if (n == -1) {
              expectedByte(3, 3);
            }
            if ((n & 0xC0) != 128) {
              invalidByte(3, 3, n);
            }
            i = k << 12 & 0xF000 | m << 6 & 0xFC0 | n & 0x3F;
          }
          else if ((k & 0xF8) == 240)
          {
            m = j == fOffset ? fInputStream.read() : fBuffer[(j++)] & 0xFF;
            if (m == -1) {
              expectedByte(2, 4);
            }
            if (((m & 0xC0) != 128) || (((m & 0x30) == 0) && ((k & 0x7) == 0))) {
              invalidByte(2, 3, m);
            }
            n = j == fOffset ? fInputStream.read() : fBuffer[(j++)] & 0xFF;
            if (n == -1) {
              expectedByte(3, 4);
            }
            if ((n & 0xC0) != 128) {
              invalidByte(3, 3, n);
            }
            int i1 = j == fOffset ? fInputStream.read() : fBuffer[(j++)] & 0xFF;
            if (i1 == -1) {
              expectedByte(4, 4);
            }
            if ((i1 & 0xC0) != 128) {
              invalidByte(4, 4, i1);
            }
            int i2 = k << 2 & 0x1C | m >> 4 & 0x3;
            if (i2 > 16) {
              invalidSurrogate(i2);
            }
            int i3 = i2 - 1;
            int i4 = 0xD800 | i3 << 6 & 0x3C0 | m << 2 & 0x3C | n >> 4 & 0x3;
            int i5 = 0xDC00 | n << 6 & 0x3C0 | i1 & 0x3F;
            i = i4;
            fSurrogate = i5;
          }
          else
          {
            invalidByte(1, 1, k);
          }
        }
      }
    }
    else
    {
      fSurrogate = -1;
    }
    return i;
  }
  
  public int read(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws IOException
  {
    int i = paramInt1;
    if (fSurrogate != -1)
    {
      paramArrayOfChar[(paramInt1 + 1)] = ((char)fSurrogate);
      fSurrogate = -1;
      paramInt2--;
      i++;
    }
    int j = 0;
    if (fOffset == 0)
    {
      if (paramInt2 > fBuffer.length) {
        paramInt2 = fBuffer.length;
      }
      j = fInputStream.read(fBuffer, 0, paramInt2);
      if (j == -1) {
        return -1;
      }
      j += i - paramInt1;
    }
    else
    {
      j = fOffset;
      fOffset = 0;
    }
    int k = j;
    int i1 = 0;
    int n;
    for (int m = 0; m < k; m++)
    {
      n = fBuffer[m];
      if (n < 0) {
        break;
      }
      paramArrayOfChar[(i++)] = ((char)n);
    }
    while (m < k)
    {
      n = fBuffer[m];
      if (n >= 0)
      {
        paramArrayOfChar[(i++)] = ((char)n);
      }
      else
      {
        int i2 = n & 0xFF;
        int i3;
        int i4;
        if (((i2 & 0xE0) == 192) && ((i2 & 0x1E) != 0))
        {
          i3 = -1;
          m++;
          if (m < k)
          {
            i3 = fBuffer[m] & 0xFF;
          }
          else
          {
            i3 = fInputStream.read();
            if (i3 == -1)
            {
              if (i > paramInt1)
              {
                fBuffer[0] = ((byte)i2);
                fOffset = 1;
                return i - paramInt1;
              }
              expectedByte(2, 2);
            }
            j++;
          }
          if ((i3 & 0xC0) != 128)
          {
            if (i > paramInt1)
            {
              fBuffer[0] = ((byte)i2);
              fBuffer[1] = ((byte)i3);
              fOffset = 2;
              return i - paramInt1;
            }
            invalidByte(2, 2, i3);
          }
          i4 = i2 << 6 & 0x7C0 | i3 & 0x3F;
          paramArrayOfChar[(i++)] = ((char)i4);
          j--;
        }
        else
        {
          int i5;
          if ((i2 & 0xF0) == 224)
          {
            i3 = -1;
            m++;
            if (m < k)
            {
              i3 = fBuffer[m] & 0xFF;
            }
            else
            {
              i3 = fInputStream.read();
              if (i3 == -1)
              {
                if (i > paramInt1)
                {
                  fBuffer[0] = ((byte)i2);
                  fOffset = 1;
                  return i - paramInt1;
                }
                expectedByte(2, 3);
              }
              j++;
            }
            if (((i3 & 0xC0) != 128) || ((i2 == 237) && (i3 >= 160)) || (((i2 & 0xF) == 0) && ((i3 & 0x20) == 0)))
            {
              if (i > paramInt1)
              {
                fBuffer[0] = ((byte)i2);
                fBuffer[1] = ((byte)i3);
                fOffset = 2;
                return i - paramInt1;
              }
              invalidByte(2, 3, i3);
            }
            i4 = -1;
            m++;
            if (m < k)
            {
              i4 = fBuffer[m] & 0xFF;
            }
            else
            {
              i4 = fInputStream.read();
              if (i4 == -1)
              {
                if (i > paramInt1)
                {
                  fBuffer[0] = ((byte)i2);
                  fBuffer[1] = ((byte)i3);
                  fOffset = 2;
                  return i - paramInt1;
                }
                expectedByte(3, 3);
              }
              j++;
            }
            if ((i4 & 0xC0) != 128)
            {
              if (i > paramInt1)
              {
                fBuffer[0] = ((byte)i2);
                fBuffer[1] = ((byte)i3);
                fBuffer[2] = ((byte)i4);
                fOffset = 3;
                return i - paramInt1;
              }
              invalidByte(3, 3, i4);
            }
            i5 = i2 << 12 & 0xF000 | i3 << 6 & 0xFC0 | i4 & 0x3F;
            paramArrayOfChar[(i++)] = ((char)i5);
            j -= 2;
          }
          else if ((i2 & 0xF8) == 240)
          {
            i3 = -1;
            m++;
            if (m < k)
            {
              i3 = fBuffer[m] & 0xFF;
            }
            else
            {
              i3 = fInputStream.read();
              if (i3 == -1)
              {
                if (i > paramInt1)
                {
                  fBuffer[0] = ((byte)i2);
                  fOffset = 1;
                  return i - paramInt1;
                }
                expectedByte(2, 4);
              }
              j++;
            }
            if (((i3 & 0xC0) != 128) || (((i3 & 0x30) == 0) && ((i2 & 0x7) == 0)))
            {
              if (i > paramInt1)
              {
                fBuffer[0] = ((byte)i2);
                fBuffer[1] = ((byte)i3);
                fOffset = 2;
                return i - paramInt1;
              }
              invalidByte(2, 4, i3);
            }
            i4 = -1;
            m++;
            if (m < k)
            {
              i4 = fBuffer[m] & 0xFF;
            }
            else
            {
              i4 = fInputStream.read();
              if (i4 == -1)
              {
                if (i > paramInt1)
                {
                  fBuffer[0] = ((byte)i2);
                  fBuffer[1] = ((byte)i3);
                  fOffset = 2;
                  return i - paramInt1;
                }
                expectedByte(3, 4);
              }
              j++;
            }
            if ((i4 & 0xC0) != 128)
            {
              if (i > paramInt1)
              {
                fBuffer[0] = ((byte)i2);
                fBuffer[1] = ((byte)i3);
                fBuffer[2] = ((byte)i4);
                fOffset = 3;
                return i - paramInt1;
              }
              invalidByte(3, 4, i4);
            }
            i5 = -1;
            m++;
            if (m < k)
            {
              i5 = fBuffer[m] & 0xFF;
            }
            else
            {
              i5 = fInputStream.read();
              if (i5 == -1)
              {
                if (i > paramInt1)
                {
                  fBuffer[0] = ((byte)i2);
                  fBuffer[1] = ((byte)i3);
                  fBuffer[2] = ((byte)i4);
                  fOffset = 3;
                  return i - paramInt1;
                }
                expectedByte(4, 4);
              }
              j++;
            }
            if ((i5 & 0xC0) != 128)
            {
              if (i > paramInt1)
              {
                fBuffer[0] = ((byte)i2);
                fBuffer[1] = ((byte)i3);
                fBuffer[2] = ((byte)i4);
                fBuffer[3] = ((byte)i5);
                fOffset = 4;
                return i - paramInt1;
              }
              invalidByte(4, 4, i4);
            }
            if (i + 1 >= paramArrayOfChar.length)
            {
              fBuffer[0] = ((byte)i2);
              fBuffer[1] = ((byte)i3);
              fBuffer[2] = ((byte)i4);
              fBuffer[3] = ((byte)i5);
              fOffset = 4;
              return i - paramInt1;
            }
            int i6 = i2 << 2 & 0x1C | i3 >> 4 & 0x3;
            if (i6 > 16) {
              invalidSurrogate(i6);
            }
            int i7 = i6 - 1;
            int i8 = i3 & 0xF;
            int i9 = i4 & 0x3F;
            int i10 = i5 & 0x3F;
            int i11 = 0xD800 | i7 << 6 & 0x3C0 | i8 << 2 | i9 >> 4;
            int i12 = 0xDC00 | i9 << 6 & 0x3C0 | i10;
            paramArrayOfChar[(i++)] = ((char)i11);
            paramArrayOfChar[(i++)] = ((char)i12);
            j -= 2;
          }
          else
          {
            if (i > paramInt1)
            {
              fBuffer[0] = ((byte)i2);
              fOffset = 1;
              return i - paramInt1;
            }
            invalidByte(1, 1, i2);
          }
        }
      }
      m++;
    }
    return j;
  }
  
  public long skip(long paramLong)
    throws IOException
  {
    long l1 = paramLong;
    char[] arrayOfChar = new char[fBuffer.length];
    do
    {
      int i = arrayOfChar.length < l1 ? arrayOfChar.length : (int)l1;
      int j = read(arrayOfChar, 0, i);
      if (j <= 0) {
        break;
      }
      l1 -= j;
    } while (l1 > 0L);
    long l2 = paramLong - l1;
    return l2;
  }
  
  public boolean ready()
    throws IOException
  {
    return false;
  }
  
  public boolean markSupported()
  {
    return false;
  }
  
  public void mark(int paramInt)
    throws IOException
  {
    throw new IOException(fFormatter.formatMessage(fLocale, "OperationNotSupported", new Object[] { "mark()", "UTF-8" }));
  }
  
  public void reset()
    throws IOException
  {
    fOffset = 0;
    fSurrogate = -1;
  }
  
  public void close()
    throws IOException
  {
    BufferAllocator localBufferAllocator = ThreadLocalBufferAllocator.getBufferAllocator();
    localBufferAllocator.returnByteBuffer(fBuffer);
    fBuffer = null;
    fInputStream.close();
  }
  
  private void expectedByte(int paramInt1, int paramInt2)
    throws MalformedByteSequenceException
  {
    throw new MalformedByteSequenceException(fFormatter, fLocale, "http://www.w3.org/TR/1998/REC-xml-19980210", "ExpectedByte", new Object[] { Integer.toString(paramInt1), Integer.toString(paramInt2) });
  }
  
  private void invalidByte(int paramInt1, int paramInt2, int paramInt3)
    throws MalformedByteSequenceException
  {
    throw new MalformedByteSequenceException(fFormatter, fLocale, "http://www.w3.org/TR/1998/REC-xml-19980210", "InvalidByte", new Object[] { Integer.toString(paramInt1), Integer.toString(paramInt2) });
  }
  
  private void invalidSurrogate(int paramInt)
    throws MalformedByteSequenceException
  {
    throw new MalformedByteSequenceException(fFormatter, fLocale, "http://www.w3.org/TR/1998/REC-xml-19980210", "InvalidHighSurrogate", new Object[] { Integer.toHexString(paramInt) });
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\io\UTF8Reader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */