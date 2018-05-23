package com.sun.imageio.plugins.gif;

import java.util.Locale;
import javax.imageio.ImageWriteParam;

class GIFImageWriteParam
  extends ImageWriteParam
{
  GIFImageWriteParam(Locale paramLocale)
  {
    super(paramLocale);
    canWriteCompressed = true;
    canWriteProgressive = true;
    compressionTypes = new String[] { "LZW", "lzw" };
    compressionType = compressionTypes[0];
  }
  
  public void setCompressionMode(int paramInt)
  {
    if (paramInt == 0) {
      throw new UnsupportedOperationException("MODE_DISABLED is not supported.");
    }
    super.setCompressionMode(paramInt);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\imageio\plugins\gif\GIFImageWriteParam.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */