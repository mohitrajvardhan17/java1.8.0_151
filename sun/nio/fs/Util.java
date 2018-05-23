package sun.nio.fs;

import java.nio.charset.Charset;
import java.nio.file.LinkOption;
import java.security.AccessController;
import java.util.HashSet;
import java.util.Set;
import sun.security.action.GetPropertyAction;

class Util
{
  private static final Charset jnuEncoding = Charset.forName((String)AccessController.doPrivileged(new GetPropertyAction("sun.jnu.encoding")));
  
  private Util() {}
  
  static Charset jnuEncoding()
  {
    return jnuEncoding;
  }
  
  static byte[] toBytes(String paramString)
  {
    return paramString.getBytes(jnuEncoding);
  }
  
  static String toString(byte[] paramArrayOfByte)
  {
    return new String(paramArrayOfByte, jnuEncoding);
  }
  
  static String[] split(String paramString, char paramChar)
  {
    int i = 0;
    for (int j = 0; j < paramString.length(); j++) {
      if (paramString.charAt(j) == paramChar) {
        i++;
      }
    }
    String[] arrayOfString = new String[i + 1];
    int k = 0;
    int m = 0;
    for (int n = 0; n < paramString.length(); n++) {
      if (paramString.charAt(n) == paramChar)
      {
        arrayOfString[(k++)] = paramString.substring(m, n);
        m = n + 1;
      }
    }
    arrayOfString[k] = paramString.substring(m, paramString.length());
    return arrayOfString;
  }
  
  @SafeVarargs
  static <E> Set<E> newSet(E... paramVarArgs)
  {
    HashSet localHashSet = new HashSet();
    for (E ? : paramVarArgs) {
      localHashSet.add(?);
    }
    return localHashSet;
  }
  
  @SafeVarargs
  static <E> Set<E> newSet(Set<E> paramSet, E... paramVarArgs)
  {
    HashSet localHashSet = new HashSet(paramSet);
    for (E ? : paramVarArgs) {
      localHashSet.add(?);
    }
    return localHashSet;
  }
  
  static boolean followLinks(LinkOption... paramVarArgs)
  {
    boolean bool = true;
    for (LinkOption localLinkOption : paramVarArgs) {
      if (localLinkOption == LinkOption.NOFOLLOW_LINKS)
      {
        bool = false;
      }
      else
      {
        if (localLinkOption == null) {
          throw new NullPointerException();
        }
        throw new AssertionError("Should not get here");
      }
    }
    return bool;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\nio\fs\Util.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */