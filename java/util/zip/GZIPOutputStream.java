package java.util.zip;

import java.io.IOException;
import java.io.OutputStream;

public class GZIPOutputStream
  extends DeflaterOutputStream
{
  protected CRC32 crc = new CRC32();
  private static final int GZIP_MAGIC = 35615;
  private static final int TRAILER_SIZE = 8;
  
  public GZIPOutputStream(OutputStream paramOutputStream, int paramInt)
    throws IOException
  {
    this(paramOutputStream, paramInt, false);
  }
  
  public GZIPOutputStream(OutputStream paramOutputStream, int paramInt, boolean paramBoolean)
    throws IOException
  {
    super(paramOutputStream, new Deflater(-1, true), paramInt, paramBoolean);
    usesDefaultDeflater = true;
    writeHeader();
    crc.reset();
  }
  
  public GZIPOutputStream(OutputStream paramOutputStream)
    throws IOException
  {
    this(paramOutputStream, 512, false);
  }
  
  public GZIPOutputStream(OutputStream paramOutputStream, boolean paramBoolean)
    throws IOException
  {
    this(paramOutputStream, 512, paramBoolean);
  }
  
  public synchronized void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    super.write(paramArrayOfByte, paramInt1, paramInt2);
    crc.update(paramArrayOfByte, paramInt1, paramInt2);
  }
  
  public void finish()
    throws IOException
  {
    if (!def.finished())
    {
      def.finish();
      while (!def.finished())
      {
        int i = def.deflate(buf, 0, buf.length);
        if ((def.finished()) && (i <= buf.length - 8))
        {
          writeTrailer(buf, i);
          i += 8;
          out.write(buf, 0, i);
          return;
        }
        if (i > 0) {
          out.write(buf, 0, i);
        }
      }
      byte[] arrayOfByte = new byte[8];
      writeTrailer(arrayOfByte, 0);
      out.write(arrayOfByte);
    }
  }
  
  private void writeHeader()
    throws IOException
  {
    out.write(new byte[] { 31, -117, 8, 0, 0, 0, 0, 0, 0, 0 });
  }
  
  private void writeTrailer(byte[] paramArrayOfByte, int paramInt)
    throws IOException
  {
    writeInt((int)crc.getValue(), paramArrayOfByte, paramInt);
    writeInt(def.getTotalIn(), paramArrayOfByte, paramInt + 4);
  }
  
  private void writeInt(int paramInt1, byte[] paramArrayOfByte, int paramInt2)
    throws IOException
  {
    writeShort(paramInt1 & 0xFFFF, paramArrayOfByte, paramInt2);
    writeShort(paramInt1 >> 16 & 0xFFFF, paramArrayOfByte, paramInt2 + 2);
  }
  
  private void writeShort(int paramInt1, byte[] paramArrayOfByte, int paramInt2)
    throws IOException
  {
    paramArrayOfByte[paramInt2] = ((byte)(paramInt1 & 0xFF));
    paramArrayOfByte[(paramInt2 + 1)] = ((byte)(paramInt1 >> 8 & 0xFF));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\zip\GZIPOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */