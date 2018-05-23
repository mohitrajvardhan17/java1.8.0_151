package java.awt.peer;

import java.awt.Rectangle;

public abstract interface RobotPeer
{
  public abstract void mouseMove(int paramInt1, int paramInt2);
  
  public abstract void mousePress(int paramInt);
  
  public abstract void mouseRelease(int paramInt);
  
  public abstract void mouseWheel(int paramInt);
  
  public abstract void keyPress(int paramInt);
  
  public abstract void keyRelease(int paramInt);
  
  public abstract int getRGBPixel(int paramInt1, int paramInt2);
  
  public abstract int[] getRGBPixels(Rectangle paramRectangle);
  
  public abstract void dispose();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\peer\RobotPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */