package sun.applet;

public class AppletIllegalArgumentException
  extends IllegalArgumentException
{
  private String key = null;
  private static AppletMessageHandler amh = new AppletMessageHandler("appletillegalargumentexception");
  
  public AppletIllegalArgumentException(String paramString)
  {
    super(paramString);
    key = paramString;
  }
  
  public String getLocalizedMessage()
  {
    return amh.getMessage(key);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\applet\AppletIllegalArgumentException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */