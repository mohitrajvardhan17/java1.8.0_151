package com.sun.xml.internal.ws.model.wsdl;

import com.sun.xml.internal.ws.api.model.SEIModel;
import javax.xml.namespace.QName;

public final class WSDLDirectProperties
  extends WSDLProperties
{
  private final QName serviceName;
  private final QName portName;
  
  public WSDLDirectProperties(QName paramQName1, QName paramQName2)
  {
    this(paramQName1, paramQName2, null);
  }
  
  public WSDLDirectProperties(QName paramQName1, QName paramQName2, SEIModel paramSEIModel)
  {
    super(paramSEIModel);
    serviceName = paramQName1;
    portName = paramQName2;
  }
  
  public QName getWSDLService()
  {
    return serviceName;
  }
  
  public QName getWSDLPort()
  {
    return portName;
  }
  
  public QName getWSDLPortType()
  {
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\model\wsdl\WSDLDirectProperties.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */