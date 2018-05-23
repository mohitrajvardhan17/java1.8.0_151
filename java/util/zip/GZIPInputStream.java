package java.util.zip;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;

public class GZIPInputStream
  extends InflaterInputStream
{
  protected CRC32 crc = new CRC32();
  protected boolean eos;
  private boolean closed = false;
  public static final int GZIP_MAGIC = 35615;
  private static final int FTEXT = 1;
  private static final int FHCRC = 2;
  private static final int FEXTRA = 4;
  private static final int FNAME = 8;
  private static final int FCOMMENT = 16;
  private byte[] tmpbuf = new byte[''];
  
  private void ensureOpen()
    throws IOException
  {
    if (closed) {
      throw new IOException("Stream closed");
    }
  }
  
  public GZIPInputStream(InputStream paramInputStream, int paramInt)
    throws IOException
  {
    super(paramInputStream, new Inflater(true), paramInt);
    usesDefaultInflater = true;
    readHeader(paramInputStream);
  }
  
  public GZIPInputStream(InputStream paramInputStream)
    throws IOException
  {
    this(paramInputStream, 512);
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    ensureOpen();
    if (eos) {
      return -1;
    }
    int i = super.read(paramArrayOfByte, paramInt1, paramInt2);
    if (i == -1)
    {
      if (readTrailer()) {
        eos = true;
      } else {
        return read(paramArrayOfByte, paramInt1, paramInt2);
      }
    }
    else {
      crc.update(paramArrayOfByte, paramInt1, i);
    }
    return i;
  }
  
  public void close()
    throws IOException
  {
    if (!closed)
    {
      super.close();
      eos = true;
      closed = true;
    }
  }
  
  private int readHeader(InputStream paramInputStream)
    throws IOException
  {
    CheckedInputStream localCheckedInputStream = new CheckedInputStream(paramInputStream, crc);
    crc.reset();
    if (readUShort(localCheckedInputStream) != 35615) {
      throw new ZipException("Not in GZIP format");
    }
    if (readUByte(localCheckedInputStream) != 8) {
      throw new ZipException("Unsupported compression method");
    }
    int i = readUByte(localCheckedInputStream);
    skipBytes(localCheckedInputStream, 6);
    int j = 10;
    int k;
    if ((i & 0x4) == 4)
    {
      k = readUShort(localCheckedInputStream);
      skipBytes(localCheckedInputStream, k);
      j += k + 2;
    }
    if ((i & 0x8) == 8) {
      do
      {
        j++;
      } while (readUByte(localCheckedInputStream) != 0);
    }
    if ((i & 0x10) == 16) {
      do
      {
        j++;
      } while (readUByte(localCheckedInputStream) != 0);
    }
    if ((i & 0x2) == 2)
    {
      k = (int)crc.getValue() & 0xFFFF;
      if (readUShort(localCheckedInputStream) != k) {
        throw new ZipException("Corrupt GZIP header");
      }
      j += 2;
    }
    crc.reset();
    return j;
  }
  
  private boolean readTrailer()
    throws IOException
  {
    Object localObject = in;
    int i = inf.getRemaining();
    if (i > 0) {
      localObject = new SequenceInputStream(new ByteArrayInputStream(buf, len - i, i), new FilterInputStream((InputStream)localObject)
      {
        public void close()
          throws IOException
        {}
      });
    }
    if ((readUInt((InputStream)localObject) != crc.getValue()) || (readUInt((InputStream)localObject) != (inf.getBytesWritten() & 0xFFFFFFFF))) {
      throw new ZipException("Corrupt GZIP trailer");
    }
    if ((in.available() > 0) || (i > 26))
    {
      int j = 8;
      try
      {
        j += readHeader((InputStream)localObject);
      }
      catch (IOException localIOException)
      {
        return true;
      }
      inf.reset();
      if (i > j) {
        inf.setInput(buf, len - i + j, i - j);
      }
      return false;
    }
    return true;
  }
  
  private long readUInt(InputStream paramInputStream)
    throws IOException
  {
    long l = readUShort(paramInputStream);
    return readUShort(paramInputStream) << 16 | l;
  }
  
  private int readUShort(InputStream paramInputStream)
    throws IOException
  {
    int i = readUByte(paramInputStream);
    return readUByte(paramInputStream) << 8 | i;
  }
  
  private int readUByte(InputStream paramInputStream)
    throws IOException
  {
    int i = paramInputStream.read();
    if (i == -1) {
      throw new EOFException();
    }
    if ((i < -1) || (i > 255)) {
      throw new IOException(in.getClass().getName() + ".read() returned value out of range -1..255: " + i);
    }
    return i;
  }
  
  private void skipBytes(InputStream paramInputStream, int paramInt)
    throws IOException
  {
    while (paramInt > 0)
    {
      int i = paramInputStream.read(tmpbuf, 0, paramInt < tmpbuf.length ? paramInt : tmpbuf.length);
      if (i == -1) {
        throw new EOFException();
      }
      paramInt -= i;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\zip\GZIPInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */