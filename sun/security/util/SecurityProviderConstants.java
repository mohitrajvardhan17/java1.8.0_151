package sun.security.util;

import java.security.InvalidParameterException;
import java.util.regex.PatternSyntaxException;
import sun.security.action.GetPropertyAction;

public final class SecurityProviderConstants
{
  private static final Debug debug = Debug.getInstance("jca", "ProviderConfig");
  public static final int DEF_DSA_KEY_SIZE;
  public static final int DEF_RSA_KEY_SIZE;
  public static final int DEF_DH_KEY_SIZE;
  public static final int DEF_EC_KEY_SIZE;
  private static final String KEY_LENGTH_PROP = "jdk.security.defaultKeySize";
  
  private SecurityProviderConstants() {}
  
  public static final int getDefDSASubprimeSize(int paramInt)
  {
    if (paramInt <= 1024) {
      return 160;
    }
    if (paramInt == 2048) {
      return 224;
    }
    if (paramInt == 3072) {
      return 256;
    }
    throw new InvalidParameterException("Invalid DSA Prime Size: " + paramInt);
  }
  
  static
  {
    String str1 = GetPropertyAction.privilegedGetProperty("jdk.security.defaultKeySize");
    int i = 1024;
    int j = 1024;
    int k = 1024;
    int m = 256;
    if (str1 != null) {
      try
      {
        String[] arrayOfString1 = str1.split(",");
        for (String str2 : arrayOfString1)
        {
          String[] arrayOfString3 = str2.split(":");
          if (arrayOfString3.length != 2)
          {
            if (debug != null) {
              debug.println("Ignoring invalid pair in jdk.security.defaultKeySize property: " + str2);
            }
          }
          else
          {
            String str3 = arrayOfString3[0].trim().toUpperCase();
            int i2 = -1;
            try
            {
              i2 = Integer.parseInt(arrayOfString3[1].trim());
            }
            catch (NumberFormatException localNumberFormatException)
            {
              if (debug != null) {
                debug.println("Ignoring invalid value in jdk.security.defaultKeySize property: " + str2);
              }
              continue;
            }
            if (str3.equals("DSA"))
            {
              i = i2;
            }
            else if (str3.equals("RSA"))
            {
              j = i2;
            }
            else if (str3.equals("DH"))
            {
              k = i2;
            }
            else if (str3.equals("EC"))
            {
              m = i2;
            }
            else
            {
              if (debug == null) {
                continue;
              }
              debug.println("Ignoring unsupported algo in jdk.security.defaultKeySize property: " + str2);
              continue;
            }
            if (debug != null) {
              debug.println("Overriding default " + str3 + " keysize with value from " + "jdk.security.defaultKeySize" + " property: " + i2);
            }
          }
        }
      }
      catch (PatternSyntaxException localPatternSyntaxException)
      {
        if (debug != null) {
          debug.println("Unexpected exception while parsing jdk.security.defaultKeySize property: " + localPatternSyntaxException);
        }
      }
    }
    DEF_DSA_KEY_SIZE = i;
    DEF_RSA_KEY_SIZE = j;
    DEF_DH_KEY_SIZE = k;
    DEF_EC_KEY_SIZE = m;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\util\SecurityProviderConstants.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */