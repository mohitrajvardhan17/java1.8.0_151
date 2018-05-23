package javax.naming.spi;

import java.util.Hashtable;
import javax.naming.Binding;
import javax.naming.CannotProceedException;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

class ContinuationContext
  implements Context, Resolver
{
  protected CannotProceedException cpe;
  protected Hashtable<?, ?> env;
  protected Context contCtx = null;
  
  protected ContinuationContext(CannotProceedException paramCannotProceedException, Hashtable<?, ?> paramHashtable)
  {
    cpe = paramCannotProceedException;
    env = paramHashtable;
  }
  
  protected Context getTargetContext()
    throws NamingException
  {
    if (contCtx == null)
    {
      if (cpe.getResolvedObj() == null) {
        throw ((NamingException)cpe.fillInStackTrace());
      }
      contCtx = NamingManager.getContext(cpe.getResolvedObj(), cpe.getAltName(), cpe.getAltNameCtx(), env);
      if (contCtx == null) {
        throw ((NamingException)cpe.fillInStackTrace());
      }
    }
    return contCtx;
  }
  
  public Object lookup(Name paramName)
    throws NamingException
  {
    Context localContext = getTargetContext();
    return localContext.lookup(paramName);
  }
  
  public Object lookup(String paramString)
    throws NamingException
  {
    Context localContext = getTargetContext();
    return localContext.lookup(paramString);
  }
  
  public void bind(Name paramName, Object paramObject)
    throws NamingException
  {
    Context localContext = getTargetContext();
    localContext.bind(paramName, paramObject);
  }
  
  public void bind(String paramString, Object paramObject)
    throws NamingException
  {
    Context localContext = getTargetContext();
    localContext.bind(paramString, paramObject);
  }
  
  public void rebind(Name paramName, Object paramObject)
    throws NamingException
  {
    Context localContext = getTargetContext();
    localContext.rebind(paramName, paramObject);
  }
  
  public void rebind(String paramString, Object paramObject)
    throws NamingException
  {
    Context localContext = getTargetContext();
    localContext.rebind(paramString, paramObject);
  }
  
  public void unbind(Name paramName)
    throws NamingException
  {
    Context localContext = getTargetContext();
    localContext.unbind(paramName);
  }
  
  public void unbind(String paramString)
    throws NamingException
  {
    Context localContext = getTargetContext();
    localContext.unbind(paramString);
  }
  
  public void rename(Name paramName1, Name paramName2)
    throws NamingException
  {
    Context localContext = getTargetContext();
    localContext.rename(paramName1, paramName2);
  }
  
  public void rename(String paramString1, String paramString2)
    throws NamingException
  {
    Context localContext = getTargetContext();
    localContext.rename(paramString1, paramString2);
  }
  
  public NamingEnumeration<NameClassPair> list(Name paramName)
    throws NamingException
  {
    Context localContext = getTargetContext();
    return localContext.list(paramName);
  }
  
  public NamingEnumeration<NameClassPair> list(String paramString)
    throws NamingException
  {
    Context localContext = getTargetContext();
    return localContext.list(paramString);
  }
  
  public NamingEnumeration<Binding> listBindings(Name paramName)
    throws NamingException
  {
    Context localContext = getTargetContext();
    return localContext.listBindings(paramName);
  }
  
  public NamingEnumeration<Binding> listBindings(String paramString)
    throws NamingException
  {
    Context localContext = getTargetContext();
    return localContext.listBindings(paramString);
  }
  
  public void destroySubcontext(Name paramName)
    throws NamingException
  {
    Context localContext = getTargetContext();
    localContext.destroySubcontext(paramName);
  }
  
  public void destroySubcontext(String paramString)
    throws NamingException
  {
    Context localContext = getTargetContext();
    localContext.destroySubcontext(paramString);
  }
  
  public Context createSubcontext(Name paramName)
    throws NamingException
  {
    Context localContext = getTargetContext();
    return localContext.createSubcontext(paramName);
  }
  
  public Context createSubcontext(String paramString)
    throws NamingException
  {
    Context localContext = getTargetContext();
    return localContext.createSubcontext(paramString);
  }
  
  public Object lookupLink(Name paramName)
    throws NamingException
  {
    Context localContext = getTargetContext();
    return localContext.lookupLink(paramName);
  }
  
  public Object lookupLink(String paramString)
    throws NamingException
  {
    Context localContext = getTargetContext();
    return localContext.lookupLink(paramString);
  }
  
  public NameParser getNameParser(Name paramName)
    throws NamingException
  {
    Context localContext = getTargetContext();
    return localContext.getNameParser(paramName);
  }
  
  public NameParser getNameParser(String paramString)
    throws NamingException
  {
    Context localContext = getTargetContext();
    return localContext.getNameParser(paramString);
  }
  
  public Name composeName(Name paramName1, Name paramName2)
    throws NamingException
  {
    Context localContext = getTargetContext();
    return localContext.composeName(paramName1, paramName2);
  }
  
  public String composeName(String paramString1, String paramString2)
    throws NamingException
  {
    Context localContext = getTargetContext();
    return localContext.composeName(paramString1, paramString2);
  }
  
  public Object addToEnvironment(String paramString, Object paramObject)
    throws NamingException
  {
    Context localContext = getTargetContext();
    return localContext.addToEnvironment(paramString, paramObject);
  }
  
  public Object removeFromEnvironment(String paramString)
    throws NamingException
  {
    Context localContext = getTargetContext();
    return localContext.removeFromEnvironment(paramString);
  }
  
  public Hashtable<?, ?> getEnvironment()
    throws NamingException
  {
    Context localContext = getTargetContext();
    return localContext.getEnvironment();
  }
  
  public String getNameInNamespace()
    throws NamingException
  {
    Context localContext = getTargetContext();
    return localContext.getNameInNamespace();
  }
  
  public ResolveResult resolveToClass(Name paramName, Class<? extends Context> paramClass)
    throws NamingException
  {
    if (cpe.getResolvedObj() == null) {
      throw ((NamingException)cpe.fillInStackTrace());
    }
    Resolver localResolver = NamingManager.getResolver(cpe.getResolvedObj(), cpe.getAltName(), cpe.getAltNameCtx(), env);
    if (localResolver == null) {
      throw ((NamingException)cpe.fillInStackTrace());
    }
    return localResolver.resolveToClass(paramName, paramClass);
  }
  
  public ResolveResult resolveToClass(String paramString, Class<? extends Context> paramClass)
    throws NamingException
  {
    if (cpe.getResolvedObj() == null) {
      throw ((NamingException)cpe.fillInStackTrace());
    }
    Resolver localResolver = NamingManager.getResolver(cpe.getResolvedObj(), cpe.getAltName(), cpe.getAltNameCtx(), env);
    if (localResolver == null) {
      throw ((NamingException)cpe.fillInStackTrace());
    }
    return localResolver.resolveToClass(paramString, paramClass);
  }
  
  public void close()
    throws NamingException
  {
    cpe = null;
    env = null;
    if (contCtx != null)
    {
      contCtx.close();
      contCtx = null;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\naming\spi\ContinuationContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */