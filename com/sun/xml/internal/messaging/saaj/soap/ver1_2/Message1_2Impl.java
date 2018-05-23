package com.sun.xml.internal.messaging.saaj.soap.ver1_2;

import com.sun.xml.internal.messaging.saaj.SOAPExceptionImpl;
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.ContentType;
import com.sun.xml.internal.messaging.saaj.soap.MessageImpl;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

public class Message1_2Impl
  extends MessageImpl
  implements SOAPConstants
{
  public Message1_2Impl() {}
  
  public Message1_2Impl(SOAPMessage paramSOAPMessage)
  {
    super(paramSOAPMessage);
  }
  
  public Message1_2Impl(boolean paramBoolean1, boolean paramBoolean2)
  {
    super(paramBoolean1, paramBoolean2);
  }
  
  public Message1_2Impl(MimeHeaders paramMimeHeaders, InputStream paramInputStream)
    throws IOException, SOAPExceptionImpl
  {
    super(paramMimeHeaders, paramInputStream);
  }
  
  public Message1_2Impl(MimeHeaders paramMimeHeaders, ContentType paramContentType, int paramInt, InputStream paramInputStream)
    throws SOAPExceptionImpl
  {
    super(paramMimeHeaders, paramContentType, paramInt, paramInputStream);
  }
  
  public SOAPPart getSOAPPart()
  {
    if (soapPartImpl == null) {
      soapPartImpl = new SOAPPart1_2Impl(this);
    }
    return soapPartImpl;
  }
  
  protected boolean isCorrectSoapVersion(int paramInt)
  {
    return (paramInt & 0x8) != 0;
  }
  
  protected String getExpectedContentType()
  {
    return isFastInfoset ? "application/soap+fastinfoset" : "application/soap+xml";
  }
  
  protected String getExpectedAcceptHeader()
  {
    String str = "application/soap+xml, text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2";
    return acceptFastInfoset ? "application/soap+fastinfoset, " + str : str;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\messaging\saaj\soap\ver1_2\Message1_2Impl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */