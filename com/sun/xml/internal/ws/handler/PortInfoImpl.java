package com.sun.xml.internal.ws.handler;

import com.sun.xml.internal.ws.api.BindingID;
import javax.xml.namespace.QName;
import javax.xml.ws.handler.PortInfo;

public class PortInfoImpl
  implements PortInfo
{
  private BindingID bindingId;
  private QName portName;
  private QName serviceName;
  
  public PortInfoImpl(BindingID paramBindingID, QName paramQName1, QName paramQName2)
  {
    if (paramBindingID == null) {
      throw new RuntimeException("bindingId cannot be null");
    }
    if (paramQName1 == null) {
      throw new RuntimeException("portName cannot be null");
    }
    if (paramQName2 == null) {
      throw new RuntimeException("serviceName cannot be null");
    }
    bindingId = paramBindingID;
    portName = paramQName1;
    serviceName = paramQName2;
  }
  
  public String getBindingID()
  {
    return bindingId.toString();
  }
  
  public QName getPortName()
  {
    return portName;
  }
  
  public QName getServiceName()
  {
    return serviceName;
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof PortInfo))
    {
      PortInfo localPortInfo = (PortInfo)paramObject;
      if ((bindingId.toString().equals(localPortInfo.getBindingID())) && (portName.equals(localPortInfo.getPortName())) && (serviceName.equals(localPortInfo.getServiceName()))) {
        return true;
      }
    }
    return false;
  }
  
  public int hashCode()
  {
    return bindingId.hashCode();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\handler\PortInfoImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */