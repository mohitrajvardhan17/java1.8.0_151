package com.sun.security.auth.module;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import javax.security.auth.DestroyFailedException;
import javax.security.auth.RefreshFailedException;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.kerberos.KerberosKey;
import javax.security.auth.kerberos.KerberosPrincipal;
import javax.security.auth.kerberos.KerberosTicket;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import jdk.Exported;
import sun.misc.HexDumpEncoder;
import sun.security.jgss.krb5.Krb5Util;
import sun.security.krb5.Config;
import sun.security.krb5.Credentials;
import sun.security.krb5.EncryptionKey;
import sun.security.krb5.KrbAsReqBuilder;
import sun.security.krb5.KrbException;
import sun.security.krb5.PrincipalName;

@Exported
public class Krb5LoginModule
  implements LoginModule
{
  private Subject subject;
  private CallbackHandler callbackHandler;
  private Map<String, Object> sharedState;
  private Map<String, ?> options;
  private boolean debug = false;
  private boolean storeKey = false;
  private boolean doNotPrompt = false;
  private boolean useTicketCache = false;
  private boolean useKeyTab = false;
  private String ticketCacheName = null;
  private String keyTabName = null;
  private String princName = null;
  private boolean useFirstPass = false;
  private boolean tryFirstPass = false;
  private boolean storePass = false;
  private boolean clearPass = false;
  private boolean refreshKrb5Config = false;
  private boolean renewTGT = false;
  private boolean isInitiator = true;
  private boolean succeeded = false;
  private boolean commitSucceeded = false;
  private String username;
  private EncryptionKey[] encKeys = null;
  javax.security.auth.kerberos.KeyTab ktab = null;
  private Credentials cred = null;
  private PrincipalName principal = null;
  private KerberosPrincipal kerbClientPrinc = null;
  private KerberosTicket kerbTicket = null;
  private KerberosKey[] kerbKeys = null;
  private StringBuffer krb5PrincName = null;
  private boolean unboundServer = false;
  private char[] password = null;
  private static final String NAME = "javax.security.auth.login.name";
  private static final String PWD = "javax.security.auth.login.password";
  private static final ResourceBundle rb = (ResourceBundle)AccessController.doPrivileged(new PrivilegedAction()
  {
    public ResourceBundle run()
    {
      return ResourceBundle.getBundle("sun.security.util.AuthResources");
    }
  });
  
  public Krb5LoginModule() {}
  
  public void initialize(Subject paramSubject, CallbackHandler paramCallbackHandler, Map<String, ?> paramMap1, Map<String, ?> paramMap2)
  {
    subject = paramSubject;
    callbackHandler = paramCallbackHandler;
    sharedState = paramMap1;
    options = paramMap2;
    debug = "true".equalsIgnoreCase((String)paramMap2.get("debug"));
    storeKey = "true".equalsIgnoreCase((String)paramMap2.get("storeKey"));
    doNotPrompt = "true".equalsIgnoreCase((String)paramMap2.get("doNotPrompt"));
    useTicketCache = "true".equalsIgnoreCase((String)paramMap2.get("useTicketCache"));
    useKeyTab = "true".equalsIgnoreCase((String)paramMap2.get("useKeyTab"));
    ticketCacheName = ((String)paramMap2.get("ticketCache"));
    keyTabName = ((String)paramMap2.get("keyTab"));
    if (keyTabName != null) {
      keyTabName = sun.security.krb5.internal.ktab.KeyTab.normalize(keyTabName);
    }
    princName = ((String)paramMap2.get("principal"));
    refreshKrb5Config = "true".equalsIgnoreCase((String)paramMap2.get("refreshKrb5Config"));
    renewTGT = "true".equalsIgnoreCase((String)paramMap2.get("renewTGT"));
    String str = (String)paramMap2.get("isInitiator");
    if (str != null) {
      isInitiator = "true".equalsIgnoreCase(str);
    }
    tryFirstPass = "true".equalsIgnoreCase((String)paramMap2.get("tryFirstPass"));
    useFirstPass = "true".equalsIgnoreCase((String)paramMap2.get("useFirstPass"));
    storePass = "true".equalsIgnoreCase((String)paramMap2.get("storePass"));
    clearPass = "true".equalsIgnoreCase((String)paramMap2.get("clearPass"));
    if (debug) {
      System.out.print("Debug is  " + debug + " storeKey " + storeKey + " useTicketCache " + useTicketCache + " useKeyTab " + useKeyTab + " doNotPrompt " + doNotPrompt + " ticketCache is " + ticketCacheName + " isInitiator " + isInitiator + " KeyTab is " + keyTabName + " refreshKrb5Config is " + refreshKrb5Config + " principal is " + princName + " tryFirstPass is " + tryFirstPass + " useFirstPass is " + useFirstPass + " storePass is " + storePass + " clearPass is " + clearPass + "\n");
    }
  }
  
  public boolean login()
    throws LoginException
  {
    if (refreshKrb5Config) {
      try
      {
        if (debug) {
          System.out.println("Refreshing Kerberos configuration");
        }
        Config.refresh();
      }
      catch (KrbException localKrbException)
      {
        LoginException localLoginException1 = new LoginException(localKrbException.getMessage());
        localLoginException1.initCause(localKrbException);
        throw localLoginException1;
      }
    }
    String str = System.getProperty("sun.security.krb5.principal");
    if (str != null) {
      krb5PrincName = new StringBuffer(str);
    } else if (princName != null) {
      krb5PrincName = new StringBuffer(princName);
    }
    validateConfiguration();
    if ((krb5PrincName != null) && (krb5PrincName.toString().equals("*"))) {
      unboundServer = true;
    }
    if (tryFirstPass) {
      try
      {
        attemptAuthentication(true);
        if (debug) {
          System.out.println("\t\t[Krb5LoginModule] authentication succeeded");
        }
        succeeded = true;
        cleanState();
        return true;
      }
      catch (LoginException localLoginException2)
      {
        cleanState();
        if (debug) {
          System.out.println("\t\t[Krb5LoginModule] tryFirstPass failed with:" + localLoginException2.getMessage());
        }
      }
    } else if (useFirstPass) {
      try
      {
        attemptAuthentication(true);
        succeeded = true;
        cleanState();
        return true;
      }
      catch (LoginException localLoginException3)
      {
        if (debug) {
          System.out.println("\t\t[Krb5LoginModule] authentication failed \n" + localLoginException3.getMessage());
        }
        succeeded = false;
        cleanState();
        throw localLoginException3;
      }
    }
    try
    {
      attemptAuthentication(false);
      succeeded = true;
      cleanState();
      return true;
    }
    catch (LoginException localLoginException4)
    {
      if (debug) {
        System.out.println("\t\t[Krb5LoginModule] authentication failed \n" + localLoginException4.getMessage());
      }
      succeeded = false;
      cleanState();
      throw localLoginException4;
    }
  }
  
  private void attemptAuthentication(boolean paramBoolean)
    throws LoginException
  {
    Object localObject2;
    if (krb5PrincName != null) {
      try
      {
        principal = new PrincipalName(krb5PrincName.toString(), 1);
      }
      catch (KrbException localKrbException1)
      {
        localObject2 = new LoginException(localKrbException1.getMessage());
        ((LoginException)localObject2).initCause(localKrbException1);
        throw ((Throwable)localObject2);
      }
    }
    try
    {
      if (useTicketCache)
      {
        if (debug) {
          System.out.println("Acquire TGT from Cache");
        }
        cred = Credentials.acquireTGTFromCache(principal, ticketCacheName);
        if ((cred != null) && (!isCurrent(cred))) {
          if (renewTGT)
          {
            cred = renewCredentials(cred);
          }
          else
          {
            cred = null;
            if (debug) {
              System.out.println("Credentials are no longer valid");
            }
          }
        }
        if ((cred != null) && (principal == null)) {
          principal = cred.getClient();
        }
        if (debug)
        {
          System.out.println("Principal is " + principal);
          if (cred == null) {
            System.out.println("null credentials from Ticket Cache");
          }
        }
      }
      if (cred == null)
      {
        if (principal == null)
        {
          promptForName(paramBoolean);
          principal = new PrincipalName(krb5PrincName.toString(), 1);
        }
        Object localObject1;
        if (useKeyTab)
        {
          if (!unboundServer)
          {
            localObject1 = new KerberosPrincipal(principal.getName());
            ktab = (keyTabName == null ? javax.security.auth.kerberos.KeyTab.getInstance((KerberosPrincipal)localObject1) : javax.security.auth.kerberos.KeyTab.getInstance((KerberosPrincipal)localObject1, new File(keyTabName)));
          }
          else
          {
            ktab = (keyTabName == null ? javax.security.auth.kerberos.KeyTab.getUnboundInstance() : javax.security.auth.kerberos.KeyTab.getUnboundInstance(new File(keyTabName)));
          }
          if ((isInitiator) && (Krb5Util.keysFromJavaxKeyTab(ktab, principal).length == 0))
          {
            ktab = null;
            if (debug) {
              System.out.println("Key for the principal " + principal + " not available in " + (keyTabName == null ? "default key tab" : keyTabName));
            }
          }
        }
        if (ktab == null)
        {
          promptForPass(paramBoolean);
          localObject1 = new KrbAsReqBuilder(principal, password);
          if (isInitiator) {
            cred = ((KrbAsReqBuilder)localObject1).action().getCreds();
          }
          if (storeKey) {
            encKeys = ((KrbAsReqBuilder)localObject1).getKeys(isInitiator);
          }
        }
        else
        {
          localObject1 = new KrbAsReqBuilder(principal, ktab);
          if (isInitiator) {
            cred = ((KrbAsReqBuilder)localObject1).action().getCreds();
          }
        }
        ((KrbAsReqBuilder)localObject1).destroy();
        if (debug)
        {
          System.out.println("principal is " + principal);
          localObject2 = new HexDumpEncoder();
          if (ktab != null) {
            System.out.println("Will use keytab");
          } else if (storeKey) {
            for (int i = 0; i < encKeys.length; i++) {
              System.out.println("EncryptionKey: keyType=" + encKeys[i].getEType() + " keyBytes (hex dump)=" + ((HexDumpEncoder)localObject2).encodeBuffer(encKeys[i].getBytes()));
            }
          }
        }
        if ((isInitiator) && (cred == null)) {
          throw new LoginException("TGT Can not be obtained from the KDC ");
        }
      }
    }
    catch (KrbException localKrbException2)
    {
      localObject2 = new LoginException(localKrbException2.getMessage());
      ((LoginException)localObject2).initCause(localKrbException2);
      throw ((Throwable)localObject2);
    }
    catch (IOException localIOException)
    {
      localObject2 = new LoginException(localIOException.getMessage());
      ((LoginException)localObject2).initCause(localIOException);
      throw ((Throwable)localObject2);
    }
  }
  
  private void promptForName(boolean paramBoolean)
    throws LoginException
  {
    krb5PrincName = new StringBuffer("");
    if (paramBoolean)
    {
      username = ((String)sharedState.get("javax.security.auth.login.name"));
      if (debug) {
        System.out.println("username from shared state is " + username + "\n");
      }
      if (username == null)
      {
        System.out.println("username from shared state is null\n");
        throw new LoginException("Username can not be obtained from sharedstate ");
      }
      if (debug) {
        System.out.println("username from shared state is " + username + "\n");
      }
      if ((username != null) && (username.length() > 0))
      {
        krb5PrincName.insert(0, username);
        return;
      }
    }
    if (doNotPrompt) {
      throw new LoginException("Unable to obtain Principal Name for authentication ");
    }
    if (callbackHandler == null) {
      throw new LoginException("No CallbackHandler available to garner authentication information from the user");
    }
    try
    {
      String str = System.getProperty("user.name");
      Callback[] arrayOfCallback = new Callback[1];
      MessageFormat localMessageFormat = new MessageFormat(rb.getString("Kerberos.username.defUsername."));
      Object[] arrayOfObject = { str };
      arrayOfCallback[0] = new NameCallback(localMessageFormat.format(arrayOfObject));
      callbackHandler.handle(arrayOfCallback);
      username = ((NameCallback)arrayOfCallback[0]).getName();
      if ((username == null) || (username.length() == 0)) {
        username = str;
      }
      krb5PrincName.insert(0, username);
    }
    catch (IOException localIOException)
    {
      throw new LoginException(localIOException.getMessage());
    }
    catch (UnsupportedCallbackException localUnsupportedCallbackException)
    {
      throw new LoginException(localUnsupportedCallbackException.getMessage() + " not available to garner  authentication information  from the user");
    }
  }
  
  private void promptForPass(boolean paramBoolean)
    throws LoginException
  {
    if (paramBoolean)
    {
      password = ((char[])sharedState.get("javax.security.auth.login.password"));
      if (password == null)
      {
        if (debug) {
          System.out.println("Password from shared state is null");
        }
        throw new LoginException("Password can not be obtained from sharedstate ");
      }
      if (debug) {
        System.out.println("password is " + new String(password));
      }
      return;
    }
    if (doNotPrompt) {
      throw new LoginException("Unable to obtain password from user\n");
    }
    if (callbackHandler == null) {
      throw new LoginException("No CallbackHandler available to garner authentication information from the user");
    }
    try
    {
      Callback[] arrayOfCallback = new Callback[1];
      String str = krb5PrincName.toString();
      MessageFormat localMessageFormat = new MessageFormat(rb.getString("Kerberos.password.for.username."));
      Object[] arrayOfObject = { str };
      arrayOfCallback[0] = new PasswordCallback(localMessageFormat.format(arrayOfObject), false);
      callbackHandler.handle(arrayOfCallback);
      char[] arrayOfChar = ((PasswordCallback)arrayOfCallback[0]).getPassword();
      if (arrayOfChar == null) {
        throw new LoginException("No password provided");
      }
      password = new char[arrayOfChar.length];
      System.arraycopy(arrayOfChar, 0, password, 0, arrayOfChar.length);
      ((PasswordCallback)arrayOfCallback[0]).clearPassword();
      for (int i = 0; i < arrayOfChar.length; i++) {
        arrayOfChar[i] = ' ';
      }
      arrayOfChar = null;
      if (debug)
      {
        System.out.println("\t\t[Krb5LoginModule] user entered username: " + krb5PrincName);
        System.out.println();
      }
    }
    catch (IOException localIOException)
    {
      throw new LoginException(localIOException.getMessage());
    }
    catch (UnsupportedCallbackException localUnsupportedCallbackException)
    {
      throw new LoginException(localUnsupportedCallbackException.getMessage() + " not available to garner  authentication information from the user");
    }
  }
  
  private void validateConfiguration()
    throws LoginException
  {
    if ((doNotPrompt) && (!useTicketCache) && (!useKeyTab) && (!tryFirstPass) && (!useFirstPass)) {
      throw new LoginException("Configuration Error - either doNotPrompt should be  false or at least one of useTicketCache,  useKeyTab, tryFirstPass and useFirstPass should be true");
    }
    if ((ticketCacheName != null) && (!useTicketCache)) {
      throw new LoginException("Configuration Error  - useTicketCache should be set to true to use the ticket cache" + ticketCacheName);
    }
    if (((keyTabName != null ? 1 : 0) & (!useKeyTab ? 1 : 0)) != 0) {
      throw new LoginException("Configuration Error - useKeyTab should be set to true to use the keytab" + keyTabName);
    }
    if ((storeKey) && (doNotPrompt) && (!useKeyTab) && (!tryFirstPass) && (!useFirstPass)) {
      throw new LoginException("Configuration Error - either doNotPrompt should be set to  false or at least one of tryFirstPass, useFirstPass or useKeyTab must be set to true for storeKey option");
    }
    if ((renewTGT) && (!useTicketCache)) {
      throw new LoginException("Configuration Error - either useTicketCache should be  true or renewTGT should be false");
    }
    if ((krb5PrincName != null) && (krb5PrincName.toString().equals("*")) && (isInitiator)) {
      throw new LoginException("Configuration Error - principal cannot be * when isInitiator is true");
    }
  }
  
  private boolean isCurrent(Credentials paramCredentials)
  {
    Date localDate = paramCredentials.getEndTime();
    if (localDate != null) {
      return System.currentTimeMillis() <= localDate.getTime();
    }
    return true;
  }
  
  private Credentials renewCredentials(Credentials paramCredentials)
  {
    Credentials localCredentials;
    try
    {
      if (!paramCredentials.isRenewable()) {
        throw new RefreshFailedException("This ticket is not renewable");
      }
      if (System.currentTimeMillis() > cred.getRenewTill().getTime()) {
        throw new RefreshFailedException("This ticket is past its last renewal time.");
      }
      localCredentials = paramCredentials.renew();
      if (debug) {
        System.out.println("Renewed Kerberos Ticket");
      }
    }
    catch (Exception localException)
    {
      localCredentials = null;
      if (debug) {
        System.out.println("Ticket could not be renewed : " + localException.getMessage());
      }
    }
    return localCredentials;
  }
  
  public boolean commit()
    throws LoginException
  {
    if (!succeeded) {
      return false;
    }
    if ((isInitiator) && (cred == null))
    {
      succeeded = false;
      throw new LoginException("Null Client Credential");
    }
    if (subject.isReadOnly())
    {
      cleanKerberosCred();
      throw new LoginException("Subject is Readonly");
    }
    Set localSet1 = subject.getPrivateCredentials();
    Set localSet2 = subject.getPrincipals();
    kerbClientPrinc = new KerberosPrincipal(principal.getName());
    if (isInitiator) {
      kerbTicket = Krb5Util.credsToTicket(cred);
    }
    int i;
    if ((storeKey) && (encKeys != null))
    {
      if (encKeys.length == 0)
      {
        succeeded = false;
        throw new LoginException("Null Server Key ");
      }
      kerbKeys = new KerberosKey[encKeys.length];
      for (i = 0; i < encKeys.length; i++)
      {
        Integer localInteger = encKeys[i].getKeyVersionNumber();
        kerbKeys[i] = new KerberosKey(kerbClientPrinc, encKeys[i].getBytes(), encKeys[i].getEType(), localInteger == null ? 0 : localInteger.intValue());
      }
    }
    if ((!unboundServer) && (!localSet2.contains(kerbClientPrinc))) {
      localSet2.add(kerbClientPrinc);
    }
    if ((kerbTicket != null) && (!localSet1.contains(kerbTicket))) {
      localSet1.add(kerbTicket);
    }
    if (storeKey) {
      if (encKeys == null)
      {
        if (ktab != null)
        {
          if (!localSet1.contains(ktab)) {
            localSet1.add(ktab);
          }
        }
        else
        {
          succeeded = false;
          throw new LoginException("No key to store");
        }
      }
      else {
        for (i = 0; i < kerbKeys.length; i++)
        {
          if (!localSet1.contains(kerbKeys[i])) {
            localSet1.add(kerbKeys[i]);
          }
          encKeys[i].destroy();
          encKeys[i] = null;
          if (debug)
          {
            System.out.println("Added server's key" + kerbKeys[i]);
            System.out.println("\t\t[Krb5LoginModule] added Krb5Principal  " + kerbClientPrinc.toString() + " to Subject");
          }
        }
      }
    }
    commitSucceeded = true;
    if (debug) {
      System.out.println("Commit Succeeded \n");
    }
    return true;
  }
  
  public boolean abort()
    throws LoginException
  {
    if (!succeeded) {
      return false;
    }
    if ((succeeded == true) && (!commitSucceeded))
    {
      succeeded = false;
      cleanKerberosCred();
    }
    else
    {
      logout();
    }
    return true;
  }
  
  public boolean logout()
    throws LoginException
  {
    if (debug) {
      System.out.println("\t\t[Krb5LoginModule]: Entering logout");
    }
    if (subject.isReadOnly())
    {
      cleanKerberosCred();
      throw new LoginException("Subject is Readonly");
    }
    subject.getPrincipals().remove(kerbClientPrinc);
    Iterator localIterator = subject.getPrivateCredentials().iterator();
    while (localIterator.hasNext())
    {
      Object localObject = localIterator.next();
      if (((localObject instanceof KerberosTicket)) || ((localObject instanceof KerberosKey)) || ((localObject instanceof javax.security.auth.kerberos.KeyTab))) {
        localIterator.remove();
      }
    }
    cleanKerberosCred();
    succeeded = false;
    commitSucceeded = false;
    if (debug) {
      System.out.println("\t\t[Krb5LoginModule]: logged out Subject");
    }
    return true;
  }
  
  private void cleanKerberosCred()
    throws LoginException
  {
    try
    {
      if (kerbTicket != null) {
        kerbTicket.destroy();
      }
      if (kerbKeys != null) {
        for (int i = 0; i < kerbKeys.length; i++) {
          kerbKeys[i].destroy();
        }
      }
    }
    catch (DestroyFailedException localDestroyFailedException)
    {
      throw new LoginException("Destroy Failed on Kerberos Private Credentials");
    }
    kerbTicket = null;
    kerbKeys = null;
    kerbClientPrinc = null;
  }
  
  private void cleanState()
  {
    if (succeeded)
    {
      if ((storePass) && (!sharedState.containsKey("javax.security.auth.login.name")) && (!sharedState.containsKey("javax.security.auth.login.password")))
      {
        sharedState.put("javax.security.auth.login.name", username);
        sharedState.put("javax.security.auth.login.password", password);
      }
    }
    else
    {
      encKeys = null;
      ktab = null;
      principal = null;
    }
    username = null;
    password = null;
    if ((krb5PrincName != null) && (krb5PrincName.length() != 0)) {
      krb5PrincName.delete(0, krb5PrincName.length());
    }
    krb5PrincName = null;
    if (clearPass)
    {
      sharedState.remove("javax.security.auth.login.name");
      sharedState.remove("javax.security.auth.login.password");
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\security\auth\module\Krb5LoginModule.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */