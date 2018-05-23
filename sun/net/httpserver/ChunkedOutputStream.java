package sun.net.httpserver;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

class ChunkedOutputStream
  extends FilterOutputStream
{
  private boolean closed = false;
  static final int CHUNK_SIZE = 4096;
  static final int OFFSET = 6;
  private int pos = 6;
  private int count = 0;
  private byte[] buf = new byte['ဈ'];
  ExchangeImpl t;
  
  ChunkedOutputStream(ExchangeImpl paramExchangeImpl, OutputStream paramOutputStream)
  {
    super(paramOutputStream);
    t = paramExchangeImpl;
  }
  
  public void write(int paramInt)
    throws IOException
  {
    if (closed) {
      throw new StreamClosedException();
    }
    buf[(pos++)] = ((byte)paramInt);
    count += 1;
    if (count == 4096) {
      writeChunk();
    }
    assert (count < 4096);
  }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    if (closed) {
      throw new StreamClosedException();
    }
    int i = 4096 - count;
    if (paramInt2 > i)
    {
      System.arraycopy(paramArrayOfByte, paramInt1, buf, pos, i);
      count = 4096;
      writeChunk();
      paramInt2 -= i;
      paramInt1 += i;
      while (paramInt2 >= 4096)
      {
        System.arraycopy(paramArrayOfByte, paramInt1, buf, 6, 4096);
        paramInt2 -= 4096;
        paramInt1 += 4096;
        count = 4096;
        writeChunk();
      }
    }
    if (paramInt2 > 0)
    {
      System.arraycopy(paramArrayOfByte, paramInt1, buf, pos, paramInt2);
      count += paramInt2;
      pos += paramInt2;
    }
    if (count == 4096) {
      writeChunk();
    }
  }
  
  private void writeChunk()
    throws IOException
  {
    char[] arrayOfChar = Integer.toHexString(count).toCharArray();
    int i = arrayOfChar.length;
    int j = 4 - i;
    for (int k = 0; k < i; k++) {
      buf[(j + k)] = ((byte)arrayOfChar[k]);
    }
    buf[(j + k++)] = 13;
    buf[(j + k++)] = 10;
    buf[(j + k++ + count)] = 13;
    buf[(j + k++ + count)] = 10;
    out.write(buf, j, k + count);
    count = 0;
    pos = 6;
  }
  
  /* Error */
  public void close()
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 110	sun/net/httpserver/ChunkedOutputStream:closed	Z
    //   4: ifeq +4 -> 8
    //   7: return
    //   8: aload_0
    //   9: invokevirtual 122	sun/net/httpserver/ChunkedOutputStream:flush	()V
    //   12: aload_0
    //   13: invokespecial 123	sun/net/httpserver/ChunkedOutputStream:writeChunk	()V
    //   16: aload_0
    //   17: getfield 112	sun/net/httpserver/ChunkedOutputStream:out	Ljava/io/OutputStream;
    //   20: invokevirtual 115	java/io/OutputStream:flush	()V
    //   23: aload_0
    //   24: getfield 113	sun/net/httpserver/ChunkedOutputStream:t	Lsun/net/httpserver/ExchangeImpl;
    //   27: invokevirtual 125	sun/net/httpserver/ExchangeImpl:getOriginalInputStream	()Lsun/net/httpserver/LeftOverInputStream;
    //   30: astore_1
    //   31: aload_1
    //   32: invokevirtual 128	sun/net/httpserver/LeftOverInputStream:isClosed	()Z
    //   35: ifne +7 -> 42
    //   38: aload_1
    //   39: invokevirtual 127	sun/net/httpserver/LeftOverInputStream:close	()V
    //   42: aload_0
    //   43: iconst_1
    //   44: putfield 110	sun/net/httpserver/ChunkedOutputStream:closed	Z
    //   47: goto +20 -> 67
    //   50: astore_1
    //   51: aload_0
    //   52: iconst_1
    //   53: putfield 110	sun/net/httpserver/ChunkedOutputStream:closed	Z
    //   56: goto +11 -> 67
    //   59: astore_2
    //   60: aload_0
    //   61: iconst_1
    //   62: putfield 110	sun/net/httpserver/ChunkedOutputStream:closed	Z
    //   65: aload_2
    //   66: athrow
    //   67: new 73	sun/net/httpserver/WriteFinishedEvent
    //   70: dup
    //   71: aload_0
    //   72: getfield 113	sun/net/httpserver/ChunkedOutputStream:t	Lsun/net/httpserver/ExchangeImpl;
    //   75: invokespecial 131	sun/net/httpserver/WriteFinishedEvent:<init>	(Lsun/net/httpserver/ExchangeImpl;)V
    //   78: astore_1
    //   79: aload_0
    //   80: getfield 113	sun/net/httpserver/ChunkedOutputStream:t	Lsun/net/httpserver/ExchangeImpl;
    //   83: invokevirtual 124	sun/net/httpserver/ExchangeImpl:getHttpContext	()Lsun/net/httpserver/HttpContextImpl;
    //   86: invokevirtual 126	sun/net/httpserver/HttpContextImpl:getServerImpl	()Lsun/net/httpserver/ServerImpl;
    //   89: aload_1
    //   90: invokevirtual 129	sun/net/httpserver/ServerImpl:addEvent	(Lsun/net/httpserver/Event;)V
    //   93: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	94	0	this	ChunkedOutputStream
    //   30	9	1	localLeftOverInputStream	LeftOverInputStream
    //   50	1	1	localIOException	IOException
    //   78	12	1	localWriteFinishedEvent	WriteFinishedEvent
    //   59	7	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   12	42	50	java/io/IOException
    //   12	42	59	finally
  }
  
  public void flush()
    throws IOException
  {
    if (closed) {
      throw new StreamClosedException();
    }
    if (count > 0) {
      writeChunk();
    }
    out.flush();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\httpserver\ChunkedOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */