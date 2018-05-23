package sun.security.provider;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessController;
import java.security.AllPermission;
import java.security.CodeSource;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.security.Security;
import java.security.UnresolvedPermission;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.security.auth.AuthPermission;
import javax.security.auth.Policy;
import javax.security.auth.PrivateCredentialPermission;
import javax.security.auth.Subject;
import sun.security.util.Debug;
import sun.security.util.PolicyUtil;
import sun.security.util.PropertyExpander;

@Deprecated
public class AuthPolicyFile
  extends Policy
{
  static final ResourceBundle rb = (ResourceBundle)AccessController.doPrivileged(new PrivilegedAction()
  {
    public ResourceBundle run()
    {
      return ResourceBundle.getBundle("sun.security.util.AuthResources");
    }
  });
  private static final Debug debug = Debug.getInstance("policy", "\t[Auth Policy]");
  private static final String AUTH_POLICY = "java.security.auth.policy";
  private static final String SECURITY_MANAGER = "java.security.manager";
  private static final String AUTH_POLICY_URL = "auth.policy.url.";
  private Vector<PolicyEntry> policyEntries;
  private Hashtable<Object, Object> aliasMapping;
  private boolean initialized = false;
  private boolean expandProperties = true;
  private boolean ignoreIdentityScope = true;
  private static final Class<?>[] PARAMS = { String.class, String.class };
  
  public AuthPolicyFile()
  {
    String str = System.getProperty("java.security.auth.policy");
    if (str == null) {
      str = System.getProperty("java.security.manager");
    }
    if (str != null) {
      init();
    }
  }
  
  private synchronized void init()
  {
    if (initialized) {
      return;
    }
    policyEntries = new Vector();
    aliasMapping = new Hashtable(11);
    initPolicyFile();
    initialized = true;
  }
  
  public synchronized void refresh()
  {
    SecurityManager localSecurityManager = System.getSecurityManager();
    if (localSecurityManager != null) {
      localSecurityManager.checkPermission(new AuthPermission("refreshPolicy"));
    }
    initialized = false;
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Void run()
      {
        AuthPolicyFile.this.init();
        return null;
      }
    });
  }
  
  private KeyStore initKeyStore(URL paramURL, String paramString1, String paramString2)
  {
    if (paramString1 != null) {
      try
      {
        URL localURL = null;
        try
        {
          localURL = new URL(paramString1);
        }
        catch (MalformedURLException localMalformedURLException)
        {
          localURL = new URL(paramURL, paramString1);
        }
        if (debug != null) {
          debug.println("reading keystore" + localURL);
        }
        BufferedInputStream localBufferedInputStream = new BufferedInputStream(PolicyUtil.getInputStream(localURL));
        KeyStore localKeyStore;
        if (paramString2 != null) {
          localKeyStore = KeyStore.getInstance(paramString2);
        } else {
          localKeyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        }
        localKeyStore.load(localBufferedInputStream, null);
        localBufferedInputStream.close();
        return localKeyStore;
      }
      catch (Exception localException)
      {
        if (debug != null) {
          localException.printStackTrace();
        }
        return null;
      }
    }
    return null;
  }
  
  private void initPolicyFile()
  {
    String str1 = Security.getProperty("policy.expandProperties");
    if (str1 != null) {
      expandProperties = str1.equalsIgnoreCase("true");
    }
    String str2 = Security.getProperty("policy.ignoreIdentityScope");
    if (str2 != null) {
      ignoreIdentityScope = str2.equalsIgnoreCase("true");
    }
    String str3 = Security.getProperty("policy.allowSystemProperty");
    if ((str3 != null) && (str3.equalsIgnoreCase("true")))
    {
      String str4 = System.getProperty("java.security.auth.policy");
      if (str4 != null)
      {
        j = 0;
        if (str4.startsWith("="))
        {
          j = 1;
          str4 = str4.substring(1);
        }
        try
        {
          str4 = PropertyExpander.expand(str4);
          File localFile = new File(str4);
          URL localURL;
          if (localFile.exists()) {
            localURL = new URL("file:" + localFile.getCanonicalPath());
          } else {
            localURL = new URL(str4);
          }
          if (debug != null) {
            debug.println("reading " + localURL);
          }
          init(localURL);
        }
        catch (Exception localException1)
        {
          if (debug != null) {
            debug.println("caught exception: " + localException1);
          }
        }
        if (j != 0)
        {
          if (debug != null) {
            debug.println("overriding other policies!");
          }
          return;
        }
      }
    }
    int i = 1;
    int j = 0;
    String str5;
    while ((str5 = Security.getProperty("auth.policy.url." + i)) != null)
    {
      try
      {
        str5 = PropertyExpander.expand(str5).replace(File.separatorChar, '/');
        if (debug != null) {
          debug.println("reading " + str5);
        }
        init(new URL(str5));
        j = 1;
      }
      catch (Exception localException2)
      {
        if (debug != null)
        {
          debug.println("error reading policy " + localException2);
          localException2.printStackTrace();
        }
      }
      i++;
    }
    if (j == 0) {}
  }
  
  private boolean checkForTrustedIdentity(Certificate paramCertificate)
  {
    return false;
  }
  
  private void init(URL paramURL)
  {
    PolicyParser localPolicyParser = new PolicyParser(expandProperties);
    try
    {
      InputStreamReader localInputStreamReader = new InputStreamReader(PolicyUtil.getInputStream(paramURL));
      Object localObject1 = null;
      try
      {
        localPolicyParser.read(localInputStreamReader);
        KeyStore localKeyStore = initKeyStore(paramURL, localPolicyParser.getKeyStoreUrl(), localPolicyParser.getKeyStoreType());
        Enumeration localEnumeration = localPolicyParser.grantElements();
        while (localEnumeration.hasMoreElements())
        {
          PolicyParser.GrantEntry localGrantEntry = (PolicyParser.GrantEntry)localEnumeration.nextElement();
          addGrantEntry(localGrantEntry, localKeyStore);
        }
      }
      catch (Throwable localThrowable2)
      {
        localObject1 = localThrowable2;
        throw localThrowable2;
      }
      finally
      {
        if (localInputStreamReader != null) {
          if (localObject1 != null) {
            try
            {
              localInputStreamReader.close();
            }
            catch (Throwable localThrowable3)
            {
              ((Throwable)localObject1).addSuppressed(localThrowable3);
            }
          } else {
            localInputStreamReader.close();
          }
        }
      }
    }
    catch (PolicyParser.ParsingException localParsingException)
    {
      System.err.println("java.security.auth.policy" + rb.getString(".error.parsing.") + paramURL);
      System.err.println("java.security.auth.policy" + rb.getString("COLON") + localParsingException.getMessage());
      if (debug != null) {
        localParsingException.printStackTrace();
      }
    }
    catch (Exception localException)
    {
      if (debug != null)
      {
        debug.println("error parsing " + paramURL);
        debug.println(localException.toString());
        localException.printStackTrace();
      }
    }
  }
  
  CodeSource getCodeSource(PolicyParser.GrantEntry paramGrantEntry, KeyStore paramKeyStore)
    throws MalformedURLException
  {
    Certificate[] arrayOfCertificate = null;
    if (signedBy != null)
    {
      arrayOfCertificate = getCertificates(paramKeyStore, signedBy);
      if (arrayOfCertificate == null)
      {
        if (debug != null) {
          debug.println(" no certs for alias " + signedBy + ", ignoring.");
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
    if ((principals == null) || (principals.size() == 0)) {
      return canonicalizeCodebase(new CodeSource(localURL, arrayOfCertificate), false);
    }
    return canonicalizeCodebase(new SubjectCodeSource(null, principals, localURL, arrayOfCertificate), false);
  }
  
  private void addGrantEntry(PolicyParser.GrantEntry paramGrantEntry, KeyStore paramKeyStore)
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
          debug.println("  " + ((PolicyParser.PrincipalEntry)localObject2).getPrincipalClass() + " " + ((PolicyParser.PrincipalEntry)localObject2).getPrincipalName());
        }
      }
      debug.println();
    }
    try
    {
      localObject1 = getCodeSource(paramGrantEntry, paramKeyStore);
      if (localObject1 == null) {
        return;
      }
      localObject2 = new PolicyEntry((CodeSource)localObject1);
      Enumeration localEnumeration = paramGrantEntry.permissionElements();
      while (localEnumeration.hasMoreElements())
      {
        PolicyParser.PermissionEntry localPermissionEntry = (PolicyParser.PermissionEntry)localEnumeration.nextElement();
        try
        {
          Permission localPermission;
          if ((permission.equals("javax.security.auth.PrivateCredentialPermission")) && (name.endsWith(" self"))) {
            localPermission = getInstance(permission, name + " \"self\"", action);
          } else {
            localPermission = getInstance(permission, name, action);
          }
          ((PolicyEntry)localObject2).add(localPermission);
          if (debug != null) {
            debug.println("  " + localPermission);
          }
        }
        catch (ClassNotFoundException localClassNotFoundException)
        {
          Certificate[] arrayOfCertificate;
          if (signedBy != null) {
            arrayOfCertificate = getCertificates(paramKeyStore, signedBy);
          } else {
            arrayOfCertificate = null;
          }
          if ((arrayOfCertificate != null) || (signedBy == null))
          {
            UnresolvedPermission localUnresolvedPermission = new UnresolvedPermission(permission, name, action, arrayOfCertificate);
            ((PolicyEntry)localObject2).add(localUnresolvedPermission);
            if (debug != null) {
              debug.println("  " + localUnresolvedPermission);
            }
          }
        }
        catch (InvocationTargetException localInvocationTargetException)
        {
          System.err.println("java.security.auth.policy" + rb.getString(".error.adding.Permission.") + permission + rb.getString("SPACE") + localInvocationTargetException.getTargetException());
        }
        catch (Exception localException2)
        {
          System.err.println("java.security.auth.policy" + rb.getString(".error.adding.Permission.") + permission + rb.getString("SPACE") + localException2);
        }
      }
      policyEntries.addElement(localObject2);
    }
    catch (Exception localException1)
    {
      System.err.println("java.security.auth.policy" + rb.getString(".error.adding.Entry.") + paramGrantEntry + rb.getString("SPACE") + localException1);
    }
    if (debug != null) {
      debug.println();
    }
  }
  
  private static final Permission getInstance(String paramString1, String paramString2, String paramString3)
    throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException
  {
    Class localClass = Class.forName(paramString1);
    Constructor localConstructor = localClass.getConstructor(PARAMS);
    return (Permission)localConstructor.newInstance(new Object[] { paramString2, paramString3 });
  }
  
  Certificate[] getCertificates(KeyStore paramKeyStore, String paramString)
  {
    Vector localVector = null;
    StringTokenizer localStringTokenizer = new StringTokenizer(paramString, ",");
    int i = 0;
    Object localObject;
    while (localStringTokenizer.hasMoreTokens())
    {
      localObject = localStringTokenizer.nextToken().trim();
      i++;
      Certificate localCertificate = null;
      localCertificate = (Certificate)aliasMapping.get(localObject);
      if ((localCertificate == null) && (paramKeyStore != null))
      {
        try
        {
          localCertificate = paramKeyStore.getCertificate((String)localObject);
        }
        catch (KeyStoreException localKeyStoreException) {}
        if (localCertificate != null)
        {
          aliasMapping.put(localObject, localCertificate);
          aliasMapping.put(localCertificate, localObject);
        }
      }
      if (localCertificate != null)
      {
        if (localVector == null) {
          localVector = new Vector();
        }
        localVector.addElement(localCertificate);
      }
    }
    if ((localVector != null) && (i == localVector.size()))
    {
      localObject = new Certificate[localVector.size()];
      localVector.copyInto((Object[])localObject);
      return (Certificate[])localObject;
    }
    return null;
  }
  
  private final synchronized Enumeration<PolicyEntry> elements()
  {
    return policyEntries.elements();
  }
  
  public PermissionCollection getPermissions(final Subject paramSubject, final CodeSource paramCodeSource)
  {
    (PermissionCollection)AccessController.doPrivileged(new PrivilegedAction()
    {
      public PermissionCollection run()
      {
        SubjectCodeSource localSubjectCodeSource = new SubjectCodeSource(paramSubject, null, paramCodeSource == null ? null : paramCodeSource.getLocation(), paramCodeSource == null ? null : paramCodeSource.getCertificates());
        if (initialized) {
          return getPermissions(new Permissions(), localSubjectCodeSource);
        }
        return new PolicyPermissions(AuthPolicyFile.this, localSubjectCodeSource);
      }
    });
  }
  
  PermissionCollection getPermissions(CodeSource paramCodeSource)
  {
    if (initialized) {
      return getPermissions(new Permissions(), paramCodeSource);
    }
    return new PolicyPermissions(this, paramCodeSource);
  }
  
  Permissions getPermissions(Permissions paramPermissions, CodeSource paramCodeSource)
  {
    if (!initialized) {
      init();
    }
    CodeSource[] arrayOfCodeSource = { null };
    arrayOfCodeSource[0] = canonicalizeCodebase(paramCodeSource, true);
    if (debug != null) {
      debug.println("evaluate(" + arrayOfCodeSource[0] + ")\n");
    }
    for (int i = 0; i < policyEntries.size(); i++)
    {
      PolicyEntry localPolicyEntry = (PolicyEntry)policyEntries.elementAt(i);
      if (debug != null) {
        debug.println("PolicyFile CodeSource implies: " + codesource.toString() + "\n\n\t" + arrayOfCodeSource[0].toString() + "\n\n");
      }
      if (codesource.implies(arrayOfCodeSource[0])) {
        for (int k = 0; k < permissions.size(); k++)
        {
          Permission localPermission = (Permission)permissions.elementAt(k);
          if (debug != null) {
            debug.println("  granting " + localPermission);
          }
          if (!addSelfPermissions(localPermission, codesource, arrayOfCodeSource[0], paramPermissions)) {
            paramPermissions.add(localPermission);
          }
        }
      }
    }
    if (!ignoreIdentityScope)
    {
      Certificate[] arrayOfCertificate = arrayOfCodeSource[0].getCertificates();
      if (arrayOfCertificate != null) {
        for (int j = 0; j < arrayOfCertificate.length; j++) {
          if ((aliasMapping.get(arrayOfCertificate[j]) == null) && (checkForTrustedIdentity(arrayOfCertificate[j]))) {
            paramPermissions.add(new AllPermission());
          }
        }
      }
    }
    return paramPermissions;
  }
  
  private boolean addSelfPermissions(Permission paramPermission, CodeSource paramCodeSource1, CodeSource paramCodeSource2, Permissions paramPermissions)
  {
    if (!(paramPermission instanceof PrivateCredentialPermission)) {
      return false;
    }
    if (!(paramCodeSource1 instanceof SubjectCodeSource)) {
      return false;
    }
    PrivateCredentialPermission localPrivateCredentialPermission1 = (PrivateCredentialPermission)paramPermission;
    SubjectCodeSource localSubjectCodeSource = (SubjectCodeSource)paramCodeSource1;
    String[][] arrayOfString1 = localPrivateCredentialPermission1.getPrincipals();
    if ((arrayOfString1.length <= 0) || (!arrayOfString1[0][0].equalsIgnoreCase("self")) || (!arrayOfString1[0][1].equalsIgnoreCase("self"))) {
      return false;
    }
    if (localSubjectCodeSource.getPrincipals() == null) {
      return true;
    }
    Iterator localIterator = localSubjectCodeSource.getPrincipals().iterator();
    while (localIterator.hasNext())
    {
      PolicyParser.PrincipalEntry localPrincipalEntry = (PolicyParser.PrincipalEntry)localIterator.next();
      String[][] arrayOfString2 = getPrincipalInfo(localPrincipalEntry, paramCodeSource2);
      for (int i = 0; i < arrayOfString2.length; i++)
      {
        PrivateCredentialPermission localPrivateCredentialPermission2 = new PrivateCredentialPermission(localPrivateCredentialPermission1.getCredentialClass() + " " + arrayOfString2[i][0] + " \"" + arrayOfString2[i][1] + "\"", "read");
        if (debug != null) {
          debug.println("adding SELF permission: " + localPrivateCredentialPermission2.toString());
        }
        paramPermissions.add(localPrivateCredentialPermission2);
      }
    }
    return true;
  }
  
  private String[][] getPrincipalInfo(PolicyParser.PrincipalEntry paramPrincipalEntry, CodeSource paramCodeSource)
  {
    if ((!paramPrincipalEntry.getPrincipalClass().equals("WILDCARD_PRINCIPAL_CLASS")) && (!paramPrincipalEntry.getPrincipalName().equals("WILDCARD_PRINCIPAL_NAME")))
    {
      localObject = new String[1][2];
      localObject[0][0] = paramPrincipalEntry.getPrincipalClass();
      localObject[0][1] = paramPrincipalEntry.getPrincipalName();
      return (String[][])localObject;
    }
    Principal localPrincipal;
    if ((!paramPrincipalEntry.getPrincipalClass().equals("WILDCARD_PRINCIPAL_CLASS")) && (paramPrincipalEntry.getPrincipalName().equals("WILDCARD_PRINCIPAL_NAME")))
    {
      localObject = (SubjectCodeSource)paramCodeSource;
      localSet = null;
      try
      {
        Class localClass = Class.forName(paramPrincipalEntry.getPrincipalClass(), false, ClassLoader.getSystemClassLoader());
        localSet = ((SubjectCodeSource)localObject).getSubject().getPrincipals(localClass);
      }
      catch (Exception localException)
      {
        if (debug != null) {
          debug.println("problem finding Principal Class when expanding SELF permission: " + localException.toString());
        }
      }
      if (localSet == null) {
        return new String[0][0];
      }
      arrayOfString = new String[localSet.size()][2];
      i = 0;
      localIterator = localSet.iterator();
      while (localIterator.hasNext())
      {
        localPrincipal = (Principal)localIterator.next();
        arrayOfString[i][0] = localPrincipal.getClass().getName();
        arrayOfString[i][1] = localPrincipal.getName();
        i++;
      }
      return arrayOfString;
    }
    Object localObject = (SubjectCodeSource)paramCodeSource;
    Set localSet = ((SubjectCodeSource)localObject).getSubject().getPrincipals();
    String[][] arrayOfString = new String[localSet.size()][2];
    int i = 0;
    Iterator localIterator = localSet.iterator();
    while (localIterator.hasNext())
    {
      localPrincipal = (Principal)localIterator.next();
      arrayOfString[i][0] = localPrincipal.getClass().getName();
      arrayOfString[i][1] = localPrincipal.getName();
      i++;
    }
    return arrayOfString;
  }
  
  Certificate[] getSignerCertificates(CodeSource paramCodeSource)
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
    Object localObject1 = paramCodeSource;
    if ((paramCodeSource.getLocation() != null) && (paramCodeSource.getLocation().getProtocol().equalsIgnoreCase("file"))) {
      try
      {
        String str = paramCodeSource.getLocation().getFile().replace('/', File.separatorChar);
        localObject2 = null;
        if (str.endsWith("*"))
        {
          str = str.substring(0, str.length() - 1);
          int i = 0;
          if (str.endsWith(File.separator)) {
            i = 1;
          }
          if (str.equals("")) {
            str = System.getProperty("user.dir");
          }
          File localFile = new File(str);
          str = localFile.getCanonicalPath();
          StringBuffer localStringBuffer = new StringBuffer(str);
          if ((!str.endsWith(File.separator)) && ((i != 0) || (localFile.isDirectory()))) {
            localStringBuffer.append(File.separatorChar);
          }
          localStringBuffer.append('*');
          str = localStringBuffer.toString();
        }
        else
        {
          str = new File(str).getCanonicalPath();
        }
        localObject2 = new File(str).toURL();
        if ((paramCodeSource instanceof SubjectCodeSource))
        {
          SubjectCodeSource localSubjectCodeSource2 = (SubjectCodeSource)paramCodeSource;
          if (paramBoolean) {
            localObject1 = new SubjectCodeSource(localSubjectCodeSource2.getSubject(), localSubjectCodeSource2.getPrincipals(), (URL)localObject2, getSignerCertificates(localSubjectCodeSource2));
          } else {
            localObject1 = new SubjectCodeSource(localSubjectCodeSource2.getSubject(), localSubjectCodeSource2.getPrincipals(), (URL)localObject2, localSubjectCodeSource2.getCertificates());
          }
        }
        else if (paramBoolean)
        {
          localObject1 = new CodeSource((URL)localObject2, getSignerCertificates(paramCodeSource));
        }
        else
        {
          localObject1 = new CodeSource((URL)localObject2, paramCodeSource.getCertificates());
        }
      }
      catch (IOException localIOException)
      {
        Object localObject2;
        if (paramBoolean) {
          if (!(paramCodeSource instanceof SubjectCodeSource))
          {
            localObject1 = new CodeSource(paramCodeSource.getLocation(), getSignerCertificates(paramCodeSource));
          }
          else
          {
            localObject2 = (SubjectCodeSource)paramCodeSource;
            localObject1 = new SubjectCodeSource(((SubjectCodeSource)localObject2).getSubject(), ((SubjectCodeSource)localObject2).getPrincipals(), ((SubjectCodeSource)localObject2).getLocation(), getSignerCertificates((CodeSource)localObject2));
          }
        }
      }
    } else if (paramBoolean) {
      if (!(paramCodeSource instanceof SubjectCodeSource))
      {
        localObject1 = new CodeSource(paramCodeSource.getLocation(), getSignerCertificates(paramCodeSource));
      }
      else
      {
        SubjectCodeSource localSubjectCodeSource1 = (SubjectCodeSource)paramCodeSource;
        localObject1 = new SubjectCodeSource(localSubjectCodeSource1.getSubject(), localSubjectCodeSource1.getPrincipals(), localSubjectCodeSource1.getLocation(), getSignerCertificates(localSubjectCodeSource1));
      }
    }
    return (CodeSource)localObject1;
  }
  
  private static class PolicyEntry
  {
    CodeSource codesource;
    Vector<Permission> permissions;
    
    PolicyEntry(CodeSource paramCodeSource)
    {
      codesource = paramCodeSource;
      permissions = new Vector();
    }
    
    void add(Permission paramPermission)
    {
      permissions.addElement(paramPermission);
    }
    
    CodeSource getCodeSource()
    {
      return codesource;
    }
    
    public String toString()
    {
      StringBuffer localStringBuffer = new StringBuffer();
      localStringBuffer.append(AuthPolicyFile.rb.getString("LPARAM"));
      localStringBuffer.append(getCodeSource());
      localStringBuffer.append("\n");
      for (int i = 0; i < permissions.size(); i++)
      {
        Permission localPermission = (Permission)permissions.elementAt(i);
        localStringBuffer.append(AuthPolicyFile.rb.getString("SPACE"));
        localStringBuffer.append(AuthPolicyFile.rb.getString("SPACE"));
        localStringBuffer.append(localPermission);
        localStringBuffer.append(AuthPolicyFile.rb.getString("NEWLINE"));
      }
      localStringBuffer.append(AuthPolicyFile.rb.getString("RPARAM"));
      localStringBuffer.append(AuthPolicyFile.rb.getString("NEWLINE"));
      return localStringBuffer.toString();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\provider\AuthPolicyFile.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */