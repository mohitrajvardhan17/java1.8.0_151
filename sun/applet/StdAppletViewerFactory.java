package sun.applet;

import java.awt.MenuBar;
import java.net.URL;
import java.util.Hashtable;

final class StdAppletViewerFactory
  implements AppletViewerFactory
{
  StdAppletViewerFactory() {}
  
  public AppletViewer createAppletViewer(int paramInt1, int paramInt2, URL paramURL, Hashtable paramHashtable)
  {
    return new AppletViewer(paramInt1, paramInt2, paramURL, paramHashtable, System.out, this);
  }
  
  public MenuBar getBaseMenuBar()
  {
    return new MenuBar();
  }
  
  public boolean isStandalone()
  {
    return true;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\applet\StdAppletViewerFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */