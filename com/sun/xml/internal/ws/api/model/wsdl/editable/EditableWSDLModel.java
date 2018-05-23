package com.sun.xml.internal.ws.api.model.wsdl.editable;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLModel;
import com.sun.xml.internal.ws.policy.PolicyMap;
import java.util.Map;
import javax.xml.namespace.QName;

public abstract interface EditableWSDLModel
  extends WSDLModel
{
  public abstract EditableWSDLPortType getPortType(@NotNull QName paramQName);
  
  public abstract void addBinding(EditableWSDLBoundPortType paramEditableWSDLBoundPortType);
  
  public abstract EditableWSDLBoundPortType getBinding(@NotNull QName paramQName);
  
  public abstract EditableWSDLBoundPortType getBinding(@NotNull QName paramQName1, @NotNull QName paramQName2);
  
  public abstract EditableWSDLService getService(@NotNull QName paramQName);
  
  @NotNull
  public abstract Map<QName, ? extends EditableWSDLMessage> getMessages();
  
  public abstract void addMessage(EditableWSDLMessage paramEditableWSDLMessage);
  
  @NotNull
  public abstract Map<QName, ? extends EditableWSDLPortType> getPortTypes();
  
  public abstract void addPortType(EditableWSDLPortType paramEditableWSDLPortType);
  
  @NotNull
  public abstract Map<QName, ? extends EditableWSDLBoundPortType> getBindings();
  
  @NotNull
  public abstract Map<QName, ? extends EditableWSDLService> getServices();
  
  public abstract void addService(EditableWSDLService paramEditableWSDLService);
  
  public abstract EditableWSDLMessage getMessage(QName paramQName);
  
  /**
   * @deprecated
   */
  public abstract void setPolicyMap(PolicyMap paramPolicyMap);
  
  public abstract void finalizeRpcLitBinding(EditableWSDLBoundPortType paramEditableWSDLBoundPortType);
  
  public abstract void freeze();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\model\wsdl\editable\EditableWSDLModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */