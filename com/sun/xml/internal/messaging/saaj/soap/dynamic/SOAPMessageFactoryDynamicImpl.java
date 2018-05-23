package com.sun.xml.internal.messaging.saaj.soap.dynamic;

import com.sun.xml.internal.messaging.saaj.soap.MessageFactoryImpl;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

public class SOAPMessageFactoryDynamicImpl
  extends MessageFactoryImpl
{
  public SOAPMessageFactoryDynamicImpl() {}
  
  public SOAPMessage createMessage()
    throws SOAPException
  {
    throw new UnsupportedOperationException("createMessage() not supported for Dynamic Protocol");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\messaging\saaj\soap\dynamic\SOAPMessageFactoryDynamicImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */