package com.sun.imageio.plugins.common;

import java.io.IOException;
import java.io.InputStream;
import javax.imageio.stream.ImageInputStream;

public class InputStreamAdapter
  extends InputStream
{
  ImageInputStream stream;
  
  public InputStreamAdapter(ImageInputStream paramImageInputStream)
  {
    stream = paramImageInputStream;
  }
  
  public int read()
    throws IOException
  {
    return stream.read();
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    return stream.read(paramArrayOfByte, paramInt1, paramInt2);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\imageio\plugins\common\InputStreamAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */