package sun.awt;

import java.awt.Canvas;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.ImageProducer;
import java.awt.image.PixelGrabber;

public abstract class CustomCursor
  extends Cursor
{
  protected Image image;
  
  public CustomCursor(Image paramImage, Point paramPoint, String paramString)
    throws IndexOutOfBoundsException
  {
    super(paramString);
    image = paramImage;
    Toolkit localToolkit = Toolkit.getDefaultToolkit();
    Canvas localCanvas = new Canvas();
    MediaTracker localMediaTracker = new MediaTracker(localCanvas);
    localMediaTracker.addImage(paramImage, 0);
    try
    {
      localMediaTracker.waitForAll();
    }
    catch (InterruptedException localInterruptedException1) {}
    int i = paramImage.getWidth(localCanvas);
    int j = paramImage.getHeight(localCanvas);
    if ((localMediaTracker.isErrorAny()) || (i < 0) || (j < 0)) {
      x = (y = 0);
    }
    Dimension localDimension = localToolkit.getBestCursorSize(i, j);
    if ((width != i) || (height != j))
    {
      paramImage = paramImage.getScaledInstance(width, height, 1);
      i = width;
      j = height;
    }
    if ((x >= i) || (y >= j) || (x < 0) || (y < 0)) {
      throw new IndexOutOfBoundsException("invalid hotSpot");
    }
    int[] arrayOfInt = new int[i * j];
    ImageProducer localImageProducer = paramImage.getSource();
    PixelGrabber localPixelGrabber = new PixelGrabber(localImageProducer, 0, 0, i, j, arrayOfInt, 0, i);
    try
    {
      localPixelGrabber.grabPixels();
    }
    catch (InterruptedException localInterruptedException2) {}
    createNativeCursor(image, arrayOfInt, i, j, x, y);
  }
  
  protected abstract void createNativeCursor(Image paramImage, int[] paramArrayOfInt, int paramInt1, int paramInt2, int paramInt3, int paramInt4);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\CustomCursor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */