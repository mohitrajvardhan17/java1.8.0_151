package com.sun.xml.internal.ws.assembler;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.EndpointAddress;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.WSService;
import com.sun.xml.internal.ws.api.client.WSPortInfo;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.pipe.ClientTubeAssemblerContext;
import com.sun.xml.internal.ws.api.pipe.Codec;
import com.sun.xml.internal.ws.api.server.Container;
import com.sun.xml.internal.ws.assembler.dev.ClientTubelineAssemblyContext;
import com.sun.xml.internal.ws.policy.PolicyMap;

class DefaultClientTubelineAssemblyContext
  extends TubelineAssemblyContextImpl
  implements ClientTubelineAssemblyContext
{
  @NotNull
  private final ClientTubeAssemblerContext wrappedContext;
  private final PolicyMap policyMap;
  private final WSPortInfo portInfo;
  private final WSDLPort wsdlPort;
  
  public DefaultClientTubelineAssemblyContext(@NotNull ClientTubeAssemblerContext paramClientTubeAssemblerContext)
  {
    wrappedContext = paramClientTubeAssemblerContext;
    wsdlPort = paramClientTubeAssemblerContext.getWsdlModel();
    portInfo = paramClientTubeAssemblerContext.getPortInfo();
    policyMap = paramClientTubeAssemblerContext.getPortInfo().getPolicyMap();
  }
  
  public PolicyMap getPolicyMap()
  {
    return policyMap;
  }
  
  public boolean isPolicyAvailable()
  {
    return (policyMap != null) && (!policyMap.isEmpty());
  }
  
  public WSDLPort getWsdlPort()
  {
    return wsdlPort;
  }
  
  public WSPortInfo getPortInfo()
  {
    return portInfo;
  }
  
  @NotNull
  public EndpointAddress getAddress()
  {
    return wrappedContext.getAddress();
  }
  
  @NotNull
  public WSService getService()
  {
    return wrappedContext.getService();
  }
  
  @NotNull
  public WSBinding getBinding()
  {
    return wrappedContext.getBinding();
  }
  
  @Nullable
  public SEIModel getSEIModel()
  {
    return wrappedContext.getSEIModel();
  }
  
  public Container getContainer()
  {
    return wrappedContext.getContainer();
  }
  
  @NotNull
  public Codec getCodec()
  {
    return wrappedContext.getCodec();
  }
  
  public void setCodec(@NotNull Codec paramCodec)
  {
    wrappedContext.setCodec(paramCodec);
  }
  
  public ClientTubeAssemblerContext getWrappedContext()
  {
    return wrappedContext;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\assembler\DefaultClientTubelineAssemblyContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */