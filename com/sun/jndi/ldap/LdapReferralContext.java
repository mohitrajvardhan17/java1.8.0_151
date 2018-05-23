package com.sun.jndi.ldap;

import com.sun.jndi.toolkit.dir.SearchFilter;
import java.util.Hashtable;
import java.util.StringTokenizer;
import javax.naming.Binding;
import javax.naming.CompositeName;
import javax.naming.Context;
import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.NotContextException;
import javax.naming.Reference;
import javax.naming.StringRefAddr;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.ExtendedRequest;
import javax.naming.ldap.ExtendedResponse;
import javax.naming.ldap.LdapContext;
import javax.naming.spi.NamingManager;

final class LdapReferralContext
  implements DirContext, LdapContext
{
  private DirContext refCtx = null;
  private Name urlName = null;
  private String urlAttrs = null;
  private String urlScope = null;
  private String urlFilter = null;
  private LdapReferralException refEx = null;
  private boolean skipThisReferral = false;
  private int hopCount = 1;
  private NamingException previousEx = null;
  
  LdapReferralContext(LdapReferralException paramLdapReferralException, Hashtable<?, ?> paramHashtable, Control[] paramArrayOfControl1, Control[] paramArrayOfControl2, String paramString, boolean paramBoolean, int paramInt)
    throws NamingException
  {
    refEx = paramLdapReferralException;
    if ((skipThisReferral = paramBoolean)) {
      return;
    }
    if (paramHashtable != null)
    {
      paramHashtable = (Hashtable)paramHashtable.clone();
      if (paramArrayOfControl1 == null) {
        paramHashtable.remove("java.naming.ldap.control.connect");
      }
    }
    else if (paramArrayOfControl1 != null)
    {
      paramHashtable = new Hashtable(5);
    }
    if (paramArrayOfControl1 != null)
    {
      Control[] arrayOfControl = new Control[paramArrayOfControl1.length];
      System.arraycopy(paramArrayOfControl1, 0, arrayOfControl, 0, paramArrayOfControl1.length);
      paramHashtable.put("java.naming.ldap.control.connect", arrayOfControl);
    }
    String str;
    Object localObject;
    for (;;)
    {
      try
      {
        str = refEx.getNextReferral();
        if (str == null)
        {
          if (previousEx != null) {
            throw ((NamingException)previousEx.fillInStackTrace());
          }
          throw new NamingException("Illegal encoding: referral is empty");
        }
      }
      catch (LdapReferralException localLdapReferralException)
      {
        if (paramInt == 2) {
          throw localLdapReferralException;
        }
        refEx = localLdapReferralException;
      }
      continue;
      Reference localReference = new Reference("javax.naming.directory.DirContext", new StringRefAddr("URL", str));
      try
      {
        localObject = NamingManager.getObjectInstance(localReference, null, null, paramHashtable);
      }
      catch (NamingException localNamingException1)
      {
        if (paramInt == 2) {
          throw localNamingException1;
        }
        previousEx = localNamingException1;
      }
      catch (Exception localException)
      {
        NamingException localNamingException2 = new NamingException("problem generating object using object factory");
        localNamingException2.setRootCause(localException);
        throw localNamingException2;
      }
    }
    if ((localObject instanceof DirContext))
    {
      refCtx = ((DirContext)localObject);
      if (((refCtx instanceof LdapContext)) && (paramArrayOfControl2 != null)) {
        ((LdapContext)refCtx).setRequestControls(paramArrayOfControl2);
      }
      initDefaults(str, paramString);
    }
    else
    {
      NotContextException localNotContextException = new NotContextException("Cannot create context for: " + str);
      localNotContextException.setRemainingName(new CompositeName().add(paramString));
      throw localNotContextException;
    }
  }
  
  private void initDefaults(String paramString1, String paramString2)
    throws NamingException
  {
    String str;
    try
    {
      LdapURL localLdapURL = new LdapURL(paramString1);
      str = localLdapURL.getDN();
      urlAttrs = localLdapURL.getAttributes();
      urlScope = localLdapURL.getScope();
      urlFilter = localLdapURL.getFilter();
    }
    catch (NamingException localNamingException)
    {
      str = paramString1;
      urlAttrs = (urlScope = urlFilter = null);
    }
    if (str == null) {
      str = paramString2;
    } else {
      str = "";
    }
    if (str == null) {
      urlName = null;
    } else {
      urlName = (str.equals("") ? new CompositeName() : new CompositeName().add(str));
    }
  }
  
  public void close()
    throws NamingException
  {
    if (refCtx != null)
    {
      refCtx.close();
      refCtx = null;
    }
    refEx = null;
  }
  
  void setHopCount(int paramInt)
  {
    hopCount = paramInt;
    if ((refCtx != null) && ((refCtx instanceof LdapCtx))) {
      ((LdapCtx)refCtx).setHopCount(paramInt);
    }
  }
  
  public Object lookup(String paramString)
    throws NamingException
  {
    return lookup(toName(paramString));
  }
  
  public Object lookup(Name paramName)
    throws NamingException
  {
    if (skipThisReferral) {
      throw ((NamingException)refEx.appendUnprocessedReferrals(null).fillInStackTrace());
    }
    return refCtx.lookup(overrideName(paramName));
  }
  
  public void bind(String paramString, Object paramObject)
    throws NamingException
  {
    bind(toName(paramString), paramObject);
  }
  
  public void bind(Name paramName, Object paramObject)
    throws NamingException
  {
    if (skipThisReferral) {
      throw ((NamingException)refEx.appendUnprocessedReferrals(null).fillInStackTrace());
    }
    refCtx.bind(overrideName(paramName), paramObject);
  }
  
  public void rebind(String paramString, Object paramObject)
    throws NamingException
  {
    rebind(toName(paramString), paramObject);
  }
  
  public void rebind(Name paramName, Object paramObject)
    throws NamingException
  {
    if (skipThisReferral) {
      throw ((NamingException)refEx.appendUnprocessedReferrals(null).fillInStackTrace());
    }
    refCtx.rebind(overrideName(paramName), paramObject);
  }
  
  public void unbind(String paramString)
    throws NamingException
  {
    unbind(toName(paramString));
  }
  
  public void unbind(Name paramName)
    throws NamingException
  {
    if (skipThisReferral) {
      throw ((NamingException)refEx.appendUnprocessedReferrals(null).fillInStackTrace());
    }
    refCtx.unbind(overrideName(paramName));
  }
  
  public void rename(String paramString1, String paramString2)
    throws NamingException
  {
    rename(toName(paramString1), toName(paramString2));
  }
  
  public void rename(Name paramName1, Name paramName2)
    throws NamingException
  {
    if (skipThisReferral) {
      throw ((NamingException)refEx.appendUnprocessedReferrals(null).fillInStackTrace());
    }
    refCtx.rename(overrideName(paramName1), toName(refEx.getNewRdn()));
  }
  
  public NamingEnumeration<NameClassPair> list(String paramString)
    throws NamingException
  {
    return list(toName(paramString));
  }
  
  public NamingEnumeration<NameClassPair> list(Name paramName)
    throws NamingException
  {
    if (skipThisReferral) {
      throw ((NamingException)refEx.appendUnprocessedReferrals(null).fillInStackTrace());
    }
    try
    {
      NamingEnumeration localNamingEnumeration = null;
      if ((urlScope != null) && (urlScope.equals("base")))
      {
        SearchControls localSearchControls = new SearchControls();
        localSearchControls.setReturningObjFlag(true);
        localSearchControls.setSearchScope(0);
        localNamingEnumeration = refCtx.search(overrideName(paramName), "(objectclass=*)", localSearchControls);
      }
      else
      {
        localNamingEnumeration = refCtx.list(overrideName(paramName));
      }
      refEx.setNameResolved(true);
      ((ReferralEnumeration)localNamingEnumeration).appendUnprocessedReferrals(refEx);
      return localNamingEnumeration;
    }
    catch (LdapReferralException localLdapReferralException)
    {
      localLdapReferralException.appendUnprocessedReferrals(refEx);
      throw ((NamingException)localLdapReferralException.fillInStackTrace());
    }
    catch (NamingException localNamingException)
    {
      if ((refEx != null) && (!refEx.hasMoreReferrals())) {
        refEx.setNamingException(localNamingException);
      }
      if ((refEx != null) && ((refEx.hasMoreReferrals()) || (refEx.hasMoreReferralExceptions()))) {
        throw ((NamingException)refEx.appendUnprocessedReferrals(null).fillInStackTrace());
      }
      throw localNamingException;
    }
  }
  
  public NamingEnumeration<Binding> listBindings(String paramString)
    throws NamingException
  {
    return listBindings(toName(paramString));
  }
  
  public NamingEnumeration<Binding> listBindings(Name paramName)
    throws NamingException
  {
    if (skipThisReferral) {
      throw ((NamingException)refEx.appendUnprocessedReferrals(null).fillInStackTrace());
    }
    try
    {
      NamingEnumeration localNamingEnumeration = null;
      if ((urlScope != null) && (urlScope.equals("base")))
      {
        SearchControls localSearchControls = new SearchControls();
        localSearchControls.setReturningObjFlag(true);
        localSearchControls.setSearchScope(0);
        localNamingEnumeration = refCtx.search(overrideName(paramName), "(objectclass=*)", localSearchControls);
      }
      else
      {
        localNamingEnumeration = refCtx.listBindings(overrideName(paramName));
      }
      refEx.setNameResolved(true);
      ((ReferralEnumeration)localNamingEnumeration).appendUnprocessedReferrals(refEx);
      return localNamingEnumeration;
    }
    catch (LdapReferralException localLdapReferralException)
    {
      localLdapReferralException.appendUnprocessedReferrals(refEx);
      throw ((NamingException)localLdapReferralException.fillInStackTrace());
    }
    catch (NamingException localNamingException)
    {
      if ((refEx != null) && (!refEx.hasMoreReferrals())) {
        refEx.setNamingException(localNamingException);
      }
      if ((refEx != null) && ((refEx.hasMoreReferrals()) || (refEx.hasMoreReferralExceptions()))) {
        throw ((NamingException)refEx.appendUnprocessedReferrals(null).fillInStackTrace());
      }
      throw localNamingException;
    }
  }
  
  public void destroySubcontext(String paramString)
    throws NamingException
  {
    destroySubcontext(toName(paramString));
  }
  
  public void destroySubcontext(Name paramName)
    throws NamingException
  {
    if (skipThisReferral) {
      throw ((NamingException)refEx.appendUnprocessedReferrals(null).fillInStackTrace());
    }
    refCtx.destroySubcontext(overrideName(paramName));
  }
  
  public Context createSubcontext(String paramString)
    throws NamingException
  {
    return createSubcontext(toName(paramString));
  }
  
  public Context createSubcontext(Name paramName)
    throws NamingException
  {
    if (skipThisReferral) {
      throw ((NamingException)refEx.appendUnprocessedReferrals(null).fillInStackTrace());
    }
    return refCtx.createSubcontext(overrideName(paramName));
  }
  
  public Object lookupLink(String paramString)
    throws NamingException
  {
    return lookupLink(toName(paramString));
  }
  
  public Object lookupLink(Name paramName)
    throws NamingException
  {
    if (skipThisReferral) {
      throw ((NamingException)refEx.appendUnprocessedReferrals(null).fillInStackTrace());
    }
    return refCtx.lookupLink(overrideName(paramName));
  }
  
  public NameParser getNameParser(String paramString)
    throws NamingException
  {
    return getNameParser(toName(paramString));
  }
  
  public NameParser getNameParser(Name paramName)
    throws NamingException
  {
    if (skipThisReferral) {
      throw ((NamingException)refEx.appendUnprocessedReferrals(null).fillInStackTrace());
    }
    return refCtx.getNameParser(overrideName(paramName));
  }
  
  public String composeName(String paramString1, String paramString2)
    throws NamingException
  {
    return composeName(toName(paramString1), toName(paramString2)).toString();
  }
  
  public Name composeName(Name paramName1, Name paramName2)
    throws NamingException
  {
    if (skipThisReferral) {
      throw ((NamingException)refEx.appendUnprocessedReferrals(null).fillInStackTrace());
    }
    return refCtx.composeName(paramName1, paramName2);
  }
  
  public Object addToEnvironment(String paramString, Object paramObject)
    throws NamingException
  {
    if (skipThisReferral) {
      throw ((NamingException)refEx.appendUnprocessedReferrals(null).fillInStackTrace());
    }
    return refCtx.addToEnvironment(paramString, paramObject);
  }
  
  public Object removeFromEnvironment(String paramString)
    throws NamingException
  {
    if (skipThisReferral) {
      throw ((NamingException)refEx.appendUnprocessedReferrals(null).fillInStackTrace());
    }
    return refCtx.removeFromEnvironment(paramString);
  }
  
  public Hashtable<?, ?> getEnvironment()
    throws NamingException
  {
    if (skipThisReferral) {
      throw ((NamingException)refEx.appendUnprocessedReferrals(null).fillInStackTrace());
    }
    return refCtx.getEnvironment();
  }
  
  public Attributes getAttributes(String paramString)
    throws NamingException
  {
    return getAttributes(toName(paramString));
  }
  
  public Attributes getAttributes(Name paramName)
    throws NamingException
  {
    if (skipThisReferral) {
      throw ((NamingException)refEx.appendUnprocessedReferrals(null).fillInStackTrace());
    }
    return refCtx.getAttributes(overrideName(paramName));
  }
  
  public Attributes getAttributes(String paramString, String[] paramArrayOfString)
    throws NamingException
  {
    return getAttributes(toName(paramString), paramArrayOfString);
  }
  
  public Attributes getAttributes(Name paramName, String[] paramArrayOfString)
    throws NamingException
  {
    if (skipThisReferral) {
      throw ((NamingException)refEx.appendUnprocessedReferrals(null).fillInStackTrace());
    }
    return refCtx.getAttributes(overrideName(paramName), paramArrayOfString);
  }
  
  public void modifyAttributes(String paramString, int paramInt, Attributes paramAttributes)
    throws NamingException
  {
    modifyAttributes(toName(paramString), paramInt, paramAttributes);
  }
  
  public void modifyAttributes(Name paramName, int paramInt, Attributes paramAttributes)
    throws NamingException
  {
    if (skipThisReferral) {
      throw ((NamingException)refEx.appendUnprocessedReferrals(null).fillInStackTrace());
    }
    refCtx.modifyAttributes(overrideName(paramName), paramInt, paramAttributes);
  }
  
  public void modifyAttributes(String paramString, ModificationItem[] paramArrayOfModificationItem)
    throws NamingException
  {
    modifyAttributes(toName(paramString), paramArrayOfModificationItem);
  }
  
  public void modifyAttributes(Name paramName, ModificationItem[] paramArrayOfModificationItem)
    throws NamingException
  {
    if (skipThisReferral) {
      throw ((NamingException)refEx.appendUnprocessedReferrals(null).fillInStackTrace());
    }
    refCtx.modifyAttributes(overrideName(paramName), paramArrayOfModificationItem);
  }
  
  public void bind(String paramString, Object paramObject, Attributes paramAttributes)
    throws NamingException
  {
    bind(toName(paramString), paramObject, paramAttributes);
  }
  
  public void bind(Name paramName, Object paramObject, Attributes paramAttributes)
    throws NamingException
  {
    if (skipThisReferral) {
      throw ((NamingException)refEx.appendUnprocessedReferrals(null).fillInStackTrace());
    }
    refCtx.bind(overrideName(paramName), paramObject, paramAttributes);
  }
  
  public void rebind(String paramString, Object paramObject, Attributes paramAttributes)
    throws NamingException
  {
    rebind(toName(paramString), paramObject, paramAttributes);
  }
  
  public void rebind(Name paramName, Object paramObject, Attributes paramAttributes)
    throws NamingException
  {
    if (skipThisReferral) {
      throw ((NamingException)refEx.appendUnprocessedReferrals(null).fillInStackTrace());
    }
    refCtx.rebind(overrideName(paramName), paramObject, paramAttributes);
  }
  
  public DirContext createSubcontext(String paramString, Attributes paramAttributes)
    throws NamingException
  {
    return createSubcontext(toName(paramString), paramAttributes);
  }
  
  public DirContext createSubcontext(Name paramName, Attributes paramAttributes)
    throws NamingException
  {
    if (skipThisReferral) {
      throw ((NamingException)refEx.appendUnprocessedReferrals(null).fillInStackTrace());
    }
    return refCtx.createSubcontext(overrideName(paramName), paramAttributes);
  }
  
  public DirContext getSchema(String paramString)
    throws NamingException
  {
    return getSchema(toName(paramString));
  }
  
  public DirContext getSchema(Name paramName)
    throws NamingException
  {
    if (skipThisReferral) {
      throw ((NamingException)refEx.appendUnprocessedReferrals(null).fillInStackTrace());
    }
    return refCtx.getSchema(overrideName(paramName));
  }
  
  public DirContext getSchemaClassDefinition(String paramString)
    throws NamingException
  {
    return getSchemaClassDefinition(toName(paramString));
  }
  
  public DirContext getSchemaClassDefinition(Name paramName)
    throws NamingException
  {
    if (skipThisReferral) {
      throw ((NamingException)refEx.appendUnprocessedReferrals(null).fillInStackTrace());
    }
    return refCtx.getSchemaClassDefinition(overrideName(paramName));
  }
  
  public NamingEnumeration<SearchResult> search(String paramString, Attributes paramAttributes)
    throws NamingException
  {
    return search(toName(paramString), SearchFilter.format(paramAttributes), new SearchControls());
  }
  
  public NamingEnumeration<SearchResult> search(Name paramName, Attributes paramAttributes)
    throws NamingException
  {
    return search(paramName, SearchFilter.format(paramAttributes), new SearchControls());
  }
  
  public NamingEnumeration<SearchResult> search(String paramString, Attributes paramAttributes, String[] paramArrayOfString)
    throws NamingException
  {
    SearchControls localSearchControls = new SearchControls();
    localSearchControls.setReturningAttributes(paramArrayOfString);
    return search(toName(paramString), SearchFilter.format(paramAttributes), localSearchControls);
  }
  
  public NamingEnumeration<SearchResult> search(Name paramName, Attributes paramAttributes, String[] paramArrayOfString)
    throws NamingException
  {
    SearchControls localSearchControls = new SearchControls();
    localSearchControls.setReturningAttributes(paramArrayOfString);
    return search(paramName, SearchFilter.format(paramAttributes), localSearchControls);
  }
  
  public NamingEnumeration<SearchResult> search(String paramString1, String paramString2, SearchControls paramSearchControls)
    throws NamingException
  {
    return search(toName(paramString1), paramString2, paramSearchControls);
  }
  
  public NamingEnumeration<SearchResult> search(Name paramName, String paramString, SearchControls paramSearchControls)
    throws NamingException
  {
    if (skipThisReferral) {
      throw ((NamingException)refEx.appendUnprocessedReferrals(null).fillInStackTrace());
    }
    try
    {
      NamingEnumeration localNamingEnumeration = refCtx.search(overrideName(paramName), overrideFilter(paramString), overrideAttributesAndScope(paramSearchControls));
      refEx.setNameResolved(true);
      ((ReferralEnumeration)localNamingEnumeration).appendUnprocessedReferrals(refEx);
      return localNamingEnumeration;
    }
    catch (LdapReferralException localLdapReferralException)
    {
      localLdapReferralException.appendUnprocessedReferrals(refEx);
      throw ((NamingException)localLdapReferralException.fillInStackTrace());
    }
    catch (NamingException localNamingException)
    {
      if ((refEx != null) && (!refEx.hasMoreReferrals())) {
        refEx.setNamingException(localNamingException);
      }
      if ((refEx != null) && ((refEx.hasMoreReferrals()) || (refEx.hasMoreReferralExceptions()))) {
        throw ((NamingException)refEx.appendUnprocessedReferrals(null).fillInStackTrace());
      }
      throw localNamingException;
    }
  }
  
  public NamingEnumeration<SearchResult> search(String paramString1, String paramString2, Object[] paramArrayOfObject, SearchControls paramSearchControls)
    throws NamingException
  {
    return search(toName(paramString1), paramString2, paramArrayOfObject, paramSearchControls);
  }
  
  public NamingEnumeration<SearchResult> search(Name paramName, String paramString, Object[] paramArrayOfObject, SearchControls paramSearchControls)
    throws NamingException
  {
    if (skipThisReferral) {
      throw ((NamingException)refEx.appendUnprocessedReferrals(null).fillInStackTrace());
    }
    try
    {
      NamingEnumeration localNamingEnumeration;
      if (urlFilter != null) {
        localNamingEnumeration = refCtx.search(overrideName(paramName), urlFilter, overrideAttributesAndScope(paramSearchControls));
      } else {
        localNamingEnumeration = refCtx.search(overrideName(paramName), paramString, paramArrayOfObject, overrideAttributesAndScope(paramSearchControls));
      }
      refEx.setNameResolved(true);
      ((ReferralEnumeration)localNamingEnumeration).appendUnprocessedReferrals(refEx);
      return localNamingEnumeration;
    }
    catch (LdapReferralException localLdapReferralException)
    {
      localLdapReferralException.appendUnprocessedReferrals(refEx);
      throw ((NamingException)localLdapReferralException.fillInStackTrace());
    }
    catch (NamingException localNamingException)
    {
      if ((refEx != null) && (!refEx.hasMoreReferrals())) {
        refEx.setNamingException(localNamingException);
      }
      if ((refEx != null) && ((refEx.hasMoreReferrals()) || (refEx.hasMoreReferralExceptions()))) {
        throw ((NamingException)refEx.appendUnprocessedReferrals(null).fillInStackTrace());
      }
      throw localNamingException;
    }
  }
  
  public String getNameInNamespace()
    throws NamingException
  {
    if (skipThisReferral) {
      throw ((NamingException)refEx.appendUnprocessedReferrals(null).fillInStackTrace());
    }
    return (urlName != null) && (!urlName.isEmpty()) ? urlName.get(0) : "";
  }
  
  public ExtendedResponse extendedOperation(ExtendedRequest paramExtendedRequest)
    throws NamingException
  {
    if (skipThisReferral) {
      throw ((NamingException)refEx.appendUnprocessedReferrals(null).fillInStackTrace());
    }
    if (!(refCtx instanceof LdapContext)) {
      throw new NotContextException("Referral context not an instance of LdapContext");
    }
    return ((LdapContext)refCtx).extendedOperation(paramExtendedRequest);
  }
  
  public LdapContext newInstance(Control[] paramArrayOfControl)
    throws NamingException
  {
    if (skipThisReferral) {
      throw ((NamingException)refEx.appendUnprocessedReferrals(null).fillInStackTrace());
    }
    if (!(refCtx instanceof LdapContext)) {
      throw new NotContextException("Referral context not an instance of LdapContext");
    }
    return ((LdapContext)refCtx).newInstance(paramArrayOfControl);
  }
  
  public void reconnect(Control[] paramArrayOfControl)
    throws NamingException
  {
    if (skipThisReferral) {
      throw ((NamingException)refEx.appendUnprocessedReferrals(null).fillInStackTrace());
    }
    if (!(refCtx instanceof LdapContext)) {
      throw new NotContextException("Referral context not an instance of LdapContext");
    }
    ((LdapContext)refCtx).reconnect(paramArrayOfControl);
  }
  
  public Control[] getConnectControls()
    throws NamingException
  {
    if (skipThisReferral) {
      throw ((NamingException)refEx.appendUnprocessedReferrals(null).fillInStackTrace());
    }
    if (!(refCtx instanceof LdapContext)) {
      throw new NotContextException("Referral context not an instance of LdapContext");
    }
    return ((LdapContext)refCtx).getConnectControls();
  }
  
  public void setRequestControls(Control[] paramArrayOfControl)
    throws NamingException
  {
    if (skipThisReferral) {
      throw ((NamingException)refEx.appendUnprocessedReferrals(null).fillInStackTrace());
    }
    if (!(refCtx instanceof LdapContext)) {
      throw new NotContextException("Referral context not an instance of LdapContext");
    }
    ((LdapContext)refCtx).setRequestControls(paramArrayOfControl);
  }
  
  public Control[] getRequestControls()
    throws NamingException
  {
    if (skipThisReferral) {
      throw ((NamingException)refEx.appendUnprocessedReferrals(null).fillInStackTrace());
    }
    if (!(refCtx instanceof LdapContext)) {
      throw new NotContextException("Referral context not an instance of LdapContext");
    }
    return ((LdapContext)refCtx).getRequestControls();
  }
  
  public Control[] getResponseControls()
    throws NamingException
  {
    if (skipThisReferral) {
      throw ((NamingException)refEx.appendUnprocessedReferrals(null).fillInStackTrace());
    }
    if (!(refCtx instanceof LdapContext)) {
      throw new NotContextException("Referral context not an instance of LdapContext");
    }
    return ((LdapContext)refCtx).getResponseControls();
  }
  
  private Name toName(String paramString)
    throws InvalidNameException
  {
    return paramString.equals("") ? new CompositeName() : new CompositeName().add(paramString);
  }
  
  private Name overrideName(Name paramName)
    throws InvalidNameException
  {
    return urlName == null ? paramName : urlName;
  }
  
  private SearchControls overrideAttributesAndScope(SearchControls paramSearchControls)
  {
    if ((urlScope != null) || (urlAttrs != null))
    {
      SearchControls localSearchControls = new SearchControls(paramSearchControls.getSearchScope(), paramSearchControls.getCountLimit(), paramSearchControls.getTimeLimit(), paramSearchControls.getReturningAttributes(), paramSearchControls.getReturningObjFlag(), paramSearchControls.getDerefLinkFlag());
      if (urlScope != null) {
        if (urlScope.equals("base")) {
          localSearchControls.setSearchScope(0);
        } else if (urlScope.equals("one")) {
          localSearchControls.setSearchScope(1);
        } else if (urlScope.equals("sub")) {
          localSearchControls.setSearchScope(2);
        }
      }
      if (urlAttrs != null)
      {
        StringTokenizer localStringTokenizer = new StringTokenizer(urlAttrs, ",");
        int i = localStringTokenizer.countTokens();
        String[] arrayOfString = new String[i];
        for (int j = 0; j < i; j++) {
          arrayOfString[j] = localStringTokenizer.nextToken();
        }
        localSearchControls.setReturningAttributes(arrayOfString);
      }
      return localSearchControls;
    }
    return paramSearchControls;
  }
  
  private String overrideFilter(String paramString)
  {
    return urlFilter == null ? paramString : urlFilter;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\ldap\LdapReferralContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */