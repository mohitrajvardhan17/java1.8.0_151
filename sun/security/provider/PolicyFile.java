package sun.security.provider;

import java.io.File;
import java.io.FilePermission;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.NetPermission;
import java.net.SocketPermission;
import java.net.URI;
import java.net.URL;
import java.security.AccessController;
import java.security.AllPermission;
import java.security.CodeSource;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.Policy;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.security.Security;
import java.security.UnresolvedPermission;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PropertyPermission;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.concurrent.atomic.AtomicReference;
import javax.security.auth.Subject;
import javax.security.auth.x500.X500Principal;
import sun.misc.JavaSecurityProtectionDomainAccess;
import sun.misc.JavaSecurityProtectionDomainAccess.ProtectionDomainCache;
import sun.misc.SharedSecrets;
import sun.net.www.ParseUtil;
import sun.security.util.Debug;
import sun.security.util.PolicyUtil;
import sun.security.util.PropertyExpander;
import sun.security.util.ResourcesMgr;
import sun.security.util.SecurityConstants;

public class PolicyFile
  extends Policy
{
  private static final Debug debug = Debug.getInstance("policy");
  private static final String NONE = "NONE";
  private static final String P11KEYSTORE = "PKCS11";
  private static final String SELF = "${{self}}";
  private static final String X500PRINCIPAL = "javax.security.auth.x500.X500Principal";
  private static final String POLICY = "java.security.policy";
  private static final String SECURITY_MANAGER = "java.security.manager";
  private static final String POLICY_URL = "policy.url.";
  private static final String AUTH_POLICY = "java.security.auth.policy";
  private static final String AUTH_POLICY_URL = "auth.policy.url.";
  private static final int DEFAULT_CACHE_SIZE = 1;
  private AtomicReference<PolicyInfo> policyInfo = new AtomicReference();
  private boolean constructed = false;
  private boolean expandProperties = true;
  private boolean ignoreIdentityScope = true;
  private boolean allowSystemProperties = true;
  private boolean notUtf8 = false;
  private URL url;
  private static final Class[] PARAMS0 = new Class[0];
  private static final Class[] PARAMS1 = { String.class };
  private static final Class[] PARAMS2 = { String.class, String.class };
  
  public PolicyFile()
  {
    init((URL)null);
  }
  
  public PolicyFile(URL paramURL)
  {
    url = paramURL;
    init(paramURL);
  }
  
  private void init(URL paramURL)
  {
    String str = (String)AccessController.doPrivileged(new PrivilegedAction()
    {
      public String run()
      {
        expandProperties = "true".equalsIgnoreCase(Security.getProperty("policy.expandProperties"));
        ignoreIdentityScope = "true".equalsIgnoreCase(Security.getProperty("policy.ignoreIdentityScope"));
        allowSystemProperties = "true".equalsIgnoreCase(Security.getProperty("policy.allowSystemProperty"));
        notUtf8 = "false".equalsIgnoreCase(System.getProperty("sun.security.policy.utf8"));
        return System.getProperty("sun.security.policy.numcaches");
      }
    });
    int i;
    if (str != null) {
      try
      {
        i = Integer.parseInt(str);
      }
      catch (NumberFormatException localNumberFormatException)
      {
        i = 1;
      }
    } else {
      i = 1;
    }
    PolicyInfo localPolicyInfo = new PolicyInfo(i);
    initPolicyFile(localPolicyInfo, paramURL);
    policyInfo.set(localPolicyInfo);
  }
  
  private void initPolicyFile(final PolicyInfo paramPolicyInfo, final URL paramURL)
  {
    if (paramURL != null)
    {
      if (debug != null) {
        debug.println("reading " + paramURL);
      }
      AccessController.doPrivileged(new PrivilegedAction()
      {
        public Void run()
        {
          if (!PolicyFile.this.init(paramURL, paramPolicyInfo)) {
            PolicyFile.this.initStaticPolicy(paramPolicyInfo);
          }
          return null;
        }
      });
    }
    else
    {
      boolean bool = initPolicyFile("java.security.policy", "policy.url.", paramPolicyInfo);
      if (!bool) {
        initStaticPolicy(paramPolicyInfo);
      }
      initPolicyFile("java.security.auth.policy", "auth.policy.url.", paramPolicyInfo);
    }
  }
  
  private boolean initPolicyFile(final String paramString1, final String paramString2, final PolicyInfo paramPolicyInfo)
  {
    Boolean localBoolean = (Boolean)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Boolean run()
      {
        boolean bool = false;
        Object localObject;
        if (allowSystemProperties)
        {
          String str1 = System.getProperty(paramString1);
          if (str1 != null)
          {
            int j = 0;
            if (str1.startsWith("="))
            {
              j = 1;
              str1 = str1.substring(1);
            }
            try
            {
              str1 = PropertyExpander.expand(str1);
              localObject = new File(str1);
              URL localURL1;
              if (((File)localObject).exists()) {
                localURL1 = ParseUtil.fileToEncodedURL(new File(((File)localObject).getCanonicalPath()));
              } else {
                localURL1 = new URL(str1);
              }
              if (PolicyFile.debug != null) {
                PolicyFile.debug.println("reading " + localURL1);
              }
              if (PolicyFile.this.init(localURL1, paramPolicyInfo)) {
                bool = true;
              }
            }
            catch (Exception localException1)
            {
              if (PolicyFile.debug != null) {
                PolicyFile.debug.println("caught exception: " + localException1);
              }
            }
            if (j != 0)
            {
              if (PolicyFile.debug != null) {
                PolicyFile.debug.println("overriding other policies!");
              }
              return Boolean.valueOf(bool);
            }
          }
        }
        String str2;
        for (int i = 1; (str2 = Security.getProperty(paramString2 + i)) != null; i++) {
          try
          {
            URL localURL2 = null;
            localObject = PropertyExpander.expand(str2).replace(File.separatorChar, '/');
            if ((str2.startsWith("file:${java.home}/")) || (str2.startsWith("file:${user.home}/"))) {
              localURL2 = new File(((String)localObject).substring(5)).toURI().toURL();
            } else {
              localURL2 = new URI((String)localObject).toURL();
            }
            if (PolicyFile.debug != null) {
              PolicyFile.debug.println("reading " + localURL2);
            }
            if (PolicyFile.this.init(localURL2, paramPolicyInfo)) {
              bool = true;
            }
          }
          catch (Exception localException2)
          {
            if (PolicyFile.debug != null)
            {
              PolicyFile.debug.println("error reading policy " + localException2);
              localException2.printStackTrace();
            }
          }
        }
        return Boolean.valueOf(bool);
      }
    });
    return localBoolean.booleanValue();
  }
  
  private boolean init(URL paramURL, PolicyInfo paramPolicyInfo)
  {
    boolean bool = false;
    PolicyParser localPolicyParser = new PolicyParser(expandProperties);
    InputStreamReader localInputStreamReader = null;
    try
    {
      if (notUtf8) {
        localInputStreamReader = new InputStreamReader(PolicyUtil.getInputStream(paramURL));
      } else {
        localInputStreamReader = new InputStreamReader(PolicyUtil.getInputStream(paramURL), "UTF-8");
      }
      localPolicyParser.read(localInputStreamReader);
      KeyStore localKeyStore = null;
      try
      {
        localKeyStore = PolicyUtil.getKeyStore(paramURL, localPolicyParser.getKeyStoreUrl(), localPolicyParser.getKeyStoreType(), localPolicyParser.getKeyStoreProvider(), localPolicyParser.getStorePassURL(), debug);
      }
      catch (Exception localException2)
      {
        if (debug != null) {
          localException2.printStackTrace();
        }
      }
      localObject1 = localPolicyParser.grantElements();
      while (((Enumeration)localObject1).hasMoreElements())
      {
        localObject2 = (PolicyParser.GrantEntry)((Enumeration)localObject1).nextElement();
        addGrantEntry((PolicyParser.GrantEntry)localObject2, localKeyStore, paramPolicyInfo);
      }
    }
    catch (PolicyParser.ParsingException localParsingException)
    {
      Object localObject1 = new MessageFormat(ResourcesMgr.getString("java.security.policy.error.parsing.policy.message"));
      Object localObject2 = { paramURL, localParsingException.getLocalizedMessage() };
      System.err.println(((MessageFormat)localObject1).format(localObject2));
      if (debug != null) {
        localParsingException.printStackTrace();
      }
    }
    catch (Exception localException1)
    {
      if (debug != null)
      {
        debug.println("error parsing " + paramURL);
        debug.println(localException1.toString());
        localException1.printStackTrace();
      }
    }
    finally
    {
      if (localInputStreamReader != null) {
        try
        {
          localInputStreamReader.close();
          bool = true;
        }
        catch (IOException localIOException4) {}
      } else {
        bool = true;
      }
    }
    return bool;
  }
  
  private void initStaticPolicy(final PolicyInfo paramPolicyInfo)
  {
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Void run()
      {
        PolicyFile.PolicyEntry localPolicyEntry = new PolicyFile.PolicyEntry(new CodeSource(null, (Certificate[])null));
        localPolicyEntry.add(SecurityConstants.LOCAL_LISTEN_PERMISSION);
        localPolicyEntry.add(new PropertyPermission("java.version", "read"));
        localPolicyEntry.add(new PropertyPermission("java.vendor", "read"));
        localPolicyEntry.add(new PropertyPermission("java.vendor.url", "read"));
        localPolicyEntry.add(new PropertyPermission("java.class.version", "read"));
        localPolicyEntry.add(new PropertyPermission("os.name", "read"));
        localPolicyEntry.add(new PropertyPermission("os.version", "read"));
        localPolicyEntry.add(new PropertyPermission("os.arch", "read"));
        localPolicyEntry.add(new PropertyPermission("file.separator", "read"));
        localPolicyEntry.add(new PropertyPermission("path.separator", "read"));
        localPolicyEntry.add(new PropertyPermission("line.separator", "read"));
        localPolicyEntry.add(new PropertyPermission("java.specification.version", "read"));
        localPolicyEntry.add(new PropertyPermission("java.specification.vendor", "read"));
        localPolicyEntry.add(new PropertyPermission("java.specification.name", "read"));
        localPolicyEntry.add(new PropertyPermission("java.vm.specification.version", "read"));
        localPolicyEntry.add(new PropertyPermission("java.vm.specification.vendor", "read"));
        localPolicyEntry.add(new PropertyPermission("java.vm.specification.name", "read"));
        localPolicyEntry.add(new PropertyPermission("java.vm.version", "read"));
        localPolicyEntry.add(new PropertyPermission("java.vm.vendor", "read"));
        localPolicyEntry.add(new PropertyPermission("java.vm.name", "read"));
        paramPolicyInfopolicyEntries.add(localPolicyEntry);
        String[] arrayOfString = PolicyParser.parseExtDirs("${{java.ext.dirs}}", 0);
        if ((arrayOfString != null) && (arrayOfString.length > 0)) {
          for (int i = 0; i < arrayOfString.length; i++) {
            try
            {
              localPolicyEntry = new PolicyFile.PolicyEntry(PolicyFile.this.canonicalizeCodebase(new CodeSource(new URL(arrayOfString[i]), (Certificate[])null), false));
              localPolicyEntry.add(SecurityConstants.ALL_PERMISSION);
              paramPolicyInfopolicyEntries.add(localPolicyEntry);
            }
            catch (Exception localException) {}
          }
        }
        return null;
      }
    });
  }
  
  private CodeSource getCodeSource(PolicyParser.GrantEntry paramGrantEntry, KeyStore paramKeyStore, PolicyInfo paramPolicyInfo)
    throws MalformedURLException
  {
    Certificate[] arrayOfCertificate = null;
    if (signedBy != null)
    {
      arrayOfCertificate = getCertificates(paramKeyStore, signedBy, paramPolicyInfo);
      if (arrayOfCertificate == null)
      {
        if (debug != null) {
          debug.println("  -- No certs for alias '" + signedBy + "' - ignoring entry");
        }
        return null;
      }
    }
    URL localURL;
    if (codeBase != null) {
      localURL = new URL(codeBase);
    } else {
      localURL = null;
    }
    return canonicalizeCodebase(new CodeSource(localURL, arrayOfCertificate), false);
  }
  
  private void addGrantEntry(PolicyParser.GrantEntry paramGrantEntry, KeyStore paramKeyStore, PolicyInfo paramPolicyInfo)
  {
    Object localObject1;
    Object localObject2;
    if (debug != null)
    {
      debug.println("Adding policy entry: ");
      debug.println("  signedBy " + signedBy);
      debug.println("  codeBase " + codeBase);
      if (principals != null)
      {
        localObject1 = principals.iterator();
        while (((Iterator)localObject1).hasNext())
        {
          localObject2 = (PolicyParser.PrincipalEntry)((Iterator)localObject1).next();
          debug.println("  " + ((PolicyParser.PrincipalEntry)localObject2).toString());
        }
      }
    }
    try
    {
      localObject1 = getCodeSource(paramGrantEntry, paramKeyStore, paramPolicyInfo);
      if (localObject1 == null) {
        return;
      }
      if (!replacePrincipals(principals, paramKeyStore)) {
        return;
      }
      localObject2 = new PolicyEntry((CodeSource)localObject1, principals);
      localObject3 = paramGrantEntry.permissionElements();
      while (((Enumeration)localObject3).hasMoreElements())
      {
        PolicyParser.PermissionEntry localPermissionEntry = (PolicyParser.PermissionEntry)((Enumeration)localObject3).nextElement();
        try
        {
          expandPermissionName(localPermissionEntry, paramKeyStore);
          if ((permission.equals("javax.security.auth.PrivateCredentialPermission")) && (name.endsWith(" self"))) {
            name = (name.substring(0, name.indexOf("self")) + "${{self}}");
          }
          Object localObject4;
          if ((name != null) && (name.indexOf("${{self}}") != -1))
          {
            if (signedBy != null) {
              localObject5 = getCertificates(paramKeyStore, signedBy, paramPolicyInfo);
            } else {
              localObject5 = null;
            }
            localObject4 = new SelfPermission(permission, name, action, (Certificate[])localObject5);
          }
          else
          {
            localObject4 = getInstance(permission, name, action);
          }
          ((PolicyEntry)localObject2).add((Permission)localObject4);
          if (debug != null) {
            debug.println("  " + localObject4);
          }
        }
        catch (ClassNotFoundException localClassNotFoundException)
        {
          if (signedBy != null) {
            localObject5 = getCertificates(paramKeyStore, signedBy, paramPolicyInfo);
          } else {
            localObject5 = null;
          }
          if ((localObject5 != null) || (signedBy == null))
          {
            localObject6 = new UnresolvedPermission(permission, name, action, (Certificate[])localObject5);
            ((PolicyEntry)localObject2).add((Permission)localObject6);
            if (debug != null) {
              debug.println("  " + localObject6);
            }
          }
        }
        catch (InvocationTargetException localInvocationTargetException)
        {
          localObject5 = new MessageFormat(ResourcesMgr.getString("java.security.policy.error.adding.Permission.perm.message"));
          localObject6 = new Object[] { permission, localInvocationTargetException.getTargetException().toString() };
          System.err.println(((MessageFormat)localObject5).format(localObject6));
        }
        catch (Exception localException2)
        {
          Object localObject5 = new MessageFormat(ResourcesMgr.getString("java.security.policy.error.adding.Permission.perm.message"));
          Object localObject6 = { permission, localException2.toString() };
          System.err.println(((MessageFormat)localObject5).format(localObject6));
        }
      }
      policyEntries.add(localObject2);
    }
    catch (Exception localException1)
    {
      localObject2 = new MessageFormat(ResourcesMgr.getString("java.security.policy.error.adding.Entry.message"));
      Object localObject3 = { localException1.toString() };
      System.err.println(((MessageFormat)localObject2).format(localObject3));
    }
    if (debug != null) {
      debug.println();
    }
  }
  
  private static final Permission getInstance(String paramString1, String paramString2, String paramString3)
    throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException
  {
    Class localClass = Class.forName(paramString1, false, null);
    Permission localPermission = getKnownInstance(localClass, paramString2, paramString3);
    if (localPermission != null) {
      return localPermission;
    }
    if (!Permission.class.isAssignableFrom(localClass)) {
      throw new ClassCastException(paramString1 + " is not a Permission");
    }
    if ((paramString2 == null) && (paramString3 == null)) {
      try
      {
        Constructor localConstructor1 = localClass.getConstructor(PARAMS0);
        return (Permission)localConstructor1.newInstance(new Object[0]);
      }
      catch (NoSuchMethodException localNoSuchMethodException1)
      {
        try
        {
          Constructor localConstructor4 = localClass.getConstructor(PARAMS1);
          return (Permission)localConstructor4.newInstance(new Object[] { paramString2 });
        }
        catch (NoSuchMethodException localNoSuchMethodException3)
        {
          Constructor localConstructor6 = localClass.getConstructor(PARAMS2);
          return (Permission)localConstructor6.newInstance(new Object[] { paramString2, paramString3 });
        }
      }
    }
    if ((paramString2 != null) && (paramString3 == null)) {
      try
      {
        Constructor localConstructor2 = localClass.getConstructor(PARAMS1);
        return (Permission)localConstructor2.newInstance(new Object[] { paramString2 });
      }
      catch (NoSuchMethodException localNoSuchMethodException2)
      {
        Constructor localConstructor5 = localClass.getConstructor(PARAMS2);
        return (Permission)localConstructor5.newInstance(new Object[] { paramString2, paramString3 });
      }
    }
    Constructor localConstructor3 = localClass.getConstructor(PARAMS2);
    return (Permission)localConstructor3.newInstance(new Object[] { paramString2, paramString3 });
  }
  
  private static final Permission getKnownInstance(Class<?> paramClass, String paramString1, String paramString2)
  {
    if (paramClass.equals(FilePermission.class)) {
      return new FilePermission(paramString1, paramString2);
    }
    if (paramClass.equals(SocketPermission.class)) {
      return new SocketPermission(paramString1, paramString2);
    }
    if (paramClass.equals(RuntimePermission.class)) {
      return new RuntimePermission(paramString1, paramString2);
    }
    if (paramClass.equals(PropertyPermission.class)) {
      return new PropertyPermission(paramString1, paramString2);
    }
    if (paramClass.equals(NetPermission.class)) {
      return new NetPermission(paramString1, paramString2);
    }
    if (paramClass.equals(AllPermission.class)) {
      return SecurityConstants.ALL_PERMISSION;
    }
    return null;
  }
  
  private Certificate[] getCertificates(KeyStore paramKeyStore, String paramString, PolicyInfo paramPolicyInfo)
  {
    ArrayList localArrayList = null;
    StringTokenizer localStringTokenizer = new StringTokenizer(paramString, ",");
    int i = 0;
    Object localObject1;
    while (localStringTokenizer.hasMoreTokens())
    {
      localObject1 = localStringTokenizer.nextToken().trim();
      i++;
      Certificate localCertificate = null;
      synchronized (aliasMapping)
      {
        localCertificate = (Certificate)aliasMapping.get(localObject1);
        if ((localCertificate == null) && (paramKeyStore != null))
        {
          try
          {
            localCertificate = paramKeyStore.getCertificate((String)localObject1);
          }
          catch (KeyStoreException localKeyStoreException) {}
          if (localCertificate != null)
          {
            aliasMapping.put(localObject1, localCertificate);
            aliasMapping.put(localCertificate, localObject1);
          }
        }
      }
      if (localCertificate != null)
      {
        if (localArrayList == null) {
          localArrayList = new ArrayList();
        }
        localArrayList.add(localCertificate);
      }
    }
    if ((localArrayList != null) && (i == localArrayList.size()))
    {
      localObject1 = new Certificate[localArrayList.size()];
      localArrayList.toArray((Object[])localObject1);
      return (Certificate[])localObject1;
    }
    return null;
  }
  
  public void refresh()
  {
    init(url);
  }
  
  public boolean implies(ProtectionDomain paramProtectionDomain, Permission paramPermission)
  {
    PolicyInfo localPolicyInfo = (PolicyInfo)policyInfo.get();
    JavaSecurityProtectionDomainAccess.ProtectionDomainCache localProtectionDomainCache = localPolicyInfo.getPdMapping();
    PermissionCollection localPermissionCollection = localProtectionDomainCache.get(paramProtectionDomain);
    if (localPermissionCollection != null) {
      return localPermissionCollection.implies(paramPermission);
    }
    localPermissionCollection = getPermissions(paramProtectionDomain);
    if (localPermissionCollection == null) {
      return false;
    }
    localProtectionDomainCache.put(paramProtectionDomain, localPermissionCollection);
    return localPermissionCollection.implies(paramPermission);
  }
  
  public PermissionCollection getPermissions(ProtectionDomain paramProtectionDomain)
  {
    Permissions localPermissions = new Permissions();
    if (paramProtectionDomain == null) {
      return localPermissions;
    }
    getPermissions(localPermissions, paramProtectionDomain);
    PermissionCollection localPermissionCollection = paramProtectionDomain.getPermissions();
    if (localPermissionCollection != null) {
      synchronized (localPermissionCollection)
      {
        Enumeration localEnumeration = localPermissionCollection.elements();
        while (localEnumeration.hasMoreElements()) {
          localPermissions.add((Permission)localEnumeration.nextElement());
        }
      }
    }
    return localPermissions;
  }
  
  public PermissionCollection getPermissions(CodeSource paramCodeSource)
  {
    return getPermissions(new Permissions(), paramCodeSource);
  }
  
  private PermissionCollection getPermissions(Permissions paramPermissions, ProtectionDomain paramProtectionDomain)
  {
    if (debug != null) {
      debug.println("getPermissions:\n\t" + printPD(paramProtectionDomain));
    }
    final CodeSource localCodeSource1 = paramProtectionDomain.getCodeSource();
    if (localCodeSource1 == null) {
      return paramPermissions;
    }
    CodeSource localCodeSource2 = (CodeSource)AccessController.doPrivileged(new PrivilegedAction()
    {
      public CodeSource run()
      {
        return PolicyFile.this.canonicalizeCodebase(localCodeSource1, true);
      }
    });
    return getPermissions(paramPermissions, localCodeSource2, paramProtectionDomain.getPrincipals());
  }
  
  private PermissionCollection getPermissions(Permissions paramPermissions, final CodeSource paramCodeSource)
  {
    if (paramCodeSource == null) {
      return paramPermissions;
    }
    CodeSource localCodeSource = (CodeSource)AccessController.doPrivileged(new PrivilegedAction()
    {
      public CodeSource run()
      {
        return PolicyFile.this.canonicalizeCodebase(paramCodeSource, true);
      }
    });
    return getPermissions(paramPermissions, localCodeSource, null);
  }
  
  private Permissions getPermissions(Permissions paramPermissions, CodeSource paramCodeSource, Principal[] paramArrayOfPrincipal)
  {
    PolicyInfo localPolicyInfo = (PolicyInfo)policyInfo.get();
    Iterator localIterator = policyEntries.iterator();
    Object localObject1;
    while (localIterator.hasNext())
    {
      localObject1 = (PolicyEntry)localIterator.next();
      addPermissions(paramPermissions, paramCodeSource, paramArrayOfPrincipal, (PolicyEntry)localObject1);
    }
    Object localObject2;
    synchronized (identityPolicyEntries)
    {
      localObject1 = identityPolicyEntries.iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (PolicyEntry)((Iterator)localObject1).next();
        addPermissions(paramPermissions, paramCodeSource, paramArrayOfPrincipal, (PolicyEntry)localObject2);
      }
    }
    if (!ignoreIdentityScope)
    {
      ??? = paramCodeSource.getCertificates();
      if (??? != null) {
        for (int i = 0; i < ???.length; i++)
        {
          localObject2 = aliasMapping.get(???[i]);
          if ((localObject2 == null) && (checkForTrustedIdentity(???[i], localPolicyInfo))) {
            paramPermissions.add(SecurityConstants.ALL_PERMISSION);
          }
        }
      }
    }
    return paramPermissions;
  }
  
  private void addPermissions(Permissions paramPermissions, final CodeSource paramCodeSource, Principal[] paramArrayOfPrincipal, final PolicyEntry paramPolicyEntry)
  {
    if (debug != null) {
      debug.println("evaluate codesources:\n\tPolicy CodeSource: " + paramPolicyEntry.getCodeSource() + "\n\tActive CodeSource: " + paramCodeSource);
    }
    Boolean localBoolean = (Boolean)AccessController.doPrivileged(new PrivilegedAction()
    {
      public Boolean run()
      {
        return new Boolean(paramPolicyEntry.getCodeSource().implies(paramCodeSource));
      }
    });
    if (!localBoolean.booleanValue())
    {
      if (debug != null) {
        debug.println("evaluation (codesource) failed");
      }
      return;
    }
    List localList = paramPolicyEntry.getPrincipals();
    if (debug != null)
    {
      localObject = new ArrayList();
      if (paramArrayOfPrincipal != null) {
        for (int i = 0; i < paramArrayOfPrincipal.length; i++) {
          ((List)localObject).add(new PolicyParser.PrincipalEntry(paramArrayOfPrincipal[i].getClass().getName(), paramArrayOfPrincipal[i].getName()));
        }
      }
      debug.println("evaluate principals:\n\tPolicy Principals: " + localList + "\n\tActive Principals: " + localObject);
    }
    if ((localList == null) || (localList.isEmpty()))
    {
      addPerms(paramPermissions, paramArrayOfPrincipal, paramPolicyEntry);
      if (debug != null) {
        debug.println("evaluation (codesource/principals) passed");
      }
      return;
    }
    if ((paramArrayOfPrincipal == null) || (paramArrayOfPrincipal.length == 0))
    {
      if (debug != null) {
        debug.println("evaluation (principals) failed");
      }
      return;
    }
    Object localObject = localList.iterator();
    while (((Iterator)localObject).hasNext())
    {
      PolicyParser.PrincipalEntry localPrincipalEntry = (PolicyParser.PrincipalEntry)((Iterator)localObject).next();
      if (!localPrincipalEntry.isWildcardClass()) {
        if (localPrincipalEntry.isWildcardName())
        {
          if (!wildcardPrincipalNameImplies(principalClass, paramArrayOfPrincipal)) {
            if (debug != null) {
              debug.println("evaluation (principal name wildcard) failed");
            }
          }
        }
        else
        {
          HashSet localHashSet = new HashSet(Arrays.asList(paramArrayOfPrincipal));
          Subject localSubject = new Subject(true, localHashSet, Collections.EMPTY_SET, Collections.EMPTY_SET);
          try
          {
            ClassLoader localClassLoader = Thread.currentThread().getContextClassLoader();
            Class localClass = Class.forName(principalClass, false, localClassLoader);
            if (!Principal.class.isAssignableFrom(localClass)) {
              throw new ClassCastException(principalClass + " is not a Principal");
            }
            Constructor localConstructor = localClass.getConstructor(PARAMS1);
            Principal localPrincipal = (Principal)localConstructor.newInstance(new Object[] { principalName });
            if (debug != null) {
              debug.println("found Principal " + localPrincipal.getClass().getName());
            }
            if (!localPrincipal.implies(localSubject))
            {
              if (debug != null) {
                debug.println("evaluation (principal implies) failed");
              }
              return;
            }
          }
          catch (Exception localException)
          {
            if (debug != null) {
              localException.printStackTrace();
            }
            if (!localPrincipalEntry.implies(localSubject))
            {
              if (debug != null) {
                debug.println("evaluation (default principal implies) failed");
              }
              return;
            }
          }
        }
      }
    }
    if (debug != null) {
      debug.println("evaluation (codesource/principals) passed");
    }
    addPerms(paramPermissions, paramArrayOfPrincipal, paramPolicyEntry);
  }
  
  private static boolean wildcardPrincipalNameImplies(String paramString, Principal[] paramArrayOfPrincipal)
  {
    for (Principal localPrincipal : paramArrayOfPrincipal) {
      if (paramString.equals(localPrincipal.getClass().getName())) {
        return true;
      }
    }
    return false;
  }
  
  private void addPerms(Permissions paramPermissions, Principal[] paramArrayOfPrincipal, PolicyEntry paramPolicyEntry)
  {
    for (int i = 0; i < permissions.size(); i++)
    {
      Permission localPermission = (Permission)permissions.get(i);
      if (debug != null) {
        debug.println("  granting " + localPermission);
      }
      if ((localPermission instanceof SelfPermission)) {
        expandSelf((SelfPermission)localPermission, paramPolicyEntry.getPrincipals(), paramArrayOfPrincipal, paramPermissions);
      } else {
        paramPermissions.add(localPermission);
      }
    }
  }
  
  private void expandSelf(SelfPermission paramSelfPermission, List<PolicyParser.PrincipalEntry> paramList, Principal[] paramArrayOfPrincipal, Permissions paramPermissions)
  {
    if ((paramList == null) || (paramList.isEmpty()))
    {
      if (debug != null) {
        debug.println("Ignoring permission " + paramSelfPermission.getSelfType() + " with target name (" + paramSelfPermission.getSelfName() + ").  No Principal(s) specified in the grant clause.  SELF-based target names are only valid in the context of a Principal-based grant entry.");
      }
      return;
    }
    int i = 0;
    StringBuilder localStringBuilder = new StringBuilder();
    int j;
    Object localObject1;
    while ((j = paramSelfPermission.getSelfName().indexOf("${{self}}", i)) != -1)
    {
      localStringBuilder.append(paramSelfPermission.getSelfName().substring(i, j));
      Iterator localIterator = paramList.iterator();
      while (localIterator.hasNext())
      {
        localObject1 = (PolicyParser.PrincipalEntry)localIterator.next();
        String[][] arrayOfString = getPrincipalInfo((PolicyParser.PrincipalEntry)localObject1, paramArrayOfPrincipal);
        for (int k = 0; k < arrayOfString.length; k++)
        {
          if (k != 0) {
            localStringBuilder.append(", ");
          }
          localStringBuilder.append(arrayOfString[k][0] + " \"" + arrayOfString[k][1] + "\"");
        }
        if (localIterator.hasNext()) {
          localStringBuilder.append(", ");
        }
      }
      i = j + "${{self}}".length();
    }
    localStringBuilder.append(paramSelfPermission.getSelfName().substring(i));
    if (debug != null) {
      debug.println("  expanded:\n\t" + paramSelfPermission.getSelfName() + "\n  into:\n\t" + localStringBuilder.toString());
    }
    try
    {
      paramPermissions.add(getInstance(paramSelfPermission.getSelfType(), localStringBuilder.toString(), paramSelfPermission.getSelfActions()));
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      localObject1 = null;
      synchronized (paramPermissions)
      {
        Enumeration localEnumeration = paramPermissions.elements();
        while (localEnumeration.hasMoreElements())
        {
          Permission localPermission = (Permission)localEnumeration.nextElement();
          if (localPermission.getClass().getName().equals(paramSelfPermission.getSelfType()))
          {
            localObject1 = localPermission.getClass();
            break;
          }
        }
      }
      if (localObject1 == null) {
        paramPermissions.add(new UnresolvedPermission(paramSelfPermission.getSelfType(), localStringBuilder.toString(), paramSelfPermission.getSelfActions(), paramSelfPermission.getCerts()));
      } else {
        try
        {
          if (paramSelfPermission.getSelfActions() == null)
          {
            try
            {
              ??? = ((Class)localObject1).getConstructor(PARAMS1);
              paramPermissions.add((Permission)((Constructor)???).newInstance(new Object[] { localStringBuilder.toString() }));
            }
            catch (NoSuchMethodException localNoSuchMethodException)
            {
              ??? = ((Class)localObject1).getConstructor(PARAMS2);
              paramPermissions.add((Permission)((Constructor)???).newInstance(new Object[] { localStringBuilder.toString(), paramSelfPermission.getSelfActions() }));
            }
          }
          else
          {
            ??? = ((Class)localObject1).getConstructor(PARAMS2);
            paramPermissions.add((Permission)((Constructor)???).newInstance(new Object[] { localStringBuilder.toString(), paramSelfPermission.getSelfActions() }));
          }
        }
        catch (Exception localException2)
        {
          if (debug != null) {
            debug.println("self entry expansion  instantiation failed: " + localException2.toString());
          }
        }
      }
    }
    catch (Exception localException1)
    {
      if (debug != null) {
        debug.println(localException1.toString());
      }
    }
  }
  
  private String[][] getPrincipalInfo(PolicyParser.PrincipalEntry paramPrincipalEntry, Principal[] paramArrayOfPrincipal)
  {
    if ((!paramPrincipalEntry.isWildcardClass()) && (!paramPrincipalEntry.isWildcardName()))
    {
      localObject = new String[1][2];
      localObject[0][0] = principalClass;
      localObject[0][1] = principalName;
      return (String[][])localObject;
    }
    if ((!paramPrincipalEntry.isWildcardClass()) && (paramPrincipalEntry.isWildcardName()))
    {
      localObject = new ArrayList();
      for (int i = 0; i < paramArrayOfPrincipal.length; i++) {
        if (principalClass.equals(paramArrayOfPrincipal[i].getClass().getName())) {
          ((List)localObject).add(paramArrayOfPrincipal[i]);
        }
      }
      String[][] arrayOfString = new String[((List)localObject).size()][2];
      int k = 0;
      Iterator localIterator = ((List)localObject).iterator();
      while (localIterator.hasNext())
      {
        Principal localPrincipal = (Principal)localIterator.next();
        arrayOfString[k][0] = localPrincipal.getClass().getName();
        arrayOfString[k][1] = localPrincipal.getName();
        k++;
      }
      return arrayOfString;
    }
    Object localObject = new String[paramArrayOfPrincipal.length][2];
    for (int j = 0; j < paramArrayOfPrincipal.length; j++)
    {
      localObject[j][0] = paramArrayOfPrincipal[j].getClass().getName();
      localObject[j][1] = paramArrayOfPrincipal[j].getName();
    }
    return (String[][])localObject;
  }
  
  protected Certificate[] getSignerCertificates(CodeSource paramCodeSource)
  {
    Certificate[] arrayOfCertificate1 = null;
    if ((arrayOfCertificate1 = paramCodeSource.getCertificates()) == null) {
      return null;
    }
    for (int i = 0; i < arrayOfCertificate1.length; i++) {
      if (!(arrayOfCertificate1[i] instanceof X509Certificate)) {
        return paramCodeSource.getCertificates();
      }
    }
    i = 0;
    int j = 0;
    while (i < arrayOfCertificate1.length)
    {
      j++;
      while ((i + 1 < arrayOfCertificate1.length) && (((X509Certificate)arrayOfCertificate1[i]).getIssuerDN().equals(((X509Certificate)arrayOfCertificate1[(i + 1)]).getSubjectDN()))) {
        i++;
      }
      i++;
    }
    if (j == arrayOfCertificate1.length) {
      return arrayOfCertificate1;
    }
    ArrayList localArrayList = new ArrayList();
    for (i = 0; i < arrayOfCertificate1.length; i++)
    {
      localArrayList.add(arrayOfCertificate1[i]);
      while ((i + 1 < arrayOfCertificate1.length) && (((X509Certificate)arrayOfCertificate1[i]).getIssuerDN().equals(((X509Certificate)arrayOfCertificate1[(i + 1)]).getSubjectDN()))) {
        i++;
      }
    }
    Certificate[] arrayOfCertificate2 = new Certificate[localArrayList.size()];
    localArrayList.toArray(arrayOfCertificate2);
    return arrayOfCertificate2;
  }
  
  private CodeSource canonicalizeCodebase(CodeSource paramCodeSource, boolean paramBoolean)
  {
    String str1 = null;
    CodeSource localCodeSource = paramCodeSource;
    URL localURL1 = paramCodeSource.getLocation();
    if (localURL1 != null)
    {
      if (localURL1.getProtocol().equals("jar"))
      {
        String str2 = localURL1.getFile();
        int j = str2.indexOf("!/");
        if (j != -1) {
          try
          {
            localURL1 = new URL(str2.substring(0, j));
          }
          catch (MalformedURLException localMalformedURLException) {}
        }
      }
      if (localURL1.getProtocol().equals("file"))
      {
        int i = 0;
        String str3 = localURL1.getHost();
        i = (str3 == null) || (str3.equals("")) || (str3.equals("~")) || (str3.equalsIgnoreCase("localhost")) ? 1 : 0;
        if (i != 0)
        {
          str1 = localURL1.getFile().replace('/', File.separatorChar);
          str1 = ParseUtil.decode(str1);
        }
      }
    }
    if (str1 != null) {
      try
      {
        URL localURL2 = null;
        str1 = canonPath(str1);
        localURL2 = ParseUtil.fileToEncodedURL(new File(str1));
        if (paramBoolean) {
          localCodeSource = new CodeSource(localURL2, getSignerCertificates(paramCodeSource));
        } else {
          localCodeSource = new CodeSource(localURL2, paramCodeSource.getCertificates());
        }
      }
      catch (IOException localIOException)
      {
        if (paramBoolean) {
          localCodeSource = new CodeSource(paramCodeSource.getLocation(), getSignerCertificates(paramCodeSource));
        }
      }
    } else if (paramBoolean) {
      localCodeSource = new CodeSource(paramCodeSource.getLocation(), getSignerCertificates(paramCodeSource));
    }
    return localCodeSource;
  }
  
  private static String canonPath(String paramString)
    throws IOException
  {
    if (paramString.endsWith("*"))
    {
      paramString = paramString.substring(0, paramString.length() - 1) + "-";
      paramString = new File(paramString).getCanonicalPath();
      return paramString.substring(0, paramString.length() - 1) + "*";
    }
    return new File(paramString).getCanonicalPath();
  }
  
  private String printPD(ProtectionDomain paramProtectionDomain)
  {
    Principal[] arrayOfPrincipal = paramProtectionDomain.getPrincipals();
    String str = "<no principals>";
    if ((arrayOfPrincipal != null) && (arrayOfPrincipal.length > 0))
    {
      StringBuilder localStringBuilder = new StringBuilder("(principals ");
      for (int i = 0; i < arrayOfPrincipal.length; i++)
      {
        localStringBuilder.append(arrayOfPrincipal[i].getClass().getName() + " \"" + arrayOfPrincipal[i].getName() + "\"");
        if (i < arrayOfPrincipal.length - 1) {
          localStringBuilder.append(", ");
        } else {
          localStringBuilder.append(")");
        }
      }
      str = localStringBuilder.toString();
    }
    return "PD CodeSource: " + paramProtectionDomain.getCodeSource() + "\n\tPD ClassLoader: " + paramProtectionDomain.getClassLoader() + "\n\tPD Principals: " + str;
  }
  
  private boolean replacePrincipals(List<PolicyParser.PrincipalEntry> paramList, KeyStore paramKeyStore)
  {
    if ((paramList == null) || (paramList.isEmpty()) || (paramKeyStore == null)) {
      return true;
    }
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      PolicyParser.PrincipalEntry localPrincipalEntry = (PolicyParser.PrincipalEntry)localIterator.next();
      if (localPrincipalEntry.isReplaceName())
      {
        String str;
        if ((str = getDN(principalName, paramKeyStore)) == null) {
          return false;
        }
        if (debug != null) {
          debug.println("  Replacing \"" + principalName + "\" with " + "javax.security.auth.x500.X500Principal" + "/\"" + str + "\"");
        }
        principalClass = "javax.security.auth.x500.X500Principal";
        principalName = str;
      }
    }
    return true;
  }
  
  private void expandPermissionName(PolicyParser.PermissionEntry paramPermissionEntry, KeyStore paramKeyStore)
    throws Exception
  {
    if ((name == null) || (name.indexOf("${{", 0) == -1)) {
      return;
    }
    int i = 0;
    StringBuilder localStringBuilder = new StringBuilder();
    int j;
    while ((j = name.indexOf("${{", i)) != -1)
    {
      int k = name.indexOf("}}", j);
      if (k < 1) {
        break;
      }
      localStringBuilder.append(name.substring(i, j));
      String str1 = name.substring(j + 3, k);
      String str2 = str1;
      int m;
      if ((m = str1.indexOf(":")) != -1) {
        str2 = str1.substring(0, m);
      }
      if (str2.equalsIgnoreCase("self"))
      {
        localStringBuilder.append(name.substring(j, k + 2));
        i = k + 2;
      }
      else
      {
        MessageFormat localMessageFormat;
        Object[] arrayOfObject;
        if (str2.equalsIgnoreCase("alias"))
        {
          if (m == -1)
          {
            localMessageFormat = new MessageFormat(ResourcesMgr.getString("alias.name.not.provided.pe.name."));
            arrayOfObject = new Object[] { name };
            throw new Exception(localMessageFormat.format(arrayOfObject));
          }
          String str3 = str1.substring(m + 1);
          if ((str3 = getDN(str3, paramKeyStore)) == null)
          {
            localMessageFormat = new MessageFormat(ResourcesMgr.getString("unable.to.perform.substitution.on.alias.suffix"));
            arrayOfObject = new Object[] { str1.substring(m + 1) };
            throw new Exception(localMessageFormat.format(arrayOfObject));
          }
          localStringBuilder.append("javax.security.auth.x500.X500Principal \"" + str3 + "\"");
          i = k + 2;
        }
        else
        {
          localMessageFormat = new MessageFormat(ResourcesMgr.getString("substitution.value.prefix.unsupported"));
          arrayOfObject = new Object[] { str2 };
          throw new Exception(localMessageFormat.format(arrayOfObject));
        }
      }
    }
    localStringBuilder.append(name.substring(i));
    if (debug != null) {
      debug.println("  Permission name expanded from:\n\t" + name + "\nto\n\t" + localStringBuilder.toString());
    }
    name = localStringBuilder.toString();
  }
  
  private String getDN(String paramString, KeyStore paramKeyStore)
  {
    Certificate localCertificate = null;
    try
    {
      localCertificate = paramKeyStore.getCertificate(paramString);
    }
    catch (Exception localException)
    {
      if (debug != null) {
        debug.println("  Error retrieving certificate for '" + paramString + "': " + localException.toString());
      }
      return null;
    }
    if ((localCertificate == null) || (!(localCertificate instanceof X509Certificate)))
    {
      if (debug != null) {
        debug.println("  -- No certificate for '" + paramString + "' - ignoring entry");
      }
      return null;
    }
    X509Certificate localX509Certificate = (X509Certificate)localCertificate;
    X500Principal localX500Principal = new X500Principal(localX509Certificate.getSubjectX500Principal().toString());
    return localX500Principal.getName();
  }
  
  private boolean checkForTrustedIdentity(Certificate paramCertificate, PolicyInfo paramPolicyInfo)
  {
    return false;
  }
  
  private static class PolicyEntry
  {
    private final CodeSource codesource;
    final List<Permission> permissions;
    private final List<PolicyParser.PrincipalEntry> principals;
    
    PolicyEntry(CodeSource paramCodeSource, List<PolicyParser.PrincipalEntry> paramList)
    {
      codesource = paramCodeSource;
      permissions = new ArrayList();
      principals = paramList;
    }
    
    PolicyEntry(CodeSource paramCodeSource)
    {
      this(paramCodeSource, null);
    }
    
    List<PolicyParser.PrincipalEntry> getPrincipals()
    {
      return principals;
    }
    
    void add(Permission paramPermission)
    {
      permissions.add(paramPermission);
    }
    
    CodeSource getCodeSource()
    {
      return codesource;
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append(ResourcesMgr.getString("LPARAM"));
      localStringBuilder.append(getCodeSource());
      localStringBuilder.append("\n");
      for (int i = 0; i < permissions.size(); i++)
      {
        Permission localPermission = (Permission)permissions.get(i);
        localStringBuilder.append(ResourcesMgr.getString("SPACE"));
        localStringBuilder.append(ResourcesMgr.getString("SPACE"));
        localStringBuilder.append(localPermission);
        localStringBuilder.append(ResourcesMgr.getString("NEWLINE"));
      }
      localStringBuilder.append(ResourcesMgr.getString("RPARAM"));
      localStringBuilder.append(ResourcesMgr.getString("NEWLINE"));
      return localStringBuilder.toString();
    }
  }
  
  private static class PolicyInfo
  {
    private static final boolean verbose = false;
    final List<PolicyFile.PolicyEntry> policyEntries = new ArrayList();
    final List<PolicyFile.PolicyEntry> identityPolicyEntries = Collections.synchronizedList(new ArrayList(2));
    final Map<Object, Object> aliasMapping = Collections.synchronizedMap(new HashMap(11));
    private final JavaSecurityProtectionDomainAccess.ProtectionDomainCache[] pdMapping;
    private Random random;
    
    PolicyInfo(int paramInt)
    {
      pdMapping = new JavaSecurityProtectionDomainAccess.ProtectionDomainCache[paramInt];
      JavaSecurityProtectionDomainAccess localJavaSecurityProtectionDomainAccess = SharedSecrets.getJavaSecurityProtectionDomainAccess();
      for (int i = 0; i < paramInt; i++) {
        pdMapping[i] = localJavaSecurityProtectionDomainAccess.getProtectionDomainCache();
      }
      if (paramInt > 1) {
        random = new Random();
      }
    }
    
    JavaSecurityProtectionDomainAccess.ProtectionDomainCache getPdMapping()
    {
      if (pdMapping.length == 1) {
        return pdMapping[0];
      }
      int i = Math.abs(random.nextInt() % pdMapping.length);
      return pdMapping[i];
    }
  }
  
  private static class SelfPermission
    extends Permission
  {
    private static final long serialVersionUID = -8315562579967246806L;
    private String type;
    private String name;
    private String actions;
    private Certificate[] certs;
    
    public SelfPermission(String paramString1, String paramString2, String paramString3, Certificate[] paramArrayOfCertificate)
    {
      super();
      if (paramString1 == null) {
        throw new NullPointerException(ResourcesMgr.getString("type.can.t.be.null"));
      }
      type = paramString1;
      name = paramString2;
      actions = paramString3;
      if (paramArrayOfCertificate != null)
      {
        for (int i = 0; i < paramArrayOfCertificate.length; i++) {
          if (!(paramArrayOfCertificate[i] instanceof X509Certificate))
          {
            certs = ((Certificate[])paramArrayOfCertificate.clone());
            break;
          }
        }
        if (certs == null)
        {
          i = 0;
          int j = 0;
          while (i < paramArrayOfCertificate.length)
          {
            j++;
            while ((i + 1 < paramArrayOfCertificate.length) && (((X509Certificate)paramArrayOfCertificate[i]).getIssuerDN().equals(((X509Certificate)paramArrayOfCertificate[(i + 1)]).getSubjectDN()))) {
              i++;
            }
            i++;
          }
          if (j == paramArrayOfCertificate.length) {
            certs = ((Certificate[])paramArrayOfCertificate.clone());
          }
          if (certs == null)
          {
            ArrayList localArrayList = new ArrayList();
            for (i = 0; i < paramArrayOfCertificate.length; i++)
            {
              localArrayList.add(paramArrayOfCertificate[i]);
              while ((i + 1 < paramArrayOfCertificate.length) && (((X509Certificate)paramArrayOfCertificate[i]).getIssuerDN().equals(((X509Certificate)paramArrayOfCertificate[(i + 1)]).getSubjectDN()))) {
                i++;
              }
            }
            certs = new Certificate[localArrayList.size()];
            localArrayList.toArray(certs);
          }
        }
      }
    }
    
    public boolean implies(Permission paramPermission)
    {
      return false;
    }
    
    public boolean equals(Object paramObject)
    {
      if (paramObject == this) {
        return true;
      }
      if (!(paramObject instanceof SelfPermission)) {
        return false;
      }
      SelfPermission localSelfPermission = (SelfPermission)paramObject;
      if ((!type.equals(type)) || (!name.equals(name)) || (!actions.equals(actions))) {
        return false;
      }
      if (certs.length != certs.length) {
        return false;
      }
      int k;
      int j;
      for (int i = 0; i < certs.length; i++)
      {
        k = 0;
        for (j = 0; j < certs.length; j++) {
          if (certs[i].equals(certs[j]))
          {
            k = 1;
            break;
          }
        }
        if (k == 0) {
          return false;
        }
      }
      for (i = 0; i < certs.length; i++)
      {
        k = 0;
        for (j = 0; j < certs.length; j++) {
          if (certs[i].equals(certs[j]))
          {
            k = 1;
            break;
          }
        }
        if (k == 0) {
          return false;
        }
      }
      return true;
    }
    
    public int hashCode()
    {
      int i = type.hashCode();
      if (name != null) {
        i ^= name.hashCode();
      }
      if (actions != null) {
        i ^= actions.hashCode();
      }
      return i;
    }
    
    public String getActions()
    {
      return "";
    }
    
    public String getSelfType()
    {
      return type;
    }
    
    public String getSelfName()
    {
      return name;
    }
    
    public String getSelfActions()
    {
      return actions;
    }
    
    public Certificate[] getCerts()
    {
      return certs;
    }
    
    public String toString()
    {
      return "(SelfPermission " + type + " " + name + " " + actions + ")";
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\provider\PolicyFile.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */