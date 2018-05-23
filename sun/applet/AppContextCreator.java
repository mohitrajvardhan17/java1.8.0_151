package sun.applet;

import sun.awt.AppContext;
import sun.awt.SunToolkit;

class AppContextCreator
  extends Thread
{
  Object syncObject = new Object();
  AppContext appContext = null;
  volatile boolean created = false;
  
  AppContextCreator(ThreadGroup paramThreadGroup)
  {
    super(paramThreadGroup, "AppContextCreator");
  }
  
  public void run()
  {
    appContext = SunToolkit.createNewAppContext();
    created = true;
    synchronized (syncObject)
    {
      syncObject.notifyAll();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\applet\AppContextCreator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */