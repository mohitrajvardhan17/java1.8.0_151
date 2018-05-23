package com.sun.xml.internal.ws.api.message;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.addressing.WsaTubeHelper;
import com.sun.xml.internal.ws.api.EndpointAddress;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.addressing.AddressingPropertySet;
import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.api.addressing.OneWayFeature;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundOperation;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundOperation.ANONYMOUS;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundPortType;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.message.RelatesToHeader;
import com.sun.xml.internal.ws.message.StringHeader;
import com.sun.xml.internal.ws.resources.AddressingMessages;
import com.sun.xml.internal.ws.resources.ClientMessages;
import java.util.Iterator;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.ws.WebServiceException;

public class AddressingUtils
{
  public AddressingUtils() {}
  
  public static void fillRequestAddressingHeaders(MessageHeaders paramMessageHeaders, Packet paramPacket, AddressingVersion paramAddressingVersion, SOAPVersion paramSOAPVersion, boolean paramBoolean, String paramString)
  {
    fillRequestAddressingHeaders(paramMessageHeaders, paramPacket, paramAddressingVersion, paramSOAPVersion, paramBoolean, paramString, false);
  }
  
  public static void fillRequestAddressingHeaders(MessageHeaders paramMessageHeaders, Packet paramPacket, AddressingVersion paramAddressingVersion, SOAPVersion paramSOAPVersion, boolean paramBoolean1, String paramString, boolean paramBoolean2)
  {
    fillCommonAddressingHeaders(paramMessageHeaders, paramPacket, paramAddressingVersion, paramSOAPVersion, paramString, paramBoolean2);
    if (!paramBoolean1)
    {
      WSEndpointReference localWSEndpointReference = anonymousEpr;
      if (paramMessageHeaders.get(replyToTag, false) == null) {
        paramMessageHeaders.add(localWSEndpointReference.createHeader(replyToTag));
      }
      if (paramMessageHeaders.get(faultToTag, false) == null) {
        paramMessageHeaders.add(localWSEndpointReference.createHeader(faultToTag));
      }
      if ((paramPacket.getMessage().getHeaders().get(messageIDTag, false) == null) && (paramMessageHeaders.get(messageIDTag, false) == null))
      {
        StringHeader localStringHeader = new StringHeader(messageIDTag, Message.generateMessageID());
        paramMessageHeaders.add(localStringHeader);
      }
    }
  }
  
  public static void fillRequestAddressingHeaders(MessageHeaders paramMessageHeaders, WSDLPort paramWSDLPort, WSBinding paramWSBinding, Packet paramPacket)
  {
    if (paramWSBinding == null) {
      throw new IllegalArgumentException(AddressingMessages.NULL_BINDING());
    }
    if (paramWSBinding.isFeatureEnabled(SuppressAutomaticWSARequestHeadersFeature.class)) {
      return;
    }
    MessageHeaders localMessageHeaders = paramPacket.getMessage().getHeaders();
    String str1 = getAction(localMessageHeaders, paramWSBinding.getAddressingVersion(), paramWSBinding.getSOAPVersion());
    if (str1 != null) {
      return;
    }
    AddressingVersion localAddressingVersion = paramWSBinding.getAddressingVersion();
    WsaTubeHelper localWsaTubeHelper = localAddressingVersion.getWsaHelper(paramWSDLPort, null, paramWSBinding);
    String str2 = localWsaTubeHelper.getEffectiveInputAction(paramPacket);
    if ((str2 == null) || ((str2.equals("")) && (paramWSBinding.getSOAPVersion() == SOAPVersion.SOAP_11))) {
      throw new WebServiceException(ClientMessages.INVALID_SOAP_ACTION());
    }
    boolean bool = !expectReply.booleanValue();
    if ((paramWSDLPort != null) && (!bool) && (paramPacket.getMessage() != null) && (paramPacket.getWSDLOperation() != null))
    {
      localObject = paramWSDLPort.getBinding().get(paramPacket.getWSDLOperation());
      if ((localObject != null) && (((WSDLBoundOperation)localObject).getAnonymous() == WSDLBoundOperation.ANONYMOUS.prohibited)) {
        throw new WebServiceException(AddressingMessages.WSAW_ANONYMOUS_PROHIBITED());
      }
    }
    Object localObject = (OneWayFeature)paramWSBinding.getFeature(OneWayFeature.class);
    AddressingPropertySet localAddressingPropertySet = (AddressingPropertySet)paramPacket.getSatellite(AddressingPropertySet.class);
    localObject = localAddressingPropertySet == null ? localObject : new OneWayFeature(localAddressingPropertySet, localAddressingVersion);
    if ((localObject == null) || (!((OneWayFeature)localObject).isEnabled())) {
      fillRequestAddressingHeaders(paramMessageHeaders, paramPacket, localAddressingVersion, paramWSBinding.getSOAPVersion(), bool, str2, AddressingVersion.isRequired(paramWSBinding));
    } else {
      fillRequestAddressingHeaders(paramMessageHeaders, paramPacket, localAddressingVersion, paramWSBinding.getSOAPVersion(), (OneWayFeature)localObject, bool, str2);
    }
  }
  
  public static String getAction(@NotNull MessageHeaders paramMessageHeaders, @NotNull AddressingVersion paramAddressingVersion, @NotNull SOAPVersion paramSOAPVersion)
  {
    if (paramAddressingVersion == null) {
      throw new IllegalArgumentException(AddressingMessages.NULL_ADDRESSING_VERSION());
    }
    String str = null;
    Header localHeader = getFirstHeader(paramMessageHeaders, actionTag, true, paramSOAPVersion);
    if (localHeader != null) {
      str = localHeader.getStringContent();
    }
    return str;
  }
  
  public static WSEndpointReference getFaultTo(@NotNull MessageHeaders paramMessageHeaders, @NotNull AddressingVersion paramAddressingVersion, @NotNull SOAPVersion paramSOAPVersion)
  {
    if (paramAddressingVersion == null) {
      throw new IllegalArgumentException(AddressingMessages.NULL_ADDRESSING_VERSION());
    }
    Header localHeader = getFirstHeader(paramMessageHeaders, faultToTag, true, paramSOAPVersion);
    WSEndpointReference localWSEndpointReference = null;
    if (localHeader != null) {
      try
      {
        localWSEndpointReference = localHeader.readAsEPR(paramAddressingVersion);
      }
      catch (XMLStreamException localXMLStreamException)
      {
        throw new WebServiceException(AddressingMessages.FAULT_TO_CANNOT_PARSE(), localXMLStreamException);
      }
    }
    return localWSEndpointReference;
  }
  
  public static String getMessageID(@NotNull MessageHeaders paramMessageHeaders, @NotNull AddressingVersion paramAddressingVersion, @NotNull SOAPVersion paramSOAPVersion)
  {
    if (paramAddressingVersion == null) {
      throw new IllegalArgumentException(AddressingMessages.NULL_ADDRESSING_VERSION());
    }
    Header localHeader = getFirstHeader(paramMessageHeaders, messageIDTag, true, paramSOAPVersion);
    String str = null;
    if (localHeader != null) {
      str = localHeader.getStringContent();
    }
    return str;
  }
  
  public static String getRelatesTo(@NotNull MessageHeaders paramMessageHeaders, @NotNull AddressingVersion paramAddressingVersion, @NotNull SOAPVersion paramSOAPVersion)
  {
    if (paramAddressingVersion == null) {
      throw new IllegalArgumentException(AddressingMessages.NULL_ADDRESSING_VERSION());
    }
    Header localHeader = getFirstHeader(paramMessageHeaders, relatesToTag, true, paramSOAPVersion);
    String str = null;
    if (localHeader != null) {
      str = localHeader.getStringContent();
    }
    return str;
  }
  
  public static WSEndpointReference getReplyTo(@NotNull MessageHeaders paramMessageHeaders, @NotNull AddressingVersion paramAddressingVersion, @NotNull SOAPVersion paramSOAPVersion)
  {
    if (paramAddressingVersion == null) {
      throw new IllegalArgumentException(AddressingMessages.NULL_ADDRESSING_VERSION());
    }
    Header localHeader = getFirstHeader(paramMessageHeaders, replyToTag, true, paramSOAPVersion);
    WSEndpointReference localWSEndpointReference;
    if (localHeader != null) {
      try
      {
        localWSEndpointReference = localHeader.readAsEPR(paramAddressingVersion);
      }
      catch (XMLStreamException localXMLStreamException)
      {
        throw new WebServiceException(AddressingMessages.REPLY_TO_CANNOT_PARSE(), localXMLStreamException);
      }
    } else {
      localWSEndpointReference = anonymousEpr;
    }
    return localWSEndpointReference;
  }
  
  public static String getTo(MessageHeaders paramMessageHeaders, AddressingVersion paramAddressingVersion, SOAPVersion paramSOAPVersion)
  {
    if (paramAddressingVersion == null) {
      throw new IllegalArgumentException(AddressingMessages.NULL_ADDRESSING_VERSION());
    }
    Header localHeader = getFirstHeader(paramMessageHeaders, toTag, true, paramSOAPVersion);
    String str;
    if (localHeader != null) {
      str = localHeader.getStringContent();
    } else {
      str = anonymousUri;
    }
    return str;
  }
  
  public static Header getFirstHeader(MessageHeaders paramMessageHeaders, QName paramQName, boolean paramBoolean, SOAPVersion paramSOAPVersion)
  {
    if (paramSOAPVersion == null) {
      throw new IllegalArgumentException(AddressingMessages.NULL_SOAP_VERSION());
    }
    Iterator localIterator = paramMessageHeaders.getHeaders(paramQName.getNamespaceURI(), paramQName.getLocalPart(), paramBoolean);
    while (localIterator.hasNext())
    {
      Header localHeader = (Header)localIterator.next();
      if (localHeader.getRole(paramSOAPVersion).equals(implicitRole)) {
        return localHeader;
      }
    }
    return null;
  }
  
  private static void fillRequestAddressingHeaders(@NotNull MessageHeaders paramMessageHeaders, @NotNull Packet paramPacket, @NotNull AddressingVersion paramAddressingVersion, @NotNull SOAPVersion paramSOAPVersion, @NotNull OneWayFeature paramOneWayFeature, boolean paramBoolean, @NotNull String paramString)
  {
    if ((!paramBoolean) && (!paramOneWayFeature.isUseAsyncWithSyncInvoke()) && (Boolean.TRUE.equals(isSynchronousMEP)))
    {
      fillRequestAddressingHeaders(paramMessageHeaders, paramPacket, paramAddressingVersion, paramSOAPVersion, paramBoolean, paramString);
    }
    else
    {
      fillCommonAddressingHeaders(paramMessageHeaders, paramPacket, paramAddressingVersion, paramSOAPVersion, paramString, false);
      int i = 0;
      Object localObject2;
      if (paramMessageHeaders.get(replyToTag, false) == null)
      {
        localObject1 = paramOneWayFeature.getReplyTo();
        if (localObject1 != null)
        {
          paramMessageHeaders.add(((WSEndpointReference)localObject1).createHeader(replyToTag));
          if (paramPacket.getMessage().getHeaders().get(messageIDTag, false) == null)
          {
            localObject2 = paramOneWayFeature.getMessageId() == null ? Message.generateMessageID() : paramOneWayFeature.getMessageId();
            paramMessageHeaders.add(new StringHeader(messageIDTag, (String)localObject2));
            i = 1;
          }
        }
      }
      Object localObject1 = paramOneWayFeature.getMessageId();
      if ((i == 0) && (localObject1 != null)) {
        paramMessageHeaders.add(new StringHeader(messageIDTag, (String)localObject1));
      }
      if (paramMessageHeaders.get(faultToTag, false) == null)
      {
        localObject2 = paramOneWayFeature.getFaultTo();
        if (localObject2 != null)
        {
          paramMessageHeaders.add(((WSEndpointReference)localObject2).createHeader(faultToTag));
          if (paramMessageHeaders.get(messageIDTag, false) == null) {
            paramMessageHeaders.add(new StringHeader(messageIDTag, Message.generateMessageID()));
          }
        }
      }
      if (paramOneWayFeature.getFrom() != null) {
        paramMessageHeaders.addOrReplace(paramOneWayFeature.getFrom().createHeader(fromTag));
      }
      if (paramOneWayFeature.getRelatesToID() != null) {
        paramMessageHeaders.addOrReplace(new RelatesToHeader(relatesToTag, paramOneWayFeature.getRelatesToID()));
      }
    }
  }
  
  private static void fillCommonAddressingHeaders(MessageHeaders paramMessageHeaders, Packet paramPacket, @NotNull AddressingVersion paramAddressingVersion, @NotNull SOAPVersion paramSOAPVersion, @NotNull String paramString, boolean paramBoolean)
  {
    if (paramPacket == null) {
      throw new IllegalArgumentException(AddressingMessages.NULL_PACKET());
    }
    if (paramAddressingVersion == null) {
      throw new IllegalArgumentException(AddressingMessages.NULL_ADDRESSING_VERSION());
    }
    if (paramSOAPVersion == null) {
      throw new IllegalArgumentException(AddressingMessages.NULL_SOAP_VERSION());
    }
    if ((paramString == null) && (!httpBindingId.equals("http://www.w3.org/2003/05/soap/bindings/HTTP/"))) {
      throw new IllegalArgumentException(AddressingMessages.NULL_ACTION());
    }
    StringHeader localStringHeader;
    if (paramMessageHeaders.get(toTag, false) == null)
    {
      localStringHeader = new StringHeader(toTag, endpointAddress.toString());
      paramMessageHeaders.add(localStringHeader);
    }
    if (paramString != null)
    {
      soapAction = paramString;
      if (paramMessageHeaders.get(actionTag, false) == null)
      {
        localStringHeader = new StringHeader(actionTag, paramString, paramSOAPVersion, paramBoolean);
        paramMessageHeaders.add(localStringHeader);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\message\AddressingUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */