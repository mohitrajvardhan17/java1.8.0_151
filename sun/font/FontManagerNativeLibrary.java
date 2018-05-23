package sun.font;

import java.security.AccessController;
import java.security.PrivilegedAction;

public class FontManagerNativeLibrary
{
  public FontManagerNativeLibrary() {}
  
  public static void load() {}
  
  static
  {
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        System.loadLibrary("awt");
        if ((FontUtilities.isOpenJDK) && (System.getProperty("os.name").startsWith("Windows"))) {
          System.loadLibrary("freetype");
        }
        System.loadLibrary("fontmanager");
        return null;
      }
    });
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\font\FontManagerNativeLibrary.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */