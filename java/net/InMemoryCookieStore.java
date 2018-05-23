package java.net;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

class InMemoryCookieStore
  implements CookieStore
{
  private List<HttpCookie> cookieJar = null;
  private Map<String, List<HttpCookie>> domainIndex = null;
  private Map<URI, List<HttpCookie>> uriIndex = null;
  private ReentrantLock lock = null;
  
  public InMemoryCookieStore() {}
  
  /* Error */
  public void add(URI paramURI, HttpCookie paramHttpCookie)
  {
    // Byte code:
    //   0: aload_2
    //   1: ifnonnull +13 -> 14
    //   4: new 97	java/lang/NullPointerException
    //   7: dup
    //   8: ldc 2
    //   10: invokespecial 197	java/lang/NullPointerException:<init>	(Ljava/lang/String;)V
    //   13: athrow
    //   14: aload_0
    //   15: getfield 196	java/net/InMemoryCookieStore:lock	Ljava/util/concurrent/locks/ReentrantLock;
    //   18: invokevirtual 226	java/util/concurrent/locks/ReentrantLock:lock	()V
    //   21: aload_0
    //   22: getfield 193	java/net/InMemoryCookieStore:cookieJar	Ljava/util/List;
    //   25: aload_2
    //   26: invokeinterface 239 2 0
    //   31: pop
    //   32: aload_2
    //   33: invokevirtual 207	java/net/HttpCookie:getMaxAge	()J
    //   36: lconst_0
    //   37: lcmp
    //   38: ifeq +52 -> 90
    //   41: aload_0
    //   42: getfield 193	java/net/InMemoryCookieStore:cookieJar	Ljava/util/List;
    //   45: aload_2
    //   46: invokeinterface 237 2 0
    //   51: pop
    //   52: aload_2
    //   53: invokevirtual 210	java/net/HttpCookie:getDomain	()Ljava/lang/String;
    //   56: ifnull +16 -> 72
    //   59: aload_0
    //   60: aload_0
    //   61: getfield 194	java/net/InMemoryCookieStore:domainIndex	Ljava/util/Map;
    //   64: aload_2
    //   65: invokevirtual 210	java/net/HttpCookie:getDomain	()Ljava/lang/String;
    //   68: aload_2
    //   69: invokespecial 216	java/net/InMemoryCookieStore:addIndex	(Ljava/util/Map;Ljava/lang/Object;Ljava/net/HttpCookie;)V
    //   72: aload_1
    //   73: ifnull +17 -> 90
    //   76: aload_0
    //   77: aload_0
    //   78: getfield 195	java/net/InMemoryCookieStore:uriIndex	Ljava/util/Map;
    //   81: aload_0
    //   82: aload_1
    //   83: invokespecial 213	java/net/InMemoryCookieStore:getEffectiveURI	(Ljava/net/URI;)Ljava/net/URI;
    //   86: aload_2
    //   87: invokespecial 216	java/net/InMemoryCookieStore:addIndex	(Ljava/util/Map;Ljava/lang/Object;Ljava/net/HttpCookie;)V
    //   90: aload_0
    //   91: getfield 196	java/net/InMemoryCookieStore:lock	Ljava/util/concurrent/locks/ReentrantLock;
    //   94: invokevirtual 227	java/util/concurrent/locks/ReentrantLock:unlock	()V
    //   97: goto +13 -> 110
    //   100: astore_3
    //   101: aload_0
    //   102: getfield 196	java/net/InMemoryCookieStore:lock	Ljava/util/concurrent/locks/ReentrantLock;
    //   105: invokevirtual 227	java/util/concurrent/locks/ReentrantLock:unlock	()V
    //   108: aload_3
    //   109: athrow
    //   110: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	111	0	this	InMemoryCookieStore
    //   0	111	1	paramURI	URI
    //   0	111	2	paramHttpCookie	HttpCookie
    //   100	9	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   21	90	100	finally
  }
  
  public List<HttpCookie> get(URI paramURI)
  {
    if (paramURI == null) {
      throw new NullPointerException("uri is null");
    }
    ArrayList localArrayList = new ArrayList();
    boolean bool = "https".equalsIgnoreCase(paramURI.getScheme());
    lock.lock();
    try
    {
      getInternal1(localArrayList, domainIndex, paramURI.getHost(), bool);
      getInternal2(localArrayList, uriIndex, getEffectiveURI(paramURI), bool);
    }
    finally
    {
      lock.unlock();
    }
    return localArrayList;
  }
  
  /* Error */
  public List<HttpCookie> getCookies()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 196	java/net/InMemoryCookieStore:lock	Ljava/util/concurrent/locks/ReentrantLock;
    //   4: invokevirtual 226	java/util/concurrent/locks/ReentrantLock:lock	()V
    //   7: aload_0
    //   8: getfield 193	java/net/InMemoryCookieStore:cookieJar	Ljava/util/List;
    //   11: invokeinterface 241 1 0
    //   16: astore_2
    //   17: aload_2
    //   18: invokeinterface 231 1 0
    //   23: ifeq +27 -> 50
    //   26: aload_2
    //   27: invokeinterface 232 1 0
    //   32: checkcast 102	java/net/HttpCookie
    //   35: invokevirtual 209	java/net/HttpCookie:hasExpired	()Z
    //   38: ifeq -21 -> 17
    //   41: aload_2
    //   42: invokeinterface 230 1 0
    //   47: goto -30 -> 17
    //   50: aload_0
    //   51: getfield 193	java/net/InMemoryCookieStore:cookieJar	Ljava/util/List;
    //   54: invokestatic 224	java/util/Collections:unmodifiableList	(Ljava/util/List;)Ljava/util/List;
    //   57: astore_1
    //   58: aload_0
    //   59: getfield 196	java/net/InMemoryCookieStore:lock	Ljava/util/concurrent/locks/ReentrantLock;
    //   62: invokevirtual 227	java/util/concurrent/locks/ReentrantLock:unlock	()V
    //   65: goto +21 -> 86
    //   68: astore_3
    //   69: aload_0
    //   70: getfield 193	java/net/InMemoryCookieStore:cookieJar	Ljava/util/List;
    //   73: invokestatic 224	java/util/Collections:unmodifiableList	(Ljava/util/List;)Ljava/util/List;
    //   76: astore_1
    //   77: aload_0
    //   78: getfield 196	java/net/InMemoryCookieStore:lock	Ljava/util/concurrent/locks/ReentrantLock;
    //   81: invokevirtual 227	java/util/concurrent/locks/ReentrantLock:unlock	()V
    //   84: aload_3
    //   85: athrow
    //   86: aload_1
    //   87: areturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	88	0	this	InMemoryCookieStore
    //   57	30	1	localList	List
    //   16	26	2	localIterator	Iterator
    //   68	17	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   7	50	68	finally
  }
  
  public List<URI> getURIs()
  {
    ArrayList localArrayList = new ArrayList();
    lock.lock();
    try
    {
      Iterator localIterator = uriIndex.keySet().iterator();
      while (localIterator.hasNext())
      {
        URI localURI = (URI)localIterator.next();
        List localList = (List)uriIndex.get(localURI);
        if ((localList == null) || (localList.size() == 0)) {
          localIterator.remove();
        }
      }
    }
    finally
    {
      localArrayList.addAll(uriIndex.keySet());
      lock.unlock();
    }
    return localArrayList;
  }
  
  public boolean remove(URI paramURI, HttpCookie paramHttpCookie)
  {
    if (paramHttpCookie == null) {
      throw new NullPointerException("cookie is null");
    }
    boolean bool = false;
    lock.lock();
    try
    {
      bool = cookieJar.remove(paramHttpCookie);
    }
    finally
    {
      lock.unlock();
    }
    return bool;
  }
  
  public boolean removeAll()
  {
    lock.lock();
    try
    {
      if (cookieJar.isEmpty())
      {
        boolean bool = false;
        return bool;
      }
      cookieJar.clear();
      domainIndex.clear();
      uriIndex.clear();
    }
    finally
    {
      lock.unlock();
    }
    return true;
  }
  
  private boolean netscapeDomainMatches(String paramString1, String paramString2)
  {
    if ((paramString1 == null) || (paramString2 == null)) {
      return false;
    }
    boolean bool = ".local".equalsIgnoreCase(paramString1);
    int i = paramString1.indexOf('.');
    if (i == 0) {
      i = paramString1.indexOf('.', 1);
    }
    if ((!bool) && ((i == -1) || (i == paramString1.length() - 1))) {
      return false;
    }
    int j = paramString2.indexOf('.');
    if ((j == -1) && (bool)) {
      return true;
    }
    int k = paramString1.length();
    int m = paramString2.length() - k;
    if (m == 0) {
      return paramString2.equalsIgnoreCase(paramString1);
    }
    if (m > 0)
    {
      String str1 = paramString2.substring(0, m);
      String str2 = paramString2.substring(m);
      return str2.equalsIgnoreCase(paramString1);
    }
    if (m == -1) {
      return (paramString1.charAt(0) == '.') && (paramString2.equalsIgnoreCase(paramString1.substring(1)));
    }
    return false;
  }
  
  private void getInternal1(List<HttpCookie> paramList, Map<String, List<HttpCookie>> paramMap, String paramString, boolean paramBoolean)
  {
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator1 = paramMap.entrySet().iterator();
    while (localIterator1.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator1.next();
      String str = (String)localEntry.getKey();
      List localList = (List)localEntry.getValue();
      Iterator localIterator2 = localList.iterator();
      HttpCookie localHttpCookie;
      while (localIterator2.hasNext())
      {
        localHttpCookie = (HttpCookie)localIterator2.next();
        if (((localHttpCookie.getVersion() == 0) && (netscapeDomainMatches(str, paramString))) || ((localHttpCookie.getVersion() == 1) && (HttpCookie.domainMatches(str, paramString)))) {
          if (cookieJar.indexOf(localHttpCookie) != -1)
          {
            if (!localHttpCookie.hasExpired())
            {
              if (((paramBoolean) || (!localHttpCookie.getSecure())) && (!paramList.contains(localHttpCookie))) {
                paramList.add(localHttpCookie);
              }
            }
            else {
              localArrayList.add(localHttpCookie);
            }
          }
          else {
            localArrayList.add(localHttpCookie);
          }
        }
      }
      localIterator2 = localArrayList.iterator();
      while (localIterator2.hasNext())
      {
        localHttpCookie = (HttpCookie)localIterator2.next();
        localList.remove(localHttpCookie);
        cookieJar.remove(localHttpCookie);
      }
      localArrayList.clear();
    }
  }
  
  private <T> void getInternal2(List<HttpCookie> paramList, Map<T, List<HttpCookie>> paramMap, Comparable<T> paramComparable, boolean paramBoolean)
  {
    Iterator localIterator1 = paramMap.keySet().iterator();
    while (localIterator1.hasNext())
    {
      Object localObject = localIterator1.next();
      if (paramComparable.compareTo(localObject) == 0)
      {
        List localList = (List)paramMap.get(localObject);
        if (localList != null)
        {
          Iterator localIterator2 = localList.iterator();
          while (localIterator2.hasNext())
          {
            HttpCookie localHttpCookie = (HttpCookie)localIterator2.next();
            if (cookieJar.indexOf(localHttpCookie) != -1)
            {
              if (!localHttpCookie.hasExpired())
              {
                if (((paramBoolean) || (!localHttpCookie.getSecure())) && (!paramList.contains(localHttpCookie))) {
                  paramList.add(localHttpCookie);
                }
              }
              else
              {
                localIterator2.remove();
                cookieJar.remove(localHttpCookie);
              }
            }
            else {
              localIterator2.remove();
            }
          }
        }
      }
    }
  }
  
  private <T> void addIndex(Map<T, List<HttpCookie>> paramMap, T paramT, HttpCookie paramHttpCookie)
  {
    if (paramT != null)
    {
      Object localObject = (List)paramMap.get(paramT);
      if (localObject != null)
      {
        ((List)localObject).remove(paramHttpCookie);
        ((List)localObject).add(paramHttpCookie);
      }
      else
      {
        localObject = new ArrayList();
        ((List)localObject).add(paramHttpCookie);
        paramMap.put(paramT, localObject);
      }
    }
  }
  
  private URI getEffectiveURI(URI paramURI)
  {
    URI localURI = null;
    try
    {
      localURI = new URI("http", paramURI.getHost(), null, null, null);
    }
    catch (URISyntaxException localURISyntaxException)
    {
      localURI = paramURI;
    }
    return localURI;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\net\InMemoryCookieStore.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */