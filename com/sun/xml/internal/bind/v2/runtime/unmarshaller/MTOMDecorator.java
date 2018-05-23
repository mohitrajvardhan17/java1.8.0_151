package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import javax.activation.DataHandler;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.attachment.AttachmentUnmarshaller;
import javax.xml.namespace.NamespaceContext;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

final class MTOMDecorator
  implements XmlVisitor
{
  private final XmlVisitor next;
  private final AttachmentUnmarshaller au;
  private UnmarshallerImpl parent;
  private final Base64Data base64data = new Base64Data();
  private boolean inXopInclude;
  private boolean followXop;
  
  public MTOMDecorator(UnmarshallerImpl paramUnmarshallerImpl, XmlVisitor paramXmlVisitor, AttachmentUnmarshaller paramAttachmentUnmarshaller)
  {
    parent = paramUnmarshallerImpl;
    next = paramXmlVisitor;
    au = paramAttachmentUnmarshaller;
  }
  
  public void startDocument(LocatorEx paramLocatorEx, NamespaceContext paramNamespaceContext)
    throws SAXException
  {
    next.startDocument(paramLocatorEx, paramNamespaceContext);
  }
  
  public void endDocument()
    throws SAXException
  {
    next.endDocument();
  }
  
  public void startElement(TagName paramTagName)
    throws SAXException
  {
    if ((local.equals("Include")) && (uri.equals("http://www.w3.org/2004/08/xop/include")))
    {
      String str = atts.getValue("href");
      DataHandler localDataHandler = au.getAttachmentAsDataHandler(str);
      if (localDataHandler == null) {
        parent.getEventHandler().handleEvent(null);
      }
      base64data.set(localDataHandler);
      next.text(base64data);
      inXopInclude = true;
      followXop = true;
    }
    else
    {
      next.startElement(paramTagName);
    }
  }
  
  public void endElement(TagName paramTagName)
    throws SAXException
  {
    if (inXopInclude)
    {
      inXopInclude = false;
      followXop = true;
      return;
    }
    next.endElement(paramTagName);
  }
  
  public void startPrefixMapping(String paramString1, String paramString2)
    throws SAXException
  {
    next.startPrefixMapping(paramString1, paramString2);
  }
  
  public void endPrefixMapping(String paramString)
    throws SAXException
  {
    next.endPrefixMapping(paramString);
  }
  
  public void text(CharSequence paramCharSequence)
    throws SAXException
  {
    if (!followXop) {
      next.text(paramCharSequence);
    } else {
      followXop = false;
    }
  }
  
  public UnmarshallingContext getContext()
  {
    return next.getContext();
  }
  
  public XmlVisitor.TextPredictor getPredictor()
  {
    return next.getPredictor();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\unmarshaller\MTOMDecorator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */