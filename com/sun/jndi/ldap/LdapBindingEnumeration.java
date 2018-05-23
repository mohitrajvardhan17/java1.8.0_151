package com.sun.jndi.ldap;

import com.sun.jndi.toolkit.ctx.Continuation;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Vector;
import javax.naming.Binding;
import javax.naming.CompositeName;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.ldap.Control;
import javax.naming.spi.DirectoryManager;

final class LdapBindingEnumeration
  extends AbstractLdapNamingEnumeration<Binding>
{
  private final AccessControlContext acc = AccessController.getContext();
  
  LdapBindingEnumeration(LdapCtx paramLdapCtx, LdapResult paramLdapResult, Name paramName, Continuation paramContinuation)
    throws NamingException
  {
    super(paramLdapCtx, paramLdapResult, paramName, paramContinuation);
  }
  
  protected Binding createItem(String paramString, final Attributes paramAttributes, Vector<Control> paramVector)
    throws NamingException
  {
    Object localObject1 = null;
    String str = getAtom(paramString);
    if (paramAttributes.get(Obj.JAVA_ATTRIBUTES[2]) != null) {
      try
      {
        localObject1 = AccessController.doPrivileged(new PrivilegedExceptionAction()
        {
          public Object run()
            throws NamingException
          {
            return Obj.decodeObject(paramAttributes);
          }
        }, acc);
      }
      catch (PrivilegedActionException localPrivilegedActionException)
      {
        throw ((NamingException)localPrivilegedActionException.getException());
      }
    }
    if (localObject1 == null) {
      localObject1 = new LdapCtx(homeCtx, paramString);
    }
    CompositeName localCompositeName = new CompositeName();
    localCompositeName.add(str);
    try
    {
      localObject1 = DirectoryManager.getObjectInstance(localObject1, localCompositeName, homeCtx, homeCtx.envprops, paramAttributes);
    }
    catch (NamingException localNamingException1)
    {
      throw localNamingException1;
    }
    catch (Exception localException)
    {
      NamingException localNamingException2 = new NamingException("problem generating object using object factory");
      localNamingException2.setRootCause(localException);
      throw localNamingException2;
    }
    Object localObject2;
    if (paramVector != null) {
      localObject2 = new BindingWithControls(localCompositeName.toString(), localObject1, homeCtx.convertControls(paramVector));
    } else {
      localObject2 = new Binding(localCompositeName.toString(), localObject1);
    }
    ((Binding)localObject2).setNameInNamespace(paramString);
    return (Binding)localObject2;
  }
  
  protected AbstractLdapNamingEnumeration<? extends NameClassPair> getReferredResults(LdapReferralContext paramLdapReferralContext)
    throws NamingException
  {
    return (AbstractLdapNamingEnumeration)paramLdapReferralContext.listBindings(listArg);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\ldap\LdapBindingEnumeration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */