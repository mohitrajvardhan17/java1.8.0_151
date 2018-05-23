package sun.net.www.protocol.http;

public abstract interface AuthCache
{
  public abstract void put(String paramString, AuthCacheValue paramAuthCacheValue);
  
  public abstract AuthCacheValue get(String paramString1, String paramString2);
  
  public abstract void remove(String paramString, AuthCacheValue paramAuthCacheValue);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\www\protocol\http\AuthCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */