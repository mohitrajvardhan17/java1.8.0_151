package java.util.zip;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class InflaterOutputStream
  extends FilterOutputStream
{
  protected final Inflater inf;
  protected final byte[] buf;
  private final byte[] wbuf = new byte[1];
  private boolean usesDefaultInflater = false;
  private boolean closed = false;
  
  private void ensureOpen()
    throws IOException
  {
    if (closed) {
      throw new IOException("Stream closed");
    }
  }
  
  public InflaterOutputStream(OutputStream paramOutputStream)
  {
    this(paramOutputStream, new Inflater());
    usesDefaultInflater = true;
  }
  
  public InflaterOutputStream(OutputStream paramOutputStream, Inflater paramInflater)
  {
    this(paramOutputStream, paramInflater, 512);
  }
  
  public InflaterOutputStream(OutputStream paramOutputStream, Inflater paramInflater, int paramInt)
  {
    super(paramOutputStream);
    if (paramOutputStream == null) {
      throw new NullPointerException("Null output");
    }
    if (paramInflater == null) {
      throw new NullPointerException("Null inflater");
    }
    if (paramInt <= 0) {
      throw new IllegalArgumentException("Buffer size < 1");
    }
    inf = paramInflater;
    buf = new byte[paramInt];
  }
  
  /* Error */
  public void close()
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 98	java/util/zip/InflaterOutputStream:closed	Z
    //   4: ifne +37 -> 41
    //   7: aload_0
    //   8: invokevirtual 121	java/util/zip/InflaterOutputStream:finish	()V
    //   11: aload_0
    //   12: getfield 102	java/util/zip/InflaterOutputStream:out	Ljava/io/OutputStream;
    //   15: invokevirtual 107	java/io/OutputStream:close	()V
    //   18: aload_0
    //   19: iconst_1
    //   20: putfield 98	java/util/zip/InflaterOutputStream:closed	Z
    //   23: goto +18 -> 41
    //   26: astore_1
    //   27: aload_0
    //   28: getfield 102	java/util/zip/InflaterOutputStream:out	Ljava/io/OutputStream;
    //   31: invokevirtual 107	java/io/OutputStream:close	()V
    //   34: aload_0
    //   35: iconst_1
    //   36: putfield 98	java/util/zip/InflaterOutputStream:closed	Z
    //   39: aload_1
    //   40: athrow
    //   41: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	42	0	this	InflaterOutputStream
    //   26	14	1	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   7	11	26	finally
  }
  
  public void flush()
    throws IOException
  {
    ensureOpen();
    if (!inf.finished()) {
      try
      {
        while ((!inf.finished()) && (!inf.needsInput()))
        {
          int i = inf.inflate(buf, 0, buf.length);
          if (i < 1) {
            break;
          }
          out.write(buf, 0, i);
        }
        super.flush();
      }
      catch (DataFormatException localDataFormatException)
      {
        String str = localDataFormatException.getMessage();
        if (str == null) {
          str = "Invalid ZLIB data format";
        }
        throw new ZipException(str);
      }
    }
  }
  
  public void finish()
    throws IOException
  {
    ensureOpen();
    flush();
    if (usesDefaultInflater) {
      inf.end();
    }
  }
  
  public void write(int paramInt)
    throws IOException
  {
    wbuf[0] = ((byte)paramInt);
    write(wbuf, 0, 1);
  }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    ensureOpen();
    if (paramArrayOfByte == null) {
      throw new NullPointerException("Null buffer for read");
    }
    if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt2 > paramArrayOfByte.length - paramInt1)) {
      throw new IndexOutOfBoundsException();
    }
    if (paramInt2 == 0) {
      return;
    }
    try
    {
      for (;;)
      {
        if (inf.needsInput())
        {
          if (paramInt2 < 1) {
            break;
          }
          int j = paramInt2 < 512 ? paramInt2 : 512;
          inf.setInput(paramArrayOfByte, paramInt1, j);
          paramInt1 += j;
          paramInt2 -= j;
        }
        int i;
        do
        {
          i = inf.inflate(buf, 0, buf.length);
          if (i > 0) {
            out.write(buf, 0, i);
          }
        } while (i > 0);
        if (inf.finished()) {
          break;
        }
        if (inf.needsDictionary()) {
          throw new ZipException("ZLIB dictionary missing");
        }
      }
    }
    catch (DataFormatException localDataFormatException)
    {
      String str = localDataFormatException.getMessage();
      if (str == null) {
        str = "Invalid ZLIB data format";
      }
      throw new ZipException(str);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\zip\InflaterOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */