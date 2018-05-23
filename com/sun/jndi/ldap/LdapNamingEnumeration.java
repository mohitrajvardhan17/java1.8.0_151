package com.sun.jndi.ldap;

import com.sun.jndi.toolkit.ctx.Continuation;
import java.util.Vector;
import javax.naming.CompositeName;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.ldap.Control;

final class LdapNamingEnumeration
  extends AbstractLdapNamingEnumeration<NameClassPair>
{
  private static final String defaultClassName = DirContext.class.getName();
  
  LdapNamingEnumeration(LdapCtx paramLdapCtx, LdapResult paramLdapResult, Name paramName, Continuation paramContinuation)
    throws NamingException
  {
    super(paramLdapCtx, paramLdapResult, paramName, paramContinuation);
  }
  
  protected NameClassPair createItem(String paramString, Attributes paramAttributes, Vector<Control> paramVector)
    throws NamingException
  {
    String str = null;
    Attribute localAttribute;
    if ((localAttribute = paramAttributes.get(Obj.JAVA_ATTRIBUTES[2])) != null) {
      str = (String)localAttribute.get();
    } else {
      str = defaultClassName;
    }
    CompositeName localCompositeName = new CompositeName();
    localCompositeName.add(getAtom(paramString));
    Object localObject;
    if (paramVector != null) {
      localObject = new NameClassPairWithControls(localCompositeName.toString(), str, homeCtx.convertControls(paramVector));
    } else {
      localObject = new NameClassPair(localCompositeName.toString(), str);
    }
    ((NameClassPair)localObject).setNameInNamespace(paramString);
    return (NameClassPair)localObject;
  }
  
  protected AbstractLdapNamingEnumeration<? extends NameClassPair> getReferredResults(LdapReferralContext paramLdapReferralContext)
    throws NamingException
  {
    return (AbstractLdapNamingEnumeration)paramLdapReferralContext.list(listArg);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\ldap\LdapNamingEnumeration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */