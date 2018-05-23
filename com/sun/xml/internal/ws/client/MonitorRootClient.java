package com.sun.xml.internal.ws.client;

import com.sun.org.glassfish.gmbal.AMXMetadata;
import com.sun.org.glassfish.gmbal.Description;
import com.sun.org.glassfish.gmbal.ManagedAttribute;
import com.sun.org.glassfish.gmbal.ManagedObject;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLService;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.server.MonitorBase;
import java.net.URL;
import java.util.Map;
import javax.xml.namespace.QName;

@ManagedObject
@Description("Metro Web Service client")
@AMXMetadata(type="WSClient")
public final class MonitorRootClient
  extends MonitorBase
{
  private final Stub stub;
  
  MonitorRootClient(Stub paramStub)
  {
    stub = paramStub;
  }
  
  @ManagedAttribute
  private Container getContainer()
  {
    return stub.owner.getContainer();
  }
  
  @ManagedAttribute
  private Map<QName, PortInfo> qnameToPortInfoMap()
  {
    return stub.owner.getQNameToPortInfoMap();
  }
  
  @ManagedAttribute
  private QName serviceName()
  {
    return stub.owner.getServiceName();
  }
  
  @ManagedAttribute
  private Class serviceClass()
  {
    return stub.owner.getServiceClass();
  }
  
  @ManagedAttribute
  private URL wsdlDocumentLocation()
  {
    return stub.owner.getWSDLDocumentLocation();
  }
  
  @ManagedAttribute
  private WSDLService wsdlService()
  {
    return stub.owner.getWsdlService();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\client\MonitorRootClient.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */