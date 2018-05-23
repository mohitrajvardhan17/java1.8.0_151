package com.sun.jndi.toolkit.url;

import java.util.Hashtable;
import javax.naming.CannotProceedException;
import javax.naming.Name;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.spi.DirectoryManager;
import javax.naming.spi.ResolveResult;

public abstract class GenericURLDirContext
  extends GenericURLContext
  implements DirContext
{
  protected GenericURLDirContext(Hashtable<?, ?> paramHashtable)
  {
    super(paramHashtable);
  }
  
  protected DirContext getContinuationDirContext(Name paramName)
    throws NamingException
  {
    Object localObject = lookup(paramName.get(0));
    CannotProceedException localCannotProceedException = new CannotProceedException();
    localCannotProceedException.setResolvedObj(localObject);
    localCannotProceedException.setEnvironment(myEnv);
    return DirectoryManager.getContinuationDirContext(localCannotProceedException);
  }
  
  public Attributes getAttributes(String paramString)
    throws NamingException
  {
    ResolveResult localResolveResult = getRootURLContext(paramString, myEnv);
    DirContext localDirContext = (DirContext)localResolveResult.getResolvedObj();
    try
    {
      Attributes localAttributes = localDirContext.getAttributes(localResolveResult.getRemainingName());
      return localAttributes;
    }
    finally
    {
      localDirContext.close();
    }
  }
  
  public Attributes getAttributes(Name paramName)
    throws NamingException
  {
    if (paramName.size() == 1) {
      return getAttributes(paramName.get(0));
    }
    DirContext localDirContext = getContinuationDirContext(paramName);
    try
    {
      Attributes localAttributes = localDirContext.getAttributes(paramName.getSuffix(1));
      return localAttributes;
    }
    finally
    {
      localDirContext.close();
    }
  }
  
  public Attributes getAttributes(String paramString, String[] paramArrayOfString)
    throws NamingException
  {
    ResolveResult localResolveResult = getRootURLContext(paramString, myEnv);
    DirContext localDirContext = (DirContext)localResolveResult.getResolvedObj();
    try
    {
      Attributes localAttributes = localDirContext.getAttributes(localResolveResult.getRemainingName(), paramArrayOfString);
      return localAttributes;
    }
    finally
    {
      localDirContext.close();
    }
  }
  
  public Attributes getAttributes(Name paramName, String[] paramArrayOfString)
    throws NamingException
  {
    if (paramName.size() == 1) {
      return getAttributes(paramName.get(0), paramArrayOfString);
    }
    DirContext localDirContext = getContinuationDirContext(paramName);
    try
    {
      Attributes localAttributes = localDirContext.getAttributes(paramName.getSuffix(1), paramArrayOfString);
      return localAttributes;
    }
    finally
    {
      localDirContext.close();
    }
  }
  
  public void modifyAttributes(String paramString, int paramInt, Attributes paramAttributes)
    throws NamingException
  {
    ResolveResult localResolveResult = getRootURLContext(paramString, myEnv);
    DirContext localDirContext = (DirContext)localResolveResult.getResolvedObj();
    try
    {
      localDirContext.modifyAttributes(localResolveResult.getRemainingName(), paramInt, paramAttributes);
    }
    finally
    {
      localDirContext.close();
    }
  }
  
  public void modifyAttributes(Name paramName, int paramInt, Attributes paramAttributes)
    throws NamingException
  {
    if (paramName.size() == 1)
    {
      modifyAttributes(paramName.get(0), paramInt, paramAttributes);
    }
    else
    {
      DirContext localDirContext = getContinuationDirContext(paramName);
      try
      {
        localDirContext.modifyAttributes(paramName.getSuffix(1), paramInt, paramAttributes);
      }
      finally
      {
        localDirContext.close();
      }
    }
  }
  
  public void modifyAttributes(String paramString, ModificationItem[] paramArrayOfModificationItem)
    throws NamingException
  {
    ResolveResult localResolveResult = getRootURLContext(paramString, myEnv);
    DirContext localDirContext = (DirContext)localResolveResult.getResolvedObj();
    try
    {
      localDirContext.modifyAttributes(localResolveResult.getRemainingName(), paramArrayOfModificationItem);
    }
    finally
    {
      localDirContext.close();
    }
  }
  
  public void modifyAttributes(Name paramName, ModificationItem[] paramArrayOfModificationItem)
    throws NamingException
  {
    if (paramName.size() == 1)
    {
      modifyAttributes(paramName.get(0), paramArrayOfModificationItem);
    }
    else
    {
      DirContext localDirContext = getContinuationDirContext(paramName);
      try
      {
        localDirContext.modifyAttributes(paramName.getSuffix(1), paramArrayOfModificationItem);
      }
      finally
      {
        localDirContext.close();
      }
    }
  }
  
  public void bind(String paramString, Object paramObject, Attributes paramAttributes)
    throws NamingException
  {
    ResolveResult localResolveResult = getRootURLContext(paramString, myEnv);
    DirContext localDirContext = (DirContext)localResolveResult.getResolvedObj();
    try
    {
      localDirContext.bind(localResolveResult.getRemainingName(), paramObject, paramAttributes);
    }
    finally
    {
      localDirContext.close();
    }
  }
  
  public void bind(Name paramName, Object paramObject, Attributes paramAttributes)
    throws NamingException
  {
    if (paramName.size() == 1)
    {
      bind(paramName.get(0), paramObject, paramAttributes);
    }
    else
    {
      DirContext localDirContext = getContinuationDirContext(paramName);
      try
      {
        localDirContext.bind(paramName.getSuffix(1), paramObject, paramAttributes);
      }
      finally
      {
        localDirContext.close();
      }
    }
  }
  
  public void rebind(String paramString, Object paramObject, Attributes paramAttributes)
    throws NamingException
  {
    ResolveResult localResolveResult = getRootURLContext(paramString, myEnv);
    DirContext localDirContext = (DirContext)localResolveResult.getResolvedObj();
    try
    {
      localDirContext.rebind(localResolveResult.getRemainingName(), paramObject, paramAttributes);
    }
    finally
    {
      localDirContext.close();
    }
  }
  
  public void rebind(Name paramName, Object paramObject, Attributes paramAttributes)
    throws NamingException
  {
    if (paramName.size() == 1)
    {
      rebind(paramName.get(0), paramObject, paramAttributes);
    }
    else
    {
      DirContext localDirContext = getContinuationDirContext(paramName);
      try
      {
        localDirContext.rebind(paramName.getSuffix(1), paramObject, paramAttributes);
      }
      finally
      {
        localDirContext.close();
      }
    }
  }
  
  public DirContext createSubcontext(String paramString, Attributes paramAttributes)
    throws NamingException
  {
    ResolveResult localResolveResult = getRootURLContext(paramString, myEnv);
    DirContext localDirContext1 = (DirContext)localResolveResult.getResolvedObj();
    try
    {
      DirContext localDirContext2 = localDirContext1.createSubcontext(localResolveResult.getRemainingName(), paramAttributes);
      return localDirContext2;
    }
    finally
    {
      localDirContext1.close();
    }
  }
  
  public DirContext createSubcontext(Name paramName, Attributes paramAttributes)
    throws NamingException
  {
    if (paramName.size() == 1) {
      return createSubcontext(paramName.get(0), paramAttributes);
    }
    DirContext localDirContext1 = getContinuationDirContext(paramName);
    try
    {
      DirContext localDirContext2 = localDirContext1.createSubcontext(paramName.getSuffix(1), paramAttributes);
      return localDirContext2;
    }
    finally
    {
      localDirContext1.close();
    }
  }
  
  public DirContext getSchema(String paramString)
    throws NamingException
  {
    ResolveResult localResolveResult = getRootURLContext(paramString, myEnv);
    DirContext localDirContext = (DirContext)localResolveResult.getResolvedObj();
    return localDirContext.getSchema(localResolveResult.getRemainingName());
  }
  
  public DirContext getSchema(Name paramName)
    throws NamingException
  {
    if (paramName.size() == 1) {
      return getSchema(paramName.get(0));
    }
    DirContext localDirContext1 = getContinuationDirContext(paramName);
    try
    {
      DirContext localDirContext2 = localDirContext1.getSchema(paramName.getSuffix(1));
      return localDirContext2;
    }
    finally
    {
      localDirContext1.close();
    }
  }
  
  public DirContext getSchemaClassDefinition(String paramString)
    throws NamingException
  {
    ResolveResult localResolveResult = getRootURLContext(paramString, myEnv);
    DirContext localDirContext1 = (DirContext)localResolveResult.getResolvedObj();
    try
    {
      DirContext localDirContext2 = localDirContext1.getSchemaClassDefinition(localResolveResult.getRemainingName());
      return localDirContext2;
    }
    finally
    {
      localDirContext1.close();
    }
  }
  
  public DirContext getSchemaClassDefinition(Name paramName)
    throws NamingException
  {
    if (paramName.size() == 1) {
      return getSchemaClassDefinition(paramName.get(0));
    }
    DirContext localDirContext1 = getContinuationDirContext(paramName);
    try
    {
      DirContext localDirContext2 = localDirContext1.getSchemaClassDefinition(paramName.getSuffix(1));
      return localDirContext2;
    }
    finally
    {
      localDirContext1.close();
    }
  }
  
  public NamingEnumeration<SearchResult> search(String paramString, Attributes paramAttributes)
    throws NamingException
  {
    ResolveResult localResolveResult = getRootURLContext(paramString, myEnv);
    DirContext localDirContext = (DirContext)localResolveResult.getResolvedObj();
    try
    {
      NamingEnumeration localNamingEnumeration = localDirContext.search(localResolveResult.getRemainingName(), paramAttributes);
      return localNamingEnumeration;
    }
    finally
    {
      localDirContext.close();
    }
  }
  
  public NamingEnumeration<SearchResult> search(Name paramName, Attributes paramAttributes)
    throws NamingException
  {
    if (paramName.size() == 1) {
      return search(paramName.get(0), paramAttributes);
    }
    DirContext localDirContext = getContinuationDirContext(paramName);
    try
    {
      NamingEnumeration localNamingEnumeration = localDirContext.search(paramName.getSuffix(1), paramAttributes);
      return localNamingEnumeration;
    }
    finally
    {
      localDirContext.close();
    }
  }
  
  public NamingEnumeration<SearchResult> search(String paramString, Attributes paramAttributes, String[] paramArrayOfString)
    throws NamingException
  {
    ResolveResult localResolveResult = getRootURLContext(paramString, myEnv);
    DirContext localDirContext = (DirContext)localResolveResult.getResolvedObj();
    try
    {
      NamingEnumeration localNamingEnumeration = localDirContext.search(localResolveResult.getRemainingName(), paramAttributes, paramArrayOfString);
      return localNamingEnumeration;
    }
    finally
    {
      localDirContext.close();
    }
  }
  
  public NamingEnumeration<SearchResult> search(Name paramName, Attributes paramAttributes, String[] paramArrayOfString)
    throws NamingException
  {
    if (paramName.size() == 1) {
      return search(paramName.get(0), paramAttributes, paramArrayOfString);
    }
    DirContext localDirContext = getContinuationDirContext(paramName);
    try
    {
      NamingEnumeration localNamingEnumeration = localDirContext.search(paramName.getSuffix(1), paramAttributes, paramArrayOfString);
      return localNamingEnumeration;
    }
    finally
    {
      localDirContext.close();
    }
  }
  
  public NamingEnumeration<SearchResult> search(String paramString1, String paramString2, SearchControls paramSearchControls)
    throws NamingException
  {
    ResolveResult localResolveResult = getRootURLContext(paramString1, myEnv);
    DirContext localDirContext = (DirContext)localResolveResult.getResolvedObj();
    try
    {
      NamingEnumeration localNamingEnumeration = localDirContext.search(localResolveResult.getRemainingName(), paramString2, paramSearchControls);
      return localNamingEnumeration;
    }
    finally
    {
      localDirContext.close();
    }
  }
  
  public NamingEnumeration<SearchResult> search(Name paramName, String paramString, SearchControls paramSearchControls)
    throws NamingException
  {
    if (paramName.size() == 1) {
      return search(paramName.get(0), paramString, paramSearchControls);
    }
    DirContext localDirContext = getContinuationDirContext(paramName);
    try
    {
      NamingEnumeration localNamingEnumeration = localDirContext.search(paramName.getSuffix(1), paramString, paramSearchControls);
      return localNamingEnumeration;
    }
    finally
    {
      localDirContext.close();
    }
  }
  
  public NamingEnumeration<SearchResult> search(String paramString1, String paramString2, Object[] paramArrayOfObject, SearchControls paramSearchControls)
    throws NamingException
  {
    ResolveResult localResolveResult = getRootURLContext(paramString1, myEnv);
    DirContext localDirContext = (DirContext)localResolveResult.getResolvedObj();
    try
    {
      NamingEnumeration localNamingEnumeration = localDirContext.search(localResolveResult.getRemainingName(), paramString2, paramArrayOfObject, paramSearchControls);
      return localNamingEnumeration;
    }
    finally
    {
      localDirContext.close();
    }
  }
  
  public NamingEnumeration<SearchResult> search(Name paramName, String paramString, Object[] paramArrayOfObject, SearchControls paramSearchControls)
    throws NamingException
  {
    if (paramName.size() == 1) {
      return search(paramName.get(0), paramString, paramArrayOfObject, paramSearchControls);
    }
    DirContext localDirContext = getContinuationDirContext(paramName);
    try
    {
      NamingEnumeration localNamingEnumeration = localDirContext.search(paramName.getSuffix(1), paramString, paramArrayOfObject, paramSearchControls);
      return localNamingEnumeration;
    }
    finally
    {
      localDirContext.close();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\toolkit\url\GenericURLDirContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */