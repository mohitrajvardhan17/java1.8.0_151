package com.sun.xml.internal.ws.wsdl;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.JavaMethod;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.WSDLOperationMapping;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.model.JavaMethodImpl;
import javax.xml.namespace.QName;

public abstract class WSDLOperationFinder
{
  protected final WSDLPort wsdlModel;
  protected final WSBinding binding;
  protected final SEIModel seiModel;
  
  public WSDLOperationFinder(@NotNull WSDLPort paramWSDLPort, @NotNull WSBinding paramWSBinding, @Nullable SEIModel paramSEIModel)
  {
    wsdlModel = paramWSDLPort;
    binding = paramWSBinding;
    seiModel = paramSEIModel;
  }
  
  /**
   * @deprecated
   */
  public QName getWSDLOperationQName(Packet paramPacket)
    throws DispatchException
  {
    WSDLOperationMapping localWSDLOperationMapping = getWSDLOperationMapping(paramPacket);
    return localWSDLOperationMapping != null ? localWSDLOperationMapping.getOperationName() : null;
  }
  
  public WSDLOperationMapping getWSDLOperationMapping(Packet paramPacket)
    throws DispatchException
  {
    return null;
  }
  
  protected WSDLOperationMapping wsdlOperationMapping(JavaMethodImpl paramJavaMethodImpl)
  {
    return new WSDLOperationMappingImpl(paramJavaMethodImpl.getOperation(), paramJavaMethodImpl);
  }
  
  protected WSDLOperationMapping wsdlOperationMapping(WSDLBoundOperation paramWSDLBoundOperation)
  {
    return new WSDLOperationMappingImpl(paramWSDLBoundOperation, null);
  }
  
  static class WSDLOperationMappingImpl
    implements WSDLOperationMapping
  {
    private WSDLBoundOperation wsdlOperation;
    private JavaMethod javaMethod;
    private QName operationName;
    
    WSDLOperationMappingImpl(WSDLBoundOperation paramWSDLBoundOperation, JavaMethodImpl paramJavaMethodImpl)
    {
      wsdlOperation = paramWSDLBoundOperation;
      javaMethod = paramJavaMethodImpl;
      operationName = (paramJavaMethodImpl != null ? paramJavaMethodImpl.getOperationQName() : paramWSDLBoundOperation.getName());
    }
    
    public WSDLBoundOperation getWSDLBoundOperation()
    {
      return wsdlOperation;
    }
    
    public JavaMethod getJavaMethod()
    {
      return javaMethod;
    }
    
    public QName getOperationName()
    {
      return operationName;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\wsdl\WSDLOperationFinder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */