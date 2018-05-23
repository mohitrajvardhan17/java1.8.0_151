package com.sun.imageio.plugins.png;

import java.io.IOException;
import java.util.zip.Deflater;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.ImageOutputStreamImpl;

final class IDATOutputStream
  extends ImageOutputStreamImpl
{
  private static byte[] chunkType = { 73, 68, 65, 84 };
  private ImageOutputStream stream;
  private int chunkLength;
  private long startPos;
  private CRC crc = new CRC();
  Deflater def = new Deflater(9);
  byte[] buf = new byte['Ȁ'];
  private int bytesRemaining;
  
  public IDATOutputStream(ImageOutputStream paramImageOutputStream, int paramInt)
    throws IOException
  {
    stream = paramImageOutputStream;
    chunkLength = paramInt;
    startChunk();
  }
  
  private void startChunk()
    throws IOException
  {
    crc.reset();
    startPos = stream.getStreamPosition();
    stream.writeInt(-1);
    crc.update(chunkType, 0, 4);
    stream.write(chunkType, 0, 4);
    bytesRemaining = chunkLength;
  }
  
  private void finishChunk()
    throws IOException
  {
    stream.writeInt(crc.getValue());
    long l = stream.getStreamPosition();
    stream.seek(startPos);
    stream.writeInt((int)(l - startPos) - 12);
    stream.seek(l);
    stream.flushBefore(l);
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
    if (paramInt2 == 0) {
      return;
    }
    if (!def.finished())
    {
      def.setInput(paramArrayOfByte, paramInt1, paramInt2);
      while (!def.needsInput()) {
        deflate();
      }
    }
  }
  
  public void deflate()
    throws IOException
  {
    int i = def.deflate(buf, 0, buf.length);
    int j = 0;
    while (i > 0)
    {
      if (bytesRemaining == 0)
      {
        finishChunk();
        startChunk();
      }
      int k = Math.min(i, bytesRemaining);
      crc.update(buf, j, k);
      stream.write(buf, j, k);
      j += k;
      i -= k;
      bytesRemaining -= k;
    }
  }
  
  public void write(int paramInt)
    throws IOException
  {
    byte[] arrayOfByte = new byte[1];
    arrayOfByte[0] = ((byte)paramInt);
    write(arrayOfByte, 0, 1);
  }
  
  /* Error */
  public void finish()
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 105	com/sun/imageio/plugins/png/IDATOutputStream:def	Ljava/util/zip/Deflater;
    //   4: invokevirtual 119	java/util/zip/Deflater:finished	()Z
    //   7: ifne +27 -> 34
    //   10: aload_0
    //   11: getfield 105	com/sun/imageio/plugins/png/IDATOutputStream:def	Ljava/util/zip/Deflater;
    //   14: invokevirtual 118	java/util/zip/Deflater:finish	()V
    //   17: aload_0
    //   18: getfield 105	com/sun/imageio/plugins/png/IDATOutputStream:def	Ljava/util/zip/Deflater;
    //   21: invokevirtual 119	java/util/zip/Deflater:finished	()Z
    //   24: ifne +10 -> 34
    //   27: aload_0
    //   28: invokevirtual 111	com/sun/imageio/plugins/png/IDATOutputStream:deflate	()V
    //   31: goto -14 -> 17
    //   34: aload_0
    //   35: invokespecial 112	com/sun/imageio/plugins/png/IDATOutputStream:finishChunk	()V
    //   38: aload_0
    //   39: getfield 105	com/sun/imageio/plugins/png/IDATOutputStream:def	Ljava/util/zip/Deflater;
    //   42: invokevirtual 117	java/util/zip/Deflater:end	()V
    //   45: goto +13 -> 58
    //   48: astore_1
    //   49: aload_0
    //   50: getfield 105	com/sun/imageio/plugins/png/IDATOutputStream:def	Ljava/util/zip/Deflater;
    //   53: invokevirtual 117	java/util/zip/Deflater:end	()V
    //   56: aload_1
    //   57: athrow
    //   58: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	59	0	this	IDATOutputStream
    //   48	9	1	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   0	38	48	finally
  }
  
  protected void finalize()
    throws Throwable
  {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\imageio\plugins\png\IDATOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */