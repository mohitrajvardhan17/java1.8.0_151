package com.sun.activation.registries;

import java.util.NoSuchElementException;
import java.util.Vector;

class LineTokenizer
{
  private int currentPosition = 0;
  private int maxPosition;
  private String str;
  private Vector stack = new Vector();
  private static final String singles = "=";
  
  public LineTokenizer(String paramString)
  {
    str = paramString;
    maxPosition = paramString.length();
  }
  
  private void skipWhiteSpace()
  {
    while ((currentPosition < maxPosition) && (Character.isWhitespace(str.charAt(currentPosition)))) {
      currentPosition += 1;
    }
  }
  
  public boolean hasMoreTokens()
  {
    if (stack.size() > 0) {
      return true;
    }
    skipWhiteSpace();
    return currentPosition < maxPosition;
  }
  
  public String nextToken()
  {
    int i = stack.size();
    if (i > 0)
    {
      String str1 = (String)stack.elementAt(i - 1);
      stack.removeElementAt(i - 1);
      return str1;
    }
    skipWhiteSpace();
    if (currentPosition >= maxPosition) {
      throw new NoSuchElementException();
    }
    int j = currentPosition;
    char c = str.charAt(j);
    if (c == '"')
    {
      currentPosition += 1;
      int k = 0;
      while (currentPosition < maxPosition)
      {
        c = str.charAt(currentPosition++);
        if (c == '\\')
        {
          currentPosition += 1;
          k = 1;
        }
        else if (c == '"')
        {
          String str2;
          if (k != 0)
          {
            StringBuffer localStringBuffer = new StringBuffer();
            for (int m = j + 1; m < currentPosition - 1; m++)
            {
              c = str.charAt(m);
              if (c != '\\') {
                localStringBuffer.append(c);
              }
            }
            str2 = localStringBuffer.toString();
          }
          else
          {
            str2 = str.substring(j + 1, currentPosition - 1);
          }
          return str2;
        }
      }
    }
    else if ("=".indexOf(c) >= 0)
    {
      currentPosition += 1;
    }
    else
    {
      while ((currentPosition < maxPosition) && ("=".indexOf(str.charAt(currentPosition)) < 0) && (!Character.isWhitespace(str.charAt(currentPosition)))) {
        currentPosition += 1;
      }
    }
    return str.substring(j, currentPosition);
  }
  
  public void pushToken(String paramString)
  {
    stack.addElement(paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\activation\registries\LineTokenizer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */