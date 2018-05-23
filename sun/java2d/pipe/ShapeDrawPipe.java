package sun.java2d.pipe;

import java.awt.Shape;
import sun.java2d.SunGraphics2D;

public abstract interface ShapeDrawPipe
{
  public abstract void draw(SunGraphics2D paramSunGraphics2D, Shape paramShape);
  
  public abstract void fill(SunGraphics2D paramSunGraphics2D, Shape paramShape);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\pipe\ShapeDrawPipe.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */