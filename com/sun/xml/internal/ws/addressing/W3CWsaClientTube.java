package com.sun.xml.internal.ws.addressing;

import com.sun.xml.internal.ws.addressing.model.MissingAddressingHeaderException;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.message.AddressingUtils;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;

public class W3CWsaClientTube
  extends WsaClientTube
{
  public W3CWsaClientTube(WSDLPort paramWSDLPort, WSBinding paramWSBinding, Tube paramTube)
  {
    super(paramWSDLPort, paramWSBinding, paramTube);
  }
  
  public W3CWsaClientTube(WsaClientTube paramWsaClientTube, TubeCloner paramTubeCloner)
  {
    super(paramWsaClientTube, paramTubeCloner);
  }
  
  public W3CWsaClientTube copy(TubeCloner paramTubeCloner)
  {
    return new W3CWsaClientTube(this, paramTubeCloner);
  }
  
  protected void checkMandatoryHeaders(Packet paramPacket, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, boolean paramBoolean5, boolean paramBoolean6)
  {
    super.checkMandatoryHeaders(paramPacket, paramBoolean1, paramBoolean2, paramBoolean3, paramBoolean4, paramBoolean5, paramBoolean6);
    if ((expectReply) && (paramPacket.getMessage() != null) && (!paramBoolean6))
    {
      String str = AddressingUtils.getAction(paramPacket.getMessage().getHeaders(), addressingVersion, soapVersion);
      if ((!paramPacket.getMessage().isFault()) || (!str.equals(addressingVersion.getDefaultFaultAction()))) {
        throw new MissingAddressingHeaderException(addressingVersion.relatesToTag, paramPacket);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\addressing\W3CWsaClientTube.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */