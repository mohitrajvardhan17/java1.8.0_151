package java.text;

class PatternEntry
{
  static final int RESET = -2;
  static final int UNSET = -1;
  int strength = -1;
  String chars = "";
  String extension = "";
  
  public void appendQuotedExtension(StringBuffer paramStringBuffer)
  {
    appendQuoted(extension, paramStringBuffer);
  }
  
  public void appendQuotedChars(StringBuffer paramStringBuffer)
  {
    appendQuoted(chars, paramStringBuffer);
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == null) {
      return false;
    }
    PatternEntry localPatternEntry = (PatternEntry)paramObject;
    boolean bool = chars.equals(chars);
    return bool;
  }
  
  public int hashCode()
  {
    return chars.hashCode();
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    addToBuffer(localStringBuffer, true, false, null);
    return localStringBuffer.toString();
  }
  
  final int getStrength()
  {
    return strength;
  }
  
  final String getExtension()
  {
    return extension;
  }
  
  final String getChars()
  {
    return chars;
  }
  
  void addToBuffer(StringBuffer paramStringBuffer, boolean paramBoolean1, boolean paramBoolean2, PatternEntry paramPatternEntry)
  {
    if ((paramBoolean2) && (paramStringBuffer.length() > 0)) {
      if ((strength == 0) || (paramPatternEntry != null)) {
        paramStringBuffer.append('\n');
      } else {
        paramStringBuffer.append(' ');
      }
    }
    if (paramPatternEntry != null)
    {
      paramStringBuffer.append('&');
      if (paramBoolean2) {
        paramStringBuffer.append(' ');
      }
      paramPatternEntry.appendQuotedChars(paramStringBuffer);
      appendQuotedExtension(paramStringBuffer);
      if (paramBoolean2) {
        paramStringBuffer.append(' ');
      }
    }
    switch (strength)
    {
    case 3: 
      paramStringBuffer.append('=');
      break;
    case 2: 
      paramStringBuffer.append(',');
      break;
    case 1: 
      paramStringBuffer.append(';');
      break;
    case 0: 
      paramStringBuffer.append('<');
      break;
    case -2: 
      paramStringBuffer.append('&');
      break;
    case -1: 
      paramStringBuffer.append('?');
    }
    if (paramBoolean2) {
      paramStringBuffer.append(' ');
    }
    appendQuoted(chars, paramStringBuffer);
    if ((paramBoolean1) && (extension.length() != 0))
    {
      paramStringBuffer.append('/');
      appendQuoted(extension, paramStringBuffer);
    }
  }
  
  static void appendQuoted(String paramString, StringBuffer paramStringBuffer)
  {
    int i = 0;
    char c = paramString.charAt(0);
    if (Character.isSpaceChar(c))
    {
      i = 1;
      paramStringBuffer.append('\'');
    }
    else if (isSpecialChar(c))
    {
      i = 1;
      paramStringBuffer.append('\'');
    }
    else
    {
      switch (c)
      {
      case '\t': 
      case '\n': 
      case '\f': 
      case '\r': 
      case '\020': 
      case '@': 
        i = 1;
        paramStringBuffer.append('\'');
        break;
      case '\'': 
        i = 1;
        paramStringBuffer.append('\'');
        break;
      default: 
        if (i != 0)
        {
          i = 0;
          paramStringBuffer.append('\'');
        }
        break;
      }
    }
    paramStringBuffer.append(paramString);
    if (i != 0) {
      paramStringBuffer.append('\'');
    }
  }
  
  PatternEntry(int paramInt, StringBuffer paramStringBuffer1, StringBuffer paramStringBuffer2)
  {
    strength = paramInt;
    chars = paramStringBuffer1.toString();
    extension = (paramStringBuffer2.length() > 0 ? paramStringBuffer2.toString() : "");
  }
  
  static boolean isSpecialChar(char paramChar)
  {
    return (paramChar == ' ') || ((paramChar <= '/') && (paramChar >= '"')) || ((paramChar <= '?') && (paramChar >= ':')) || ((paramChar <= '`') && (paramChar >= '[')) || ((paramChar <= '~') && (paramChar >= '{'));
  }
  
  static class Parser
  {
    private String pattern;
    private int i;
    private StringBuffer newChars = new StringBuffer();
    private StringBuffer newExtension = new StringBuffer();
    
    public Parser(String paramString)
    {
      pattern = paramString;
      i = 0;
    }
    
    public PatternEntry next()
      throws ParseException
    {
      int j = -1;
      newChars.setLength(0);
      newExtension.setLength(0);
      int k = 1;
      int m = 0;
      while (i < pattern.length())
      {
        char c = pattern.charAt(i);
        if (m != 0)
        {
          if (c == '\'') {
            m = 0;
          } else if (newChars.length() == 0) {
            newChars.append(c);
          } else if (k != 0) {
            newChars.append(c);
          } else {
            newExtension.append(c);
          }
        }
        else {
          switch (c)
          {
          case '=': 
            if (j != -1) {
              break label546;
            }
            j = 3;
            break;
          case ',': 
            if (j != -1) {
              break label546;
            }
            j = 2;
            break;
          case ';': 
            if (j != -1) {
              break label546;
            }
            j = 1;
            break;
          case '<': 
            if (j != -1) {
              break label546;
            }
            j = 0;
            break;
          case '&': 
            if (j != -1) {
              break label546;
            }
            j = -2;
            break;
          case '\t': 
          case '\n': 
          case '\f': 
          case '\r': 
          case ' ': 
            break;
          case '/': 
            k = 0;
            break;
          case '\'': 
            m = 1;
            c = pattern.charAt(++i);
            if (newChars.length() == 0) {
              newChars.append(c);
            } else if (k != 0) {
              newChars.append(c);
            } else {
              newExtension.append(c);
            }
            break;
          default: 
            if (j == -1) {
              throw new ParseException("missing char (=,;<&) : " + pattern.substring(i, i + 10 < pattern.length() ? i + 10 : pattern.length()), i);
            }
            if ((PatternEntry.isSpecialChar(c)) && (m == 0)) {
              throw new ParseException("Unquoted punctuation character : " + Integer.toString(c, 16), i);
            }
            if (k != 0) {
              newChars.append(c);
            } else {
              newExtension.append(c);
            }
            break;
          }
        }
        i += 1;
      }
      label546:
      if (j == -1) {
        return null;
      }
      if (newChars.length() == 0) {
        throw new ParseException("missing chars (=,;<&): " + pattern.substring(i, i + 10 < pattern.length() ? i + 10 : pattern.length()), i);
      }
      return new PatternEntry(j, newChars, newExtension);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\text\PatternEntry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */