package com.sun.org.apache.xml.internal.serializer;

import java.util.Vector;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ext.LexicalHandler;

public abstract class ToSAXHandler
  extends SerializerBase
{
  protected ContentHandler m_saxHandler;
  protected LexicalHandler m_lexHandler;
  private boolean m_shouldGenerateNSAttribute = true;
  protected TransformStateSetter m_state = null;
  
  public ToSAXHandler() {}
  
  public ToSAXHandler(ContentHandler paramContentHandler, LexicalHandler paramLexicalHandler, String paramString)
  {
    setContentHandler(paramContentHandler);
    setLexHandler(paramLexicalHandler);
    setEncoding(paramString);
  }
  
  public ToSAXHandler(ContentHandler paramContentHandler, String paramString)
  {
    setContentHandler(paramContentHandler);
    setEncoding(paramString);
  }
  
  protected void startDocumentInternal()
    throws SAXException
  {
    if (m_needToCallStartDocument)
    {
      super.startDocumentInternal();
      m_saxHandler.startDocument();
      m_needToCallStartDocument = false;
    }
  }
  
  public void startDTD(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {}
  
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
  
  public void comment(String paramString)
    throws SAXException
  {
    flushPending();
    if (m_lexHandler != null)
    {
      int i = paramString.length();
      if (i > m_charsBuff.length) {
        m_charsBuff = new char[i * 2 + 1];
      }
      paramString.getChars(0, i, m_charsBuff, 0);
      m_lexHandler.comment(m_charsBuff, 0, i);
      if (m_tracer != null) {
        super.fireCommentEvent(m_charsBuff, 0, i);
      }
    }
  }
  
  public void processingInstruction(String paramString1, String paramString2)
    throws SAXException
  {}
  
  protected void closeStartTag()
    throws SAXException
  {}
  
  protected void closeCDATA()
    throws SAXException
  {}
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
    throws SAXException
  {
    if (m_state != null) {
      m_state.resetState(getTransformer());
    }
    if (m_tracer != null) {
      super.fireStartElem(paramString3);
    }
  }
  
  public void setLexHandler(LexicalHandler paramLexicalHandler)
  {
    m_lexHandler = paramLexicalHandler;
  }
  
  public void setContentHandler(ContentHandler paramContentHandler)
  {
    m_saxHandler = paramContentHandler;
    if ((m_lexHandler == null) && ((paramContentHandler instanceof LexicalHandler))) {
      m_lexHandler = ((LexicalHandler)paramContentHandler);
    }
  }
  
  public void setCdataSectionElements(Vector paramVector) {}
  
  public void setShouldOutputNSAttr(boolean paramBoolean)
  {
    m_shouldGenerateNSAttribute = paramBoolean;
  }
  
  boolean getShouldOutputNSAttr()
  {
    return m_shouldGenerateNSAttribute;
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
    if (m_cdataTagOpen)
    {
      closeCDATA();
      m_cdataTagOpen = false;
    }
  }
  
  public void setTransformState(TransformStateSetter paramTransformStateSetter)
  {
    m_state = paramTransformStateSetter;
  }
  
  public void startElement(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {
    if (m_state != null) {
      m_state.resetState(getTransformer());
    }
    if (m_tracer != null) {
      super.fireStartElem(paramString3);
    }
  }
  
  public void startElement(String paramString)
    throws SAXException
  {
    if (m_state != null) {
      m_state.resetState(getTransformer());
    }
    if (m_tracer != null) {
      super.fireStartElem(paramString);
    }
  }
  
  public void characters(Node paramNode)
    throws SAXException
  {
    if (m_state != null) {
      m_state.setCurrentNode(paramNode);
    }
    String str = paramNode.getNodeValue();
    if (str != null) {
      characters(str);
    }
  }
  
  public void fatalError(SAXParseException paramSAXParseException)
    throws SAXException
  {
    super.fatalError(paramSAXParseException);
    m_needToCallStartDocument = false;
    if ((m_saxHandler instanceof ErrorHandler)) {
      ((ErrorHandler)m_saxHandler).fatalError(paramSAXParseException);
    }
  }
  
  public void error(SAXParseException paramSAXParseException)
    throws SAXException
  {
    super.error(paramSAXParseException);
    if ((m_saxHandler instanceof ErrorHandler)) {
      ((ErrorHandler)m_saxHandler).error(paramSAXParseException);
    }
  }
  
  public void warning(SAXParseException paramSAXParseException)
    throws SAXException
  {
    super.warning(paramSAXParseException);
    if ((m_saxHandler instanceof ErrorHandler)) {
      ((ErrorHandler)m_saxHandler).warning(paramSAXParseException);
    }
  }
  
  public boolean reset()
  {
    boolean bool = false;
    if (super.reset())
    {
      resetToSAXHandler();
      bool = true;
    }
    return bool;
  }
  
  private void resetToSAXHandler()
  {
    m_lexHandler = null;
    m_saxHandler = null;
    m_state = null;
    m_shouldGenerateNSAttribute = false;
  }
  
  public void addUniqueAttribute(String paramString1, String paramString2, int paramInt)
    throws SAXException
  {
    addAttribute(paramString1, paramString2);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\serializer\ToSAXHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */