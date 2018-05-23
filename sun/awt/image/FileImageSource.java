package sun.awt.image;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class FileImageSource
  extends InputStreamImageSource
{
  String imagefile;
  
  public FileImageSource(String paramString)
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkRead(paramString);
    }
    imagefile = paramString;
  }
  
  final boolean checkSecurity(Object paramObject, boolean paramBoolean)
  {
    return true;
  }
  
  protected ImageDecoder getDecoder()
  {
    if (imagefile == null) {
      return null;
    }
    BufferedInputStream localBufferedInputStream;
    try
    {
      localBufferedInputStream = new BufferedInputStream(new FileInputStream(imagefile));
    }
    catch (FileNotFoundException localFileNotFoundException)
    {
      return null;
    }
    return getDecoder(localBufferedInputStream);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\image\FileImageSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */