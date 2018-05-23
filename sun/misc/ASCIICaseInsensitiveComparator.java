package sun.misc;

import java.util.Comparator;

public class ASCIICaseInsensitiveComparator
  implements Comparator<String>
{
  public static final Comparator<String> CASE_INSENSITIVE_ORDER = new ASCIICaseInsensitiveComparator();
  
  public ASCIICaseInsensitiveComparator() {}
  
  public int compare(String paramString1, String paramString2)
  {
    int i = paramString1.length();
    int j = paramString2.length();
    int k = i < j ? i : j;
    for (int m = 0; m < k; m++)
    {
      int n = paramString1.charAt(m);
      int i1 = paramString2.charAt(m);
      assert ((n <= 127) && (i1 <= 127));
      if (n != i1)
      {
        n = (char)toLower(n);
        i1 = (char)toLower(i1);
        if (n != i1) {
          return n - i1;
        }
      }
    }
    return i - j;
  }
  
  public static int lowerCaseHashCode(String paramString)
  {
    int i = 0;
    int j = paramString.length();
    for (int k = 0; k < j; k++) {
      i = 31 * i + toLower(paramString.charAt(k));
    }
    return i;
  }
  
  static boolean isLower(int paramInt)
  {
    return (paramInt - 97 | 122 - paramInt) >= 0;
  }
  
  static boolean isUpper(int paramInt)
  {
    return (paramInt - 65 | 90 - paramInt) >= 0;
  }
  
  static int toLower(int paramInt)
  {
    return isUpper(paramInt) ? paramInt + 32 : paramInt;
  }
  
  static int toUpper(int paramInt)
  {
    return isLower(paramInt) ? paramInt - 32 : paramInt;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\misc\ASCIICaseInsensitiveComparator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */