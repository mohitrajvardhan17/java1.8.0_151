package com.sun.xml.internal.ws.wsdl.parser;

import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundFault;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundOperation;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundPortType;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLFault;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLInput;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLMessage;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLOperation;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLOutput;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLPort;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLPortType;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLService;
import com.sun.xml.internal.ws.api.wsdl.parser.WSDLParserExtension;
import com.sun.xml.internal.ws.api.wsdl.parser.WSDLParserExtensionContext;
import javax.xml.stream.XMLStreamReader;

class DelegatingParserExtension
  extends WSDLParserExtension
{
  protected final WSDLParserExtension core;
  
  public DelegatingParserExtension(WSDLParserExtension paramWSDLParserExtension)
  {
    core = paramWSDLParserExtension;
  }
  
  public void start(WSDLParserExtensionContext paramWSDLParserExtensionContext)
  {
    core.start(paramWSDLParserExtensionContext);
  }
  
  public void serviceAttributes(EditableWSDLService paramEditableWSDLService, XMLStreamReader paramXMLStreamReader)
  {
    core.serviceAttributes(paramEditableWSDLService, paramXMLStreamReader);
  }
  
  public boolean serviceElements(EditableWSDLService paramEditableWSDLService, XMLStreamReader paramXMLStreamReader)
  {
    return core.serviceElements(paramEditableWSDLService, paramXMLStreamReader);
  }
  
  public void portAttributes(EditableWSDLPort paramEditableWSDLPort, XMLStreamReader paramXMLStreamReader)
  {
    core.portAttributes(paramEditableWSDLPort, paramXMLStreamReader);
  }
  
  public boolean portElements(EditableWSDLPort paramEditableWSDLPort, XMLStreamReader paramXMLStreamReader)
  {
    return core.portElements(paramEditableWSDLPort, paramXMLStreamReader);
  }
  
  public boolean portTypeOperationInput(EditableWSDLOperation paramEditableWSDLOperation, XMLStreamReader paramXMLStreamReader)
  {
    return core.portTypeOperationInput(paramEditableWSDLOperation, paramXMLStreamReader);
  }
  
  public boolean portTypeOperationOutput(EditableWSDLOperation paramEditableWSDLOperation, XMLStreamReader paramXMLStreamReader)
  {
    return core.portTypeOperationOutput(paramEditableWSDLOperation, paramXMLStreamReader);
  }
  
  public boolean portTypeOperationFault(EditableWSDLOperation paramEditableWSDLOperation, XMLStreamReader paramXMLStreamReader)
  {
    return core.portTypeOperationFault(paramEditableWSDLOperation, paramXMLStreamReader);
  }
  
  public boolean definitionsElements(XMLStreamReader paramXMLStreamReader)
  {
    return core.definitionsElements(paramXMLStreamReader);
  }
  
  public boolean bindingElements(EditableWSDLBoundPortType paramEditableWSDLBoundPortType, XMLStreamReader paramXMLStreamReader)
  {
    return core.bindingElements(paramEditableWSDLBoundPortType, paramXMLStreamReader);
  }
  
  public void bindingAttributes(EditableWSDLBoundPortType paramEditableWSDLBoundPortType, XMLStreamReader paramXMLStreamReader)
  {
    core.bindingAttributes(paramEditableWSDLBoundPortType, paramXMLStreamReader);
  }
  
  public boolean portTypeElements(EditableWSDLPortType paramEditableWSDLPortType, XMLStreamReader paramXMLStreamReader)
  {
    return core.portTypeElements(paramEditableWSDLPortType, paramXMLStreamReader);
  }
  
  public void portTypeAttributes(EditableWSDLPortType paramEditableWSDLPortType, XMLStreamReader paramXMLStreamReader)
  {
    core.portTypeAttributes(paramEditableWSDLPortType, paramXMLStreamReader);
  }
  
  public boolean portTypeOperationElements(EditableWSDLOperation paramEditableWSDLOperation, XMLStreamReader paramXMLStreamReader)
  {
    return core.portTypeOperationElements(paramEditableWSDLOperation, paramXMLStreamReader);
  }
  
  public void portTypeOperationAttributes(EditableWSDLOperation paramEditableWSDLOperation, XMLStreamReader paramXMLStreamReader)
  {
    core.portTypeOperationAttributes(paramEditableWSDLOperation, paramXMLStreamReader);
  }
  
  public boolean bindingOperationElements(EditableWSDLBoundOperation paramEditableWSDLBoundOperation, XMLStreamReader paramXMLStreamReader)
  {
    return core.bindingOperationElements(paramEditableWSDLBoundOperation, paramXMLStreamReader);
  }
  
  public void bindingOperationAttributes(EditableWSDLBoundOperation paramEditableWSDLBoundOperation, XMLStreamReader paramXMLStreamReader)
  {
    core.bindingOperationAttributes(paramEditableWSDLBoundOperation, paramXMLStreamReader);
  }
  
  public boolean messageElements(EditableWSDLMessage paramEditableWSDLMessage, XMLStreamReader paramXMLStreamReader)
  {
    return core.messageElements(paramEditableWSDLMessage, paramXMLStreamReader);
  }
  
  public void messageAttributes(EditableWSDLMessage paramEditableWSDLMessage, XMLStreamReader paramXMLStreamReader)
  {
    core.messageAttributes(paramEditableWSDLMessage, paramXMLStreamReader);
  }
  
  public boolean portTypeOperationInputElements(EditableWSDLInput paramEditableWSDLInput, XMLStreamReader paramXMLStreamReader)
  {
    return core.portTypeOperationInputElements(paramEditableWSDLInput, paramXMLStreamReader);
  }
  
  public void portTypeOperationInputAttributes(EditableWSDLInput paramEditableWSDLInput, XMLStreamReader paramXMLStreamReader)
  {
    core.portTypeOperationInputAttributes(paramEditableWSDLInput, paramXMLStreamReader);
  }
  
  public boolean portTypeOperationOutputElements(EditableWSDLOutput paramEditableWSDLOutput, XMLStreamReader paramXMLStreamReader)
  {
    return core.portTypeOperationOutputElements(paramEditableWSDLOutput, paramXMLStreamReader);
  }
  
  public void portTypeOperationOutputAttributes(EditableWSDLOutput paramEditableWSDLOutput, XMLStreamReader paramXMLStreamReader)
  {
    core.portTypeOperationOutputAttributes(paramEditableWSDLOutput, paramXMLStreamReader);
  }
  
  public boolean portTypeOperationFaultElements(EditableWSDLFault paramEditableWSDLFault, XMLStreamReader paramXMLStreamReader)
  {
    return core.portTypeOperationFaultElements(paramEditableWSDLFault, paramXMLStreamReader);
  }
  
  public void portTypeOperationFaultAttributes(EditableWSDLFault paramEditableWSDLFault, XMLStreamReader paramXMLStreamReader)
  {
    core.portTypeOperationFaultAttributes(paramEditableWSDLFault, paramXMLStreamReader);
  }
  
  public boolean bindingOperationInputElements(EditableWSDLBoundOperation paramEditableWSDLBoundOperation, XMLStreamReader paramXMLStreamReader)
  {
    return core.bindingOperationInputElements(paramEditableWSDLBoundOperation, paramXMLStreamReader);
  }
  
  public void bindingOperationInputAttributes(EditableWSDLBoundOperation paramEditableWSDLBoundOperation, XMLStreamReader paramXMLStreamReader)
  {
    core.bindingOperationInputAttributes(paramEditableWSDLBoundOperation, paramXMLStreamReader);
  }
  
  public boolean bindingOperationOutputElements(EditableWSDLBoundOperation paramEditableWSDLBoundOperation, XMLStreamReader paramXMLStreamReader)
  {
    return core.bindingOperationOutputElements(paramEditableWSDLBoundOperation, paramXMLStreamReader);
  }
  
  public void bindingOperationOutputAttributes(EditableWSDLBoundOperation paramEditableWSDLBoundOperation, XMLStreamReader paramXMLStreamReader)
  {
    core.bindingOperationOutputAttributes(paramEditableWSDLBoundOperation, paramXMLStreamReader);
  }
  
  public boolean bindingOperationFaultElements(EditableWSDLBoundFault paramEditableWSDLBoundFault, XMLStreamReader paramXMLStreamReader)
  {
    return core.bindingOperationFaultElements(paramEditableWSDLBoundFault, paramXMLStreamReader);
  }
  
  public void bindingOperationFaultAttributes(EditableWSDLBoundFault paramEditableWSDLBoundFault, XMLStreamReader paramXMLStreamReader)
  {
    core.bindingOperationFaultAttributes(paramEditableWSDLBoundFault, paramXMLStreamReader);
  }
  
  public void finished(WSDLParserExtensionContext paramWSDLParserExtensionContext)
  {
    core.finished(paramWSDLParserExtensionContext);
  }
  
  public void postFinished(WSDLParserExtensionContext paramWSDLParserExtensionContext)
  {
    core.postFinished(paramWSDLParserExtensionContext);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\wsdl\parser\DelegatingParserExtension.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */