package com.sun.org.apache.xerces.internal.impl.io;

import com.sun.org.apache.xerces.internal.util.MessageFormatter;
import com.sun.xml.internal.stream.util.BufferAllocator;
import com.sun.xml.internal.stream.util.ThreadLocalBufferAllocator;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Locale;

public class ASCIIReader
  extends Reader
{
  public static final int DEFAULT_BUFFER_SIZE = 2048;
  protected InputStream fInputStream;
  protected byte[] fBuffer;
  private MessageFormatter fFormatter = null;
  private Locale fLocale = null;
  
  public ASCIIReader(InputStream paramInputStream, MessageFormatter paramMessageFormatter, Locale paramLocale)
  {
    this(paramInputStream, 2048, paramMessageFormatter, paramLocale);
  }
  
  public ASCIIReader(InputStream paramInputStream, int paramInt, MessageFormatter paramMessageFormatter, Locale paramLocale)
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
    int i = fInputStream.read();
    if (i >= 128) {
      throw new MalformedByteSequenceException(fFormatter, fLocale, "http://www.w3.org/TR/1998/REC-xml-19980210", "InvalidASCII", new Object[] { Integer.toString(i) });
    }
    return i;
  }
  
  public int read(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws IOException
  {
    if (paramInt2 > fBuffer.length) {
      paramInt2 = fBuffer.length;
    }
    int i = fInputStream.read(fBuffer, 0, paramInt2);
    for (int j = 0; j < i; j++)
    {
      int k = fBuffer[j];
      if (k < 0) {
        throw new MalformedByteSequenceException(fFormatter, fLocale, "http://www.w3.org/TR/1998/REC-xml-19980210", "InvalidASCII", new Object[] { Integer.toString(k & 0xFF) });
      }
      paramArrayOfChar[(paramInt1 + j)] = ((char)k);
    }
    return i;
  }
  
  public long skip(long paramLong)
    throws IOException
  {
    return fInputStream.skip(paramLong);
  }
  
  public boolean ready()
    throws IOException
  {
    return false;
  }
  
  public boolean markSupported()
  {
    return fInputStream.markSupported();
  }
  
  public void mark(int paramInt)
    throws IOException
  {
    fInputStream.mark(paramInt);
  }
  
  public void reset()
    throws IOException
  {
    fInputStream.reset();
  }
  
  public void close()
    throws IOException
  {
    BufferAllocator localBufferAllocator = ThreadLocalBufferAllocator.getBufferAllocator();
    localBufferAllocator.returnByteBuffer(fBuffer);
    fBuffer = null;
    fInputStream.close();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\io\ASCIIReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */