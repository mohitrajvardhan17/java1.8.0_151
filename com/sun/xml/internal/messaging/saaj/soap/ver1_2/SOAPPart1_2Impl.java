package com.sun.xml.internal.messaging.saaj.soap.ver1_2;

import com.sun.xml.internal.messaging.saaj.soap.Envelope;
import com.sun.xml.internal.messaging.saaj.soap.EnvelopeFactory;
import com.sun.xml.internal.messaging.saaj.soap.MessageImpl;
import com.sun.xml.internal.messaging.saaj.soap.SOAPPartImpl;
import com.sun.xml.internal.messaging.saaj.soap.impl.EnvelopeImpl;
import com.sun.xml.internal.messaging.saaj.util.XMLDeclarationParser;
import java.util.logging.Logger;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPException;
import javax.xml.transform.Source;

public class SOAPPart1_2Impl
  extends SOAPPartImpl
  implements SOAPConstants
{
  protected static final Logger log = Logger.getLogger(SOAPPart1_2Impl.class.getName(), "com.sun.xml.internal.messaging.saaj.soap.ver1_2.LocalStrings");
  
  public SOAPPart1_2Impl() {}
  
  public SOAPPart1_2Impl(MessageImpl paramMessageImpl)
  {
    super(paramMessageImpl);
  }
  
  protected String getContentType()
  {
    return "application/soap+xml";
  }
  
  protected Envelope createEmptyEnvelope(String paramString)
    throws SOAPException
  {
    return new Envelope1_2Impl(getDocument(), paramString, true, true);
  }
  
  protected Envelope createEnvelopeFromSource()
    throws SOAPException
  {
    XMLDeclarationParser localXMLDeclarationParser = lookForXmlDecl();
    Source localSource = source;
    source = null;
    EnvelopeImpl localEnvelopeImpl = (EnvelopeImpl)EnvelopeFactory.createEnvelope(localSource, this);
    if (!localEnvelopeImpl.getNamespaceURI().equals("http://www.w3.org/2003/05/soap-envelope"))
    {
      log.severe("SAAJ0415.ver1_2.msg.invalid.soap1.2");
      throw new SOAPException("InputStream does not represent a valid SOAP 1.2 Message");
    }
    if ((localXMLDeclarationParser != null) && (!omitXmlDecl))
    {
      localEnvelopeImpl.setOmitXmlDecl("no");
      localEnvelopeImpl.setXmlDecl(localXMLDeclarationParser.getXmlDeclaration());
      localEnvelopeImpl.setCharsetEncoding(localXMLDeclarationParser.getEncoding());
    }
    return localEnvelopeImpl;
  }
  
  protected SOAPPartImpl duplicateType()
  {
    return new SOAPPart1_2Impl();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\messaging\saaj\soap\ver1_2\SOAPPart1_2Impl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */