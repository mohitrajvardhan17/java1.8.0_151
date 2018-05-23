package com.sun.xml.internal.ws.developer;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.addressing.v200408.MemberSubmissionAddressingConstants;
import com.sun.xml.internal.ws.wsdl.parser.WSDLConstants;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.ws.EndpointReference;
import javax.xml.ws.WebServiceException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

@XmlRootElement(name="EndpointReference", namespace="http://schemas.xmlsoap.org/ws/2004/08/addressing")
@XmlType(name="EndpointReferenceType", namespace="http://schemas.xmlsoap.org/ws/2004/08/addressing")
public final class MemberSubmissionEndpointReference
  extends EndpointReference
  implements MemberSubmissionAddressingConstants
{
  private static final ContextClassloaderLocal<JAXBContext> msjc = new ContextClassloaderLocal()
  {
    protected JAXBContext initialValue()
      throws Exception
    {
      return MemberSubmissionEndpointReference.access$000();
    }
  };
  @XmlElement(name="Address", namespace="http://schemas.xmlsoap.org/ws/2004/08/addressing")
  public Address addr;
  @XmlElement(name="ReferenceProperties", namespace="http://schemas.xmlsoap.org/ws/2004/08/addressing")
  public Elements referenceProperties;
  @XmlElement(name="ReferenceParameters", namespace="http://schemas.xmlsoap.org/ws/2004/08/addressing")
  public Elements referenceParameters;
  @XmlElement(name="PortType", namespace="http://schemas.xmlsoap.org/ws/2004/08/addressing")
  public AttributedQName portTypeName;
  @XmlElement(name="ServiceName", namespace="http://schemas.xmlsoap.org/ws/2004/08/addressing")
  public ServiceNameType serviceName;
  @XmlAnyAttribute
  public Map<QName, String> attributes;
  @XmlAnyElement
  public List<Element> elements;
  protected static final String MSNS = "http://schemas.xmlsoap.org/ws/2004/08/addressing";
  
  public MemberSubmissionEndpointReference() {}
  
  public MemberSubmissionEndpointReference(@NotNull Source paramSource)
  {
    if (paramSource == null) {
      throw new WebServiceException("Source parameter can not be null on constructor");
    }
    try
    {
      Unmarshaller localUnmarshaller = ((JAXBContext)msjc.get()).createUnmarshaller();
      MemberSubmissionEndpointReference localMemberSubmissionEndpointReference = (MemberSubmissionEndpointReference)localUnmarshaller.unmarshal(paramSource, MemberSubmissionEndpointReference.class).getValue();
      addr = addr;
      referenceProperties = referenceProperties;
      referenceParameters = referenceParameters;
      portTypeName = portTypeName;
      serviceName = serviceName;
      attributes = attributes;
      elements = elements;
    }
    catch (JAXBException localJAXBException)
    {
      throw new WebServiceException("Error unmarshalling MemberSubmissionEndpointReference ", localJAXBException);
    }
    catch (ClassCastException localClassCastException)
    {
      throw new WebServiceException("Source did not contain MemberSubmissionEndpointReference", localClassCastException);
    }
  }
  
  public void writeTo(Result paramResult)
  {
    try
    {
      Marshaller localMarshaller = ((JAXBContext)msjc.get()).createMarshaller();
      localMarshaller.marshal(this, paramResult);
    }
    catch (JAXBException localJAXBException)
    {
      throw new WebServiceException("Error marshalling W3CEndpointReference. ", localJAXBException);
    }
  }
  
  public Source toWSDLSource()
  {
    Object localObject = null;
    Iterator localIterator = elements.iterator();
    while (localIterator.hasNext())
    {
      Element localElement = (Element)localIterator.next();
      if ((localElement.getNamespaceURI().equals("http://schemas.xmlsoap.org/wsdl/")) && (localElement.getLocalName().equals(WSDLConstants.QNAME_DEFINITIONS.getLocalPart()))) {
        localObject = localElement;
      }
    }
    return new DOMSource((Node)localObject);
  }
  
  private static JAXBContext getMSJaxbContext()
  {
    try
    {
      return JAXBContext.newInstance(new Class[] { MemberSubmissionEndpointReference.class });
    }
    catch (JAXBException localJAXBException)
    {
      throw new WebServiceException("Error creating JAXBContext for MemberSubmissionEndpointReference. ", localJAXBException);
    }
  }
  
  @XmlType(name="address", namespace="http://schemas.xmlsoap.org/ws/2004/08/addressing")
  public static class Address
  {
    @XmlValue
    public String uri;
    @XmlAnyAttribute
    public Map<QName, String> attributes;
    
    public Address() {}
  }
  
  public static class AttributedQName
  {
    @XmlValue
    public QName name;
    @XmlAnyAttribute
    public Map<QName, String> attributes;
    
    public AttributedQName() {}
  }
  
  @XmlType(name="elements", namespace="http://schemas.xmlsoap.org/ws/2004/08/addressing")
  public static class Elements
  {
    @XmlAnyElement
    public List<Element> elements;
    
    public Elements() {}
  }
  
  public static class ServiceNameType
    extends MemberSubmissionEndpointReference.AttributedQName
  {
    @XmlAttribute(name="PortName")
    public String portName;
    
    public ServiceNameType() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\developer\MemberSubmissionEndpointReference.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */