package com.sun.xml.internal.ws.model.wsdl;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundPortType;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLService;
import javax.xml.namespace.QName;

public final class WSDLPortProperties
  extends WSDLProperties
{
  @NotNull
  private final WSDLPort port;
  
  public WSDLPortProperties(@NotNull WSDLPort paramWSDLPort)
  {
    this(paramWSDLPort, null);
  }
  
  public WSDLPortProperties(@NotNull WSDLPort paramWSDLPort, @Nullable SEIModel paramSEIModel)
  {
    super(paramSEIModel);
    port = paramWSDLPort;
  }
  
  public QName getWSDLService()
  {
    return port.getOwner().getName();
  }
  
  public QName getWSDLPort()
  {
    return port.getName();
  }
  
  public QName getWSDLPortType()
  {
    return port.getBinding().getPortTypeName();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\model\wsdl\WSDLPortProperties.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */