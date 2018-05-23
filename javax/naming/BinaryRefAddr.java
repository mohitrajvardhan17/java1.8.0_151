package javax.naming;

public class BinaryRefAddr
  extends RefAddr
{
  private byte[] buf = null;
  private static final long serialVersionUID = -3415254970957330361L;
  
  public BinaryRefAddr(String paramString, byte[] paramArrayOfByte)
  {
    this(paramString, paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  public BinaryRefAddr(String paramString, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    super(paramString);
    buf = new byte[paramInt2];
    System.arraycopy(paramArrayOfByte, paramInt1, buf, 0, paramInt2);
  }
  
  public Object getContent()
  {
    return buf;
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject != null) && ((paramObject instanceof BinaryRefAddr)))
    {
      BinaryRefAddr localBinaryRefAddr = (BinaryRefAddr)paramObject;
      if (addrType.compareTo(addrType) == 0)
      {
        if ((buf == null) && (buf == null)) {
          return true;
        }
        if ((buf == null) || (buf == null) || (buf.length != buf.length)) {
          return false;
        }
        for (int i = 0; i < buf.length; i++) {
          if (buf[i] != buf[i]) {
            return false;
          }
        }
        return true;
      }
    }
    return false;
  }
  
  public int hashCode()
  {
    int i = addrType.hashCode();
    for (int j = 0; j < buf.length; j++) {
      i += buf[j];
    }
    return i;
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer("Address Type: " + addrType + "\n");
    localStringBuffer.append("AddressContents: ");
    for (int i = 0; (i < buf.length) && (i < 32); i++) {
      localStringBuffer.append(Integer.toHexString(buf[i]) + " ");
    }
    if (buf.length >= 32) {
      localStringBuffer.append(" ...\n");
    }
    return localStringBuffer.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\naming\BinaryRefAddr.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */