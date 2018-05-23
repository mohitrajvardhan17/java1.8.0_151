package sun.security.util;

import java.io.PrintStream;
import java.math.BigInteger;
import java.security.AccessController;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import sun.security.action.GetPropertyAction;

public class Debug
{
  private String prefix;
  private static String args = (String)AccessController.doPrivileged(new GetPropertyAction("java.security.debug"));
  private static final char[] hexDigits = "0123456789abcdef".toCharArray();
  
  public Debug() {}
  
  public static void Help()
  {
    System.err.println();
    System.err.println("all           turn on all debugging");
    System.err.println("access        print all checkPermission results");
    System.err.println("certpath      PKIX CertPathBuilder and");
    System.err.println("              CertPathValidator debugging");
    System.err.println("combiner      SubjectDomainCombiner debugging");
    System.err.println("gssloginconfig");
    System.err.println("              GSS LoginConfigImpl debugging");
    System.err.println("configfile    JAAS ConfigFile loading");
    System.err.println("configparser  JAAS ConfigFile parsing");
    System.err.println("jar           jar verification");
    System.err.println("logincontext  login context results");
    System.err.println("jca           JCA engine class debugging");
    System.err.println("policy        loading and granting");
    System.err.println("provider      security provider debugging");
    System.err.println("pkcs11        PKCS11 session manager debugging");
    System.err.println("pkcs11keystore");
    System.err.println("              PKCS11 KeyStore debugging");
    System.err.println("sunpkcs11     SunPKCS11 provider debugging");
    System.err.println("scl           permissions SecureClassLoader assigns");
    System.err.println("ts            timestamping");
    System.err.println();
    System.err.println("The following can be used with access:");
    System.err.println();
    System.err.println("stack         include stack trace");
    System.err.println("domain        dump all domains in context");
    System.err.println("failure       before throwing exception, dump stack");
    System.err.println("              and domain that didn't have permission");
    System.err.println();
    System.err.println("The following can be used with stack and domain:");
    System.err.println();
    System.err.println("permission=<classname>");
    System.err.println("              only dump output if specified permission");
    System.err.println("              is being checked");
    System.err.println("codebase=<URL>");
    System.err.println("              only dump output if specified codebase");
    System.err.println("              is being checked");
    System.err.println();
    System.err.println("The following can be used with provider:");
    System.err.println();
    System.err.println("engine=<engines>");
    System.err.println("              only dump output for the specified list");
    System.err.println("              of JCA engines. Supported values:");
    System.err.println("              Cipher, KeyAgreement, KeyGenerator,");
    System.err.println("              KeyPairGenerator, KeyStore, Mac,");
    System.err.println("              MessageDigest, SecureRandom, Signature.");
    System.err.println();
    System.err.println("Note: Separate multiple options with a comma");
    System.exit(0);
  }
  
  public static Debug getInstance(String paramString)
  {
    return getInstance(paramString, paramString);
  }
  
  public static Debug getInstance(String paramString1, String paramString2)
  {
    if (isOn(paramString1))
    {
      Debug localDebug = new Debug();
      prefix = paramString2;
      return localDebug;
    }
    return null;
  }
  
  public static boolean isOn(String paramString)
  {
    if (args == null) {
      return false;
    }
    if (args.indexOf("all") != -1) {
      return true;
    }
    return args.indexOf(paramString) != -1;
  }
  
  public void println(String paramString)
  {
    System.err.println(prefix + ": " + paramString);
  }
  
  public void println()
  {
    System.err.println(prefix + ":");
  }
  
  public static void println(String paramString1, String paramString2)
  {
    System.err.println(paramString1 + ": " + paramString2);
  }
  
  public static String toHexString(BigInteger paramBigInteger)
  {
    String str = paramBigInteger.toString(16);
    StringBuffer localStringBuffer = new StringBuffer(str.length() * 2);
    if (str.startsWith("-"))
    {
      localStringBuffer.append("   -");
      str = str.substring(1);
    }
    else
    {
      localStringBuffer.append("    ");
    }
    if (str.length() % 2 != 0) {
      str = "0" + str;
    }
    int i = 0;
    while (i < str.length())
    {
      localStringBuffer.append(str.substring(i, i + 2));
      i += 2;
      if (i != str.length()) {
        if (i % 64 == 0) {
          localStringBuffer.append("\n    ");
        } else if (i % 8 == 0) {
          localStringBuffer.append(" ");
        }
      }
    }
    return localStringBuffer.toString();
  }
  
  private static String marshal(String paramString)
  {
    if (paramString != null)
    {
      StringBuffer localStringBuffer1 = new StringBuffer();
      Object localObject = new StringBuffer(paramString);
      String str1 = "[Pp][Ee][Rr][Mm][Ii][Ss][Ss][Ii][Oo][Nn]=";
      String str2 = "permission=";
      String str3 = str1 + "[a-zA-Z_$][a-zA-Z0-9_$]*([.][a-zA-Z_$][a-zA-Z0-9_$]*)*";
      Pattern localPattern = Pattern.compile(str3);
      Matcher localMatcher = localPattern.matcher((CharSequence)localObject);
      StringBuffer localStringBuffer2 = new StringBuffer();
      String str4;
      while (localMatcher.find())
      {
        str4 = localMatcher.group();
        localStringBuffer1.append(str4.replaceFirst(str1, str2));
        localStringBuffer1.append("  ");
        localMatcher.appendReplacement(localStringBuffer2, "");
      }
      localMatcher.appendTail(localStringBuffer2);
      localObject = localStringBuffer2;
      str1 = "[Cc][Oo][Dd][Ee][Bb][Aa][Ss][Ee]=";
      str2 = "codebase=";
      str3 = str1 + "[^, ;]*";
      localPattern = Pattern.compile(str3);
      localMatcher = localPattern.matcher((CharSequence)localObject);
      localStringBuffer2 = new StringBuffer();
      while (localMatcher.find())
      {
        str4 = localMatcher.group();
        localStringBuffer1.append(str4.replaceFirst(str1, str2));
        localStringBuffer1.append("  ");
        localMatcher.appendReplacement(localStringBuffer2, "");
      }
      localMatcher.appendTail(localStringBuffer2);
      localObject = localStringBuffer2;
      localStringBuffer1.append(((StringBuffer)localObject).toString().toLowerCase(Locale.ENGLISH));
      return localStringBuffer1.toString();
    }
    return null;
  }
  
  public static String toString(byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte == null) {
      return "(null)";
    }
    StringBuilder localStringBuilder = new StringBuilder(paramArrayOfByte.length * 3);
    for (int i = 0; i < paramArrayOfByte.length; i++)
    {
      int j = paramArrayOfByte[i] & 0xFF;
      if (i != 0) {
        localStringBuilder.append(':');
      }
      localStringBuilder.append(hexDigits[(j >>> 4)]);
      localStringBuilder.append(hexDigits[(j & 0xF)]);
    }
    return localStringBuilder.toString();
  }
  
  static
  {
    String str = (String)AccessController.doPrivileged(new GetPropertyAction("java.security.auth.debug"));
    if (args == null) {
      args = str;
    } else if (str != null) {
      args = args + "," + str;
    }
    if (args != null)
    {
      args = marshal(args);
      if (args.equals("help")) {
        Help();
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\util\Debug.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */