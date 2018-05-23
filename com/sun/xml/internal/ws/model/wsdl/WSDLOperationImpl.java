package com.sun.xml.internal.ws.model.wsdl;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPartDescriptor;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLFault;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLInput;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLMessage;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLModel;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLOperation;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLOutput;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLPart;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLPortType;
import com.sun.xml.internal.ws.util.QNameMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

public final class WSDLOperationImpl
  extends AbstractExtensibleImpl
  implements EditableWSDLOperation
{
  private final QName name;
  private String parameterOrder;
  private EditableWSDLInput input;
  private EditableWSDLOutput output;
  private final List<EditableWSDLFault> faults;
  private final QNameMap<EditableWSDLFault> faultMap;
  protected Iterable<EditableWSDLMessage> messages;
  private final EditableWSDLPortType owner;
  
  public WSDLOperationImpl(XMLStreamReader paramXMLStreamReader, EditableWSDLPortType paramEditableWSDLPortType, QName paramQName)
  {
    super(paramXMLStreamReader);
    name = paramQName;
    faults = new ArrayList();
    faultMap = new QNameMap();
    owner = paramEditableWSDLPortType;
  }
  
  public QName getName()
  {
    return name;
  }
  
  public String getParameterOrder()
  {
    return parameterOrder;
  }
  
  public void setParameterOrder(String paramString)
  {
    parameterOrder = paramString;
  }
  
  public EditableWSDLInput getInput()
  {
    return input;
  }
  
  public void setInput(EditableWSDLInput paramEditableWSDLInput)
  {
    input = paramEditableWSDLInput;
  }
  
  public EditableWSDLOutput getOutput()
  {
    return output;
  }
  
  public boolean isOneWay()
  {
    return output == null;
  }
  
  public void setOutput(EditableWSDLOutput paramEditableWSDLOutput)
  {
    output = paramEditableWSDLOutput;
  }
  
  public Iterable<EditableWSDLFault> getFaults()
  {
    return faults;
  }
  
  public EditableWSDLFault getFault(QName paramQName)
  {
    EditableWSDLFault localEditableWSDLFault1 = (EditableWSDLFault)faultMap.get(paramQName);
    if (localEditableWSDLFault1 != null) {
      return localEditableWSDLFault1;
    }
    Iterator localIterator = faults.iterator();
    while (localIterator.hasNext())
    {
      EditableWSDLFault localEditableWSDLFault2 = (EditableWSDLFault)localIterator.next();
      assert (localEditableWSDLFault2.getMessage().parts().iterator().hasNext());
      EditableWSDLPart localEditableWSDLPart = (EditableWSDLPart)localEditableWSDLFault2.getMessage().parts().iterator().next();
      if (localEditableWSDLPart.getDescriptor().name().equals(paramQName))
      {
        faultMap.put(paramQName, localEditableWSDLFault2);
        return localEditableWSDLFault2;
      }
    }
    return null;
  }
  
  @NotNull
  public QName getPortTypeName()
  {
    return owner.getName();
  }
  
  public void addFault(EditableWSDLFault paramEditableWSDLFault)
  {
    faults.add(paramEditableWSDLFault);
  }
  
  public void freeze(EditableWSDLModel paramEditableWSDLModel)
  {
    assert (input != null);
    input.freeze(paramEditableWSDLModel);
    if (output != null) {
      output.freeze(paramEditableWSDLModel);
    }
    Iterator localIterator = faults.iterator();
    while (localIterator.hasNext())
    {
      EditableWSDLFault localEditableWSDLFault = (EditableWSDLFault)localIterator.next();
      localEditableWSDLFault.freeze(paramEditableWSDLModel);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\model\wsdl\WSDLOperationImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */