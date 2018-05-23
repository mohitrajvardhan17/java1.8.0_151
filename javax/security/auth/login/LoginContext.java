package javax.security.auth.login;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.Security;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import javax.security.auth.AuthPermission;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import sun.security.util.Debug;
import sun.security.util.PendingException;
import sun.security.util.ResourcesMgr;

public class LoginContext
{
  private static final String INIT_METHOD = "initialize";
  private static final String LOGIN_METHOD = "login";
  private static final String COMMIT_METHOD = "commit";
  private static final String ABORT_METHOD = "abort";
  private static final String LOGOUT_METHOD = "logout";
  private static final String OTHER = "other";
  private static final String DEFAULT_HANDLER = "auth.login.defaultCallbackHandler";
  private Subject subject = null;
  private boolean subjectProvided = false;
  private boolean loginSucceeded = false;
  private CallbackHandler callbackHandler;
  private Map<String, ?> state = new HashMap();
  private Configuration config;
  private AccessControlContext creatorAcc = null;
  private ModuleInfo[] moduleStack;
  private ClassLoader contextClassLoader = null;
  private static final Class<?>[] PARAMS = new Class[0];
  private int moduleIndex = 0;
  private LoginException firstError = null;
  private LoginException firstRequiredError = null;
  private boolean success = false;
  private static final Debug debug = Debug.getInstance("logincontext", "\t[LoginContext]");
  
  private void init(String paramString)
    throws LoginException
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if ((localSecurityManager != null) && (creatorAcc == null)) {
      localSecurityManager.checkPermission(new AuthPermission("createLoginContext." + paramString));
    }
    if (paramString == null) {
      throw new LoginException(ResourcesMgr.getString("Invalid.null.input.name"));
    }
    if (config == null) {
      config = ((Configuration)AccessController.doPrivileged(new PrivilegedAction()
      {
        public Configuration run()
        {
          return Configuration.getConfiguration();
        }
      }));
    }
    AppConfigurationEntry[] arrayOfAppConfigurationEntry = config.getAppConfigurationEntry(paramString);
    if (arrayOfAppConfigurationEntry == null)
    {
      if ((localSecurityManager != null) && (creatorAcc == null)) {
        localSecurityManager.checkPermission(new AuthPermission("createLoginContext.other"));
      }
      arrayOfAppConfigurationEntry = config.getAppConfigurationEntry("other");
      if (arrayOfAppConfigurationEntry == null)
      {
        MessageFormat localMessageFormat = new MessageFormat(ResourcesMgr.getString("No.LoginModules.configured.for.name"));
        Object[] arrayOfObject = { paramString };
        throw new LoginException(localMessageFormat.format(arrayOfObject));
      }
    }
    moduleStack = new ModuleInfo[arrayOfAppConfigurationEntry.length];
    for (int i = 0; i < arrayOfAppConfigurationEntry.length; i++) {
      moduleStack[i] = new ModuleInfo(new AppConfigurationEntry(arrayOfAppConfigurationEntry[i].getLoginModuleName(), arrayOfAppConfigurationEntry[i].getControlFlag(), arrayOfAppConfigurationEntry[i].getOptions()), null);
    }
    contextClassLoader = ((ClassLoader)AccessController.doPrivileged(new PrivilegedAction()
    {
      public ClassLoader run()
      {
        ClassLoader localClassLoader = Thread.currentThread().getContextClassLoader();
        if (localClassLoader == null) {
          localClassLoader = ClassLoader.getSystemClassLoader();
        }
        return localClassLoader;
      }
    }));
  }
  
  private void loadDefaultCallbackHandler()
    throws LoginException
  {
    try
    {
      final ClassLoader localClassLoader = contextClassLoader;
      callbackHandler = ((CallbackHandler)AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        public CallbackHandler run()
          throws Exception
        {
          String str = Security.getProperty("auth.login.defaultCallbackHandler");
          if ((str == null) || (str.length() == 0)) {
            return null;
          }
          Class localClass = Class.forName(str, true, localClassLoader).asSubclass(CallbackHandler.class);
          return (CallbackHandler)localClass.newInstance();
        }
      }));
    }
    catch (PrivilegedActionException localPrivilegedActionException)
    {
      throw new LoginException(localPrivilegedActionException.getException().toString());
    }
    if ((callbackHandler != null) && (creatorAcc == null)) {
      callbackHandler = new SecureCallbackHandler(AccessController.getContext(), callbackHandler);
    }
  }
  
  public LoginContext(String paramString)
    throws LoginException
  {
    init(paramString);
    loadDefaultCallbackHandler();
  }
  
  public LoginContext(String paramString, Subject paramSubject)
    throws LoginException
  {
    init(paramString);
    if (paramSubject == null) {
      throw new LoginException(ResourcesMgr.getString("invalid.null.Subject.provided"));
    }
    subject = paramSubject;
    subjectProvided = true;
    loadDefaultCallbackHandler();
  }
  
  public LoginContext(String paramString, CallbackHandler paramCallbackHandler)
    throws LoginException
  {
    init(paramString);
    if (paramCallbackHandler == null) {
      throw new LoginException(ResourcesMgr.getString("invalid.null.CallbackHandler.provided"));
    }
    callbackHandler = new SecureCallbackHandler(AccessController.getContext(), paramCallbackHandler);
  }
  
  public LoginContext(String paramString, Subject paramSubject, CallbackHandler paramCallbackHandler)
    throws LoginException
  {
    this(paramString, paramSubject);
    if (paramCallbackHandler == null) {
      throw new LoginException(ResourcesMgr.getString("invalid.null.CallbackHandler.provided"));
    }
    callbackHandler = new SecureCallbackHandler(AccessController.getContext(), paramCallbackHandler);
  }
  
  public LoginContext(String paramString, Subject paramSubject, CallbackHandler paramCallbackHandler, Configuration paramConfiguration)
    throws LoginException
  {
    config = paramConfiguration;
    if (paramConfiguration != null) {
      creatorAcc = AccessController.getContext();
    }
    init(paramString);
    if (paramSubject != null)
    {
      subject = paramSubject;
      subjectProvided = true;
    }
    if (paramCallbackHandler == null) {
      loadDefaultCallbackHandler();
    } else if (creatorAcc == null) {
      callbackHandler = new SecureCallbackHandler(AccessController.getContext(), paramCallbackHandler);
    } else {
      callbackHandler = paramCallbackHandler;
    }
  }
  
  public void login()
    throws LoginException
  {
    loginSucceeded = false;
    if (subject == null) {
      subject = new Subject();
    }
    try
    {
      invokePriv("login");
      invokePriv("commit");
      loginSucceeded = true;
    }
    catch (LoginException localLoginException1)
    {
      try
      {
        invokePriv("abort");
      }
      catch (LoginException localLoginException2)
      {
        throw localLoginException1;
      }
      throw localLoginException1;
    }
  }
  
  public void logout()
    throws LoginException
  {
    if (subject == null) {
      throw new LoginException(ResourcesMgr.getString("null.subject.logout.called.before.login"));
    }
    invokePriv("logout");
  }
  
  public Subject getSubject()
  {
    if ((!loginSucceeded) && (!subjectProvided)) {
      return null;
    }
    return subject;
  }
  
  private void clearState()
  {
    moduleIndex = 0;
    firstError = null;
    firstRequiredError = null;
    success = false;
  }
  
  private void throwException(LoginException paramLoginException1, LoginException paramLoginException2)
    throws LoginException
  {
    clearState();
    LoginException localLoginException = paramLoginException1 != null ? paramLoginException1 : paramLoginException2;
    throw localLoginException;
  }
  
  private void invokePriv(final String paramString)
    throws LoginException
  {
    try
    {
      AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        public Void run()
          throws LoginException
        {
          LoginContext.this.invoke(paramString);
          return null;
        }
      }, creatorAcc);
    }
    catch (PrivilegedActionException localPrivilegedActionException)
    {
      throw ((LoginException)localPrivilegedActionException.getException());
    }
  }
  
  private void invoke(String paramString)
    throws LoginException
  {
    int i = moduleIndex;
    while (i < moduleStack.length)
    {
      try
      {
        int j = 0;
        localObject1 = null;
        if (moduleStack[i].module != null)
        {
          localObject1 = moduleStack[i].module.getClass().getMethods();
        }
        else
        {
          localObject2 = Class.forName(moduleStack[i].entry.getLoginModuleName(), true, contextClassLoader);
          Constructor localConstructor = ((Class)localObject2).getConstructor(PARAMS);
          Object[] arrayOfObject1 = new Object[0];
          moduleStack[i].module = localConstructor.newInstance(arrayOfObject1);
          localObject1 = moduleStack[i].module.getClass().getMethods();
          for (j = 0; (j < localObject1.length) && (!localObject1[j].getName().equals("initialize")); j++) {}
          Object[] arrayOfObject2 = { subject, callbackHandler, state, moduleStack[i].entry.getOptions() };
          localObject1[j].invoke(moduleStack[i].module, arrayOfObject2);
        }
        for (j = 0; (j < localObject1.length) && (!localObject1[j].getName().equals(paramString)); j++) {}
        localObject2 = new Object[0];
        boolean bool = ((Boolean)localObject1[j].invoke(moduleStack[i].module, (Object[])localObject2)).booleanValue();
        if (bool == true)
        {
          if ((!paramString.equals("abort")) && (!paramString.equals("logout")) && (moduleStack[i].entry.getControlFlag() == AppConfigurationEntry.LoginModuleControlFlag.SUFFICIENT) && (firstRequiredError == null))
          {
            clearState();
            if (debug != null) {
              debug.println(paramString + " SUFFICIENT success");
            }
            return;
          }
          if (debug != null) {
            debug.println(paramString + " success");
          }
          success = true;
        }
        else if (debug != null)
        {
          debug.println(paramString + " ignored");
        }
      }
      catch (NoSuchMethodException localNoSuchMethodException)
      {
        localObject1 = new MessageFormat(ResourcesMgr.getString("unable.to.instantiate.LoginModule.module.because.it.does.not.provide.a.no.argument.constructor"));
        localObject2 = new Object[] { moduleStack[i].entry.getLoginModuleName() };
        throwException(null, new LoginException(((MessageFormat)localObject1).format(localObject2)));
      }
      catch (InstantiationException localInstantiationException)
      {
        throwException(null, new LoginException(ResourcesMgr.getString("unable.to.instantiate.LoginModule.") + localInstantiationException.getMessage()));
      }
      catch (ClassNotFoundException localClassNotFoundException)
      {
        throwException(null, new LoginException(ResourcesMgr.getString("unable.to.find.LoginModule.class.") + localClassNotFoundException.getMessage()));
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        throwException(null, new LoginException(ResourcesMgr.getString("unable.to.access.LoginModule.") + localIllegalAccessException.getMessage()));
      }
      catch (InvocationTargetException localInvocationTargetException)
      {
        Object localObject1;
        Object localObject2;
        if (((localInvocationTargetException.getCause() instanceof PendingException)) && (paramString.equals("login"))) {
          throw ((PendingException)localInvocationTargetException.getCause());
        }
        if ((localInvocationTargetException.getCause() instanceof LoginException))
        {
          localObject1 = (LoginException)localInvocationTargetException.getCause();
        }
        else if ((localInvocationTargetException.getCause() instanceof SecurityException))
        {
          localObject1 = new LoginException("Security Exception");
          ((LoginException)localObject1).initCause(new SecurityException());
          if (debug != null)
          {
            debug.println("original security exception with detail msg replaced by new exception with empty detail msg");
            debug.println("original security exception: " + localInvocationTargetException.getCause().toString());
          }
        }
        else
        {
          localObject2 = new StringWriter();
          localInvocationTargetException.getCause().printStackTrace(new PrintWriter((Writer)localObject2));
          ((StringWriter)localObject2).flush();
          localObject1 = new LoginException(((StringWriter)localObject2).toString());
        }
        if (moduleStack[i].entry.getControlFlag() == AppConfigurationEntry.LoginModuleControlFlag.REQUISITE)
        {
          if (debug != null) {
            debug.println(paramString + " REQUISITE failure");
          }
          if ((paramString.equals("abort")) || (paramString.equals("logout")))
          {
            if (firstRequiredError == null) {
              firstRequiredError = ((LoginException)localObject1);
            }
          }
          else {
            throwException(firstRequiredError, (LoginException)localObject1);
          }
        }
        else if (moduleStack[i].entry.getControlFlag() == AppConfigurationEntry.LoginModuleControlFlag.REQUIRED)
        {
          if (debug != null) {
            debug.println(paramString + " REQUIRED failure");
          }
          if (firstRequiredError == null) {
            firstRequiredError = ((LoginException)localObject1);
          }
        }
        else
        {
          if (debug != null) {
            debug.println(paramString + " OPTIONAL failure");
          }
          if (firstError == null) {
            firstError = ((LoginException)localObject1);
          }
        }
      }
      i++;
      moduleIndex += 1;
    }
    if (firstRequiredError != null)
    {
      throwException(firstRequiredError, null);
    }
    else if ((!success) && (firstError != null))
    {
      throwException(firstError, null);
    }
    else if (!success)
    {
      throwException(new LoginException(ResourcesMgr.getString("Login.Failure.all.modules.ignored")), null);
    }
    else
    {
      clearState();
      return;
    }
  }
  
  private static class ModuleInfo
  {
    AppConfigurationEntry entry;
    Object module;
    
    ModuleInfo(AppConfigurationEntry paramAppConfigurationEntry, Object paramObject)
    {
      entry = paramAppConfigurationEntry;
      module = paramObject;
    }
  }
  
  private static class SecureCallbackHandler
    implements CallbackHandler
  {
    private final AccessControlContext acc;
    private final CallbackHandler ch;
    
    SecureCallbackHandler(AccessControlContext paramAccessControlContext, CallbackHandler paramCallbackHandler)
    {
      acc = paramAccessControlContext;
      ch = paramCallbackHandler;
    }
    
    public void handle(final Callback[] paramArrayOfCallback)
      throws IOException, UnsupportedCallbackException
    {
      try
      {
        AccessController.doPrivileged(new PrivilegedExceptionAction()
        {
          public Void run()
            throws IOException, UnsupportedCallbackException
          {
            ch.handle(paramArrayOfCallback);
            return null;
          }
        }, acc);
      }
      catch (PrivilegedActionException localPrivilegedActionException)
      {
        if ((localPrivilegedActionException.getException() instanceof IOException)) {
          throw ((IOException)localPrivilegedActionException.getException());
        }
        throw ((UnsupportedCallbackException)localPrivilegedActionException.getException());
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\security\auth\login\LoginContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */