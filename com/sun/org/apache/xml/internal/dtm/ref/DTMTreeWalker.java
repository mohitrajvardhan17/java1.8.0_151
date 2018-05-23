package com.sun.org.apache.xml.internal.dtm.ref;

import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.utils.NodeConsumer;
import com.sun.org.apache.xml.internal.utils.XMLString;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.AttributesImpl;

public class DTMTreeWalker
{
  private ContentHandler m_contentHandler = null;
  protected DTM m_dtm;
  boolean nextIsRaw = false;
  
  public void setDTM(DTM paramDTM)
  {
    m_dtm = paramDTM;
  }
  
  public ContentHandler getcontentHandler()
  {
    return m_contentHandler;
  }
  
  public void setcontentHandler(ContentHandler paramContentHandler)
  {
    m_contentHandler = paramContentHandler;
  }
  
  public DTMTreeWalker() {}
  
  public DTMTreeWalker(ContentHandler paramContentHandler, DTM paramDTM)
  {
    m_contentHandler = paramContentHandler;
    m_dtm = paramDTM;
  }
  
  public void traverse(int paramInt)
    throws SAXException
  {
    int i = paramInt;
    while (-1 != paramInt)
    {
      startNode(paramInt);
      int j = m_dtm.getFirstChild(paramInt);
      while (-1 == j)
      {
        endNode(paramInt);
        if (i != paramInt)
        {
          j = m_dtm.getNextSibling(paramInt);
          if (-1 == j)
          {
            paramInt = m_dtm.getParent(paramInt);
            if ((-1 == paramInt) || (i == paramInt))
            {
              if (-1 != paramInt) {
                endNode(paramInt);
              }
              j = -1;
            }
          }
        }
      }
      paramInt = j;
    }
  }
  
  public void traverse(int paramInt1, int paramInt2)
    throws SAXException
  {
    while (-1 != paramInt1)
    {
      startNode(paramInt1);
      int i = m_dtm.getFirstChild(paramInt1);
      while (-1 == i)
      {
        endNode(paramInt1);
        if ((-1 == paramInt2) || (paramInt2 != paramInt1))
        {
          i = m_dtm.getNextSibling(paramInt1);
          if (-1 == i)
          {
            paramInt1 = m_dtm.getParent(paramInt1);
            if ((-1 == paramInt1) || ((-1 != paramInt2) && (paramInt2 == paramInt1))) {
              i = -1;
            }
          }
        }
      }
      paramInt1 = i;
    }
  }
  
  private final void dispatachChars(int paramInt)
    throws SAXException
  {
    m_dtm.dispatchCharactersEvents(paramInt, m_contentHandler, false);
  }
  
  protected void startNode(int paramInt)
    throws SAXException
  {
    if ((m_contentHandler instanceof NodeConsumer)) {}
    Object localObject1;
    switch (m_dtm.getNodeType(paramInt))
    {
    case 8: 
      localObject1 = m_dtm.getStringValue(paramInt);
      if ((m_contentHandler instanceof LexicalHandler))
      {
        LexicalHandler localLexicalHandler = (LexicalHandler)m_contentHandler;
        ((XMLString)localObject1).dispatchAsComment(localLexicalHandler);
      }
      break;
    case 11: 
      break;
    case 9: 
      m_contentHandler.startDocument();
      break;
    case 1: 
      localObject1 = m_dtm;
      for (int i = ((DTM)localObject1).getFirstNamespaceNode(paramInt, true); -1 != i; i = ((DTM)localObject1).getNextNamespaceNode(paramInt, i, true))
      {
        localObject2 = ((DTM)localObject1).getNodeNameX(i);
        m_contentHandler.startPrefixMapping((String)localObject2, ((DTM)localObject1).getNodeValue(i));
      }
      String str1 = ((DTM)localObject1).getNamespaceURI(paramInt);
      if (null == str1) {
        str1 = "";
      }
      Object localObject2 = new AttributesImpl();
      for (int j = ((DTM)localObject1).getFirstAttribute(paramInt); j != -1; j = ((DTM)localObject1).getNextAttribute(j)) {
        ((AttributesImpl)localObject2).addAttribute(((DTM)localObject1).getNamespaceURI(j), ((DTM)localObject1).getLocalName(j), ((DTM)localObject1).getNodeName(j), "CDATA", ((DTM)localObject1).getNodeValue(j));
      }
      m_contentHandler.startElement(str1, m_dtm.getLocalName(paramInt), m_dtm.getNodeName(paramInt), (Attributes)localObject2);
      break;
    case 7: 
      String str2 = m_dtm.getNodeName(paramInt);
      if (str2.equals("xslt-next-is-raw")) {
        nextIsRaw = true;
      } else {
        m_contentHandler.processingInstruction(str2, m_dtm.getNodeValue(paramInt));
      }
      break;
    case 4: 
      boolean bool = m_contentHandler instanceof LexicalHandler;
      Object localObject3 = bool ? (LexicalHandler)m_contentHandler : null;
      if (bool) {
        ((LexicalHandler)localObject3).startCDATA();
      }
      dispatachChars(paramInt);
      if (bool) {
        ((LexicalHandler)localObject3).endCDATA();
      }
      break;
    case 3: 
      if (nextIsRaw)
      {
        nextIsRaw = false;
        m_contentHandler.processingInstruction("javax.xml.transform.disable-output-escaping", "");
        dispatachChars(paramInt);
        m_contentHandler.processingInstruction("javax.xml.transform.enable-output-escaping", "");
      }
      else
      {
        dispatachChars(paramInt);
      }
      break;
    case 5: 
      if ((m_contentHandler instanceof LexicalHandler)) {
        ((LexicalHandler)m_contentHandler).startEntity(m_dtm.getNodeName(paramInt));
      }
      break;
    }
  }
  
  protected void endNode(int paramInt)
    throws SAXException
  {
    switch (m_dtm.getNodeType(paramInt))
    {
    case 9: 
      m_contentHandler.endDocument();
      break;
    case 1: 
      String str1 = m_dtm.getNamespaceURI(paramInt);
      if (null == str1) {
        str1 = "";
      }
      m_contentHandler.endElement(str1, m_dtm.getLocalName(paramInt), m_dtm.getNodeName(paramInt));
      for (int i = m_dtm.getFirstNamespaceNode(paramInt, true); -1 != i; i = m_dtm.getNextNamespaceNode(paramInt, i, true))
      {
        String str2 = m_dtm.getNodeNameX(i);
        m_contentHandler.endPrefixMapping(str2);
      }
      break;
    case 4: 
      break;
    case 5: 
      if ((m_contentHandler instanceof LexicalHandler))
      {
        LexicalHandler localLexicalHandler = (LexicalHandler)m_contentHandler;
        localLexicalHandler.endEntity(m_dtm.getNodeName(paramInt));
      }
      break;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\dtm\ref\DTMTreeWalker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */