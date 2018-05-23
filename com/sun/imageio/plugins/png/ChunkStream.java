package com.sun.imageio.plugins.png;

import java.io.IOException;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.ImageOutputStreamImpl;

final class ChunkStream
  extends ImageOutputStreamImpl
{
  private ImageOutputStream stream;
  private long startPos;
  private CRC crc = new CRC();
  
  public ChunkStream(int paramInt, ImageOutputStream paramImageOutputStream)
    throws IOException
  {
    stream = paramImageOutputStream;
    startPos = paramImageOutputStream.getStreamPosition();
    paramImageOutputStream.writeInt(-1);
    writeInt(paramInt);
  }
  
  public int read()
    throws IOException
  {
    throw new RuntimeException("Method not available");
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    throw new RuntimeException("Method not available");
  }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    crc.update(paramArrayOfByte, paramInt1, paramInt2);
    stream.write(paramArrayOfByte, paramInt1, paramInt2);
  }
  
  public void write(int paramInt)
    throws IOException
  {
    crc.update(paramInt);
    stream.write(paramInt);
  }
  
  public void finish()
    throws IOException
  {
    stream.writeInt(crc.getValue());
    long l = stream.getStreamPosition();
    stream.seek(startPos);
    stream.writeInt((int)(l - startPos) - 12);
    stream.seek(l);
    stream.flushBefore(l);
  }
  
  protected void finalize()
    throws Throwable
  {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\imageio\plugins\png\ChunkStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */