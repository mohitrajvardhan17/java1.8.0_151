package sun.awt.windows;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.MenuBar;
import java.awt.Rectangle;
import java.awt.peer.FramePeer;
import java.security.AccessController;
import sun.awt.AWTAccessor;
import sun.awt.AWTAccessor.FrameAccessor;
import sun.awt.im.InputMethodManager;
import sun.security.action.GetPropertyAction;

class WFramePeer
  extends WWindowPeer
  implements FramePeer
{
  private static final boolean keepOnMinimize = "true".equals(AccessController.doPrivileged(new GetPropertyAction("sun.awt.keepWorkingSetOnMinimize")));
  
  private static native void initIDs();
  
  public native void setState(int paramInt);
  
  public native int getState();
  
  public void setExtendedState(int paramInt)
  {
    AWTAccessor.getFrameAccessor().setExtendedState((Frame)target, paramInt);
  }
  
  public int getExtendedState()
  {
    return AWTAccessor.getFrameAccessor().getExtendedState((Frame)target);
  }
  
  private native void setMaximizedBounds(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  private native void clearMaximizedBounds();
  
  public void setMaximizedBounds(Rectangle paramRectangle)
  {
    if (paramRectangle == null)
    {
      clearMaximizedBounds();
    }
    else
    {
      Rectangle localRectangle = (Rectangle)paramRectangle.clone();
      adjustMaximizedBounds(localRectangle);
      setMaximizedBounds(x, y, width, height);
    }
  }
  
  private void adjustMaximizedBounds(Rectangle paramRectangle)
  {
    GraphicsConfiguration localGraphicsConfiguration1 = getGraphicsConfiguration();
    GraphicsDevice localGraphicsDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
    GraphicsConfiguration localGraphicsConfiguration2 = localGraphicsDevice.getDefaultConfiguration();
    if ((localGraphicsConfiguration1 != null) && (localGraphicsConfiguration1 != localGraphicsConfiguration2))
    {
      Rectangle localRectangle1 = localGraphicsConfiguration1.getBounds();
      Rectangle localRectangle2 = localGraphicsConfiguration2.getBounds();
      int i = (width - width > 0) || (height - height > 0) ? 1 : 0;
      if (i != 0)
      {
        width -= width - width;
        height -= height - height;
      }
    }
  }
  
  public boolean updateGraphicsData(GraphicsConfiguration paramGraphicsConfiguration)
  {
    boolean bool = super.updateGraphicsData(paramGraphicsConfiguration);
    Rectangle localRectangle = AWTAccessor.getFrameAccessor().getMaximizedBounds((Frame)target);
    if (localRectangle != null) {
      setMaximizedBounds(localRectangle);
    }
    return bool;
  }
  
  boolean isTargetUndecorated()
  {
    return ((Frame)target).isUndecorated();
  }
  
  public void reshape(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (((Frame)target).isUndecorated()) {
      super.reshape(paramInt1, paramInt2, paramInt3, paramInt4);
    } else {
      reshapeFrame(paramInt1, paramInt2, paramInt3, paramInt4);
    }
  }
  
  public Dimension getMinimumSize()
  {
    Dimension localDimension = new Dimension();
    if (!((Frame)target).isUndecorated()) {
      localDimension.setSize(getSysMinWidth(), getSysMinHeight());
    }
    if (((Frame)target).getMenuBar() != null) {
      height += getSysMenuHeight();
    }
    return localDimension;
  }
  
  public void setMenuBar(MenuBar paramMenuBar)
  {
    WMenuBarPeer localWMenuBarPeer = (WMenuBarPeer)WToolkit.targetToPeer(paramMenuBar);
    if (localWMenuBarPeer != null)
    {
      if (framePeer != this)
      {
        paramMenuBar.removeNotify();
        paramMenuBar.addNotify();
        localWMenuBarPeer = (WMenuBarPeer)WToolkit.targetToPeer(paramMenuBar);
        if ((localWMenuBarPeer != null) && (framePeer != this)) {
          throw new IllegalStateException("Wrong parent peer");
        }
      }
      if (localWMenuBarPeer != null) {
        addChildPeer(localWMenuBarPeer);
      }
    }
    setMenuBar0(localWMenuBarPeer);
    updateInsets(insets_);
  }
  
  private native void setMenuBar0(WMenuBarPeer paramWMenuBarPeer);
  
  WFramePeer(Frame paramFrame)
  {
    super(paramFrame);
    InputMethodManager localInputMethodManager = InputMethodManager.getInstance();
    String str = localInputMethodManager.getTriggerMenuString();
    if (str != null) {
      pSetIMMOption(str);
    }
  }
  
  native void createAwtFrame(WComponentPeer paramWComponentPeer);
  
  void create(WComponentPeer paramWComponentPeer)
  {
    preCreate(paramWComponentPeer);
    createAwtFrame(paramWComponentPeer);
  }
  
  void initialize()
  {
    super.initialize();
    Frame localFrame = (Frame)target;
    if (localFrame.getTitle() != null) {
      setTitle(localFrame.getTitle());
    }
    setResizable(localFrame.isResizable());
    setState(localFrame.getExtendedState());
  }
  
  private static native int getSysMenuHeight();
  
  native void pSetIMMOption(String paramString);
  
  void notifyIMMOptionChange()
  {
    InputMethodManager.getInstance().notifyChangeRequest((Component)target);
  }
  
  public void setBoundsPrivate(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    setBounds(paramInt1, paramInt2, paramInt3, paramInt4, 3);
  }
  
  public Rectangle getBoundsPrivate()
  {
    return getBounds();
  }
  
  public void emulateActivation(boolean paramBoolean)
  {
    synthesizeWmActivate(paramBoolean);
  }
  
  private native void synthesizeWmActivate(boolean paramBoolean);
  
  static {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\windows\WFramePeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */