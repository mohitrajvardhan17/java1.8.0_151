package com.sun.org.apache.xml.internal.dtm.ref;

import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.dtm.DTMAxisTraverser;
import com.sun.org.apache.xml.internal.dtm.DTMException;
import com.sun.org.apache.xml.internal.dtm.DTMManager;
import com.sun.org.apache.xml.internal.dtm.DTMWSFilter;
import com.sun.org.apache.xml.internal.res.XMLMessages;
import com.sun.org.apache.xml.internal.utils.BoolStack;
import com.sun.org.apache.xml.internal.utils.SuballocatedIntVector;
import com.sun.org.apache.xml.internal.utils.XMLString;
import com.sun.org.apache.xml.internal.utils.XMLStringFactory;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Vector;
import javax.xml.transform.Source;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public abstract class DTMDefaultBase
  implements DTM
{
  static final boolean JJK_DEBUG = false;
  public static final int ROOTNODE = 0;
  protected int m_size = 0;
  protected SuballocatedIntVector m_exptype;
  protected SuballocatedIntVector m_firstch;
  protected SuballocatedIntVector m_nextsib;
  protected SuballocatedIntVector m_prevsib;
  protected SuballocatedIntVector m_parent;
  protected Vector m_namespaceDeclSets = null;
  protected SuballocatedIntVector m_namespaceDeclSetElements = null;
  protected int[][][] m_elemIndexes;
  public static final int DEFAULT_BLOCKSIZE = 512;
  public static final int DEFAULT_NUMBLOCKS = 32;
  public static final int DEFAULT_NUMBLOCKS_SMALL = 4;
  protected static final int NOTPROCESSED = -2;
  public DTMManager m_mgr;
  protected DTMManagerDefault m_mgrDefault = null;
  protected SuballocatedIntVector m_dtmIdent;
  protected String m_documentBaseURI;
  protected DTMWSFilter m_wsfilter;
  protected boolean m_shouldStripWS = false;
  protected BoolStack m_shouldStripWhitespaceStack;
  protected XMLStringFactory m_xstrf;
  protected ExpandedNameTable m_expandedNameTable;
  protected boolean m_indexing;
  protected DTMAxisTraverser[] m_traversers;
  private Vector m_namespaceLists = null;
  
  public DTMDefaultBase(DTMManager paramDTMManager, Source paramSource, int paramInt, DTMWSFilter paramDTMWSFilter, XMLStringFactory paramXMLStringFactory, boolean paramBoolean)
  {
    this(paramDTMManager, paramSource, paramInt, paramDTMWSFilter, paramXMLStringFactory, paramBoolean, 512, true, false);
  }
  
  public DTMDefaultBase(DTMManager paramDTMManager, Source paramSource, int paramInt1, DTMWSFilter paramDTMWSFilter, XMLStringFactory paramXMLStringFactory, boolean paramBoolean1, int paramInt2, boolean paramBoolean2, boolean paramBoolean3)
  {
    int i;
    if (paramInt2 <= 64)
    {
      i = 4;
      m_dtmIdent = new SuballocatedIntVector(4, 1);
    }
    else
    {
      i = 32;
      m_dtmIdent = new SuballocatedIntVector(32);
    }
    m_exptype = new SuballocatedIntVector(paramInt2, i);
    m_firstch = new SuballocatedIntVector(paramInt2, i);
    m_nextsib = new SuballocatedIntVector(paramInt2, i);
    m_parent = new SuballocatedIntVector(paramInt2, i);
    if (paramBoolean2) {
      m_prevsib = new SuballocatedIntVector(paramInt2, i);
    }
    m_mgr = paramDTMManager;
    if ((paramDTMManager instanceof DTMManagerDefault)) {
      m_mgrDefault = ((DTMManagerDefault)paramDTMManager);
    }
    m_documentBaseURI = (null != paramSource ? paramSource.getSystemId() : null);
    m_dtmIdent.setElementAt(paramInt1, 0);
    m_wsfilter = paramDTMWSFilter;
    m_xstrf = paramXMLStringFactory;
    m_indexing = paramBoolean1;
    if (paramBoolean1) {
      m_expandedNameTable = new ExpandedNameTable();
    } else {
      m_expandedNameTable = m_mgrDefault.getExpandedNameTable(this);
    }
    if (null != paramDTMWSFilter)
    {
      m_shouldStripWhitespaceStack = new BoolStack();
      pushShouldStripWhitespace(false);
    }
  }
  
  protected void ensureSizeOfIndex(int paramInt1, int paramInt2)
  {
    if (null == m_elemIndexes)
    {
      m_elemIndexes = new int[paramInt1 + 20][][];
    }
    else if (m_elemIndexes.length <= paramInt1)
    {
      localObject1 = m_elemIndexes;
      m_elemIndexes = new int[paramInt1 + 20][][];
      System.arraycopy(localObject1, 0, m_elemIndexes, 0, localObject1.length);
    }
    Object localObject1 = m_elemIndexes[paramInt1];
    if (null == localObject1)
    {
      localObject1 = new int[paramInt2 + 100][];
      m_elemIndexes[paramInt1] = localObject1;
    }
    else if (localObject1.length <= paramInt2)
    {
      localObject2 = localObject1;
      localObject1 = new int[paramInt2 + 100][];
      System.arraycopy(localObject2, 0, localObject1, 0, localObject2.length);
      m_elemIndexes[paramInt1] = localObject1;
    }
    Object localObject2 = localObject1[paramInt2];
    if (null == localObject2)
    {
      localObject2 = new int[''];
      localObject1[paramInt2] = localObject2;
      localObject2[0] = 1;
    }
    else if (localObject2.length <= localObject2[0] + 1)
    {
      Object localObject3 = localObject2;
      localObject2 = new int[localObject2[0] + 1024];
      System.arraycopy(localObject3, 0, localObject2, 0, localObject3.length);
      localObject1[paramInt2] = localObject2;
    }
  }
  
  protected void indexNode(int paramInt1, int paramInt2)
  {
    ExpandedNameTable localExpandedNameTable = m_expandedNameTable;
    int i = localExpandedNameTable.getType(paramInt1);
    if (1 == i)
    {
      int j = localExpandedNameTable.getNamespaceID(paramInt1);
      int k = localExpandedNameTable.getLocalNameID(paramInt1);
      ensureSizeOfIndex(j, k);
      int[] arrayOfInt = m_elemIndexes[j][k];
      arrayOfInt[arrayOfInt[0]] = paramInt2;
      arrayOfInt[0] += 1;
    }
  }
  
  protected int findGTE(int[] paramArrayOfInt, int paramInt1, int paramInt2, int paramInt3)
  {
    int i = paramInt1;
    int j = paramInt1 + (paramInt2 - 1);
    int k = j;
    while (i <= j)
    {
      int m = (i + j) / 2;
      int n = paramArrayOfInt[m];
      if (n > paramInt3) {
        j = m - 1;
      } else if (n < paramInt3) {
        i = m + 1;
      } else {
        return m;
      }
    }
    return (i <= k) && (paramArrayOfInt[i] > paramInt3) ? i : -1;
  }
  
  int findElementFromIndex(int paramInt1, int paramInt2, int paramInt3)
  {
    int[][][] arrayOfInt = m_elemIndexes;
    if ((null != arrayOfInt) && (paramInt1 < arrayOfInt.length))
    {
      int[][] arrayOfInt1 = arrayOfInt[paramInt1];
      if ((null != arrayOfInt1) && (paramInt2 < arrayOfInt1.length))
      {
        int[] arrayOfInt2 = arrayOfInt1[paramInt2];
        if (null != arrayOfInt2)
        {
          int i = findGTE(arrayOfInt2, 1, arrayOfInt2[0], paramInt3);
          if (i > -1) {
            return arrayOfInt2[i];
          }
        }
      }
    }
    return -2;
  }
  
  protected abstract int getNextNodeIdentity(int paramInt);
  
  protected abstract boolean nextNode();
  
  protected abstract int getNumberOfNodes();
  
  protected short _type(int paramInt)
  {
    int i = _exptype(paramInt);
    if (-1 != i) {
      return m_expandedNameTable.getType(i);
    }
    return -1;
  }
  
  protected int _exptype(int paramInt)
  {
    if (paramInt == -1) {
      return -1;
    }
    while (paramInt >= m_size) {
      if ((!nextNode()) && (paramInt >= m_size)) {
        return -1;
      }
    }
    return m_exptype.elementAt(paramInt);
  }
  
  protected int _level(int paramInt)
  {
    while (paramInt >= m_size)
    {
      i = nextNode();
      if ((i == 0) && (paramInt >= m_size)) {
        return -1;
      }
    }
    for (int i = 0; -1 != (paramInt = _parent(paramInt)); i++) {}
    return i;
  }
  
  protected int _firstch(int paramInt)
  {
    int i = paramInt >= m_size ? -2 : m_firstch.elementAt(paramInt);
    while (i == -2)
    {
      boolean bool = nextNode();
      if ((paramInt >= m_size) && (!bool)) {
        return -1;
      }
      i = m_firstch.elementAt(paramInt);
      if ((i == -2) && (!bool)) {
        return -1;
      }
    }
    return i;
  }
  
  protected int _nextsib(int paramInt)
  {
    int i = paramInt >= m_size ? -2 : m_nextsib.elementAt(paramInt);
    while (i == -2)
    {
      boolean bool = nextNode();
      if ((paramInt >= m_size) && (!bool)) {
        return -1;
      }
      i = m_nextsib.elementAt(paramInt);
      if ((i == -2) && (!bool)) {
        return -1;
      }
    }
    return i;
  }
  
  protected int _prevsib(int paramInt)
  {
    if (paramInt < m_size) {
      return m_prevsib.elementAt(paramInt);
    }
    for (;;)
    {
      boolean bool = nextNode();
      if ((paramInt >= m_size) && (!bool)) {
        return -1;
      }
      if (paramInt < m_size) {
        return m_prevsib.elementAt(paramInt);
      }
    }
  }
  
  protected int _parent(int paramInt)
  {
    if (paramInt < m_size) {
      return m_parent.elementAt(paramInt);
    }
    for (;;)
    {
      boolean bool = nextNode();
      if ((paramInt >= m_size) && (!bool)) {
        return -1;
      }
      if (paramInt < m_size) {
        return m_parent.elementAt(paramInt);
      }
    }
  }
  
  public void dumpDTM(OutputStream paramOutputStream)
  {
    try
    {
      if (paramOutputStream == null)
      {
        localObject = new File("DTMDump" + hashCode() + ".txt");
        System.err.println("Dumping... " + ((File)localObject).getAbsolutePath());
        paramOutputStream = new FileOutputStream((File)localObject);
      }
      Object localObject = new PrintStream(paramOutputStream);
      while (nextNode()) {}
      int i = m_size;
      ((PrintStream)localObject).println("Total nodes: " + i);
      for (int j = 0; j < i; j++)
      {
        int k = makeNodeHandle(j);
        ((PrintStream)localObject).println("=========== index=" + j + " handle=" + k + " ===========");
        ((PrintStream)localObject).println("NodeName: " + getNodeName(k));
        ((PrintStream)localObject).println("NodeNameX: " + getNodeNameX(k));
        ((PrintStream)localObject).println("LocalName: " + getLocalName(k));
        ((PrintStream)localObject).println("NamespaceURI: " + getNamespaceURI(k));
        ((PrintStream)localObject).println("Prefix: " + getPrefix(k));
        int m = _exptype(j);
        ((PrintStream)localObject).println("Expanded Type ID: " + Integer.toHexString(m));
        int n = _type(j);
        String str;
        switch (n)
        {
        case 2: 
          str = "ATTRIBUTE_NODE";
          break;
        case 4: 
          str = "CDATA_SECTION_NODE";
          break;
        case 8: 
          str = "COMMENT_NODE";
          break;
        case 11: 
          str = "DOCUMENT_FRAGMENT_NODE";
          break;
        case 9: 
          str = "DOCUMENT_NODE";
          break;
        case 10: 
          str = "DOCUMENT_NODE";
          break;
        case 1: 
          str = "ELEMENT_NODE";
          break;
        case 6: 
          str = "ENTITY_NODE";
          break;
        case 5: 
          str = "ENTITY_REFERENCE_NODE";
          break;
        case 13: 
          str = "NAMESPACE_NODE";
          break;
        case 12: 
          str = "NOTATION_NODE";
          break;
        case -1: 
          str = "NULL";
          break;
        case 7: 
          str = "PROCESSING_INSTRUCTION_NODE";
          break;
        case 3: 
          str = "TEXT_NODE";
          break;
        case 0: 
        default: 
          str = "Unknown!";
        }
        ((PrintStream)localObject).println("Type: " + str);
        int i1 = _firstch(j);
        if (-1 == i1) {
          ((PrintStream)localObject).println("First child: DTM.NULL");
        } else if (-2 == i1) {
          ((PrintStream)localObject).println("First child: NOTPROCESSED");
        } else {
          ((PrintStream)localObject).println("First child: " + i1);
        }
        if (m_prevsib != null)
        {
          i2 = _prevsib(j);
          if (-1 == i2) {
            ((PrintStream)localObject).println("Prev sibling: DTM.NULL");
          } else if (-2 == i2) {
            ((PrintStream)localObject).println("Prev sibling: NOTPROCESSED");
          } else {
            ((PrintStream)localObject).println("Prev sibling: " + i2);
          }
        }
        int i2 = _nextsib(j);
        if (-1 == i2) {
          ((PrintStream)localObject).println("Next sibling: DTM.NULL");
        } else if (-2 == i2) {
          ((PrintStream)localObject).println("Next sibling: NOTPROCESSED");
        } else {
          ((PrintStream)localObject).println("Next sibling: " + i2);
        }
        int i3 = _parent(j);
        if (-1 == i3) {
          ((PrintStream)localObject).println("Parent: DTM.NULL");
        } else if (-2 == i3) {
          ((PrintStream)localObject).println("Parent: NOTPROCESSED");
        } else {
          ((PrintStream)localObject).println("Parent: " + i3);
        }
        int i4 = _level(j);
        ((PrintStream)localObject).println("Level: " + i4);
        ((PrintStream)localObject).println("Node Value: " + getNodeValue(k));
        ((PrintStream)localObject).println("String Value: " + getStringValue(k));
      }
    }
    catch (IOException localIOException)
    {
      localIOException.printStackTrace(System.err);
      throw new RuntimeException(localIOException.getMessage());
    }
  }
  
  public String dumpNode(int paramInt)
  {
    if (paramInt == -1) {
      return "[null]";
    }
    String str;
    switch (getNodeType(paramInt))
    {
    case 2: 
      str = "ATTR";
      break;
    case 4: 
      str = "CDATA";
      break;
    case 8: 
      str = "COMMENT";
      break;
    case 11: 
      str = "DOC_FRAG";
      break;
    case 9: 
      str = "DOC";
      break;
    case 10: 
      str = "DOC_TYPE";
      break;
    case 1: 
      str = "ELEMENT";
      break;
    case 6: 
      str = "ENTITY";
      break;
    case 5: 
      str = "ENT_REF";
      break;
    case 13: 
      str = "NAMESPACE";
      break;
    case 12: 
      str = "NOTATION";
      break;
    case -1: 
      str = "null";
      break;
    case 7: 
      str = "PI";
      break;
    case 3: 
      str = "TEXT";
      break;
    case 0: 
    default: 
      str = "Unknown!";
    }
    return "[" + paramInt + ": " + str + "(0x" + Integer.toHexString(getExpandedTypeID(paramInt)) + ") " + getNodeNameX(paramInt) + " {" + getNamespaceURI(paramInt) + "}=\"" + getNodeValue(paramInt) + "\"]";
  }
  
  public void setFeature(String paramString, boolean paramBoolean) {}
  
  public boolean hasChildNodes(int paramInt)
  {
    int i = makeNodeIdentity(paramInt);
    int j = _firstch(i);
    return j != -1;
  }
  
  public final int makeNodeHandle(int paramInt)
  {
    if (-1 == paramInt) {
      return -1;
    }
    return m_dtmIdent.elementAt(paramInt >>> 16) + (paramInt & 0xFFFF);
  }
  
  public final int makeNodeIdentity(int paramInt)
  {
    if (-1 == paramInt) {
      return -1;
    }
    if (m_mgrDefault != null)
    {
      i = paramInt >>> 16;
      if (m_mgrDefault.m_dtms[i] != this) {
        return -1;
      }
      return m_mgrDefault.m_dtm_offsets[i] | paramInt & 0xFFFF;
    }
    int i = m_dtmIdent.indexOf(paramInt & 0xFFFF0000);
    return i == -1 ? -1 : (i << 16) + (paramInt & 0xFFFF);
  }
  
  public int getFirstChild(int paramInt)
  {
    int i = makeNodeIdentity(paramInt);
    int j = _firstch(i);
    return makeNodeHandle(j);
  }
  
  public int getTypedFirstChild(int paramInt1, int paramInt2)
  {
    if (paramInt2 < 14) {
      for (i = _firstch(makeNodeIdentity(paramInt1)); i != -1; i = _nextsib(i))
      {
        int j = _exptype(i);
        if ((j == paramInt2) || ((j >= 14) && (m_expandedNameTable.getType(j) == paramInt2))) {
          return makeNodeHandle(i);
        }
      }
    }
    for (int i = _firstch(makeNodeIdentity(paramInt1)); i != -1; i = _nextsib(i)) {
      if (_exptype(i) == paramInt2) {
        return makeNodeHandle(i);
      }
    }
    return -1;
  }
  
  public int getLastChild(int paramInt)
  {
    int i = makeNodeIdentity(paramInt);
    int j = _firstch(i);
    int k = -1;
    while (j != -1)
    {
      k = j;
      j = _nextsib(j);
    }
    return makeNodeHandle(k);
  }
  
  public abstract int getAttributeNode(int paramInt, String paramString1, String paramString2);
  
  public int getFirstAttribute(int paramInt)
  {
    int i = makeNodeIdentity(paramInt);
    return makeNodeHandle(getFirstAttributeIdentity(i));
  }
  
  protected int getFirstAttributeIdentity(int paramInt)
  {
    int i = _type(paramInt);
    if (1 == i) {
      while (-1 != (paramInt = getNextNodeIdentity(paramInt)))
      {
        i = _type(paramInt);
        if (i == 2) {
          return paramInt;
        }
        if (13 != i) {
          break;
        }
      }
    }
    return -1;
  }
  
  protected int getTypedAttribute(int paramInt1, int paramInt2)
  {
    int i = getNodeType(paramInt1);
    if (1 == i)
    {
      int j = makeNodeIdentity(paramInt1);
      while (-1 != (j = getNextNodeIdentity(j)))
      {
        i = _type(j);
        if (i == 2)
        {
          if (_exptype(j) == paramInt2) {
            return makeNodeHandle(j);
          }
        }
        else if (13 != i) {
          break;
        }
      }
    }
    return -1;
  }
  
  public int getNextSibling(int paramInt)
  {
    if (paramInt == -1) {
      return -1;
    }
    return makeNodeHandle(_nextsib(makeNodeIdentity(paramInt)));
  }
  
  public int getTypedNextSibling(int paramInt1, int paramInt2)
  {
    if (paramInt1 == -1) {
      return -1;
    }
    int i = makeNodeIdentity(paramInt1);
    int j;
    while (((i = _nextsib(i)) != -1) && ((j = _exptype(i)) != paramInt2) && (m_expandedNameTable.getType(j) != paramInt2)) {}
    return i == -1 ? -1 : makeNodeHandle(i);
  }
  
  public int getPreviousSibling(int paramInt)
  {
    if (paramInt == -1) {
      return -1;
    }
    if (m_prevsib != null) {
      return makeNodeHandle(_prevsib(makeNodeIdentity(paramInt)));
    }
    int i = makeNodeIdentity(paramInt);
    int j = _parent(i);
    int k = _firstch(j);
    int m = -1;
    while (k != i)
    {
      m = k;
      k = _nextsib(k);
    }
    return makeNodeHandle(m);
  }
  
  public int getNextAttribute(int paramInt)
  {
    int i = makeNodeIdentity(paramInt);
    if (_type(i) == 2) {
      return makeNodeHandle(getNextAttributeIdentity(i));
    }
    return -1;
  }
  
  protected int getNextAttributeIdentity(int paramInt)
  {
    while (-1 != (paramInt = getNextNodeIdentity(paramInt)))
    {
      int i = _type(paramInt);
      if (i == 2) {
        return paramInt;
      }
      if (i != 13) {
        break;
      }
    }
    return -1;
  }
  
  protected void declareNamespaceInContext(int paramInt1, int paramInt2)
  {
    SuballocatedIntVector localSuballocatedIntVector1 = null;
    if (m_namespaceDeclSets == null)
    {
      m_namespaceDeclSetElements = new SuballocatedIntVector(32);
      m_namespaceDeclSetElements.addElement(paramInt1);
      m_namespaceDeclSets = new Vector();
      localSuballocatedIntVector1 = new SuballocatedIntVector(32);
      m_namespaceDeclSets.addElement(localSuballocatedIntVector1);
    }
    else
    {
      int i = m_namespaceDeclSetElements.size() - 1;
      if ((i >= 0) && (paramInt1 == m_namespaceDeclSetElements.elementAt(i))) {
        localSuballocatedIntVector1 = (SuballocatedIntVector)m_namespaceDeclSets.elementAt(i);
      }
    }
    if (localSuballocatedIntVector1 == null)
    {
      m_namespaceDeclSetElements.addElement(paramInt1);
      SuballocatedIntVector localSuballocatedIntVector2 = findNamespaceContext(_parent(paramInt1));
      if (localSuballocatedIntVector2 != null)
      {
        k = localSuballocatedIntVector2.size();
        localSuballocatedIntVector1 = new SuballocatedIntVector(Math.max(Math.min(k + 16, 2048), 32));
        for (int m = 0; m < k; m++) {
          localSuballocatedIntVector1.addElement(localSuballocatedIntVector2.elementAt(m));
        }
      }
      else
      {
        localSuballocatedIntVector1 = new SuballocatedIntVector(32);
      }
      m_namespaceDeclSets.addElement(localSuballocatedIntVector1);
    }
    int j = _exptype(paramInt2);
    for (int k = localSuballocatedIntVector1.size() - 1; k >= 0; k--) {
      if (j == getExpandedTypeID(localSuballocatedIntVector1.elementAt(k)))
      {
        localSuballocatedIntVector1.setElementAt(makeNodeHandle(paramInt2), k);
        return;
      }
    }
    localSuballocatedIntVector1.addElement(makeNodeHandle(paramInt2));
  }
  
  protected SuballocatedIntVector findNamespaceContext(int paramInt)
  {
    if (null != m_namespaceDeclSetElements)
    {
      int i = findInSortedSuballocatedIntVector(m_namespaceDeclSetElements, paramInt);
      if (i >= 0) {
        return (SuballocatedIntVector)m_namespaceDeclSets.elementAt(i);
      }
      if (i == -1) {
        return null;
      }
      i = -1 - i;
      int j = m_namespaceDeclSetElements.elementAt(--i);
      int k = _parent(paramInt);
      if ((i == 0) && (j < k))
      {
        int m = getDocumentRoot(makeNodeHandle(paramInt));
        int n = makeNodeIdentity(m);
        int i1;
        if (getNodeType(m) == 9)
        {
          int i2 = _firstch(n);
          i1 = i2 != -1 ? i2 : n;
        }
        else
        {
          i1 = n;
        }
        if (j == i1) {
          return (SuballocatedIntVector)m_namespaceDeclSets.elementAt(i);
        }
      }
      while ((i >= 0) && (k > 0))
      {
        if (j == k) {
          return (SuballocatedIntVector)m_namespaceDeclSets.elementAt(i);
        }
        if (j < k)
        {
          do
          {
            k = _parent(k);
          } while (j < k);
        }
        else
        {
          if (i <= 0) {
            break;
          }
          j = m_namespaceDeclSetElements.elementAt(--i);
        }
      }
    }
    return null;
  }
  
  protected int findInSortedSuballocatedIntVector(SuballocatedIntVector paramSuballocatedIntVector, int paramInt)
  {
    int i = 0;
    if (paramSuballocatedIntVector != null)
    {
      int j = 0;
      int k = paramSuballocatedIntVector.size() - 1;
      while (j <= k)
      {
        i = (j + k) / 2;
        int m = paramInt - paramSuballocatedIntVector.elementAt(i);
        if (m == 0) {
          return i;
        }
        if (m < 0) {
          k = i - 1;
        } else {
          j = i + 1;
        }
      }
      if (j > i) {
        i = j;
      }
    }
    return -1 - i;
  }
  
  public int getFirstNamespaceNode(int paramInt, boolean paramBoolean)
  {
    if (paramBoolean)
    {
      i = makeNodeIdentity(paramInt);
      if (_type(i) == 1)
      {
        SuballocatedIntVector localSuballocatedIntVector = findNamespaceContext(i);
        if ((localSuballocatedIntVector == null) || (localSuballocatedIntVector.size() < 1)) {
          return -1;
        }
        return localSuballocatedIntVector.elementAt(0);
      }
      return -1;
    }
    int i = makeNodeIdentity(paramInt);
    if (_type(i) == 1)
    {
      while (-1 != (i = getNextNodeIdentity(i)))
      {
        int j = _type(i);
        if (j == 13) {
          return makeNodeHandle(i);
        }
        if (2 != j) {
          break;
        }
      }
      return -1;
    }
    return -1;
  }
  
  public int getNextNamespaceNode(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    int j;
    if (paramBoolean)
    {
      SuballocatedIntVector localSuballocatedIntVector = findNamespaceContext(makeNodeIdentity(paramInt1));
      if (localSuballocatedIntVector == null) {
        return -1;
      }
      j = 1 + localSuballocatedIntVector.indexOf(paramInt2);
      if ((j <= 0) || (j == localSuballocatedIntVector.size())) {
        return -1;
      }
      return localSuballocatedIntVector.elementAt(j);
    }
    int i = makeNodeIdentity(paramInt2);
    while (-1 != (i = getNextNodeIdentity(i)))
    {
      j = _type(i);
      if (j == 13) {
        return makeNodeHandle(i);
      }
      if (j != 2) {
        break;
      }
    }
    return -1;
  }
  
  public int getParent(int paramInt)
  {
    int i = makeNodeIdentity(paramInt);
    if (i > 0) {
      return makeNodeHandle(_parent(i));
    }
    return -1;
  }
  
  public int getDocument()
  {
    return m_dtmIdent.elementAt(0);
  }
  
  public int getOwnerDocument(int paramInt)
  {
    if (9 == getNodeType(paramInt)) {
      return -1;
    }
    return getDocumentRoot(paramInt);
  }
  
  public int getDocumentRoot(int paramInt)
  {
    return getManager().getDTM(paramInt).getDocument();
  }
  
  public abstract XMLString getStringValue(int paramInt);
  
  public int getStringValueChunkCount(int paramInt)
  {
    error(XMLMessages.createXMLMessage("ER_METHOD_NOT_SUPPORTED", null));
    return 0;
  }
  
  public char[] getStringValueChunk(int paramInt1, int paramInt2, int[] paramArrayOfInt)
  {
    error(XMLMessages.createXMLMessage("ER_METHOD_NOT_SUPPORTED", null));
    return null;
  }
  
  public int getExpandedTypeID(int paramInt)
  {
    int i = makeNodeIdentity(paramInt);
    if (i == -1) {
      return -1;
    }
    return _exptype(i);
  }
  
  public int getExpandedTypeID(String paramString1, String paramString2, int paramInt)
  {
    ExpandedNameTable localExpandedNameTable = m_expandedNameTable;
    return localExpandedNameTable.getExpandedTypeID(paramString1, paramString2, paramInt);
  }
  
  public String getLocalNameFromExpandedNameID(int paramInt)
  {
    return m_expandedNameTable.getLocalName(paramInt);
  }
  
  public String getNamespaceFromExpandedNameID(int paramInt)
  {
    return m_expandedNameTable.getNamespace(paramInt);
  }
  
  public int getNamespaceType(int paramInt)
  {
    int i = makeNodeIdentity(paramInt);
    int j = _exptype(i);
    return m_expandedNameTable.getNamespaceID(j);
  }
  
  public abstract String getNodeName(int paramInt);
  
  public String getNodeNameX(int paramInt)
  {
    error(XMLMessages.createXMLMessage("ER_METHOD_NOT_SUPPORTED", null));
    return null;
  }
  
  public abstract String getLocalName(int paramInt);
  
  public abstract String getPrefix(int paramInt);
  
  public abstract String getNamespaceURI(int paramInt);
  
  public abstract String getNodeValue(int paramInt);
  
  public short getNodeType(int paramInt)
  {
    if (paramInt == -1) {
      return -1;
    }
    return m_expandedNameTable.getType(_exptype(makeNodeIdentity(paramInt)));
  }
  
  public short getLevel(int paramInt)
  {
    int i = makeNodeIdentity(paramInt);
    return (short)(_level(i) + 1);
  }
  
  public int getNodeIdent(int paramInt)
  {
    return makeNodeIdentity(paramInt);
  }
  
  public int getNodeHandle(int paramInt)
  {
    return makeNodeHandle(paramInt);
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
    return m_documentBaseURI;
  }
  
  public String getDocumentEncoding(int paramInt)
  {
    return "UTF-8";
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
    return true;
  }
  
  public abstract String getDocumentTypeDeclarationSystemIdentifier();
  
  public abstract String getDocumentTypeDeclarationPublicIdentifier();
  
  public abstract int getElementById(String paramString);
  
  public abstract String getUnparsedEntityURI(String paramString);
  
  public boolean supportsPreStripping()
  {
    return true;
  }
  
  public boolean isNodeAfter(int paramInt1, int paramInt2)
  {
    int i = makeNodeIdentity(paramInt1);
    int j = makeNodeIdentity(paramInt2);
    return (i != -1) && (j != -1) && (i <= j);
  }
  
  public boolean isCharacterElementContentWhitespace(int paramInt)
  {
    return false;
  }
  
  public boolean isDocumentAllDeclarationsProcessed(int paramInt)
  {
    return true;
  }
  
  public abstract boolean isAttributeSpecified(int paramInt);
  
  public abstract void dispatchCharactersEvents(int paramInt, ContentHandler paramContentHandler, boolean paramBoolean)
    throws SAXException;
  
  public abstract void dispatchToEvents(int paramInt, ContentHandler paramContentHandler)
    throws SAXException;
  
  public Node getNode(int paramInt)
  {
    return new DTMNodeProxy(this, paramInt);
  }
  
  public void appendChild(int paramInt, boolean paramBoolean1, boolean paramBoolean2)
  {
    error(XMLMessages.createXMLMessage("ER_METHOD_NOT_SUPPORTED", null));
  }
  
  public void appendTextChild(String paramString)
  {
    error(XMLMessages.createXMLMessage("ER_METHOD_NOT_SUPPORTED", null));
  }
  
  protected void error(String paramString)
  {
    throw new DTMException(paramString);
  }
  
  protected boolean getShouldStripWhitespace()
  {
    return m_shouldStripWS;
  }
  
  protected void pushShouldStripWhitespace(boolean paramBoolean)
  {
    m_shouldStripWS = paramBoolean;
    if (null != m_shouldStripWhitespaceStack) {
      m_shouldStripWhitespaceStack.push(paramBoolean);
    }
  }
  
  protected void popShouldStripWhitespace()
  {
    if (null != m_shouldStripWhitespaceStack) {
      m_shouldStripWS = m_shouldStripWhitespaceStack.popAndTop();
    }
  }
  
  protected void setShouldStripWhitespace(boolean paramBoolean)
  {
    m_shouldStripWS = paramBoolean;
    if (null != m_shouldStripWhitespaceStack) {
      m_shouldStripWhitespaceStack.setTop(paramBoolean);
    }
  }
  
  public void documentRegistration() {}
  
  public void documentRelease() {}
  
  public void migrateTo(DTMManager paramDTMManager)
  {
    m_mgr = paramDTMManager;
    if ((paramDTMManager instanceof DTMManagerDefault)) {
      m_mgrDefault = ((DTMManagerDefault)paramDTMManager);
    }
  }
  
  public DTMManager getManager()
  {
    return m_mgr;
  }
  
  public SuballocatedIntVector getDTMIDs()
  {
    if (m_mgr == null) {
      return null;
    }
    return m_dtmIdent;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\dtm\ref\DTMDefaultBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */