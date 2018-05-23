package sun.awt;

import java.awt.AWTError;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.peer.ComponentPeer;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.ListIterator;
import sun.awt.windows.WToolkit;
import sun.java2d.SunGraphicsEnvironment;
import sun.java2d.SurfaceManagerFactory;
import sun.java2d.WindowsSurfaceManagerFactory;
import sun.java2d.d3d.D3DGraphicsDevice;
import sun.java2d.windows.WindowsFlags;

public class Win32GraphicsEnvironment
  extends SunGraphicsEnvironment
{
  private static boolean displayInitialized;
  private ArrayList<WeakReference<Win32GraphicsDevice>> oldDevices;
  private static volatile boolean isDWMCompositionEnabled;
  
  private static native void initDisplay();
  
  public static void initDisplayWrapper()
  {
    if (!displayInitialized)
    {
      displayInitialized = true;
      initDisplay();
    }
  }
  
  public Win32GraphicsEnvironment() {}
  
  protected native int getNumScreens();
  
  protected native int getDefaultScreen();
  
  public GraphicsDevice getDefaultScreenDevice()
  {
    GraphicsDevice[] arrayOfGraphicsDevice = getScreenDevices();
    if (arrayOfGraphicsDevice.length == 0) {
      throw new AWTError("no screen devices");
    }
    int i = getDefaultScreen();
    return arrayOfGraphicsDevice[0];
  }
  
  public native int getXResolution();
  
  public native int getYResolution();
  
  public void displayChanged()
  {
    GraphicsDevice[] arrayOfGraphicsDevice1 = new GraphicsDevice[getNumScreens()];
    GraphicsDevice[] arrayOfGraphicsDevice2 = screens;
    if (arrayOfGraphicsDevice2 != null)
    {
      for (i = 0; i < arrayOfGraphicsDevice2.length; i++) {
        if (!(screens[i] instanceof Win32GraphicsDevice))
        {
          if (!$assertionsDisabled) {
            throw new AssertionError(arrayOfGraphicsDevice2[i]);
          }
        }
        else
        {
          Win32GraphicsDevice localWin32GraphicsDevice1 = (Win32GraphicsDevice)arrayOfGraphicsDevice2[i];
          if (!localWin32GraphicsDevice1.isValid())
          {
            if (oldDevices == null) {
              oldDevices = new ArrayList();
            }
            oldDevices.add(new WeakReference(localWin32GraphicsDevice1));
          }
          else if (i < arrayOfGraphicsDevice1.length)
          {
            arrayOfGraphicsDevice1[i] = localWin32GraphicsDevice1;
          }
        }
      }
      arrayOfGraphicsDevice2 = null;
    }
    for (int i = 0; i < arrayOfGraphicsDevice1.length; i++) {
      if (arrayOfGraphicsDevice1[i] == null) {
        arrayOfGraphicsDevice1[i] = makeScreenDevice(i);
      }
    }
    screens = arrayOfGraphicsDevice1;
    for (GraphicsDevice localGraphicsDevice : screens) {
      if ((localGraphicsDevice instanceof DisplayChangedListener)) {
        ((DisplayChangedListener)localGraphicsDevice).displayChanged();
      }
    }
    if (oldDevices != null)
    {
      int j = getDefaultScreen();
      ListIterator localListIterator = oldDevices.listIterator();
      while (localListIterator.hasNext())
      {
        Win32GraphicsDevice localWin32GraphicsDevice2 = (Win32GraphicsDevice)((WeakReference)localListIterator.next()).get();
        if (localWin32GraphicsDevice2 != null)
        {
          localWin32GraphicsDevice2.invalidate(j);
          localWin32GraphicsDevice2.displayChanged();
        }
        else
        {
          localListIterator.remove();
        }
      }
    }
    WToolkit.resetGC();
    displayChanger.notifyListeners();
  }
  
  protected GraphicsDevice makeScreenDevice(int paramInt)
  {
    Object localObject = null;
    if (WindowsFlags.isD3DEnabled()) {
      localObject = D3DGraphicsDevice.createDevice(paramInt);
    }
    if (localObject == null) {
      localObject = new Win32GraphicsDevice(paramInt);
    }
    return (GraphicsDevice)localObject;
  }
  
  public boolean isDisplayLocal()
  {
    return true;
  }
  
  public boolean isFlipStrategyPreferred(ComponentPeer paramComponentPeer)
  {
    GraphicsConfiguration localGraphicsConfiguration;
    if ((paramComponentPeer != null) && ((localGraphicsConfiguration = paramComponentPeer.getGraphicsConfiguration()) != null))
    {
      GraphicsDevice localGraphicsDevice = localGraphicsConfiguration.getDevice();
      if ((localGraphicsDevice instanceof D3DGraphicsDevice)) {
        return ((D3DGraphicsDevice)localGraphicsDevice).isD3DEnabledOnDevice();
      }
    }
    return false;
  }
  
  public static boolean isDWMCompositionEnabled()
  {
    return isDWMCompositionEnabled;
  }
  
  private static void dwmCompositionChanged(boolean paramBoolean)
  {
    isDWMCompositionEnabled = paramBoolean;
  }
  
  public static native boolean isVistaOS();
  
  static
  {
    WToolkit.loadLibraries();
    WindowsFlags.initFlags();
    initDisplayWrapper();
    SurfaceManagerFactory.setInstance(new WindowsSurfaceManagerFactory());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\Win32GraphicsEnvironment.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */