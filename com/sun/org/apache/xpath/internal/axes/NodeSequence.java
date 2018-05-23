package com.sun.org.apache.xpath.internal.axes;

import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.dtm.DTMIterator;
import com.sun.org.apache.xml.internal.dtm.DTMManager;
import com.sun.org.apache.xml.internal.utils.NodeVector;
import com.sun.org.apache.xpath.internal.NodeSetDTM;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XObject;
import java.util.Vector;

public class NodeSequence
  extends XObject
  implements DTMIterator, Cloneable, PathComponent
{
  static final long serialVersionUID = 3866261934726581044L;
  protected int m_last = -1;
  protected int m_next = 0;
  private IteratorCache m_cache;
  protected DTMIterator m_iter;
  protected DTMManager m_dtmMgr;
  
  protected NodeVector getVector()
  {
    NodeVector localNodeVector = m_cache != null ? m_cache.getVector() : null;
    return localNodeVector;
  }
  
  private IteratorCache getCache()
  {
    return m_cache;
  }
  
  protected void SetVector(NodeVector paramNodeVector)
  {
    setObject(paramNodeVector);
  }
  
  public boolean hasCache()
  {
    NodeVector localNodeVector = getVector();
    return localNodeVector != null;
  }
  
  private boolean cacheComplete()
  {
    boolean bool;
    if (m_cache != null) {
      bool = m_cache.isComplete();
    } else {
      bool = false;
    }
    return bool;
  }
  
  private void markCacheComplete()
  {
    NodeVector localNodeVector = getVector();
    if (localNodeVector != null) {
      m_cache.setCacheComplete(true);
    }
  }
  
  public final void setIter(DTMIterator paramDTMIterator)
  {
    m_iter = paramDTMIterator;
  }
  
  public final DTMIterator getContainedIter()
  {
    return m_iter;
  }
  
  private NodeSequence(DTMIterator paramDTMIterator, int paramInt, XPathContext paramXPathContext, boolean paramBoolean)
  {
    setIter(paramDTMIterator);
    setRoot(paramInt, paramXPathContext);
    setShouldCacheNodes(paramBoolean);
  }
  
  public NodeSequence(Object paramObject)
  {
    super(paramObject);
    if ((paramObject instanceof NodeVector)) {
      SetVector((NodeVector)paramObject);
    }
    if (null != paramObject)
    {
      assertion(paramObject instanceof NodeVector, "Must have a NodeVector as the object for NodeSequence!");
      if ((paramObject instanceof DTMIterator))
      {
        setIter((DTMIterator)paramObject);
        m_last = ((DTMIterator)paramObject).getLength();
      }
    }
  }
  
  private NodeSequence(DTMManager paramDTMManager)
  {
    super(new NodeVector());
    m_last = 0;
    m_dtmMgr = paramDTMManager;
  }
  
  public NodeSequence() {}
  
  public DTM getDTM(int paramInt)
  {
    DTMManager localDTMManager = getDTMManager();
    if (null != localDTMManager) {
      return getDTMManager().getDTM(paramInt);
    }
    assertion(false, "Can not get a DTM Unless a DTMManager has been set!");
    return null;
  }
  
  public DTMManager getDTMManager()
  {
    return m_dtmMgr;
  }
  
  public int getRoot()
  {
    if (null != m_iter) {
      return m_iter.getRoot();
    }
    return -1;
  }
  
  public void setRoot(int paramInt, Object paramObject)
  {
    if (paramInt == -1) {
      throw new RuntimeException("Unable to evaluate expression using this context");
    }
    if (null != m_iter)
    {
      XPathContext localXPathContext = (XPathContext)paramObject;
      m_dtmMgr = localXPathContext.getDTMManager();
      m_iter.setRoot(paramInt, paramObject);
      if (!m_iter.isDocOrdered())
      {
        if (!hasCache()) {
          setShouldCacheNodes(true);
        }
        runTo(-1);
        m_next = 0;
      }
    }
    else
    {
      assertion(false, "Can not setRoot on a non-iterated NodeSequence!");
    }
  }
  
  public void reset()
  {
    m_next = 0;
  }
  
  public int getWhatToShow()
  {
    return hasCache() ? -17 : m_iter.getWhatToShow();
  }
  
  public boolean getExpandEntityReferences()
  {
    if (null != m_iter) {
      return m_iter.getExpandEntityReferences();
    }
    return true;
  }
  
  public int nextNode()
  {
    NodeVector localNodeVector = getVector();
    if (null != localNodeVector)
    {
      if (m_next < localNodeVector.size())
      {
        i = localNodeVector.elementAt(m_next);
        m_next += 1;
        return i;
      }
      if ((cacheComplete()) || (-1 != m_last) || (null == m_iter))
      {
        m_next += 1;
        return -1;
      }
    }
    if (null == m_iter) {
      return -1;
    }
    int i = m_iter.nextNode();
    if (-1 != i)
    {
      if (hasCache())
      {
        if (m_iter.isDocOrdered())
        {
          getVector().addElement(i);
          m_next += 1;
        }
        else
        {
          int j = addNodeInDocOrder(i);
          if (j >= 0) {
            m_next += 1;
          }
        }
      }
      else {
        m_next += 1;
      }
    }
    else
    {
      markCacheComplete();
      m_last = m_next;
      m_next += 1;
    }
    return i;
  }
  
  public int previousNode()
  {
    if (hasCache())
    {
      if (m_next <= 0) {
        return -1;
      }
      m_next -= 1;
      return item(m_next);
    }
    int i = m_iter.previousNode();
    m_next = m_iter.getCurrentPos();
    return m_next;
  }
  
  public void detach()
  {
    if (null != m_iter) {
      m_iter.detach();
    }
    super.detach();
  }
  
  public void allowDetachToRelease(boolean paramBoolean)
  {
    if ((false == paramBoolean) && (!hasCache())) {
      setShouldCacheNodes(true);
    }
    if (null != m_iter) {
      m_iter.allowDetachToRelease(paramBoolean);
    }
    super.allowDetachToRelease(paramBoolean);
  }
  
  public int getCurrentNode()
  {
    if (hasCache())
    {
      int i = m_next - 1;
      NodeVector localNodeVector = getVector();
      if ((i >= 0) && (i < localNodeVector.size())) {
        return localNodeVector.elementAt(i);
      }
      return -1;
    }
    if (null != m_iter) {
      return m_iter.getCurrentNode();
    }
    return -1;
  }
  
  public boolean isFresh()
  {
    return 0 == m_next;
  }
  
  public void setShouldCacheNodes(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      if (!hasCache()) {
        SetVector(new NodeVector());
      }
    }
    else {
      SetVector(null);
    }
  }
  
  public boolean isMutable()
  {
    return hasCache();
  }
  
  public int getCurrentPos()
  {
    return m_next;
  }
  
  public void runTo(int paramInt)
  {
    int i;
    if (-1 == paramInt)
    {
      int j = m_next;
      while (-1 != (i = nextNode())) {}
      m_next = j;
    }
    else
    {
      if (m_next == paramInt) {
        return;
      }
      if ((hasCache()) && (paramInt < getVector().size()))
      {
        m_next = paramInt;
      }
      else
      {
        if ((null == getVector()) && (paramInt < m_next)) {}
        while ((m_next >= paramInt) && (-1 != (i = previousNode())))
        {
          continue;
          while ((m_next < paramInt) && (-1 != (i = nextNode()))) {}
        }
      }
    }
  }
  
  public void setCurrentPos(int paramInt)
  {
    runTo(paramInt);
  }
  
  public int item(int paramInt)
  {
    setCurrentPos(paramInt);
    int i = nextNode();
    m_next = paramInt;
    return i;
  }
  
  public void setItem(int paramInt1, int paramInt2)
  {
    Object localObject = getVector();
    if (null != localObject)
    {
      int i = ((NodeVector)localObject).elementAt(paramInt2);
      if ((i != paramInt1) && (m_cache.useCount() > 1))
      {
        IteratorCache localIteratorCache = new IteratorCache();
        NodeVector localNodeVector;
        try
        {
          localNodeVector = (NodeVector)((NodeVector)localObject).clone();
        }
        catch (CloneNotSupportedException localCloneNotSupportedException)
        {
          localCloneNotSupportedException.printStackTrace();
          RuntimeException localRuntimeException = new RuntimeException(localCloneNotSupportedException.getMessage());
          throw localRuntimeException;
        }
        localIteratorCache.setVector(localNodeVector);
        localIteratorCache.setCacheComplete(true);
        m_cache = localIteratorCache;
        localObject = localNodeVector;
        super.setObject(localNodeVector);
      }
      ((NodeVector)localObject).setElementAt(paramInt1, paramInt2);
      m_last = ((NodeVector)localObject).size();
    }
    else
    {
      m_iter.setItem(paramInt1, paramInt2);
    }
  }
  
  public int getLength()
  {
    IteratorCache localIteratorCache = getCache();
    if (localIteratorCache != null)
    {
      if (localIteratorCache.isComplete())
      {
        NodeVector localNodeVector = localIteratorCache.getVector();
        return localNodeVector.size();
      }
      if ((m_iter instanceof NodeSetDTM)) {
        return m_iter.getLength();
      }
      if (-1 == m_last)
      {
        int i = m_next;
        runTo(-1);
        m_next = i;
      }
      return m_last;
    }
    return -1 == m_last ? (m_last = m_iter.getLength()) : m_last;
  }
  
  public DTMIterator cloneWithReset()
    throws CloneNotSupportedException
  {
    NodeSequence localNodeSequence = (NodeSequence)super.clone();
    m_next = 0;
    if (m_cache != null) {
      m_cache.increaseUseCount();
    }
    return localNodeSequence;
  }
  
  public Object clone()
    throws CloneNotSupportedException
  {
    NodeSequence localNodeSequence = (NodeSequence)super.clone();
    if (null != m_iter) {
      m_iter = ((DTMIterator)m_iter.clone());
    }
    if (m_cache != null) {
      m_cache.increaseUseCount();
    }
    return localNodeSequence;
  }
  
  public boolean isDocOrdered()
  {
    if (null != m_iter) {
      return m_iter.isDocOrdered();
    }
    return true;
  }
  
  public int getAxis()
  {
    if (null != m_iter) {
      return m_iter.getAxis();
    }
    assertion(false, "Can not getAxis from a non-iterated node sequence!");
    return 0;
  }
  
  public int getAnalysisBits()
  {
    if ((null != m_iter) && ((m_iter instanceof PathComponent))) {
      return ((PathComponent)m_iter).getAnalysisBits();
    }
    return 0;
  }
  
  public void fixupVariables(Vector paramVector, int paramInt)
  {
    super.fixupVariables(paramVector, paramInt);
  }
  
  protected int addNodeInDocOrder(int paramInt)
  {
    assertion(hasCache(), "addNodeInDocOrder must be done on a mutable sequence!");
    int i = -1;
    NodeVector localNodeVector = getVector();
    int j = localNodeVector.size();
    for (int k = j - 1; k >= 0; k--)
    {
      int m = localNodeVector.elementAt(k);
      if (m == paramInt)
      {
        k = -2;
      }
      else
      {
        DTM localDTM = m_dtmMgr.getDTM(paramInt);
        if (!localDTM.isNodeAfter(paramInt, m)) {
          break;
        }
      }
    }
    if (k != -2)
    {
      i = k + 1;
      localNodeVector.insertElementAt(paramInt, i);
    }
    return i;
  }
  
  protected void setObject(Object paramObject)
  {
    Object localObject;
    if ((paramObject instanceof NodeVector))
    {
      super.setObject(paramObject);
      localObject = (NodeVector)paramObject;
      if (m_cache != null)
      {
        m_cache.setVector((NodeVector)localObject);
      }
      else if (localObject != null)
      {
        m_cache = new IteratorCache();
        m_cache.setVector((NodeVector)localObject);
      }
    }
    else if ((paramObject instanceof IteratorCache))
    {
      localObject = (IteratorCache)paramObject;
      m_cache = ((IteratorCache)localObject);
      m_cache.increaseUseCount();
      super.setObject(((IteratorCache)localObject).getVector());
    }
    else
    {
      super.setObject(paramObject);
    }
  }
  
  protected IteratorCache getIteratorCache()
  {
    return m_cache;
  }
  
  private static final class IteratorCache
  {
    private NodeVector m_vec2 = null;
    private boolean m_isComplete2 = false;
    private int m_useCount2 = 1;
    
    IteratorCache() {}
    
    private int useCount()
    {
      return m_useCount2;
    }
    
    private void increaseUseCount()
    {
      if (m_vec2 != null) {
        m_useCount2 += 1;
      }
    }
    
    private void setVector(NodeVector paramNodeVector)
    {
      m_vec2 = paramNodeVector;
      m_useCount2 = 1;
    }
    
    private NodeVector getVector()
    {
      return m_vec2;
    }
    
    private void setCacheComplete(boolean paramBoolean)
    {
      m_isComplete2 = paramBoolean;
    }
    
    private boolean isComplete()
    {
      return m_isComplete2;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\axes\NodeSequence.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */