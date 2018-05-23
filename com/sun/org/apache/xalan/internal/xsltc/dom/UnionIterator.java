package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;

public final class UnionIterator
  extends MultiValuedNodeHeapIterator
{
  private final DOM _dom;
  
  public UnionIterator(DOM paramDOM)
  {
    _dom = paramDOM;
  }
  
  public UnionIterator addIterator(DTMAxisIterator paramDTMAxisIterator)
  {
    addHeapNode(new LookAheadIterator(paramDTMAxisIterator));
    return this;
  }
  
  private final class LookAheadIterator
    extends MultiValuedNodeHeapIterator.HeapNode
  {
    public DTMAxisIterator iterator;
    
    public LookAheadIterator(DTMAxisIterator paramDTMAxisIterator)
    {
      super();
      iterator = paramDTMAxisIterator;
    }
    
    public int step()
    {
      _node = iterator.next();
      return _node;
    }
    
    public MultiValuedNodeHeapIterator.HeapNode cloneHeapNode()
    {
      LookAheadIterator localLookAheadIterator = (LookAheadIterator)super.cloneHeapNode();
      iterator = iterator.cloneIterator();
      return localLookAheadIterator;
    }
    
    public void setMark()
    {
      super.setMark();
      iterator.setMark();
    }
    
    public void gotoMark()
    {
      super.gotoMark();
      iterator.gotoMark();
    }
    
    public boolean isLessThan(MultiValuedNodeHeapIterator.HeapNode paramHeapNode)
    {
      LookAheadIterator localLookAheadIterator = (LookAheadIterator)paramHeapNode;
      return _dom.lessThan(_node, _node);
    }
    
    public MultiValuedNodeHeapIterator.HeapNode setStartNode(int paramInt)
    {
      iterator.setStartNode(paramInt);
      return this;
    }
    
    public MultiValuedNodeHeapIterator.HeapNode reset()
    {
      iterator.reset();
      return this;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\dom\UnionIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */