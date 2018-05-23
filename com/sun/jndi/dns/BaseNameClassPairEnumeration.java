package com.sun.jndi.dns;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.NoSuchElementException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

abstract class BaseNameClassPairEnumeration<T>
  implements NamingEnumeration<T>
{
  protected Enumeration<NameNode> nodes;
  protected DnsContext ctx;
  
  BaseNameClassPairEnumeration(DnsContext paramDnsContext, Hashtable<String, NameNode> paramHashtable)
  {
    ctx = paramDnsContext;
    nodes = (paramHashtable != null ? paramHashtable.elements() : null);
  }
  
  public final void close()
  {
    nodes = null;
    ctx = null;
  }
  
  public final boolean hasMore()
  {
    boolean bool = (nodes != null) && (nodes.hasMoreElements());
    if (!bool) {
      close();
    }
    return bool;
  }
  
  public final boolean hasMoreElements()
  {
    return hasMore();
  }
  
  public abstract T next()
    throws NamingException;
  
  public final T nextElement()
  {
    try
    {
      return (T)next();
    }
    catch (NamingException localNamingException)
    {
      NoSuchElementException localNoSuchElementException = new NoSuchElementException();
      localNoSuchElementException.initCause(localNamingException);
      throw localNoSuchElementException;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\dns\BaseNameClassPairEnumeration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */