package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import javax.xml.bind.ValidationEventLocator;
import javax.xml.bind.helpers.ValidationEventLocatorImpl;
import org.xml.sax.Locator;

class LocatorExWrapper
  implements LocatorEx
{
  private final Locator locator;
  
  public LocatorExWrapper(Locator paramLocator)
  {
    locator = paramLocator;
  }
  
  public ValidationEventLocator getLocation()
  {
    return new ValidationEventLocatorImpl(locator);
  }
  
  public String getPublicId()
  {
    return locator.getPublicId();
  }
  
  public String getSystemId()
  {
    return locator.getSystemId();
  }
  
  public int getLineNumber()
  {
    return locator.getLineNumber();
  }
  
  public int getColumnNumber()
  {
    return locator.getColumnNumber();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\unmarshaller\LocatorExWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */