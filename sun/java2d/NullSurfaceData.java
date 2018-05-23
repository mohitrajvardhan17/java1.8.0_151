package sun.java2d;

import java.awt.GraphicsConfiguration;
import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import sun.java2d.loops.SurfaceType;
import sun.java2d.pipe.NullPipe;

public class NullSurfaceData
  extends SurfaceData
{
  public static final SurfaceData theInstance = new NullSurfaceData();
  private static final NullPipe nullpipe = new NullPipe();
  
  private NullSurfaceData()
  {
    super(StateTrackable.State.IMMUTABLE, SurfaceType.Any, ColorModel.getRGBdefault());
  }
  
  public void invalidate() {}
  
  public SurfaceData getReplacement()
  {
    return this;
  }
  
  public void validatePipe(SunGraphics2D paramSunGraphics2D)
  {
    drawpipe = nullpipe;
    fillpipe = nullpipe;
    shapepipe = nullpipe;
    textpipe = nullpipe;
    imagepipe = nullpipe;
  }
  
  public GraphicsConfiguration getDeviceConfiguration()
  {
    return null;
  }
  
  public Raster getRaster(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    throw new InvalidPipeException("should be NOP");
  }
  
  public boolean useTightBBoxes()
  {
    return false;
  }
  
  public int pixelFor(int paramInt)
  {
    return paramInt;
  }
  
  public int rgbFor(int paramInt)
  {
    return paramInt;
  }
  
  public Rectangle getBounds()
  {
    return new Rectangle();
  }
  
  protected void checkCustomComposite() {}
  
  public boolean copyArea(SunGraphics2D paramSunGraphics2D, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    return true;
  }
  
  public Object getDestination()
  {
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\NullSurfaceData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */