package sun.awt.image;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.ImageCapabilities;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import sun.awt.DisplayChangedListener;
import sun.java2d.InvalidPipeException;
import sun.java2d.StateTrackableDelegate;
import sun.java2d.SunGraphicsEnvironment;
import sun.java2d.SurfaceData;

public abstract class VolatileSurfaceManager
  extends SurfaceManager
  implements DisplayChangedListener
{
  protected SunVolatileImage vImg;
  protected SurfaceData sdAccel;
  protected SurfaceData sdBackup;
  protected SurfaceData sdCurrent;
  protected SurfaceData sdPrevious;
  protected boolean lostSurface;
  protected Object context;
  
  protected VolatileSurfaceManager(SunVolatileImage paramSunVolatileImage, Object paramObject)
  {
    vImg = paramSunVolatileImage;
    context = paramObject;
    GraphicsEnvironment localGraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
    if ((localGraphicsEnvironment instanceof SunGraphicsEnvironment)) {
      ((SunGraphicsEnvironment)localGraphicsEnvironment).addDisplayChangedListener(this);
    }
  }
  
  public void initialize()
  {
    if (isAccelerationEnabled())
    {
      sdAccel = initAcceleratedSurface();
      if (sdAccel != null) {
        sdCurrent = sdAccel;
      }
    }
    if ((sdCurrent == null) && (vImg.getForcedAccelSurfaceType() == 0)) {
      sdCurrent = getBackupSurface();
    }
  }
  
  public SurfaceData getPrimarySurfaceData()
  {
    return sdCurrent;
  }
  
  protected abstract boolean isAccelerationEnabled();
  
  public int validate(GraphicsConfiguration paramGraphicsConfiguration)
  {
    int i = 0;
    boolean bool = lostSurface;
    lostSurface = false;
    if (isAccelerationEnabled())
    {
      if (!isConfigValid(paramGraphicsConfiguration))
      {
        i = 2;
      }
      else if (sdAccel == null)
      {
        sdAccel = initAcceleratedSurface();
        if (sdAccel != null)
        {
          sdCurrent = sdAccel;
          sdBackup = null;
          i = 1;
        }
        else
        {
          sdCurrent = getBackupSurface();
        }
      }
      else if (sdAccel.isSurfaceLost())
      {
        try
        {
          restoreAcceleratedSurface();
          sdCurrent = sdAccel;
          sdAccel.setSurfaceLost(false);
          sdBackup = null;
          i = 1;
        }
        catch (InvalidPipeException localInvalidPipeException)
        {
          sdCurrent = getBackupSurface();
        }
      }
      else if (bool)
      {
        i = 1;
      }
    }
    else if (sdAccel != null)
    {
      sdCurrent = getBackupSurface();
      sdAccel = null;
      i = 1;
    }
    if ((i != 2) && (sdCurrent != sdPrevious))
    {
      sdPrevious = sdCurrent;
      i = 1;
    }
    if (i == 1) {
      initContents();
    }
    return i;
  }
  
  public boolean contentsLost()
  {
    return lostSurface;
  }
  
  protected abstract SurfaceData initAcceleratedSurface();
  
  protected SurfaceData getBackupSurface()
  {
    if (sdBackup == null)
    {
      BufferedImage localBufferedImage = vImg.getBackupImage();
      SunWritableRaster.stealTrackable(localBufferedImage.getRaster().getDataBuffer()).setUntrackable();
      sdBackup = BufImgSurfaceData.createData(localBufferedImage);
    }
    return sdBackup;
  }
  
  public void initContents()
  {
    if (sdCurrent != null)
    {
      Graphics2D localGraphics2D = vImg.createGraphics();
      localGraphics2D.clearRect(0, 0, vImg.getWidth(), vImg.getHeight());
      localGraphics2D.dispose();
    }
  }
  
  public SurfaceData restoreContents()
  {
    return getBackupSurface();
  }
  
  public void acceleratedSurfaceLost()
  {
    if ((isAccelerationEnabled()) && (sdCurrent == sdAccel)) {
      lostSurface = true;
    }
  }
  
  protected void restoreAcceleratedSurface() {}
  
  public void displayChanged()
  {
    if (!isAccelerationEnabled()) {
      return;
    }
    lostSurface = true;
    if (sdAccel != null)
    {
      sdBackup = null;
      SurfaceData localSurfaceData = sdAccel;
      sdAccel = null;
      localSurfaceData.invalidate();
      sdCurrent = getBackupSurface();
    }
    vImg.updateGraphicsConfig();
  }
  
  public void paletteChanged()
  {
    lostSurface = true;
  }
  
  protected boolean isConfigValid(GraphicsConfiguration paramGraphicsConfiguration)
  {
    return (paramGraphicsConfiguration == null) || (paramGraphicsConfiguration.getDevice() == vImg.getGraphicsConfig().getDevice());
  }
  
  public ImageCapabilities getCapabilities(GraphicsConfiguration paramGraphicsConfiguration)
  {
    if (isConfigValid(paramGraphicsConfiguration)) {
      return isAccelerationEnabled() ? new AcceleratedImageCapabilities() : new ImageCapabilities(false);
    }
    return super.getCapabilities(paramGraphicsConfiguration);
  }
  
  public void flush()
  {
    lostSurface = true;
    SurfaceData localSurfaceData = sdAccel;
    sdAccel = null;
    if (localSurfaceData != null) {
      localSurfaceData.flush();
    }
  }
  
  private class AcceleratedImageCapabilities
    extends ImageCapabilities
  {
    AcceleratedImageCapabilities()
    {
      super();
    }
    
    public boolean isAccelerated()
    {
      return sdCurrent == sdAccel;
    }
    
    public boolean isTrueVolatile()
    {
      return isAccelerated();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\image\VolatileSurfaceManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */