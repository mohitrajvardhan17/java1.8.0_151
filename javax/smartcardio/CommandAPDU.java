package javax.smartcardio;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Arrays;

public final class CommandAPDU
  implements Serializable
{
  private static final long serialVersionUID = 398698301286670877L;
  private static final int MAX_APDU_SIZE = 65544;
  private byte[] apdu;
  private transient int nc;
  private transient int ne;
  private transient int dataOffset;
  
  public CommandAPDU(byte[] paramArrayOfByte)
  {
    apdu = ((byte[])paramArrayOfByte.clone());
    parse();
  }
  
  public CommandAPDU(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    checkArrayBounds(paramArrayOfByte, paramInt1, paramInt2);
    apdu = new byte[paramInt2];
    System.arraycopy(paramArrayOfByte, paramInt1, apdu, 0, paramInt2);
    parse();
  }
  
  private void checkArrayBounds(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    if ((paramInt1 < 0) || (paramInt2 < 0)) {
      throw new IllegalArgumentException("Offset and length must not be negative");
    }
    if (paramArrayOfByte == null)
    {
      if ((paramInt1 != 0) && (paramInt2 != 0)) {
        throw new IllegalArgumentException("offset and length must be 0 if array is null");
      }
    }
    else if (paramInt1 > paramArrayOfByte.length - paramInt2) {
      throw new IllegalArgumentException("Offset plus length exceed array size");
    }
  }
  
  public CommandAPDU(ByteBuffer paramByteBuffer)
  {
    apdu = new byte[paramByteBuffer.remaining()];
    paramByteBuffer.get(apdu);
    parse();
  }
  
  public CommandAPDU(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    this(paramInt1, paramInt2, paramInt3, paramInt4, null, 0, 0, 0);
  }
  
  public CommandAPDU(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    this(paramInt1, paramInt2, paramInt3, paramInt4, null, 0, 0, paramInt5);
  }
  
  public CommandAPDU(int paramInt1, int paramInt2, int paramInt3, int paramInt4, byte[] paramArrayOfByte)
  {
    this(paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfByte, 0, arrayLength(paramArrayOfByte), 0);
  }
  
  public CommandAPDU(int paramInt1, int paramInt2, int paramInt3, int paramInt4, byte[] paramArrayOfByte, int paramInt5, int paramInt6)
  {
    this(paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfByte, paramInt5, paramInt6, 0);
  }
  
  public CommandAPDU(int paramInt1, int paramInt2, int paramInt3, int paramInt4, byte[] paramArrayOfByte, int paramInt5)
  {
    this(paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfByte, 0, arrayLength(paramArrayOfByte), paramInt5);
  }
  
  private static int arrayLength(byte[] paramArrayOfByte)
  {
    return paramArrayOfByte != null ? paramArrayOfByte.length : 0;
  }
  
  private void parse()
  {
    if (apdu.length < 4) {
      throw new IllegalArgumentException("apdu must be at least 4 bytes long");
    }
    if (apdu.length == 4) {
      return;
    }
    int i = apdu[4] & 0xFF;
    if (apdu.length == 5)
    {
      ne = (i == 0 ? 256 : i);
      return;
    }
    if (i != 0)
    {
      if (apdu.length == 5 + i)
      {
        nc = i;
        dataOffset = 5;
        return;
      }
      if (apdu.length == 6 + i)
      {
        nc = i;
        dataOffset = 5;
        j = apdu[(apdu.length - 1)] & 0xFF;
        ne = (j == 0 ? 256 : j);
        return;
      }
      throw new IllegalArgumentException("Invalid APDU: length=" + apdu.length + ", b1=" + i);
    }
    if (apdu.length < 7) {
      throw new IllegalArgumentException("Invalid APDU: length=" + apdu.length + ", b1=" + i);
    }
    int j = (apdu[5] & 0xFF) << 8 | apdu[6] & 0xFF;
    if (apdu.length == 7)
    {
      ne = (j == 0 ? 65536 : j);
      return;
    }
    if (j == 0) {
      throw new IllegalArgumentException("Invalid APDU: length=" + apdu.length + ", b1=" + i + ", b2||b3=" + j);
    }
    if (apdu.length == 7 + j)
    {
      nc = j;
      dataOffset = 7;
      return;
    }
    if (apdu.length == 9 + j)
    {
      nc = j;
      dataOffset = 7;
      int k = apdu.length - 2;
      int m = (apdu[k] & 0xFF) << 8 | apdu[(k + 1)] & 0xFF;
      ne = (m == 0 ? 65536 : m);
    }
    else
    {
      throw new IllegalArgumentException("Invalid APDU: length=" + apdu.length + ", b1=" + i + ", b2||b3=" + j);
    }
  }
  
  public CommandAPDU(int paramInt1, int paramInt2, int paramInt3, int paramInt4, byte[] paramArrayOfByte, int paramInt5, int paramInt6, int paramInt7)
  {
    checkArrayBounds(paramArrayOfByte, paramInt5, paramInt6);
    if (paramInt6 > 65535) {
      throw new IllegalArgumentException("dataLength is too large");
    }
    if (paramInt7 < 0) {
      throw new IllegalArgumentException("ne must not be negative");
    }
    if (paramInt7 > 65536) {
      throw new IllegalArgumentException("ne is too large");
    }
    ne = paramInt7;
    nc = paramInt6;
    int i;
    if (paramInt6 == 0)
    {
      if (paramInt7 == 0)
      {
        apdu = new byte[4];
        setHeader(paramInt1, paramInt2, paramInt3, paramInt4);
      }
      else if (paramInt7 <= 256)
      {
        i = paramInt7 != 256 ? (byte)paramInt7 : 0;
        apdu = new byte[5];
        setHeader(paramInt1, paramInt2, paramInt3, paramInt4);
        apdu[4] = i;
      }
      else
      {
        int j;
        if (paramInt7 == 65536)
        {
          i = 0;
          j = 0;
        }
        else
        {
          i = (byte)(paramInt7 >> 8);
          j = (byte)paramInt7;
        }
        apdu = new byte[7];
        setHeader(paramInt1, paramInt2, paramInt3, paramInt4);
        apdu[5] = i;
        apdu[6] = j;
      }
    }
    else if (paramInt7 == 0)
    {
      if (paramInt6 <= 255)
      {
        apdu = new byte[5 + paramInt6];
        setHeader(paramInt1, paramInt2, paramInt3, paramInt4);
        apdu[4] = ((byte)paramInt6);
        dataOffset = 5;
        System.arraycopy(paramArrayOfByte, paramInt5, apdu, 5, paramInt6);
      }
      else
      {
        apdu = new byte[7 + paramInt6];
        setHeader(paramInt1, paramInt2, paramInt3, paramInt4);
        apdu[4] = 0;
        apdu[5] = ((byte)(paramInt6 >> 8));
        apdu[6] = ((byte)paramInt6);
        dataOffset = 7;
        System.arraycopy(paramArrayOfByte, paramInt5, apdu, 7, paramInt6);
      }
    }
    else if ((paramInt6 <= 255) && (paramInt7 <= 256))
    {
      apdu = new byte[6 + paramInt6];
      setHeader(paramInt1, paramInt2, paramInt3, paramInt4);
      apdu[4] = ((byte)paramInt6);
      dataOffset = 5;
      System.arraycopy(paramArrayOfByte, paramInt5, apdu, 5, paramInt6);
      apdu[(apdu.length - 1)] = (paramInt7 != 256 ? (byte)paramInt7 : 0);
    }
    else
    {
      apdu = new byte[9 + paramInt6];
      setHeader(paramInt1, paramInt2, paramInt3, paramInt4);
      apdu[4] = 0;
      apdu[5] = ((byte)(paramInt6 >> 8));
      apdu[6] = ((byte)paramInt6);
      dataOffset = 7;
      System.arraycopy(paramArrayOfByte, paramInt5, apdu, 7, paramInt6);
      if (paramInt7 != 65536)
      {
        i = apdu.length - 2;
        apdu[i] = ((byte)(paramInt7 >> 8));
        apdu[(i + 1)] = ((byte)paramInt7);
      }
    }
  }
  
  private void setHeader(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    apdu[0] = ((byte)paramInt1);
    apdu[1] = ((byte)paramInt2);
    apdu[2] = ((byte)paramInt3);
    apdu[3] = ((byte)paramInt4);
  }
  
  public int getCLA()
  {
    return apdu[0] & 0xFF;
  }
  
  public int getINS()
  {
    return apdu[1] & 0xFF;
  }
  
  public int getP1()
  {
    return apdu[2] & 0xFF;
  }
  
  public int getP2()
  {
    return apdu[3] & 0xFF;
  }
  
  public int getNc()
  {
    return nc;
  }
  
  public byte[] getData()
  {
    byte[] arrayOfByte = new byte[nc];
    System.arraycopy(apdu, dataOffset, arrayOfByte, 0, nc);
    return arrayOfByte;
  }
  
  public int getNe()
  {
    return ne;
  }
  
  public byte[] getBytes()
  {
    return (byte[])apdu.clone();
  }
  
  public String toString()
  {
    return "CommmandAPDU: " + apdu.length + " bytes, nc=" + nc + ", ne=" + ne;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof CommandAPDU)) {
      return false;
    }
    CommandAPDU localCommandAPDU = (CommandAPDU)paramObject;
    return Arrays.equals(apdu, apdu);
  }
  
  public int hashCode()
  {
    return Arrays.hashCode(apdu);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    apdu = ((byte[])paramObjectInputStream.readUnshared());
    parse();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\smartcardio\CommandAPDU.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */