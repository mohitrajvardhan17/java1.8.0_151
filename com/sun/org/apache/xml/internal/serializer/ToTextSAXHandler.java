package com.sun.org.apache.xml.internal.serializer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Properties;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;

public final class ToTextSAXHandler
  extends ToSAXHandler
{
  public void endElement(String paramString)
    throws SAXException
  {
    if (m_tracer != null) {
      super.fireEndElem(paramString);
    }
  }
  
  public void endElement(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {
    if (m_tracer != null) {
      super.fireEndElem(paramString3);
    }
  }
  
  public ToTextSAXHandler(ContentHandler paramContentHandler, LexicalHandler paramLexicalHandler, String paramString)
  {
    super(paramContentHandler, paramLexicalHandler, paramString);
  }
  
  public ToTextSAXHandler(ContentHandler paramContentHandler, String paramString)
  {
    super(paramContentHandler, paramString);
  }
  
  public void comment(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    if (m_tracer != null) {
      super.fireCommentEvent(paramArrayOfChar, paramInt1, paramInt2);
    }
  }
  
  public void comment(String paramString)
    throws SAXException
  {
    int i = paramString.length();
    if (i > m_charsBuff.length) {
      m_charsBuff = new char[i * 2 + 1];
    }
    paramString.getChars(0, i, m_charsBuff, 0);
    comment(m_charsBuff, 0, i);
  }
  
  public Properties getOutputFormat()
  {
    return null;
  }
  
  public OutputStream getOutputStream()
  {
    return null;
  }
  
  public Writer getWriter()
  {
    return null;
  }
  
  public void indent(int paramInt)
    throws SAXException
  {}
  
  public boolean reset()
  {
    return false;
  }
  
  public void serialize(Node paramNode)
    throws IOException
  {}
  
  public boolean setEscaping(boolean paramBoolean)
  {
    return false;
  }
  
  public void setIndent(boolean paramBoolean) {}
  
  public void setOutputFormat(Properties paramProperties) {}
  
  public void setOutputStream(OutputStream paramOutputStream) {}
  
  public void setWriter(Writer paramWriter) {}
  
  public void addAttribute(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, boolean paramBoolean) {}
  
  public void attributeDecl(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5)
    throws SAXException
  {}
  
  public void elementDecl(String paramString1, String paramString2)
    throws SAXException
  {}
  
  public void externalEntityDecl(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {}
  
  public void internalEntityDecl(String paramString1, String paramString2)
    throws SAXException
  {}
  
  public void endPrefixMapping(String paramString)
    throws SAXException
  {}
  
  public void ignorableWhitespace(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {}
  
  public void processingInstruction(String paramString1, String paramString2)
    throws SAXException
  {
    if (m_tracer != null) {
      super.fireEscapingEvent(paramString1, paramString2);
    }
  }
  
  public void setDocumentLocator(Locator paramLocator)
  {
    super.setDocumentLocator(paramLocator);
  }
  
  public void skippedEntity(String paramString)
    throws SAXException
  {}
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
    throws SAXException
  {
    flushPending();
    super.startElement(paramString1, paramString2, paramString3, paramAttributes);
  }
  
  public void endCDATA()
    throws SAXException
  {}
  
  public void endDTD()
    throws SAXException
  {}
  
  public void startCDATA()
    throws SAXException
  {}
  
  public void startEntity(String paramString)
    throws SAXException
  {}
  
  public void startElement(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {
    super.startElement(paramString1, paramString2, paramString3);
  }
  
  public void startElement(String paramString)
    throws SAXException
  {
    super.startElement(paramString);
  }
  
  public void endDocument()
    throws SAXException
  {
    flushPending();
    m_saxHandler.endDocument();
    if (m_tracer != null) {
      super.fireEndDoc();
    }
  }
  
  public void characters(String paramString)
    throws SAXException
  {
    int i = paramString.length();
    if (i > m_charsBuff.length) {
      m_charsBuff = new char[i * 2 + 1];
    }
    paramString.getChars(0, i, m_charsBuff, 0);
    m_saxHandler.characters(m_charsBuff, 0, i);
  }
  
  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    m_saxHandler.characters(paramArrayOfChar, paramInt1, paramInt2);
    if (m_tracer != null) {
      super.fireCharEvent(paramArrayOfChar, paramInt1, paramInt2);
    }
  }
  
  public void addAttribute(String paramString1, String paramString2) {}
  
  public boolean startPrefixMapping(String paramString1, String paramString2, boolean paramBoolean)
    throws SAXException
  {
    return false;
  }
  
  public void startPrefixMapping(String paramString1, String paramString2)
    throws SAXException
  {}
  
  public void namespaceAfterStartElement(String paramString1, String paramString2)
    throws SAXException
  {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\serializer\ToTextSAXHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */