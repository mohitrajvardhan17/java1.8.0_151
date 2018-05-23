package com.sun.org.apache.xpath.internal;

import com.sun.org.apache.xalan.internal.res.XSLMessages;
import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.dtm.DTMFilter;
import com.sun.org.apache.xml.internal.dtm.DTMIterator;
import com.sun.org.apache.xml.internal.dtm.DTMManager;
import com.sun.org.apache.xml.internal.utils.NodeVector;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.NodeIterator;

public class NodeSetDTM
  extends NodeVector
  implements DTMIterator, Cloneable
{
  static final long serialVersionUID = 7686480133331317070L;
  DTMManager m_manager;
  protected transient int m_next = 0;
  protected transient boolean m_mutable = true;
  protected transient boolean m_cacheNodes = true;
  protected int m_root = -1;
  private transient int m_last = 0;
  
  public NodeSetDTM(DTMManager paramDTMManager)
  {
    m_manager = paramDTMManager;
  }
  
  public NodeSetDTM(int paramInt1, int paramInt2, DTMManager paramDTMManager)
  {
    super(paramInt1);
    m_manager = paramDTMManager;
  }
  
  public NodeSetDTM(NodeSetDTM paramNodeSetDTM)
  {
    m_manager = paramNodeSetDTM.getDTMManager();
    m_root = paramNodeSetDTM.getRoot();
    addNodes(paramNodeSetDTM);
  }
  
  public NodeSetDTM(DTMIterator paramDTMIterator)
  {
    m_manager = paramDTMIterator.getDTMManager();
    m_root = paramDTMIterator.getRoot();
    addNodes(paramDTMIterator);
  }
  
  public NodeSetDTM(NodeIterator paramNodeIterator, XPathContext paramXPathContext)
  {
    m_manager = paramXPathContext.getDTMManager();
    Node localNode;
    while (null != (localNode = paramNodeIterator.nextNode()))
    {
      int i = paramXPathContext.getDTMHandleFromNode(localNode);
      addNodeInDocOrder(i, paramXPathContext);
    }
  }
  
  public NodeSetDTM(NodeList paramNodeList, XPathContext paramXPathContext)
  {
    m_manager = paramXPathContext.getDTMManager();
    int i = paramNodeList.getLength();
    for (int j = 0; j < i; j++)
    {
      Node localNode = paramNodeList.item(j);
      int k = paramXPathContext.getDTMHandleFromNode(localNode);
      addNode(k);
    }
  }
  
  public NodeSetDTM(int paramInt, DTMManager paramDTMManager)
  {
    m_manager = paramDTMManager;
    addNode(paramInt);
  }
  
  public void setEnvironment(Object paramObject) {}
  
  public int getRoot()
  {
    if (-1 == m_root)
    {
      if (size() > 0) {
        return item(0);
      }
      return -1;
    }
    return m_root;
  }
  
  public void setRoot(int paramInt, Object paramObject) {}
  
  public Object clone()
    throws CloneNotSupportedException
  {
    NodeSetDTM localNodeSetDTM = (NodeSetDTM)super.clone();
    return localNodeSetDTM;
  }
  
  public DTMIterator cloneWithReset()
    throws CloneNotSupportedException
  {
    NodeSetDTM localNodeSetDTM = (NodeSetDTM)clone();
    localNodeSetDTM.reset();
    return localNodeSetDTM;
  }
  
  public void reset()
  {
    m_next = 0;
  }
  
  public int getWhatToShow()
  {
    return -17;
  }
  
  public DTMFilter getFilter()
  {
    return null;
  }
  
  public boolean getExpandEntityReferences()
  {
    return true;
  }
  
  public DTM getDTM(int paramInt)
  {
    return m_manager.getDTM(paramInt);
  }
  
  public DTMManager getDTMManager()
  {
    return m_manager;
  }
  
  public int nextNode()
  {
    if (m_next < size())
    {
      int i = elementAt(m_next);
      m_next += 1;
      return i;
    }
    return -1;
  }
  
  public int previousNode()
  {
    if (!m_cacheNodes) {
      throw new RuntimeException(XSLMessages.createXPATHMessage("ER_NODESETDTM_CANNOT_ITERATE", null));
    }
    if (m_next - 1 > 0)
    {
      m_next -= 1;
      return elementAt(m_next);
    }
    return -1;
  }
  
  public void detach() {}
  
  public void allowDetachToRelease(boolean paramBoolean) {}
  
  public boolean isFresh()
  {
    return m_next == 0;
  }
  
  public void runTo(int paramInt)
  {
    if (!m_cacheNodes) {
      throw new RuntimeException(XSLMessages.createXPATHMessage("ER_NODESETDTM_CANNOT_INDEX", null));
    }
    if ((paramInt >= 0) && (m_next < m_firstFree)) {
      m_next = paramInt;
    } else {
      m_next = (m_firstFree - 1);
    }
  }
  
  public int item(int paramInt)
  {
    runTo(paramInt);
    return elementAt(paramInt);
  }
  
  public int getLength()
  {
    runTo(-1);
    return size();
  }
  
  public void addNode(int paramInt)
  {
    if (!m_mutable) {
      throw new RuntimeException(XSLMessages.createXPATHMessage("ER_NODESETDTM_NOT_MUTABLE", null));
    }
    addElement(paramInt);
  }
  
  public void insertNode(int paramInt1, int paramInt2)
  {
    if (!m_mutable) {
      throw new RuntimeException(XSLMessages.createXPATHMessage("ER_NODESETDTM_NOT_MUTABLE", null));
    }
    insertElementAt(paramInt1, paramInt2);
  }
  
  public void removeNode(int paramInt)
  {
    if (!m_mutable) {
      throw new RuntimeException(XSLMessages.createXPATHMessage("ER_NODESETDTM_NOT_MUTABLE", null));
    }
    removeElement(paramInt);
  }
  
  public void addNodes(DTMIterator paramDTMIterator)
  {
    if (!m_mutable) {
      throw new RuntimeException(XSLMessages.createXPATHMessage("ER_NODESETDTM_NOT_MUTABLE", null));
    }
    if (null != paramDTMIterator)
    {
      int i;
      while (-1 != (i = paramDTMIterator.nextNode())) {
        addElement(i);
      }
    }
  }
  
  public void addNodesInDocOrder(DTMIterator paramDTMIterator, XPathContext paramXPathContext)
  {
    if (!m_mutable) {
      throw new RuntimeException(XSLMessages.createXPATHMessage("ER_NODESETDTM_NOT_MUTABLE", null));
    }
    int i;
    while (-1 != (i = paramDTMIterator.nextNode())) {
      addNodeInDocOrder(i, paramXPathContext);
    }
  }
  
  public int addNodeInDocOrder(int paramInt, boolean paramBoolean, XPathContext paramXPathContext)
  {
    if (!m_mutable) {
      throw new RuntimeException(XSLMessages.createXPATHMessage("ER_NODESETDTM_NOT_MUTABLE", null));
    }
    int i = -1;
    int j;
    int k;
    if (paramBoolean)
    {
      j = size();
      for (k = j - 1; k >= 0; k--)
      {
        int m = elementAt(k);
        if (m == paramInt)
        {
          k = -2;
        }
        else
        {
          DTM localDTM = paramXPathContext.getDTM(paramInt);
          if (!localDTM.isNodeAfter(paramInt, m)) {
            break;
          }
        }
      }
      if (k != -2)
      {
        i = k + 1;
        insertElementAt(paramInt, i);
      }
    }
    else
    {
      i = size();
      j = 0;
      for (k = 0; k < i; k++) {
        if (k == paramInt)
        {
          j = 1;
          break;
        }
      }
      if (j == 0) {
        addElement(paramInt);
      }
    }
    return i;
  }
  
  public int addNodeInDocOrder(int paramInt, XPathContext paramXPathContext)
  {
    if (!m_mutable) {
      throw new RuntimeException(XSLMessages.createXPATHMessage("ER_NODESETDTM_NOT_MUTABLE", null));
    }
    return addNodeInDocOrder(paramInt, true, paramXPathContext);
  }
  
  public int size()
  {
    return super.size();
  }
  
  public void addElement(int paramInt)
  {
    if (!m_mutable) {
      throw new RuntimeException(XSLMessages.createXPATHMessage("ER_NODESETDTM_NOT_MUTABLE", null));
    }
    super.addElement(paramInt);
  }
  
  public void insertElementAt(int paramInt1, int paramInt2)
  {
    if (!m_mutable) {
      throw new RuntimeException(XSLMessages.createXPATHMessage("ER_NODESETDTM_NOT_MUTABLE", null));
    }
    super.insertElementAt(paramInt1, paramInt2);
  }
  
  public void appendNodes(NodeVector paramNodeVector)
  {
    if (!m_mutable) {
      throw new RuntimeException(XSLMessages.createXPATHMessage("ER_NODESETDTM_NOT_MUTABLE", null));
    }
    super.appendNodes(paramNodeVector);
  }
  
  public void removeAllElements()
  {
    if (!m_mutable) {
      throw new RuntimeException(XSLMessages.createXPATHMessage("ER_NODESETDTM_NOT_MUTABLE", null));
    }
    super.removeAllElements();
  }
  
  public boolean removeElement(int paramInt)
  {
    if (!m_mutable) {
      throw new RuntimeException(XSLMessages.createXPATHMessage("ER_NODESETDTM_NOT_MUTABLE", null));
    }
    return super.removeElement(paramInt);
  }
  
  public void removeElementAt(int paramInt)
  {
    if (!m_mutable) {
      throw new RuntimeException(XSLMessages.createXPATHMessage("ER_NODESETDTM_NOT_MUTABLE", null));
    }
    super.removeElementAt(paramInt);
  }
  
  public void setElementAt(int paramInt1, int paramInt2)
  {
    if (!m_mutable) {
      throw new RuntimeException(XSLMessages.createXPATHMessage("ER_NODESETDTM_NOT_MUTABLE", null));
    }
    super.setElementAt(paramInt1, paramInt2);
  }
  
  public void setItem(int paramInt1, int paramInt2)
  {
    if (!m_mutable) {
      throw new RuntimeException(XSLMessages.createXPATHMessage("ER_NODESETDTM_NOT_MUTABLE", null));
    }
    super.setElementAt(paramInt1, paramInt2);
  }
  
  public int elementAt(int paramInt)
  {
    runTo(paramInt);
    return super.elementAt(paramInt);
  }
  
  public boolean contains(int paramInt)
  {
    runTo(-1);
    return super.contains(paramInt);
  }
  
  public int indexOf(int paramInt1, int paramInt2)
  {
    runTo(-1);
    return super.indexOf(paramInt1, paramInt2);
  }
  
  public int indexOf(int paramInt)
  {
    runTo(-1);
    return super.indexOf(paramInt);
  }
  
  public int getCurrentPos()
  {
    return m_next;
  }
  
  public void setCurrentPos(int paramInt)
  {
    if (!m_cacheNodes) {
      throw new RuntimeException(XSLMessages.createXPATHMessage("ER_NODESETDTM_CANNOT_INDEX", null));
    }
    m_next = paramInt;
  }
  
  public int getCurrentNode()
  {
    if (!m_cacheNodes) {
      throw new RuntimeException("This NodeSetDTM can not do indexing or counting functions!");
    }
    int i = m_next;
    int j = m_next > 0 ? m_next - 1 : m_next;
    int k = j < m_firstFree ? elementAt(j) : -1;
    m_next = i;
    return k;
  }
  
  public boolean getShouldCacheNodes()
  {
    return m_cacheNodes;
  }
  
  public void setShouldCacheNodes(boolean paramBoolean)
  {
    if (!isFresh()) {
      throw new RuntimeException(XSLMessages.createXPATHMessage("ER_CANNOT_CALL_SETSHOULDCACHENODE", null));
    }
    m_cacheNodes = paramBoolean;
    m_mutable = true;
  }
  
  public boolean isMutable()
  {
    return m_mutable;
  }
  
  public int getLast()
  {
    return m_last;
  }
  
  public void setLast(int paramInt)
  {
    m_last = paramInt;
  }
  
  public boolean isDocOrdered()
  {
    return true;
  }
  
  public int getAxis()
  {
    return -1;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\NodeSetDTM.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */