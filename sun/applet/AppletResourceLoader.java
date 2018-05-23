package sun.applet;

import java.awt.Image;
import java.net.URL;
import sun.misc.Ref;

public class AppletResourceLoader
{
  public AppletResourceLoader() {}
  
  public static Image getImage(URL paramURL)
  {
    return AppletViewer.getCachedImage(paramURL);
  }
  
  public static Ref getImageRef(URL paramURL)
  {
    return AppletViewer.getCachedImageRef(paramURL);
  }
  
  public static void flushImages() {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\applet\AppletResourceLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */