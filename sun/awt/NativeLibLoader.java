package sun.awt;

import java.security.AccessController;
import java.security.PrivilegedAction;

class NativeLibLoader
{
  NativeLibLoader() {}
  
  static void loadLibraries()
  {
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Void run()
      {
        System.loadLibrary("awt");
        return null;
      }
    });
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\NativeLibLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */