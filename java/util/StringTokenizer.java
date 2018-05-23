package java.util;

public class StringTokenizer
  implements Enumeration<Object>
{
  private int currentPosition = 0;
  private int newPosition = -1;
  private int maxPosition;
  private String str;
  private String delimiters;
  private boolean retDelims;
  private boolean delimsChanged = false;
  private int maxDelimCodePoint;
  private boolean hasSurrogates = false;
  private int[] delimiterCodePoints;
  
  private void setMaxDelimCodePoint()
  {
    if (delimiters == null)
    {
      maxDelimCodePoint = 0;
      return;
    }
    int i = 0;
    int k = 0;
    int m = 0;
    int j;
    while (m < delimiters.length())
    {
      j = delimiters.charAt(m);
      if ((j >= 55296) && (j <= 57343))
      {
        j = delimiters.codePointAt(m);
        hasSurrogates = true;
      }
      if (i < j) {
        i = j;
      }
      k++;
      m += Character.charCount(j);
    }
    maxDelimCodePoint = i;
    if (hasSurrogates)
    {
      delimiterCodePoints = new int[k];
      m = 0;
      int n = 0;
      while (m < k)
      {
        j = delimiters.codePointAt(n);
        delimiterCodePoints[m] = j;
        m++;
        n += Character.charCount(j);
      }
    }
  }
  
  public StringTokenizer(String paramString1, String paramString2, boolean paramBoolean)
  {
    str = paramString1;
    maxPosition = paramString1.length();
    delimiters = paramString2;
    retDelims = paramBoolean;
    setMaxDelimCodePoint();
  }
  
  public StringTokenizer(String paramString1, String paramString2)
  {
    this(paramString1, paramString2, false);
  }
  
  public StringTokenizer(String paramString)
  {
    this(paramString, " \t\n\r\f", false);
  }
  
  private int skipDelimiters(int paramInt)
  {
    if (delimiters == null) {
      throw new NullPointerException();
    }
    int i = paramInt;
    while ((!retDelims) && (i < maxPosition))
    {
      int j;
      if (!hasSurrogates)
      {
        j = str.charAt(i);
        if ((j > maxDelimCodePoint) || (delimiters.indexOf(j) < 0)) {
          break;
        }
        i++;
      }
      else
      {
        j = str.codePointAt(i);
        if ((j > maxDelimCodePoint) || (!isDelimiter(j))) {
          break;
        }
        i += Character.charCount(j);
      }
    }
    return i;
  }
  
  private int scanToken(int paramInt)
  {
    int i = paramInt;
    int j;
    while (i < maxPosition) {
      if (!hasSurrogates)
      {
        j = str.charAt(i);
        if ((j <= maxDelimCodePoint) && (delimiters.indexOf(j) >= 0)) {
          break;
        }
        i++;
      }
      else
      {
        j = str.codePointAt(i);
        if ((j <= maxDelimCodePoint) && (isDelimiter(j))) {
          break;
        }
        i += Character.charCount(j);
      }
    }
    if ((retDelims) && (paramInt == i)) {
      if (!hasSurrogates)
      {
        j = str.charAt(i);
        if ((j <= maxDelimCodePoint) && (delimiters.indexOf(j) >= 0)) {
          i++;
        }
      }
      else
      {
        j = str.codePointAt(i);
        if ((j <= maxDelimCodePoint) && (isDelimiter(j))) {
          i += Character.charCount(j);
        }
      }
    }
    return i;
  }
  
  private boolean isDelimiter(int paramInt)
  {
    for (int i = 0; i < delimiterCodePoints.length; i++) {
      if (delimiterCodePoints[i] == paramInt) {
        return true;
      }
    }
    return false;
  }
  
  public boolean hasMoreTokens()
  {
    newPosition = skipDelimiters(currentPosition);
    return newPosition < maxPosition;
  }
  
  public String nextToken()
  {
    currentPosition = ((newPosition >= 0) && (!delimsChanged) ? newPosition : skipDelimiters(currentPosition));
    delimsChanged = false;
    newPosition = -1;
    if (currentPosition >= maxPosition) {
      throw new NoSuchElementException();
    }
    int i = currentPosition;
    currentPosition = scanToken(currentPosition);
    return str.substring(i, currentPosition);
  }
  
  public String nextToken(String paramString)
  {
    delimiters = paramString;
    delimsChanged = true;
    setMaxDelimCodePoint();
    return nextToken();
  }
  
  public boolean hasMoreElements()
  {
    return hasMoreTokens();
  }
  
  public Object nextElement()
  {
    return nextToken();
  }
  
  public int countTokens()
  {
    int i = 0;
    int j = currentPosition;
    while (j < maxPosition)
    {
      j = skipDelimiters(j);
      if (j >= maxPosition) {
        break;
      }
      j = scanToken(j);
      i++;
    }
    return i;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\util\StringTokenizer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */