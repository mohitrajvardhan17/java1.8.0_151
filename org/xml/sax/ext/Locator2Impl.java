package org.xml.sax.ext;

import org.xml.sax.Locator;
import org.xml.sax.helpers.LocatorImpl;

public class Locator2Impl
  extends LocatorImpl
  implements Locator2
{
  private String encoding;
  private String version;
  
  public Locator2Impl() {}
  
  public Locator2Impl(Locator paramLocator)
  {
    super(paramLocator);
    if ((paramLocator instanceof Locator2))
    {
      Locator2 localLocator2 = (Locator2)paramLocator;
      version = localLocator2.getXMLVersion();
      encoding = localLocator2.getEncoding();
    }
  }
  
  public String getXMLVersion()
  {
    return version;
  }
  
  public String getEncoding()
  {
    return encoding;
  }
  
  public void setXMLVersion(String paramString)
  {
    version = paramString;
  }
  
  public void setEncoding(String paramString)
  {
    encoding = paramString;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\xml\sax\ext\Locator2Impl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */