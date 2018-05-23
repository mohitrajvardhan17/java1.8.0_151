package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import com.sun.xml.internal.bind.v2.util.FatalAdapter;
import javax.xml.namespace.NamespaceContext;
import javax.xml.validation.Schema;
import javax.xml.validation.ValidatorHandler;
import org.xml.sax.SAXException;

final class ValidatingUnmarshaller
  implements XmlVisitor, XmlVisitor.TextPredictor
{
  private final XmlVisitor next;
  private final ValidatorHandler validator;
  private NamespaceContext nsContext = null;
  private final XmlVisitor.TextPredictor predictor;
  private char[] buf = new char['Ā'];
  
  public ValidatingUnmarshaller(Schema paramSchema, XmlVisitor paramXmlVisitor)
  {
    validator = paramSchema.newValidatorHandler();
    next = paramXmlVisitor;
    predictor = paramXmlVisitor.getPredictor();
    validator.setErrorHandler(new FatalAdapter(getContext()));
  }
  
  public void startDocument(LocatorEx paramLocatorEx, NamespaceContext paramNamespaceContext)
    throws SAXException
  {
    nsContext = paramNamespaceContext;
    validator.setDocumentLocator(paramLocatorEx);
    validator.startDocument();
    next.startDocument(paramLocatorEx, paramNamespaceContext);
  }
  
  public void endDocument()
    throws SAXException
  {
    nsContext = null;
    validator.endDocument();
    next.endDocument();
  }
  
  public void startElement(TagName paramTagName)
    throws SAXException
  {
    if (nsContext != null)
    {
      String str = paramTagName.getPrefix().intern();
      if (str != "") {
        validator.startPrefixMapping(str, nsContext.getNamespaceURI(str));
      }
    }
    validator.startElement(uri, local, paramTagName.getQname(), atts);
    next.startElement(paramTagName);
  }
  
  public void endElement(TagName paramTagName)
    throws SAXException
  {
    validator.endElement(uri, local, paramTagName.getQname());
    next.endElement(paramTagName);
  }
  
  public void startPrefixMapping(String paramString1, String paramString2)
    throws SAXException
  {
    validator.startPrefixMapping(paramString1, paramString2);
    next.startPrefixMapping(paramString1, paramString2);
  }
  
  public void endPrefixMapping(String paramString)
    throws SAXException
  {
    validator.endPrefixMapping(paramString);
    next.endPrefixMapping(paramString);
  }
  
  public void text(CharSequence paramCharSequence)
    throws SAXException
  {
    int i = paramCharSequence.length();
    if (buf.length < i) {
      buf = new char[i];
    }
    for (int j = 0; j < i; j++) {
      buf[j] = paramCharSequence.charAt(j);
    }
    validator.characters(buf, 0, i);
    if (predictor.expectText()) {
      next.text(paramCharSequence);
    }
  }
  
  public UnmarshallingContext getContext()
  {
    return next.getContext();
  }
  
  public XmlVisitor.TextPredictor getPredictor()
  {
    return this;
  }
  
  @Deprecated
  public boolean expectText()
  {
    return true;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\unmarshaller\ValidatingUnmarshaller.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */