package com.sun.xml.internal.ws.model.wsdl;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.model.ParameterBinding;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLInput;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLMessage;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLOperation;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLOutput;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPortType;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundOperation;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLBoundPortType;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLMessage;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLModel;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLPart;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLPort;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLPortType;
import com.sun.xml.internal.ws.api.model.wsdl.editable.EditableWSDLService;
import com.sun.xml.internal.ws.policy.PolicyMap;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.jws.WebParam.Mode;
import javax.xml.namespace.QName;

public final class WSDLModelImpl
  extends AbstractExtensibleImpl
  implements EditableWSDLModel
{
  private final Map<QName, EditableWSDLMessage> messages = new HashMap();
  private final Map<QName, EditableWSDLPortType> portTypes = new HashMap();
  private final Map<QName, EditableWSDLBoundPortType> bindings = new HashMap();
  private final Map<QName, EditableWSDLService> services = new LinkedHashMap();
  private PolicyMap policyMap;
  private final Map<QName, EditableWSDLBoundPortType> unmBindings = Collections.unmodifiableMap(bindings);
  
  public WSDLModelImpl(@NotNull String paramString)
  {
    super(paramString, -1);
  }
  
  public WSDLModelImpl()
  {
    super(null, -1);
  }
  
  public void addMessage(EditableWSDLMessage paramEditableWSDLMessage)
  {
    messages.put(paramEditableWSDLMessage.getName(), paramEditableWSDLMessage);
  }
  
  public EditableWSDLMessage getMessage(QName paramQName)
  {
    return (EditableWSDLMessage)messages.get(paramQName);
  }
  
  public void addPortType(EditableWSDLPortType paramEditableWSDLPortType)
  {
    portTypes.put(paramEditableWSDLPortType.getName(), paramEditableWSDLPortType);
  }
  
  public EditableWSDLPortType getPortType(QName paramQName)
  {
    return (EditableWSDLPortType)portTypes.get(paramQName);
  }
  
  public void addBinding(EditableWSDLBoundPortType paramEditableWSDLBoundPortType)
  {
    assert (!bindings.containsValue(paramEditableWSDLBoundPortType));
    bindings.put(paramEditableWSDLBoundPortType.getName(), paramEditableWSDLBoundPortType);
  }
  
  public EditableWSDLBoundPortType getBinding(QName paramQName)
  {
    return (EditableWSDLBoundPortType)bindings.get(paramQName);
  }
  
  public void addService(EditableWSDLService paramEditableWSDLService)
  {
    services.put(paramEditableWSDLService.getName(), paramEditableWSDLService);
  }
  
  public EditableWSDLService getService(QName paramQName)
  {
    return (EditableWSDLService)services.get(paramQName);
  }
  
  public Map<QName, EditableWSDLMessage> getMessages()
  {
    return messages;
  }
  
  @NotNull
  public Map<QName, EditableWSDLPortType> getPortTypes()
  {
    return portTypes;
  }
  
  @NotNull
  public Map<QName, ? extends EditableWSDLBoundPortType> getBindings()
  {
    return unmBindings;
  }
  
  @NotNull
  public Map<QName, EditableWSDLService> getServices()
  {
    return services;
  }
  
  public QName getFirstServiceName()
  {
    if (services.isEmpty()) {
      return null;
    }
    return ((EditableWSDLService)services.values().iterator().next()).getName();
  }
  
  public EditableWSDLBoundPortType getBinding(QName paramQName1, QName paramQName2)
  {
    EditableWSDLService localEditableWSDLService = (EditableWSDLService)services.get(paramQName1);
    if (localEditableWSDLService != null)
    {
      EditableWSDLPort localEditableWSDLPort = localEditableWSDLService.get(paramQName2);
      if (localEditableWSDLPort != null) {
        return localEditableWSDLPort.getBinding();
      }
    }
    return null;
  }
  
  public void finalizeRpcLitBinding(EditableWSDLBoundPortType paramEditableWSDLBoundPortType)
  {
    assert (paramEditableWSDLBoundPortType != null);
    QName localQName = paramEditableWSDLBoundPortType.getPortTypeName();
    if (localQName == null) {
      return;
    }
    WSDLPortType localWSDLPortType = (WSDLPortType)portTypes.get(localQName);
    if (localWSDLPortType == null) {
      return;
    }
    Iterator localIterator = paramEditableWSDLBoundPortType.getBindingOperations().iterator();
    while (localIterator.hasNext())
    {
      EditableWSDLBoundOperation localEditableWSDLBoundOperation = (EditableWSDLBoundOperation)localIterator.next();
      WSDLOperation localWSDLOperation = localWSDLPortType.get(localEditableWSDLBoundOperation.getName().getLocalPart());
      WSDLMessage localWSDLMessage = localWSDLOperation.getInput().getMessage();
      if (localWSDLMessage != null)
      {
        EditableWSDLMessage localEditableWSDLMessage = (EditableWSDLMessage)messages.get(localWSDLMessage.getName());
        int i = 0;
        Object localObject1;
        Object localObject2;
        Object localObject3;
        Object localObject4;
        if (localEditableWSDLMessage != null)
        {
          localObject1 = localEditableWSDLMessage.parts().iterator();
          while (((Iterator)localObject1).hasNext())
          {
            localObject2 = (EditableWSDLPart)((Iterator)localObject1).next();
            localObject3 = ((EditableWSDLPart)localObject2).getName();
            localObject4 = localEditableWSDLBoundOperation.getInputBinding((String)localObject3);
            if (((ParameterBinding)localObject4).isBody())
            {
              ((EditableWSDLPart)localObject2).setIndex(i++);
              ((EditableWSDLPart)localObject2).setBinding((ParameterBinding)localObject4);
              localEditableWSDLBoundOperation.addPart((EditableWSDLPart)localObject2, WebParam.Mode.IN);
            }
          }
        }
        i = 0;
        if (!localWSDLOperation.isOneWay())
        {
          localObject1 = localWSDLOperation.getOutput().getMessage();
          if (localObject1 != null)
          {
            localObject2 = (EditableWSDLMessage)messages.get(((WSDLMessage)localObject1).getName());
            if (localObject2 != null)
            {
              localObject3 = ((EditableWSDLMessage)localObject2).parts().iterator();
              while (((Iterator)localObject3).hasNext())
              {
                localObject4 = (EditableWSDLPart)((Iterator)localObject3).next();
                String str = ((EditableWSDLPart)localObject4).getName();
                ParameterBinding localParameterBinding = localEditableWSDLBoundOperation.getOutputBinding(str);
                if (localParameterBinding.isBody())
                {
                  ((EditableWSDLPart)localObject4).setIndex(i++);
                  ((EditableWSDLPart)localObject4).setBinding(localParameterBinding);
                  localEditableWSDLBoundOperation.addPart((EditableWSDLPart)localObject4, WebParam.Mode.OUT);
                }
              }
            }
          }
        }
      }
    }
  }
  
  public PolicyMap getPolicyMap()
  {
    return policyMap;
  }
  
  public void setPolicyMap(PolicyMap paramPolicyMap)
  {
    policyMap = paramPolicyMap;
  }
  
  public void freeze()
  {
    Iterator localIterator = services.values().iterator();
    Object localObject;
    while (localIterator.hasNext())
    {
      localObject = (EditableWSDLService)localIterator.next();
      ((EditableWSDLService)localObject).freeze(this);
    }
    localIterator = bindings.values().iterator();
    while (localIterator.hasNext())
    {
      localObject = (EditableWSDLBoundPortType)localIterator.next();
      ((EditableWSDLBoundPortType)localObject).freeze();
    }
    localIterator = portTypes.values().iterator();
    while (localIterator.hasNext())
    {
      localObject = (EditableWSDLPortType)localIterator.next();
      ((EditableWSDLPortType)localObject).freeze();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\model\wsdl\WSDLModelImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */