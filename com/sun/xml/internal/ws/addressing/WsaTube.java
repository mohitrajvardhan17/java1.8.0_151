package com.sun.xml.internal.ws.addressing;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.addressing.model.InvalidAddressingHeaderException;
import com.sun.xml.internal.ws.addressing.model.MissingAddressingHeaderException;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.message.AddressingUtils;
import com.sun.xml.internal.ws.api.message.Header;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.MessageHeaders;
import com.sun.xml.internal.ws.api.message.Messages;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundPortType;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.pipe.NextAction;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;
import com.sun.xml.internal.ws.api.pipe.helper.AbstractFilterTubeImpl;
import com.sun.xml.internal.ws.developer.MemberSubmissionAddressingFeature;
import com.sun.xml.internal.ws.message.FaultDetailHeader;
import com.sun.xml.internal.ws.resources.AddressingMessages;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPFault;
import javax.xml.stream.XMLStreamException;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.AddressingFeature;
import javax.xml.ws.soap.SOAPBinding;

abstract class WsaTube
  extends AbstractFilterTubeImpl
{
  @NotNull
  protected final WSDLPort wsdlPort;
  protected final WSBinding binding;
  final WsaTubeHelper helper;
  @NotNull
  protected final AddressingVersion addressingVersion;
  protected final SOAPVersion soapVersion;
  private final boolean addressingRequired;
  private static final Logger LOGGER = Logger.getLogger(WsaTube.class.getName());
  
  public WsaTube(WSDLPort paramWSDLPort, WSBinding paramWSBinding, Tube paramTube)
  {
    super(paramTube);
    wsdlPort = paramWSDLPort;
    binding = paramWSBinding;
    addKnownHeadersToBinding(paramWSBinding);
    addressingVersion = paramWSBinding.getAddressingVersion();
    soapVersion = paramWSBinding.getSOAPVersion();
    helper = getTubeHelper();
    addressingRequired = AddressingVersion.isRequired(paramWSBinding);
  }
  
  public WsaTube(WsaTube paramWsaTube, TubeCloner paramTubeCloner)
  {
    super(paramWsaTube, paramTubeCloner);
    wsdlPort = wsdlPort;
    binding = binding;
    helper = helper;
    addressingVersion = addressingVersion;
    soapVersion = soapVersion;
    addressingRequired = addressingRequired;
  }
  
  private void addKnownHeadersToBinding(WSBinding paramWSBinding)
  {
    for (AddressingVersion localAddressingVersion : )
    {
      paramWSBinding.addKnownHeader(actionTag);
      paramWSBinding.addKnownHeader(faultDetailTag);
      paramWSBinding.addKnownHeader(faultToTag);
      paramWSBinding.addKnownHeader(fromTag);
      paramWSBinding.addKnownHeader(messageIDTag);
      paramWSBinding.addKnownHeader(relatesToTag);
      paramWSBinding.addKnownHeader(replyToTag);
      paramWSBinding.addKnownHeader(toTag);
    }
  }
  
  @NotNull
  public NextAction processException(Throwable paramThrowable)
  {
    return super.processException(paramThrowable);
  }
  
  protected WsaTubeHelper getTubeHelper()
  {
    if (binding.isFeatureEnabled(AddressingFeature.class)) {
      return new WsaTubeHelperImpl(wsdlPort, null, binding);
    }
    if (binding.isFeatureEnabled(MemberSubmissionAddressingFeature.class)) {
      return new com.sun.xml.internal.ws.addressing.v200408.WsaTubeHelperImpl(wsdlPort, null, binding);
    }
    throw new WebServiceException(AddressingMessages.ADDRESSING_NOT_ENABLED(getClass().getSimpleName()));
  }
  
  protected Packet validateInboundHeaders(Packet paramPacket)
  {
    SOAPFault localSOAPFault;
    FaultDetailHeader localFaultDetailHeader;
    try
    {
      checkMessageAddressingProperties(paramPacket);
      return paramPacket;
    }
    catch (InvalidAddressingHeaderException localInvalidAddressingHeaderException)
    {
      LOGGER.log(Level.WARNING, addressingVersion.getInvalidMapText() + ", Problem header:" + localInvalidAddressingHeaderException.getProblemHeader() + ", Reason: " + localInvalidAddressingHeaderException.getSubsubcode(), localInvalidAddressingHeaderException);
      localSOAPFault = helper.createInvalidAddressingHeaderFault(localInvalidAddressingHeaderException, addressingVersion);
      localFaultDetailHeader = new FaultDetailHeader(addressingVersion, addressingVersion.problemHeaderQNameTag.getLocalPart(), localInvalidAddressingHeaderException.getProblemHeader());
    }
    catch (MissingAddressingHeaderException localMissingAddressingHeaderException)
    {
      LOGGER.log(Level.WARNING, addressingVersion.getMapRequiredText() + ", Problem header:" + localMissingAddressingHeaderException.getMissingHeaderQName(), localMissingAddressingHeaderException);
      localSOAPFault = helper.newMapRequiredFault(localMissingAddressingHeaderException);
      localFaultDetailHeader = new FaultDetailHeader(addressingVersion, addressingVersion.problemHeaderQNameTag.getLocalPart(), localMissingAddressingHeaderException.getMissingHeaderQName());
    }
    if (localSOAPFault != null)
    {
      if ((wsdlPort != null) && (paramPacket.getMessage().isOneWay(wsdlPort))) {
        return paramPacket.createServerResponse(null, wsdlPort, null, binding);
      }
      Message localMessage = Messages.create(localSOAPFault);
      if (soapVersion == SOAPVersion.SOAP_11) {
        localMessage.getHeaders().add(localFaultDetailHeader);
      }
      return paramPacket.createServerResponse(localMessage, wsdlPort, null, binding);
    }
    return paramPacket;
  }
  
  protected void checkMessageAddressingProperties(Packet paramPacket)
  {
    checkCardinality(paramPacket);
  }
  
  final boolean isAddressingEngagedOrRequired(Packet paramPacket, WSBinding paramWSBinding)
  {
    if (AddressingVersion.isRequired(paramWSBinding)) {
      return true;
    }
    if (paramPacket == null) {
      return false;
    }
    if (paramPacket.getMessage() == null) {
      return false;
    }
    if (paramPacket.getMessage().getHeaders() != null) {
      return false;
    }
    String str = AddressingUtils.getAction(paramPacket.getMessage().getHeaders(), addressingVersion, soapVersion);
    return str == null;
  }
  
  protected void checkCardinality(Packet paramPacket)
  {
    Message localMessage = paramPacket.getMessage();
    if (localMessage == null)
    {
      if (addressingRequired) {
        throw new WebServiceException(AddressingMessages.NULL_MESSAGE());
      }
      return;
    }
    Iterator localIterator = localMessage.getHeaders().getHeaders(addressingVersion.nsUri, true);
    if (!localIterator.hasNext())
    {
      if (addressingRequired) {
        throw new MissingAddressingHeaderException(addressingVersion.actionTag, paramPacket);
      }
      return;
    }
    int i = 0;
    boolean bool1 = false;
    boolean bool2 = false;
    boolean bool3 = false;
    Header localHeader1 = 0;
    boolean bool4 = false;
    boolean bool5 = false;
    QName localQName = null;
    while (localIterator.hasNext())
    {
      localHeader2 = (Header)localIterator.next();
      if (isInCurrentRole(localHeader2, binding))
      {
        String str = localHeader2.getLocalPart();
        if (str.equals(addressingVersion.fromTag.getLocalPart()))
        {
          if (i != 0)
          {
            localQName = addressingVersion.fromTag;
            break;
          }
          i = 1;
        }
        else if (str.equals(addressingVersion.toTag.getLocalPart()))
        {
          if (bool1)
          {
            localQName = addressingVersion.toTag;
            break;
          }
          bool1 = true;
        }
        else if (str.equals(addressingVersion.replyToTag.getLocalPart()))
        {
          if (bool2)
          {
            localQName = addressingVersion.replyToTag;
            break;
          }
          bool2 = true;
          try
          {
            localHeader2.readAsEPR(addressingVersion);
          }
          catch (XMLStreamException localXMLStreamException1)
          {
            throw new WebServiceException(AddressingMessages.REPLY_TO_CANNOT_PARSE(), localXMLStreamException1);
          }
        }
        else if (str.equals(addressingVersion.faultToTag.getLocalPart()))
        {
          if (bool3)
          {
            localQName = addressingVersion.faultToTag;
            break;
          }
          bool3 = true;
          try
          {
            localHeader2.readAsEPR(addressingVersion);
          }
          catch (XMLStreamException localXMLStreamException2)
          {
            throw new WebServiceException(AddressingMessages.FAULT_TO_CANNOT_PARSE(), localXMLStreamException2);
          }
        }
        else if (str.equals(addressingVersion.actionTag.getLocalPart()))
        {
          if (localHeader1 != 0)
          {
            localQName = addressingVersion.actionTag;
            break;
          }
          localHeader1 = 1;
        }
        else if (str.equals(addressingVersion.messageIDTag.getLocalPart()))
        {
          if (bool4)
          {
            localQName = addressingVersion.messageIDTag;
            break;
          }
          bool4 = true;
        }
        else if (str.equals(addressingVersion.relatesToTag.getLocalPart()))
        {
          bool5 = true;
        }
        else if (!str.equals(addressingVersion.faultDetailTag.getLocalPart()))
        {
          System.err.println(AddressingMessages.UNKNOWN_WSA_HEADER());
        }
      }
    }
    if (localQName != null) {
      throw new InvalidAddressingHeaderException(localQName, addressingVersion.invalidCardinalityTag);
    }
    Header localHeader2 = localHeader1;
    if ((localHeader2 != 0) || (addressingRequired)) {
      checkMandatoryHeaders(paramPacket, localHeader1, bool1, bool2, bool3, bool4, bool5);
    }
  }
  
  final boolean isInCurrentRole(Header paramHeader, WSBinding paramWSBinding)
  {
    if (paramWSBinding == null) {
      return true;
    }
    return ((SOAPBinding)paramWSBinding).getRoles().contains(paramHeader.getRole(soapVersion));
  }
  
  protected final WSDLBoundOperation getWSDLBoundOperation(Packet paramPacket)
  {
    if (wsdlPort == null) {
      return null;
    }
    QName localQName = paramPacket.getWSDLOperation();
    if (localQName != null) {
      return wsdlPort.getBinding().get(localQName);
    }
    return null;
  }
  
  protected void validateSOAPAction(Packet paramPacket)
  {
    String str = AddressingUtils.getAction(paramPacket.getMessage().getHeaders(), addressingVersion, soapVersion);
    if (str == null) {
      throw new WebServiceException(AddressingMessages.VALIDATION_SERVER_NULL_ACTION());
    }
    if ((soapAction != null) && (!soapAction.equals("\"\"")) && (!soapAction.equals("\"" + str + "\""))) {
      throw new InvalidAddressingHeaderException(addressingVersion.actionTag, addressingVersion.actionMismatchTag);
    }
  }
  
  protected abstract void validateAction(Packet paramPacket);
  
  protected void checkMandatoryHeaders(Packet paramPacket, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, boolean paramBoolean5, boolean paramBoolean6)
  {
    if (!paramBoolean1) {
      throw new MissingAddressingHeaderException(addressingVersion.actionTag, paramPacket);
    }
    validateSOAPAction(paramPacket);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\addressing\WsaTube.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */