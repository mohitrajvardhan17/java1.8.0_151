package sun.swing;

import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.lang.ref.SoftReference;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

public class ImageCache
{
  private int maxCount;
  private final LinkedList<SoftReference<Entry>> entries;
  
  public ImageCache(int paramInt)
  {
    maxCount = paramInt;
    entries = new LinkedList();
  }
  
  void setMaxCount(int paramInt)
  {
    maxCount = paramInt;
  }
  
  public void flush()
  {
    entries.clear();
  }
  
  private Entry getEntry(Object paramObject, GraphicsConfiguration paramGraphicsConfiguration, int paramInt1, int paramInt2, Object[] paramArrayOfObject)
  {
    ListIterator localListIterator = entries.listIterator();
    while (localListIterator.hasNext())
    {
      SoftReference localSoftReference = (SoftReference)localListIterator.next();
      localEntry = (Entry)localSoftReference.get();
      if (localEntry == null)
      {
        localListIterator.remove();
      }
      else if (localEntry.equals(paramGraphicsConfiguration, paramInt1, paramInt2, paramArrayOfObject))
      {
        localListIterator.remove();
        entries.addFirst(localSoftReference);
        return localEntry;
      }
    }
    Entry localEntry = new Entry(paramGraphicsConfiguration, paramInt1, paramInt2, paramArrayOfObject);
    if (entries.size() >= maxCount) {
      entries.removeLast();
    }
    entries.addFirst(new SoftReference(localEntry));
    return localEntry;
  }
  
  public Image getImage(Object paramObject, GraphicsConfiguration paramGraphicsConfiguration, int paramInt1, int paramInt2, Object[] paramArrayOfObject)
  {
    Entry localEntry = getEntry(paramObject, paramGraphicsConfiguration, paramInt1, paramInt2, paramArrayOfObject);
    return localEntry.getImage();
  }
  
  public void setImage(Object paramObject, GraphicsConfiguration paramGraphicsConfiguration, int paramInt1, int paramInt2, Object[] paramArrayOfObject, Image paramImage)
  {
    Entry localEntry = getEntry(paramObject, paramGraphicsConfiguration, paramInt1, paramInt2, paramArrayOfObject);
    localEntry.setImage(paramImage);
  }
  
  private static class Entry
  {
    private final GraphicsConfiguration config;
    private final int w;
    private final int h;
    private final Object[] args;
    private Image image;
    
    Entry(GraphicsConfiguration paramGraphicsConfiguration, int paramInt1, int paramInt2, Object[] paramArrayOfObject)
    {
      config = paramGraphicsConfiguration;
      args = paramArrayOfObject;
      w = paramInt1;
      h = paramInt2;
    }
    
    public void setImage(Image paramImage)
    {
      image = paramImage;
    }
    
    public Image getImage()
    {
      return image;
    }
    
    public String toString()
    {
      String str = super.toString() + "[ graphicsConfig=" + config + ", image=" + image + ", w=" + w + ", h=" + h;
      if (args != null) {
        for (int i = 0; i < args.length; i++) {
          str = str + ", " + args[i];
        }
      }
      str = str + "]";
      return str;
    }
    
    public boolean equals(GraphicsConfiguration paramGraphicsConfiguration, int paramInt1, int paramInt2, Object[] paramArrayOfObject)
    {
      if ((w == paramInt1) && (h == paramInt2) && (((config != null) && (config.equals(paramGraphicsConfiguration))) || ((config == null) && (paramGraphicsConfiguration == null))))
      {
        if ((args == null) && (paramArrayOfObject == null)) {
          return true;
        }
        if ((args != null) && (paramArrayOfObject != null) && (args.length == paramArrayOfObject.length))
        {
          for (int i = paramArrayOfObject.length - 1; i >= 0; i--)
          {
            Object localObject1 = args[i];
            Object localObject2 = paramArrayOfObject[i];
            if (((localObject1 == null) && (localObject2 != null)) || ((localObject1 != null) && (!localObject1.equals(localObject2)))) {
              return false;
            }
          }
          return true;
        }
      }
      return false;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\swing\ImageCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */