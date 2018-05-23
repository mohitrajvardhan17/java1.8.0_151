package java.net;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import sun.util.logging.PlatformLogger;
import sun.util.logging.PlatformLogger.Level;

public class CookieManager
  extends CookieHandler
{
  private CookiePolicy policyCallback;
  private CookieStore cookieJar = null;
  
  public CookieManager()
  {
    this(null, null);
  }
  
  public CookieManager(CookieStore paramCookieStore, CookiePolicy paramCookiePolicy)
  {
    policyCallback = (paramCookiePolicy == null ? CookiePolicy.ACCEPT_ORIGINAL_SERVER : paramCookiePolicy);
    if (paramCookieStore == null) {
      cookieJar = new InMemoryCookieStore();
    } else {
      cookieJar = paramCookieStore;
    }
  }
  
  public void setCookiePolicy(CookiePolicy paramCookiePolicy)
  {
    if (paramCookiePolicy != null) {
      policyCallback = paramCookiePolicy;
    }
  }
  
  public CookieStore getCookieStore()
  {
    return cookieJar;
  }
  
  public Map<String, List<String>> get(URI paramURI, Map<String, List<String>> paramMap)
    throws IOException
  {
    if ((paramURI == null) || (paramMap == null)) {
      throw new IllegalArgumentException("Argument is null");
    }
    HashMap localHashMap = new HashMap();
    if (cookieJar == null) {
      return Collections.unmodifiableMap(localHashMap);
    }
    boolean bool = "https".equalsIgnoreCase(paramURI.getScheme());
    ArrayList localArrayList = new ArrayList();
    String str1 = paramURI.getPath();
    if ((str1 == null) || (str1.isEmpty())) {
      str1 = "/";
    }
    Object localObject = cookieJar.get(paramURI).iterator();
    while (((Iterator)localObject).hasNext())
    {
      HttpCookie localHttpCookie = (HttpCookie)((Iterator)localObject).next();
      if ((pathMatches(str1, localHttpCookie.getPath())) && ((bool) || (!localHttpCookie.getSecure())))
      {
        String str2;
        if (localHttpCookie.isHttpOnly())
        {
          str2 = paramURI.getScheme();
          if ((!"http".equalsIgnoreCase(str2)) && (!"https".equalsIgnoreCase(str2))) {}
        }
        else
        {
          str2 = localHttpCookie.getPortlist();
          if ((str2 != null) && (!str2.isEmpty()))
          {
            int i = paramURI.getPort();
            if (i == -1) {
              i = "https".equals(paramURI.getScheme()) ? 443 : 80;
            }
            if (isInPortList(str2, i)) {
              localArrayList.add(localHttpCookie);
            }
          }
          else
          {
            localArrayList.add(localHttpCookie);
          }
        }
      }
    }
    localObject = sortByPath(localArrayList);
    localHashMap.put("Cookie", localObject);
    return Collections.unmodifiableMap(localHashMap);
  }
  
  public void put(URI paramURI, Map<String, List<String>> paramMap)
    throws IOException
  {
    if ((paramURI == null) || (paramMap == null)) {
      throw new IllegalArgumentException("Argument is null");
    }
    if (cookieJar == null) {
      return;
    }
    PlatformLogger localPlatformLogger = PlatformLogger.getLogger("java.net.CookieManager");
    Iterator localIterator1 = paramMap.keySet().iterator();
    while (localIterator1.hasNext())
    {
      String str1 = (String)localIterator1.next();
      if ((str1 != null) && ((str1.equalsIgnoreCase("Set-Cookie2")) || (str1.equalsIgnoreCase("Set-Cookie"))))
      {
        Iterator localIterator2 = ((List)paramMap.get(str1)).iterator();
        while (localIterator2.hasNext())
        {
          String str2 = (String)localIterator2.next();
          try
          {
            List localList;
            try
            {
              localList = HttpCookie.parse(str2);
            }
            catch (IllegalArgumentException localIllegalArgumentException2)
            {
              localList = Collections.emptyList();
              if (localPlatformLogger.isLoggable(PlatformLogger.Level.SEVERE)) {
                localPlatformLogger.severe("Invalid cookie for " + paramURI + ": " + str2);
              }
            }
            Iterator localIterator3 = localList.iterator();
            while (localIterator3.hasNext())
            {
              HttpCookie localHttpCookie = (HttpCookie)localIterator3.next();
              int i;
              if (localHttpCookie.getPath() == null)
              {
                str3 = paramURI.getPath();
                if (!str3.endsWith("/"))
                {
                  i = str3.lastIndexOf("/");
                  if (i > 0) {
                    str3 = str3.substring(0, i + 1);
                  } else {
                    str3 = "/";
                  }
                }
                localHttpCookie.setPath(str3);
              }
              if (localHttpCookie.getDomain() == null)
              {
                str3 = paramURI.getHost();
                if ((str3 != null) && (!str3.contains("."))) {
                  str3 = str3 + ".local";
                }
                localHttpCookie.setDomain(str3);
              }
              String str3 = localHttpCookie.getPortlist();
              if (str3 != null)
              {
                i = paramURI.getPort();
                if (i == -1) {
                  i = "https".equals(paramURI.getScheme()) ? 443 : 80;
                }
                if (str3.isEmpty())
                {
                  localHttpCookie.setPortlist("" + i);
                  if (shouldAcceptInternal(paramURI, localHttpCookie)) {
                    cookieJar.add(paramURI, localHttpCookie);
                  }
                }
                else if ((isInPortList(str3, i)) && (shouldAcceptInternal(paramURI, localHttpCookie)))
                {
                  cookieJar.add(paramURI, localHttpCookie);
                }
              }
              else if (shouldAcceptInternal(paramURI, localHttpCookie))
              {
                cookieJar.add(paramURI, localHttpCookie);
              }
            }
          }
          catch (IllegalArgumentException localIllegalArgumentException1) {}
        }
      }
    }
  }
  
  private boolean shouldAcceptInternal(URI paramURI, HttpCookie paramHttpCookie)
  {
    try
    {
      return policyCallback.shouldAccept(paramURI, paramHttpCookie);
    }
    catch (Exception localException) {}
    return false;
  }
  
  private static boolean isInPortList(String paramString, int paramInt)
  {
    int i = paramString.indexOf(",");
    int j = -1;
    while (i > 0)
    {
      try
      {
        j = Integer.parseInt(paramString.substring(0, i));
        if (j == paramInt) {
          return true;
        }
      }
      catch (NumberFormatException localNumberFormatException1) {}
      paramString = paramString.substring(i + 1);
      i = paramString.indexOf(",");
    }
    if (!paramString.isEmpty()) {
      try
      {
        j = Integer.parseInt(paramString);
        if (j == paramInt) {
          return true;
        }
      }
      catch (NumberFormatException localNumberFormatException2) {}
    }
    return false;
  }
  
  private boolean pathMatches(String paramString1, String paramString2)
  {
    if (paramString1 == paramString2) {
      return true;
    }
    if ((paramString1 == null) || (paramString2 == null)) {
      return false;
    }
    return paramString1.startsWith(paramString2);
  }
  
  private List<String> sortByPath(List<HttpCookie> paramList)
  {
    Collections.sort(paramList, new CookiePathComparator());
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      HttpCookie localHttpCookie = (HttpCookie)localIterator.next();
      if ((paramList.indexOf(localHttpCookie) == 0) && (localHttpCookie.getVersion() > 0)) {
        localArrayList.add("$Version=\"1\"");
      }
      localArrayList.add(localHttpCookie.toString());
    }
    return localArrayList;
  }
  
  static class CookiePathComparator
    implements Comparator<HttpCookie>
  {
    CookiePathComparator() {}
    
    public int compare(HttpCookie paramHttpCookie1, HttpCookie paramHttpCookie2)
    {
      if (paramHttpCookie1 == paramHttpCookie2) {
        return 0;
      }
      if (paramHttpCookie1 == null) {
        return -1;
      }
      if (paramHttpCookie2 == null) {
        return 1;
      }
      if (!paramHttpCookie1.getName().equals(paramHttpCookie2.getName())) {
        return 0;
      }
      if (paramHttpCookie1.getPath().startsWith(paramHttpCookie2.getPath())) {
        return -1;
      }
      if (paramHttpCookie2.getPath().startsWith(paramHttpCookie1.getPath())) {
        return 1;
      }
      return 0;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\net\CookieManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */