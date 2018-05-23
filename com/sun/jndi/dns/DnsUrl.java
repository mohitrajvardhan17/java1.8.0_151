package com.sun.jndi.dns;

import com.sun.jndi.toolkit.url.Uri;
import com.sun.jndi.toolkit.url.UrlUtil;
import java.net.MalformedURLException;
import java.util.StringTokenizer;

public class DnsUrl
  extends Uri
{
  private String domain;
  
  public static DnsUrl[] fromList(String paramString)
    throws MalformedURLException
  {
    DnsUrl[] arrayOfDnsUrl1 = new DnsUrl[(paramString.length() + 1) / 2];
    int i = 0;
    StringTokenizer localStringTokenizer = new StringTokenizer(paramString, " ");
    while (localStringTokenizer.hasMoreTokens()) {
      arrayOfDnsUrl1[(i++)] = new DnsUrl(localStringTokenizer.nextToken());
    }
    DnsUrl[] arrayOfDnsUrl2 = new DnsUrl[i];
    System.arraycopy(arrayOfDnsUrl1, 0, arrayOfDnsUrl2, 0, i);
    return arrayOfDnsUrl2;
  }
  
  public DnsUrl(String paramString)
    throws MalformedURLException
  {
    super(paramString);
    if (!scheme.equals("dns")) {
      throw new MalformedURLException(paramString + " is not a valid DNS pseudo-URL");
    }
    domain = (path.startsWith("/") ? path.substring(1) : path);
    domain = (domain.equals("") ? "." : UrlUtil.decode(domain));
  }
  
  public String getDomain()
  {
    return domain;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\dns\DnsUrl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */