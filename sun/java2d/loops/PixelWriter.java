package sun.java2d.loops;

import java.awt.image.WritableRaster;

abstract class PixelWriter
{
  protected WritableRaster dstRast;
  
  PixelWriter() {}
  
  public void setRaster(WritableRaster paramWritableRaster)
  {
    dstRast = paramWritableRaster;
  }
  
  public abstract void writePixel(int paramInt1, int paramInt2);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\loops\PixelWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */