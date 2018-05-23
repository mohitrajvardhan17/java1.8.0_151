package org.xml.sax.helpers;

import org.xml.sax.Locator;

public class LocatorImpl
  implements Locator
{
  private String publicId;
  private String systemId;
  private int lineNumber;
  private int columnNumber;
  
  public LocatorImpl() {}
  
  public LocatorImpl(Locator paramLocator)
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\xml\sax\helpers\LocatorImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */