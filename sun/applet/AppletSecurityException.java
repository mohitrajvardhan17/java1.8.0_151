package sun.applet;

public class AppletSecurityException
  extends SecurityException
{
  private String key = null;
  private Object[] msgobj = null;
  private static AppletMessageHandler amh = new AppletMessageHandler("appletsecurityexception");
  
  public AppletSecurityException(String paramString)
  {
    super(paramString);
    key = paramString;
  }
  
  public AppletSecurityException(String paramString1, String paramString2)
  {
    this(paramString1);
    msgobj = new Object[1];
    msgobj[0] = paramString2;
  }
  
  public AppletSecurityException(String paramString1, String paramString2, String paramString3)
  {
    this(paramString1);
    msgobj = new Object[2];
    msgobj[0] = paramString2;
    msgobj[1] = paramString3;
  }
  
  public String getLocalizedMessage()
  {
    if (msgobj != null) {
      return amh.getMessage(key, msgobj);
    }
    return amh.getMessage(key);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\applet\AppletSecurityException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */