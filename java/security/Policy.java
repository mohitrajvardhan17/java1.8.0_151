package java.security;

import java.util.Enumeration;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicReference;
import sun.security.jca.GetInstance;
import sun.security.jca.GetInstance.Instance;
import sun.security.provider.PolicyFile;
import sun.security.util.Debug;
import sun.security.util.SecurityConstants;

public abstract class Policy
{
  public static final PermissionCollection UNSUPPORTED_EMPTY_COLLECTION = new UnsupportedEmptyCollection();
  private static AtomicReference<PolicyInfo> policy = new AtomicReference(new PolicyInfo(null, false));
  private static final Debug debug = Debug.getInstance("policy");
  private WeakHashMap<ProtectionDomain.Key, PermissionCollection> pdMapping;
  
  public Policy() {}
  
  static boolean isSet()
  {
    PolicyInfo localPolicyInfo = (PolicyInfo)policy.get();
    return (policy != null) && (initialized == true);
  }
  
  private static void checkPermission(String paramString)
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPermission(new SecurityPermission("createPolicy." + paramString));
    }
  }
  
  public static Policy getPolicy()
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPermission(SecurityConstants.GET_POLICY_PERMISSION);
    }
    return getPolicyNoCheck();
  }
  
  static Policy getPolicyNoCheck()
  {
    PolicyInfo localPolicyInfo1 = (PolicyInfo)policy.get();
    if ((!initialized) || (policy == null)) {
      synchronized (Policy.class)
      {
        PolicyInfo localPolicyInfo2 = (PolicyInfo)policy.get();
        if (policy == null)
        {
          String str1 = (String)AccessController.doPrivileged(new PrivilegedAction()
          {
            public String run()
            {
              return Security.getProperty("policy.provider");
            }
          });
          if (str1 == null) {
            str1 = "sun.security.provider.PolicyFile";
          }
          try
          {
            localPolicyInfo2 = new PolicyInfo((Policy)Class.forName(str1).newInstance(), true);
          }
          catch (Exception localException)
          {
            PolicyFile localPolicyFile = new PolicyFile();
            localPolicyInfo2 = new PolicyInfo(localPolicyFile, false);
            policy.set(localPolicyInfo2);
            String str2 = str1;
            Policy localPolicy = (Policy)AccessController.doPrivileged(new PrivilegedAction()
            {
              public Policy run()
              {
                try
                {
                  ClassLoader localClassLoader1 = ClassLoader.getSystemClassLoader();
                  ClassLoader localClassLoader2 = null;
                  while (localClassLoader1 != null)
                  {
                    localClassLoader2 = localClassLoader1;
                    localClassLoader1 = localClassLoader1.getParent();
                  }
                  return localClassLoader2 != null ? (Policy)Class.forName(val$pc, true, localClassLoader2).newInstance() : null;
                }
                catch (Exception localException)
                {
                  if (Policy.debug != null)
                  {
                    Policy.debug.println("policy provider " + val$pc + " not available");
                    localException.printStackTrace();
                  }
                }
                return null;
              }
            });
            if (localPolicy != null)
            {
              localPolicyInfo2 = new PolicyInfo(localPolicy, true);
            }
            else
            {
              if (debug != null) {
                debug.println("using sun.security.provider.PolicyFile");
              }
              localPolicyInfo2 = new PolicyInfo(localPolicyFile, true);
            }
          }
          policy.set(localPolicyInfo2);
        }
        return policy;
      }
    }
    return policy;
  }
  
  public static void setPolicy(Policy paramPolicy)
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPermission(new SecurityPermission("setPolicy"));
    }
    if (paramPolicy != null) {
      initPolicy(paramPolicy);
    }
    synchronized (Policy.class)
    {
      policy.set(new PolicyInfo(paramPolicy, paramPolicy != null));
    }
  }
  
  private static void initPolicy(Policy paramPolicy)
  {
    ProtectionDomain localProtectionDomain = (ProtectionDomain)AccessController.doPrivileged(new PrivilegedAction()
    {
      public ProtectionDomain run()
      {
        return val$p.getClass().getProtectionDomain();
      }
    });
    Object localObject1 = null;
    synchronized (paramPolicy)
    {
      if (pdMapping == null) {
        pdMapping = new WeakHashMap();
      }
    }
    if (localProtectionDomain.getCodeSource() != null)
    {
      ??? = policygetpolicy;
      if (??? != null) {
        localObject1 = ((Policy)???).getPermissions(localProtectionDomain);
      }
      if (localObject1 == null)
      {
        localObject1 = new Permissions();
        ((PermissionCollection)localObject1).add(SecurityConstants.ALL_PERMISSION);
      }
      synchronized (pdMapping)
      {
        pdMapping.put(key, localObject1);
      }
    }
  }
  
  public static Policy getInstance(String paramString, Parameters paramParameters)
    throws NoSuchAlgorithmException
  {
    checkPermission(paramString);
    try
    {
      GetInstance.Instance localInstance = GetInstance.getInstance("Policy", PolicySpi.class, paramString, paramParameters);
      return new PolicyDelegate((PolicySpi)impl, provider, paramString, paramParameters, null);
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
      return handleException(localNoSuchAlgorithmException);
    }
  }
  
  public static Policy getInstance(String paramString1, Parameters paramParameters, String paramString2)
    throws NoSuchProviderException, NoSuchAlgorithmException
  {
    if ((paramString2 == null) || (paramString2.length() == 0)) {
      throw new IllegalArgumentException("missing provider");
    }
    checkPermission(paramString1);
    try
    {
      GetInstance.Instance localInstance = GetInstance.getInstance("Policy", PolicySpi.class, paramString1, paramParameters, paramString2);
      return new PolicyDelegate((PolicySpi)impl, provider, paramString1, paramParameters, null);
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
      return handleException(localNoSuchAlgorithmException);
    }
  }
  
  public static Policy getInstance(String paramString, Parameters paramParameters, Provider paramProvider)
    throws NoSuchAlgorithmException
  {
    if (paramProvider == null) {
      throw new IllegalArgumentException("missing provider");
    }
    checkPermission(paramString);
    try
    {
      GetInstance.Instance localInstance = GetInstance.getInstance("Policy", PolicySpi.class, paramString, paramParameters, paramProvider);
      return new PolicyDelegate((PolicySpi)impl, provider, paramString, paramParameters, null);
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
      return handleException(localNoSuchAlgorithmException);
    }
  }
  
  private static Policy handleException(NoSuchAlgorithmException paramNoSuchAlgorithmException)
    throws NoSuchAlgorithmException
  {
    Throwable localThrowable = paramNoSuchAlgorithmException.getCause();
    if ((localThrowable instanceof IllegalArgumentException)) {
      throw ((IllegalArgumentException)localThrowable);
    }
    throw paramNoSuchAlgorithmException;
  }
  
  public Provider getProvider()
  {
    return null;
  }
  
  public String getType()
  {
    return null;
  }
  
  public Parameters getParameters()
  {
    return null;
  }
  
  public PermissionCollection getPermissions(CodeSource paramCodeSource)
  {
    return UNSUPPORTED_EMPTY_COLLECTION;
  }
  
  public PermissionCollection getPermissions(ProtectionDomain paramProtectionDomain)
  {
    Object localObject1 = null;
    if (paramProtectionDomain == null) {
      return new Permissions();
    }
    if (pdMapping == null) {
      initPolicy(this);
    }
    synchronized (pdMapping)
    {
      localObject1 = (PermissionCollection)pdMapping.get(key);
    }
    if (localObject1 != null)
    {
      ??? = new Permissions();
      synchronized (localObject1)
      {
        Enumeration localEnumeration = ((PermissionCollection)localObject1).elements();
        while (localEnumeration.hasMoreElements()) {
          ((Permissions)???).add((Permission)localEnumeration.nextElement());
        }
      }
      return (PermissionCollection)???;
    }
    localObject1 = getPermissions(paramProtectionDomain.getCodeSource());
    if ((localObject1 == null) || (localObject1 == UNSUPPORTED_EMPTY_COLLECTION)) {
      localObject1 = new Permissions();
    }
    addStaticPerms((PermissionCollection)localObject1, paramProtectionDomain.getPermissions());
    return (PermissionCollection)localObject1;
  }
  
  private void addStaticPerms(PermissionCollection paramPermissionCollection1, PermissionCollection paramPermissionCollection2)
  {
    if (paramPermissionCollection2 != null) {
      synchronized (paramPermissionCollection2)
      {
        Enumeration localEnumeration = paramPermissionCollection2.elements();
        while (localEnumeration.hasMoreElements()) {
          paramPermissionCollection1.add((Permission)localEnumeration.nextElement());
        }
      }
    }
  }
  
  public boolean implies(ProtectionDomain paramProtectionDomain, Permission paramPermission)
  {
    if (pdMapping == null) {
      initPolicy(this);
    }
    synchronized (pdMapping)
    {
      localPermissionCollection = (PermissionCollection)pdMapping.get(key);
    }
    if (localPermissionCollection != null) {
      return localPermissionCollection.implies(paramPermission);
    }
    PermissionCollection localPermissionCollection = getPermissions(paramProtectionDomain);
    if (localPermissionCollection == null) {
      return false;
    }
    synchronized (pdMapping)
    {
      pdMapping.put(key, localPermissionCollection);
    }
    return localPermissionCollection.implies(paramPermission);
  }
  
  public void refresh() {}
  
  public static abstract interface Parameters {}
  
  private static class PolicyDelegate
    extends Policy
  {
    private PolicySpi spi;
    private Provider p;
    private String type;
    private Policy.Parameters params;
    
    private PolicyDelegate(PolicySpi paramPolicySpi, Provider paramProvider, String paramString, Policy.Parameters paramParameters)
    {
      spi = paramPolicySpi;
      p = paramProvider;
      type = paramString;
      params = paramParameters;
    }
    
    public String getType()
    {
      return type;
    }
    
    public Policy.Parameters getParameters()
    {
      return params;
    }
    
    public Provider getProvider()
    {
      return p;
    }
    
    public PermissionCollection getPermissions(CodeSource paramCodeSource)
    {
      return spi.engineGetPermissions(paramCodeSource);
    }
    
    public PermissionCollection getPermissions(ProtectionDomain paramProtectionDomain)
    {
      return spi.engineGetPermissions(paramProtectionDomain);
    }
    
    public boolean implies(ProtectionDomain paramProtectionDomain, Permission paramPermission)
    {
      return spi.engineImplies(paramProtectionDomain, paramPermission);
    }
    
    public void refresh()
    {
      spi.engineRefresh();
    }
  }
  
  private static class PolicyInfo
  {
    final Policy policy;
    final boolean initialized;
    
    PolicyInfo(Policy paramPolicy, boolean paramBoolean)
    {
      policy = paramPolicy;
      initialized = paramBoolean;
    }
  }
  
  private static class UnsupportedEmptyCollection
    extends PermissionCollection
  {
    private static final long serialVersionUID = -8492269157353014774L;
    private Permissions perms = new Permissions();
    
    public UnsupportedEmptyCollection()
    {
      perms.setReadOnly();
    }
    
    public void add(Permission paramPermission)
    {
      perms.add(paramPermission);
    }
    
    public boolean implies(Permission paramPermission)
    {
      return perms.implies(paramPermission);
    }
    
    public Enumeration<Permission> elements()
    {
      return perms.elements();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\security\Policy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */