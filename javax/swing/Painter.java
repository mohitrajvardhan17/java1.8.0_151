package javax.swing;

import java.awt.Graphics2D;

public abstract interface Painter<T>
{
  public abstract void paint(Graphics2D paramGraphics2D, T paramT, int paramInt1, int paramInt2);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\Painter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */