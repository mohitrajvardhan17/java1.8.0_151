package com.sun.org.apache.regexp.internal;

public class RESyntaxException
  extends RuntimeException
{
  public RESyntaxException(String paramString)
  {
    super("Syntax error: " + paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\regexp\internal\RESyntaxException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */