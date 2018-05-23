package com.sun.org.apache.xerces.internal.impl.xpath.regex;

public class ParseException
  extends RuntimeException
{
  static final long serialVersionUID = -7012400318097691370L;
  int location;
  
  public ParseException(String paramString, int paramInt)
  {
    super(paramString);
    location = paramInt;
  }
  
  public int getLocation()
  {
    return location;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\xpath\regex\ParseException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */