package sun.security.krb5.internal;

import sun.security.krb5.Confounder;

public class LocalSeqNumber
  implements SeqNumber
{
  private int lastSeqNumber;
  
  public LocalSeqNumber()
  {
    randInit();
  }
  
  public LocalSeqNumber(int paramInt)
  {
    init(paramInt);
  }
  
  public LocalSeqNumber(Integer paramInteger)
  {
    init(paramInteger.intValue());
  }
  
  public synchronized void randInit()
  {
    byte[] arrayOfByte = Confounder.bytes(4);
    arrayOfByte[0] = ((byte)(arrayOfByte[0] & 0x3F));
    int i = arrayOfByte[3] & 0xFF | (arrayOfByte[2] & 0xFF) << 8 | (arrayOfByte[1] & 0xFF) << 16 | (arrayOfByte[0] & 0xFF) << 24;
    if (i == 0) {
      i = 1;
    }
    lastSeqNumber = i;
  }
  
  public synchronized void init(int paramInt)
  {
    lastSeqNumber = paramInt;
  }
  
  public synchronized int current()
  {
    return lastSeqNumber;
  }
  
  public synchronized int next()
  {
    return lastSeqNumber + 1;
  }
  
  public synchronized int step()
  {
    return ++lastSeqNumber;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\krb5\internal\LocalSeqNumber.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */