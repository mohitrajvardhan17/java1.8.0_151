package com.sun.security.auth.module;

import com.sun.security.auth.NTDomainPrincipal;
import com.sun.security.auth.NTNumericCredential;
import com.sun.security.auth.NTSidDomainPrincipal;
import com.sun.security.auth.NTSidGroupPrincipal;
import com.sun.security.auth.NTSidPrimaryGroupPrincipal;
import com.sun.security.auth.NTSidUserPrincipal;
import com.sun.security.auth.NTUserPrincipal;
import java.io.PrintStream;
import java.util.Map;
import java.util.Set;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import jdk.Exported;

@Exported
public class NTLoginModule
  implements LoginModule
{
  private NTSystem ntSystem;
  private Subject subject;
  private CallbackHandler callbackHandler;
  private Map<String, ?> sharedState;
  private Map<String, ?> options;
  private boolean debug = false;
  private boolean debugNative = false;
  private boolean succeeded = false;
  private boolean commitSucceeded = false;
  private NTUserPrincipal userPrincipal;
  private NTSidUserPrincipal userSID;
  private NTDomainPrincipal userDomain;
  private NTSidDomainPrincipal domainSID;
  private NTSidPrimaryGroupPrincipal primaryGroup;
  private NTSidGroupPrincipal[] groups;
  private NTNumericCredential iToken;
  
  public NTLoginModule() {}
  
  public void initialize(Subject paramSubject, CallbackHandler paramCallbackHandler, Map<String, ?> paramMap1, Map<String, ?> paramMap2)
  {
    subject = paramSubject;
    callbackHandler = paramCallbackHandler;
    sharedState = paramMap1;
    options = paramMap2;
    debug = "true".equalsIgnoreCase((String)paramMap2.get("debug"));
    debugNative = "true".equalsIgnoreCase((String)paramMap2.get("debugNative"));
    if (debugNative == true) {
      debug = true;
    }
  }
  
  public boolean login()
    throws LoginException
  {
    succeeded = false;
    ntSystem = new NTSystem(debugNative);
    if (ntSystem == null)
    {
      if (debug) {
        System.out.println("\t\t[NTLoginModule] Failed in NT login");
      }
      throw new FailedLoginException("Failed in attempt to import the underlying NT system identity information");
    }
    if (ntSystem.getName() == null) {
      throw new FailedLoginException("Failed in attempt to import the underlying NT system identity information");
    }
    userPrincipal = new NTUserPrincipal(ntSystem.getName());
    if (debug)
    {
      System.out.println("\t\t[NTLoginModule] succeeded importing info: ");
      System.out.println("\t\t\tuser name = " + userPrincipal.getName());
    }
    if (ntSystem.getUserSID() != null)
    {
      userSID = new NTSidUserPrincipal(ntSystem.getUserSID());
      if (debug) {
        System.out.println("\t\t\tuser SID = " + userSID.getName());
      }
    }
    if (ntSystem.getDomain() != null)
    {
      userDomain = new NTDomainPrincipal(ntSystem.getDomain());
      if (debug) {
        System.out.println("\t\t\tuser domain = " + userDomain.getName());
      }
    }
    if (ntSystem.getDomainSID() != null)
    {
      domainSID = new NTSidDomainPrincipal(ntSystem.getDomainSID());
      if (debug) {
        System.out.println("\t\t\tuser domain SID = " + domainSID.getName());
      }
    }
    if (ntSystem.getPrimaryGroupID() != null)
    {
      primaryGroup = new NTSidPrimaryGroupPrincipal(ntSystem.getPrimaryGroupID());
      if (debug) {
        System.out.println("\t\t\tuser primary group = " + primaryGroup.getName());
      }
    }
    if ((ntSystem.getGroupIDs() != null) && (ntSystem.getGroupIDs().length > 0))
    {
      String[] arrayOfString = ntSystem.getGroupIDs();
      groups = new NTSidGroupPrincipal[arrayOfString.length];
      for (int i = 0; i < arrayOfString.length; i++)
      {
        groups[i] = new NTSidGroupPrincipal(arrayOfString[i]);
        if (debug) {
          System.out.println("\t\t\tuser group = " + groups[i].getName());
        }
      }
    }
    if (ntSystem.getImpersonationToken() != 0L)
    {
      iToken = new NTNumericCredential(ntSystem.getImpersonationToken());
      if (debug) {
        System.out.println("\t\t\timpersonation token = " + ntSystem.getImpersonationToken());
      }
    }
    succeeded = true;
    return succeeded;
  }
  
  public boolean commit()
    throws LoginException
  {
    if (!succeeded)
    {
      if (debug) {
        System.out.println("\t\t[NTLoginModule]: did not add any Principals to Subject because own authentication failed.");
      }
      return false;
    }
    if (subject.isReadOnly()) {
      throw new LoginException("Subject is ReadOnly");
    }
    Set localSet1 = subject.getPrincipals();
    if (!localSet1.contains(userPrincipal)) {
      localSet1.add(userPrincipal);
    }
    if ((userSID != null) && (!localSet1.contains(userSID))) {
      localSet1.add(userSID);
    }
    if ((userDomain != null) && (!localSet1.contains(userDomain))) {
      localSet1.add(userDomain);
    }
    if ((domainSID != null) && (!localSet1.contains(domainSID))) {
      localSet1.add(domainSID);
    }
    if ((primaryGroup != null) && (!localSet1.contains(primaryGroup))) {
      localSet1.add(primaryGroup);
    }
    for (int i = 0; (groups != null) && (i < groups.length); i++) {
      if (!localSet1.contains(groups[i])) {
        localSet1.add(groups[i]);
      }
    }
    Set localSet2 = subject.getPublicCredentials();
    if ((iToken != null) && (!localSet2.contains(iToken))) {
      localSet2.add(iToken);
    }
    commitSucceeded = true;
    return true;
  }
  
  public boolean abort()
    throws LoginException
  {
    if (debug) {
      System.out.println("\t\t[NTLoginModule]: aborted authentication attempt");
    }
    if (!succeeded) {
      return false;
    }
    if ((succeeded == true) && (!commitSucceeded))
    {
      ntSystem = null;
      userPrincipal = null;
      userSID = null;
      userDomain = null;
      domainSID = null;
      primaryGroup = null;
      groups = null;
      iToken = null;
      succeeded = false;
    }
    else
    {
      logout();
    }
    return succeeded;
  }
  
  public boolean logout()
    throws LoginException
  {
    if (subject.isReadOnly()) {
      throw new LoginException("Subject is ReadOnly");
    }
    Set localSet1 = subject.getPrincipals();
    if (localSet1.contains(userPrincipal)) {
      localSet1.remove(userPrincipal);
    }
    if (localSet1.contains(userSID)) {
      localSet1.remove(userSID);
    }
    if (localSet1.contains(userDomain)) {
      localSet1.remove(userDomain);
    }
    if (localSet1.contains(domainSID)) {
      localSet1.remove(domainSID);
    }
    if (localSet1.contains(primaryGroup)) {
      localSet1.remove(primaryGroup);
    }
    for (int i = 0; (groups != null) && (i < groups.length); i++) {
      if (localSet1.contains(groups[i])) {
        localSet1.remove(groups[i]);
      }
    }
    Set localSet2 = subject.getPublicCredentials();
    if (localSet2.contains(iToken)) {
      localSet2.remove(iToken);
    }
    succeeded = false;
    commitSucceeded = false;
    userPrincipal = null;
    userDomain = null;
    userSID = null;
    domainSID = null;
    groups = null;
    primaryGroup = null;
    iToken = null;
    ntSystem = null;
    if (debug) {
      System.out.println("\t\t[NTLoginModule] completed logout processing");
    }
    return true;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\security\auth\module\NTLoginModule.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */