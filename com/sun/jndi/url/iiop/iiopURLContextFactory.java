package com.sun.jndi.url.iiop;

import com.sun.jndi.cosnaming.CNCtx;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.spi.ObjectFactory;
import javax.naming.spi.ResolveResult;

public class iiopURLContextFactory
  implements ObjectFactory
{
  public iiopURLContextFactory() {}
  
  public Object getObjectInstance(Object paramObject, Name paramName, Context paramContext, Hashtable<?, ?> paramHashtable)
    throws Exception
  {
    if (paramObject == null) {
      return new iiopURLContext(paramHashtable);
    }
    if ((paramObject instanceof String)) {
      return getUsingURL((String)paramObject, paramHashtable);
    }
    if ((paramObject instanceof String[])) {
      return getUsingURLs((String[])paramObject, paramHashtable);
    }
    throw new IllegalArgumentException("iiopURLContextFactory.getObjectInstance: argument must be a URL String or array of URLs");
  }
  
  static ResolveResult getUsingURLIgnoreRest(String paramString, Hashtable<?, ?> paramHashtable)
    throws NamingException
  {
    return CNCtx.createUsingURL(paramString, paramHashtable);
  }
  
  private static Object getUsingURL(String paramString, Hashtable<?, ?> paramHashtable)
    throws NamingException
  {
    ResolveResult localResolveResult = getUsingURLIgnoreRest(paramString, paramHashtable);
    Context localContext = (Context)localResolveResult.getResolvedObj();
    try
    {
      Object localObject1 = localContext.lookup(localResolveResult.getRemainingName());
      return localObject1;
    }
    finally
    {
      localContext.close();
    }
  }
  
  private static Object getUsingURLs(String[] paramArrayOfString, Hashtable<?, ?> paramHashtable)
  {
    for (int i = 0; i < paramArrayOfString.length; i++)
    {
      String str = paramArrayOfString[i];
      try
      {
        Object localObject = getUsingURL(str, paramHashtable);
        if (localObject != null) {
          return localObject;
        }
      }
      catch (NamingException localNamingException) {}
    }
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\url\iiop\iiopURLContextFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */