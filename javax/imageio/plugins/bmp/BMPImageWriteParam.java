package javax.imageio.plugins.bmp;

import com.sun.imageio.plugins.bmp.BMPCompressionTypes;
import java.util.Locale;
import javax.imageio.ImageWriteParam;

public class BMPImageWriteParam
  extends ImageWriteParam
{
  private boolean topDown = false;
  
  public BMPImageWriteParam(Locale paramLocale)
  {
    super(paramLocale);
    compressionTypes = BMPCompressionTypes.getCompressionTypes();
    canWriteCompressed = true;
    compressionMode = 3;
    compressionType = compressionTypes[0];
  }
  
  public BMPImageWriteParam()
  {
    this(null);
  }
  
  public void setTopDown(boolean paramBoolean)
  {
    topDown = paramBoolean;
  }
  
  public boolean isTopDown()
  {
    return topDown;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\imageio\plugins\bmp\BMPImageWriteParam.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */