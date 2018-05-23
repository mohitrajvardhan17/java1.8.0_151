package com.sun.xml.internal.ws.addressing;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.stream.buffer.XMLStreamBufferSource;
import com.sun.xml.internal.stream.buffer.stax.StreamWriterBufferCreator;
import com.sun.xml.internal.ws.addressing.v200408.MemberSubmissionAddressingConstants;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion.EPR;
import com.sun.xml.internal.ws.developer.MemberSubmissionEndpointReference;
import com.sun.xml.internal.ws.developer.MemberSubmissionEndpointReference.Address;
import com.sun.xml.internal.ws.developer.MemberSubmissionEndpointReference.AttributedQName;
import com.sun.xml.internal.ws.developer.MemberSubmissionEndpointReference.Elements;
import com.sun.xml.internal.ws.developer.MemberSubmissionEndpointReference.ServiceNameType;
import com.sun.xml.internal.ws.util.DOMUtil;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import com.sun.xml.internal.ws.wsdl.parser.WSDLConstants;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.dom.DOMResult;
import javax.xml.ws.EndpointReference;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.wsaddressing.W3CEndpointReference;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class EndpointReferenceUtil
{
  private static boolean w3cMetadataWritten = false;
  
  public EndpointReferenceUtil() {}
  
  public static <T extends EndpointReference> T transform(Class<T> paramClass, @NotNull EndpointReference paramEndpointReference)
  {
    assert (paramEndpointReference != null);
    if (paramClass.isAssignableFrom(W3CEndpointReference.class))
    {
      if ((paramEndpointReference instanceof W3CEndpointReference)) {
        return paramEndpointReference;
      }
      if ((paramEndpointReference instanceof MemberSubmissionEndpointReference)) {
        return toW3CEpr((MemberSubmissionEndpointReference)paramEndpointReference);
      }
    }
    else if (paramClass.isAssignableFrom(MemberSubmissionEndpointReference.class))
    {
      if ((paramEndpointReference instanceof W3CEndpointReference)) {
        return toMSEpr((W3CEndpointReference)paramEndpointReference);
      }
      if ((paramEndpointReference instanceof MemberSubmissionEndpointReference)) {
        return paramEndpointReference;
      }
    }
    throw new WebServiceException("Unknwon EndpointReference: " + paramEndpointReference.getClass());
  }
  
  private static W3CEndpointReference toW3CEpr(MemberSubmissionEndpointReference paramMemberSubmissionEndpointReference)
  {
    StreamWriterBufferCreator localStreamWriterBufferCreator = new StreamWriterBufferCreator();
    w3cMetadataWritten = false;
    try
    {
      localStreamWriterBufferCreator.writeStartDocument();
      localStreamWriterBufferCreator.writeStartElement(AddressingVersion.W3C.getPrefix(), "EndpointReference", W3CnsUri);
      localStreamWriterBufferCreator.writeNamespace(AddressingVersion.W3C.getPrefix(), W3CnsUri);
      localStreamWriterBufferCreator.writeStartElement(AddressingVersion.W3C.getPrefix(), W3CeprType.address, W3CnsUri);
      localStreamWriterBufferCreator.writeCharacters(addr.uri);
      localStreamWriterBufferCreator.writeEndElement();
      Object localObject2;
      if (((referenceProperties != null) && (referenceProperties.elements.size() > 0)) || ((referenceParameters != null) && (referenceParameters.elements.size() > 0)))
      {
        localStreamWriterBufferCreator.writeStartElement(AddressingVersion.W3C.getPrefix(), "ReferenceParameters", W3CnsUri);
        if (referenceProperties != null)
        {
          localObject1 = referenceProperties.elements.iterator();
          while (((Iterator)localObject1).hasNext())
          {
            localObject2 = (Element)((Iterator)localObject1).next();
            DOMUtil.serializeNode((Element)localObject2, localStreamWriterBufferCreator);
          }
        }
        if (referenceParameters != null)
        {
          localObject1 = referenceParameters.elements.iterator();
          while (((Iterator)localObject1).hasNext())
          {
            localObject2 = (Element)((Iterator)localObject1).next();
            DOMUtil.serializeNode((Element)localObject2, localStreamWriterBufferCreator);
          }
        }
        localStreamWriterBufferCreator.writeEndElement();
      }
      Object localObject1 = null;
      Element localElement;
      if ((elements != null) && (elements.size() > 0))
      {
        localObject2 = elements.iterator();
        while (((Iterator)localObject2).hasNext())
        {
          localElement = (Element)((Iterator)localObject2).next();
          if ((localElement.getNamespaceURI().equals(MemberSubmissionAddressingConstants.MEX_METADATA.getNamespaceURI())) && (localElement.getLocalName().equals(MemberSubmissionAddressingConstants.MEX_METADATA.getLocalPart())))
          {
            NodeList localNodeList = localElement.getElementsByTagNameNS("http://schemas.xmlsoap.org/wsdl/", WSDLConstants.QNAME_DEFINITIONS.getLocalPart());
            if (localNodeList != null) {
              localObject1 = (Element)localNodeList.item(0);
            }
          }
        }
      }
      if (localObject1 != null) {
        DOMUtil.serializeNode((Element)localObject1, localStreamWriterBufferCreator);
      }
      if (w3cMetadataWritten) {
        localStreamWriterBufferCreator.writeEndElement();
      }
      if ((elements != null) && (elements.size() > 0))
      {
        localObject2 = elements.iterator();
        while (((Iterator)localObject2).hasNext())
        {
          localElement = (Element)((Iterator)localObject2).next();
          if ((localElement.getNamespaceURI().equals("http://schemas.xmlsoap.org/wsdl/")) && (localElement.getLocalName().equals(WSDLConstants.QNAME_DEFINITIONS.getLocalPart()))) {}
          DOMUtil.serializeNode(localElement, localStreamWriterBufferCreator);
        }
      }
      localStreamWriterBufferCreator.writeEndElement();
      localStreamWriterBufferCreator.writeEndDocument();
      localStreamWriterBufferCreator.flush();
    }
    catch (XMLStreamException localXMLStreamException)
    {
      throw new WebServiceException(localXMLStreamException);
    }
    return new W3CEndpointReference(new XMLStreamBufferSource(localStreamWriterBufferCreator.getXMLStreamBuffer()));
  }
  
  private static MemberSubmissionEndpointReference toMSEpr(W3CEndpointReference paramW3CEndpointReference)
  {
    DOMResult localDOMResult = new DOMResult();
    paramW3CEndpointReference.writeTo(localDOMResult);
    Node localNode = localDOMResult.getNode();
    Element localElement1 = DOMUtil.getFirstElementChild(localNode);
    if (localElement1 == null) {
      return null;
    }
    MemberSubmissionEndpointReference localMemberSubmissionEndpointReference = new MemberSubmissionEndpointReference();
    NodeList localNodeList = localElement1.getChildNodes();
    for (int i = 0; i < localNodeList.getLength(); i++)
    {
      Object localObject1;
      Object localObject2;
      String str1;
      Object localObject3;
      if (localNodeList.item(i).getNodeType() == 1)
      {
        localObject1 = (Element)localNodeList.item(i);
        if ((((Element)localObject1).getNamespaceURI().equals(W3CnsUri)) && (((Element)localObject1).getLocalName().equals(W3CeprType.address)))
        {
          if (addr == null) {
            addr = new MemberSubmissionEndpointReference.Address();
          }
          addr.uri = XmlUtil.getTextForNode((Node)localObject1);
        }
        else if ((((Element)localObject1).getNamespaceURI().equals(W3CnsUri)) && (((Element)localObject1).getLocalName().equals("ReferenceParameters")))
        {
          localObject2 = ((Element)localObject1).getChildNodes();
          for (int j = 0; j < ((NodeList)localObject2).getLength(); j++) {
            if (((NodeList)localObject2).item(j).getNodeType() == 1)
            {
              if (referenceParameters == null)
              {
                referenceParameters = new MemberSubmissionEndpointReference.Elements();
                referenceParameters.elements = new ArrayList();
              }
              referenceParameters.elements.add((Element)((NodeList)localObject2).item(j));
            }
          }
        }
        else if ((((Element)localObject1).getNamespaceURI().equals(W3CnsUri)) && (((Element)localObject1).getLocalName().equals(W3CeprType.wsdlMetadata.getLocalPart())))
        {
          localObject2 = ((Element)localObject1).getChildNodes();
          str1 = ((Element)localObject1).getAttributeNS("http://www.w3.org/ns/wsdl-instance", "wsdlLocation");
          localObject3 = null;
          String str2;
          Object localObject5;
          Object localObject6;
          for (int k = 0; k < ((NodeList)localObject2).getLength(); k++)
          {
            localObject4 = ((NodeList)localObject2).item(k);
            if (((Node)localObject4).getNodeType() == 1)
            {
              localElement2 = (Element)localObject4;
              String str3;
              if (((localElement2.getNamespaceURI().equals(W3CwsdlNsUri)) || (localElement2.getNamespaceURI().equals("http://www.w3.org/2007/05/addressing/metadata"))) && (localElement2.getLocalName().equals(W3CeprType.serviceName)))
              {
                serviceName = new MemberSubmissionEndpointReference.ServiceNameType();
                serviceName.portName = localElement2.getAttribute(W3CeprType.portName);
                str2 = localElement2.getTextContent();
                localObject5 = XmlUtil.getPrefix(str2);
                localObject6 = XmlUtil.getLocalPart(str2);
                if (localObject6 != null)
                {
                  if (localObject5 != null)
                  {
                    str3 = localElement2.lookupNamespaceURI((String)localObject5);
                    if (str3 != null) {
                      serviceName.name = new QName(str3, (String)localObject6, (String)localObject5);
                    }
                  }
                  else
                  {
                    serviceName.name = new QName(null, (String)localObject6);
                  }
                  serviceName.attributes = getAttributes(localElement2);
                }
              }
              else if (((localElement2.getNamespaceURI().equals(W3CwsdlNsUri)) || (localElement2.getNamespaceURI().equals("http://www.w3.org/2007/05/addressing/metadata"))) && (localElement2.getLocalName().equals(W3CeprType.portTypeName)))
              {
                portTypeName = new MemberSubmissionEndpointReference.AttributedQName();
                str2 = localElement2.getTextContent();
                localObject5 = XmlUtil.getPrefix(str2);
                localObject6 = XmlUtil.getLocalPart(str2);
                if (localObject6 != null)
                {
                  if (localObject5 != null)
                  {
                    str3 = localElement2.lookupNamespaceURI((String)localObject5);
                    if (str3 != null) {
                      portTypeName.name = new QName(str3, (String)localObject6, (String)localObject5);
                    }
                  }
                  else
                  {
                    portTypeName.name = new QName(null, (String)localObject6);
                  }
                  portTypeName.attributes = getAttributes(localElement2);
                }
              }
              else if ((localElement2.getNamespaceURI().equals("http://schemas.xmlsoap.org/wsdl/")) && (localElement2.getLocalName().equals(WSDLConstants.QNAME_DEFINITIONS.getLocalPart())))
              {
                localObject3 = localElement2;
              }
              else
              {
                if (elements == null) {
                  elements = new ArrayList();
                }
                elements.add(localElement2);
              }
            }
          }
          Document localDocument = DOMUtil.createDom();
          Object localObject4 = localDocument.createElementNS(MemberSubmissionAddressingConstants.MEX_METADATA.getNamespaceURI(), MemberSubmissionAddressingConstants.MEX_METADATA.getPrefix() + ":" + MemberSubmissionAddressingConstants.MEX_METADATA.getLocalPart());
          Element localElement2 = localDocument.createElementNS(MemberSubmissionAddressingConstants.MEX_METADATA_SECTION.getNamespaceURI(), MemberSubmissionAddressingConstants.MEX_METADATA_SECTION.getPrefix() + ":" + MemberSubmissionAddressingConstants.MEX_METADATA_SECTION.getLocalPart());
          localElement2.setAttribute("Dialect", "http://schemas.xmlsoap.org/wsdl/");
          if ((localObject3 == null) && (str1 != null) && (!str1.equals("")))
          {
            str1 = str1.trim();
            str2 = str1.substring(0, str1.indexOf(' '));
            str1 = str1.substring(str1.indexOf(' ') + 1);
            localObject5 = localDocument.createElementNS("http://schemas.xmlsoap.org/wsdl/", "wsdl:" + WSDLConstants.QNAME_DEFINITIONS.getLocalPart());
            localObject6 = localDocument.createElementNS("http://schemas.xmlsoap.org/wsdl/", "wsdl:" + WSDLConstants.QNAME_IMPORT.getLocalPart());
            ((Element)localObject6).setAttribute("namespace", str2);
            ((Element)localObject6).setAttribute("location", str1);
            ((Element)localObject5).appendChild((Node)localObject6);
            localElement2.appendChild((Node)localObject5);
          }
          else if (localObject3 != null)
          {
            localElement2.appendChild((Node)localObject3);
          }
          ((Element)localObject4).appendChild(localElement2);
          if (elements == null) {
            elements = new ArrayList();
          }
          elements.add(localObject4);
        }
        else
        {
          if (elements == null) {
            elements = new ArrayList();
          }
          elements.add(localObject1);
        }
      }
      else if (localNodeList.item(i).getNodeType() == 2)
      {
        localObject1 = localNodeList.item(i);
        if (attributes == null)
        {
          attributes = new HashMap();
          localObject2 = fixNull(((Node)localObject1).getPrefix());
          str1 = fixNull(((Node)localObject1).getNamespaceURI());
          localObject3 = ((Node)localObject1).getLocalName();
          attributes.put(new QName(str1, (String)localObject3, (String)localObject2), ((Node)localObject1).getNodeValue());
        }
      }
    }
    return localMemberSubmissionEndpointReference;
  }
  
  private static Map<QName, String> getAttributes(Node paramNode)
  {
    HashMap localHashMap = null;
    NamedNodeMap localNamedNodeMap = paramNode.getAttributes();
    for (int i = 0; i < localNamedNodeMap.getLength(); i++)
    {
      if (localHashMap == null) {
        localHashMap = new HashMap();
      }
      Node localNode = localNamedNodeMap.item(i);
      String str1 = fixNull(localNode.getPrefix());
      String str2 = fixNull(localNode.getNamespaceURI());
      String str3 = localNode.getLocalName();
      if ((!str1.equals("xmlns")) && ((str1.length() != 0) || (!str3.equals("xmlns"))) && (!str3.equals(W3CeprType.portName))) {
        localHashMap.put(new QName(str2, str3, str1), localNode.getNodeValue());
      }
    }
    return localHashMap;
  }
  
  @NotNull
  private static String fixNull(@Nullable String paramString)
  {
    if (paramString == null) {
      return "";
    }
    return paramString;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\addressing\EndpointReferenceUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */