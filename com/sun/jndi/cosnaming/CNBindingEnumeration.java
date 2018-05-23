package com.sun.jndi.cosnaming;

import com.sun.jndi.toolkit.corba.CorbaUtils;
import java.util.Hashtable;
import java.util.NoSuchElementException;
import javax.naming.Name;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.spi.NamingManager;
import org.omg.CosNaming.BindingIterator;
import org.omg.CosNaming.BindingIteratorHolder;
import org.omg.CosNaming.BindingListHolder;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContext;

final class CNBindingEnumeration
  implements NamingEnumeration<javax.naming.Binding>
{
  private static final int DEFAULT_BATCHSIZE = 100;
  private BindingListHolder _bindingList;
  private BindingIterator _bindingIter;
  private int counter;
  private int batchsize = 100;
  private CNCtx _ctx;
  private Hashtable<?, ?> _env;
  private boolean more = false;
  private boolean isLookedUpCtx = false;
  
  CNBindingEnumeration(CNCtx paramCNCtx, boolean paramBoolean, Hashtable<?, ?> paramHashtable)
  {
    String str = paramHashtable != null ? (String)paramHashtable.get("java.naming.batchsize") : null;
    if (str != null) {
      try
      {
        batchsize = Integer.parseInt(str);
      }
      catch (NumberFormatException localNumberFormatException)
      {
        throw new IllegalArgumentException("Batch size not numeric: " + str);
      }
    }
    _ctx = paramCNCtx;
    _ctx.incEnumCount();
    isLookedUpCtx = paramBoolean;
    _env = paramHashtable;
    _bindingList = new BindingListHolder();
    BindingIteratorHolder localBindingIteratorHolder = new BindingIteratorHolder();
    _ctx._nc.list(0, _bindingList, localBindingIteratorHolder);
    _bindingIter = value;
    if (_bindingIter != null) {
      more = _bindingIter.next_n(batchsize, _bindingList);
    } else {
      more = false;
    }
    counter = 0;
  }
  
  public javax.naming.Binding next()
    throws NamingException
  {
    if ((more) && (counter >= _bindingList.value.length)) {
      getMore();
    }
    if ((more) && (counter < _bindingList.value.length))
    {
      org.omg.CosNaming.Binding localBinding = _bindingList.value[counter];
      counter += 1;
      return mapBinding(localBinding);
    }
    throw new NoSuchElementException();
  }
  
  public boolean hasMore()
    throws NamingException
  {
    return (counter < _bindingList.value.length) || (getMore());
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
  
  public javax.naming.Binding nextElement()
  {
    try
    {
      return next();
    }
    catch (NamingException localNamingException)
    {
      throw new NoSuchElementException();
    }
  }
  
  public void close()
    throws NamingException
  {
    more = false;
    if (_bindingIter != null)
    {
      _bindingIter.destroy();
      _bindingIter = null;
    }
    if (_ctx != null)
    {
      _ctx.decEnumCount();
      if (isLookedUpCtx) {
        _ctx.close();
      }
      _ctx = null;
    }
  }
  
  protected void finalize()
  {
    try
    {
      close();
    }
    catch (NamingException localNamingException) {}
  }
  
  private boolean getMore()
    throws NamingException
  {
    try
    {
      more = _bindingIter.next_n(batchsize, _bindingList);
      counter = 0;
    }
    catch (Exception localException)
    {
      more = false;
      NamingException localNamingException = new NamingException("Problem getting binding list");
      localNamingException.setRootCause(localException);
      throw localNamingException;
    }
    return more;
  }
  
  private javax.naming.Binding mapBinding(org.omg.CosNaming.Binding paramBinding)
    throws NamingException
  {
    Object localObject1 = _ctx.callResolve(binding_name);
    Name localName = CNNameParser.cosNameToName(binding_name);
    try
    {
      if (CorbaUtils.isObjectFactoryTrusted(localObject1)) {
        localObject1 = NamingManager.getObjectInstance(localObject1, localName, _ctx, _env);
      }
    }
    catch (NamingException localNamingException)
    {
      throw localNamingException;
    }
    catch (Exception localException)
    {
      localObject2 = new NamingException("problem generating object using object factory");
      ((NamingException)localObject2).setRootCause(localException);
      throw ((Throwable)localObject2);
    }
    String str1 = localName.toString();
    Object localObject2 = new javax.naming.Binding(str1, localObject1);
    NameComponent[] arrayOfNameComponent = _ctx.makeFullName(binding_name);
    String str2 = CNNameParser.cosNameToInsString(arrayOfNameComponent);
    ((javax.naming.Binding)localObject2).setNameInNamespace(str2);
    return (javax.naming.Binding)localObject2;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\cosnaming\CNBindingEnumeration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */