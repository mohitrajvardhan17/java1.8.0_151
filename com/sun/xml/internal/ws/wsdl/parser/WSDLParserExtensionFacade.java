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
import com.sun.xml.internal.ws.streaming.XMLStreamReaderUtil;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamReader;
import org.xml.sax.Locator;
import org.xml.sax.helpers.LocatorImpl;

final class WSDLParserExtensionFacade
  extends WSDLParserExtension
{
  private final WSDLParserExtension[] extensions;
  
  WSDLParserExtensionFacade(WSDLParserExtension... paramVarArgs)
  {
    assert (paramVarArgs != null);
    extensions = paramVarArgs;
  }
  
  public void start(WSDLParserExtensionContext paramWSDLParserExtensionContext)
  {
    for (WSDLParserExtension localWSDLParserExtension : extensions) {
      localWSDLParserExtension.start(paramWSDLParserExtensionContext);
    }
  }
  
  public boolean serviceElements(EditableWSDLService paramEditableWSDLService, XMLStreamReader paramXMLStreamReader)
  {
    for (WSDLParserExtension localWSDLParserExtension : extensions) {
      if (localWSDLParserExtension.serviceElements(paramEditableWSDLService, paramXMLStreamReader)) {
        return true;
      }
    }
    XMLStreamReaderUtil.skipElement(paramXMLStreamReader);
    return true;
  }
  
  public void serviceAttributes(EditableWSDLService paramEditableWSDLService, XMLStreamReader paramXMLStreamReader)
  {
    for (WSDLParserExtension localWSDLParserExtension : extensions) {
      localWSDLParserExtension.serviceAttributes(paramEditableWSDLService, paramXMLStreamReader);
    }
  }
  
  public boolean portElements(EditableWSDLPort paramEditableWSDLPort, XMLStreamReader paramXMLStreamReader)
  {
    for (WSDLParserExtension localWSDLParserExtension : extensions) {
      if (localWSDLParserExtension.portElements(paramEditableWSDLPort, paramXMLStreamReader)) {
        return true;
      }
    }
    if (isRequiredExtension(paramXMLStreamReader)) {
      paramEditableWSDLPort.addNotUnderstoodExtension(paramXMLStreamReader.getName(), getLocator(paramXMLStreamReader));
    }
    XMLStreamReaderUtil.skipElement(paramXMLStreamReader);
    return true;
  }
  
  public boolean portTypeOperationInput(EditableWSDLOperation paramEditableWSDLOperation, XMLStreamReader paramXMLStreamReader)
  {
    for (WSDLParserExtension localWSDLParserExtension : extensions) {
      localWSDLParserExtension.portTypeOperationInput(paramEditableWSDLOperation, paramXMLStreamReader);
    }
    return false;
  }
  
  public boolean portTypeOperationOutput(EditableWSDLOperation paramEditableWSDLOperation, XMLStreamReader paramXMLStreamReader)
  {
    for (WSDLParserExtension localWSDLParserExtension : extensions) {
      localWSDLParserExtension.portTypeOperationOutput(paramEditableWSDLOperation, paramXMLStreamReader);
    }
    return false;
  }
  
  public boolean portTypeOperationFault(EditableWSDLOperation paramEditableWSDLOperation, XMLStreamReader paramXMLStreamReader)
  {
    for (WSDLParserExtension localWSDLParserExtension : extensions) {
      localWSDLParserExtension.portTypeOperationFault(paramEditableWSDLOperation, paramXMLStreamReader);
    }
    return false;
  }
  
  public void portAttributes(EditableWSDLPort paramEditableWSDLPort, XMLStreamReader paramXMLStreamReader)
  {
    for (WSDLParserExtension localWSDLParserExtension : extensions) {
      localWSDLParserExtension.portAttributes(paramEditableWSDLPort, paramXMLStreamReader);
    }
  }
  
  public boolean definitionsElements(XMLStreamReader paramXMLStreamReader)
  {
    for (WSDLParserExtension localWSDLParserExtension : extensions) {
      if (localWSDLParserExtension.definitionsElements(paramXMLStreamReader)) {
        return true;
      }
    }
    XMLStreamReaderUtil.skipElement(paramXMLStreamReader);
    return true;
  }
  
  public boolean bindingElements(EditableWSDLBoundPortType paramEditableWSDLBoundPortType, XMLStreamReader paramXMLStreamReader)
  {
    for (WSDLParserExtension localWSDLParserExtension : extensions) {
      if (localWSDLParserExtension.bindingElements(paramEditableWSDLBoundPortType, paramXMLStreamReader)) {
        return true;
      }
    }
    if (isRequiredExtension(paramXMLStreamReader)) {
      paramEditableWSDLBoundPortType.addNotUnderstoodExtension(paramXMLStreamReader.getName(), getLocator(paramXMLStreamReader));
    }
    XMLStreamReaderUtil.skipElement(paramXMLStreamReader);
    return true;
  }
  
  public void bindingAttributes(EditableWSDLBoundPortType paramEditableWSDLBoundPortType, XMLStreamReader paramXMLStreamReader)
  {
    for (WSDLParserExtension localWSDLParserExtension : extensions) {
      localWSDLParserExtension.bindingAttributes(paramEditableWSDLBoundPortType, paramXMLStreamReader);
    }
  }
  
  public boolean portTypeElements(EditableWSDLPortType paramEditableWSDLPortType, XMLStreamReader paramXMLStreamReader)
  {
    for (WSDLParserExtension localWSDLParserExtension : extensions) {
      if (localWSDLParserExtension.portTypeElements(paramEditableWSDLPortType, paramXMLStreamReader)) {
        return true;
      }
    }
    XMLStreamReaderUtil.skipElement(paramXMLStreamReader);
    return true;
  }
  
  public void portTypeAttributes(EditableWSDLPortType paramEditableWSDLPortType, XMLStreamReader paramXMLStreamReader)
  {
    for (WSDLParserExtension localWSDLParserExtension : extensions) {
      localWSDLParserExtension.portTypeAttributes(paramEditableWSDLPortType, paramXMLStreamReader);
    }
  }
  
  public boolean portTypeOperationElements(EditableWSDLOperation paramEditableWSDLOperation, XMLStreamReader paramXMLStreamReader)
  {
    for (WSDLParserExtension localWSDLParserExtension : extensions) {
      if (localWSDLParserExtension.portTypeOperationElements(paramEditableWSDLOperation, paramXMLStreamReader)) {
        return true;
      }
    }
    XMLStreamReaderUtil.skipElement(paramXMLStreamReader);
    return true;
  }
  
  public void portTypeOperationAttributes(EditableWSDLOperation paramEditableWSDLOperation, XMLStreamReader paramXMLStreamReader)
  {
    for (WSDLParserExtension localWSDLParserExtension : extensions) {
      localWSDLParserExtension.portTypeOperationAttributes(paramEditableWSDLOperation, paramXMLStreamReader);
    }
  }
  
  public boolean bindingOperationElements(EditableWSDLBoundOperation paramEditableWSDLBoundOperation, XMLStreamReader paramXMLStreamReader)
  {
    for (WSDLParserExtension localWSDLParserExtension : extensions) {
      if (localWSDLParserExtension.bindingOperationElements(paramEditableWSDLBoundOperation, paramXMLStreamReader)) {
        return true;
      }
    }
    XMLStreamReaderUtil.skipElement(paramXMLStreamReader);
    return true;
  }
  
  public void bindingOperationAttributes(EditableWSDLBoundOperation paramEditableWSDLBoundOperation, XMLStreamReader paramXMLStreamReader)
  {
    for (WSDLParserExtension localWSDLParserExtension : extensions) {
      localWSDLParserExtension.bindingOperationAttributes(paramEditableWSDLBoundOperation, paramXMLStreamReader);
    }
  }
  
  public boolean messageElements(EditableWSDLMessage paramEditableWSDLMessage, XMLStreamReader paramXMLStreamReader)
  {
    for (WSDLParserExtension localWSDLParserExtension : extensions) {
      if (localWSDLParserExtension.messageElements(paramEditableWSDLMessage, paramXMLStreamReader)) {
        return true;
      }
    }
    XMLStreamReaderUtil.skipElement(paramXMLStreamReader);
    return true;
  }
  
  public void messageAttributes(EditableWSDLMessage paramEditableWSDLMessage, XMLStreamReader paramXMLStreamReader)
  {
    for (WSDLParserExtension localWSDLParserExtension : extensions) {
      localWSDLParserExtension.messageAttributes(paramEditableWSDLMessage, paramXMLStreamReader);
    }
  }
  
  public boolean portTypeOperationInputElements(EditableWSDLInput paramEditableWSDLInput, XMLStreamReader paramXMLStreamReader)
  {
    for (WSDLParserExtension localWSDLParserExtension : extensions) {
      if (localWSDLParserExtension.portTypeOperationInputElements(paramEditableWSDLInput, paramXMLStreamReader)) {
        return true;
      }
    }
    XMLStreamReaderUtil.skipElement(paramXMLStreamReader);
    return true;
  }
  
  public void portTypeOperationInputAttributes(EditableWSDLInput paramEditableWSDLInput, XMLStreamReader paramXMLStreamReader)
  {
    for (WSDLParserExtension localWSDLParserExtension : extensions) {
      localWSDLParserExtension.portTypeOperationInputAttributes(paramEditableWSDLInput, paramXMLStreamReader);
    }
  }
  
  public boolean portTypeOperationOutputElements(EditableWSDLOutput paramEditableWSDLOutput, XMLStreamReader paramXMLStreamReader)
  {
    for (WSDLParserExtension localWSDLParserExtension : extensions) {
      if (localWSDLParserExtension.portTypeOperationOutputElements(paramEditableWSDLOutput, paramXMLStreamReader)) {
        return true;
      }
    }
    XMLStreamReaderUtil.skipElement(paramXMLStreamReader);
    return true;
  }
  
  public void portTypeOperationOutputAttributes(EditableWSDLOutput paramEditableWSDLOutput, XMLStreamReader paramXMLStreamReader)
  {
    for (WSDLParserExtension localWSDLParserExtension : extensions) {
      localWSDLParserExtension.portTypeOperationOutputAttributes(paramEditableWSDLOutput, paramXMLStreamReader);
    }
  }
  
  public boolean portTypeOperationFaultElements(EditableWSDLFault paramEditableWSDLFault, XMLStreamReader paramXMLStreamReader)
  {
    for (WSDLParserExtension localWSDLParserExtension : extensions) {
      if (localWSDLParserExtension.portTypeOperationFaultElements(paramEditableWSDLFault, paramXMLStreamReader)) {
        return true;
      }
    }
    XMLStreamReaderUtil.skipElement(paramXMLStreamReader);
    return true;
  }
  
  public void portTypeOperationFaultAttributes(EditableWSDLFault paramEditableWSDLFault, XMLStreamReader paramXMLStreamReader)
  {
    for (WSDLParserExtension localWSDLParserExtension : extensions) {
      localWSDLParserExtension.portTypeOperationFaultAttributes(paramEditableWSDLFault, paramXMLStreamReader);
    }
  }
  
  public boolean bindingOperationInputElements(EditableWSDLBoundOperation paramEditableWSDLBoundOperation, XMLStreamReader paramXMLStreamReader)
  {
    for (WSDLParserExtension localWSDLParserExtension : extensions) {
      if (localWSDLParserExtension.bindingOperationInputElements(paramEditableWSDLBoundOperation, paramXMLStreamReader)) {
        return true;
      }
    }
    XMLStreamReaderUtil.skipElement(paramXMLStreamReader);
    return true;
  }
  
  public void bindingOperationInputAttributes(EditableWSDLBoundOperation paramEditableWSDLBoundOperation, XMLStreamReader paramXMLStreamReader)
  {
    for (WSDLParserExtension localWSDLParserExtension : extensions) {
      localWSDLParserExtension.bindingOperationInputAttributes(paramEditableWSDLBoundOperation, paramXMLStreamReader);
    }
  }
  
  public boolean bindingOperationOutputElements(EditableWSDLBoundOperation paramEditableWSDLBoundOperation, XMLStreamReader paramXMLStreamReader)
  {
    for (WSDLParserExtension localWSDLParserExtension : extensions) {
      if (localWSDLParserExtension.bindingOperationOutputElements(paramEditableWSDLBoundOperation, paramXMLStreamReader)) {
        return true;
      }
    }
    XMLStreamReaderUtil.skipElement(paramXMLStreamReader);
    return true;
  }
  
  public void bindingOperationOutputAttributes(EditableWSDLBoundOperation paramEditableWSDLBoundOperation, XMLStreamReader paramXMLStreamReader)
  {
    for (WSDLParserExtension localWSDLParserExtension : extensions) {
      localWSDLParserExtension.bindingOperationOutputAttributes(paramEditableWSDLBoundOperation, paramXMLStreamReader);
    }
  }
  
  public boolean bindingOperationFaultElements(EditableWSDLBoundFault paramEditableWSDLBoundFault, XMLStreamReader paramXMLStreamReader)
  {
    for (WSDLParserExtension localWSDLParserExtension : extensions) {
      if (localWSDLParserExtension.bindingOperationFaultElements(paramEditableWSDLBoundFault, paramXMLStreamReader)) {
        return true;
      }
    }
    XMLStreamReaderUtil.skipElement(paramXMLStreamReader);
    return true;
  }
  
  public void bindingOperationFaultAttributes(EditableWSDLBoundFault paramEditableWSDLBoundFault, XMLStreamReader paramXMLStreamReader)
  {
    for (WSDLParserExtension localWSDLParserExtension : extensions) {
      localWSDLParserExtension.bindingOperationFaultAttributes(paramEditableWSDLBoundFault, paramXMLStreamReader);
    }
  }
  
  public void finished(WSDLParserExtensionContext paramWSDLParserExtensionContext)
  {
    for (WSDLParserExtension localWSDLParserExtension : extensions) {
      localWSDLParserExtension.finished(paramWSDLParserExtensionContext);
    }
  }
  
  public void postFinished(WSDLParserExtensionContext paramWSDLParserExtensionContext)
  {
    for (WSDLParserExtension localWSDLParserExtension : extensions) {
      localWSDLParserExtension.postFinished(paramWSDLParserExtensionContext);
    }
  }
  
  private boolean isRequiredExtension(XMLStreamReader paramXMLStreamReader)
  {
    String str = paramXMLStreamReader.getAttributeValue("http://schemas.xmlsoap.org/wsdl/", "required");
    if (str != null) {
      return Boolean.parseBoolean(str);
    }
    return false;
  }
  
  private Locator getLocator(XMLStreamReader paramXMLStreamReader)
  {
    Location localLocation = paramXMLStreamReader.getLocation();
    LocatorImpl localLocatorImpl = new LocatorImpl();
    localLocatorImpl.setSystemId(localLocation.getSystemId());
    localLocatorImpl.setLineNumber(localLocation.getLineNumber());
    return localLocatorImpl;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\wsdl\parser\WSDLParserExtensionFacade.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */