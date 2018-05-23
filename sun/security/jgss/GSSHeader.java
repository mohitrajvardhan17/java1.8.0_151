package sun.security.jgss;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import org.ietf.jgss.GSSException;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;

public class GSSHeader
{
  private ObjectIdentifier mechOid = null;
  private byte[] mechOidBytes = null;
  private int mechTokenLength = 0;
  public static final int TOKEN_ID = 96;
  
  public GSSHeader(ObjectIdentifier paramObjectIdentifier, int paramInt)
    throws IOException
  {
    mechOid = paramObjectIdentifier;
    DerOutputStream localDerOutputStream = new DerOutputStream();
    localDerOutputStream.putOID(paramObjectIdentifier);
    mechOidBytes = localDerOutputStream.toByteArray();
    mechTokenLength = paramInt;
  }
  
  public GSSHeader(InputStream paramInputStream)
    throws IOException, GSSException
  {
    int i = paramInputStream.read();
    if (i != 96) {
      throw new GSSException(10, -1, "GSSHeader did not find the right tag");
    }
    int j = getLength(paramInputStream);
    DerValue localDerValue = new DerValue(paramInputStream);
    mechOidBytes = localDerValue.toByteArray();
    mechOid = localDerValue.getOID();
    mechTokenLength = (j - mechOidBytes.length);
  }
  
  public ObjectIdentifier getOid()
  {
    return mechOid;
  }
  
  public int getMechTokenLength()
  {
    return mechTokenLength;
  }
  
  public int getLength()
  {
    int i = mechOidBytes.length + mechTokenLength;
    return 1 + getLenFieldSize(i) + mechOidBytes.length;
  }
  
  public static int getMaxMechTokenSize(ObjectIdentifier paramObjectIdentifier, int paramInt)
  {
    int i = 0;
    try
    {
      DerOutputStream localDerOutputStream = new DerOutputStream();
      localDerOutputStream.putOID(paramObjectIdentifier);
      i = localDerOutputStream.toByteArray().length;
    }
    catch (IOException localIOException) {}
    paramInt -= 1 + i;
    paramInt -= 5;
    return paramInt;
  }
  
  private int getLenFieldSize(int paramInt)
  {
    int i = 1;
    if (paramInt < 128) {
      i = 1;
    } else if (paramInt < 256) {
      i = 2;
    } else if (paramInt < 65536) {
      i = 3;
    } else if (paramInt < 16777216) {
      i = 4;
    } else {
      i = 5;
    }
    return i;
  }
  
  public int encode(OutputStream paramOutputStream)
    throws IOException
  {
    int i = 1 + mechOidBytes.length;
    paramOutputStream.write(96);
    int j = mechOidBytes.length + mechTokenLength;
    i += putLength(j, paramOutputStream);
    paramOutputStream.write(mechOidBytes);
    return i;
  }
  
  private int getLength(InputStream paramInputStream)
    throws IOException
  {
    return getLength(paramInputStream.read(), paramInputStream);
  }
  
  private int getLength(int paramInt, InputStream paramInputStream)
    throws IOException
  {
    int j = paramInt;
    int i;
    if ((j & 0x80) == 0)
    {
      i = j;
    }
    else
    {
      j &= 0x7F;
      if (j == 0) {
        return -1;
      }
      if ((j < 0) || (j > 4)) {
        throw new IOException("DerInputStream.getLength(): lengthTag=" + j + ", " + (j < 0 ? "incorrect DER encoding." : "too big."));
      }
      i = 0;
      while (j > 0)
      {
        i <<= 8;
        i += (0xFF & paramInputStream.read());
        j--;
      }
      if (i < 0) {
        throw new IOException("Invalid length bytes");
      }
    }
    return i;
  }
  
  private int putLength(int paramInt, OutputStream paramOutputStream)
    throws IOException
  {
    int i = 0;
    if (paramInt < 128)
    {
      paramOutputStream.write((byte)paramInt);
      i = 1;
    }
    else if (paramInt < 256)
    {
      paramOutputStream.write(-127);
      paramOutputStream.write((byte)paramInt);
      i = 2;
    }
    else if (paramInt < 65536)
    {
      paramOutputStream.write(-126);
      paramOutputStream.write((byte)(paramInt >> 8));
      paramOutputStream.write((byte)paramInt);
      i = 3;
    }
    else if (paramInt < 16777216)
    {
      paramOutputStream.write(-125);
      paramOutputStream.write((byte)(paramInt >> 16));
      paramOutputStream.write((byte)(paramInt >> 8));
      paramOutputStream.write((byte)paramInt);
      i = 4;
    }
    else
    {
      paramOutputStream.write(-124);
      paramOutputStream.write((byte)(paramInt >> 24));
      paramOutputStream.write((byte)(paramInt >> 16));
      paramOutputStream.write((byte)(paramInt >> 8));
      paramOutputStream.write((byte)paramInt);
      i = 5;
    }
    return i;
  }
  
  private void debug(String paramString)
  {
    System.err.print(paramString);
  }
  
  private String getHexBytes(byte[] paramArrayOfByte, int paramInt)
    throws IOException
  {
    StringBuffer localStringBuffer = new StringBuffer();
    for (int i = 0; i < paramInt; i++)
    {
      int j = paramArrayOfByte[i] >> 4 & 0xF;
      int k = paramArrayOfByte[i] & 0xF;
      localStringBuffer.append(Integer.toHexString(j));
      localStringBuffer.append(Integer.toHexString(k));
      localStringBuffer.append(' ');
    }
    return localStringBuffer.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\jgss\GSSHeader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */