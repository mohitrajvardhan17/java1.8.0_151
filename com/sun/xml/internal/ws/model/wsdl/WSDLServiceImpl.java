package com.sun.xml.internal.ws.model.wsdl;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundPortType;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLModel;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLPort;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLService;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

public final class WSDLServiceImpl
  extends AbstractExtensibleImpl
  implements EditableWSDLService
{
  private final QName name;
  private final Map<QName, EditableWSDLPort> ports;
  private final EditableWSDLModel parent;
  
  public WSDLServiceImpl(XMLStreamReader paramXMLStreamReader, EditableWSDLModel paramEditableWSDLModel, QName paramQName)
  {
    super(paramXMLStreamReader);
    parent = paramEditableWSDLModel;
    name = paramQName;
    ports = new LinkedHashMap();
  }
  
  @NotNull
  public EditableWSDLModel getParent()
  {
    return parent;
  }
  
  public QName getName()
  {
    return name;
  }
  
  public EditableWSDLPort get(QName paramQName)
  {
    return (EditableWSDLPort)ports.get(paramQName);
  }
  
  public EditableWSDLPort getFirstPort()
  {
    if (ports.isEmpty()) {
      return null;
    }
    return (EditableWSDLPort)ports.values().iterator().next();
  }
  
  public Iterable<EditableWSDLPort> getPorts()
  {
    return ports.values();
  }
  
  @Nullable
  public EditableWSDLPort getMatchingPort(QName paramQName)
  {
    Iterator localIterator = getPorts().iterator();
    while (localIterator.hasNext())
    {
      EditableWSDLPort localEditableWSDLPort = (EditableWSDLPort)localIterator.next();
      QName localQName = localEditableWSDLPort.getBinding().getPortTypeName();
      assert (localQName != null);
      if (localQName.equals(paramQName)) {
        return localEditableWSDLPort;
      }
    }
    return null;
  }
  
  public void put(QName paramQName, EditableWSDLPort paramEditableWSDLPort)
  {
    if ((paramQName == null) || (paramEditableWSDLPort == null)) {
      throw new NullPointerException();
    }
    ports.put(paramQName, paramEditableWSDLPort);
  }
  
  public void freeze(EditableWSDLModel paramEditableWSDLModel)
  {
    Iterator localIterator = ports.values().iterator();
    while (localIterator.hasNext())
    {
      EditableWSDLPort localEditableWSDLPort = (EditableWSDLPort)localIterator.next();
      localEditableWSDLPort.freeze(paramEditableWSDLModel);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\model\wsdl\WSDLServiceImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */