package javax.smartcardio;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Arrays;

public final class ATR
  implements Serializable
{
  private static final long serialVersionUID = 6695383790847736493L;
  private byte[] atr;
  private transient int startHistorical;
  private transient int nHistorical;
  
  public ATR(byte[] paramArrayOfByte)
  {
    atr = ((byte[])paramArrayOfByte.clone());
    parse();
  }
  
  private void parse()
  {
    if (atr.length < 2) {
      return;
    }
    if ((atr[0] != 59) && (atr[0] != 63)) {
      return;
    }
    int i = (atr[1] & 0xF0) >> 4;
    int j = atr[1] & 0xF;
    int k = 2;
    while ((i != 0) && (k < atr.length))
    {
      if ((i & 0x1) != 0) {
        k++;
      }
      if ((i & 0x2) != 0) {
        k++;
      }
      if ((i & 0x4) != 0) {
        k++;
      }
      if ((i & 0x8) != 0)
      {
        if (k >= atr.length) {
          return;
        }
        i = (atr[(k++)] & 0xF0) >> 4;
      }
      else
      {
        i = 0;
      }
    }
    int m = k + j;
    if ((m == atr.length) || (m == atr.length - 1))
    {
      startHistorical = k;
      nHistorical = j;
    }
  }
  
  public byte[] getBytes()
  {
    return (byte[])atr.clone();
  }
  
  public byte[] getHistoricalBytes()
  {
    byte[] arrayOfByte = new byte[nHistorical];
    System.arraycopy(atr, startHistorical, arrayOfByte, 0, nHistorical);
    return arrayOfByte;
  }
  
  public String toString()
  {
    return "ATR: " + atr.length + " bytes";
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof ATR)) {
      return false;
    }
    ATR localATR = (ATR)paramObject;
    return Arrays.equals(atr, atr);
  }
  
  public int hashCode()
  {
    return Arrays.hashCode(atr);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    atr = ((byte[])paramObjectInputStream.readUnshared());
    parse();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\smartcardio\ATR.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */