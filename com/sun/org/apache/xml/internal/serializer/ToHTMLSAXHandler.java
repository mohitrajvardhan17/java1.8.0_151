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

public final class ToHTMLSAXHandler
  extends ToSAXHandler
{
  private boolean m_dtdHandled = false;
  protected boolean m_escapeSetting = true;
  
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
  
  public void serialize(Node paramNode)
    throws IOException
  {}
  
  public boolean setEscaping(boolean paramBoolean)
    throws SAXException
  {
    boolean bool = m_escapeSetting;
    m_escapeSetting = paramBoolean;
    if (paramBoolean) {
      processingInstruction("javax.xml.transform.enable-output-escaping", "");
    } else {
      processingInstruction("javax.xml.transform.disable-output-escaping", "");
    }
    return bool;
  }
  
  public void setIndent(boolean paramBoolean) {}
  
  public void setOutputFormat(Properties paramProperties) {}
  
  public void setOutputStream(OutputStream paramOutputStream) {}
  
  public void setWriter(Writer paramWriter) {}
  
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
  
  public void endElement(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {
    flushPending();
    m_saxHandler.endElement(paramString1, paramString2, paramString3);
    if (m_tracer != null) {
      super.fireEndElem(paramString3);
    }
  }
  
  public void endPrefixMapping(String paramString)
    throws SAXException
  {}
  
  public void ignorableWhitespace(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {}
  
  public void processingInstruction(String paramString1, String paramString2)
    throws SAXException
  {
    flushPending();
    m_saxHandler.processingInstruction(paramString1, paramString2);
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
    m_saxHandler.startElement(paramString1, paramString2, paramString3, paramAttributes);
    m_elemContext.m_startTagOpen = false;
  }
  
  public void comment(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    flushPending();
    if (m_lexHandler != null) {
      m_lexHandler.comment(paramArrayOfChar, paramInt1, paramInt2);
    }
    if (m_tracer != null) {
      super.fireCommentEvent(paramArrayOfChar, paramInt1, paramInt2);
    }
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
  
  public void endDocument()
    throws SAXException
  {
    flushPending();
    m_saxHandler.endDocument();
    if (m_tracer != null) {
      super.fireEndDoc();
    }
  }
  
  protected void closeStartTag()
    throws SAXException
  {
    m_elemContext.m_startTagOpen = false;
    m_saxHandler.startElement("", m_elemContext.m_elementName, m_elemContext.m_elementName, m_attributes);
    m_attributes.clear();
  }
  
  public void close() {}
  
  public void characters(String paramString)
    throws SAXException
  {
    int i = paramString.length();
    if (i > m_charsBuff.length) {
      m_charsBuff = new char[i * 2 + 1];
    }
    paramString.getChars(0, i, m_charsBuff, 0);
    characters(m_charsBuff, 0, i);
  }
  
  public ToHTMLSAXHandler(ContentHandler paramContentHandler, String paramString)
  {
    super(paramContentHandler, paramString);
  }
  
  public ToHTMLSAXHandler(ContentHandler paramContentHandler, LexicalHandler paramLexicalHandler, String paramString)
  {
    super(paramContentHandler, paramLexicalHandler, paramString);
  }
  
  public void startElement(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {
    super.startElement(paramString1, paramString2, paramString3);
    flushPending();
    if (!m_dtdHandled)
    {
      String str1 = getDoctypeSystem();
      String str2 = getDoctypePublic();
      if (((str1 != null) || (str2 != null)) && (m_lexHandler != null)) {
        m_lexHandler.startDTD(paramString3, str2, str1);
      }
      m_dtdHandled = true;
    }
    m_elemContext = m_elemContext.push(paramString1, paramString2, paramString3);
  }
  
  public void startElement(String paramString)
    throws SAXException
  {
    startElement(null, null, paramString);
  }
  
  public void endElement(String paramString)
    throws SAXException
  {
    flushPending();
    m_saxHandler.endElement("", paramString, paramString);
    if (m_tracer != null) {
      super.fireEndElem(paramString);
    }
  }
  
  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    flushPending();
    m_saxHandler.characters(paramArrayOfChar, paramInt1, paramInt2);
    if (m_tracer != null) {
      super.fireCharEvent(paramArrayOfChar, paramInt1, paramInt2);
    }
  }
  
  public void flushPending()
    throws SAXException
  {
    if (m_needToCallStartDocument)
    {
      startDocumentInternal();
      m_needToCallStartDocument = false;
    }
    if (m_elemContext.m_startTagOpen)
    {
      closeStartTag();
      m_elemContext.m_startTagOpen = false;
    }
  }
  
  public boolean startPrefixMapping(String paramString1, String paramString2, boolean paramBoolean)
    throws SAXException
  {
    if (paramBoolean) {
      flushPending();
    }
    m_saxHandler.startPrefixMapping(paramString1, paramString2);
    return false;
  }
  
  public void startPrefixMapping(String paramString1, String paramString2)
    throws SAXException
  {
    startPrefixMapping(paramString1, paramString2, true);
  }
  
  public void namespaceAfterStartElement(String paramString1, String paramString2)
    throws SAXException
  {
    if (m_elemContext.m_elementURI == null)
    {
      String str = getPrefixPart(m_elemContext.m_elementName);
      if ((str == null) && ("".equals(paramString1))) {
        m_elemContext.m_elementURI = paramString2;
      }
    }
    startPrefixMapping(paramString1, paramString2, false);
  }
  
  public boolean reset()
  {
    boolean bool = false;
    if (super.reset())
    {
      resetToHTMLSAXHandler();
      bool = true;
    }
    return bool;
  }
  
  private void resetToHTMLSAXHandler()
  {
    m_escapeSetting = true;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\serializer\ToHTMLSAXHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */