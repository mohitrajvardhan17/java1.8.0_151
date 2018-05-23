package sun.rmi.transport.proxy;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import sun.rmi.runtime.Log;

class HttpInputStream
  extends FilterInputStream
{
  protected int bytesLeft;
  protected int bytesLeftAtMark;
  
  public HttpInputStream(InputStream paramInputStream)
    throws IOException
  {
    super(paramInputStream);
    if (paramInputStream.markSupported()) {
      paramInputStream.mark(0);
    }
    DataInputStream localDataInputStream = new DataInputStream(paramInputStream);
    String str1 = "Content-length:".toLowerCase();
    int i = 0;
    String str2;
    do
    {
      str2 = localDataInputStream.readLine();
      if (RMIMasterSocketFactory.proxyLog.isLoggable(Log.VERBOSE)) {
        RMIMasterSocketFactory.proxyLog.log(Log.VERBOSE, "received header line: \"" + str2 + "\"");
      }
      if (str2 == null) {
        throw new EOFException();
      }
      if (str2.toLowerCase().startsWith(str1))
      {
        if (i != 0) {
          throw new IOException("Multiple Content-length entries found.");
        }
        bytesLeft = Integer.parseInt(str2.substring(str1.length()).trim());
        i = 1;
      }
    } while ((str2.length() != 0) && (str2.charAt(0) != '\r') && (str2.charAt(0) != '\n'));
    if ((i == 0) || (bytesLeft < 0)) {
      bytesLeft = Integer.MAX_VALUE;
    }
    bytesLeftAtMark = bytesLeft;
    if (RMIMasterSocketFactory.proxyLog.isLoggable(Log.VERBOSE)) {
      RMIMasterSocketFactory.proxyLog.log(Log.VERBOSE, "content length: " + bytesLeft);
    }
  }
  
  public int available()
    throws IOException
  {
    int i = in.available();
    if (i > bytesLeft) {
      i = bytesLeft;
    }
    return i;
  }
  
  public int read()
    throws IOException
  {
    if (bytesLeft > 0)
    {
      int i = in.read();
      if (i != -1) {
        bytesLeft -= 1;
      }
      if (RMIMasterSocketFactory.proxyLog.isLoggable(Log.VERBOSE)) {
        RMIMasterSocketFactory.proxyLog.log(Log.VERBOSE, "received byte: '" + ((i & 0x7F) < 32 ? " " : String.valueOf((char)i)) + "' " + i);
      }
      return i;
    }
    RMIMasterSocketFactory.proxyLog.log(Log.VERBOSE, "read past content length");
    return -1;
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    if ((bytesLeft == 0) && (paramInt2 > 0))
    {
      RMIMasterSocketFactory.proxyLog.log(Log.VERBOSE, "read past content length");
      return -1;
    }
    if (paramInt2 > bytesLeft) {
      paramInt2 = bytesLeft;
    }
    int i = in.read(paramArrayOfByte, paramInt1, paramInt2);
    bytesLeft -= i;
    if (RMIMasterSocketFactory.proxyLog.isLoggable(Log.VERBOSE)) {
      RMIMasterSocketFactory.proxyLog.log(Log.VERBOSE, "read " + i + " bytes, " + bytesLeft + " remaining");
    }
    return i;
  }
  
  public void mark(int paramInt)
  {
    in.mark(paramInt);
    if (in.markSupported()) {
      bytesLeftAtMark = bytesLeft;
    }
  }
  
  public void reset()
    throws IOException
  {
    in.reset();
    bytesLeft = bytesLeftAtMark;
  }
  
  public long skip(long paramLong)
    throws IOException
  {
    if (paramLong > bytesLeft) {
      paramLong = bytesLeft;
    }
    long l = in.skip(paramLong);
    bytesLeft = ((int)(bytesLeft - l));
    return l;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\rmi\transport\proxy\HttpInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */