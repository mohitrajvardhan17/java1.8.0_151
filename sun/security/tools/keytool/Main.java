package sun.security.tools.keytool;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.CodeSigner;
import java.security.CryptoPrimitive;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStore.Entry;
import java.security.KeyStore.PasswordProtection;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.KeyStore.ProtectionParameter;
import java.security.KeyStore.SecretKeyEntry;
import java.security.KeyStore.TrustedCertificateEntry;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.Timestamp;
import java.security.UnrecoverableEntryException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CRL;
import java.security.cert.CertPath;
import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLEntry;
import java.security.cert.X509CRLSelector;
import java.security.cert.X509Certificate;
import java.text.Collator;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.security.auth.x500.X500Principal;
import sun.misc.HexDumpEncoder;
import sun.security.pkcs.PKCS9Attribute;
import sun.security.pkcs10.PKCS10;
import sun.security.pkcs10.PKCS10Attribute;
import sun.security.pkcs10.PKCS10Attributes;
import sun.security.provider.certpath.CertStoreHelper;
import sun.security.tools.KeyStoreUtil;
import sun.security.tools.PathList;
import sun.security.util.DerValue;
import sun.security.util.DisabledAlgorithmConstraints;
import sun.security.util.KeyUtil;
import sun.security.util.ObjectIdentifier;
import sun.security.util.Password;
import sun.security.util.Pem;
import sun.security.util.SecurityProviderConstants;
import sun.security.x509.AccessDescription;
import sun.security.x509.AlgorithmId;
import sun.security.x509.AuthorityInfoAccessExtension;
import sun.security.x509.AuthorityKeyIdentifierExtension;
import sun.security.x509.BasicConstraintsExtension;
import sun.security.x509.CRLDistributionPointsExtension;
import sun.security.x509.CRLExtensions;
import sun.security.x509.CRLReasonCodeExtension;
import sun.security.x509.CertificateAlgorithmId;
import sun.security.x509.CertificateExtensions;
import sun.security.x509.CertificateSerialNumber;
import sun.security.x509.CertificateValidity;
import sun.security.x509.CertificateVersion;
import sun.security.x509.CertificateX509Key;
import sun.security.x509.DNSName;
import sun.security.x509.DistributionPoint;
import sun.security.x509.ExtendedKeyUsageExtension;
import sun.security.x509.Extension;
import sun.security.x509.GeneralName;
import sun.security.x509.GeneralNameInterface;
import sun.security.x509.GeneralNames;
import sun.security.x509.IPAddressName;
import sun.security.x509.IssuerAlternativeNameExtension;
import sun.security.x509.KeyIdentifier;
import sun.security.x509.KeyUsageExtension;
import sun.security.x509.OIDName;
import sun.security.x509.PKIXExtensions;
import sun.security.x509.RFC822Name;
import sun.security.x509.SubjectAlternativeNameExtension;
import sun.security.x509.SubjectInfoAccessExtension;
import sun.security.x509.SubjectKeyIdentifierExtension;
import sun.security.x509.URIName;
import sun.security.x509.X500Name;
import sun.security.x509.X509CRLEntryImpl;
import sun.security.x509.X509CRLImpl;
import sun.security.x509.X509CertImpl;
import sun.security.x509.X509CertInfo;

public final class Main
{
  private static final byte[] CRLF = { 13, 10 };
  private boolean debug = false;
  private Command command = null;
  private String sigAlgName = null;
  private String keyAlgName = null;
  private boolean verbose = false;
  private int keysize = -1;
  private boolean rfc = false;
  private long validity = 90L;
  private String alias = null;
  private String dname = null;
  private String dest = null;
  private String filename = null;
  private String infilename = null;
  private String outfilename = null;
  private String srcksfname = null;
  private Set<Pair<String, String>> providers = null;
  private String storetype = null;
  private String srcProviderName = null;
  private String providerName = null;
  private String pathlist = null;
  private char[] storePass = null;
  private char[] storePassNew = null;
  private char[] keyPass = null;
  private char[] keyPassNew = null;
  private char[] newPass = null;
  private char[] destKeyPass = null;
  private char[] srckeyPass = null;
  private String ksfname = null;
  private File ksfile = null;
  private InputStream ksStream = null;
  private String sslserver = null;
  private String jarfile = null;
  private KeyStore keyStore = null;
  private boolean token = false;
  private boolean nullStream = false;
  private boolean kssave = false;
  private boolean noprompt = false;
  private boolean trustcacerts = false;
  private boolean nowarn = false;
  private boolean protectedPath = false;
  private boolean srcprotectedPath = false;
  private CertificateFactory cf = null;
  private KeyStore caks = null;
  private char[] srcstorePass = null;
  private String srcstoretype = null;
  private Set<char[]> passwords = new HashSet();
  private String startDate = null;
  private List<String> ids = new ArrayList();
  private List<String> v3ext = new ArrayList();
  private boolean inplaceImport = false;
  private String inplaceBackupName = null;
  private List<String> weakWarnings = new ArrayList();
  private static final DisabledAlgorithmConstraints DISABLED_CHECK = new DisabledAlgorithmConstraints("jdk.certpath.disabledAlgorithms");
  private static final Set<CryptoPrimitive> SIG_PRIMITIVE_SET = Collections.unmodifiableSet(EnumSet.of(CryptoPrimitive.SIGNATURE));
  private static final Class<?>[] PARAM_STRING = { String.class };
  private static final String NONE = "NONE";
  private static final String P11KEYSTORE = "PKCS11";
  private static final String P12KEYSTORE = "PKCS12";
  private static final String keyAlias = "mykey";
  private static final ResourceBundle rb = ResourceBundle.getBundle("sun.security.tools.keytool.Resources");
  private static final Collator collator = Collator.getInstance();
  private static final String[] extSupported = { "BasicConstraints", "KeyUsage", "ExtendedKeyUsage", "SubjectAlternativeName", "IssuerAlternativeName", "SubjectInfoAccess", "AuthorityInfoAccess", null, "CRLDistributionPoints" };
  
  private Main() {}
  
  public static void main(String[] paramArrayOfString)
    throws Exception
  {
    Main localMain = new Main();
    localMain.run(paramArrayOfString, System.out);
  }
  
  private void run(String[] paramArrayOfString, PrintStream paramPrintStream)
    throws Exception
  {
    try
    {
      parseArgs(paramArrayOfString);
      if (command != null) {
        doCommands(paramPrintStream);
      }
    }
    catch (Exception localException)
    {
      Iterator localIterator1;
      System.out.println(rb.getString("keytool.error.") + localException);
      if (verbose) {
        localException.printStackTrace(System.out);
      }
      if (!debug) {
        System.exit(1);
      } else {
        throw localException;
      }
    }
    finally
    {
      char[] arrayOfChar1;
      Iterator localIterator2;
      printWeakWarnings(false);
      Iterator localIterator3 = passwords.iterator();
      while (localIterator3.hasNext())
      {
        char[] arrayOfChar2 = (char[])localIterator3.next();
        if (arrayOfChar2 != null)
        {
          Arrays.fill(arrayOfChar2, ' ');
          arrayOfChar2 = null;
        }
      }
      if (ksStream != null) {
        ksStream.close();
      }
    }
  }
  
  void parseArgs(String[] paramArrayOfString)
  {
    int i = 0;
    int j = paramArrayOfString.length == 0 ? 1 : 0;
    for (i = 0; (i < paramArrayOfString.length) && (paramArrayOfString[i].startsWith("-")); i++)
    {
      String str1 = paramArrayOfString[i];
      Object localObject2;
      if (i == paramArrayOfString.length - 1) {
        for (localObject2 : Option.values()) {
          if (collator.compare(str1, ((Option)localObject2).toString()) == 0)
          {
            if (arg == null) {
              break;
            }
            errorNeedArgument(str1);
            break;
          }
        }
      }
      ??? = null;
      ??? = str1.indexOf(':');
      if (??? > 0)
      {
        ??? = str1.substring(??? + 1);
        str1 = str1.substring(0, ???);
      }
      ??? = 0;
      for (Command localCommand : Command.values()) {
        if (collator.compare(str1, localCommand.toString()) == 0)
        {
          command = localCommand;
          ??? = 1;
          break;
        }
      }
      if (??? == 0) {
        if (collator.compare(str1, "-export") == 0)
        {
          command = Command.EXPORTCERT;
        }
        else if (collator.compare(str1, "-genkey") == 0)
        {
          command = Command.GENKEYPAIR;
        }
        else if (collator.compare(str1, "-import") == 0)
        {
          command = Command.IMPORTCERT;
        }
        else if (collator.compare(str1, "-importpassword") == 0)
        {
          command = Command.IMPORTPASS;
        }
        else if (collator.compare(str1, "-help") == 0)
        {
          j = 1;
        }
        else if (collator.compare(str1, "-nowarn") == 0)
        {
          nowarn = true;
        }
        else if ((collator.compare(str1, "-keystore") == 0) || (collator.compare(str1, "-destkeystore") == 0))
        {
          ksfname = paramArrayOfString[(++i)];
        }
        else if ((collator.compare(str1, "-storepass") == 0) || (collator.compare(str1, "-deststorepass") == 0))
        {
          storePass = getPass((String)???, paramArrayOfString[(++i)]);
          passwords.add(storePass);
        }
        else if ((collator.compare(str1, "-storetype") == 0) || (collator.compare(str1, "-deststoretype") == 0))
        {
          storetype = paramArrayOfString[(++i)];
        }
        else if (collator.compare(str1, "-srcstorepass") == 0)
        {
          srcstorePass = getPass((String)???, paramArrayOfString[(++i)]);
          passwords.add(srcstorePass);
        }
        else if (collator.compare(str1, "-srcstoretype") == 0)
        {
          srcstoretype = paramArrayOfString[(++i)];
        }
        else if (collator.compare(str1, "-srckeypass") == 0)
        {
          srckeyPass = getPass((String)???, paramArrayOfString[(++i)]);
          passwords.add(srckeyPass);
        }
        else if (collator.compare(str1, "-srcprovidername") == 0)
        {
          srcProviderName = paramArrayOfString[(++i)];
        }
        else if ((collator.compare(str1, "-providername") == 0) || (collator.compare(str1, "-destprovidername") == 0))
        {
          providerName = paramArrayOfString[(++i)];
        }
        else if (collator.compare(str1, "-providerpath") == 0)
        {
          pathlist = paramArrayOfString[(++i)];
        }
        else if (collator.compare(str1, "-keypass") == 0)
        {
          keyPass = getPass((String)???, paramArrayOfString[(++i)]);
          passwords.add(keyPass);
        }
        else if (collator.compare(str1, "-new") == 0)
        {
          newPass = getPass((String)???, paramArrayOfString[(++i)]);
          passwords.add(newPass);
        }
        else if (collator.compare(str1, "-destkeypass") == 0)
        {
          destKeyPass = getPass((String)???, paramArrayOfString[(++i)]);
          passwords.add(destKeyPass);
        }
        else if ((collator.compare(str1, "-alias") == 0) || (collator.compare(str1, "-srcalias") == 0))
        {
          alias = paramArrayOfString[(++i)];
        }
        else if ((collator.compare(str1, "-dest") == 0) || (collator.compare(str1, "-destalias") == 0))
        {
          dest = paramArrayOfString[(++i)];
        }
        else if (collator.compare(str1, "-dname") == 0)
        {
          dname = paramArrayOfString[(++i)];
        }
        else if (collator.compare(str1, "-keysize") == 0)
        {
          keysize = Integer.parseInt(paramArrayOfString[(++i)]);
        }
        else if (collator.compare(str1, "-keyalg") == 0)
        {
          keyAlgName = paramArrayOfString[(++i)];
        }
        else if (collator.compare(str1, "-sigalg") == 0)
        {
          sigAlgName = paramArrayOfString[(++i)];
        }
        else if (collator.compare(str1, "-startdate") == 0)
        {
          startDate = paramArrayOfString[(++i)];
        }
        else if (collator.compare(str1, "-validity") == 0)
        {
          validity = Long.parseLong(paramArrayOfString[(++i)]);
        }
        else if (collator.compare(str1, "-ext") == 0)
        {
          v3ext.add(paramArrayOfString[(++i)]);
        }
        else if (collator.compare(str1, "-id") == 0)
        {
          ids.add(paramArrayOfString[(++i)]);
        }
        else if (collator.compare(str1, "-file") == 0)
        {
          filename = paramArrayOfString[(++i)];
        }
        else if (collator.compare(str1, "-infile") == 0)
        {
          infilename = paramArrayOfString[(++i)];
        }
        else if (collator.compare(str1, "-outfile") == 0)
        {
          outfilename = paramArrayOfString[(++i)];
        }
        else if (collator.compare(str1, "-sslserver") == 0)
        {
          sslserver = paramArrayOfString[(++i)];
        }
        else if (collator.compare(str1, "-jarfile") == 0)
        {
          jarfile = paramArrayOfString[(++i)];
        }
        else if (collator.compare(str1, "-srckeystore") == 0)
        {
          srcksfname = paramArrayOfString[(++i)];
        }
        else if ((collator.compare(str1, "-provider") == 0) || (collator.compare(str1, "-providerclass") == 0))
        {
          if (providers == null) {
            providers = new HashSet(3);
          }
          localObject2 = paramArrayOfString[(++i)];
          String str2 = null;
          if (paramArrayOfString.length > i + 1)
          {
            str1 = paramArrayOfString[(i + 1)];
            if (collator.compare(str1, "-providerarg") == 0)
            {
              if (paramArrayOfString.length == i + 2) {
                errorNeedArgument(str1);
              }
              str2 = paramArrayOfString[(i + 2)];
              i += 2;
            }
          }
          providers.add(Pair.of(localObject2, str2));
        }
        else if (collator.compare(str1, "-v") == 0)
        {
          verbose = true;
        }
        else if (collator.compare(str1, "-debug") == 0)
        {
          debug = true;
        }
        else if (collator.compare(str1, "-rfc") == 0)
        {
          rfc = true;
        }
        else if (collator.compare(str1, "-noprompt") == 0)
        {
          noprompt = true;
        }
        else if (collator.compare(str1, "-trustcacerts") == 0)
        {
          trustcacerts = true;
        }
        else if ((collator.compare(str1, "-protected") == 0) || (collator.compare(str1, "-destprotected") == 0))
        {
          protectedPath = true;
        }
        else if (collator.compare(str1, "-srcprotected") == 0)
        {
          srcprotectedPath = true;
        }
        else
        {
          System.err.println(rb.getString("Illegal.option.") + str1);
          tinyHelp();
        }
      }
    }
    if (i < paramArrayOfString.length)
    {
      System.err.println(rb.getString("Illegal.option.") + paramArrayOfString[i]);
      tinyHelp();
    }
    if (command == null)
    {
      if (j != 0)
      {
        usage();
      }
      else
      {
        System.err.println(rb.getString("Usage.error.no.command.provided"));
        tinyHelp();
      }
    }
    else if (j != 0)
    {
      usage();
      command = null;
    }
  }
  
  boolean isKeyStoreRelated(Command paramCommand)
  {
    return (paramCommand != Command.PRINTCERT) && (paramCommand != Command.PRINTCERTREQ);
  }
  
  void doCommands(PrintStream paramPrintStream)
    throws Exception
  {
    if (storetype == null) {
      storetype = KeyStore.getDefaultType();
    }
    storetype = KeyStoreUtil.niceStoreTypeName(storetype);
    if (srcstoretype == null) {
      srcstoretype = KeyStore.getDefaultType();
    }
    srcstoretype = KeyStoreUtil.niceStoreTypeName(srcstoretype);
    if (("PKCS11".equalsIgnoreCase(storetype)) || (KeyStoreUtil.isWindowsKeyStore(storetype)))
    {
      token = true;
      if (ksfname == null) {
        ksfname = "NONE";
      }
    }
    if ("NONE".equals(ksfname)) {
      nullStream = true;
    }
    if ((token) && (!nullStream))
    {
      System.err.println(MessageFormat.format(rb.getString(".keystore.must.be.NONE.if.storetype.is.{0}"), new Object[] { storetype }));
      System.err.println();
      tinyHelp();
    }
    if ((token) && ((command == Command.KEYPASSWD) || (command == Command.STOREPASSWD))) {
      throw new UnsupportedOperationException(MessageFormat.format(rb.getString(".storepasswd.and.keypasswd.commands.not.supported.if.storetype.is.{0}"), new Object[] { storetype }));
    }
    if (("PKCS12".equalsIgnoreCase(storetype)) && (command == Command.KEYPASSWD)) {
      throw new UnsupportedOperationException(rb.getString(".keypasswd.commands.not.supported.if.storetype.is.PKCS12"));
    }
    if ((token) && ((keyPass != null) || (newPass != null) || (destKeyPass != null))) {
      throw new IllegalArgumentException(MessageFormat.format(rb.getString(".keypass.and.new.can.not.be.specified.if.storetype.is.{0}"), new Object[] { storetype }));
    }
    if ((protectedPath) && ((storePass != null) || (keyPass != null) || (newPass != null) || (destKeyPass != null))) {
      throw new IllegalArgumentException(rb.getString("if.protected.is.specified.then.storepass.keypass.and.new.must.not.be.specified"));
    }
    if ((srcprotectedPath) && ((srcstorePass != null) || (srckeyPass != null))) {
      throw new IllegalArgumentException(rb.getString("if.srcprotected.is.specified.then.srcstorepass.and.srckeypass.must.not.be.specified"));
    }
    if ((KeyStoreUtil.isWindowsKeyStore(storetype)) && ((storePass != null) || (keyPass != null) || (newPass != null) || (destKeyPass != null))) {
      throw new IllegalArgumentException(rb.getString("if.keystore.is.not.password.protected.then.storepass.keypass.and.new.must.not.be.specified"));
    }
    if ((KeyStoreUtil.isWindowsKeyStore(srcstoretype)) && ((srcstorePass != null) || (srckeyPass != null))) {
      throw new IllegalArgumentException(rb.getString("if.source.keystore.is.not.password.protected.then.srcstorepass.and.srckeypass.must.not.be.specified"));
    }
    if (validity <= 0L) {
      throw new Exception(rb.getString("Validity.must.be.greater.than.zero"));
    }
    Object localObject4;
    Object localObject5;
    if (providers != null)
    {
      localObject1 = null;
      if (pathlist != null)
      {
        localObject2 = null;
        localObject2 = PathList.appendPath((String)localObject2, System.getProperty("java.class.path"));
        localObject2 = PathList.appendPath((String)localObject2, System.getProperty("env.class.path"));
        localObject2 = PathList.appendPath((String)localObject2, pathlist);
        localObject4 = PathList.pathToURLs((String)localObject2);
        localObject1 = new URLClassLoader((URL[])localObject4);
      }
      else
      {
        localObject1 = ClassLoader.getSystemClassLoader();
      }
      Object localObject2 = providers.iterator();
      while (((Iterator)localObject2).hasNext())
      {
        localObject4 = (Pair)((Iterator)localObject2).next();
        String str1 = (String)fst;
        if (localObject1 != null) {
          localObject5 = ((ClassLoader)localObject1).loadClass(str1);
        } else {
          localObject5 = Class.forName(str1);
        }
        String str3 = (String)snd;
        Object localObject6;
        Object localObject7;
        if (str3 == null)
        {
          localObject6 = ((Class)localObject5).newInstance();
        }
        else
        {
          localObject7 = ((Class)localObject5).getConstructor(PARAM_STRING);
          localObject6 = ((Constructor)localObject7).newInstance(new Object[] { str3 });
        }
        if (!(localObject6 instanceof Provider))
        {
          localObject7 = new MessageFormat(rb.getString("provName.not.a.provider"));
          Object[] arrayOfObject = { str1 };
          throw new Exception(((MessageFormat)localObject7).format(arrayOfObject));
        }
        Security.addProvider((Provider)localObject6);
      }
    }
    if ((command == Command.LIST) && (verbose) && (rfc))
    {
      System.err.println(rb.getString("Must.not.specify.both.v.and.rfc.with.list.command"));
      tinyHelp();
    }
    if ((command == Command.GENKEYPAIR) && (keyPass != null) && (keyPass.length < 6)) {
      throw new Exception(rb.getString("Key.password.must.be.at.least.6.characters"));
    }
    if ((newPass != null) && (newPass.length < 6)) {
      throw new Exception(rb.getString("New.password.must.be.at.least.6.characters"));
    }
    if ((destKeyPass != null) && (destKeyPass.length < 6)) {
      throw new Exception(rb.getString("New.password.must.be.at.least.6.characters"));
    }
    if (ksfname == null) {
      ksfname = (System.getProperty("user.home") + File.separator + ".keystore");
    }
    Object localObject1 = null;
    if (command == Command.IMPORTKEYSTORE)
    {
      inplaceImport = inplaceImportCheck();
      if (inplaceImport)
      {
        localObject1 = loadSourceKeyStore();
        if (storePass == null) {
          storePass = srcstorePass;
        }
      }
    }
    if ((isKeyStoreRelated(command)) && (!nullStream) && (!inplaceImport)) {
      try
      {
        ksfile = new File(ksfname);
        if ((ksfile.exists()) && (ksfile.length() == 0L)) {
          throw new Exception(rb.getString("Keystore.file.exists.but.is.empty.") + ksfname);
        }
        ksStream = new FileInputStream(ksfile);
      }
      catch (FileNotFoundException localFileNotFoundException)
      {
        if ((command != Command.GENKEYPAIR) && (command != Command.GENSECKEY) && (command != Command.IDENTITYDB) && (command != Command.IMPORTCERT) && (command != Command.IMPORTPASS) && (command != Command.IMPORTKEYSTORE) && (command != Command.PRINTCRL)) {
          throw new Exception(rb.getString("Keystore.file.does.not.exist.") + ksfname);
        }
      }
    }
    if (((command == Command.KEYCLONE) || (command == Command.CHANGEALIAS)) && (dest == null))
    {
      dest = getAlias("destination");
      if ("".equals(dest)) {
        throw new Exception(rb.getString("Must.specify.destination.alias"));
      }
    }
    if ((command == Command.DELETE) && (alias == null))
    {
      alias = getAlias(null);
      if ("".equals(alias)) {
        throw new Exception(rb.getString("Must.specify.alias"));
      }
    }
    if (providerName == null) {
      keyStore = KeyStore.getInstance(storetype);
    } else {
      keyStore = KeyStore.getInstance(storetype, providerName);
    }
    if (!nullStream)
    {
      if (inplaceImport) {
        keyStore.load(null, storePass);
      } else {
        keyStore.load(ksStream, storePass);
      }
      if (ksStream != null) {
        ksStream.close();
      }
    }
    if ((nullStream) && (storePass != null))
    {
      keyStore.load(null, storePass);
    }
    else if ((!nullStream) && (storePass != null))
    {
      if ((ksStream == null) && (storePass.length < 6)) {
        throw new Exception(rb.getString("Keystore.password.must.be.at.least.6.characters"));
      }
    }
    else if (storePass == null)
    {
      if ((!protectedPath) && (!KeyStoreUtil.isWindowsKeyStore(storetype)) && ((command == Command.CERTREQ) || (command == Command.DELETE) || (command == Command.GENKEYPAIR) || (command == Command.GENSECKEY) || (command == Command.IMPORTCERT) || (command == Command.IMPORTPASS) || (command == Command.IMPORTKEYSTORE) || (command == Command.KEYCLONE) || (command == Command.CHANGEALIAS) || (command == Command.SELFCERT) || (command == Command.STOREPASSWD) || (command == Command.KEYPASSWD) || (command == Command.IDENTITYDB)))
      {
        int i = 0;
        do
        {
          if (command == Command.IMPORTKEYSTORE) {
            System.err.print(rb.getString("Enter.destination.keystore.password."));
          } else {
            System.err.print(rb.getString("Enter.keystore.password."));
          }
          System.err.flush();
          storePass = Password.readPassword(System.in);
          passwords.add(storePass);
          if ((!nullStream) && ((storePass == null) || (storePass.length < 6)))
          {
            System.err.println(rb.getString("Keystore.password.is.too.short.must.be.at.least.6.characters"));
            storePass = null;
          }
          if ((storePass != null) && (!nullStream) && (ksStream == null))
          {
            System.err.print(rb.getString("Re.enter.new.password."));
            localObject4 = Password.readPassword(System.in);
            passwords.add(localObject4);
            if (!Arrays.equals(storePass, (char[])localObject4))
            {
              System.err.println(rb.getString("They.don.t.match.Try.again"));
              storePass = null;
            }
          }
          i++;
        } while ((storePass == null) && (i < 3));
        if (storePass == null)
        {
          System.err.println(rb.getString("Too.many.failures.try.later"));
          return;
        }
      }
      else if ((!protectedPath) && (!KeyStoreUtil.isWindowsKeyStore(storetype)) && (isKeyStoreRelated(command)) && (command != Command.PRINTCRL))
      {
        System.err.print(rb.getString("Enter.keystore.password."));
        System.err.flush();
        storePass = Password.readPassword(System.in);
        passwords.add(storePass);
      }
      if (nullStream)
      {
        keyStore.load(null, storePass);
      }
      else if (ksStream != null)
      {
        ksStream = new FileInputStream(ksfile);
        keyStore.load(ksStream, storePass);
        ksStream.close();
      }
    }
    Object localObject3;
    if ((storePass != null) && ("PKCS12".equalsIgnoreCase(storetype)))
    {
      localObject3 = new MessageFormat(rb.getString("Warning.Different.store.and.key.passwords.not.supported.for.PKCS12.KeyStores.Ignoring.user.specified.command.value."));
      if ((keyPass != null) && (!Arrays.equals(storePass, keyPass)))
      {
        localObject4 = new Object[] { "-keypass" };
        System.err.println(((MessageFormat)localObject3).format(localObject4));
        keyPass = storePass;
      }
      if ((newPass != null) && (!Arrays.equals(storePass, newPass)))
      {
        localObject4 = new Object[] { "-new" };
        System.err.println(((MessageFormat)localObject3).format(localObject4));
        newPass = storePass;
      }
      if ((destKeyPass != null) && (!Arrays.equals(storePass, destKeyPass)))
      {
        localObject4 = new Object[] { "-destkeypass" };
        System.err.println(((MessageFormat)localObject3).format(localObject4));
        destKeyPass = storePass;
      }
    }
    if ((command == Command.PRINTCERT) || (command == Command.IMPORTCERT) || (command == Command.IDENTITYDB) || (command == Command.PRINTCRL)) {
      cf = CertificateFactory.getInstance("X509");
    }
    if (command != Command.IMPORTCERT) {
      trustcacerts = false;
    }
    if (trustcacerts) {
      caks = KeyStoreUtil.getCacertsKeyStore();
    }
    if (command == Command.CERTREQ)
    {
      if (filename != null)
      {
        localObject3 = new PrintStream(new FileOutputStream(filename));
        localObject4 = null;
        try
        {
          doCertReq(alias, sigAlgName, (PrintStream)localObject3);
        }
        catch (Throwable localThrowable2)
        {
          localObject4 = localThrowable2;
          throw localThrowable2;
        }
        finally
        {
          if (localObject3 != null) {
            if (localObject4 != null) {
              try
              {
                ((PrintStream)localObject3).close();
              }
              catch (Throwable localThrowable13)
              {
                ((Throwable)localObject4).addSuppressed(localThrowable13);
              }
            } else {
              ((PrintStream)localObject3).close();
            }
          }
        }
      }
      else
      {
        doCertReq(alias, sigAlgName, paramPrintStream);
      }
      if ((verbose) && (filename != null))
      {
        localObject3 = new MessageFormat(rb.getString("Certification.request.stored.in.file.filename."));
        localObject4 = new Object[] { filename };
        System.err.println(((MessageFormat)localObject3).format(localObject4));
        System.err.println(rb.getString("Submit.this.to.your.CA"));
      }
    }
    else if (command == Command.DELETE)
    {
      doDeleteEntry(alias);
      kssave = true;
    }
    else if (command == Command.EXPORTCERT)
    {
      if (filename != null)
      {
        localObject3 = new PrintStream(new FileOutputStream(filename));
        localObject4 = null;
        try
        {
          doExportCert(alias, (PrintStream)localObject3);
        }
        catch (Throwable localThrowable4)
        {
          localObject4 = localThrowable4;
          throw localThrowable4;
        }
        finally
        {
          if (localObject3 != null) {
            if (localObject4 != null) {
              try
              {
                ((PrintStream)localObject3).close();
              }
              catch (Throwable localThrowable14)
              {
                ((Throwable)localObject4).addSuppressed(localThrowable14);
              }
            } else {
              ((PrintStream)localObject3).close();
            }
          }
        }
      }
      else
      {
        doExportCert(alias, paramPrintStream);
      }
      if (filename != null)
      {
        localObject3 = new MessageFormat(rb.getString("Certificate.stored.in.file.filename."));
        localObject4 = new Object[] { filename };
        System.err.println(((MessageFormat)localObject3).format(localObject4));
      }
    }
    else if (command == Command.GENKEYPAIR)
    {
      if (keyAlgName == null) {
        keyAlgName = "DSA";
      }
      doGenKeyPair(alias, dname, keyAlgName, keysize, sigAlgName);
      kssave = true;
    }
    else if (command == Command.GENSECKEY)
    {
      if (keyAlgName == null) {
        keyAlgName = "DES";
      }
      doGenSecretKey(alias, keyAlgName, keysize);
      kssave = true;
    }
    else if (command == Command.IMPORTPASS)
    {
      if (keyAlgName == null) {
        keyAlgName = "PBE";
      }
      doGenSecretKey(alias, keyAlgName, keysize);
      kssave = true;
    }
    else if (command == Command.IDENTITYDB)
    {
      if (filename != null)
      {
        localObject3 = new FileInputStream(filename);
        localObject4 = null;
        try
        {
          doImportIdentityDatabase((InputStream)localObject3);
        }
        catch (Throwable localThrowable6)
        {
          localObject4 = localThrowable6;
          throw localThrowable6;
        }
        finally
        {
          if (localObject3 != null) {
            if (localObject4 != null) {
              try
              {
                ((InputStream)localObject3).close();
              }
              catch (Throwable localThrowable15)
              {
                ((Throwable)localObject4).addSuppressed(localThrowable15);
              }
            } else {
              ((InputStream)localObject3).close();
            }
          }
        }
      }
      else
      {
        doImportIdentityDatabase(System.in);
      }
    }
    else if (command == Command.IMPORTCERT)
    {
      localObject3 = System.in;
      if (filename != null) {
        localObject3 = new FileInputStream(filename);
      }
      localObject4 = alias != null ? alias : "mykey";
      try
      {
        if (keyStore.entryInstanceOf((String)localObject4, KeyStore.PrivateKeyEntry.class))
        {
          kssave = installReply((String)localObject4, (InputStream)localObject3);
          if (kssave) {
            System.err.println(rb.getString("Certificate.reply.was.installed.in.keystore"));
          } else {
            System.err.println(rb.getString("Certificate.reply.was.not.installed.in.keystore"));
          }
        }
        else if ((!keyStore.containsAlias((String)localObject4)) || (keyStore.entryInstanceOf((String)localObject4, KeyStore.TrustedCertificateEntry.class)))
        {
          kssave = addTrustedCert((String)localObject4, (InputStream)localObject3);
          if (kssave) {
            System.err.println(rb.getString("Certificate.was.added.to.keystore"));
          } else {
            System.err.println(rb.getString("Certificate.was.not.added.to.keystore"));
          }
        }
      }
      finally
      {
        if (localObject3 != System.in) {
          ((InputStream)localObject3).close();
        }
      }
    }
    else if (command == Command.IMPORTKEYSTORE)
    {
      if (localObject1 == null) {
        localObject1 = loadSourceKeyStore();
      }
      doImportKeyStore((KeyStore)localObject1);
      kssave = true;
    }
    else if (command == Command.KEYCLONE)
    {
      keyPassNew = newPass;
      if (alias == null) {
        alias = "mykey";
      }
      if (!keyStore.containsAlias(alias))
      {
        localObject3 = new MessageFormat(rb.getString("Alias.alias.does.not.exist"));
        localObject4 = new Object[] { alias };
        throw new Exception(((MessageFormat)localObject3).format(localObject4));
      }
      if (!keyStore.entryInstanceOf(alias, KeyStore.PrivateKeyEntry.class))
      {
        localObject3 = new MessageFormat(rb.getString("Alias.alias.references.an.entry.type.that.is.not.a.private.key.entry.The.keyclone.command.only.supports.cloning.of.private.key"));
        localObject4 = new Object[] { alias };
        throw new Exception(((MessageFormat)localObject3).format(localObject4));
      }
      doCloneEntry(alias, dest, true);
      kssave = true;
    }
    else if (command == Command.CHANGEALIAS)
    {
      if (alias == null) {
        alias = "mykey";
      }
      doCloneEntry(alias, dest, false);
      if (keyStore.containsAlias(alias)) {
        doDeleteEntry(alias);
      }
      kssave = true;
    }
    else if (command == Command.KEYPASSWD)
    {
      keyPassNew = newPass;
      doChangeKeyPasswd(alias);
      kssave = true;
    }
    else if (command == Command.LIST)
    {
      if ((storePass == null) && (!KeyStoreUtil.isWindowsKeyStore(storetype))) {
        printNoIntegrityWarning();
      }
      if (alias != null) {
        doPrintEntry(rb.getString("the.certificate"), alias, paramPrintStream);
      } else {
        doPrintEntries(paramPrintStream);
      }
    }
    else if (command == Command.PRINTCERT)
    {
      doPrintCert(paramPrintStream);
    }
    else if (command == Command.SELFCERT)
    {
      doSelfCert(alias, dname, sigAlgName);
      kssave = true;
    }
    else if (command == Command.STOREPASSWD)
    {
      storePassNew = newPass;
      if (storePassNew == null) {
        storePassNew = getNewPasswd("keystore password", storePass);
      }
      kssave = true;
    }
    else if (command == Command.GENCERT)
    {
      if (alias == null) {
        alias = "mykey";
      }
      localObject3 = System.in;
      if (infilename != null) {
        localObject3 = new FileInputStream(infilename);
      }
      localObject4 = null;
      if (outfilename != null)
      {
        localObject4 = new PrintStream(new FileOutputStream(outfilename));
        paramPrintStream = (PrintStream)localObject4;
      }
      try
      {
        doGenCert(alias, sigAlgName, (InputStream)localObject3, paramPrintStream);
      }
      finally
      {
        if (localObject3 != System.in) {
          ((InputStream)localObject3).close();
        }
        if (localObject4 != null) {
          ((PrintStream)localObject4).close();
        }
      }
    }
    else if (command == Command.GENCRL)
    {
      if (alias == null) {
        alias = "mykey";
      }
      if (filename != null)
      {
        localObject3 = new PrintStream(new FileOutputStream(filename));
        localObject4 = null;
        try
        {
          doGenCRL((PrintStream)localObject3);
        }
        catch (Throwable localThrowable8)
        {
          localObject4 = localThrowable8;
          throw localThrowable8;
        }
        finally
        {
          if (localObject3 != null) {
            if (localObject4 != null) {
              try
              {
                ((PrintStream)localObject3).close();
              }
              catch (Throwable localThrowable16)
              {
                ((Throwable)localObject4).addSuppressed(localThrowable16);
              }
            } else {
              ((PrintStream)localObject3).close();
            }
          }
        }
      }
      else
      {
        doGenCRL(paramPrintStream);
      }
    }
    else if (command == Command.PRINTCERTREQ)
    {
      if (filename != null)
      {
        localObject3 = new FileInputStream(filename);
        localObject4 = null;
        try
        {
          doPrintCertReq((InputStream)localObject3, paramPrintStream);
        }
        catch (Throwable localThrowable10)
        {
          localObject4 = localThrowable10;
          throw localThrowable10;
        }
        finally
        {
          if (localObject3 != null) {
            if (localObject4 != null) {
              try
              {
                ((InputStream)localObject3).close();
              }
              catch (Throwable localThrowable17)
              {
                ((Throwable)localObject4).addSuppressed(localThrowable17);
              }
            } else {
              ((InputStream)localObject3).close();
            }
          }
        }
      }
      else
      {
        doPrintCertReq(System.in, paramPrintStream);
      }
    }
    else if (command == Command.PRINTCRL)
    {
      doPrintCRL(filename, paramPrintStream);
    }
    if (kssave)
    {
      if (verbose)
      {
        localObject3 = new MessageFormat(rb.getString(".Storing.ksfname."));
        localObject4 = new Object[] { nullStream ? "keystore" : ksfname };
        System.err.println(((MessageFormat)localObject3).format(localObject4));
      }
      if (token)
      {
        keyStore.store(null, null);
      }
      else
      {
        localObject3 = storePassNew != null ? storePassNew : storePass;
        if (nullStream)
        {
          keyStore.store(null, (char[])localObject3);
        }
        else
        {
          localObject4 = new ByteArrayOutputStream();
          keyStore.store((OutputStream)localObject4, (char[])localObject3);
          FileOutputStream localFileOutputStream = new FileOutputStream(ksfname);
          localObject5 = null;
          try
          {
            localFileOutputStream.write(((ByteArrayOutputStream)localObject4).toByteArray());
          }
          catch (Throwable localThrowable12)
          {
            localObject5 = localThrowable12;
            throw localThrowable12;
          }
          finally
          {
            if (localFileOutputStream != null) {
              if (localObject5 != null) {
                try
                {
                  localFileOutputStream.close();
                }
                catch (Throwable localThrowable18)
                {
                  ((Throwable)localObject5).addSuppressed(localThrowable18);
                }
              } else {
                localFileOutputStream.close();
              }
            }
          }
        }
      }
    }
    if ((isKeyStoreRelated(command)) && (!token) && (!nullStream) && (ksfname != null))
    {
      localObject3 = new File(ksfname);
      if (((File)localObject3).exists())
      {
        localObject4 = keyStoreType((File)localObject3);
        if ((((String)localObject4).equalsIgnoreCase("JKS")) || (((String)localObject4).equalsIgnoreCase("JCEKS")))
        {
          int j = 1;
          localObject5 = Collections.list(keyStore.aliases()).iterator();
          while (((Iterator)localObject5).hasNext())
          {
            String str4 = (String)((Iterator)localObject5).next();
            if (!keyStore.entryInstanceOf(str4, KeyStore.TrustedCertificateEntry.class))
            {
              j = 0;
              break;
            }
          }
          if (j == 0) {
            weakWarnings.add(String.format(rb.getString("jks.storetype.warning"), new Object[] { localObject4, ksfname }));
          }
        }
        if (inplaceImport)
        {
          String str2 = keyStoreType(new File(inplaceBackupName));
          localObject5 = ((String)localObject4).equalsIgnoreCase(str2) ? rb.getString("backup.keystore.warning") : rb.getString("migrate.keystore.warning");
          weakWarnings.add(String.format((String)localObject5, new Object[] { srcksfname, str2, inplaceBackupName, localObject4 }));
        }
      }
    }
  }
  
  private String keyStoreType(File paramFile)
    throws IOException
  {
    int i = -17957139;
    int j = -825307442;
    DataInputStream localDataInputStream = new DataInputStream(new FileInputStream(paramFile));
    Object localObject1 = null;
    try
    {
      int k = localDataInputStream.readInt();
      if (k == i)
      {
        str = "JKS";
        return str;
      }
      if (k == j)
      {
        str = "JCEKS";
        return str;
      }
      String str = "Non JKS/JCEKS";
      return str;
    }
    catch (Throwable localThrowable1)
    {
      localObject1 = localThrowable1;
      throw localThrowable1;
    }
    finally
    {
      if (localDataInputStream != null) {
        if (localObject1 != null) {
          try
          {
            localDataInputStream.close();
          }
          catch (Throwable localThrowable5)
          {
            ((Throwable)localObject1).addSuppressed(localThrowable5);
          }
        } else {
          localDataInputStream.close();
        }
      }
    }
  }
  
  private void doGenCert(String paramString1, String paramString2, InputStream paramInputStream, PrintStream paramPrintStream)
    throws Exception
  {
    if (!keyStore.containsAlias(paramString1))
    {
      localObject1 = new MessageFormat(rb.getString("Alias.alias.does.not.exist"));
      localObject2 = new Object[] { paramString1 };
      throw new Exception(((MessageFormat)localObject1).format(localObject2));
    }
    Object localObject1 = keyStore.getCertificate(paramString1);
    Object localObject2 = ((Certificate)localObject1).getEncoded();
    X509CertImpl localX509CertImpl1 = new X509CertImpl((byte[])localObject2);
    X509CertInfo localX509CertInfo1 = (X509CertInfo)localX509CertImpl1.get("x509.info");
    X500Name localX500Name = (X500Name)localX509CertInfo1.get("subject.dname");
    Date localDate1 = getStartDate(startDate);
    Date localDate2 = new Date();
    localDate2.setTime(localDate1.getTime() + validity * 1000L * 24L * 60L * 60L);
    CertificateValidity localCertificateValidity = new CertificateValidity(localDate1, localDate2);
    PrivateKey localPrivateKey = (PrivateKey)recoverKeystorePass, keyPass).fst;
    if (paramString2 == null) {
      paramString2 = getCompatibleSigAlgName(localPrivateKey.getAlgorithm());
    }
    Signature localSignature = Signature.getInstance(paramString2);
    localSignature.initSign(localPrivateKey);
    X509CertInfo localX509CertInfo2 = new X509CertInfo();
    localX509CertInfo2.set("validity", localCertificateValidity);
    localX509CertInfo2.set("serialNumber", new CertificateSerialNumber(new Random().nextInt() & 0x7FFFFFFF));
    localX509CertInfo2.set("version", new CertificateVersion(2));
    localX509CertInfo2.set("algorithmID", new CertificateAlgorithmId(AlgorithmId.get(paramString2)));
    localX509CertInfo2.set("issuer", localX500Name);
    BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(paramInputStream));
    int i = 0;
    StringBuffer localStringBuffer = new StringBuffer();
    for (;;)
    {
      localObject3 = localBufferedReader.readLine();
      if (localObject3 == null) {
        break;
      }
      if ((((String)localObject3).startsWith("-----BEGIN")) && (((String)localObject3).indexOf("REQUEST") >= 0))
      {
        i = 1;
      }
      else
      {
        if ((((String)localObject3).startsWith("-----END")) && (((String)localObject3).indexOf("REQUEST") >= 0)) {
          break;
        }
        if (i != 0) {
          localStringBuffer.append((String)localObject3);
        }
      }
    }
    Object localObject3 = Pem.decode(new String(localStringBuffer));
    PKCS10 localPKCS10 = new PKCS10((byte[])localObject3);
    checkWeak(rb.getString("the.certificate.request"), localPKCS10);
    localX509CertInfo2.set("key", new CertificateX509Key(localPKCS10.getSubjectPublicKeyInfo()));
    localX509CertInfo2.set("subject", dname == null ? localPKCS10.getSubjectName() : new X500Name(dname));
    CertificateExtensions localCertificateExtensions = null;
    Iterator localIterator = localPKCS10.getAttributes().getAttributes().iterator();
    while (localIterator.hasNext())
    {
      localObject4 = (PKCS10Attribute)localIterator.next();
      if (((PKCS10Attribute)localObject4).getAttributeId().equals(PKCS9Attribute.EXTENSION_REQUEST_OID)) {
        localCertificateExtensions = (CertificateExtensions)((PKCS10Attribute)localObject4).getAttributeValue();
      }
    }
    Object localObject4 = createV3Extensions(localCertificateExtensions, null, v3ext, localPKCS10.getSubjectPublicKeyInfo(), ((Certificate)localObject1).getPublicKey());
    localX509CertInfo2.set("extensions", localObject4);
    X509CertImpl localX509CertImpl2 = new X509CertImpl(localX509CertInfo2);
    localX509CertImpl2.sign(localPrivateKey, paramString2);
    dumpCert(localX509CertImpl2, paramPrintStream);
    for (Certificate localCertificate : keyStore.getCertificateChain(paramString1)) {
      if ((localCertificate instanceof X509Certificate))
      {
        X509Certificate localX509Certificate = (X509Certificate)localCertificate;
        if (!isSelfSigned(localX509Certificate)) {
          dumpCert(localX509Certificate, paramPrintStream);
        }
      }
    }
    checkWeak(rb.getString("the.issuer"), keyStore.getCertificateChain(paramString1));
    checkWeak(rb.getString("the.generated.certificate"), localX509CertImpl2);
  }
  
  private void doGenCRL(PrintStream paramPrintStream)
    throws Exception
  {
    if (ids == null) {
      throw new Exception("Must provide -id when -gencrl");
    }
    Certificate localCertificate = keyStore.getCertificate(alias);
    byte[] arrayOfByte = localCertificate.getEncoded();
    X509CertImpl localX509CertImpl = new X509CertImpl(arrayOfByte);
    X509CertInfo localX509CertInfo = (X509CertInfo)localX509CertImpl.get("x509.info");
    X500Name localX500Name = (X500Name)localX509CertInfo.get("subject.dname");
    Date localDate1 = getStartDate(startDate);
    Date localDate2 = (Date)localDate1.clone();
    localDate2.setTime(localDate2.getTime() + validity * 1000L * 24L * 60L * 60L);
    CertificateValidity localCertificateValidity = new CertificateValidity(localDate1, localDate2);
    PrivateKey localPrivateKey = (PrivateKey)recoverKeyalias, storePass, keyPass).fst;
    if (sigAlgName == null) {
      sigAlgName = getCompatibleSigAlgName(localPrivateKey.getAlgorithm());
    }
    X509CRLEntry[] arrayOfX509CRLEntry = new X509CRLEntry[ids.size()];
    for (int i = 0; i < ids.size(); i++)
    {
      String str = (String)ids.get(i);
      int j = str.indexOf(':');
      if (j >= 0)
      {
        CRLExtensions localCRLExtensions = new CRLExtensions();
        localCRLExtensions.set("Reason", new CRLReasonCodeExtension(Integer.parseInt(str.substring(j + 1))));
        arrayOfX509CRLEntry[i] = new X509CRLEntryImpl(new BigInteger(str.substring(0, j)), localDate1, localCRLExtensions);
      }
      else
      {
        arrayOfX509CRLEntry[i] = new X509CRLEntryImpl(new BigInteger((String)ids.get(i)), localDate1);
      }
    }
    X509CRLImpl localX509CRLImpl = new X509CRLImpl(localX500Name, localDate1, localDate2, arrayOfX509CRLEntry);
    localX509CRLImpl.sign(localPrivateKey, sigAlgName);
    if (rfc)
    {
      paramPrintStream.println("-----BEGIN X509 CRL-----");
      paramPrintStream.println(Base64.getMimeEncoder(64, CRLF).encodeToString(localX509CRLImpl.getEncodedInternal()));
      paramPrintStream.println("-----END X509 CRL-----");
    }
    else
    {
      paramPrintStream.write(localX509CRLImpl.getEncodedInternal());
    }
    checkWeak(rb.getString("the.generated.crl"), localX509CRLImpl, localPrivateKey);
  }
  
  private void doCertReq(String paramString1, String paramString2, PrintStream paramPrintStream)
    throws Exception
  {
    if (paramString1 == null) {
      paramString1 = "mykey";
    }
    Pair localPair = recoverKey(paramString1, storePass, keyPass);
    PrivateKey localPrivateKey = (PrivateKey)fst;
    if (keyPass == null) {
      keyPass = ((char[])snd);
    }
    Certificate localCertificate = keyStore.getCertificate(paramString1);
    if (localCertificate == null)
    {
      localObject1 = new MessageFormat(rb.getString("alias.has.no.public.key.certificate."));
      localObject2 = new Object[] { paramString1 };
      throw new Exception(((MessageFormat)localObject1).format(localObject2));
    }
    Object localObject1 = new PKCS10(localCertificate.getPublicKey());
    Object localObject2 = createV3Extensions(null, null, v3ext, localCertificate.getPublicKey(), null);
    ((PKCS10)localObject1).getAttributes().setAttribute("extensions", new PKCS10Attribute(PKCS9Attribute.EXTENSION_REQUEST_OID, localObject2));
    if (paramString2 == null) {
      paramString2 = getCompatibleSigAlgName(localPrivateKey.getAlgorithm());
    }
    Signature localSignature = Signature.getInstance(paramString2);
    localSignature.initSign(localPrivateKey);
    X500Name localX500Name = dname == null ? new X500Name(((X509Certificate)localCertificate).getSubjectDN().toString()) : new X500Name(dname);
    ((PKCS10)localObject1).encodeAndSign(localX500Name, localSignature);
    ((PKCS10)localObject1).print(paramPrintStream);
    checkWeak(rb.getString("the.generated.certificate.request"), (PKCS10)localObject1);
  }
  
  private void doDeleteEntry(String paramString)
    throws Exception
  {
    if (!keyStore.containsAlias(paramString))
    {
      MessageFormat localMessageFormat = new MessageFormat(rb.getString("Alias.alias.does.not.exist"));
      Object[] arrayOfObject = { paramString };
      throw new Exception(localMessageFormat.format(arrayOfObject));
    }
    keyStore.deleteEntry(paramString);
  }
  
  private void doExportCert(String paramString, PrintStream paramPrintStream)
    throws Exception
  {
    if ((storePass == null) && (!KeyStoreUtil.isWindowsKeyStore(storetype))) {
      printNoIntegrityWarning();
    }
    if (paramString == null) {
      paramString = "mykey";
    }
    Object localObject2;
    if (!keyStore.containsAlias(paramString))
    {
      localObject1 = new MessageFormat(rb.getString("Alias.alias.does.not.exist"));
      localObject2 = new Object[] { paramString };
      throw new Exception(((MessageFormat)localObject1).format(localObject2));
    }
    Object localObject1 = (X509Certificate)keyStore.getCertificate(paramString);
    if (localObject1 == null)
    {
      localObject2 = new MessageFormat(rb.getString("Alias.alias.has.no.certificate"));
      Object[] arrayOfObject = { paramString };
      throw new Exception(((MessageFormat)localObject2).format(arrayOfObject));
    }
    dumpCert((Certificate)localObject1, paramPrintStream);
    checkWeak(rb.getString("the.certificate"), (Certificate)localObject1);
  }
  
  private char[] promptForKeyPass(String paramString1, String paramString2, char[] paramArrayOfChar)
    throws Exception
  {
    if ("PKCS12".equalsIgnoreCase(storetype)) {
      return paramArrayOfChar;
    }
    if ((!token) && (!protectedPath))
    {
      for (int i = 0; i < 3; i++)
      {
        MessageFormat localMessageFormat = new MessageFormat(rb.getString("Enter.key.password.for.alias."));
        Object[] arrayOfObject = { paramString1 };
        System.err.println(localMessageFormat.format(arrayOfObject));
        if (paramString2 == null)
        {
          System.err.print(rb.getString(".RETURN.if.same.as.keystore.password."));
        }
        else
        {
          localMessageFormat = new MessageFormat(rb.getString(".RETURN.if.same.as.for.otherAlias."));
          localObject = new Object[] { paramString2 };
          System.err.print(localMessageFormat.format(localObject));
        }
        System.err.flush();
        Object localObject = Password.readPassword(System.in);
        passwords.add(localObject);
        if (localObject == null) {
          return paramArrayOfChar;
        }
        if (localObject.length >= 6)
        {
          System.err.print(rb.getString("Re.enter.new.password."));
          char[] arrayOfChar = Password.readPassword(System.in);
          passwords.add(arrayOfChar);
          if (!Arrays.equals((char[])localObject, arrayOfChar)) {
            System.err.println(rb.getString("They.don.t.match.Try.again"));
          } else {
            return (char[])localObject;
          }
        }
        else
        {
          System.err.println(rb.getString("Key.password.is.too.short.must.be.at.least.6.characters"));
        }
      }
      if (i == 3)
      {
        if (command == Command.KEYCLONE) {
          throw new Exception(rb.getString("Too.many.failures.Key.entry.not.cloned"));
        }
        throw new Exception(rb.getString("Too.many.failures.key.not.added.to.keystore"));
      }
    }
    return null;
  }
  
  private char[] promptForCredential()
    throws Exception
  {
    if (System.console() == null)
    {
      char[] arrayOfChar1 = Password.readPassword(System.in);
      passwords.add(arrayOfChar1);
      return arrayOfChar1;
    }
    for (int i = 0; i < 3; i++)
    {
      System.err.print(rb.getString("Enter.the.password.to.be.stored."));
      System.err.flush();
      char[] arrayOfChar2 = Password.readPassword(System.in);
      passwords.add(arrayOfChar2);
      System.err.print(rb.getString("Re.enter.password."));
      char[] arrayOfChar3 = Password.readPassword(System.in);
      passwords.add(arrayOfChar3);
      if (!Arrays.equals(arrayOfChar2, arrayOfChar3)) {
        System.err.println(rb.getString("They.don.t.match.Try.again"));
      } else {
        return arrayOfChar2;
      }
    }
    if (i == 3) {
      throw new Exception(rb.getString("Too.many.failures.key.not.added.to.keystore"));
    }
    return null;
  }
  
  private void doGenSecretKey(String paramString1, String paramString2, int paramInt)
    throws Exception
  {
    if (paramString1 == null) {
      paramString1 = "mykey";
    }
    if (keyStore.containsAlias(paramString1))
    {
      MessageFormat localMessageFormat1 = new MessageFormat(rb.getString("Secret.key.not.generated.alias.alias.already.exists"));
      localObject1 = new Object[] { paramString1 };
      throw new Exception(localMessageFormat1.format(localObject1));
    }
    int i = 1;
    Object localObject1 = null;
    Object localObject2;
    MessageFormat localMessageFormat2;
    Object[] arrayOfObject;
    if (paramString2.toUpperCase(Locale.ENGLISH).startsWith("PBE"))
    {
      localObject2 = SecretKeyFactory.getInstance("PBE");
      localObject1 = ((SecretKeyFactory)localObject2).generateSecret(new PBEKeySpec(promptForCredential()));
      if (!"PBE".equalsIgnoreCase(paramString2)) {
        i = 0;
      }
      if (verbose)
      {
        localMessageFormat2 = new MessageFormat(rb.getString("Generated.keyAlgName.secret.key"));
        arrayOfObject = new Object[] { i != 0 ? "PBE" : ((SecretKey)localObject1).getAlgorithm() };
        System.err.println(localMessageFormat2.format(arrayOfObject));
      }
    }
    else
    {
      localObject2 = KeyGenerator.getInstance(paramString2);
      if (paramInt == -1) {
        if ("DES".equalsIgnoreCase(paramString2)) {
          paramInt = 56;
        } else if ("DESede".equalsIgnoreCase(paramString2)) {
          paramInt = 168;
        } else {
          throw new Exception(rb.getString("Please.provide.keysize.for.secret.key.generation"));
        }
      }
      ((KeyGenerator)localObject2).init(paramInt);
      localObject1 = ((KeyGenerator)localObject2).generateKey();
      if (verbose)
      {
        localMessageFormat2 = new MessageFormat(rb.getString("Generated.keysize.bit.keyAlgName.secret.key"));
        arrayOfObject = new Object[] { new Integer(paramInt), ((SecretKey)localObject1).getAlgorithm() };
        System.err.println(localMessageFormat2.format(arrayOfObject));
      }
    }
    if (keyPass == null) {
      keyPass = promptForKeyPass(paramString1, null, storePass);
    }
    if (i != 0) {
      keyStore.setKeyEntry(paramString1, (Key)localObject1, keyPass, null);
    } else {
      keyStore.setEntry(paramString1, new KeyStore.SecretKeyEntry((SecretKey)localObject1), new KeyStore.PasswordProtection(keyPass, paramString2, null));
    }
  }
  
  private static String getCompatibleSigAlgName(String paramString)
    throws Exception
  {
    if ("DSA".equalsIgnoreCase(paramString)) {
      return "SHA256WithDSA";
    }
    if ("RSA".equalsIgnoreCase(paramString)) {
      return "SHA256WithRSA";
    }
    if ("EC".equalsIgnoreCase(paramString)) {
      return "SHA256withECDSA";
    }
    throw new Exception(rb.getString("Cannot.derive.signature.algorithm"));
  }
  
  private void doGenKeyPair(String paramString1, String paramString2, String paramString3, int paramInt, String paramString4)
    throws Exception
  {
    if (paramInt == -1) {
      if ("EC".equalsIgnoreCase(paramString3)) {
        paramInt = SecurityProviderConstants.DEF_EC_KEY_SIZE;
      } else if ("RSA".equalsIgnoreCase(paramString3)) {
        paramInt = 2048;
      } else if ("DSA".equalsIgnoreCase(paramString3)) {
        paramInt = 2048;
      }
    }
    if (paramString1 == null) {
      paramString1 = "mykey";
    }
    Object localObject2;
    if (keyStore.containsAlias(paramString1))
    {
      localObject1 = new MessageFormat(rb.getString("Key.pair.not.generated.alias.alias.already.exists"));
      localObject2 = new Object[] { paramString1 };
      throw new Exception(((MessageFormat)localObject1).format(localObject2));
    }
    if (paramString4 == null) {
      paramString4 = getCompatibleSigAlgName(paramString3);
    }
    Object localObject1 = new CertAndKeyGen(paramString3, paramString4, providerName);
    if (paramString2 == null) {
      localObject2 = getX500Name();
    } else {
      localObject2 = new X500Name(paramString2);
    }
    ((CertAndKeyGen)localObject1).generate(paramInt);
    PrivateKey localPrivateKey = ((CertAndKeyGen)localObject1).getPrivateKey();
    CertificateExtensions localCertificateExtensions = createV3Extensions(null, null, v3ext, ((CertAndKeyGen)localObject1).getPublicKeyAnyway(), null);
    X509Certificate[] arrayOfX509Certificate = new X509Certificate[1];
    arrayOfX509Certificate[0] = ((CertAndKeyGen)localObject1).getSelfCertificate((X500Name)localObject2, getStartDate(startDate), validity * 24L * 60L * 60L, localCertificateExtensions);
    if (verbose)
    {
      MessageFormat localMessageFormat = new MessageFormat(rb.getString("Generating.keysize.bit.keyAlgName.key.pair.and.self.signed.certificate.sigAlgName.with.a.validity.of.validality.days.for"));
      Object[] arrayOfObject = { new Integer(paramInt), localPrivateKey.getAlgorithm(), arrayOfX509Certificate[0].getSigAlgName(), new Long(validity), localObject2 };
      System.err.println(localMessageFormat.format(arrayOfObject));
    }
    if (keyPass == null) {
      keyPass = promptForKeyPass(paramString1, null, storePass);
    }
    checkWeak(rb.getString("the.generated.certificate"), arrayOfX509Certificate[0]);
    keyStore.setKeyEntry(paramString1, localPrivateKey, keyPass, arrayOfX509Certificate);
  }
  
  private void doCloneEntry(String paramString1, String paramString2, boolean paramBoolean)
    throws Exception
  {
    if (paramString1 == null) {
      paramString1 = "mykey";
    }
    if (keyStore.containsAlias(paramString2))
    {
      localObject1 = new MessageFormat(rb.getString("Destination.alias.dest.already.exists"));
      localObject2 = new Object[] { paramString2 };
      throw new Exception(((MessageFormat)localObject1).format(localObject2));
    }
    Object localObject1 = recoverEntry(keyStore, paramString1, storePass, keyPass);
    Object localObject2 = (KeyStore.Entry)fst;
    keyPass = ((char[])snd);
    KeyStore.PasswordProtection localPasswordProtection = null;
    if (keyPass != null)
    {
      if ((!paramBoolean) || ("PKCS12".equalsIgnoreCase(storetype))) {
        keyPassNew = keyPass;
      } else if (keyPassNew == null) {
        keyPassNew = promptForKeyPass(paramString2, paramString1, keyPass);
      }
      localPasswordProtection = new KeyStore.PasswordProtection(keyPassNew);
    }
    keyStore.setEntry(paramString2, (KeyStore.Entry)localObject2, localPasswordProtection);
  }
  
  private void doChangeKeyPasswd(String paramString)
    throws Exception
  {
    if (paramString == null) {
      paramString = "mykey";
    }
    Pair localPair = recoverKey(paramString, storePass, keyPass);
    Key localKey = (Key)fst;
    if (keyPass == null) {
      keyPass = ((char[])snd);
    }
    if (keyPassNew == null)
    {
      MessageFormat localMessageFormat = new MessageFormat(rb.getString("key.password.for.alias."));
      Object[] arrayOfObject = { paramString };
      keyPassNew = getNewPasswd(localMessageFormat.format(arrayOfObject), keyPass);
    }
    keyStore.setKeyEntry(paramString, localKey, keyPassNew, keyStore.getCertificateChain(paramString));
  }
  
  private void doImportIdentityDatabase(InputStream paramInputStream)
    throws Exception
  {
    System.err.println(rb.getString("No.entries.from.identity.database.added"));
  }
  
  private void doPrintEntry(String paramString1, String paramString2, PrintStream paramPrintStream)
    throws Exception
  {
    Object localObject1;
    Object[] arrayOfObject1;
    if (!keyStore.containsAlias(paramString2))
    {
      localObject1 = new MessageFormat(rb.getString("Alias.alias.does.not.exist"));
      arrayOfObject1 = new Object[] { paramString2 };
      throw new Exception(((MessageFormat)localObject1).format(arrayOfObject1));
    }
    Object localObject2;
    if ((verbose) || (rfc) || (debug))
    {
      localObject1 = new MessageFormat(rb.getString("Alias.name.alias"));
      arrayOfObject1 = new Object[] { paramString2 };
      paramPrintStream.println(((MessageFormat)localObject1).format(arrayOfObject1));
      if (!token)
      {
        localObject1 = new MessageFormat(rb.getString("Creation.date.keyStore.getCreationDate.alias."));
        localObject2 = new Object[] { keyStore.getCreationDate(paramString2) };
        paramPrintStream.println(((MessageFormat)localObject1).format(localObject2));
      }
    }
    else if (!token)
    {
      localObject1 = new MessageFormat(rb.getString("alias.keyStore.getCreationDate.alias."));
      arrayOfObject1 = new Object[] { paramString2, keyStore.getCreationDate(paramString2) };
      paramPrintStream.print(((MessageFormat)localObject1).format(arrayOfObject1));
    }
    else
    {
      localObject1 = new MessageFormat(rb.getString("alias."));
      arrayOfObject1 = new Object[] { paramString2 };
      paramPrintStream.print(((MessageFormat)localObject1).format(arrayOfObject1));
    }
    if (keyStore.entryInstanceOf(paramString2, KeyStore.SecretKeyEntry.class))
    {
      if ((verbose) || (rfc) || (debug))
      {
        localObject1 = new Object[] { "SecretKeyEntry" };
        paramPrintStream.println(new MessageFormat(rb.getString("Entry.type.type.")).format(localObject1));
      }
      else
      {
        paramPrintStream.println("SecretKeyEntry, ");
      }
    }
    else if (keyStore.entryInstanceOf(paramString2, KeyStore.PrivateKeyEntry.class))
    {
      if ((verbose) || (rfc) || (debug))
      {
        localObject1 = new Object[] { "PrivateKeyEntry" };
        paramPrintStream.println(new MessageFormat(rb.getString("Entry.type.type.")).format(localObject1));
      }
      else
      {
        paramPrintStream.println("PrivateKeyEntry, ");
      }
      localObject1 = keyStore.getCertificateChain(paramString2);
      if (localObject1 != null) {
        if ((verbose) || (rfc) || (debug))
        {
          paramPrintStream.println(rb.getString("Certificate.chain.length.") + localObject1.length);
          for (int i = 0; i < localObject1.length; i++)
          {
            localObject2 = new MessageFormat(rb.getString("Certificate.i.1."));
            Object[] arrayOfObject3 = { new Integer(i + 1) };
            paramPrintStream.println(((MessageFormat)localObject2).format(arrayOfObject3));
            if ((verbose) && ((localObject1[i] instanceof X509Certificate))) {
              printX509Cert((X509Certificate)localObject1[i], paramPrintStream);
            } else if (debug) {
              paramPrintStream.println(localObject1[i].toString());
            } else {
              dumpCert(localObject1[i], paramPrintStream);
            }
            checkWeak(paramString1, localObject1[i]);
          }
        }
        else
        {
          paramPrintStream.println(rb.getString("Certificate.fingerprint.SHA1.") + getCertFingerPrint("SHA1", localObject1[0]));
          checkWeak(paramString1, localObject1[0]);
        }
      }
    }
    else if (keyStore.entryInstanceOf(paramString2, KeyStore.TrustedCertificateEntry.class))
    {
      localObject1 = keyStore.getCertificate(paramString2);
      Object[] arrayOfObject2 = { "trustedCertEntry" };
      localObject2 = new MessageFormat(rb.getString("Entry.type.type.")).format(arrayOfObject2) + "\n";
      if ((verbose) && ((localObject1 instanceof X509Certificate)))
      {
        paramPrintStream.println((String)localObject2);
        printX509Cert((X509Certificate)localObject1, paramPrintStream);
      }
      else if (rfc)
      {
        paramPrintStream.println((String)localObject2);
        dumpCert((Certificate)localObject1, paramPrintStream);
      }
      else if (debug)
      {
        paramPrintStream.println(((Certificate)localObject1).toString());
      }
      else
      {
        paramPrintStream.println("trustedCertEntry, ");
        paramPrintStream.println(rb.getString("Certificate.fingerprint.SHA1.") + getCertFingerPrint("SHA1", (Certificate)localObject1));
      }
      checkWeak(paramString1, (Certificate)localObject1);
    }
    else
    {
      paramPrintStream.println(rb.getString("Unknown.Entry.Type"));
    }
  }
  
  boolean inplaceImportCheck()
    throws Exception
  {
    if (("PKCS11".equalsIgnoreCase(srcstoretype)) || (KeyStoreUtil.isWindowsKeyStore(srcstoretype))) {
      return false;
    }
    if (srcksfname != null)
    {
      File localFile = new File(srcksfname);
      if ((localFile.exists()) && (localFile.length() == 0L)) {
        throw new Exception(rb.getString("Source.keystore.file.exists.but.is.empty.") + srcksfname);
      }
      if (localFile.getCanonicalFile().equals(new File(ksfname).getCanonicalFile())) {
        return true;
      }
      System.err.println(String.format(rb.getString("importing.keystore.status"), new Object[] { srcksfname, ksfname }));
      return false;
    }
    throw new Exception(rb.getString("Please.specify.srckeystore"));
  }
  
  KeyStore loadSourceKeyStore()
    throws Exception
  {
    FileInputStream localFileInputStream = null;
    File localFile = null;
    if (("PKCS11".equalsIgnoreCase(srcstoretype)) || (KeyStoreUtil.isWindowsKeyStore(srcstoretype)))
    {
      if (!"NONE".equals(srcksfname))
      {
        System.err.println(MessageFormat.format(rb.getString(".keystore.must.be.NONE.if.storetype.is.{0}"), new Object[] { srcstoretype }));
        System.err.println();
        tinyHelp();
      }
    }
    else
    {
      localFile = new File(srcksfname);
      localFileInputStream = new FileInputStream(localFile);
    }
    KeyStore localKeyStore;
    try
    {
      if (srcProviderName == null) {
        localKeyStore = KeyStore.getInstance(srcstoretype);
      } else {
        localKeyStore = KeyStore.getInstance(srcstoretype, srcProviderName);
      }
      if ((srcstorePass == null) && (!srcprotectedPath) && (!KeyStoreUtil.isWindowsKeyStore(srcstoretype)))
      {
        System.err.print(rb.getString("Enter.source.keystore.password."));
        System.err.flush();
        srcstorePass = Password.readPassword(System.in);
        passwords.add(srcstorePass);
      }
      if (("PKCS12".equalsIgnoreCase(srcstoretype)) && (srckeyPass != null) && (srcstorePass != null) && (!Arrays.equals(srcstorePass, srckeyPass)))
      {
        MessageFormat localMessageFormat = new MessageFormat(rb.getString("Warning.Different.store.and.key.passwords.not.supported.for.PKCS12.KeyStores.Ignoring.user.specified.command.value."));
        Object[] arrayOfObject = { "-srckeypass" };
        System.err.println(localMessageFormat.format(arrayOfObject));
        srckeyPass = srcstorePass;
      }
      localKeyStore.load(localFileInputStream, srcstorePass);
    }
    finally
    {
      if (localFileInputStream != null) {
        localFileInputStream.close();
      }
    }
    if ((srcstorePass == null) && (!KeyStoreUtil.isWindowsKeyStore(srcstoretype)))
    {
      System.err.println();
      System.err.println(rb.getString(".WARNING.WARNING.WARNING."));
      System.err.println(rb.getString(".The.integrity.of.the.information.stored.in.the.srckeystore."));
      System.err.println(rb.getString(".WARNING.WARNING.WARNING."));
      System.err.println();
    }
    return localKeyStore;
  }
  
  private void doImportKeyStore(KeyStore paramKeyStore)
    throws Exception
  {
    if (alias != null)
    {
      doImportKeyStoreSingle(paramKeyStore, alias);
    }
    else
    {
      if ((dest != null) || (srckeyPass != null)) {
        throw new Exception(rb.getString("if.alias.not.specified.destalias.and.srckeypass.must.not.be.specified"));
      }
      doImportKeyStoreAll(paramKeyStore);
    }
    if (inplaceImport) {
      for (int i = 1;; i++)
      {
        inplaceBackupName = (srcksfname + ".old" + (i == 1 ? "" : Integer.valueOf(i)));
        File localFile = new File(inplaceBackupName);
        if (!localFile.exists())
        {
          Files.copy(Paths.get(srcksfname, new String[0]), localFile.toPath(), new CopyOption[0]);
          break;
        }
      }
    }
  }
  
  private int doImportKeyStoreSingle(KeyStore paramKeyStore, String paramString)
    throws Exception
  {
    String str = dest == null ? paramString : dest;
    if (keyStore.containsAlias(str))
    {
      localObject1 = new Object[] { paramString };
      if (noprompt)
      {
        System.err.println(new MessageFormat(rb.getString("Warning.Overwriting.existing.alias.alias.in.destination.keystore")).format(localObject1));
      }
      else
      {
        localObject2 = getYesNoReply(new MessageFormat(rb.getString("Existing.entry.alias.alias.exists.overwrite.no.")).format(localObject1));
        if ("NO".equals(localObject2))
        {
          str = inputStringFromStdin(rb.getString("Enter.new.alias.name.RETURN.to.cancel.import.for.this.entry."));
          if ("".equals(str))
          {
            System.err.println(new MessageFormat(rb.getString("Entry.for.alias.alias.not.imported.")).format(localObject1));
            return 0;
          }
        }
      }
    }
    Object localObject1 = recoverEntry(paramKeyStore, paramString, srcstorePass, srckeyPass);
    Object localObject2 = (KeyStore.Entry)fst;
    KeyStore.PasswordProtection localPasswordProtection = null;
    char[] arrayOfChar = null;
    if (destKeyPass != null)
    {
      arrayOfChar = destKeyPass;
      localPasswordProtection = new KeyStore.PasswordProtection(destKeyPass);
    }
    else if (snd != null)
    {
      arrayOfChar = (char[])snd;
      localPasswordProtection = new KeyStore.PasswordProtection((char[])snd);
    }
    try
    {
      Certificate localCertificate = paramKeyStore.getCertificate(paramString);
      if (localCertificate != null) {
        checkWeak("<" + str + ">", localCertificate);
      }
      keyStore.setEntry(str, (KeyStore.Entry)localObject2, localPasswordProtection);
      if (("PKCS12".equalsIgnoreCase(storetype)) && (arrayOfChar != null) && (!Arrays.equals(arrayOfChar, storePass))) {
        throw new Exception(rb.getString("The.destination.pkcs12.keystore.has.different.storepass.and.keypass.Please.retry.with.destkeypass.specified."));
      }
      return 1;
    }
    catch (KeyStoreException localKeyStoreException)
    {
      Object[] arrayOfObject = { paramString, localKeyStoreException.toString() };
      MessageFormat localMessageFormat = new MessageFormat(rb.getString("Problem.importing.entry.for.alias.alias.exception.Entry.for.alias.alias.not.imported."));
      System.err.println(localMessageFormat.format(arrayOfObject));
    }
    return 2;
  }
  
  private void doImportKeyStoreAll(KeyStore paramKeyStore)
    throws Exception
  {
    int i = 0;
    int j = paramKeyStore.size();
    Object localObject1 = paramKeyStore.aliases();
    while (((Enumeration)localObject1).hasMoreElements())
    {
      localObject2 = (String)((Enumeration)localObject1).nextElement();
      int k = doImportKeyStoreSingle(paramKeyStore, (String)localObject2);
      Object localObject3;
      if (k == 1)
      {
        i++;
        localObject3 = new Object[] { localObject2 };
        MessageFormat localMessageFormat = new MessageFormat(rb.getString("Entry.for.alias.alias.successfully.imported."));
        System.err.println(localMessageFormat.format(localObject3));
      }
      else if ((k == 2) && (!noprompt))
      {
        localObject3 = getYesNoReply("Do you want to quit the import process? [no]:  ");
        if ("YES".equals(localObject3)) {
          break;
        }
      }
    }
    localObject1 = new Object[] { Integer.valueOf(i), Integer.valueOf(j - i) };
    Object localObject2 = new MessageFormat(rb.getString("Import.command.completed.ok.entries.successfully.imported.fail.entries.failed.or.cancelled"));
    System.err.println(((MessageFormat)localObject2).format(localObject1));
  }
  
  private void doPrintEntries(PrintStream paramPrintStream)
    throws Exception
  {
    paramPrintStream.println(rb.getString("Keystore.type.") + keyStore.getType());
    paramPrintStream.println(rb.getString("Keystore.provider.") + keyStore.getProvider().getName());
    paramPrintStream.println();
    MessageFormat localMessageFormat = keyStore.size() == 1 ? new MessageFormat(rb.getString("Your.keystore.contains.keyStore.size.entry")) : new MessageFormat(rb.getString("Your.keystore.contains.keyStore.size.entries"));
    Object[] arrayOfObject = { new Integer(keyStore.size()) };
    paramPrintStream.println(localMessageFormat.format(arrayOfObject));
    paramPrintStream.println();
    Enumeration localEnumeration = keyStore.aliases();
    while (localEnumeration.hasMoreElements())
    {
      String str = (String)localEnumeration.nextElement();
      doPrintEntry("<" + str + ">", str, paramPrintStream);
      if ((verbose) || (rfc))
      {
        paramPrintStream.println(rb.getString("NEWLINE"));
        paramPrintStream.println(rb.getString("STAR"));
        paramPrintStream.println(rb.getString("STARNN"));
      }
    }
  }
  
  private static <T> Iterable<T> e2i(Enumeration<T> paramEnumeration)
  {
    new Iterable()
    {
      public Iterator<T> iterator()
      {
        new Iterator()
        {
          public boolean hasNext()
          {
            return val$e.hasMoreElements();
          }
          
          public T next()
          {
            return (T)val$e.nextElement();
          }
          
          public void remove()
          {
            throw new UnsupportedOperationException("Not supported yet.");
          }
        };
      }
    };
  }
  
  public static Collection<? extends CRL> loadCRLs(String paramString)
    throws Exception
  {
    Object localObject1 = null;
    URI localURI = null;
    if (paramString == null) {
      localObject1 = System.in;
    } else {
      try
      {
        localURI = new URI(paramString);
        if (!localURI.getScheme().equals("ldap")) {
          localObject1 = localURI.toURL().openStream();
        }
      }
      catch (Exception localException1)
      {
        try
        {
          localObject1 = new FileInputStream(paramString);
        }
        catch (Exception localException2)
        {
          if ((localURI == null) || (localURI.getScheme() == null)) {
            throw localException2;
          }
          throw localException1;
        }
      }
    }
    if (localObject1 != null) {
      try
      {
        localObject2 = new ByteArrayOutputStream();
        localObject3 = new byte['က'];
        for (;;)
        {
          int i = ((InputStream)localObject1).read((byte[])localObject3);
          if (i < 0) {
            break;
          }
          ((ByteArrayOutputStream)localObject2).write((byte[])localObject3, 0, i);
        }
        localObject4 = CertificateFactory.getInstance("X509").generateCRLs(new ByteArrayInputStream(((ByteArrayOutputStream)localObject2).toByteArray()));
        return (Collection<? extends CRL>)localObject4;
      }
      finally
      {
        if (localObject1 != System.in) {
          ((InputStream)localObject1).close();
        }
      }
    }
    Object localObject2 = CertStoreHelper.getInstance("LDAP");
    Object localObject3 = localURI.getPath();
    if (((String)localObject3).charAt(0) == '/') {
      localObject3 = ((String)localObject3).substring(1);
    }
    Object localObject4 = ((CertStoreHelper)localObject2).getCertStore(localURI);
    X509CRLSelector localX509CRLSelector = ((CertStoreHelper)localObject2).wrap(new X509CRLSelector(), null, (String)localObject3);
    return ((CertStore)localObject4).getCRLs(localX509CRLSelector);
  }
  
  public static List<CRL> readCRLsFromCert(X509Certificate paramX509Certificate)
    throws Exception
  {
    ArrayList localArrayList = new ArrayList();
    CRLDistributionPointsExtension localCRLDistributionPointsExtension = X509CertImpl.toImpl(paramX509Certificate).getCRLDistributionPointsExtension();
    if (localCRLDistributionPointsExtension == null) {
      return localArrayList;
    }
    List localList = localCRLDistributionPointsExtension.get("points");
    Iterator localIterator1 = localList.iterator();
    while (localIterator1.hasNext())
    {
      DistributionPoint localDistributionPoint = (DistributionPoint)localIterator1.next();
      GeneralNames localGeneralNames = localDistributionPoint.getFullName();
      if (localGeneralNames != null)
      {
        Iterator localIterator2 = localGeneralNames.names().iterator();
        while (localIterator2.hasNext())
        {
          GeneralName localGeneralName = (GeneralName)localIterator2.next();
          if (localGeneralName.getType() == 6)
          {
            URIName localURIName = (URIName)localGeneralName.getName();
            Iterator localIterator3 = loadCRLs(localURIName.getName()).iterator();
            while (localIterator3.hasNext())
            {
              CRL localCRL = (CRL)localIterator3.next();
              if ((localCRL instanceof X509CRL)) {
                localArrayList.add((X509CRL)localCRL);
              }
            }
            break;
          }
        }
      }
    }
    return localArrayList;
  }
  
  private static String verifyCRL(KeyStore paramKeyStore, CRL paramCRL)
    throws Exception
  {
    X509CRLImpl localX509CRLImpl = (X509CRLImpl)paramCRL;
    X500Principal localX500Principal = localX509CRLImpl.getIssuerX500Principal();
    Iterator localIterator = e2i(paramKeyStore.aliases()).iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      Certificate localCertificate = paramKeyStore.getCertificate(str);
      if ((localCertificate instanceof X509Certificate))
      {
        X509Certificate localX509Certificate = (X509Certificate)localCertificate;
        if (localX509Certificate.getSubjectX500Principal().equals(localX500Principal)) {
          try
          {
            ((X509CRLImpl)paramCRL).verify(localCertificate.getPublicKey());
            return str;
          }
          catch (Exception localException) {}
        }
      }
    }
    return null;
  }
  
  private void doPrintCRL(String paramString, PrintStream paramPrintStream)
    throws Exception
  {
    Iterator localIterator = loadCRLs(paramString).iterator();
    while (localIterator.hasNext())
    {
      CRL localCRL = (CRL)localIterator.next();
      printCRL(localCRL, paramPrintStream);
      String str = null;
      Certificate localCertificate = null;
      if (caks != null)
      {
        str = verifyCRL(caks, localCRL);
        if (str != null)
        {
          localCertificate = caks.getCertificate(str);
          paramPrintStream.printf(rb.getString("verified.by.s.in.s.weak"), new Object[] { str, "cacerts", withWeak(localCertificate.getPublicKey()) });
          paramPrintStream.println();
        }
      }
      if ((str == null) && (keyStore != null))
      {
        str = verifyCRL(keyStore, localCRL);
        if (str != null)
        {
          localCertificate = keyStore.getCertificate(str);
          paramPrintStream.printf(rb.getString("verified.by.s.in.s.weak"), new Object[] { str, "keystore", withWeak(localCertificate.getPublicKey()) });
          paramPrintStream.println();
        }
      }
      if (str == null)
      {
        paramPrintStream.println(rb.getString("STAR"));
        paramPrintStream.println(rb.getString("warning.not.verified.make.sure.keystore.is.correct"));
        paramPrintStream.println(rb.getString("STARNN"));
      }
      checkWeak(rb.getString("the.crl"), localCRL, localCertificate == null ? null : localCertificate.getPublicKey());
    }
  }
  
  private void printCRL(CRL paramCRL, PrintStream paramPrintStream)
    throws Exception
  {
    X509CRL localX509CRL = (X509CRL)paramCRL;
    if (rfc)
    {
      paramPrintStream.println("-----BEGIN X509 CRL-----");
      paramPrintStream.println(Base64.getMimeEncoder(64, CRLF).encodeToString(localX509CRL.getEncoded()));
      paramPrintStream.println("-----END X509 CRL-----");
    }
    else
    {
      String str;
      if ((paramCRL instanceof X509CRLImpl))
      {
        X509CRLImpl localX509CRLImpl = (X509CRLImpl)paramCRL;
        str = localX509CRLImpl.toStringWithAlgName(withWeak("" + localX509CRLImpl.getSigAlgId()));
      }
      else
      {
        str = paramCRL.toString();
      }
      paramPrintStream.println(str);
    }
  }
  
  private void doPrintCertReq(InputStream paramInputStream, PrintStream paramPrintStream)
    throws Exception
  {
    BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(paramInputStream));
    StringBuffer localStringBuffer = new StringBuffer();
    int i = 0;
    for (;;)
    {
      localObject1 = localBufferedReader.readLine();
      if (localObject1 == null) {
        break;
      }
      if (i == 0)
      {
        if (((String)localObject1).startsWith("-----")) {
          i = 1;
        }
      }
      else
      {
        if (((String)localObject1).startsWith("-----")) {
          break;
        }
        localStringBuffer.append((String)localObject1);
      }
    }
    Object localObject1 = new PKCS10(Pem.decode(new String(localStringBuffer)));
    PublicKey localPublicKey = ((PKCS10)localObject1).getSubjectPublicKeyInfo();
    paramPrintStream.printf(rb.getString("PKCS.10.with.weak"), new Object[] { ((PKCS10)localObject1).getSubjectName(), localPublicKey.getFormat(), withWeak(localPublicKey), withWeak(((PKCS10)localObject1).getSigAlg()) });
    Iterator localIterator = ((PKCS10)localObject1).getAttributes().getAttributes().iterator();
    while (localIterator.hasNext())
    {
      PKCS10Attribute localPKCS10Attribute = (PKCS10Attribute)localIterator.next();
      ObjectIdentifier localObjectIdentifier = localPKCS10Attribute.getAttributeId();
      Object localObject2;
      if (localObjectIdentifier.equals(PKCS9Attribute.EXTENSION_REQUEST_OID))
      {
        localObject2 = (CertificateExtensions)localPKCS10Attribute.getAttributeValue();
        if (localObject2 != null) {
          printExtensions(rb.getString("Extension.Request."), (CertificateExtensions)localObject2, paramPrintStream);
        }
      }
      else
      {
        paramPrintStream.println("Attribute: " + localPKCS10Attribute.getAttributeId());
        localObject2 = new PKCS9Attribute(localPKCS10Attribute.getAttributeId(), localPKCS10Attribute.getAttributeValue());
        paramPrintStream.print(((PKCS9Attribute)localObject2).getName() + ": ");
        Object localObject3 = localPKCS10Attribute.getAttributeValue();
        paramPrintStream.println((localObject3 instanceof String[]) ? Arrays.toString((String[])localObject3) : localObject3);
      }
    }
    if (debug) {
      paramPrintStream.println(localObject1);
    }
    checkWeak(rb.getString("the.certificate.request"), (PKCS10)localObject1);
  }
  
  private void printCertFromStream(InputStream paramInputStream, PrintStream paramPrintStream)
    throws Exception
  {
    Collection localCollection = null;
    try
    {
      localCollection = cf.generateCertificates(paramInputStream);
    }
    catch (CertificateException localCertificateException)
    {
      throw new Exception(rb.getString("Failed.to.parse.input"), localCertificateException);
    }
    if (localCollection.isEmpty()) {
      throw new Exception(rb.getString("Empty.input"));
    }
    Certificate[] arrayOfCertificate = (Certificate[])localCollection.toArray(new Certificate[localCollection.size()]);
    for (int i = 0; i < arrayOfCertificate.length; i++)
    {
      X509Certificate localX509Certificate = null;
      try
      {
        localX509Certificate = (X509Certificate)arrayOfCertificate[i];
      }
      catch (ClassCastException localClassCastException)
      {
        throw new Exception(rb.getString("Not.X.509.certificate"));
      }
      if (arrayOfCertificate.length > 1)
      {
        MessageFormat localMessageFormat = new MessageFormat(rb.getString("Certificate.i.1."));
        Object[] arrayOfObject = { new Integer(i + 1) };
        paramPrintStream.println(localMessageFormat.format(arrayOfObject));
      }
      if (rfc) {
        dumpCert(localX509Certificate, paramPrintStream);
      } else {
        printX509Cert(localX509Certificate, paramPrintStream);
      }
      if (i < arrayOfCertificate.length - 1) {
        paramPrintStream.println();
      }
      checkWeak(oneInMany(rb.getString("the.certificate"), i, arrayOfCertificate.length), localX509Certificate);
    }
  }
  
  private static String oneInMany(String paramString, int paramInt1, int paramInt2)
  {
    if (paramInt2 == 1) {
      return paramString;
    }
    return String.format(rb.getString("one.in.many"), new Object[] { paramString, Integer.valueOf(paramInt1 + 1), Integer.valueOf(paramInt2) });
  }
  
  private void doPrintCert(PrintStream paramPrintStream)
    throws Exception
  {
    Object localObject1;
    Object localObject2;
    Object localObject3;
    Object localObject4;
    if (jarfile != null)
    {
      localObject1 = new JarFile(jarfile, true);
      localObject2 = ((JarFile)localObject1).entries();
      localObject3 = new HashSet();
      byte[] arrayOfByte = new byte[' '];
      int j = 0;
      while (((Enumeration)localObject2).hasMoreElements())
      {
        localObject4 = (JarEntry)((Enumeration)localObject2).nextElement();
        Object localObject5 = ((JarFile)localObject1).getInputStream((ZipEntry)localObject4);
        Object localObject6 = null;
        try
        {
          while (((InputStream)localObject5).read(arrayOfByte) != -1) {}
        }
        catch (Throwable localThrowable4)
        {
          localObject6 = localThrowable4;
          throw localThrowable4;
        }
        finally
        {
          if (localObject5 != null) {
            if (localObject6 != null) {
              try
              {
                ((InputStream)localObject5).close();
              }
              catch (Throwable localThrowable5)
              {
                ((Throwable)localObject6).addSuppressed(localThrowable5);
              }
            } else {
              ((InputStream)localObject5).close();
            }
          }
        }
        localObject5 = ((JarEntry)localObject4).getCodeSigners();
        if (localObject5 != null) {
          for (Object localObject8 : localObject5) {
            if (!((Set)localObject3).contains(localObject8))
            {
              ((Set)localObject3).add(localObject8);
              paramPrintStream.printf(rb.getString("Signer.d."), new Object[] { Integer.valueOf(++j) });
              paramPrintStream.println();
              paramPrintStream.println();
              paramPrintStream.println(rb.getString("Signature."));
              paramPrintStream.println();
              List localList = ((CodeSigner)localObject8).getSignerCertPath().getCertificates();
              int n = 0;
              Object localObject9 = localList.iterator();
              Object localObject10;
              Object localObject11;
              while (((Iterator)localObject9).hasNext())
              {
                localObject10 = (Certificate)((Iterator)localObject9).next();
                localObject11 = (X509Certificate)localObject10;
                if (rfc)
                {
                  paramPrintStream.println(rb.getString("Certificate.owner.") + ((X509Certificate)localObject11).getSubjectDN() + "\n");
                  dumpCert((Certificate)localObject11, paramPrintStream);
                }
                else
                {
                  printX509Cert((X509Certificate)localObject11, paramPrintStream);
                }
                paramPrintStream.println();
                checkWeak(oneInMany(rb.getString("the.certificate"), n++, localList.size()), (Certificate)localObject11);
              }
              localObject9 = ((CodeSigner)localObject8).getTimestamp();
              if (localObject9 != null)
              {
                paramPrintStream.println(rb.getString("Timestamp."));
                paramPrintStream.println();
                localList = ((Timestamp)localObject9).getSignerCertPath().getCertificates();
                n = 0;
                localObject10 = localList.iterator();
                while (((Iterator)localObject10).hasNext())
                {
                  localObject11 = (Certificate)((Iterator)localObject10).next();
                  X509Certificate localX509Certificate = (X509Certificate)localObject11;
                  if (rfc)
                  {
                    paramPrintStream.println(rb.getString("Certificate.owner.") + localX509Certificate.getSubjectDN() + "\n");
                    dumpCert(localX509Certificate, paramPrintStream);
                  }
                  else
                  {
                    printX509Cert(localX509Certificate, paramPrintStream);
                  }
                  paramPrintStream.println();
                  checkWeak(oneInMany(rb.getString("the.tsa.certificate"), n++, localList.size()), localX509Certificate);
                }
              }
            }
          }
        }
      }
      ((JarFile)localObject1).close();
      if (((Set)localObject3).isEmpty()) {
        paramPrintStream.println(rb.getString("Not.a.signed.jar.file"));
      }
    }
    else if (sslserver != null)
    {
      localObject1 = CertStoreHelper.getInstance("SSLServer");
      localObject2 = ((CertStoreHelper)localObject1).getCertStore(new URI("https://" + sslserver));
      try
      {
        localObject3 = ((CertStore)localObject2).getCertificates(null);
        if (((Collection)localObject3).isEmpty()) {
          throw new Exception(rb.getString("No.certificate.from.the.SSL.server"));
        }
      }
      catch (CertStoreException localCertStoreException)
      {
        if ((localCertStoreException.getCause() instanceof IOException)) {
          throw new Exception(rb.getString("No.certificate.from.the.SSL.server"), localCertStoreException.getCause());
        }
        throw localCertStoreException;
      }
      int i = 0;
      Iterator localIterator = ((Collection)localObject3).iterator();
      while (localIterator.hasNext())
      {
        localObject4 = (Certificate)localIterator.next();
        try
        {
          if (rfc)
          {
            dumpCert((Certificate)localObject4, paramPrintStream);
          }
          else
          {
            paramPrintStream.println("Certificate #" + i++);
            paramPrintStream.println("====================================");
            printX509Cert((X509Certificate)localObject4, paramPrintStream);
            paramPrintStream.println();
          }
          checkWeak(oneInMany(rb.getString("the.certificate"), i, ((Collection)localObject3).size()), (Certificate)localObject4);
        }
        catch (Exception localException)
        {
          if (debug) {
            localException.printStackTrace();
          }
        }
      }
    }
    else if (filename != null)
    {
      localObject1 = new FileInputStream(filename);
      localObject2 = null;
      try
      {
        printCertFromStream((InputStream)localObject1, paramPrintStream);
      }
      catch (Throwable localThrowable2)
      {
        localObject2 = localThrowable2;
        throw localThrowable2;
      }
      finally
      {
        if (localObject1 != null) {
          if (localObject2 != null) {
            try
            {
              ((FileInputStream)localObject1).close();
            }
            catch (Throwable localThrowable6)
            {
              ((Throwable)localObject2).addSuppressed(localThrowable6);
            }
          } else {
            ((FileInputStream)localObject1).close();
          }
        }
      }
    }
    else
    {
      printCertFromStream(System.in, paramPrintStream);
    }
  }
  
  private void doSelfCert(String paramString1, String paramString2, String paramString3)
    throws Exception
  {
    if (paramString1 == null) {
      paramString1 = "mykey";
    }
    Pair localPair = recoverKey(paramString1, storePass, keyPass);
    PrivateKey localPrivateKey = (PrivateKey)fst;
    if (keyPass == null) {
      keyPass = ((char[])snd);
    }
    if (paramString3 == null) {
      paramString3 = getCompatibleSigAlgName(localPrivateKey.getAlgorithm());
    }
    Certificate localCertificate = keyStore.getCertificate(paramString1);
    if (localCertificate == null)
    {
      localObject1 = new MessageFormat(rb.getString("alias.has.no.public.key"));
      localObject2 = new Object[] { paramString1 };
      throw new Exception(((MessageFormat)localObject1).format(localObject2));
    }
    if (!(localCertificate instanceof X509Certificate))
    {
      localObject1 = new MessageFormat(rb.getString("alias.has.no.X.509.certificate"));
      localObject2 = new Object[] { paramString1 };
      throw new Exception(((MessageFormat)localObject1).format(localObject2));
    }
    Object localObject1 = localCertificate.getEncoded();
    Object localObject2 = new X509CertImpl((byte[])localObject1);
    X509CertInfo localX509CertInfo = (X509CertInfo)((X509CertImpl)localObject2).get("x509.info");
    Date localDate1 = getStartDate(startDate);
    Date localDate2 = new Date();
    localDate2.setTime(localDate1.getTime() + validity * 1000L * 24L * 60L * 60L);
    CertificateValidity localCertificateValidity = new CertificateValidity(localDate1, localDate2);
    localX509CertInfo.set("validity", localCertificateValidity);
    localX509CertInfo.set("serialNumber", new CertificateSerialNumber(new Random().nextInt() & 0x7FFFFFFF));
    X500Name localX500Name;
    if (paramString2 == null)
    {
      localX500Name = (X500Name)localX509CertInfo.get("subject.dname");
    }
    else
    {
      localX500Name = new X500Name(paramString2);
      localX509CertInfo.set("subject.dname", localX500Name);
    }
    localX509CertInfo.set("issuer.dname", localX500Name);
    X509CertImpl localX509CertImpl = new X509CertImpl(localX509CertInfo);
    localX509CertImpl.sign(localPrivateKey, paramString3);
    AlgorithmId localAlgorithmId = (AlgorithmId)localX509CertImpl.get("x509.algorithm");
    localX509CertInfo.set("algorithmID.algorithm", localAlgorithmId);
    localX509CertInfo.set("version", new CertificateVersion(2));
    CertificateExtensions localCertificateExtensions = createV3Extensions(null, (CertificateExtensions)localX509CertInfo.get("extensions"), v3ext, localCertificate.getPublicKey(), null);
    localX509CertInfo.set("extensions", localCertificateExtensions);
    localX509CertImpl = new X509CertImpl(localX509CertInfo);
    localX509CertImpl.sign(localPrivateKey, paramString3);
    keyStore.setKeyEntry(paramString1, localPrivateKey, keyPass != null ? keyPass : storePass, new Certificate[] { localX509CertImpl });
    if (verbose)
    {
      System.err.println(rb.getString("New.certificate.self.signed."));
      System.err.print(localX509CertImpl.toString());
      System.err.println();
    }
  }
  
  private boolean installReply(String paramString, InputStream paramInputStream)
    throws Exception
  {
    if (paramString == null) {
      paramString = "mykey";
    }
    Pair localPair = recoverKey(paramString, storePass, keyPass);
    PrivateKey localPrivateKey = (PrivateKey)fst;
    if (keyPass == null) {
      keyPass = ((char[])snd);
    }
    Certificate localCertificate = keyStore.getCertificate(paramString);
    if (localCertificate == null)
    {
      localObject1 = new MessageFormat(rb.getString("alias.has.no.public.key.certificate."));
      localObject2 = new Object[] { paramString };
      throw new Exception(((MessageFormat)localObject1).format(localObject2));
    }
    Object localObject1 = cf.generateCertificates(paramInputStream);
    if (((Collection)localObject1).isEmpty()) {
      throw new Exception(rb.getString("Reply.has.no.certificates"));
    }
    Object localObject2 = (Certificate[])((Collection)localObject1).toArray(new Certificate[((Collection)localObject1).size()]);
    Certificate[] arrayOfCertificate;
    if (localObject2.length == 1) {
      arrayOfCertificate = establishCertChain(localCertificate, localObject2[0]);
    } else {
      arrayOfCertificate = validateReply(paramString, localCertificate, (Certificate[])localObject2);
    }
    if (arrayOfCertificate != null)
    {
      keyStore.setKeyEntry(paramString, localPrivateKey, keyPass != null ? keyPass : storePass, arrayOfCertificate);
      return true;
    }
    return false;
  }
  
  private boolean addTrustedCert(String paramString, InputStream paramInputStream)
    throws Exception
  {
    if (paramString == null) {
      throw new Exception(rb.getString("Must.specify.alias"));
    }
    if (keyStore.containsAlias(paramString))
    {
      localObject1 = new MessageFormat(rb.getString("Certificate.not.imported.alias.alias.already.exists"));
      Object[] arrayOfObject1 = { paramString };
      throw new Exception(((MessageFormat)localObject1).format(arrayOfObject1));
    }
    Object localObject1 = null;
    try
    {
      localObject1 = (X509Certificate)cf.generateCertificate(paramInputStream);
    }
    catch (ClassCastException|CertificateException localClassCastException)
    {
      throw new Exception(rb.getString("Input.not.an.X.509.certificate"));
    }
    if (noprompt)
    {
      checkWeak(rb.getString("the.input"), (Certificate)localObject1);
      keyStore.setCertificateEntry(paramString, (Certificate)localObject1);
      return true;
    }
    int i = 0;
    if (isSelfSigned((X509Certificate)localObject1))
    {
      ((X509Certificate)localObject1).verify(((X509Certificate)localObject1).getPublicKey());
      i = 1;
    }
    String str1 = null;
    String str2 = keyStore.getCertificateAlias((Certificate)localObject1);
    Object localObject2;
    Object[] arrayOfObject2;
    if (str2 != null)
    {
      localObject2 = new MessageFormat(rb.getString("Certificate.already.exists.in.keystore.under.alias.trustalias."));
      arrayOfObject2 = new Object[] { str2 };
      System.err.println(((MessageFormat)localObject2).format(arrayOfObject2));
      checkWeak(rb.getString("the.input"), (Certificate)localObject1);
      printWeakWarnings(true);
      str1 = getYesNoReply(rb.getString("Do.you.still.want.to.add.it.no."));
    }
    else if (i != 0)
    {
      if ((trustcacerts) && (caks != null) && ((str2 = caks.getCertificateAlias((Certificate)localObject1)) != null))
      {
        localObject2 = new MessageFormat(rb.getString("Certificate.already.exists.in.system.wide.CA.keystore.under.alias.trustalias."));
        arrayOfObject2 = new Object[] { str2 };
        System.err.println(((MessageFormat)localObject2).format(arrayOfObject2));
        checkWeak(rb.getString("the.input"), (Certificate)localObject1);
        printWeakWarnings(true);
        str1 = getYesNoReply(rb.getString("Do.you.still.want.to.add.it.to.your.own.keystore.no."));
      }
      if (str2 == null)
      {
        printX509Cert((X509Certificate)localObject1, System.out);
        checkWeak(rb.getString("the.input"), (Certificate)localObject1);
        printWeakWarnings(true);
        str1 = getYesNoReply(rb.getString("Trust.this.certificate.no."));
      }
    }
    if (str1 != null)
    {
      if ("YES".equals(str1))
      {
        keyStore.setCertificateEntry(paramString, (Certificate)localObject1);
        return true;
      }
      return false;
    }
    try
    {
      localObject2 = establishCertChain(null, (Certificate)localObject1);
      if (localObject2 != null)
      {
        keyStore.setCertificateEntry(paramString, (Certificate)localObject1);
        return true;
      }
    }
    catch (Exception localException)
    {
      printX509Cert((X509Certificate)localObject1, System.out);
      checkWeak(rb.getString("the.input"), (Certificate)localObject1);
      printWeakWarnings(true);
      str1 = getYesNoReply(rb.getString("Trust.this.certificate.no."));
      if ("YES".equals(str1))
      {
        keyStore.setCertificateEntry(paramString, (Certificate)localObject1);
        return true;
      }
      return false;
    }
    return false;
  }
  
  private char[] getNewPasswd(String paramString, char[] paramArrayOfChar)
    throws Exception
  {
    char[] arrayOfChar1 = null;
    char[] arrayOfChar2 = null;
    for (int i = 0; i < 3; i++)
    {
      MessageFormat localMessageFormat = new MessageFormat(rb.getString("New.prompt."));
      Object[] arrayOfObject1 = { paramString };
      System.err.print(localMessageFormat.format(arrayOfObject1));
      arrayOfChar1 = Password.readPassword(System.in);
      passwords.add(arrayOfChar1);
      if ((arrayOfChar1 == null) || (arrayOfChar1.length < 6))
      {
        System.err.println(rb.getString("Password.is.too.short.must.be.at.least.6.characters"));
      }
      else if (Arrays.equals(arrayOfChar1, paramArrayOfChar))
      {
        System.err.println(rb.getString("Passwords.must.differ"));
      }
      else
      {
        localMessageFormat = new MessageFormat(rb.getString("Re.enter.new.prompt."));
        Object[] arrayOfObject2 = { paramString };
        System.err.print(localMessageFormat.format(arrayOfObject2));
        arrayOfChar2 = Password.readPassword(System.in);
        passwords.add(arrayOfChar2);
        if (!Arrays.equals(arrayOfChar1, arrayOfChar2))
        {
          System.err.println(rb.getString("They.don.t.match.Try.again"));
        }
        else
        {
          Arrays.fill(arrayOfChar2, ' ');
          return arrayOfChar1;
        }
      }
      if (arrayOfChar1 != null)
      {
        Arrays.fill(arrayOfChar1, ' ');
        arrayOfChar1 = null;
      }
      if (arrayOfChar2 != null)
      {
        Arrays.fill(arrayOfChar2, ' ');
        arrayOfChar2 = null;
      }
    }
    throw new Exception(rb.getString("Too.many.failures.try.later"));
  }
  
  private String getAlias(String paramString)
    throws Exception
  {
    if (paramString != null)
    {
      MessageFormat localMessageFormat = new MessageFormat(rb.getString("Enter.prompt.alias.name."));
      Object[] arrayOfObject = { paramString };
      System.err.print(localMessageFormat.format(arrayOfObject));
    }
    else
    {
      System.err.print(rb.getString("Enter.alias.name."));
    }
    return new BufferedReader(new InputStreamReader(System.in)).readLine();
  }
  
  private String inputStringFromStdin(String paramString)
    throws Exception
  {
    System.err.print(paramString);
    return new BufferedReader(new InputStreamReader(System.in)).readLine();
  }
  
  private char[] getKeyPasswd(String paramString1, String paramString2, char[] paramArrayOfChar)
    throws Exception
  {
    int i = 0;
    char[] arrayOfChar = null;
    do
    {
      MessageFormat localMessageFormat;
      Object[] arrayOfObject1;
      if (paramArrayOfChar != null)
      {
        localMessageFormat = new MessageFormat(rb.getString("Enter.key.password.for.alias."));
        arrayOfObject1 = new Object[] { paramString1 };
        System.err.println(localMessageFormat.format(arrayOfObject1));
        localMessageFormat = new MessageFormat(rb.getString(".RETURN.if.same.as.for.otherAlias."));
        Object[] arrayOfObject2 = { paramString2 };
        System.err.print(localMessageFormat.format(arrayOfObject2));
      }
      else
      {
        localMessageFormat = new MessageFormat(rb.getString("Enter.key.password.for.alias."));
        arrayOfObject1 = new Object[] { paramString1 };
        System.err.print(localMessageFormat.format(arrayOfObject1));
      }
      System.err.flush();
      arrayOfChar = Password.readPassword(System.in);
      passwords.add(arrayOfChar);
      if (arrayOfChar == null) {
        arrayOfChar = paramArrayOfChar;
      }
      i++;
    } while ((arrayOfChar == null) && (i < 3));
    if (arrayOfChar == null) {
      throw new Exception(rb.getString("Too.many.failures.try.later"));
    }
    return arrayOfChar;
  }
  
  private String withWeak(String paramString)
  {
    if (DISABLED_CHECK.permits(SIG_PRIMITIVE_SET, paramString, null)) {
      return paramString;
    }
    return String.format(rb.getString("with.weak"), new Object[] { paramString });
  }
  
  private String withWeak(PublicKey paramPublicKey)
  {
    if (DISABLED_CHECK.permits(SIG_PRIMITIVE_SET, paramPublicKey)) {
      return String.format(rb.getString("key.bit"), new Object[] { Integer.valueOf(KeyUtil.getKeySize(paramPublicKey)), paramPublicKey.getAlgorithm() });
    }
    return String.format(rb.getString("key.bit.weak"), new Object[] { Integer.valueOf(KeyUtil.getKeySize(paramPublicKey)), paramPublicKey.getAlgorithm() });
  }
  
  private void printX509Cert(X509Certificate paramX509Certificate, PrintStream paramPrintStream)
    throws Exception
  {
    MessageFormat localMessageFormat = new MessageFormat(rb.getString(".PATTERN.printX509Cert.with.weak"));
    PublicKey localPublicKey = paramX509Certificate.getPublicKey();
    String str = paramX509Certificate.getSigAlgName();
    if (!isTrustedCert(paramX509Certificate)) {
      str = withWeak(str);
    }
    Object[] arrayOfObject = { paramX509Certificate.getSubjectDN().toString(), paramX509Certificate.getIssuerDN().toString(), paramX509Certificate.getSerialNumber().toString(16), paramX509Certificate.getNotBefore().toString(), paramX509Certificate.getNotAfter().toString(), getCertFingerPrint("MD5", paramX509Certificate), getCertFingerPrint("SHA1", paramX509Certificate), getCertFingerPrint("SHA-256", paramX509Certificate), str, withWeak(localPublicKey), Integer.valueOf(paramX509Certificate.getVersion()) };
    paramPrintStream.println(localMessageFormat.format(arrayOfObject));
    if ((paramX509Certificate instanceof X509CertImpl))
    {
      X509CertImpl localX509CertImpl = (X509CertImpl)paramX509Certificate;
      X509CertInfo localX509CertInfo = (X509CertInfo)localX509CertImpl.get("x509.info");
      CertificateExtensions localCertificateExtensions = (CertificateExtensions)localX509CertInfo.get("extensions");
      if (localCertificateExtensions != null) {
        printExtensions(rb.getString("Extensions."), localCertificateExtensions, paramPrintStream);
      }
    }
  }
  
  private static void printExtensions(String paramString, CertificateExtensions paramCertificateExtensions, PrintStream paramPrintStream)
    throws Exception
  {
    int i = 0;
    Iterator localIterator1 = paramCertificateExtensions.getAllExtensions().iterator();
    Iterator localIterator2 = paramCertificateExtensions.getUnparseableExtensions().values().iterator();
    while ((localIterator1.hasNext()) || (localIterator2.hasNext()))
    {
      Extension localExtension = localIterator1.hasNext() ? (Extension)localIterator1.next() : (Extension)localIterator2.next();
      if (i == 0)
      {
        paramPrintStream.println();
        paramPrintStream.println(paramString);
        paramPrintStream.println();
      }
      paramPrintStream.print("#" + ++i + ": " + localExtension);
      if (localExtension.getClass() == Extension.class)
      {
        byte[] arrayOfByte = localExtension.getExtensionValue();
        if (arrayOfByte.length == 0)
        {
          paramPrintStream.println(rb.getString(".Empty.value."));
        }
        else
        {
          new HexDumpEncoder().encodeBuffer(localExtension.getExtensionValue(), paramPrintStream);
          paramPrintStream.println();
        }
      }
      paramPrintStream.println();
    }
  }
  
  private boolean isSelfSigned(X509Certificate paramX509Certificate)
  {
    return signedBy(paramX509Certificate, paramX509Certificate);
  }
  
  private boolean signedBy(X509Certificate paramX509Certificate1, X509Certificate paramX509Certificate2)
  {
    if (!paramX509Certificate2.getSubjectDN().equals(paramX509Certificate1.getIssuerDN())) {
      return false;
    }
    try
    {
      paramX509Certificate1.verify(paramX509Certificate2.getPublicKey());
      return true;
    }
    catch (Exception localException) {}
    return false;
  }
  
  private static Pair<String, Certificate> getSigner(Certificate paramCertificate, KeyStore paramKeyStore)
    throws Exception
  {
    if (paramKeyStore.getCertificateAlias(paramCertificate) != null) {
      return new Pair("", paramCertificate);
    }
    Enumeration localEnumeration = paramKeyStore.aliases();
    while (localEnumeration.hasMoreElements())
    {
      String str = (String)localEnumeration.nextElement();
      Certificate localCertificate = paramKeyStore.getCertificate(str);
      if (localCertificate != null) {
        try
        {
          paramCertificate.verify(localCertificate.getPublicKey());
          return new Pair(str, localCertificate);
        }
        catch (Exception localException) {}
      }
    }
    return null;
  }
  
  private X500Name getX500Name()
    throws IOException
  {
    BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(System.in));
    String str1 = "Unknown";
    String str2 = "Unknown";
    String str3 = "Unknown";
    String str4 = "Unknown";
    String str5 = "Unknown";
    String str6 = "Unknown";
    String str7 = null;
    int i = 20;
    X500Name localX500Name;
    do
    {
      if (i-- < 0) {
        throw new RuntimeException(rb.getString("Too.many.retries.program.terminated"));
      }
      str1 = inputString(localBufferedReader, rb.getString("What.is.your.first.and.last.name."), str1);
      str2 = inputString(localBufferedReader, rb.getString("What.is.the.name.of.your.organizational.unit."), str2);
      str3 = inputString(localBufferedReader, rb.getString("What.is.the.name.of.your.organization."), str3);
      str4 = inputString(localBufferedReader, rb.getString("What.is.the.name.of.your.City.or.Locality."), str4);
      str5 = inputString(localBufferedReader, rb.getString("What.is.the.name.of.your.State.or.Province."), str5);
      str6 = inputString(localBufferedReader, rb.getString("What.is.the.two.letter.country.code.for.this.unit."), str6);
      localX500Name = new X500Name(str1, str2, str3, str4, str5, str6);
      MessageFormat localMessageFormat = new MessageFormat(rb.getString("Is.name.correct."));
      Object[] arrayOfObject = { localX500Name };
      str7 = inputString(localBufferedReader, localMessageFormat.format(arrayOfObject), rb.getString("no"));
    } while ((collator.compare(str7, rb.getString("yes")) != 0) && (collator.compare(str7, rb.getString("y")) != 0));
    System.err.println();
    return localX500Name;
  }
  
  private String inputString(BufferedReader paramBufferedReader, String paramString1, String paramString2)
    throws IOException
  {
    System.err.println(paramString1);
    MessageFormat localMessageFormat = new MessageFormat(rb.getString(".defaultValue."));
    Object[] arrayOfObject = { paramString2 };
    System.err.print(localMessageFormat.format(arrayOfObject));
    System.err.flush();
    String str = paramBufferedReader.readLine();
    if ((str == null) || (collator.compare(str, "") == 0)) {
      str = paramString2;
    }
    return str;
  }
  
  private void dumpCert(Certificate paramCertificate, PrintStream paramPrintStream)
    throws IOException, CertificateException
  {
    if (rfc)
    {
      paramPrintStream.println("-----BEGIN CERTIFICATE-----");
      paramPrintStream.println(Base64.getMimeEncoder(64, CRLF).encodeToString(paramCertificate.getEncoded()));
      paramPrintStream.println("-----END CERTIFICATE-----");
    }
    else
    {
      paramPrintStream.write(paramCertificate.getEncoded());
    }
  }
  
  private void byte2hex(byte paramByte, StringBuffer paramStringBuffer)
  {
    char[] arrayOfChar = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
    int i = (paramByte & 0xF0) >> 4;
    int j = paramByte & 0xF;
    paramStringBuffer.append(arrayOfChar[i]);
    paramStringBuffer.append(arrayOfChar[j]);
  }
  
  private String toHexString(byte[] paramArrayOfByte)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    int i = paramArrayOfByte.length;
    for (int j = 0; j < i; j++)
    {
      byte2hex(paramArrayOfByte[j], localStringBuffer);
      if (j < i - 1) {
        localStringBuffer.append(":");
      }
    }
    return localStringBuffer.toString();
  }
  
  private Pair<Key, char[]> recoverKey(String paramString, char[] paramArrayOfChar1, char[] paramArrayOfChar2)
    throws Exception
  {
    Key localKey = null;
    MessageFormat localMessageFormat;
    Object[] arrayOfObject;
    if (!keyStore.containsAlias(paramString))
    {
      localMessageFormat = new MessageFormat(rb.getString("Alias.alias.does.not.exist"));
      arrayOfObject = new Object[] { paramString };
      throw new Exception(localMessageFormat.format(arrayOfObject));
    }
    if ((!keyStore.entryInstanceOf(paramString, KeyStore.PrivateKeyEntry.class)) && (!keyStore.entryInstanceOf(paramString, KeyStore.SecretKeyEntry.class)))
    {
      localMessageFormat = new MessageFormat(rb.getString("Alias.alias.has.no.key"));
      arrayOfObject = new Object[] { paramString };
      throw new Exception(localMessageFormat.format(arrayOfObject));
    }
    if (paramArrayOfChar2 == null) {
      try
      {
        localKey = keyStore.getKey(paramString, paramArrayOfChar1);
        paramArrayOfChar2 = paramArrayOfChar1;
        passwords.add(paramArrayOfChar2);
      }
      catch (UnrecoverableKeyException localUnrecoverableKeyException)
      {
        if (!token)
        {
          paramArrayOfChar2 = getKeyPasswd(paramString, null, null);
          localKey = keyStore.getKey(paramString, paramArrayOfChar2);
        }
        else
        {
          throw localUnrecoverableKeyException;
        }
      }
    } else {
      localKey = keyStore.getKey(paramString, paramArrayOfChar2);
    }
    return Pair.of(localKey, paramArrayOfChar2);
  }
  
  private Pair<KeyStore.Entry, char[]> recoverEntry(KeyStore paramKeyStore, String paramString, char[] paramArrayOfChar1, char[] paramArrayOfChar2)
    throws Exception
  {
    Object localObject2;
    if (!paramKeyStore.containsAlias(paramString))
    {
      localObject1 = new MessageFormat(rb.getString("Alias.alias.does.not.exist"));
      localObject2 = new Object[] { paramString };
      throw new Exception(((MessageFormat)localObject1).format(localObject2));
    }
    Object localObject1 = null;
    try
    {
      localObject2 = paramKeyStore.getEntry(paramString, (KeyStore.ProtectionParameter)localObject1);
      paramArrayOfChar2 = null;
    }
    catch (UnrecoverableEntryException localUnrecoverableEntryException1)
    {
      if (("PKCS11".equalsIgnoreCase(paramKeyStore.getType())) || (KeyStoreUtil.isWindowsKeyStore(paramKeyStore.getType()))) {
        throw localUnrecoverableEntryException1;
      }
      if (paramArrayOfChar2 != null)
      {
        localObject1 = new KeyStore.PasswordProtection(paramArrayOfChar2);
        localObject2 = paramKeyStore.getEntry(paramString, (KeyStore.ProtectionParameter)localObject1);
      }
      else
      {
        try
        {
          localObject1 = new KeyStore.PasswordProtection(paramArrayOfChar1);
          localObject2 = paramKeyStore.getEntry(paramString, (KeyStore.ProtectionParameter)localObject1);
          paramArrayOfChar2 = paramArrayOfChar1;
        }
        catch (UnrecoverableEntryException localUnrecoverableEntryException2)
        {
          if ("PKCS12".equalsIgnoreCase(paramKeyStore.getType())) {
            throw localUnrecoverableEntryException2;
          }
          paramArrayOfChar2 = getKeyPasswd(paramString, null, null);
          localObject1 = new KeyStore.PasswordProtection(paramArrayOfChar2);
          localObject2 = paramKeyStore.getEntry(paramString, (KeyStore.ProtectionParameter)localObject1);
        }
      }
    }
    return Pair.of(localObject2, paramArrayOfChar2);
  }
  
  private String getCertFingerPrint(String paramString, Certificate paramCertificate)
    throws Exception
  {
    byte[] arrayOfByte1 = paramCertificate.getEncoded();
    MessageDigest localMessageDigest = MessageDigest.getInstance(paramString);
    byte[] arrayOfByte2 = localMessageDigest.digest(arrayOfByte1);
    return toHexString(arrayOfByte2);
  }
  
  private void printNoIntegrityWarning()
  {
    System.err.println();
    System.err.println(rb.getString(".WARNING.WARNING.WARNING."));
    System.err.println(rb.getString(".The.integrity.of.the.information.stored.in.your.keystore."));
    System.err.println(rb.getString(".WARNING.WARNING.WARNING."));
    System.err.println();
  }
  
  private Certificate[] validateReply(String paramString, Certificate paramCertificate, Certificate[] paramArrayOfCertificate)
    throws Exception
  {
    checkWeak(rb.getString("reply"), paramArrayOfCertificate);
    PublicKey localPublicKey = paramCertificate.getPublicKey();
    for (int i = 0; (i < paramArrayOfCertificate.length) && (!localPublicKey.equals(paramArrayOfCertificate[i].getPublicKey())); i++) {}
    if (i == paramArrayOfCertificate.length)
    {
      localObject1 = new MessageFormat(rb.getString("Certificate.reply.does.not.contain.public.key.for.alias."));
      localObject2 = new Object[] { paramString };
      throw new Exception(((MessageFormat)localObject1).format(localObject2));
    }
    Object localObject1 = paramArrayOfCertificate[0];
    paramArrayOfCertificate[0] = paramArrayOfCertificate[i];
    paramArrayOfCertificate[i] = localObject1;
    Object localObject2 = (X509Certificate)paramArrayOfCertificate[0];
    for (i = 1; i < paramArrayOfCertificate.length - 1; i++)
    {
      for (int j = i; j < paramArrayOfCertificate.length; j++) {
        if (signedBy((X509Certificate)localObject2, (X509Certificate)paramArrayOfCertificate[j]))
        {
          localObject1 = paramArrayOfCertificate[i];
          paramArrayOfCertificate[i] = paramArrayOfCertificate[j];
          paramArrayOfCertificate[j] = localObject1;
          localObject2 = (X509Certificate)paramArrayOfCertificate[i];
          break;
        }
      }
      if (j == paramArrayOfCertificate.length) {
        throw new Exception(rb.getString("Incomplete.certificate.chain.in.reply"));
      }
    }
    if (noprompt) {
      return paramArrayOfCertificate;
    }
    Certificate localCertificate = paramArrayOfCertificate[(paramArrayOfCertificate.length - 1)];
    int k = 1;
    Pair localPair = getSigner(localCertificate, keyStore);
    if ((localPair == null) && (trustcacerts) && (caks != null))
    {
      localPair = getSigner(localCertificate, caks);
      k = 0;
    }
    Object localObject3;
    if (localPair == null)
    {
      System.err.println();
      System.err.println(rb.getString("Top.level.certificate.in.reply."));
      printX509Cert((X509Certificate)localCertificate, System.out);
      System.err.println();
      System.err.print(rb.getString(".is.not.trusted."));
      printWeakWarnings(true);
      localObject3 = getYesNoReply(rb.getString("Install.reply.anyway.no."));
      if ("NO".equals(localObject3)) {
        return null;
      }
    }
    else if (snd != localCertificate)
    {
      localObject3 = new Certificate[paramArrayOfCertificate.length + 1];
      System.arraycopy(paramArrayOfCertificate, 0, localObject3, 0, paramArrayOfCertificate.length);
      localObject3[(localObject3.length - 1)] = ((Certificate)snd);
      paramArrayOfCertificate = (Certificate[])localObject3;
      checkWeak(String.format(rb.getString(k != 0 ? "alias.in.keystore" : "alias.in.cacerts"), new Object[] { fst }), (Certificate)snd);
    }
    return paramArrayOfCertificate;
  }
  
  private Certificate[] establishCertChain(Certificate paramCertificate1, Certificate paramCertificate2)
    throws Exception
  {
    if (paramCertificate1 != null)
    {
      localObject1 = paramCertificate1.getPublicKey();
      localObject2 = paramCertificate2.getPublicKey();
      if (!localObject1.equals(localObject2)) {
        throw new Exception(rb.getString("Public.keys.in.reply.and.keystore.don.t.match"));
      }
      if (paramCertificate2.equals(paramCertificate1)) {
        throw new Exception(rb.getString("Certificate.reply.and.certificate.in.keystore.are.identical"));
      }
    }
    Object localObject1 = null;
    if (keyStore.size() > 0)
    {
      localObject1 = new Hashtable(11);
      keystorecerts2Hashtable(keyStore, (Hashtable)localObject1);
    }
    if ((trustcacerts) && (caks != null) && (caks.size() > 0))
    {
      if (localObject1 == null) {
        localObject1 = new Hashtable(11);
      }
      keystorecerts2Hashtable(caks, (Hashtable)localObject1);
    }
    Object localObject2 = new Vector(2);
    if (buildChain(new Pair(rb.getString("the.input"), (X509Certificate)paramCertificate2), (Vector)localObject2, (Hashtable)localObject1))
    {
      Object localObject3 = ((Vector)localObject2).iterator();
      while (((Iterator)localObject3).hasNext())
      {
        Pair localPair = (Pair)((Iterator)localObject3).next();
        checkWeak((String)fst, (Certificate)snd);
      }
      localObject3 = new Certificate[((Vector)localObject2).size()];
      int i = 0;
      for (int j = ((Vector)localObject2).size() - 1; j >= 0; j--)
      {
        localObject3[i] = ((Certificate)elementAtsnd);
        i++;
      }
      return (Certificate[])localObject3;
    }
    throw new Exception(rb.getString("Failed.to.establish.chain.from.reply"));
  }
  
  private boolean buildChain(Pair<String, X509Certificate> paramPair, Vector<Pair<String, X509Certificate>> paramVector, Hashtable<Principal, Vector<Pair<String, X509Certificate>>> paramHashtable)
  {
    if (isSelfSigned((X509Certificate)snd))
    {
      paramVector.addElement(paramPair);
      return true;
    }
    Principal localPrincipal = ((X509Certificate)snd).getIssuerDN();
    Vector localVector = (Vector)paramHashtable.get(localPrincipal);
    if (localVector == null) {
      return false;
    }
    Enumeration localEnumeration = localVector.elements();
    while (localEnumeration.hasMoreElements())
    {
      Pair localPair = (Pair)localEnumeration.nextElement();
      PublicKey localPublicKey = ((X509Certificate)snd).getPublicKey();
      try
      {
        ((X509Certificate)snd).verify(localPublicKey);
      }
      catch (Exception localException) {}
      continue;
      if (buildChain(localPair, paramVector, paramHashtable))
      {
        paramVector.addElement(paramPair);
        return true;
      }
    }
    return false;
  }
  
  private String getYesNoReply(String paramString)
    throws IOException
  {
    String str = null;
    int i = 20;
    do
    {
      if (i-- < 0) {
        throw new RuntimeException(rb.getString("Too.many.retries.program.terminated"));
      }
      System.err.print(paramString);
      System.err.flush();
      str = new BufferedReader(new InputStreamReader(System.in)).readLine();
      if ((collator.compare(str, "") == 0) || (collator.compare(str, rb.getString("n")) == 0) || (collator.compare(str, rb.getString("no")) == 0))
      {
        str = "NO";
      }
      else if ((collator.compare(str, rb.getString("y")) == 0) || (collator.compare(str, rb.getString("yes")) == 0))
      {
        str = "YES";
      }
      else
      {
        System.err.println(rb.getString("Wrong.answer.try.again"));
        str = null;
      }
    } while (str == null);
    return str;
  }
  
  private void keystorecerts2Hashtable(KeyStore paramKeyStore, Hashtable<Principal, Vector<Pair<String, X509Certificate>>> paramHashtable)
    throws Exception
  {
    Enumeration localEnumeration = paramKeyStore.aliases();
    while (localEnumeration.hasMoreElements())
    {
      String str = (String)localEnumeration.nextElement();
      Certificate localCertificate = paramKeyStore.getCertificate(str);
      if (localCertificate != null)
      {
        Principal localPrincipal = ((X509Certificate)localCertificate).getSubjectDN();
        Pair localPair = new Pair(String.format(rb.getString(paramKeyStore == caks ? "alias.in.cacerts" : "alias.in.keystore"), new Object[] { str }), (X509Certificate)localCertificate);
        Vector localVector = (Vector)paramHashtable.get(localPrincipal);
        if (localVector == null)
        {
          localVector = new Vector();
          localVector.addElement(localPair);
        }
        else if (!localVector.contains(localPair))
        {
          localVector.addElement(localPair);
        }
        paramHashtable.put(localPrincipal, localVector);
      }
    }
  }
  
  private static Date getStartDate(String paramString)
    throws IOException
  {
    GregorianCalendar localGregorianCalendar = new GregorianCalendar();
    if (paramString != null)
    {
      IOException localIOException = new IOException(rb.getString("Illegal.startdate.value"));
      int i = paramString.length();
      if (i == 0) {
        throw localIOException;
      }
      if ((paramString.charAt(0) == '-') || (paramString.charAt(0) == '+'))
      {
        int m;
        for (int j = 0; j < i; j = m + 1)
        {
          int k = 0;
          switch (paramString.charAt(j))
          {
          case '+': 
            k = 1;
            break;
          case '-': 
            k = -1;
            break;
          default: 
            throw localIOException;
          }
          for (m = j + 1; m < i; m++)
          {
            n = paramString.charAt(m);
            if ((n < 48) || (n > 57)) {
              break;
            }
          }
          if (m == j + 1) {
            throw localIOException;
          }
          int n = Integer.parseInt(paramString.substring(j + 1, m));
          if (m >= i) {
            throw localIOException;
          }
          int i1 = 0;
          switch (paramString.charAt(m))
          {
          case 'y': 
            i1 = 1;
            break;
          case 'm': 
            i1 = 2;
            break;
          case 'd': 
            i1 = 5;
            break;
          case 'H': 
            i1 = 10;
            break;
          case 'M': 
            i1 = 12;
            break;
          case 'S': 
            i1 = 13;
            break;
          default: 
            throw localIOException;
          }
          localGregorianCalendar.add(i1, k * n);
        }
      }
      else
      {
        String str1 = null;
        String str2 = null;
        if (i == 19)
        {
          str1 = paramString.substring(0, 10);
          str2 = paramString.substring(11);
          if (paramString.charAt(10) != ' ') {
            throw localIOException;
          }
        }
        else if (i == 10)
        {
          str1 = paramString;
        }
        else if (i == 8)
        {
          str2 = paramString;
        }
        else
        {
          throw localIOException;
        }
        if (str1 != null) {
          if (str1.matches("\\d\\d\\d\\d\\/\\d\\d\\/\\d\\d")) {
            localGregorianCalendar.set(Integer.valueOf(str1.substring(0, 4)).intValue(), Integer.valueOf(str1.substring(5, 7)).intValue() - 1, Integer.valueOf(str1.substring(8, 10)).intValue());
          } else {
            throw localIOException;
          }
        }
        if (str2 != null) {
          if (str2.matches("\\d\\d:\\d\\d:\\d\\d"))
          {
            localGregorianCalendar.set(11, Integer.valueOf(str2.substring(0, 2)).intValue());
            localGregorianCalendar.set(12, Integer.valueOf(str2.substring(0, 2)).intValue());
            localGregorianCalendar.set(13, Integer.valueOf(str2.substring(0, 2)).intValue());
            localGregorianCalendar.set(14, 0);
          }
          else
          {
            throw localIOException;
          }
        }
      }
    }
    return localGregorianCalendar.getTime();
  }
  
  private static int oneOf(String paramString, String... paramVarArgs)
    throws Exception
  {
    int[] arrayOfInt = new int[paramVarArgs.length];
    int i = 0;
    int j = Integer.MAX_VALUE;
    for (int k = 0; k < paramVarArgs.length; k++)
    {
      localObject1 = paramVarArgs[k];
      if (localObject1 == null)
      {
        j = k;
      }
      else if (((String)localObject1).toLowerCase(Locale.ENGLISH).startsWith(paramString.toLowerCase(Locale.ENGLISH)))
      {
        arrayOfInt[(i++)] = k;
      }
      else
      {
        localObject2 = new StringBuffer();
        m = 1;
        for (char c : ((String)localObject1).toCharArray()) {
          if (m != 0)
          {
            ((StringBuffer)localObject2).append(c);
            m = 0;
          }
          else if (!Character.isLowerCase(c))
          {
            ((StringBuffer)localObject2).append(c);
          }
        }
        if (((StringBuffer)localObject2).toString().equalsIgnoreCase(paramString)) {
          arrayOfInt[(i++)] = k;
        }
      }
    }
    if (i == 0) {
      return -1;
    }
    if (i == 1) {
      return arrayOfInt[0];
    }
    if (arrayOfInt[1] > j) {
      return arrayOfInt[0];
    }
    StringBuffer localStringBuffer = new StringBuffer();
    Object localObject1 = new MessageFormat(rb.getString("command.{0}.is.ambiguous."));
    Object localObject2 = { paramString };
    localStringBuffer.append(((MessageFormat)localObject1).format(localObject2));
    localStringBuffer.append("\n    ");
    for (int m = 0; (m < i) && (arrayOfInt[m] < j); m++)
    {
      localStringBuffer.append(' ');
      localStringBuffer.append(paramVarArgs[arrayOfInt[m]]);
    }
    throw new Exception(localStringBuffer.toString());
  }
  
  private GeneralName createGeneralName(String paramString1, String paramString2)
    throws Exception
  {
    int i = oneOf(paramString1, new String[] { "EMAIL", "URI", "DNS", "IP", "OID" });
    if (i < 0) {
      throw new Exception(rb.getString("Unrecognized.GeneralName.type.") + paramString1);
    }
    Object localObject;
    switch (i)
    {
    case 0: 
      localObject = new RFC822Name(paramString2);
      break;
    case 1: 
      localObject = new URIName(paramString2);
      break;
    case 2: 
      localObject = new DNSName(paramString2);
      break;
    case 3: 
      localObject = new IPAddressName(paramString2);
      break;
    default: 
      localObject = new OIDName(paramString2);
    }
    return new GeneralName((GeneralNameInterface)localObject);
  }
  
  private ObjectIdentifier findOidForExtName(String paramString)
    throws Exception
  {
    switch (oneOf(paramString, extSupported))
    {
    case 0: 
      return PKIXExtensions.BasicConstraints_Id;
    case 1: 
      return PKIXExtensions.KeyUsage_Id;
    case 2: 
      return PKIXExtensions.ExtendedKeyUsage_Id;
    case 3: 
      return PKIXExtensions.SubjectAlternativeName_Id;
    case 4: 
      return PKIXExtensions.IssuerAlternativeName_Id;
    case 5: 
      return PKIXExtensions.SubjectInfoAccess_Id;
    case 6: 
      return PKIXExtensions.AuthInfoAccess_Id;
    case 8: 
      return PKIXExtensions.CRLDistributionPoints_Id;
    }
    return new ObjectIdentifier(paramString);
  }
  
  private CertificateExtensions createV3Extensions(CertificateExtensions paramCertificateExtensions1, CertificateExtensions paramCertificateExtensions2, List<String> paramList, PublicKey paramPublicKey1, PublicKey paramPublicKey2)
    throws Exception
  {
    if ((paramCertificateExtensions2 != null) && (paramCertificateExtensions1 != null)) {
      throw new Exception("One of request and original should be null.");
    }
    if (paramCertificateExtensions2 == null) {
      paramCertificateExtensions2 = new CertificateExtensions();
    }
    try
    {
      String str1;
      Object localObject1;
      Object localObject2;
      int i;
      int j;
      if (paramCertificateExtensions1 != null)
      {
        localIterator = paramList.iterator();
        while (localIterator.hasNext())
        {
          str1 = (String)localIterator.next();
          if (str1.toLowerCase(Locale.ENGLISH).startsWith("honored="))
          {
            localObject1 = Arrays.asList(str1.toLowerCase(Locale.ENGLISH).substring(8).split(","));
            if (((List)localObject1).contains("all")) {
              paramCertificateExtensions2 = paramCertificateExtensions1;
            }
            localObject2 = ((List)localObject1).iterator();
            while (((Iterator)localObject2).hasNext())
            {
              String str2 = (String)((Iterator)localObject2).next();
              if (!str2.equals("all"))
              {
                i = 1;
                j = -1;
                String str3 = null;
                if (str2.startsWith("-"))
                {
                  i = 0;
                  str3 = str2.substring(1);
                }
                else
                {
                  int m = str2.indexOf(':');
                  if (m >= 0)
                  {
                    str3 = str2.substring(0, m);
                    j = oneOf(str2.substring(m + 1), new String[] { "critical", "non-critical" });
                    if (j == -1) {
                      throw new Exception(rb.getString("Illegal.value.") + str2);
                    }
                  }
                }
                String str4 = paramCertificateExtensions1.getNameByOid(findOidForExtName(str3));
                if (i != 0)
                {
                  Extension localExtension = paramCertificateExtensions1.get(str4);
                  if (((!localExtension.isCritical()) && (j == 0)) || ((localExtension.isCritical()) && (j == 1)))
                  {
                    localExtension = Extension.newExtension(localExtension.getExtensionId(), !localExtension.isCritical(), localExtension.getExtensionValue());
                    paramCertificateExtensions2.set(str4, localExtension);
                  }
                }
                else
                {
                  paramCertificateExtensions2.delete(str4);
                }
              }
            }
            break;
          }
        }
      }
      Iterator localIterator = paramList.iterator();
      while (localIterator.hasNext())
      {
        str1 = (String)localIterator.next();
        boolean bool1 = false;
        i = str1.indexOf('=');
        if (i >= 0)
        {
          localObject1 = str1.substring(0, i);
          localObject2 = str1.substring(i + 1);
        }
        else
        {
          localObject1 = str1;
          localObject2 = null;
        }
        j = ((String)localObject1).indexOf(':');
        if (j >= 0)
        {
          if (oneOf(((String)localObject1).substring(j + 1), new String[] { "critical" }) == 0) {
            bool1 = true;
          }
          localObject1 = ((String)localObject1).substring(0, j);
        }
        if (!((String)localObject1).equalsIgnoreCase("honored"))
        {
          int k = oneOf((String)localObject1, extSupported);
          Object localObject4;
          int i1;
          int i3;
          String str5;
          Object localObject3;
          int i5;
          String str6;
          String str9;
          switch (k)
          {
          case 0: 
            int n = -1;
            boolean bool2 = false;
            if (localObject2 == null)
            {
              bool2 = true;
            }
            else
            {
              try
              {
                n = Integer.parseInt((String)localObject2);
                bool2 = true;
              }
              catch (NumberFormatException localNumberFormatException)
              {
                localObject4 = ((String)localObject2).split(",");
                i1 = localObject4.length;
                i3 = 0;
              }
              while (i3 < i1)
              {
                str5 = localObject4[i3];
                String[] arrayOfString = str5.split(":");
                if (arrayOfString.length != 2) {
                  throw new Exception(rb.getString("Illegal.value.") + str1);
                }
                if (arrayOfString[0].equalsIgnoreCase("ca")) {
                  bool2 = Boolean.parseBoolean(arrayOfString[1]);
                } else if (arrayOfString[0].equalsIgnoreCase("pathlen")) {
                  n = Integer.parseInt(arrayOfString[1]);
                } else {
                  throw new Exception(rb.getString("Illegal.value.") + str1);
                }
                i3++;
              }
            }
            paramCertificateExtensions2.set("BasicConstraints", new BasicConstraintsExtension(Boolean.valueOf(bool1), bool2, n));
            break;
          case 1: 
            if (localObject2 != null)
            {
              localObject3 = new boolean[9];
              for (str5 : ((String)localObject2).split(","))
              {
                i5 = oneOf(str5, new String[] { "digitalSignature", "nonRepudiation", "keyEncipherment", "dataEncipherment", "keyAgreement", "keyCertSign", "cRLSign", "encipherOnly", "decipherOnly", "contentCommitment" });
                if (i5 < 0) {
                  throw new Exception(rb.getString("Unknown.keyUsage.type.") + str5);
                }
                if (i5 == 9) {
                  i5 = 1;
                }
                localObject3[i5] = 1;
              }
              localObject4 = new KeyUsageExtension((boolean[])localObject3);
              paramCertificateExtensions2.set("KeyUsage", Extension.newExtension(((KeyUsageExtension)localObject4).getExtensionId(), bool1, ((KeyUsageExtension)localObject4).getExtensionValue()));
            }
            else
            {
              throw new Exception(rb.getString("Illegal.value.") + str1);
            }
            break;
          case 2: 
            if (localObject2 != null)
            {
              localObject3 = new Vector();
              for (str5 : ((String)localObject2).split(","))
              {
                i5 = oneOf(str5, new String[] { "anyExtendedKeyUsage", "serverAuth", "clientAuth", "codeSigning", "emailProtection", "", "", "", "timeStamping", "OCSPSigning" });
                if (i5 < 0) {
                  try
                  {
                    ((Vector)localObject3).add(new ObjectIdentifier(str5));
                  }
                  catch (Exception localException1)
                  {
                    throw new Exception(rb.getString("Unknown.extendedkeyUsage.type.") + str5);
                  }
                } else if (i5 == 0) {
                  ((Vector)localObject3).add(new ObjectIdentifier("2.5.29.37.0"));
                } else {
                  ((Vector)localObject3).add(new ObjectIdentifier("1.3.6.1.5.5.7.3." + i5));
                }
              }
              paramCertificateExtensions2.set("ExtendedKeyUsage", new ExtendedKeyUsageExtension(Boolean.valueOf(bool1), (Vector)localObject3));
            }
            else
            {
              throw new Exception(rb.getString("Illegal.value.") + str1);
            }
            break;
          case 3: 
          case 4: 
            if (localObject2 != null)
            {
              localObject3 = ((String)localObject2).split(",");
              localObject4 = new GeneralNames();
              for (str6 : localObject3)
              {
                j = str6.indexOf(':');
                if (j < 0) {
                  throw new Exception("Illegal item " + str6 + " in " + str1);
                }
                String str7 = str6.substring(0, j);
                str9 = str6.substring(j + 1);
                ((GeneralNames)localObject4).add(createGeneralName(str7, str9));
              }
              if (k == 3) {
                paramCertificateExtensions2.set("SubjectAlternativeName", new SubjectAlternativeNameExtension(Boolean.valueOf(bool1), (GeneralNames)localObject4));
              } else {
                paramCertificateExtensions2.set("IssuerAlternativeName", new IssuerAlternativeNameExtension(Boolean.valueOf(bool1), (GeneralNames)localObject4));
              }
            }
            else
            {
              throw new Exception(rb.getString("Illegal.value.") + str1);
            }
            break;
          case 5: 
          case 6: 
            if (bool1) {
              throw new Exception(rb.getString("This.extension.cannot.be.marked.as.critical.") + str1);
            }
            if (localObject2 != null)
            {
              localObject3 = new ArrayList();
              localObject4 = ((String)localObject2).split(",");
              for (str6 : localObject4)
              {
                j = str6.indexOf(':');
                int i7 = str6.indexOf(':', j + 1);
                if ((j < 0) || (i7 < 0)) {
                  throw new Exception(rb.getString("Illegal.value.") + str1);
                }
                str9 = str6.substring(0, j);
                String str10 = str6.substring(j + 1, i7);
                String str11 = str6.substring(i7 + 1);
                int i10 = oneOf(str9, new String[] { "", "ocsp", "caIssuers", "timeStamping", "", "caRepository" });
                ObjectIdentifier localObjectIdentifier;
                if (i10 < 0) {
                  try
                  {
                    localObjectIdentifier = new ObjectIdentifier(str9);
                  }
                  catch (Exception localException2)
                  {
                    throw new Exception(rb.getString("Unknown.AccessDescription.type.") + str9);
                  }
                } else {
                  localObjectIdentifier = new ObjectIdentifier("1.3.6.1.5.5.7.48." + i10);
                }
                ((List)localObject3).add(new AccessDescription(localObjectIdentifier, createGeneralName(str10, str11)));
              }
              if (k == 5) {
                paramCertificateExtensions2.set("SubjectInfoAccess", new SubjectInfoAccessExtension((List)localObject3));
              } else {
                paramCertificateExtensions2.set("AuthorityInfoAccess", new AuthorityInfoAccessExtension((List)localObject3));
              }
            }
            else
            {
              throw new Exception(rb.getString("Illegal.value.") + str1);
            }
            break;
          case 8: 
            if (localObject2 != null)
            {
              localObject3 = ((String)localObject2).split(",");
              localObject4 = new GeneralNames();
              for (str6 : localObject3)
              {
                j = str6.indexOf(':');
                if (j < 0) {
                  throw new Exception("Illegal item " + str6 + " in " + str1);
                }
                String str8 = str6.substring(0, j);
                str9 = str6.substring(j + 1);
                ((GeneralNames)localObject4).add(createGeneralName(str8, str9));
              }
              paramCertificateExtensions2.set("CRLDistributionPoints", new CRLDistributionPointsExtension(bool1, Collections.singletonList(new DistributionPoint((GeneralNames)localObject4, null, null))));
            }
            else
            {
              throw new Exception(rb.getString("Illegal.value.") + str1);
            }
            break;
          case -1: 
            localObject3 = new ObjectIdentifier((String)localObject1);
            localObject4 = null;
            if (localObject2 != null)
            {
              localObject4 = new byte[((String)localObject2).length() / 2 + 1];
              int i2 = 0;
              for (int i8 : ((String)localObject2).toCharArray())
              {
                int i9;
                if ((i8 >= 48) && (i8 <= 57))
                {
                  i9 = i8 - 48;
                }
                else if ((i8 >= 65) && (i8 <= 70))
                {
                  i9 = i8 - 65 + 10;
                }
                else
                {
                  if ((i8 < 97) || (i8 > 102)) {
                    continue;
                  }
                  i9 = i8 - 97 + 10;
                }
                if (i2 % 2 == 0)
                {
                  localObject4[(i2 / 2)] = ((byte)(i9 << 4));
                }
                else
                {
                  int tmp2446_2445 = (i2 / 2);
                  Object tmp2446_2440 = localObject4;
                  tmp2446_2440[tmp2446_2445] = ((byte)(tmp2446_2440[tmp2446_2445] + i9));
                }
                i2++;
              }
              if (i2 % 2 != 0) {
                throw new Exception(rb.getString("Odd.number.of.hex.digits.found.") + str1);
              }
              localObject4 = Arrays.copyOf((byte[])localObject4, i2 / 2);
            }
            else
            {
              localObject4 = new byte[0];
            }
            paramCertificateExtensions2.set(((ObjectIdentifier)localObject3).toString(), new Extension((ObjectIdentifier)localObject3, bool1, new DerValue((byte)4, (byte[])localObject4).toByteArray()));
            break;
          case 7: 
          default: 
            throw new Exception(rb.getString("Unknown.extension.type.") + str1);
          }
        }
      }
      paramCertificateExtensions2.set("SubjectKeyIdentifier", new SubjectKeyIdentifierExtension(new KeyIdentifier(paramPublicKey1).getIdentifier()));
      if ((paramPublicKey2 != null) && (!paramPublicKey1.equals(paramPublicKey2))) {
        paramCertificateExtensions2.set("AuthorityKeyIdentifier", new AuthorityKeyIdentifierExtension(new KeyIdentifier(paramPublicKey2), null, null));
      }
    }
    catch (IOException localIOException)
    {
      throw new RuntimeException(localIOException);
    }
    return paramCertificateExtensions2;
  }
  
  private boolean isTrustedCert(Certificate paramCertificate)
    throws KeyStoreException
  {
    if ((caks != null) && (caks.getCertificateAlias(paramCertificate) != null)) {
      return true;
    }
    String str = keyStore.getCertificateAlias(paramCertificate);
    return (str != null) && (keyStore.isCertificateEntry(str));
  }
  
  private void checkWeak(String paramString1, String paramString2, Key paramKey)
  {
    if ((paramString2 != null) && (!DISABLED_CHECK.permits(SIG_PRIMITIVE_SET, paramString2, null))) {
      weakWarnings.add(String.format(rb.getString("whose.sigalg.risk"), new Object[] { paramString1, paramString2 }));
    }
    if ((paramKey != null) && (!DISABLED_CHECK.permits(SIG_PRIMITIVE_SET, paramKey))) {
      weakWarnings.add(String.format(rb.getString("whose.key.risk"), new Object[] { paramString1, String.format(rb.getString("key.bit"), new Object[] { Integer.valueOf(KeyUtil.getKeySize(paramKey)), paramKey.getAlgorithm() }) }));
    }
  }
  
  private void checkWeak(String paramString, Certificate[] paramArrayOfCertificate)
    throws KeyStoreException
  {
    for (int i = 0; i < paramArrayOfCertificate.length; i++)
    {
      Certificate localCertificate = paramArrayOfCertificate[i];
      if ((localCertificate instanceof X509Certificate))
      {
        X509Certificate localX509Certificate = (X509Certificate)localCertificate;
        String str = paramString;
        if (paramArrayOfCertificate.length > 1) {
          str = oneInMany(paramString, i, paramArrayOfCertificate.length);
        }
        checkWeak(str, localX509Certificate);
      }
    }
  }
  
  private void checkWeak(String paramString, Certificate paramCertificate)
    throws KeyStoreException
  {
    if ((paramCertificate instanceof X509Certificate))
    {
      X509Certificate localX509Certificate = (X509Certificate)paramCertificate;
      String str = isTrustedCert(paramCertificate) ? null : localX509Certificate.getSigAlgName();
      checkWeak(paramString, str, localX509Certificate.getPublicKey());
    }
  }
  
  private void checkWeak(String paramString, PKCS10 paramPKCS10)
  {
    checkWeak(paramString, paramPKCS10.getSigAlg(), paramPKCS10.getSubjectPublicKeyInfo());
  }
  
  private void checkWeak(String paramString, CRL paramCRL, Key paramKey)
  {
    if ((paramCRL instanceof X509CRLImpl))
    {
      X509CRLImpl localX509CRLImpl = (X509CRLImpl)paramCRL;
      checkWeak(paramString, localX509CRLImpl.getSigAlgName(), paramKey);
    }
  }
  
  private void printWeakWarnings(boolean paramBoolean)
  {
    if ((!weakWarnings.isEmpty()) && (!nowarn))
    {
      System.err.println("\nWarning:");
      Iterator localIterator = weakWarnings.iterator();
      while (localIterator.hasNext())
      {
        String str = (String)localIterator.next();
        System.err.println(str);
      }
      if (paramBoolean) {
        System.err.println();
      }
    }
    weakWarnings.clear();
  }
  
  private void usage()
  {
    Object localObject1;
    int j;
    if (command != null)
    {
      System.err.println("keytool " + command + rb.getString(".OPTION."));
      System.err.println();
      System.err.println(rb.getString(command.description));
      System.err.println();
      System.err.println(rb.getString("Options."));
      System.err.println();
      localObject1 = new String[command.options.length];
      String[] arrayOfString = new String[command.options.length];
      j = 0;
      int k = 0;
      for (int m = 0; m < localObject1.length; m++)
      {
        Option localOption = command.options[m];
        localObject1[m] = localOption.toString();
        if (arg != null)
        {
          int tmp178_176 = m;
          Object tmp178_175 = localObject1;
          tmp178_175[tmp178_176] = (tmp178_175[tmp178_176] + " " + arg);
        }
        if (localObject1[m].length() > k) {
          k = localObject1[m].length();
        }
        arrayOfString[m] = rb.getString(description);
      }
      for (m = 0; m < localObject1.length; m++) {
        System.err.printf(" %-" + k + "s  %s\n", new Object[] { localObject1[m], arrayOfString[m] });
      }
      System.err.println();
      System.err.println(rb.getString("Use.keytool.help.for.all.available.commands"));
    }
    else
    {
      System.err.println(rb.getString("Key.and.Certificate.Management.Tool"));
      System.err.println();
      System.err.println(rb.getString("Commands."));
      System.err.println();
      for (Object localObject2 : Command.values())
      {
        if (localObject2 == Command.KEYCLONE) {
          break;
        }
        System.err.printf(" %-20s%s\n", new Object[] { localObject2, rb.getString(description) });
      }
      System.err.println();
      System.err.println(rb.getString("Use.keytool.command.name.help.for.usage.of.command.name"));
    }
  }
  
  private void tinyHelp()
  {
    usage();
    if (debug) {
      throw new RuntimeException("NO BIG ERROR, SORRY");
    }
    System.exit(1);
  }
  
  private void errorNeedArgument(String paramString)
  {
    Object[] arrayOfObject = { paramString };
    System.err.println(new MessageFormat(rb.getString("Command.option.flag.needs.an.argument.")).format(arrayOfObject));
    tinyHelp();
  }
  
  private char[] getPass(String paramString1, String paramString2)
  {
    char[] arrayOfChar = KeyStoreUtil.getPassWithModifier(paramString1, paramString2, rb);
    if (arrayOfChar != null) {
      return arrayOfChar;
    }
    tinyHelp();
    return null;
  }
  
  static
  {
    collator.setStrength(0);
  }
  
  static enum Command
  {
    CERTREQ("Generates.a.certificate.request", new Main.Option[] { Main.Option.ALIAS, Main.Option.SIGALG, Main.Option.FILEOUT, Main.Option.KEYPASS, Main.Option.KEYSTORE, Main.Option.DNAME, Main.Option.STOREPASS, Main.Option.STORETYPE, Main.Option.PROVIDERNAME, Main.Option.PROVIDERCLASS, Main.Option.PROVIDERARG, Main.Option.PROVIDERPATH, Main.Option.V, Main.Option.PROTECTED }),  CHANGEALIAS("Changes.an.entry.s.alias", new Main.Option[] { Main.Option.ALIAS, Main.Option.DESTALIAS, Main.Option.KEYPASS, Main.Option.KEYSTORE, Main.Option.STOREPASS, Main.Option.STORETYPE, Main.Option.PROVIDERNAME, Main.Option.PROVIDERCLASS, Main.Option.PROVIDERARG, Main.Option.PROVIDERPATH, Main.Option.V, Main.Option.PROTECTED }),  DELETE("Deletes.an.entry", new Main.Option[] { Main.Option.ALIAS, Main.Option.KEYSTORE, Main.Option.STOREPASS, Main.Option.STORETYPE, Main.Option.PROVIDERNAME, Main.Option.PROVIDERCLASS, Main.Option.PROVIDERARG, Main.Option.PROVIDERPATH, Main.Option.V, Main.Option.PROTECTED }),  EXPORTCERT("Exports.certificate", new Main.Option[] { Main.Option.RFC, Main.Option.ALIAS, Main.Option.FILEOUT, Main.Option.KEYSTORE, Main.Option.STOREPASS, Main.Option.STORETYPE, Main.Option.PROVIDERNAME, Main.Option.PROVIDERCLASS, Main.Option.PROVIDERARG, Main.Option.PROVIDERPATH, Main.Option.V, Main.Option.PROTECTED }),  GENKEYPAIR("Generates.a.key.pair", new Main.Option[] { Main.Option.ALIAS, Main.Option.KEYALG, Main.Option.KEYSIZE, Main.Option.SIGALG, Main.Option.DESTALIAS, Main.Option.DNAME, Main.Option.STARTDATE, Main.Option.EXT, Main.Option.VALIDITY, Main.Option.KEYPASS, Main.Option.KEYSTORE, Main.Option.STOREPASS, Main.Option.STORETYPE, Main.Option.PROVIDERNAME, Main.Option.PROVIDERCLASS, Main.Option.PROVIDERARG, Main.Option.PROVIDERPATH, Main.Option.V, Main.Option.PROTECTED }),  GENSECKEY("Generates.a.secret.key", new Main.Option[] { Main.Option.ALIAS, Main.Option.KEYPASS, Main.Option.KEYALG, Main.Option.KEYSIZE, Main.Option.KEYSTORE, Main.Option.STOREPASS, Main.Option.STORETYPE, Main.Option.PROVIDERNAME, Main.Option.PROVIDERCLASS, Main.Option.PROVIDERARG, Main.Option.PROVIDERPATH, Main.Option.V, Main.Option.PROTECTED }),  GENCERT("Generates.certificate.from.a.certificate.request", new Main.Option[] { Main.Option.RFC, Main.Option.INFILE, Main.Option.OUTFILE, Main.Option.ALIAS, Main.Option.SIGALG, Main.Option.DNAME, Main.Option.STARTDATE, Main.Option.EXT, Main.Option.VALIDITY, Main.Option.KEYPASS, Main.Option.KEYSTORE, Main.Option.STOREPASS, Main.Option.STORETYPE, Main.Option.PROVIDERNAME, Main.Option.PROVIDERCLASS, Main.Option.PROVIDERARG, Main.Option.PROVIDERPATH, Main.Option.V, Main.Option.PROTECTED }),  IMPORTCERT("Imports.a.certificate.or.a.certificate.chain", new Main.Option[] { Main.Option.NOPROMPT, Main.Option.TRUSTCACERTS, Main.Option.PROTECTED, Main.Option.ALIAS, Main.Option.FILEIN, Main.Option.KEYPASS, Main.Option.KEYSTORE, Main.Option.STOREPASS, Main.Option.STORETYPE, Main.Option.PROVIDERNAME, Main.Option.PROVIDERCLASS, Main.Option.PROVIDERARG, Main.Option.PROVIDERPATH, Main.Option.V }),  IMPORTPASS("Imports.a.password", new Main.Option[] { Main.Option.ALIAS, Main.Option.KEYPASS, Main.Option.KEYALG, Main.Option.KEYSIZE, Main.Option.KEYSTORE, Main.Option.STOREPASS, Main.Option.STORETYPE, Main.Option.PROVIDERNAME, Main.Option.PROVIDERCLASS, Main.Option.PROVIDERARG, Main.Option.PROVIDERPATH, Main.Option.V, Main.Option.PROTECTED }),  IMPORTKEYSTORE("Imports.one.or.all.entries.from.another.keystore", new Main.Option[] { Main.Option.SRCKEYSTORE, Main.Option.DESTKEYSTORE, Main.Option.SRCSTORETYPE, Main.Option.DESTSTORETYPE, Main.Option.SRCSTOREPASS, Main.Option.DESTSTOREPASS, Main.Option.SRCPROTECTED, Main.Option.SRCPROVIDERNAME, Main.Option.DESTPROVIDERNAME, Main.Option.SRCALIAS, Main.Option.DESTALIAS, Main.Option.SRCKEYPASS, Main.Option.DESTKEYPASS, Main.Option.NOPROMPT, Main.Option.PROVIDERCLASS, Main.Option.PROVIDERARG, Main.Option.PROVIDERPATH, Main.Option.V }),  KEYPASSWD("Changes.the.key.password.of.an.entry", new Main.Option[] { Main.Option.ALIAS, Main.Option.KEYPASS, Main.Option.NEW, Main.Option.KEYSTORE, Main.Option.STOREPASS, Main.Option.STORETYPE, Main.Option.PROVIDERNAME, Main.Option.PROVIDERCLASS, Main.Option.PROVIDERARG, Main.Option.PROVIDERPATH, Main.Option.V }),  LIST("Lists.entries.in.a.keystore", new Main.Option[] { Main.Option.RFC, Main.Option.ALIAS, Main.Option.KEYSTORE, Main.Option.STOREPASS, Main.Option.STORETYPE, Main.Option.PROVIDERNAME, Main.Option.PROVIDERCLASS, Main.Option.PROVIDERARG, Main.Option.PROVIDERPATH, Main.Option.V, Main.Option.PROTECTED }),  PRINTCERT("Prints.the.content.of.a.certificate", new Main.Option[] { Main.Option.RFC, Main.Option.FILEIN, Main.Option.SSLSERVER, Main.Option.JARFILE, Main.Option.V }),  PRINTCERTREQ("Prints.the.content.of.a.certificate.request", new Main.Option[] { Main.Option.FILEIN, Main.Option.V }),  PRINTCRL("Prints.the.content.of.a.CRL.file", new Main.Option[] { Main.Option.FILEIN, Main.Option.V }),  STOREPASSWD("Changes.the.store.password.of.a.keystore", new Main.Option[] { Main.Option.NEW, Main.Option.KEYSTORE, Main.Option.STOREPASS, Main.Option.STORETYPE, Main.Option.PROVIDERNAME, Main.Option.PROVIDERCLASS, Main.Option.PROVIDERARG, Main.Option.PROVIDERPATH, Main.Option.V }),  KEYCLONE("Clones.a.key.entry", new Main.Option[] { Main.Option.ALIAS, Main.Option.DESTALIAS, Main.Option.KEYPASS, Main.Option.NEW, Main.Option.STORETYPE, Main.Option.KEYSTORE, Main.Option.STOREPASS, Main.Option.PROVIDERNAME, Main.Option.PROVIDERCLASS, Main.Option.PROVIDERARG, Main.Option.PROVIDERPATH, Main.Option.V }),  SELFCERT("Generates.a.self.signed.certificate", new Main.Option[] { Main.Option.ALIAS, Main.Option.SIGALG, Main.Option.DNAME, Main.Option.STARTDATE, Main.Option.VALIDITY, Main.Option.KEYPASS, Main.Option.STORETYPE, Main.Option.KEYSTORE, Main.Option.STOREPASS, Main.Option.PROVIDERNAME, Main.Option.PROVIDERCLASS, Main.Option.PROVIDERARG, Main.Option.PROVIDERPATH, Main.Option.V }),  GENCRL("Generates.CRL", new Main.Option[] { Main.Option.RFC, Main.Option.FILEOUT, Main.Option.ID, Main.Option.ALIAS, Main.Option.SIGALG, Main.Option.EXT, Main.Option.KEYPASS, Main.Option.KEYSTORE, Main.Option.STOREPASS, Main.Option.STORETYPE, Main.Option.PROVIDERNAME, Main.Option.PROVIDERCLASS, Main.Option.PROVIDERARG, Main.Option.PROVIDERPATH, Main.Option.V, Main.Option.PROTECTED }),  IDENTITYDB("Imports.entries.from.a.JDK.1.1.x.style.identity.database", new Main.Option[] { Main.Option.FILEIN, Main.Option.STORETYPE, Main.Option.KEYSTORE, Main.Option.STOREPASS, Main.Option.PROVIDERNAME, Main.Option.PROVIDERCLASS, Main.Option.PROVIDERARG, Main.Option.PROVIDERPATH, Main.Option.V });
    
    final String description;
    final Main.Option[] options;
    
    private Command(String paramString, Main.Option... paramVarArgs)
    {
      description = paramString;
      options = paramVarArgs;
    }
    
    public String toString()
    {
      return "-" + name().toLowerCase(Locale.ENGLISH);
    }
  }
  
  static enum Option
  {
    ALIAS("alias", "<alias>", "alias.name.of.the.entry.to.process"),  DESTALIAS("destalias", "<destalias>", "destination.alias"),  DESTKEYPASS("destkeypass", "<arg>", "destination.key.password"),  DESTKEYSTORE("destkeystore", "<destkeystore>", "destination.keystore.name"),  DESTPROTECTED("destprotected", null, "destination.keystore.password.protected"),  DESTPROVIDERNAME("destprovidername", "<destprovidername>", "destination.keystore.provider.name"),  DESTSTOREPASS("deststorepass", "<arg>", "destination.keystore.password"),  DESTSTORETYPE("deststoretype", "<deststoretype>", "destination.keystore.type"),  DNAME("dname", "<dname>", "distinguished.name"),  EXT("ext", "<value>", "X.509.extension"),  FILEOUT("file", "<filename>", "output.file.name"),  FILEIN("file", "<filename>", "input.file.name"),  ID("id", "<id:reason>", "Serial.ID.of.cert.to.revoke"),  INFILE("infile", "<filename>", "input.file.name"),  KEYALG("keyalg", "<keyalg>", "key.algorithm.name"),  KEYPASS("keypass", "<arg>", "key.password"),  KEYSIZE("keysize", "<keysize>", "key.bit.size"),  KEYSTORE("keystore", "<keystore>", "keystore.name"),  NEW("new", "<arg>", "new.password"),  NOPROMPT("noprompt", null, "do.not.prompt"),  OUTFILE("outfile", "<filename>", "output.file.name"),  PROTECTED("protected", null, "password.through.protected.mechanism"),  PROVIDERARG("providerarg", "<arg>", "provider.argument"),  PROVIDERCLASS("providerclass", "<providerclass>", "provider.class.name"),  PROVIDERNAME("providername", "<providername>", "provider.name"),  PROVIDERPATH("providerpath", "<pathlist>", "provider.classpath"),  RFC("rfc", null, "output.in.RFC.style"),  SIGALG("sigalg", "<sigalg>", "signature.algorithm.name"),  SRCALIAS("srcalias", "<srcalias>", "source.alias"),  SRCKEYPASS("srckeypass", "<arg>", "source.key.password"),  SRCKEYSTORE("srckeystore", "<srckeystore>", "source.keystore.name"),  SRCPROTECTED("srcprotected", null, "source.keystore.password.protected"),  SRCPROVIDERNAME("srcprovidername", "<srcprovidername>", "source.keystore.provider.name"),  SRCSTOREPASS("srcstorepass", "<arg>", "source.keystore.password"),  SRCSTORETYPE("srcstoretype", "<srcstoretype>", "source.keystore.type"),  SSLSERVER("sslserver", "<server[:port]>", "SSL.server.host.and.port"),  JARFILE("jarfile", "<filename>", "signed.jar.file"),  STARTDATE("startdate", "<startdate>", "certificate.validity.start.date.time"),  STOREPASS("storepass", "<arg>", "keystore.password"),  STORETYPE("storetype", "<storetype>", "keystore.type"),  TRUSTCACERTS("trustcacerts", null, "trust.certificates.from.cacerts"),  V("v", null, "verbose.output"),  VALIDITY("validity", "<valDays>", "validity.number.of.days");
    
    final String name;
    final String arg;
    final String description;
    
    private Option(String paramString1, String paramString2, String paramString3)
    {
      name = paramString1;
      arg = paramString2;
      description = paramString3;
    }
    
    public String toString()
    {
      return "-" + name;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\tools\keytool\Main.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */