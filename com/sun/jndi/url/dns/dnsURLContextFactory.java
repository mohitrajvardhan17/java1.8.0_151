package com.sun.jndi.url.dns;

import java.util.Hashtable;
import javax.naming.ConfigurationException;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.spi.ObjectFactory;

public class dnsURLContextFactory
  implements ObjectFactory
{
  public dnsURLContextFactory() {}
  
  public Object getObjectInstance(Object paramObject, Name paramName, Context paramContext, Hashtable<?, ?> paramHashtable)
    throws NamingException
  {
    if (paramObject == null) {
      return new dnsURLContext(paramHashtable);
    }
    if ((paramObject instanceof String)) {
      return getUsingURL((String)paramObject, paramHashtable);
    }
    if ((paramObject instanceof String[])) {
      return getUsingURLs((String[])paramObject, paramHashtable);
    }
    throw new ConfigurationException("dnsURLContextFactory.getObjectInstance: argument must be a DNS URL String or an array of them");
  }
  
  private static Object getUsingURL(String paramString, Hashtable<?, ?> paramHashtable)
    throws NamingException
  {
    dnsURLContext localdnsURLContext = new dnsURLContext(paramHashtable);
    try
    {
      Object localObject1 = localdnsURLContext.lookup(paramString);
      return localObject1;
    }
    finally
    {
      localdnsURLContext.close();
    }
  }
  
  private static Object getUsingURLs(String[] paramArrayOfString, Hashtable<?, ?> paramHashtable)
    throws NamingException
  {
    if (paramArrayOfString.length == 0) {
      throw new ConfigurationException("dnsURLContextFactory: empty URL array");
    }
    dnsURLContext localdnsURLContext = new dnsURLContext(paramHashtable);
    try
    {
      Object localObject1 = null;
      int i = 0;
      while (i < paramArrayOfString.length) {
        try
        {
          Object localObject2 = localdnsURLContext.lookup(paramArrayOfString[i]);
          return localObject2;
        }
        catch (NamingException localNamingException)
        {
          localObject1 = localNamingException;
          i++;
        }
      }
      throw ((Throwable)localObject1);
    }
    finally
    {
      localdnsURLContext.close();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\url\dns\dnsURLContextFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */