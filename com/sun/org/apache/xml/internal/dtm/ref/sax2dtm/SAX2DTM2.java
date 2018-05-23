package com.sun.org.apache.xml.internal.dtm.ref.sax2dtm;

import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.dtm.DTMException;
import com.sun.org.apache.xml.internal.dtm.DTMManager;
import com.sun.org.apache.xml.internal.dtm.DTMWSFilter;
import com.sun.org.apache.xml.internal.dtm.ref.DTMDefaultBaseIterators.InternalAxisIteratorBase;
import com.sun.org.apache.xml.internal.dtm.ref.DTMDefaultBaseIterators.RootIterator;
import com.sun.org.apache.xml.internal.dtm.ref.DTMDefaultBaseIterators.SingletonIterator;
import com.sun.org.apache.xml.internal.dtm.ref.DTMStringPool;
import com.sun.org.apache.xml.internal.dtm.ref.ExpandedNameTable;
import com.sun.org.apache.xml.internal.dtm.ref.ExtendedType;
import com.sun.org.apache.xml.internal.res.XMLMessages;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import com.sun.org.apache.xml.internal.utils.FastStringBuffer;
import com.sun.org.apache.xml.internal.utils.IntStack;
import com.sun.org.apache.xml.internal.utils.SuballocatedIntVector;
import com.sun.org.apache.xml.internal.utils.XMLString;
import com.sun.org.apache.xml.internal.utils.XMLStringDefault;
import com.sun.org.apache.xml.internal.utils.XMLStringFactory;
import java.util.Vector;
import javax.xml.transform.Source;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class SAX2DTM2
  extends SAX2DTM
{
  private int[] m_exptype_map0;
  private int[] m_nextsib_map0;
  private int[] m_firstch_map0;
  private int[] m_parent_map0;
  private int[][] m_exptype_map;
  private int[][] m_nextsib_map;
  private int[][] m_firstch_map;
  private int[][] m_parent_map;
  protected ExtendedType[] m_extendedTypes;
  protected Vector m_values;
  private int m_valueIndex = 0;
  private int m_maxNodeIndex;
  protected int m_SHIFT;
  protected int m_MASK;
  protected int m_blocksize;
  protected static final int TEXT_LENGTH_BITS = 10;
  protected static final int TEXT_OFFSET_BITS = 21;
  protected static final int TEXT_LENGTH_MAX = 1023;
  protected static final int TEXT_OFFSET_MAX = 2097151;
  protected boolean m_buildIdIndex = true;
  private static final String EMPTY_STR = "";
  private static final XMLString EMPTY_XML_STR = new XMLStringDefault("");
  
  public SAX2DTM2(DTMManager paramDTMManager, Source paramSource, int paramInt, DTMWSFilter paramDTMWSFilter, XMLStringFactory paramXMLStringFactory, boolean paramBoolean)
  {
    this(paramDTMManager, paramSource, paramInt, paramDTMWSFilter, paramXMLStringFactory, paramBoolean, 512, true, true, false);
  }
  
  public SAX2DTM2(DTMManager paramDTMManager, Source paramSource, int paramInt1, DTMWSFilter paramDTMWSFilter, XMLStringFactory paramXMLStringFactory, boolean paramBoolean1, int paramInt2, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4)
  {
    super(paramDTMManager, paramSource, paramInt1, paramDTMWSFilter, paramXMLStringFactory, paramBoolean1, paramInt2, paramBoolean2, paramBoolean4);
    for (int i = 0; paramInt2 >>>= 1 != 0; i++) {}
    m_blocksize = (1 << i);
    m_SHIFT = i;
    m_MASK = (m_blocksize - 1);
    m_buildIdIndex = paramBoolean3;
    m_values = new Vector(32, 512);
    m_maxNodeIndex = 65536;
    m_exptype_map0 = m_exptype.getMap0();
    m_nextsib_map0 = m_nextsib.getMap0();
    m_firstch_map0 = m_firstch.getMap0();
    m_parent_map0 = m_parent.getMap0();
  }
  
  public final int _exptype(int paramInt)
  {
    return m_exptype.elementAt(paramInt);
  }
  
  public final int _exptype2(int paramInt)
  {
    if (paramInt < m_blocksize) {
      return m_exptype_map0[paramInt];
    }
    return m_exptype_map[(paramInt >>> m_SHIFT)][(paramInt & m_MASK)];
  }
  
  public final int _nextsib2(int paramInt)
  {
    if (paramInt < m_blocksize) {
      return m_nextsib_map0[paramInt];
    }
    return m_nextsib_map[(paramInt >>> m_SHIFT)][(paramInt & m_MASK)];
  }
  
  public final int _firstch2(int paramInt)
  {
    if (paramInt < m_blocksize) {
      return m_firstch_map0[paramInt];
    }
    return m_firstch_map[(paramInt >>> m_SHIFT)][(paramInt & m_MASK)];
  }
  
  public final int _parent2(int paramInt)
  {
    if (paramInt < m_blocksize) {
      return m_parent_map0[paramInt];
    }
    return m_parent_map[(paramInt >>> m_SHIFT)][(paramInt & m_MASK)];
  }
  
  public final int _type2(int paramInt)
  {
    int i;
    if (paramInt < m_blocksize) {
      i = m_exptype_map0[paramInt];
    } else {
      i = m_exptype_map[(paramInt >>> m_SHIFT)][(paramInt & m_MASK)];
    }
    if (-1 != i) {
      return m_extendedTypes[i].getNodeType();
    }
    return -1;
  }
  
  public final int getExpandedTypeID2(int paramInt)
  {
    int i = makeNodeIdentity(paramInt);
    if (i != -1)
    {
      if (i < m_blocksize) {
        return m_exptype_map0[i];
      }
      return m_exptype_map[(i >>> m_SHIFT)][(i & m_MASK)];
    }
    return -1;
  }
  
  public final int _exptype2Type(int paramInt)
  {
    if (-1 != paramInt) {
      return m_extendedTypes[paramInt].getNodeType();
    }
    return -1;
  }
  
  public int getIdForNamespace(String paramString)
  {
    int i = m_values.indexOf(paramString);
    if (i < 0)
    {
      m_values.addElement(paramString);
      return m_valueIndex++;
    }
    return i;
  }
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
    throws SAXException
  {
    charactersFlush();
    int i = m_expandedNameTable.getExpandedTypeID(paramString1, paramString2, 1);
    int j = paramString3.length() != paramString2.length() ? m_valuesOrPrefixes.stringToIndex(paramString3) : 0;
    int k = addNode(1, i, m_parents.peek(), m_previous, j, true);
    if (m_indexing) {
      indexNode(i, k);
    }
    m_parents.push(k);
    String str1 = m_contextIndexes.peek();
    String str2 = m_prefixMappings.size();
    String str3;
    if (!m_pastFirstElement)
    {
      str3 = "xml";
      str4 = "http://www.w3.org/XML/1998/namespace";
      i = m_expandedNameTable.getExpandedTypeID(null, str3, 13);
      m_values.addElement(str4);
      int n = m_valueIndex++;
      addNode(13, i, k, -1, n, false);
      m_pastFirstElement = true;
    }
    for (String str4 = str1; str4 < str2; str4 += 2)
    {
      str3 = (String)m_prefixMappings.elementAt(str4);
      if (str3 != null)
      {
        String str5 = (String)m_prefixMappings.elementAt(str4 + 1);
        i = m_expandedNameTable.getExpandedTypeID(null, str3, 13);
        m_values.addElement(str5);
        int i2 = m_valueIndex++;
        addNode(13, i, k, -1, i2, false);
      }
    }
    int m = paramAttributes.getLength();
    for (int i1 = 0; i1 < m; i1++)
    {
      String str6 = paramAttributes.getURI(i1);
      String str7 = paramAttributes.getQName(i1);
      String str8 = paramAttributes.getValue(i1);
      String str9 = paramAttributes.getLocalName(i1);
      int i3;
      if ((null != str7) && ((str7.equals("xmlns")) || (str7.startsWith("xmlns:"))))
      {
        str3 = getPrefix(str7, str6);
        if (declAlreadyDeclared(str3)) {
          continue;
        }
        i3 = 13;
      }
      else
      {
        i3 = 2;
        if ((m_buildIdIndex) && (paramAttributes.getType(i1).equalsIgnoreCase("ID"))) {
          setIDAttribute(str8, k);
        }
      }
      if (null == str8) {
        str8 = "";
      }
      m_values.addElement(str8);
      int i4 = m_valueIndex++;
      if (str9.length() != str7.length())
      {
        j = m_valuesOrPrefixes.stringToIndex(str7);
        int i5 = m_data.size();
        m_data.addElement(j);
        m_data.addElement(i4);
        i4 = -i5;
      }
      i = m_expandedNameTable.getExpandedTypeID(str6, str9, i3);
      addNode(i3, i, k, -1, i4, false);
    }
    if (null != m_wsfilter)
    {
      i1 = m_wsfilter.getShouldStripSpace(makeNodeHandle(k), this);
      boolean bool = 2 == i1 ? true : 3 == i1 ? getShouldStripWhitespace() : false;
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
    m_previous = m_parents.pop();
    popShouldStripWhitespace();
  }
  
  public void comment(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SAXException
  {
    if (m_insideDTD) {
      return;
    }
    charactersFlush();
    m_values.addElement(new String(paramArrayOfChar, paramInt1, paramInt2));
    int i = m_valueIndex++;
    m_previous = addNode(8, 8, m_parents.peek(), m_previous, i, false);
  }
  
  public void startDocument()
    throws SAXException
  {
    int i = addNode(9, 9, -1, -1, 0, true);
    m_parents.push(i);
    m_previous = -1;
    m_contextIndexes.push(m_prefixMappings.size());
  }
  
  public void endDocument()
    throws SAXException
  {
    super.endDocument();
    m_exptype.addElement(-1);
    m_parent.addElement(-1);
    m_nextsib.addElement(-1);
    m_firstch.addElement(-1);
    m_extendedTypes = m_expandedNameTable.getExtendedTypes();
    m_exptype_map = m_exptype.getMap();
    m_nextsib_map = m_nextsib.getMap();
    m_firstch_map = m_firstch.getMap();
    m_parent_map = m_parent.getMap();
  }
  
  protected final int addNode(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, boolean paramBoolean)
  {
    int i = m_size++;
    if (i == m_maxNodeIndex)
    {
      addNewDTMID(i);
      m_maxNodeIndex += 65536;
    }
    m_firstch.addElement(-1);
    m_nextsib.addElement(-1);
    m_parent.addElement(paramInt3);
    m_exptype.addElement(paramInt2);
    m_dataOrQName.addElement(paramInt5);
    if (m_prevsib != null) {
      m_prevsib.addElement(paramInt4);
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
      if (-1 != paramInt4) {
        m_nextsib.setElementAt(i, paramInt4);
      } else if (-1 != paramInt3) {
        m_firstch.setElementAt(i, paramInt3);
      }
      break;
    }
    return i;
  }
  
  protected final void charactersFlush()
  {
    if (m_textPendingStart >= 0)
    {
      int i = m_chars.size() - m_textPendingStart;
      boolean bool = false;
      if (getShouldStripWhitespace()) {
        bool = m_chars.isWhitespace(m_textPendingStart, i);
      }
      if (bool) {
        m_chars.setLength(m_textPendingStart);
      } else if (i > 0) {
        if ((i <= 1023) && (m_textPendingStart <= 2097151))
        {
          m_previous = addNode(m_coalescedTextType, 3, m_parents.peek(), m_previous, i + (m_textPendingStart << 10), false);
        }
        else
        {
          int j = m_data.size();
          m_previous = addNode(m_coalescedTextType, 3, m_parents.peek(), m_previous, -j, false);
          m_data.addElement(m_textPendingStart);
          m_data.addElement(i);
        }
      }
      m_textPendingStart = -1;
      m_textType = (m_coalescedTextType = 3);
    }
  }
  
  public void processingInstruction(String paramString1, String paramString2)
    throws SAXException
  {
    charactersFlush();
    int i = m_data.size();
    m_previous = addNode(7, 7, m_parents.peek(), m_previous, -i, false);
    m_data.addElement(m_valuesOrPrefixes.stringToIndex(paramString1));
    m_values.addElement(paramString2);
    m_data.addElement(m_valueIndex++);
  }
  
  public final int getFirstAttribute(int paramInt)
  {
    int i = makeNodeIdentity(paramInt);
    if (i == -1) {
      return -1;
    }
    int j = _type2(i);
    if (1 == j) {
      for (;;)
      {
        i++;
        j = _type2(i);
        if (j == 2) {
          return makeNodeHandle(i);
        }
        if (13 != j) {
          break;
        }
      }
    }
    return -1;
  }
  
  protected int getFirstAttributeIdentity(int paramInt)
  {
    if (paramInt == -1) {
      return -1;
    }
    int i = _type2(paramInt);
    if (1 == i) {
      for (;;)
      {
        paramInt++;
        i = _type2(paramInt);
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
  
  protected int getNextAttributeIdentity(int paramInt)
  {
    for (;;)
    {
      paramInt++;
      int i = _type2(paramInt);
      if (i == 2) {
        return paramInt;
      }
      if (i != 13) {
        break;
      }
    }
    return -1;
  }
  
  protected final int getTypedAttribute(int paramInt1, int paramInt2)
  {
    int i = makeNodeIdentity(paramInt1);
    if (i == -1) {
      return -1;
    }
    int j = _type2(i);
    if (1 == j) {
      for (;;)
      {
        i++;
        int k = _exptype2(i);
        if (k != -1) {
          j = m_extendedTypes[k].getNodeType();
        } else {
          return -1;
        }
        if (j == 2)
        {
          if (k == paramInt2) {
            return makeNodeHandle(i);
          }
        }
        else if (13 != j) {
          break;
        }
      }
    }
    return -1;
  }
  
  public String getLocalName(int paramInt)
  {
    int i = _exptype(makeNodeIdentity(paramInt));
    if (i == 7)
    {
      int j = _dataOrQName(makeNodeIdentity(paramInt));
      j = m_data.elementAt(-j);
      return m_valuesOrPrefixes.indexToString(j);
    }
    return m_expandedNameTable.getLocalName(i);
  }
  
  public final String getNodeNameX(int paramInt)
  {
    int i = makeNodeIdentity(paramInt);
    int j = _exptype2(i);
    if (j == 7)
    {
      int k = _dataOrQName(i);
      k = m_data.elementAt(-k);
      return m_valuesOrPrefixes.indexToString(k);
    }
    ExtendedType localExtendedType = m_extendedTypes[j];
    if (localExtendedType.getNamespace().length() == 0) {
      return localExtendedType.getLocalName();
    }
    int m = m_dataOrQName.elementAt(i);
    if (m == 0) {
      return localExtendedType.getLocalName();
    }
    if (m < 0)
    {
      m = -m;
      m = m_data.elementAt(m);
    }
    return m_valuesOrPrefixes.indexToString(m);
  }
  
  public String getNodeName(int paramInt)
  {
    int i = makeNodeIdentity(paramInt);
    int j = _exptype2(i);
    ExtendedType localExtendedType = m_extendedTypes[j];
    if (localExtendedType.getNamespace().length() == 0)
    {
      k = localExtendedType.getNodeType();
      String str = localExtendedType.getLocalName();
      if (k == 13)
      {
        if (str.length() == 0) {
          return "xmlns";
        }
        return "xmlns:" + str;
      }
      if (k == 7)
      {
        int m = _dataOrQName(i);
        m = m_data.elementAt(-m);
        return m_valuesOrPrefixes.indexToString(m);
      }
      if (str.length() == 0) {
        return getFixedNames(k);
      }
      return str;
    }
    int k = m_dataOrQName.elementAt(i);
    if (k == 0) {
      return localExtendedType.getLocalName();
    }
    if (k < 0)
    {
      k = -k;
      k = m_data.elementAt(k);
    }
    return m_valuesOrPrefixes.indexToString(k);
  }
  
  public XMLString getStringValue(int paramInt)
  {
    int i = makeNodeIdentity(paramInt);
    if (i == -1) {
      return EMPTY_XML_STR;
    }
    int j = _type2(i);
    if ((j == 1) || (j == 9))
    {
      k = i;
      i = _firstch2(i);
      if (-1 != i)
      {
        int m = -1;
        int n = 0;
        do
        {
          j = _exptype2(i);
          if ((j == 3) || (j == 4))
          {
            int i1 = m_dataOrQName.elementAt(i);
            if (i1 >= 0)
            {
              if (-1 == m) {
                m = i1 >>> 10;
              }
              n += (i1 & 0x3FF);
            }
            else
            {
              if (-1 == m) {
                m = m_data.elementAt(-i1);
              }
              n += m_data.elementAt(-i1 + 1);
            }
          }
          i++;
        } while (_parent2(i) >= k);
        if (n > 0)
        {
          if (m_xstrf != null) {
            return m_xstrf.newstr(m_chars, m, n);
          }
          return new XMLStringDefault(m_chars.getString(m, n));
        }
        return EMPTY_XML_STR;
      }
      return EMPTY_XML_STR;
    }
    if ((3 == j) || (4 == j))
    {
      k = m_dataOrQName.elementAt(i);
      if (k >= 0)
      {
        if (m_xstrf != null) {
          return m_xstrf.newstr(m_chars, k >>> 10, k & 0x3FF);
        }
        return new XMLStringDefault(m_chars.getString(k >>> 10, k & 0x3FF));
      }
      if (m_xstrf != null) {
        return m_xstrf.newstr(m_chars, m_data.elementAt(-k), m_data.elementAt(-k + 1));
      }
      return new XMLStringDefault(m_chars.getString(m_data.elementAt(-k), m_data.elementAt(-k + 1)));
    }
    int k = m_dataOrQName.elementAt(i);
    if (k < 0)
    {
      k = -k;
      k = m_data.elementAt(k + 1);
    }
    if (m_xstrf != null) {
      return m_xstrf.newstr((String)m_values.elementAt(k));
    }
    return new XMLStringDefault((String)m_values.elementAt(k));
  }
  
  public final String getStringValueX(int paramInt)
  {
    int i = makeNodeIdentity(paramInt);
    if (i == -1) {
      return "";
    }
    int j = _type2(i);
    if ((j == 1) || (j == 9))
    {
      k = i;
      i = _firstch2(i);
      if (-1 != i)
      {
        int m = -1;
        int n = 0;
        do
        {
          j = _exptype2(i);
          if ((j == 3) || (j == 4))
          {
            int i1 = m_dataOrQName.elementAt(i);
            if (i1 >= 0)
            {
              if (-1 == m) {
                m = i1 >>> 10;
              }
              n += (i1 & 0x3FF);
            }
            else
            {
              if (-1 == m) {
                m = m_data.elementAt(-i1);
              }
              n += m_data.elementAt(-i1 + 1);
            }
          }
          i++;
        } while (_parent2(i) >= k);
        if (n > 0) {
          return m_chars.getString(m, n);
        }
        return "";
      }
      return "";
    }
    if ((3 == j) || (4 == j))
    {
      k = m_dataOrQName.elementAt(i);
      if (k >= 0) {
        return m_chars.getString(k >>> 10, k & 0x3FF);
      }
      return m_chars.getString(m_data.elementAt(-k), m_data.elementAt(-k + 1));
    }
    int k = m_dataOrQName.elementAt(i);
    if (k < 0)
    {
      k = -k;
      k = m_data.elementAt(k + 1);
    }
    return (String)m_values.elementAt(k);
  }
  
  public String getStringValue()
  {
    int i = _firstch2(0);
    if (i == -1) {
      return "";
    }
    if ((_exptype2(i) == 3) && (_nextsib2(i) == -1))
    {
      int j = m_dataOrQName.elementAt(i);
      if (j >= 0) {
        return m_chars.getString(j >>> 10, j & 0x3FF);
      }
      return m_chars.getString(m_data.elementAt(-j), m_data.elementAt(-j + 1));
    }
    return getStringValueX(getDocument());
  }
  
  public final void dispatchCharactersEvents(int paramInt, ContentHandler paramContentHandler, boolean paramBoolean)
    throws SAXException
  {
    int i = makeNodeIdentity(paramInt);
    if (i == -1) {
      return;
    }
    int j = _type2(i);
    int k;
    if ((j == 1) || (j == 9))
    {
      k = i;
      i = _firstch2(i);
      if (-1 != i)
      {
        int m = -1;
        int n = 0;
        do
        {
          j = _exptype2(i);
          if ((j == 3) || (j == 4))
          {
            int i1 = m_dataOrQName.elementAt(i);
            if (i1 >= 0)
            {
              if (-1 == m) {
                m = i1 >>> 10;
              }
              n += (i1 & 0x3FF);
            }
            else
            {
              if (-1 == m) {
                m = m_data.elementAt(-i1);
              }
              n += m_data.elementAt(-i1 + 1);
            }
          }
          i++;
        } while (_parent2(i) >= k);
        if (n > 0) {
          if (paramBoolean) {
            m_chars.sendNormalizedSAXcharacters(paramContentHandler, m, n);
          } else {
            m_chars.sendSAXcharacters(paramContentHandler, m, n);
          }
        }
      }
    }
    else if ((3 == j) || (4 == j))
    {
      k = m_dataOrQName.elementAt(i);
      if (k >= 0)
      {
        if (paramBoolean) {
          m_chars.sendNormalizedSAXcharacters(paramContentHandler, k >>> 10, k & 0x3FF);
        } else {
          m_chars.sendSAXcharacters(paramContentHandler, k >>> 10, k & 0x3FF);
        }
      }
      else if (paramBoolean) {
        m_chars.sendNormalizedSAXcharacters(paramContentHandler, m_data.elementAt(-k), m_data.elementAt(-k + 1));
      } else {
        m_chars.sendSAXcharacters(paramContentHandler, m_data.elementAt(-k), m_data.elementAt(-k + 1));
      }
    }
    else
    {
      k = m_dataOrQName.elementAt(i);
      if (k < 0)
      {
        k = -k;
        k = m_data.elementAt(k + 1);
      }
      String str = (String)m_values.elementAt(k);
      if (paramBoolean) {
        FastStringBuffer.sendNormalizedSAXcharacters(str.toCharArray(), 0, str.length(), paramContentHandler);
      } else {
        paramContentHandler.characters(str.toCharArray(), 0, str.length());
      }
    }
  }
  
  public String getNodeValue(int paramInt)
  {
    int i = makeNodeIdentity(paramInt);
    int j = _type2(i);
    if ((j == 3) || (j == 4))
    {
      k = _dataOrQName(i);
      if (k > 0) {
        return m_chars.getString(k >>> 10, k & 0x3FF);
      }
      return m_chars.getString(m_data.elementAt(-k), m_data.elementAt(-k + 1));
    }
    if ((1 == j) || (11 == j) || (9 == j)) {
      return null;
    }
    int k = m_dataOrQName.elementAt(i);
    if (k < 0)
    {
      k = -k;
      k = m_data.elementAt(k + 1);
    }
    return (String)m_values.elementAt(k);
  }
  
  protected final void copyTextNode(int paramInt, SerializationHandler paramSerializationHandler)
    throws SAXException
  {
    if (paramInt != -1)
    {
      int i = m_dataOrQName.elementAt(paramInt);
      if (i >= 0) {
        m_chars.sendSAXcharacters(paramSerializationHandler, i >>> 10, i & 0x3FF);
      } else {
        m_chars.sendSAXcharacters(paramSerializationHandler, m_data.elementAt(-i), m_data.elementAt(-i + 1));
      }
    }
  }
  
  protected final String copyElement(int paramInt1, int paramInt2, SerializationHandler paramSerializationHandler)
    throws SAXException
  {
    ExtendedType localExtendedType = m_extendedTypes[paramInt2];
    String str1 = localExtendedType.getNamespace();
    String str2 = localExtendedType.getLocalName();
    if (str1.length() == 0)
    {
      paramSerializationHandler.startElement(str2);
      return str2;
    }
    int i = m_dataOrQName.elementAt(paramInt1);
    if (i == 0)
    {
      paramSerializationHandler.startElement(str2);
      paramSerializationHandler.namespaceAfterStartElement("", str1);
      return str2;
    }
    if (i < 0)
    {
      i = -i;
      i = m_data.elementAt(i);
    }
    String str3 = m_valuesOrPrefixes.indexToString(i);
    paramSerializationHandler.startElement(str3);
    int j = str3.indexOf(':');
    String str4;
    if (j > 0) {
      str4 = str3.substring(0, j);
    } else {
      str4 = null;
    }
    paramSerializationHandler.namespaceAfterStartElement(str4, str1);
    return str3;
  }
  
  protected final void copyNS(int paramInt, SerializationHandler paramSerializationHandler, boolean paramBoolean)
    throws SAXException
  {
    if ((m_namespaceDeclSetElements != null) && (m_namespaceDeclSetElements.size() == 1) && (m_namespaceDeclSets != null) && (((SuballocatedIntVector)m_namespaceDeclSets.elementAt(0)).size() == 1)) {
      return;
    }
    SuballocatedIntVector localSuballocatedIntVector = null;
    int i;
    if (paramBoolean)
    {
      localSuballocatedIntVector = findNamespaceContext(paramInt);
      if ((localSuballocatedIntVector == null) || (localSuballocatedIntVector.size() < 1)) {
        return;
      }
      i = makeNodeIdentity(localSuballocatedIntVector.elementAt(0));
    }
    else
    {
      i = getNextNamespaceNode2(paramInt);
    }
    int j = 1;
    while (i != -1)
    {
      int k = _exptype2(i);
      String str1 = m_extendedTypes[k].getLocalName();
      int m = m_dataOrQName.elementAt(i);
      if (m < 0)
      {
        m = -m;
        m = m_data.elementAt(m + 1);
      }
      String str2 = (String)m_values.elementAt(m);
      paramSerializationHandler.namespaceAfterStartElement(str1, str2);
      if (paramBoolean)
      {
        if (j < localSuballocatedIntVector.size())
        {
          i = makeNodeIdentity(localSuballocatedIntVector.elementAt(j));
          j++;
        }
      }
      else {
        i = getNextNamespaceNode2(i);
      }
    }
  }
  
  protected final int getNextNamespaceNode2(int paramInt)
  {
    int i;
    while ((i = _type2(++paramInt)) == 2) {}
    if (i == 13) {
      return paramInt;
    }
    return -1;
  }
  
  protected final void copyAttributes(int paramInt, SerializationHandler paramSerializationHandler)
    throws SAXException
  {
    for (int i = getFirstAttributeIdentity(paramInt); i != -1; i = getNextAttributeIdentity(i))
    {
      int j = _exptype2(i);
      copyAttribute(i, j, paramSerializationHandler);
    }
  }
  
  protected final void copyAttribute(int paramInt1, int paramInt2, SerializationHandler paramSerializationHandler)
    throws SAXException
  {
    ExtendedType localExtendedType = m_extendedTypes[paramInt2];
    String str1 = localExtendedType.getNamespace();
    String str2 = localExtendedType.getLocalName();
    String str3 = null;
    String str4 = null;
    int i = _dataOrQName(paramInt1);
    int j = i;
    if (i <= 0)
    {
      int k = m_data.elementAt(-i);
      j = m_data.elementAt(-i + 1);
      str4 = m_valuesOrPrefixes.indexToString(k);
      int m = str4.indexOf(':');
      if (m > 0) {
        str3 = str4.substring(0, m);
      }
    }
    if (str1.length() != 0) {
      paramSerializationHandler.namespaceAfterStartElement(str3, str1);
    }
    String str5 = str3 != null ? str4 : str2;
    String str6 = (String)m_values.elementAt(j);
    paramSerializationHandler.addAttribute(str1, str2, str5, "CDATA", str6);
  }
  
  public class AncestorIterator
    extends DTMDefaultBaseIterators.InternalAxisIteratorBase
  {
    private static final int m_blocksize = 32;
    int[] m_ancestors = new int[32];
    int m_size = 0;
    int m_ancestorsPos;
    int m_markedPos;
    int m_realStartNode;
    
    public AncestorIterator()
    {
      super();
    }
    
    public int getStartNode()
    {
      return m_realStartNode;
    }
    
    public final boolean isReverse()
    {
      return true;
    }
    
    public DTMAxisIterator cloneIterator()
    {
      _isRestartable = false;
      try
      {
        AncestorIterator localAncestorIterator = (AncestorIterator)super.clone();
        _startNode = _startNode;
        return localAncestorIterator;
      }
      catch (CloneNotSupportedException localCloneNotSupportedException)
      {
        throw new DTMException(XMLMessages.createXMLMessage("ER_ITERATOR_CLONE_NOT_SUPPORTED", null));
      }
    }
    
    public DTMAxisIterator setStartNode(int paramInt)
    {
      if (paramInt == 0) {
        paramInt = getDocument();
      }
      m_realStartNode = paramInt;
      if (_isRestartable)
      {
        int i = makeNodeIdentity(paramInt);
        m_size = 0;
        if (i == -1)
        {
          _currentNode = -1;
          m_ancestorsPos = 0;
          return this;
        }
        if (!_includeSelf)
        {
          i = _parent2(i);
          paramInt = makeNodeHandle(i);
        }
        _startNode = paramInt;
        while (i != -1)
        {
          if (m_size >= m_ancestors.length)
          {
            int[] arrayOfInt = new int[m_size * 2];
            System.arraycopy(m_ancestors, 0, arrayOfInt, 0, m_ancestors.length);
            m_ancestors = arrayOfInt;
          }
          m_ancestors[(m_size++)] = paramInt;
          i = _parent2(i);
          paramInt = makeNodeHandle(i);
        }
        m_ancestorsPos = (m_size - 1);
        _currentNode = (m_ancestorsPos >= 0 ? m_ancestors[m_ancestorsPos] : -1);
        return resetPosition();
      }
      return this;
    }
    
    public DTMAxisIterator reset()
    {
      m_ancestorsPos = (m_size - 1);
      _currentNode = (m_ancestorsPos >= 0 ? m_ancestors[m_ancestorsPos] : -1);
      return resetPosition();
    }
    
    public int next()
    {
      int i = _currentNode;
      int j = --m_ancestorsPos;
      _currentNode = (j >= 0 ? m_ancestors[m_ancestorsPos] : -1);
      return returnNode(i);
    }
    
    public void setMark()
    {
      m_markedPos = m_ancestorsPos;
    }
    
    public void gotoMark()
    {
      m_ancestorsPos = m_markedPos;
      _currentNode = (m_ancestorsPos >= 0 ? m_ancestors[m_ancestorsPos] : -1);
    }
  }
  
  public final class AttributeIterator
    extends DTMDefaultBaseIterators.InternalAxisIteratorBase
  {
    public AttributeIterator()
    {
      super();
    }
    
    public DTMAxisIterator setStartNode(int paramInt)
    {
      if (paramInt == 0) {
        paramInt = getDocument();
      }
      if (_isRestartable)
      {
        _startNode = paramInt;
        _currentNode = getFirstAttributeIdentity(makeNodeIdentity(paramInt));
        return resetPosition();
      }
      return this;
    }
    
    public int next()
    {
      int i = _currentNode;
      if (i != -1)
      {
        _currentNode = getNextAttributeIdentity(i);
        return returnNode(makeNodeHandle(i));
      }
      return -1;
    }
  }
  
  public final class ChildrenIterator
    extends DTMDefaultBaseIterators.InternalAxisIteratorBase
  {
    public ChildrenIterator()
    {
      super();
    }
    
    public DTMAxisIterator setStartNode(int paramInt)
    {
      if (paramInt == 0) {
        paramInt = getDocument();
      }
      if (_isRestartable)
      {
        _startNode = paramInt;
        _currentNode = (paramInt == -1 ? -1 : _firstch2(makeNodeIdentity(paramInt)));
        return resetPosition();
      }
      return this;
    }
    
    public int next()
    {
      if (_currentNode != -1)
      {
        int i = _currentNode;
        _currentNode = _nextsib2(i);
        return returnNode(makeNodeHandle(i));
      }
      return -1;
    }
  }
  
  public class DescendantIterator
    extends DTMDefaultBaseIterators.InternalAxisIteratorBase
  {
    public DescendantIterator()
    {
      super();
    }
    
    public DTMAxisIterator setStartNode(int paramInt)
    {
      if (paramInt == 0) {
        paramInt = getDocument();
      }
      if (_isRestartable)
      {
        paramInt = makeNodeIdentity(paramInt);
        _startNode = paramInt;
        if (_includeSelf) {
          paramInt--;
        }
        _currentNode = paramInt;
        return resetPosition();
      }
      return this;
    }
    
    protected final boolean isDescendant(int paramInt)
    {
      return (_parent2(paramInt) >= _startNode) || (_startNode == paramInt);
    }
    
    public int next()
    {
      int i = _startNode;
      if (i == -1) {
        return -1;
      }
      if ((_includeSelf) && (_currentNode + 1 == i)) {
        return returnNode(makeNodeHandle(++_currentNode));
      }
      int j = _currentNode;
      int k;
      if (i == 0)
      {
        int m;
        do
        {
          j++;
          m = _exptype2(j);
          if (-1 == m)
          {
            _currentNode = -1;
            return -1;
          }
        } while ((m == 3) || ((k = m_extendedTypes[m].getNodeType()) == 2) || (k == 13));
      }
      else
      {
        do
        {
          j++;
          k = _type2(j);
          if ((-1 == k) || (!isDescendant(j)))
          {
            _currentNode = -1;
            return -1;
          }
        } while ((2 == k) || (3 == k) || (13 == k));
      }
      _currentNode = j;
      return returnNode(makeNodeHandle(j));
    }
    
    public DTMAxisIterator reset()
    {
      boolean bool = _isRestartable;
      _isRestartable = true;
      setStartNode(makeNodeHandle(_startNode));
      _isRestartable = bool;
      return this;
    }
  }
  
  public class FollowingIterator
    extends DTMDefaultBaseIterators.InternalAxisIteratorBase
  {
    public FollowingIterator()
    {
      super();
    }
    
    public DTMAxisIterator setStartNode(int paramInt)
    {
      if (paramInt == 0) {
        paramInt = getDocument();
      }
      if (_isRestartable)
      {
        _startNode = paramInt;
        paramInt = makeNodeIdentity(paramInt);
        int j = _type2(paramInt);
        int i;
        if ((2 == j) || (13 == j))
        {
          paramInt = _parent2(paramInt);
          i = _firstch2(paramInt);
          if (-1 != i)
          {
            _currentNode = makeNodeHandle(i);
            return resetPosition();
          }
        }
        do
        {
          i = _nextsib2(paramInt);
          if (-1 == i) {
            paramInt = _parent2(paramInt);
          }
        } while ((-1 == i) && (-1 != paramInt));
        _currentNode = makeNodeHandle(i);
        return resetPosition();
      }
      return this;
    }
    
    public int next()
    {
      int i = _currentNode;
      int j = makeNodeIdentity(i);
      int k;
      do
      {
        j++;
        k = _type2(j);
        if (-1 == k)
        {
          _currentNode = -1;
          return returnNode(i);
        }
      } while ((2 == k) || (13 == k));
      _currentNode = makeNodeHandle(j);
      return returnNode(i);
    }
  }
  
  public class FollowingSiblingIterator
    extends DTMDefaultBaseIterators.InternalAxisIteratorBase
  {
    public FollowingSiblingIterator()
    {
      super();
    }
    
    public DTMAxisIterator setStartNode(int paramInt)
    {
      if (paramInt == 0) {
        paramInt = getDocument();
      }
      if (_isRestartable)
      {
        _startNode = paramInt;
        _currentNode = makeNodeIdentity(paramInt);
        return resetPosition();
      }
      return this;
    }
    
    public int next()
    {
      _currentNode = (_currentNode == -1 ? -1 : _nextsib2(_currentNode));
      return returnNode(makeNodeHandle(_currentNode));
    }
  }
  
  public final class ParentIterator
    extends DTMDefaultBaseIterators.InternalAxisIteratorBase
  {
    private int _nodeType = -1;
    
    public ParentIterator()
    {
      super();
    }
    
    public DTMAxisIterator setStartNode(int paramInt)
    {
      if (paramInt == 0) {
        paramInt = getDocument();
      }
      if (_isRestartable)
      {
        _startNode = paramInt;
        if (paramInt != -1) {
          _currentNode = _parent2(makeNodeIdentity(paramInt));
        } else {
          _currentNode = -1;
        }
        return resetPosition();
      }
      return this;
    }
    
    public DTMAxisIterator setNodeType(int paramInt)
    {
      _nodeType = paramInt;
      return this;
    }
    
    public int next()
    {
      int i = _currentNode;
      if (i == -1) {
        return -1;
      }
      if (_nodeType == -1)
      {
        _currentNode = -1;
        return returnNode(makeNodeHandle(i));
      }
      if (_nodeType >= 14)
      {
        if (_nodeType == _exptype2(i))
        {
          _currentNode = -1;
          return returnNode(makeNodeHandle(i));
        }
      }
      else if (_nodeType == _type2(i))
      {
        _currentNode = -1;
        return returnNode(makeNodeHandle(i));
      }
      return -1;
    }
  }
  
  public class PrecedingIterator
    extends DTMDefaultBaseIterators.InternalAxisIteratorBase
  {
    private final int _maxAncestors = 8;
    protected int[] _stack = new int[8];
    protected int _sp;
    protected int _oldsp;
    protected int _markedsp;
    protected int _markedNode;
    protected int _markedDescendant;
    
    public PrecedingIterator()
    {
      super();
    }
    
    public boolean isReverse()
    {
      return true;
    }
    
    public DTMAxisIterator cloneIterator()
    {
      _isRestartable = false;
      try
      {
        PrecedingIterator localPrecedingIterator = (PrecedingIterator)super.clone();
        int[] arrayOfInt = new int[_stack.length];
        System.arraycopy(_stack, 0, arrayOfInt, 0, _stack.length);
        _stack = arrayOfInt;
        return localPrecedingIterator;
      }
      catch (CloneNotSupportedException localCloneNotSupportedException)
      {
        throw new DTMException(XMLMessages.createXMLMessage("ER_ITERATOR_CLONE_NOT_SUPPORTED", null));
      }
    }
    
    public DTMAxisIterator setStartNode(int paramInt)
    {
      if (paramInt == 0) {
        paramInt = getDocument();
      }
      if (_isRestartable)
      {
        paramInt = makeNodeIdentity(paramInt);
        if (_type2(paramInt) == 2) {
          paramInt = _parent2(paramInt);
        }
        _startNode = paramInt;
        int j;
        _stack[(j = 0)] = paramInt;
        int i = paramInt;
        while ((i = _parent2(i)) != -1)
        {
          j++;
          if (j == _stack.length)
          {
            int[] arrayOfInt = new int[j * 2];
            System.arraycopy(_stack, 0, arrayOfInt, 0, j);
            _stack = arrayOfInt;
          }
          _stack[j] = i;
        }
        if (j > 0) {
          j--;
        }
        _currentNode = _stack[j];
        _oldsp = (_sp = j);
        return resetPosition();
      }
      return this;
    }
    
    public int next()
    {
      for (_currentNode += 1; _sp >= 0; _currentNode += 1) {
        if (_currentNode < _stack[_sp])
        {
          int i = _type2(_currentNode);
          if ((i != 2) && (i != 13)) {
            return returnNode(makeNodeHandle(_currentNode));
          }
        }
        else
        {
          _sp -= 1;
        }
      }
      return -1;
    }
    
    public DTMAxisIterator reset()
    {
      _sp = _oldsp;
      return resetPosition();
    }
    
    public void setMark()
    {
      _markedsp = _sp;
      _markedNode = _currentNode;
      _markedDescendant = _stack[0];
    }
    
    public void gotoMark()
    {
      _sp = _markedsp;
      _currentNode = _markedNode;
    }
  }
  
  public class PrecedingSiblingIterator
    extends DTMDefaultBaseIterators.InternalAxisIteratorBase
  {
    protected int _startNodeID;
    
    public PrecedingSiblingIterator()
    {
      super();
    }
    
    public boolean isReverse()
    {
      return true;
    }
    
    public DTMAxisIterator setStartNode(int paramInt)
    {
      if (paramInt == 0) {
        paramInt = getDocument();
      }
      if (_isRestartable)
      {
        _startNode = paramInt;
        paramInt = _startNodeID = makeNodeIdentity(paramInt);
        if (paramInt == -1)
        {
          _currentNode = paramInt;
          return resetPosition();
        }
        int i = _type2(paramInt);
        if ((2 == i) || (13 == i))
        {
          _currentNode = paramInt;
        }
        else
        {
          _currentNode = _parent2(paramInt);
          if (-1 != _currentNode) {
            _currentNode = _firstch2(_currentNode);
          } else {
            _currentNode = paramInt;
          }
        }
        return resetPosition();
      }
      return this;
    }
    
    public int next()
    {
      if ((_currentNode == _startNodeID) || (_currentNode == -1)) {
        return -1;
      }
      int i = _currentNode;
      _currentNode = _nextsib2(i);
      return returnNode(makeNodeHandle(i));
    }
  }
  
  public final class TypedAncestorIterator
    extends SAX2DTM2.AncestorIterator
  {
    private final int _nodeType;
    
    public TypedAncestorIterator(int paramInt)
    {
      super();
      _nodeType = paramInt;
    }
    
    public DTMAxisIterator setStartNode(int paramInt)
    {
      if (paramInt == 0) {
        paramInt = getDocument();
      }
      m_realStartNode = paramInt;
      if (_isRestartable)
      {
        int i = makeNodeIdentity(paramInt);
        m_size = 0;
        if (i == -1)
        {
          _currentNode = -1;
          m_ancestorsPos = 0;
          return this;
        }
        int j = _nodeType;
        if (!_includeSelf)
        {
          i = _parent2(i);
          paramInt = makeNodeHandle(i);
        }
        _startNode = paramInt;
        int k;
        int[] arrayOfInt;
        if (j >= 14) {
          while (i != -1)
          {
            k = _exptype2(i);
            if (k == j)
            {
              if (m_size >= m_ancestors.length)
              {
                arrayOfInt = new int[m_size * 2];
                System.arraycopy(m_ancestors, 0, arrayOfInt, 0, m_ancestors.length);
                m_ancestors = arrayOfInt;
              }
              m_ancestors[(m_size++)] = makeNodeHandle(i);
            }
            i = _parent2(i);
          }
        }
        while (i != -1)
        {
          k = _exptype2(i);
          if (((k < 14) && (k == j)) || ((k >= 14) && (m_extendedTypes[k].getNodeType() == j)))
          {
            if (m_size >= m_ancestors.length)
            {
              arrayOfInt = new int[m_size * 2];
              System.arraycopy(m_ancestors, 0, arrayOfInt, 0, m_ancestors.length);
              m_ancestors = arrayOfInt;
            }
            m_ancestors[(m_size++)] = makeNodeHandle(i);
          }
          i = _parent2(i);
        }
        m_ancestorsPos = (m_size - 1);
        _currentNode = (m_ancestorsPos >= 0 ? m_ancestors[m_ancestorsPos] : -1);
        return resetPosition();
      }
      return this;
    }
    
    public int getNodeByPosition(int paramInt)
    {
      if ((paramInt > 0) && (paramInt <= m_size)) {
        return m_ancestors[(paramInt - 1)];
      }
      return -1;
    }
    
    public int getLast()
    {
      return m_size;
    }
  }
  
  public final class TypedAttributeIterator
    extends DTMDefaultBaseIterators.InternalAxisIteratorBase
  {
    private final int _nodeType;
    
    public TypedAttributeIterator(int paramInt)
    {
      super();
      _nodeType = paramInt;
    }
    
    public DTMAxisIterator setStartNode(int paramInt)
    {
      if (_isRestartable)
      {
        _startNode = paramInt;
        _currentNode = getTypedAttribute(paramInt, _nodeType);
        return resetPosition();
      }
      return this;
    }
    
    public int next()
    {
      int i = _currentNode;
      _currentNode = -1;
      return returnNode(i);
    }
  }
  
  public final class TypedChildrenIterator
    extends DTMDefaultBaseIterators.InternalAxisIteratorBase
  {
    private final int _nodeType;
    
    public TypedChildrenIterator(int paramInt)
    {
      super();
      _nodeType = paramInt;
    }
    
    public DTMAxisIterator setStartNode(int paramInt)
    {
      if (paramInt == 0) {
        paramInt = getDocument();
      }
      if (_isRestartable)
      {
        _startNode = paramInt;
        _currentNode = (paramInt == -1 ? -1 : _firstch2(makeNodeIdentity(_startNode)));
        return resetPosition();
      }
      return this;
    }
    
    public int next()
    {
      int i = _currentNode;
      if (i == -1) {
        return -1;
      }
      int j = _nodeType;
      if (j != 1) {
        while ((i != -1) && (_exptype2(i) != j)) {
          i = _nextsib2(i);
        }
      }
      while (i != -1)
      {
        int k = _exptype2(i);
        if (k >= 14) {
          break;
        }
        i = _nextsib2(i);
      }
      if (i == -1)
      {
        _currentNode = -1;
        return -1;
      }
      _currentNode = _nextsib2(i);
      return returnNode(makeNodeHandle(i));
    }
    
    public int getNodeByPosition(int paramInt)
    {
      if (paramInt <= 0) {
        return -1;
      }
      int i = _currentNode;
      int j = 0;
      int k = _nodeType;
      if (k != 1)
      {
        while (i != -1)
        {
          if (_exptype2(i) == k)
          {
            j++;
            if (j == paramInt) {
              return makeNodeHandle(i);
            }
          }
          i = _nextsib2(i);
        }
        return -1;
      }
      while (i != -1)
      {
        if (_exptype2(i) >= 14)
        {
          j++;
          if (j == paramInt) {
            return makeNodeHandle(i);
          }
        }
        i = _nextsib2(i);
      }
      return -1;
    }
  }
  
  public final class TypedDescendantIterator
    extends SAX2DTM2.DescendantIterator
  {
    private final int _nodeType;
    
    public TypedDescendantIterator(int paramInt)
    {
      super();
      _nodeType = paramInt;
    }
    
    public int next()
    {
      int i = _startNode;
      if (_startNode == -1) {
        return -1;
      }
      int j = _currentNode;
      int m = _nodeType;
      int k;
      if (m != 1) {
        do
        {
          j++;
          k = _exptype2(j);
          if ((-1 == k) || ((_parent2(j) < i) && (i != j)))
          {
            _currentNode = -1;
            return -1;
          }
        } while (k != m);
      } else if (i == 0) {
        do
        {
          j++;
          k = _exptype2(j);
          if (-1 == k)
          {
            _currentNode = -1;
            return -1;
          }
        } while ((k < 14) || (m_extendedTypes[k].getNodeType() != 1));
      } else {
        do
        {
          j++;
          k = _exptype2(j);
          if ((-1 == k) || ((_parent2(j) < i) && (i != j)))
          {
            _currentNode = -1;
            return -1;
          }
        } while ((k < 14) || (m_extendedTypes[k].getNodeType() != 1));
      }
      _currentNode = j;
      return returnNode(makeNodeHandle(j));
    }
  }
  
  public final class TypedFollowingIterator
    extends SAX2DTM2.FollowingIterator
  {
    private final int _nodeType;
    
    public TypedFollowingIterator(int paramInt)
    {
      super();
      _nodeType = paramInt;
    }
    
    public int next()
    {
      int m = _nodeType;
      int n = makeNodeIdentity(_currentNode);
      int j;
      int i;
      int k;
      if (m >= 14) {
        do
        {
          j = n;
          i = j;
          do
          {
            i++;
            k = _type2(i);
          } while ((k != -1) && ((2 == k) || (13 == k)));
          n = k != -1 ? i : -1;
          if (j == -1) {
            break;
          }
        } while (_exptype2(j) != m);
      } else {
        do
        {
          j = n;
          i = j;
          do
          {
            i++;
            k = _type2(i);
          } while ((k != -1) && ((2 == k) || (13 == k)));
          n = k != -1 ? i : -1;
        } while ((j != -1) && (_exptype2(j) != m) && (_type2(j) != m));
      }
      _currentNode = makeNodeHandle(n);
      return j == -1 ? -1 : returnNode(makeNodeHandle(j));
    }
  }
  
  public final class TypedFollowingSiblingIterator
    extends SAX2DTM2.FollowingSiblingIterator
  {
    private final int _nodeType;
    
    public TypedFollowingSiblingIterator(int paramInt)
    {
      super();
      _nodeType = paramInt;
    }
    
    public int next()
    {
      if (_currentNode == -1) {
        return -1;
      }
      int i = _currentNode;
      int j = _nodeType;
      if (j != 1) {
        while (((i = _nextsib2(i)) != -1) && (_exptype2(i) != j)) {}
      }
      while (((i = _nextsib2(i)) != -1) && (_exptype2(i) < 14)) {}
      _currentNode = i;
      return i == -1 ? -1 : returnNode(makeNodeHandle(i));
    }
  }
  
  public final class TypedPrecedingIterator
    extends SAX2DTM2.PrecedingIterator
  {
    private final int _nodeType;
    
    public TypedPrecedingIterator(int paramInt)
    {
      super();
      _nodeType = paramInt;
    }
    
    public int next()
    {
      int i = _currentNode;
      int j = _nodeType;
      if (j >= 14) {
        do
        {
          do
          {
            i++;
            if (_sp < 0)
            {
              i = -1;
              break label167;
            }
            if (i < _stack[_sp]) {
              break;
            }
          } while (--_sp >= 0);
          i = -1;
          break;
        } while (_exptype2(i) != j);
      } else {
        for (;;)
        {
          i++;
          if (_sp < 0)
          {
            i = -1;
          }
          else if (i >= _stack[_sp])
          {
            if (--_sp < 0) {
              i = -1;
            }
          }
          else
          {
            int k = _exptype2(i);
            if (k < 14)
            {
              if (k == j) {
                break;
              }
            }
            else if (m_extendedTypes[k].getNodeType() == j) {
              break;
            }
          }
        }
      }
      label167:
      _currentNode = i;
      return i == -1 ? -1 : returnNode(makeNodeHandle(i));
    }
  }
  
  public final class TypedPrecedingSiblingIterator
    extends SAX2DTM2.PrecedingSiblingIterator
  {
    private final int _nodeType;
    
    public TypedPrecedingSiblingIterator(int paramInt)
    {
      super();
      _nodeType = paramInt;
    }
    
    public int next()
    {
      int i = _currentNode;
      int j = _nodeType;
      int k = _startNodeID;
      if (j != 1) {
        while ((i != -1) && (i != k) && (_exptype2(i) != j)) {
          i = _nextsib2(i);
        }
      }
      while ((i != -1) && (i != k) && (_exptype2(i) < 14)) {
        i = _nextsib2(i);
      }
      if ((i == -1) || (i == k))
      {
        _currentNode = -1;
        return -1;
      }
      _currentNode = _nextsib2(i);
      return returnNode(makeNodeHandle(i));
    }
    
    public int getLast()
    {
      if (_last != -1) {
        return _last;
      }
      setMark();
      int i = _currentNode;
      int j = _nodeType;
      int k = _startNodeID;
      int m = 0;
      if (j != 1) {
        while ((i != -1) && (i != k))
        {
          if (_exptype2(i) == j) {
            m++;
          }
          i = _nextsib2(i);
        }
      }
      while ((i != -1) && (i != k))
      {
        if (_exptype2(i) >= 14) {
          m++;
        }
        i = _nextsib2(i);
      }
      gotoMark();
      return _last = m;
    }
  }
  
  public class TypedRootIterator
    extends DTMDefaultBaseIterators.RootIterator
  {
    private final int _nodeType;
    
    public TypedRootIterator(int paramInt)
    {
      super();
      _nodeType = paramInt;
    }
    
    public int next()
    {
      if (_startNode == _currentNode) {
        return -1;
      }
      int i = _startNode;
      int j = _exptype2(makeNodeIdentity(i));
      _currentNode = i;
      if (_nodeType >= 14)
      {
        if (_nodeType == j) {
          return returnNode(i);
        }
      }
      else if (j < 14)
      {
        if (j == _nodeType) {
          return returnNode(i);
        }
      }
      else if (m_extendedTypes[j].getNodeType() == _nodeType) {
        return returnNode(i);
      }
      return -1;
    }
  }
  
  public final class TypedSingletonIterator
    extends DTMDefaultBaseIterators.SingletonIterator
  {
    private final int _nodeType;
    
    public TypedSingletonIterator(int paramInt)
    {
      super();
      _nodeType = paramInt;
    }
    
    public int next()
    {
      int i = _currentNode;
      if (i == -1) {
        return -1;
      }
      _currentNode = -1;
      if (_nodeType >= 14)
      {
        if (_exptype2(makeNodeIdentity(i)) == _nodeType) {
          return returnNode(i);
        }
      }
      else if (_type2(makeNodeIdentity(i)) == _nodeType) {
        return returnNode(i);
      }
      return -1;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\dtm\ref\sax2dtm\SAX2DTM2.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */