package com.sun.xml.internal.ws.encoding;

import javax.xml.ws.WebServiceException;

class HeaderTokenizer
{
  private String string;
  private boolean skipComments;
  private String delimiters;
  private int currentPos;
  private int maxPos;
  private int nextPos;
  private int peekPos;
  private static final String RFC822 = "()<>@,;:\\\"\t .[]";
  static final String MIME = "()<>@,;:\\\"\t []/?=";
  private static final Token EOFToken = new Token(-4, null);
  
  HeaderTokenizer(String paramString1, String paramString2, boolean paramBoolean)
  {
    string = (paramString1 == null ? "" : paramString1);
    skipComments = paramBoolean;
    delimiters = paramString2;
    currentPos = (nextPos = peekPos = 0);
    maxPos = string.length();
  }
  
  HeaderTokenizer(String paramString1, String paramString2)
  {
    this(paramString1, paramString2, true);
  }
  
  HeaderTokenizer(String paramString)
  {
    this(paramString, "()<>@,;:\\\"\t .[]");
  }
  
  Token next()
    throws WebServiceException
  {
    currentPos = nextPos;
    Token localToken = getNext();
    nextPos = (peekPos = currentPos);
    return localToken;
  }
  
  Token peek()
    throws WebServiceException
  {
    currentPos = peekPos;
    Token localToken = getNext();
    peekPos = currentPos;
    return localToken;
  }
  
  String getRemainder()
  {
    return string.substring(nextPos);
  }
  
  private Token getNext()
    throws WebServiceException
  {
    if (currentPos >= maxPos) {
      return EOFToken;
    }
    if (skipWhiteSpace() == -4) {
      return EOFToken;
    }
    int k = 0;
    for (int i = string.charAt(currentPos); i == 40; i = string.charAt(currentPos))
    {
      j = ++currentPos;
      int m = 1;
      while ((m > 0) && (currentPos < maxPos))
      {
        i = string.charAt(currentPos);
        if (i == 92)
        {
          currentPos += 1;
          k = 1;
        }
        else if (i == 13)
        {
          k = 1;
        }
        else if (i == 40)
        {
          m++;
        }
        else if (i == 41)
        {
          m--;
        }
        currentPos += 1;
      }
      if (m != 0) {
        throw new WebServiceException("Unbalanced comments");
      }
      if (!skipComments)
      {
        String str;
        if (k != 0) {
          str = filterToken(string, j, currentPos - 1);
        } else {
          str = string.substring(j, currentPos - 1);
        }
        return new Token(-3, str);
      }
      if (skipWhiteSpace() == -4) {
        return EOFToken;
      }
    }
    Object localObject;
    if (i == 34)
    {
      j = ++currentPos;
      while (currentPos < maxPos)
      {
        i = string.charAt(currentPos);
        if (i == 92)
        {
          currentPos += 1;
          k = 1;
        }
        else if (i == 13)
        {
          k = 1;
        }
        else if (i == 34)
        {
          currentPos += 1;
          if (k != 0) {
            localObject = filterToken(string, j, currentPos - 1);
          } else {
            localObject = string.substring(j, currentPos - 1);
          }
          return new Token(-2, (String)localObject);
        }
        currentPos += 1;
      }
      throw new WebServiceException("Unbalanced quoted string");
    }
    if ((i < 32) || (i >= 127) || (delimiters.indexOf(i) >= 0))
    {
      currentPos += 1;
      localObject = new char[1];
      localObject[0] = i;
      return new Token(i, new String((char[])localObject));
    }
    int j = currentPos;
    while (currentPos < maxPos)
    {
      i = string.charAt(currentPos);
      if ((i < 32) || (i >= 127) || (i == 40) || (i == 32) || (i == 34) || (delimiters.indexOf(i) >= 0)) {
        break;
      }
      currentPos += 1;
    }
    return new Token(-1, string.substring(j, currentPos));
  }
  
  private int skipWhiteSpace()
  {
    while (currentPos < maxPos)
    {
      int i;
      if (((i = string.charAt(currentPos)) != ' ') && (i != 9) && (i != 13) && (i != 10)) {
        return currentPos;
      }
      currentPos += 1;
    }
    return -4;
  }
  
  private static String filterToken(String paramString, int paramInt1, int paramInt2)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    int i = 0;
    int j = 0;
    for (int k = paramInt1; k < paramInt2; k++)
    {
      char c = paramString.charAt(k);
      if ((c == '\n') && (j != 0))
      {
        j = 0;
      }
      else
      {
        j = 0;
        if (i == 0)
        {
          if (c == '\\') {
            i = 1;
          } else if (c == '\r') {
            j = 1;
          } else {
            localStringBuffer.append(c);
          }
        }
        else
        {
          localStringBuffer.append(c);
          i = 0;
        }
      }
    }
    return localStringBuffer.toString();
  }
  
  static class Token
  {
    private int type;
    private String value;
    public static final int ATOM = -1;
    public static final int QUOTEDSTRING = -2;
    public static final int COMMENT = -3;
    public static final int EOF = -4;
    
    public Token(int paramInt, String paramString)
    {
      type = paramInt;
      value = paramString;
    }
    
    public int getType()
    {
      return type;
    }
    
    public String getValue()
    {
      return value;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\encoding\HeaderTokenizer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */