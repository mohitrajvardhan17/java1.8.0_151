package com.sun.xml.internal.ws.addressing;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.addressing.model.ActionNotSupportedException;
import com.sun.xml.internal.ws.addressing.model.InvalidAddressingHeaderException;
import com.sun.xml.internal.ws.api.EndpointAddress;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.addressing.NonAnonymousResponseProcessor;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.api.message.AddressingUtils;
import com.sun.xml.internal.ws.api.message.Header;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.MessageHeaders;
import com.sun.xml.internal.ws.api.message.Messages;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.pipe.Fiber;
import com.sun.xml.internal.ws.api.pipe.NextAction;
import com.sun.xml.internal.ws.api.pipe.ThrowableContainerPropertySet;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;
import com.sun.xml.internal.ws.api.server.TransportBackChannel;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.client.Stub;
import com.sun.xml.internal.ws.message.FaultDetailHeader;
import com.sun.xml.internal.ws.resources.AddressingMessages;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPFault;
import javax.xml.ws.WebServiceException;

public class WsaServerTube
  extends WsaTube
{
  private WSEndpoint endpoint;
  private WSEndpointReference replyTo;
  private WSEndpointReference faultTo;
  private boolean isAnonymousRequired = false;
  protected boolean isEarlyBackchannelCloseAllowed = true;
  private WSDLBoundOperation wbo;
  /**
   * @deprecated
   */
  public static final String REQUEST_MESSAGE_ID = "com.sun.xml.internal.ws.addressing.request.messageID";
  private static final Logger LOGGER = Logger.getLogger(WsaServerTube.class.getName());
  
  public WsaServerTube(WSEndpoint paramWSEndpoint, @NotNull WSDLPort paramWSDLPort, WSBinding paramWSBinding, Tube paramTube)
  {
    super(paramWSDLPort, paramWSBinding, paramTube);
    endpoint = paramWSEndpoint;
  }
  
  public WsaServerTube(WsaServerTube paramWsaServerTube, TubeCloner paramTubeCloner)
  {
    super(paramWsaServerTube, paramTubeCloner);
    endpoint = endpoint;
  }
  
  public WsaServerTube copy(TubeCloner paramTubeCloner)
  {
    return new WsaServerTube(this, paramTubeCloner);
  }
  
  @NotNull
  public NextAction processRequest(Packet paramPacket)
  {
    Message localMessage = paramPacket.getMessage();
    if (localMessage == null) {
      return doInvoke(next, paramPacket);
    }
    paramPacket.addSatellite(new WsaPropertyBag(addressingVersion, soapVersion, paramPacket));
    MessageHeaders localMessageHeaders = paramPacket.getMessage().getHeaders();
    String str;
    try
    {
      replyTo = AddressingUtils.getReplyTo(localMessageHeaders, addressingVersion, soapVersion);
      faultTo = AddressingUtils.getFaultTo(localMessageHeaders, addressingVersion, soapVersion);
      str = AddressingUtils.getMessageID(localMessageHeaders, addressingVersion, soapVersion);
    }
    catch (InvalidAddressingHeaderException localInvalidAddressingHeaderException)
    {
      LOGGER.log(Level.WARNING, addressingVersion.getInvalidMapText() + ", Problem header:" + localInvalidAddressingHeaderException.getProblemHeader() + ", Reason: " + localInvalidAddressingHeaderException.getSubsubcode(), localInvalidAddressingHeaderException);
      localMessageHeaders.remove(localInvalidAddressingHeaderException.getProblemHeader());
      SOAPFault localSOAPFault = helper.createInvalidAddressingHeaderFault(localInvalidAddressingHeaderException, addressingVersion);
      if ((wsdlPort != null) && (paramPacket.getMessage().isOneWay(wsdlPort)))
      {
        localObject1 = paramPacket.createServerResponse(null, wsdlPort, null, binding);
        return doReturnWith((Packet)localObject1);
      }
      Object localObject1 = Messages.create(localSOAPFault);
      if (soapVersion == SOAPVersion.SOAP_11)
      {
        localObject2 = new FaultDetailHeader(addressingVersion, addressingVersion.problemHeaderQNameTag.getLocalPart(), localInvalidAddressingHeaderException.getProblemHeader());
        ((Message)localObject1).getHeaders().add((Header)localObject2);
      }
      Object localObject2 = paramPacket.createServerResponse((Message)localObject1, wsdlPort, null, binding);
      return doReturnWith((Packet)localObject2);
    }
    if (replyTo == null) {
      replyTo = addressingVersion.anonymousEpr;
    }
    if (faultTo == null) {
      faultTo = replyTo;
    }
    paramPacket.put("com.sun.xml.internal.ws.addressing.WsaPropertyBag.ReplyToFromRequest", replyTo);
    paramPacket.put("com.sun.xml.internal.ws.addressing.WsaPropertyBag.FaultToFromRequest", faultTo);
    paramPacket.put("com.sun.xml.internal.ws.addressing.WsaPropertyBag.MessageIdFromRequest", str);
    wbo = getWSDLBoundOperation(paramPacket);
    isAnonymousRequired = isAnonymousRequired(wbo);
    Packet localPacket = validateInboundHeaders(paramPacket);
    if (localPacket.getMessage() == null) {
      return doReturnWith(localPacket);
    }
    if (localPacket.getMessage().isFault())
    {
      if ((isEarlyBackchannelCloseAllowed) && (!isAnonymousRequired) && (!faultTo.isAnonymous()) && (transportBackChannel != null)) {
        transportBackChannel.close();
      }
      return processResponse(localPacket);
    }
    if ((isEarlyBackchannelCloseAllowed) && (!isAnonymousRequired) && (!replyTo.isAnonymous()) && (!faultTo.isAnonymous()) && (transportBackChannel != null)) {
      transportBackChannel.close();
    }
    return doInvoke(next, localPacket);
  }
  
  protected boolean isAnonymousRequired(@Nullable WSDLBoundOperation paramWSDLBoundOperation)
  {
    return false;
  }
  
  protected void checkAnonymousSemantics(WSDLBoundOperation paramWSDLBoundOperation, WSEndpointReference paramWSEndpointReference1, WSEndpointReference paramWSEndpointReference2) {}
  
  @NotNull
  public NextAction processException(Throwable paramThrowable)
  {
    Packet localPacket = Fiber.current().getPacket();
    ThrowableContainerPropertySet localThrowableContainerPropertySet = (ThrowableContainerPropertySet)localPacket.getSatellite(ThrowableContainerPropertySet.class);
    if (localThrowableContainerPropertySet == null)
    {
      localThrowableContainerPropertySet = new ThrowableContainerPropertySet(paramThrowable);
      localPacket.addSatellite(localThrowableContainerPropertySet);
    }
    else if (paramThrowable != localThrowableContainerPropertySet.getThrowable())
    {
      localThrowableContainerPropertySet.setThrowable(paramThrowable);
    }
    return processResponse(endpoint.createServiceResponseForException(localThrowableContainerPropertySet, localPacket, soapVersion, wsdlPort, endpoint.getSEIModel(), binding));
  }
  
  @NotNull
  public NextAction processResponse(Packet paramPacket)
  {
    Message localMessage = paramPacket.getMessage();
    if (localMessage == null) {
      return doReturnWith(paramPacket);
    }
    String str = AddressingUtils.getTo(localMessage.getHeaders(), addressingVersion, soapVersion);
    if (str != null) {
      replyTo = (faultTo = new WSEndpointReference(str, addressingVersion));
    }
    if (replyTo == null) {
      replyTo = ((WSEndpointReference)paramPacket.get("com.sun.xml.internal.ws.addressing.WsaPropertyBag.ReplyToFromRequest"));
    }
    if (faultTo == null) {
      faultTo = ((WSEndpointReference)paramPacket.get("com.sun.xml.internal.ws.addressing.WsaPropertyBag.FaultToFromRequest"));
    }
    WSEndpointReference localWSEndpointReference = localMessage.isFault() ? faultTo : replyTo;
    if ((localWSEndpointReference == null) && ((proxy instanceof Stub))) {
      localWSEndpointReference = ((Stub)proxy).getWSEndpointReference();
    }
    if ((localWSEndpointReference == null) || (localWSEndpointReference.isAnonymous()) || (isAnonymousRequired)) {
      return doReturnWith(paramPacket);
    }
    if (localWSEndpointReference.isNone())
    {
      paramPacket.setMessage(null);
      return doReturnWith(paramPacket);
    }
    if ((wsdlPort != null) && (paramPacket.getMessage().isOneWay(wsdlPort)))
    {
      LOGGER.fine(AddressingMessages.NON_ANONYMOUS_RESPONSE_ONEWAY());
      return doReturnWith(paramPacket);
    }
    Object localObject;
    if ((wbo != null) || (soapAction == null))
    {
      localObject = paramPacket.getMessage().isFault() ? helper.getFaultAction(wbo, paramPacket) : helper.getOutputAction(wbo);
      if ((soapAction == null) || ((localObject != null) && (!((String)localObject).equals("http://jax-ws.dev.java.net/addressing/output-action-not-set")))) {
        soapAction = ((String)localObject);
      }
    }
    expectReply = Boolean.valueOf(false);
    try
    {
      localObject = new EndpointAddress(URI.create(localWSEndpointReference.getAddress()));
    }
    catch (NullPointerException localNullPointerException)
    {
      throw new WebServiceException(localNullPointerException);
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      throw new WebServiceException(localIllegalArgumentException);
    }
    endpointAddress = ((EndpointAddress)localObject);
    if (isAdapterDeliversNonAnonymousResponse) {
      return doReturnWith(paramPacket);
    }
    return doReturnWith(NonAnonymousResponseProcessor.getDefault().process(paramPacket));
  }
  
  protected void validateAction(Packet paramPacket)
  {
    WSDLBoundOperation localWSDLBoundOperation = getWSDLBoundOperation(paramPacket);
    if (localWSDLBoundOperation == null) {
      return;
    }
    String str1 = AddressingUtils.getAction(paramPacket.getMessage().getHeaders(), addressingVersion, soapVersion);
    if (str1 == null) {
      throw new WebServiceException(AddressingMessages.VALIDATION_SERVER_NULL_ACTION());
    }
    Object localObject = helper.getInputAction(paramPacket);
    String str2 = helper.getSOAPAction(paramPacket);
    if ((helper.isInputActionDefault(paramPacket)) && (str2 != null) && (!str2.equals(""))) {
      localObject = str2;
    }
    if ((localObject != null) && (!str1.equals(localObject))) {
      throw new ActionNotSupportedException(str1);
    }
  }
  
  protected void checkMessageAddressingProperties(Packet paramPacket)
  {
    super.checkMessageAddressingProperties(paramPacket);
    WSDLBoundOperation localWSDLBoundOperation = getWSDLBoundOperation(paramPacket);
    checkAnonymousSemantics(localWSDLBoundOperation, replyTo, faultTo);
    checkNonAnonymousAddresses(replyTo, faultTo);
  }
  
  private void checkNonAnonymousAddresses(WSEndpointReference paramWSEndpointReference1, WSEndpointReference paramWSEndpointReference2)
  {
    if (!paramWSEndpointReference1.isAnonymous()) {
      try
      {
        new EndpointAddress(URI.create(paramWSEndpointReference1.getAddress()));
      }
      catch (Exception localException)
      {
        throw new InvalidAddressingHeaderException(addressingVersion.replyToTag, addressingVersion.invalidAddressTag);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\addressing\WsaServerTube.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */