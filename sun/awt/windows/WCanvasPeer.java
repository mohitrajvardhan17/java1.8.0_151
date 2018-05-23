package sun.awt.windows;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.peer.CanvasPeer;
import sun.awt.Graphics2Delegate;
import sun.awt.PaintEventDispatcher;
import sun.awt.SunToolkit;

class WCanvasPeer
  extends WComponentPeer
  implements CanvasPeer
{
  private boolean eraseBackground;
  
  WCanvasPeer(Component paramComponent)
  {
    super(paramComponent);
  }
  
  native void create(WComponentPeer paramWComponentPeer);
  
  void initialize()
  {
    eraseBackground = (!SunToolkit.getSunAwtNoerasebackground());
    boolean bool = SunToolkit.getSunAwtErasebackgroundonresize();
    if (!PaintEventDispatcher.getPaintEventDispatcher().shouldDoNativeBackgroundErase((Component)target)) {
      eraseBackground = false;
    }
    setNativeBackgroundErase(eraseBackground, bool);
    super.initialize();
    Color localColor = ((Component)target).getBackground();
    if (localColor != null) {
      setBackground(localColor);
    }
  }
  
  public void paint(Graphics paramGraphics)
  {
    Dimension localDimension = ((Component)target).getSize();
    if (((paramGraphics instanceof Graphics2D)) || ((paramGraphics instanceof Graphics2Delegate)))
    {
      paramGraphics.clearRect(0, 0, width, height);
    }
    else
    {
      paramGraphics.setColor(((Component)target).getBackground());
      paramGraphics.fillRect(0, 0, width, height);
      paramGraphics.setColor(((Component)target).getForeground());
    }
    super.paint(paramGraphics);
  }
  
  public boolean shouldClearRectBeforePaint()
  {
    return eraseBackground;
  }
  
  void disableBackgroundErase()
  {
    eraseBackground = false;
    setNativeBackgroundErase(false, false);
  }
  
  private native void setNativeBackgroundErase(boolean paramBoolean1, boolean paramBoolean2);
  
  public GraphicsConfiguration getAppropriateGraphicsConfiguration(GraphicsConfiguration paramGraphicsConfiguration)
  {
    return paramGraphicsConfiguration;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\windows\WCanvasPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */