package sun.invoke.util;

public class BytecodeName
{
  static char ESCAPE_C;
  static char NULL_ESCAPE_C;
  static String NULL_ESCAPE;
  static final String DANGEROUS_CHARS = "\\/.;:$[]<>";
  static final String REPLACEMENT_CHARS = "-|,?!%{}^_";
  static final int DANGEROUS_CHAR_FIRST_INDEX = 1;
  static char[] DANGEROUS_CHARS_A;
  static char[] REPLACEMENT_CHARS_A;
  static final Character[] DANGEROUS_CHARS_CA;
  static final long[] SPECIAL_BITMAP;
  
  private BytecodeName() {}
  
  public static String toBytecodeName(String paramString)
  {
    String str = mangle(paramString);
    assert ((str == paramString) || (looksMangled(str))) : str;
    assert (paramString.equals(toSourceName(str))) : paramString;
    return str;
  }
  
  public static String toSourceName(String paramString)
  {
    checkSafeBytecodeName(paramString);
    String str = paramString;
    if (looksMangled(paramString))
    {
      str = demangle(paramString);
      assert (paramString.equals(mangle(str))) : (paramString + " => " + str + " => " + mangle(str));
    }
    return str;
  }
  
  public static Object[] parseBytecodeName(String paramString)
  {
    int i = paramString.length();
    Object[] arrayOfObject = null;
    for (int j = 0; j <= 1; j++)
    {
      int k = 0;
      int m = 0;
      for (int n = 0; n <= i; n++)
      {
        int i1 = -1;
        if (n < i)
        {
          i1 = "\\/.;:$[]<>".indexOf(paramString.charAt(n));
          if (i1 < 1) {}
        }
        else
        {
          if (m < n)
          {
            if (j != 0) {
              arrayOfObject[k] = toSourceName(paramString.substring(m, n));
            }
            k++;
            m = n + 1;
          }
          if (i1 >= 1)
          {
            if (j != 0) {
              arrayOfObject[k] = DANGEROUS_CHARS_CA[i1];
            }
            k++;
            m = n + 1;
          }
        }
      }
      if (j != 0) {
        break;
      }
      arrayOfObject = new Object[k];
      if ((k <= 1) && (m == 0))
      {
        if (k == 0) {
          break;
        }
        arrayOfObject[0] = toSourceName(paramString);
        break;
      }
    }
    return arrayOfObject;
  }
  
  public static String unparseBytecodeName(Object[] paramArrayOfObject)
  {
    Object[] arrayOfObject = paramArrayOfObject;
    for (int i = 0; i < paramArrayOfObject.length; i++)
    {
      Object localObject = paramArrayOfObject[i];
      if ((localObject instanceof String))
      {
        String str = toBytecodeName((String)localObject);
        if ((i == 0) && (paramArrayOfObject.length == 1)) {
          return str;
        }
        if (str != localObject)
        {
          if (paramArrayOfObject == arrayOfObject) {
            paramArrayOfObject = (Object[])paramArrayOfObject.clone();
          }
          paramArrayOfObject[i] = (localObject = str);
        }
      }
    }
    return appendAll(paramArrayOfObject);
  }
  
  private static String appendAll(Object[] paramArrayOfObject)
  {
    if (paramArrayOfObject.length <= 1)
    {
      if (paramArrayOfObject.length == 1) {
        return String.valueOf(paramArrayOfObject[0]);
      }
      return "";
    }
    int i = 0;
    for (Object localObject2 : paramArrayOfObject) {
      if ((localObject2 instanceof String)) {
        i += String.valueOf(localObject2).length();
      } else {
        i++;
      }
    }
    ??? = new StringBuilder(i);
    for (Object localObject3 : paramArrayOfObject) {
      ((StringBuilder)???).append(localObject3);
    }
    return ((StringBuilder)???).toString();
  }
  
  public static String toDisplayName(String paramString)
  {
    Object[] arrayOfObject = parseBytecodeName(paramString);
    for (int i = 0; i < arrayOfObject.length; i++) {
      if ((arrayOfObject[i] instanceof String))
      {
        String str = (String)arrayOfObject[i];
        if ((!isJavaIdent(str)) || (str.indexOf('$') >= 0)) {
          arrayOfObject[i] = quoteDisplay(str);
        }
      }
    }
    return appendAll(arrayOfObject);
  }
  
  private static boolean isJavaIdent(String paramString)
  {
    int i = paramString.length();
    if (i == 0) {
      return false;
    }
    if (!Character.isJavaIdentifierStart(paramString.charAt(0))) {
      return false;
    }
    for (int j = 1; j < i; j++) {
      if (!Character.isJavaIdentifierPart(paramString.charAt(j))) {
        return false;
      }
    }
    return true;
  }
  
  private static String quoteDisplay(String paramString)
  {
    return "'" + paramString.replaceAll("['\\\\]", "\\\\$0") + "'";
  }
  
  private static void checkSafeBytecodeName(String paramString)
    throws IllegalArgumentException
  {
    if (!isSafeBytecodeName(paramString)) {
      throw new IllegalArgumentException(paramString);
    }
  }
  
  public static boolean isSafeBytecodeName(String paramString)
  {
    if (paramString.length() == 0) {
      return false;
    }
    for (int k : DANGEROUS_CHARS_A) {
      if ((k != ESCAPE_C) && (paramString.indexOf(k) >= 0)) {
        return false;
      }
    }
    return true;
  }
  
  public static boolean isSafeBytecodeChar(char paramChar)
  {
    return "\\/.;:$[]<>".indexOf(paramChar) < 1;
  }
  
  private static boolean looksMangled(String paramString)
  {
    return paramString.charAt(0) == ESCAPE_C;
  }
  
  private static String mangle(String paramString)
  {
    if (paramString.length() == 0) {
      return NULL_ESCAPE;
    }
    StringBuilder localStringBuilder = null;
    int i = 0;
    int j = paramString.length();
    while (i < j)
    {
      char c1 = paramString.charAt(i);
      boolean bool = false;
      if (c1 == ESCAPE_C)
      {
        if (i + 1 < j)
        {
          char c2 = paramString.charAt(i + 1);
          if (((i == 0) && (c2 == NULL_ESCAPE_C)) || (c2 != originalOfReplacement(c2))) {
            bool = true;
          }
        }
      }
      else {
        bool = isDangerous(c1);
      }
      if (!bool)
      {
        if (localStringBuilder != null) {
          localStringBuilder.append(c1);
        }
      }
      else
      {
        if (localStringBuilder == null)
        {
          localStringBuilder = new StringBuilder(paramString.length() + 10);
          if ((paramString.charAt(0) != ESCAPE_C) && (i > 0)) {
            localStringBuilder.append(NULL_ESCAPE);
          }
          localStringBuilder.append(paramString.substring(0, i));
        }
        localStringBuilder.append(ESCAPE_C);
        localStringBuilder.append(replacementOf(c1));
      }
      i++;
    }
    if (localStringBuilder != null) {
      return localStringBuilder.toString();
    }
    return paramString;
  }
  
  private static String demangle(String paramString)
  {
    StringBuilder localStringBuilder = null;
    int i = 0;
    if (paramString.startsWith(NULL_ESCAPE)) {
      i = 2;
    }
    int j = i;
    int k = paramString.length();
    while (j < k)
    {
      char c1 = paramString.charAt(j);
      if ((c1 == ESCAPE_C) && (j + 1 < k))
      {
        char c2 = paramString.charAt(j + 1);
        char c3 = originalOfReplacement(c2);
        if (c3 != c2)
        {
          if (localStringBuilder == null)
          {
            localStringBuilder = new StringBuilder(paramString.length());
            localStringBuilder.append(paramString.substring(i, j));
          }
          j++;
          c1 = c3;
        }
      }
      if (localStringBuilder != null) {
        localStringBuilder.append(c1);
      }
      j++;
    }
    if (localStringBuilder != null) {
      return localStringBuilder.toString();
    }
    return paramString.substring(i);
  }
  
  static boolean isSpecial(char paramChar)
  {
    if (paramChar >>> '\006' < SPECIAL_BITMAP.length) {
      return (SPECIAL_BITMAP[(paramChar >>> '\006')] >> paramChar & 1L) != 0L;
    }
    return false;
  }
  
  static char replacementOf(char paramChar)
  {
    if (!isSpecial(paramChar)) {
      return paramChar;
    }
    int i = "\\/.;:$[]<>".indexOf(paramChar);
    if (i < 0) {
      return paramChar;
    }
    return "-|,?!%{}^_".charAt(i);
  }
  
  static char originalOfReplacement(char paramChar)
  {
    if (!isSpecial(paramChar)) {
      return paramChar;
    }
    int i = "-|,?!%{}^_".indexOf(paramChar);
    if (i < 0) {
      return paramChar;
    }
    return "\\/.;:$[]<>".charAt(i);
  }
  
  static boolean isDangerous(char paramChar)
  {
    if (!isSpecial(paramChar)) {
      return false;
    }
    return "\\/.;:$[]<>".indexOf(paramChar) >= 1;
  }
  
  static int indexOfDangerousChar(String paramString, int paramInt)
  {
    int i = paramInt;
    int j = paramString.length();
    while (i < j)
    {
      if (isDangerous(paramString.charAt(i))) {
        return i;
      }
      i++;
    }
    return -1;
  }
  
  static int lastIndexOfDangerousChar(String paramString, int paramInt)
  {
    for (int i = Math.min(paramInt, paramString.length() - 1); i >= 0; i--) {
      if (isDangerous(paramString.charAt(i))) {
        return i;
      }
    }
    return -1;
  }
  
  static
  {
    ESCAPE_C = '\\';
    NULL_ESCAPE_C = '=';
    NULL_ESCAPE = ESCAPE_C + "" + NULL_ESCAPE_C;
    DANGEROUS_CHARS_A = "\\/.;:$[]<>".toCharArray();
    REPLACEMENT_CHARS_A = "-|,?!%{}^_".toCharArray();
    Object localObject = new Character["\\/.;:$[]<>".length()];
    for (int i = 0; i < localObject.length; i++) {
      localObject[i] = Character.valueOf("\\/.;:$[]<>".charAt(i));
    }
    DANGEROUS_CHARS_CA = (Character[])localObject;
    SPECIAL_BITMAP = new long[2];
    localObject = "\\/.;:$[]<>-|,?!%{}^_";
    for (int m : ((String)localObject).toCharArray()) {
      SPECIAL_BITMAP[(m >>> 6)] |= 1L << m;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\invoke\util\BytecodeName.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */