package sun.security.krb5;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;
import sun.net.dns.ResolverConfiguration;
import sun.security.action.GetPropertyAction;
import sun.security.krb5.internal.Krb5;
import sun.security.krb5.internal.crypto.EType;

public class Config
{
  private static Config singleton = null;
  private Hashtable<String, Object> stanzaTable = new Hashtable();
  private static boolean DEBUG = Krb5.DEBUG;
  private static final int BASE16_0 = 1;
  private static final int BASE16_1 = 16;
  private static final int BASE16_2 = 256;
  private static final int BASE16_3 = 4096;
  private final String defaultRealm;
  private final String defaultKDC;
  
  private static native String getWindowsDirectory(boolean paramBoolean);
  
  public static synchronized Config getInstance()
    throws KrbException
  {
    if (singleton == null) {
      singleton = new Config();
    }
    return singleton;
  }
  
  public static synchronized void refresh()
    throws KrbException
  {
    singleton = new Config();
    KdcComm.initStatic();
    EType.initStatic();
    Checksum.initStatic();
  }
  
  private static boolean isMacosLionOrBetter()
  {
    String str1 = getProperty("os.name");
    if (!str1.contains("OS X")) {
      return false;
    }
    String str2 = getProperty("os.version");
    String[] arrayOfString = str2.split("\\.");
    if (!arrayOfString[0].equals("10")) {
      return false;
    }
    if (arrayOfString.length < 2) {
      return false;
    }
    try
    {
      int i = Integer.parseInt(arrayOfString[1]);
      if (i >= 7) {
        return true;
      }
    }
    catch (NumberFormatException localNumberFormatException) {}
    return false;
  }
  
  private Config()
    throws KrbException
  {
    String str1 = getProperty("java.security.krb5.kdc");
    if (str1 != null) {
      defaultKDC = str1.replace(':', ' ');
    } else {
      defaultKDC = null;
    }
    defaultRealm = getProperty("java.security.krb5.realm");
    if (((defaultKDC == null) && (defaultRealm != null)) || ((defaultRealm == null) && (defaultKDC != null))) {
      throw new KrbException("System property java.security.krb5.kdc and java.security.krb5.realm both must be set or neither must be set.");
    }
    try
    {
      String str2 = getJavaFileName();
      List localList;
      if (str2 != null)
      {
        localList = loadConfigFile(str2);
        stanzaTable = parseStanzaTable(localList);
        if (DEBUG) {
          System.out.println("Loaded from Java config");
        }
      }
      else
      {
        int i = 0;
        if (isMacosLionOrBetter()) {
          try
          {
            stanzaTable = SCDynamicStoreConfig.getConfig();
            if (DEBUG) {
              System.out.println("Loaded from SCDynamicStoreConfig");
            }
            i = 1;
          }
          catch (IOException localIOException2) {}
        }
        if (i == 0)
        {
          str2 = getNativeFileName();
          localList = loadConfigFile(str2);
          stanzaTable = parseStanzaTable(localList);
          if (DEBUG) {
            System.out.println("Loaded from native config");
          }
        }
      }
    }
    catch (IOException localIOException1) {}
  }
  
  public String get(String... paramVarArgs)
  {
    Vector localVector = getString0(paramVarArgs);
    if (localVector == null) {
      return null;
    }
    return (String)localVector.lastElement();
  }
  
  private Boolean getBooleanObject(String... paramVarArgs)
  {
    String str1 = get(paramVarArgs);
    if (str1 == null) {
      return null;
    }
    switch (str1.toLowerCase(Locale.US))
    {
    case "true": 
    case "yes": 
      return Boolean.TRUE;
    case "false": 
    case "no": 
      return Boolean.FALSE;
    }
    return null;
  }
  
  public String getAll(String... paramVarArgs)
  {
    Vector localVector = getString0(paramVarArgs);
    if (localVector == null) {
      return null;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    int i = 1;
    Iterator localIterator = localVector.iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      if (i != 0)
      {
        localStringBuilder.append(str);
        i = 0;
      }
      else
      {
        localStringBuilder.append(' ').append(str);
      }
    }
    return localStringBuilder.toString();
  }
  
  public boolean exists(String... paramVarArgs)
  {
    return get0(paramVarArgs) != null;
  }
  
  private Vector<String> getString0(String... paramVarArgs)
  {
    try
    {
      return (Vector)get0(paramVarArgs);
    }
    catch (ClassCastException localClassCastException)
    {
      throw new IllegalArgumentException(localClassCastException);
    }
  }
  
  private Object get0(String... paramVarArgs)
  {
    Object localObject = stanzaTable;
    try
    {
      for (String str : paramVarArgs)
      {
        localObject = ((Hashtable)localObject).get(str);
        if (localObject == null) {
          return null;
        }
      }
      return localObject;
    }
    catch (ClassCastException localClassCastException)
    {
      throw new IllegalArgumentException(localClassCastException);
    }
  }
  
  public int getIntValue(String... paramVarArgs)
  {
    String str = get(paramVarArgs);
    int i = Integer.MIN_VALUE;
    if (str != null) {
      try
      {
        i = parseIntValue(str);
      }
      catch (NumberFormatException localNumberFormatException)
      {
        if (DEBUG)
        {
          System.out.println("Exception in getting value of " + Arrays.toString(paramVarArgs) + " " + localNumberFormatException.getMessage());
          System.out.println("Setting " + Arrays.toString(paramVarArgs) + " to minimum value");
        }
        i = Integer.MIN_VALUE;
      }
    }
    return i;
  }
  
  public boolean getBooleanValue(String... paramVarArgs)
  {
    String str = get(paramVarArgs);
    return (str != null) && (str.equalsIgnoreCase("true"));
  }
  
  private int parseIntValue(String paramString)
    throws NumberFormatException
  {
    int i = 0;
    String str;
    if (paramString.startsWith("+"))
    {
      str = paramString.substring(1);
      return Integer.parseInt(str);
    }
    if (paramString.startsWith("0x"))
    {
      str = paramString.substring(2);
      char[] arrayOfChar = str.toCharArray();
      if (arrayOfChar.length > 8) {
        throw new NumberFormatException();
      }
      for (int j = 0; j < arrayOfChar.length; j++)
      {
        int k = arrayOfChar.length - j - 1;
        switch (arrayOfChar[j])
        {
        case '0': 
          i += 0;
          break;
        case '1': 
          i += 1 * getBase(k);
          break;
        case '2': 
          i += 2 * getBase(k);
          break;
        case '3': 
          i += 3 * getBase(k);
          break;
        case '4': 
          i += 4 * getBase(k);
          break;
        case '5': 
          i += 5 * getBase(k);
          break;
        case '6': 
          i += 6 * getBase(k);
          break;
        case '7': 
          i += 7 * getBase(k);
          break;
        case '8': 
          i += 8 * getBase(k);
          break;
        case '9': 
          i += 9 * getBase(k);
          break;
        case 'A': 
        case 'a': 
          i += 10 * getBase(k);
          break;
        case 'B': 
        case 'b': 
          i += 11 * getBase(k);
          break;
        case 'C': 
        case 'c': 
          i += 12 * getBase(k);
          break;
        case 'D': 
        case 'd': 
          i += 13 * getBase(k);
          break;
        case 'E': 
        case 'e': 
          i += 14 * getBase(k);
          break;
        case 'F': 
        case 'f': 
          i += 15 * getBase(k);
          break;
        case ':': 
        case ';': 
        case '<': 
        case '=': 
        case '>': 
        case '?': 
        case '@': 
        case 'G': 
        case 'H': 
        case 'I': 
        case 'J': 
        case 'K': 
        case 'L': 
        case 'M': 
        case 'N': 
        case 'O': 
        case 'P': 
        case 'Q': 
        case 'R': 
        case 'S': 
        case 'T': 
        case 'U': 
        case 'V': 
        case 'W': 
        case 'X': 
        case 'Y': 
        case 'Z': 
        case '[': 
        case '\\': 
        case ']': 
        case '^': 
        case '_': 
        case '`': 
        default: 
          throw new NumberFormatException("Invalid numerical format");
        }
      }
      if (i < 0) {
        throw new NumberFormatException("Data overflow.");
      }
    }
    else
    {
      i = Integer.parseInt(paramString);
    }
    return i;
  }
  
  private int getBase(int paramInt)
  {
    int i = 16;
    switch (paramInt)
    {
    case 0: 
      i = 1;
      break;
    case 1: 
      i = 16;
      break;
    case 2: 
      i = 256;
      break;
    case 3: 
      i = 4096;
      break;
    default: 
      for (int j = 1; j < paramInt; j++) {
        i *= 16;
      }
    }
    return i;
  }
  
  private List<String> loadConfigFile(final String paramString)
    throws IOException, KrbException
  {
    try
    {
      ArrayList localArrayList = new ArrayList();
      BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader((InputStream)AccessController.doPrivileged(new PrivilegedExceptionAction()
      {
        public FileInputStream run()
          throws IOException
        {
          return new FileInputStream(paramString);
        }
      })));
      Object localObject1 = null;
      try
      {
        Object localObject2 = null;
        String str1;
        while ((str1 = localBufferedReader.readLine()) != null)
        {
          str1 = str1.trim();
          if ((!str1.isEmpty()) && (!str1.startsWith("#")) && (!str1.startsWith(";"))) {
            if (str1.startsWith("["))
            {
              if (!str1.endsWith("]")) {
                throw new KrbException("Illegal config content:" + str1);
              }
              if (localObject2 != null)
              {
                localArrayList.add(localObject2);
                localArrayList.add("}");
              }
              String str2 = str1.substring(1, str1.length() - 1).trim();
              if (str2.isEmpty()) {
                throw new KrbException("Illegal config content:" + str1);
              }
              localObject2 = str2 + " = {";
            }
            else if (str1.startsWith("{"))
            {
              if (localObject2 == null) {
                throw new KrbException("Config file should not start with \"{\"");
              }
              localObject2 = (String)localObject2 + " {";
              if (str1.length() > 1)
              {
                localArrayList.add(localObject2);
                localObject2 = str1.substring(1).trim();
              }
            }
            else if (localObject2 != null)
            {
              localArrayList.add(localObject2);
              localObject2 = str1;
            }
          }
        }
        if (localObject2 != null)
        {
          localArrayList.add(localObject2);
          localArrayList.add("}");
        }
      }
      catch (Throwable localThrowable2)
      {
        localObject1 = localThrowable2;
        throw localThrowable2;
      }
      finally
      {
        if (localBufferedReader != null) {
          if (localObject1 != null) {
            try
            {
              localBufferedReader.close();
            }
            catch (Throwable localThrowable3)
            {
              ((Throwable)localObject1).addSuppressed(localThrowable3);
            }
          } else {
            localBufferedReader.close();
          }
        }
      }
      return localArrayList;
    }
    catch (PrivilegedActionException localPrivilegedActionException)
    {
      throw ((IOException)localPrivilegedActionException.getException());
    }
  }
  
  private Hashtable<String, Object> parseStanzaTable(List<String> paramList)
    throws KrbException
  {
    Object localObject1 = stanzaTable;
    Iterator localIterator = paramList.iterator();
    while (localIterator.hasNext())
    {
      String str1 = (String)localIterator.next();
      if (str1.equals("}"))
      {
        localObject1 = (Hashtable)((Hashtable)localObject1).remove(" PARENT ");
        if (localObject1 == null) {
          throw new KrbException("Unmatched close brace");
        }
      }
      else
      {
        int i = str1.indexOf('=');
        if (i < 0) {
          throw new KrbException("Illegal config content:" + str1);
        }
        String str2 = str1.substring(0, i).trim();
        String str3 = trimmed(str1.substring(i + 1));
        Object localObject2;
        if (str3.equals("{"))
        {
          if (localObject1 == stanzaTable) {
            str2 = str2.toLowerCase(Locale.US);
          }
          localObject2 = new Hashtable();
          ((Hashtable)localObject1).put(str2, localObject2);
          ((Hashtable)localObject2).put(" PARENT ", localObject1);
          localObject1 = localObject2;
        }
        else
        {
          if (((Hashtable)localObject1).containsKey(str2))
          {
            Object localObject3 = ((Hashtable)localObject1).get(str2);
            if (!(localObject3 instanceof Vector)) {
              throw new KrbException("Key " + str2 + "used for both value and section");
            }
            localObject2 = (Vector)((Hashtable)localObject1).get(str2);
          }
          else
          {
            localObject2 = new Vector();
            ((Hashtable)localObject1).put(str2, localObject2);
          }
          ((Vector)localObject2).add(str3);
        }
      }
    }
    if (localObject1 != stanzaTable) {
      throw new KrbException("Not closed");
    }
    return (Hashtable<String, Object>)localObject1;
  }
  
  private String getJavaFileName()
  {
    String str = getProperty("java.security.krb5.conf");
    if (str == null)
    {
      str = getProperty("java.home") + File.separator + "lib" + File.separator + "security" + File.separator + "krb5.conf";
      if (!fileExists(str)) {
        str = null;
      }
    }
    if (DEBUG) {
      System.out.println("Java config name: " + str);
    }
    return str;
  }
  
  private String getNativeFileName()
  {
    Object localObject = null;
    String str1 = getProperty("os.name");
    if (str1.startsWith("Windows"))
    {
      try
      {
        Credentials.ensureLoaded();
      }
      catch (Exception localException) {}
      if (Credentials.alreadyLoaded)
      {
        String str2 = getWindowsDirectory(false);
        if (str2 != null)
        {
          if (str2.endsWith("\\")) {
            str2 = str2 + "krb5.ini";
          } else {
            str2 = str2 + "\\krb5.ini";
          }
          if (fileExists(str2)) {
            localObject = str2;
          }
        }
        if (localObject == null)
        {
          str2 = getWindowsDirectory(true);
          if (str2 != null)
          {
            if (str2.endsWith("\\")) {
              str2 = str2 + "krb5.ini";
            } else {
              str2 = str2 + "\\krb5.ini";
            }
            localObject = str2;
          }
        }
      }
      if (localObject == null) {
        localObject = "c:\\winnt\\krb5.ini";
      }
    }
    else if (str1.startsWith("SunOS"))
    {
      localObject = "/etc/krb5/krb5.conf";
    }
    else if (str1.contains("OS X"))
    {
      localObject = findMacosConfigFile();
    }
    else
    {
      localObject = "/etc/krb5.conf";
    }
    if (DEBUG) {
      System.out.println("Native config name: " + (String)localObject);
    }
    return (String)localObject;
  }
  
  private static String getProperty(String paramString)
  {
    return (String)AccessController.doPrivileged(new GetPropertyAction(paramString));
  }
  
  private String findMacosConfigFile()
  {
    String str1 = getProperty("user.home");
    String str2 = str1 + "/Library/Preferences/edu.mit.Kerberos";
    if (fileExists(str2)) {
      return str2;
    }
    if (fileExists("/Library/Preferences/edu.mit.Kerberos")) {
      return "/Library/Preferences/edu.mit.Kerberos";
    }
    return "/etc/krb5.conf";
  }
  
  private static String trimmed(String paramString)
  {
    paramString = paramString.trim();
    if ((paramString.length() >= 2) && (((paramString.charAt(0) == '"') && (paramString.charAt(paramString.length() - 1) == '"')) || ((paramString.charAt(0) == '\'') && (paramString.charAt(paramString.length() - 1) == '\'')))) {
      paramString = paramString.substring(1, paramString.length() - 1).trim();
    }
    return paramString;
  }
  
  public void listTable()
  {
    System.out.println(this);
  }
  
  public int[] defaultEtype(String paramString)
    throws KrbException
  {
    String str1 = get(new String[] { "libdefaults", paramString });
    int[] arrayOfInt;
    if (str1 == null)
    {
      if (DEBUG) {
        System.out.println("Using builtin default etypes for " + paramString);
      }
      arrayOfInt = EType.getBuiltInDefaults();
    }
    else
    {
      String str2 = " ";
      for (int j = 0; j < str1.length(); j++) {
        if (str1.substring(j, j + 1).equals(","))
        {
          str2 = ",";
          break;
        }
      }
      StringTokenizer localStringTokenizer = new StringTokenizer(str1, str2);
      j = localStringTokenizer.countTokens();
      ArrayList localArrayList = new ArrayList(j);
      for (int m = 0; m < j; m++)
      {
        int k = getType(localStringTokenizer.nextToken());
        if ((k != -1) && (EType.isSupported(k))) {
          localArrayList.add(Integer.valueOf(k));
        }
      }
      if (localArrayList.isEmpty()) {
        throw new KrbException("no supported default etypes for " + paramString);
      }
      arrayOfInt = new int[localArrayList.size()];
      for (m = 0; m < arrayOfInt.length; m++) {
        arrayOfInt[m] = ((Integer)localArrayList.get(m)).intValue();
      }
    }
    if (DEBUG)
    {
      System.out.print("default etypes for " + paramString + ":");
      for (int i = 0; i < arrayOfInt.length; i++) {
        System.out.print(" " + arrayOfInt[i]);
      }
      System.out.println(".");
    }
    return arrayOfInt;
  }
  
  public static int getType(String paramString)
  {
    int i = -1;
    if (paramString == null) {
      return i;
    }
    if ((paramString.startsWith("d")) || (paramString.startsWith("D")))
    {
      if (paramString.equalsIgnoreCase("des-cbc-crc")) {
        i = 1;
      } else if (paramString.equalsIgnoreCase("des-cbc-md5")) {
        i = 3;
      } else if (paramString.equalsIgnoreCase("des-mac")) {
        i = 4;
      } else if (paramString.equalsIgnoreCase("des-mac-k")) {
        i = 5;
      } else if (paramString.equalsIgnoreCase("des-cbc-md4")) {
        i = 2;
      } else if ((paramString.equalsIgnoreCase("des3-cbc-sha1")) || (paramString.equalsIgnoreCase("des3-hmac-sha1")) || (paramString.equalsIgnoreCase("des3-cbc-sha1-kd")) || (paramString.equalsIgnoreCase("des3-cbc-hmac-sha1-kd"))) {
        i = 16;
      }
    }
    else if ((paramString.startsWith("a")) || (paramString.startsWith("A")))
    {
      if ((paramString.equalsIgnoreCase("aes128-cts")) || (paramString.equalsIgnoreCase("aes128-cts-hmac-sha1-96"))) {
        i = 17;
      } else if ((paramString.equalsIgnoreCase("aes256-cts")) || (paramString.equalsIgnoreCase("aes256-cts-hmac-sha1-96"))) {
        i = 18;
      } else if ((paramString.equalsIgnoreCase("arcfour-hmac")) || (paramString.equalsIgnoreCase("arcfour-hmac-md5"))) {
        i = 23;
      }
    }
    else if (paramString.equalsIgnoreCase("rc4-hmac")) {
      i = 23;
    } else if (paramString.equalsIgnoreCase("CRC32")) {
      i = 1;
    } else if ((paramString.startsWith("r")) || (paramString.startsWith("R")))
    {
      if (paramString.equalsIgnoreCase("rsa-md5")) {
        i = 7;
      } else if (paramString.equalsIgnoreCase("rsa-md5-des")) {
        i = 8;
      }
    }
    else if (paramString.equalsIgnoreCase("hmac-sha1-des3-kd")) {
      i = 12;
    } else if (paramString.equalsIgnoreCase("hmac-sha1-96-aes128")) {
      i = 15;
    } else if (paramString.equalsIgnoreCase("hmac-sha1-96-aes256")) {
      i = 16;
    } else if ((paramString.equalsIgnoreCase("hmac-md5-rc4")) || (paramString.equalsIgnoreCase("hmac-md5-arcfour")) || (paramString.equalsIgnoreCase("hmac-md5-enc"))) {
      i = 65398;
    } else if (paramString.equalsIgnoreCase("NULL")) {
      i = 0;
    }
    return i;
  }
  
  public void resetDefaultRealm(String paramString)
  {
    if (DEBUG) {
      System.out.println(">>> Config try resetting default kdc " + paramString);
    }
  }
  
  public boolean useAddresses()
  {
    boolean bool = false;
    String str = get(new String[] { "libdefaults", "no_addresses" });
    bool = (str != null) && (str.equalsIgnoreCase("false"));
    if (!bool)
    {
      str = get(new String[] { "libdefaults", "noaddresses" });
      bool = (str != null) && (str.equalsIgnoreCase("false"));
    }
    return bool;
  }
  
  private boolean useDNS(String paramString, boolean paramBoolean)
  {
    Boolean localBoolean = getBooleanObject(new String[] { "libdefaults", paramString });
    if (localBoolean != null) {
      return localBoolean.booleanValue();
    }
    localBoolean = getBooleanObject(new String[] { "libdefaults", "dns_fallback" });
    if (localBoolean != null) {
      return localBoolean.booleanValue();
    }
    return paramBoolean;
  }
  
  private boolean useDNS_KDC()
  {
    return useDNS("dns_lookup_kdc", true);
  }
  
  private boolean useDNS_Realm()
  {
    return useDNS("dns_lookup_realm", false);
  }
  
  public String getDefaultRealm()
    throws KrbException
  {
    if (defaultRealm != null) {
      return defaultRealm;
    }
    Object localObject = null;
    String str = get(new String[] { "libdefaults", "default_realm" });
    if ((str == null) && (useDNS_Realm())) {
      try
      {
        str = getRealmFromDNS();
      }
      catch (KrbException localKrbException1)
      {
        localObject = localKrbException1;
      }
    }
    if (str == null) {
      str = (String)AccessController.doPrivileged(new PrivilegedAction()
      {
        public String run()
        {
          String str = System.getProperty("os.name");
          if (str.startsWith("Windows")) {
            return System.getenv("USERDNSDOMAIN");
          }
          return null;
        }
      });
    }
    if (str == null)
    {
      KrbException localKrbException2 = new KrbException("Cannot locate default realm");
      if (localObject != null) {
        localKrbException2.initCause((Throwable)localObject);
      }
      throw localKrbException2;
    }
    return str;
  }
  
  public String getKDCList(String paramString)
    throws KrbException
  {
    if (paramString == null) {
      paramString = getDefaultRealm();
    }
    if (paramString.equalsIgnoreCase(defaultRealm)) {
      return defaultKDC;
    }
    Object localObject = null;
    String str = getAll(new String[] { "realms", paramString, "kdc" });
    if ((str == null) && (useDNS_KDC())) {
      try
      {
        str = getKDCFromDNS(paramString);
      }
      catch (KrbException localKrbException1)
      {
        localObject = localKrbException1;
      }
    }
    if (str == null) {
      str = (String)AccessController.doPrivileged(new PrivilegedAction()
      {
        public String run()
        {
          String str1 = System.getProperty("os.name");
          if (str1.startsWith("Windows"))
          {
            String str2 = System.getenv("LOGONSERVER");
            if ((str2 != null) && (str2.startsWith("\\\\"))) {
              str2 = str2.substring(2);
            }
            return str2;
          }
          return null;
        }
      });
    }
    if (str == null)
    {
      if (defaultKDC != null) {
        return defaultKDC;
      }
      KrbException localKrbException2 = new KrbException("Cannot locate KDC");
      if (localObject != null) {
        localKrbException2.initCause((Throwable)localObject);
      }
      throw localKrbException2;
    }
    return str;
  }
  
  private String getRealmFromDNS()
    throws KrbException
  {
    String str1 = null;
    String str2 = null;
    Object localObject;
    try
    {
      str2 = InetAddress.getLocalHost().getCanonicalHostName();
    }
    catch (UnknownHostException localUnknownHostException)
    {
      localObject = new KrbException(60, "Unable to locate Kerberos realm: " + localUnknownHostException.getMessage());
      ((KrbException)localObject).initCause(localUnknownHostException);
      throw ((Throwable)localObject);
    }
    String str3 = PrincipalName.mapHostToRealm(str2);
    if (str3 == null)
    {
      localObject = ResolverConfiguration.open().searchlist();
      Iterator localIterator = ((List)localObject).iterator();
      while (localIterator.hasNext())
      {
        String str4 = (String)localIterator.next();
        str1 = checkRealm(str4);
        if (str1 != null) {
          break;
        }
      }
    }
    else
    {
      str1 = checkRealm(str3);
    }
    if (str1 == null) {
      throw new KrbException(60, "Unable to locate Kerberos realm");
    }
    return str1;
  }
  
  private static String checkRealm(String paramString)
  {
    if (DEBUG) {
      System.out.println("getRealmFromDNS: trying " + paramString);
    }
    String[] arrayOfString = null;
    for (String str = paramString; (arrayOfString == null) && (str != null); str = Realm.parseRealmComponent(str)) {
      arrayOfString = KrbServiceLocator.getKerberosService(str);
    }
    if (arrayOfString != null) {
      for (int i = 0; i < arrayOfString.length; i++) {
        if (arrayOfString[i].equalsIgnoreCase(paramString)) {
          return arrayOfString[i];
        }
      }
    }
    return null;
  }
  
  private String getKDCFromDNS(String paramString)
    throws KrbException
  {
    String str = "";
    String[] arrayOfString = null;
    if (DEBUG) {
      System.out.println("getKDCFromDNS using UDP");
    }
    arrayOfString = KrbServiceLocator.getKerberosService(paramString, "_udp");
    if (arrayOfString == null)
    {
      if (DEBUG) {
        System.out.println("getKDCFromDNS using TCP");
      }
      arrayOfString = KrbServiceLocator.getKerberosService(paramString, "_tcp");
    }
    if (arrayOfString == null) {
      throw new KrbException(60, "Unable to locate KDC for realm " + paramString);
    }
    if (arrayOfString.length == 0) {
      return null;
    }
    for (int i = 0; i < arrayOfString.length; i++) {
      str = str + arrayOfString[i].trim() + " ";
    }
    str = str.trim();
    if (str.equals("")) {
      return null;
    }
    return str;
  }
  
  private boolean fileExists(String paramString)
  {
    return ((Boolean)AccessController.doPrivileged(new FileExistsAction(paramString))).booleanValue();
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    toStringInternal("", stanzaTable, localStringBuffer);
    return localStringBuffer.toString();
  }
  
  private static void toStringInternal(String paramString, Object paramObject, StringBuffer paramStringBuffer)
  {
    if ((paramObject instanceof String))
    {
      paramStringBuffer.append(paramObject).append('\n');
    }
    else
    {
      Object localObject1;
      Object localObject2;
      if ((paramObject instanceof Hashtable))
      {
        localObject1 = (Hashtable)paramObject;
        paramStringBuffer.append("{\n");
        Iterator localIterator = ((Hashtable)localObject1).keySet().iterator();
        while (localIterator.hasNext())
        {
          localObject2 = localIterator.next();
          paramStringBuffer.append(paramString).append("    ").append(localObject2).append(" = ");
          toStringInternal(paramString + "    ", ((Hashtable)localObject1).get(localObject2), paramStringBuffer);
        }
        paramStringBuffer.append(paramString).append("}\n");
      }
      else if ((paramObject instanceof Vector))
      {
        localObject1 = (Vector)paramObject;
        paramStringBuffer.append("[");
        int i = 1;
        for (Object localObject3 : ((Vector)localObject1).toArray())
        {
          if (i == 0) {
            paramStringBuffer.append(",");
          }
          paramStringBuffer.append(localObject3);
          i = 0;
        }
        paramStringBuffer.append("]\n");
      }
    }
  }
  
  static class FileExistsAction
    implements PrivilegedAction<Boolean>
  {
    private String fileName;
    
    public FileExistsAction(String paramString)
    {
      fileName = paramString;
    }
    
    public Boolean run()
    {
      return Boolean.valueOf(new File(fileName).exists());
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\krb5\Config.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */