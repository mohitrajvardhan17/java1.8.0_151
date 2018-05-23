package sun.security.util;

import java.security.AccessController;
import java.security.AlgorithmConstraints;
import java.security.PrivilegedAction;
import java.security.Security;
import java.util.Iterator;
import java.util.Set;

public abstract class AbstractAlgorithmConstraints
  implements AlgorithmConstraints
{
  protected final AlgorithmDecomposer decomposer;
  
  protected AbstractAlgorithmConstraints(AlgorithmDecomposer paramAlgorithmDecomposer)
  {
    decomposer = paramAlgorithmDecomposer;
  }
  
  static String[] getAlgorithms(String paramString)
  {
    String str = (String)AccessController.doPrivileged(new PrivilegedAction()
    {
      public String run()
      {
        return Security.getProperty(val$propertyName);
      }
    });
    String[] arrayOfString = null;
    if ((str != null) && (!str.isEmpty()))
    {
      if ((str.length() >= 2) && (str.charAt(0) == '"') && (str.charAt(str.length() - 1) == '"')) {
        str = str.substring(1, str.length() - 1);
      }
      arrayOfString = str.split(",");
      for (int i = 0; i < arrayOfString.length; i++) {
        arrayOfString[i] = arrayOfString[i].trim();
      }
    }
    if (arrayOfString == null) {
      arrayOfString = new String[0];
    }
    return arrayOfString;
  }
  
  static boolean checkAlgorithm(String[] paramArrayOfString, String paramString, AlgorithmDecomposer paramAlgorithmDecomposer)
  {
    if ((paramString == null) || (paramString.length() == 0)) {
      throw new IllegalArgumentException("No algorithm name specified");
    }
    Set localSet = null;
    for (String str1 : paramArrayOfString) {
      if ((str1 != null) && (!str1.isEmpty()))
      {
        if (str1.equalsIgnoreCase(paramString)) {
          return false;
        }
        if (localSet == null) {
          localSet = paramAlgorithmDecomposer.decompose(paramString);
        }
        Iterator localIterator = localSet.iterator();
        while (localIterator.hasNext())
        {
          String str2 = (String)localIterator.next();
          if (str1.equalsIgnoreCase(str2)) {
            return false;
          }
        }
      }
    }
    return true;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\security\util\AbstractAlgorithmConstraints.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */