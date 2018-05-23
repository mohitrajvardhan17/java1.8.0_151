package sun.security.util;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class AlgorithmDecomposer
{
  private static final Pattern transPattern = Pattern.compile("/");
  private static final Pattern pattern = Pattern.compile("with|and", 2);
  
  public AlgorithmDecomposer() {}
  
  private static Set<String> decomposeImpl(String paramString)
  {
    String[] arrayOfString1 = transPattern.split(paramString);
    HashSet localHashSet = new HashSet();
    for (String str1 : arrayOfString1) {
      if ((str1 != null) && (str1.length() != 0))
      {
        String[] arrayOfString3 = pattern.split(str1);
        for (String str2 : arrayOfString3) {
          if ((str2 != null) && (str2.length() != 0)) {
            localHashSet.add(str2);
          }
        }
      }
    }
    return localHashSet;
  }
  
  public Set<String> decompose(String paramString)
  {
    if ((paramString == null) || (paramString.length() == 0)) {
      return new HashSet();
    }
    Set localSet = decomposeImpl(paramString);
    if ((localSet.contains("SHA1")) && (!localSet.contains("SHA-1"))) {
      localSet.add("SHA-1");
    }
    if ((localSet.contains("SHA-1")) && (!localSet.contains("SHA1"))) {
      localSet.add("SHA1");
    }
    if ((localSet.contains("SHA224")) && (!localSet.contains("SHA-224"))) {
      localSet.add("SHA-224");
    }
    if ((localSet.contains("SHA-224")) && (!localSet.contains("SHA224"))) {
      localSet.add("SHA224");
    }
    if ((localSet.contains("SHA256")) && (!localSet.contains("SHA-256"))) {
      localSet.add("SHA-256");
    }
    if ((localSet.contains("SHA-256")) && (!localSet.contains("SHA256"))) {
      localSet.add("SHA256");
    }
    if ((localSet.contains("SHA384")) && (!localSet.contains("SHA-384"))) {
      localSet.add("SHA-384");
    }
    if ((localSet.contains("SHA-384")) && (!localSet.contains("SHA384"))) {
      localSet.add("SHA384");
    }
    if ((localSet.contains("SHA512")) && (!localSet.contains("SHA-512"))) {
      localSet.add("SHA-512");
    }
    if ((localSet.contains("SHA-512")) && (!localSet.contains("SHA512"))) {
      localSet.add("SHA512");
    }
    return localSet;
  }
  
  private static void hasLoop(Set<String> paramSet, String paramString1, String paramString2)
  {
    if (paramSet.contains(paramString1))
    {
      if (!paramSet.contains(paramString2)) {
        paramSet.add(paramString2);
      }
      paramSet.remove(paramString1);
    }
  }
  
  public static Set<String> decomposeOneHash(String paramString)
  {
    if ((paramString == null) || (paramString.length() == 0)) {
      return new HashSet();
    }
    Set localSet = decomposeImpl(paramString);
    hasLoop(localSet, "SHA-1", "SHA1");
    hasLoop(localSet, "SHA-224", "SHA224");
    hasLoop(localSet, "SHA-256", "SHA256");
    hasLoop(localSet, "SHA-384", "SHA384");
    hasLoop(localSet, "SHA-512", "SHA512");
    return localSet;
  }
  
  public static String hashName(String paramString)
  {
    return paramString.replace("-", "");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\util\AlgorithmDecomposer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */