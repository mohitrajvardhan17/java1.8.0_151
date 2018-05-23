package com.sun.imageio.plugins.png;

import com.sun.imageio.plugins.common.InputStreamAdapter;
import com.sun.imageio.plugins.common.SubImageInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import javax.imageio.stream.ImageInputStream;

class PNGImageDataEnumeration
  implements Enumeration<InputStream>
{
  boolean firstTime = true;
  ImageInputStream stream;
  int length;
  
  public PNGImageDataEnumeration(ImageInputStream paramImageInputStream)
    throws IOException
  {
    stream = paramImageInputStream;
    length = paramImageInputStream.readInt();
    int i = paramImageInputStream.readInt();
  }
  
  public InputStream nextElement()
  {
    try
    {
      firstTime = false;
      SubImageInputStream localSubImageInputStream = new SubImageInputStream(stream, length);
      return new InputStreamAdapter(localSubImageInputStream);
    }
    catch (IOException localIOException) {}
    return null;
  }
  
  public boolean hasMoreElements()
  {
    if (firstTime) {
      return true;
    }
    try
    {
      int i = stream.readInt();
      length = stream.readInt();
      int j = stream.readInt();
      return j == 1229209940;
    }
    catch (IOException localIOException) {}
    return false;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\imageio\plugins\png\PNGImageDataEnumeration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */