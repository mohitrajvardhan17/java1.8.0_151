package java.security;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import sun.security.util.Debug;
import sun.security.util.SecurityConstants;

public final class AccessControlContext
{
  private ProtectionDomain[] context;
  private boolean isPrivileged;
  private boolean isAuthorized = false;
  private AccessControlContext privilegedContext;
  private DomainCombiner combiner = null;
  private Permission[] permissions;
  private AccessControlContext parent;
  private boolean isWrapped;
  private boolean isLimited;
  private ProtectionDomain[] limitedContext;
  private static boolean debugInit = false;
  private static Debug debug = null;
  
  static Debug getDebug()
  {
    if (debugInit) {
      return debug;
    }
    if (Policy.isSet())
    {
      debug = Debug.getInstance("access");
      debugInit = true;
    }
    return debug;
  }
  
  public AccessControlContext(ProtectionDomain[] paramArrayOfProtectionDomain)
  {
    if (paramArrayOfProtectionDomain.length == 0)
    {
      context = null;
    }
    else if (paramArrayOfProtectionDomain.length == 1)
    {
      if (paramArrayOfProtectionDomain[0] != null) {
        context = ((ProtectionDomain[])paramArrayOfProtectionDomain.clone());
      } else {
        context = null;
      }
    }
    else
    {
      ArrayList localArrayList = new ArrayList(paramArrayOfProtectionDomain.length);
      for (int i = 0; i < paramArrayOfProtectionDomain.length; i++) {
        if ((paramArrayOfProtectionDomain[i] != null) && (!localArrayList.contains(paramArrayOfProtectionDomain[i]))) {
          localArrayList.add(paramArrayOfProtectionDomain[i]);
        }
      }
      if (!localArrayList.isEmpty())
      {
        context = new ProtectionDomain[localArrayList.size()];
        context = ((ProtectionDomain[])localArrayList.toArray(context));
      }
    }
  }
  
  public AccessControlContext(AccessControlContext paramAccessControlContext, DomainCombiner paramDomainCombiner)
  {
    this(paramAccessControlContext, paramDomainCombiner, false);
  }
  
  AccessControlContext(AccessControlContext paramAccessControlContext, DomainCombiner paramDomainCombiner, boolean paramBoolean)
  {
    if (!paramBoolean)
    {
      SecurityManager localSecurityManager = System.getSecurityManager();
      if (localSecurityManager != null)
      {
        localSecurityManager.checkPermission(SecurityConstants.CREATE_ACC_PERMISSION);
        isAuthorized = true;
      }
    }
    else
    {
      isAuthorized = true;
    }
    context = context;
    combiner = paramDomainCombiner;
  }
  
  AccessControlContext(ProtectionDomain paramProtectionDomain, DomainCombiner paramDomainCombiner, AccessControlContext paramAccessControlContext1, AccessControlContext paramAccessControlContext2, Permission[] paramArrayOfPermission)
  {
    ProtectionDomain[] arrayOfProtectionDomain = null;
    if (paramProtectionDomain != null) {
      arrayOfProtectionDomain = new ProtectionDomain[] { paramProtectionDomain };
    }
    if (paramAccessControlContext2 != null)
    {
      if (paramDomainCombiner != null) {
        context = paramDomainCombiner.combine(arrayOfProtectionDomain, context);
      } else {
        context = combine(arrayOfProtectionDomain, context);
      }
    }
    else if (paramDomainCombiner != null) {
      context = paramDomainCombiner.combine(arrayOfProtectionDomain, null);
    } else {
      context = combine(arrayOfProtectionDomain, null);
    }
    combiner = paramDomainCombiner;
    Permission[] arrayOfPermission = null;
    if (paramArrayOfPermission != null)
    {
      arrayOfPermission = new Permission[paramArrayOfPermission.length];
      for (int i = 0; i < paramArrayOfPermission.length; i++)
      {
        if (paramArrayOfPermission[i] == null) {
          throw new NullPointerException("permission can't be null");
        }
        if (paramArrayOfPermission[i].getClass() == AllPermission.class) {
          paramAccessControlContext1 = null;
        }
        arrayOfPermission[i] = paramArrayOfPermission[i];
      }
    }
    if (paramAccessControlContext1 != null)
    {
      limitedContext = combine(context, limitedContext);
      isLimited = true;
      isWrapped = true;
      permissions = arrayOfPermission;
      parent = paramAccessControlContext1;
      privilegedContext = paramAccessControlContext2;
    }
    isAuthorized = true;
  }
  
  AccessControlContext(ProtectionDomain[] paramArrayOfProtectionDomain, boolean paramBoolean)
  {
    context = paramArrayOfProtectionDomain;
    isPrivileged = paramBoolean;
    isAuthorized = true;
  }
  
  AccessControlContext(ProtectionDomain[] paramArrayOfProtectionDomain, AccessControlContext paramAccessControlContext)
  {
    context = paramArrayOfProtectionDomain;
    privilegedContext = paramAccessControlContext;
    isPrivileged = true;
  }
  
  ProtectionDomain[] getContext()
  {
    return context;
  }
  
  boolean isPrivileged()
  {
    return isPrivileged;
  }
  
  DomainCombiner getAssignedCombiner()
  {
    AccessControlContext localAccessControlContext;
    if (isPrivileged) {
      localAccessControlContext = privilegedContext;
    } else {
      localAccessControlContext = AccessController.getInheritedAccessControlContext();
    }
    if (localAccessControlContext != null) {
      return combiner;
    }
    return null;
  }
  
  public DomainCombiner getDomainCombiner()
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPermission(SecurityConstants.GET_COMBINER_PERMISSION);
    }
    return getCombiner();
  }
  
  DomainCombiner getCombiner()
  {
    return combiner;
  }
  
  boolean isAuthorized()
  {
    return isAuthorized;
  }
  
  public void checkPermission(Permission paramPermission)
    throws AccessControlException
  {
    int i = 0;
    if (paramPermission == null) {
      throw new NullPointerException("permission can't be null");
    }
    if (getDebug() != null)
    {
      i = !Debug.isOn("codebase=") ? 1 : 0;
      if (i == 0) {
        for (j = 0; (context != null) && (j < context.length); j++) {
          if ((context[j].getCodeSource() != null) && (context[j].getCodeSource().getLocation() != null) && (Debug.isOn("codebase=" + context[j].getCodeSource().getLocation().toString())))
          {
            i = 1;
            break;
          }
        }
      }
      i &= ((!Debug.isOn("permission=")) || (Debug.isOn("permission=" + paramPermission.getClass().getCanonicalName())) ? 1 : 0);
      if ((i != 0) && (Debug.isOn("stack"))) {
        Thread.dumpStack();
      }
      if ((i != 0) && (Debug.isOn("domain"))) {
        if (context == null) {
          debug.println("domain (context is null)");
        } else {
          for (j = 0; j < context.length; j++) {
            debug.println("domain " + j + " " + context[j]);
          }
        }
      }
    }
    if (context == null)
    {
      checkPermission2(paramPermission);
      return;
    }
    for (int j = 0; j < context.length; j++) {
      if ((context[j] != null) && (!context[j].implies(paramPermission)))
      {
        if (i != 0) {
          debug.println("access denied " + paramPermission);
        }
        if ((Debug.isOn("failure")) && (debug != null))
        {
          if (i == 0) {
            debug.println("access denied " + paramPermission);
          }
          Thread.dumpStack();
          final ProtectionDomain localProtectionDomain = context[j];
          final Debug localDebug = debug;
          AccessController.doPrivileged(new PrivilegedAction()
          {
            public Void run()
            {
              localDebug.println("domain that failed " + localProtectionDomain);
              return null;
            }
          });
        }
        throw new AccessControlException("access denied " + paramPermission, paramPermission);
      }
    }
    if (i != 0) {
      debug.println("access allowed " + paramPermission);
    }
    checkPermission2(paramPermission);
  }
  
  private void checkPermission2(Permission paramPermission)
  {
    if (!isLimited) {
      return;
    }
    if (privilegedContext != null) {
      privilegedContext.checkPermission2(paramPermission);
    }
    if (isWrapped) {
      return;
    }
    if (permissions != null)
    {
      Class localClass = paramPermission.getClass();
      for (int i = 0; i < permissions.length; i++)
      {
        Permission localPermission = permissions[i];
        if ((localPermission.getClass().equals(localClass)) && (localPermission.implies(paramPermission))) {
          return;
        }
      }
    }
    if (parent != null) {
      if (permissions == null) {
        parent.checkPermission2(paramPermission);
      } else {
        parent.checkPermission(paramPermission);
      }
    }
  }
  
  AccessControlContext optimize()
  {
    DomainCombiner localDomainCombiner = null;
    Object localObject = null;
    Permission[] arrayOfPermission = null;
    AccessControlContext localAccessControlContext;
    if (isPrivileged)
    {
      localAccessControlContext = privilegedContext;
      if ((localAccessControlContext != null) && (isWrapped))
      {
        arrayOfPermission = permissions;
        localObject = parent;
      }
    }
    else
    {
      localAccessControlContext = AccessController.getInheritedAccessControlContext();
      if ((localAccessControlContext != null) && (isLimited)) {
        localObject = localAccessControlContext;
      }
    }
    int i = context == null ? 1 : 0;
    int j = (localAccessControlContext == null) || (context == null) ? 1 : 0;
    ProtectionDomain[] arrayOfProtectionDomain1 = j != 0 ? null : context;
    int k = ((localAccessControlContext == null) || (!isWrapped)) && (localObject == null) ? 1 : 0;
    ProtectionDomain[] arrayOfProtectionDomain2;
    if ((localAccessControlContext != null) && (combiner != null))
    {
      if (getDebug() != null) {
        debug.println("AccessControlContext invoking the Combiner");
      }
      localDomainCombiner = combiner;
      arrayOfProtectionDomain2 = localDomainCombiner.combine(context, arrayOfProtectionDomain1);
    }
    else
    {
      if (i != 0)
      {
        if (j != 0)
        {
          calculateFields(localAccessControlContext, (AccessControlContext)localObject, arrayOfPermission);
          return this;
        }
        if (k != 0) {
          return localAccessControlContext;
        }
      }
      else if ((arrayOfProtectionDomain1 != null) && (k != 0) && (context.length == 1) && (context[0] == arrayOfProtectionDomain1[0]))
      {
        return localAccessControlContext;
      }
      arrayOfProtectionDomain2 = combine(context, arrayOfProtectionDomain1);
      if ((k != 0) && (j == 0) && (arrayOfProtectionDomain2 == arrayOfProtectionDomain1)) {
        return localAccessControlContext;
      }
      if ((j != 0) && (arrayOfProtectionDomain2 == context))
      {
        calculateFields(localAccessControlContext, (AccessControlContext)localObject, arrayOfPermission);
        return this;
      }
    }
    context = arrayOfProtectionDomain2;
    combiner = localDomainCombiner;
    isPrivileged = false;
    calculateFields(localAccessControlContext, (AccessControlContext)localObject, arrayOfPermission);
    return this;
  }
  
  private static ProtectionDomain[] combine(ProtectionDomain[] paramArrayOfProtectionDomain1, ProtectionDomain[] paramArrayOfProtectionDomain2)
  {
    int i = paramArrayOfProtectionDomain1 == null ? 1 : 0;
    int j = paramArrayOfProtectionDomain2 == null ? 1 : 0;
    int k = i != 0 ? 0 : paramArrayOfProtectionDomain1.length;
    if ((j != 0) && (k <= 2)) {
      return paramArrayOfProtectionDomain1;
    }
    int m = j != 0 ? 0 : paramArrayOfProtectionDomain2.length;
    Object localObject = new ProtectionDomain[k + m];
    if (j == 0) {
      System.arraycopy(paramArrayOfProtectionDomain2, 0, localObject, 0, m);
    }
    label140:
    for (int n = 0; n < k; n++)
    {
      ProtectionDomain localProtectionDomain = paramArrayOfProtectionDomain1[n];
      if (localProtectionDomain != null)
      {
        for (int i1 = 0; i1 < m; i1++) {
          if (localProtectionDomain == localObject[i1]) {
            break label140;
          }
        }
        localObject[(m++)] = localProtectionDomain;
      }
    }
    if (m != localObject.length)
    {
      if ((j == 0) && (m == paramArrayOfProtectionDomain2.length)) {
        return paramArrayOfProtectionDomain2;
      }
      if ((j != 0) && (m == k)) {
        return paramArrayOfProtectionDomain1;
      }
      ProtectionDomain[] arrayOfProtectionDomain = new ProtectionDomain[m];
      System.arraycopy(localObject, 0, arrayOfProtectionDomain, 0, m);
      localObject = arrayOfProtectionDomain;
    }
    return (ProtectionDomain[])localObject;
  }
  
  private void calculateFields(AccessControlContext paramAccessControlContext1, AccessControlContext paramAccessControlContext2, Permission[] paramArrayOfPermission)
  {
    ProtectionDomain[] arrayOfProtectionDomain1 = null;
    ProtectionDomain[] arrayOfProtectionDomain2 = null;
    arrayOfProtectionDomain1 = paramAccessControlContext2 != null ? limitedContext : null;
    arrayOfProtectionDomain2 = paramAccessControlContext1 != null ? limitedContext : null;
    ProtectionDomain[] arrayOfProtectionDomain3 = combine(arrayOfProtectionDomain1, arrayOfProtectionDomain2);
    if ((arrayOfProtectionDomain3 != null) && ((context == null) || (!containsAllPDs(arrayOfProtectionDomain3, context))))
    {
      limitedContext = arrayOfProtectionDomain3;
      permissions = paramArrayOfPermission;
      parent = paramAccessControlContext2;
      isLimited = true;
    }
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if (!(paramObject instanceof AccessControlContext)) {
      return false;
    }
    AccessControlContext localAccessControlContext = (AccessControlContext)paramObject;
    if (!equalContext(localAccessControlContext)) {
      return false;
    }
    return equalLimitedContext(localAccessControlContext);
  }
  
  private boolean equalContext(AccessControlContext paramAccessControlContext)
  {
    if (!equalPDs(context, context)) {
      return false;
    }
    if ((combiner == null) && (combiner != null)) {
      return false;
    }
    return (combiner == null) || (combiner.equals(combiner));
  }
  
  private boolean equalPDs(ProtectionDomain[] paramArrayOfProtectionDomain1, ProtectionDomain[] paramArrayOfProtectionDomain2)
  {
    if (paramArrayOfProtectionDomain1 == null) {
      return paramArrayOfProtectionDomain2 == null;
    }
    if (paramArrayOfProtectionDomain2 == null) {
      return false;
    }
    return (containsAllPDs(paramArrayOfProtectionDomain1, paramArrayOfProtectionDomain2)) && (containsAllPDs(paramArrayOfProtectionDomain2, paramArrayOfProtectionDomain1));
  }
  
  private boolean equalLimitedContext(AccessControlContext paramAccessControlContext)
  {
    if (paramAccessControlContext == null) {
      return false;
    }
    if ((!isLimited) && (!isLimited)) {
      return true;
    }
    if ((!isLimited) || (!isLimited)) {
      return false;
    }
    if (((isWrapped) && (!isWrapped)) || ((!isWrapped) && (isWrapped))) {
      return false;
    }
    if ((permissions == null) && (permissions != null)) {
      return false;
    }
    if ((permissions != null) && (permissions == null)) {
      return false;
    }
    if ((!containsAllLimits(paramAccessControlContext)) || (!paramAccessControlContext.containsAllLimits(this))) {
      return false;
    }
    AccessControlContext localAccessControlContext1 = getNextPC(this);
    AccessControlContext localAccessControlContext2 = getNextPC(paramAccessControlContext);
    if ((localAccessControlContext1 == null) && (localAccessControlContext2 != null) && (isLimited)) {
      return false;
    }
    if ((localAccessControlContext1 != null) && (!localAccessControlContext1.equalLimitedContext(localAccessControlContext2))) {
      return false;
    }
    if ((parent == null) && (parent != null)) {
      return false;
    }
    return (parent == null) || (parent.equals(parent));
  }
  
  private static AccessControlContext getNextPC(AccessControlContext paramAccessControlContext)
  {
    while ((paramAccessControlContext != null) && (privilegedContext != null))
    {
      paramAccessControlContext = privilegedContext;
      if (!isWrapped) {
        return paramAccessControlContext;
      }
    }
    return null;
  }
  
  private static boolean containsAllPDs(ProtectionDomain[] paramArrayOfProtectionDomain1, ProtectionDomain[] paramArrayOfProtectionDomain2)
  {
    boolean bool = false;
    for (int i = 0; i < paramArrayOfProtectionDomain1.length; i++)
    {
      bool = false;
      ProtectionDomain localProtectionDomain1;
      if ((localProtectionDomain1 = paramArrayOfProtectionDomain1[i]) == null)
      {
        for (int j = 0; (j < paramArrayOfProtectionDomain2.length) && (!bool); j++) {
          bool = paramArrayOfProtectionDomain2[j] == null;
        }
      }
      else
      {
        Class localClass = localProtectionDomain1.getClass();
        for (int k = 0; (k < paramArrayOfProtectionDomain2.length) && (!bool); k++)
        {
          ProtectionDomain localProtectionDomain2 = paramArrayOfProtectionDomain2[k];
          bool = (localProtectionDomain2 != null) && (localClass == localProtectionDomain2.getClass()) && (localProtectionDomain1.equals(localProtectionDomain2));
        }
      }
      if (!bool) {
        return false;
      }
    }
    return bool;
  }
  
  private boolean containsAllLimits(AccessControlContext paramAccessControlContext)
  {
    boolean bool = false;
    if ((permissions == null) && (permissions == null)) {
      return true;
    }
    for (int i = 0; i < permissions.length; i++)
    {
      Permission localPermission1 = permissions[i];
      Class localClass = localPermission1.getClass();
      bool = false;
      for (int j = 0; (j < permissions.length) && (!bool); j++)
      {
        Permission localPermission2 = permissions[j];
        bool = (localClass.equals(localPermission2.getClass())) && (localPermission1.equals(localPermission2));
      }
      if (!bool) {
        return false;
      }
    }
    return bool;
  }
  
  public int hashCode()
  {
    int i = 0;
    if (context == null) {
      return i;
    }
    for (int j = 0; j < context.length; j++) {
      if (context[j] != null) {
        i ^= context[j].hashCode();
      }
    }
    return i;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\AccessControlContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */