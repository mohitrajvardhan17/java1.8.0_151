package com.sun.xml.internal.ws.message;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.bind.api.Bridge;
import com.sun.xml.internal.bind.api.BridgeContext;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.api.message.Header;
import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory;
import com.sun.xml.internal.ws.spi.db.XMLBridge;
import java.util.Set;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.xml.sax.helpers.AttributesImpl;

public abstract class AbstractHeaderImpl
  implements Header
{
  protected static final AttributesImpl EMPTY_ATTS = new AttributesImpl();
  
  protected AbstractHeaderImpl() {}
  
  /**
   * @deprecated
   */
  public final <T> T readAsJAXB(Bridge<T> paramBridge, BridgeContext paramBridgeContext)
    throws JAXBException
  {
    return (T)readAsJAXB(paramBridge);
  }
  
  public <T> T readAsJAXB(Unmarshaller paramUnmarshaller)
    throws JAXBException
  {
    try
    {
      return (T)paramUnmarshaller.unmarshal(readHeader());
    }
    catch (Exception localException)
    {
      throw new JAXBException(localException);
    }
  }
  
  /**
   * @deprecated
   */
  public <T> T readAsJAXB(Bridge<T> paramBridge)
    throws JAXBException
  {
    try
    {
      return (T)paramBridge.unmarshal(readHeader());
    }
    catch (XMLStreamException localXMLStreamException)
    {
      throw new JAXBException(localXMLStreamException);
    }
  }
  
  public <T> T readAsJAXB(XMLBridge<T> paramXMLBridge)
    throws JAXBException
  {
    try
    {
      return (T)paramXMLBridge.unmarshal(readHeader(), null);
    }
    catch (XMLStreamException localXMLStreamException)
    {
      throw new JAXBException(localXMLStreamException);
    }
  }
  
  public WSEndpointReference readAsEPR(AddressingVersion paramAddressingVersion)
    throws XMLStreamException
  {
    XMLStreamReader localXMLStreamReader = readHeader();
    WSEndpointReference localWSEndpointReference = new WSEndpointReference(localXMLStreamReader, paramAddressingVersion);
    XMLStreamReaderFactory.recycle(localXMLStreamReader);
    return localWSEndpointReference;
  }
  
  public boolean isIgnorable(@NotNull SOAPVersion paramSOAPVersion, @NotNull Set<String> paramSet)
  {
    String str = getAttribute(nsUri, "mustUnderstand");
    if ((str == null) || (!parseBool(str))) {
      return true;
    }
    if (paramSet == null) {
      return true;
    }
    return !paramSet.contains(getRole(paramSOAPVersion));
  }
  
  @NotNull
  public String getRole(@NotNull SOAPVersion paramSOAPVersion)
  {
    String str = getAttribute(nsUri, roleAttributeName);
    if (str == null) {
      str = implicitRole;
    }
    return str;
  }
  
  public boolean isRelay()
  {
    String str = getAttribute(SOAP_12nsUri, "relay");
    if (str == null) {
      return false;
    }
    return parseBool(str);
  }
  
  public String getAttribute(QName paramQName)
  {
    return getAttribute(paramQName.getNamespaceURI(), paramQName.getLocalPart());
  }
  
  protected final boolean parseBool(String paramString)
  {
    if (paramString.length() == 0) {
      return false;
    }
    int i = paramString.charAt(0);
    return (i == 116) || (i == 49);
  }
  
  public String getStringContent()
  {
    try
    {
      XMLStreamReader localXMLStreamReader = readHeader();
      localXMLStreamReader.nextTag();
      return localXMLStreamReader.getElementText();
    }
    catch (XMLStreamException localXMLStreamException) {}
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\message\AbstractHeaderImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */