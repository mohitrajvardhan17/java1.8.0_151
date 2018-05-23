package sun.font;

import java.security.AccessController;
import java.security.PrivilegedAction;

public final class FontManagerFactory
{
  private static FontManager instance = null;
  private static final String DEFAULT_CLASS;
  
  public FontManagerFactory() {}
  
  public static synchronized FontManager getInstance()
  {
    if (instance != null) {
      return instance;
    }
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        try
        {
          String str = System.getProperty("sun.font.fontmanager", FontManagerFactory.DEFAULT_CLASS);
          ClassLoader localClassLoader = ClassLoader.getSystemClassLoader();
          Class localClass = Class.forName(str, true, localClassLoader);
          FontManagerFactory.access$102((FontManager)localClass.newInstance());
        }
        catch (ClassNotFoundException|InstantiationException|IllegalAccessException localClassNotFoundException)
        {
          throw new InternalError(localClassNotFoundException);
        }
        return null;
      }
    });
    return instance;
  }
  
  static
  {
    if (FontUtilities.isWindows) {
      DEFAULT_CLASS = "sun.awt.Win32FontManager";
    } else if (FontUtilities.isMacOSX) {
      DEFAULT_CLASS = "sun.font.CFontManager";
    } else {
      DEFAULT_CLASS = "sun.awt.X11FontManager";
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\font\FontManagerFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */