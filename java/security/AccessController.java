package java.security;

import sun.reflect.CallerSensitive;
import sun.reflect.Reflection;
import sun.security.util.Debug;

public final class AccessController
{
  private AccessController() {}
  
  @CallerSensitive
  public static native <T> T doPrivileged(PrivilegedAction<T> paramPrivilegedAction);
  
  @CallerSensitive
  public static <T> T doPrivilegedWithCombiner(PrivilegedAction<T> paramPrivilegedAction)
  {
    AccessControlContext localAccessControlContext = getStackAccessControlContext();
    if (localAccessControlContext == null) {
      return (T)doPrivileged(paramPrivilegedAction);
    }
    DomainCombiner localDomainCombiner = localAccessControlContext.getAssignedCombiner();
    return (T)doPrivileged(paramPrivilegedAction, preserveCombiner(localDomainCombiner, Reflection.getCallerClass()));
  }
  
  @CallerSensitive
  public static native <T> T doPrivileged(PrivilegedAction<T> paramPrivilegedAction, AccessControlContext paramAccessControlContext);
  
  @CallerSensitive
  public static <T> T doPrivileged(PrivilegedAction<T> paramPrivilegedAction, AccessControlContext paramAccessControlContext, Permission... paramVarArgs)
  {
    AccessControlContext localAccessControlContext = getContext();
    if (paramVarArgs == null) {
      throw new NullPointerException("null permissions parameter");
    }
    Class localClass = Reflection.getCallerClass();
    return (T)doPrivileged(paramPrivilegedAction, createWrapper(null, localClass, localAccessControlContext, paramAccessControlContext, paramVarArgs));
  }
  
  @CallerSensitive
  public static <T> T doPrivilegedWithCombiner(PrivilegedAction<T> paramPrivilegedAction, AccessControlContext paramAccessControlContext, Permission... paramVarArgs)
  {
    AccessControlContext localAccessControlContext = getContext();
    DomainCombiner localDomainCombiner = localAccessControlContext.getCombiner();
    if ((localDomainCombiner == null) && (paramAccessControlContext != null)) {
      localDomainCombiner = paramAccessControlContext.getCombiner();
    }
    if (paramVarArgs == null) {
      throw new NullPointerException("null permissions parameter");
    }
    Class localClass = Reflection.getCallerClass();
    return (T)doPrivileged(paramPrivilegedAction, createWrapper(localDomainCombiner, localClass, localAccessControlContext, paramAccessControlContext, paramVarArgs));
  }
  
  @CallerSensitive
  public static native <T> T doPrivileged(PrivilegedExceptionAction<T> paramPrivilegedExceptionAction)
    throws PrivilegedActionException;
  
  @CallerSensitive
  public static <T> T doPrivilegedWithCombiner(PrivilegedExceptionAction<T> paramPrivilegedExceptionAction)
    throws PrivilegedActionException
  {
    AccessControlContext localAccessControlContext = getStackAccessControlContext();
    if (localAccessControlContext == null) {
      return (T)doPrivileged(paramPrivilegedExceptionAction);
    }
    DomainCombiner localDomainCombiner = localAccessControlContext.getAssignedCombiner();
    return (T)doPrivileged(paramPrivilegedExceptionAction, preserveCombiner(localDomainCombiner, Reflection.getCallerClass()));
  }
  
  private static AccessControlContext preserveCombiner(DomainCombiner paramDomainCombiner, Class<?> paramClass)
  {
    return createWrapper(paramDomainCombiner, paramClass, null, null, null);
  }
  
  private static AccessControlContext createWrapper(DomainCombiner paramDomainCombiner, Class<?> paramClass, AccessControlContext paramAccessControlContext1, AccessControlContext paramAccessControlContext2, Permission[] paramArrayOfPermission)
  {
    ProtectionDomain localProtectionDomain1 = getCallerPD(paramClass);
    if ((paramAccessControlContext2 != null) && (!paramAccessControlContext2.isAuthorized()) && (System.getSecurityManager() != null) && (!localProtectionDomain1.impliesCreateAccessControlContext()))
    {
      ProtectionDomain localProtectionDomain2 = new ProtectionDomain(null, null);
      return new AccessControlContext(new ProtectionDomain[] { localProtectionDomain2 });
    }
    return new AccessControlContext(localProtectionDomain1, paramDomainCombiner, paramAccessControlContext1, paramAccessControlContext2, paramArrayOfPermission);
  }
  
  private static ProtectionDomain getCallerPD(Class<?> paramClass)
  {
    ProtectionDomain localProtectionDomain = (ProtectionDomain)doPrivileged(new PrivilegedAction()
    {
      public ProtectionDomain run()
      {
        return val$caller.getProtectionDomain();
      }
    });
    return localProtectionDomain;
  }
  
  @CallerSensitive
  public static native <T> T doPrivileged(PrivilegedExceptionAction<T> paramPrivilegedExceptionAction, AccessControlContext paramAccessControlContext)
    throws PrivilegedActionException;
  
  @CallerSensitive
  public static <T> T doPrivileged(PrivilegedExceptionAction<T> paramPrivilegedExceptionAction, AccessControlContext paramAccessControlContext, Permission... paramVarArgs)
    throws PrivilegedActionException
  {
    AccessControlContext localAccessControlContext = getContext();
    if (paramVarArgs == null) {
      throw new NullPointerException("null permissions parameter");
    }
    Class localClass = Reflection.getCallerClass();
    return (T)doPrivileged(paramPrivilegedExceptionAction, createWrapper(null, localClass, localAccessControlContext, paramAccessControlContext, paramVarArgs));
  }
  
  @CallerSensitive
  public static <T> T doPrivilegedWithCombiner(PrivilegedExceptionAction<T> paramPrivilegedExceptionAction, AccessControlContext paramAccessControlContext, Permission... paramVarArgs)
    throws PrivilegedActionException
  {
    AccessControlContext localAccessControlContext = getContext();
    DomainCombiner localDomainCombiner = localAccessControlContext.getCombiner();
    if ((localDomainCombiner == null) && (paramAccessControlContext != null)) {
      localDomainCombiner = paramAccessControlContext.getCombiner();
    }
    if (paramVarArgs == null) {
      throw new NullPointerException("null permissions parameter");
    }
    Class localClass = Reflection.getCallerClass();
    return (T)doPrivileged(paramPrivilegedExceptionAction, createWrapper(localDomainCombiner, localClass, localAccessControlContext, paramAccessControlContext, paramVarArgs));
  }
  
  private static native AccessControlContext getStackAccessControlContext();
  
  static native AccessControlContext getInheritedAccessControlContext();
  
  public static AccessControlContext getContext()
  {
    AccessControlContext localAccessControlContext = getStackAccessControlContext();
    if (localAccessControlContext == null) {
      return new AccessControlContext(null, true);
    }
    return localAccessControlContext.optimize();
  }
  
  public static void checkPermission(Permission paramPermission)
    throws AccessControlException
  {
    if (paramPermission == null) {
      throw new NullPointerException("permission can't be null");
    }
    AccessControlContext localAccessControlContext = getStackAccessControlContext();
    if (localAccessControlContext == null)
    {
      localObject = AccessControlContext.getDebug();
      int i = 0;
      if (localObject != null)
      {
        i = !Debug.isOn("codebase=") ? 1 : 0;
        i &= ((!Debug.isOn("permission=")) || (Debug.isOn("permission=" + paramPermission.getClass().getCanonicalName())) ? 1 : 0);
      }
      if ((i != 0) && (Debug.isOn("stack"))) {
        Thread.dumpStack();
      }
      if ((i != 0) && (Debug.isOn("domain"))) {
        ((Debug)localObject).println("domain (context is null)");
      }
      if (i != 0) {
        ((Debug)localObject).println("access allowed " + paramPermission);
      }
      return;
    }
    Object localObject = localAccessControlContext.optimize();
    ((AccessControlContext)localObject).checkPermission(paramPermission);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\AccessController.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */