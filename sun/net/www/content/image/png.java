package sun.net.www.content.image;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.IOException;
import java.net.ContentHandler;
import java.net.URLConnection;
import sun.awt.image.URLImageSource;

public class png
  extends ContentHandler
{
  public png() {}
  
  public Object getContent(URLConnection paramURLConnection)
    throws IOException
  {
    return new URLImageSource(paramURLConnection);
  }
  
  public Object getContent(URLConnection paramURLConnection, Class[] paramArrayOfClass)
    throws IOException
  {
    Class[] arrayOfClass = paramArrayOfClass;
    for (int i = 0; i < arrayOfClass.length; i++)
    {
      if (arrayOfClass[i].isAssignableFrom(URLImageSource.class)) {
        return new URLImageSource(paramURLConnection);
      }
      if (arrayOfClass[i].isAssignableFrom(Image.class))
      {
        Toolkit localToolkit = Toolkit.getDefaultToolkit();
        return localToolkit.createImage(new URLImageSource(paramURLConnection));
      }
    }
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\www\content\image\png.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */