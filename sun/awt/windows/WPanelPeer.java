package sun.awt.windows;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.peer.PanelPeer;
import sun.awt.SunGraphicsCallback.PaintHeavyweightComponentsCallback;
import sun.awt.SunGraphicsCallback.PrintHeavyweightComponentsCallback;

class WPanelPeer
  extends WCanvasPeer
  implements PanelPeer
{
  Insets insets_;
  
  public void paint(Graphics paramGraphics)
  {
    super.paint(paramGraphics);
    SunGraphicsCallback.PaintHeavyweightComponentsCallback.getInstance().runComponents(((Container)target).getComponents(), paramGraphics, 3);
  }
  
  public void print(Graphics paramGraphics)
  {
    super.print(paramGraphics);
    SunGraphicsCallback.PrintHeavyweightComponentsCallback.getInstance().runComponents(((Container)target).getComponents(), paramGraphics, 3);
  }
  
  public Insets getInsets()
  {
    return insets_;
  }
  
  private static native void initIDs();
  
  WPanelPeer(Component paramComponent)
  {
    super(paramComponent);
  }
  
  void initialize()
  {
    super.initialize();
    insets_ = new Insets(0, 0, 0, 0);
    Color localColor = ((Component)target).getBackground();
    if (localColor == null)
    {
      localColor = WColor.getDefaultColor(1);
      ((Component)target).setBackground(localColor);
      setBackground(localColor);
    }
    localColor = ((Component)target).getForeground();
    if (localColor == null)
    {
      localColor = WColor.getDefaultColor(2);
      ((Component)target).setForeground(localColor);
      setForeground(localColor);
    }
  }
  
  public Insets insets()
  {
    return getInsets();
  }
  
  static {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\windows\WPanelPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */