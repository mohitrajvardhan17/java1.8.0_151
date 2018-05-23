package com.sun.xml.internal.ws.addressing.v200408;

import com.sun.xml.internal.ws.addressing.WsaClientTube;
import com.sun.xml.internal.ws.addressing.model.MissingAddressingHeaderException;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.message.AddressingUtils;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;
import com.sun.xml.internal.ws.developer.MemberSubmissionAddressing.Validation;
import com.sun.xml.internal.ws.developer.MemberSubmissionAddressingFeature;

public class MemberSubmissionWsaClientTube
  extends WsaClientTube
{
  private final MemberSubmissionAddressing.Validation validation;
  
  public MemberSubmissionWsaClientTube(WSDLPort paramWSDLPort, WSBinding paramWSBinding, Tube paramTube)
  {
    super(paramWSDLPort, paramWSBinding, paramTube);
    validation = ((MemberSubmissionAddressingFeature)paramWSBinding.getFeature(MemberSubmissionAddressingFeature.class)).getValidation();
  }
  
  public MemberSubmissionWsaClientTube(MemberSubmissionWsaClientTube paramMemberSubmissionWsaClientTube, TubeCloner paramTubeCloner)
  {
    super(paramMemberSubmissionWsaClientTube, paramTubeCloner);
    validation = validation;
  }
  
  public MemberSubmissionWsaClientTube copy(TubeCloner paramTubeCloner)
  {
    return new MemberSubmissionWsaClientTube(this, paramTubeCloner);
  }
  
  protected void checkMandatoryHeaders(Packet paramPacket, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, boolean paramBoolean5, boolean paramBoolean6)
  {
    super.checkMandatoryHeaders(paramPacket, paramBoolean1, paramBoolean2, paramBoolean3, paramBoolean4, paramBoolean5, paramBoolean6);
    if (!paramBoolean2) {
      throw new MissingAddressingHeaderException(addressingVersion.toTag, paramPacket);
    }
    if ((!validation.equals(MemberSubmissionAddressing.Validation.LAX)) && (expectReply) && (paramPacket.getMessage() != null) && (!paramBoolean6))
    {
      String str = AddressingUtils.getAction(paramPacket.getMessage().getHeaders(), addressingVersion, soapVersion);
      if ((!paramPacket.getMessage().isFault()) || (!str.equals(addressingVersion.getDefaultFaultAction()))) {
        throw new MissingAddressingHeaderException(addressingVersion.relatesToTag, paramPacket);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\addressing\v200408\MemberSubmissionWsaClientTube.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */