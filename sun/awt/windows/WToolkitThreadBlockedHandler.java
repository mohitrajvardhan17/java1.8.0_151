package sun.awt.windows;

import sun.awt.Mutex;
import sun.awt.datatransfer.ToolkitThreadBlockedHandler;

final class WToolkitThreadBlockedHandler
  extends Mutex
  implements ToolkitThreadBlockedHandler
{
  WToolkitThreadBlockedHandler() {}
  
  public void enter()
  {
    if (!isOwned()) {
      throw new IllegalMonitorStateException();
    }
    unlock();
    startSecondaryEventLoop();
    lock();
  }
  
  public void exit()
  {
    if (!isOwned()) {
      throw new IllegalMonitorStateException();
    }
    WToolkit.quitSecondaryEventLoop();
  }
  
  private native void startSecondaryEventLoop();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\windows\WToolkitThreadBlockedHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */