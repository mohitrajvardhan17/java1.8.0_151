package com.sun.jndi.rmi.registry;

import java.util.NoSuchElementException;
import javax.naming.Binding;
import javax.naming.CompositeName;
import javax.naming.Name;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

class BindingEnumeration
  implements NamingEnumeration<Binding>
{
  private RegistryContext ctx;
  private final String[] names;
  private int nextName;
  
  BindingEnumeration(RegistryContext paramRegistryContext, String[] paramArrayOfString)
  {
    ctx = new RegistryContext(paramRegistryContext);
    names = paramArrayOfString;
    nextName = 0;
  }
  
  protected void finalize()
  {
    ctx.close();
  }
  
  public boolean hasMore()
  {
    if (nextName >= names.length) {
      ctx.close();
    }
    return nextName < names.length;
  }
  
  public Binding next()
    throws NamingException
  {
    if (!hasMore()) {
      throw new NoSuchElementException();
    }
    String str1 = names[(nextName++)];
    Name localName = new CompositeName().add(str1);
    Object localObject = ctx.lookup(localName);
    String str2 = localName.toString();
    Binding localBinding = new Binding(str2, localObject);
    localBinding.setNameInNamespace(str2);
    return localBinding;
  }
  
  public boolean hasMoreElements()
  {
    return hasMore();
  }
  
  public Binding nextElement()
  {
    try
    {
      return next();
    }
    catch (NamingException localNamingException)
    {
      throw new NoSuchElementException("javax.naming.NamingException was thrown");
    }
  }
  
  public void close()
  {
    finalize();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\rmi\registry\BindingEnumeration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */