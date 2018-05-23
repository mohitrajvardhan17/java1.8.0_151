package com.sun.jndi.ldap.sasl;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.security.sasl.SaslClient;
import javax.security.sasl.SaslException;

class SaslOutputStream
  extends FilterOutputStream
{
  private static final boolean debug = false;
  private byte[] lenBuf = new byte[4];
  private int rawSendSize = 65536;
  private SaslClient sc;
  
  SaslOutputStream(SaslClient paramSaslClient, OutputStream paramOutputStream)
    throws SaslException
  {
    super(paramOutputStream);
    sc = paramSaslClient;
    String str = (String)paramSaslClient.getNegotiatedProperty("javax.security.sasl.rawsendsize");
    if (str != null) {
      try
      {
        rawSendSize = Integer.parseInt(str);
      }
      catch (NumberFormatException localNumberFormatException)
      {
        throw new SaslException("javax.security.sasl.rawsendsize property must be numeric string: " + str);
      }
    }
  }
  
  public void write(int paramInt)
    throws IOException
  {
    byte[] arrayOfByte = new byte[1];
    arrayOfByte[0] = ((byte)paramInt);
    write(arrayOfByte, 0, 1);
  }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    int j = 0;
    while (j < paramInt2)
    {
      int i = paramInt2 - j < rawSendSize ? paramInt2 - j : rawSendSize;
      byte[] arrayOfByte = sc.wrap(paramArrayOfByte, paramInt1 + j, i);
      intToNetworkByteOrder(arrayOfByte.length, lenBuf, 0, 4);
      out.write(lenBuf, 0, 4);
      out.write(arrayOfByte, 0, arrayOfByte.length);
      j += rawSendSize;
    }
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
    super.close();
    if (localObject != null) {
      throw ((Throwable)localObject);
    }
  }
  
  private static void intToNetworkByteOrder(int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3)
  {
    if (paramInt3 > 4) {
      throw new IllegalArgumentException("Cannot handle more than 4 bytes");
    }
    for (int i = paramInt3 - 1; i >= 0; i--)
    {
      paramArrayOfByte[(paramInt2 + i)] = ((byte)(paramInt1 & 0xFF));
      paramInt1 >>>= 8;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\ldap\sasl\SaslOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */