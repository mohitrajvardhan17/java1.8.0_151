package com.sun.xml.internal.ws.api.message;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.bind.api.Bridge;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.spi.db.XMLBridge;
import java.util.Set;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

public abstract interface Header
{
  public abstract boolean isIgnorable(@NotNull SOAPVersion paramSOAPVersion, @NotNull Set<String> paramSet);
  
  @NotNull
  public abstract String getRole(@NotNull SOAPVersion paramSOAPVersion);
  
  public abstract boolean isRelay();
  
  @NotNull
  public abstract String getNamespaceURI();
  
  @NotNull
  public abstract String getLocalPart();
  
  @Nullable
  public abstract String getAttribute(@NotNull String paramString1, @NotNull String paramString2);
  
  @Nullable
  public abstract String getAttribute(@NotNull QName paramQName);
  
  public abstract XMLStreamReader readHeader()
    throws XMLStreamException;
  
  public abstract <T> T readAsJAXB(Unmarshaller paramUnmarshaller)
    throws JAXBException;
  
  /**
   * @deprecated
   */
  public abstract <T> T readAsJAXB(Bridge<T> paramBridge)
    throws JAXBException;
  
  public abstract <T> T readAsJAXB(XMLBridge<T> paramXMLBridge)
    throws JAXBException;
  
  @NotNull
  public abstract WSEndpointReference readAsEPR(AddressingVersion paramAddressingVersion)
    throws XMLStreamException;
  
  public abstract void writeTo(XMLStreamWriter paramXMLStreamWriter)
    throws XMLStreamException;
  
  public abstract void writeTo(SOAPMessage paramSOAPMessage)
    throws SOAPException;
  
  public abstract void writeTo(ContentHandler paramContentHandler, ErrorHandler paramErrorHandler)
    throws SAXException;
  
  @NotNull
  public abstract String getStringContent();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\message\Header.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */