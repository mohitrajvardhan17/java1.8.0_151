package javax.imageio.stream;

import com.sun.imageio.stream.CloseableDisposerRecord;
import com.sun.imageio.stream.StreamFinalizer;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import sun.java2d.Disposer;

public class FileImageInputStream
  extends ImageInputStreamImpl
{
  private RandomAccessFile raf;
  private final Object disposerReferent;
  private final CloseableDisposerRecord disposerRecord;
  
  public FileImageInputStream(File paramFile)
    throws FileNotFoundException, IOException
  {
    this(paramFile == null ? null : new RandomAccessFile(paramFile, "r"));
  }
  
  public FileImageInputStream(RandomAccessFile paramRandomAccessFile)
  {
    if (paramRandomAccessFile == null) {
      throw new IllegalArgumentException("raf == null!");
    }
    raf = paramRandomAccessFile;
    disposerRecord = new CloseableDisposerRecord(paramRandomAccessFile);
    if (getClass() == FileImageInputStream.class)
    {
      disposerReferent = new Object();
      Disposer.addRecord(disposerReferent, disposerRecord);
    }
    else
    {
      disposerReferent = new StreamFinalizer(this);
    }
  }
  
  public int read()
    throws IOException
  {
    checkClosed();
    bitOffset = 0;
    int i = raf.read();
    if (i != -1) {
      streamPos += 1L;
    }
    return i;
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    checkClosed();
    bitOffset = 0;
    int i = raf.read(paramArrayOfByte, paramInt1, paramInt2);
    if (i != -1) {
      streamPos += i;
    }
    return i;
  }
  
  public long length()
  {
    try
    {
      checkClosed();
      return raf.length();
    }
    catch (IOException localIOException) {}
    return -1L;
  }
  
  public void seek(long paramLong)
    throws IOException
  {
    checkClosed();
    if (paramLong < flushedPos) {
      throw new IndexOutOfBoundsException("pos < flushedPos!");
    }
    bitOffset = 0;
    raf.seek(paramLong);
    streamPos = raf.getFilePointer();
  }
  
  public void close()
    throws IOException
  {
    super.close();
    disposerRecord.dispose();
    raf = null;
  }
  
  protected void finalize()
    throws Throwable
  {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\imageio\stream\FileImageInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */