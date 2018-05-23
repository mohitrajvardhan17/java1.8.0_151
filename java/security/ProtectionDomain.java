package java.security;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import sun.misc.JavaSecurityAccess;
import sun.misc.JavaSecurityProtectionDomainAccess;
import sun.misc.JavaSecurityProtectionDomainAccess.ProtectionDomainCache;
import sun.misc.SharedSecrets;
import sun.security.util.Debug;
import sun.security.util.SecurityConstants;

public class ProtectionDomain
{
  private CodeSource codesource;
  private ClassLoader classloader;
  private Principal[] principals;
  private PermissionCollection permissions;
  private boolean hasAllPerm = false;
  private boolean staticPermissions;
  final Key key = new Key();
  private static final Debug debug;
  
  public ProtectionDomain(CodeSource paramCodeSource, PermissionCollection paramPermissionCollection)
  {
    codesource = paramCodeSource;
    if (paramPermissionCollection != null)
    {
      permissions = paramPermissionCollection;
      permissions.setReadOnly();
      if (((paramPermissionCollection instanceof Permissions)) && (allPermission != null)) {
        hasAllPerm = true;
      }
    }
    classloader = null;
    principals = new Principal[0];
    staticPermissions = true;
  }
  
  public ProtectionDomain(CodeSource paramCodeSource, PermissionCollection paramPermissionCollection, ClassLoader paramClassLoader, Principal[] paramArrayOfPrincipal)
  {
    codesource = paramCodeSource;
    if (paramPermissionCollection != null)
    {
      permissions = paramPermissionCollection;
      permissions.setReadOnly();
      if (((paramPermissionCollection instanceof Permissions)) && (allPermission != null)) {
        hasAllPerm = true;
      }
    }
    classloader = paramClassLoader;
    principals = (paramArrayOfPrincipal != null ? (Principal[])paramArrayOfPrincipal.clone() : new Principal[0]);
    staticPermissions = false;
  }
  
  public final CodeSource getCodeSource()
  {
    return codesource;
  }
  
  public final ClassLoader getClassLoader()
  {
    return classloader;
  }
  
  public final Principal[] getPrincipals()
  {
    return (Principal[])principals.clone();
  }
  
  public final PermissionCollection getPermissions()
  {
    return permissions;
  }
  
  public boolean implies(Permission paramPermission)
  {
    if (hasAllPerm) {
      return true;
    }
    if ((!staticPermissions) && (Policy.getPolicyNoCheck().implies(this, paramPermission))) {
      return true;
    }
    if (permissions != null) {
      return permissions.implies(paramPermission);
    }
    return false;
  }
  
  boolean impliesCreateAccessControlContext()
  {
    return implies(SecurityConstants.CREATE_ACC_PERMISSION);
  }
  
  public String toString()
  {
    String str = "<no principals>";
    if ((principals != null) && (principals.length > 0))
    {
      localObject = new StringBuilder("(principals ");
      for (int i = 0; i < principals.length; i++)
      {
        ((StringBuilder)localObject).append(principals[i].getClass().getName() + " \"" + principals[i].getName() + "\"");
        if (i < principals.length - 1) {
          ((StringBuilder)localObject).append(",\n");
        } else {
          ((StringBuilder)localObject).append(")\n");
        }
      }
      str = ((StringBuilder)localObject).toString();
    }
    Object localObject = (Policy.isSet()) && (seeAllp()) ? mergePermissions() : getPermissions();
    return "ProtectionDomain  " + codesource + "\n " + classloader + "\n " + str + "\n " + localObject + "\n";
  }
  
  private static boolean seeAllp()
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager == null) {
      return true;
    }
    if (debug != null)
    {
      if ((localSecurityManager.getClass().getClassLoader() == null) && (Policy.getPolicyNoCheck().getClass().getClassLoader() == null)) {
        return true;
      }
    }
    else {
      try
      {
        localSecurityManager.checkPermission(SecurityConstants.GET_POLICY_PERMISSION);
        return true;
      }
      catch (SecurityException localSecurityException) {}
    }
    return false;
  }
  
  private PermissionCollection mergePermissions()
  {
    if (staticPermissions) {
      return permissions;
    }
    PermissionCollection localPermissionCollection = (PermissionCollection)AccessController.doPrivileged(new PrivilegedAction()
    {
      public PermissionCollection run()
      {
        Policy localPolicy = Policy.getPolicyNoCheck();
        return localPolicy.getPermissions(ProtectionDomain.this);
      }
    });
    Permissions localPermissions = new Permissions();
    int i = 32;
    int j = 8;
    ArrayList localArrayList1 = new ArrayList(j);
    ArrayList localArrayList2 = new ArrayList(i);
    Enumeration localEnumeration;
    if (permissions != null) {
      synchronized (permissions)
      {
        localEnumeration = permissions.elements();
        while (localEnumeration.hasMoreElements()) {
          localArrayList1.add(localEnumeration.nextElement());
        }
      }
    }
    if (localPermissionCollection != null) {
      synchronized (localPermissionCollection)
      {
        localEnumeration = localPermissionCollection.elements();
        while (localEnumeration.hasMoreElements())
        {
          localArrayList2.add(localEnumeration.nextElement());
          j++;
        }
      }
    }
    if ((localPermissionCollection != null) && (permissions != null)) {
      synchronized (permissions)
      {
        localEnumeration = permissions.elements();
        while (localEnumeration.hasMoreElements())
        {
          Permission localPermission1 = (Permission)localEnumeration.nextElement();
          Class localClass = localPermission1.getClass();
          String str1 = localPermission1.getActions();
          String str2 = localPermission1.getName();
          for (int m = 0; m < localArrayList2.size(); m++)
          {
            Permission localPermission2 = (Permission)localArrayList2.get(m);
            if ((localClass.isInstance(localPermission2)) && (str2.equals(localPermission2.getName())) && (str1.equals(localPermission2.getActions())))
            {
              localArrayList2.remove(m);
              break;
            }
          }
        }
      }
    }
    int k;
    if (localPermissionCollection != null) {
      for (k = localArrayList2.size() - 1; k >= 0; k--) {
        localPermissions.add((Permission)localArrayList2.get(k));
      }
    }
    if (permissions != null) {
      for (k = localArrayList1.size() - 1; k >= 0; k--) {
        localPermissions.add((Permission)localArrayList1.get(k));
      }
    }
    return localPermissions;
  }
  
  static
  {
    SharedSecrets.setJavaSecurityAccess(new JavaSecurityAccessImpl(null));
    debug = Debug.getInstance("domain");
    SharedSecrets.setJavaSecurityProtectionDomainAccess(new JavaSecurityProtectionDomainAccess()
    {
      public JavaSecurityProtectionDomainAccess.ProtectionDomainCache getProtectionDomainCache()
      {
        return new ProtectionDomain.PDCache(null);
      }
      
      public boolean getStaticPermissionsField(ProtectionDomain paramAnonymousProtectionDomain)
      {
        return staticPermissions;
      }
    });
  }
  
  private static class JavaSecurityAccessImpl
    implements JavaSecurityAccess
  {
    private JavaSecurityAccessImpl() {}
    
    public <T> T doIntersectionPrivilege(PrivilegedAction<T> paramPrivilegedAction, AccessControlContext paramAccessControlContext1, AccessControlContext paramAccessControlContext2)
    {
      if (paramPrivilegedAction == null) {
        throw new NullPointerException();
      }
      return (T)AccessController.doPrivileged(paramPrivilegedAction, getCombinedACC(paramAccessControlContext2, paramAccessControlContext1));
    }
    
    public <T> T doIntersectionPrivilege(PrivilegedAction<T> paramPrivilegedAction, AccessControlContext paramAccessControlContext)
    {
      return (T)doIntersectionPrivilege(paramPrivilegedAction, AccessController.getContext(), paramAccessControlContext);
    }
    
    private static AccessControlContext getCombinedACC(AccessControlContext paramAccessControlContext1, AccessControlContext paramAccessControlContext2)
    {
      AccessControlContext localAccessControlContext = new AccessControlContext(paramAccessControlContext1, paramAccessControlContext2.getCombiner(), true);
      return new AccessControlContext(paramAccessControlContext2.getContext(), localAccessControlContext).optimize();
    }
  }
  
  static final class Key
  {
    Key() {}
  }
  
  private static class PDCache
    implements JavaSecurityProtectionDomainAccess.ProtectionDomainCache
  {
    private final ConcurrentHashMap<ProtectionDomain.WeakProtectionDomainKey, SoftReference<PermissionCollection>> pdMap = new ConcurrentHashMap();
    private final ReferenceQueue<ProtectionDomain.Key> queue = new ReferenceQueue();
    
    private PDCache() {}
    
    public void put(ProtectionDomain paramProtectionDomain, PermissionCollection paramPermissionCollection)
    {
      processQueue(queue, pdMap);
      ProtectionDomain.WeakProtectionDomainKey localWeakProtectionDomainKey = new ProtectionDomain.WeakProtectionDomainKey(paramProtectionDomain, queue);
      pdMap.put(localWeakProtectionDomainKey, new SoftReference(paramPermissionCollection));
    }
    
    public PermissionCollection get(ProtectionDomain paramProtectionDomain)
    {
      processQueue(queue, pdMap);
      ProtectionDomain.WeakProtectionDomainKey localWeakProtectionDomainKey = new ProtectionDomain.WeakProtectionDomainKey(paramProtectionDomain);
      SoftReference localSoftReference = (SoftReference)pdMap.get(localWeakProtectionDomainKey);
      return localSoftReference == null ? null : (PermissionCollection)localSoftReference.get();
    }
    
    private static void processQueue(ReferenceQueue<ProtectionDomain.Key> paramReferenceQueue, ConcurrentHashMap<? extends WeakReference<ProtectionDomain.Key>, ?> paramConcurrentHashMap)
    {
      Reference localReference;
      while ((localReference = paramReferenceQueue.poll()) != null) {
        paramConcurrentHashMap.remove(localReference);
      }
    }
  }
  
  private static class WeakProtectionDomainKey
    extends WeakReference<ProtectionDomain.Key>
  {
    private final int hash;
    private static final ProtectionDomain.Key NULL_KEY = new ProtectionDomain.Key();
    
    WeakProtectionDomainKey(ProtectionDomain paramProtectionDomain, ReferenceQueue<ProtectionDomain.Key> paramReferenceQueue)
    {
      this(paramProtectionDomain == null ? NULL_KEY : key, paramReferenceQueue);
    }
    
    WeakProtectionDomainKey(ProtectionDomain paramProtectionDomain)
    {
      this(paramProtectionDomain == null ? NULL_KEY : key);
    }
    
    private WeakProtectionDomainKey(ProtectionDomain.Key paramKey, ReferenceQueue<ProtectionDomain.Key> paramReferenceQueue)
    {
      super(paramReferenceQueue);
      hash = paramKey.hashCode();
    }
    
    private WeakProtectionDomainKey(ProtectionDomain.Key paramKey)
    {
      super();
      hash = paramKey.hashCode();
    }
    
    public int hashCode()
    {
      return hash;
    }
    
    public boolean equals(Object paramObject)
    {
      if (paramObject == this) {
        return true;
      }
      if ((paramObject instanceof WeakProtectionDomainKey))
      {
        Object localObject = get();
        return (localObject != null) && (localObject == ((WeakProtectionDomainKey)paramObject).get());
      }
      return false;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\ProtectionDomain.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */