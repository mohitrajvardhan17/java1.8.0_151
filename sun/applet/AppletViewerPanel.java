package sun.applet;

import java.applet.AppletContext;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;

class AppletViewerPanel
  extends AppletPanel
{
  static boolean debug = false;
  URL documentURL;
  URL baseURL;
  Hashtable atts;
  private static final long serialVersionUID = 8890989370785545619L;
  
  AppletViewerPanel(URL paramURL, Hashtable paramHashtable)
  {
    documentURL = paramURL;
    atts = paramHashtable;
    String str1 = getParameter("codebase");
    if (str1 != null)
    {
      if (!str1.endsWith("/")) {
        str1 = str1 + "/";
      }
      try
      {
        baseURL = new URL(paramURL, str1);
      }
      catch (MalformedURLException localMalformedURLException1) {}
    }
    if (baseURL == null)
    {
      String str2 = paramURL.getFile();
      int i = str2.lastIndexOf('/');
      if ((i >= 0) && (i < str2.length() - 1)) {
        try
        {
          baseURL = new URL(paramURL, str2.substring(0, i + 1));
        }
        catch (MalformedURLException localMalformedURLException2) {}
      }
    }
    if (baseURL == null) {
      baseURL = paramURL;
    }
  }
  
  public String getParameter(String paramString)
  {
    return (String)atts.get(paramString.toLowerCase());
  }
  
  public URL getDocumentBase()
  {
    return documentURL;
  }
  
  public URL getCodeBase()
  {
    return baseURL;
  }
  
  public int getWidth()
  {
    String str = getParameter("width");
    if (str != null) {
      return Integer.valueOf(str).intValue();
    }
    return 0;
  }
  
  public int getHeight()
  {
    String str = getParameter("height");
    if (str != null) {
      return Integer.valueOf(str).intValue();
    }
    return 0;
  }
  
  public boolean hasInitialFocus()
  {
    if ((isJDK11Applet()) || (isJDK12Applet())) {
      return false;
    }
    String str = getParameter("initial_focus");
    return (str == null) || (!str.toLowerCase().equals("false"));
  }
  
  public String getCode()
  {
    return getParameter("code");
  }
  
  public String getJarFiles()
  {
    return getParameter("archive");
  }
  
  public String getSerializedObject()
  {
    return getParameter("object");
  }
  
  public AppletContext getAppletContext()
  {
    return (AppletContext)getParent();
  }
  
  static void debug(String paramString)
  {
    if (debug) {
      System.err.println("AppletViewerPanel:::" + paramString);
    }
  }
  
  static void debug(String paramString, Throwable paramThrowable)
  {
    if (debug)
    {
      paramThrowable.printStackTrace();
      debug(paramString);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\applet\AppletViewerPanel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */