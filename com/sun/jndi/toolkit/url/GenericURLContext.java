package com.sun.jndi.toolkit.url;

import java.net.MalformedURLException;
import java.util.Hashtable;
import javax.naming.Binding;
import javax.naming.CannotProceedException;
import javax.naming.CompositeName;
import javax.naming.Context;
import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.OperationNotSupportedException;
import javax.naming.spi.NamingManager;
import javax.naming.spi.ResolveResult;

public abstract class GenericURLContext
  implements Context
{
  protected Hashtable<String, Object> myEnv = null;
  
  public GenericURLContext(Hashtable<?, ?> paramHashtable)
  {
    myEnv = ((Hashtable)(paramHashtable == null ? null : paramHashtable.clone()));
  }
  
  public void close()
    throws NamingException
  {
    myEnv = null;
  }
  
  public String getNameInNamespace()
    throws NamingException
  {
    return "";
  }
  
  protected abstract ResolveResult getRootURLContext(String paramString, Hashtable<?, ?> paramHashtable)
    throws NamingException;
  
  protected Name getURLSuffix(String paramString1, String paramString2)
    throws NamingException
  {
    String str = paramString2.substring(paramString1.length());
    if (str.length() == 0) {
      return new CompositeName();
    }
    if (str.charAt(0) == '/') {
      str = str.substring(1);
    }
    try
    {
      return new CompositeName().add(UrlUtil.decode(str));
    }
    catch (MalformedURLException localMalformedURLException)
    {
      throw new InvalidNameException(localMalformedURLException.getMessage());
    }
  }
  
  protected String getURLPrefix(String paramString)
    throws NamingException
  {
    int i = paramString.indexOf(":");
    if (i < 0) {
      throw new OperationNotSupportedException("Invalid URL: " + paramString);
    }
    i++;
    if (paramString.startsWith("//", i))
    {
      i += 2;
      int j = paramString.indexOf("/", i);
      if (j >= 0) {
        i = j;
      } else {
        i = paramString.length();
      }
    }
    return paramString.substring(0, i);
  }
  
  protected boolean urlEquals(String paramString1, String paramString2)
  {
    return paramString1.equals(paramString2);
  }
  
  protected Context getContinuationContext(Name paramName)
    throws NamingException
  {
    Object localObject = lookup(paramName.get(0));
    CannotProceedException localCannotProceedException = new CannotProceedException();
    localCannotProceedException.setResolvedObj(localObject);
    localCannotProceedException.setEnvironment(myEnv);
    return NamingManager.getContinuationContext(localCannotProceedException);
  }
  
  public Object lookup(String paramString)
    throws NamingException
  {
    ResolveResult localResolveResult = getRootURLContext(paramString, myEnv);
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
  
  public Object lookup(Name paramName)
    throws NamingException
  {
    if (paramName.size() == 1) {
      return lookup(paramName.get(0));
    }
    Context localContext = getContinuationContext(paramName);
    try
    {
      Object localObject1 = localContext.lookup(paramName.getSuffix(1));
      return localObject1;
    }
    finally
    {
      localContext.close();
    }
  }
  
  public void bind(String paramString, Object paramObject)
    throws NamingException
  {
    ResolveResult localResolveResult = getRootURLContext(paramString, myEnv);
    Context localContext = (Context)localResolveResult.getResolvedObj();
    try
    {
      localContext.bind(localResolveResult.getRemainingName(), paramObject);
    }
    finally
    {
      localContext.close();
    }
  }
  
  public void bind(Name paramName, Object paramObject)
    throws NamingException
  {
    if (paramName.size() == 1)
    {
      bind(paramName.get(0), paramObject);
    }
    else
    {
      Context localContext = getContinuationContext(paramName);
      try
      {
        localContext.bind(paramName.getSuffix(1), paramObject);
      }
      finally
      {
        localContext.close();
      }
    }
  }
  
  public void rebind(String paramString, Object paramObject)
    throws NamingException
  {
    ResolveResult localResolveResult = getRootURLContext(paramString, myEnv);
    Context localContext = (Context)localResolveResult.getResolvedObj();
    try
    {
      localContext.rebind(localResolveResult.getRemainingName(), paramObject);
    }
    finally
    {
      localContext.close();
    }
  }
  
  public void rebind(Name paramName, Object paramObject)
    throws NamingException
  {
    if (paramName.size() == 1)
    {
      rebind(paramName.get(0), paramObject);
    }
    else
    {
      Context localContext = getContinuationContext(paramName);
      try
      {
        localContext.rebind(paramName.getSuffix(1), paramObject);
      }
      finally
      {
        localContext.close();
      }
    }
  }
  
  public void unbind(String paramString)
    throws NamingException
  {
    ResolveResult localResolveResult = getRootURLContext(paramString, myEnv);
    Context localContext = (Context)localResolveResult.getResolvedObj();
    try
    {
      localContext.unbind(localResolveResult.getRemainingName());
    }
    finally
    {
      localContext.close();
    }
  }
  
  /* Error */
  public void unbind(Name paramName)
    throws NamingException
  {
    // Byte code:
    //   0: aload_1
    //   1: invokeinterface 274 1 0
    //   6: iconst_1
    //   7: if_icmpne +17 -> 24
    //   10: aload_0
    //   11: aload_1
    //   12: iconst_0
    //   13: invokeinterface 276 2 0
    //   18: invokevirtual 218	com/sun/jndi/toolkit/url/GenericURLContext:unbind	(Ljava/lang/String;)V
    //   21: goto +40 -> 61
    //   24: aload_0
    //   25: aload_1
    //   26: invokevirtual 227	com/sun/jndi/toolkit/url/GenericURLContext:getContinuationContext	(Ljavax/naming/Name;)Ljavax/naming/Context;
    //   29: astore_2
    //   30: aload_2
    //   31: aload_1
    //   32: iconst_1
    //   33: invokeinterface 277 2 0
    //   38: invokeinterface 264 2 0
    //   43: aload_2
    //   44: invokeinterface 262 1 0
    //   49: goto +12 -> 61
    //   52: astore_3
    //   53: aload_2
    //   54: invokeinterface 262 1 0
    //   59: aload_3
    //   60: athrow
    //   61: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	62	0	this	GenericURLContext
    //   0	62	1	paramName	Name
    //   29	25	2	localContext	Context
    //   52	8	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   30	43	52	finally
  }
  
  public void rename(String paramString1, String paramString2)
    throws NamingException
  {
    String str1 = getURLPrefix(paramString1);
    String str2 = getURLPrefix(paramString2);
    if (!urlEquals(str1, str2)) {
      throw new OperationNotSupportedException("Renaming using different URL prefixes not supported : " + paramString1 + " " + paramString2);
    }
    ResolveResult localResolveResult = getRootURLContext(paramString1, myEnv);
    Context localContext = (Context)localResolveResult.getResolvedObj();
    try
    {
      localContext.rename(localResolveResult.getRemainingName(), getURLSuffix(str2, paramString2));
    }
    finally
    {
      localContext.close();
    }
  }
  
  public void rename(Name paramName1, Name paramName2)
    throws NamingException
  {
    if (paramName1.size() == 1)
    {
      if (paramName2.size() != 1) {
        throw new OperationNotSupportedException("Renaming to a Name with more components not supported: " + paramName2);
      }
      rename(paramName1.get(0), paramName2.get(0));
    }
    else
    {
      if (!urlEquals(paramName1.get(0), paramName2.get(0))) {
        throw new OperationNotSupportedException("Renaming using different URLs as first components not supported: " + paramName1 + " " + paramName2);
      }
      Context localContext = getContinuationContext(paramName1);
      try
      {
        localContext.rename(paramName1.getSuffix(1), paramName2.getSuffix(1));
      }
      finally
      {
        localContext.close();
      }
    }
  }
  
  public NamingEnumeration<NameClassPair> list(String paramString)
    throws NamingException
  {
    ResolveResult localResolveResult = getRootURLContext(paramString, myEnv);
    Context localContext = (Context)localResolveResult.getResolvedObj();
    try
    {
      NamingEnumeration localNamingEnumeration = localContext.list(localResolveResult.getRemainingName());
      return localNamingEnumeration;
    }
    finally
    {
      localContext.close();
    }
  }
  
  public NamingEnumeration<NameClassPair> list(Name paramName)
    throws NamingException
  {
    if (paramName.size() == 1) {
      return list(paramName.get(0));
    }
    Context localContext = getContinuationContext(paramName);
    try
    {
      NamingEnumeration localNamingEnumeration = localContext.list(paramName.getSuffix(1));
      return localNamingEnumeration;
    }
    finally
    {
      localContext.close();
    }
  }
  
  public NamingEnumeration<Binding> listBindings(String paramString)
    throws NamingException
  {
    ResolveResult localResolveResult = getRootURLContext(paramString, myEnv);
    Context localContext = (Context)localResolveResult.getResolvedObj();
    try
    {
      NamingEnumeration localNamingEnumeration = localContext.listBindings(localResolveResult.getRemainingName());
      return localNamingEnumeration;
    }
    finally
    {
      localContext.close();
    }
  }
  
  public NamingEnumeration<Binding> listBindings(Name paramName)
    throws NamingException
  {
    if (paramName.size() == 1) {
      return listBindings(paramName.get(0));
    }
    Context localContext = getContinuationContext(paramName);
    try
    {
      NamingEnumeration localNamingEnumeration = localContext.listBindings(paramName.getSuffix(1));
      return localNamingEnumeration;
    }
    finally
    {
      localContext.close();
    }
  }
  
  public void destroySubcontext(String paramString)
    throws NamingException
  {
    ResolveResult localResolveResult = getRootURLContext(paramString, myEnv);
    Context localContext = (Context)localResolveResult.getResolvedObj();
    try
    {
      localContext.destroySubcontext(localResolveResult.getRemainingName());
    }
    finally
    {
      localContext.close();
    }
  }
  
  /* Error */
  public void destroySubcontext(Name paramName)
    throws NamingException
  {
    // Byte code:
    //   0: aload_1
    //   1: invokeinterface 274 1 0
    //   6: iconst_1
    //   7: if_icmpne +17 -> 24
    //   10: aload_0
    //   11: aload_1
    //   12: iconst_0
    //   13: invokeinterface 276 2 0
    //   18: invokevirtual 217	com/sun/jndi/toolkit/url/GenericURLContext:destroySubcontext	(Ljava/lang/String;)V
    //   21: goto +40 -> 61
    //   24: aload_0
    //   25: aload_1
    //   26: invokevirtual 227	com/sun/jndi/toolkit/url/GenericURLContext:getContinuationContext	(Ljavax/naming/Name;)Ljavax/naming/Context;
    //   29: astore_2
    //   30: aload_2
    //   31: aload_1
    //   32: iconst_1
    //   33: invokeinterface 277 2 0
    //   38: invokeinterface 263 2 0
    //   43: aload_2
    //   44: invokeinterface 262 1 0
    //   49: goto +12 -> 61
    //   52: astore_3
    //   53: aload_2
    //   54: invokeinterface 262 1 0
    //   59: aload_3
    //   60: athrow
    //   61: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	62	0	this	GenericURLContext
    //   0	62	1	paramName	Name
    //   29	25	2	localContext	Context
    //   52	8	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   30	43	52	finally
  }
  
  public Context createSubcontext(String paramString)
    throws NamingException
  {
    ResolveResult localResolveResult = getRootURLContext(paramString, myEnv);
    Context localContext1 = (Context)localResolveResult.getResolvedObj();
    try
    {
      Context localContext2 = localContext1.createSubcontext(localResolveResult.getRemainingName());
      return localContext2;
    }
    finally
    {
      localContext1.close();
    }
  }
  
  public Context createSubcontext(Name paramName)
    throws NamingException
  {
    if (paramName.size() == 1) {
      return createSubcontext(paramName.get(0));
    }
    Context localContext1 = getContinuationContext(paramName);
    try
    {
      Context localContext2 = localContext1.createSubcontext(paramName.getSuffix(1));
      return localContext2;
    }
    finally
    {
      localContext1.close();
    }
  }
  
  public Object lookupLink(String paramString)
    throws NamingException
  {
    ResolveResult localResolveResult = getRootURLContext(paramString, myEnv);
    Context localContext = (Context)localResolveResult.getResolvedObj();
    try
    {
      Object localObject1 = localContext.lookupLink(localResolveResult.getRemainingName());
      return localObject1;
    }
    finally
    {
      localContext.close();
    }
  }
  
  public Object lookupLink(Name paramName)
    throws NamingException
  {
    if (paramName.size() == 1) {
      return lookupLink(paramName.get(0));
    }
    Context localContext = getContinuationContext(paramName);
    try
    {
      Object localObject1 = localContext.lookupLink(paramName.getSuffix(1));
      return localObject1;
    }
    finally
    {
      localContext.close();
    }
  }
  
  public NameParser getNameParser(String paramString)
    throws NamingException
  {
    ResolveResult localResolveResult = getRootURLContext(paramString, myEnv);
    Context localContext = (Context)localResolveResult.getResolvedObj();
    try
    {
      NameParser localNameParser = localContext.getNameParser(localResolveResult.getRemainingName());
      return localNameParser;
    }
    finally
    {
      localContext.close();
    }
  }
  
  public NameParser getNameParser(Name paramName)
    throws NamingException
  {
    if (paramName.size() == 1) {
      return getNameParser(paramName.get(0));
    }
    Context localContext = getContinuationContext(paramName);
    try
    {
      NameParser localNameParser = localContext.getNameParser(paramName.getSuffix(1));
      return localNameParser;
    }
    finally
    {
      localContext.close();
    }
  }
  
  public String composeName(String paramString1, String paramString2)
    throws NamingException
  {
    if (paramString2.equals("")) {
      return paramString1;
    }
    if (paramString1.equals("")) {
      return paramString2;
    }
    return paramString2 + "/" + paramString1;
  }
  
  public Name composeName(Name paramName1, Name paramName2)
    throws NamingException
  {
    Name localName = (Name)paramName2.clone();
    localName.addAll(paramName1);
    return localName;
  }
  
  public Object removeFromEnvironment(String paramString)
    throws NamingException
  {
    if (myEnv == null) {
      return null;
    }
    return myEnv.remove(paramString);
  }
  
  public Object addToEnvironment(String paramString, Object paramObject)
    throws NamingException
  {
    if (myEnv == null) {
      myEnv = new Hashtable(11, 0.75F);
    }
    return myEnv.put(paramString, paramObject);
  }
  
  public Hashtable<String, Object> getEnvironment()
    throws NamingException
  {
    if (myEnv == null) {
      return new Hashtable(5, 0.75F);
    }
    return (Hashtable)myEnv.clone();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\toolkit\url\GenericURLContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */