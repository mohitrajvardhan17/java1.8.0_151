package sun.awt.windows;

import java.awt.AWTEvent;
import java.awt.AWTEventMulticaster;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Window;
import java.awt.Window.Type;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.DataBufferInt;
import java.awt.peer.WindowPeer;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.LinkedList;
import java.util.List;
import sun.awt.AWTAccessor;
import sun.awt.AWTAccessor.ComponentAccessor;
import sun.awt.AWTAccessor.WindowAccessor;
import sun.awt.AppContext;
import sun.awt.CausedFocusEvent.Cause;
import sun.awt.DisplayChangedListener;
import sun.awt.SunToolkit;
import sun.awt.Win32GraphicsConfig;
import sun.awt.Win32GraphicsDevice;
import sun.awt.Win32GraphicsEnvironment;
import sun.java2d.pipe.Region;
import sun.util.logging.PlatformLogger;
import sun.util.logging.PlatformLogger.Level;

public class WWindowPeer
  extends WPanelPeer
  implements WindowPeer, DisplayChangedListener
{
  private static final PlatformLogger log = PlatformLogger.getLogger("sun.awt.windows.WWindowPeer");
  private static final PlatformLogger screenLog = PlatformLogger.getLogger("sun.awt.windows.screen.WWindowPeer");
  private WWindowPeer modalBlocker = null;
  private boolean isOpaque;
  private TranslucentWindowPainter painter;
  private static final StringBuffer ACTIVE_WINDOWS_KEY = new StringBuffer("active_windows_list");
  private static PropertyChangeListener activeWindowListener = new ActiveWindowListener(null);
  private static final PropertyChangeListener guiDisposedListener = new GuiDisposedListener(null);
  private WindowListener windowListener;
  private volatile Window.Type windowType = Window.Type.NORMAL;
  private volatile int sysX = 0;
  private volatile int sysY = 0;
  private volatile int sysW = 0;
  private volatile int sysH = 0;
  private float opacity = 1.0F;
  
  private static native void initIDs();
  
  protected void disposeImpl()
  {
    AppContext localAppContext = SunToolkit.targetToAppContext(target);
    synchronized (localAppContext)
    {
      List localList = (List)localAppContext.get(ACTIVE_WINDOWS_KEY);
      if (localList != null) {
        localList.remove(this);
      }
    }
    ??? = getGraphicsConfiguration();
    ((Win32GraphicsDevice)((GraphicsConfiguration)???).getDevice()).removeDisplayChangedListener(this);
    synchronized (getStateLock())
    {
      TranslucentWindowPainter localTranslucentWindowPainter = painter;
      if (localTranslucentWindowPainter != null) {
        localTranslucentWindowPainter.flush();
      }
    }
    super.disposeImpl();
  }
  
  public void toFront()
  {
    updateFocusableWindowState();
    _toFront();
  }
  
  private native void _toFront();
  
  public native void toBack();
  
  private native void setAlwaysOnTopNative(boolean paramBoolean);
  
  public void setAlwaysOnTop(boolean paramBoolean)
  {
    if (((paramBoolean) && (((Window)target).isVisible())) || (!paramBoolean)) {
      setAlwaysOnTopNative(paramBoolean);
    }
  }
  
  public void updateAlwaysOnTopState()
  {
    setAlwaysOnTop(((Window)target).isAlwaysOnTop());
  }
  
  public void updateFocusableWindowState()
  {
    setFocusableWindow(((Window)target).isFocusableWindow());
  }
  
  native void setFocusableWindow(boolean paramBoolean);
  
  public void setTitle(String paramString)
  {
    if (paramString == null) {
      paramString = "";
    }
    _setTitle(paramString);
  }
  
  private native void _setTitle(String paramString);
  
  public void setResizable(boolean paramBoolean)
  {
    _setResizable(paramBoolean);
  }
  
  private native void _setResizable(boolean paramBoolean);
  
  WWindowPeer(Window paramWindow)
  {
    super(paramWindow);
  }
  
  void initialize()
  {
    super.initialize();
    updateInsets(insets_);
    Font localFont = ((Window)target).getFont();
    if (localFont == null)
    {
      localFont = defaultFont;
      ((Window)target).setFont(localFont);
      setFont(localFont);
    }
    GraphicsConfiguration localGraphicsConfiguration = getGraphicsConfiguration();
    ((Win32GraphicsDevice)localGraphicsConfiguration.getDevice()).addDisplayChangedListener(this);
    initActiveWindowsTracking((Window)target);
    updateIconImages();
    Shape localShape = ((Window)target).getShape();
    if (localShape != null) {
      applyShape(Region.getInstance(localShape, null));
    }
    float f = ((Window)target).getOpacity();
    if (f < 1.0F) {
      setOpacity(f);
    }
    synchronized (getStateLock())
    {
      isOpaque = true;
      setOpaque(((Window)target).isOpaque());
    }
  }
  
  native void createAwtWindow(WComponentPeer paramWComponentPeer);
  
  void preCreate(WComponentPeer paramWComponentPeer)
  {
    windowType = ((Window)target).getType();
  }
  
  void create(WComponentPeer paramWComponentPeer)
  {
    preCreate(paramWComponentPeer);
    createAwtWindow(paramWComponentPeer);
  }
  
  final WComponentPeer getNativeParent()
  {
    Window localWindow = ((Window)target).getOwner();
    return (WComponentPeer)WToolkit.targetToPeer(localWindow);
  }
  
  protected void realShow()
  {
    super.show();
  }
  
  public void show()
  {
    updateFocusableWindowState();
    boolean bool = ((Window)target).isAlwaysOnTop();
    updateGC();
    realShow();
    updateMinimumSize();
    if ((((Window)target).isAlwaysOnTopSupported()) && (bool)) {
      setAlwaysOnTop(bool);
    }
    synchronized (getStateLock())
    {
      if (!isOpaque) {
        updateWindow(true);
      }
    }
    ??? = getNativeParent();
    if ((??? != null) && (((WComponentPeer)???).isLightweightFramePeer()))
    {
      Rectangle localRectangle = getBounds();
      handleExpose(0, 0, width, height);
    }
  }
  
  native void updateInsets(Insets paramInsets);
  
  static native int getSysMinWidth();
  
  static native int getSysMinHeight();
  
  static native int getSysIconWidth();
  
  static native int getSysIconHeight();
  
  static native int getSysSmIconWidth();
  
  static native int getSysSmIconHeight();
  
  native void setIconImagesData(int[] paramArrayOfInt1, int paramInt1, int paramInt2, int[] paramArrayOfInt2, int paramInt3, int paramInt4);
  
  synchronized native void reshapeFrame(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  public boolean requestWindowFocus(CausedFocusEvent.Cause paramCause)
  {
    if (!focusAllowedFor()) {
      return false;
    }
    return requestWindowFocus(paramCause == CausedFocusEvent.Cause.MOUSE_EVENT);
  }
  
  private native boolean requestWindowFocus(boolean paramBoolean);
  
  public boolean focusAllowedFor()
  {
    Window localWindow = (Window)target;
    if ((!localWindow.isVisible()) || (!localWindow.isEnabled()) || (!localWindow.isFocusableWindow())) {
      return false;
    }
    return !isModalBlocked();
  }
  
  void hide()
  {
    WindowListener localWindowListener = windowListener;
    if (localWindowListener != null) {
      localWindowListener.windowClosing(new WindowEvent((Window)target, 201));
    }
    super.hide();
  }
  
  void preprocessPostEvent(AWTEvent paramAWTEvent)
  {
    if ((paramAWTEvent instanceof WindowEvent))
    {
      WindowListener localWindowListener = windowListener;
      if (localWindowListener != null) {
        switch (paramAWTEvent.getID())
        {
        case 201: 
          localWindowListener.windowClosing((WindowEvent)paramAWTEvent);
          break;
        case 203: 
          localWindowListener.windowIconified((WindowEvent)paramAWTEvent);
        }
      }
    }
  }
  
  synchronized void addWindowListener(WindowListener paramWindowListener)
  {
    windowListener = AWTEventMulticaster.add(windowListener, paramWindowListener);
  }
  
  synchronized void removeWindowListener(WindowListener paramWindowListener)
  {
    windowListener = AWTEventMulticaster.remove(windowListener, paramWindowListener);
  }
  
  public void updateMinimumSize()
  {
    Dimension localDimension = null;
    if (((Component)target).isMinimumSizeSet()) {
      localDimension = ((Component)target).getMinimumSize();
    }
    if (localDimension != null)
    {
      int i = getSysMinWidth();
      int j = getSysMinHeight();
      int k = width >= i ? width : i;
      int m = height >= j ? height : j;
      setMinSize(k, m);
    }
    else
    {
      setMinSize(0, 0);
    }
  }
  
  public void updateIconImages()
  {
    List localList = ((Window)target).getIconImages();
    if ((localList == null) || (localList.size() == 0))
    {
      setIconImagesData(null, 0, 0, null, 0, 0);
    }
    else
    {
      int i = getSysIconWidth();
      int j = getSysIconHeight();
      int k = getSysSmIconWidth();
      int m = getSysSmIconHeight();
      DataBufferInt localDataBufferInt1 = SunToolkit.getScaledIconData(localList, i, j);
      DataBufferInt localDataBufferInt2 = SunToolkit.getScaledIconData(localList, k, m);
      if ((localDataBufferInt1 != null) && (localDataBufferInt2 != null)) {
        setIconImagesData(localDataBufferInt1.getData(), i, j, localDataBufferInt2.getData(), k, m);
      } else {
        setIconImagesData(null, 0, 0, null, 0, 0);
      }
    }
  }
  
  native void setMinSize(int paramInt1, int paramInt2);
  
  public boolean isModalBlocked()
  {
    return modalBlocker != null;
  }
  
  public void setModalBlocked(Dialog paramDialog, boolean paramBoolean)
  {
    synchronized (((Component)getTarget()).getTreeLock())
    {
      WWindowPeer localWWindowPeer = (WWindowPeer)paramDialog.getPeer();
      if (paramBoolean)
      {
        modalBlocker = localWWindowPeer;
        if ((localWWindowPeer instanceof WFileDialogPeer)) {
          ((WFileDialogPeer)localWWindowPeer).blockWindow(this);
        } else if ((localWWindowPeer instanceof WPrintDialogPeer)) {
          ((WPrintDialogPeer)localWWindowPeer).blockWindow(this);
        } else {
          modalDisable(paramDialog, localWWindowPeer.getHWnd());
        }
      }
      else
      {
        modalBlocker = null;
        if ((localWWindowPeer instanceof WFileDialogPeer)) {
          ((WFileDialogPeer)localWWindowPeer).unblockWindow(this);
        } else if ((localWWindowPeer instanceof WPrintDialogPeer)) {
          ((WPrintDialogPeer)localWWindowPeer).unblockWindow(this);
        } else {
          modalEnable(paramDialog);
        }
      }
    }
  }
  
  native void modalDisable(Dialog paramDialog, long paramLong);
  
  native void modalEnable(Dialog paramDialog);
  
  public static long[] getActiveWindowHandles(Component paramComponent)
  {
    AppContext localAppContext = SunToolkit.targetToAppContext(paramComponent);
    if (localAppContext == null) {
      return null;
    }
    synchronized (localAppContext)
    {
      List localList = (List)localAppContext.get(ACTIVE_WINDOWS_KEY);
      if (localList == null) {
        return null;
      }
      long[] arrayOfLong = new long[localList.size()];
      for (int i = 0; i < localList.size(); i++) {
        arrayOfLong[i] = ((WWindowPeer)localList.get(i)).getHWnd();
      }
      return arrayOfLong;
    }
  }
  
  void draggedToNewScreen()
  {
    SunToolkit.executeOnEventHandlerThread((Component)target, new Runnable()
    {
      public void run()
      {
        displayChanged();
      }
    });
  }
  
  public void updateGC()
  {
    int i = getScreenImOn();
    if (screenLog.isLoggable(PlatformLogger.Level.FINER)) {
      log.finer("Screen number: " + i);
    }
    Win32GraphicsDevice localWin32GraphicsDevice1 = (Win32GraphicsDevice)winGraphicsConfig.getDevice();
    GraphicsDevice[] arrayOfGraphicsDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
    Win32GraphicsDevice localWin32GraphicsDevice2;
    if (i >= arrayOfGraphicsDevice.length) {
      localWin32GraphicsDevice2 = (Win32GraphicsDevice)GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
    } else {
      localWin32GraphicsDevice2 = (Win32GraphicsDevice)arrayOfGraphicsDevice[i];
    }
    winGraphicsConfig = ((Win32GraphicsConfig)localWin32GraphicsDevice2.getDefaultConfiguration());
    if ((screenLog.isLoggable(PlatformLogger.Level.FINE)) && (winGraphicsConfig == null)) {
      screenLog.fine("Assertion (winGraphicsConfig != null) failed");
    }
    if (localWin32GraphicsDevice1 != localWin32GraphicsDevice2)
    {
      localWin32GraphicsDevice1.removeDisplayChangedListener(this);
      localWin32GraphicsDevice2.addDisplayChangedListener(this);
    }
    AWTAccessor.getComponentAccessor().setGraphicsConfiguration((Component)target, winGraphicsConfig);
  }
  
  public void displayChanged()
  {
    updateGC();
  }
  
  public void paletteChanged() {}
  
  private native int getScreenImOn();
  
  public final native void setFullScreenExclusiveModeState(boolean paramBoolean);
  
  public void grab()
  {
    nativeGrab();
  }
  
  public void ungrab()
  {
    nativeUngrab();
  }
  
  private native void nativeGrab();
  
  private native void nativeUngrab();
  
  private final boolean hasWarningWindow()
  {
    return ((Window)target).getWarningString() != null;
  }
  
  boolean isTargetUndecorated()
  {
    return true;
  }
  
  public native void repositionSecurityWarning();
  
  public void setBounds(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    sysX = paramInt1;
    sysY = paramInt2;
    sysW = paramInt3;
    sysH = paramInt4;
    super.setBounds(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
  }
  
  public void print(Graphics paramGraphics)
  {
    Shape localShape = AWTAccessor.getWindowAccessor().getShape((Window)target);
    if (localShape != null) {
      paramGraphics.setClip(localShape);
    }
    super.print(paramGraphics);
  }
  
  private void replaceSurfaceDataRecursively(Component paramComponent)
  {
    if ((paramComponent instanceof Container)) {
      for (Component localComponent : ((Container)paramComponent).getComponents()) {
        replaceSurfaceDataRecursively(localComponent);
      }
    }
    ??? = paramComponent.getPeer();
    if ((??? instanceof WComponentPeer)) {
      ((WComponentPeer)???).replaceSurfaceDataLater();
    }
  }
  
  public final Graphics getTranslucentGraphics()
  {
    synchronized (getStateLock())
    {
      return isOpaque ? null : painter.getBackBuffer(false).getGraphics();
    }
  }
  
  public void setBackground(Color paramColor)
  {
    super.setBackground(paramColor);
    synchronized (getStateLock())
    {
      if ((!isOpaque) && (((Window)target).isVisible())) {
        updateWindow(true);
      }
    }
  }
  
  private native void setOpacity(int paramInt);
  
  public void setOpacity(float paramFloat)
  {
    if (!((SunToolkit)((Window)target).getToolkit()).isWindowOpacitySupported()) {
      return;
    }
    if ((paramFloat < 0.0F) || (paramFloat > 1.0F)) {
      throw new IllegalArgumentException("The value of opacity should be in the range [0.0f .. 1.0f].");
    }
    if (((opacity == 1.0F) && (paramFloat < 1.0F)) || ((opacity < 1.0F) && (paramFloat == 1.0F) && (!Win32GraphicsEnvironment.isVistaOS()))) {
      replaceSurfaceDataRecursively((Component)getTarget());
    }
    opacity = paramFloat;
    int i = (int)(paramFloat * 255.0F);
    if (i < 0) {
      i = 0;
    }
    if (i > 255) {
      i = 255;
    }
    setOpacity(i);
    synchronized (getStateLock())
    {
      if ((!isOpaque) && (((Window)target).isVisible())) {
        updateWindow(true);
      }
    }
  }
  
  private native void setOpaqueImpl(boolean paramBoolean);
  
  public void setOpaque(boolean paramBoolean)
  {
    synchronized (getStateLock())
    {
      if (isOpaque == paramBoolean) {
        return;
      }
    }
    ??? = (Window)getTarget();
    if (!paramBoolean)
    {
      SunToolkit localSunToolkit = (SunToolkit)((Window)???).getToolkit();
      if ((!localSunToolkit.isWindowTranslucencySupported()) || (!localSunToolkit.isTranslucencyCapable(((Window)???).getGraphicsConfiguration()))) {
        return;
      }
    }
    boolean bool = Win32GraphicsEnvironment.isVistaOS();
    if ((isOpaque != paramBoolean) && (!bool)) {
      replaceSurfaceDataRecursively((Component)???);
    }
    synchronized (getStateLock())
    {
      isOpaque = paramBoolean;
      setOpaqueImpl(paramBoolean);
      if (paramBoolean)
      {
        TranslucentWindowPainter localTranslucentWindowPainter = painter;
        if (localTranslucentWindowPainter != null)
        {
          localTranslucentWindowPainter.flush();
          painter = null;
        }
      }
      else
      {
        painter = TranslucentWindowPainter.createInstance(this);
      }
    }
    if (bool)
    {
      ??? = ((Window)???).getShape();
      if (??? != null) {
        ((Window)???).setShape((Shape)???);
      }
    }
    if (((Window)???).isVisible()) {
      updateWindow(true);
    }
  }
  
  native void updateWindowImpl(int[] paramArrayOfInt, int paramInt1, int paramInt2);
  
  public void updateWindow()
  {
    updateWindow(false);
  }
  
  private void updateWindow(boolean paramBoolean)
  {
    Window localWindow = (Window)target;
    synchronized (getStateLock())
    {
      if ((isOpaque) || (!localWindow.isVisible()) || (localWindow.getWidth() <= 0) || (localWindow.getHeight() <= 0)) {
        return;
      }
      TranslucentWindowPainter localTranslucentWindowPainter = painter;
      if (localTranslucentWindowPainter != null) {
        localTranslucentWindowPainter.updateWindow(paramBoolean);
      } else if (log.isLoggable(PlatformLogger.Level.FINER)) {
        log.finer("Translucent window painter is null in updateWindow");
      }
    }
  }
  
  private static void initActiveWindowsTracking(Window paramWindow)
  {
    AppContext localAppContext = AppContext.getAppContext();
    synchronized (localAppContext)
    {
      Object localObject1 = (List)localAppContext.get(ACTIVE_WINDOWS_KEY);
      if (localObject1 == null)
      {
        localObject1 = new LinkedList();
        localAppContext.put(ACTIVE_WINDOWS_KEY, localObject1);
        localAppContext.addPropertyChangeListener("guidisposed", guiDisposedListener);
        KeyboardFocusManager localKeyboardFocusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        localKeyboardFocusManager.addPropertyChangeListener("activeWindow", activeWindowListener);
      }
    }
  }
  
  static
  {
    initIDs();
  }
  
  private static class ActiveWindowListener
    implements PropertyChangeListener
  {
    private ActiveWindowListener() {}
    
    public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
    {
      Window localWindow = (Window)paramPropertyChangeEvent.getNewValue();
      if (localWindow == null) {
        return;
      }
      AppContext localAppContext = SunToolkit.targetToAppContext(localWindow);
      synchronized (localAppContext)
      {
        WWindowPeer localWWindowPeer = (WWindowPeer)localWindow.getPeer();
        List localList = (List)localAppContext.get(WWindowPeer.ACTIVE_WINDOWS_KEY);
        if (localList != null)
        {
          localList.remove(localWWindowPeer);
          localList.add(localWWindowPeer);
        }
      }
    }
  }
  
  private static class GuiDisposedListener
    implements PropertyChangeListener
  {
    private GuiDisposedListener() {}
    
    public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
    {
      boolean bool = ((Boolean)paramPropertyChangeEvent.getNewValue()).booleanValue();
      if ((bool != true) && (WWindowPeer.log.isLoggable(PlatformLogger.Level.FINE))) {
        WWindowPeer.log.fine(" Assertion (newValue != true) failed for AppContext.GUI_DISPOSED ");
      }
      AppContext localAppContext = AppContext.getAppContext();
      synchronized (localAppContext)
      {
        localAppContext.remove(WWindowPeer.ACTIVE_WINDOWS_KEY);
        localAppContext.removePropertyChangeListener("guidisposed", this);
        KeyboardFocusManager localKeyboardFocusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        localKeyboardFocusManager.removePropertyChangeListener("activeWindow", WWindowPeer.activeWindowListener);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\windows\WWindowPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */