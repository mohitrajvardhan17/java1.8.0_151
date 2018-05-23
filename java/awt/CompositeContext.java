package java.awt;

import java.awt.image.Raster;
import java.awt.image.WritableRaster;

public abstract interface CompositeContext
{
  public abstract void dispose();
  
  public abstract void compose(Raster paramRaster1, Raster paramRaster2, WritableRaster paramWritableRaster);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\CompositeContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */