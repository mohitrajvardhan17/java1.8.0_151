package com.sun.xml.internal.ws.model.wsdl;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLObject;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamReader;
import org.xml.sax.Locator;
import org.xml.sax.helpers.LocatorImpl;

abstract class AbstractObjectImpl
  implements WSDLObject
{
  private final int lineNumber;
  private final String systemId;
  
  AbstractObjectImpl(XMLStreamReader paramXMLStreamReader)
  {
    Location localLocation = paramXMLStreamReader.getLocation();
    lineNumber = localLocation.getLineNumber();
    systemId = localLocation.getSystemId();
  }
  
  AbstractObjectImpl(String paramString, int paramInt)
  {
    systemId = paramString;
    lineNumber = paramInt;
  }
  
  @NotNull
  public final Locator getLocation()
  {
    LocatorImpl localLocatorImpl = new LocatorImpl();
    localLocatorImpl.setSystemId(systemId);
    localLocatorImpl.setLineNumber(lineNumber);
    return localLocatorImpl;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\model\wsdl\AbstractObjectImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */