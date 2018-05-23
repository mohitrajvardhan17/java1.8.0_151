package sun.net.www.protocol.http;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;

public class AuthCacheImpl
  implements AuthCache
{
  HashMap<String, LinkedList<AuthCacheValue>> hashtable = new HashMap();
  
  public AuthCacheImpl() {}
  
  public void setMap(HashMap<String, LinkedList<AuthCacheValue>> paramHashMap)
  {
    hashtable = paramHashMap;
  }
  
  public synchronized void put(String paramString, AuthCacheValue paramAuthCacheValue)
  {
    LinkedList localLinkedList = (LinkedList)hashtable.get(paramString);
    String str = paramAuthCacheValue.getPath();
    if (localLinkedList == null)
    {
      localLinkedList = new LinkedList();
      hashtable.put(paramString, localLinkedList);
    }
    ListIterator localListIterator = localLinkedList.listIterator();
    while (localListIterator.hasNext())
    {
      AuthenticationInfo localAuthenticationInfo = (AuthenticationInfo)localListIterator.next();
      if ((path == null) || (path.startsWith(str))) {
        localListIterator.remove();
      }
    }
    localListIterator.add(paramAuthCacheValue);
  }
  
  public synchronized AuthCacheValue get(String paramString1, String paramString2)
  {
    Object localObject = null;
    LinkedList localLinkedList = (LinkedList)hashtable.get(paramString1);
    if ((localLinkedList == null) || (localLinkedList.size() == 0)) {
      return null;
    }
    if (paramString2 == null) {
      return (AuthenticationInfo)localLinkedList.get(0);
    }
    ListIterator localListIterator = localLinkedList.listIterator();
    while (localListIterator.hasNext())
    {
      AuthenticationInfo localAuthenticationInfo = (AuthenticationInfo)localListIterator.next();
      if (paramString2.startsWith(path)) {
        return localAuthenticationInfo;
      }
    }
    return null;
  }
  
  public synchronized void remove(String paramString, AuthCacheValue paramAuthCacheValue)
  {
    LinkedList localLinkedList = (LinkedList)hashtable.get(paramString);
    if (localLinkedList == null) {
      return;
    }
    if (paramAuthCacheValue == null)
    {
      localLinkedList.clear();
      return;
    }
    ListIterator localListIterator = localLinkedList.listIterator();
    while (localListIterator.hasNext())
    {
      AuthenticationInfo localAuthenticationInfo = (AuthenticationInfo)localListIterator.next();
      if (paramAuthCacheValue.equals(localAuthenticationInfo)) {
        localListIterator.remove();
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\www\protocol\http\AuthCacheImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */