package com.sun.jndi.ldap.sasl;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import javax.security.sasl.SaslClient;
import javax.security.sasl.SaslException;

public class SaslInputStream
  extends InputStream
{
  private static final boolean debug = false;
  private byte[] saslBuffer;
  private byte[] lenBuf = new byte[4];
  private byte[] buf = new byte[0];
  private int bufPos = 0;
  private InputStream in;
  private SaslClient sc;
  private int recvMaxBufSize = 65536;
  
  SaslInputStream(SaslClient paramSaslClient, InputStream paramInputStream)
    throws SaslException
  {
    in = paramInputStream;
    sc = paramSaslClient;
    String str = (String)paramSaslClient.getNegotiatedProperty("javax.security.sasl.maxbuffer");
    if (str != null) {
      try
      {
        recvMaxBufSize = Integer.parseInt(str);
      }
      catch (NumberFormatException localNumberFormatException)
      {
        throw new SaslException("javax.security.sasl.maxbuffer property must be numeric string: " + str);
      }
    }
    saslBuffer = new byte[recvMaxBufSize];
  }
  
  public int read()
    throws IOException
  {
    byte[] arrayOfByte = new byte[1];
    int i = read(arrayOfByte, 0, 1);
    if (i > 0) {
      return arrayOfByte[0];
    }
    return -1;
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    if (bufPos >= buf.length)
    {
      for (i = fill(); i == 0; i = fill()) {}
      if (i == -1) {
        return -1;
      }
    }
    int i = buf.length - bufPos;
    if (paramInt2 > i)
    {
      System.arraycopy(buf, bufPos, paramArrayOfByte, paramInt1, i);
      bufPos = buf.length;
      return i;
    }
    System.arraycopy(buf, bufPos, paramArrayOfByte, paramInt1, paramInt2);
    bufPos += paramInt2;
    return paramInt2;
  }
  
  private int fill()
    throws IOException
  {
    int i = readFully(lenBuf, 4);
    if (i != 4) {
      return -1;
    }
    int j = networkByteOrderToInt(lenBuf, 0, 4);
    if (j > recvMaxBufSize) {
      throw new IOException(j + "exceeds the negotiated receive buffer size limit:" + recvMaxBufSize);
    }
    i = readFully(saslBuffer, j);
    if (i != j) {
      throw new EOFException("Expecting to read " + j + " bytes but got " + i + " bytes before EOF");
    }
    buf = sc.unwrap(saslBuffer, 0, j);
    bufPos = 0;
    return buf.length;
  }
  
  private int readFully(byte[] paramArrayOfByte, int paramInt)
    throws IOException
  {
    int j = 0;
    while (paramInt > 0)
    {
      int i = in.read(paramArrayOfByte, j, paramInt);
      if (i == -1) {
        return j == 0 ? -1 : j;
      }
      j += i;
      paramInt -= i;
    }
    return j;
  }
  
  public int available()
    throws IOException
  {
    return buf.length - bufPos;
  }
  
  public void close()
    throws IOException
  {
    Object localObject = null;
    try
    {
      sc.dispose();
    }
    catch (SaslException localSaslException)
    {
      localObject = localSaslException;
    }
    in.close();
    if (localObject != null) {
      throw ((Throwable)localObject);
    }
  }
  
  private static int networkByteOrderToInt(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    if (paramInt2 > 4) {
      throw new IllegalArgumentException("Cannot handle more than 4 bytes");
    }
    int i = 0;
    for (int j = 0; j < paramInt2; j++)
    {
      i <<= 8;
      i |= paramArrayOfByte[(paramInt1 + j)] & 0xFF;
    }
    return i;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\ldap\sasl\SaslInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */