package java.applet;

import java.awt.AWTPermission;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Panel;
import java.awt.Panel.AccessibleAWTPanel;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import sun.applet.AppletAudioClip;

public class Applet
  extends Panel
{
  private transient AppletStub stub;
  private static final long serialVersionUID = -5836846270535785031L;
  AccessibleContext accessibleContext = null;
  
  public Applet()
    throws HeadlessException
  {
    if (GraphicsEnvironment.isHeadless()) {
      throw new HeadlessException();
    }
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws ClassNotFoundException, IOException, HeadlessException
  {
    if (GraphicsEnvironment.isHeadless()) {
      throw new HeadlessException();
    }
    paramObjectInputStream.defaultReadObject();
  }
  
  public final void setStub(AppletStub paramAppletStub)
  {
    if (stub != null)
    {
      SecurityManager localSecurityManager = System.getSecurityManager();
      if (localSecurityManager != null) {
        localSecurityManager.checkPermission(new AWTPermission("setAppletStub"));
      }
    }
    stub = paramAppletStub;
  }
  
  public boolean isActive()
  {
    if (stub != null) {
      return stub.isActive();
    }
    return false;
  }
  
  public URL getDocumentBase()
  {
    return stub.getDocumentBase();
  }
  
  public URL getCodeBase()
  {
    return stub.getCodeBase();
  }
  
  public String getParameter(String paramString)
  {
    return stub.getParameter(paramString);
  }
  
  public AppletContext getAppletContext()
  {
    return stub.getAppletContext();
  }
  
  public void resize(int paramInt1, int paramInt2)
  {
    Dimension localDimension = size();
    if ((width != paramInt1) || (height != paramInt2))
    {
      super.resize(paramInt1, paramInt2);
      if (stub != null) {
        stub.appletResize(paramInt1, paramInt2);
      }
    }
  }
  
  public void resize(Dimension paramDimension)
  {
    resize(width, height);
  }
  
  public boolean isValidateRoot()
  {
    return true;
  }
  
  public void showStatus(String paramString)
  {
    getAppletContext().showStatus(paramString);
  }
  
  public Image getImage(URL paramURL)
  {
    return getAppletContext().getImage(paramURL);
  }
  
  public Image getImage(URL paramURL, String paramString)
  {
    try
    {
      return getImage(new URL(paramURL, paramString));
    }
    catch (MalformedURLException localMalformedURLException) {}
    return null;
  }
  
  public static final AudioClip newAudioClip(URL paramURL)
  {
    return new AppletAudioClip(paramURL);
  }
  
  public AudioClip getAudioClip(URL paramURL)
  {
    return getAppletContext().getAudioClip(paramURL);
  }
  
  public AudioClip getAudioClip(URL paramURL, String paramString)
  {
    try
    {
      return getAudioClip(new URL(paramURL, paramString));
    }
    catch (MalformedURLException localMalformedURLException) {}
    return null;
  }
  
  public String getAppletInfo()
  {
    return null;
  }
  
  public Locale getLocale()
  {
    Locale localLocale = super.getLocale();
    if (localLocale == null) {
      return Locale.getDefault();
    }
    return localLocale;
  }
  
  public String[][] getParameterInfo()
  {
    return (String[][])null;
  }
  
  public void play(URL paramURL)
  {
    AudioClip localAudioClip = getAudioClip(paramURL);
    if (localAudioClip != null) {
      localAudioClip.play();
    }
  }
  
  public void play(URL paramURL, String paramString)
  {
    AudioClip localAudioClip = getAudioClip(paramURL, paramString);
    if (localAudioClip != null) {
      localAudioClip.play();
    }
  }
  
  public void init() {}
  
  public void start() {}
  
  public void stop() {}
  
  public void destroy() {}
  
  public AccessibleContext getAccessibleContext()
  {
    if (accessibleContext == null) {
      accessibleContext = new AccessibleApplet();
    }
    return accessibleContext;
  }
  
  protected class AccessibleApplet
    extends Panel.AccessibleAWTPanel
  {
    private static final long serialVersionUID = 8127374778187708896L;
    
    protected AccessibleApplet()
    {
      super();
    }
    
    public AccessibleRole getAccessibleRole()
    {
      return AccessibleRole.FRAME;
    }
    
    public AccessibleStateSet getAccessibleStateSet()
    {
      AccessibleStateSet localAccessibleStateSet = super.getAccessibleStateSet();
      localAccessibleStateSet.add(AccessibleState.ACTIVE);
      return localAccessibleStateSet;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\applet\Applet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */