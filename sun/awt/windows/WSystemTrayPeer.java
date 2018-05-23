package sun.awt.windows;

import java.awt.Dimension;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.peer.SystemTrayPeer;

final class WSystemTrayPeer
  extends WObjectPeer
  implements SystemTrayPeer
{
  WSystemTrayPeer(SystemTray paramSystemTray)
  {
    target = paramSystemTray;
  }
  
  public Dimension getTrayIconSize()
  {
    return new Dimension(16, 16);
  }
  
  public boolean isSupported()
  {
    return ((WToolkit)Toolkit.getDefaultToolkit()).isTraySupported();
  }
  
  protected void disposeImpl() {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\windows\WSystemTrayPeer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */