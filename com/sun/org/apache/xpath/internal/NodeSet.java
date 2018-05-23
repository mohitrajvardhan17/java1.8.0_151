package com.sun.org.apache.xpath.internal;

import com.sun.org.apache.xalan.internal.res.XSLMessages;
import com.sun.org.apache.xml.internal.utils.DOM2Helper;
import com.sun.org.apache.xpath.internal.axes.ContextNodeList;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.NodeIterator;

public class NodeSet
  implements NodeList, NodeIterator, Cloneable, ContextNodeList
{
  protected transient int m_next = 0;
  protected transient boolean m_mutable = true;
  protected transient boolean m_cacheNodes = true;
  private transient int m_last = 0;
  private int m_blocksize;
  Node[] m_map;
  protected int m_firstFree = 0;
  private int m_mapSize;
  
  public NodeSet()
  {
    m_blocksize = 32;
    m_mapSize = 0;
  }
  
  public NodeSet(int paramInt)
  {
    m_blocksize = paramInt;
    m_mapSize = 0;
  }
  
  public NodeSet(NodeList paramNodeList)
  {
    this(32);
    addNodes(paramNodeList);
  }
  
  public NodeSet(NodeSet paramNodeSet)
  {
    this(32);
    addNodes(paramNodeSet);
  }
  
  public NodeSet(NodeIterator paramNodeIterator)
  {
    this(32);
    addNodes(paramNodeIterator);
  }
  
  public NodeSet(Node paramNode)
  {
    this(32);
    addNode(paramNode);
  }
  
  public Node getRoot()
  {
    return null;
  }
  
  public NodeIterator cloneWithReset()
    throws CloneNotSupportedException
  {
    NodeSet localNodeSet = (NodeSet)clone();
    localNodeSet.reset();
    return localNodeSet;
  }
  
  public void reset()
  {
    m_next = 0;
  }
  
  public int getWhatToShow()
  {
    return -17;
  }
  
  public NodeFilter getFilter()
  {
    return null;
  }
  
  public boolean getExpandEntityReferences()
  {
    return true;
  }
  
  public Node nextNode()
    throws DOMException
  {
    if (m_next < size())
    {
      Node localNode = elementAt(m_next);
      m_next += 1;
      return localNode;
    }
    return null;
  }
  
  public Node previousNode()
    throws DOMException
  {
    if (!m_cacheNodes) {
      throw new RuntimeException(XSLMessages.createXPATHMessage("ER_NODESET_CANNOT_ITERATE", null));
    }
    if (m_next - 1 > 0)
    {
      m_next -= 1;
      return elementAt(m_next);
    }
    return null;
  }
  
  public void detach() {}
  
  public boolean isFresh()
  {
    return m_next == 0;
  }
  
  public void runTo(int paramInt)
  {
    if (!m_cacheNodes) {
      throw new RuntimeException(XSLMessages.createXPATHMessage("ER_NODESET_CANNOT_INDEX", null));
    }
    if ((paramInt >= 0) && (m_next < m_firstFree)) {
      m_next = paramInt;
    } else {
      m_next = (m_firstFree - 1);
    }
  }
  
  public Node item(int paramInt)
  {
    runTo(paramInt);
    return elementAt(paramInt);
  }
  
  public int getLength()
  {
    runTo(-1);
    return size();
  }
  
  public void addNode(Node paramNode)
  {
    if (!m_mutable) {
      throw new RuntimeException(XSLMessages.createXPATHMessage("ER_NODESET_NOT_MUTABLE", null));
    }
    addElement(paramNode);
  }
  
  public void insertNode(Node paramNode, int paramInt)
  {
    if (!m_mutable) {
      throw new RuntimeException(XSLMessages.createXPATHMessage("ER_NODESET_NOT_MUTABLE", null));
    }
    insertElementAt(paramNode, paramInt);
  }
  
  public void removeNode(Node paramNode)
  {
    if (!m_mutable) {
      throw new RuntimeException(XSLMessages.createXPATHMessage("ER_NODESET_NOT_MUTABLE", null));
    }
    removeElement(paramNode);
  }
  
  public void addNodes(NodeList paramNodeList)
  {
    if (!m_mutable) {
      throw new RuntimeException(XSLMessages.createXPATHMessage("ER_NODESET_NOT_MUTABLE", null));
    }
    if (null != paramNodeList)
    {
      int i = paramNodeList.getLength();
      for (int j = 0; j < i; j++)
      {
        Node localNode = paramNodeList.item(j);
        if (null != localNode) {
          addElement(localNode);
        }
      }
    }
  }
  
  public void addNodes(NodeSet paramNodeSet)
  {
    if (!m_mutable) {
      throw new RuntimeException(XSLMessages.createXPATHMessage("ER_NODESET_NOT_MUTABLE", null));
    }
    addNodes(paramNodeSet);
  }
  
  public void addNodes(NodeIterator paramNodeIterator)
  {
    if (!m_mutable) {
      throw new RuntimeException(XSLMessages.createXPATHMessage("ER_NODESET_NOT_MUTABLE", null));
    }
    if (null != paramNodeIterator)
    {
      Node localNode;
      while (null != (localNode = paramNodeIterator.nextNode())) {
        addElement(localNode);
      }
    }
  }
  
  public void addNodesInDocOrder(NodeList paramNodeList, XPathContext paramXPathContext)
  {
    if (!m_mutable) {
      throw new RuntimeException(XSLMessages.createXPATHMessage("ER_NODESET_NOT_MUTABLE", null));
    }
    int i = paramNodeList.getLength();
    for (int j = 0; j < i; j++)
    {
      Node localNode = paramNodeList.item(j);
      if (null != localNode) {
        addNodeInDocOrder(localNode, paramXPathContext);
      }
    }
  }
  
  public void addNodesInDocOrder(NodeIterator paramNodeIterator, XPathContext paramXPathContext)
  {
    if (!m_mutable) {
      throw new RuntimeException(XSLMessages.createXPATHMessage("ER_NODESET_NOT_MUTABLE", null));
    }
    Node localNode;
    while (null != (localNode = paramNodeIterator.nextNode())) {
      addNodeInDocOrder(localNode, paramXPathContext);
    }
  }
  
  private boolean addNodesInDocOrder(int paramInt1, int paramInt2, int paramInt3, NodeList paramNodeList, XPathContext paramXPathContext)
  {
    if (!m_mutable) {
      throw new RuntimeException(XSLMessages.createXPATHMessage("ER_NODESET_NOT_MUTABLE", null));
    }
    boolean bool1 = false;
    Node localNode1 = paramNodeList.item(paramInt3);
    for (int i = paramInt2; i >= paramInt1; i--)
    {
      Node localNode2 = elementAt(i);
      if (localNode2 == localNode1)
      {
        i = -2;
        break;
      }
      if (!DOM2Helper.isNodeAfter(localNode1, localNode2))
      {
        insertElementAt(localNode1, i + 1);
        paramInt3--;
        if (paramInt3 <= 0) {
          break;
        }
        boolean bool2 = addNodesInDocOrder(0, i, paramInt3, paramNodeList, paramXPathContext);
        if (!bool2) {
          addNodesInDocOrder(i, size() - 1, paramInt3, paramNodeList, paramXPathContext);
        }
        break;
      }
    }
    if (i == -1) {
      insertElementAt(localNode1, 0);
    }
    return bool1;
  }
  
  public int addNodeInDocOrder(Node paramNode, boolean paramBoolean, XPathContext paramXPathContext)
  {
    if (!m_mutable) {
      throw new RuntimeException(XSLMessages.createXPATHMessage("ER_NODESET_NOT_MUTABLE", null));
    }
    int i = -1;
    int j;
    int k;
    if (paramBoolean)
    {
      j = size();
      for (k = j - 1; k >= 0; k--)
      {
        Node localNode = elementAt(k);
        if (localNode == paramNode) {
          k = -2;
        } else {
          if (!DOM2Helper.isNodeAfter(paramNode, localNode)) {
            break;
          }
        }
      }
      if (k != -2)
      {
        i = k + 1;
        insertElementAt(paramNode, i);
      }
    }
    else
    {
      i = size();
      j = 0;
      for (k = 0; k < i; k++) {
        if (item(k).equals(paramNode))
        {
          j = 1;
          break;
        }
      }
      if (j == 0) {
        addElement(paramNode);
      }
    }
    return i;
  }
  
  public int addNodeInDocOrder(Node paramNode, XPathContext paramXPathContext)
  {
    if (!m_mutable) {
      throw new RuntimeException(XSLMessages.createXPATHMessage("ER_NODESET_NOT_MUTABLE", null));
    }
    return addNodeInDocOrder(paramNode, true, paramXPathContext);
  }
  
  public int getCurrentPos()
  {
    return m_next;
  }
  
  public void setCurrentPos(int paramInt)
  {
    if (!m_cacheNodes) {
      throw new RuntimeException(XSLMessages.createXPATHMessage("ER_NODESET_CANNOT_INDEX", null));
    }
    m_next = paramInt;
  }
  
  public Node getCurrentNode()
  {
    if (!m_cacheNodes) {
      throw new RuntimeException(XSLMessages.createXPATHMessage("ER_NODESET_CANNOT_INDEX", null));
    }
    int i = m_next;
    Node localNode = m_next < m_firstFree ? elementAt(m_next) : null;
    m_next = i;
    return localNode;
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
  
  public int getLast()
  {
    return m_last;
  }
  
  public void setLast(int paramInt)
  {
    m_last = paramInt;
  }
  
  public Object clone()
    throws CloneNotSupportedException
  {
    NodeSet localNodeSet = (NodeSet)super.clone();
    if ((null != m_map) && (m_map == m_map))
    {
      m_map = new Node[m_map.length];
      System.arraycopy(m_map, 0, m_map, 0, m_map.length);
    }
    return localNodeSet;
  }
  
  public int size()
  {
    return m_firstFree;
  }
  
  public void addElement(Node paramNode)
  {
    if (!m_mutable) {
      throw new RuntimeException(XSLMessages.createXPATHMessage("ER_NODESET_NOT_MUTABLE", null));
    }
    if (m_firstFree + 1 >= m_mapSize) {
      if (null == m_map)
      {
        m_map = new Node[m_blocksize];
        m_mapSize = m_blocksize;
      }
      else
      {
        m_mapSize += m_blocksize;
        Node[] arrayOfNode = new Node[m_mapSize];
        System.arraycopy(m_map, 0, arrayOfNode, 0, m_firstFree + 1);
        m_map = arrayOfNode;
      }
    }
    m_map[m_firstFree] = paramNode;
    m_firstFree += 1;
  }
  
  public final void push(Node paramNode)
  {
    int i = m_firstFree;
    if (i + 1 >= m_mapSize) {
      if (null == m_map)
      {
        m_map = new Node[m_blocksize];
        m_mapSize = m_blocksize;
      }
      else
      {
        m_mapSize += m_blocksize;
        Node[] arrayOfNode = new Node[m_mapSize];
        System.arraycopy(m_map, 0, arrayOfNode, 0, i + 1);
        m_map = arrayOfNode;
      }
    }
    m_map[i] = paramNode;
    i++;
    m_firstFree = i;
  }
  
  public final Node pop()
  {
    m_firstFree -= 1;
    Node localNode = m_map[m_firstFree];
    m_map[m_firstFree] = null;
    return localNode;
  }
  
  public final Node popAndTop()
  {
    m_firstFree -= 1;
    m_map[m_firstFree] = null;
    return m_firstFree == 0 ? null : m_map[(m_firstFree - 1)];
  }
  
  public final void popQuick()
  {
    m_firstFree -= 1;
    m_map[m_firstFree] = null;
  }
  
  public final Node peepOrNull()
  {
    return (null != m_map) && (m_firstFree > 0) ? m_map[(m_firstFree - 1)] : null;
  }
  
  public final void pushPair(Node paramNode1, Node paramNode2)
  {
    if (null == m_map)
    {
      m_map = new Node[m_blocksize];
      m_mapSize = m_blocksize;
    }
    else if (m_firstFree + 2 >= m_mapSize)
    {
      m_mapSize += m_blocksize;
      Node[] arrayOfNode = new Node[m_mapSize];
      System.arraycopy(m_map, 0, arrayOfNode, 0, m_firstFree);
      m_map = arrayOfNode;
    }
    m_map[m_firstFree] = paramNode1;
    m_map[(m_firstFree + 1)] = paramNode2;
    m_firstFree += 2;
  }
  
  public final void popPair()
  {
    m_firstFree -= 2;
    m_map[m_firstFree] = null;
    m_map[(m_firstFree + 1)] = null;
  }
  
  public final void setTail(Node paramNode)
  {
    m_map[(m_firstFree - 1)] = paramNode;
  }
  
  public final void setTailSub1(Node paramNode)
  {
    m_map[(m_firstFree - 2)] = paramNode;
  }
  
  public final Node peepTail()
  {
    return m_map[(m_firstFree - 1)];
  }
  
  public final Node peepTailSub1()
  {
    return m_map[(m_firstFree - 2)];
  }
  
  public void insertElementAt(Node paramNode, int paramInt)
  {
    if (!m_mutable) {
      throw new RuntimeException(XSLMessages.createXPATHMessage("ER_NODESET_NOT_MUTABLE", null));
    }
    if (null == m_map)
    {
      m_map = new Node[m_blocksize];
      m_mapSize = m_blocksize;
    }
    else if (m_firstFree + 1 >= m_mapSize)
    {
      m_mapSize += m_blocksize;
      Node[] arrayOfNode = new Node[m_mapSize];
      System.arraycopy(m_map, 0, arrayOfNode, 0, m_firstFree + 1);
      m_map = arrayOfNode;
    }
    if (paramInt <= m_firstFree - 1) {
      System.arraycopy(m_map, paramInt, m_map, paramInt + 1, m_firstFree - paramInt);
    }
    m_map[paramInt] = paramNode;
    m_firstFree += 1;
  }
  
  public void appendNodes(NodeSet paramNodeSet)
  {
    int i = paramNodeSet.size();
    if (null == m_map)
    {
      m_mapSize = (i + m_blocksize);
      m_map = new Node[m_mapSize];
    }
    else if (m_firstFree + i >= m_mapSize)
    {
      m_mapSize += i + m_blocksize;
      Node[] arrayOfNode = new Node[m_mapSize];
      System.arraycopy(m_map, 0, arrayOfNode, 0, m_firstFree + i);
      m_map = arrayOfNode;
    }
    System.arraycopy(m_map, 0, m_map, m_firstFree, i);
    m_firstFree += i;
  }
  
  public void removeAllElements()
  {
    if (null == m_map) {
      return;
    }
    for (int i = 0; i < m_firstFree; i++) {
      m_map[i] = null;
    }
    m_firstFree = 0;
  }
  
  public boolean removeElement(Node paramNode)
  {
    if (!m_mutable) {
      throw new RuntimeException(XSLMessages.createXPATHMessage("ER_NODESET_NOT_MUTABLE", null));
    }
    if (null == m_map) {
      return false;
    }
    for (int i = 0; i < m_firstFree; i++)
    {
      Node localNode = m_map[i];
      if ((null != localNode) && (localNode.equals(paramNode)))
      {
        if (i < m_firstFree - 1) {
          System.arraycopy(m_map, i + 1, m_map, i, m_firstFree - i - 1);
        }
        m_firstFree -= 1;
        m_map[m_firstFree] = null;
        return true;
      }
    }
    return false;
  }
  
  public void removeElementAt(int paramInt)
  {
    if (null == m_map) {
      return;
    }
    if (paramInt >= m_firstFree) {
      throw new ArrayIndexOutOfBoundsException(paramInt + " >= " + m_firstFree);
    }
    if (paramInt < 0) {
      throw new ArrayIndexOutOfBoundsException(paramInt);
    }
    if (paramInt < m_firstFree - 1) {
      System.arraycopy(m_map, paramInt + 1, m_map, paramInt, m_firstFree - paramInt - 1);
    }
    m_firstFree -= 1;
    m_map[m_firstFree] = null;
  }
  
  public void setElementAt(Node paramNode, int paramInt)
  {
    if (!m_mutable) {
      throw new RuntimeException(XSLMessages.createXPATHMessage("ER_NODESET_NOT_MUTABLE", null));
    }
    if (null == m_map)
    {
      m_map = new Node[m_blocksize];
      m_mapSize = m_blocksize;
    }
    m_map[paramInt] = paramNode;
  }
  
  public Node elementAt(int paramInt)
  {
    if (null == m_map) {
      return null;
    }
    return m_map[paramInt];
  }
  
  public boolean contains(Node paramNode)
  {
    runTo(-1);
    if (null == m_map) {
      return false;
    }
    for (int i = 0; i < m_firstFree; i++)
    {
      Node localNode = m_map[i];
      if ((null != localNode) && (localNode.equals(paramNode))) {
        return true;
      }
    }
    return false;
  }
  
  public int indexOf(Node paramNode, int paramInt)
  {
    runTo(-1);
    if (null == m_map) {
      return -1;
    }
    for (int i = paramInt; i < m_firstFree; i++)
    {
      Node localNode = m_map[i];
      if ((null != localNode) && (localNode.equals(paramNode))) {
        return i;
      }
    }
    return -1;
  }
  
  public int indexOf(Node paramNode)
  {
    runTo(-1);
    if (null == m_map) {
      return -1;
    }
    for (int i = 0; i < m_firstFree; i++)
    {
      Node localNode = m_map[i];
      if ((null != localNode) && (localNode.equals(paramNode))) {
        return i;
      }
    }
    return -1;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\NodeSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */