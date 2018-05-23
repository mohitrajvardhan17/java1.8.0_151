package com.sun.security.auth.module;

import com.sun.security.auth.UnixNumericGroupPrincipal;
import com.sun.security.auth.UnixNumericUserPrincipal;
import com.sun.security.auth.UnixPrincipal;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.LinkedList;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import javax.naming.InitialContext;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
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
public class JndiLoginModule
  implements LoginModule
{
  private static final ResourceBundle rb = (ResourceBundle)AccessController.doPrivileged(new PrivilegedAction()
  {
    public ResourceBundle run()
    {
      return ResourceBundle.getBundle("sun.security.util.AuthResources");
    }
  });
  public final String USER_PROVIDER = "user.provider.url";
  public final String GROUP_PROVIDER = "group.provider.url";
  private boolean debug = false;
  private boolean strongDebug = false;
  private String userProvider;
  private String groupProvider;
  private boolean useFirstPass = false;
  private boolean tryFirstPass = false;
  private boolean storePass = false;
  private boolean clearPass = false;
  private boolean succeeded = false;
  private boolean commitSucceeded = false;
  private String username;
  private char[] password;
  DirContext ctx;
  private UnixPrincipal userPrincipal;
  private UnixNumericUserPrincipal UIDPrincipal;
  private UnixNumericGroupPrincipal GIDPrincipal;
  private LinkedList<UnixNumericGroupPrincipal> supplementaryGroups = new LinkedList();
  private Subject subject;
  private CallbackHandler callbackHandler;
  private Map<String, Object> sharedState;
  private Map<String, ?> options;
  private static final String CRYPT = "{crypt}";
  private static final String USER_PWD = "userPassword";
  private static final String USER_UID = "uidNumber";
  private static final String USER_GID = "gidNumber";
  private static final String GROUP_ID = "gidNumber";
  private static final String NAME = "javax.security.auth.login.name";
  private static final String PWD = "javax.security.auth.login.password";
  
  public JndiLoginModule() {}
  
  public void initialize(Subject paramSubject, CallbackHandler paramCallbackHandler, Map<String, ?> paramMap1, Map<String, ?> paramMap2)
  {
    subject = paramSubject;
    callbackHandler = paramCallbackHandler;
    sharedState = paramMap1;
    options = paramMap2;
    debug = "true".equalsIgnoreCase((String)paramMap2.get("debug"));
    strongDebug = "true".equalsIgnoreCase((String)paramMap2.get("strongDebug"));
    userProvider = ((String)paramMap2.get("user.provider.url"));
    groupProvider = ((String)paramMap2.get("group.provider.url"));
    tryFirstPass = "true".equalsIgnoreCase((String)paramMap2.get("tryFirstPass"));
    useFirstPass = "true".equalsIgnoreCase((String)paramMap2.get("useFirstPass"));
    storePass = "true".equalsIgnoreCase((String)paramMap2.get("storePass"));
    clearPass = "true".equalsIgnoreCase((String)paramMap2.get("clearPass"));
  }
  
  public boolean login()
    throws LoginException
  {
    if (userProvider == null) {
      throw new LoginException("Error: Unable to locate JNDI user provider");
    }
    if (groupProvider == null) {
      throw new LoginException("Error: Unable to locate JNDI group provider");
    }
    if (debug)
    {
      System.out.println("\t\t[JndiLoginModule] user provider: " + userProvider);
      System.out.println("\t\t[JndiLoginModule] group provider: " + groupProvider);
    }
    if (tryFirstPass) {
      try
      {
        attemptAuthentication(true);
        succeeded = true;
        if (debug) {
          System.out.println("\t\t[JndiLoginModule] tryFirstPass succeeded");
        }
        return true;
      }
      catch (LoginException localLoginException1)
      {
        cleanState();
        if (debug) {
          System.out.println("\t\t[JndiLoginModule] tryFirstPass failed with:" + localLoginException1.toString());
        }
      }
    } else if (useFirstPass) {
      try
      {
        attemptAuthentication(true);
        succeeded = true;
        if (debug) {
          System.out.println("\t\t[JndiLoginModule] useFirstPass succeeded");
        }
        return true;
      }
      catch (LoginException localLoginException2)
      {
        cleanState();
        if (debug) {
          System.out.println("\t\t[JndiLoginModule] useFirstPass failed");
        }
        throw localLoginException2;
      }
    }
    try
    {
      attemptAuthentication(false);
      succeeded = true;
      if (debug) {
        System.out.println("\t\t[JndiLoginModule] regular authentication succeeded");
      }
      return true;
    }
    catch (LoginException localLoginException3)
    {
      cleanState();
      if (debug) {
        System.out.println("\t\t[JndiLoginModule] regular authentication failed");
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
      throw new LoginException("Subject is Readonly");
    }
    if (!subject.getPrincipals().contains(userPrincipal)) {
      subject.getPrincipals().add(userPrincipal);
    }
    if (!subject.getPrincipals().contains(UIDPrincipal)) {
      subject.getPrincipals().add(UIDPrincipal);
    }
    if (!subject.getPrincipals().contains(GIDPrincipal)) {
      subject.getPrincipals().add(GIDPrincipal);
    }
    for (int i = 0; i < supplementaryGroups.size(); i++) {
      if (!subject.getPrincipals().contains(supplementaryGroups.get(i))) {
        subject.getPrincipals().add(supplementaryGroups.get(i));
      }
    }
    if (debug)
    {
      System.out.println("\t\t[JndiLoginModule]: added UnixPrincipal,");
      System.out.println("\t\t\t\tUnixNumericUserPrincipal,");
      System.out.println("\t\t\t\tUnixNumericGroupPrincipal(s),");
      System.out.println("\t\t\t to Subject");
    }
    cleanState();
    commitSucceeded = true;
    return true;
  }
  
  public boolean abort()
    throws LoginException
  {
    if (debug) {
      System.out.println("\t\t[JndiLoginModule]: aborted authentication failed");
    }
    if (!succeeded) {
      return false;
    }
    if ((succeeded == true) && (!commitSucceeded))
    {
      succeeded = false;
      cleanState();
      userPrincipal = null;
      UIDPrincipal = null;
      GIDPrincipal = null;
      supplementaryGroups = new LinkedList();
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
      throw new LoginException("Subject is Readonly");
    }
    subject.getPrincipals().remove(userPrincipal);
    subject.getPrincipals().remove(UIDPrincipal);
    subject.getPrincipals().remove(GIDPrincipal);
    for (int i = 0; i < supplementaryGroups.size(); i++) {
      subject.getPrincipals().remove(supplementaryGroups.get(i));
    }
    cleanState();
    succeeded = false;
    commitSucceeded = false;
    userPrincipal = null;
    UIDPrincipal = null;
    GIDPrincipal = null;
    supplementaryGroups = new LinkedList();
    if (debug) {
      System.out.println("\t\t[JndiLoginModule]: logged out Subject");
    }
    return true;
  }
  
  private void attemptAuthentication(boolean paramBoolean)
    throws LoginException
  {
    String str1 = null;
    getUsernamePassword(paramBoolean);
    try
    {
      InitialContext localInitialContext = new InitialContext();
      ctx = ((DirContext)localInitialContext.lookup(userProvider));
      SearchControls localSearchControls = new SearchControls();
      NamingEnumeration localNamingEnumeration = ctx.search("", "(uid=" + username + ")", localSearchControls);
      if (localNamingEnumeration.hasMore())
      {
        SearchResult localSearchResult = (SearchResult)localNamingEnumeration.next();
        Attributes localAttributes = localSearchResult.getAttributes();
        Attribute localAttribute1 = localAttributes.get("userPassword");
        String str2 = new String((byte[])localAttribute1.get(), "UTF8");
        str1 = str2.substring("{crypt}".length());
        if (verifyPassword(str1, new String(password)) == true)
        {
          if (debug) {
            System.out.println("\t\t[JndiLoginModule] attemptAuthentication() succeeded");
          }
        }
        else
        {
          if (debug) {
            System.out.println("\t\t[JndiLoginModule] attemptAuthentication() failed");
          }
          throw new FailedLoginException("Login incorrect");
        }
        if ((storePass) && (!sharedState.containsKey("javax.security.auth.login.name")) && (!sharedState.containsKey("javax.security.auth.login.password")))
        {
          sharedState.put("javax.security.auth.login.name", username);
          sharedState.put("javax.security.auth.login.password", password);
        }
        userPrincipal = new UnixPrincipal(username);
        Attribute localAttribute2 = localAttributes.get("uidNumber");
        String str3 = (String)localAttribute2.get();
        UIDPrincipal = new UnixNumericUserPrincipal(str3);
        if ((debug) && (str3 != null)) {
          System.out.println("\t\t[JndiLoginModule] user: '" + username + "' has UID: " + str3);
        }
        Attribute localAttribute3 = localAttributes.get("gidNumber");
        String str4 = (String)localAttribute3.get();
        GIDPrincipal = new UnixNumericGroupPrincipal(str4, true);
        if ((debug) && (str4 != null)) {
          System.out.println("\t\t[JndiLoginModule] user: '" + username + "' has GID: " + str4);
        }
        ctx = ((DirContext)localInitialContext.lookup(groupProvider));
        localNamingEnumeration = ctx.search("", new BasicAttributes("memberUid", username));
        while (localNamingEnumeration.hasMore())
        {
          localSearchResult = (SearchResult)localNamingEnumeration.next();
          localAttributes = localSearchResult.getAttributes();
          localAttribute3 = localAttributes.get("gidNumber");
          String str5 = (String)localAttribute3.get();
          if (!str4.equals(str5))
          {
            UnixNumericGroupPrincipal localUnixNumericGroupPrincipal = new UnixNumericGroupPrincipal(str5, false);
            supplementaryGroups.add(localUnixNumericGroupPrincipal);
            if ((debug) && (str5 != null)) {
              System.out.println("\t\t[JndiLoginModule] user: '" + username + "' has Supplementary Group: " + str5);
            }
          }
        }
      }
      else
      {
        if (debug) {
          System.out.println("\t\t[JndiLoginModule]: User not found");
        }
        throw new FailedLoginException("User not found");
      }
    }
    catch (NamingException localNamingException)
    {
      if (debug)
      {
        System.out.println("\t\t[JndiLoginModule]:  User not found");
        localNamingException.printStackTrace();
      }
      throw new FailedLoginException("User not found");
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      if (debug)
      {
        System.out.println("\t\t[JndiLoginModule]:  password incorrectly encoded");
        localUnsupportedEncodingException.printStackTrace();
      }
      throw new LoginException("Login failure due to incorrect password encoding in the password database");
    }
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
      throw new LoginException("Error: no CallbackHandler available to garner authentication information from the user");
    }
    String str = userProvider.substring(0, userProvider.indexOf(":"));
    Callback[] arrayOfCallback = new Callback[2];
    arrayOfCallback[0] = new NameCallback(str + " " + rb.getString("username."));
    arrayOfCallback[1] = new PasswordCallback(str + " " + rb.getString("password."), false);
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
      throw new LoginException("Error: " + localUnsupportedCallbackException.getCallback().toString() + " not available to garner authentication information from the user");
    }
    if (strongDebug)
    {
      System.out.println("\t\t[JndiLoginModule] user entered username: " + username);
      System.out.print("\t\t[JndiLoginModule] user entered password: ");
      for (int i = 0; i < password.length; i++) {
        System.out.print(password[i]);
      }
      System.out.println();
    }
  }
  
  private boolean verifyPassword(String paramString1, String paramString2)
  {
    if (paramString1 == null) {
      return false;
    }
    Crypt localCrypt = new Crypt();
    try
    {
      byte[] arrayOfByte1 = paramString1.getBytes("UTF8");
      byte[] arrayOfByte2 = localCrypt.crypt(paramString2.getBytes("UTF8"), arrayOfByte1);
      if (arrayOfByte2.length != arrayOfByte1.length) {
        return false;
      }
      for (int i = 0; i < arrayOfByte2.length; i++) {
        if (arrayOfByte1[i] != arrayOfByte2[i]) {
          return false;
        }
      }
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      return false;
    }
    return true;
  }
  
  private void cleanState()
  {
    username = null;
    if (password != null)
    {
      for (int i = 0; i < password.length; i++) {
        password[i] = ' ';
      }
      password = null;
    }
    ctx = null;
    if (clearPass)
    {
      sharedState.remove("javax.security.auth.login.name");
      sharedState.remove("javax.security.auth.login.password");
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\security\auth\module\JndiLoginModule.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */