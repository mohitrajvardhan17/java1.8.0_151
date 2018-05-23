package sun.awt.image;

import java.awt.image.BufferedImage;
import sun.java2d.SurfaceData;

public class BufImgSurfaceManager
  extends SurfaceManager
{
  protected BufferedImage bImg;
  protected SurfaceData sdDefault;
  
  public BufImgSurfaceManager(BufferedImage paramBufferedImage)
  {
    bImg = paramBufferedImage;
    sdDefault = BufImgSurfaceData.createData(paramBufferedImage);
  }
  
  public SurfaceData getPrimarySurfaceData()
  {
    return sdDefault;
  }
  
  public SurfaceData restoreContents()
  {
    return sdDefault;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\image\BufImgSurfaceManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */