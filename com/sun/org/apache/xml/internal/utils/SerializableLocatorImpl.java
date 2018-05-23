package com.sun.org.apache.xml.internal.utils;

import java.io.Serializable;
import org.xml.sax.Locator;

public class SerializableLocatorImpl
  implements Locator, Serializable
{
  static final long serialVersionUID = -2660312888446371460L;
  private String publicId;
  private String systemId;
  private int lineNumber;
  private int columnNumber;
  
  public SerializableLocatorImpl() {}
  
  public SerializableLocatorImpl(Locator paramLocator)
  {
    setPublicId(paramLocator.getPublicId());
    setSystemId(paramLocator.getSystemId());
    setLineNumber(paramLocator.getLineNumber());
    setColumnNumber(paramLocator.getColumnNumber());
  }
  
  public String getPublicId()
  {
    return publicId;
  }
  
  public String getSystemId()
  {
    return systemId;
  }
  
  public int getLineNumber()
  {
    return lineNumber;
  }
  
  public int getColumnNumber()
  {
    return columnNumber;
  }
  
  public void setPublicId(String paramString)
  {
    publicId = paramString;
  }
  
  public void setSystemId(String paramString)
  {
    systemId = paramString;
  }
  
  public void setLineNumber(int paramInt)
  {
    lineNumber = paramInt;
  }
  
  public void setColumnNumber(int paramInt)
  {
    columnNumber = paramInt;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\utils\SerializableLocatorImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */