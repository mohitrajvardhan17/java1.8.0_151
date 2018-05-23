package com.sun.jndi.dns;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.NoSuchElementException;
import javax.naming.CompositeName;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

final class NameClassPairEnumeration
  extends BaseNameClassPairEnumeration<NameClassPair>
  implements NamingEnumeration<NameClassPair>
{
  NameClassPairEnumeration(DnsContext paramDnsContext, Hashtable<String, NameNode> paramHashtable)
  {
    super(paramDnsContext, paramHashtable);
  }
  
  public NameClassPair next()
    throws NamingException
  {
    if (!hasMore()) {
      throw new NoSuchElementException();
    }
    NameNode localNameNode = (NameNode)nodes.nextElement();
    String str1 = (localNameNode.isZoneCut()) || (localNameNode.getChildren() != null) ? "javax.naming.directory.DirContext" : "java.lang.Object";
    String str2 = localNameNode.getLabel();
    Name localName1 = new DnsName().add(str2);
    Name localName2 = new CompositeName().add(localName1.toString());
    NameClassPair localNameClassPair = new NameClassPair(localName2.toString(), str1);
    localNameClassPair.setNameInNamespace(ctx.fullyQualify(localName2).toString());
    return localNameClassPair;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\dns\NameClassPairEnumeration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */