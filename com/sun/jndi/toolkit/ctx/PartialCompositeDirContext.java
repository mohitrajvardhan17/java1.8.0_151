package com.sun.jndi.toolkit.ctx;

import java.util.Hashtable;
import javax.naming.Binding;
import javax.naming.CannotProceedException;
import javax.naming.CompositeName;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.NotContextException;
import javax.naming.OperationNotSupportedException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.spi.DirectoryManager;

public abstract class PartialCompositeDirContext
  extends AtomicContext
  implements DirContext
{
  protected PartialCompositeDirContext()
  {
    _contextType = 1;
  }
  
  protected abstract Attributes p_getAttributes(Name paramName, String[] paramArrayOfString, Continuation paramContinuation)
    throws NamingException;
  
  protected abstract void p_modifyAttributes(Name paramName, int paramInt, Attributes paramAttributes, Continuation paramContinuation)
    throws NamingException;
  
  protected abstract void p_modifyAttributes(Name paramName, ModificationItem[] paramArrayOfModificationItem, Continuation paramContinuation)
    throws NamingException;
  
  protected abstract void p_bind(Name paramName, Object paramObject, Attributes paramAttributes, Continuation paramContinuation)
    throws NamingException;
  
  protected abstract void p_rebind(Name paramName, Object paramObject, Attributes paramAttributes, Continuation paramContinuation)
    throws NamingException;
  
  protected abstract DirContext p_createSubcontext(Name paramName, Attributes paramAttributes, Continuation paramContinuation)
    throws NamingException;
  
  protected abstract NamingEnumeration<SearchResult> p_search(Name paramName, Attributes paramAttributes, String[] paramArrayOfString, Continuation paramContinuation)
    throws NamingException;
  
  protected abstract NamingEnumeration<SearchResult> p_search(Name paramName, String paramString, SearchControls paramSearchControls, Continuation paramContinuation)
    throws NamingException;
  
  protected abstract NamingEnumeration<SearchResult> p_search(Name paramName, String paramString, Object[] paramArrayOfObject, SearchControls paramSearchControls, Continuation paramContinuation)
    throws NamingException;
  
  protected abstract DirContext p_getSchema(Name paramName, Continuation paramContinuation)
    throws NamingException;
  
  protected abstract DirContext p_getSchemaClassDefinition(Name paramName, Continuation paramContinuation)
    throws NamingException;
  
  public Attributes getAttributes(String paramString)
    throws NamingException
  {
    return getAttributes(paramString, null);
  }
  
  public Attributes getAttributes(Name paramName)
    throws NamingException
  {
    return getAttributes(paramName, null);
  }
  
  public Attributes getAttributes(String paramString, String[] paramArrayOfString)
    throws NamingException
  {
    return getAttributes(new CompositeName(paramString), paramArrayOfString);
  }
  
  public Attributes getAttributes(Name paramName, String[] paramArrayOfString)
    throws NamingException
  {
    PartialCompositeDirContext localPartialCompositeDirContext = this;
    Hashtable localHashtable = p_getEnvironment();
    Continuation localContinuation = new Continuation(paramName, localHashtable);
    Name localName = paramName;
    Attributes localAttributes;
    try
    {
      for (localAttributes = localPartialCompositeDirContext.p_getAttributes(localName, paramArrayOfString, localContinuation); localContinuation.isContinue(); localAttributes = localPartialCompositeDirContext.p_getAttributes(localName, paramArrayOfString, localContinuation))
      {
        localName = localContinuation.getRemainingName();
        localPartialCompositeDirContext = getPCDirContext(localContinuation);
      }
    }
    catch (CannotProceedException localCannotProceedException)
    {
      DirContext localDirContext = DirectoryManager.getContinuationDirContext(localCannotProceedException);
      localAttributes = localDirContext.getAttributes(localCannotProceedException.getRemainingName(), paramArrayOfString);
    }
    return localAttributes;
  }
  
  public void modifyAttributes(String paramString, int paramInt, Attributes paramAttributes)
    throws NamingException
  {
    modifyAttributes(new CompositeName(paramString), paramInt, paramAttributes);
  }
  
  public void modifyAttributes(Name paramName, int paramInt, Attributes paramAttributes)
    throws NamingException
  {
    PartialCompositeDirContext localPartialCompositeDirContext = this;
    Hashtable localHashtable = p_getEnvironment();
    Continuation localContinuation = new Continuation(paramName, localHashtable);
    Name localName = paramName;
    try
    {
      localPartialCompositeDirContext.p_modifyAttributes(localName, paramInt, paramAttributes, localContinuation);
      while (localContinuation.isContinue())
      {
        localName = localContinuation.getRemainingName();
        localPartialCompositeDirContext = getPCDirContext(localContinuation);
        localPartialCompositeDirContext.p_modifyAttributes(localName, paramInt, paramAttributes, localContinuation);
      }
    }
    catch (CannotProceedException localCannotProceedException)
    {
      DirContext localDirContext = DirectoryManager.getContinuationDirContext(localCannotProceedException);
      localDirContext.modifyAttributes(localCannotProceedException.getRemainingName(), paramInt, paramAttributes);
    }
  }
  
  public void modifyAttributes(String paramString, ModificationItem[] paramArrayOfModificationItem)
    throws NamingException
  {
    modifyAttributes(new CompositeName(paramString), paramArrayOfModificationItem);
  }
  
  public void modifyAttributes(Name paramName, ModificationItem[] paramArrayOfModificationItem)
    throws NamingException
  {
    PartialCompositeDirContext localPartialCompositeDirContext = this;
    Hashtable localHashtable = p_getEnvironment();
    Continuation localContinuation = new Continuation(paramName, localHashtable);
    Name localName = paramName;
    try
    {
      localPartialCompositeDirContext.p_modifyAttributes(localName, paramArrayOfModificationItem, localContinuation);
      while (localContinuation.isContinue())
      {
        localName = localContinuation.getRemainingName();
        localPartialCompositeDirContext = getPCDirContext(localContinuation);
        localPartialCompositeDirContext.p_modifyAttributes(localName, paramArrayOfModificationItem, localContinuation);
      }
    }
    catch (CannotProceedException localCannotProceedException)
    {
      DirContext localDirContext = DirectoryManager.getContinuationDirContext(localCannotProceedException);
      localDirContext.modifyAttributes(localCannotProceedException.getRemainingName(), paramArrayOfModificationItem);
    }
  }
  
  public void bind(String paramString, Object paramObject, Attributes paramAttributes)
    throws NamingException
  {
    bind(new CompositeName(paramString), paramObject, paramAttributes);
  }
  
  public void bind(Name paramName, Object paramObject, Attributes paramAttributes)
    throws NamingException
  {
    PartialCompositeDirContext localPartialCompositeDirContext = this;
    Hashtable localHashtable = p_getEnvironment();
    Continuation localContinuation = new Continuation(paramName, localHashtable);
    Name localName = paramName;
    try
    {
      localPartialCompositeDirContext.p_bind(localName, paramObject, paramAttributes, localContinuation);
      while (localContinuation.isContinue())
      {
        localName = localContinuation.getRemainingName();
        localPartialCompositeDirContext = getPCDirContext(localContinuation);
        localPartialCompositeDirContext.p_bind(localName, paramObject, paramAttributes, localContinuation);
      }
    }
    catch (CannotProceedException localCannotProceedException)
    {
      DirContext localDirContext = DirectoryManager.getContinuationDirContext(localCannotProceedException);
      localDirContext.bind(localCannotProceedException.getRemainingName(), paramObject, paramAttributes);
    }
  }
  
  public void rebind(String paramString, Object paramObject, Attributes paramAttributes)
    throws NamingException
  {
    rebind(new CompositeName(paramString), paramObject, paramAttributes);
  }
  
  public void rebind(Name paramName, Object paramObject, Attributes paramAttributes)
    throws NamingException
  {
    PartialCompositeDirContext localPartialCompositeDirContext = this;
    Hashtable localHashtable = p_getEnvironment();
    Continuation localContinuation = new Continuation(paramName, localHashtable);
    Name localName = paramName;
    try
    {
      localPartialCompositeDirContext.p_rebind(localName, paramObject, paramAttributes, localContinuation);
      while (localContinuation.isContinue())
      {
        localName = localContinuation.getRemainingName();
        localPartialCompositeDirContext = getPCDirContext(localContinuation);
        localPartialCompositeDirContext.p_rebind(localName, paramObject, paramAttributes, localContinuation);
      }
    }
    catch (CannotProceedException localCannotProceedException)
    {
      DirContext localDirContext = DirectoryManager.getContinuationDirContext(localCannotProceedException);
      localDirContext.rebind(localCannotProceedException.getRemainingName(), paramObject, paramAttributes);
    }
  }
  
  public DirContext createSubcontext(String paramString, Attributes paramAttributes)
    throws NamingException
  {
    return createSubcontext(new CompositeName(paramString), paramAttributes);
  }
  
  public DirContext createSubcontext(Name paramName, Attributes paramAttributes)
    throws NamingException
  {
    PartialCompositeDirContext localPartialCompositeDirContext = this;
    Hashtable localHashtable = p_getEnvironment();
    Continuation localContinuation = new Continuation(paramName, localHashtable);
    Name localName = paramName;
    DirContext localDirContext1;
    try
    {
      for (localDirContext1 = localPartialCompositeDirContext.p_createSubcontext(localName, paramAttributes, localContinuation); localContinuation.isContinue(); localDirContext1 = localPartialCompositeDirContext.p_createSubcontext(localName, paramAttributes, localContinuation))
      {
        localName = localContinuation.getRemainingName();
        localPartialCompositeDirContext = getPCDirContext(localContinuation);
      }
    }
    catch (CannotProceedException localCannotProceedException)
    {
      DirContext localDirContext2 = DirectoryManager.getContinuationDirContext(localCannotProceedException);
      localDirContext1 = localDirContext2.createSubcontext(localCannotProceedException.getRemainingName(), paramAttributes);
    }
    return localDirContext1;
  }
  
  public NamingEnumeration<SearchResult> search(String paramString, Attributes paramAttributes)
    throws NamingException
  {
    return search(paramString, paramAttributes, null);
  }
  
  public NamingEnumeration<SearchResult> search(Name paramName, Attributes paramAttributes)
    throws NamingException
  {
    return search(paramName, paramAttributes, null);
  }
  
  public NamingEnumeration<SearchResult> search(String paramString, Attributes paramAttributes, String[] paramArrayOfString)
    throws NamingException
  {
    return search(new CompositeName(paramString), paramAttributes, paramArrayOfString);
  }
  
  public NamingEnumeration<SearchResult> search(Name paramName, Attributes paramAttributes, String[] paramArrayOfString)
    throws NamingException
  {
    PartialCompositeDirContext localPartialCompositeDirContext = this;
    Hashtable localHashtable = p_getEnvironment();
    Continuation localContinuation = new Continuation(paramName, localHashtable);
    Name localName = paramName;
    NamingEnumeration localNamingEnumeration;
    try
    {
      for (localNamingEnumeration = localPartialCompositeDirContext.p_search(localName, paramAttributes, paramArrayOfString, localContinuation); localContinuation.isContinue(); localNamingEnumeration = localPartialCompositeDirContext.p_search(localName, paramAttributes, paramArrayOfString, localContinuation))
      {
        localName = localContinuation.getRemainingName();
        localPartialCompositeDirContext = getPCDirContext(localContinuation);
      }
    }
    catch (CannotProceedException localCannotProceedException)
    {
      DirContext localDirContext = DirectoryManager.getContinuationDirContext(localCannotProceedException);
      localNamingEnumeration = localDirContext.search(localCannotProceedException.getRemainingName(), paramAttributes, paramArrayOfString);
    }
    return localNamingEnumeration;
  }
  
  public NamingEnumeration<SearchResult> search(String paramString1, String paramString2, SearchControls paramSearchControls)
    throws NamingException
  {
    return search(new CompositeName(paramString1), paramString2, paramSearchControls);
  }
  
  public NamingEnumeration<SearchResult> search(Name paramName, String paramString, SearchControls paramSearchControls)
    throws NamingException
  {
    PartialCompositeDirContext localPartialCompositeDirContext = this;
    Hashtable localHashtable = p_getEnvironment();
    Continuation localContinuation = new Continuation(paramName, localHashtable);
    Name localName = paramName;
    NamingEnumeration localNamingEnumeration;
    try
    {
      for (localNamingEnumeration = localPartialCompositeDirContext.p_search(localName, paramString, paramSearchControls, localContinuation); localContinuation.isContinue(); localNamingEnumeration = localPartialCompositeDirContext.p_search(localName, paramString, paramSearchControls, localContinuation))
      {
        localName = localContinuation.getRemainingName();
        localPartialCompositeDirContext = getPCDirContext(localContinuation);
      }
    }
    catch (CannotProceedException localCannotProceedException)
    {
      DirContext localDirContext = DirectoryManager.getContinuationDirContext(localCannotProceedException);
      localNamingEnumeration = localDirContext.search(localCannotProceedException.getRemainingName(), paramString, paramSearchControls);
    }
    return localNamingEnumeration;
  }
  
  public NamingEnumeration<SearchResult> search(String paramString1, String paramString2, Object[] paramArrayOfObject, SearchControls paramSearchControls)
    throws NamingException
  {
    return search(new CompositeName(paramString1), paramString2, paramArrayOfObject, paramSearchControls);
  }
  
  public NamingEnumeration<SearchResult> search(Name paramName, String paramString, Object[] paramArrayOfObject, SearchControls paramSearchControls)
    throws NamingException
  {
    PartialCompositeDirContext localPartialCompositeDirContext = this;
    Hashtable localHashtable = p_getEnvironment();
    Continuation localContinuation = new Continuation(paramName, localHashtable);
    Name localName = paramName;
    NamingEnumeration localNamingEnumeration;
    try
    {
      for (localNamingEnumeration = localPartialCompositeDirContext.p_search(localName, paramString, paramArrayOfObject, paramSearchControls, localContinuation); localContinuation.isContinue(); localNamingEnumeration = localPartialCompositeDirContext.p_search(localName, paramString, paramArrayOfObject, paramSearchControls, localContinuation))
      {
        localName = localContinuation.getRemainingName();
        localPartialCompositeDirContext = getPCDirContext(localContinuation);
      }
    }
    catch (CannotProceedException localCannotProceedException)
    {
      DirContext localDirContext = DirectoryManager.getContinuationDirContext(localCannotProceedException);
      localNamingEnumeration = localDirContext.search(localCannotProceedException.getRemainingName(), paramString, paramArrayOfObject, paramSearchControls);
    }
    return localNamingEnumeration;
  }
  
  public DirContext getSchema(String paramString)
    throws NamingException
  {
    return getSchema(new CompositeName(paramString));
  }
  
  public DirContext getSchema(Name paramName)
    throws NamingException
  {
    PartialCompositeDirContext localPartialCompositeDirContext = this;
    Hashtable localHashtable = p_getEnvironment();
    Continuation localContinuation = new Continuation(paramName, localHashtable);
    Name localName = paramName;
    DirContext localDirContext1;
    try
    {
      for (localDirContext1 = localPartialCompositeDirContext.p_getSchema(localName, localContinuation); localContinuation.isContinue(); localDirContext1 = localPartialCompositeDirContext.p_getSchema(localName, localContinuation))
      {
        localName = localContinuation.getRemainingName();
        localPartialCompositeDirContext = getPCDirContext(localContinuation);
      }
    }
    catch (CannotProceedException localCannotProceedException)
    {
      DirContext localDirContext2 = DirectoryManager.getContinuationDirContext(localCannotProceedException);
      localDirContext1 = localDirContext2.getSchema(localCannotProceedException.getRemainingName());
    }
    return localDirContext1;
  }
  
  public DirContext getSchemaClassDefinition(String paramString)
    throws NamingException
  {
    return getSchemaClassDefinition(new CompositeName(paramString));
  }
  
  public DirContext getSchemaClassDefinition(Name paramName)
    throws NamingException
  {
    PartialCompositeDirContext localPartialCompositeDirContext = this;
    Hashtable localHashtable = p_getEnvironment();
    Continuation localContinuation = new Continuation(paramName, localHashtable);
    Name localName = paramName;
    DirContext localDirContext1;
    try
    {
      for (localDirContext1 = localPartialCompositeDirContext.p_getSchemaClassDefinition(localName, localContinuation); localContinuation.isContinue(); localDirContext1 = localPartialCompositeDirContext.p_getSchemaClassDefinition(localName, localContinuation))
      {
        localName = localContinuation.getRemainingName();
        localPartialCompositeDirContext = getPCDirContext(localContinuation);
      }
    }
    catch (CannotProceedException localCannotProceedException)
    {
      DirContext localDirContext2 = DirectoryManager.getContinuationDirContext(localCannotProceedException);
      localDirContext1 = localDirContext2.getSchemaClassDefinition(localCannotProceedException.getRemainingName());
    }
    return localDirContext1;
  }
  
  protected static PartialCompositeDirContext getPCDirContext(Continuation paramContinuation)
    throws NamingException
  {
    PartialCompositeContext localPartialCompositeContext = PartialCompositeContext.getPCContext(paramContinuation);
    if (!(localPartialCompositeContext instanceof PartialCompositeDirContext)) {
      throw paramContinuation.fillInException(new NotContextException("Resolved object is not a DirContext."));
    }
    return (PartialCompositeDirContext)localPartialCompositeContext;
  }
  
  protected StringHeadTail c_parseComponent(String paramString, Continuation paramContinuation)
    throws NamingException
  {
    OperationNotSupportedException localOperationNotSupportedException = new OperationNotSupportedException();
    throw paramContinuation.fillInException(localOperationNotSupportedException);
  }
  
  protected Object a_lookup(String paramString, Continuation paramContinuation)
    throws NamingException
  {
    OperationNotSupportedException localOperationNotSupportedException = new OperationNotSupportedException();
    throw paramContinuation.fillInException(localOperationNotSupportedException);
  }
  
  protected Object a_lookupLink(String paramString, Continuation paramContinuation)
    throws NamingException
  {
    OperationNotSupportedException localOperationNotSupportedException = new OperationNotSupportedException();
    throw paramContinuation.fillInException(localOperationNotSupportedException);
  }
  
  protected NamingEnumeration<NameClassPair> a_list(Continuation paramContinuation)
    throws NamingException
  {
    OperationNotSupportedException localOperationNotSupportedException = new OperationNotSupportedException();
    throw paramContinuation.fillInException(localOperationNotSupportedException);
  }
  
  protected NamingEnumeration<Binding> a_listBindings(Continuation paramContinuation)
    throws NamingException
  {
    OperationNotSupportedException localOperationNotSupportedException = new OperationNotSupportedException();
    throw paramContinuation.fillInException(localOperationNotSupportedException);
  }
  
  protected void a_bind(String paramString, Object paramObject, Continuation paramContinuation)
    throws NamingException
  {
    OperationNotSupportedException localOperationNotSupportedException = new OperationNotSupportedException();
    throw paramContinuation.fillInException(localOperationNotSupportedException);
  }
  
  protected void a_rebind(String paramString, Object paramObject, Continuation paramContinuation)
    throws NamingException
  {
    OperationNotSupportedException localOperationNotSupportedException = new OperationNotSupportedException();
    throw paramContinuation.fillInException(localOperationNotSupportedException);
  }
  
  protected void a_unbind(String paramString, Continuation paramContinuation)
    throws NamingException
  {
    OperationNotSupportedException localOperationNotSupportedException = new OperationNotSupportedException();
    throw paramContinuation.fillInException(localOperationNotSupportedException);
  }
  
  protected void a_destroySubcontext(String paramString, Continuation paramContinuation)
    throws NamingException
  {
    OperationNotSupportedException localOperationNotSupportedException = new OperationNotSupportedException();
    throw paramContinuation.fillInException(localOperationNotSupportedException);
  }
  
  protected Context a_createSubcontext(String paramString, Continuation paramContinuation)
    throws NamingException
  {
    OperationNotSupportedException localOperationNotSupportedException = new OperationNotSupportedException();
    throw paramContinuation.fillInException(localOperationNotSupportedException);
  }
  
  protected void a_rename(String paramString, Name paramName, Continuation paramContinuation)
    throws NamingException
  {
    OperationNotSupportedException localOperationNotSupportedException = new OperationNotSupportedException();
    throw paramContinuation.fillInException(localOperationNotSupportedException);
  }
  
  protected NameParser a_getNameParser(Continuation paramContinuation)
    throws NamingException
  {
    OperationNotSupportedException localOperationNotSupportedException = new OperationNotSupportedException();
    throw paramContinuation.fillInException(localOperationNotSupportedException);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\toolkit\ctx\PartialCompositeDirContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */