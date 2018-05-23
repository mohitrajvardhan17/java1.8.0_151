package javax.xml.ws.handler;

import javax.xml.namespace.QName;

public abstract interface PortInfo
{
  public abstract QName getServiceName();
  
  public abstract QName getPortName();
  
  public abstract String getBindingID();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\javax\xml\ws\handler\PortInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */