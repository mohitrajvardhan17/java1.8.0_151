package com.sun.xml.internal.ws.protocol.soap;

import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.NextAction;
import com.sun.xml.internal.ws.api.pipe.ServerTubeAssemblerContext;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.binding.SOAPBindingImpl;
import com.sun.xml.internal.ws.client.HandlerConfiguration;
import java.util.Set;
import javax.xml.namespace.QName;

public class ServerMUTube
  extends MUTube
{
  private ServerTubeAssemblerContext tubeContext;
  private final Set<String> roles;
  private final Set<QName> handlerKnownHeaders;
  
  public ServerMUTube(ServerTubeAssemblerContext paramServerTubeAssemblerContext, Tube paramTube)
  {
    super(paramServerTubeAssemblerContext.getEndpoint().getBinding(), paramTube);
    tubeContext = paramServerTubeAssemblerContext;
    HandlerConfiguration localHandlerConfiguration = binding.getHandlerConfig();
    roles = localHandlerConfiguration.getRoles();
    handlerKnownHeaders = binding.getKnownHeaders();
  }
  
  protected ServerMUTube(ServerMUTube paramServerMUTube, TubeCloner paramTubeCloner)
  {
    super(paramServerMUTube, paramTubeCloner);
    tubeContext = tubeContext;
    roles = roles;
    handlerKnownHeaders = handlerKnownHeaders;
  }
  
  public NextAction processRequest(Packet paramPacket)
  {
    Set localSet = getMisUnderstoodHeaders(paramPacket.getMessage().getHeaders(), roles, handlerKnownHeaders);
    if ((localSet == null) || (localSet.isEmpty())) {
      return doInvoke(next, paramPacket);
    }
    return doReturnWith(paramPacket.createServerResponse(createMUSOAPFaultMessage(localSet), tubeContext.getWsdlModel(), tubeContext.getSEIModel(), tubeContext.getEndpoint().getBinding()));
  }
  
  public ServerMUTube copy(TubeCloner paramTubeCloner)
  {
    return new ServerMUTube(this, paramTubeCloner);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\protocol\soap\ServerMUTube.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */