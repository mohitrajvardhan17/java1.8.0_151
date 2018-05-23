package sun.awt;

import java.net.URL;

public abstract class DesktopBrowse
{
  private static volatile DesktopBrowse mInstance;
  
  public DesktopBrowse() {}
  
  public static void setInstance(DesktopBrowse paramDesktopBrowse)
  {
    if (mInstance != null) {
      throw new IllegalStateException("DesktopBrowse instance has already been set.");
    }
    mInstance = paramDesktopBrowse;
  }
  
  public static DesktopBrowse getInstance()
  {
    return mInstance;
  }
  
  public abstract void browse(URL paramURL);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\awt\DesktopBrowse.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */