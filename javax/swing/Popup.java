package javax.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Window;
import java.awt.Window.Type;
import sun.awt.ModalExclude;

public class Popup
{
  private Component component;
  
  protected Popup(Component paramComponent1, Component paramComponent2, int paramInt1, int paramInt2)
  {
    this();
    if (paramComponent2 == null) {
      throw new IllegalArgumentException("Contents must be non-null");
    }
    reset(paramComponent1, paramComponent2, paramInt1, paramInt2);
  }
  
  protected Popup() {}
  
  public void show()
  {
    Component localComponent = getComponent();
    if (localComponent != null) {
      localComponent.show();
    }
  }
  
  public void hide()
  {
    Component localComponent = getComponent();
    if ((localComponent instanceof JWindow))
    {
      localComponent.hide();
      ((JWindow)localComponent).getContentPane().removeAll();
    }
    dispose();
  }
  
  void dispose()
  {
    Component localComponent = getComponent();
    Window localWindow = SwingUtilities.getWindowAncestor(localComponent);
    if ((localComponent instanceof JWindow))
    {
      ((Window)localComponent).dispose();
      localComponent = null;
    }
    if ((localWindow instanceof DefaultFrame)) {
      localWindow.dispose();
    }
  }
  
  void reset(Component paramComponent1, Component paramComponent2, int paramInt1, int paramInt2)
  {
    if (getComponent() == null) {
      component = createComponent(paramComponent1);
    }
    Component localComponent = getComponent();
    if ((localComponent instanceof JWindow))
    {
      JWindow localJWindow = (JWindow)getComponent();
      localJWindow.setLocation(paramInt1, paramInt2);
      localJWindow.getContentPane().add(paramComponent2, "Center");
      localJWindow.invalidate();
      localJWindow.validate();
      if (localJWindow.isVisible()) {
        pack();
      }
    }
  }
  
  void pack()
  {
    Component localComponent = getComponent();
    if ((localComponent instanceof Window)) {
      ((Window)localComponent).pack();
    }
  }
  
  private Window getParentWindow(Component paramComponent)
  {
    Object localObject = null;
    if ((paramComponent instanceof Window)) {
      localObject = (Window)paramComponent;
    } else if (paramComponent != null) {
      localObject = SwingUtilities.getWindowAncestor(paramComponent);
    }
    if (localObject == null) {
      localObject = new DefaultFrame();
    }
    return (Window)localObject;
  }
  
  Component createComponent(Component paramComponent)
  {
    if (GraphicsEnvironment.isHeadless()) {
      return null;
    }
    return new HeavyWeightWindow(getParentWindow(paramComponent));
  }
  
  Component getComponent()
  {
    return component;
  }
  
  static class DefaultFrame
    extends Frame
  {
    DefaultFrame() {}
  }
  
  static class HeavyWeightWindow
    extends JWindow
    implements ModalExclude
  {
    HeavyWeightWindow(Window paramWindow)
    {
      super();
      setFocusableWindowState(false);
      setType(Window.Type.POPUP);
      getRootPane().setUseTrueDoubleBuffering(false);
      try
      {
        setAlwaysOnTop(true);
      }
      catch (SecurityException localSecurityException) {}
    }
    
    public void update(Graphics paramGraphics)
    {
      paint(paramGraphics);
    }
    
    public void show()
    {
      pack();
      if ((getWidth() > 0) && (getHeight() > 0)) {
        super.show();
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\swing\Popup.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */