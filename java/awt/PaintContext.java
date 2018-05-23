package java.awt;

import java.awt.image.ColorModel;
import java.awt.image.Raster;

public abstract interface PaintContext
{
  public abstract void dispose();
  
  public abstract ColorModel getColorModel();
  
  public abstract Raster getRaster(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\PaintContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */