package com.sun.xml.internal.ws.assembler;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.pipe.Codec;
import com.sun.xml.internal.ws.api.pipe.ServerTubeAssemblerContext;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.assembler.dev.ServerTubelineAssemblyContext;
import com.sun.xml.internal.ws.policy.PolicyMap;

class DefaultServerTubelineAssemblyContext
  extends TubelineAssemblyContextImpl
  implements ServerTubelineAssemblyContext
{
  @NotNull
  private final ServerTubeAssemblerContext wrappedContext;
  private final PolicyMap policyMap;
  
  public DefaultServerTubelineAssemblyContext(@NotNull ServerTubeAssemblerContext paramServerTubeAssemblerContext)
  {
    wrappedContext = paramServerTubeAssemblerContext;
    policyMap = paramServerTubeAssemblerContext.getEndpoint().getPolicyMap();
  }
  
  public PolicyMap getPolicyMap()
  {
    return policyMap;
  }
  
  public boolean isPolicyAvailable()
  {
    return (policyMap != null) && (!policyMap.isEmpty());
  }
  
  @Nullable
  public SEIModel getSEIModel()
  {
    return wrappedContext.getSEIModel();
  }
  
  @Nullable
  public WSDLPort getWsdlPort()
  {
    return wrappedContext.getWsdlModel();
  }
  
  @NotNull
  public WSEndpoint getEndpoint()
  {
    return wrappedContext.getEndpoint();
  }
  
  @NotNull
  public Tube getTerminalTube()
  {
    return wrappedContext.getTerminalTube();
  }
  
  public boolean isSynchronous()
  {
    return wrappedContext.isSynchronous();
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
  
  public ServerTubeAssemblerContext getWrappedContext()
  {
    return wrappedContext;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\assembler\DefaultServerTubelineAssemblyContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */