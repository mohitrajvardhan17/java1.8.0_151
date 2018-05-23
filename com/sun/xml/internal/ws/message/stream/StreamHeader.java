package com.sun.xml.internal.ws.message.stream;

import com.sun.istack.internal.FinalArrayList;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.stream.buffer.XMLStreamBuffer;
import com.sun.xml.internal.stream.buffer.XMLStreamBufferSource;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.message.AbstractHeaderImpl;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import java.util.Map;
import java.util.Set;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

public abstract class StreamHeader
  extends AbstractHeaderImpl
{
  protected final XMLStreamBuffer _mark;
  protected boolean _isMustUnderstand;
  @NotNull
  protected String _role;
  protected boolean _isRelay;
  protected String _localName;
  protected String _namespaceURI;
  private final FinalArrayList<Attribute> attributes;
  
  protected StreamHeader(XMLStreamReader paramXMLStreamReader, XMLStreamBuffer paramXMLStreamBuffer)
  {
    assert ((paramXMLStreamReader != null) && (paramXMLStreamBuffer != null));
    _mark = paramXMLStreamBuffer;
    _localName = paramXMLStreamReader.getLocalName();
    _namespaceURI = paramXMLStreamReader.getNamespaceURI();
    attributes = processHeaderAttributes(paramXMLStreamReader);
  }
  
  protected StreamHeader(XMLStreamReader paramXMLStreamReader)
    throws XMLStreamException
  {
    _localName = paramXMLStreamReader.getLocalName();
    _namespaceURI = paramXMLStreamReader.getNamespaceURI();
    attributes = processHeaderAttributes(paramXMLStreamReader);
    _mark = XMLStreamBuffer.createNewBufferFromXMLStreamReader(paramXMLStreamReader);
  }
  
  public final boolean isIgnorable(@NotNull SOAPVersion paramSOAPVersion, @NotNull Set<String> paramSet)
  {
    if (!_isMustUnderstand) {
      return true;
    }
    if (paramSet == null) {
      return true;
    }
    return !paramSet.contains(_role);
  }
  
  @NotNull
  public String getRole(@NotNull SOAPVersion paramSOAPVersion)
  {
    assert (_role != null);
    return _role;
  }
  
  public boolean isRelay()
  {
    return _isRelay;
  }
  
  @NotNull
  public String getNamespaceURI()
  {
    return _namespaceURI;
  }
  
  @NotNull
  public String getLocalPart()
  {
    return _localName;
  }
  
  public String getAttribute(String paramString1, String paramString2)
  {
    if (attributes != null) {
      for (int i = attributes.size() - 1; i >= 0; i--)
      {
        Attribute localAttribute = (Attribute)attributes.get(i);
        if ((localName.equals(paramString2)) && (nsUri.equals(paramString1))) {
          return value;
        }
      }
    }
    return null;
  }
  
  public XMLStreamReader readHeader()
    throws XMLStreamException
  {
    return _mark.readAsXMLStreamReader();
  }
  
  public void writeTo(XMLStreamWriter paramXMLStreamWriter)
    throws XMLStreamException
  {
    if (_mark.getInscopeNamespaces().size() > 0) {
      _mark.writeToXMLStreamWriter(paramXMLStreamWriter, true);
    } else {
      _mark.writeToXMLStreamWriter(paramXMLStreamWriter);
    }
  }
  
  public void writeTo(SOAPMessage paramSOAPMessage)
    throws SOAPException
  {
    try
    {
      TransformerFactory localTransformerFactory = XmlUtil.newTransformerFactory();
      Transformer localTransformer = localTransformerFactory.newTransformer();
      XMLStreamBufferSource localXMLStreamBufferSource = new XMLStreamBufferSource(_mark);
      DOMResult localDOMResult = new DOMResult();
      localTransformer.transform(localXMLStreamBufferSource, localDOMResult);
      Node localNode1 = localDOMResult.getNode();
      if (localNode1.getNodeType() == 9) {
        localNode1 = localNode1.getFirstChild();
      }
      SOAPHeader localSOAPHeader = paramSOAPMessage.getSOAPHeader();
      if (localSOAPHeader == null) {
        localSOAPHeader = paramSOAPMessage.getSOAPPart().getEnvelope().addHeader();
      }
      Node localNode2 = localSOAPHeader.getOwnerDocument().importNode(localNode1, true);
      localSOAPHeader.appendChild(localNode2);
    }
    catch (Exception localException)
    {
      throw new SOAPException(localException);
    }
  }
  
  public void writeTo(ContentHandler paramContentHandler, ErrorHandler paramErrorHandler)
    throws SAXException
  {
    _mark.writeTo(paramContentHandler);
  }
  
  @NotNull
  public WSEndpointReference readAsEPR(AddressingVersion paramAddressingVersion)
    throws XMLStreamException
  {
    return new WSEndpointReference(_mark, paramAddressingVersion);
  }
  
  protected abstract FinalArrayList<Attribute> processHeaderAttributes(XMLStreamReader paramXMLStreamReader);
  
  private static String fixNull(String paramString)
  {
    if (paramString == null) {
      return "";
    }
    return paramString;
  }
  
  protected static final class Attribute
  {
    final String nsUri;
    final String localName;
    final String value;
    
    public Attribute(String paramString1, String paramString2, String paramString3)
    {
      nsUri = StreamHeader.fixNull(paramString1);
      localName = paramString2;
      value = paramString3;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\message\stream\StreamHeader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */