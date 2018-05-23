package com.sun.jndi.cosnaming;

import com.sun.jndi.toolkit.url.UrlUtil;
import java.net.MalformedURLException;
import javax.naming.Name;
import javax.naming.NamingException;

public final class CorbanameUrl
{
  private String stringName;
  private String location;
  
  public String getStringName()
  {
    return stringName;
  }
  
  public Name getCosName()
    throws NamingException
  {
    return CNCtx.parser.parse(stringName);
  }
  
  public String getLocation()
  {
    return "corbaloc:" + location;
  }
  
  public CorbanameUrl(String paramString)
    throws MalformedURLException
  {
    if (!paramString.startsWith("corbaname:")) {
      throw new MalformedURLException("Invalid corbaname URL: " + paramString);
    }
    int i = 10;
    int j = paramString.indexOf('#', i);
    if (j < 0)
    {
      j = paramString.length();
      stringName = "";
    }
    else
    {
      stringName = UrlUtil.decode(paramString.substring(j + 1));
    }
    location = paramString.substring(i, j);
    int k = location.indexOf("/");
    if (k >= 0)
    {
      if (k == location.length() - 1) {
        location += "NameService";
      }
    }
    else {
      location += "/NameService";
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\cosnaming\CorbanameUrl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */