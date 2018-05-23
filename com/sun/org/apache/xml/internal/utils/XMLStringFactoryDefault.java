package com.sun.org.apache.xml.internal.utils;

public class XMLStringFactoryDefault
  extends XMLStringFactory
{
  private static final XMLStringDefault EMPTY_STR = new XMLStringDefault("");
  
  public XMLStringFactoryDefault() {}
  
  public XMLString newstr(String paramString)
  {
    return new XMLStringDefault(paramString);
  }
  
  public XMLString newstr(FastStringBuffer paramFastStringBuffer, int paramInt1, int paramInt2)
  {
    return new XMLStringDefault(paramFastStringBuffer.getString(paramInt1, paramInt2));
  }
  
  public XMLString newstr(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    return new XMLStringDefault(new String(paramArrayOfChar, paramInt1, paramInt2));
  }
  
  public XMLString emptystr()
  {
    return EMPTY_STR;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\utils\XMLStringFactoryDefault.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */