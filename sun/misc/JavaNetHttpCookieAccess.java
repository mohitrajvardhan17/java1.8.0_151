package sun.misc;

import java.net.HttpCookie;
import java.util.List;

public abstract interface JavaNetHttpCookieAccess
{
  public abstract List<HttpCookie> parse(String paramString);
  
  public abstract String header(HttpCookie paramHttpCookie);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\misc\JavaNetHttpCookieAccess.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */