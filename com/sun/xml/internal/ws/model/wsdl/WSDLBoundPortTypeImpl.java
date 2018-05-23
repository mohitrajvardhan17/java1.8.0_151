package com.sun.xml.internal.ws.model.wsdl;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.BindingID;
import com.sun.xml.internal.ws.api.model.ParameterBinding;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundOperation;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundPortType;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLModel;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLPortType;
import com.sun.xml.internal.ws.resources.ClientMessages;
import com.sun.xml.internal.ws.util.QNameMap;
import com.sun.xml.internal.ws.util.exception.LocatableWebServiceException;
import java.util.Iterator;
import javax.jws.WebParam.Mode;
import javax.jws.soap.SOAPBinding.Style;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;
import org.xml.sax.Locator;

public final class WSDLBoundPortTypeImpl
  extends AbstractFeaturedObjectImpl
  implements EditableWSDLBoundPortType
{
  private final QName name;
  private final QName portTypeName;
  private EditableWSDLPortType portType;
  private BindingID bindingId;
  @NotNull
  private final EditableWSDLModel owner;
  private final QNameMap<EditableWSDLBoundOperation> bindingOperations = new QNameMap();
  private QNameMap<EditableWSDLBoundOperation> payloadMap;
  private EditableWSDLBoundOperation emptyPayloadOperation;
  private SOAPBinding.Style style = SOAPBinding.Style.DOCUMENT;
  
  public WSDLBoundPortTypeImpl(XMLStreamReader paramXMLStreamReader, @NotNull EditableWSDLModel paramEditableWSDLModel, QName paramQName1, QName paramQName2)
  {
    super(paramXMLStreamReader);
    owner = paramEditableWSDLModel;
    name = paramQName1;
    portTypeName = paramQName2;
    paramEditableWSDLModel.addBinding(this);
  }
  
  public QName getName()
  {
    return name;
  }
  
  @NotNull
  public EditableWSDLModel getOwner()
  {
    return owner;
  }
  
  public EditableWSDLBoundOperation get(QName paramQName)
  {
    return (EditableWSDLBoundOperation)bindingOperations.get(paramQName);
  }
  
  public void put(QName paramQName, EditableWSDLBoundOperation paramEditableWSDLBoundOperation)
  {
    bindingOperations.put(paramQName, paramEditableWSDLBoundOperation);
  }
  
  public QName getPortTypeName()
  {
    return portTypeName;
  }
  
  public EditableWSDLPortType getPortType()
  {
    return portType;
  }
  
  public Iterable<EditableWSDLBoundOperation> getBindingOperations()
  {
    return bindingOperations.values();
  }
  
  public BindingID getBindingId()
  {
    return bindingId == null ? BindingID.SOAP11_HTTP : bindingId;
  }
  
  public void setBindingId(BindingID paramBindingID)
  {
    bindingId = paramBindingID;
  }
  
  public void setStyle(SOAPBinding.Style paramStyle)
  {
    style = paramStyle;
  }
  
  public SOAPBinding.Style getStyle()
  {
    return style;
  }
  
  public boolean isRpcLit()
  {
    return SOAPBinding.Style.RPC == style;
  }
  
  public boolean isDoclit()
  {
    return SOAPBinding.Style.DOCUMENT == style;
  }
  
  public ParameterBinding getBinding(QName paramQName, String paramString, WebParam.Mode paramMode)
  {
    EditableWSDLBoundOperation localEditableWSDLBoundOperation = get(paramQName);
    if (localEditableWSDLBoundOperation == null) {
      return null;
    }
    if ((WebParam.Mode.IN == paramMode) || (WebParam.Mode.INOUT == paramMode)) {
      return localEditableWSDLBoundOperation.getInputBinding(paramString);
    }
    return localEditableWSDLBoundOperation.getOutputBinding(paramString);
  }
  
  public EditableWSDLBoundOperation getOperation(String paramString1, String paramString2)
  {
    if ((paramString1 == null) && (paramString2 == null)) {
      return emptyPayloadOperation;
    }
    return (EditableWSDLBoundOperation)payloadMap.get(paramString1 == null ? "" : paramString1, paramString2);
  }
  
  public void freeze()
  {
    portType = owner.getPortType(portTypeName);
    if (portType == null) {
      throw new LocatableWebServiceException(ClientMessages.UNDEFINED_PORT_TYPE(portTypeName), new Locator[] { getLocation() });
    }
    portType.freeze();
    Iterator localIterator = bindingOperations.values().iterator();
    while (localIterator.hasNext())
    {
      EditableWSDLBoundOperation localEditableWSDLBoundOperation = (EditableWSDLBoundOperation)localIterator.next();
      localEditableWSDLBoundOperation.freeze(owner);
    }
    freezePayloadMap();
    owner.finalizeRpcLitBinding(this);
  }
  
  private void freezePayloadMap()
  {
    Iterator localIterator;
    EditableWSDLBoundOperation localEditableWSDLBoundOperation;
    if (style == SOAPBinding.Style.RPC)
    {
      payloadMap = new QNameMap();
      localIterator = bindingOperations.values().iterator();
      while (localIterator.hasNext())
      {
        localEditableWSDLBoundOperation = (EditableWSDLBoundOperation)localIterator.next();
        payloadMap.put(localEditableWSDLBoundOperation.getRequestPayloadName(), localEditableWSDLBoundOperation);
      }
    }
    else
    {
      payloadMap = new QNameMap();
      localIterator = bindingOperations.values().iterator();
      while (localIterator.hasNext())
      {
        localEditableWSDLBoundOperation = (EditableWSDLBoundOperation)localIterator.next();
        QName localQName = localEditableWSDLBoundOperation.getRequestPayloadName();
        if (localQName == null) {
          emptyPayloadOperation = localEditableWSDLBoundOperation;
        } else {
          payloadMap.put(localQName, localEditableWSDLBoundOperation);
        }
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\model\wsdl\WSDLBoundPortTypeImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */