package javax.imageio;

import java.awt.Point;
import java.awt.Rectangle;

public abstract class IIOParam
{
  protected Rectangle sourceRegion = null;
  protected int sourceXSubsampling = 1;
  protected int sourceYSubsampling = 1;
  protected int subsamplingXOffset = 0;
  protected int subsamplingYOffset = 0;
  protected int[] sourceBands = null;
  protected ImageTypeSpecifier destinationType = null;
  protected Point destinationOffset = new Point(0, 0);
  protected IIOParamController defaultController = null;
  protected IIOParamController controller = null;
  
  protected IIOParam() {}
  
  public void setSourceRegion(Rectangle paramRectangle)
  {
    if (paramRectangle == null)
    {
      sourceRegion = null;
      return;
    }
    if (x < 0) {
      throw new IllegalArgumentException("sourceRegion.x < 0!");
    }
    if (y < 0) {
      throw new IllegalArgumentException("sourceRegion.y < 0!");
    }
    if (width <= 0) {
      throw new IllegalArgumentException("sourceRegion.width <= 0!");
    }
    if (height <= 0) {
      throw new IllegalArgumentException("sourceRegion.height <= 0!");
    }
    if (width <= subsamplingXOffset) {
      throw new IllegalStateException("sourceRegion.width <= subsamplingXOffset!");
    }
    if (height <= subsamplingYOffset) {
      throw new IllegalStateException("sourceRegion.height <= subsamplingYOffset!");
    }
    sourceRegion = ((Rectangle)paramRectangle.clone());
  }
  
  public Rectangle getSourceRegion()
  {
    if (sourceRegion == null) {
      return null;
    }
    return (Rectangle)sourceRegion.clone();
  }
  
  public void setSourceSubsampling(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (paramInt1 <= 0) {
      throw new IllegalArgumentException("sourceXSubsampling <= 0!");
    }
    if (paramInt2 <= 0) {
      throw new IllegalArgumentException("sourceYSubsampling <= 0!");
    }
    if ((paramInt3 < 0) || (paramInt3 >= paramInt1)) {
      throw new IllegalArgumentException("subsamplingXOffset out of range!");
    }
    if ((paramInt4 < 0) || (paramInt4 >= paramInt2)) {
      throw new IllegalArgumentException("subsamplingYOffset out of range!");
    }
    if ((sourceRegion != null) && ((paramInt3 >= sourceRegion.width) || (paramInt4 >= sourceRegion.height))) {
      throw new IllegalStateException("region contains no pixels!");
    }
    sourceXSubsampling = paramInt1;
    sourceYSubsampling = paramInt2;
    subsamplingXOffset = paramInt3;
    subsamplingYOffset = paramInt4;
  }
  
  public int getSourceXSubsampling()
  {
    return sourceXSubsampling;
  }
  
  public int getSourceYSubsampling()
  {
    return sourceYSubsampling;
  }
  
  public int getSubsamplingXOffset()
  {
    return subsamplingXOffset;
  }
  
  public int getSubsamplingYOffset()
  {
    return subsamplingYOffset;
  }
  
  public void setSourceBands(int[] paramArrayOfInt)
  {
    if (paramArrayOfInt == null)
    {
      sourceBands = null;
    }
    else
    {
      int i = paramArrayOfInt.length;
      for (int j = 0; j < i; j++)
      {
        int k = paramArrayOfInt[j];
        if (k < 0) {
          throw new IllegalArgumentException("Band value < 0!");
        }
        for (int m = j + 1; m < i; m++) {
          if (k == paramArrayOfInt[m]) {
            throw new IllegalArgumentException("Duplicate band value!");
          }
        }
      }
      sourceBands = ((int[])paramArrayOfInt.clone());
    }
  }
  
  public int[] getSourceBands()
  {
    if (sourceBands == null) {
      return null;
    }
    return (int[])sourceBands.clone();
  }
  
  public void setDestinationType(ImageTypeSpecifier paramImageTypeSpecifier)
  {
    destinationType = paramImageTypeSpecifier;
  }
  
  public ImageTypeSpecifier getDestinationType()
  {
    return destinationType;
  }
  
  public void setDestinationOffset(Point paramPoint)
  {
    if (paramPoint == null) {
      throw new IllegalArgumentException("destinationOffset == null!");
    }
    destinationOffset = ((Point)paramPoint.clone());
  }
  
  public Point getDestinationOffset()
  {
    return (Point)destinationOffset.clone();
  }
  
  public void setController(IIOParamController paramIIOParamController)
  {
    controller = paramIIOParamController;
  }
  
  public IIOParamController getController()
  {
    return controller;
  }
  
  public IIOParamController getDefaultController()
  {
    return defaultController;
  }
  
  public boolean hasController()
  {
    return controller != null;
  }
  
  public boolean activateController()
  {
    if (!hasController()) {
      throw new IllegalStateException("hasController() == false!");
    }
    return getController().activate(this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\imageio\IIOParam.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */