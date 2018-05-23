package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.DOMEnhancedForDTM;
import com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary;
import com.sun.org.apache.xalan.internal.xsltc.util.IntegerArray;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.dtm.ref.DTMAxisIteratorBase;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class KeyIndex
  extends DTMAxisIteratorBase
{
  private Map<String, IntegerArray> _index;
  private int _currentDocumentNode = -1;
  private Map<Integer, Map> _rootToIndexMap = new HashMap();
  private IntegerArray _nodes = null;
  private DOM _dom;
  private DOMEnhancedForDTM _enhancedDOM;
  private int _markedPosition = 0;
  private static final IntegerArray EMPTY_NODES = new IntegerArray(0);
  
  public KeyIndex(int paramInt) {}
  
  public void setRestartable(boolean paramBoolean) {}
  
  public void add(String paramString, int paramInt1, int paramInt2)
  {
    if (_currentDocumentNode != paramInt2)
    {
      _currentDocumentNode = paramInt2;
      _index = new HashMap();
      _rootToIndexMap.put(Integer.valueOf(paramInt2), _index);
    }
    IntegerArray localIntegerArray = (IntegerArray)_index.get(paramString);
    if (localIntegerArray == null)
    {
      localIntegerArray = new IntegerArray();
      _index.put(paramString, localIntegerArray);
      localIntegerArray.add(paramInt1);
    }
    else if (paramInt1 != localIntegerArray.at(localIntegerArray.cardinality() - 1))
    {
      localIntegerArray.add(paramInt1);
    }
  }
  
  /**
   * @deprecated
   */
  public void merge(KeyIndex paramKeyIndex)
  {
    if (paramKeyIndex == null) {
      return;
    }
    if (_nodes != null) {
      if (_nodes == null) {
        _nodes = ((IntegerArray)_nodes.clone());
      } else {
        _nodes.merge(_nodes);
      }
    }
  }
  
  /**
   * @deprecated
   */
  public void lookupId(Object paramObject)
  {
    _nodes = null;
    StringTokenizer localStringTokenizer = new StringTokenizer((String)paramObject, " \n\t");
    while (localStringTokenizer.hasMoreElements())
    {
      String str = (String)localStringTokenizer.nextElement();
      IntegerArray localIntegerArray = (IntegerArray)_index.get(str);
      if ((localIntegerArray == null) && (_enhancedDOM != null) && (_enhancedDOM.hasDOMSource())) {
        localIntegerArray = getDOMNodeById(str);
      }
      if (localIntegerArray != null) {
        if (_nodes == null)
        {
          localIntegerArray = (IntegerArray)localIntegerArray.clone();
          _nodes = localIntegerArray;
        }
        else
        {
          _nodes.merge(localIntegerArray);
        }
      }
    }
  }
  
  public IntegerArray getDOMNodeById(String paramString)
  {
    IntegerArray localIntegerArray = null;
    if (_enhancedDOM != null)
    {
      int i = _enhancedDOM.getElementById(paramString);
      if (i != -1)
      {
        Integer localInteger = new Integer(_enhancedDOM.getDocument());
        Object localObject = (Map)_rootToIndexMap.get(localInteger);
        if (localObject == null)
        {
          localObject = new HashMap();
          _rootToIndexMap.put(localInteger, localObject);
        }
        else
        {
          localIntegerArray = (IntegerArray)((Map)localObject).get(paramString);
        }
        if (localIntegerArray == null)
        {
          localIntegerArray = new IntegerArray();
          ((Map)localObject).put(paramString, localIntegerArray);
        }
        localIntegerArray.add(_enhancedDOM.getNodeHandle(i));
      }
    }
    return localIntegerArray;
  }
  
  /**
   * @deprecated
   */
  public void lookupKey(Object paramObject)
  {
    IntegerArray localIntegerArray = (IntegerArray)_index.get(paramObject);
    _nodes = (localIntegerArray != null ? (IntegerArray)localIntegerArray.clone() : null);
    _position = 0;
  }
  
  /**
   * @deprecated
   */
  public int next()
  {
    if (_nodes == null) {
      return -1;
    }
    return _position < _nodes.cardinality() ? _dom.getNodeHandle(_nodes.at(_position++)) : -1;
  }
  
  public int containsID(int paramInt, Object paramObject)
  {
    String str1 = (String)paramObject;
    int i = _dom.getAxisIterator(19).setStartNode(paramInt).next();
    Map localMap = (Map)_rootToIndexMap.get(Integer.valueOf(i));
    StringTokenizer localStringTokenizer = new StringTokenizer(str1, " \n\t");
    while (localStringTokenizer.hasMoreElements())
    {
      String str2 = (String)localStringTokenizer.nextElement();
      IntegerArray localIntegerArray = null;
      if (localMap != null) {
        localIntegerArray = (IntegerArray)localMap.get(str2);
      }
      if ((localIntegerArray == null) && (_enhancedDOM != null) && (_enhancedDOM.hasDOMSource())) {
        localIntegerArray = getDOMNodeById(str2);
      }
      if ((localIntegerArray != null) && (localIntegerArray.indexOf(paramInt) >= 0)) {
        return 1;
      }
    }
    return 0;
  }
  
  public int containsKey(int paramInt, Object paramObject)
  {
    int i = _dom.getAxisIterator(19).setStartNode(paramInt).next();
    Map localMap = (Map)_rootToIndexMap.get(new Integer(i));
    if (localMap != null)
    {
      IntegerArray localIntegerArray = (IntegerArray)localMap.get(paramObject);
      return (localIntegerArray != null) && (localIntegerArray.indexOf(paramInt) >= 0) ? 1 : 0;
    }
    return 0;
  }
  
  /**
   * @deprecated
   */
  public DTMAxisIterator reset()
  {
    _position = 0;
    return this;
  }
  
  /**
   * @deprecated
   */
  public int getLast()
  {
    return _nodes == null ? 0 : _nodes.cardinality();
  }
  
  /**
   * @deprecated
   */
  public int getPosition()
  {
    return _position;
  }
  
  /**
   * @deprecated
   */
  public void setMark()
  {
    _markedPosition = _position;
  }
  
  /**
   * @deprecated
   */
  public void gotoMark()
  {
    _position = _markedPosition;
  }
  
  /**
   * @deprecated
   */
  public DTMAxisIterator setStartNode(int paramInt)
  {
    if (paramInt == -1) {
      _nodes = null;
    } else if (_nodes != null) {
      _position = 0;
    }
    return this;
  }
  
  /**
   * @deprecated
   */
  public int getStartNode()
  {
    return 0;
  }
  
  /**
   * @deprecated
   */
  public boolean isReverse()
  {
    return false;
  }
  
  /**
   * @deprecated
   */
  public DTMAxisIterator cloneIterator()
  {
    KeyIndex localKeyIndex = new KeyIndex(0);
    _index = _index;
    _rootToIndexMap = _rootToIndexMap;
    _nodes = _nodes;
    _position = _position;
    return localKeyIndex;
  }
  
  public void setDom(DOM paramDOM, int paramInt)
  {
    _dom = paramDOM;
    if ((paramDOM instanceof MultiDOM)) {
      paramDOM = ((MultiDOM)paramDOM).getDTM(paramInt);
    }
    if ((paramDOM instanceof DOMEnhancedForDTM))
    {
      _enhancedDOM = ((DOMEnhancedForDTM)paramDOM);
    }
    else if ((paramDOM instanceof DOMAdapter))
    {
      DOM localDOM = ((DOMAdapter)paramDOM).getDOMImpl();
      if ((localDOM instanceof DOMEnhancedForDTM)) {
        _enhancedDOM = ((DOMEnhancedForDTM)localDOM);
      }
    }
  }
  
  public KeyIndexIterator getKeyIndexIterator(Object paramObject, boolean paramBoolean)
  {
    if ((paramObject instanceof DTMAxisIterator)) {
      return getKeyIndexIterator((DTMAxisIterator)paramObject, paramBoolean);
    }
    return getKeyIndexIterator(BasisLibrary.stringF(paramObject, _dom), paramBoolean);
  }
  
  public KeyIndexIterator getKeyIndexIterator(String paramString, boolean paramBoolean)
  {
    return new KeyIndexIterator(paramString, paramBoolean);
  }
  
  public KeyIndexIterator getKeyIndexIterator(DTMAxisIterator paramDTMAxisIterator, boolean paramBoolean)
  {
    return new KeyIndexIterator(paramDTMAxisIterator, paramBoolean);
  }
  
  public class KeyIndexIterator
    extends MultiValuedNodeHeapIterator
  {
    private IntegerArray _nodes;
    private DTMAxisIterator _keyValueIterator;
    private String _keyValue;
    private boolean _isKeyIterator;
    
    KeyIndexIterator(String paramString, boolean paramBoolean)
    {
      _isKeyIterator = paramBoolean;
      _keyValue = paramString;
    }
    
    KeyIndexIterator(DTMAxisIterator paramDTMAxisIterator, boolean paramBoolean)
    {
      _keyValueIterator = paramDTMAxisIterator;
      _isKeyIterator = paramBoolean;
    }
    
    protected IntegerArray lookupNodes(int paramInt, String paramString)
    {
      IntegerArray localIntegerArray1 = null;
      Map localMap = (Map)_rootToIndexMap.get(Integer.valueOf(paramInt));
      if (!_isKeyIterator)
      {
        StringTokenizer localStringTokenizer = new StringTokenizer(paramString, " \n\t");
        while (localStringTokenizer.hasMoreElements())
        {
          String str = (String)localStringTokenizer.nextElement();
          IntegerArray localIntegerArray2 = null;
          if (localMap != null) {
            localIntegerArray2 = (IntegerArray)localMap.get(str);
          }
          if ((localIntegerArray2 == null) && (_enhancedDOM != null) && (_enhancedDOM.hasDOMSource())) {
            localIntegerArray2 = getDOMNodeById(str);
          }
          if (localIntegerArray2 != null) {
            if (localIntegerArray1 == null) {
              localIntegerArray1 = (IntegerArray)localIntegerArray2.clone();
            } else {
              localIntegerArray1.merge(localIntegerArray2);
            }
          }
        }
      }
      else if (localMap != null)
      {
        localIntegerArray1 = (IntegerArray)localMap.get(paramString);
      }
      return localIntegerArray1;
    }
    
    public DTMAxisIterator setStartNode(int paramInt)
    {
      _startNode = paramInt;
      if (_keyValueIterator != null) {
        _keyValueIterator = _keyValueIterator.setStartNode(paramInt);
      }
      init();
      return super.setStartNode(paramInt);
    }
    
    public int next()
    {
      int i;
      if (_nodes != null)
      {
        if (_position < _nodes.cardinality()) {
          i = returnNode(_nodes.at(_position));
        } else {
          i = -1;
        }
      }
      else {
        i = super.next();
      }
      return i;
    }
    
    public DTMAxisIterator reset()
    {
      if (_nodes == null) {
        init();
      } else {
        super.reset();
      }
      return resetPosition();
    }
    
    protected void init()
    {
      super.init();
      _position = 0;
      int i = _dom.getAxisIterator(19).setStartNode(_startNode).next();
      if (_keyValueIterator == null)
      {
        _nodes = lookupNodes(i, _keyValue);
        if (_nodes == null) {
          _nodes = KeyIndex.EMPTY_NODES;
        }
      }
      else
      {
        DTMAxisIterator localDTMAxisIterator = _keyValueIterator.reset();
        int j = 0;
        int k = 0;
        _nodes = null;
        for (int m = localDTMAxisIterator.next(); m != -1; m = localDTMAxisIterator.next())
        {
          String str = BasisLibrary.stringF(m, _dom);
          IntegerArray localIntegerArray = lookupNodes(i, str);
          if (localIntegerArray != null) {
            if (k == 0)
            {
              _nodes = localIntegerArray;
              k = 1;
            }
            else
            {
              if (_nodes != null)
              {
                addHeapNode(new KeyIndexHeapNode(_nodes));
                _nodes = null;
              }
              addHeapNode(new KeyIndexHeapNode(localIntegerArray));
            }
          }
        }
        if (k == 0) {
          _nodes = KeyIndex.EMPTY_NODES;
        }
      }
    }
    
    public int getLast()
    {
      return _nodes != null ? _nodes.cardinality() : super.getLast();
    }
    
    public int getNodeByPosition(int paramInt)
    {
      int i = -1;
      if (_nodes != null)
      {
        if (paramInt > 0) {
          if (paramInt <= _nodes.cardinality())
          {
            _position = paramInt;
            i = _nodes.at(paramInt - 1);
          }
          else
          {
            _position = _nodes.cardinality();
          }
        }
      }
      else {
        i = super.getNodeByPosition(paramInt);
      }
      return i;
    }
    
    protected class KeyIndexHeapNode
      extends MultiValuedNodeHeapIterator.HeapNode
    {
      private IntegerArray _nodes;
      private int _position = 0;
      private int _markPosition = -1;
      
      KeyIndexHeapNode(IntegerArray paramIntegerArray)
      {
        super();
        _nodes = paramIntegerArray;
      }
      
      public int step()
      {
        if (_position < _nodes.cardinality())
        {
          _node = _nodes.at(_position);
          _position += 1;
        }
        else
        {
          _node = -1;
        }
        return _node;
      }
      
      public MultiValuedNodeHeapIterator.HeapNode cloneHeapNode()
      {
        KeyIndexHeapNode localKeyIndexHeapNode = (KeyIndexHeapNode)super.cloneHeapNode();
        _nodes = _nodes;
        _position = _position;
        _markPosition = _markPosition;
        return localKeyIndexHeapNode;
      }
      
      public void setMark()
      {
        _markPosition = _position;
      }
      
      public void gotoMark()
      {
        _position = _markPosition;
      }
      
      public boolean isLessThan(MultiValuedNodeHeapIterator.HeapNode paramHeapNode)
      {
        return _node < _node;
      }
      
      public MultiValuedNodeHeapIterator.HeapNode setStartNode(int paramInt)
      {
        return this;
      }
      
      public MultiValuedNodeHeapIterator.HeapNode reset()
      {
        _position = 0;
        return this;
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\dom\KeyIndex.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */