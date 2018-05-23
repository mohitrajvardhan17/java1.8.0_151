package com.sun.org.apache.xerces.internal.util;

import com.sun.org.apache.xerces.internal.impl.XMLEntityManager;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import javax.xml.transform.Source;

public final class XMLInputSourceAdaptor
  implements Source
{
  public final XMLInputSource fSource;
  
  public XMLInputSourceAdaptor(XMLInputSource paramXMLInputSource)
  {
    fSource = paramXMLInputSource;
  }
  
  public void setSystemId(String paramString)
  {
    fSource.setSystemId(paramString);
  }
  
  public String getSystemId()
  {
    try
    {
      return XMLEntityManager.expandSystemId(fSource.getSystemId(), fSource.getBaseSystemId(), false);
    }
    catch (URI.MalformedURIException localMalformedURIException) {}
    return fSource.getSystemId();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\util\XMLInputSourceAdaptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */