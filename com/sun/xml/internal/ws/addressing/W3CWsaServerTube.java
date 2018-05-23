package com.sun.xml.internal.ws.addressing;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.addressing.model.InvalidAddressingHeaderException;
import com.sun.xml.internal.ws.addressing.model.MissingAddressingHeaderException;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundOperation.ANONYMOUS;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLOperation;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import javax.xml.ws.soap.AddressingFeature;
import javax.xml.ws.soap.AddressingFeature.Responses;

public class W3CWsaServerTube
  extends WsaServerTube
{
  private final AddressingFeature af;
  
  public W3CWsaServerTube(WSEndpoint paramWSEndpoint, @NotNull WSDLPort paramWSDLPort, WSBinding paramWSBinding, Tube paramTube)
  {
    super(paramWSEndpoint, paramWSDLPort, paramWSBinding, paramTube);
    af = ((AddressingFeature)paramWSBinding.getFeature(AddressingFeature.class));
  }
  
  public W3CWsaServerTube(W3CWsaServerTube paramW3CWsaServerTube, TubeCloner paramTubeCloner)
  {
    super(paramW3CWsaServerTube, paramTubeCloner);
    af = af;
  }
  
  public W3CWsaServerTube copy(TubeCloner paramTubeCloner)
  {
    return new W3CWsaServerTube(this, paramTubeCloner);
  }
  
  protected void checkMandatoryHeaders(Packet paramPacket, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, boolean paramBoolean5, boolean paramBoolean6)
  {
    super.checkMandatoryHeaders(paramPacket, paramBoolean1, paramBoolean2, paramBoolean3, paramBoolean4, paramBoolean5, paramBoolean6);
    WSDLBoundOperation localWSDLBoundOperation = getWSDLBoundOperation(paramPacket);
    if ((localWSDLBoundOperation != null) && (!localWSDLBoundOperation.getOperation().isOneWay()) && (!paramBoolean5)) {
      throw new MissingAddressingHeaderException(addressingVersion.messageIDTag, paramPacket);
    }
  }
  
  protected boolean isAnonymousRequired(@Nullable WSDLBoundOperation paramWSDLBoundOperation)
  {
    return getResponseRequirement(paramWSDLBoundOperation) == WSDLBoundOperation.ANONYMOUS.required;
  }
  
  private WSDLBoundOperation.ANONYMOUS getResponseRequirement(@Nullable WSDLBoundOperation paramWSDLBoundOperation)
  {
    try
    {
      if (af.getResponses() == AddressingFeature.Responses.ANONYMOUS) {
        return WSDLBoundOperation.ANONYMOUS.required;
      }
      if (af.getResponses() == AddressingFeature.Responses.NON_ANONYMOUS) {
        return WSDLBoundOperation.ANONYMOUS.prohibited;
      }
    }
    catch (NoSuchMethodError localNoSuchMethodError) {}
    return paramWSDLBoundOperation != null ? paramWSDLBoundOperation.getAnonymous() : WSDLBoundOperation.ANONYMOUS.optional;
  }
  
  protected void checkAnonymousSemantics(WSDLBoundOperation paramWSDLBoundOperation, WSEndpointReference paramWSEndpointReference1, WSEndpointReference paramWSEndpointReference2)
  {
    String str1 = null;
    String str2 = null;
    if (paramWSEndpointReference1 != null) {
      str1 = paramWSEndpointReference1.getAddress();
    }
    if (paramWSEndpointReference2 != null) {
      str2 = paramWSEndpointReference2.getAddress();
    }
    WSDLBoundOperation.ANONYMOUS localANONYMOUS = getResponseRequirement(paramWSDLBoundOperation);
    switch (localANONYMOUS)
    {
    case prohibited: 
      if ((str1 != null) && (str1.equals(addressingVersion.anonymousUri))) {
        throw new InvalidAddressingHeaderException(addressingVersion.replyToTag, W3CAddressingConstants.ONLY_NON_ANONYMOUS_ADDRESS_SUPPORTED);
      }
      if ((str2 != null) && (str2.equals(addressingVersion.anonymousUri))) {
        throw new InvalidAddressingHeaderException(addressingVersion.faultToTag, W3CAddressingConstants.ONLY_NON_ANONYMOUS_ADDRESS_SUPPORTED);
      }
      break;
    case required: 
      if ((str1 != null) && (!str1.equals(addressingVersion.anonymousUri))) {
        throw new InvalidAddressingHeaderException(addressingVersion.replyToTag, W3CAddressingConstants.ONLY_ANONYMOUS_ADDRESS_SUPPORTED);
      }
      if ((str2 != null) && (!str2.equals(addressingVersion.anonymousUri))) {
        throw new InvalidAddressingHeaderException(addressingVersion.faultToTag, W3CAddressingConstants.ONLY_ANONYMOUS_ADDRESS_SUPPORTED);
      }
      break;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\addressing\W3CWsaServerTube.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */