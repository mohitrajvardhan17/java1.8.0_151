package com.sun.xml.internal.messaging.saaj.soap.ver1_2;

import com.sun.xml.internal.messaging.saaj.SOAPExceptionImpl;
import com.sun.xml.internal.messaging.saaj.soap.MessageFactoryImpl;
import com.sun.xml.internal.messaging.saaj.soap.MessageImpl;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

public class SOAPMessageFactory1_2Impl
  extends MessageFactoryImpl
{
  public SOAPMessageFactory1_2Impl() {}
  
  public SOAPMessage createMessage()
    throws SOAPException
  {
    return new Message1_2Impl();
  }
  
  public SOAPMessage createMessage(boolean paramBoolean1, boolean paramBoolean2)
    throws SOAPException
  {
    return new Message1_2Impl(paramBoolean1, paramBoolean2);
  }
  
  public SOAPMessage createMessage(MimeHeaders paramMimeHeaders, InputStream paramInputStream)
    throws IOException, SOAPExceptionImpl
  {
    if (paramMimeHeaders == null) {
      paramMimeHeaders = new MimeHeaders();
    }
    if (getContentType(paramMimeHeaders) == null) {
      paramMimeHeaders.setHeader("Content-Type", "application/soap+xml");
    }
    Message1_2Impl localMessage1_2Impl = new Message1_2Impl(paramMimeHeaders, paramInputStream);
    localMessage1_2Impl.setLazyAttachments(lazyAttachments);
    return localMessage1_2Impl;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\messaging\saaj\soap\ver1_2\SOAPMessageFactory1_2Impl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */