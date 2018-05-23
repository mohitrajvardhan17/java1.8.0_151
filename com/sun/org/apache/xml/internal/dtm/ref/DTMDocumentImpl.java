package com.sun.org.apache.xml.internal.dtm.ref;

import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.dtm.DTMAxisTraverser;
import com.sun.org.apache.xml.internal.dtm.DTMManager;
import com.sun.org.apache.xml.internal.dtm.DTMWSFilter;
import com.sun.org.apache.xml.internal.utils.FastStringBuffer;
import com.sun.org.apache.xml.internal.utils.XMLString;
import com.sun.org.apache.xml.internal.utils.XMLStringFactory;
import java.io.PrintStream;
import javax.xml.transform.SourceLocator;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;

public class DTMDocumentImpl
  implements DTM, ContentHandler, LexicalHandler
{
  protected static final byte DOCHANDLE_SHIFT = 22;
  protected static final int NODEHANDLE_MASK = 8388607;
  protected static final int DOCHANDLE_MASK = -8388608;
  int m_docHandle = -1;
  int m_docElement = -1;
  int currentParent = 0;
  int previousSibling = 0;
  protected int m_currentNode = -1;
  private boolean previousSiblingWasParent = false;
  int[] gotslot = new int[4];
  private boolean done = false;
  boolean m_isError = false;
  private final boolean DEBUG = false;
  protected String m_documentBaseURI;
  private IncrementalSAXSource m_incrSAXSource = null;
  ChunkedIntArray nodes = new ChunkedIntArray(4);
  private FastStringBuffer m_char = new FastStringBuffer();
  private int m_char_current_start = 0;
  private DTMStringPool m_localNames = new DTMStringPool();
  private DTMStringPool m_nsNames = new DTMStringPool();
  private DTMStringPool m_prefixNames = new DTMStringPool();
  private ExpandedNameTable m_expandedNames = new ExpandedNameTable();
  private XMLStringFactory m_xsf;
  private static final String[] fixednames = { null, null, null, "#text", "#cdata_section", null, null, null, "#comment", "#document", null, "#document-fragment", null };
  
  public DTMDocumentImpl(DTMManager paramDTMManager, int paramInt, DTMWSFilter paramDTMWSFilter, XMLStringFactory paramXMLStringFactory)
  {
    initDocument(paramInt);
    m_xsf = paramXMLStringFactory;
  }
  
  public void setIncrementalSAXSource(IncrementalSAXSource paramIncrementalSAXSource)
  {
    m_incrSAXSource = paramIncrementalSAXSource;
    paramIncrementalSAXSource.setContentHandler(this);
    paramIncrementalSAXSource.setLexicalHandler(this);
  }
  
  private final int appendNode(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int i = nodes.appendSlot(paramInt1, paramInt2, paramInt3, paramInt4);
    if (previousSiblingWasParent) {
      nodes.writeEntry(previousSibling, 2, i);
    }
    previousSiblingWasParent = false;
    return i;
  }
  
  public void setFeature(String paramString, boolean paramBoolean) {}
  
  public void setLocalNameTable(DTMStringPool paramDTMStringPool)
  {
    m_localNames = paramDTMStringPool;
  }
  
  public DTMStringPool getLocalNameTable()
  {
    return m_localNames;
  }
  
  public void setNsNameTable(DTMStringPool paramDTMStringPool)
  {
    m_nsNames = paramDTMStringPool;
  }
  
  public DTMStringPool getNsNameTable()
  {
    return m_nsNames;
  }
  
  public void setPrefixNameTable(DTMStringPool paramDTMStringPool)
  {
    m_prefixNames = paramDTMStringPool;
  }
  
  public DTMStringPool getPrefixNameTable()
  {
    return m_prefixNames;
  }
  
  void setContentBuffer(FastStringBuffer paramFastStringBuffer)
  {
    m_char = paramFastStringBuffer;
  }
  
  FastStringBuffer getContentBuffer()
  {
    return m_char;
  }
  
  public ContentHandler getContentHandler()
  {
    if ((m_incrSAXSource instanceof IncrementalSAXSource_Filter)) {
      return (ContentHandler)m_incrSAXSource;
    }
    return this;
  }
  
  public LexicalHandler getLexicalHandler()
  {
    if ((m_incrSAXSource instanceof IncrementalSAXSource_Filter)) {
      return (LexicalHandler)m_incrSAXSource;
    }
    return this;
  }
  
  public EntityResolver getEntityResolver()
  {
    return null;
  }
  
  public DTDHandler getDTDHandler()
  {
    return null;
  }
  
  public ErrorHandler getErrorHandler()
  {
    return null;
  }
  
  public DeclHandler getDeclHandler()
  {
    return null;
  }
  
  public boolean needsTwoThreads()
  {
    return null != m_incrSAXSource;
  }
  
  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    m_char.append(paramArrayOfChar, paramInt1, paramInt2);
  }
  
  private void processAccumulatedText()
  {
    int i = m_char.length();
    if (i != m_char_current_start)
    {
      appendTextChild(m_char_current_start, i - m_char_current_start);
      m_char_current_start = i;
    }
  }
  
  public void endDocument()
    throws SAXException
  {
    appendEndDocument();
  }
  
  public void endElement(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {
    processAccumulatedText();
    appendEndElement();
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
    processAccumulatedText();
  }
  
  public void setDocumentLocator(Locator paramLocator) {}
  
  public void skippedEntity(String paramString)
    throws SAXException
  {
    processAccumulatedText();
  }
  
  public void startDocument()
    throws SAXException
  {
    appendStartDocument();
  }
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
    throws SAXException
  {
    processAccumulatedText();
    String str = null;
    int i = paramString3.indexOf(':');
    if (i > 0) {
      str = paramString3.substring(0, i);
    }
    System.out.println("Prefix=" + str + " index=" + m_prefixNames.stringToIndex(str));
    appendStartElement(m_nsNames.stringToIndex(paramString1), m_localNames.stringToIndex(paramString2), m_prefixNames.stringToIndex(str));
    int j = paramAttributes == null ? 0 : paramAttributes.getLength();
    for (int k = j - 1; k >= 0; k--)
    {
      paramString3 = paramAttributes.getQName(k);
      if ((paramString3.startsWith("xmlns:")) || ("xmlns".equals(paramString3)))
      {
        str = null;
        i = paramString3.indexOf(':');
        if (i > 0) {
          str = paramString3.substring(0, i);
        } else {
          str = null;
        }
        appendNSDeclaration(m_prefixNames.stringToIndex(str), m_nsNames.stringToIndex(paramAttributes.getValue(k)), paramAttributes.getType(k).equalsIgnoreCase("ID"));
      }
    }
    for (k = j - 1; k >= 0; k--)
    {
      paramString3 = paramAttributes.getQName(k);
      if ((!paramString3.startsWith("xmlns:")) && (!"xmlns".equals(paramString3)))
      {
        str = null;
        i = paramString3.indexOf(':');
        if (i > 0)
        {
          str = paramString3.substring(0, i);
          paramString2 = paramString3.substring(i + 1);
        }
        else
        {
          str = "";
          paramString2 = paramString3;
        }
        m_char.append(paramAttributes.getValue(k));
        int m = m_char.length();
        if ((!"xmlns".equals(str)) && (!"xmlns".equals(paramString3))) {
          appendAttribute(m_nsNames.stringToIndex(paramAttributes.getURI(k)), m_localNames.stringToIndex(paramString2), m_prefixNames.stringToIndex(str), paramAttributes.getType(k).equalsIgnoreCase("ID"), m_char_current_start, m - m_char_current_start);
        }
        m_char_current_start = m;
      }
    }
  }
  
  public void startPrefixMapping(String paramString1, String paramString2)
    throws SAXException
  {}
  
  public void comment(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    processAccumulatedText();
    m_char.append(paramArrayOfChar, paramInt1, paramInt2);
    appendComment(m_char_current_start, paramInt2);
    m_char_current_start += paramInt2;
  }
  
  public void endCDATA()
    throws SAXException
  {}
  
  public void endDTD()
    throws SAXException
  {}
  
  public void endEntity(String paramString)
    throws SAXException
  {}
  
  public void startCDATA()
    throws SAXException
  {}
  
  public void startDTD(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {}
  
  public void startEntity(String paramString)
    throws SAXException
  {}
  
  final void initDocument(int paramInt)
  {
    m_docHandle = (paramInt << 22);
    nodes.writeSlot(0, 9, -1, -1, 0);
    done = false;
  }
  
  public boolean hasChildNodes(int paramInt)
  {
    return getFirstChild(paramInt) != -1;
  }
  
  public int getFirstChild(int paramInt)
  {
    paramInt &= 0x7FFFFF;
    nodes.readSlot(paramInt, gotslot);
    int i = (short)(gotslot[0] & 0xFFFF);
    if ((i == 1) || (i == 9) || (i == 5))
    {
      int j = paramInt + 1;
      nodes.readSlot(j, gotslot);
      while (2 == (gotslot[0] & 0xFFFF))
      {
        j = gotslot[2];
        if (j == -1) {
          return -1;
        }
        nodes.readSlot(j, gotslot);
      }
      if (gotslot[1] == paramInt)
      {
        int k = j | m_docHandle;
        return k;
      }
    }
    return -1;
  }
  
  public int getLastChild(int paramInt)
  {
    paramInt &= 0x7FFFFF;
    int i = -1;
    for (int j = getFirstChild(paramInt); j != -1; j = getNextSibling(j)) {
      i = j;
    }
    return i | m_docHandle;
  }
  
  public int getAttributeNode(int paramInt, String paramString1, String paramString2)
  {
    int i = m_nsNames.stringToIndex(paramString1);
    int j = m_localNames.stringToIndex(paramString2);
    paramInt &= 0x7FFFFF;
    nodes.readSlot(paramInt, gotslot);
    int k = (short)(gotslot[0] & 0xFFFF);
    if (k == 1) {
      paramInt++;
    }
    while (k == 2)
    {
      if ((i == gotslot[0] << 16) && (gotslot[3] == j)) {
        return paramInt | m_docHandle;
      }
      paramInt = gotslot[2];
      nodes.readSlot(paramInt, gotslot);
    }
    return -1;
  }
  
  public int getFirstAttribute(int paramInt)
  {
    paramInt &= 0x7FFFFF;
    if (1 != (nodes.readEntry(paramInt, 0) & 0xFFFF)) {
      return -1;
    }
    paramInt++;
    return 2 == (nodes.readEntry(paramInt, 0) & 0xFFFF) ? paramInt | m_docHandle : -1;
  }
  
  public int getFirstNamespaceNode(int paramInt, boolean paramBoolean)
  {
    return -1;
  }
  
  public int getNextSibling(int paramInt)
  {
    paramInt &= 0x7FFFFF;
    if (paramInt == 0) {
      return -1;
    }
    int i = (short)(nodes.readEntry(paramInt, 0) & 0xFFFF);
    if ((i == 1) || (i == 2) || (i == 5))
    {
      j = nodes.readEntry(paramInt, 2);
      if (j == -1) {
        return -1;
      }
      if (j != 0) {
        return m_docHandle | j;
      }
    }
    int j = nodes.readEntry(paramInt, 1);
    if (nodes.readEntry(++paramInt, 1) == j) {
      return m_docHandle | paramInt;
    }
    return -1;
  }
  
  public int getPreviousSibling(int paramInt)
  {
    paramInt &= 0x7FFFFF;
    if (paramInt == 0) {
      return -1;
    }
    int i = nodes.readEntry(paramInt, 1);
    int j = -1;
    for (int k = getFirstChild(i); k != paramInt; k = getNextSibling(k)) {
      j = k;
    }
    return j | m_docHandle;
  }
  
  public int getNextAttribute(int paramInt)
  {
    paramInt &= 0x7FFFFF;
    nodes.readSlot(paramInt, gotslot);
    int i = (short)(gotslot[0] & 0xFFFF);
    if (i == 1) {
      return getFirstAttribute(paramInt);
    }
    if ((i == 2) && (gotslot[2] != -1)) {
      return m_docHandle | gotslot[2];
    }
    return -1;
  }
  
  public int getNextNamespaceNode(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    return -1;
  }
  
  public int getNextDescendant(int paramInt1, int paramInt2)
  {
    paramInt1 &= 0x7FFFFF;
    paramInt2 &= 0x7FFFFF;
    if (paramInt2 == 0) {
      return -1;
    }
    while ((!m_isError) && ((!done) || (paramInt2 <= nodes.slotsUsed()))) {
      if (paramInt2 > paramInt1)
      {
        nodes.readSlot(paramInt2 + 1, gotslot);
        if (gotslot[2] != 0)
        {
          int i = (short)(gotslot[0] & 0xFFFF);
          if (i == 2)
          {
            paramInt2 += 2;
          }
          else
          {
            int j = gotslot[1];
            if (j < paramInt1) {
              break;
            }
            return m_docHandle | paramInt2 + 1;
          }
        }
        else
        {
          if (done) {
            break;
          }
        }
      }
      else
      {
        paramInt2++;
      }
    }
    return -1;
  }
  
  public int getNextFollowing(int paramInt1, int paramInt2)
  {
    return -1;
  }
  
  public int getNextPreceding(int paramInt1, int paramInt2)
  {
    paramInt2 &= 0x7FFFFF;
    while (paramInt2 > 1)
    {
      paramInt2--;
      if (2 != (nodes.readEntry(paramInt2, 0) & 0xFFFF)) {
        return m_docHandle | nodes.specialFind(paramInt1, paramInt2);
      }
    }
    return -1;
  }
  
  public int getParent(int paramInt)
  {
    return m_docHandle | nodes.readEntry(paramInt, 1);
  }
  
  public int getDocumentRoot()
  {
    return m_docHandle | m_docElement;
  }
  
  public int getDocument()
  {
    return m_docHandle;
  }
  
  public int getOwnerDocument(int paramInt)
  {
    if ((paramInt & 0x7FFFFF) == 0) {
      return -1;
    }
    return paramInt & 0xFF800000;
  }
  
  public int getDocumentRoot(int paramInt)
  {
    if ((paramInt & 0x7FFFFF) == 0) {
      return -1;
    }
    return paramInt & 0xFF800000;
  }
  
  public XMLString getStringValue(int paramInt)
  {
    nodes.readSlot(paramInt, gotslot);
    int i = gotslot[0] & 0xFF;
    String str = null;
    switch (i)
    {
    case 3: 
    case 4: 
    case 8: 
      str = m_char.getString(gotslot[2], gotslot[3]);
      break;
    }
    return m_xsf.newstr(str);
  }
  
  public int getStringValueChunkCount(int paramInt)
  {
    return 0;
  }
  
  public char[] getStringValueChunk(int paramInt1, int paramInt2, int[] paramArrayOfInt)
  {
    return new char[0];
  }
  
  public int getExpandedTypeID(int paramInt)
  {
    nodes.readSlot(paramInt, gotslot);
    String str1 = m_localNames.indexToString(gotslot[3]);
    int i = str1.indexOf(":");
    String str2 = str1.substring(i + 1);
    String str3 = m_nsNames.indexToString(gotslot[0] << 16);
    String str4 = str3 + ":" + str2;
    int j = m_nsNames.stringToIndex(str4);
    return j;
  }
  
  public int getExpandedTypeID(String paramString1, String paramString2, int paramInt)
  {
    String str = paramString1 + ":" + paramString2;
    int i = m_nsNames.stringToIndex(str);
    return i;
  }
  
  public String getLocalNameFromExpandedNameID(int paramInt)
  {
    String str1 = m_localNames.indexToString(paramInt);
    int i = str1.indexOf(":");
    String str2 = str1.substring(i + 1);
    return str2;
  }
  
  public String getNamespaceFromExpandedNameID(int paramInt)
  {
    String str1 = m_localNames.indexToString(paramInt);
    int i = str1.indexOf(":");
    String str2 = str1.substring(0, i);
    return str2;
  }
  
  public String getNodeName(int paramInt)
  {
    nodes.readSlot(paramInt, gotslot);
    int i = (short)(gotslot[0] & 0xFFFF);
    String str1 = fixednames[i];
    if (null == str1)
    {
      int j = gotslot[3];
      System.out.println("got i=" + j + " " + (j >> 16) + "/" + (j & 0xFFFF));
      str1 = m_localNames.indexToString(j & 0xFFFF);
      String str2 = m_prefixNames.indexToString(j >> 16);
      if ((str2 != null) && (str2.length() > 0)) {
        str1 = str2 + ":" + str1;
      }
    }
    return str1;
  }
  
  public String getNodeNameX(int paramInt)
  {
    return null;
  }
  
  public String getLocalName(int paramInt)
  {
    nodes.readSlot(paramInt, gotslot);
    int i = (short)(gotslot[0] & 0xFFFF);
    String str = "";
    if ((i == 1) || (i == 2))
    {
      int j = gotslot[3];
      str = m_localNames.indexToString(j & 0xFFFF);
      if (str == null) {
        str = "";
      }
    }
    return str;
  }
  
  public String getPrefix(int paramInt)
  {
    nodes.readSlot(paramInt, gotslot);
    int i = (short)(gotslot[0] & 0xFFFF);
    String str = "";
    if ((i == 1) || (i == 2))
    {
      int j = gotslot[3];
      str = m_prefixNames.indexToString(j >> 16);
      if (str == null) {
        str = "";
      }
    }
    return str;
  }
  
  public String getNamespaceURI(int paramInt)
  {
    return null;
  }
  
  public String getNodeValue(int paramInt)
  {
    nodes.readSlot(paramInt, gotslot);
    int i = gotslot[0] & 0xFF;
    String str = null;
    switch (i)
    {
    case 2: 
      nodes.readSlot(paramInt + 1, gotslot);
    case 3: 
    case 4: 
    case 8: 
      str = m_char.getString(gotslot[2], gotslot[3]);
      break;
    }
    return str;
  }
  
  public short getNodeType(int paramInt)
  {
    return (short)(nodes.readEntry(paramInt, 0) & 0xFFFF);
  }
  
  public short getLevel(int paramInt)
  {
    short s = 0;
    while (paramInt != 0)
    {
      s = (short)(s + 1);
      paramInt = nodes.readEntry(paramInt, 1);
    }
    return s;
  }
  
  public boolean isSupported(String paramString1, String paramString2)
  {
    return false;
  }
  
  public String getDocumentBaseURI()
  {
    return m_documentBaseURI;
  }
  
  public void setDocumentBaseURI(String paramString)
  {
    m_documentBaseURI = paramString;
  }
  
  public String getDocumentSystemIdentifier(int paramInt)
  {
    return null;
  }
  
  public String getDocumentEncoding(int paramInt)
  {
    return null;
  }
  
  public String getDocumentStandalone(int paramInt)
  {
    return null;
  }
  
  public String getDocumentVersion(int paramInt)
  {
    return null;
  }
  
  public boolean getDocumentAllDeclarationsProcessed()
  {
    return false;
  }
  
  public String getDocumentTypeDeclarationSystemIdentifier()
  {
    return null;
  }
  
  public String getDocumentTypeDeclarationPublicIdentifier()
  {
    return null;
  }
  
  public int getElementById(String paramString)
  {
    return 0;
  }
  
  public String getUnparsedEntityURI(String paramString)
  {
    return null;
  }
  
  public boolean supportsPreStripping()
  {
    return false;
  }
  
  public boolean isNodeAfter(int paramInt1, int paramInt2)
  {
    return false;
  }
  
  public boolean isCharacterElementContentWhitespace(int paramInt)
  {
    return false;
  }
  
  public boolean isDocumentAllDeclarationsProcessed(int paramInt)
  {
    return false;
  }
  
  public boolean isAttributeSpecified(int paramInt)
  {
    return false;
  }
  
  public void dispatchCharactersEvents(int paramInt, ContentHandler paramContentHandler, boolean paramBoolean)
    throws SAXException
  {}
  
  public void dispatchToEvents(int paramInt, ContentHandler paramContentHandler)
    throws SAXException
  {}
  
  public Node getNode(int paramInt)
  {
    return null;
  }
  
  public void appendChild(int paramInt, boolean paramBoolean1, boolean paramBoolean2)
  {
    int i = (paramInt & 0xFF800000) == m_docHandle ? 1 : 0;
    if ((!paramBoolean1) && (i == 0)) {}
  }
  
  public void appendTextChild(String paramString) {}
  
  void appendTextChild(int paramInt1, int paramInt2)
  {
    int i = 3;
    int j = currentParent;
    int k = paramInt1;
    int m = paramInt2;
    int n = appendNode(i, j, k, m);
    previousSibling = n;
  }
  
  void appendComment(int paramInt1, int paramInt2)
  {
    int i = 8;
    int j = currentParent;
    int k = paramInt1;
    int m = paramInt2;
    int n = appendNode(i, j, k, m);
    previousSibling = n;
  }
  
  void appendStartElement(int paramInt1, int paramInt2, int paramInt3)
  {
    int i = paramInt1 << 16 | 0x1;
    int j = currentParent;
    int k = 0;
    int m = paramInt2 | paramInt3 << 16;
    System.out.println("set w3=" + m + " " + (m >> 16) + "/" + (m & 0xFFFF));
    int n = appendNode(i, j, k, m);
    currentParent = n;
    previousSibling = 0;
    if (m_docElement == -1) {
      m_docElement = n;
    }
  }
  
  void appendNSDeclaration(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    int i = m_nsNames.stringToIndex("http://www.w3.org/2000/xmlns/");
    int j = 0xD | m_nsNames.stringToIndex("http://www.w3.org/2000/xmlns/") << 16;
    int k = currentParent;
    int m = 0;
    int n = paramInt2;
    int i1 = appendNode(j, k, m, n);
    previousSibling = i1;
    previousSiblingWasParent = false;
  }
  
  void appendAttribute(int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean, int paramInt4, int paramInt5)
  {
    int i = 0x2 | paramInt1 << 16;
    int j = currentParent;
    int k = 0;
    int m = paramInt2 | paramInt3 << 16;
    System.out.println("set w3=" + m + " " + (m >> 16) + "/" + (m & 0xFFFF));
    int n = appendNode(i, j, k, m);
    previousSibling = n;
    i = 3;
    j = n;
    k = paramInt4;
    m = paramInt5;
    appendNode(i, j, k, m);
    previousSiblingWasParent = true;
  }
  
  public DTMAxisTraverser getAxisTraverser(int paramInt)
  {
    return null;
  }
  
  public DTMAxisIterator getAxisIterator(int paramInt)
  {
    return null;
  }
  
  public DTMAxisIterator getTypedAxisIterator(int paramInt1, int paramInt2)
  {
    return null;
  }
  
  void appendEndElement()
  {
    if (previousSiblingWasParent) {
      nodes.writeEntry(previousSibling, 2, -1);
    }
    previousSibling = currentParent;
    nodes.readSlot(currentParent, gotslot);
    currentParent = (gotslot[1] & 0xFFFF);
    previousSiblingWasParent = true;
  }
  
  void appendStartDocument()
  {
    m_docElement = -1;
    initDocument(0);
  }
  
  void appendEndDocument()
  {
    done = true;
  }
  
  public void setProperty(String paramString, Object paramObject) {}
  
  public SourceLocator getSourceLocatorFor(int paramInt)
  {
    return null;
  }
  
  public void documentRegistration() {}
  
  public void documentRelease() {}
  
  public void migrateTo(DTMManager paramDTMManager) {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\dtm\ref\DTMDocumentImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */