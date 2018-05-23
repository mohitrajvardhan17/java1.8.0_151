package com.sun.jmx.remote.security;

import com.sun.jmx.mbeanserver.GetPropertyAction;
import com.sun.jmx.mbeanserver.Util;
import com.sun.jmx.remote.util.ClassLogger;
import com.sun.jmx.remote.util.EnvHelp;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilePermission;
import java.io.IOException;
import java.security.AccessControlException;
import java.security.AccessController;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.management.remote.JMXPrincipal;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

public class FileLoginModule
  implements LoginModule
{
  private static final String DEFAULT_PASSWORD_FILE_NAME = (String)AccessController.doPrivileged(new GetPropertyAction("java.home")) + File.separatorChar + "lib" + File.separatorChar + "management" + File.separatorChar + "jmxremote.password";
  private static final String USERNAME_KEY = "javax.security.auth.login.name";
  private static final String PASSWORD_KEY = "javax.security.auth.login.password";
  private static final ClassLogger logger = new ClassLogger("javax.management.remote.misc", "FileLoginModule");
  private boolean useFirstPass = false;
  private boolean tryFirstPass = false;
  private boolean storePass = false;
  private boolean clearPass = false;
  private boolean succeeded = false;
  private boolean commitSucceeded = false;
  private String username;
  private char[] password;
  private JMXPrincipal user;
  private Subject subject;
  private CallbackHandler callbackHandler;
  private Map<String, Object> sharedState;
  private Map<String, ?> options;
  private String passwordFile;
  private String passwordFileDisplayName;
  private boolean userSuppliedPasswordFile;
  private boolean hasJavaHomePermission;
  private Properties userCredentials;
  
  public FileLoginModule() {}
  
  public void initialize(Subject paramSubject, CallbackHandler paramCallbackHandler, Map<String, ?> paramMap1, Map<String, ?> paramMap2)
  {
    subject = paramSubject;
    callbackHandler = paramCallbackHandler;
    sharedState = ((Map)Util.cast(paramMap1));
    options = paramMap2;
    tryFirstPass = "true".equalsIgnoreCase((String)paramMap2.get("tryFirstPass"));
    useFirstPass = "true".equalsIgnoreCase((String)paramMap2.get("useFirstPass"));
    storePass = "true".equalsIgnoreCase((String)paramMap2.get("storePass"));
    clearPass = "true".equalsIgnoreCase((String)paramMap2.get("clearPass"));
    passwordFile = ((String)paramMap2.get("passwordFile"));
    passwordFileDisplayName = passwordFile;
    userSuppliedPasswordFile = true;
    if (passwordFile == null)
    {
      passwordFile = DEFAULT_PASSWORD_FILE_NAME;
      userSuppliedPasswordFile = false;
      try
      {
        System.getProperty("java.home");
        hasJavaHomePermission = true;
        passwordFileDisplayName = passwordFile;
      }
      catch (SecurityException localSecurityException)
      {
        hasJavaHomePermission = false;
        passwordFileDisplayName = "jmxremote.password";
      }
    }
  }
  
  public boolean login()
    throws LoginException
  {
    try
    {
      loadPasswordFile();
    }
    catch (IOException localIOException)
    {
      LoginException localLoginException4 = new LoginException("Error: unable to load the password file: " + passwordFileDisplayName);
      throw ((LoginException)EnvHelp.initCause(localLoginException4, localIOException));
    }
    if (userCredentials == null) {
      throw new LoginException("Error: unable to locate the users' credentials.");
    }
    if (logger.debugOn()) {
      logger.debug("login", "Using password file: " + passwordFileDisplayName);
    }
    if (tryFirstPass) {
      try
      {
        attemptAuthentication(true);
        succeeded = true;
        if (logger.debugOn()) {
          logger.debug("login", "Authentication using cached password has succeeded");
        }
        return true;
      }
      catch (LoginException localLoginException1)
      {
        cleanState();
        logger.debug("login", "Authentication using cached password has failed");
      }
    } else if (useFirstPass) {
      try
      {
        attemptAuthentication(true);
        succeeded = true;
        if (logger.debugOn()) {
          logger.debug("login", "Authentication using cached password has succeeded");
        }
        return true;
      }
      catch (LoginException localLoginException2)
      {
        cleanState();
        logger.debug("login", "Authentication using cached password has failed");
        throw localLoginException2;
      }
    }
    if (logger.debugOn()) {
      logger.debug("login", "Acquiring password");
    }
    try
    {
      attemptAuthentication(false);
      succeeded = true;
      if (logger.debugOn()) {
        logger.debug("login", "Authentication has succeeded");
      }
      return true;
    }
    catch (LoginException localLoginException3)
    {
      cleanState();
      logger.debug("login", "Authentication has failed");
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
    if (!subject.getPrincipals().contains(user)) {
      subject.getPrincipals().add(user);
    }
    if (logger.debugOn()) {
      logger.debug("commit", "Authentication has completed successfully");
    }
    cleanState();
    commitSucceeded = true;
    return true;
  }
  
  public boolean abort()
    throws LoginException
  {
    if (logger.debugOn()) {
      logger.debug("abort", "Authentication has not completed successfully");
    }
    if (!succeeded) {
      return false;
    }
    if ((succeeded == true) && (!commitSucceeded))
    {
      succeeded = false;
      cleanState();
      user = null;
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
    subject.getPrincipals().remove(user);
    cleanState();
    succeeded = false;
    commitSucceeded = false;
    user = null;
    if (logger.debugOn()) {
      logger.debug("logout", "Subject is being logged out");
    }
    return true;
  }
  
  private void attemptAuthentication(boolean paramBoolean)
    throws LoginException
  {
    getUsernamePassword(paramBoolean);
    String str;
    if (((str = userCredentials.getProperty(username)) == null) || (!str.equals(new String(password))))
    {
      if (logger.debugOn()) {
        logger.debug("login", "Invalid username or password");
      }
      throw new FailedLoginException("Invalid username or password");
    }
    if ((storePass) && (!sharedState.containsKey("javax.security.auth.login.name")) && (!sharedState.containsKey("javax.security.auth.login.password")))
    {
      sharedState.put("javax.security.auth.login.name", username);
      sharedState.put("javax.security.auth.login.password", password);
    }
    user = new JMXPrincipal(username);
    if (logger.debugOn()) {
      logger.debug("login", "User '" + username + "' successfully validated");
    }
  }
  
  private void loadPasswordFile()
    throws IOException
  {
    FileInputStream localFileInputStream;
    try
    {
      localFileInputStream = new FileInputStream(passwordFile);
    }
    catch (SecurityException localSecurityException)
    {
      if ((userSuppliedPasswordFile) || (hasJavaHomePermission)) {
        throw localSecurityException;
      }
      FilePermission localFilePermission = new FilePermission(passwordFileDisplayName, "read");
      AccessControlException localAccessControlException = new AccessControlException("access denied " + localFilePermission.toString());
      localAccessControlException.setStackTrace(localSecurityException.getStackTrace());
      throw localAccessControlException;
    }
    try
    {
      BufferedInputStream localBufferedInputStream = new BufferedInputStream(localFileInputStream);
      try
      {
        userCredentials = new Properties();
        userCredentials.load(localBufferedInputStream);
      }
      finally {}
    }
    finally
    {
      localFileInputStream.close();
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
    Callback[] arrayOfCallback = new Callback[2];
    arrayOfCallback[0] = new NameCallback("username");
    arrayOfCallback[1] = new PasswordCallback("password", false);
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
      localLoginException = new LoginException(localIOException.toString());
      throw ((LoginException)EnvHelp.initCause(localLoginException, localIOException));
    }
    catch (UnsupportedCallbackException localUnsupportedCallbackException)
    {
      LoginException localLoginException = new LoginException("Error: " + localUnsupportedCallbackException.getCallback().toString() + " not available to garner authentication information from the user");
      throw ((LoginException)EnvHelp.initCause(localLoginException, localUnsupportedCallbackException));
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
    if (clearPass)
    {
      sharedState.remove("javax.security.auth.login.name");
      sharedState.remove("javax.security.auth.login.password");
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\remote\security\FileLoginModule.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */