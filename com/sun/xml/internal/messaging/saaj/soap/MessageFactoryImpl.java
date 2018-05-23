package com.sun.xml.internal.messaging.saaj.soap;

import com.sun.xml.internal.messaging.saaj.SOAPExceptionImpl;
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.ContentType;
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.ParseException;
import com.sun.xml.internal.messaging.saaj.soap.ver1_1.Message1_1Impl;
import com.sun.xml.internal.messaging.saaj.soap.ver1_2.Message1_2Impl;
import com.sun.xml.internal.messaging.saaj.util.TeeInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

public class MessageFactoryImpl
  extends MessageFactory
{
  protected static final Logger log = Logger.getLogger("com.sun.xml.internal.messaging.saaj.soap", "com.sun.xml.internal.messaging.saaj.soap.LocalStrings");
  protected OutputStream listener;
  protected boolean lazyAttachments = false;
  
  public MessageFactoryImpl() {}
  
  public OutputStream listen(OutputStream paramOutputStream)
  {
    OutputStream localOutputStream = listener;
    listener = paramOutputStream;
    return localOutputStream;
  }
  
  public SOAPMessage createMessage()
    throws SOAPException
  {
    throw new UnsupportedOperationException();
  }
  
  public SOAPMessage createMessage(boolean paramBoolean1, boolean paramBoolean2)
    throws SOAPException
  {
    throw new UnsupportedOperationException();
  }
  
  public SOAPMessage createMessage(MimeHeaders paramMimeHeaders, InputStream paramInputStream)
    throws SOAPException, IOException
  {
    String str = MessageImpl.getContentType(paramMimeHeaders);
    if (listener != null) {
      paramInputStream = new TeeInputStream(paramInputStream, listener);
    }
    try
    {
      ContentType localContentType = new ContentType(str);
      int i = MessageImpl.identifyContentType(localContentType);
      if (MessageImpl.isSoap1_1Content(i)) {
        return new Message1_1Impl(paramMimeHeaders, localContentType, i, paramInputStream);
      }
      if (MessageImpl.isSoap1_2Content(i)) {
        return new Message1_2Impl(paramMimeHeaders, localContentType, i, paramInputStream);
      }
      log.severe("SAAJ0530.soap.unknown.Content-Type");
      throw new SOAPExceptionImpl("Unrecognized Content-Type");
    }
    catch (ParseException localParseException)
    {
      log.severe("SAAJ0531.soap.cannot.parse.Content-Type");
      throw new SOAPExceptionImpl("Unable to parse content type: " + localParseException.getMessage());
    }
  }
  
  protected static final String getContentType(MimeHeaders paramMimeHeaders)
  {
    String[] arrayOfString = paramMimeHeaders.getHeader("Content-Type");
    if (arrayOfString == null) {
      return null;
    }
    return arrayOfString[0];
  }
  
  public void setLazyAttachmentOptimization(boolean paramBoolean)
  {
    lazyAttachments = paramBoolean;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\messaging\saaj\soap\MessageFactoryImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */