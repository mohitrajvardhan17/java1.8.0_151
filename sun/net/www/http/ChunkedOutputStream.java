package sun.net.www.http;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

public class ChunkedOutputStream
  extends PrintStream
{
  static final int DEFAULT_CHUNK_SIZE = 4096;
  private static final byte[] CRLF = { 13, 10 };
  private static final int CRLF_SIZE = CRLF.length;
  private static final byte[] FOOTER = CRLF;
  private static final int FOOTER_SIZE = CRLF_SIZE;
  private static final byte[] EMPTY_CHUNK_HEADER = getHeader(0);
  private static final int EMPTY_CHUNK_HEADER_SIZE = getHeaderSize(0);
  private byte[] buf;
  private int size;
  private int count;
  private int spaceInCurrentChunk;
  private PrintStream out;
  private int preferredChunkDataSize;
  private int preferedHeaderSize;
  private int preferredChunkGrossSize;
  private byte[] completeHeader;
  
  private static int getHeaderSize(int paramInt)
  {
    return Integer.toHexString(paramInt).length() + CRLF_SIZE;
  }
  
  private static byte[] getHeader(int paramInt)
  {
    try
    {
      String str = Integer.toHexString(paramInt);
      byte[] arrayOfByte1 = str.getBytes("US-ASCII");
      byte[] arrayOfByte2 = new byte[getHeaderSize(paramInt)];
      for (int i = 0; i < arrayOfByte1.length; i++) {
        arrayOfByte2[i] = arrayOfByte1[i];
      }
      arrayOfByte2[arrayOfByte1.length] = CRLF[0];
      arrayOfByte2[(arrayOfByte1.length + 1)] = CRLF[1];
      return arrayOfByte2;
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      throw new InternalError(localUnsupportedEncodingException.getMessage(), localUnsupportedEncodingException);
    }
  }
  
  public ChunkedOutputStream(PrintStream paramPrintStream)
  {
    this(paramPrintStream, 4096);
  }
  
  public ChunkedOutputStream(PrintStream paramPrintStream, int paramInt)
  {
    super(paramPrintStream);
    out = paramPrintStream;
    if (paramInt <= 0) {
      paramInt = 4096;
    }
    if (paramInt > 0)
    {
      int i = paramInt - getHeaderSize(paramInt) - FOOTER_SIZE;
      if (getHeaderSize(i + 1) < getHeaderSize(paramInt)) {
        i++;
      }
      paramInt = i;
    }
    if (paramInt > 0) {
      preferredChunkDataSize = paramInt;
    } else {
      preferredChunkDataSize = (4096 - getHeaderSize(4096) - FOOTER_SIZE);
    }
    preferedHeaderSize = getHeaderSize(preferredChunkDataSize);
    preferredChunkGrossSize = (preferedHeaderSize + preferredChunkDataSize + FOOTER_SIZE);
    completeHeader = getHeader(preferredChunkDataSize);
    buf = new byte[preferredChunkGrossSize];
    reset();
  }
  
  private void flush(boolean paramBoolean)
  {
    if (spaceInCurrentChunk == 0)
    {
      out.write(buf, 0, preferredChunkGrossSize);
      out.flush();
      reset();
    }
    else if (paramBoolean)
    {
      if (size > 0)
      {
        int i = preferedHeaderSize - getHeaderSize(size);
        System.arraycopy(getHeader(size), 0, buf, i, getHeaderSize(size));
        buf[(count++)] = FOOTER[0];
        buf[(count++)] = FOOTER[1];
        out.write(buf, i, count - i);
      }
      else
      {
        out.write(EMPTY_CHUNK_HEADER, 0, EMPTY_CHUNK_HEADER_SIZE);
      }
      out.flush();
      reset();
    }
  }
  
  public boolean checkError()
  {
    return out.checkError();
  }
  
  private void ensureOpen()
  {
    if (out == null) {
      setError();
    }
  }
  
  public synchronized void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    ensureOpen();
    if ((paramInt1 < 0) || (paramInt1 > paramArrayOfByte.length) || (paramInt2 < 0) || (paramInt1 + paramInt2 > paramArrayOfByte.length) || (paramInt1 + paramInt2 < 0)) {
      throw new IndexOutOfBoundsException();
    }
    if (paramInt2 == 0) {
      return;
    }
    int i = paramInt2;
    int j = paramInt1;
    do
    {
      if (i >= spaceInCurrentChunk)
      {
        for (int k = 0; k < completeHeader.length; k++) {
          buf[k] = completeHeader[k];
        }
        System.arraycopy(paramArrayOfByte, j, buf, count, spaceInCurrentChunk);
        j += spaceInCurrentChunk;
        i -= spaceInCurrentChunk;
        count += spaceInCurrentChunk;
        buf[(count++)] = FOOTER[0];
        buf[(count++)] = FOOTER[1];
        spaceInCurrentChunk = 0;
        flush(false);
        if (checkError()) {
          break;
        }
      }
      else
      {
        System.arraycopy(paramArrayOfByte, j, buf, count, i);
        count += i;
        size += i;
        spaceInCurrentChunk -= i;
        i = 0;
      }
    } while (i > 0);
  }
  
  public synchronized void write(int paramInt)
  {
    byte[] arrayOfByte = { (byte)paramInt };
    write(arrayOfByte, 0, 1);
  }
  
  public synchronized void reset()
  {
    count = preferedHeaderSize;
    size = 0;
    spaceInCurrentChunk = preferredChunkDataSize;
  }
  
  public int size()
  {
    return size;
  }
  
  public synchronized void close()
  {
    ensureOpen();
    if (size > 0) {
      flush(true);
    }
    flush(true);
    out = null;
  }
  
  public synchronized void flush()
  {
    ensureOpen();
    if (size > 0) {
      flush(true);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\www\http\ChunkedOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */