package com.sun.xml.internal.ws.wsdl.writer;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.addressing.W3CAddressingConstants;
import com.sun.xml.internal.ws.addressing.v200408.MemberSubmissionAddressingConstants;
import com.sun.xml.internal.ws.api.server.PortAddressResolver;
import com.sun.xml.internal.ws.util.xml.XMLStreamReaderToXMLStreamWriter;
import com.sun.xml.internal.ws.wsdl.parser.WSDLConstants;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

public final class WSDLPatcher
  extends XMLStreamReaderToXMLStreamWriter
{
  private static final String NS_XSD = "http://www.w3.org/2001/XMLSchema";
  private static final QName SCHEMA_INCLUDE_QNAME = new QName("http://www.w3.org/2001/XMLSchema", "include");
  private static final QName SCHEMA_IMPORT_QNAME = new QName("http://www.w3.org/2001/XMLSchema", "import");
  private static final QName SCHEMA_REDEFINE_QNAME = new QName("http://www.w3.org/2001/XMLSchema", "redefine");
  private static final Logger logger = Logger.getLogger("com.sun.xml.internal.ws.wsdl.patcher");
  private final DocumentLocationResolver docResolver;
  private final PortAddressResolver portAddressResolver;
  private String targetNamespace;
  private QName serviceName;
  private QName portName;
  private String portAddress;
  private boolean inEpr;
  private boolean inEprAddress;
  
  public WSDLPatcher(@NotNull PortAddressResolver paramPortAddressResolver, @NotNull DocumentLocationResolver paramDocumentLocationResolver)
  {
    portAddressResolver = paramPortAddressResolver;
    docResolver = paramDocumentLocationResolver;
  }
  
  protected void handleAttribute(int paramInt)
    throws XMLStreamException
  {
    QName localQName = in.getName();
    String str1 = in.getAttributeLocalName(paramInt);
    String str2;
    if (((localQName.equals(SCHEMA_INCLUDE_QNAME)) && (str1.equals("schemaLocation"))) || ((localQName.equals(SCHEMA_IMPORT_QNAME)) && (str1.equals("schemaLocation"))) || ((localQName.equals(SCHEMA_REDEFINE_QNAME)) && (str1.equals("schemaLocation"))) || ((localQName.equals(WSDLConstants.QNAME_IMPORT)) && (str1.equals("location"))))
    {
      str2 = in.getAttributeValue(paramInt);
      String str3 = getPatchedImportLocation(str2);
      if (str3 == null) {
        return;
      }
      logger.fine("Fixing the relative location:" + str2 + " with absolute location:" + str3);
      writeAttribute(paramInt, str3);
      return;
    }
    if (((localQName.equals(WSDLConstants.NS_SOAP_BINDING_ADDRESS)) || (localQName.equals(WSDLConstants.NS_SOAP12_BINDING_ADDRESS))) && (str1.equals("location")))
    {
      portAddress = in.getAttributeValue(paramInt);
      str2 = getAddressLocation();
      if (str2 != null)
      {
        logger.fine("Service:" + serviceName + " port:" + portName + " current address " + portAddress + " Patching it with " + str2);
        writeAttribute(paramInt, str2);
        return;
      }
    }
    super.handleAttribute(paramInt);
  }
  
  private void writeAttribute(int paramInt, String paramString)
    throws XMLStreamException
  {
    String str = in.getAttributeNamespace(paramInt);
    if (str != null) {
      out.writeAttribute(in.getAttributePrefix(paramInt), str, in.getAttributeLocalName(paramInt), paramString);
    } else {
      out.writeAttribute(in.getAttributeLocalName(paramInt), paramString);
    }
  }
  
  protected void handleStartElement()
    throws XMLStreamException
  {
    QName localQName = in.getName();
    String str;
    if (localQName.equals(WSDLConstants.QNAME_DEFINITIONS))
    {
      str = in.getAttributeValue(null, "targetNamespace");
      if (str != null) {
        targetNamespace = str;
      }
    }
    else if (localQName.equals(WSDLConstants.QNAME_SERVICE))
    {
      str = in.getAttributeValue(null, "name");
      if (str != null) {
        serviceName = new QName(targetNamespace, str);
      }
    }
    else if (localQName.equals(WSDLConstants.QNAME_PORT))
    {
      str = in.getAttributeValue(null, "name");
      if (str != null) {
        portName = new QName(targetNamespace, str);
      }
    }
    else if ((localQName.equals(W3CAddressingConstants.WSA_EPR_QNAME)) || (localQName.equals(MemberSubmissionAddressingConstants.WSA_EPR_QNAME)))
    {
      if ((serviceName != null) && (portName != null)) {
        inEpr = true;
      }
    }
    else if (((localQName.equals(W3CAddressingConstants.WSA_ADDRESS_QNAME)) || (localQName.equals(MemberSubmissionAddressingConstants.WSA_ADDRESS_QNAME))) && (inEpr))
    {
      inEprAddress = true;
    }
    super.handleStartElement();
  }
  
  protected void handleEndElement()
    throws XMLStreamException
  {
    QName localQName = in.getName();
    if (localQName.equals(WSDLConstants.QNAME_SERVICE))
    {
      serviceName = null;
    }
    else if (localQName.equals(WSDLConstants.QNAME_PORT))
    {
      portName = null;
    }
    else if ((localQName.equals(W3CAddressingConstants.WSA_EPR_QNAME)) || (localQName.equals(MemberSubmissionAddressingConstants.WSA_EPR_QNAME)))
    {
      if (inEpr) {
        inEpr = false;
      }
    }
    else if (((localQName.equals(W3CAddressingConstants.WSA_ADDRESS_QNAME)) || (localQName.equals(MemberSubmissionAddressingConstants.WSA_ADDRESS_QNAME))) && (inEprAddress))
    {
      String str = getAddressLocation();
      if (str != null)
      {
        logger.fine("Fixing EPR Address for service:" + serviceName + " port:" + portName + " address with " + str);
        out.writeCharacters(str);
      }
      inEprAddress = false;
    }
    super.handleEndElement();
  }
  
  protected void handleCharacters()
    throws XMLStreamException
  {
    if (inEprAddress)
    {
      String str = getAddressLocation();
      if (str != null) {
        return;
      }
    }
    super.handleCharacters();
  }
  
  @Nullable
  private String getPatchedImportLocation(String paramString)
  {
    return docResolver.getLocationFor(null, paramString);
  }
  
  private String getAddressLocation()
  {
    return (portAddressResolver == null) || (portName == null) ? null : portAddressResolver.getAddressFor(serviceName, portName.getLocalPart(), portAddress);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\wsdl\writer\WSDLPatcher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */