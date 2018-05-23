package java.util.zip;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class DeflaterOutputStream
  extends FilterOutputStream
{
  protected Deflater def;
  protected byte[] buf;
  private boolean closed = false;
  private final boolean syncFlush;
  boolean usesDefaultDeflater = false;
  
  public DeflaterOutputStream(OutputStream paramOutputStream, Deflater paramDeflater, int paramInt, boolean paramBoolean)
  {
    super(paramOutputStream);
    if ((paramOutputStream == null) || (paramDeflater == null)) {
      throw new NullPointerException();
    }
    if (paramInt <= 0) {
      throw new IllegalArgumentException("buffer size <= 0");
    }
    def = paramDeflater;
    buf = new byte[paramInt];
    syncFlush = paramBoolean;
  }
  
  public DeflaterOutputStream(OutputStream paramOutputStream, Deflater paramDeflater, int paramInt)
  {
    this(paramOutputStream, paramDeflater, paramInt, false);
  }
  
  public DeflaterOutputStream(OutputStream paramOutputStream, Deflater paramDeflater, boolean paramBoolean)
  {
    this(paramOutputStream, paramDeflater, 512, paramBoolean);
  }
  
  public DeflaterOutputStream(OutputStream paramOutputStream, Deflater paramDeflater)
  {
    this(paramOutputStream, paramDeflater, 512, false);
  }
  
  public DeflaterOutputStream(OutputStream paramOutputStream, boolean paramBoolean)
  {
    this(paramOutputStream, new Deflater(), 512, paramBoolean);
    usesDefaultDeflater = true;
  }
  
  public DeflaterOutputStream(OutputStream paramOutputStream)
  {
    this(paramOutputStream, false);
    usesDefaultDeflater = true;
  }
  
  public void write(int paramInt)
    throws IOException
  {
    byte[] arrayOfByte = new byte[1];
    arrayOfByte[0] = ((byte)(paramInt & 0xFF));
    write(arrayOfByte, 0, 1);
  }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    if (def.finished()) {
      throw new IOException("write beyond end of stream");
    }
    if ((paramInt1 | paramInt2 | paramInt1 + paramInt2 | paramArrayOfByte.length - (paramInt1 + paramInt2)) < 0) {
      throw new IndexOutOfBoundsException();
    }
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
  
  public void finish()
    throws IOException
  {
    if (!def.finished())
    {
      def.finish();
      while (!def.finished()) {
        deflate();
      }
    }
  }
  
  public void close()
    throws IOException
  {
    if (!closed)
    {
      finish();
      if (usesDefaultDeflater) {
        def.end();
      }
      out.close();
      closed = true;
    }
  }
  
  protected void deflate()
    throws IOException
  {
    int i = def.deflate(buf, 0, buf.length);
    if (i > 0) {
      out.write(buf, 0, i);
    }
  }
  
  public void flush()
    throws IOException
  {
    if ((syncFlush) && (!def.finished()))
    {
      int i = 0;
      while ((i = def.deflate(buf, 0, buf.length, 2)) > 0)
      {
        out.write(buf, 0, i);
        if (i < buf.length) {
          break;
        }
      }
    }
    out.flush();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\zip\DeflaterOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */