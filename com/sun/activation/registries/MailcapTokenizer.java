package com.sun.activation.registries;

public class MailcapTokenizer
{
  public static final int UNKNOWN_TOKEN = 0;
  public static final int START_TOKEN = 1;
  public static final int STRING_TOKEN = 2;
  public static final int EOI_TOKEN = 5;
  public static final int SLASH_TOKEN = 47;
  public static final int SEMICOLON_TOKEN = 59;
  public static final int EQUALS_TOKEN = 61;
  private String data;
  private int dataIndex;
  private int dataLength;
  private int currentToken;
  private String currentTokenValue;
  private boolean isAutoquoting;
  private char autoquoteChar;
  
  public MailcapTokenizer(String paramString)
  {
    data = paramString;
    dataIndex = 0;
    dataLength = paramString.length();
    currentToken = 1;
    currentTokenValue = "";
    isAutoquoting = false;
    autoquoteChar = ';';
  }
  
  public void setIsAutoquoting(boolean paramBoolean)
  {
    isAutoquoting = paramBoolean;
  }
  
  public int getCurrentToken()
  {
    return currentToken;
  }
  
  public static String nameForToken(int paramInt)
  {
    String str = "really unknown";
    switch (paramInt)
    {
    case 0: 
      str = "unknown";
      break;
    case 1: 
      str = "start";
      break;
    case 2: 
      str = "string";
      break;
    case 5: 
      str = "EOI";
      break;
    case 47: 
      str = "'/'";
      break;
    case 59: 
      str = "';'";
      break;
    case 61: 
      str = "'='";
    }
    return str;
  }
  
  public String getCurrentTokenValue()
  {
    return currentTokenValue;
  }
  
  public int nextToken()
  {
    if (dataIndex < dataLength)
    {
      while ((dataIndex < dataLength) && (isWhiteSpaceChar(data.charAt(dataIndex)))) {
        dataIndex += 1;
      }
      if (dataIndex < dataLength)
      {
        char c = data.charAt(dataIndex);
        if (isAutoquoting)
        {
          if ((c == ';') || (c == '='))
          {
            currentToken = c;
            currentTokenValue = new Character(c).toString();
            dataIndex += 1;
          }
          else
          {
            processAutoquoteToken();
          }
        }
        else if (isStringTokenChar(c))
        {
          processStringToken();
        }
        else if ((c == '/') || (c == ';') || (c == '='))
        {
          currentToken = c;
          currentTokenValue = new Character(c).toString();
          dataIndex += 1;
        }
        else
        {
          currentToken = 0;
          currentTokenValue = new Character(c).toString();
          dataIndex += 1;
        }
      }
      else
      {
        currentToken = 5;
        currentTokenValue = null;
      }
    }
    else
    {
      currentToken = 5;
      currentTokenValue = null;
    }
    return currentToken;
  }
  
  private void processStringToken()
  {
    int i = dataIndex;
    while ((dataIndex < dataLength) && (isStringTokenChar(data.charAt(dataIndex)))) {
      dataIndex += 1;
    }
    currentToken = 2;
    currentTokenValue = data.substring(i, dataIndex);
  }
  
  private void processAutoquoteToken()
  {
    int i = dataIndex;
    int j = 0;
    while ((dataIndex < dataLength) && (j == 0))
    {
      int k = data.charAt(dataIndex);
      if (k != autoquoteChar) {
        dataIndex += 1;
      } else {
        j = 1;
      }
    }
    currentToken = 2;
    currentTokenValue = fixEscapeSequences(data.substring(i, dataIndex));
  }
  
  private static boolean isSpecialChar(char paramChar)
  {
    boolean bool = false;
    switch (paramChar)
    {
    case '"': 
    case '(': 
    case ')': 
    case ',': 
    case '/': 
    case ':': 
    case ';': 
    case '<': 
    case '=': 
    case '>': 
    case '?': 
    case '@': 
    case '[': 
    case '\\': 
    case ']': 
      bool = true;
    }
    return bool;
  }
  
  private static boolean isControlChar(char paramChar)
  {
    return Character.isISOControl(paramChar);
  }
  
  private static boolean isWhiteSpaceChar(char paramChar)
  {
    return Character.isWhitespace(paramChar);
  }
  
  private static boolean isStringTokenChar(char paramChar)
  {
    return (!isSpecialChar(paramChar)) && (!isControlChar(paramChar)) && (!isWhiteSpaceChar(paramChar));
  }
  
  private static String fixEscapeSequences(String paramString)
  {
    int i = paramString.length();
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.ensureCapacity(i);
    for (int j = 0; j < i; j++)
    {
      char c1 = paramString.charAt(j);
      if (c1 != '\\')
      {
        localStringBuffer.append(c1);
      }
      else if (j < i - 1)
      {
        char c2 = paramString.charAt(j + 1);
        localStringBuffer.append(c2);
        j++;
      }
      else
      {
        localStringBuffer.append(c1);
      }
    }
    return localStringBuffer.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\activation\registries\MailcapTokenizer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */