package sun.awt;

import java.applet.Applet;
import java.awt.AWTKeyStroke;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FocusTraversalPolicy;
import java.awt.Frame;
import java.awt.Image;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.MenuBar;
import java.awt.MenuComponent;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.peer.ComponentPeer;
import java.awt.peer.FramePeer;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Set;

public abstract class EmbeddedFrame
  extends Frame
  implements KeyEventDispatcher, PropertyChangeListener
{
  private boolean isCursorAllowed = true;
  private boolean supportsXEmbed = false;
  private KeyboardFocusManager appletKFM;
  private static final long serialVersionUID = 2967042741780317130L;
  protected static final boolean FORWARD = true;
  protected static final boolean BACKWARD = false;
  
  public boolean supportsXEmbed()
  {
    return (supportsXEmbed) && (SunToolkit.needsXEmbed());
  }
  
  protected EmbeddedFrame(boolean paramBoolean)
  {
    this(0L, paramBoolean);
  }
  
  protected EmbeddedFrame()
  {
    this(0L);
  }
  
  @Deprecated
  protected EmbeddedFrame(int paramInt)
  {
    this(paramInt);
  }
  
  protected EmbeddedFrame(long paramLong)
  {
    this(paramLong, false);
  }
  
  protected EmbeddedFrame(long paramLong, boolean paramBoolean)
  {
    supportsXEmbed = paramBoolean;
    registerListeners();
  }
  
  public Container getParent()
  {
    return null;
  }
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
  {
    if (!paramPropertyChangeEvent.getPropertyName().equals("managingFocus")) {
      return;
    }
    if (paramPropertyChangeEvent.getNewValue() == Boolean.TRUE) {
      return;
    }
    removeTraversingOutListeners((KeyboardFocusManager)paramPropertyChangeEvent.getSource());
    appletKFM = KeyboardFocusManager.getCurrentKeyboardFocusManager();
    if (isVisible()) {
      addTraversingOutListeners(appletKFM);
    }
  }
  
  private void addTraversingOutListeners(KeyboardFocusManager paramKeyboardFocusManager)
  {
    paramKeyboardFocusManager.addKeyEventDispatcher(this);
    paramKeyboardFocusManager.addPropertyChangeListener("managingFocus", this);
  }
  
  private void removeTraversingOutListeners(KeyboardFocusManager paramKeyboardFocusManager)
  {
    paramKeyboardFocusManager.removeKeyEventDispatcher(this);
    paramKeyboardFocusManager.removePropertyChangeListener("managingFocus", this);
  }
  
  public void registerListeners()
  {
    if (appletKFM != null) {
      removeTraversingOutListeners(appletKFM);
    }
    appletKFM = KeyboardFocusManager.getCurrentKeyboardFocusManager();
    if (isVisible()) {
      addTraversingOutListeners(appletKFM);
    }
  }
  
  public void show()
  {
    if (appletKFM != null) {
      addTraversingOutListeners(appletKFM);
    }
    super.show();
  }
  
  public void hide()
  {
    if (appletKFM != null) {
      removeTraversingOutListeners(appletKFM);
    }
    super.hide();
  }
  
  public boolean dispatchKeyEvent(KeyEvent paramKeyEvent)
  {
    Container localContainer = AWTAccessor.getKeyboardFocusManagerAccessor().getCurrentFocusCycleRoot();
    if (this != localContainer) {
      return false;
    }
    if (paramKeyEvent.getID() == 400) {
      return false;
    }
    if ((!getFocusTraversalKeysEnabled()) || (paramKeyEvent.isConsumed())) {
      return false;
    }
    AWTKeyStroke localAWTKeyStroke = AWTKeyStroke.getAWTKeyStrokeForEvent(paramKeyEvent);
    Component localComponent1 = paramKeyEvent.getComponent();
    Set localSet = getFocusTraversalKeys(0);
    Component localComponent2;
    if (localSet.contains(localAWTKeyStroke))
    {
      localComponent2 = getFocusTraversalPolicy().getLastComponent(this);
      if (((localComponent1 == localComponent2) || (localComponent2 == null)) && (traverseOut(true)))
      {
        paramKeyEvent.consume();
        return true;
      }
    }
    localSet = getFocusTraversalKeys(1);
    if (localSet.contains(localAWTKeyStroke))
    {
      localComponent2 = getFocusTraversalPolicy().getFirstComponent(this);
      if (((localComponent1 == localComponent2) || (localComponent2 == null)) && (traverseOut(false)))
      {
        paramKeyEvent.consume();
        return true;
      }
    }
    return false;
  }
  
  public boolean traverseIn(boolean paramBoolean)
  {
    Component localComponent = null;
    if (paramBoolean == true) {
      localComponent = getFocusTraversalPolicy().getFirstComponent(this);
    } else {
      localComponent = getFocusTraversalPolicy().getLastComponent(this);
    }
    if (localComponent != null)
    {
      AWTAccessor.getKeyboardFocusManagerAccessor().setMostRecentFocusOwner(this, localComponent);
      synthesizeWindowActivation(true);
    }
    return null != localComponent;
  }
  
  protected boolean traverseOut(boolean paramBoolean)
  {
    return false;
  }
  
  public void setTitle(String paramString) {}
  
  public void setIconImage(Image paramImage) {}
  
  public void setIconImages(List<? extends Image> paramList) {}
  
  public void setMenuBar(MenuBar paramMenuBar) {}
  
  public void setResizable(boolean paramBoolean) {}
  
  public void remove(MenuComponent paramMenuComponent) {}
  
  public boolean isResizable()
  {
    return true;
  }
  
  public void addNotify()
  {
    synchronized (getTreeLock())
    {
      if (getPeer() == null) {
        setPeer(new NullEmbeddedFramePeer(null));
      }
      super.addNotify();
    }
  }
  
  public void setCursorAllowed(boolean paramBoolean)
  {
    isCursorAllowed = paramBoolean;
    getPeer().updateCursorImmediately();
  }
  
  public boolean isCursorAllowed()
  {
    return isCursorAllowed;
  }
  
  public Cursor getCursor()
  {
    return isCursorAllowed ? super.getCursor() : Cursor.getPredefinedCursor(0);
  }
  
  protected void setPeer(ComponentPeer paramComponentPeer)
  {
    AWTAccessor.getComponentAccessor().setPeer(this, paramComponentPeer);
  }
  
  public void synthesizeWindowActivation(boolean paramBoolean) {}
  
  protected void setLocationPrivate(int paramInt1, int paramInt2)
  {
    Dimension localDimension = getSize();
    setBoundsPrivate(paramInt1, paramInt2, width, height);
  }
  
  protected Point getLocationPrivate()
  {
    Rectangle localRectangle = getBoundsPrivate();
    return new Point(x, y);
  }
  
  protected void setBoundsPrivate(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    FramePeer localFramePeer = (FramePeer)getPeer();
    if (localFramePeer != null) {
      localFramePeer.setBoundsPrivate(paramInt1, paramInt2, paramInt3, paramInt4);
    }
  }
  
  protected Rectangle getBoundsPrivate()
  {
    FramePeer localFramePeer = (FramePeer)getPeer();
    if (localFramePeer != null) {
      return localFramePeer.getBoundsPrivate();
    }
    return getBounds();
  }
  
  public void toFront() {}
  
  public void toBack() {}
  
  public abstract void registerAccelerator(AWTKeyStroke paramAWTKeyStroke);
  
  public abstract void unregisterAccelerator(AWTKeyStroke paramAWTKeyStroke);
  
  public static Applet getAppletIfAncestorOf(Component paramComponent)
  {
    Container localContainer = paramComponent.getParent();
    Applet localApplet = null;
    while ((localContainer != null) && (!(localContainer instanceof EmbeddedFrame)))
    {
      if ((localContainer instanceof Applet)) {
        localApplet = (Applet)localContainer;
      }
      localContainer = localContainer.getParent();
    }
    return localContainer == null ? null : localApplet;
  }
  
  public void notifyModalBlocked(Dialog paramDialog, boolean paramBoolean) {}
  
  private static class NullEmbeddedFramePeer
    extends NullComponentPeer
    implements FramePeer
  {
    private NullEmbeddedFramePeer() {}
    
    public void setTitle(String paramString) {}
    
    public void setIconImage(Image paramImage) {}
    
    public void updateIconImages() {}
    
    public void setMenuBar(MenuBar paramMenuBar) {}
    
    public void setResizable(boolean paramBoolean) {}
    
    public void setState(int paramInt) {}
    
    public int getState()
    {
      return 0;
    }
    
    public void setMaximizedBounds(Rectangle paramRectangle) {}
    
    public void toFront() {}
    
    public void toBack() {}
    
    public void updateFocusableWindowState() {}
    
    public void updateAlwaysOnTop() {}
    
    public void updateAlwaysOnTopState() {}
    
    public Component getGlobalHeavyweightFocusOwner()
    {
      return null;
    }
    
    public void setBoundsPrivate(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      setBounds(paramInt1, paramInt2, paramInt3, paramInt4, 3);
    }
    
    public Rectangle getBoundsPrivate()
    {
      return getBounds();
    }
    
    public void setModalBlocked(Dialog paramDialog, boolean paramBoolean) {}
    
    public void restack()
    {
      throw new UnsupportedOperationException();
    }
    
    public boolean isRestackSupported()
    {
      return false;
    }
    
    public boolean requestWindowFocus()
    {
      return false;
    }
    
    public void updateMinimumSize() {}
    
    public void setOpacity(float paramFloat) {}
    
    public void setOpaque(boolean paramBoolean) {}
    
    public void updateWindow() {}
    
    public void repositionSecurityWarning() {}
    
    public void emulateActivation(boolean paramBoolean) {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\EmbeddedFrame.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */