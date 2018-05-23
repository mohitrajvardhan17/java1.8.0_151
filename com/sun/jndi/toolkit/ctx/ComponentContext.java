package com.sun.jndi.toolkit.ctx;

import java.io.PrintStream;
import javax.naming.Binding;
import javax.naming.CompositeName;
import javax.naming.Context;
import javax.naming.InvalidNameException;
import javax.naming.LinkRef;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ResolveResult;

public abstract class ComponentContext
  extends PartialCompositeContext
{
  private static int debug = 0;
  protected static final byte USE_CONTINUATION = 1;
  protected static final byte TERMINAL_COMPONENT = 2;
  protected static final byte TERMINAL_NNS_COMPONENT = 3;
  
  protected ComponentContext()
  {
    _contextType = 2;
  }
  
  protected abstract Object c_lookup(Name paramName, Continuation paramContinuation)
    throws NamingException;
  
  protected abstract Object c_lookupLink(Name paramName, Continuation paramContinuation)
    throws NamingException;
  
  protected abstract NamingEnumeration<NameClassPair> c_list(Name paramName, Continuation paramContinuation)
    throws NamingException;
  
  protected abstract NamingEnumeration<Binding> c_listBindings(Name paramName, Continuation paramContinuation)
    throws NamingException;
  
  protected abstract void c_bind(Name paramName, Object paramObject, Continuation paramContinuation)
    throws NamingException;
  
  protected abstract void c_rebind(Name paramName, Object paramObject, Continuation paramContinuation)
    throws NamingException;
  
  protected abstract void c_unbind(Name paramName, Continuation paramContinuation)
    throws NamingException;
  
  protected abstract void c_destroySubcontext(Name paramName, Continuation paramContinuation)
    throws NamingException;
  
  protected abstract Context c_createSubcontext(Name paramName, Continuation paramContinuation)
    throws NamingException;
  
  protected abstract void c_rename(Name paramName1, Name paramName2, Continuation paramContinuation)
    throws NamingException;
  
  protected abstract NameParser c_getNameParser(Name paramName, Continuation paramContinuation)
    throws NamingException;
  
  protected HeadTail p_parseComponent(Name paramName, Continuation paramContinuation)
    throws NamingException
  {
    int i;
    if ((paramName.isEmpty()) || (paramName.get(0).equals(""))) {
      i = 0;
    } else {
      i = 1;
    }
    Name localName1;
    Name localName2;
    if ((paramName instanceof CompositeName))
    {
      localName1 = paramName.getPrefix(i);
      localName2 = paramName.getSuffix(i);
    }
    else
    {
      localName1 = new CompositeName().add(paramName.toString());
      localName2 = null;
    }
    if (debug > 2)
    {
      System.err.println("ORIG: " + paramName);
      System.err.println("PREFIX: " + paramName);
      System.err.println("SUFFIX: " + null);
    }
    return new HeadTail(localName1, localName2);
  }
  
  protected Object c_resolveIntermediate_nns(Name paramName, Continuation paramContinuation)
    throws NamingException
  {
    try
    {
      final Object localObject = c_lookup(paramName, paramContinuation);
      if ((localObject != null) && (getClass().isInstance(localObject)))
      {
        paramContinuation.setContinueNNS(localObject, paramName, this);
        return null;
      }
      if ((localObject != null) && (!(localObject instanceof Context)))
      {
        RefAddr local1 = new RefAddr("nns")
        {
          private static final long serialVersionUID = -8831204798861786362L;
          
          public Object getContent()
          {
            return localObject;
          }
        };
        Reference localReference = new Reference("java.lang.Object", local1);
        CompositeName localCompositeName = (CompositeName)paramName.clone();
        localCompositeName.add("");
        paramContinuation.setContinue(localReference, localCompositeName, this);
        return null;
      }
      return localObject;
    }
    catch (NamingException localNamingException)
    {
      localNamingException.appendRemainingComponent("");
      throw localNamingException;
    }
  }
  
  protected Object c_lookup_nns(Name paramName, Continuation paramContinuation)
    throws NamingException
  {
    c_processJunction_nns(paramName, paramContinuation);
    return null;
  }
  
  protected Object c_lookupLink_nns(Name paramName, Continuation paramContinuation)
    throws NamingException
  {
    c_processJunction_nns(paramName, paramContinuation);
    return null;
  }
  
  protected NamingEnumeration<NameClassPair> c_list_nns(Name paramName, Continuation paramContinuation)
    throws NamingException
  {
    c_processJunction_nns(paramName, paramContinuation);
    return null;
  }
  
  protected NamingEnumeration<Binding> c_listBindings_nns(Name paramName, Continuation paramContinuation)
    throws NamingException
  {
    c_processJunction_nns(paramName, paramContinuation);
    return null;
  }
  
  protected void c_bind_nns(Name paramName, Object paramObject, Continuation paramContinuation)
    throws NamingException
  {
    c_processJunction_nns(paramName, paramContinuation);
  }
  
  protected void c_rebind_nns(Name paramName, Object paramObject, Continuation paramContinuation)
    throws NamingException
  {
    c_processJunction_nns(paramName, paramContinuation);
  }
  
  protected void c_unbind_nns(Name paramName, Continuation paramContinuation)
    throws NamingException
  {
    c_processJunction_nns(paramName, paramContinuation);
  }
  
  protected Context c_createSubcontext_nns(Name paramName, Continuation paramContinuation)
    throws NamingException
  {
    c_processJunction_nns(paramName, paramContinuation);
    return null;
  }
  
  protected void c_destroySubcontext_nns(Name paramName, Continuation paramContinuation)
    throws NamingException
  {
    c_processJunction_nns(paramName, paramContinuation);
  }
  
  protected void c_rename_nns(Name paramName1, Name paramName2, Continuation paramContinuation)
    throws NamingException
  {
    c_processJunction_nns(paramName1, paramContinuation);
  }
  
  protected NameParser c_getNameParser_nns(Name paramName, Continuation paramContinuation)
    throws NamingException
  {
    c_processJunction_nns(paramName, paramContinuation);
    return null;
  }
  
  protected void c_processJunction_nns(Name paramName, Continuation paramContinuation)
    throws NamingException
  {
    Object localObject;
    if (paramName.isEmpty())
    {
      localObject = new RefAddr("nns")
      {
        private static final long serialVersionUID = -1389472957988053402L;
        
        public Object getContent()
        {
          return ComponentContext.this;
        }
      };
      Reference localReference = new Reference("java.lang.Object", (RefAddr)localObject);
      paramContinuation.setContinue(localReference, _NNS_NAME, this);
      return;
    }
    try
    {
      localObject = c_lookup(paramName, paramContinuation);
      if (paramContinuation.isContinue()) {
        paramContinuation.appendRemainingComponent("");
      } else {
        paramContinuation.setContinueNNS(localObject, paramName, this);
      }
    }
    catch (NamingException localNamingException)
    {
      localNamingException.appendRemainingComponent("");
      throw localNamingException;
    }
  }
  
  protected HeadTail p_resolveIntermediate(Name paramName, Continuation paramContinuation)
    throws NamingException
  {
    int i = 1;
    paramContinuation.setSuccess();
    HeadTail localHeadTail = p_parseComponent(paramName, paramContinuation);
    Name localName1 = localHeadTail.getTail();
    Name localName2 = localHeadTail.getHead();
    if ((localName1 == null) || (localName1.isEmpty()))
    {
      i = 2;
    }
    else if (!localName1.get(0).equals(""))
    {
      try
      {
        Object localObject1 = c_resolveIntermediate_nns(localName2, paramContinuation);
        if (localObject1 != null)
        {
          paramContinuation.setContinue(localObject1, localName2, this, localName1);
        }
        else if (paramContinuation.isContinue())
        {
          checkAndAdjustRemainingName(paramContinuation.getRemainingName());
          paramContinuation.appendRemainingName(localName1);
        }
      }
      catch (NamingException localNamingException1)
      {
        checkAndAdjustRemainingName(localNamingException1.getRemainingName());
        localNamingException1.appendRemainingName(localName1);
        throw localNamingException1;
      }
    }
    else if (localName1.size() == 1)
    {
      i = 3;
    }
    else
    {
      Object localObject2;
      if ((localName2.isEmpty()) || (isAllEmpty(localName1)))
      {
        localObject2 = localName1.getSuffix(1);
        try
        {
          Object localObject3 = c_lookup_nns(localName2, paramContinuation);
          if (localObject3 != null) {
            paramContinuation.setContinue(localObject3, localName2, this, (Name)localObject2);
          } else if (paramContinuation.isContinue()) {
            paramContinuation.appendRemainingName((Name)localObject2);
          }
        }
        catch (NamingException localNamingException3)
        {
          localNamingException3.appendRemainingName((Name)localObject2);
          throw localNamingException3;
        }
      }
      else
      {
        try
        {
          localObject2 = c_resolveIntermediate_nns(localName2, paramContinuation);
          if (localObject2 != null)
          {
            paramContinuation.setContinue(localObject2, localName2, this, localName1);
          }
          else if (paramContinuation.isContinue())
          {
            checkAndAdjustRemainingName(paramContinuation.getRemainingName());
            paramContinuation.appendRemainingName(localName1);
          }
        }
        catch (NamingException localNamingException2)
        {
          checkAndAdjustRemainingName(localNamingException2.getRemainingName());
          localNamingException2.appendRemainingName(localName1);
          throw localNamingException2;
        }
      }
    }
    localHeadTail.setStatus(i);
    return localHeadTail;
  }
  
  void checkAndAdjustRemainingName(Name paramName)
    throws InvalidNameException
  {
    int i;
    if ((paramName != null) && ((i = paramName.size()) > 1) && (paramName.get(i - 1).equals(""))) {
      paramName.remove(i - 1);
    }
  }
  
  protected boolean isAllEmpty(Name paramName)
  {
    int i = paramName.size();
    for (int j = 0; j < i; j++) {
      if (!paramName.get(j).equals("")) {
        return false;
      }
    }
    return true;
  }
  
  protected ResolveResult p_resolveToClass(Name paramName, Class<?> paramClass, Continuation paramContinuation)
    throws NamingException
  {
    if (paramClass.isInstance(this))
    {
      paramContinuation.setSuccess();
      return new ResolveResult(this, paramName);
    }
    ResolveResult localResolveResult = null;
    HeadTail localHeadTail = p_resolveIntermediate(paramName, paramContinuation);
    switch (localHeadTail.getStatus())
    {
    case 3: 
      Object localObject = p_lookup(paramName, paramContinuation);
      if ((!paramContinuation.isContinue()) && (paramClass.isInstance(localObject))) {
        localResolveResult = new ResolveResult(localObject, _EMPTY_NAME);
      }
      break;
    case 2: 
      paramContinuation.setSuccess();
      break;
    }
    return localResolveResult;
  }
  
  protected Object p_lookup(Name paramName, Continuation paramContinuation)
    throws NamingException
  {
    Object localObject = null;
    HeadTail localHeadTail = p_resolveIntermediate(paramName, paramContinuation);
    switch (localHeadTail.getStatus())
    {
    case 3: 
      localObject = c_lookup_nns(localHeadTail.getHead(), paramContinuation);
      if ((localObject instanceof LinkRef))
      {
        paramContinuation.setContinue(localObject, localHeadTail.getHead(), this);
        localObject = null;
      }
      break;
    case 2: 
      localObject = c_lookup(localHeadTail.getHead(), paramContinuation);
      if ((localObject instanceof LinkRef))
      {
        paramContinuation.setContinue(localObject, localHeadTail.getHead(), this);
        localObject = null;
      }
      break;
    }
    return localObject;
  }
  
  protected NamingEnumeration<NameClassPair> p_list(Name paramName, Continuation paramContinuation)
    throws NamingException
  {
    NamingEnumeration localNamingEnumeration = null;
    HeadTail localHeadTail = p_resolveIntermediate(paramName, paramContinuation);
    switch (localHeadTail.getStatus())
    {
    case 3: 
      if (debug > 0) {
        System.out.println("c_list_nns(" + localHeadTail.getHead() + ")");
      }
      localNamingEnumeration = c_list_nns(localHeadTail.getHead(), paramContinuation);
      break;
    case 2: 
      if (debug > 0) {
        System.out.println("c_list(" + localHeadTail.getHead() + ")");
      }
      localNamingEnumeration = c_list(localHeadTail.getHead(), paramContinuation);
      break;
    }
    return localNamingEnumeration;
  }
  
  protected NamingEnumeration<Binding> p_listBindings(Name paramName, Continuation paramContinuation)
    throws NamingException
  {
    NamingEnumeration localNamingEnumeration = null;
    HeadTail localHeadTail = p_resolveIntermediate(paramName, paramContinuation);
    switch (localHeadTail.getStatus())
    {
    case 3: 
      localNamingEnumeration = c_listBindings_nns(localHeadTail.getHead(), paramContinuation);
      break;
    case 2: 
      localNamingEnumeration = c_listBindings(localHeadTail.getHead(), paramContinuation);
      break;
    }
    return localNamingEnumeration;
  }
  
  protected void p_bind(Name paramName, Object paramObject, Continuation paramContinuation)
    throws NamingException
  {
    HeadTail localHeadTail = p_resolveIntermediate(paramName, paramContinuation);
    switch (localHeadTail.getStatus())
    {
    case 3: 
      c_bind_nns(localHeadTail.getHead(), paramObject, paramContinuation);
      break;
    case 2: 
      c_bind(localHeadTail.getHead(), paramObject, paramContinuation);
      break;
    }
  }
  
  protected void p_rebind(Name paramName, Object paramObject, Continuation paramContinuation)
    throws NamingException
  {
    HeadTail localHeadTail = p_resolveIntermediate(paramName, paramContinuation);
    switch (localHeadTail.getStatus())
    {
    case 3: 
      c_rebind_nns(localHeadTail.getHead(), paramObject, paramContinuation);
      break;
    case 2: 
      c_rebind(localHeadTail.getHead(), paramObject, paramContinuation);
      break;
    }
  }
  
  protected void p_unbind(Name paramName, Continuation paramContinuation)
    throws NamingException
  {
    HeadTail localHeadTail = p_resolveIntermediate(paramName, paramContinuation);
    switch (localHeadTail.getStatus())
    {
    case 3: 
      c_unbind_nns(localHeadTail.getHead(), paramContinuation);
      break;
    case 2: 
      c_unbind(localHeadTail.getHead(), paramContinuation);
      break;
    }
  }
  
  protected void p_destroySubcontext(Name paramName, Continuation paramContinuation)
    throws NamingException
  {
    HeadTail localHeadTail = p_resolveIntermediate(paramName, paramContinuation);
    switch (localHeadTail.getStatus())
    {
    case 3: 
      c_destroySubcontext_nns(localHeadTail.getHead(), paramContinuation);
      break;
    case 2: 
      c_destroySubcontext(localHeadTail.getHead(), paramContinuation);
      break;
    }
  }
  
  protected Context p_createSubcontext(Name paramName, Continuation paramContinuation)
    throws NamingException
  {
    Context localContext = null;
    HeadTail localHeadTail = p_resolveIntermediate(paramName, paramContinuation);
    switch (localHeadTail.getStatus())
    {
    case 3: 
      localContext = c_createSubcontext_nns(localHeadTail.getHead(), paramContinuation);
      break;
    case 2: 
      localContext = c_createSubcontext(localHeadTail.getHead(), paramContinuation);
      break;
    }
    return localContext;
  }
  
  protected void p_rename(Name paramName1, Name paramName2, Continuation paramContinuation)
    throws NamingException
  {
    HeadTail localHeadTail = p_resolveIntermediate(paramName1, paramContinuation);
    switch (localHeadTail.getStatus())
    {
    case 3: 
      c_rename_nns(localHeadTail.getHead(), paramName2, paramContinuation);
      break;
    case 2: 
      c_rename(localHeadTail.getHead(), paramName2, paramContinuation);
      break;
    }
  }
  
  protected NameParser p_getNameParser(Name paramName, Continuation paramContinuation)
    throws NamingException
  {
    NameParser localNameParser = null;
    HeadTail localHeadTail = p_resolveIntermediate(paramName, paramContinuation);
    switch (localHeadTail.getStatus())
    {
    case 3: 
      localNameParser = c_getNameParser_nns(localHeadTail.getHead(), paramContinuation);
      break;
    case 2: 
      localNameParser = c_getNameParser(localHeadTail.getHead(), paramContinuation);
      break;
    }
    return localNameParser;
  }
  
  protected Object p_lookupLink(Name paramName, Continuation paramContinuation)
    throws NamingException
  {
    Object localObject = null;
    HeadTail localHeadTail = p_resolveIntermediate(paramName, paramContinuation);
    switch (localHeadTail.getStatus())
    {
    case 3: 
      localObject = c_lookupLink_nns(localHeadTail.getHead(), paramContinuation);
      break;
    case 2: 
      localObject = c_lookupLink(localHeadTail.getHead(), paramContinuation);
      break;
    }
    return localObject;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\toolkit\ctx\ComponentContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */