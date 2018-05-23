package javax.swing;

import java.awt.Graphics;
import java.awt.Rectangle;

abstract interface GraphicsWrapper
{
  public abstract Graphics subGraphics();
  
  public abstract boolean isClipIntersecting(Rectangle paramRectangle);
  
  public abstract int getClipX();
  
  public abstract int getClipY();
  
  public abstract int getClipWidth();
  
  public abstract int getClipHeight();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\GraphicsWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */