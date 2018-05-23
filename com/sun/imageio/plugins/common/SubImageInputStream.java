package com.sun.imageio.plugins.common;

import java.io.IOException;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageInputStreamImpl;

public final class SubImageInputStream
  extends ImageInputStreamImpl
{
  ImageInputStream stream;
  long startingPos;
  int startingLength;
  int length;
  
  public SubImageInputStream(ImageInputStream paramImageInputStream, int paramInt)
    throws IOException
  {
    stream = paramImageInputStream;
    startingPos = paramImageInputStream.getStreamPosition();
    startingLength = (length = paramInt);
  }
  
  public int read()
    throws IOException
  {
    if (length == 0) {
      return -1;
    }
    length -= 1;
    return stream.read();
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    if (length == 0) {
      return -1;
    }
    paramInt2 = Math.min(paramInt2, length);
    int i = stream.read(paramArrayOfByte, paramInt1, paramInt2);
    length -= i;
    return i;
  }
  
  public long length()
  {
    return startingLength;
  }
  
  public void seek(long paramLong)
    throws IOException
  {
    stream.seek(paramLong - startingPos);
    streamPos = paramLong;
  }
  
  protected void finalize()
    throws Throwable
  {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\imageio\plugins\common\SubImageInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */