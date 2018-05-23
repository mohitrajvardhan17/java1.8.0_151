package com.sun.xml.internal.ws.assembler.jaxws;

import com.sun.xml.internal.ws.api.pipe.ClientTubeAssemblerContext;
import com.sun.xml.internal.ws.api.pipe.ServerTubeAssemblerContext;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.assembler.dev.ClientTubelineAssemblyContext;
import com.sun.xml.internal.ws.assembler.dev.ServerTubelineAssemblyContext;
import com.sun.xml.internal.ws.assembler.dev.TubeFactory;
import javax.xml.ws.WebServiceException;

public final class MustUnderstandTubeFactory
  implements TubeFactory
{
  public MustUnderstandTubeFactory() {}
  
  public Tube createTube(ClientTubelineAssemblyContext paramClientTubelineAssemblyContext)
    throws WebServiceException
  {
    return paramClientTubelineAssemblyContext.getWrappedContext().createClientMUTube(paramClientTubelineAssemblyContext.getTubelineHead());
  }
  
  public Tube createTube(ServerTubelineAssemblyContext paramServerTubelineAssemblyContext)
    throws WebServiceException
  {
    return paramServerTubelineAssemblyContext.getWrappedContext().createServerMUTube(paramServerTubelineAssemblyContext.getTubelineHead());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\assembler\jaxws\MustUnderstandTubeFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */