package com.sun.xml.internal.ws.protocol.soap;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.NextAction;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;
import com.sun.xml.internal.ws.binding.SOAPBindingImpl;
import com.sun.xml.internal.ws.client.HandlerConfiguration;
import java.util.Set;

public class ClientMUTube
  extends MUTube
{
  public ClientMUTube(WSBinding paramWSBinding, Tube paramTube)
  {
    super(paramWSBinding, paramTube);
  }
  
  protected ClientMUTube(ClientMUTube paramClientMUTube, TubeCloner paramTubeCloner)
  {
    super(paramClientMUTube, paramTubeCloner);
  }
  
  @NotNull
  public NextAction processResponse(Packet paramPacket)
  {
    if (paramPacket.getMessage() == null) {
      return super.processResponse(paramPacket);
    }
    HandlerConfiguration localHandlerConfiguration = handlerConfig;
    if (localHandlerConfiguration == null) {
      localHandlerConfiguration = binding.getHandlerConfig();
    }
    Set localSet = getMisUnderstoodHeaders(paramPacket.getMessage().getHeaders(), localHandlerConfiguration.getRoles(), binding.getKnownHeaders());
    if ((localSet == null) || (localSet.isEmpty())) {
      return super.processResponse(paramPacket);
    }
    throw createMUSOAPFaultException(localSet);
  }
  
  public ClientMUTube copy(TubeCloner paramTubeCloner)
  {
    return new ClientMUTube(this, paramTubeCloner);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\protocol\soap\ClientMUTube.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */