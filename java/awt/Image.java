package java.awt;

import java.awt.image.AreaAveragingScaleFilter;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.image.ReplicateScaleFilter;
import sun.awt.image.SurfaceManager;
import sun.awt.image.SurfaceManager.ImageAccessor;

public abstract class Image
{
  private static ImageCapabilities defaultImageCaps = new ImageCapabilities(false);
  protected float accelerationPriority = 0.5F;
  public static final Object UndefinedProperty = new Object();
  public static final int SCALE_DEFAULT = 1;
  public static final int SCALE_FAST = 2;
  public static final int SCALE_SMOOTH = 4;
  public static final int SCALE_REPLICATE = 8;
  public static final int SCALE_AREA_AVERAGING = 16;
  SurfaceManager surfaceManager;
  
  public Image() {}
  
  public abstract int getWidth(ImageObserver paramImageObserver);
  
  public abstract int getHeight(ImageObserver paramImageObserver);
  
  public abstract ImageProducer getSource();
  
  public abstract Graphics getGraphics();
  
  public abstract Object getProperty(String paramString, ImageObserver paramImageObserver);
  
  public Image getScaledInstance(int paramInt1, int paramInt2, int paramInt3)
  {
    Object localObject;
    if ((paramInt3 & 0x14) != 0) {
      localObject = new AreaAveragingScaleFilter(paramInt1, paramInt2);
    } else {
      localObject = new ReplicateScaleFilter(paramInt1, paramInt2);
    }
    FilteredImageSource localFilteredImageSource = new FilteredImageSource(getSource(), (ImageFilter)localObject);
    return Toolkit.getDefaultToolkit().createImage(localFilteredImageSource);
  }
  
  public void flush()
  {
    if (surfaceManager != null) {
      surfaceManager.flush();
    }
  }
  
  public ImageCapabilities getCapabilities(GraphicsConfiguration paramGraphicsConfiguration)
  {
    if (surfaceManager != null) {
      return surfaceManager.getCapabilities(paramGraphicsConfiguration);
    }
    return defaultImageCaps;
  }
  
  public void setAccelerationPriority(float paramFloat)
  {
    if ((paramFloat < 0.0F) || (paramFloat > 1.0F)) {
      throw new IllegalArgumentException("Priority must be a value between 0 and 1, inclusive");
    }
    accelerationPriority = paramFloat;
    if (surfaceManager != null) {
      surfaceManager.setAccelerationPriority(accelerationPriority);
    }
  }
  
  public float getAccelerationPriority()
  {
    return accelerationPriority;
  }
  
  static
  {
    SurfaceManager.setImageAccessor(new SurfaceManager.ImageAccessor()
    {
      public SurfaceManager getSurfaceManager(Image paramAnonymousImage)
      {
        return surfaceManager;
      }
      
      public void setSurfaceManager(Image paramAnonymousImage, SurfaceManager paramAnonymousSurfaceManager)
      {
        surfaceManager = paramAnonymousSurfaceManager;
      }
    });
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\Image.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */