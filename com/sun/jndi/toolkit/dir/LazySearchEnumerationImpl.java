package com.sun.jndi.toolkit.dir;

import java.util.Hashtable;
import java.util.NoSuchElementException;
import javax.naming.Binding;
import javax.naming.CompositeName;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.spi.DirectoryManager;

public final class LazySearchEnumerationImpl
  implements NamingEnumeration<SearchResult>
{
  private NamingEnumeration<Binding> candidates;
  private SearchResult nextMatch = null;
  private SearchControls cons;
  private AttrFilter filter;
  private Context context;
  private Hashtable<String, Object> env;
  private boolean useFactory = true;
  
  public LazySearchEnumerationImpl(NamingEnumeration<Binding> paramNamingEnumeration, AttrFilter paramAttrFilter, SearchControls paramSearchControls)
    throws NamingException
  {
    candidates = paramNamingEnumeration;
    filter = paramAttrFilter;
    if (paramSearchControls == null) {
      cons = new SearchControls();
    } else {
      cons = paramSearchControls;
    }
  }
  
  public LazySearchEnumerationImpl(NamingEnumeration<Binding> paramNamingEnumeration, AttrFilter paramAttrFilter, SearchControls paramSearchControls, Context paramContext, Hashtable<String, Object> paramHashtable, boolean paramBoolean)
    throws NamingException
  {
    candidates = paramNamingEnumeration;
    filter = paramAttrFilter;
    env = ((Hashtable)(paramHashtable == null ? null : paramHashtable.clone()));
    context = paramContext;
    useFactory = paramBoolean;
    if (paramSearchControls == null) {
      cons = new SearchControls();
    } else {
      cons = paramSearchControls;
    }
  }
  
  public LazySearchEnumerationImpl(NamingEnumeration<Binding> paramNamingEnumeration, AttrFilter paramAttrFilter, SearchControls paramSearchControls, Context paramContext, Hashtable<String, Object> paramHashtable)
    throws NamingException
  {
    this(paramNamingEnumeration, paramAttrFilter, paramSearchControls, paramContext, paramHashtable, true);
  }
  
  public boolean hasMore()
    throws NamingException
  {
    return findNextMatch(false) != null;
  }
  
  public boolean hasMoreElements()
  {
    try
    {
      return hasMore();
    }
    catch (NamingException localNamingException) {}
    return false;
  }
  
  public SearchResult nextElement()
  {
    try
    {
      return findNextMatch(true);
    }
    catch (NamingException localNamingException)
    {
      throw new NoSuchElementException(localNamingException.toString());
    }
  }
  
  public SearchResult next()
    throws NamingException
  {
    return findNextMatch(true);
  }
  
  public void close()
    throws NamingException
  {
    if (candidates != null) {
      candidates.close();
    }
  }
  
  private SearchResult findNextMatch(boolean paramBoolean)
    throws NamingException
  {
    SearchResult localSearchResult;
    if (nextMatch != null)
    {
      localSearchResult = nextMatch;
      if (paramBoolean) {
        nextMatch = null;
      }
      return localSearchResult;
    }
    while (candidates.hasMore())
    {
      Binding localBinding = (Binding)candidates.next();
      Object localObject = localBinding.getObject();
      if ((localObject instanceof DirContext))
      {
        Attributes localAttributes = ((DirContext)localObject).getAttributes("");
        if (filter.check(localAttributes))
        {
          if (!cons.getReturningObjFlag()) {
            localObject = null;
          } else if (useFactory) {
            try
            {
              Name localName = context != null ? new CompositeName(localBinding.getName()) : null;
              localObject = DirectoryManager.getObjectInstance(localObject, localName, context, env, localAttributes);
            }
            catch (NamingException localNamingException1)
            {
              throw localNamingException1;
            }
            catch (Exception localException)
            {
              NamingException localNamingException2 = new NamingException("problem generating object using object factory");
              localNamingException2.setRootCause(localException);
              throw localNamingException2;
            }
          }
          localSearchResult = new SearchResult(localBinding.getName(), localBinding.getClassName(), localObject, SearchFilter.selectAttributes(localAttributes, cons.getReturningAttributes()), true);
          if (!paramBoolean) {
            nextMatch = localSearchResult;
          }
          return localSearchResult;
        }
      }
    }
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\toolkit\dir\LazySearchEnumerationImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */