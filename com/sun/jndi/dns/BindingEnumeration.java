package com.sun.jndi.dns;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.NoSuchElementException;
import javax.naming.Binding;
import javax.naming.CompositeName;
import javax.naming.Name;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.spi.DirectoryManager;

final class BindingEnumeration
  extends BaseNameClassPairEnumeration<Binding>
  implements NamingEnumeration<Binding>
{
  BindingEnumeration(DnsContext paramDnsContext, Hashtable<String, NameNode> paramHashtable)
  {
    super(paramDnsContext, paramHashtable);
  }
  
  public Binding next()
    throws NamingException
  {
    if (!hasMore()) {
      throw new NoSuchElementException();
    }
    NameNode localNameNode = (NameNode)nodes.nextElement();
    String str1 = localNameNode.getLabel();
    Name localName1 = new DnsName().add(str1);
    String str2 = localName1.toString();
    Name localName2 = new CompositeName().add(str2);
    String str3 = localName2.toString();
    DnsName localDnsName = ctx.fullyQualify(localName1);
    DnsContext localDnsContext = new DnsContext(ctx, localDnsName);
    try
    {
      Object localObject1 = DirectoryManager.getObjectInstance(localDnsContext, localName2, ctx, environment, null);
      localObject2 = new Binding(str3, localObject1);
      ((Binding)localObject2).setNameInNamespace(ctx.fullyQualify(localName2).toString());
      return (Binding)localObject2;
    }
    catch (Exception localException)
    {
      Object localObject2 = new NamingException("Problem generating object using object factory");
      ((NamingException)localObject2).setRootCause(localException);
      throw ((Throwable)localObject2);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\dns\BindingEnumeration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */