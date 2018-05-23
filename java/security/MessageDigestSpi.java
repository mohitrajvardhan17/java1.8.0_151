package java.security;

import java.nio.ByteBuffer;
import sun.security.jca.JCAUtil;

public abstract class MessageDigestSpi
{
  private byte[] tempArray;
  
  public MessageDigestSpi() {}
  
  protected int engineGetDigestLength()
  {
    return 0;
  }
  
  protected abstract void engineUpdate(byte paramByte);
  
  protected abstract void engineUpdate(byte[] paramArrayOfByte, int paramInt1, int paramInt2);
  
  protected void engineUpdate(ByteBuffer paramByteBuffer)
  {
    if (!paramByteBuffer.hasRemaining()) {
      return;
    }
    int j;
    int k;
    if (paramByteBuffer.hasArray())
    {
      byte[] arrayOfByte = paramByteBuffer.array();
      j = paramByteBuffer.arrayOffset();
      k = paramByteBuffer.position();
      int m = paramByteBuffer.limit();
      engineUpdate(arrayOfByte, j + k, m - k);
      paramByteBuffer.position(m);
    }
    else
    {
      int i = paramByteBuffer.remaining();
      j = JCAUtil.getTempArraySize(i);
      if ((tempArray == null) || (j > tempArray.length)) {
        tempArray = new byte[j];
      }
      while (i > 0)
      {
        k = Math.min(i, tempArray.length);
        paramByteBuffer.get(tempArray, 0, k);
        engineUpdate(tempArray, 0, k);
        i -= k;
      }
    }
  }
  
  protected abstract byte[] engineDigest();
  
  protected int engineDigest(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws DigestException
  {
    byte[] arrayOfByte = engineDigest();
    if (paramInt2 < arrayOfByte.length) {
      throw new DigestException("partial digests not returned");
    }
    if (paramArrayOfByte.length - paramInt1 < arrayOfByte.length) {
      throw new DigestException("insufficient space in the output buffer to store the digest");
    }
    System.arraycopy(arrayOfByte, 0, paramArrayOfByte, paramInt1, arrayOfByte.length);
    return arrayOfByte.length;
  }
  
  protected abstract void engineReset();
  
  public Object clone()
    throws CloneNotSupportedException
  {
    if ((this instanceof Cloneable)) {
      return super.clone();
    }
    throw new CloneNotSupportedException();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\MessageDigestSpi.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */