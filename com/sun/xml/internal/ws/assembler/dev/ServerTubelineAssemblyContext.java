package com.sun.xml.internal.ws.assembler.dev;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.pipe.Codec;
import com.sun.xml.internal.ws.api.pipe.ServerTubeAssemblerContext;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.policy.PolicyMap;

public abstract interface ServerTubelineAssemblyContext
  extends TubelineAssemblyContext
{
  @NotNull
  public abstract Codec getCodec();
  
  @NotNull
  public abstract WSEndpoint getEndpoint();
  
  public abstract PolicyMap getPolicyMap();
  
  @Nullable
  public abstract SEIModel getSEIModel();
  
  @NotNull
  public abstract Tube getTerminalTube();
  
  public abstract ServerTubeAssemblerContext getWrappedContext();
  
  @Nullable
  public abstract WSDLPort getWsdlPort();
  
  public abstract boolean isPolicyAvailable();
  
  public abstract boolean isSynchronous();
  
  public abstract void setCodec(@NotNull Codec paramCodec);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\assembler\dev\ServerTubelineAssemblyContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */