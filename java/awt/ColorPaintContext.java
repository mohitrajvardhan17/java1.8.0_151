package java.awt;

import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.Arrays;
import sun.awt.image.IntegerComponentRaster;

class ColorPaintContext
  implements PaintContext
{
  int color;
  WritableRaster savedTile;
  
  protected ColorPaintContext(int paramInt, ColorModel paramColorModel)
  {
    color = paramInt;
  }
  
  public void dispose() {}
  
  int getRGB()
  {
    return color;
  }
  
  public ColorModel getColorModel()
  {
    return ColorModel.getRGBdefault();
  }
  
  public synchronized Raster getRaster(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    WritableRaster localWritableRaster = savedTile;
    if ((localWritableRaster == null) || (paramInt3 > localWritableRaster.getWidth()) || (paramInt4 > localWritableRaster.getHeight()))
    {
      localWritableRaster = getColorModel().createCompatibleWritableRaster(paramInt3, paramInt4);
      IntegerComponentRaster localIntegerComponentRaster = (IntegerComponentRaster)localWritableRaster;
      Arrays.fill(localIntegerComponentRaster.getDataStorage(), color);
      localIntegerComponentRaster.markDirty();
      if ((paramInt3 <= 64) && (paramInt4 <= 64)) {
        savedTile = localWritableRaster;
      }
    }
    return localWritableRaster;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\ColorPaintContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */