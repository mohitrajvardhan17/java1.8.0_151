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

public final class ToXMLSAXHandler
  extends ToSAXHandler
{
  protected boolean m_escapeSetting = true;
  
  public ToXMLSAXHandler()
  {
    m_prefixMap = new NamespaceMappings();
    initCDATA();
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
    String str1 = getLocalName(m_elemContext.m_elementName);
    String str2 = getNamespaceURI(m_elemContext.m_elementName, true);
    if (m_needToCallStartDocument) {
      startDocumentInternal();
    }
    m_saxHandler.startElement(str2, str1, m_elemContext.m_elementName, m_attributes);
    m_attributes.clear();
    if (m_state != null) {
      m_state.setCurrentNode(null);
    }
  }
  
  public void closeCDATA()
    throws SAXException
  {
    if ((m_lexHandler != null) && (m_cdataTagOpen)) {
      m_lexHandler.endCDATA();
    }
    m_cdataTagOpen = false;
  }
  
  public void endElement(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {
    flushPending();
    if (paramString1 == null) {
      if (m_elemContext.m_elementURI != null) {
        paramString1 = m_elemContext.m_elementURI;
      } else {
        paramString1 = getNamespaceURI(paramString3, true);
      }
    }
    if (paramString2 == null) {
      if (m_elemContext.m_elementLocalName != null) {
        paramString2 = m_elemContext.m_elementLocalName;
      } else {
        paramString2 = getLocalName(paramString3);
      }
    }
    m_saxHandler.endElement(paramString1, paramString2, paramString3);
    if (m_tracer != null) {
      super.fireEndElem(paramString3);
    }
    m_prefixMap.popNamespaces(m_elemContext.m_currentElemDepth, m_saxHandler);
    m_elemContext = m_elemContext.m_prev;
  }
  
  public void endPrefixMapping(String paramString)
    throws SAXException
  {}
  
  public void ignorableWhitespace(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    m_saxHandler.ignorableWhitespace(paramArrayOfChar, paramInt1, paramInt2);
  }
  
  public void setDocumentLocator(Locator paramLocator)
  {
    super.setDocumentLocator(paramLocator);
    m_saxHandler.setDocumentLocator(paramLocator);
  }
  
  public void skippedEntity(String paramString)
    throws SAXException
  {
    m_saxHandler.skippedEntity(paramString);
  }
  
  public void startPrefixMapping(String paramString1, String paramString2)
    throws SAXException
  {
    startPrefixMapping(paramString1, paramString2, true);
  }
  
  public boolean startPrefixMapping(String paramString1, String paramString2, boolean paramBoolean)
    throws SAXException
  {
    int i;
    if (paramBoolean)
    {
      flushPending();
      i = m_elemContext.m_currentElemDepth + 1;
    }
    else
    {
      i = m_elemContext.m_currentElemDepth;
    }
    boolean bool = m_prefixMap.pushNamespace(paramString1, paramString2, i);
    if (bool)
    {
      m_saxHandler.startPrefixMapping(paramString1, paramString2);
      if (getShouldOutputNSAttr())
      {
        String str;
        if ("".equals(paramString1))
        {
          str = "xmlns";
          addAttributeAlways("http://www.w3.org/2000/xmlns/", str, str, "CDATA", paramString2, false);
        }
        else if (!"".equals(paramString2))
        {
          str = "xmlns:" + paramString1;
          addAttributeAlways("http://www.w3.org/2000/xmlns/", paramString1, str, "CDATA", paramString2, false);
        }
      }
    }
    return bool;
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
  {
    if (m_lexHandler != null) {
      m_lexHandler.endDTD();
    }
  }
  
  public void startEntity(String paramString)
    throws SAXException
  {
    if (m_lexHandler != null) {
      m_lexHandler.startEntity(paramString);
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
    characters(m_charsBuff, 0, i);
  }
  
  public ToXMLSAXHandler(ContentHandler paramContentHandler, String paramString)
  {
    super(paramContentHandler, paramString);
    initCDATA();
    m_prefixMap = new NamespaceMappings();
  }
  
  public ToXMLSAXHandler(ContentHandler paramContentHandler, LexicalHandler paramLexicalHandler, String paramString)
  {
    super(paramContentHandler, paramLexicalHandler, paramString);
    initCDATA();
    m_prefixMap = new NamespaceMappings();
  }
  
  public void startElement(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {
    startElement(paramString1, paramString2, paramString3, null);
  }
  
  public void startElement(String paramString)
    throws SAXException
  {
    startElement(null, null, paramString, null);
  }
  
  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2)
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
    if ((m_elemContext.m_isCdataSection) && (!m_cdataTagOpen) && (m_lexHandler != null))
    {
      m_lexHandler.startCDATA();
      m_cdataTagOpen = true;
    }
    m_saxHandler.characters(paramArrayOfChar, paramInt1, paramInt2);
    if (m_tracer != null) {
      fireCharEvent(paramArrayOfChar, paramInt1, paramInt2);
    }
  }
  
  public void endElement(String paramString)
    throws SAXException
  {
    endElement(null, null, paramString);
  }
  
  public void namespaceAfterStartElement(String paramString1, String paramString2)
    throws SAXException
  {
    startPrefixMapping(paramString1, paramString2, false);
  }
  
  public void processingInstruction(String paramString1, String paramString2)
    throws SAXException
  {
    flushPending();
    m_saxHandler.processingInstruction(paramString1, paramString2);
    if (m_tracer != null) {
      super.fireEscapingEvent(paramString1, paramString2);
    }
  }
  
  protected boolean popNamespace(String paramString)
  {
    try
    {
      if (m_prefixMap.popNamespace(paramString))
      {
        m_saxHandler.endPrefixMapping(paramString);
        return true;
      }
    }
    catch (SAXException localSAXException) {}
    return false;
  }
  
  public void startCDATA()
    throws SAXException
  {
    if (!m_cdataTagOpen)
    {
      flushPending();
      if (m_lexHandler != null)
      {
        m_lexHandler.startCDATA();
        m_cdataTagOpen = true;
      }
    }
  }
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
    throws SAXException
  {
    flushPending();
    super.startElement(paramString1, paramString2, paramString3, paramAttributes);
    if (m_needToOutputDocTypeDecl)
    {
      String str1 = getDoctypeSystem();
      if ((str1 != null) && (m_lexHandler != null))
      {
        String str2 = getDoctypePublic();
        if (str1 != null) {
          m_lexHandler.startDTD(paramString3, str2, str1);
        }
      }
      m_needToOutputDocTypeDecl = false;
    }
    m_elemContext = m_elemContext.push(paramString1, paramString2, paramString3);
    if (paramString1 != null) {
      ensurePrefixIsDeclared(paramString1, paramString3);
    }
    if (paramAttributes != null) {
      addAttributes(paramAttributes);
    }
    m_elemContext.m_isCdataSection = isCdataSection();
  }
  
  private void ensurePrefixIsDeclared(String paramString1, String paramString2)
    throws SAXException
  {
    if ((paramString1 != null) && (paramString1.length() > 0))
    {
      int i;
      int j = (i = paramString2.indexOf(":")) < 0 ? 1 : 0;
      String str1 = j != 0 ? "" : paramString2.substring(0, i);
      if (null != str1)
      {
        String str2 = m_prefixMap.lookupNamespace(str1);
        if ((null == str2) || (!str2.equals(paramString1)))
        {
          startPrefixMapping(str1, paramString1, false);
          if (getShouldOutputNSAttr()) {
            addAttributeAlways("http://www.w3.org/2000/xmlns/", j != 0 ? "xmlns" : str1, "xmlns:" + str1, "CDATA", paramString1, false);
          }
        }
      }
    }
  }
  
  public void addAttribute(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, boolean paramBoolean)
    throws SAXException
  {
    if (m_elemContext.m_startTagOpen)
    {
      ensurePrefixIsDeclared(paramString1, paramString3);
      addAttributeAlways(paramString1, paramString2, paramString3, paramString4, paramString5, false);
    }
  }
  
  public boolean reset()
  {
    boolean bool = false;
    if (super.reset())
    {
      resetToXMLSAXHandler();
      bool = true;
    }
    return bool;
  }
  
  private void resetToXMLSAXHandler()
  {
    m_escapeSetting = true;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\serializer\ToXMLSAXHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */