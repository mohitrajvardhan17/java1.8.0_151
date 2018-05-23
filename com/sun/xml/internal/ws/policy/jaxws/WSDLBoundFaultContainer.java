package com.sun.xml.internal.ws.policy.jaxws;

import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundFault;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLObject;
import org.xml.sax.Locator;

class WSDLBoundFaultContainer
  implements WSDLObject
{
  private final WSDLBoundFault boundFault;
  private final WSDLBoundOperation boundOperation;
  
  public WSDLBoundFaultContainer(WSDLBoundFault paramWSDLBoundFault, WSDLBoundOperation paramWSDLBoundOperation)
  {
    boundFault = paramWSDLBoundFault;
    boundOperation = paramWSDLBoundOperation;
  }
  
  public Locator getLocation()
  {
    return null;
  }
  
  public WSDLBoundFault getBoundFault()
  {
    return boundFault;
  }
  
  public WSDLBoundOperation getBoundOperation()
  {
    return boundOperation;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\policy\jaxws\WSDLBoundFaultContainer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */