package com.sun.xml.internal.ws.api;

import com.oracle.webservices.internal.api.EnvelopeStyle.Style;
import com.oracle.webservices.internal.api.EnvelopeStyleFeature;
import com.sun.xml.internal.bind.util.Which;
import com.sun.xml.internal.ws.api.message.saaj.SAAJFactory;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;

public enum SOAPVersion
{
  SOAP_11("http://schemas.xmlsoap.org/wsdl/soap/http", "http://schemas.xmlsoap.org/soap/envelope/", "text/xml", "http://schemas.xmlsoap.org/soap/actor/next", "actor", "SOAP 1.1 Protocol", new QName("http://schemas.xmlsoap.org/soap/envelope/", "MustUnderstand"), "Client", "Server", Collections.singleton("http://schemas.xmlsoap.org/soap/actor/next")),  SOAP_12("http://www.w3.org/2003/05/soap/bindings/HTTP/", "http://www.w3.org/2003/05/soap-envelope", "application/soap+xml", "http://www.w3.org/2003/05/soap-envelope/role/ultimateReceiver", "role", "SOAP 1.2 Protocol", new QName("http://www.w3.org/2003/05/soap-envelope", "MustUnderstand"), "Sender", "Receiver", new HashSet(Arrays.asList(new String[] { "http://www.w3.org/2003/05/soap-envelope/role/next", "http://www.w3.org/2003/05/soap-envelope/role/ultimateReceiver" })));
  
  public final String httpBindingId;
  public final String nsUri;
  public final String contentType;
  public final QName faultCodeMustUnderstand;
  /**
   * @deprecated
   */
  public final MessageFactory saajMessageFactory;
  /**
   * @deprecated
   */
  public final SOAPFactory saajSoapFactory;
  private final String saajFactoryString;
  public final String implicitRole;
  public final Set<String> implicitRoleSet;
  public final Set<String> requiredRoles;
  public final String roleAttributeName;
  public final QName faultCodeClient;
  public final QName faultCodeServer;
  
  private SOAPVersion(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, QName paramQName, String paramString7, String paramString8, Set<String> paramSet)
  {
    httpBindingId = paramString1;
    nsUri = paramString2;
    contentType = paramString3;
    implicitRole = paramString4;
    implicitRoleSet = Collections.singleton(paramString4);
    roleAttributeName = paramString5;
    saajFactoryString = paramString6;
    try
    {
      saajMessageFactory = MessageFactory.newInstance(paramString6);
      saajSoapFactory = SOAPFactory.newInstance(paramString6);
    }
    catch (SOAPException localSOAPException)
    {
      throw new Error(localSOAPException);
    }
    catch (NoSuchMethodError localNoSuchMethodError)
    {
      LinkageError localLinkageError = new LinkageError("You are loading old SAAJ from " + Which.which(MessageFactory.class));
      localLinkageError.initCause(localNoSuchMethodError);
      throw localLinkageError;
    }
    faultCodeMustUnderstand = paramQName;
    requiredRoles = paramSet;
    faultCodeClient = new QName(paramString2, paramString7);
    faultCodeServer = new QName(paramString2, paramString8);
  }
  
  public SOAPFactory getSOAPFactory()
  {
    try
    {
      return SAAJFactory.getSOAPFactory(saajFactoryString);
    }
    catch (SOAPException localSOAPException)
    {
      throw new Error(localSOAPException);
    }
    catch (NoSuchMethodError localNoSuchMethodError)
    {
      LinkageError localLinkageError = new LinkageError("You are loading old SAAJ from " + Which.which(MessageFactory.class));
      localLinkageError.initCause(localNoSuchMethodError);
      throw localLinkageError;
    }
  }
  
  public MessageFactory getMessageFactory()
  {
    try
    {
      return SAAJFactory.getMessageFactory(saajFactoryString);
    }
    catch (SOAPException localSOAPException)
    {
      throw new Error(localSOAPException);
    }
    catch (NoSuchMethodError localNoSuchMethodError)
    {
      LinkageError localLinkageError = new LinkageError("You are loading old SAAJ from " + Which.which(MessageFactory.class));
      localLinkageError.initCause(localNoSuchMethodError);
      throw localLinkageError;
    }
  }
  
  public String toString()
  {
    return httpBindingId;
  }
  
  public static SOAPVersion fromHttpBinding(String paramString)
  {
    if (paramString == null) {
      return SOAP_11;
    }
    if (paramString.equals(SOAP_12httpBindingId)) {
      return SOAP_12;
    }
    return SOAP_11;
  }
  
  public static SOAPVersion fromNsUri(String paramString)
  {
    if (paramString.equals(SOAP_12nsUri)) {
      return SOAP_12;
    }
    return SOAP_11;
  }
  
  public static SOAPVersion from(EnvelopeStyleFeature paramEnvelopeStyleFeature)
  {
    EnvelopeStyle.Style[] arrayOfStyle = paramEnvelopeStyleFeature.getStyles();
    if (arrayOfStyle.length != 1) {
      throw new IllegalArgumentException("The EnvelopingFeature must has exactly one Enveloping.Style");
    }
    return from(arrayOfStyle[0]);
  }
  
  public static SOAPVersion from(EnvelopeStyle.Style paramStyle)
  {
    switch (paramStyle)
    {
    case SOAP11: 
      return SOAP_11;
    case SOAP12: 
      return SOAP_12;
    }
    return SOAP_11;
  }
  
  public EnvelopeStyleFeature toFeature()
  {
    return SOAP_11.equals(this) ? new EnvelopeStyleFeature(new EnvelopeStyle.Style[] { EnvelopeStyle.Style.SOAP11 }) : new EnvelopeStyleFeature(new EnvelopeStyle.Style[] { EnvelopeStyle.Style.SOAP12 });
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\SOAPVersion.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */