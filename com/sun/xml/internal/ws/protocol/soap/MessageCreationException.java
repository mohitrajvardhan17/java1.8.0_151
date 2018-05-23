package com.sun.xml.internal.ws.protocol.soap;

import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.message.ExceptionHasMessage;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.fault.SOAPFaultBuilder;
import javax.xml.namespace.QName;

public class MessageCreationException
  extends ExceptionHasMessage
{
  private final SOAPVersion soapVersion;
  
  public MessageCreationException(SOAPVersion paramSOAPVersion, Object... paramVarArgs)
  {
    super("soap.msg.create.err", paramVarArgs);
    soapVersion = paramSOAPVersion;
  }
  
  public String getDefaultResourceBundleName()
  {
    return "com.sun.xml.internal.ws.resources.soap";
  }
  
  public Message getFaultMessage()
  {
    QName localQName = soapVersion.faultCodeClient;
    return SOAPFaultBuilder.createSOAPFaultMessage(soapVersion, getLocalizedMessage(), localQName);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\protocol\soap\MessageCreationException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */