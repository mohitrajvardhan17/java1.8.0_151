package sun.awt.image;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

class PNGFilterInputStream
  extends FilterInputStream
{
  PNGImageDecoder owner;
  public InputStream underlyingInputStream = in;
  
  public PNGFilterInputStream(PNGImageDecoder paramPNGImageDecoder, InputStream paramInputStream)
  {
    super(paramInputStream);
    owner = paramPNGImageDecoder;
  }
  
  public int available()
    throws IOException
  {
    return owner.limit - owner.pos + in.available();
  }
  
  public boolean markSupported()
  {
    return false;
  }
  
  public int read()
    throws IOException
  {
    if ((owner.chunkLength <= 0) && (!owner.getData())) {
      return -1;
    }
    owner.chunkLength -= 1;
    return owner.inbuf[(owner.chunkStart++)] & 0xFF;
  }
  
  public int read(byte[] paramArrayOfByte)
    throws IOException
  {
    return read(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    if ((owner.chunkLength <= 0) && (!owner.getData())) {
      return -1;
    }
    if (owner.chunkLength < paramInt2) {
      paramInt2 = owner.chunkLength;
    }
    System.arraycopy(owner.inbuf, owner.chunkStart, paramArrayOfByte, paramInt1, paramInt2);
    owner.chunkLength -= paramInt2;
    owner.chunkStart += paramInt2;
    return paramInt2;
  }
  
  public long skip(long paramLong)
    throws IOException
  {
    for (int i = 0; (i < paramLong) && (read() >= 0); i++) {}
    return i;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\image\PNGFilterInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */