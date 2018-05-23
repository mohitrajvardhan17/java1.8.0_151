package com.sun.jndi.ldap;

import java.io.PrintStream;
import java.util.Hashtable;
import java.util.Vector;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.ReferralException;
import javax.naming.ldap.Control;

public final class LdapReferralException
  extends javax.naming.ldap.LdapReferralException
{
  private static final long serialVersionUID = 627059076356906399L;
  private int handleReferrals;
  private Hashtable<?, ?> envprops;
  private String nextName;
  private Control[] reqCtls;
  private Vector<?> referrals = null;
  private int referralIndex = 0;
  private int referralCount = 0;
  private boolean foundEntry = false;
  private boolean skipThisReferral = false;
  private int hopCount = 1;
  private NamingException errorEx = null;
  private String newRdn = null;
  private boolean debug = false;
  LdapReferralException nextReferralEx = null;
  
  LdapReferralException(Name paramName1, Object paramObject, Name paramName2, String paramString1, Hashtable<?, ?> paramHashtable, String paramString2, int paramInt, Control[] paramArrayOfControl)
  {
    super(paramString1);
    if (debug) {
      System.out.println("LdapReferralException constructor");
    }
    setResolvedName(paramName1);
    setResolvedObj(paramObject);
    setRemainingName(paramName2);
    envprops = paramHashtable;
    nextName = paramString2;
    handleReferrals = paramInt;
    reqCtls = ((paramInt == 1) || (paramInt == 4) ? paramArrayOfControl : null);
  }
  
  public Context getReferralContext()
    throws NamingException
  {
    return getReferralContext(envprops, null);
  }
  
  public Context getReferralContext(Hashtable<?, ?> paramHashtable)
    throws NamingException
  {
    return getReferralContext(paramHashtable, null);
  }
  
  public Context getReferralContext(Hashtable<?, ?> paramHashtable, Control[] paramArrayOfControl)
    throws NamingException
  {
    if (debug) {
      System.out.println("LdapReferralException.getReferralContext");
    }
    LdapReferralContext localLdapReferralContext = new LdapReferralContext(this, paramHashtable, paramArrayOfControl, reqCtls, nextName, skipThisReferral, handleReferrals);
    localLdapReferralContext.setHopCount(hopCount + 1);
    if (skipThisReferral) {
      skipThisReferral = false;
    }
    return localLdapReferralContext;
  }
  
  public Object getReferralInfo()
  {
    if (debug)
    {
      System.out.println("LdapReferralException.getReferralInfo");
      System.out.println("  referralIndex=" + referralIndex);
    }
    if (hasMoreReferrals()) {
      return referrals.elementAt(referralIndex);
    }
    return null;
  }
  
  public void retryReferral()
  {
    if (debug) {
      System.out.println("LdapReferralException.retryReferral");
    }
    if (referralIndex > 0) {
      referralIndex -= 1;
    }
  }
  
  public boolean skipReferral()
  {
    if (debug) {
      System.out.println("LdapReferralException.skipReferral");
    }
    skipThisReferral = true;
    try
    {
      getNextReferral();
    }
    catch (ReferralException localReferralException) {}
    return (hasMoreReferrals()) || (hasMoreReferralExceptions());
  }
  
  void setReferralInfo(Vector<?> paramVector, boolean paramBoolean)
  {
    if (debug) {
      System.out.println("LdapReferralException.setReferralInfo");
    }
    referrals = paramVector;
    referralCount = (paramVector == null ? 0 : paramVector.size());
    if (debug) {
      if (paramVector != null) {
        for (int i = 0; i < referralCount; i++) {
          System.out.println("  [" + i + "] " + paramVector.elementAt(i));
        }
      } else {
        System.out.println("setReferralInfo : referrals == null");
      }
    }
  }
  
  String getNextReferral()
    throws ReferralException
  {
    if (debug) {
      System.out.println("LdapReferralException.getNextReferral");
    }
    if (hasMoreReferrals()) {
      return (String)referrals.elementAt(referralIndex++);
    }
    if (hasMoreReferralExceptions()) {
      throw nextReferralEx;
    }
    return null;
  }
  
  LdapReferralException appendUnprocessedReferrals(LdapReferralException paramLdapReferralException)
  {
    if (debug)
    {
      System.out.println("LdapReferralException.appendUnprocessedReferrals");
      dump();
      if (paramLdapReferralException != null) {
        paramLdapReferralException.dump();
      }
    }
    LdapReferralException localLdapReferralException1 = this;
    if (!localLdapReferralException1.hasMoreReferrals())
    {
      localLdapReferralException1 = nextReferralEx;
      if ((errorEx != null) && (localLdapReferralException1 != null)) {
        localLdapReferralException1.setNamingException(errorEx);
      }
    }
    if (this == paramLdapReferralException) {
      return localLdapReferralException1;
    }
    if ((paramLdapReferralException != null) && (!paramLdapReferralException.hasMoreReferrals())) {
      paramLdapReferralException = nextReferralEx;
    }
    if (paramLdapReferralException == null) {
      return localLdapReferralException1;
    }
    for (LdapReferralException localLdapReferralException2 = localLdapReferralException1; nextReferralEx != null; localLdapReferralException2 = nextReferralEx) {}
    nextReferralEx = paramLdapReferralException;
    return localLdapReferralException1;
  }
  
  boolean hasMoreReferrals()
  {
    if (debug) {
      System.out.println("LdapReferralException.hasMoreReferrals");
    }
    return (!foundEntry) && (referralIndex < referralCount);
  }
  
  boolean hasMoreReferralExceptions()
  {
    if (debug) {
      System.out.println("LdapReferralException.hasMoreReferralExceptions");
    }
    return nextReferralEx != null;
  }
  
  void setHopCount(int paramInt)
  {
    if (debug) {
      System.out.println("LdapReferralException.setHopCount");
    }
    hopCount = paramInt;
  }
  
  void setNameResolved(boolean paramBoolean)
  {
    if (debug) {
      System.out.println("LdapReferralException.setNameResolved");
    }
    foundEntry = paramBoolean;
  }
  
  void setNamingException(NamingException paramNamingException)
  {
    if (debug) {
      System.out.println("LdapReferralException.setNamingException");
    }
    if (errorEx == null)
    {
      paramNamingException.setRootCause(this);
      errorEx = paramNamingException;
    }
  }
  
  String getNewRdn()
  {
    if (debug) {
      System.out.println("LdapReferralException.getNewRdn");
    }
    return newRdn;
  }
  
  void setNewRdn(String paramString)
  {
    if (debug) {
      System.out.println("LdapReferralException.setNewRdn");
    }
    newRdn = paramString;
  }
  
  NamingException getNamingException()
  {
    if (debug) {
      System.out.println("LdapReferralException.getNamingException");
    }
    return errorEx;
  }
  
  void dump()
  {
    System.out.println();
    System.out.println("LdapReferralException.dump");
    for (LdapReferralException localLdapReferralException = this; localLdapReferralException != null; localLdapReferralException = nextReferralEx) {
      localLdapReferralException.dumpState();
    }
  }
  
  private void dumpState()
  {
    System.out.println("LdapReferralException.dumpState");
    System.out.println("  hashCode=" + hashCode());
    System.out.println("  foundEntry=" + foundEntry);
    System.out.println("  skipThisReferral=" + skipThisReferral);
    System.out.println("  referralIndex=" + referralIndex);
    if (referrals != null)
    {
      System.out.println("  referrals:");
      for (int i = 0; i < referralCount; i++) {
        System.out.println("    [" + i + "] " + referrals.elementAt(i));
      }
    }
    else
    {
      System.out.println("  referrals=null");
    }
    System.out.println("  errorEx=" + errorEx);
    if (nextReferralEx == null) {
      System.out.println("  nextRefEx=null");
    } else {
      System.out.println("  nextRefEx=" + nextReferralEx.hashCode());
    }
    System.out.println();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jndi\ldap\LdapReferralException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */