package com.sun.org.apache.xml.internal.utils;

public class XMLCharacterRecognizer
{
  public XMLCharacterRecognizer() {}
  
  public static boolean isWhiteSpace(char paramChar)
  {
    return (paramChar == ' ') || (paramChar == '\t') || (paramChar == '\r') || (paramChar == '\n');
  }
  
  public static boolean isWhiteSpace(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    int i = paramInt1 + paramInt2;
    for (int j = paramInt1; j < i; j++) {
      if (!isWhiteSpace(paramArrayOfChar[j])) {
        return false;
      }
    }
    return true;
  }
  
  public static boolean isWhiteSpace(StringBuffer paramStringBuffer)
  {
    int i = paramStringBuffer.length();
    for (int j = 0; j < i; j++) {
      if (!isWhiteSpace(paramStringBuffer.charAt(j))) {
        return false;
      }
    }
    return true;
  }
  
  public static boolean isWhiteSpace(String paramString)
  {
    if (null != paramString)
    {
      int i = paramString.length();
      for (int j = 0; j < i; j++) {
        if (!isWhiteSpace(paramString.charAt(j))) {
          return false;
        }
      }
    }
    return true;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\utils\XMLCharacterRecognizer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */