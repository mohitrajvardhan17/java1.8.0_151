package sun.net.www.http;

import java.io.IOException;
import java.io.InputStream;
import sun.net.www.MessageHeader;

public class ChunkedInputStream
  extends InputStream
  implements Hurryable
{
  private InputStream in;
  private HttpClient hc;
  private MessageHeader responses;
  private int chunkSize;
  private int chunkRead;
  private byte[] chunkData = new byte['က'];
  private int chunkPos;
  private int chunkCount;
  private byte[] rawData = new byte[32];
  private int rawPos;
  private int rawCount;
  private boolean error;
  private boolean closed;
  private static final int MAX_CHUNK_HEADER_SIZE = 2050;
  static final int STATE_AWAITING_CHUNK_HEADER = 1;
  static final int STATE_READING_CHUNK = 2;
  static final int STATE_AWAITING_CHUNK_EOL = 3;
  static final int STATE_AWAITING_TRAILERS = 4;
  static final int STATE_DONE = 5;
  private int state;
  
  private void ensureOpen()
    throws IOException
  {
    if (closed) {
      throw new IOException("stream is closed");
    }
  }
  
  private void ensureRawAvailable(int paramInt)
  {
    if (rawCount + paramInt > rawData.length)
    {
      int i = rawCount - rawPos;
      if (i + paramInt > rawData.length)
      {
        byte[] arrayOfByte = new byte[i + paramInt];
        if (i > 0) {
          System.arraycopy(rawData, rawPos, arrayOfByte, 0, i);
        }
        rawData = arrayOfByte;
      }
      else if (i > 0)
      {
        System.arraycopy(rawData, rawPos, rawData, 0, i);
      }
      rawCount = i;
      rawPos = 0;
    }
  }
  
  private void closeUnderlying()
    throws IOException
  {
    if (in == null) {
      return;
    }
    if ((!error) && (state == 5)) {
      hc.finished();
    } else if (!hurry()) {
      hc.closeServer();
    }
    in = null;
  }
  
  private int fastRead(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    int i = chunkSize - chunkRead;
    int j = i < paramInt2 ? i : paramInt2;
    if (j > 0)
    {
      int k;
      try
      {
        k = in.read(paramArrayOfByte, paramInt1, j);
      }
      catch (IOException localIOException)
      {
        error = true;
        throw localIOException;
      }
      if (k > 0)
      {
        chunkRead += k;
        if (chunkRead >= chunkSize) {
          state = 3;
        }
        return k;
      }
      error = true;
      throw new IOException("Premature EOF");
    }
    return 0;
  }
  
  private void processRaw()
    throws IOException
  {
    while (state != 5)
    {
      int i;
      int j;
      Object localObject;
      switch (state)
      {
      case 1: 
        i = rawPos;
        while ((i < rawCount) && (rawData[i] != 10))
        {
          i++;
          if (i - rawPos >= 2050)
          {
            error = true;
            throw new IOException("Chunk header too long");
          }
        }
        if (i >= rawCount) {
          return;
        }
        String str1 = new String(rawData, rawPos, i - rawPos + 1, "US-ASCII");
        for (j = 0; (j < str1.length()) && (Character.digit(str1.charAt(j), 16) != -1); j++) {}
        try
        {
          chunkSize = Integer.parseInt(str1.substring(0, j), 16);
        }
        catch (NumberFormatException localNumberFormatException)
        {
          error = true;
          throw new IOException("Bogus chunk size");
        }
        rawPos = (i + 1);
        chunkRead = 0;
        if (chunkSize > 0) {
          state = 2;
        } else {
          state = 4;
        }
        break;
      case 2: 
        if (rawPos >= rawCount) {
          return;
        }
        int k = Math.min(chunkSize - chunkRead, rawCount - rawPos);
        if (chunkData.length < chunkCount + k)
        {
          int m = chunkCount - chunkPos;
          if (chunkData.length < m + k)
          {
            localObject = new byte[m + k];
            System.arraycopy(chunkData, chunkPos, localObject, 0, m);
            chunkData = ((byte[])localObject);
          }
          else
          {
            System.arraycopy(chunkData, chunkPos, chunkData, 0, m);
          }
          chunkPos = 0;
          chunkCount = m;
        }
        System.arraycopy(rawData, rawPos, chunkData, chunkCount, k);
        rawPos += k;
        chunkCount += k;
        chunkRead += k;
        if (chunkSize - chunkRead <= 0) {
          state = 3;
        } else {
          return;
        }
        break;
      case 3: 
        if (rawPos + 1 >= rawCount) {
          return;
        }
        if (rawData[rawPos] != 13)
        {
          error = true;
          throw new IOException("missing CR");
        }
        if (rawData[(rawPos + 1)] != 10)
        {
          error = true;
          throw new IOException("missing LF");
        }
        rawPos += 2;
        state = 1;
        break;
      case 4: 
        for (i = rawPos; (i < rawCount) && (rawData[i] != 10); i++) {}
        if (i >= rawCount) {
          return;
        }
        if (i == rawPos)
        {
          error = true;
          throw new IOException("LF should be proceeded by CR");
        }
        if (rawData[(i - 1)] != 13)
        {
          error = true;
          throw new IOException("LF should be proceeded by CR");
        }
        if (i == rawPos + 1)
        {
          state = 5;
          closeUnderlying();
          return;
        }
        String str2 = new String(rawData, rawPos, i - rawPos, "US-ASCII");
        j = str2.indexOf(':');
        if (j == -1) {
          throw new IOException("Malformed tailer - format should be key:value");
        }
        localObject = str2.substring(0, j).trim();
        String str3 = str2.substring(j + 1, str2.length()).trim();
        responses.add((String)localObject, str3);
        rawPos = (i + 1);
      }
    }
  }
  
  private int readAheadNonBlocking()
    throws IOException
  {
    int i = in.available();
    if (i > 0)
    {
      ensureRawAvailable(i);
      int j;
      try
      {
        j = in.read(rawData, rawCount, i);
      }
      catch (IOException localIOException)
      {
        error = true;
        throw localIOException;
      }
      if (j < 0)
      {
        error = true;
        return -1;
      }
      rawCount += j;
      processRaw();
    }
    return chunkCount - chunkPos;
  }
  
  private int readAheadBlocking()
    throws IOException
  {
    do
    {
      if (state == 5) {
        return -1;
      }
      ensureRawAvailable(32);
      int i;
      try
      {
        i = in.read(rawData, rawCount, rawData.length - rawCount);
      }
      catch (IOException localIOException)
      {
        error = true;
        throw localIOException;
      }
      if (i < 0)
      {
        error = true;
        throw new IOException("Premature EOF");
      }
      rawCount += i;
      processRaw();
    } while (chunkCount <= 0);
    return chunkCount - chunkPos;
  }
  
  private int readAhead(boolean paramBoolean)
    throws IOException
  {
    if (state == 5) {
      return -1;
    }
    if (chunkPos >= chunkCount)
    {
      chunkCount = 0;
      chunkPos = 0;
    }
    if (paramBoolean) {
      return readAheadBlocking();
    }
    return readAheadNonBlocking();
  }
  
  public ChunkedInputStream(InputStream paramInputStream, HttpClient paramHttpClient, MessageHeader paramMessageHeader)
    throws IOException
  {
    in = paramInputStream;
    responses = paramMessageHeader;
    hc = paramHttpClient;
    state = 1;
  }
  
  public synchronized int read()
    throws IOException
  {
    ensureOpen();
    if ((chunkPos >= chunkCount) && (readAhead(true) <= 0)) {
      return -1;
    }
    return chunkData[(chunkPos++)] & 0xFF;
  }
  
  public synchronized int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    ensureOpen();
    if ((paramInt1 < 0) || (paramInt1 > paramArrayOfByte.length) || (paramInt2 < 0) || (paramInt1 + paramInt2 > paramArrayOfByte.length) || (paramInt1 + paramInt2 < 0)) {
      throw new IndexOutOfBoundsException();
    }
    if (paramInt2 == 0) {
      return 0;
    }
    int i = chunkCount - chunkPos;
    if (i <= 0)
    {
      if (state == 2) {
        return fastRead(paramArrayOfByte, paramInt1, paramInt2);
      }
      i = readAhead(true);
      if (i < 0) {
        return -1;
      }
    }
    int j = i < paramInt2 ? i : paramInt2;
    System.arraycopy(chunkData, chunkPos, paramArrayOfByte, paramInt1, j);
    chunkPos += j;
    return j;
  }
  
  public synchronized int available()
    throws IOException
  {
    ensureOpen();
    int i = chunkCount - chunkPos;
    if (i > 0) {
      return i;
    }
    i = readAhead(false);
    if (i < 0) {
      return 0;
    }
    return i;
  }
  
  public synchronized void close()
    throws IOException
  {
    if (closed) {
      return;
    }
    closeUnderlying();
    closed = true;
  }
  
  public synchronized boolean hurry()
  {
    if ((in == null) || (error)) {
      return false;
    }
    try
    {
      readAhead(false);
    }
    catch (Exception localException)
    {
      return false;
    }
    if (error) {
      return false;
    }
    return state == 5;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\www\http\ChunkedInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */