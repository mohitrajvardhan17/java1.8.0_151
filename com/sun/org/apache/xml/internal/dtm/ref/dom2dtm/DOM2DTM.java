package com.sun.org.apache.xml.internal.dtm.ref.dom2dtm;

import com.sun.org.apache.xml.internal.dtm.DTMManager;
import com.sun.org.apache.xml.internal.dtm.DTMWSFilter;
import com.sun.org.apache.xml.internal.dtm.ref.DTMDefaultBaseIterators;
import com.sun.org.apache.xml.internal.dtm.ref.DTMManagerDefault;
import com.sun.org.apache.xml.internal.dtm.ref.ExpandedNameTable;
import com.sun.org.apache.xml.internal.dtm.ref.IncrementalSAXSource;
import com.sun.org.apache.xml.internal.res.XMLMessages;
import com.sun.org.apache.xml.internal.utils.FastStringBuffer;
import com.sun.org.apache.xml.internal.utils.QName;
import com.sun.org.apache.xml.internal.utils.StringBufferPool;
import com.sun.org.apache.xml.internal.utils.SuballocatedIntVector;
import com.sun.org.apache.xml.internal.utils.TreeWalker;
import com.sun.org.apache.xml.internal.utils.XMLCharacterRecognizer;
import com.sun.org.apache.xml.internal.utils.XMLString;
import com.sun.org.apache.xml.internal.utils.XMLStringFactory;
import java.util.Vector;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.dom.DOMSource;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Entity;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;

public class DOM2DTM
  extends DTMDefaultBaseIterators
{
  static final boolean JJK_DEBUG = false;
  static final boolean JJK_NEWCODE = true;
  static final String NAMESPACE_DECL_NS = "http://www.w3.org/XML/1998/namespace";
  private transient Node m_pos = m_root = paramDOMSource.getNode();
  private int m_last_parent = 0;
  private int m_last_kid = -1;
  private transient Node m_root;
  boolean m_processedFirstElement = false;
  private transient boolean m_nodesAreProcessed;
  protected Vector m_nodes = new Vector();
  TreeWalker m_walker = new TreeWalker(null);
  
  public DOM2DTM(DTMManager paramDTMManager, DOMSource paramDOMSource, int paramInt, DTMWSFilter paramDTMWSFilter, XMLStringFactory paramXMLStringFactory, boolean paramBoolean)
  {
    super(paramDTMManager, paramDOMSource, paramInt, paramDTMWSFilter, paramXMLStringFactory, paramBoolean);
    if (1 == m_root.getNodeType())
    {
      NamedNodeMap localNamedNodeMap = m_root.getAttributes();
      int i = localNamedNodeMap == null ? 0 : localNamedNodeMap.getLength();
      if (i > 0)
      {
        int j = -1;
        for (int k = 0; k < i; k++)
        {
          j = addNode(localNamedNodeMap.item(k), 0, j, -1);
          m_firstch.setElementAt(-1, j);
        }
        m_nextsib.setElementAt(-1, j);
      }
    }
    m_nodesAreProcessed = false;
  }
  
  protected int addNode(Node paramNode, int paramInt1, int paramInt2, int paramInt3)
  {
    int i = m_nodes.size();
    if (m_dtmIdent.size() == i >>> 16) {
      try
      {
        if (m_mgr == null) {
          throw new ClassCastException();
        }
        DTMManagerDefault localDTMManagerDefault = (DTMManagerDefault)m_mgr;
        int k = localDTMManagerDefault.getFirstFreeDTMID();
        localDTMManagerDefault.addDTM(this, k, i);
        m_dtmIdent.addElement(k << 16);
      }
      catch (ClassCastException localClassCastException)
      {
        error(XMLMessages.createXMLMessage("ER_NO_DTMIDS_AVAIL", null));
      }
    }
    m_size += 1;
    int j;
    if (-1 == paramInt3) {
      j = paramNode.getNodeType();
    } else {
      j = paramInt3;
    }
    if (2 == j)
    {
      str1 = paramNode.getNodeName();
      if ((str1.startsWith("xmlns:")) || (str1.equals("xmlns"))) {
        j = 13;
      }
    }
    m_nodes.addElement(paramNode);
    m_firstch.setElementAt(-2, i);
    m_nextsib.setElementAt(-2, i);
    m_prevsib.setElementAt(paramInt2, i);
    m_parent.setElementAt(paramInt1, i);
    if ((-1 != paramInt1) && (j != 2) && (j != 13) && (-2 == m_firstch.elementAt(paramInt1))) {
      m_firstch.setElementAt(i, paramInt1);
    }
    String str1 = paramNode.getNamespaceURI();
    String str2 = j == 7 ? paramNode.getNodeName() : paramNode.getLocalName();
    if (((j == 1) || (j == 2)) && (null == str2)) {
      str2 = paramNode.getNodeName();
    }
    ExpandedNameTable localExpandedNameTable = m_expandedNameTable;
    int m = ((paramNode.getLocalName() != null) || (j == 1) || (j != 2)) || (null != str2) ? localExpandedNameTable.getExpandedTypeID(str1, str2, j) : localExpandedNameTable.getExpandedTypeID(j);
    m_exptype.setElementAt(m, i);
    indexNode(m, i);
    if (-1 != paramInt2) {
      m_nextsib.setElementAt(i, paramInt2);
    }
    if (j == 13) {
      declareNamespaceInContext(paramInt1, i);
    }
    return i;
  }
  
  public int getNumberOfNodes()
  {
    return m_nodes.size();
  }
  
  protected boolean nextNode()
  {
    if (m_nodesAreProcessed) {
      return false;
    }
    Object localObject1 = m_pos;
    Object localObject2 = null;
    int i = -1;
    do
    {
      if (((Node)localObject1).hasChildNodes())
      {
        localObject2 = ((Node)localObject1).getFirstChild();
        if ((localObject2 != null) && (10 == ((Node)localObject2).getNodeType())) {
          localObject2 = ((Node)localObject2).getNextSibling();
        }
        if (5 != ((Node)localObject1).getNodeType())
        {
          m_last_parent = m_last_kid;
          m_last_kid = -1;
          if (null != m_wsfilter)
          {
            j = m_wsfilter.getShouldStripSpace(makeNodeHandle(m_last_parent), this);
            boolean bool2 = 2 == j ? true : 3 == j ? getShouldStripWhitespace() : false;
            pushShouldStripWhitespace(bool2);
          }
        }
      }
      else
      {
        if ((m_last_kid != -1) && (m_firstch.elementAt(m_last_kid) == -2)) {
          m_firstch.setElementAt(-1, m_last_kid);
        }
        while (m_last_parent != -1)
        {
          localObject2 = ((Node)localObject1).getNextSibling();
          if ((localObject2 != null) && (10 == ((Node)localObject2).getNodeType())) {
            localObject2 = ((Node)localObject2).getNextSibling();
          }
          if (localObject2 != null) {
            break;
          }
          localObject1 = ((Node)localObject1).getParentNode();
          if ((localObject1 != null) || ((localObject1 == null) || (5 != ((Node)localObject1).getNodeType())))
          {
            popShouldStripWhitespace();
            if (m_last_kid == -1) {
              m_firstch.setElementAt(-1, m_last_parent);
            } else {
              m_nextsib.setElementAt(-1, m_last_kid);
            }
            m_last_parent = m_parent.elementAt(m_last_kid = m_last_parent);
          }
        }
        if (m_last_parent == -1) {
          localObject2 = null;
        }
      }
      if (localObject2 != null) {
        i = ((Node)localObject2).getNodeType();
      }
      if (5 == i) {
        localObject1 = localObject2;
      }
    } while (5 == i);
    if (localObject2 == null)
    {
      m_nextsib.setElementAt(-1, 0);
      m_nodesAreProcessed = true;
      m_pos = null;
      return false;
    }
    int j = 0;
    Object localObject3 = null;
    i = ((Node)localObject2).getNodeType();
    boolean bool1;
    if ((3 == i) || (4 == i))
    {
      j = (null != m_wsfilter) && (getShouldStripWhitespace()) ? 1 : 0;
      for (Object localObject4 = localObject2; localObject4 != null; localObject4 = logicalNextDOMTextNode((Node)localObject4))
      {
        localObject3 = localObject4;
        if (3 == ((Node)localObject4).getNodeType()) {
          i = 3;
        }
        j &= XMLCharacterRecognizer.isWhiteSpace(((Node)localObject4).getNodeValue());
      }
    }
    else if (7 == i)
    {
      bool1 = ((Node)localObject1).getNodeName().toLowerCase().equals("xml");
    }
    if (!bool1)
    {
      int k = addNode((Node)localObject2, m_last_parent, m_last_kid, i);
      m_last_kid = k;
      if (1 == i)
      {
        int m = -1;
        NamedNodeMap localNamedNodeMap = ((Node)localObject2).getAttributes();
        int n = localNamedNodeMap == null ? 0 : localNamedNodeMap.getLength();
        if (n > 0) {
          for (int i1 = 0; i1 < n; i1++)
          {
            m = addNode(localNamedNodeMap.item(i1), k, m, -1);
            m_firstch.setElementAt(-1, m);
            if ((!m_processedFirstElement) && ("xmlns:xml".equals(localNamedNodeMap.item(i1).getNodeName()))) {
              m_processedFirstElement = true;
            }
          }
        }
        if (!m_processedFirstElement)
        {
          m = addNode(new DOM2DTMdefaultNamespaceDeclarationNode((Element)localObject2, "xml", "http://www.w3.org/XML/1998/namespace", makeNodeHandle((m == -1 ? k : m) + 1)), k, m, -1);
          m_firstch.setElementAt(-1, m);
          m_processedFirstElement = true;
        }
        if (m != -1) {
          m_nextsib.setElementAt(-1, m);
        }
      }
    }
    if ((3 == i) || (4 == i)) {
      localObject2 = localObject3;
    }
    m_pos = ((Node)localObject2);
    return true;
  }
  
  public Node getNode(int paramInt)
  {
    int i = makeNodeIdentity(paramInt);
    return (Node)m_nodes.elementAt(i);
  }
  
  protected Node lookupNode(int paramInt)
  {
    return (Node)m_nodes.elementAt(paramInt);
  }
  
  protected int getNextNodeIdentity(int paramInt)
  {
    
    if ((paramInt >= m_nodes.size()) && (!nextNode())) {
      paramInt = -1;
    }
    return paramInt;
  }
  
  private int getHandleFromNode(Node paramNode)
  {
    if (null != paramNode)
    {
      int i = m_nodes.size();
      int j = 0;
      boolean bool;
      do
      {
        while (j < i)
        {
          if (m_nodes.elementAt(j) == paramNode) {
            return makeNodeHandle(j);
          }
          j++;
        }
        bool = nextNode();
        i = m_nodes.size();
      } while ((bool) || (j < i));
    }
    return -1;
  }
  
  public int getHandleOfNode(Node paramNode)
  {
    if ((null != paramNode) && ((m_root == paramNode) || ((m_root.getNodeType() == 9) && (m_root == paramNode.getOwnerDocument())) || ((m_root.getNodeType() != 9) && (m_root.getOwnerDocument() == paramNode.getOwnerDocument())))) {
      for (Object localObject = paramNode; localObject != null; localObject = ((Node)localObject).getNodeType() != 2 ? ((Node)localObject).getParentNode() : ((Attr)localObject).getOwnerElement()) {
        if (localObject == m_root) {
          return getHandleFromNode(paramNode);
        }
      }
    }
    return -1;
  }
  
  public int getAttributeNode(int paramInt, String paramString1, String paramString2)
  {
    if (null == paramString1) {
      paramString1 = "";
    }
    int i = getNodeType(paramInt);
    if (1 == i)
    {
      int j = makeNodeIdentity(paramInt);
      while (-1 != (j = getNextNodeIdentity(j)))
      {
        i = _type(j);
        if ((i != 2) && (i != 13)) {
          break;
        }
        Node localNode = lookupNode(j);
        String str1 = localNode.getNamespaceURI();
        if (null == str1) {
          str1 = "";
        }
        String str2 = localNode.getLocalName();
        if ((str1.equals(paramString1)) && (paramString2.equals(str2))) {
          return makeNodeHandle(j);
        }
      }
    }
    return -1;
  }
  
  public XMLString getStringValue(int paramInt)
  {
    int i = getNodeType(paramInt);
    Node localNode = getNode(paramInt);
    FastStringBuffer localFastStringBuffer;
    String str;
    if ((1 == i) || (9 == i) || (11 == i))
    {
      localFastStringBuffer = StringBufferPool.get();
      try
      {
        getNodeData(localNode, localFastStringBuffer);
        str = localFastStringBuffer.length() > 0 ? localFastStringBuffer.toString() : "";
      }
      finally
      {
        StringBufferPool.free(localFastStringBuffer);
      }
      return m_xstrf.newstr(str);
    }
    if ((3 == i) || (4 == i))
    {
      localFastStringBuffer = StringBufferPool.get();
      while (localNode != null)
      {
        localFastStringBuffer.append(localNode.getNodeValue());
        localNode = logicalNextDOMTextNode(localNode);
      }
      str = localFastStringBuffer.length() > 0 ? localFastStringBuffer.toString() : "";
      StringBufferPool.free(localFastStringBuffer);
      return m_xstrf.newstr(str);
    }
    return m_xstrf.newstr(localNode.getNodeValue());
  }
  
  public boolean isWhitespace(int paramInt)
  {
    int i = getNodeType(paramInt);
    Node localNode = getNode(paramInt);
    if ((3 == i) || (4 == i))
    {
      FastStringBuffer localFastStringBuffer = StringBufferPool.get();
      while (localNode != null)
      {
        localFastStringBuffer.append(localNode.getNodeValue());
        localNode = logicalNextDOMTextNode(localNode);
      }
      boolean bool = localFastStringBuffer.isWhitespace(0, localFastStringBuffer.length());
      StringBufferPool.free(localFastStringBuffer);
      return bool;
    }
    return false;
  }
  
  protected static void getNodeData(Node paramNode, FastStringBuffer paramFastStringBuffer)
  {
    switch (paramNode.getNodeType())
    {
    case 1: 
    case 9: 
    case 11: 
      for (Node localNode = paramNode.getFirstChild(); null != localNode; localNode = localNode.getNextSibling()) {
        getNodeData(localNode, paramFastStringBuffer);
      }
      break;
    case 2: 
    case 3: 
    case 4: 
      paramFastStringBuffer.append(paramNode.getNodeValue());
      break;
    case 7: 
      break;
    }
  }
  
  public String getNodeName(int paramInt)
  {
    Node localNode = getNode(paramInt);
    return localNode.getNodeName();
  }
  
  public String getNodeNameX(int paramInt)
  {
    int i = getNodeType(paramInt);
    Node localNode;
    String str;
    switch (i)
    {
    case 13: 
      localNode = getNode(paramInt);
      str = localNode.getNodeName();
      if (str.startsWith("xmlns:")) {
        str = QName.getLocalPart(str);
      } else if (str.equals("xmlns")) {
        str = "";
      }
      break;
    case 1: 
    case 2: 
    case 5: 
    case 7: 
      localNode = getNode(paramInt);
      str = localNode.getNodeName();
      break;
    case 3: 
    case 4: 
    case 6: 
    case 8: 
    case 9: 
    case 10: 
    case 11: 
    case 12: 
    default: 
      str = "";
    }
    return str;
  }
  
  public String getLocalName(int paramInt)
  {
    int i = makeNodeIdentity(paramInt);
    if (-1 == i) {
      return null;
    }
    Node localNode = (Node)m_nodes.elementAt(i);
    String str1 = localNode.getLocalName();
    if (null == str1)
    {
      String str2 = localNode.getNodeName();
      if ('#' == str2.charAt(0))
      {
        str1 = "";
      }
      else
      {
        int j = str2.indexOf(':');
        str1 = j < 0 ? str2 : str2.substring(j + 1);
      }
    }
    return str1;
  }
  
  public String getPrefix(int paramInt)
  {
    int i = getNodeType(paramInt);
    Node localNode;
    String str2;
    int j;
    String str1;
    switch (i)
    {
    case 13: 
      localNode = getNode(paramInt);
      str2 = localNode.getNodeName();
      j = str2.indexOf(':');
      str1 = j < 0 ? "" : str2.substring(j + 1);
      break;
    case 1: 
    case 2: 
      localNode = getNode(paramInt);
      str2 = localNode.getNodeName();
      j = str2.indexOf(':');
      str1 = j < 0 ? "" : str2.substring(0, j);
      break;
    default: 
      str1 = "";
    }
    return str1;
  }
  
  public String getNamespaceURI(int paramInt)
  {
    int i = makeNodeIdentity(paramInt);
    if (i == -1) {
      return null;
    }
    Node localNode = (Node)m_nodes.elementAt(i);
    return localNode.getNamespaceURI();
  }
  
  private Node logicalNextDOMTextNode(Node paramNode)
  {
    Node localNode = paramNode.getNextSibling();
    if (localNode == null) {
      for (paramNode = paramNode.getParentNode(); (paramNode != null) && (5 == paramNode.getNodeType()); paramNode = paramNode.getParentNode())
      {
        localNode = paramNode.getNextSibling();
        if (localNode != null) {
          break;
        }
      }
    }
    paramNode = localNode;
    while ((paramNode != null) && (5 == paramNode.getNodeType())) {
      if (paramNode.hasChildNodes()) {
        paramNode = paramNode.getFirstChild();
      } else {
        paramNode = paramNode.getNextSibling();
      }
    }
    if (paramNode != null)
    {
      int i = paramNode.getNodeType();
      if ((3 != i) && (4 != i)) {
        paramNode = null;
      }
    }
    return paramNode;
  }
  
  public String getNodeValue(int paramInt)
  {
    int i = _exptype(makeNodeIdentity(paramInt));
    i = -1 != i ? getNodeType(paramInt) : -1;
    if ((3 != i) && (4 != i)) {
      return getNode(paramInt).getNodeValue();
    }
    Node localNode1 = getNode(paramInt);
    Node localNode2 = logicalNextDOMTextNode(localNode1);
    if (localNode2 == null) {
      return localNode1.getNodeValue();
    }
    FastStringBuffer localFastStringBuffer = StringBufferPool.get();
    localFastStringBuffer.append(localNode1.getNodeValue());
    while (localNode2 != null)
    {
      localFastStringBuffer.append(localNode2.getNodeValue());
      localNode2 = logicalNextDOMTextNode(localNode2);
    }
    String str = localFastStringBuffer.length() > 0 ? localFastStringBuffer.toString() : "";
    StringBufferPool.free(localFastStringBuffer);
    return str;
  }
  
  public String getDocumentTypeDeclarationSystemIdentifier()
  {
    Document localDocument;
    if (m_root.getNodeType() == 9) {
      localDocument = (Document)m_root;
    } else {
      localDocument = m_root.getOwnerDocument();
    }
    if (null != localDocument)
    {
      DocumentType localDocumentType = localDocument.getDoctype();
      if (null != localDocumentType) {
        return localDocumentType.getSystemId();
      }
    }
    return null;
  }
  
  public String getDocumentTypeDeclarationPublicIdentifier()
  {
    Document localDocument;
    if (m_root.getNodeType() == 9) {
      localDocument = (Document)m_root;
    } else {
      localDocument = m_root.getOwnerDocument();
    }
    if (null != localDocument)
    {
      DocumentType localDocumentType = localDocument.getDoctype();
      if (null != localDocumentType) {
        return localDocumentType.getPublicId();
      }
    }
    return null;
  }
  
  public int getElementById(String paramString)
  {
    Document localDocument = m_root.getNodeType() == 9 ? (Document)m_root : m_root.getOwnerDocument();
    if (null != localDocument)
    {
      Element localElement = localDocument.getElementById(paramString);
      if (null != localElement)
      {
        int i = getHandleFromNode(localElement);
        if (-1 == i)
        {
          int j = m_nodes.size() - 1;
          while (-1 != (j = getNextNodeIdentity(j)))
          {
            Node localNode = getNode(j);
            if (localNode == localElement)
            {
              i = getHandleFromNode(localElement);
              break;
            }
          }
        }
        return i;
      }
    }
    return -1;
  }
  
  public String getUnparsedEntityURI(String paramString)
  {
    String str1 = "";
    Document localDocument = m_root.getNodeType() == 9 ? (Document)m_root : m_root.getOwnerDocument();
    if (null != localDocument)
    {
      DocumentType localDocumentType = localDocument.getDoctype();
      if (null != localDocumentType)
      {
        NamedNodeMap localNamedNodeMap = localDocumentType.getEntities();
        if (null == localNamedNodeMap) {
          return str1;
        }
        Entity localEntity = (Entity)localNamedNodeMap.getNamedItem(paramString);
        if (null == localEntity) {
          return str1;
        }
        String str2 = localEntity.getNotationName();
        if (null != str2)
        {
          str1 = localEntity.getSystemId();
          if (null == str1) {
            str1 = localEntity.getPublicId();
          }
        }
      }
    }
    return str1;
  }
  
  public boolean isAttributeSpecified(int paramInt)
  {
    int i = getNodeType(paramInt);
    if (2 == i)
    {
      Attr localAttr = (Attr)getNode(paramInt);
      return localAttr.getSpecified();
    }
    return false;
  }
  
  public void setIncrementalSAXSource(IncrementalSAXSource paramIncrementalSAXSource) {}
  
  public ContentHandler getContentHandler()
  {
    return null;
  }
  
  public LexicalHandler getLexicalHandler()
  {
    return null;
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
    return false;
  }
  
  private static boolean isSpace(char paramChar)
  {
    return XMLCharacterRecognizer.isWhiteSpace(paramChar);
  }
  
  public void dispatchCharactersEvents(int paramInt, ContentHandler paramContentHandler, boolean paramBoolean)
    throws SAXException
  {
    if (paramBoolean)
    {
      XMLString localXMLString = getStringValue(paramInt);
      localXMLString = localXMLString.fixWhiteSpace(true, true, false);
      localXMLString.dispatchCharactersEvents(paramContentHandler);
    }
    else
    {
      int i = getNodeType(paramInt);
      Node localNode = getNode(paramInt);
      dispatchNodeData(localNode, paramContentHandler, 0);
      if ((3 == i) || (4 == i)) {
        while (null != (localNode = logicalNextDOMTextNode(localNode))) {
          dispatchNodeData(localNode, paramContentHandler, 0);
        }
      }
    }
  }
  
  protected static void dispatchNodeData(Node paramNode, ContentHandler paramContentHandler, int paramInt)
    throws SAXException
  {
    Object localObject;
    switch (paramNode.getNodeType())
    {
    case 1: 
    case 9: 
    case 11: 
      for (localObject = paramNode.getFirstChild(); null != localObject; localObject = ((Node)localObject).getNextSibling()) {
        dispatchNodeData((Node)localObject, paramContentHandler, paramInt + 1);
      }
      break;
    case 7: 
    case 8: 
      if (0 != paramInt) {
        break;
      }
    case 2: 
    case 3: 
    case 4: 
      localObject = paramNode.getNodeValue();
      if ((paramContentHandler instanceof CharacterNodeHandler)) {
        ((CharacterNodeHandler)paramContentHandler).characters(paramNode);
      } else {
        paramContentHandler.characters(((String)localObject).toCharArray(), 0, ((String)localObject).length());
      }
      break;
    }
  }
  
  public void dispatchToEvents(int paramInt, ContentHandler paramContentHandler)
    throws SAXException
  {
    TreeWalker localTreeWalker = m_walker;
    ContentHandler localContentHandler = localTreeWalker.getContentHandler();
    if (null != localContentHandler) {
      localTreeWalker = new TreeWalker(null);
    }
    localTreeWalker.setContentHandler(paramContentHandler);
    try
    {
      Node localNode = getNode(paramInt);
      localTreeWalker.traverseFragment(localNode);
    }
    finally
    {
      localTreeWalker.setContentHandler(null);
    }
  }
  
  public void setProperty(String paramString, Object paramObject) {}
  
  public SourceLocator getSourceLocatorFor(int paramInt)
  {
    return null;
  }
  
  public static abstract interface CharacterNodeHandler
  {
    public abstract void characters(Node paramNode)
      throws SAXException;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\dtm\ref\dom2dtm\DOM2DTM.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */