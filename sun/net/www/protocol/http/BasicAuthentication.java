package sun.net.www.protocol.http;

import java.io.UnsupportedEncodingException;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Base64;
import java.util.Base64.Encoder;
import sun.net.www.HeaderParser;

class BasicAuthentication
  extends AuthenticationInfo
{
  private static final long serialVersionUID = 100L;
  String auth;
  
  public BasicAuthentication(boolean paramBoolean, String paramString1, int paramInt, String paramString2, PasswordAuthentication paramPasswordAuthentication)
  {
    super(paramBoolean ? 'p' : 's', AuthScheme.BASIC, paramString1, paramInt, paramString2);
    String str = paramPasswordAuthentication.getUserName() + ":";
    byte[] arrayOfByte1 = null;
    try
    {
      arrayOfByte1 = str.getBytes("ISO-8859-1");
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      if (!$assertionsDisabled) {
        throw new AssertionError();
      }
    }
    char[] arrayOfChar = paramPasswordAuthentication.getPassword();
    byte[] arrayOfByte2 = new byte[arrayOfChar.length];
    for (int i = 0; i < arrayOfChar.length; i++) {
      arrayOfByte2[i] = ((byte)arrayOfChar[i]);
    }
    byte[] arrayOfByte3 = new byte[arrayOfByte1.length + arrayOfByte2.length];
    System.arraycopy(arrayOfByte1, 0, arrayOfByte3, 0, arrayOfByte1.length);
    System.arraycopy(arrayOfByte2, 0, arrayOfByte3, arrayOfByte1.length, arrayOfByte2.length);
    auth = ("Basic " + Base64.getEncoder().encodeToString(arrayOfByte3));
    pw = paramPasswordAuthentication;
  }
  
  public BasicAuthentication(boolean paramBoolean, String paramString1, int paramInt, String paramString2, String paramString3)
  {
    super(paramBoolean ? 'p' : 's', AuthScheme.BASIC, paramString1, paramInt, paramString2);
    auth = ("Basic " + paramString3);
  }
  
  public BasicAuthentication(boolean paramBoolean, URL paramURL, String paramString, PasswordAuthentication paramPasswordAuthentication)
  {
    super(paramBoolean ? 'p' : 's', AuthScheme.BASIC, paramURL, paramString);
    String str = paramPasswordAuthentication.getUserName() + ":";
    byte[] arrayOfByte1 = null;
    try
    {
      arrayOfByte1 = str.getBytes("ISO-8859-1");
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      if (!$assertionsDisabled) {
        throw new AssertionError();
      }
    }
    char[] arrayOfChar = paramPasswordAuthentication.getPassword();
    byte[] arrayOfByte2 = new byte[arrayOfChar.length];
    for (int i = 0; i < arrayOfChar.length; i++) {
      arrayOfByte2[i] = ((byte)arrayOfChar[i]);
    }
    byte[] arrayOfByte3 = new byte[arrayOfByte1.length + arrayOfByte2.length];
    System.arraycopy(arrayOfByte1, 0, arrayOfByte3, 0, arrayOfByte1.length);
    System.arraycopy(arrayOfByte2, 0, arrayOfByte3, arrayOfByte1.length, arrayOfByte2.length);
    auth = ("Basic " + Base64.getEncoder().encodeToString(arrayOfByte3));
    pw = paramPasswordAuthentication;
  }
  
  public BasicAuthentication(boolean paramBoolean, URL paramURL, String paramString1, String paramString2)
  {
    super(paramBoolean ? 'p' : 's', AuthScheme.BASIC, paramURL, paramString1);
    auth = ("Basic " + paramString2);
  }
  
  public boolean supportsPreemptiveAuthorization()
  {
    return true;
  }
  
  public boolean setHeaders(HttpURLConnection paramHttpURLConnection, HeaderParser paramHeaderParser, String paramString)
  {
    paramHttpURLConnection.setAuthenticationProperty(getHeaderName(), getHeaderValue(null, null));
    return true;
  }
  
  public String getHeaderValue(URL paramURL, String paramString)
  {
    return auth;
  }
  
  public boolean isAuthorizationStale(String paramString)
  {
    return false;
  }
  
  static String getRootPath(String paramString1, String paramString2)
  {
    int i = 0;
    try
    {
      paramString1 = new URI(paramString1).normalize().getPath();
      paramString2 = new URI(paramString2).normalize().getPath();
    }
    catch (URISyntaxException localURISyntaxException) {}
    while (i < paramString2.length())
    {
      int j = paramString2.indexOf('/', i + 1);
      if ((j != -1) && (paramString2.regionMatches(0, paramString1, 0, j + 1))) {
        i = j;
      } else {
        return paramString2.substring(0, i + 1);
      }
    }
    return paramString1;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\www\protocol\http\BasicAuthentication.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */