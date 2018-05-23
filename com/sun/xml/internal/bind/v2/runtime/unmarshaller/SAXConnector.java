package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import com.sun.xml.internal.bind.Util;
import com.sun.xml.internal.bind.WhiteSpaceProcessor;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshallerHandler;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public final class SAXConnector
  implements UnmarshallerHandler
{
  private LocatorEx loc;
  private static final Logger logger = ;
  private final StringBuilder buffer = new StringBuilder();
  private final XmlVisitor next;
  private final UnmarshallingContext context;
  private final XmlVisitor.TextPredictor predictor;
  private final TagNameImpl tagName = new TagNameImpl(null);
  
  public SAXConnector(XmlVisitor paramXmlVisitor, LocatorEx paramLocatorEx)
  {
    next = paramXmlVisitor;
    context = paramXmlVisitor.getContext();
    predictor = paramXmlVisitor.getPredictor();
    loc = paramLocatorEx;
  }
  
  public Object getResult()
    throws JAXBException, IllegalStateException
  {
    return context.getResult();
  }
  
  public UnmarshallingContext getContext()
  {
    return context;
  }
  
  public void setDocumentLocator(Locator paramLocator)
  {
    if (loc != null) {
      return;
    }
    loc = new LocatorExWrapper(paramLocator);
  }
  
  public void startDocument()
    throws SAXException
  {
    if (logger.isLoggable(Level.FINER)) {
      logger.log(Level.FINER, "SAXConnector.startDocument");
    }
    next.startDocument(loc, null);
  }
  
  public void endDocument()
    throws SAXException
  {
    if (logger.isLoggable(Level.FINER)) {
      logger.log(Level.FINER, "SAXConnector.endDocument");
    }
    next.endDocument();
  }
  
  public void startPrefixMapping(String paramString1, String paramString2)
    throws SAXException
  {
    if (logger.isLoggable(Level.FINER)) {
      logger.log(Level.FINER, "SAXConnector.startPrefixMapping: {0}:{1}", new Object[] { paramString1, paramString2 });
    }
    next.startPrefixMapping(paramString1, paramString2);
  }
  
  public void endPrefixMapping(String paramString)
    throws SAXException
  {
    if (logger.isLoggable(Level.FINER)) {
      logger.log(Level.FINER, "SAXConnector.endPrefixMapping: {0}", new Object[] { paramString });
    }
    next.endPrefixMapping(paramString);
  }
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
    throws SAXException
  {
    if (logger.isLoggable(Level.FINER)) {
      logger.log(Level.FINER, "SAXConnector.startElement: {0}:{1}:{2}, attrs: {3}", new Object[] { paramString1, paramString2, paramString3, paramAttributes });
    }
    if ((paramString1 == null) || (paramString1.length() == 0)) {
      paramString1 = "";
    }
    if ((paramString2 == null) || (paramString2.length() == 0)) {
      paramString2 = paramString3;
    }
    if ((paramString3 == null) || (paramString3.length() == 0)) {
      paramString3 = paramString2;
    }
    processText(!context.getCurrentState().isMixed());
    tagName.uri = paramString1;
    tagName.local = paramString2;
    tagName.qname = paramString3;
    tagName.atts = paramAttributes;
    next.startElement(tagName);
  }
  
  public void endElement(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {
    if (logger.isLoggable(Level.FINER)) {
      logger.log(Level.FINER, "SAXConnector.startElement: {0}:{1}:{2}", new Object[] { paramString1, paramString2, paramString3 });
    }
    processText(false);
    tagName.uri = paramString1;
    tagName.local = paramString2;
    tagName.qname = paramString3;
    next.endElement(tagName);
  }
  
  public final void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    if (logger.isLoggable(Level.FINEST)) {
      logger.log(Level.FINEST, "SAXConnector.characters: {0}", paramArrayOfChar);
    }
    if (predictor.expectText()) {
      buffer.append(paramArrayOfChar, paramInt1, paramInt2);
    }
  }
  
  public final void ignorableWhitespace(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    if (logger.isLoggable(Level.FINEST)) {
      logger.log(Level.FINEST, "SAXConnector.characters{0}", paramArrayOfChar);
    }
    characters(paramArrayOfChar, paramInt1, paramInt2);
  }
  
  public void processingInstruction(String paramString1, String paramString2) {}
  
  public void skippedEntity(String paramString) {}
  
  private void processText(boolean paramBoolean)
    throws SAXException
  {
    if ((predictor.expectText()) && ((!paramBoolean) || (!WhiteSpaceProcessor.isWhiteSpace(buffer)))) {
      next.text(buffer);
    }
    buffer.setLength(0);
  }
  
  private static final class TagNameImpl
    extends TagName
  {
    String qname;
    
    private TagNameImpl() {}
    
    public String getQname()
    {
      return qname;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\unmarshaller\SAXConnector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */