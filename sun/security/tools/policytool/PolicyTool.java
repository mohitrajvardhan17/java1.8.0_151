package sun.security.tools.policytool;

import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Permission;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.text.Collator;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.ResourceBundle;
import java.util.Vector;
import javax.security.auth.login.LoginException;
import javax.security.auth.x500.X500Principal;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import sun.security.provider.PolicyParser;
import sun.security.provider.PolicyParser.GrantEntry;
import sun.security.provider.PolicyParser.ParsingException;
import sun.security.provider.PolicyParser.PermissionEntry;
import sun.security.provider.PolicyParser.PrincipalEntry;
import sun.security.util.PolicyUtil;
import sun.security.util.PropertyExpander;
import sun.security.util.PropertyExpander.ExpandException;

public class PolicyTool
{
  static final ResourceBundle rb = ResourceBundle.getBundle("sun.security.tools.policytool.Resources");
  static final Collator collator = Collator.getInstance();
  Vector<String> warnings = new Vector();
  boolean newWarning = false;
  boolean modified = false;
  private static final boolean testing = false;
  private static final Class<?>[] TWOPARAMS = { String.class, String.class };
  private static final Class<?>[] ONEPARAMS = { String.class };
  private static final Class<?>[] NOPARAMS = new Class[0];
  private static String policyFileName = null;
  private Vector<PolicyEntry> policyEntries = null;
  private PolicyParser parser = null;
  private KeyStore keyStore = null;
  private String keyStoreName = " ";
  private String keyStoreType = " ";
  private String keyStoreProvider = " ";
  private String keyStorePwdURL = " ";
  private static final String P11KEYSTORE = "PKCS11";
  private static final String NONE = "NONE";
  
  private PolicyTool() {}
  
  String getPolicyFileName()
  {
    return policyFileName;
  }
  
  void setPolicyFileName(String paramString)
  {
    policyFileName = paramString;
  }
  
  void clearKeyStoreInfo()
  {
    keyStoreName = null;
    keyStoreType = null;
    keyStoreProvider = null;
    keyStorePwdURL = null;
    keyStore = null;
  }
  
  String getKeyStoreName()
  {
    return keyStoreName;
  }
  
  String getKeyStoreType()
  {
    return keyStoreType;
  }
  
  String getKeyStoreProvider()
  {
    return keyStoreProvider;
  }
  
  String getKeyStorePwdURL()
  {
    return keyStorePwdURL;
  }
  
  void openPolicy(String paramString)
    throws FileNotFoundException, PolicyParser.ParsingException, KeyStoreException, CertificateException, InstantiationException, MalformedURLException, IOException, NoSuchAlgorithmException, IllegalAccessException, NoSuchMethodException, UnrecoverableKeyException, NoSuchProviderException, ClassNotFoundException, PropertyExpander.ExpandException, InvocationTargetException
  {
    newWarning = false;
    policyEntries = new Vector();
    parser = new PolicyParser();
    warnings = new Vector();
    setPolicyFileName(null);
    clearKeyStoreInfo();
    if (paramString == null)
    {
      modified = false;
      return;
    }
    setPolicyFileName(paramString);
    parser.read(new FileReader(paramString));
    openKeyStore(parser.getKeyStoreUrl(), parser.getKeyStoreType(), parser.getKeyStoreProvider(), parser.getStorePassURL());
    Enumeration localEnumeration = parser.grantElements();
    while (localEnumeration.hasMoreElements())
    {
      PolicyParser.GrantEntry localGrantEntry = (PolicyParser.GrantEntry)localEnumeration.nextElement();
      MessageFormat localMessageFormat1;
      Object localObject4;
      if (signedBy != null)
      {
        localObject1 = parseSigners(signedBy);
        for (int i = 0; i < localObject1.length; i++)
        {
          PublicKey localPublicKey = getPublicKeyAlias(localObject1[i]);
          if (localPublicKey == null)
          {
            newWarning = true;
            localMessageFormat1 = new MessageFormat(getMessage("Warning.A.public.key.for.alias.signers.i.does.not.exist.Make.sure.a.KeyStore.is.properly.configured."));
            localObject4 = new Object[] { localObject1[i] };
            warnings.addElement(localMessageFormat1.format(localObject4));
          }
        }
      }
      Object localObject1 = principals.listIterator(0);
      while (((ListIterator)localObject1).hasNext())
      {
        localObject2 = (PolicyParser.PrincipalEntry)((ListIterator)localObject1).next();
        try
        {
          verifyPrincipal(((PolicyParser.PrincipalEntry)localObject2).getPrincipalClass(), ((PolicyParser.PrincipalEntry)localObject2).getPrincipalName());
        }
        catch (ClassNotFoundException localClassNotFoundException1)
        {
          newWarning = true;
          localMessageFormat1 = new MessageFormat(getMessage("Warning.Class.not.found.class"));
          localObject4 = new Object[] { ((PolicyParser.PrincipalEntry)localObject2).getPrincipalClass() };
          warnings.addElement(localMessageFormat1.format(localObject4));
        }
      }
      Object localObject2 = localGrantEntry.permissionElements();
      while (((Enumeration)localObject2).hasMoreElements())
      {
        localObject3 = (PolicyParser.PermissionEntry)((Enumeration)localObject2).nextElement();
        Object localObject5;
        try
        {
          verifyPermission(permission, name, action);
        }
        catch (ClassNotFoundException localClassNotFoundException2)
        {
          newWarning = true;
          localObject4 = new MessageFormat(getMessage("Warning.Class.not.found.class"));
          localObject5 = new Object[] { permission };
          warnings.addElement(((MessageFormat)localObject4).format(localObject5));
        }
        catch (InvocationTargetException localInvocationTargetException)
        {
          newWarning = true;
          localObject4 = new MessageFormat(getMessage("Warning.Invalid.argument.s.for.constructor.arg"));
          localObject5 = new Object[] { permission };
          warnings.addElement(((MessageFormat)localObject4).format(localObject5));
        }
        if (signedBy != null)
        {
          String[] arrayOfString = parseSigners(signedBy);
          for (int j = 0; j < arrayOfString.length; j++)
          {
            localObject5 = getPublicKeyAlias(arrayOfString[j]);
            if (localObject5 == null)
            {
              newWarning = true;
              MessageFormat localMessageFormat2 = new MessageFormat(getMessage("Warning.A.public.key.for.alias.signers.i.does.not.exist.Make.sure.a.KeyStore.is.properly.configured."));
              Object[] arrayOfObject = { arrayOfString[j] };
              warnings.addElement(localMessageFormat2.format(arrayOfObject));
            }
          }
        }
      }
      Object localObject3 = new PolicyEntry(this, localGrantEntry);
      policyEntries.addElement(localObject3);
    }
    modified = false;
  }
  
  void savePolicy(String paramString)
    throws FileNotFoundException, IOException
  {
    parser.setKeyStoreUrl(keyStoreName);
    parser.setKeyStoreType(keyStoreType);
    parser.setKeyStoreProvider(keyStoreProvider);
    parser.setStorePassURL(keyStorePwdURL);
    parser.write(new FileWriter(paramString));
    modified = false;
  }
  
  void openKeyStore(String paramString1, String paramString2, String paramString3, String paramString4)
    throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException, IOException, CertificateException, NoSuchProviderException, PropertyExpander.ExpandException
  {
    if ((paramString1 == null) && (paramString2 == null) && (paramString3 == null) && (paramString4 == null))
    {
      keyStoreName = null;
      keyStoreType = null;
      keyStoreProvider = null;
      keyStorePwdURL = null;
      return;
    }
    URL localURL = null;
    if (policyFileName != null)
    {
      File localFile = new File(policyFileName);
      localURL = new URL("file:" + localFile.getCanonicalPath());
    }
    if ((paramString1 != null) && (paramString1.length() > 0)) {
      paramString1 = PropertyExpander.expand(paramString1).replace(File.separatorChar, '/');
    }
    if ((paramString2 == null) || (paramString2.length() == 0)) {
      paramString2 = KeyStore.getDefaultType();
    }
    if ((paramString4 != null) && (paramString4.length() > 0)) {
      paramString4 = PropertyExpander.expand(paramString4).replace(File.separatorChar, '/');
    }
    try
    {
      keyStore = PolicyUtil.getKeyStore(localURL, paramString1, paramString2, paramString3, paramString4, null);
    }
    catch (IOException localIOException)
    {
      String str = "no password provided, and no callback handler available for retrieving password";
      Throwable localThrowable = localIOException.getCause();
      if ((localThrowable != null) && ((localThrowable instanceof LoginException)) && (str.equals(localThrowable.getMessage()))) {
        throw new IOException(str);
      }
      throw localIOException;
    }
    keyStoreName = paramString1;
    keyStoreType = paramString2;
    keyStoreProvider = paramString3;
    keyStorePwdURL = paramString4;
  }
  
  boolean addEntry(PolicyEntry paramPolicyEntry, int paramInt)
  {
    if (paramInt < 0)
    {
      policyEntries.addElement(paramPolicyEntry);
      parser.add(paramPolicyEntry.getGrantEntry());
    }
    else
    {
      PolicyEntry localPolicyEntry = (PolicyEntry)policyEntries.elementAt(paramInt);
      parser.replace(localPolicyEntry.getGrantEntry(), paramPolicyEntry.getGrantEntry());
      policyEntries.setElementAt(paramPolicyEntry, paramInt);
    }
    return true;
  }
  
  boolean addPrinEntry(PolicyEntry paramPolicyEntry, PolicyParser.PrincipalEntry paramPrincipalEntry, int paramInt)
  {
    PolicyParser.GrantEntry localGrantEntry = paramPolicyEntry.getGrantEntry();
    if (localGrantEntry.contains(paramPrincipalEntry) == true) {
      return false;
    }
    LinkedList localLinkedList = principals;
    if (paramInt != -1) {
      localLinkedList.set(paramInt, paramPrincipalEntry);
    } else {
      localLinkedList.add(paramPrincipalEntry);
    }
    modified = true;
    return true;
  }
  
  boolean addPermEntry(PolicyEntry paramPolicyEntry, PolicyParser.PermissionEntry paramPermissionEntry, int paramInt)
  {
    PolicyParser.GrantEntry localGrantEntry = paramPolicyEntry.getGrantEntry();
    if (localGrantEntry.contains(paramPermissionEntry) == true) {
      return false;
    }
    Vector localVector = permissionEntries;
    if (paramInt != -1) {
      localVector.setElementAt(paramPermissionEntry, paramInt);
    } else {
      localVector.addElement(paramPermissionEntry);
    }
    modified = true;
    return true;
  }
  
  boolean removePermEntry(PolicyEntry paramPolicyEntry, PolicyParser.PermissionEntry paramPermissionEntry)
  {
    PolicyParser.GrantEntry localGrantEntry = paramPolicyEntry.getGrantEntry();
    modified = localGrantEntry.remove(paramPermissionEntry);
    return modified;
  }
  
  boolean removeEntry(PolicyEntry paramPolicyEntry)
  {
    parser.remove(paramPolicyEntry.getGrantEntry());
    modified = true;
    return policyEntries.removeElement(paramPolicyEntry);
  }
  
  PolicyEntry[] getEntry()
  {
    if (policyEntries.size() > 0)
    {
      PolicyEntry[] arrayOfPolicyEntry = new PolicyEntry[policyEntries.size()];
      for (int i = 0; i < policyEntries.size(); i++) {
        arrayOfPolicyEntry[i] = ((PolicyEntry)policyEntries.elementAt(i));
      }
      return arrayOfPolicyEntry;
    }
    return null;
  }
  
  PublicKey getPublicKeyAlias(String paramString)
    throws KeyStoreException
  {
    if (keyStore == null) {
      return null;
    }
    Certificate localCertificate = keyStore.getCertificate(paramString);
    if (localCertificate == null) {
      return null;
    }
    PublicKey localPublicKey = localCertificate.getPublicKey();
    return localPublicKey;
  }
  
  String[] getPublicKeyAlias()
    throws KeyStoreException
  {
    int i = 0;
    String[] arrayOfString = null;
    if (keyStore == null) {
      return null;
    }
    Enumeration localEnumeration = keyStore.aliases();
    while (localEnumeration.hasMoreElements())
    {
      localEnumeration.nextElement();
      i++;
    }
    if (i > 0)
    {
      arrayOfString = new String[i];
      i = 0;
      localEnumeration = keyStore.aliases();
      while (localEnumeration.hasMoreElements())
      {
        arrayOfString[i] = new String((String)localEnumeration.nextElement());
        i++;
      }
    }
    return arrayOfString;
  }
  
  String[] parseSigners(String paramString)
  {
    String[] arrayOfString = null;
    int i = 1;
    int j = 0;
    int k = 0;
    int m = 0;
    while (k >= 0)
    {
      k = paramString.indexOf(',', j);
      if (k >= 0)
      {
        i++;
        j = k + 1;
      }
    }
    arrayOfString = new String[i];
    k = 0;
    j = 0;
    while (k >= 0) {
      if ((k = paramString.indexOf(',', j)) >= 0)
      {
        arrayOfString[m] = paramString.substring(j, k).trim();
        m++;
        j = k + 1;
      }
      else
      {
        arrayOfString[m] = paramString.substring(j).trim();
      }
    }
    return arrayOfString;
  }
  
  void verifyPrincipal(String paramString1, String paramString2)
    throws ClassNotFoundException, InstantiationException
  {
    if ((paramString1.equals("WILDCARD_PRINCIPAL_CLASS")) || (paramString1.equals("PolicyParser.REPLACE_NAME"))) {
      return;
    }
    Class localClass1 = Class.forName("java.security.Principal");
    Class localClass2 = Class.forName(paramString1, true, Thread.currentThread().getContextClassLoader());
    Object localObject;
    if (!localClass1.isAssignableFrom(localClass2))
    {
      localObject = new MessageFormat(getMessage("Illegal.Principal.Type.type"));
      Object[] arrayOfObject = { paramString1 };
      throw new InstantiationException(((MessageFormat)localObject).format(arrayOfObject));
    }
    if ("javax.security.auth.x500.X500Principal".equals(localClass2.getName())) {
      localObject = new X500Principal(paramString2);
    }
  }
  
  void verifyPermission(String paramString1, String paramString2, String paramString3)
    throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException
  {
    Class localClass = Class.forName(paramString1, true, Thread.currentThread().getContextClassLoader());
    Constructor localConstructor = null;
    Vector localVector = new Vector(2);
    if (paramString2 != null) {
      localVector.add(paramString2);
    }
    if (paramString3 != null) {
      localVector.add(paramString3);
    }
    switch (localVector.size())
    {
    case 0: 
      try
      {
        localConstructor = localClass.getConstructor(NOPARAMS);
      }
      catch (NoSuchMethodException localNoSuchMethodException1)
      {
        localVector.add(null);
      }
    case 1: 
      try
      {
        localConstructor = localClass.getConstructor(ONEPARAMS);
      }
      catch (NoSuchMethodException localNoSuchMethodException2)
      {
        localVector.add(null);
      }
    case 2: 
      localConstructor = localClass.getConstructor(TWOPARAMS);
    }
    Object[] arrayOfObject = localVector.toArray();
    Permission localPermission = (Permission)localConstructor.newInstance(arrayOfObject);
  }
  
  static void parseArgs(String[] paramArrayOfString)
  {
    int i = 0;
    for (i = 0; (i < paramArrayOfString.length) && (paramArrayOfString[i].startsWith("-")); i++)
    {
      String str = paramArrayOfString[i];
      if (collator.compare(str, "-file") == 0)
      {
        i++;
        if (i == paramArrayOfString.length) {
          usage();
        }
        policyFileName = paramArrayOfString[i];
      }
      else
      {
        MessageFormat localMessageFormat = new MessageFormat(getMessage("Illegal.option.option"));
        Object[] arrayOfObject = { str };
        System.err.println(localMessageFormat.format(arrayOfObject));
        usage();
      }
    }
  }
  
  static void usage()
  {
    System.out.println(getMessage("Usage.policytool.options."));
    System.out.println();
    System.out.println(getMessage(".file.file.policy.file.location"));
    System.out.println();
    System.exit(1);
  }
  
  public static void main(String[] paramArrayOfString)
  {
    parseArgs(paramArrayOfString);
    SwingUtilities.invokeLater(new Runnable()
    {
      public void run()
      {
        ToolWindow localToolWindow = new ToolWindow(new PolicyTool(null));
        localToolWindow.displayToolWindow(val$args);
      }
    });
  }
  
  static String splitToWords(String paramString)
  {
    return paramString.replaceAll("([A-Z])", " $1");
  }
  
  static String getMessage(String paramString)
  {
    return removeMnemonicAmpersand(rb.getString(paramString));
  }
  
  static int getMnemonicInt(String paramString)
  {
    String str = rb.getString(paramString);
    return findMnemonicInt(str);
  }
  
  static int getDisplayedMnemonicIndex(String paramString)
  {
    String str = rb.getString(paramString);
    return findMnemonicIndex(str);
  }
  
  private static int findMnemonicInt(String paramString)
  {
    for (int i = 0; i < paramString.length() - 1; i++) {
      if (paramString.charAt(i) == '&')
      {
        if (paramString.charAt(i + 1) != '&') {
          return KeyEvent.getExtendedKeyCodeForChar(paramString.charAt(i + 1));
        }
        i++;
      }
    }
    return 0;
  }
  
  private static int findMnemonicIndex(String paramString)
  {
    for (int i = 0; i < paramString.length() - 1; i++) {
      if (paramString.charAt(i) == '&')
      {
        if (paramString.charAt(i + 1) != '&') {
          return i;
        }
        i++;
      }
    }
    return -1;
  }
  
  private static String removeMnemonicAmpersand(String paramString)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    for (int i = 0; i < paramString.length(); i++)
    {
      char c = paramString.charAt(i);
      if ((c != '&') || (i == paramString.length() - 1) || (paramString.charAt(i + 1) == '&')) {
        localStringBuilder.append(c);
      }
    }
    return localStringBuilder.toString();
  }
  
  static
  {
    collator.setStrength(0);
    if (System.getProperty("apple.laf.useScreenMenuBar") == null) {
      System.setProperty("apple.laf.useScreenMenuBar", "true");
    }
    System.setProperty("apple.awt.application.name", getMessage("Policy.Tool"));
    if (System.getProperty("swing.defaultlaf") == null) {
      try
      {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      }
      catch (Exception localException) {}
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\tools\policytool\PolicyTool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */