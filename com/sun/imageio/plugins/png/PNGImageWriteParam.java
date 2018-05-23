package com.sun.imageio.plugins.png;

import java.util.Locale;
import javax.imageio.ImageWriteParam;

class PNGImageWriteParam
  extends ImageWriteParam
{
  public PNGImageWriteParam(Locale paramLocale)
  {
    canWriteProgressive = true;
    locale = paramLocale;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\imageio\plugins\png\PNGImageWriteParam.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */