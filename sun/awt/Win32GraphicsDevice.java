package sun.awt;

import java.awt.AWTPermission;
import java.awt.DisplayMode;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.ColorModel;
import java.awt.peer.WindowPeer;
import java.io.PrintStream;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.Vector;
import sun.awt.windows.WWindowPeer;
import sun.java2d.opengl.WGLGraphicsConfig;
import sun.java2d.windows.WindowsFlags;
import sun.security.action.GetPropertyAction;

public class Win32GraphicsDevice
  extends GraphicsDevice
  implements DisplayChangedListener
{
  int screen;
  ColorModel dynamicColorModel;
  ColorModel colorModel;
  protected GraphicsConfiguration[] configs;
  protected GraphicsConfiguration defaultConfig;
  private final String idString;
  protected String descString;
  private boolean valid;
  private SunDisplayChanger topLevels = new SunDisplayChanger();
  protected static boolean pfDisabled;
  private static AWTPermission fullScreenExclusivePermission;
  private DisplayMode defaultDisplayMode;
  private WindowListener fsWindowListener;
  
  private static native void initIDs();
  
  native void initDevice(int paramInt);
  
  public Win32GraphicsDevice(int paramInt)
  {
    screen = paramInt;
    idString = ("\\Display" + screen);
    descString = ("Win32GraphicsDevice[screen=" + screen);
    valid = true;
    initDevice(paramInt);
  }
  
  public int getType()
  {
    return 0;
  }
  
  public int getScreen()
  {
    return screen;
  }
  
  public boolean isValid()
  {
    return valid;
  }
  
  protected void invalidate(int paramInt)
  {
    valid = false;
    screen = paramInt;
  }
  
  public String getIDstring()
  {
    return idString;
  }
  
  public GraphicsConfiguration[] getConfigurations()
  {
    if (configs == null)
    {
      if ((WindowsFlags.isOGLEnabled()) && (isDefaultDevice()))
      {
        defaultConfig = getDefaultConfiguration();
        if (defaultConfig != null)
        {
          configs = new GraphicsConfiguration[1];
          configs[0] = defaultConfig;
          return (GraphicsConfiguration[])configs.clone();
        }
      }
      int i = getMaxConfigs(screen);
      int j = getDefaultPixID(screen);
      Vector localVector = new Vector(i);
      if (j == 0)
      {
        defaultConfig = Win32GraphicsConfig.getConfig(this, j);
        localVector.addElement(defaultConfig);
      }
      else
      {
        for (int k = 1; k <= i; k++) {
          if (isPixFmtSupported(k, screen)) {
            if (k == j)
            {
              defaultConfig = Win32GraphicsConfig.getConfig(this, k);
              localVector.addElement(defaultConfig);
            }
            else
            {
              localVector.addElement(Win32GraphicsConfig.getConfig(this, k));
            }
          }
        }
      }
      configs = new GraphicsConfiguration[localVector.size()];
      localVector.copyInto(configs);
    }
    return (GraphicsConfiguration[])configs.clone();
  }
  
  protected int getMaxConfigs(int paramInt)
  {
    if (pfDisabled) {
      return 1;
    }
    return getMaxConfigsImpl(paramInt);
  }
  
  private native int getMaxConfigsImpl(int paramInt);
  
  protected native boolean isPixFmtSupported(int paramInt1, int paramInt2);
  
  protected int getDefaultPixID(int paramInt)
  {
    if (pfDisabled) {
      return 0;
    }
    return getDefaultPixIDImpl(paramInt);
  }
  
  private native int getDefaultPixIDImpl(int paramInt);
  
  public GraphicsConfiguration getDefaultConfiguration()
  {
    if (defaultConfig == null)
    {
      if ((WindowsFlags.isOGLEnabled()) && (isDefaultDevice()))
      {
        int i = WGLGraphicsConfig.getDefaultPixFmt(screen);
        defaultConfig = WGLGraphicsConfig.getConfig(this, i);
        if (WindowsFlags.isOGLVerbose())
        {
          if (defaultConfig != null) {
            System.out.print("OpenGL pipeline enabled");
          } else {
            System.out.print("Could not enable OpenGL pipeline");
          }
          System.out.println(" for default config on screen " + screen);
        }
      }
      if (defaultConfig == null) {
        defaultConfig = Win32GraphicsConfig.getConfig(this, 0);
      }
    }
    return defaultConfig;
  }
  
  public String toString()
  {
    return descString + ", removed]";
  }
  
  private boolean isDefaultDevice()
  {
    return this == GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
  }
  
  private static boolean isFSExclusiveModeAllowed()
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null)
    {
      if (fullScreenExclusivePermission == null) {
        fullScreenExclusivePermission = new AWTPermission("fullScreenExclusive");
      }
      try
      {
        localSecurityManager.checkPermission(fullScreenExclusivePermission);
      }
      catch (SecurityException localSecurityException)
      {
        return false;
      }
    }
    return true;
  }
  
  public boolean isFullScreenSupported()
  {
    return isFSExclusiveModeAllowed();
  }
  
  public synchronized void setFullScreenWindow(Window paramWindow)
  {
    Window localWindow = getFullScreenWindow();
    if (paramWindow == localWindow) {
      return;
    }
    if (!isFullScreenSupported())
    {
      super.setFullScreenWindow(paramWindow);
      return;
    }
    WWindowPeer localWWindowPeer;
    if (localWindow != null)
    {
      if (defaultDisplayMode != null)
      {
        setDisplayMode(defaultDisplayMode);
        defaultDisplayMode = null;
      }
      localWWindowPeer = (WWindowPeer)localWindow.getPeer();
      if (localWWindowPeer != null)
      {
        localWWindowPeer.setFullScreenExclusiveModeState(false);
        synchronized (localWWindowPeer)
        {
          exitFullScreenExclusive(screen, localWWindowPeer);
        }
      }
      removeFSWindowListener(localWindow);
    }
    super.setFullScreenWindow(paramWindow);
    if (paramWindow != null)
    {
      defaultDisplayMode = getDisplayMode();
      addFSWindowListener(paramWindow);
      localWWindowPeer = (WWindowPeer)paramWindow.getPeer();
      if (localWWindowPeer != null)
      {
        synchronized (localWWindowPeer)
        {
          enterFullScreenExclusive(screen, localWWindowPeer);
        }
        localWWindowPeer.setFullScreenExclusiveModeState(true);
      }
      localWWindowPeer.updateGC();
    }
  }
  
  protected native void enterFullScreenExclusive(int paramInt, WindowPeer paramWindowPeer);
  
  protected native void exitFullScreenExclusive(int paramInt, WindowPeer paramWindowPeer);
  
  public boolean isDisplayChangeSupported()
  {
    return (isFullScreenSupported()) && (getFullScreenWindow() != null);
  }
  
  public synchronized void setDisplayMode(DisplayMode paramDisplayMode)
  {
    if (!isDisplayChangeSupported())
    {
      super.setDisplayMode(paramDisplayMode);
      return;
    }
    if ((paramDisplayMode == null) || ((paramDisplayMode = getMatchingDisplayMode(paramDisplayMode)) == null)) {
      throw new IllegalArgumentException("Invalid display mode");
    }
    if (getDisplayMode().equals(paramDisplayMode)) {
      return;
    }
    Window localWindow = getFullScreenWindow();
    if (localWindow != null)
    {
      WWindowPeer localWWindowPeer = (WWindowPeer)localWindow.getPeer();
      configDisplayMode(screen, localWWindowPeer, paramDisplayMode.getWidth(), paramDisplayMode.getHeight(), paramDisplayMode.getBitDepth(), paramDisplayMode.getRefreshRate());
      Rectangle localRectangle = getDefaultConfiguration().getBounds();
      localWindow.setBounds(x, y, paramDisplayMode.getWidth(), paramDisplayMode.getHeight());
    }
    else
    {
      throw new IllegalStateException("Must be in fullscreen mode in order to set display mode");
    }
  }
  
  protected native DisplayMode getCurrentDisplayMode(int paramInt);
  
  protected native void configDisplayMode(int paramInt1, WindowPeer paramWindowPeer, int paramInt2, int paramInt3, int paramInt4, int paramInt5);
  
  protected native void enumDisplayModes(int paramInt, ArrayList paramArrayList);
  
  public synchronized DisplayMode getDisplayMode()
  {
    DisplayMode localDisplayMode = getCurrentDisplayMode(screen);
    return localDisplayMode;
  }
  
  public synchronized DisplayMode[] getDisplayModes()
  {
    ArrayList localArrayList = new ArrayList();
    enumDisplayModes(screen, localArrayList);
    int i = localArrayList.size();
    DisplayMode[] arrayOfDisplayMode = new DisplayMode[i];
    for (int j = 0; j < i; j++) {
      arrayOfDisplayMode[j] = ((DisplayMode)localArrayList.get(j));
    }
    return arrayOfDisplayMode;
  }
  
  protected synchronized DisplayMode getMatchingDisplayMode(DisplayMode paramDisplayMode)
  {
    if (!isDisplayChangeSupported()) {
      return null;
    }
    DisplayMode[] arrayOfDisplayMode1 = getDisplayModes();
    for (DisplayMode localDisplayMode : arrayOfDisplayMode1) {
      if ((paramDisplayMode.equals(localDisplayMode)) || ((paramDisplayMode.getRefreshRate() == 0) && (paramDisplayMode.getWidth() == localDisplayMode.getWidth()) && (paramDisplayMode.getHeight() == localDisplayMode.getHeight()) && (paramDisplayMode.getBitDepth() == localDisplayMode.getBitDepth()))) {
        return localDisplayMode;
      }
    }
    return null;
  }
  
  public void displayChanged()
  {
    dynamicColorModel = null;
    defaultConfig = null;
    configs = null;
    topLevels.notifyListeners();
  }
  
  public void paletteChanged() {}
  
  public void addDisplayChangedListener(DisplayChangedListener paramDisplayChangedListener)
  {
    topLevels.add(paramDisplayChangedListener);
  }
  
  public void removeDisplayChangedListener(DisplayChangedListener paramDisplayChangedListener)
  {
    topLevels.remove(paramDisplayChangedListener);
  }
  
  private native ColorModel makeColorModel(int paramInt, boolean paramBoolean);
  
  public ColorModel getDynamicColorModel()
  {
    if (dynamicColorModel == null) {
      dynamicColorModel = makeColorModel(screen, true);
    }
    return dynamicColorModel;
  }
  
  public ColorModel getColorModel()
  {
    if (colorModel == null) {
      colorModel = makeColorModel(screen, false);
    }
    return colorModel;
  }
  
  protected void addFSWindowListener(final Window paramWindow)
  {
    fsWindowListener = new Win32FSWindowAdapter(this);
    EventQueue.invokeLater(new Runnable()
    {
      public void run()
      {
        paramWindow.addWindowListener(fsWindowListener);
      }
    });
  }
  
  protected void removeFSWindowListener(Window paramWindow)
  {
    paramWindow.removeWindowListener(fsWindowListener);
    fsWindowListener = null;
  }
  
  static
  {
    String str = (String)AccessController.doPrivileged(new GetPropertyAction("sun.awt.nopixfmt"));
    pfDisabled = str != null;
    initIDs();
  }
  
  private static class Win32FSWindowAdapter
    extends WindowAdapter
  {
    private Win32GraphicsDevice device;
    private DisplayMode dm;
    
    Win32FSWindowAdapter(Win32GraphicsDevice paramWin32GraphicsDevice)
    {
      device = paramWin32GraphicsDevice;
    }
    
    private void setFSWindowsState(Window paramWindow, int paramInt)
    {
      GraphicsDevice[] arrayOfGraphicsDevice1 = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
      GraphicsDevice localGraphicsDevice;
      if (paramWindow != null) {
        for (localGraphicsDevice : arrayOfGraphicsDevice1) {
          if (paramWindow == localGraphicsDevice.getFullScreenWindow()) {
            return;
          }
        }
      }
      for (localGraphicsDevice : arrayOfGraphicsDevice1)
      {
        Window localWindow = localGraphicsDevice.getFullScreenWindow();
        if ((localWindow instanceof Frame)) {
          ((Frame)localWindow).setExtendedState(paramInt);
        }
      }
    }
    
    public void windowDeactivated(WindowEvent paramWindowEvent)
    {
      setFSWindowsState(paramWindowEvent.getOppositeWindow(), 1);
    }
    
    public void windowActivated(WindowEvent paramWindowEvent)
    {
      setFSWindowsState(paramWindowEvent.getOppositeWindow(), 0);
    }
    
    public void windowIconified(WindowEvent paramWindowEvent)
    {
      DisplayMode localDisplayMode = device.defaultDisplayMode;
      if (localDisplayMode != null)
      {
        dm = device.getDisplayMode();
        device.setDisplayMode(localDisplayMode);
      }
    }
    
    public void windowDeiconified(WindowEvent paramWindowEvent)
    {
      if (dm != null)
      {
        device.setDisplayMode(dm);
        dm = null;
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\Win32GraphicsDevice.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */