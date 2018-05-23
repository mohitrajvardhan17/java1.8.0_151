package sun.awt;

import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import sun.util.logging.PlatformLogger;
import sun.util.logging.PlatformLogger.Level;

public abstract class SunGraphicsCallback
{
  public static final int HEAVYWEIGHTS = 1;
  public static final int LIGHTWEIGHTS = 2;
  public static final int TWO_PASSES = 4;
  private static final PlatformLogger log = PlatformLogger.getLogger("sun.awt.SunGraphicsCallback");
  
  public SunGraphicsCallback() {}
  
  public abstract void run(Component paramComponent, Graphics paramGraphics);
  
  protected void constrainGraphics(Graphics paramGraphics, Rectangle paramRectangle)
  {
    if ((paramGraphics instanceof ConstrainableGraphics)) {
      ((ConstrainableGraphics)paramGraphics).constrain(x, y, width, height);
    } else {
      paramGraphics.translate(x, y);
    }
    paramGraphics.clipRect(0, 0, width, height);
  }
  
  public final void runOneComponent(Component paramComponent, Rectangle paramRectangle, Graphics paramGraphics, Shape paramShape, int paramInt)
  {
    if ((paramComponent == null) || (paramComponent.getPeer() == null) || (!paramComponent.isVisible())) {
      return;
    }
    boolean bool = paramComponent.isLightweight();
    if (((bool) && ((paramInt & 0x2) == 0)) || ((!bool) && ((paramInt & 0x1) == 0))) {
      return;
    }
    if (paramRectangle == null) {
      paramRectangle = paramComponent.getBounds();
    }
    if ((paramShape == null) || (paramShape.intersects(paramRectangle)))
    {
      Graphics localGraphics = paramGraphics.create();
      try
      {
        constrainGraphics(localGraphics, paramRectangle);
        localGraphics.setFont(paramComponent.getFont());
        localGraphics.setColor(paramComponent.getForeground());
        if ((localGraphics instanceof Graphics2D)) {
          ((Graphics2D)localGraphics).setBackground(paramComponent.getBackground());
        } else if ((localGraphics instanceof Graphics2Delegate)) {
          ((Graphics2Delegate)localGraphics).setBackground(paramComponent.getBackground());
        }
        run(paramComponent, localGraphics);
      }
      finally
      {
        localGraphics.dispose();
      }
    }
  }
  
  public final void runComponents(Component[] paramArrayOfComponent, Graphics paramGraphics, int paramInt)
  {
    int i = paramArrayOfComponent.length;
    Shape localShape = paramGraphics.getClip();
    if ((log.isLoggable(PlatformLogger.Level.FINER)) && (localShape != null))
    {
      Rectangle localRectangle = localShape.getBounds();
      log.finer("x = " + x + ", y = " + y + ", width = " + width + ", height = " + height);
    }
    int j;
    if ((paramInt & 0x4) != 0)
    {
      for (j = i - 1; j >= 0; j--) {
        runOneComponent(paramArrayOfComponent[j], null, paramGraphics, localShape, 2);
      }
      for (j = i - 1; j >= 0; j--) {
        runOneComponent(paramArrayOfComponent[j], null, paramGraphics, localShape, 1);
      }
    }
    else
    {
      for (j = i - 1; j >= 0; j--) {
        runOneComponent(paramArrayOfComponent[j], null, paramGraphics, localShape, paramInt);
      }
    }
  }
  
  public static final class PaintHeavyweightComponentsCallback
    extends SunGraphicsCallback
  {
    private static PaintHeavyweightComponentsCallback instance = new PaintHeavyweightComponentsCallback();
    
    private PaintHeavyweightComponentsCallback() {}
    
    public void run(Component paramComponent, Graphics paramGraphics)
    {
      if (!paramComponent.isLightweight()) {
        paramComponent.paintAll(paramGraphics);
      } else if ((paramComponent instanceof Container)) {
        runComponents(((Container)paramComponent).getComponents(), paramGraphics, 3);
      }
    }
    
    public static PaintHeavyweightComponentsCallback getInstance()
    {
      return instance;
    }
  }
  
  public static final class PrintHeavyweightComponentsCallback
    extends SunGraphicsCallback
  {
    private static PrintHeavyweightComponentsCallback instance = new PrintHeavyweightComponentsCallback();
    
    private PrintHeavyweightComponentsCallback() {}
    
    public void run(Component paramComponent, Graphics paramGraphics)
    {
      if (!paramComponent.isLightweight()) {
        paramComponent.printAll(paramGraphics);
      } else if ((paramComponent instanceof Container)) {
        runComponents(((Container)paramComponent).getComponents(), paramGraphics, 3);
      }
    }
    
    public static PrintHeavyweightComponentsCallback getInstance()
    {
      return instance;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\SunGraphicsCallback.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */