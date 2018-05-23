package sun.awt.image;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.ImageCapabilities;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import sun.java2d.SurfaceData;
import sun.java2d.SurfaceDataProxy;

public abstract class SurfaceManager
{
  private static ImageAccessor imgaccessor;
  private ConcurrentHashMap<Object, Object> cacheMap;
  
  public SurfaceManager() {}
  
  public static void setImageAccessor(ImageAccessor paramImageAccessor)
  {
    if (imgaccessor != null) {
      throw new InternalError("Attempt to set ImageAccessor twice");
    }
    imgaccessor = paramImageAccessor;
  }
  
  public static SurfaceManager getManager(Image paramImage)
  {
    Object localObject = imgaccessor.getSurfaceManager(paramImage);
    if (localObject == null) {
      try
      {
        BufferedImage localBufferedImage = (BufferedImage)paramImage;
        localObject = new BufImgSurfaceManager(localBufferedImage);
        setManager(localBufferedImage, (SurfaceManager)localObject);
      }
      catch (ClassCastException localClassCastException)
      {
        throw new IllegalArgumentException("Invalid Image variant");
      }
    }
    return (SurfaceManager)localObject;
  }
  
  public static void setManager(Image paramImage, SurfaceManager paramSurfaceManager)
  {
    imgaccessor.setSurfaceManager(paramImage, paramSurfaceManager);
  }
  
  public Object getCacheData(Object paramObject)
  {
    return cacheMap == null ? null : cacheMap.get(paramObject);
  }
  
  public void setCacheData(Object paramObject1, Object paramObject2)
  {
    if (cacheMap == null) {
      synchronized (this)
      {
        if (cacheMap == null) {
          cacheMap = new ConcurrentHashMap(2);
        }
      }
    }
    cacheMap.put(paramObject1, paramObject2);
  }
  
  public abstract SurfaceData getPrimarySurfaceData();
  
  public abstract SurfaceData restoreContents();
  
  public void acceleratedSurfaceLost() {}
  
  public ImageCapabilities getCapabilities(GraphicsConfiguration paramGraphicsConfiguration)
  {
    return new ImageCapabilitiesGc(paramGraphicsConfiguration);
  }
  
  public synchronized void flush()
  {
    flush(false);
  }
  
  synchronized void flush(boolean paramBoolean)
  {
    if (cacheMap != null)
    {
      Iterator localIterator = cacheMap.values().iterator();
      while (localIterator.hasNext())
      {
        Object localObject = localIterator.next();
        if (((localObject instanceof FlushableCacheData)) && (((FlushableCacheData)localObject).flush(paramBoolean))) {
          localIterator.remove();
        }
      }
    }
  }
  
  public void setAccelerationPriority(float paramFloat)
  {
    if (paramFloat == 0.0F) {
      flush(true);
    }
  }
  
  public static int getImageScale(Image paramImage)
  {
    if (!(paramImage instanceof VolatileImage)) {
      return 1;
    }
    SurfaceManager localSurfaceManager = getManager(paramImage);
    return localSurfaceManager.getPrimarySurfaceData().getDefaultScale();
  }
  
  public static abstract interface FlushableCacheData
  {
    public abstract boolean flush(boolean paramBoolean);
  }
  
  public static abstract class ImageAccessor
  {
    public ImageAccessor() {}
    
    public abstract SurfaceManager getSurfaceManager(Image paramImage);
    
    public abstract void setSurfaceManager(Image paramImage, SurfaceManager paramSurfaceManager);
  }
  
  class ImageCapabilitiesGc
    extends ImageCapabilities
  {
    GraphicsConfiguration gc;
    
    public ImageCapabilitiesGc(GraphicsConfiguration paramGraphicsConfiguration)
    {
      super();
      gc = paramGraphicsConfiguration;
    }
    
    public boolean isAccelerated()
    {
      GraphicsConfiguration localGraphicsConfiguration = gc;
      if (localGraphicsConfiguration == null) {
        localGraphicsConfiguration = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
      }
      if ((localGraphicsConfiguration instanceof SurfaceManager.ProxiedGraphicsConfig))
      {
        Object localObject = ((SurfaceManager.ProxiedGraphicsConfig)localGraphicsConfiguration).getProxyKey();
        if (localObject != null)
        {
          SurfaceDataProxy localSurfaceDataProxy = (SurfaceDataProxy)getCacheData(localObject);
          return (localSurfaceDataProxy != null) && (localSurfaceDataProxy.isAccelerated());
        }
      }
      return false;
    }
  }
  
  public static abstract interface ProxiedGraphicsConfig
  {
    public abstract Object getProxyKey();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\image\SurfaceManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */