package java.applet;

import java.net.URL;

public abstract interface AppletStub
{
  public abstract boolean isActive();
  
  public abstract URL getDocumentBase();
  
  public abstract URL getCodeBase();
  
  public abstract String getParameter(String paramString);
  
  public abstract AppletContext getAppletContext();
  
  public abstract void appletResize(int paramInt1, int paramInt2);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\applet\AppletStub.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */