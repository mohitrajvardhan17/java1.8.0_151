package java.awt.image;

import java.awt.Image;

public abstract interface ImageObserver
{
  public static final int WIDTH = 1;
  public static final int HEIGHT = 2;
  public static final int PROPERTIES = 4;
  public static final int SOMEBITS = 8;
  public static final int FRAMEBITS = 16;
  public static final int ALLBITS = 32;
  public static final int ERROR = 64;
  public static final int ABORT = 128;
  
  public abstract boolean imageUpdate(Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\image\ImageObserver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */