package com.sun.security.auth.module;

import com.sun.security.auth.LdapPrincipal;
import com.sun.security.auth.UserPrincipal;
import java.io.IOException;
import java.io.PrintStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.naming.InvalidNameException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import jdk.Exported;

@Exported
public class LdapLoginModule
  implements LoginModule
{
  private static final ResourceBundle rb = (ResourceBundle)AccessController.doPrivileged(new PrivilegedAction()
  {
    public ResourceBundle run()
    {
      return ResourceBundle.getBundle("sun.security.util.AuthResources");
    }
  });
  private static final String USERNAME_KEY = "javax.security.auth.login.name";
  private static final String PASSWORD_KEY = "javax.security.auth.login.password";
  private static final String USER_PROVIDER = "userProvider";
  private static final String USER_FILTER = "userFilter";
  private static final String AUTHC_IDENTITY = "authIdentity";
  private static final String AUTHZ_IDENTITY = "authzIdentity";
  private static final String USERNAME_TOKEN = "{USERNAME}";
  private static final Pattern USERNAME_PATTERN = Pattern.compile("\\{USERNAME\\}");
  private String userProvider;
  private String userFilter;
  private String authcIdentity;
  private String authzIdentity;
  private String authzIdentityAttr = null;
  private boolean useSSL = true;
  private boolean authFirst = false;
  private boolean authOnly = false;
  private boolean useFirstPass = false;
  private boolean tryFirstPass = false;
  private boolean storePass = false;
  private boolean clearPass = false;
  private boolean debug = false;
  private boolean succeeded = false;
  private boolean commitSucceeded = false;
  private String username;
  private char[] password;
  private LdapPrincipal ldapPrincipal;
  private UserPrincipal userPrincipal;
  private UserPrincipal authzPrincipal;
  private Subject subject;
  private CallbackHandler callbackHandler;
  private Map<String, Object> sharedState;
  private Map<String, ?> options;
  private LdapContext ctx;
  private Matcher identityMatcher = null;
  private Matcher filterMatcher = null;
  private Hashtable<String, Object> ldapEnvironment;
  private SearchControls constraints = null;
  
  public LdapLoginModule() {}
  
  public void initialize(Subject paramSubject, CallbackHandler paramCallbackHandler, Map<String, ?> paramMap1, Map<String, ?> paramMap2)
  {
    subject = paramSubject;
    callbackHandler = paramCallbackHandler;
    sharedState = paramMap1;
    options = paramMap2;
    ldapEnvironment = new Hashtable(9);
    ldapEnvironment.put("java.naming.factory.initial", "com.sun.jndi.ldap.LdapCtxFactory");
    Iterator localIterator = paramMap2.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      if (str.indexOf(".") > -1) {
        ldapEnvironment.put(str, paramMap2.get(str));
      }
    }
    userProvider = ((String)paramMap2.get("userProvider"));
    if (userProvider != null) {
      ldapEnvironment.put("java.naming.provider.url", userProvider);
    }
    authcIdentity = ((String)paramMap2.get("authIdentity"));
    if ((authcIdentity != null) && (authcIdentity.indexOf("{USERNAME}") != -1)) {
      identityMatcher = USERNAME_PATTERN.matcher(authcIdentity);
    }
    userFilter = ((String)paramMap2.get("userFilter"));
    if (userFilter != null)
    {
      if (userFilter.indexOf("{USERNAME}") != -1) {
        filterMatcher = USERNAME_PATTERN.matcher(userFilter);
      }
      constraints = new SearchControls();
      constraints.setSearchScope(2);
      constraints.setReturningAttributes(new String[0]);
    }
    authzIdentity = ((String)paramMap2.get("authzIdentity"));
    if ((authzIdentity != null) && (authzIdentity.startsWith("{")) && (authzIdentity.endsWith("}")))
    {
      if (constraints != null)
      {
        authzIdentityAttr = authzIdentity.substring(1, authzIdentity.length() - 1);
        constraints.setReturningAttributes(new String[] { authzIdentityAttr });
      }
      authzIdentity = null;
    }
    if (authcIdentity != null) {
      if (userFilter != null) {
        authFirst = true;
      } else {
        authOnly = true;
      }
    }
    if ("false".equalsIgnoreCase((String)paramMap2.get("useSSL")))
    {
      useSSL = false;
      ldapEnvironment.remove("java.naming.security.protocol");
    }
    else
    {
      ldapEnvironment.put("java.naming.security.protocol", "ssl");
    }
    tryFirstPass = "true".equalsIgnoreCase((String)paramMap2.get("tryFirstPass"));
    useFirstPass = "true".equalsIgnoreCase((String)paramMap2.get("useFirstPass"));
    storePass = "true".equalsIgnoreCase((String)paramMap2.get("storePass"));
    clearPass = "true".equalsIgnoreCase((String)paramMap2.get("clearPass"));
    debug = "true".equalsIgnoreCase((String)paramMap2.get("debug"));
    if (debug) {
      if (authFirst) {
        System.out.println("\t\t[LdapLoginModule] authentication-first mode; " + (useSSL ? "SSL enabled" : "SSL disabled"));
      } else if (authOnly) {
        System.out.println("\t\t[LdapLoginModule] authentication-only mode; " + (useSSL ? "SSL enabled" : "SSL disabled"));
      } else {
        System.out.println("\t\t[LdapLoginModule] search-first mode; " + (useSSL ? "SSL enabled" : "SSL disabled"));
      }
    }
  }
  
  public boolean login()
    throws LoginException
  {
    if (userProvider == null) {
      throw new LoginException("Unable to locate the LDAP directory service");
    }
    if (debug) {
      System.out.println("\t\t[LdapLoginModule] user provider: " + userProvider);
    }
    if (tryFirstPass) {
      try
      {
        attemptAuthentication(true);
        succeeded = true;
        if (debug) {
          System.out.println("\t\t[LdapLoginModule] tryFirstPass succeeded");
        }
        return true;
      }
      catch (LoginException localLoginException1)
      {
        cleanState();
        if (debug) {
          System.out.println("\t\t[LdapLoginModule] tryFirstPass failed: " + localLoginException1.toString());
        }
      }
    } else if (useFirstPass) {
      try
      {
        attemptAuthentication(true);
        succeeded = true;
        if (debug) {
          System.out.println("\t\t[LdapLoginModule] useFirstPass succeeded");
        }
        return true;
      }
      catch (LoginException localLoginException2)
      {
        cleanState();
        if (debug) {
          System.out.println("\t\t[LdapLoginModule] useFirstPass failed");
        }
        throw localLoginException2;
      }
    }
    try
    {
      attemptAuthentication(false);
      succeeded = true;
      if (debug) {
        System.out.println("\t\t[LdapLoginModule] authentication succeeded");
      }
      return true;
    }
    catch (LoginException localLoginException3)
    {
      cleanState();
      if (debug) {
        System.out.println("\t\t[LdapLoginModule] authentication failed");
      }
      throw localLoginException3;
    }
  }
  
  public boolean commit()
    throws LoginException
  {
    if (!succeeded) {
      return false;
    }
    if (subject.isReadOnly())
    {
      cleanState();
      throw new LoginException("Subject is read-only");
    }
    Set localSet = subject.getPrincipals();
    if (!localSet.contains(ldapPrincipal)) {
      localSet.add(ldapPrincipal);
    }
    if (debug) {
      System.out.println("\t\t[LdapLoginModule] added LdapPrincipal \"" + ldapPrincipal + "\" to Subject");
    }
    if (!localSet.contains(userPrincipal)) {
      localSet.add(userPrincipal);
    }
    if (debug) {
      System.out.println("\t\t[LdapLoginModule] added UserPrincipal \"" + userPrincipal + "\" to Subject");
    }
    if ((authzPrincipal != null) && (!localSet.contains(authzPrincipal)))
    {
      localSet.add(authzPrincipal);
      if (debug) {
        System.out.println("\t\t[LdapLoginModule] added UserPrincipal \"" + authzPrincipal + "\" to Subject");
      }
    }
    cleanState();
    commitSucceeded = true;
    return true;
  }
  
  public boolean abort()
    throws LoginException
  {
    if (debug) {
      System.out.println("\t\t[LdapLoginModule] aborted authentication");
    }
    if (!succeeded) {
      return false;
    }
    if ((succeeded == true) && (!commitSucceeded))
    {
      succeeded = false;
      cleanState();
      ldapPrincipal = null;
      userPrincipal = null;
      authzPrincipal = null;
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
    if (subject.isReadOnly())
    {
      cleanState();
      throw new LoginException("Subject is read-only");
    }
    Set localSet = subject.getPrincipals();
    localSet.remove(ldapPrincipal);
    localSet.remove(userPrincipal);
    if (authzIdentity != null) {
      localSet.remove(authzPrincipal);
    }
    cleanState();
    succeeded = false;
    commitSucceeded = false;
    ldapPrincipal = null;
    userPrincipal = null;
    authzPrincipal = null;
    if (debug) {
      System.out.println("\t\t[LdapLoginModule] logged out Subject");
    }
    return true;
  }
  
  private void attemptAuthentication(boolean paramBoolean)
    throws LoginException
  {
    getUsernamePassword(paramBoolean);
    if ((password == null) || (password.length == 0)) {
      throw new FailedLoginException("No password was supplied");
    }
    Object localObject = "";
    if ((authFirst) || (authOnly))
    {
      String str = replaceUsernameToken(identityMatcher, authcIdentity);
      ldapEnvironment.put("java.naming.security.credentials", password);
      ldapEnvironment.put("java.naming.security.principal", str);
      if (debug) {
        System.out.println("\t\t[LdapLoginModule] attempting to authenticate user: " + username);
      }
      try
      {
        ctx = new InitialLdapContext(ldapEnvironment, null);
      }
      catch (NamingException localNamingException3)
      {
        throw ((LoginException)new FailedLoginException("Cannot bind to LDAP server").initCause(localNamingException3));
      }
      if (userFilter != null) {
        localObject = findUserDN(ctx);
      } else {
        localObject = str;
      }
    }
    else
    {
      try
      {
        ctx = new InitialLdapContext(ldapEnvironment, null);
      }
      catch (NamingException localNamingException1)
      {
        throw ((LoginException)new FailedLoginException("Cannot connect to LDAP server").initCause(localNamingException1));
      }
      localObject = findUserDN(ctx);
      try
      {
        ctx.addToEnvironment("java.naming.security.authentication", "simple");
        ctx.addToEnvironment("java.naming.security.principal", localObject);
        ctx.addToEnvironment("java.naming.security.credentials", password);
        if (debug) {
          System.out.println("\t\t[LdapLoginModule] attempting to authenticate user: " + username);
        }
        ctx.reconnect(null);
      }
      catch (NamingException localNamingException2)
      {
        throw ((LoginException)new FailedLoginException("Cannot bind to LDAP server").initCause(localNamingException2));
      }
    }
    if ((storePass) && (!sharedState.containsKey("javax.security.auth.login.name")) && (!sharedState.containsKey("javax.security.auth.login.password")))
    {
      sharedState.put("javax.security.auth.login.name", username);
      sharedState.put("javax.security.auth.login.password", password);
    }
    userPrincipal = new UserPrincipal(username);
    if (authzIdentity != null) {
      authzPrincipal = new UserPrincipal(authzIdentity);
    }
    try
    {
      ldapPrincipal = new LdapPrincipal((String)localObject);
    }
    catch (InvalidNameException localInvalidNameException)
    {
      if (debug) {
        System.out.println("\t\t[LdapLoginModule] cannot create LdapPrincipal: bad DN");
      }
      throw ((LoginException)new FailedLoginException("Cannot create LdapPrincipal").initCause(localInvalidNameException));
    }
  }
  
  private String findUserDN(LdapContext paramLdapContext)
    throws LoginException
  {
    String str = "";
    if (userFilter != null)
    {
      if (debug) {
        System.out.println("\t\t[LdapLoginModule] searching for entry belonging to user: " + username);
      }
    }
    else
    {
      if (debug) {
        System.out.println("\t\t[LdapLoginModule] cannot search for entry belonging to user: " + username);
      }
      throw new FailedLoginException("Cannot find user's LDAP entry");
    }
    try
    {
      NamingEnumeration localNamingEnumeration = paramLdapContext.search("", replaceUsernameToken(filterMatcher, userFilter), constraints);
      if (localNamingEnumeration.hasMore())
      {
        SearchResult localSearchResult = (SearchResult)localNamingEnumeration.next();
        str = localSearchResult.getNameInNamespace();
        if (debug) {
          System.out.println("\t\t[LdapLoginModule] found entry: " + str);
        }
        if (authzIdentityAttr != null)
        {
          Attribute localAttribute = localSearchResult.getAttributes().get(authzIdentityAttr);
          if (localAttribute != null)
          {
            Object localObject = localAttribute.get();
            if ((localObject instanceof String)) {
              authzIdentity = ((String)localObject);
            }
          }
        }
        localNamingEnumeration.close();
      }
      else if (debug)
      {
        System.out.println("\t\t[LdapLoginModule] user's entry not found");
      }
    }
    catch (NamingException localNamingException) {}
    if (str.equals("")) {
      throw new FailedLoginException("Cannot find user's LDAP entry");
    }
    return str;
  }
  
  private String replaceUsernameToken(Matcher paramMatcher, String paramString)
  {
    return paramMatcher != null ? paramMatcher.replaceAll(username) : paramString;
  }
  
  private void getUsernamePassword(boolean paramBoolean)
    throws LoginException
  {
    if (paramBoolean)
    {
      username = ((String)sharedState.get("javax.security.auth.login.name"));
      password = ((char[])sharedState.get("javax.security.auth.login.password"));
      return;
    }
    if (callbackHandler == null) {
      throw new LoginException("No CallbackHandler available to acquire authentication information from the user");
    }
    Callback[] arrayOfCallback = new Callback[2];
    arrayOfCallback[0] = new NameCallback(rb.getString("username."));
    arrayOfCallback[1] = new PasswordCallback(rb.getString("password."), false);
    try
    {
      callbackHandler.handle(arrayOfCallback);
      username = ((NameCallback)arrayOfCallback[0]).getName();
      char[] arrayOfChar = ((PasswordCallback)arrayOfCallback[1]).getPassword();
      password = new char[arrayOfChar.length];
      System.arraycopy(arrayOfChar, 0, password, 0, arrayOfChar.length);
      ((PasswordCallback)arrayOfCallback[1]).clearPassword();
    }
    catch (IOException localIOException)
    {
      throw new LoginException(localIOException.toString());
    }
    catch (UnsupportedCallbackException localUnsupportedCallbackException)
    {
      throw new LoginException("Error: " + localUnsupportedCallbackException.getCallback().toString() + " not available to acquire authentication information from the user");
    }
  }
  
  private void cleanState()
  {
    username = null;
    if (password != null)
    {
      Arrays.fill(password, ' ');
      password = null;
    }
    try
    {
      if (ctx != null) {
        ctx.close();
      }
    }
    catch (NamingException localNamingException) {}
    ctx = null;
    if (clearPass)
    {
      sharedState.remove("javax.security.auth.login.name");
      sharedState.remove("javax.security.auth.login.password");
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\security\auth\module\LdapLoginModule.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */