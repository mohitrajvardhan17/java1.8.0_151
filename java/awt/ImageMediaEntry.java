package java.awt;

import java.awt.image.ImageObserver;
import java.io.Serializable;

class ImageMediaEntry
  extends MediaEntry
  implements ImageObserver, Serializable
{
  Image image;
  int width;
  int height;
  private static final long serialVersionUID = 4739377000350280650L;
  
  ImageMediaEntry(MediaTracker paramMediaTracker, Image paramImage, int paramInt1, int paramInt2, int paramInt3)
  {
    super(paramMediaTracker, paramInt1);
    image = paramImage;
    width = paramInt2;
    height = paramInt3;
  }
  
  boolean matches(Image paramImage, int paramInt1, int paramInt2)
  {
    return (image == paramImage) && (width == paramInt1) && (height == paramInt2);
  }
  
  Object getMedia()
  {
    return image;
  }
  
  synchronized int getStatus(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (paramBoolean2)
    {
      int i = tracker.target.checkImage(image, width, height, null);
      int j = parseflags(i);
      if (j == 0)
      {
        if ((status & 0xC) != 0) {
          setStatus(2);
        }
      }
      else if (j != status) {
        setStatus(j);
      }
    }
    return super.getStatus(paramBoolean1, paramBoolean2);
  }
  
  void startLoad()
  {
    if (tracker.target.prepareImage(image, width, height, this)) {
      setStatus(8);
    }
  }
  
  int parseflags(int paramInt)
  {
    if ((paramInt & 0x40) != 0) {
      return 4;
    }
    if ((paramInt & 0x80) != 0) {
      return 2;
    }
    if ((paramInt & 0x30) != 0) {
      return 8;
    }
    return 0;
  }
  
  public boolean imageUpdate(Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    if (cancelled) {
      return false;
    }
    int i = parseflags(paramInt1);
    if ((i != 0) && (i != status)) {
      setStatus(i);
    }
    return (status & 0x1) != 0;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\ImageMediaEntry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */