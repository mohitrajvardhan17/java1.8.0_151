package javax.swing;

import java.awt.Image;
import java.awt.image.ImageObserver;

class DebugGraphicsObserver
  implements ImageObserver
{
  int lastInfo;
  
  DebugGraphicsObserver() {}
  
  synchronized boolean allBitsPresent()
  {
    return (lastInfo & 0x20) != 0;
  }
  
  synchronized boolean imageHasProblem()
  {
    return ((lastInfo & 0x40) != 0) || ((lastInfo & 0x80) != 0);
  }
  
  public synchronized boolean imageUpdate(Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    lastInfo = paramInt1;
    return true;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\DebugGraphicsObserver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */