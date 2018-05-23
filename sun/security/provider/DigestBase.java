package sun.security.provider;

import java.security.DigestException;
import java.security.MessageDigestSpi;
import java.security.ProviderException;

abstract class DigestBase
  extends MessageDigestSpi
  implements Cloneable
{
  private byte[] oneByte;
  private final String algorithm;
  private final int digestLength;
  private final int blockSize;
  byte[] buffer;
  private int bufOfs;
  long bytesProcessed;
  static final byte[] padding = new byte[''];
  
  DigestBase(String paramString, int paramInt1, int paramInt2)
  {
    algorithm = paramString;
    digestLength = paramInt1;
    blockSize = paramInt2;
    buffer = new byte[paramInt2];
  }
  
  protected final int engineGetDigestLength()
  {
    return digestLength;
  }
  
  protected final void engineUpdate(byte paramByte)
  {
    if (oneByte == null) {
      oneByte = new byte[1];
    }
    oneByte[0] = paramByte;
    engineUpdate(oneByte, 0, 1);
  }
  
  protected final void engineUpdate(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    if (paramInt2 == 0) {
      return;
    }
    if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 > paramArrayOfByte.length - paramInt2)) {
      throw new ArrayIndexOutOfBoundsException();
    }
    if (bytesProcessed < 0L) {
      engineReset();
    }
    bytesProcessed += paramInt2;
    int i;
    if (bufOfs != 0)
    {
      i = Math.min(paramInt2, blockSize - bufOfs);
      System.arraycopy(paramArrayOfByte, paramInt1, buffer, bufOfs, i);
      bufOfs += i;
      paramInt1 += i;
      paramInt2 -= i;
      if (bufOfs >= blockSize)
      {
        implCompress(buffer, 0);
        bufOfs = 0;
      }
    }
    if (paramInt2 >= blockSize)
    {
      i = paramInt1 + paramInt2;
      paramInt1 = implCompressMultiBlock(paramArrayOfByte, paramInt1, i - blockSize);
      paramInt2 = i - paramInt1;
    }
    if (paramInt2 > 0)
    {
      System.arraycopy(paramArrayOfByte, paramInt1, buffer, 0, paramInt2);
      bufOfs = paramInt2;
    }
  }
  
  private int implCompressMultiBlock(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    while (paramInt1 <= paramInt2)
    {
      implCompress(paramArrayOfByte, paramInt1);
      paramInt1 += blockSize;
    }
    return paramInt1;
  }
  
  protected final void engineReset()
  {
    if (bytesProcessed == 0L) {
      return;
    }
    implReset();
    bufOfs = 0;
    bytesProcessed = 0L;
  }
  
  protected final byte[] engineDigest()
  {
    byte[] arrayOfByte = new byte[digestLength];
    try
    {
      engineDigest(arrayOfByte, 0, arrayOfByte.length);
    }
    catch (DigestException localDigestException)
    {
      throw ((ProviderException)new ProviderException("Internal error").initCause(localDigestException));
    }
    return arrayOfByte;
  }
  
  protected final int engineDigest(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws DigestException
  {
    if (paramInt2 < digestLength) {
      throw new DigestException("Length must be at least " + digestLength + " for " + algorithm + "digests");
    }
    if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt1 > paramArrayOfByte.length - paramInt2)) {
      throw new DigestException("Buffer too short to store digest");
    }
    if (bytesProcessed < 0L) {
      engineReset();
    }
    implDigest(paramArrayOfByte, paramInt1);
    bytesProcessed = -1L;
    return digestLength;
  }
  
  abstract void implCompress(byte[] paramArrayOfByte, int paramInt);
  
  abstract void implDigest(byte[] paramArrayOfByte, int paramInt);
  
  abstract void implReset();
  
  public Object clone()
    throws CloneNotSupportedException
  {
    DigestBase localDigestBase = (DigestBase)super.clone();
    buffer = ((byte[])buffer.clone());
    return localDigestBase;
  }
  
  static
  {
    padding[0] = Byte.MIN_VALUE;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\provider\DigestBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */