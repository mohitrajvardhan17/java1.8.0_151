package javax.xml.bind;

abstract class WhiteSpaceProcessor
{
  WhiteSpaceProcessor() {}
  
  public static String replace(String paramString)
  {
    return replace(paramString).toString();
  }
  
  public static CharSequence replace(CharSequence paramCharSequence)
  {
    for (int i = paramCharSequence.length() - 1; (i >= 0) && (!isWhiteSpaceExceptSpace(paramCharSequence.charAt(i))); i--) {}
    if (i < 0) {
      return paramCharSequence;
    }
    StringBuilder localStringBuilder = new StringBuilder(paramCharSequence);
    localStringBuilder.setCharAt(i--, ' ');
    while (i >= 0)
    {
      if (isWhiteSpaceExceptSpace(localStringBuilder.charAt(i))) {
        localStringBuilder.setCharAt(i, ' ');
      }
      i--;
    }
    return new String(localStringBuilder);
  }
  
  public static CharSequence trim(CharSequence paramCharSequence)
  {
    int i = paramCharSequence.length();
    for (int j = 0; (j < i) && (isWhiteSpace(paramCharSequence.charAt(j))); j++) {}
    for (int k = i - 1; (k > j) && (isWhiteSpace(paramCharSequence.charAt(k))); k--) {}
    if ((j == 0) && (k == i - 1)) {
      return paramCharSequence;
    }
    return paramCharSequence.subSequence(j, k + 1);
  }
  
  public static String collapse(String paramString)
  {
    return collapse(paramString).toString();
  }
  
  public static CharSequence collapse(CharSequence paramCharSequence)
  {
    int i = paramCharSequence.length();
    for (int j = 0; (j < i) && (!isWhiteSpace(paramCharSequence.charAt(j))); j++) {}
    if (j == i) {
      return paramCharSequence;
    }
    StringBuilder localStringBuilder = new StringBuilder(i);
    if (j != 0)
    {
      for (k = 0; k < j; k++) {
        localStringBuilder.append(paramCharSequence.charAt(k));
      }
      localStringBuilder.append(' ');
    }
    int k = 1;
    for (int m = j + 1; m < i; m++)
    {
      char c = paramCharSequence.charAt(m);
      int n = isWhiteSpace(c);
      if ((k == 0) || (n == 0))
      {
        k = n;
        if (k != 0) {
          localStringBuilder.append(' ');
        } else {
          localStringBuilder.append(c);
        }
      }
    }
    i = localStringBuilder.length();
    if ((i > 0) && (localStringBuilder.charAt(i - 1) == ' ')) {
      localStringBuilder.setLength(i - 1);
    }
    return localStringBuilder;
  }
  
  public static final boolean isWhiteSpace(CharSequence paramCharSequence)
  {
    for (int i = paramCharSequence.length() - 1; i >= 0; i--) {
      if (!isWhiteSpace(paramCharSequence.charAt(i))) {
        return false;
      }
    }
    return true;
  }
  
  public static final boolean isWhiteSpace(char paramChar)
  {
    if (paramChar > ' ') {
      return false;
    }
    return (paramChar == '\t') || (paramChar == '\n') || (paramChar == '\r') || (paramChar == ' ');
  }
  
  protected static final boolean isWhiteSpaceExceptSpace(char paramChar)
  {
    if (paramChar >= ' ') {
      return false;
    }
    return (paramChar == '\t') || (paramChar == '\n') || (paramChar == '\r');
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\bind\WhiteSpaceProcessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */