package sun.security.krb5.internal.rcache;

import java.io.PrintStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import sun.security.krb5.internal.KerberosTime;
import sun.security.krb5.internal.Krb5;
import sun.security.krb5.internal.KrbApErrException;
import sun.security.krb5.internal.ReplayCache;

public class MemoryCache
  extends ReplayCache
{
  private static final int lifespan = ;
  private static final boolean DEBUG = Krb5.DEBUG;
  private final Map<String, AuthList> content = new HashMap();
  
  public MemoryCache() {}
  
  public synchronized void checkAndStore(KerberosTime paramKerberosTime, AuthTimeWithHash paramAuthTimeWithHash)
    throws KrbApErrException
  {
    String str = client + "|" + server;
    AuthList localAuthList = (AuthList)content.get(str);
    if (DEBUG) {
      System.out.println("MemoryCache: add " + paramAuthTimeWithHash + " to " + str);
    }
    if (localAuthList == null)
    {
      localAuthList = new AuthList(lifespan);
      localAuthList.put(paramAuthTimeWithHash, paramKerberosTime);
      if (!localAuthList.isEmpty()) {
        content.put(str, localAuthList);
      }
    }
    else
    {
      if (DEBUG) {
        System.out.println("MemoryCache: Existing AuthList:\n" + localAuthList);
      }
      localAuthList.put(paramAuthTimeWithHash, paramKerberosTime);
      if (localAuthList.isEmpty()) {
        content.remove(str);
      }
    }
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    Iterator localIterator = content.values().iterator();
    while (localIterator.hasNext())
    {
      AuthList localAuthList = (AuthList)localIterator.next();
      localStringBuilder.append(localAuthList.toString());
    }
    return localStringBuilder.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\krb5\internal\rcache\MemoryCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */