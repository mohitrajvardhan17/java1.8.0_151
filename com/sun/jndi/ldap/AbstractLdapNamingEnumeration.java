package com.sun.jndi.ldap;

import com.sun.jndi.toolkit.ctx.Continuation;
import java.util.NoSuchElementException;
import java.util.Vector;
import javax.naming.LimitExceededException;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.PartialResultException;
import javax.naming.directory.Attributes;
import javax.naming.ldap.Control;

abstract class AbstractLdapNamingEnumeration<T extends NameClassPair>
  implements NamingEnumeration<T>, ReferralEnumeration<T>
{
  protected Name listArg;
  private boolean cleaned = false;
  private LdapResult res;
  private LdapClient enumClnt;
  private Continuation cont;
  private Vector<LdapEntry> entries = null;
  private int limit = 0;
  private int posn = 0;
  protected LdapCtx homeCtx;
  private LdapReferralException refEx = null;
  private NamingException errEx = null;
  private boolean more = true;
  private boolean hasMoreCalled = false;
  
  AbstractLdapNamingEnumeration(LdapCtx paramLdapCtx, LdapResult paramLdapResult, Name paramName, Continuation paramContinuation)
    throws NamingException
  {
    if ((status != 0) && (status != 4) && (status != 3) && (status != 11) && (status != 10) && (status != 9))
    {
      NamingException localNamingException = new NamingException(LdapClient.getErrorMessage(status, errorMessage));
      throw paramContinuation.fillInException(localNamingException);
    }
    res = paramLdapResult;
    entries = entries;
    limit = (entries == null ? 0 : entries.size());
    listArg = paramName;
    cont = paramContinuation;
    if (refEx != null) {
      refEx = refEx;
    }
    homeCtx = paramLdapCtx;
    paramLdapCtx.incEnumCount();
    enumClnt = clnt;
  }
  
  public final T nextElement()
  {
    try
    {
      return next();
    }
    catch (NamingException localNamingException)
    {
      cleanup();
    }
    return null;
  }
  
  public final boolean hasMoreElements()
  {
    try
    {
      return hasMore();
    }
    catch (NamingException localNamingException)
    {
      cleanup();
    }
    return false;
  }
  
  private void getNextBatch()
    throws NamingException
  {
    res = homeCtx.getSearchReply(enumClnt, res);
    if (res == null)
    {
      limit = (posn = 0);
      return;
    }
    entries = res.entries;
    limit = (entries == null ? 0 : entries.size());
    posn = 0;
    if ((res.status != 0) || ((res.status == 0) && (res.referrals != null))) {
      try
      {
        homeCtx.processReturnCode(res, listArg);
      }
      catch (LimitExceededException|PartialResultException localLimitExceededException)
      {
        setNamingException(localLimitExceededException);
      }
    }
    if (res.refEx != null)
    {
      if (refEx == null) {
        refEx = res.refEx;
      } else {
        refEx = refEx.appendUnprocessedReferrals(res.refEx);
      }
      res.refEx = null;
    }
    if (res.resControls != null) {
      homeCtx.respCtls = res.resControls;
    }
  }
  
  public final boolean hasMore()
    throws NamingException
  {
    if (hasMoreCalled) {
      return more;
    }
    hasMoreCalled = true;
    if (!more) {
      return false;
    }
    return more = hasMoreImpl();
  }
  
  public final T next()
    throws NamingException
  {
    if (!hasMoreCalled) {
      hasMore();
    }
    hasMoreCalled = false;
    return nextImpl();
  }
  
  private boolean hasMoreImpl()
    throws NamingException
  {
    if (posn == limit) {
      getNextBatch();
    }
    if (posn < limit) {
      return true;
    }
    try
    {
      return hasMoreReferrals();
    }
    catch (LdapReferralException|LimitExceededException|PartialResultException localLdapReferralException)
    {
      cleanup();
      throw localLdapReferralException;
    }
    catch (NamingException localNamingException)
    {
      cleanup();
      PartialResultException localPartialResultException = new PartialResultException();
      localPartialResultException.setRootCause(localNamingException);
      throw localPartialResultException;
    }
  }
  
  private T nextImpl()
    throws NamingException
  {
    try
    {
      return nextAux();
    }
    catch (NamingException localNamingException)
    {
      cleanup();
      throw cont.fillInException(localNamingException);
    }
  }
  
  private T nextAux()
    throws NamingException
  {
    if (posn == limit) {
      getNextBatch();
    }
    if (posn >= limit)
    {
      cleanup();
      throw new NoSuchElementException("invalid enumeration handle");
    }
    LdapEntry localLdapEntry = (LdapEntry)entries.elementAt(posn++);
    return createItem(DN, attributes, respCtls);
  }
  
  protected final String getAtom(String paramString)
  {
    try
    {
      LdapName localLdapName = new LdapName(paramString);
      return localLdapName.get(localLdapName.size() - 1);
    }
    catch (NamingException localNamingException) {}
    return paramString;
  }
  
  protected abstract T createItem(String paramString, Attributes paramAttributes, Vector<Control> paramVector)
    throws NamingException;
  
  public void appendUnprocessedReferrals(LdapReferralException paramLdapReferralException)
  {
    if (refEx != null) {
      refEx = refEx.appendUnprocessedReferrals(paramLdapReferralException);
    } else {
      refEx = paramLdapReferralException.appendUnprocessedReferrals(refEx);
    }
  }
  
  final void setNamingException(NamingException paramNamingException)
  {
    errEx = paramNamingException;
  }
  
  protected abstract AbstractLdapNamingEnumeration<? extends NameClassPair> getReferredResults(LdapReferralContext paramLdapReferralContext)
    throws NamingException;
  
  protected final boolean hasMoreReferrals()
    throws NamingException
  {
    if ((refEx != null) && ((refEx.hasMoreReferrals()) || (refEx.hasMoreReferralExceptions())))
    {
      if (homeCtx.handleReferrals == 2) {
        throw ((NamingException)refEx.fillInStackTrace());
      }
      for (;;)
      {
        LdapReferralContext localLdapReferralContext = (LdapReferralContext)refEx.getReferralContext(homeCtx.envprops, homeCtx.reqCtls);
        try
        {
          update(getReferredResults(localLdapReferralContext));
        }
        catch (LdapReferralException localLdapReferralException)
        {
          if (errEx == null) {
            errEx = localLdapReferralException.getNamingException();
          }
          refEx = localLdapReferralException;
          localLdapReferralContext.close();
        }
        finally
        {
          localLdapReferralContext.close();
        }
      }
      return hasMoreImpl();
    }
    cleanup();
    if (errEx != null) {
      throw errEx;
    }
    return false;
  }
  
  protected void update(AbstractLdapNamingEnumeration<? extends NameClassPair> paramAbstractLdapNamingEnumeration)
  {
    homeCtx.decEnumCount();
    homeCtx = homeCtx;
    enumClnt = enumClnt;
    homeCtx = null;
    posn = posn;
    limit = limit;
    res = res;
    entries = entries;
    refEx = refEx;
    listArg = listArg;
  }
  
  protected final void finalize()
  {
    cleanup();
  }
  
  protected final void cleanup()
  {
    if (cleaned) {
      return;
    }
    if (enumClnt != null) {
      enumClnt.clearSearchReply(res, homeCtx.reqCtls);
    }
    enumClnt = null;
    cleaned = true;
    if (homeCtx != null)
    {
      homeCtx.decEnumCount();
      homeCtx = null;
    }
  }
  
  public final void close()
  {
    cleanup();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\ldap\AbstractLdapNamingEnumeration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */