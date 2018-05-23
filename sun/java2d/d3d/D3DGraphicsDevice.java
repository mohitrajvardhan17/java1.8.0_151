package sun.java2d.d3d;

import java.awt.Dialog;
import java.awt.DisplayMode;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.peer.WindowPeer;
import java.io.PrintStream;
import java.util.ArrayList;
import sun.awt.Win32GraphicsDevice;
import sun.awt.windows.WWindowPeer;
import sun.java2d.pipe.hw.ContextCapabilities;
import sun.java2d.windows.WindowsFlags;
import sun.misc.PerfCounter;

public class D3DGraphicsDevice
  extends Win32GraphicsDevice
{
  private D3DContext context;
  private static boolean d3dAvailable;
  private ContextCapabilities d3dCaps;
  private boolean fsStatus;
  private Rectangle ownerOrigBounds = null;
  private boolean ownerWasVisible;
  private Window realFSWindow;
  private WindowListener fsWindowListener;
  private boolean fsWindowWasAlwaysOnTop;
  
  private static native boolean initD3D();
  
  public static D3DGraphicsDevice createDevice(int paramInt)
  {
    if (!d3dAvailable) {
      return null;
    }
    ContextCapabilities localContextCapabilities = getDeviceCaps(paramInt);
    if ((localContextCapabilities.getCaps() & 0x40000) == 0)
    {
      if (WindowsFlags.isD3DVerbose()) {
        System.out.println("Could not enable Direct3D pipeline on screen " + paramInt);
      }
      return null;
    }
    if (WindowsFlags.isD3DVerbose()) {
      System.out.println("Direct3D pipeline enabled on screen " + paramInt);
    }
    D3DGraphicsDevice localD3DGraphicsDevice = new D3DGraphicsDevice(paramInt, localContextCapabilities);
    return localD3DGraphicsDevice;
  }
  
  private static native int getDeviceCapsNative(int paramInt);
  
  private static native String getDeviceIdNative(int paramInt);
  
  private static ContextCapabilities getDeviceCaps(final int paramInt)
  {
    D3DContext.D3DContextCaps localD3DContextCaps = null;
    D3DRenderQueue localD3DRenderQueue = D3DRenderQueue.getInstance();
    localD3DRenderQueue.lock();
    try
    {
      Object local1Result = new Object()
      {
        int caps;
        String id;
      };
      localD3DRenderQueue.flushAndInvokeNow(new Runnable()
      {
        public void run()
        {
          val$res.caps = D3DGraphicsDevice.getDeviceCapsNative(paramInt);
          val$res.id = D3DGraphicsDevice.getDeviceIdNative(paramInt);
        }
      });
      localD3DContextCaps = new D3DContext.D3DContextCaps(caps, id);
    }
    finally
    {
      localD3DRenderQueue.unlock();
    }
    return localD3DContextCaps != null ? localD3DContextCaps : new D3DContext.D3DContextCaps(0, null);
  }
  
  public final boolean isCapPresent(int paramInt)
  {
    return (d3dCaps.getCaps() & paramInt) != 0;
  }
  
  private D3DGraphicsDevice(int paramInt, ContextCapabilities paramContextCapabilities)
  {
    super(paramInt);
    descString = ("D3DGraphicsDevice[screen=" + paramInt);
    d3dCaps = paramContextCapabilities;
    context = new D3DContext(D3DRenderQueue.getInstance(), this);
  }
  
  public boolean isD3DEnabledOnDevice()
  {
    return (isValid()) && (isCapPresent(262144));
  }
  
  public static boolean isD3DAvailable()
  {
    return d3dAvailable;
  }
  
  private Frame getToplevelOwner(Window paramWindow)
  {
    Window localWindow = paramWindow;
    while (localWindow != null)
    {
      localWindow = localWindow.getOwner();
      if ((localWindow instanceof Frame)) {
        return (Frame)localWindow;
      }
    }
    return null;
  }
  
  private static native boolean enterFullScreenExclusiveNative(int paramInt, long paramLong);
  
  protected void enterFullScreenExclusive(final int paramInt, WindowPeer paramWindowPeer)
  {
    final WWindowPeer localWWindowPeer = (WWindowPeer)realFSWindow.getPeer();
    D3DRenderQueue localD3DRenderQueue = D3DRenderQueue.getInstance();
    localD3DRenderQueue.lock();
    try
    {
      localD3DRenderQueue.flushAndInvokeNow(new Runnable()
      {
        public void run()
        {
          long l = localWWindowPeer.getHWnd();
          if (l == 0L)
          {
            fsStatus = false;
            return;
          }
          fsStatus = D3DGraphicsDevice.enterFullScreenExclusiveNative(paramInt, l);
        }
      });
    }
    finally
    {
      localD3DRenderQueue.unlock();
    }
    if (!fsStatus) {
      super.enterFullScreenExclusive(paramInt, paramWindowPeer);
    }
  }
  
  private static native boolean exitFullScreenExclusiveNative(int paramInt);
  
  protected void exitFullScreenExclusive(final int paramInt, WindowPeer paramWindowPeer)
  {
    if (fsStatus)
    {
      D3DRenderQueue localD3DRenderQueue = D3DRenderQueue.getInstance();
      localD3DRenderQueue.lock();
      try
      {
        localD3DRenderQueue.flushAndInvokeNow(new Runnable()
        {
          public void run()
          {
            D3DGraphicsDevice.exitFullScreenExclusiveNative(paramInt);
          }
        });
      }
      finally
      {
        localD3DRenderQueue.unlock();
      }
    }
    else
    {
      super.exitFullScreenExclusive(paramInt, paramWindowPeer);
    }
  }
  
  protected void addFSWindowListener(Window paramWindow)
  {
    if ((!(paramWindow instanceof Frame)) && (!(paramWindow instanceof Dialog)) && ((realFSWindow = getToplevelOwner(paramWindow)) != null))
    {
      ownerOrigBounds = realFSWindow.getBounds();
      WWindowPeer localWWindowPeer = (WWindowPeer)realFSWindow.getPeer();
      ownerWasVisible = realFSWindow.isVisible();
      Rectangle localRectangle = paramWindow.getBounds();
      localWWindowPeer.reshape(x, y, width, height);
      localWWindowPeer.setVisible(true);
    }
    else
    {
      realFSWindow = paramWindow;
    }
    fsWindowWasAlwaysOnTop = realFSWindow.isAlwaysOnTop();
    ((WWindowPeer)realFSWindow.getPeer()).setAlwaysOnTop(true);
    fsWindowListener = new D3DFSWindowAdapter(null);
    realFSWindow.addWindowListener(fsWindowListener);
  }
  
  protected void removeFSWindowListener(Window paramWindow)
  {
    realFSWindow.removeWindowListener(fsWindowListener);
    fsWindowListener = null;
    WWindowPeer localWWindowPeer = (WWindowPeer)realFSWindow.getPeer();
    if (localWWindowPeer != null)
    {
      if (ownerOrigBounds != null)
      {
        if (ownerOrigBounds.width == 0) {
          ownerOrigBounds.width = 1;
        }
        if (ownerOrigBounds.height == 0) {
          ownerOrigBounds.height = 1;
        }
        localWWindowPeer.reshape(ownerOrigBounds.x, ownerOrigBounds.y, ownerOrigBounds.width, ownerOrigBounds.height);
        if (!ownerWasVisible) {
          localWWindowPeer.setVisible(false);
        }
        ownerOrigBounds = null;
      }
      if (!fsWindowWasAlwaysOnTop) {
        localWWindowPeer.setAlwaysOnTop(false);
      }
    }
    realFSWindow = null;
  }
  
  private static native DisplayMode getCurrentDisplayModeNative(int paramInt);
  
  protected DisplayMode getCurrentDisplayMode(final int paramInt)
  {
    D3DRenderQueue localD3DRenderQueue = D3DRenderQueue.getInstance();
    localD3DRenderQueue.lock();
    try
    {
      final Object local2Result = new Object()
      {
        DisplayMode dm = null;
      };
      localD3DRenderQueue.flushAndInvokeNow(new Runnable()
      {
        public void run()
        {
          local2Resultdm = D3DGraphicsDevice.getCurrentDisplayModeNative(paramInt);
        }
      });
      if (dm == null)
      {
        localDisplayMode = super.getCurrentDisplayMode(paramInt);
        return localDisplayMode;
      }
      DisplayMode localDisplayMode = dm;
      return localDisplayMode;
    }
    finally
    {
      localD3DRenderQueue.unlock();
    }
  }
  
  private static native void configDisplayModeNative(int paramInt1, long paramLong, int paramInt2, int paramInt3, int paramInt4, int paramInt5);
  
  protected void configDisplayMode(final int paramInt1, WindowPeer paramWindowPeer, final int paramInt2, final int paramInt3, final int paramInt4, final int paramInt5)
  {
    if (!fsStatus)
    {
      super.configDisplayMode(paramInt1, paramWindowPeer, paramInt2, paramInt3, paramInt4, paramInt5);
      return;
    }
    final WWindowPeer localWWindowPeer = (WWindowPeer)realFSWindow.getPeer();
    if (getFullScreenWindow() != realFSWindow)
    {
      localObject1 = getDefaultConfiguration().getBounds();
      localWWindowPeer.reshape(x, y, paramInt2, paramInt3);
    }
    Object localObject1 = D3DRenderQueue.getInstance();
    ((D3DRenderQueue)localObject1).lock();
    try
    {
      ((D3DRenderQueue)localObject1).flushAndInvokeNow(new Runnable()
      {
        public void run()
        {
          long l = localWWindowPeer.getHWnd();
          if (l == 0L) {
            return;
          }
          D3DGraphicsDevice.configDisplayModeNative(paramInt1, l, paramInt2, paramInt3, paramInt4, paramInt5);
        }
      });
    }
    finally
    {
      ((D3DRenderQueue)localObject1).unlock();
    }
  }
  
  private static native void enumDisplayModesNative(int paramInt, ArrayList paramArrayList);
  
  protected void enumDisplayModes(final int paramInt, final ArrayList paramArrayList)
  {
    D3DRenderQueue localD3DRenderQueue = D3DRenderQueue.getInstance();
    localD3DRenderQueue.lock();
    try
    {
      localD3DRenderQueue.flushAndInvokeNow(new Runnable()
      {
        public void run()
        {
          D3DGraphicsDevice.enumDisplayModesNative(paramInt, paramArrayList);
        }
      });
      if (paramArrayList.size() == 0) {
        paramArrayList.add(getCurrentDisplayModeNative(paramInt));
      }
    }
    finally
    {
      localD3DRenderQueue.unlock();
    }
  }
  
  private static native long getAvailableAcceleratedMemoryNative(int paramInt);
  
  public int getAvailableAcceleratedMemory()
  {
    D3DRenderQueue localD3DRenderQueue = D3DRenderQueue.getInstance();
    localD3DRenderQueue.lock();
    try
    {
      final Object local3Result = new Object()
      {
        long mem = 0L;
      };
      localD3DRenderQueue.flushAndInvokeNow(new Runnable()
      {
        public void run()
        {
          local3Resultmem = D3DGraphicsDevice.getAvailableAcceleratedMemoryNative(getScreen());
        }
      });
      int i = (int)mem;
      return i;
    }
    finally
    {
      localD3DRenderQueue.unlock();
    }
  }
  
  public GraphicsConfiguration[] getConfigurations()
  {
    if ((configs == null) && (isD3DEnabledOnDevice()))
    {
      defaultConfig = getDefaultConfiguration();
      if (defaultConfig != null)
      {
        configs = new GraphicsConfiguration[1];
        configs[0] = defaultConfig;
        return (GraphicsConfiguration[])configs.clone();
      }
    }
    return super.getConfigurations();
  }
  
  public GraphicsConfiguration getDefaultConfiguration()
  {
    if (defaultConfig == null) {
      if (isD3DEnabledOnDevice()) {
        defaultConfig = new D3DGraphicsConfig(this);
      } else {
        defaultConfig = super.getDefaultConfiguration();
      }
    }
    return defaultConfig;
  }
  
  private static native boolean isD3DAvailableOnDeviceNative(int paramInt);
  
  public static boolean isD3DAvailableOnDevice(final int paramInt)
  {
    if (!d3dAvailable) {
      return false;
    }
    D3DRenderQueue localD3DRenderQueue = D3DRenderQueue.getInstance();
    localD3DRenderQueue.lock();
    try
    {
      Object local4Result = new Object()
      {
        boolean avail = false;
      };
      localD3DRenderQueue.flushAndInvokeNow(new Runnable()
      {
        public void run()
        {
          val$res.avail = D3DGraphicsDevice.isD3DAvailableOnDeviceNative(paramInt);
        }
      });
      boolean bool = avail;
      return bool;
    }
    finally
    {
      localD3DRenderQueue.unlock();
    }
  }
  
  D3DContext getContext()
  {
    return context;
  }
  
  ContextCapabilities getContextCapabilities()
  {
    return d3dCaps;
  }
  
  public void displayChanged()
  {
    super.displayChanged();
    if (d3dAvailable) {
      d3dCaps = getDeviceCaps(getScreen());
    }
  }
  
  protected void invalidate(int paramInt)
  {
    super.invalidate(paramInt);
    d3dCaps = new D3DContext.D3DContextCaps(0, null);
  }
  
  static
  {
    Toolkit.getDefaultToolkit();
    d3dAvailable = initD3D();
    if (d3dAvailable)
    {
      pfDisabled = true;
      PerfCounter.getD3DAvailable().set(1L);
    }
    else
    {
      PerfCounter.getD3DAvailable().set(0L);
    }
  }
  
  private static class D3DFSWindowAdapter
    extends WindowAdapter
  {
    private D3DFSWindowAdapter() {}
    
    public void windowDeactivated(WindowEvent paramWindowEvent)
    {
      D3DRenderQueue.getInstance();
      D3DRenderQueue.restoreDevices();
    }
    
    public void windowActivated(WindowEvent paramWindowEvent)
    {
      D3DRenderQueue.getInstance();
      D3DRenderQueue.restoreDevices();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\java2d\d3d\D3DGraphicsDevice.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */