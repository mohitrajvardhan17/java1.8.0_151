package com.sun.xml.internal.messaging.saaj.client.p2p;

import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPException;

public class HttpSOAPConnectionFactory
  extends SOAPConnectionFactory
{
  public HttpSOAPConnectionFactory() {}
  
  public SOAPConnection createConnection()
    throws SOAPException
  {
    return new HttpSOAPConnection();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\messaging\saaj\client\p2p\HttpSOAPConnectionFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */