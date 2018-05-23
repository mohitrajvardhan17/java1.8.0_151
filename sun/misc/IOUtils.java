package sun.misc;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class IOUtils
{
  public IOUtils() {}
  
  public static byte[] readFully(InputStream paramInputStream, int paramInt, boolean paramBoolean)
    throws IOException
  {
    byte[] arrayOfByte = new byte[0];
    if (paramInt == -1) {
      paramInt = Integer.MAX_VALUE;
    }
    int i = 0;
    while (i < paramInt)
    {
      int j;
      if (i >= arrayOfByte.length)
      {
        j = Math.min(paramInt - i, arrayOfByte.length + 1024);
        if (arrayOfByte.length < i + j) {
          arrayOfByte = Arrays.copyOf(arrayOfByte, i + j);
        }
      }
      else
      {
        j = arrayOfByte.length - i;
      }
      int k = paramInputStream.read(arrayOfByte, i, j);
      if (k < 0)
      {
        if ((paramBoolean) && (paramInt != Integer.MAX_VALUE)) {
          throw new EOFException("Detect premature EOF");
        }
        if (arrayOfByte.length == i) {
          break;
        }
        arrayOfByte = Arrays.copyOf(arrayOfByte, i);
        break;
      }
      i += k;
    }
    return arrayOfByte;
  }
  
  public static byte[] readNBytes(InputStream paramInputStream, int paramInt)
    throws IOException
  {
    if (paramInt < 0) {
      throw new IOException("length cannot be negative: " + paramInt);
    }
    return readFully(paramInputStream, paramInt, true);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\misc\IOUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */