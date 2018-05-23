package javax.swing;

import java.awt.Dimension;
import java.awt.Rectangle;

public abstract interface Scrollable
{
  public abstract Dimension getPreferredScrollableViewportSize();
  
  public abstract int getScrollableUnitIncrement(Rectangle paramRectangle, int paramInt1, int paramInt2);
  
  public abstract int getScrollableBlockIncrement(Rectangle paramRectangle, int paramInt1, int paramInt2);
  
  public abstract boolean getScrollableTracksViewportWidth();
  
  public abstract boolean getScrollableTracksViewportHeight();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\Scrollable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */