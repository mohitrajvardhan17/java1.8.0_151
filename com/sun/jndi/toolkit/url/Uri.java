package com.sun.jndi.toolkit.url;

import java.net.MalformedURLException;

public class Uri
{
  protected String uri;
  protected String scheme;
  protected String host = null;
  protected int port = -1;
  protected boolean hasAuthority;
  protected String path;
  protected String query = null;
  
  public Uri(String paramString)
    throws MalformedURLException
  {
    init(paramString);
  }
  
  protected Uri() {}
  
  protected void init(String paramString)
    throws MalformedURLException
  {
    uri = paramString;
    parse(paramString);
  }
  
  public String getScheme()
  {
    return scheme;
  }
  
  public String getHost()
  {
    return host;
  }
  
  public int getPort()
  {
    return port;
  }
  
  public String getPath()
  {
    return path;
  }
  
  public String getQuery()
  {
    return query;
  }
  
  public String toString()
  {
    return uri;
  }
  
  private void parse(String paramString)
    throws MalformedURLException
  {
    int i = paramString.indexOf(':');
    if (i < 0) {
      throw new MalformedURLException("Invalid URI: " + paramString);
    }
    scheme = paramString.substring(0, i);
    i++;
    hasAuthority = paramString.startsWith("//", i);
    if (hasAuthority)
    {
      i += 2;
      j = paramString.indexOf('/', i);
      if (j < 0) {
        j = paramString.length();
      }
      int k;
      if (paramString.startsWith("[", i))
      {
        k = paramString.indexOf(']', i + 1);
        if ((k < 0) || (k > j)) {
          throw new MalformedURLException("Invalid URI: " + paramString);
        }
        host = paramString.substring(i, k + 1);
        i = k + 1;
      }
      else
      {
        k = paramString.indexOf(':', i);
        int m = (k < 0) || (k > j) ? j : k;
        if (i < m) {
          host = paramString.substring(i, m);
        }
        i = m;
      }
      if ((i + 1 < j) && (paramString.startsWith(":", i)))
      {
        i++;
        port = Integer.parseInt(paramString.substring(i, j));
      }
      i = j;
    }
    int j = paramString.indexOf('?', i);
    if (j < 0)
    {
      path = paramString.substring(i);
    }
    else
    {
      path = paramString.substring(i, j);
      query = paramString.substring(j);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\toolkit\url\Uri.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */