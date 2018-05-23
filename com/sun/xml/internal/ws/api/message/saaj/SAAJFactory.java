package com.sun.xml.internal.ws.api.message.saaj;

import com.sun.xml.internal.bind.marshaller.SAX2DOMEx;
import com.sun.xml.internal.ws.api.SOAPVersion;
import com.sun.xml.internal.ws.api.message.Attachment;
import com.sun.xml.internal.ws.api.message.AttachmentEx;
import com.sun.xml.internal.ws.api.message.AttachmentEx.MimeHeader;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import com.sun.xml.internal.ws.api.message.Message;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.message.saaj.SAAJMessage;
import com.sun.xml.internal.ws.util.ServiceFinder;
import com.sun.xml.internal.ws.util.xml.XmlUtil;
import java.util.Iterator;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPMessage;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

public class SAAJFactory
{
  private static final SAAJFactory instance = new SAAJFactory();
  
  public SAAJFactory() {}
  
  public static MessageFactory getMessageFactory(String paramString)
    throws SOAPException
  {
    Iterator localIterator = ServiceFinder.find(SAAJFactory.class).iterator();
    while (localIterator.hasNext())
    {
      SAAJFactory localSAAJFactory = (SAAJFactory)localIterator.next();
      MessageFactory localMessageFactory = localSAAJFactory.createMessageFactory(paramString);
      if (localMessageFactory != null) {
        return localMessageFactory;
      }
    }
    return instance.createMessageFactory(paramString);
  }
  
  public static SOAPFactory getSOAPFactory(String paramString)
    throws SOAPException
  {
    Iterator localIterator = ServiceFinder.find(SAAJFactory.class).iterator();
    while (localIterator.hasNext())
    {
      SAAJFactory localSAAJFactory = (SAAJFactory)localIterator.next();
      SOAPFactory localSOAPFactory = localSAAJFactory.createSOAPFactory(paramString);
      if (localSOAPFactory != null) {
        return localSOAPFactory;
      }
    }
    return instance.createSOAPFactory(paramString);
  }
  
  public static Message create(SOAPMessage paramSOAPMessage)
  {
    Iterator localIterator = ServiceFinder.find(SAAJFactory.class).iterator();
    while (localIterator.hasNext())
    {
      SAAJFactory localSAAJFactory = (SAAJFactory)localIterator.next();
      Message localMessage = localSAAJFactory.createMessage(paramSOAPMessage);
      if (localMessage != null) {
        return localMessage;
      }
    }
    return instance.createMessage(paramSOAPMessage);
  }
  
  public static SOAPMessage read(SOAPVersion paramSOAPVersion, Message paramMessage)
    throws SOAPException
  {
    Iterator localIterator = ServiceFinder.find(SAAJFactory.class).iterator();
    while (localIterator.hasNext())
    {
      SAAJFactory localSAAJFactory = (SAAJFactory)localIterator.next();
      SOAPMessage localSOAPMessage = localSAAJFactory.readAsSOAPMessage(paramSOAPVersion, paramMessage);
      if (localSOAPMessage != null) {
        return localSOAPMessage;
      }
    }
    return instance.readAsSOAPMessage(paramSOAPVersion, paramMessage);
  }
  
  public static SOAPMessage read(SOAPVersion paramSOAPVersion, Message paramMessage, Packet paramPacket)
    throws SOAPException
  {
    Iterator localIterator = ServiceFinder.find(SAAJFactory.class).iterator();
    while (localIterator.hasNext())
    {
      SAAJFactory localSAAJFactory = (SAAJFactory)localIterator.next();
      SOAPMessage localSOAPMessage = localSAAJFactory.readAsSOAPMessage(paramSOAPVersion, paramMessage, paramPacket);
      if (localSOAPMessage != null) {
        return localSOAPMessage;
      }
    }
    return instance.readAsSOAPMessage(paramSOAPVersion, paramMessage, paramPacket);
  }
  
  public static SAAJMessage read(Packet paramPacket)
    throws SOAPException
  {
    ServiceFinder localServiceFinder = component != null ? ServiceFinder.find(SAAJFactory.class, component) : ServiceFinder.find(SAAJFactory.class);
    Iterator localIterator = localServiceFinder.iterator();
    while (localIterator.hasNext())
    {
      SAAJFactory localSAAJFactory = (SAAJFactory)localIterator.next();
      SAAJMessage localSAAJMessage = localSAAJFactory.readAsSAAJ(paramPacket);
      if (localSAAJMessage != null) {
        return localSAAJMessage;
      }
    }
    return instance.readAsSAAJ(paramPacket);
  }
  
  public SAAJMessage readAsSAAJ(Packet paramPacket)
    throws SOAPException
  {
    SOAPVersion localSOAPVersion = paramPacket.getMessage().getSOAPVersion();
    SOAPMessage localSOAPMessage = readAsSOAPMessage(localSOAPVersion, paramPacket.getMessage());
    return new SAAJMessage(localSOAPMessage);
  }
  
  public MessageFactory createMessageFactory(String paramString)
    throws SOAPException
  {
    return MessageFactory.newInstance(paramString);
  }
  
  public SOAPFactory createSOAPFactory(String paramString)
    throws SOAPException
  {
    return SOAPFactory.newInstance(paramString);
  }
  
  public Message createMessage(SOAPMessage paramSOAPMessage)
  {
    return new SAAJMessage(paramSOAPMessage);
  }
  
  public SOAPMessage readAsSOAPMessage(SOAPVersion paramSOAPVersion, Message paramMessage)
    throws SOAPException
  {
    SOAPMessage localSOAPMessage = paramSOAPVersion.getMessageFactory().createMessage();
    SaajStaxWriter localSaajStaxWriter = new SaajStaxWriter(localSOAPMessage);
    try
    {
      paramMessage.writeTo(localSaajStaxWriter);
    }
    catch (XMLStreamException localXMLStreamException)
    {
      throw ((localXMLStreamException.getCause() instanceof SOAPException) ? (SOAPException)localXMLStreamException.getCause() : new SOAPException(localXMLStreamException));
    }
    localSOAPMessage = localSaajStaxWriter.getSOAPMessage();
    addAttachmentsToSOAPMessage(localSOAPMessage, paramMessage);
    if (localSOAPMessage.saveRequired()) {
      localSOAPMessage.saveChanges();
    }
    return localSOAPMessage;
  }
  
  public SOAPMessage readAsSOAPMessageSax2Dom(SOAPVersion paramSOAPVersion, Message paramMessage)
    throws SOAPException
  {
    SOAPMessage localSOAPMessage = paramSOAPVersion.getMessageFactory().createMessage();
    SAX2DOMEx localSAX2DOMEx = new SAX2DOMEx(localSOAPMessage.getSOAPPart());
    try
    {
      paramMessage.writeTo(localSAX2DOMEx, XmlUtil.DRACONIAN_ERROR_HANDLER);
    }
    catch (SAXException localSAXException)
    {
      throw new SOAPException(localSAXException);
    }
    addAttachmentsToSOAPMessage(localSOAPMessage, paramMessage);
    if (localSOAPMessage.saveRequired()) {
      localSOAPMessage.saveChanges();
    }
    return localSOAPMessage;
  }
  
  protected static void addAttachmentsToSOAPMessage(SOAPMessage paramSOAPMessage, Message paramMessage)
  {
    Iterator localIterator1 = paramMessage.getAttachments().iterator();
    while (localIterator1.hasNext())
    {
      Attachment localAttachment = (Attachment)localIterator1.next();
      AttachmentPart localAttachmentPart = paramSOAPMessage.createAttachmentPart();
      localAttachmentPart.setDataHandler(localAttachment.asDataHandler());
      String str = localAttachment.getContentId();
      if (str != null) {
        if ((str.startsWith("<")) && (str.endsWith(">"))) {
          localAttachmentPart.setContentId(str);
        } else {
          localAttachmentPart.setContentId('<' + str + '>');
        }
      }
      if ((localAttachment instanceof AttachmentEx))
      {
        AttachmentEx localAttachmentEx = (AttachmentEx)localAttachment;
        Iterator localIterator2 = localAttachmentEx.getMimeHeaders();
        while (localIterator2.hasNext())
        {
          AttachmentEx.MimeHeader localMimeHeader = (AttachmentEx.MimeHeader)localIterator2.next();
          if ((!"Content-ID".equals(localMimeHeader.getName())) && (!"Content-Type".equals(localMimeHeader.getName()))) {
            localAttachmentPart.addMimeHeader(localMimeHeader.getName(), localMimeHeader.getValue());
          }
        }
      }
      paramSOAPMessage.addAttachmentPart(localAttachmentPart);
    }
  }
  
  public SOAPMessage readAsSOAPMessage(SOAPVersion paramSOAPVersion, Message paramMessage, Packet paramPacket)
    throws SOAPException
  {
    return readAsSOAPMessage(paramSOAPVersion, paramMessage);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\message\saaj\SAAJFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */