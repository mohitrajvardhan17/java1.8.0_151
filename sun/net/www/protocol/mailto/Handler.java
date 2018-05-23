package sun.net.www.protocol.mailto;

import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

public class Handler
  extends URLStreamHandler
{
  public Handler() {}
  
  public synchronized URLConnection openConnection(URL paramURL)
  {
    return new MailToURLConnection(paramURL);
  }
  
  public void parseURL(URL paramURL, String paramString, int paramInt1, int paramInt2)
  {
    String str1 = paramURL.getProtocol();
    String str2 = "";
    int i = paramURL.getPort();
    String str3 = "";
    if (paramInt1 < paramInt2) {
      str3 = paramString.substring(paramInt1, paramInt2);
    }
    int j = 0;
    if ((str3 == null) || (str3.equals("")))
    {
      j = 1;
    }
    else
    {
      int k = 1;
      for (int m = 0; m < str3.length(); m++) {
        if (!Character.isWhitespace(str3.charAt(m))) {
          k = 0;
        }
      }
      if (k != 0) {
        j = 1;
      }
    }
    if (j != 0) {
      throw new RuntimeException("No email address");
    }
    setURLHandler(paramURL, str1, str2, i, str3, null);
  }
  
  private void setURLHandler(URL paramURL, String paramString1, String paramString2, int paramInt, String paramString3, String paramString4)
  {
    setURL(paramURL, paramString1, paramString2, paramInt, paramString3, null);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\www\protocol\mailto\Handler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */