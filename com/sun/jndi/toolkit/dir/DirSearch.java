package com.sun.jndi.toolkit.dir;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

public class DirSearch
{
  public DirSearch() {}
  
  public static NamingEnumeration<SearchResult> search(DirContext paramDirContext, Attributes paramAttributes, String[] paramArrayOfString)
    throws NamingException
  {
    SearchControls localSearchControls = new SearchControls(1, 0L, 0, paramArrayOfString, false, false);
    return new LazySearchEnumerationImpl(new ContextEnumerator(paramDirContext, 1), new ContainmentFilter(paramAttributes), localSearchControls);
  }
  
  public static NamingEnumeration<SearchResult> search(DirContext paramDirContext, String paramString, SearchControls paramSearchControls)
    throws NamingException
  {
    if (paramSearchControls == null) {
      paramSearchControls = new SearchControls();
    }
    return new LazySearchEnumerationImpl(new ContextEnumerator(paramDirContext, paramSearchControls.getSearchScope()), new SearchFilter(paramString), paramSearchControls);
  }
  
  public static NamingEnumeration<SearchResult> search(DirContext paramDirContext, String paramString, Object[] paramArrayOfObject, SearchControls paramSearchControls)
    throws NamingException
  {
    String str = SearchFilter.format(paramString, paramArrayOfObject);
    return search(paramDirContext, str, paramSearchControls);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\toolkit\dir\DirSearch.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */