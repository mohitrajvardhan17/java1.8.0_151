package javax.security.auth;

import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.PrivilegedAction;
import java.security.PrivilegedExceptionAction;
import java.security.Security;
import java.util.Objects;
import sun.security.util.Debug;
import sun.security.util.ResourcesMgr;

@Deprecated
public abstract class Policy
{
  private static Policy policy;
  private static final String AUTH_POLICY = "sun.security.provider.AuthPolicyFile";
  private final AccessControlContext acc = AccessController.getContext();
  private static boolean isCustomPolicy;
  
  protected Policy() {}
  
  public static Policy getPolicy()
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPermission(new AuthPermission("getPolicy"));
    }
    return getPolicyNoCheck();
  }
  
  static Policy getPolicyNoCheck()
  {
    if (policy == null) {
      synchronized (Policy.class)
      {
        if (policy == null)
        {
          String str1 = null;
          str1 = (String)AccessController.doPrivileged(new PrivilegedAction()
          {
            public String run()
            {
              return Security.getProperty("auth.policy.provider");
            }
          });
          if (str1 == null) {
            str1 = "sun.security.provider.AuthPolicyFile";
          }
          try
          {
            final String str2 = str1;
            Policy localPolicy = (Policy)AccessController.doPrivileged(new PrivilegedExceptionAction()
            {
              public Policy run()
                throws ClassNotFoundException, InstantiationException, IllegalAccessException
              {
                Class localClass = Class.forName(val$finalClass, false, Thread.currentThread().getContextClassLoader()).asSubclass(Policy.class);
                return (Policy)localClass.newInstance();
              }
            });
            AccessController.doPrivileged(new PrivilegedExceptionAction()
            {
              public Void run()
              {
                Policy.setPolicy(val$untrustedImpl);
                Policy.access$002(!str2.equals("sun.security.provider.AuthPolicyFile"));
                return null;
              }
            }, (AccessControlContext)Objects.requireNonNull(acc));
          }
          catch (Exception localException)
          {
            throw new SecurityException(ResourcesMgr.getString("unable.to.instantiate.Subject.based.policy"));
          }
        }
      }
    }
    return policy;
  }
  
  public static void setPolicy(Policy paramPolicy)
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPermission(new AuthPermission("setPolicy"));
    }
    policy = paramPolicy;
    isCustomPolicy = paramPolicy != null;
  }
  
  static boolean isCustomPolicySet(Debug paramDebug)
  {
    if (policy != null)
    {
      if ((paramDebug != null) && (isCustomPolicy)) {
        paramDebug.println("Providing backwards compatibility for javax.security.auth.policy implementation: " + policy.toString());
      }
      return isCustomPolicy;
    }
    String str = (String)AccessController.doPrivileged(new PrivilegedAction()
    {
      public String run()
      {
        return Security.getProperty("auth.policy.provider");
      }
    });
    if ((str != null) && (!str.equals("sun.security.provider.AuthPolicyFile")))
    {
      if (paramDebug != null) {
        paramDebug.println("Providing backwards compatibility for javax.security.auth.policy implementation: " + str);
      }
      return true;
    }
    return false;
  }
  
  public abstract PermissionCollection getPermissions(Subject paramSubject, CodeSource paramCodeSource);
  
  public abstract void refresh();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\security\auth\Policy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */