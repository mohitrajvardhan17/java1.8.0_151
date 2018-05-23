package sun.awt.image;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;

public class ByteArrayImageSource
  extends InputStreamImageSource
{
  byte[] imagedata;
  int imageoffset;
  int imagelength;
  
  public ByteArrayImageSource(byte[] paramArrayOfByte)
  {
    this(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  public ByteArrayImageSource(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    imagedata = paramArrayOfByte;
    imageoffset = paramInt1;
    imagelength = paramInt2;
  }
  
  final boolean checkSecurity(Object paramObject, boolean paramBoolean)
  {
    return true;
  }
  
  protected ImageDecoder getDecoder()
  {
    BufferedInputStream localBufferedInputStream = new BufferedInputStream(new ByteArrayInputStream(imagedata, imageoffset, imagelength));
    return getDecoder(localBufferedInputStream);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\image\ByteArrayImageSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */