package java.beans;

import java.applet.Applet;
import java.applet.AppletContext;
import java.applet.AppletStub;
import java.net.URL;

class BeansAppletStub
  implements AppletStub
{
  transient boolean active;
  transient Applet target;
  transient AppletContext context;
  transient URL codeBase;
  transient URL docBase;
  
  BeansAppletStub(Applet paramApplet, AppletContext paramAppletContext, URL paramURL1, URL paramURL2)
  {
    target = paramApplet;
    context = paramAppletContext;
    codeBase = paramURL1;
    docBase = paramURL2;
  }
  
  public boolean isActive()
  {
    return active;
  }
  
  public URL getDocumentBase()
  {
    return docBase;
  }
  
  public URL getCodeBase()
  {
    return codeBase;
  }
  
  public String getParameter(String paramString)
  {
    return null;
  }
  
  public AppletContext getAppletContext()
  {
    return context;
  }
  
  public void appletResize(int paramInt1, int paramInt2) {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\beans\BeansAppletStub.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */