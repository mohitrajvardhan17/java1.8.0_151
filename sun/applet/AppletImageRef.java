package sun.applet;

import java.awt.Image;
import java.awt.Toolkit;
import java.net.URL;
import sun.awt.image.URLImageSource;
import sun.misc.Ref;

class AppletImageRef
  extends Ref
{
  URL url;
  
  AppletImageRef(URL paramURL)
  {
    url = paramURL;
  }
  
  public void flush()
  {
    super.flush();
  }
  
  public Object reconstitute()
  {
    Image localImage = Toolkit.getDefaultToolkit().createImage(new URLImageSource(url));
    return localImage;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\applet\AppletImageRef.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */