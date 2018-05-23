package com.sun.xml.internal.ws.wsdl;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.message.AddressingUtils;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.MessageHeaders;
import com.sun.xml.internal.ws.api.message.Messages;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.MEP;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.WSDLOperationMapping;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundPortType;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLInput;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLOperation;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.model.AbstractSEIModelImpl;
import com.sun.xml.internal.ws.model.JavaMethodImpl;
import com.sun.xml.internal.ws.resources.AddressingMessages;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;
import javax.xml.namespace.QName;

final class ActionBasedOperationFinder
  extends WSDLOperationFinder
{
  private static final Logger LOGGER = Logger.getLogger(ActionBasedOperationFinder.class.getName());
  private final Map<ActionBasedOperationSignature, WSDLOperationMapping> uniqueOpSignatureMap;
  private final Map<String, WSDLOperationMapping> actionMap;
  @NotNull
  private final AddressingVersion av;
  
  public ActionBasedOperationFinder(WSDLPort paramWSDLPort, WSBinding paramWSBinding, @Nullable SEIModel paramSEIModel)
  {
    super(paramWSDLPort, paramWSBinding, paramSEIModel);
    assert (paramWSBinding.getAddressingVersion() != null);
    av = paramWSBinding.getAddressingVersion();
    uniqueOpSignatureMap = new HashMap();
    actionMap = new HashMap();
    Iterator localIterator;
    Object localObject1;
    Object localObject2;
    Object localObject3;
    ActionBasedOperationSignature localActionBasedOperationSignature;
    if (paramSEIModel != null)
    {
      localIterator = ((AbstractSEIModelImpl)paramSEIModel).getJavaMethods().iterator();
      while (localIterator.hasNext())
      {
        localObject1 = (JavaMethodImpl)localIterator.next();
        if (!getMEPisAsync)
        {
          localObject2 = ((JavaMethodImpl)localObject1).getInputAction();
          localObject3 = ((JavaMethodImpl)localObject1).getRequestPayloadName();
          if (localObject3 == null) {
            localObject3 = PayloadQNameBasedOperationFinder.EMPTY_PAYLOAD;
          }
          if (((localObject2 == null) || (((String)localObject2).equals(""))) && (((JavaMethodImpl)localObject1).getOperation() != null)) {
            localObject2 = ((JavaMethodImpl)localObject1).getOperation().getOperation().getInput().getAction();
          }
          if (localObject2 != null)
          {
            localActionBasedOperationSignature = new ActionBasedOperationSignature((String)localObject2, (QName)localObject3);
            if (uniqueOpSignatureMap.get(localActionBasedOperationSignature) != null) {
              LOGGER.warning(AddressingMessages.NON_UNIQUE_OPERATION_SIGNATURE(uniqueOpSignatureMap.get(localActionBasedOperationSignature), ((JavaMethodImpl)localObject1).getOperationQName(), localObject2, localObject3));
            }
            uniqueOpSignatureMap.put(localActionBasedOperationSignature, wsdlOperationMapping((JavaMethodImpl)localObject1));
            actionMap.put(localObject2, wsdlOperationMapping((JavaMethodImpl)localObject1));
          }
        }
      }
    }
    else
    {
      localIterator = paramWSDLPort.getBinding().getBindingOperations().iterator();
      while (localIterator.hasNext())
      {
        localObject1 = (WSDLBoundOperation)localIterator.next();
        localObject2 = ((WSDLBoundOperation)localObject1).getRequestPayloadName();
        if (localObject2 == null) {
          localObject2 = PayloadQNameBasedOperationFinder.EMPTY_PAYLOAD;
        }
        localObject3 = ((WSDLBoundOperation)localObject1).getOperation().getInput().getAction();
        localActionBasedOperationSignature = new ActionBasedOperationSignature((String)localObject3, (QName)localObject2);
        if (uniqueOpSignatureMap.get(localActionBasedOperationSignature) != null) {
          LOGGER.warning(AddressingMessages.NON_UNIQUE_OPERATION_SIGNATURE(uniqueOpSignatureMap.get(localActionBasedOperationSignature), ((WSDLBoundOperation)localObject1).getName(), localObject3, localObject2));
        }
        uniqueOpSignatureMap.put(localActionBasedOperationSignature, wsdlOperationMapping((WSDLBoundOperation)localObject1));
        actionMap.put(localObject3, wsdlOperationMapping((WSDLBoundOperation)localObject1));
      }
    }
  }
  
  public WSDLOperationMapping getWSDLOperationMapping(Packet paramPacket)
    throws DispatchException
  {
    MessageHeaders localMessageHeaders = paramPacket.getMessage().getHeaders();
    String str1 = AddressingUtils.getAction(localMessageHeaders, av, binding.getSOAPVersion());
    if (str1 == null) {
      return null;
    }
    Message localMessage1 = paramPacket.getMessage();
    String str2 = localMessage1.getPayloadLocalPart();
    QName localQName;
    if (str2 == null)
    {
      localQName = PayloadQNameBasedOperationFinder.EMPTY_PAYLOAD;
    }
    else
    {
      localObject = localMessage1.getPayloadNamespaceURI();
      if (localObject == null) {
        localObject = "";
      }
      localQName = new QName((String)localObject, str2);
    }
    Object localObject = (WSDLOperationMapping)uniqueOpSignatureMap.get(new ActionBasedOperationSignature(str1, localQName));
    if (localObject != null) {
      return (WSDLOperationMapping)localObject;
    }
    localObject = (WSDLOperationMapping)actionMap.get(str1);
    if (localObject != null) {
      return (WSDLOperationMapping)localObject;
    }
    Message localMessage2 = Messages.create(str1, av, binding.getSOAPVersion());
    throw new DispatchException(localMessage2);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\wsdl\ActionBasedOperationFinder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */