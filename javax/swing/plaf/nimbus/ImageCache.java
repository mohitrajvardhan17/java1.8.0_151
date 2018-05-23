package javax.swing.plaf.nimbus;

import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class ImageCache
{
  private final LinkedHashMap<Integer, PixelCountSoftReference> map = new LinkedHashMap(16, 0.75F, true);
  private final int maxPixelCount;
  private final int maxSingleImagePixelSize;
  private int currentPixelCount = 0;
  private ReadWriteLock lock = new ReentrantReadWriteLock();
  private ReferenceQueue<Image> referenceQueue = new ReferenceQueue();
  private static final ImageCache instance = new ImageCache();
  
  static ImageCache getInstance()
  {
    return instance;
  }
  
  public ImageCache()
  {
    maxPixelCount = 2097152;
    maxSingleImagePixelSize = 90000;
  }
  
  public ImageCache(int paramInt1, int paramInt2)
  {
    maxPixelCount = paramInt1;
    maxSingleImagePixelSize = paramInt2;
  }
  
  /* Error */
  public void flush()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 153	javax/swing/plaf/nimbus/ImageCache:lock	Ljava/util/concurrent/locks/ReadWriteLock;
    //   4: invokeinterface 186 1 0
    //   9: invokeinterface 184 1 0
    //   14: aload_0
    //   15: getfield 152	javax/swing/plaf/nimbus/ImageCache:map	Ljava/util/LinkedHashMap;
    //   18: invokevirtual 164	java/util/LinkedHashMap:clear	()V
    //   21: aload_0
    //   22: getfield 153	javax/swing/plaf/nimbus/ImageCache:lock	Ljava/util/concurrent/locks/ReadWriteLock;
    //   25: invokeinterface 186 1 0
    //   30: invokeinterface 185 1 0
    //   35: goto +20 -> 55
    //   38: astore_1
    //   39: aload_0
    //   40: getfield 153	javax/swing/plaf/nimbus/ImageCache:lock	Ljava/util/concurrent/locks/ReadWriteLock;
    //   43: invokeinterface 186 1 0
    //   48: invokeinterface 185 1 0
    //   53: aload_1
    //   54: athrow
    //   55: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	56	0	this	ImageCache
    //   38	16	1	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   14	21	38	finally
  }
  
  public boolean isImageCachable(int paramInt1, int paramInt2)
  {
    return paramInt1 * paramInt2 < maxSingleImagePixelSize;
  }
  
  public Image getImage(GraphicsConfiguration paramGraphicsConfiguration, int paramInt1, int paramInt2, Object... paramVarArgs)
  {
    lock.readLock().lock();
    try
    {
      PixelCountSoftReference localPixelCountSoftReference = (PixelCountSoftReference)map.get(Integer.valueOf(hash(paramGraphicsConfiguration, paramInt1, paramInt2, paramVarArgs)));
      if ((localPixelCountSoftReference != null) && (localPixelCountSoftReference.equals(paramGraphicsConfiguration, paramInt1, paramInt2, paramVarArgs)))
      {
        localImage = (Image)localPixelCountSoftReference.get();
        return localImage;
      }
      Image localImage = null;
      return localImage;
    }
    finally
    {
      lock.readLock().unlock();
    }
  }
  
  public boolean setImage(Image paramImage, GraphicsConfiguration paramGraphicsConfiguration, int paramInt1, int paramInt2, Object... paramVarArgs)
  {
    if (!isImageCachable(paramInt1, paramInt2)) {
      return false;
    }
    int i = hash(paramGraphicsConfiguration, paramInt1, paramInt2, paramVarArgs);
    lock.writeLock().lock();
    try
    {
      PixelCountSoftReference localPixelCountSoftReference = (PixelCountSoftReference)map.get(Integer.valueOf(i));
      if ((localPixelCountSoftReference != null) && (localPixelCountSoftReference.get() == paramImage))
      {
        boolean bool1 = true;
        return bool1;
      }
      if (localPixelCountSoftReference != null)
      {
        currentPixelCount -= pixelCount;
        map.remove(Integer.valueOf(i));
      }
      int j = paramImage.getWidth(null) * paramImage.getHeight(null);
      currentPixelCount += j;
      if (currentPixelCount > maxPixelCount) {
        while ((localPixelCountSoftReference = (PixelCountSoftReference)referenceQueue.poll()) != null)
        {
          map.remove(Integer.valueOf(hash));
          currentPixelCount -= pixelCount;
        }
      }
      if (currentPixelCount > maxPixelCount)
      {
        Iterator localIterator = map.entrySet().iterator();
        while ((currentPixelCount > maxPixelCount) && (localIterator.hasNext()))
        {
          Map.Entry localEntry = (Map.Entry)localIterator.next();
          localIterator.remove();
          Image localImage = (Image)((PixelCountSoftReference)localEntry.getValue()).get();
          if (localImage != null) {
            localImage.flush();
          }
          currentPixelCount -= getValuepixelCount;
        }
      }
      map.put(Integer.valueOf(i), new PixelCountSoftReference(paramImage, referenceQueue, j, i, paramGraphicsConfiguration, paramInt1, paramInt2, paramVarArgs));
      boolean bool2 = true;
      return bool2;
    }
    finally
    {
      lock.writeLock().unlock();
    }
  }
  
  private int hash(GraphicsConfiguration paramGraphicsConfiguration, int paramInt1, int paramInt2, Object... paramVarArgs)
  {
    int i = paramGraphicsConfiguration != null ? paramGraphicsConfiguration.hashCode() : 0;
    i = 31 * i + paramInt1;
    i = 31 * i + paramInt2;
    i = 31 * i + Arrays.deepHashCode(paramVarArgs);
    return i;
  }
  
  private static class PixelCountSoftReference
    extends SoftReference<Image>
  {
    private final int pixelCount;
    private final int hash;
    private final GraphicsConfiguration config;
    private final int w;
    private final int h;
    private final Object[] args;
    
    public PixelCountSoftReference(Image paramImage, ReferenceQueue<? super Image> paramReferenceQueue, int paramInt1, int paramInt2, GraphicsConfiguration paramGraphicsConfiguration, int paramInt3, int paramInt4, Object[] paramArrayOfObject)
    {
      super(paramReferenceQueue);
      pixelCount = paramInt1;
      hash = paramInt2;
      config = paramGraphicsConfiguration;
      w = paramInt3;
      h = paramInt4;
      args = paramArrayOfObject;
    }
    
    public boolean equals(GraphicsConfiguration paramGraphicsConfiguration, int paramInt1, int paramInt2, Object[] paramArrayOfObject)
    {
      return (paramGraphicsConfiguration == config) && (paramInt1 == w) && (paramInt2 == h) && (Arrays.equals(paramArrayOfObject, args));
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\plaf\nimbus\ImageCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */