package javax.xml.ws.wsaddressing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.ws.spi.Provider;
import org.w3c.dom.Element;

public final class W3CEndpointReferenceBuilder
{
  private String address;
  private List<Element> referenceParameters = new ArrayList();
  private List<Element> metadata = new ArrayList();
  private QName interfaceName;
  private QName serviceName;
  private QName endpointName;
  private String wsdlDocumentLocation;
  private Map<QName, String> attributes = new HashMap();
  private List<Element> elements = new ArrayList();
  
  public W3CEndpointReferenceBuilder() {}
  
  public W3CEndpointReferenceBuilder address(String paramString)
  {
    address = paramString;
    return this;
  }
  
  public W3CEndpointReferenceBuilder interfaceName(QName paramQName)
  {
    interfaceName = paramQName;
    return this;
  }
  
  public W3CEndpointReferenceBuilder serviceName(QName paramQName)
  {
    serviceName = paramQName;
    return this;
  }
  
  public W3CEndpointReferenceBuilder endpointName(QName paramQName)
  {
    if (serviceName == null) {
      throw new IllegalStateException("The W3CEndpointReferenceBuilder's serviceName must be set before setting the endpointName: " + paramQName);
    }
    endpointName = paramQName;
    return this;
  }
  
  public W3CEndpointReferenceBuilder wsdlDocumentLocation(String paramString)
  {
    wsdlDocumentLocation = paramString;
    return this;
  }
  
  public W3CEndpointReferenceBuilder referenceParameter(Element paramElement)
  {
    if (paramElement == null) {
      throw new IllegalArgumentException("The referenceParameter cannot be null.");
    }
    referenceParameters.add(paramElement);
    return this;
  }
  
  public W3CEndpointReferenceBuilder metadata(Element paramElement)
  {
    if (paramElement == null) {
      throw new IllegalArgumentException("The metadataElement cannot be null.");
    }
    metadata.add(paramElement);
    return this;
  }
  
  public W3CEndpointReferenceBuilder element(Element paramElement)
  {
    if (paramElement == null) {
      throw new IllegalArgumentException("The extension element cannot be null.");
    }
    elements.add(paramElement);
    return this;
  }
  
  public W3CEndpointReferenceBuilder attribute(QName paramQName, String paramString)
  {
    if ((paramQName == null) || (paramString == null)) {
      throw new IllegalArgumentException("The extension attribute name or value cannot be null.");
    }
    attributes.put(paramQName, paramString);
    return this;
  }
  
  public W3CEndpointReference build()
  {
    if ((elements.isEmpty()) && (attributes.isEmpty()) && (interfaceName == null)) {
      return Provider.provider().createW3CEndpointReference(address, serviceName, endpointName, metadata, wsdlDocumentLocation, referenceParameters);
    }
    return Provider.provider().createW3CEndpointReference(address, interfaceName, serviceName, endpointName, metadata, wsdlDocumentLocation, referenceParameters, elements, attributes);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\ws\wsaddressing\W3CEndpointReferenceBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */