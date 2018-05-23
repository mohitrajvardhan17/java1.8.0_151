package com.sun.jndi.ldap;

import com.sun.jndi.toolkit.ctx.Continuation;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Vector;
import javax.naming.CompositeName;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.LdapName;
import javax.naming.spi.DirectoryManager;

final class LdapSearchEnumeration
  extends AbstractLdapNamingEnumeration<SearchResult>
{
  private Name startName;
  private LdapCtx.SearchArgs searchArgs = null;
  private final AccessControlContext acc = AccessController.getContext();
  
  LdapSearchEnumeration(LdapCtx paramLdapCtx, LdapResult paramLdapResult, String paramString, LdapCtx.SearchArgs paramSearchArgs, Continuation paramContinuation)
    throws NamingException
  {
    super(paramLdapCtx, paramLdapResult, name, paramContinuation);
    startName = new LdapName(paramString);
    searchArgs = paramSearchArgs;
  }
  
  protected SearchResult createItem(String paramString, final Attributes paramAttributes, Vector<Control> paramVector)
    throws NamingException
  {
    Object localObject1 = null;
    boolean bool = true;
    String str1;
    String str2;
    try
    {
      LdapName localLdapName = new LdapName(paramString);
      if ((startName != null) && (localLdapName.startsWith(startName)))
      {
        str1 = localLdapName.getSuffix(startName.size()).toString();
        str2 = localLdapName.getSuffix(homeCtx.currentParsedDN.size()).toString();
      }
      else
      {
        bool = false;
        str2 = str1 = LdapURL.toUrlString(homeCtx.hostname, homeCtx.port_number, paramString, homeCtx.hasLdapsScheme);
      }
    }
    catch (NamingException localNamingException1)
    {
      bool = false;
      str2 = str1 = LdapURL.toUrlString(homeCtx.hostname, homeCtx.port_number, paramString, homeCtx.hasLdapsScheme);
    }
    CompositeName localCompositeName1 = new CompositeName();
    if (!str1.equals("")) {
      localCompositeName1.add(str1);
    }
    CompositeName localCompositeName2 = new CompositeName();
    if (!str2.equals("")) {
      localCompositeName2.add(str2);
    }
    homeCtx.setParents(paramAttributes, localCompositeName2);
    Object localObject2;
    if (searchArgs.cons.getReturningObjFlag())
    {
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
      Object localObject3;
      try
      {
        localObject1 = DirectoryManager.getObjectInstance(localObject1, localCompositeName2, bool ? homeCtx : null, homeCtx.envprops, paramAttributes);
      }
      catch (NamingException localNamingException2)
      {
        throw localNamingException2;
      }
      catch (Exception localException)
      {
        localObject3 = new NamingException("problem generating object using object factory");
        ((NamingException)localObject3).setRootCause(localException);
        throw ((Throwable)localObject3);
      }
      if ((localObject2 = searchArgs.reqAttrs) != null)
      {
        localObject3 = new BasicAttributes(true);
        for (int i = 0; i < localObject2.length; i++) {
          ((Attributes)localObject3).put(localObject2[i], null);
        }
        for (i = 0; i < Obj.JAVA_ATTRIBUTES.length; i++) {
          if (((Attributes)localObject3).get(Obj.JAVA_ATTRIBUTES[i]) == null) {
            paramAttributes.remove(Obj.JAVA_ATTRIBUTES[i]);
          }
        }
      }
    }
    if (paramVector != null) {
      localObject2 = new SearchResultWithControls(bool ? localCompositeName1.toString() : str1, localObject1, paramAttributes, bool, homeCtx.convertControls(paramVector));
    } else {
      localObject2 = new SearchResult(bool ? localCompositeName1.toString() : str1, localObject1, paramAttributes, bool);
    }
    ((SearchResult)localObject2).setNameInNamespace(paramString);
    return (SearchResult)localObject2;
  }
  
  public void appendUnprocessedReferrals(LdapReferralException paramLdapReferralException)
  {
    startName = null;
    super.appendUnprocessedReferrals(paramLdapReferralException);
  }
  
  protected AbstractLdapNamingEnumeration<? extends NameClassPair> getReferredResults(LdapReferralContext paramLdapReferralContext)
    throws NamingException
  {
    return (AbstractLdapNamingEnumeration)paramLdapReferralContext.search(searchArgs.name, searchArgs.filter, searchArgs.cons);
  }
  
  protected void update(AbstractLdapNamingEnumeration<? extends NameClassPair> paramAbstractLdapNamingEnumeration)
  {
    super.update(paramAbstractLdapNamingEnumeration);
    LdapSearchEnumeration localLdapSearchEnumeration = (LdapSearchEnumeration)paramAbstractLdapNamingEnumeration;
    startName = startName;
  }
  
  void setStartName(Name paramName)
  {
    startName = paramName;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\ldap\LdapSearchEnumeration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */