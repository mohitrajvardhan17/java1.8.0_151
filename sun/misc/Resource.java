package sun.misc;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.security.CodeSigner;
import java.security.cert.Certificate;
import java.util.Arrays;
import java.util.jar.Manifest;
import sun.nio.ByteBuffered;

public abstract class Resource
{
  private InputStream cis;
  
  public Resource() {}
  
  public abstract String getName();
  
  public abstract URL getURL();
  
  public abstract URL getCodeSourceURL();
  
  public abstract InputStream getInputStream()
    throws IOException;
  
  public abstract int getContentLength()
    throws IOException;
  
  private synchronized InputStream cachedInputStream()
    throws IOException
  {
    if (cis == null) {
      cis = getInputStream();
    }
    return cis;
  }
  
  public byte[] getBytes()
    throws IOException
  {
    InputStream localInputStream = cachedInputStream();
    boolean bool = Thread.interrupted();
    int i;
    for (;;)
    {
      try
      {
        i = getContentLength();
      }
      catch (InterruptedIOException localInterruptedIOException1)
      {
        Thread.interrupted();
        bool = true;
      }
    }
    byte[] arrayOfByte;
    try
    {
      arrayOfByte = new byte[0];
      if (i == -1) {
        i = Integer.MAX_VALUE;
      }
      int j = 0;
      while (j < i)
      {
        int k;
        if (j >= arrayOfByte.length)
        {
          k = Math.min(i - j, arrayOfByte.length + 1024);
          if (arrayOfByte.length < j + k) {
            arrayOfByte = Arrays.copyOf(arrayOfByte, j + k);
          }
        }
        else
        {
          k = arrayOfByte.length - j;
        }
        int m = 0;
        try
        {
          m = localInputStream.read(arrayOfByte, j, k);
        }
        catch (InterruptedIOException localInterruptedIOException3)
        {
          Thread.interrupted();
          bool = true;
        }
        if (m < 0)
        {
          if (i != Integer.MAX_VALUE) {
            throw new EOFException("Detect premature EOF");
          }
          if (arrayOfByte.length == j) {
            break;
          }
          arrayOfByte = Arrays.copyOf(arrayOfByte, j);
          break;
        }
        j += m;
      }
    }
    finally
    {
      try
      {
        localInputStream.close();
      }
      catch (InterruptedIOException localInterruptedIOException4)
      {
        bool = true;
      }
      catch (IOException localIOException2) {}
      if (bool) {
        Thread.currentThread().interrupt();
      }
    }
    return arrayOfByte;
  }
  
  public ByteBuffer getByteBuffer()
    throws IOException
  {
    InputStream localInputStream = cachedInputStream();
    if ((localInputStream instanceof ByteBuffered)) {
      return ((ByteBuffered)localInputStream).getByteBuffer();
    }
    return null;
  }
  
  public Manifest getManifest()
    throws IOException
  {
    return null;
  }
  
  public Certificate[] getCertificates()
  {
    return null;
  }
  
  public CodeSigner[] getCodeSigners()
  {
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\misc\Resource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */