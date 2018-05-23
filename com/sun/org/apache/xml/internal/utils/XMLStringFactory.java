package com.sun.org.apache.xml.internal.utils;

public abstract class XMLStringFactory
{
  public XMLStringFactory() {}
  
  public abstract XMLString newstr(String paramString);
  
  public abstract XMLString newstr(FastStringBuffer paramFastStringBuffer, int paramInt1, int paramInt2);
  
  public abstract XMLString newstr(char[] paramArrayOfChar, int paramInt1, int paramInt2);
  
  public abstract XMLString emptystr();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\utils\XMLStringFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */