package java.awt;

import java.awt.peer.MouseInfoPeer;
import sun.security.util.SecurityConstants.AWT;

public class MouseInfo
{
  private MouseInfo() {}
  
  public static PointerInfo getPointerInfo()
    throws HeadlessException
  {
    if (GraphicsEnvironment.isHeadless()) {
      throw new HeadlessException();
    }
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPermission(SecurityConstants.AWT.WATCH_MOUSE_PERMISSION);
    }
    Point localPoint = new Point(0, 0);
    int i = Toolkit.getDefaultToolkit().getMouseInfoPeer().fillPointWithCoords(localPoint);
    GraphicsDevice[] arrayOfGraphicsDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
    PointerInfo localPointerInfo = null;
    if (areScreenDevicesIndependent(arrayOfGraphicsDevice)) {
      localPointerInfo = new PointerInfo(arrayOfGraphicsDevice[i], localPoint);
    } else {
      for (int j = 0; j < arrayOfGraphicsDevice.length; j++)
      {
        GraphicsConfiguration localGraphicsConfiguration = arrayOfGraphicsDevice[j].getDefaultConfiguration();
        Rectangle localRectangle = localGraphicsConfiguration.getBounds();
        if (localRectangle.contains(localPoint)) {
          localPointerInfo = new PointerInfo(arrayOfGraphicsDevice[j], localPoint);
        }
      }
    }
    return localPointerInfo;
  }
  
  private static boolean areScreenDevicesIndependent(GraphicsDevice[] paramArrayOfGraphicsDevice)
  {
    for (int i = 0; i < paramArrayOfGraphicsDevice.length; i++)
    {
      Rectangle localRectangle = paramArrayOfGraphicsDevice[i].getDefaultConfiguration().getBounds();
      if ((x != 0) || (y != 0)) {
        return false;
      }
    }
    return true;
  }
  
  public static int getNumberOfButtons()
    throws HeadlessException
  {
    if (GraphicsEnvironment.isHeadless()) {
      throw new HeadlessException();
    }
    Object localObject = Toolkit.getDefaultToolkit().getDesktopProperty("awt.mouse.numButtons");
    if ((localObject instanceof Integer)) {
      return ((Integer)localObject).intValue();
    }
    if (!$assertionsDisabled) {
      throw new AssertionError("awt.mouse.numButtons is not an integer property");
    }
    return 0;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\awt\MouseInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */