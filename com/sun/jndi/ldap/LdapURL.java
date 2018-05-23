package com.sun.jndi.ldap;

import com.sun.jndi.toolkit.url.Uri;
import com.sun.jndi.toolkit.url.UrlUtil;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.StringTokenizer;
import javax.naming.NamingException;

public final class LdapURL
  extends Uri
{
  private boolean useSsl = false;
  private String DN = null;
  private String attributes = null;
  private String scope = null;
  private String filter = null;
  private String extensions = null;
  
  public LdapURL(String paramString)
    throws NamingException
  {
    try
    {
      init(paramString);
      useSsl = scheme.equalsIgnoreCase("ldaps");
      if ((!scheme.equalsIgnoreCase("ldap")) && (!useSsl)) {
        throw new MalformedURLException("Not an LDAP URL: " + paramString);
      }
      parsePathAndQuery();
    }
    catch (MalformedURLException localMalformedURLException)
    {
      localNamingException = new NamingException("Cannot parse url: " + paramString);
      localNamingException.setRootCause(localMalformedURLException);
      throw localNamingException;
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      NamingException localNamingException = new NamingException("Cannot parse url: " + paramString);
      localNamingException.setRootCause(localUnsupportedEncodingException);
      throw localNamingException;
    }
  }
  
  public boolean useSsl()
  {
    return useSsl;
  }
  
  public String getDN()
  {
    return DN;
  }
  
  public String getAttributes()
  {
    return attributes;
  }
  
  public String getScope()
  {
    return scope;
  }
  
  public String getFilter()
  {
    return filter;
  }
  
  public String getExtensions()
  {
    return extensions;
  }
  
  public static String[] fromList(String paramString)
    throws NamingException
  {
    String[] arrayOfString1 = new String[(paramString.length() + 1) / 2];
    int i = 0;
    StringTokenizer localStringTokenizer = new StringTokenizer(paramString, " ");
    while (localStringTokenizer.hasMoreTokens()) {
      arrayOfString1[(i++)] = localStringTokenizer.nextToken();
    }
    String[] arrayOfString2 = new String[i];
    System.arraycopy(arrayOfString1, 0, arrayOfString2, 0, i);
    return arrayOfString2;
  }
  
  public static boolean hasQueryComponents(String paramString)
  {
    return paramString.lastIndexOf('?') != -1;
  }
  
  static String toUrlString(String paramString1, int paramInt, String paramString2, boolean paramBoolean)
  {
    try
    {
      String str1 = paramString1 != null ? paramString1 : "";
      if ((str1.indexOf(':') != -1) && (str1.charAt(0) != '[')) {
        str1 = "[" + str1 + "]";
      }
      String str2 = paramInt != -1 ? ":" + paramInt : "";
      String str3 = paramString2 != null ? "/" + UrlUtil.encode(paramString2, "UTF8") : "";
      return "ldap://" + str1 + str2 + str3;
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      throw new IllegalStateException("UTF-8 encoding unavailable");
    }
  }
  
  private void parsePathAndQuery()
    throws MalformedURLException, UnsupportedEncodingException
  {
    if (path.equals("")) {
      return;
    }
    DN = (path.startsWith("/") ? path.substring(1) : path);
    if (DN.length() > 0) {
      DN = UrlUtil.decode(DN, "UTF8");
    }
    if ((query == null) || (query.length() < 2)) {
      return;
    }
    int i = 1;
    int j = query.indexOf('?', i);
    int k = j == -1 ? query.length() : j;
    if (k - i > 0) {
      attributes = query.substring(i, k);
    }
    i = k + 1;
    if (i >= query.length()) {
      return;
    }
    j = query.indexOf('?', i);
    k = j == -1 ? query.length() : j;
    if (k - i > 0) {
      scope = query.substring(i, k);
    }
    i = k + 1;
    if (i >= query.length()) {
      return;
    }
    j = query.indexOf('?', i);
    k = j == -1 ? query.length() : j;
    if (k - i > 0)
    {
      filter = query.substring(i, k);
      filter = UrlUtil.decode(filter, "UTF8");
    }
    i = k + 1;
    if (i >= query.length()) {
      return;
    }
    if (query.length() - i > 0)
    {
      extensions = query.substring(i);
      extensions = UrlUtil.decode(extensions, "UTF8");
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\ldap\LdapURL.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */