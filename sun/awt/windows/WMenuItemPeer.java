package sun.awt.windows;

import java.awt.AWTEvent;
import java.awt.Font;
import java.awt.MenuItem;
import java.awt.MenuShortcut;
import java.awt.event.ActionEvent;
import java.awt.peer.MenuItemPeer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import sun.util.logging.PlatformLogger;
import sun.util.logging.PlatformLogger.Level;

class WMenuItemPeer
  extends WObjectPeer
  implements MenuItemPeer
{
  private static final PlatformLogger log = PlatformLogger.getLogger("sun.awt.WMenuItemPeer");
  String shortcutLabel;
  protected WMenuPeer parent;
  private final boolean isCheckbox;
  private static Font defaultMenuFont = (Font)AccessController.doPrivileged(new PrivilegedAction()
  {
    public Font run()
    {
      try
      {
        ResourceBundle localResourceBundle = ResourceBundle.getBundle("sun.awt.windows.awtLocalization");
        return Font.decode(localResourceBundle.getString("menuFont"));
      }
      catch (MissingResourceException localMissingResourceException)
      {
        if (WMenuItemPeer.log.isLoggable(PlatformLogger.Level.FINE)) {
          WMenuItemPeer.log.fine("WMenuItemPeer: " + localMissingResourceException.getMessage() + ". Using default MenuItem font.", localMissingResourceException);
        }
      }
      return new Font("SanSerif", 0, 11);
    }
  });
  
  private synchronized native void _dispose();
  
  protected void disposeImpl()
  {
    WToolkit.targetDisposedPeer(target, this);
    _dispose();
  }
  
  public void setEnabled(boolean paramBoolean)
  {
    enable(paramBoolean);
  }
  
  public void enable()
  {
    enable(true);
  }
  
  public void disable()
  {
    enable(false);
  }
  
  private void readShortcutLabel()
  {
    for (WMenuPeer localWMenuPeer = parent; (localWMenuPeer != null) && (!(localWMenuPeer instanceof WMenuBarPeer)); localWMenuPeer = parent) {}
    if ((localWMenuPeer instanceof WMenuBarPeer))
    {
      MenuShortcut localMenuShortcut = ((MenuItem)target).getShortcut();
      shortcutLabel = (localMenuShortcut != null ? localMenuShortcut.toString() : null);
    }
    else
    {
      shortcutLabel = null;
    }
  }
  
  public void setLabel(String paramString)
  {
    readShortcutLabel();
    _setLabel(paramString);
  }
  
  public native void _setLabel(String paramString);
  
  protected WMenuItemPeer()
  {
    isCheckbox = false;
  }
  
  WMenuItemPeer(MenuItem paramMenuItem)
  {
    this(paramMenuItem, false);
  }
  
  WMenuItemPeer(MenuItem paramMenuItem, boolean paramBoolean)
  {
    target = paramMenuItem;
    parent = ((WMenuPeer)WToolkit.targetToPeer(paramMenuItem.getParent()));
    isCheckbox = paramBoolean;
    parent.addChildPeer(this);
    create(parent);
    checkMenuCreation();
    readShortcutLabel();
  }
  
  void checkMenuCreation()
  {
    if (pData == 0L)
    {
      if (createError != null) {
        throw createError;
      }
      throw new InternalError("couldn't create menu peer");
    }
  }
  
  void postEvent(AWTEvent paramAWTEvent)
  {
    WToolkit.postEvent(WToolkit.targetToAppContext(target), paramAWTEvent);
  }
  
  native void create(WMenuPeer paramWMenuPeer);
  
  native void enable(boolean paramBoolean);
  
  void handleAction(final long paramLong, int paramInt)
  {
    WToolkit.executeOnEventHandlerThread(target, new Runnable()
    {
      public void run()
      {
        postEvent(new ActionEvent(target, 1001, ((MenuItem)target).getActionCommand(), paramLong, val$modifiers));
      }
    });
  }
  
  static Font getDefaultFont()
  {
    return defaultMenuFont;
  }
  
  private static native void initIDs();
  
  private native void _setFont(Font paramFont);
  
  public void setFont(Font paramFont)
  {
    _setFont(paramFont);
  }
  
  static
  {
    initIDs();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\windows\WMenuItemPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */