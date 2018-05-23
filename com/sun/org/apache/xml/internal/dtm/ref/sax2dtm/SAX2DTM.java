package com.sun.org.apache.xml.internal.dtm.ref.sax2dtm;

import com.sun.org.apache.xml.internal.dtm.DTMManager;
import com.sun.org.apache.xml.internal.dtm.DTMWSFilter;
import com.sun.org.apache.xml.internal.dtm.ref.DTMDefaultBaseIterators;
import com.sun.org.apache.xml.internal.dtm.ref.DTMManagerDefault;
import com.sun.org.apache.xml.internal.dtm.ref.DTMStringPool;
import com.sun.org.apache.xml.internal.dtm.ref.DTMTreeWalker;
import com.sun.org.apache.xml.internal.dtm.ref.ExpandedNameTable;
import com.sun.org.apache.xml.internal.dtm.ref.IncrementalSAXSource;
import com.sun.org.apache.xml.internal.dtm.ref.NodeLocator;
import com.sun.org.apache.xml.internal.res.XMLMessages;
import com.sun.org.apache.xml.internal.utils.FastStringBuffer;
import com.sun.org.apache.xml.internal.utils.IntStack;
import com.sun.org.apache.xml.internal.utils.IntVector;
import com.sun.org.apache.xml.internal.utils.StringVector;
import com.sun.org.apache.xml.internal.utils.SuballocatedIntVector;
import com.sun.org.apache.xml.internal.utils.SystemIDResolver;
import com.sun.org.apache.xml.internal.utils.WrappedRuntimeException;
import com.sun.org.apache.xml.internal.utils.XMLString;
import com.sun.org.apache.xml.internal.utils.XMLStringFactory;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import javax.xml.transform.Source;
import javax.xml.transform.SourceLocator;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;

public class SAX2DTM
  extends DTMDefaultBaseIterators
  implements EntityResolver, DTDHandler, ContentHandler, ErrorHandler, DeclHandler, LexicalHandler
{
  private static final boolean DEBUG = false;
  private IncrementalSAXSource m_incrementalSAXSource = null;
  protected FastStringBuffer m_chars;
  protected SuballocatedIntVector m_data;
  protected transient IntStack m_parents;
  protected transient int m_previous = 0;
  protected transient Vector m_prefixMappings = new Vector();
  protected transient IntStack m_contextIndexes;
  protected transient int m_textType = 3;
  protected transient int m_coalescedTextType = 3;
  protected transient Locator m_locator = null;
  private transient String m_systemId = null;
  protected transient boolean m_insideDTD = false;
  protected DTMTreeWalker m_walker = new DTMTreeWalker();
  protected DTMStringPool m_valuesOrPrefixes;
  protected boolean m_endDocumentOccured = false;
  protected SuballocatedIntVector m_dataOrQName;
  protected Map<String, Integer> m_idAttributes = new HashMap();
  private static final String[] m_fixednames = { null, null, null, "#text", "#cdata_section", null, null, null, "#comment", "#document", null, "#document-fragment", null };
  private Vector m_entities = null;
  private static final int ENTITY_FIELD_PUBLICID = 0;
  private static final int ENTITY_FIELD_SYSTEMID = 1;
  private static final int ENTITY_FIELD_NOTATIONNAME = 2;
  private static final int ENTITY_FIELD_NAME = 3;
  private static final int ENTITY_FIELDS_PER = 4;
  protected int m_textPendingStart = -1;
  protected boolean m_useSourceLocationProperty = false;
  protected StringVector m_sourceSystemId;
  protected IntVector m_sourceLine;
  protected IntVector m_sourceColumn;
  boolean m_pastFirstElement = false;
  
  public SAX2DTM(DTMManager paramDTMManager, Source paramSource, int paramInt, DTMWSFilter paramDTMWSFilter, XMLStringFactory paramXMLStringFactory, boolean paramBoolean)
  {
    this(paramDTMManager, paramSource, paramInt, paramDTMWSFilter, paramXMLStringFactory, paramBoolean, 512, true, false);
  }
  
  public SAX2DTM(DTMManager paramDTMManager, Source paramSource, int paramInt1, DTMWSFilter paramDTMWSFilter, XMLStringFactory paramXMLStringFactory, boolean paramBoolean1, int paramInt2, boolean paramBoolean2, boolean paramBoolean3)
  {
    super(paramDTMManager, paramSource, paramInt1, paramDTMWSFilter, paramXMLStringFactory, paramBoolean1, paramInt2, paramBoolean2, paramBoolean3);
    if (paramInt2 <= 64)
    {
      m_data = new SuballocatedIntVector(paramInt2, 4);
      m_dataOrQName = new SuballocatedIntVector(paramInt2, 4);
      m_valuesOrPrefixes = new DTMStringPool(16);
      m_chars = new FastStringBuffer(7, 10);
      m_contextIndexes = new IntStack(4);
      m_parents = new IntStack(4);
    }
    else
    {
      m_data = new SuballocatedIntVector(paramInt2, 32);
      m_dataOrQName = new SuballocatedIntVector(paramInt2, 32);
      m_valuesOrPrefixes = new DTMStringPool();
      m_chars = new FastStringBuffer(10, 13);
      m_contextIndexes = new IntStack();
      m_parents = new IntStack();
    }
    m_data.addElement(0);
    m_useSourceLocationProperty = paramDTMManager.getSource_location();
    m_sourceSystemId = (m_useSourceLocationProperty ? new StringVector() : null);
    m_sourceLine = (m_useSourceLocationProperty ? new IntVector() : null);
    m_sourceColumn = (m_useSourceLocationProperty ? new IntVector() : null);
  }
  
  public void setUseSourceLocation(boolean paramBoolean)
  {
    m_useSourceLocationProperty = paramBoolean;
  }
  
  protected int _dataOrQName(int paramInt)
  {
    if (paramInt < m_size) {
      return m_dataOrQName.elementAt(paramInt);
    }
    for (;;)
    {
      boolean bool = nextNode();
      if (!bool) {
        return -1;
      }
      if (paramInt < m_size) {
        return m_dataOrQName.elementAt(paramInt);
      }
    }
  }
  
  public void clearCoRoutine()
  {
    clearCoRoutine(true);
  }
  
  public void clearCoRoutine(boolean paramBoolean)
  {
    if (null != m_incrementalSAXSource)
    {
      if (paramBoolean) {
        m_incrementalSAXSource.deliverMoreNodes(false);
      }
      m_incrementalSAXSource = null;
    }
  }
  
  public void setIncrementalSAXSource(IncrementalSAXSource paramIncrementalSAXSource)
  {
    m_incrementalSAXSource = paramIncrementalSAXSource;
    paramIncrementalSAXSource.setContentHandler(this);
    paramIncrementalSAXSource.setLexicalHandler(this);
    paramIncrementalSAXSource.setDTDHandler(this);
  }
  
  public ContentHandler getContentHandler()
  {
    if (m_incrementalSAXSource.getClass().getName().equals("com.sun.org.apache.xml.internal.dtm.ref.IncrementalSAXSource_Filter")) {
      return (ContentHandler)m_incrementalSAXSource;
    }
    return this;
  }
  
  public LexicalHandler getLexicalHandler()
  {
    if (m_incrementalSAXSource.getClass().getName().equals("com.sun.org.apache.xml.internal.dtm.ref.IncrementalSAXSource_Filter")) {
      return (LexicalHandler)m_incrementalSAXSource;
    }
    return this;
  }
  
  public EntityResolver getEntityResolver()
  {
    return this;
  }
  
  public DTDHandler getDTDHandler()
  {
    return this;
  }
  
  public ErrorHandler getErrorHandler()
  {
    return this;
  }
  
  public DeclHandler getDeclHandler()
  {
    return this;
  }
  
  public boolean needsTwoThreads()
  {
    return null != m_incrementalSAXSource;
  }
  
  public void dispatchCharactersEvents(int paramInt, ContentHandler paramContentHandler, boolean paramBoolean)
    throws SAXException
  {
    int i = makeNodeIdentity(paramInt);
    if (i == -1) {
      return;
    }
    int j = _type(i);
    int k;
    int m;
    int n;
    if (isTextType(j))
    {
      k = m_dataOrQName.elementAt(i);
      m = m_data.elementAt(k);
      n = m_data.elementAt(k + 1);
      if (paramBoolean) {
        m_chars.sendNormalizedSAXcharacters(paramContentHandler, m, n);
      } else {
        m_chars.sendSAXcharacters(paramContentHandler, m, n);
      }
    }
    else
    {
      k = _firstch(i);
      if (-1 != k)
      {
        m = -1;
        n = 0;
        int i1 = i;
        i = k;
        do
        {
          j = _type(i);
          if (isTextType(j))
          {
            int i2 = _dataOrQName(i);
            if (-1 == m) {
              m = m_data.elementAt(i2);
            }
            n += m_data.elementAt(i2 + 1);
          }
          i = getNextNodeIdentity(i);
        } while ((-1 != i) && (_parent(i) >= i1));
        if (n > 0) {
          if (paramBoolean) {
            m_chars.sendNormalizedSAXcharacters(paramContentHandler, m, n);
          } else {
            m_chars.sendSAXcharacters(paramContentHandler, m, n);
          }
        }
      }
      else if (j != 1)
      {
        m = _dataOrQName(i);
        if (m < 0)
        {
          m = -m;
          m = m_data.elementAt(m + 1);
        }
        String str = m_valuesOrPrefixes.indexToString(m);
        if (paramBoolean) {
          FastStringBuffer.sendNormalizedSAXcharacters(str.toCharArray(), 0, str.length(), paramContentHandler);
        } else {
          paramContentHandler.characters(str.toCharArray(), 0, str.length());
        }
      }
    }
  }
  
  public String getNodeName(int paramInt)
  {
    int i = getExpandedTypeID(paramInt);
    int j = m_expandedNameTable.getNamespaceID(i);
    if (0 == j)
    {
      k = getNodeType(paramInt);
      if (k == 13)
      {
        if (null == m_expandedNameTable.getLocalName(i)) {
          return "xmlns";
        }
        return "xmlns:" + m_expandedNameTable.getLocalName(i);
      }
      if (0 == m_expandedNameTable.getLocalNameID(i)) {
        return m_fixednames[k];
      }
      return m_expandedNameTable.getLocalName(i);
    }
    int k = m_dataOrQName.elementAt(makeNodeIdentity(paramInt));
    if (k < 0)
    {
      k = -k;
      k = m_data.elementAt(k);
    }
    return m_valuesOrPrefixes.indexToString(k);
  }
  
  public String getNodeNameX(int paramInt)
  {
    int i = getExpandedTypeID(paramInt);
    int j = m_expandedNameTable.getNamespaceID(i);
    if (0 == j)
    {
      String str = m_expandedNameTable.getLocalName(i);
      if (str == null) {
        return "";
      }
      return str;
    }
    int k = m_dataOrQName.elementAt(makeNodeIdentity(paramInt));
    if (k < 0)
    {
      k = -k;
      k = m_data.elementAt(k);
    }
    return m_valuesOrPrefixes.indexToString(k);
  }
  
  public boolean isAttributeSpecified(int paramInt)
  {
    return true;
  }
  
  public String getDocumentTypeDeclarationSystemIdentifier()
  {
    error(XMLMessages.createXMLMessage("ER_METHOD_NOT_SUPPORTED", null));
    return null;
  }
  
  protected int getNextNodeIdentity(int paramInt)
  {
    
    while (paramInt >= m_size)
    {
      if (null == m_incrementalSAXSource) {
        return -1;
      }
      nextNode();
    }
    return paramInt;
  }
  
  public void dispatchToEvents(int paramInt, ContentHandler paramContentHandler)
    throws SAXException
  {
    DTMTreeWalker localDTMTreeWalker = m_walker;
    ContentHandler localContentHandler = localDTMTreeWalker.getcontentHandler();
    if (null != localContentHandler) {
      localDTMTreeWalker = new DTMTreeWalker();
    }
    localDTMTreeWalker.setcontentHandler(paramContentHandler);
    localDTMTreeWalker.setDTM(this);
    try
    {
      localDTMTreeWalker.traverse(paramInt);
    }
    finally
    {
      localDTMTreeWalker.setcontentHandler(null);
    }
  }
  
  public int getNumberOfNodes()
  {
    return m_size;
  }
  
  protected boolean nextNode()
  {
    if (null == m_incrementalSAXSource) {
      return false;
    }
    if (m_endDocumentOccured)
    {
      clearCoRoutine();
      return false;
    }
    Object localObject = m_incrementalSAXSource.deliverMoreNodes(true);
    if (!(localObject instanceof Boolean))
    {
      if ((localObject instanceof RuntimeException)) {
        throw ((RuntimeException)localObject);
      }
      if ((localObject instanceof Exception)) {
        throw new WrappedRuntimeException((Exception)localObject);
      }
      clearCoRoutine();
      return false;
    }
    if (localObject != Boolean.TRUE) {
      clearCoRoutine();
    }
    return true;
  }
  
  private final boolean isTextType(int paramInt)
  {
    return (3 == paramInt) || (4 == paramInt);
  }
  
  protected int addNode(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, boolean paramBoolean)
  {
    int i = m_size++;
    if (m_dtmIdent.size() == i >>> 16) {
      addNewDTMID(i);
    }
    m_firstch.addElement(paramBoolean ? -2 : -1);
    m_nextsib.addElement(-2);
    m_parent.addElement(paramInt3);
    m_exptype.addElement(paramInt2);
    m_dataOrQName.addElement(paramInt5);
    if (m_prevsib != null) {
      m_prevsib.addElement(paramInt4);
    }
    if (-1 != paramInt4) {
      m_nextsib.setElementAt(i, paramInt4);
    }
    if ((m_locator != null) && (m_useSourceLocationProperty)) {
      setSourceLocation();
    }
    switch (paramInt1)
    {
    case 13: 
      declareNamespaceInContext(paramInt3, i);
      break;
    case 2: 
      break;
    default: 
      if ((-1 == paramInt4) && (-1 != paramInt3)) {
        m_firstch.setElementAt(i, paramInt3);
      }
      break;
    }
    return i;
  }
  
  protected void addNewDTMID(int paramInt)
  {
    try
    {
      if (m_mgr == null) {
        throw new ClassCastException();
      }
      DTMManagerDefault localDTMManagerDefault = (DTMManagerDefault)m_mgr;
      int i = localDTMManagerDefault.getFirstFreeDTMID();
      localDTMManagerDefault.addDTM(this, i, paramInt);
      m_dtmIdent.addElement(i << 16);
    }
    catch (ClassCastException localClassCastException)
    {
      error(XMLMessages.createXMLMessage("ER_NO_DTMIDS_AVAIL", null));
    }
  }
  
  public void migrateTo(DTMManager paramDTMManager)
  {
    super.migrateTo(paramDTMManager);
    int i = m_dtmIdent.size();
    int j = m_mgrDefault.getFirstFreeDTMID();
    int k = 0;
    for (int m = 0; m < i; m++)
    {
      m_dtmIdent.setElementAt(j << 16, m);
      m_mgrDefault.addDTM(this, j, k);
      j++;
      k += 65536;
    }
  }
  
  protected void setSourceLocation()
  {
    m_sourceSystemId.addElement(m_locator.getSystemId());
    m_sourceLine.addElement(m_locator.getLineNumber());
    m_sourceColumn.addElement(m_locator.getColumnNumber());
    if (m_sourceSystemId.size() != m_size)
    {
      String str = "CODING ERROR in Source Location: " + m_size + " != " + m_sourceSystemId.size();
      System.err.println(str);
      throw new RuntimeException(str);
    }
  }
  
  public String getNodeValue(int paramInt)
  {
    int i = makeNodeIdentity(paramInt);
    int j = _type(i);
    if (isTextType(j))
    {
      k = _dataOrQName(i);
      int m = m_data.elementAt(k);
      int n = m_data.elementAt(k + 1);
      return m_chars.getString(m, n);
    }
    if ((1 == j) || (11 == j) || (9 == j)) {
      return null;
    }
    int k = _dataOrQName(i);
    if (k < 0)
    {
      k = -k;
      k = m_data.elementAt(k + 1);
    }
    return m_valuesOrPrefixes.indexToString(k);
  }
  
  public String getLocalName(int paramInt)
  {
    return m_expandedNameTable.getLocalName(_exptype(makeNodeIdentity(paramInt)));
  }
  
  public String getUnparsedEntityURI(String paramString)
  {
    String str1 = "";
    if (null == m_entities) {
      return str1;
    }
    int i = m_entities.size();
    for (int j = 0; j < i; j += 4)
    {
      String str2 = (String)m_entities.elementAt(j + 3);
      if ((null != str2) && (str2.equals(paramString)))
      {
        String str3 = (String)m_entities.elementAt(j + 2);
        if (null == str3) {
          break;
        }
        str1 = (String)m_entities.elementAt(j + 1);
        if (null != str1) {
          break;
        }
        str1 = (String)m_entities.elementAt(j + 0);
        break;
      }
    }
    return str1;
  }
  
  public String getPrefix(int paramInt)
  {
    int i = makeNodeIdentity(paramInt);
    int j = _type(i);
    int k;
    String str;
    if (1 == j)
    {
      k = _dataOrQName(i);
      if (0 == k) {
        return "";
      }
      str = m_valuesOrPrefixes.indexToString(k);
      return getPrefix(str, null);
    }
    if (2 == j)
    {
      k = _dataOrQName(i);
      if (k < 0)
      {
        k = m_data.elementAt(-k);
        str = m_valuesOrPrefixes.indexToString(k);
        return getPrefix(str, null);
      }
    }
    return "";
  }
  
  public int getAttributeNode(int paramInt, String paramString1, String paramString2)
  {
    for (int i = getFirstAttribute(paramInt); -1 != i; i = getNextAttribute(i))
    {
      String str1 = getNamespaceURI(i);
      String str2 = getLocalName(i);
      int j = (paramString1 == str1) || ((paramString1 != null) && (paramString1.equals(str1))) ? 1 : 0;
      if ((j != 0) && (paramString2.equals(str2))) {
        return i;
      }
    }
    return -1;
  }
  
  public String getDocumentTypeDeclarationPublicIdentifier()
  {
    error(XMLMessages.createXMLMessage("ER_METHOD_NOT_SUPPORTED", null));
    return null;
  }
  
  public String getNamespaceURI(int paramInt)
  {
    return m_expandedNameTable.getNamespace(_exptype(makeNodeIdentity(paramInt)));
  }
  
  public XMLString getStringValue(int paramInt)
  {
    int i = makeNodeIdentity(paramInt);
    int j;
    if (i == -1) {
      j = -1;
    } else {
      j = _type(i);
    }
    int m;
    int n;
    if (isTextType(j))
    {
      k = _dataOrQName(i);
      m = m_data.elementAt(k);
      n = m_data.elementAt(k + 1);
      return m_xstrf.newstr(m_chars, m, n);
    }
    int k = _firstch(i);
    if (-1 != k)
    {
      m = -1;
      n = 0;
      int i1 = i;
      i = k;
      do
      {
        j = _type(i);
        if (isTextType(j))
        {
          int i2 = _dataOrQName(i);
          if (-1 == m) {
            m = m_data.elementAt(i2);
          }
          n += m_data.elementAt(i2 + 1);
        }
        i = getNextNodeIdentity(i);
      } while ((-1 != i) && (_parent(i) >= i1));
      if (n > 0) {
        return m_xstrf.newstr(m_chars, m, n);
      }
    }
    else if (j != 1)
    {
      m = _dataOrQName(i);
      if (m < 0)
      {
        m = -m;
        m = m_data.elementAt(m + 1);
      }
      return m_xstrf.newstr(m_valuesOrPrefixes.indexToString(m));
    }
    return m_xstrf.emptystr();
  }
  
  public boolean isWhitespace(int paramInt)
  {
    int i = makeNodeIdentity(paramInt);
    int j;
    if (i == -1) {
      j = -1;
    } else {
      j = _type(i);
    }
    if (isTextType(j))
    {
      int k = _dataOrQName(i);
      int m = m_data.elementAt(k);
      int n = m_data.elementAt(k + 1);
      return m_chars.isWhitespace(m, n);
    }
    return false;
  }
  
  public int getElementById(String paramString)
  {
    boolean bool = true;
    Integer localInteger;
    do
    {
      localInteger = (Integer)m_idAttributes.get(paramString);
      if (null != localInteger) {
        return makeNodeHandle(localInteger.intValue());
      }
      if ((!bool) || (m_endDocumentOccured)) {
        break;
      }
      bool = nextNode();
    } while (null == localInteger);
    return -1;
  }
  
  public String getPrefix(String paramString1, String paramString2)
  {
    int i = -1;
    String str;
    int j;
    if ((null != paramString2) && (paramString2.length() > 0))
    {
      do
      {
        i = m_prefixMappings.indexOf(paramString2, ++i);
      } while ((i & 0x1) == 0);
      if (i >= 0)
      {
        str = (String)m_prefixMappings.elementAt(i - 1);
      }
      else if (null != paramString1)
      {
        j = paramString1.indexOf(':');
        if (paramString1.equals("xmlns")) {
          str = "";
        } else if (paramString1.startsWith("xmlns:")) {
          str = paramString1.substring(j + 1);
        } else {
          str = j > 0 ? paramString1.substring(0, j) : null;
        }
      }
      else
      {
        str = null;
      }
    }
    else if (null != paramString1)
    {
      j = paramString1.indexOf(':');
      if (j > 0)
      {
        if (paramString1.startsWith("xmlns:")) {
          str = paramString1.substring(j + 1);
        } else {
          str = paramString1.substring(0, j);
        }
      }
      else if (paramString1.equals("xmlns")) {
        str = "";
      } else {
        str = null;
      }
    }
    else
    {
      str = null;
    }
    return str;
  }
  
  public int getIdForNamespace(String paramString)
  {
    return m_valuesOrPrefixes.stringToIndex(paramString);
  }
  
  public String getNamespaceURI(String paramString)
  {
    String str = "";
    int i = m_contextIndexes.peek() - 1;
    if (null == paramString) {
      paramString = "";
    }
    do
    {
      i = m_prefixMappings.indexOf(paramString, ++i);
    } while ((i >= 0) && ((i & 0x1) == 1));
    if (i > -1) {
      str = (String)m_prefixMappings.elementAt(i + 1);
    }
    return str;
  }
  
  public void setIDAttribute(String paramString, int paramInt)
  {
    m_idAttributes.put(paramString, Integer.valueOf(paramInt));
  }
  
  protected void charactersFlush()
  {
    if (m_textPendingStart >= 0)
    {
      int i = m_chars.size() - m_textPendingStart;
      boolean bool = false;
      if (getShouldStripWhitespace()) {
        bool = m_chars.isWhitespace(m_textPendingStart, i);
      }
      if (bool)
      {
        m_chars.setLength(m_textPendingStart);
      }
      else if (i > 0)
      {
        int j = m_expandedNameTable.getExpandedTypeID(3);
        int k = m_data.size();
        m_previous = addNode(m_coalescedTextType, j, m_parents.peek(), m_previous, k, false);
        m_data.addElement(m_textPendingStart);
        m_data.addElement(i);
      }
      m_textPendingStart = -1;
      m_textType = (m_coalescedTextType = 3);
    }
  }
  
  public InputSource resolveEntity(String paramString1, String paramString2)
    throws SAXException
  {
    return null;
  }
  
  public void notationDecl(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {}
  
  public void unparsedEntityDecl(String paramString1, String paramString2, String paramString3, String paramString4)
    throws SAXException
  {
    if (null == m_entities) {
      m_entities = new Vector();
    }
    try
    {
      paramString3 = SystemIDResolver.getAbsoluteURI(paramString3, getDocumentBaseURI());
    }
    catch (Exception localException)
    {
      throw new SAXException(localException);
    }
    m_entities.addElement(paramString2);
    m_entities.addElement(paramString3);
    m_entities.addElement(paramString4);
    m_entities.addElement(paramString1);
  }
  
  public void setDocumentLocator(Locator paramLocator)
  {
    m_locator = paramLocator;
    m_systemId = paramLocator.getSystemId();
  }
  
  public void startDocument()
    throws SAXException
  {
    int i = addNode(9, m_expandedNameTable.getExpandedTypeID(9), -1, -1, 0, true);
    m_parents.push(i);
    m_previous = -1;
    m_contextIndexes.push(m_prefixMappings.size());
  }
  
  public void endDocument()
    throws SAXException
  {
    charactersFlush();
    m_nextsib.setElementAt(-1, 0);
    if (m_firstch.elementAt(0) == -2) {
      m_firstch.setElementAt(-1, 0);
    }
    if (-1 != m_previous) {
      m_nextsib.setElementAt(-1, m_previous);
    }
    m_parents = null;
    m_prefixMappings = null;
    m_contextIndexes = null;
    m_endDocumentOccured = true;
    m_locator = null;
  }
  
  public void startPrefixMapping(String paramString1, String paramString2)
    throws SAXException
  {
    if (null == paramString1) {
      paramString1 = "";
    }
    m_prefixMappings.addElement(paramString1);
    m_prefixMappings.addElement(paramString2);
  }
  
  public void endPrefixMapping(String paramString)
    throws SAXException
  {
    if (null == paramString) {
      paramString = "";
    }
    int i = m_contextIndexes.peek() - 1;
    do
    {
      i = m_prefixMappings.indexOf(paramString, ++i);
    } while ((i >= 0) && ((i & 0x1) == 1));
    if (i > -1)
    {
      m_prefixMappings.setElementAt("%@$#^@#", i);
      m_prefixMappings.setElementAt("%@$#^@#", i + 1);
    }
  }
  
  protected boolean declAlreadyDeclared(String paramString)
  {
    int i = m_contextIndexes.peek();
    Vector localVector = m_prefixMappings;
    int j = localVector.size();
    for (int k = i; k < j; k += 2)
    {
      String str = (String)localVector.elementAt(k);
      if ((str != null) && (str.equals(paramString))) {
        return true;
      }
    }
    return false;
  }
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
    throws SAXException
  {
    charactersFlush();
    int i = m_expandedNameTable.getExpandedTypeID(paramString1, paramString2, 1);
    String str1 = getPrefix(paramString3, paramString1);
    int j = null != str1 ? m_valuesOrPrefixes.stringToIndex(paramString3) : 0;
    int k = addNode(1, i, m_parents.peek(), m_previous, j, true);
    if (m_indexing) {
      indexNode(i, k);
    }
    m_parents.push(k);
    String str2 = m_contextIndexes.peek();
    String str3 = m_prefixMappings.size();
    int m = -1;
    if (!m_pastFirstElement)
    {
      str1 = "xml";
      str4 = "http://www.w3.org/XML/1998/namespace";
      i = m_expandedNameTable.getExpandedTypeID(null, str1, 13);
      int i1 = m_valuesOrPrefixes.stringToIndex(str4);
      m = addNode(13, i, k, m, i1, false);
      m_pastFirstElement = true;
    }
    for (String str4 = str2; str4 < str3; str4 += 2)
    {
      str1 = (String)m_prefixMappings.elementAt(str4);
      if (str1 != null)
      {
        String str5 = (String)m_prefixMappings.elementAt(str4 + 1);
        i = m_expandedNameTable.getExpandedTypeID(null, str1, 13);
        int i3 = m_valuesOrPrefixes.stringToIndex(str5);
        m = addNode(13, i, k, m, i3, false);
      }
    }
    int n = paramAttributes.getLength();
    for (int i2 = 0; i2 < n; i2++)
    {
      String str6 = paramAttributes.getURI(i2);
      String str7 = paramAttributes.getQName(i2);
      String str8 = paramAttributes.getValue(i2);
      str1 = getPrefix(str7, str6);
      String str9 = paramAttributes.getLocalName(i2);
      int i4;
      if ((null != str7) && ((str7.equals("xmlns")) || (str7.startsWith("xmlns:"))))
      {
        if (declAlreadyDeclared(str1)) {
          continue;
        }
        i4 = 13;
      }
      else
      {
        i4 = 2;
        if (paramAttributes.getType(i2).equalsIgnoreCase("ID")) {
          setIDAttribute(str8, k);
        }
      }
      if (null == str8) {
        str8 = "";
      }
      int i5 = m_valuesOrPrefixes.stringToIndex(str8);
      if (null != str1)
      {
        j = m_valuesOrPrefixes.stringToIndex(str7);
        int i6 = m_data.size();
        m_data.addElement(j);
        m_data.addElement(i5);
        i5 = -i6;
      }
      i = m_expandedNameTable.getExpandedTypeID(str6, str9, i4);
      m = addNode(i4, i, k, m, i5, false);
    }
    if (-1 != m) {
      m_nextsib.setElementAt(-1, m);
    }
    if (null != m_wsfilter)
    {
      i2 = m_wsfilter.getShouldStripSpace(makeNodeHandle(k), this);
      boolean bool = 2 == i2 ? true : 3 == i2 ? getShouldStripWhitespace() : false;
      pushShouldStripWhitespace(bool);
    }
    m_previous = -1;
    m_contextIndexes.push(m_prefixMappings.size());
  }
  
  public void endElement(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {
    charactersFlush();
    m_contextIndexes.quickPop(1);
    int i = m_contextIndexes.peek();
    if (i != m_prefixMappings.size()) {
      m_prefixMappings.setSize(i);
    }
    int j = m_previous;
    m_previous = m_parents.pop();
    if (-1 == j) {
      m_firstch.setElementAt(-1, m_previous);
    } else {
      m_nextsib.setElementAt(-1, j);
    }
    popShouldStripWhitespace();
  }
  
  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    if (m_textPendingStart == -1)
    {
      m_textPendingStart = m_chars.size();
      m_coalescedTextType = m_textType;
    }
    else if (m_textType == 3)
    {
      m_coalescedTextType = 3;
    }
    m_chars.append(paramArrayOfChar, paramInt1, paramInt2);
  }
  
  public void ignorableWhitespace(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    characters(paramArrayOfChar, paramInt1, paramInt2);
  }
  
  public void processingInstruction(String paramString1, String paramString2)
    throws SAXException
  {
    charactersFlush();
    int i = m_expandedNameTable.getExpandedTypeID(null, paramString1, 7);
    int j = m_valuesOrPrefixes.stringToIndex(paramString2);
    m_previous = addNode(7, i, m_parents.peek(), m_previous, j, false);
  }
  
  public void skippedEntity(String paramString)
    throws SAXException
  {}
  
  public void warning(SAXParseException paramSAXParseException)
    throws SAXException
  {
    System.err.println(paramSAXParseException.getMessage());
  }
  
  public void error(SAXParseException paramSAXParseException)
    throws SAXException
  {
    throw paramSAXParseException;
  }
  
  public void fatalError(SAXParseException paramSAXParseException)
    throws SAXException
  {
    throw paramSAXParseException;
  }
  
  public void elementDecl(String paramString1, String paramString2)
    throws SAXException
  {}
  
  public void attributeDecl(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5)
    throws SAXException
  {}
  
  public void internalEntityDecl(String paramString1, String paramString2)
    throws SAXException
  {}
  
  public void externalEntityDecl(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {}
  
  public void startDTD(String paramString1, String paramString2, String paramString3)
    throws SAXException
  {
    m_insideDTD = true;
  }
  
  public void endDTD()
    throws SAXException
  {
    m_insideDTD = false;
  }
  
  public void startEntity(String paramString)
    throws SAXException
  {}
  
  public void endEntity(String paramString)
    throws SAXException
  {}
  
  public void startCDATA()
    throws SAXException
  {
    m_textType = 4;
  }
  
  public void endCDATA()
    throws SAXException
  {
    m_textType = 3;
  }
  
  public void comment(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    if (m_insideDTD) {
      return;
    }
    charactersFlush();
    int i = m_expandedNameTable.getExpandedTypeID(8);
    int j = m_valuesOrPrefixes.stringToIndex(new String(paramArrayOfChar, paramInt1, paramInt2));
    m_previous = addNode(8, i, m_parents.peek(), m_previous, j, false);
  }
  
  public void setProperty(String paramString, Object paramObject) {}
  
  public SourceLocator getSourceLocatorFor(int paramInt)
  {
    if (m_useSourceLocationProperty)
    {
      paramInt = makeNodeIdentity(paramInt);
      return new NodeLocator(null, m_sourceSystemId.elementAt(paramInt), m_sourceLine.elementAt(paramInt), m_sourceColumn.elementAt(paramInt));
    }
    if (m_locator != null) {
      return new NodeLocator(null, m_locator.getSystemId(), -1, -1);
    }
    if (m_systemId != null) {
      return new NodeLocator(null, m_systemId, -1, -1);
    }
    return null;
  }
  
  public String getFixedNames(int paramInt)
  {
    return m_fixednames[paramInt];
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\dtm\ref\sax2dtm\SAX2DTM.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */