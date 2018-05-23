package com.sun.xml.internal.ws.model.wsdl;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.model.ParameterBinding;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundOperation.ANONYMOUS;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPartDescriptor;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundFault;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundOperation;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundPortType;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLInput;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLMessage;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLModel;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLOperation;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLOutput;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLPart;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLPortType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.jws.WebParam.Mode;
import javax.jws.soap.SOAPBinding.Style;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

public final class WSDLBoundOperationImpl
  extends AbstractExtensibleImpl
  implements EditableWSDLBoundOperation
{
  private final QName name;
  private final Map<String, ParameterBinding> inputParts;
  private final Map<String, ParameterBinding> outputParts;
  private final Map<String, ParameterBinding> faultParts;
  private final Map<String, String> inputMimeTypes;
  private final Map<String, String> outputMimeTypes;
  private final Map<String, String> faultMimeTypes;
  private boolean explicitInputSOAPBodyParts = false;
  private boolean explicitOutputSOAPBodyParts = false;
  private boolean explicitFaultSOAPBodyParts = false;
  private Boolean emptyInputBody;
  private Boolean emptyOutputBody;
  private Boolean emptyFaultBody;
  private final Map<String, EditableWSDLPart> inParts;
  private final Map<String, EditableWSDLPart> outParts;
  private final List<EditableWSDLBoundFault> wsdlBoundFaults;
  private EditableWSDLOperation operation;
  private String soapAction;
  private WSDLBoundOperation.ANONYMOUS anonymous;
  private final EditableWSDLBoundPortType owner;
  private SOAPBinding.Style style = SOAPBinding.Style.DOCUMENT;
  private String reqNamespace;
  private String respNamespace;
  private QName requestPayloadName;
  private QName responsePayloadName;
  private boolean emptyRequestPayload;
  private boolean emptyResponsePayload;
  private Map<QName, ? extends EditableWSDLMessage> messages;
  
  public WSDLBoundOperationImpl(XMLStreamReader paramXMLStreamReader, EditableWSDLBoundPortType paramEditableWSDLBoundPortType, QName paramQName)
  {
    super(paramXMLStreamReader);
    name = paramQName;
    inputParts = new HashMap();
    outputParts = new HashMap();
    faultParts = new HashMap();
    inputMimeTypes = new HashMap();
    outputMimeTypes = new HashMap();
    faultMimeTypes = new HashMap();
    inParts = new HashMap();
    outParts = new HashMap();
    wsdlBoundFaults = new ArrayList();
    owner = paramEditableWSDLBoundPortType;
  }
  
  public QName getName()
  {
    return name;
  }
  
  public String getSOAPAction()
  {
    return soapAction;
  }
  
  public void setSoapAction(String paramString)
  {
    soapAction = (paramString != null ? paramString : "");
  }
  
  public EditableWSDLPart getPart(String paramString, WebParam.Mode paramMode)
  {
    if (paramMode == WebParam.Mode.IN) {
      return (EditableWSDLPart)inParts.get(paramString);
    }
    if (paramMode == WebParam.Mode.OUT) {
      return (EditableWSDLPart)outParts.get(paramString);
    }
    return null;
  }
  
  public void addPart(EditableWSDLPart paramEditableWSDLPart, WebParam.Mode paramMode)
  {
    if (paramMode == WebParam.Mode.IN) {
      inParts.put(paramEditableWSDLPart.getName(), paramEditableWSDLPart);
    } else if (paramMode == WebParam.Mode.OUT) {
      outParts.put(paramEditableWSDLPart.getName(), paramEditableWSDLPart);
    }
  }
  
  public Map<String, ParameterBinding> getInputParts()
  {
    return inputParts;
  }
  
  public Map<String, ParameterBinding> getOutputParts()
  {
    return outputParts;
  }
  
  public Map<String, ParameterBinding> getFaultParts()
  {
    return faultParts;
  }
  
  public Map<String, ? extends EditableWSDLPart> getInParts()
  {
    return Collections.unmodifiableMap(inParts);
  }
  
  public Map<String, ? extends EditableWSDLPart> getOutParts()
  {
    return Collections.unmodifiableMap(outParts);
  }
  
  @NotNull
  public List<? extends EditableWSDLBoundFault> getFaults()
  {
    return wsdlBoundFaults;
  }
  
  public void addFault(@NotNull EditableWSDLBoundFault paramEditableWSDLBoundFault)
  {
    wsdlBoundFaults.add(paramEditableWSDLBoundFault);
  }
  
  public ParameterBinding getInputBinding(String paramString)
  {
    if (emptyInputBody == null) {
      if (inputParts.get(" ") != null) {
        emptyInputBody = Boolean.valueOf(true);
      } else {
        emptyInputBody = Boolean.valueOf(false);
      }
    }
    ParameterBinding localParameterBinding = (ParameterBinding)inputParts.get(paramString);
    if (localParameterBinding == null)
    {
      if ((explicitInputSOAPBodyParts) || (emptyInputBody.booleanValue())) {
        return ParameterBinding.UNBOUND;
      }
      return ParameterBinding.BODY;
    }
    return localParameterBinding;
  }
  
  public ParameterBinding getOutputBinding(String paramString)
  {
    if (emptyOutputBody == null) {
      if (outputParts.get(" ") != null) {
        emptyOutputBody = Boolean.valueOf(true);
      } else {
        emptyOutputBody = Boolean.valueOf(false);
      }
    }
    ParameterBinding localParameterBinding = (ParameterBinding)outputParts.get(paramString);
    if (localParameterBinding == null)
    {
      if ((explicitOutputSOAPBodyParts) || (emptyOutputBody.booleanValue())) {
        return ParameterBinding.UNBOUND;
      }
      return ParameterBinding.BODY;
    }
    return localParameterBinding;
  }
  
  public ParameterBinding getFaultBinding(String paramString)
  {
    if (emptyFaultBody == null) {
      if (faultParts.get(" ") != null) {
        emptyFaultBody = Boolean.valueOf(true);
      } else {
        emptyFaultBody = Boolean.valueOf(false);
      }
    }
    ParameterBinding localParameterBinding = (ParameterBinding)faultParts.get(paramString);
    if (localParameterBinding == null)
    {
      if ((explicitFaultSOAPBodyParts) || (emptyFaultBody.booleanValue())) {
        return ParameterBinding.UNBOUND;
      }
      return ParameterBinding.BODY;
    }
    return localParameterBinding;
  }
  
  public String getMimeTypeForInputPart(String paramString)
  {
    return (String)inputMimeTypes.get(paramString);
  }
  
  public String getMimeTypeForOutputPart(String paramString)
  {
    return (String)outputMimeTypes.get(paramString);
  }
  
  public String getMimeTypeForFaultPart(String paramString)
  {
    return (String)faultMimeTypes.get(paramString);
  }
  
  public EditableWSDLOperation getOperation()
  {
    return operation;
  }
  
  public EditableWSDLBoundPortType getBoundPortType()
  {
    return owner;
  }
  
  public void setInputExplicitBodyParts(boolean paramBoolean)
  {
    explicitInputSOAPBodyParts = paramBoolean;
  }
  
  public void setOutputExplicitBodyParts(boolean paramBoolean)
  {
    explicitOutputSOAPBodyParts = paramBoolean;
  }
  
  public void setFaultExplicitBodyParts(boolean paramBoolean)
  {
    explicitFaultSOAPBodyParts = paramBoolean;
  }
  
  public void setStyle(SOAPBinding.Style paramStyle)
  {
    style = paramStyle;
  }
  
  @Nullable
  public QName getRequestPayloadName()
  {
    if (emptyRequestPayload) {
      return null;
    }
    if (requestPayloadName != null) {
      return requestPayloadName;
    }
    if (style.equals(SOAPBinding.Style.RPC))
    {
      localObject = getRequestNamespace() != null ? getRequestNamespace() : name.getNamespaceURI();
      requestPayloadName = new QName((String)localObject, name.getLocalPart());
      return requestPayloadName;
    }
    Object localObject = operation.getInput().getMessage().getName();
    EditableWSDLMessage localEditableWSDLMessage = (EditableWSDLMessage)messages.get(localObject);
    Iterator localIterator = localEditableWSDLMessage.parts().iterator();
    while (localIterator.hasNext())
    {
      EditableWSDLPart localEditableWSDLPart = (EditableWSDLPart)localIterator.next();
      ParameterBinding localParameterBinding = getInputBinding(localEditableWSDLPart.getName());
      if (localParameterBinding.isBody())
      {
        requestPayloadName = localEditableWSDLPart.getDescriptor().name();
        return requestPayloadName;
      }
    }
    emptyRequestPayload = true;
    return null;
  }
  
  @Nullable
  public QName getResponsePayloadName()
  {
    if (emptyResponsePayload) {
      return null;
    }
    if (responsePayloadName != null) {
      return responsePayloadName;
    }
    if (style.equals(SOAPBinding.Style.RPC))
    {
      localObject = getResponseNamespace() != null ? getResponseNamespace() : name.getNamespaceURI();
      responsePayloadName = new QName((String)localObject, name.getLocalPart() + "Response");
      return responsePayloadName;
    }
    Object localObject = operation.getOutput().getMessage().getName();
    EditableWSDLMessage localEditableWSDLMessage = (EditableWSDLMessage)messages.get(localObject);
    Iterator localIterator = localEditableWSDLMessage.parts().iterator();
    while (localIterator.hasNext())
    {
      EditableWSDLPart localEditableWSDLPart = (EditableWSDLPart)localIterator.next();
      ParameterBinding localParameterBinding = getOutputBinding(localEditableWSDLPart.getName());
      if (localParameterBinding.isBody())
      {
        responsePayloadName = localEditableWSDLPart.getDescriptor().name();
        return responsePayloadName;
      }
    }
    emptyResponsePayload = true;
    return null;
  }
  
  public String getRequestNamespace()
  {
    return reqNamespace != null ? reqNamespace : name.getNamespaceURI();
  }
  
  public void setRequestNamespace(String paramString)
  {
    reqNamespace = paramString;
  }
  
  public String getResponseNamespace()
  {
    return respNamespace != null ? respNamespace : name.getNamespaceURI();
  }
  
  public void setResponseNamespace(String paramString)
  {
    respNamespace = paramString;
  }
  
  EditableWSDLBoundPortType getOwner()
  {
    return owner;
  }
  
  public void freeze(EditableWSDLModel paramEditableWSDLModel)
  {
    messages = paramEditableWSDLModel.getMessages();
    operation = owner.getPortType().get(name.getLocalPart());
    Iterator localIterator = wsdlBoundFaults.iterator();
    while (localIterator.hasNext())
    {
      EditableWSDLBoundFault localEditableWSDLBoundFault = (EditableWSDLBoundFault)localIterator.next();
      localEditableWSDLBoundFault.freeze(this);
    }
  }
  
  public void setAnonymous(WSDLBoundOperation.ANONYMOUS paramANONYMOUS)
  {
    anonymous = paramANONYMOUS;
  }
  
  public WSDLBoundOperation.ANONYMOUS getAnonymous()
  {
    return anonymous;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\model\wsdl\WSDLBoundOperationImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */