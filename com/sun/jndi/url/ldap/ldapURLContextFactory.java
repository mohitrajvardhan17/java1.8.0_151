package com.sun.jndi.url.ldap;

import com.sun.jndi.ldap.LdapCtx;
import com.sun.jndi.ldap.LdapCtxFactory;
import com.sun.jndi.ldap.LdapURL;
import java.util.Hashtable;
import javax.naming.CompositeName;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.spi.ObjectFactory;
import javax.naming.spi.ResolveResult;

public class ldapURLContextFactory
  implements ObjectFactory
{
  public ldapURLContextFactory() {}
  
  public Object getObjectInstance(Object paramObject, Name paramName, Context paramContext, Hashtable<?, ?> paramHashtable)
    throws Exception
  {
    if (paramObject == null) {
      return new ldapURLContext(paramHashtable);
    }
    return LdapCtxFactory.getLdapCtxInstance(paramObject, paramHashtable);
  }
  
  static ResolveResult getUsingURLIgnoreRootDN(String paramString, Hashtable<?, ?> paramHashtable)
    throws NamingException
  {
    LdapURL localLdapURL = new LdapURL(paramString);
    LdapCtx localLdapCtx = new LdapCtx("", localLdapURL.getHost(), localLdapURL.getPort(), paramHashtable, localLdapURL.useSsl());
    String str = localLdapURL.getDN() != null ? localLdapURL.getDN() : "";
    CompositeName localCompositeName = new CompositeName();
    if (!"".equals(str)) {
      localCompositeName.add(str);
    }
    return new ResolveResult(localLdapCtx, localCompositeName);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\url\ldap\ldapURLContextFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */