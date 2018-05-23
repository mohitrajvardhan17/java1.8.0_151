package com.sun.xml.internal.ws.wsdl;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.WSDLOperationMapping;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.fault.SOAPFaultBuilder;
import com.sun.xml.internal.ws.resources.ServerMessages;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.xml.namespace.QName;

public class OperationDispatcher
{
  private List<WSDLOperationFinder> opFinders;
  private WSBinding binding;
  
  public OperationDispatcher(@NotNull WSDLPort paramWSDLPort, @NotNull WSBinding paramWSBinding, @Nullable SEIModel paramSEIModel)
  {
    binding = paramWSBinding;
    opFinders = new ArrayList();
    if (paramWSBinding.getAddressingVersion() != null) {
      opFinders.add(new ActionBasedOperationFinder(paramWSDLPort, paramWSBinding, paramSEIModel));
    }
    opFinders.add(new PayloadQNameBasedOperationFinder(paramWSDLPort, paramWSBinding, paramSEIModel));
    opFinders.add(new SOAPActionBasedOperationFinder(paramWSDLPort, paramWSBinding, paramSEIModel));
  }
  
  /**
   * @deprecated
   */
  @NotNull
  public QName getWSDLOperationQName(Packet paramPacket)
    throws DispatchException
  {
    WSDLOperationMapping localWSDLOperationMapping = getWSDLOperationMapping(paramPacket);
    return localWSDLOperationMapping != null ? localWSDLOperationMapping.getOperationName() : null;
  }
  
  @NotNull
  public WSDLOperationMapping getWSDLOperationMapping(Packet paramPacket)
    throws DispatchException
  {
    Object localObject1 = opFinders.iterator();
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (WSDLOperationFinder)((Iterator)localObject1).next();
      WSDLOperationMapping localWSDLOperationMapping = ((WSDLOperationFinder)localObject2).getWSDLOperationMapping(paramPacket);
      if (localWSDLOperationMapping != null) {
        return localWSDLOperationMapping;
      }
    }
    localObject1 = MessageFormat.format("Request=[SOAPAction={0},Payload='{'{1}'}'{2}]", new Object[] { soapAction, paramPacket.getMessage().getPayloadNamespaceURI(), paramPacket.getMessage().getPayloadLocalPart() });
    Object localObject2 = ServerMessages.DISPATCH_CANNOT_FIND_METHOD(localObject1);
    Message localMessage = SOAPFaultBuilder.createSOAPFaultMessage(binding.getSOAPVersion(), (String)localObject2, binding.getSOAPVersion().faultCodeClient);
    throw new DispatchException(localMessage);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\wsdl\OperationDispatcher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */