package sun.net.httpserver;

import java.util.Iterator;
import java.util.LinkedList;

class ContextList
{
  static final int MAX_CONTEXTS = 50;
  LinkedList<HttpContextImpl> list = new LinkedList();
  
  ContextList() {}
  
  public synchronized void add(HttpContextImpl paramHttpContextImpl)
  {
    assert (paramHttpContextImpl.getPath() != null);
    list.add(paramHttpContextImpl);
  }
  
  public synchronized int size()
  {
    return list.size();
  }
  
  synchronized HttpContextImpl findContext(String paramString1, String paramString2)
  {
    return findContext(paramString1, paramString2, false);
  }
  
  synchronized HttpContextImpl findContext(String paramString1, String paramString2, boolean paramBoolean)
  {
    paramString1 = paramString1.toLowerCase();
    Object localObject1 = "";
    Object localObject2 = null;
    Iterator localIterator = list.iterator();
    while (localIterator.hasNext())
    {
      HttpContextImpl localHttpContextImpl = (HttpContextImpl)localIterator.next();
      if (localHttpContextImpl.getProtocol().equals(paramString1))
      {
        String str = localHttpContextImpl.getPath();
        if (((!paramBoolean) || (str.equals(paramString2))) && ((paramBoolean) || (paramString2.startsWith(str)))) {
          if (str.length() > ((String)localObject1).length())
          {
            localObject1 = str;
            localObject2 = localHttpContextImpl;
          }
        }
      }
    }
    return (HttpContextImpl)localObject2;
  }
  
  public synchronized void remove(String paramString1, String paramString2)
    throws IllegalArgumentException
  {
    HttpContextImpl localHttpContextImpl = findContext(paramString1, paramString2, true);
    if (localHttpContextImpl == null) {
      throw new IllegalArgumentException("cannot remove element from list");
    }
    list.remove(localHttpContextImpl);
  }
  
  public synchronized void remove(HttpContextImpl paramHttpContextImpl)
    throws IllegalArgumentException
  {
    Iterator localIterator = list.iterator();
    while (localIterator.hasNext())
    {
      HttpContextImpl localHttpContextImpl = (HttpContextImpl)localIterator.next();
      if (localHttpContextImpl.equals(paramHttpContextImpl))
      {
        list.remove(localHttpContextImpl);
        return;
      }
    }
    throw new IllegalArgumentException("no such context in list");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\net\httpserver\ContextList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */