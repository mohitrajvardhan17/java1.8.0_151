package sun.applet;

import java.io.IOException;

public class AppletIOException
  extends IOException
{
  private String key = null;
  private Object msgobj = null;
  private static AppletMessageHandler amh = new AppletMessageHandler("appletioexception");
  
  public AppletIOException(String paramString)
  {
    super(paramString);
    key = paramString;
  }
  
  public AppletIOException(String paramString, Object paramObject)
  {
    this(paramString);
    msgobj = paramObject;
  }
  
  public String getLocalizedMessage()
  {
    if (msgobj != null) {
      return amh.getMessage(key, msgobj);
    }
    return amh.getMessage(key);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\applet\AppletIOException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */