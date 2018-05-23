package com.sun.xml.internal.messaging.saaj.soap.dynamic;

import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.internal.messaging.saaj.soap.SOAPFactoryImpl;
import javax.xml.soap.Detail;
import javax.xml.soap.SOAPException;

public class SOAPFactoryDynamicImpl
  extends SOAPFactoryImpl
{
  public SOAPFactoryDynamicImpl() {}
  
  protected SOAPDocumentImpl createDocument()
  {
    return null;
  }
  
  public Detail createDetail()
    throws SOAPException
  {
    throw new UnsupportedOperationException("createDetail() not supported for Dynamic Protocol");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\messaging\saaj\soap\dynamic\SOAPFactoryDynamicImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */