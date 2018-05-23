package sun.awt;

import java.awt.AWTEvent;
import java.awt.AWTException;
import java.awt.CheckboxMenuItem;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.DefaultKeyboardFocusManager;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.KeyboardFocusManager;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuComponent;
import java.awt.MenuContainer;
import java.awt.MenuItem;
import java.awt.MenuShortcut;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.Rectangle;
import java.awt.ScrollPaneAdjustable;
import java.awt.Shape;
import java.awt.SystemColor;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.Window;
import java.awt.event.InputEvent;
import java.awt.event.InvocationEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.awt.peer.ComponentPeer;
import java.awt.peer.MenuComponentPeer;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.security.AccessControlContext;
import java.util.ResourceBundle;
import java.util.Vector;
import javax.accessibility.AccessibleContext;
import sun.misc.Unsafe;

public final class AWTAccessor
{
  private static final Unsafe unsafe = ;
  private static ComponentAccessor componentAccessor;
  private static ContainerAccessor containerAccessor;
  private static WindowAccessor windowAccessor;
  private static AWTEventAccessor awtEventAccessor;
  private static InputEventAccessor inputEventAccessor;
  private static FrameAccessor frameAccessor;
  private static KeyboardFocusManagerAccessor kfmAccessor;
  private static MenuComponentAccessor menuComponentAccessor;
  private static EventQueueAccessor eventQueueAccessor;
  private static PopupMenuAccessor popupMenuAccessor;
  private static FileDialogAccessor fileDialogAccessor;
  private static ScrollPaneAdjustableAccessor scrollPaneAdjustableAccessor;
  private static CheckboxMenuItemAccessor checkboxMenuItemAccessor;
  private static CursorAccessor cursorAccessor;
  private static MenuBarAccessor menuBarAccessor;
  private static MenuItemAccessor menuItemAccessor;
  private static MenuAccessor menuAccessor;
  private static KeyEventAccessor keyEventAccessor;
  private static ClientPropertyKeyAccessor clientPropertyKeyAccessor;
  private static SystemTrayAccessor systemTrayAccessor;
  private static TrayIconAccessor trayIconAccessor;
  private static DefaultKeyboardFocusManagerAccessor defaultKeyboardFocusManagerAccessor;
  private static SequencedEventAccessor sequencedEventAccessor;
  private static ToolkitAccessor toolkitAccessor;
  private static InvocationEventAccessor invocationEventAccessor;
  private static SystemColorAccessor systemColorAccessor;
  private static AccessibleContextAccessor accessibleContextAccessor;
  
  private AWTAccessor() {}
  
  public static void setComponentAccessor(ComponentAccessor paramComponentAccessor)
  {
    componentAccessor = paramComponentAccessor;
  }
  
  public static ComponentAccessor getComponentAccessor()
  {
    if (componentAccessor == null) {
      unsafe.ensureClassInitialized(Component.class);
    }
    return componentAccessor;
  }
  
  public static void setContainerAccessor(ContainerAccessor paramContainerAccessor)
  {
    containerAccessor = paramContainerAccessor;
  }
  
  public static ContainerAccessor getContainerAccessor()
  {
    if (containerAccessor == null) {
      unsafe.ensureClassInitialized(Container.class);
    }
    return containerAccessor;
  }
  
  public static void setWindowAccessor(WindowAccessor paramWindowAccessor)
  {
    windowAccessor = paramWindowAccessor;
  }
  
  public static WindowAccessor getWindowAccessor()
  {
    if (windowAccessor == null) {
      unsafe.ensureClassInitialized(Window.class);
    }
    return windowAccessor;
  }
  
  public static void setAWTEventAccessor(AWTEventAccessor paramAWTEventAccessor)
  {
    awtEventAccessor = paramAWTEventAccessor;
  }
  
  public static AWTEventAccessor getAWTEventAccessor()
  {
    if (awtEventAccessor == null) {
      unsafe.ensureClassInitialized(AWTEvent.class);
    }
    return awtEventAccessor;
  }
  
  public static void setInputEventAccessor(InputEventAccessor paramInputEventAccessor)
  {
    inputEventAccessor = paramInputEventAccessor;
  }
  
  public static InputEventAccessor getInputEventAccessor()
  {
    if (inputEventAccessor == null) {
      unsafe.ensureClassInitialized(InputEvent.class);
    }
    return inputEventAccessor;
  }
  
  public static void setFrameAccessor(FrameAccessor paramFrameAccessor)
  {
    frameAccessor = paramFrameAccessor;
  }
  
  public static FrameAccessor getFrameAccessor()
  {
    if (frameAccessor == null) {
      unsafe.ensureClassInitialized(Frame.class);
    }
    return frameAccessor;
  }
  
  public static void setKeyboardFocusManagerAccessor(KeyboardFocusManagerAccessor paramKeyboardFocusManagerAccessor)
  {
    kfmAccessor = paramKeyboardFocusManagerAccessor;
  }
  
  public static KeyboardFocusManagerAccessor getKeyboardFocusManagerAccessor()
  {
    if (kfmAccessor == null) {
      unsafe.ensureClassInitialized(KeyboardFocusManager.class);
    }
    return kfmAccessor;
  }
  
  public static void setMenuComponentAccessor(MenuComponentAccessor paramMenuComponentAccessor)
  {
    menuComponentAccessor = paramMenuComponentAccessor;
  }
  
  public static MenuComponentAccessor getMenuComponentAccessor()
  {
    if (menuComponentAccessor == null) {
      unsafe.ensureClassInitialized(MenuComponent.class);
    }
    return menuComponentAccessor;
  }
  
  public static void setEventQueueAccessor(EventQueueAccessor paramEventQueueAccessor)
  {
    eventQueueAccessor = paramEventQueueAccessor;
  }
  
  public static EventQueueAccessor getEventQueueAccessor()
  {
    if (eventQueueAccessor == null) {
      unsafe.ensureClassInitialized(EventQueue.class);
    }
    return eventQueueAccessor;
  }
  
  public static void setPopupMenuAccessor(PopupMenuAccessor paramPopupMenuAccessor)
  {
    popupMenuAccessor = paramPopupMenuAccessor;
  }
  
  public static PopupMenuAccessor getPopupMenuAccessor()
  {
    if (popupMenuAccessor == null) {
      unsafe.ensureClassInitialized(PopupMenu.class);
    }
    return popupMenuAccessor;
  }
  
  public static void setFileDialogAccessor(FileDialogAccessor paramFileDialogAccessor)
  {
    fileDialogAccessor = paramFileDialogAccessor;
  }
  
  public static FileDialogAccessor getFileDialogAccessor()
  {
    if (fileDialogAccessor == null) {
      unsafe.ensureClassInitialized(FileDialog.class);
    }
    return fileDialogAccessor;
  }
  
  public static void setScrollPaneAdjustableAccessor(ScrollPaneAdjustableAccessor paramScrollPaneAdjustableAccessor)
  {
    scrollPaneAdjustableAccessor = paramScrollPaneAdjustableAccessor;
  }
  
  public static ScrollPaneAdjustableAccessor getScrollPaneAdjustableAccessor()
  {
    if (scrollPaneAdjustableAccessor == null) {
      unsafe.ensureClassInitialized(ScrollPaneAdjustable.class);
    }
    return scrollPaneAdjustableAccessor;
  }
  
  public static void setCheckboxMenuItemAccessor(CheckboxMenuItemAccessor paramCheckboxMenuItemAccessor)
  {
    checkboxMenuItemAccessor = paramCheckboxMenuItemAccessor;
  }
  
  public static CheckboxMenuItemAccessor getCheckboxMenuItemAccessor()
  {
    if (checkboxMenuItemAccessor == null) {
      unsafe.ensureClassInitialized(CheckboxMenuItemAccessor.class);
    }
    return checkboxMenuItemAccessor;
  }
  
  public static void setCursorAccessor(CursorAccessor paramCursorAccessor)
  {
    cursorAccessor = paramCursorAccessor;
  }
  
  public static CursorAccessor getCursorAccessor()
  {
    if (cursorAccessor == null) {
      unsafe.ensureClassInitialized(CursorAccessor.class);
    }
    return cursorAccessor;
  }
  
  public static void setMenuBarAccessor(MenuBarAccessor paramMenuBarAccessor)
  {
    menuBarAccessor = paramMenuBarAccessor;
  }
  
  public static MenuBarAccessor getMenuBarAccessor()
  {
    if (menuBarAccessor == null) {
      unsafe.ensureClassInitialized(MenuBarAccessor.class);
    }
    return menuBarAccessor;
  }
  
  public static void setMenuItemAccessor(MenuItemAccessor paramMenuItemAccessor)
  {
    menuItemAccessor = paramMenuItemAccessor;
  }
  
  public static MenuItemAccessor getMenuItemAccessor()
  {
    if (menuItemAccessor == null) {
      unsafe.ensureClassInitialized(MenuItemAccessor.class);
    }
    return menuItemAccessor;
  }
  
  public static void setMenuAccessor(MenuAccessor paramMenuAccessor)
  {
    menuAccessor = paramMenuAccessor;
  }
  
  public static MenuAccessor getMenuAccessor()
  {
    if (menuAccessor == null) {
      unsafe.ensureClassInitialized(MenuAccessor.class);
    }
    return menuAccessor;
  }
  
  public static void setKeyEventAccessor(KeyEventAccessor paramKeyEventAccessor)
  {
    keyEventAccessor = paramKeyEventAccessor;
  }
  
  public static KeyEventAccessor getKeyEventAccessor()
  {
    if (keyEventAccessor == null) {
      unsafe.ensureClassInitialized(KeyEventAccessor.class);
    }
    return keyEventAccessor;
  }
  
  public static void setClientPropertyKeyAccessor(ClientPropertyKeyAccessor paramClientPropertyKeyAccessor)
  {
    clientPropertyKeyAccessor = paramClientPropertyKeyAccessor;
  }
  
  public static ClientPropertyKeyAccessor getClientPropertyKeyAccessor()
  {
    if (clientPropertyKeyAccessor == null) {
      unsafe.ensureClassInitialized(ClientPropertyKeyAccessor.class);
    }
    return clientPropertyKeyAccessor;
  }
  
  public static void setSystemTrayAccessor(SystemTrayAccessor paramSystemTrayAccessor)
  {
    systemTrayAccessor = paramSystemTrayAccessor;
  }
  
  public static SystemTrayAccessor getSystemTrayAccessor()
  {
    if (systemTrayAccessor == null) {
      unsafe.ensureClassInitialized(SystemTrayAccessor.class);
    }
    return systemTrayAccessor;
  }
  
  public static void setTrayIconAccessor(TrayIconAccessor paramTrayIconAccessor)
  {
    trayIconAccessor = paramTrayIconAccessor;
  }
  
  public static TrayIconAccessor getTrayIconAccessor()
  {
    if (trayIconAccessor == null) {
      unsafe.ensureClassInitialized(TrayIconAccessor.class);
    }
    return trayIconAccessor;
  }
  
  public static void setDefaultKeyboardFocusManagerAccessor(DefaultKeyboardFocusManagerAccessor paramDefaultKeyboardFocusManagerAccessor)
  {
    defaultKeyboardFocusManagerAccessor = paramDefaultKeyboardFocusManagerAccessor;
  }
  
  public static DefaultKeyboardFocusManagerAccessor getDefaultKeyboardFocusManagerAccessor()
  {
    if (defaultKeyboardFocusManagerAccessor == null) {
      unsafe.ensureClassInitialized(DefaultKeyboardFocusManagerAccessor.class);
    }
    return defaultKeyboardFocusManagerAccessor;
  }
  
  public static void setSequencedEventAccessor(SequencedEventAccessor paramSequencedEventAccessor)
  {
    sequencedEventAccessor = paramSequencedEventAccessor;
  }
  
  public static SequencedEventAccessor getSequencedEventAccessor()
  {
    return sequencedEventAccessor;
  }
  
  public static void setToolkitAccessor(ToolkitAccessor paramToolkitAccessor)
  {
    toolkitAccessor = paramToolkitAccessor;
  }
  
  public static ToolkitAccessor getToolkitAccessor()
  {
    if (toolkitAccessor == null) {
      unsafe.ensureClassInitialized(Toolkit.class);
    }
    return toolkitAccessor;
  }
  
  public static void setInvocationEventAccessor(InvocationEventAccessor paramInvocationEventAccessor)
  {
    invocationEventAccessor = paramInvocationEventAccessor;
  }
  
  public static InvocationEventAccessor getInvocationEventAccessor()
  {
    return invocationEventAccessor;
  }
  
  public static SystemColorAccessor getSystemColorAccessor()
  {
    if (systemColorAccessor == null) {
      unsafe.ensureClassInitialized(SystemColor.class);
    }
    return systemColorAccessor;
  }
  
  public static void setSystemColorAccessor(SystemColorAccessor paramSystemColorAccessor)
  {
    systemColorAccessor = paramSystemColorAccessor;
  }
  
  public static AccessibleContextAccessor getAccessibleContextAccessor()
  {
    if (accessibleContextAccessor == null) {
      unsafe.ensureClassInitialized(AccessibleContext.class);
    }
    return accessibleContextAccessor;
  }
  
  public static void setAccessibleContextAccessor(AccessibleContextAccessor paramAccessibleContextAccessor)
  {
    accessibleContextAccessor = paramAccessibleContextAccessor;
  }
  
  public static abstract interface AWTEventAccessor
  {
    public abstract void setPosted(AWTEvent paramAWTEvent);
    
    public abstract void setSystemGenerated(AWTEvent paramAWTEvent);
    
    public abstract boolean isSystemGenerated(AWTEvent paramAWTEvent);
    
    public abstract AccessControlContext getAccessControlContext(AWTEvent paramAWTEvent);
    
    public abstract byte[] getBData(AWTEvent paramAWTEvent);
    
    public abstract void setBData(AWTEvent paramAWTEvent, byte[] paramArrayOfByte);
  }
  
  public static abstract interface AccessibleContextAccessor
  {
    public abstract void setAppContext(AccessibleContext paramAccessibleContext, AppContext paramAppContext);
    
    public abstract AppContext getAppContext(AccessibleContext paramAccessibleContext);
  }
  
  public static abstract interface CheckboxMenuItemAccessor
  {
    public abstract boolean getState(CheckboxMenuItem paramCheckboxMenuItem);
  }
  
  public static abstract interface ClientPropertyKeyAccessor
  {
    public abstract Object getJComponent_TRANSFER_HANDLER();
  }
  
  public static abstract interface ComponentAccessor
  {
    public abstract void setBackgroundEraseDisabled(Component paramComponent, boolean paramBoolean);
    
    public abstract boolean getBackgroundEraseDisabled(Component paramComponent);
    
    public abstract Rectangle getBounds(Component paramComponent);
    
    public abstract void setMixingCutoutShape(Component paramComponent, Shape paramShape);
    
    public abstract void setGraphicsConfiguration(Component paramComponent, GraphicsConfiguration paramGraphicsConfiguration);
    
    public abstract boolean requestFocus(Component paramComponent, CausedFocusEvent.Cause paramCause);
    
    public abstract boolean canBeFocusOwner(Component paramComponent);
    
    public abstract boolean isVisible(Component paramComponent);
    
    public abstract void setRequestFocusController(RequestFocusController paramRequestFocusController);
    
    public abstract AppContext getAppContext(Component paramComponent);
    
    public abstract void setAppContext(Component paramComponent, AppContext paramAppContext);
    
    public abstract Container getParent(Component paramComponent);
    
    public abstract void setParent(Component paramComponent, Container paramContainer);
    
    public abstract void setSize(Component paramComponent, int paramInt1, int paramInt2);
    
    public abstract Point getLocation(Component paramComponent);
    
    public abstract void setLocation(Component paramComponent, int paramInt1, int paramInt2);
    
    public abstract boolean isEnabled(Component paramComponent);
    
    public abstract boolean isDisplayable(Component paramComponent);
    
    public abstract Cursor getCursor(Component paramComponent);
    
    public abstract ComponentPeer getPeer(Component paramComponent);
    
    public abstract void setPeer(Component paramComponent, ComponentPeer paramComponentPeer);
    
    public abstract boolean isLightweight(Component paramComponent);
    
    public abstract boolean getIgnoreRepaint(Component paramComponent);
    
    public abstract int getWidth(Component paramComponent);
    
    public abstract int getHeight(Component paramComponent);
    
    public abstract int getX(Component paramComponent);
    
    public abstract int getY(Component paramComponent);
    
    public abstract Color getForeground(Component paramComponent);
    
    public abstract Color getBackground(Component paramComponent);
    
    public abstract void setBackground(Component paramComponent, Color paramColor);
    
    public abstract Font getFont(Component paramComponent);
    
    public abstract void processEvent(Component paramComponent, AWTEvent paramAWTEvent);
    
    public abstract AccessControlContext getAccessControlContext(Component paramComponent);
    
    public abstract void revalidateSynchronously(Component paramComponent);
  }
  
  public static abstract interface ContainerAccessor
  {
    public abstract void validateUnconditionally(Container paramContainer);
    
    public abstract Component findComponentAt(Container paramContainer, int paramInt1, int paramInt2, boolean paramBoolean);
  }
  
  public static abstract interface CursorAccessor
  {
    public abstract long getPData(Cursor paramCursor);
    
    public abstract void setPData(Cursor paramCursor, long paramLong);
    
    public abstract int getType(Cursor paramCursor);
  }
  
  public static abstract interface DefaultKeyboardFocusManagerAccessor
  {
    public abstract void consumeNextKeyTyped(DefaultKeyboardFocusManager paramDefaultKeyboardFocusManager, KeyEvent paramKeyEvent);
  }
  
  public static abstract interface EventQueueAccessor
  {
    public abstract Thread getDispatchThread(EventQueue paramEventQueue);
    
    public abstract boolean isDispatchThreadImpl(EventQueue paramEventQueue);
    
    public abstract void removeSourceEvents(EventQueue paramEventQueue, Object paramObject, boolean paramBoolean);
    
    public abstract boolean noEvents(EventQueue paramEventQueue);
    
    public abstract void wakeup(EventQueue paramEventQueue, boolean paramBoolean);
    
    public abstract void invokeAndWait(Object paramObject, Runnable paramRunnable)
      throws InterruptedException, InvocationTargetException;
    
    public abstract void setFwDispatcher(EventQueue paramEventQueue, FwDispatcher paramFwDispatcher);
    
    public abstract long getMostRecentEventTime(EventQueue paramEventQueue);
  }
  
  public static abstract interface FileDialogAccessor
  {
    public abstract void setFiles(FileDialog paramFileDialog, File[] paramArrayOfFile);
    
    public abstract void setFile(FileDialog paramFileDialog, String paramString);
    
    public abstract void setDirectory(FileDialog paramFileDialog, String paramString);
    
    public abstract boolean isMultipleMode(FileDialog paramFileDialog);
  }
  
  public static abstract interface FrameAccessor
  {
    public abstract void setExtendedState(Frame paramFrame, int paramInt);
    
    public abstract int getExtendedState(Frame paramFrame);
    
    public abstract Rectangle getMaximizedBounds(Frame paramFrame);
  }
  
  public static abstract interface InputEventAccessor
  {
    public abstract int[] getButtonDownMasks();
  }
  
  public static abstract interface InvocationEventAccessor
  {
    public abstract void dispose(InvocationEvent paramInvocationEvent);
  }
  
  public static abstract interface KeyEventAccessor
  {
    public abstract void setRawCode(KeyEvent paramKeyEvent, long paramLong);
    
    public abstract void setPrimaryLevelUnicode(KeyEvent paramKeyEvent, long paramLong);
    
    public abstract void setExtendedKeyCode(KeyEvent paramKeyEvent, long paramLong);
    
    public abstract Component getOriginalSource(KeyEvent paramKeyEvent);
  }
  
  public static abstract interface KeyboardFocusManagerAccessor
  {
    public abstract int shouldNativelyFocusHeavyweight(Component paramComponent1, Component paramComponent2, boolean paramBoolean1, boolean paramBoolean2, long paramLong, CausedFocusEvent.Cause paramCause);
    
    public abstract boolean processSynchronousLightweightTransfer(Component paramComponent1, Component paramComponent2, boolean paramBoolean1, boolean paramBoolean2, long paramLong);
    
    public abstract void removeLastFocusRequest(Component paramComponent);
    
    public abstract void setMostRecentFocusOwner(Window paramWindow, Component paramComponent);
    
    public abstract KeyboardFocusManager getCurrentKeyboardFocusManager(AppContext paramAppContext);
    
    public abstract Container getCurrentFocusCycleRoot();
  }
  
  public static abstract interface MenuAccessor
  {
    public abstract Vector getItems(Menu paramMenu);
  }
  
  public static abstract interface MenuBarAccessor
  {
    public abstract Menu getHelpMenu(MenuBar paramMenuBar);
    
    public abstract Vector getMenus(MenuBar paramMenuBar);
  }
  
  public static abstract interface MenuComponentAccessor
  {
    public abstract AppContext getAppContext(MenuComponent paramMenuComponent);
    
    public abstract void setAppContext(MenuComponent paramMenuComponent, AppContext paramAppContext);
    
    public abstract MenuContainer getParent(MenuComponent paramMenuComponent);
    
    public abstract Font getFont_NoClientCode(MenuComponent paramMenuComponent);
    
    public abstract <T extends MenuComponentPeer> T getPeer(MenuComponent paramMenuComponent);
  }
  
  public static abstract interface MenuItemAccessor
  {
    public abstract boolean isEnabled(MenuItem paramMenuItem);
    
    public abstract String getActionCommandImpl(MenuItem paramMenuItem);
    
    public abstract boolean isItemEnabled(MenuItem paramMenuItem);
    
    public abstract String getLabel(MenuItem paramMenuItem);
    
    public abstract MenuShortcut getShortcut(MenuItem paramMenuItem);
  }
  
  public static abstract interface PopupMenuAccessor
  {
    public abstract boolean isTrayIconPopup(PopupMenu paramPopupMenu);
  }
  
  public static abstract interface ScrollPaneAdjustableAccessor
  {
    public abstract void setTypedValue(ScrollPaneAdjustable paramScrollPaneAdjustable, int paramInt1, int paramInt2);
  }
  
  public static abstract interface SequencedEventAccessor
  {
    public abstract AWTEvent getNested(AWTEvent paramAWTEvent);
    
    public abstract boolean isSequencedEvent(AWTEvent paramAWTEvent);
  }
  
  public static abstract interface SystemColorAccessor
  {
    public abstract void updateSystemColors();
  }
  
  public static abstract interface SystemTrayAccessor
  {
    public abstract void firePropertyChange(SystemTray paramSystemTray, String paramString, Object paramObject1, Object paramObject2);
  }
  
  public static abstract interface ToolkitAccessor
  {
    public abstract void setPlatformResources(ResourceBundle paramResourceBundle);
  }
  
  public static abstract interface TrayIconAccessor
  {
    public abstract void addNotify(TrayIcon paramTrayIcon)
      throws AWTException;
    
    public abstract void removeNotify(TrayIcon paramTrayIcon);
  }
  
  public static abstract interface WindowAccessor
  {
    public abstract float getOpacity(Window paramWindow);
    
    public abstract void setOpacity(Window paramWindow, float paramFloat);
    
    public abstract Shape getShape(Window paramWindow);
    
    public abstract void setShape(Window paramWindow, Shape paramShape);
    
    public abstract void setOpaque(Window paramWindow, boolean paramBoolean);
    
    public abstract void updateWindow(Window paramWindow);
    
    public abstract Dimension getSecurityWarningSize(Window paramWindow);
    
    public abstract void setSecurityWarningSize(Window paramWindow, int paramInt1, int paramInt2);
    
    public abstract void setSecurityWarningPosition(Window paramWindow, Point2D paramPoint2D, float paramFloat1, float paramFloat2);
    
    public abstract Point2D calculateSecurityWarningPosition(Window paramWindow, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4);
    
    public abstract void setLWRequestStatus(Window paramWindow, boolean paramBoolean);
    
    public abstract boolean isAutoRequestFocus(Window paramWindow);
    
    public abstract boolean isTrayIconWindow(Window paramWindow);
    
    public abstract void setTrayIconWindow(Window paramWindow, boolean paramBoolean);
    
    public abstract Window[] getOwnedWindows(Window paramWindow);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\AWTAccessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */