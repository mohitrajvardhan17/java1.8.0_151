package com.sun.xml.internal.ws.dump;

import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.assembler.dev.ClientTubelineAssemblyContext;
import com.sun.xml.internal.ws.assembler.dev.ServerTubelineAssemblyContext;
import com.sun.xml.internal.ws.assembler.dev.TubeFactory;
import javax.xml.ws.WebServiceException;

public final class MessageDumpingTubeFactory
  implements TubeFactory
{
  public MessageDumpingTubeFactory() {}
  
  public Tube createTube(ClientTubelineAssemblyContext paramClientTubelineAssemblyContext)
    throws WebServiceException
  {
    MessageDumpingFeature localMessageDumpingFeature = (MessageDumpingFeature)paramClientTubelineAssemblyContext.getBinding().getFeature(MessageDumpingFeature.class);
    if (localMessageDumpingFeature != null) {
      return new MessageDumpingTube(paramClientTubelineAssemblyContext.getTubelineHead(), localMessageDumpingFeature);
    }
    return paramClientTubelineAssemblyContext.getTubelineHead();
  }
  
  public Tube createTube(ServerTubelineAssemblyContext paramServerTubelineAssemblyContext)
    throws WebServiceException
  {
    MessageDumpingFeature localMessageDumpingFeature = (MessageDumpingFeature)paramServerTubelineAssemblyContext.getEndpoint().getBinding().getFeature(MessageDumpingFeature.class);
    if (localMessageDumpingFeature != null) {
      return new MessageDumpingTube(paramServerTubelineAssemblyContext.getTubelineHead(), localMessageDumpingFeature);
    }
    return paramServerTubelineAssemblyContext.getTubelineHead();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\dump\MessageDumpingTubeFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */