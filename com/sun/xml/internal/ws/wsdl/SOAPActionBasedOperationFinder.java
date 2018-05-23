package com.sun.xml.internal.ws.wsdl;

import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.WSDLOperationMapping;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundPortType;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.model.AbstractSEIModelImpl;
import com.sun.xml.internal.ws.model.JavaMethodImpl;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

final class SOAPActionBasedOperationFinder
  extends WSDLOperationFinder
{
  private final Map<String, WSDLOperationMapping> methodHandlers = new HashMap();
  
  public SOAPActionBasedOperationFinder(WSDLPort paramWSDLPort, WSBinding paramWSBinding, @Nullable SEIModel paramSEIModel)
  {
    super(paramWSDLPort, paramWSBinding, paramSEIModel);
    HashMap localHashMap = new HashMap();
    Iterator localIterator;
    Object localObject;
    if (paramSEIModel != null)
    {
      localIterator = ((AbstractSEIModelImpl)paramSEIModel).getJavaMethods().iterator();
      String str;
      while (localIterator.hasNext())
      {
        localObject = (JavaMethodImpl)localIterator.next();
        str = ((JavaMethodImpl)localObject).getSOAPAction();
        Integer localInteger = (Integer)localHashMap.get(str);
        if (localInteger == null) {
          localHashMap.put(str, Integer.valueOf(1));
        } else {
          localHashMap.put(str, localInteger = Integer.valueOf(localInteger.intValue() + 1));
        }
      }
      localIterator = ((AbstractSEIModelImpl)paramSEIModel).getJavaMethods().iterator();
      while (localIterator.hasNext())
      {
        localObject = (JavaMethodImpl)localIterator.next();
        str = ((JavaMethodImpl)localObject).getSOAPAction();
        if (((Integer)localHashMap.get(str)).intValue() == 1) {
          methodHandlers.put('"' + str + '"', wsdlOperationMapping((JavaMethodImpl)localObject));
        }
      }
    }
    else
    {
      localIterator = paramWSDLPort.getBinding().getBindingOperations().iterator();
      while (localIterator.hasNext())
      {
        localObject = (WSDLBoundOperation)localIterator.next();
        methodHandlers.put(((WSDLBoundOperation)localObject).getSOAPAction(), wsdlOperationMapping((WSDLBoundOperation)localObject));
      }
    }
  }
  
  public WSDLOperationMapping getWSDLOperationMapping(Packet paramPacket)
    throws DispatchException
  {
    return soapAction == null ? null : (WSDLOperationMapping)methodHandlers.get(soapAction);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\wsdl\SOAPActionBasedOperationFinder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */