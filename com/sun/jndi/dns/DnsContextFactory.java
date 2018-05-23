package com.sun.jndi.dns;

import com.sun.jndi.toolkit.url.UrlUtil;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import javax.naming.ConfigurationException;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;
import sun.net.dns.ResolverConfiguration;

public class DnsContextFactory
  implements InitialContextFactory
{
  private static final String DEFAULT_URL = "dns:";
  private static final int DEFAULT_PORT = 53;
  
  public DnsContextFactory() {}
  
  public Context getInitialContext(Hashtable<?, ?> paramHashtable)
    throws NamingException
  {
    if (paramHashtable == null) {
      paramHashtable = new Hashtable(5);
    }
    return urlToContext(getInitCtxUrl(paramHashtable), paramHashtable);
  }
  
  public static DnsContext getContext(String paramString, String[] paramArrayOfString, Hashtable<?, ?> paramHashtable)
    throws NamingException
  {
    return new DnsContext(paramString, paramArrayOfString, paramHashtable);
  }
  
  public static DnsContext getContext(String paramString, DnsUrl[] paramArrayOfDnsUrl, Hashtable<?, ?> paramHashtable)
    throws NamingException
  {
    String[] arrayOfString = serversForUrls(paramArrayOfDnsUrl);
    DnsContext localDnsContext = getContext(paramString, arrayOfString, paramHashtable);
    if (platformServersUsed(paramArrayOfDnsUrl)) {
      localDnsContext.setProviderUrl(constructProviderUrl(paramString, arrayOfString));
    }
    return localDnsContext;
  }
  
  public static boolean platformServersAvailable()
  {
    return !filterNameServers(ResolverConfiguration.open().nameservers(), true).isEmpty();
  }
  
  private static Context urlToContext(String paramString, Hashtable<?, ?> paramHashtable)
    throws NamingException
  {
    DnsUrl[] arrayOfDnsUrl;
    try
    {
      arrayOfDnsUrl = DnsUrl.fromList(paramString);
    }
    catch (MalformedURLException localMalformedURLException)
    {
      throw new ConfigurationException(localMalformedURLException.getMessage());
    }
    if (arrayOfDnsUrl.length == 0) {
      throw new ConfigurationException("Invalid DNS pseudo-URL(s): " + paramString);
    }
    String str = arrayOfDnsUrl[0].getDomain();
    for (int i = 1; i < arrayOfDnsUrl.length; i++) {
      if (!str.equalsIgnoreCase(arrayOfDnsUrl[i].getDomain())) {
        throw new ConfigurationException("Conflicting domains: " + paramString);
      }
    }
    return getContext(str, arrayOfDnsUrl, paramHashtable);
  }
  
  private static String[] serversForUrls(DnsUrl[] paramArrayOfDnsUrl)
    throws NamingException
  {
    if (paramArrayOfDnsUrl.length == 0) {
      throw new ConfigurationException("DNS pseudo-URL required");
    }
    ArrayList localArrayList = new ArrayList();
    for (int i = 0; i < paramArrayOfDnsUrl.length; i++)
    {
      String str = paramArrayOfDnsUrl[i].getHost();
      int j = paramArrayOfDnsUrl[i].getPort();
      if ((str == null) && (j < 0))
      {
        List localList = filterNameServers(ResolverConfiguration.open().nameservers(), false);
        if (!localList.isEmpty())
        {
          localArrayList.addAll(localList);
          continue;
        }
      }
      if (str == null) {
        str = "localhost";
      }
      localArrayList.add(str + ":" + j);
    }
    return (String[])localArrayList.toArray(new String[localArrayList.size()]);
  }
  
  private static boolean platformServersUsed(DnsUrl[] paramArrayOfDnsUrl)
  {
    if (!platformServersAvailable()) {
      return false;
    }
    for (int i = 0; i < paramArrayOfDnsUrl.length; i++) {
      if ((paramArrayOfDnsUrl[i].getHost() == null) && (paramArrayOfDnsUrl[i].getPort() < 0)) {
        return true;
      }
    }
    return false;
  }
  
  private static String constructProviderUrl(String paramString, String[] paramArrayOfString)
  {
    String str = "";
    if (!paramString.equals(".")) {
      try
      {
        str = "/" + UrlUtil.encode(paramString, "ISO-8859-1");
      }
      catch (UnsupportedEncodingException localUnsupportedEncodingException) {}
    }
    StringBuffer localStringBuffer = new StringBuffer();
    for (int i = 0; i < paramArrayOfString.length; i++)
    {
      if (i > 0) {
        localStringBuffer.append(' ');
      }
      localStringBuffer.append("dns://").append(paramArrayOfString[i]).append(str);
    }
    return localStringBuffer.toString();
  }
  
  private static String getInitCtxUrl(Hashtable<?, ?> paramHashtable)
  {
    String str = (String)paramHashtable.get("java.naming.provider.url");
    return str != null ? str : "dns:";
  }
  
  private static List<String> filterNameServers(List<String> paramList, boolean paramBoolean)
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if ((localSecurityManager == null) || (paramList == null) || (paramList.isEmpty())) {
      return paramList;
    }
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      String str1 = (String)localIterator.next();
      int i = str1.indexOf(':', str1.indexOf(93) + 1);
      int j = i < 0 ? 53 : Integer.parseInt(str1.substring(i + 1));
      String str2 = i < 0 ? str1 : str1.substring(0, i);
      try
      {
        localSecurityManager.checkConnect(str2, j);
        localArrayList.add(str1);
        if (paramBoolean) {
          return localArrayList;
        }
      }
      catch (SecurityException localSecurityException) {}
    }
    return localArrayList;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\dns\DnsContextFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */