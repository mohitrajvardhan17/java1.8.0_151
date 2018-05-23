package com.sun.org.apache.xml.internal.dtm.ref.sax2dtm;

import com.sun.org.apache.xml.internal.dtm.DTMManager;
import com.sun.org.apache.xml.internal.dtm.DTMWSFilter;
import com.sun.org.apache.xml.internal.utils.FastStringBuffer;
import com.sun.org.apache.xml.internal.utils.IntStack;
import com.sun.org.apache.xml.internal.utils.IntVector;
import com.sun.org.apache.xml.internal.utils.StringVector;
import com.sun.org.apache.xml.internal.utils.SuballocatedIntVector;
import com.sun.org.apache.xml.internal.utils.XMLStringFactory;
import java.util.Vector;
import javax.xml.transform.Source;
import org.xml.sax.SAXException;

public class SAX2RTFDTM
  extends SAX2DTM
{
  private static final boolean DEBUG = false;
  private int m_currentDocumentNode = -1;
  IntStack mark_size = new IntStack();
  IntStack mark_data_size = new IntStack();
  IntStack mark_char_size = new IntStack();
  IntStack mark_doq_size = new IntStack();
  IntStack mark_nsdeclset_size = new IntStack();
  IntStack mark_nsdeclelem_size = new IntStack();
  int m_emptyNodeCount = m_size;
  int m_emptyNSDeclSetCount = m_namespaceDeclSets == null ? 0 : m_namespaceDeclSets.size();
  int m_emptyNSDeclSetElemsCount = m_namespaceDeclSetElements == null ? 0 : m_namespaceDeclSetElements.size();
  int m_emptyDataCount = m_data.size();
  int m_emptyCharsCount = m_chars.size();
  int m_emptyDataQNCount = m_dataOrQName.size();
  
  public SAX2RTFDTM(DTMManager paramDTMManager, Source paramSource, int paramInt, DTMWSFilter paramDTMWSFilter, XMLStringFactory paramXMLStringFactory, boolean paramBoolean)
  {
    super(paramDTMManager, paramSource, paramInt, paramDTMWSFilter, paramXMLStringFactory, paramBoolean);
    m_useSourceLocationProperty = false;
    m_sourceSystemId = (m_useSourceLocationProperty ? new StringVector() : null);
    m_sourceLine = (m_useSourceLocationProperty ? new IntVector() : null);
    m_sourceColumn = (m_useSourceLocationProperty ? new IntVector() : null);
  }
  
  public int getDocument()
  {
    return makeNodeHandle(m_currentDocumentNode);
  }
  
  public int getDocumentRoot(int paramInt)
  {
    for (int i = makeNodeIdentity(paramInt); i != -1; i = _parent(i)) {
      if (_type(i) == 9) {
        return makeNodeHandle(i);
      }
    }
    return -1;
  }
  
  protected int _documentRoot(int paramInt)
  {
    if (paramInt == -1) {
      return -1;
    }
    for (int i = _parent(paramInt); i != -1; i = _parent(paramInt)) {
      paramInt = i;
    }
    return paramInt;
  }
  
  public void startDocument()
    throws SAXException
  {
    m_endDocumentOccured = false;
    m_prefixMappings = new Vector();
    m_contextIndexes = new IntStack();
    m_parents = new IntStack();
    m_currentDocumentNode = m_size;
    super.startDocument();
  }
  
  public void endDocument()
    throws SAXException
  {
    charactersFlush();
    m_nextsib.setElementAt(-1, m_currentDocumentNode);
    if (m_firstch.elementAt(m_currentDocumentNode) == -2) {
      m_firstch.setElementAt(-1, m_currentDocumentNode);
    }
    if (-1 != m_previous) {
      m_nextsib.setElementAt(-1, m_previous);
    }
    m_parents = null;
    m_prefixMappings = null;
    m_contextIndexes = null;
    m_currentDocumentNode = -1;
    m_endDocumentOccured = true;
  }
  
  public void pushRewindMark()
  {
    if ((m_indexing) || (m_elemIndexes != null)) {
      throw new NullPointerException("Coding error; Don't try to mark/rewind an indexed DTM");
    }
    mark_size.push(m_size);
    mark_nsdeclset_size.push(m_namespaceDeclSets == null ? 0 : m_namespaceDeclSets.size());
    mark_nsdeclelem_size.push(m_namespaceDeclSetElements == null ? 0 : m_namespaceDeclSetElements.size());
    mark_data_size.push(m_data.size());
    mark_char_size.push(m_chars.size());
    mark_doq_size.push(m_dataOrQName.size());
  }
  
  public boolean popRewindMark()
  {
    boolean bool = mark_size.empty();
    m_size = (bool ? m_emptyNodeCount : mark_size.pop());
    m_exptype.setSize(m_size);
    m_firstch.setSize(m_size);
    m_nextsib.setSize(m_size);
    m_prevsib.setSize(m_size);
    m_parent.setSize(m_size);
    m_elemIndexes = ((int[][][])null);
    int i = bool ? m_emptyNSDeclSetCount : mark_nsdeclset_size.pop();
    if (m_namespaceDeclSets != null) {
      m_namespaceDeclSets.setSize(i);
    }
    int j = bool ? m_emptyNSDeclSetElemsCount : mark_nsdeclelem_size.pop();
    if (m_namespaceDeclSetElements != null) {
      m_namespaceDeclSetElements.setSize(j);
    }
    m_data.setSize(bool ? m_emptyDataCount : mark_data_size.pop());
    m_chars.setLength(bool ? m_emptyCharsCount : mark_char_size.pop());
    m_dataOrQName.setSize(bool ? m_emptyDataQNCount : mark_doq_size.pop());
    return m_size == 0;
  }
  
  public boolean isTreeIncomplete()
  {
    return !m_endDocumentOccured;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\dtm\ref\sax2dtm\SAX2RTFDTM.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */