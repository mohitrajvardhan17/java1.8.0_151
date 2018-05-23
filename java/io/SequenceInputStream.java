package java.io;

import java.util.Enumeration;
import java.util.Vector;

public class SequenceInputStream
  extends InputStream
{
  Enumeration<? extends InputStream> e;
  InputStream in;
  
  public SequenceInputStream(Enumeration<? extends InputStream> paramEnumeration)
  {
    e = paramEnumeration;
    try
    {
      nextStream();
    }
    catch (IOException localIOException)
    {
      throw new Error("panic");
    }
  }
  
  public SequenceInputStream(InputStream paramInputStream1, InputStream paramInputStream2)
  {
    Vector localVector = new Vector(2);
    localVector.addElement(paramInputStream1);
    localVector.addElement(paramInputStream2);
    e = localVector.elements();
    try
    {
      nextStream();
    }
    catch (IOException localIOException)
    {
      throw new Error("panic");
    }
  }
  
  final void nextStream()
    throws IOException
  {
    if (in != null) {
      in.close();
    }
    if (e.hasMoreElements())
    {
      in = ((InputStream)e.nextElement());
      if (in == null) {
        throw new NullPointerException();
      }
    }
    else
    {
      in = null;
    }
  }
  
  public int available()
    throws IOException
  {
    if (in == null) {
      return 0;
    }
    return in.available();
  }
  
  public int read()
    throws IOException
  {
    while (in != null)
    {
      int i = in.read();
      if (i != -1) {
        return i;
      }
      nextStream();
    }
    return -1;
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    if (in == null) {
      return -1;
    }
    if (paramArrayOfByte == null) {
      throw new NullPointerException();
    }
    if ((paramInt1 < 0) || (paramInt2 < 0) || (paramInt2 > paramArrayOfByte.length - paramInt1)) {
      throw new IndexOutOfBoundsException();
    }
    if (paramInt2 == 0) {
      return 0;
    }
    do
    {
      int i = in.read(paramArrayOfByte, paramInt1, paramInt2);
      if (i > 0) {
        return i;
      }
      nextStream();
    } while (in != null);
    return -1;
  }
  
  public void close()
    throws IOException
  {
    do
    {
      nextStream();
    } while (in != null);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\io\SequenceInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */