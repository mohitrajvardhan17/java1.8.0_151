package sun.java2d.loops;

import java.awt.image.WritableRaster;

class SolidPixelWriter
  extends PixelWriter
{
  protected Object srcData;
  
  SolidPixelWriter(Object paramObject)
  {
    srcData = paramObject;
  }
  
  public void writePixel(int paramInt1, int paramInt2)
  {
    dstRast.setDataElements(paramInt1, paramInt2, srcData);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\loops\SolidPixelWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */