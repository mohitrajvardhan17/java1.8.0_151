package com.sun.xml.internal.ws.addressing;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion.EPR;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference.EPRExtension;
import com.sun.xml.internal.ws.api.server.BoundEndpoint;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.api.server.Module;
import com.sun.xml.internal.ws.api.server.SDDocument;
import com.sun.xml.internal.ws.api.server.SDDocumentFilter;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory;
import com.sun.xml.internal.ws.server.WSEndpointImpl;
import com.sun.xml.internal.ws.util.xml.XMLStreamReaderToXMLStreamWriter;
import com.sun.xml.internal.ws.util.xml.XMLStreamWriterFilter;
import com.sun.xml.internal.ws.wsdl.parser.WSDLConstants;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

public class EPRSDDocumentFilter
  implements SDDocumentFilter
{
  private final WSEndpointImpl<?> endpoint;
  List<BoundEndpoint> beList;
  
  public EPRSDDocumentFilter(@NotNull WSEndpointImpl<?> paramWSEndpointImpl)
  {
    endpoint = paramWSEndpointImpl;
  }
  
  @Nullable
  private WSEndpointImpl<?> getEndpoint(String paramString1, String paramString2)
  {
    if ((paramString1 == null) || (paramString2 == null)) {
      return null;
    }
    if ((endpoint.getServiceName().getLocalPart().equals(paramString1)) && (endpoint.getPortName().getLocalPart().equals(paramString2))) {
      return endpoint;
    }
    if (beList == null)
    {
      localObject = (Module)endpoint.getContainer().getSPI(Module.class);
      if (localObject != null) {
        beList = ((Module)localObject).getBoundEndpoints();
      } else {
        beList = Collections.emptyList();
      }
    }
    Object localObject = beList.iterator();
    while (((Iterator)localObject).hasNext())
    {
      BoundEndpoint localBoundEndpoint = (BoundEndpoint)((Iterator)localObject).next();
      WSEndpoint localWSEndpoint = localBoundEndpoint.getEndpoint();
      if ((localWSEndpoint.getServiceName().getLocalPart().equals(paramString1)) && (localWSEndpoint.getPortName().getLocalPart().equals(paramString2))) {
        return (WSEndpointImpl)localWSEndpoint;
      }
    }
    return null;
  }
  
  public XMLStreamWriter filter(SDDocument paramSDDocument, XMLStreamWriter paramXMLStreamWriter)
    throws XMLStreamException, IOException
  {
    if (!paramSDDocument.isWSDL()) {
      return paramXMLStreamWriter;
    }
    new XMLStreamWriterFilter(paramXMLStreamWriter)
    {
      private boolean eprExtnFilterON = false;
      private boolean portHasEPR = false;
      private int eprDepth = -1;
      private String serviceName = null;
      private boolean onService = false;
      private int serviceDepth = -1;
      private String portName = null;
      private boolean onPort = false;
      private int portDepth = -1;
      private String portAddress;
      private boolean onPortAddress = false;
      
      private void handleStartElement(String paramAnonymousString1, String paramAnonymousString2)
        throws XMLStreamException
      {
        resetOnElementFlags();
        if (serviceDepth >= 0) {
          serviceDepth += 1;
        }
        if (portDepth >= 0) {
          portDepth += 1;
        }
        if (eprDepth >= 0) {
          eprDepth += 1;
        }
        if ((paramAnonymousString2.equals(WSDLConstants.QNAME_SERVICE.getNamespaceURI())) && (paramAnonymousString1.equals(WSDLConstants.QNAME_SERVICE.getLocalPart())))
        {
          onService = true;
          serviceDepth = 0;
        }
        else if ((paramAnonymousString2.equals(WSDLConstants.QNAME_PORT.getNamespaceURI())) && (paramAnonymousString1.equals(WSDLConstants.QNAME_PORT.getLocalPart())))
        {
          if (serviceDepth >= 1)
          {
            onPort = true;
            portDepth = 0;
          }
        }
        else if ((paramAnonymousString2.equals("http://www.w3.org/2005/08/addressing")) && (paramAnonymousString1.equals("EndpointReference")))
        {
          if ((serviceDepth >= 1) && (portDepth >= 1))
          {
            portHasEPR = true;
            eprDepth = 0;
          }
        }
        else if (((paramAnonymousString2.equals(WSDLConstants.NS_SOAP_BINDING_ADDRESS.getNamespaceURI())) || (paramAnonymousString2.equals(WSDLConstants.NS_SOAP12_BINDING_ADDRESS.getNamespaceURI()))) && (paramAnonymousString1.equals("address")) && (portDepth == 1))
        {
          onPortAddress = true;
        }
        WSEndpointImpl localWSEndpointImpl = EPRSDDocumentFilter.this.getEndpoint(serviceName, portName);
        if ((localWSEndpointImpl != null) && (eprDepth == 1) && (!paramAnonymousString2.equals("http://www.w3.org/2005/08/addressing"))) {
          eprExtnFilterON = true;
        }
      }
      
      private void resetOnElementFlags()
      {
        if (onService) {
          onService = false;
        }
        if (onPort) {
          onPort = false;
        }
        if (onPortAddress) {
          onPortAddress = false;
        }
      }
      
      private void writeEPRExtensions(Collection<WSEndpointReference.EPRExtension> paramAnonymousCollection)
        throws XMLStreamException
      {
        if (paramAnonymousCollection != null)
        {
          Iterator localIterator = paramAnonymousCollection.iterator();
          while (localIterator.hasNext())
          {
            WSEndpointReference.EPRExtension localEPRExtension = (WSEndpointReference.EPRExtension)localIterator.next();
            XMLStreamReaderToXMLStreamWriter localXMLStreamReaderToXMLStreamWriter = new XMLStreamReaderToXMLStreamWriter();
            XMLStreamReader localXMLStreamReader = localEPRExtension.readAsXMLStreamReader();
            localXMLStreamReaderToXMLStreamWriter.bridge(localXMLStreamReader, writer);
            XMLStreamReaderFactory.recycle(localXMLStreamReader);
          }
        }
      }
      
      public void writeStartElement(String paramAnonymousString1, String paramAnonymousString2, String paramAnonymousString3)
        throws XMLStreamException
      {
        handleStartElement(paramAnonymousString2, paramAnonymousString3);
        if (!eprExtnFilterON) {
          super.writeStartElement(paramAnonymousString1, paramAnonymousString2, paramAnonymousString3);
        }
      }
      
      public void writeStartElement(String paramAnonymousString1, String paramAnonymousString2)
        throws XMLStreamException
      {
        handleStartElement(paramAnonymousString2, paramAnonymousString1);
        if (!eprExtnFilterON) {
          super.writeStartElement(paramAnonymousString1, paramAnonymousString2);
        }
      }
      
      public void writeStartElement(String paramAnonymousString)
        throws XMLStreamException
      {
        if (!eprExtnFilterON) {
          super.writeStartElement(paramAnonymousString);
        }
      }
      
      private void handleEndElement()
        throws XMLStreamException
      {
        resetOnElementFlags();
        if ((portDepth == 0) && (!portHasEPR) && (EPRSDDocumentFilter.this.getEndpoint(serviceName, portName) != null))
        {
          writer.writeStartElement(AddressingVersion.W3C.getPrefix(), "EndpointReference", W3CnsUri);
          writer.writeNamespace(AddressingVersion.W3C.getPrefix(), W3CnsUri);
          writer.writeStartElement(AddressingVersion.W3C.getPrefix(), W3CeprType.address, W3CnsUri);
          writer.writeCharacters(portAddress);
          writer.writeEndElement();
          writeEPRExtensions(EPRSDDocumentFilter.this.getEndpoint(serviceName, portName).getEndpointReferenceExtensions());
          writer.writeEndElement();
        }
        if (eprDepth == 0)
        {
          if ((portHasEPR) && (EPRSDDocumentFilter.this.getEndpoint(serviceName, portName) != null)) {
            writeEPRExtensions(EPRSDDocumentFilter.this.getEndpoint(serviceName, portName).getEndpointReferenceExtensions());
          }
          eprExtnFilterON = false;
        }
        if (serviceDepth >= 0) {
          serviceDepth -= 1;
        }
        if (portDepth >= 0) {
          portDepth -= 1;
        }
        if (eprDepth >= 0) {
          eprDepth -= 1;
        }
        if (serviceDepth == -1) {
          serviceName = null;
        }
        if (portDepth == -1)
        {
          portHasEPR = false;
          portAddress = null;
          portName = null;
        }
      }
      
      public void writeEndElement()
        throws XMLStreamException
      {
        handleEndElement();
        if (!eprExtnFilterON) {
          super.writeEndElement();
        }
      }
      
      private void handleAttribute(String paramAnonymousString1, String paramAnonymousString2)
      {
        if (paramAnonymousString1.equals("name")) {
          if (onService)
          {
            serviceName = paramAnonymousString2;
            onService = false;
          }
          else if (onPort)
          {
            portName = paramAnonymousString2;
            onPort = false;
          }
        }
        if ((paramAnonymousString1.equals("location")) && (onPortAddress)) {
          portAddress = paramAnonymousString2;
        }
      }
      
      public void writeAttribute(String paramAnonymousString1, String paramAnonymousString2, String paramAnonymousString3, String paramAnonymousString4)
        throws XMLStreamException
      {
        handleAttribute(paramAnonymousString3, paramAnonymousString4);
        if (!eprExtnFilterON) {
          super.writeAttribute(paramAnonymousString1, paramAnonymousString2, paramAnonymousString3, paramAnonymousString4);
        }
      }
      
      public void writeAttribute(String paramAnonymousString1, String paramAnonymousString2, String paramAnonymousString3)
        throws XMLStreamException
      {
        handleAttribute(paramAnonymousString2, paramAnonymousString3);
        if (!eprExtnFilterON) {
          super.writeAttribute(paramAnonymousString1, paramAnonymousString2, paramAnonymousString3);
        }
      }
      
      public void writeAttribute(String paramAnonymousString1, String paramAnonymousString2)
        throws XMLStreamException
      {
        handleAttribute(paramAnonymousString1, paramAnonymousString2);
        if (!eprExtnFilterON) {
          super.writeAttribute(paramAnonymousString1, paramAnonymousString2);
        }
      }
      
      public void writeEmptyElement(String paramAnonymousString1, String paramAnonymousString2)
        throws XMLStreamException
      {
        if (!eprExtnFilterON) {
          super.writeEmptyElement(paramAnonymousString1, paramAnonymousString2);
        }
      }
      
      public void writeNamespace(String paramAnonymousString1, String paramAnonymousString2)
        throws XMLStreamException
      {
        if (!eprExtnFilterON) {
          super.writeNamespace(paramAnonymousString1, paramAnonymousString2);
        }
      }
      
      public void setNamespaceContext(NamespaceContext paramAnonymousNamespaceContext)
        throws XMLStreamException
      {
        if (!eprExtnFilterON) {
          super.setNamespaceContext(paramAnonymousNamespaceContext);
        }
      }
      
      public void setDefaultNamespace(String paramAnonymousString)
        throws XMLStreamException
      {
        if (!eprExtnFilterON) {
          super.setDefaultNamespace(paramAnonymousString);
        }
      }
      
      public void setPrefix(String paramAnonymousString1, String paramAnonymousString2)
        throws XMLStreamException
      {
        if (!eprExtnFilterON) {
          super.setPrefix(paramAnonymousString1, paramAnonymousString2);
        }
      }
      
      public void writeProcessingInstruction(String paramAnonymousString1, String paramAnonymousString2)
        throws XMLStreamException
      {
        if (!eprExtnFilterON) {
          super.writeProcessingInstruction(paramAnonymousString1, paramAnonymousString2);
        }
      }
      
      public void writeEmptyElement(String paramAnonymousString1, String paramAnonymousString2, String paramAnonymousString3)
        throws XMLStreamException
      {
        if (!eprExtnFilterON) {
          super.writeEmptyElement(paramAnonymousString1, paramAnonymousString2, paramAnonymousString3);
        }
      }
      
      public void writeCData(String paramAnonymousString)
        throws XMLStreamException
      {
        if (!eprExtnFilterON) {
          super.writeCData(paramAnonymousString);
        }
      }
      
      public void writeCharacters(String paramAnonymousString)
        throws XMLStreamException
      {
        if (!eprExtnFilterON) {
          super.writeCharacters(paramAnonymousString);
        }
      }
      
      public void writeComment(String paramAnonymousString)
        throws XMLStreamException
      {
        if (!eprExtnFilterON) {
          super.writeComment(paramAnonymousString);
        }
      }
      
      public void writeDTD(String paramAnonymousString)
        throws XMLStreamException
      {
        if (!eprExtnFilterON) {
          super.writeDTD(paramAnonymousString);
        }
      }
      
      public void writeDefaultNamespace(String paramAnonymousString)
        throws XMLStreamException
      {
        if (!eprExtnFilterON) {
          super.writeDefaultNamespace(paramAnonymousString);
        }
      }
      
      public void writeEmptyElement(String paramAnonymousString)
        throws XMLStreamException
      {
        if (!eprExtnFilterON) {
          super.writeEmptyElement(paramAnonymousString);
        }
      }
      
      public void writeEntityRef(String paramAnonymousString)
        throws XMLStreamException
      {
        if (!eprExtnFilterON) {
          super.writeEntityRef(paramAnonymousString);
        }
      }
      
      public void writeProcessingInstruction(String paramAnonymousString)
        throws XMLStreamException
      {
        if (!eprExtnFilterON) {
          super.writeProcessingInstruction(paramAnonymousString);
        }
      }
      
      public void writeCharacters(char[] paramAnonymousArrayOfChar, int paramAnonymousInt1, int paramAnonymousInt2)
        throws XMLStreamException
      {
        if (!eprExtnFilterON) {
          super.writeCharacters(paramAnonymousArrayOfChar, paramAnonymousInt1, paramAnonymousInt2);
        }
      }
    };
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\addressing\EPRSDDocumentFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */