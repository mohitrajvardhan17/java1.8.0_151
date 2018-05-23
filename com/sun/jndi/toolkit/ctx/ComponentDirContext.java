package com.sun.jndi.toolkit.ctx;

import javax.naming.Name;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

public abstract class ComponentDirContext
  extends PartialCompositeDirContext
{
  protected ComponentDirContext()
  {
    _contextType = 2;
  }
  
  protected abstract Attributes c_getAttributes(Name paramName, String[] paramArrayOfString, Continuation paramContinuation)
    throws NamingException;
  
  protected abstract void c_modifyAttributes(Name paramName, int paramInt, Attributes paramAttributes, Continuation paramContinuation)
    throws NamingException;
  
  protected abstract void c_modifyAttributes(Name paramName, ModificationItem[] paramArrayOfModificationItem, Continuation paramContinuation)
    throws NamingException;
  
  protected abstract void c_bind(Name paramName, Object paramObject, Attributes paramAttributes, Continuation paramContinuation)
    throws NamingException;
  
  protected abstract void c_rebind(Name paramName, Object paramObject, Attributes paramAttributes, Continuation paramContinuation)
    throws NamingException;
  
  protected abstract DirContext c_createSubcontext(Name paramName, Attributes paramAttributes, Continuation paramContinuation)
    throws NamingException;
  
  protected abstract NamingEnumeration<SearchResult> c_search(Name paramName, Attributes paramAttributes, String[] paramArrayOfString, Continuation paramContinuation)
    throws NamingException;
  
  protected abstract NamingEnumeration<SearchResult> c_search(Name paramName, String paramString, SearchControls paramSearchControls, Continuation paramContinuation)
    throws NamingException;
  
  protected abstract NamingEnumeration<SearchResult> c_search(Name paramName, String paramString, Object[] paramArrayOfObject, SearchControls paramSearchControls, Continuation paramContinuation)
    throws NamingException;
  
  protected abstract DirContext c_getSchema(Name paramName, Continuation paramContinuation)
    throws NamingException;
  
  protected abstract DirContext c_getSchemaClassDefinition(Name paramName, Continuation paramContinuation)
    throws NamingException;
  
  protected Attributes c_getAttributes_nns(Name paramName, String[] paramArrayOfString, Continuation paramContinuation)
    throws NamingException
  {
    c_processJunction_nns(paramName, paramContinuation);
    return null;
  }
  
  protected void c_modifyAttributes_nns(Name paramName, int paramInt, Attributes paramAttributes, Continuation paramContinuation)
    throws NamingException
  {
    c_processJunction_nns(paramName, paramContinuation);
  }
  
  protected void c_modifyAttributes_nns(Name paramName, ModificationItem[] paramArrayOfModificationItem, Continuation paramContinuation)
    throws NamingException
  {
    c_processJunction_nns(paramName, paramContinuation);
  }
  
  protected void c_bind_nns(Name paramName, Object paramObject, Attributes paramAttributes, Continuation paramContinuation)
    throws NamingException
  {
    c_processJunction_nns(paramName, paramContinuation);
  }
  
  protected void c_rebind_nns(Name paramName, Object paramObject, Attributes paramAttributes, Continuation paramContinuation)
    throws NamingException
  {
    c_processJunction_nns(paramName, paramContinuation);
  }
  
  protected DirContext c_createSubcontext_nns(Name paramName, Attributes paramAttributes, Continuation paramContinuation)
    throws NamingException
  {
    c_processJunction_nns(paramName, paramContinuation);
    return null;
  }
  
  protected NamingEnumeration<SearchResult> c_search_nns(Name paramName, Attributes paramAttributes, String[] paramArrayOfString, Continuation paramContinuation)
    throws NamingException
  {
    c_processJunction_nns(paramName, paramContinuation);
    return null;
  }
  
  protected NamingEnumeration<SearchResult> c_search_nns(Name paramName, String paramString, SearchControls paramSearchControls, Continuation paramContinuation)
    throws NamingException
  {
    c_processJunction_nns(paramName, paramContinuation);
    return null;
  }
  
  protected NamingEnumeration<SearchResult> c_search_nns(Name paramName, String paramString, Object[] paramArrayOfObject, SearchControls paramSearchControls, Continuation paramContinuation)
    throws NamingException
  {
    c_processJunction_nns(paramName, paramContinuation);
    return null;
  }
  
  protected DirContext c_getSchema_nns(Name paramName, Continuation paramContinuation)
    throws NamingException
  {
    c_processJunction_nns(paramName, paramContinuation);
    return null;
  }
  
  protected DirContext c_getSchemaClassDefinition_nns(Name paramName, Continuation paramContinuation)
    throws NamingException
  {
    c_processJunction_nns(paramName, paramContinuation);
    return null;
  }
  
  protected Attributes p_getAttributes(Name paramName, String[] paramArrayOfString, Continuation paramContinuation)
    throws NamingException
  {
    HeadTail localHeadTail = p_resolveIntermediate(paramName, paramContinuation);
    Attributes localAttributes = null;
    switch (localHeadTail.getStatus())
    {
    case 3: 
      localAttributes = c_getAttributes_nns(localHeadTail.getHead(), paramArrayOfString, paramContinuation);
      break;
    case 2: 
      localAttributes = c_getAttributes(localHeadTail.getHead(), paramArrayOfString, paramContinuation);
      break;
    }
    return localAttributes;
  }
  
  protected void p_modifyAttributes(Name paramName, int paramInt, Attributes paramAttributes, Continuation paramContinuation)
    throws NamingException
  {
    HeadTail localHeadTail = p_resolveIntermediate(paramName, paramContinuation);
    switch (localHeadTail.getStatus())
    {
    case 3: 
      c_modifyAttributes_nns(localHeadTail.getHead(), paramInt, paramAttributes, paramContinuation);
      break;
    case 2: 
      c_modifyAttributes(localHeadTail.getHead(), paramInt, paramAttributes, paramContinuation);
      break;
    }
  }
  
  protected void p_modifyAttributes(Name paramName, ModificationItem[] paramArrayOfModificationItem, Continuation paramContinuation)
    throws NamingException
  {
    HeadTail localHeadTail = p_resolveIntermediate(paramName, paramContinuation);
    switch (localHeadTail.getStatus())
    {
    case 3: 
      c_modifyAttributes_nns(localHeadTail.getHead(), paramArrayOfModificationItem, paramContinuation);
      break;
    case 2: 
      c_modifyAttributes(localHeadTail.getHead(), paramArrayOfModificationItem, paramContinuation);
      break;
    }
  }
  
  protected void p_bind(Name paramName, Object paramObject, Attributes paramAttributes, Continuation paramContinuation)
    throws NamingException
  {
    HeadTail localHeadTail = p_resolveIntermediate(paramName, paramContinuation);
    switch (localHeadTail.getStatus())
    {
    case 3: 
      c_bind_nns(localHeadTail.getHead(), paramObject, paramAttributes, paramContinuation);
      break;
    case 2: 
      c_bind(localHeadTail.getHead(), paramObject, paramAttributes, paramContinuation);
      break;
    }
  }
  
  protected void p_rebind(Name paramName, Object paramObject, Attributes paramAttributes, Continuation paramContinuation)
    throws NamingException
  {
    HeadTail localHeadTail = p_resolveIntermediate(paramName, paramContinuation);
    switch (localHeadTail.getStatus())
    {
    case 3: 
      c_rebind_nns(localHeadTail.getHead(), paramObject, paramAttributes, paramContinuation);
      break;
    case 2: 
      c_rebind(localHeadTail.getHead(), paramObject, paramAttributes, paramContinuation);
      break;
    }
  }
  
  protected DirContext p_createSubcontext(Name paramName, Attributes paramAttributes, Continuation paramContinuation)
    throws NamingException
  {
    HeadTail localHeadTail = p_resolveIntermediate(paramName, paramContinuation);
    DirContext localDirContext = null;
    switch (localHeadTail.getStatus())
    {
    case 3: 
      localDirContext = c_createSubcontext_nns(localHeadTail.getHead(), paramAttributes, paramContinuation);
      break;
    case 2: 
      localDirContext = c_createSubcontext(localHeadTail.getHead(), paramAttributes, paramContinuation);
      break;
    }
    return localDirContext;
  }
  
  protected NamingEnumeration<SearchResult> p_search(Name paramName, Attributes paramAttributes, String[] paramArrayOfString, Continuation paramContinuation)
    throws NamingException
  {
    HeadTail localHeadTail = p_resolveIntermediate(paramName, paramContinuation);
    NamingEnumeration localNamingEnumeration = null;
    switch (localHeadTail.getStatus())
    {
    case 3: 
      localNamingEnumeration = c_search_nns(localHeadTail.getHead(), paramAttributes, paramArrayOfString, paramContinuation);
      break;
    case 2: 
      localNamingEnumeration = c_search(localHeadTail.getHead(), paramAttributes, paramArrayOfString, paramContinuation);
      break;
    }
    return localNamingEnumeration;
  }
  
  protected NamingEnumeration<SearchResult> p_search(Name paramName, String paramString, SearchControls paramSearchControls, Continuation paramContinuation)
    throws NamingException
  {
    HeadTail localHeadTail = p_resolveIntermediate(paramName, paramContinuation);
    NamingEnumeration localNamingEnumeration = null;
    switch (localHeadTail.getStatus())
    {
    case 3: 
      localNamingEnumeration = c_search_nns(localHeadTail.getHead(), paramString, paramSearchControls, paramContinuation);
      break;
    case 2: 
      localNamingEnumeration = c_search(localHeadTail.getHead(), paramString, paramSearchControls, paramContinuation);
      break;
    }
    return localNamingEnumeration;
  }
  
  protected NamingEnumeration<SearchResult> p_search(Name paramName, String paramString, Object[] paramArrayOfObject, SearchControls paramSearchControls, Continuation paramContinuation)
    throws NamingException
  {
    HeadTail localHeadTail = p_resolveIntermediate(paramName, paramContinuation);
    NamingEnumeration localNamingEnumeration = null;
    switch (localHeadTail.getStatus())
    {
    case 3: 
      localNamingEnumeration = c_search_nns(localHeadTail.getHead(), paramString, paramArrayOfObject, paramSearchControls, paramContinuation);
      break;
    case 2: 
      localNamingEnumeration = c_search(localHeadTail.getHead(), paramString, paramArrayOfObject, paramSearchControls, paramContinuation);
      break;
    }
    return localNamingEnumeration;
  }
  
  protected DirContext p_getSchema(Name paramName, Continuation paramContinuation)
    throws NamingException
  {
    DirContext localDirContext = null;
    HeadTail localHeadTail = p_resolveIntermediate(paramName, paramContinuation);
    switch (localHeadTail.getStatus())
    {
    case 3: 
      localDirContext = c_getSchema_nns(localHeadTail.getHead(), paramContinuation);
      break;
    case 2: 
      localDirContext = c_getSchema(localHeadTail.getHead(), paramContinuation);
      break;
    }
    return localDirContext;
  }
  
  protected DirContext p_getSchemaClassDefinition(Name paramName, Continuation paramContinuation)
    throws NamingException
  {
    DirContext localDirContext = null;
    HeadTail localHeadTail = p_resolveIntermediate(paramName, paramContinuation);
    switch (localHeadTail.getStatus())
    {
    case 3: 
      localDirContext = c_getSchemaClassDefinition_nns(localHeadTail.getHead(), paramContinuation);
      break;
    case 2: 
      localDirContext = c_getSchemaClassDefinition(localHeadTail.getHead(), paramContinuation);
      break;
    }
    return localDirContext;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\toolkit\ctx\ComponentDirContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */