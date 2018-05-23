package java.awt.image;

import java.awt.Point;

public abstract interface WritableRenderedImage
  extends RenderedImage
{
  public abstract void addTileObserver(TileObserver paramTileObserver);
  
  public abstract void removeTileObserver(TileObserver paramTileObserver);
  
  public abstract WritableRaster getWritableTile(int paramInt1, int paramInt2);
  
  public abstract void releaseWritableTile(int paramInt1, int paramInt2);
  
  public abstract boolean isTileWritable(int paramInt1, int paramInt2);
  
  public abstract Point[] getWritableTileIndices();
  
  public abstract boolean hasTileWriters();
  
  public abstract void setData(Raster paramRaster);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\image\WritableRenderedImage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */