package javax.smartcardio;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Arrays;

public final class ResponseAPDU
  implements Serializable
{
  private static final long serialVersionUID = 6962744978375594225L;
  private byte[] apdu;
  
  public ResponseAPDU(byte[] paramArrayOfByte)
  {
    paramArrayOfByte = (byte[])paramArrayOfByte.clone();
    check(paramArrayOfByte);
    apdu = paramArrayOfByte;
  }
  
  private static void check(byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte.length < 2) {
      throw new IllegalArgumentException("apdu must be at least 2 bytes long");
    }
  }
  
  public int getNr()
  {
    return apdu.length - 2;
  }
  
  public byte[] getData()
  {
    byte[] arrayOfByte = new byte[apdu.length - 2];
    System.arraycopy(apdu, 0, arrayOfByte, 0, arrayOfByte.length);
    return arrayOfByte;
  }
  
  public int getSW1()
  {
    return apdu[(apdu.length - 2)] & 0xFF;
  }
  
  public int getSW2()
  {
    return apdu[(apdu.length - 1)] & 0xFF;
  }
  
  public int getSW()
  {
    return getSW1() << 8 | getSW2();
  }
  
  public byte[] getBytes()
  {
    return (byte[])apdu.clone();
  }
  
  public String toString()
  {
    return "ResponseAPDU: " + apdu.length + " bytes, SW=" + Integer.toHexString(getSW());
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof ResponseAPDU)) {
      return false;
    }
    ResponseAPDU localResponseAPDU = (ResponseAPDU)paramObject;
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
    check(apdu);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\smartcardio\ResponseAPDU.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */