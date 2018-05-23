package com.sun.xml.internal.ws.model.wsdl;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.EndpointAddress;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundPortType;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLModel;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLPort;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLService;
import com.sun.xml.internal.ws.binding.WebServiceFeatureList;
import com.sun.xml.internal.ws.resources.ClientMessages;
import com.sun.xml.internal.ws.util.exception.LocatableWebServiceException;
import java.util.List;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;
import org.xml.sax.Locator;

public final class WSDLPortImpl
  extends AbstractFeaturedObjectImpl
  implements EditableWSDLPort
{
  private final QName name;
  private EndpointAddress address;
  private final QName bindingName;
  private final EditableWSDLService owner;
  private WSEndpointReference epr;
  private EditableWSDLBoundPortType boundPortType;
  
  public WSDLPortImpl(XMLStreamReader paramXMLStreamReader, EditableWSDLService paramEditableWSDLService, QName paramQName1, QName paramQName2)
  {
    super(paramXMLStreamReader);
    owner = paramEditableWSDLService;
    name = paramQName1;
    bindingName = paramQName2;
  }
  
  public QName getName()
  {
    return name;
  }
  
  public QName getBindingName()
  {
    return bindingName;
  }
  
  public EndpointAddress getAddress()
  {
    return address;
  }
  
  public EditableWSDLService getOwner()
  {
    return owner;
  }
  
  public void setAddress(EndpointAddress paramEndpointAddress)
  {
    assert (paramEndpointAddress != null);
    address = paramEndpointAddress;
  }
  
  public void setEPR(@NotNull WSEndpointReference paramWSEndpointReference)
  {
    assert (paramWSEndpointReference != null);
    addExtension(paramWSEndpointReference);
    epr = paramWSEndpointReference;
  }
  
  @Nullable
  public WSEndpointReference getEPR()
  {
    return epr;
  }
  
  public EditableWSDLBoundPortType getBinding()
  {
    return boundPortType;
  }
  
  public void freeze(EditableWSDLModel paramEditableWSDLModel)
  {
    boundPortType = paramEditableWSDLModel.getBinding(bindingName);
    if (boundPortType == null) {
      throw new LocatableWebServiceException(ClientMessages.UNDEFINED_BINDING(bindingName), new Locator[] { getLocation() });
    }
    if (features == null) {
      features = new WebServiceFeatureList();
    }
    features.setParentFeaturedObject(boundPortType);
    notUnderstoodExtensions.addAll(boundPortType.getNotUnderstoodExtensions());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\model\wsdl\WSDLPortImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */