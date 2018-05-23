package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.StripFilter;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary;
import com.sun.org.apache.xml.internal.dtm.Axis;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.dtm.DTMManager;
import com.sun.org.apache.xml.internal.dtm.ref.DTMAxisIterNodeList;
import com.sun.org.apache.xml.internal.dtm.ref.DTMAxisIteratorBase;
import com.sun.org.apache.xml.internal.dtm.ref.DTMDefaultBase;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import com.sun.org.apache.xml.internal.utils.SuballocatedIntVector;
import java.util.HashMap;
import java.util.Map;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public final class MultiDOM
  implements DOM
{
  private static final int NO_TYPE = -2;
  private static final int INITIAL_SIZE = 4;
  private DOM[] _adapters = new DOM[4];
  private DOMAdapter _main;
  private DTMManager _dtmManager;
  private int _free = 1;
  private int _size = 4;
  private Map<String, Integer> _documents = new HashMap();
  
  public MultiDOM(DOM paramDOM)
  {
    DOMAdapter localDOMAdapter = (DOMAdapter)paramDOM;
    _adapters[0] = localDOMAdapter;
    _main = localDOMAdapter;
    DOM localDOM = localDOMAdapter.getDOMImpl();
    if ((localDOM instanceof DTMDefaultBase)) {
      _dtmManager = ((DTMDefaultBase)localDOM).getManager();
    }
    addDOMAdapter(localDOMAdapter, false);
  }
  
  public int nextMask()
  {
    return _free;
  }
  
  public void setupMapping(String[] paramArrayOfString1, String[] paramArrayOfString2, int[] paramArrayOfInt, String[] paramArrayOfString3) {}
  
  public int addDOMAdapter(DOMAdapter paramDOMAdapter)
  {
    return addDOMAdapter(paramDOMAdapter, true);
  }
  
  private int addDOMAdapter(DOMAdapter paramDOMAdapter, boolean paramBoolean)
  {
    DOM localDOM1 = paramDOMAdapter.getDOMImpl();
    int i = 1;
    int j = 1;
    SuballocatedIntVector localSuballocatedIntVector = null;
    Object localObject1;
    if ((localDOM1 instanceof DTMDefaultBase))
    {
      localObject1 = (DTMDefaultBase)localDOM1;
      localSuballocatedIntVector = ((DTMDefaultBase)localObject1).getDTMIDs();
      j = localSuballocatedIntVector.size();
      i = localSuballocatedIntVector.elementAt(j - 1) >>> 16;
    }
    else if ((localDOM1 instanceof SimpleResultTreeImpl))
    {
      localObject1 = (SimpleResultTreeImpl)localDOM1;
      i = ((SimpleResultTreeImpl)localObject1).getDocument() >>> 16;
    }
    int k;
    if (i >= _size)
    {
      k = _size;
      do
      {
        _size *= 2;
      } while (_size <= i);
      DOMAdapter[] arrayOfDOMAdapter = new DOMAdapter[_size];
      System.arraycopy(_adapters, 0, arrayOfDOMAdapter, 0, k);
      _adapters = arrayOfDOMAdapter;
    }
    _free = (i + 1);
    if (j == 1)
    {
      _adapters[i] = paramDOMAdapter;
    }
    else if (localSuballocatedIntVector != null)
    {
      k = 0;
      for (int m = j - 1; m >= 0; m--)
      {
        k = localSuballocatedIntVector.elementAt(m) >>> 16;
        _adapters[k] = paramDOMAdapter;
      }
      i = k;
    }
    Object localObject2;
    if (paramBoolean)
    {
      localObject2 = paramDOMAdapter.getDocumentURI(0);
      _documents.put(localObject2, Integer.valueOf(i));
    }
    if ((localDOM1 instanceof AdaptiveResultTreeImpl))
    {
      localObject2 = (AdaptiveResultTreeImpl)localDOM1;
      DOM localDOM2 = ((AdaptiveResultTreeImpl)localObject2).getNestedDOM();
      if (localDOM2 != null)
      {
        DOMAdapter localDOMAdapter = new DOMAdapter(localDOM2, paramDOMAdapter.getNamesArray(), paramDOMAdapter.getUrisArray(), paramDOMAdapter.getTypesArray(), paramDOMAdapter.getNamespaceArray());
        addDOMAdapter(localDOMAdapter);
      }
    }
    return i;
  }
  
  public int getDocumentMask(String paramString)
  {
    Integer localInteger = (Integer)_documents.get(paramString);
    if (localInteger == null) {
      return -1;
    }
    return localInteger.intValue();
  }
  
  public DOM getDOMAdapter(String paramString)
  {
    Integer localInteger = (Integer)_documents.get(paramString);
    if (localInteger == null) {
      return null;
    }
    return _adapters[localInteger.intValue()];
  }
  
  public int getDocument()
  {
    return _main.getDocument();
  }
  
  public DTMManager getDTMManager()
  {
    return _dtmManager;
  }
  
  public DTMAxisIterator getIterator()
  {
    return _main.getIterator();
  }
  
  public String getStringValue()
  {
    return _main.getStringValue();
  }
  
  public DTMAxisIterator getChildren(int paramInt)
  {
    return _adapters[getDTMId(paramInt)].getChildren(paramInt);
  }
  
  public DTMAxisIterator getTypedChildren(int paramInt)
  {
    return new AxisIterator(3, paramInt);
  }
  
  public DTMAxisIterator getAxisIterator(int paramInt)
  {
    return new AxisIterator(paramInt, -2);
  }
  
  public DTMAxisIterator getTypedAxisIterator(int paramInt1, int paramInt2)
  {
    return new AxisIterator(paramInt1, paramInt2);
  }
  
  public DTMAxisIterator getNthDescendant(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    return _adapters[getDTMId(paramInt1)].getNthDescendant(paramInt1, paramInt2, paramBoolean);
  }
  
  public DTMAxisIterator getNodeValueIterator(DTMAxisIterator paramDTMAxisIterator, int paramInt, String paramString, boolean paramBoolean)
  {
    return new NodeValueIterator(paramDTMAxisIterator, paramInt, paramString, paramBoolean);
  }
  
  public DTMAxisIterator getNamespaceAxisIterator(int paramInt1, int paramInt2)
  {
    DTMAxisIterator localDTMAxisIterator = _main.getNamespaceAxisIterator(paramInt1, paramInt2);
    return localDTMAxisIterator;
  }
  
  public DTMAxisIterator orderNodes(DTMAxisIterator paramDTMAxisIterator, int paramInt)
  {
    return _adapters[getDTMId(paramInt)].orderNodes(paramDTMAxisIterator, paramInt);
  }
  
  public int getExpandedTypeID(int paramInt)
  {
    if (paramInt != -1) {
      return _adapters[(paramInt >>> 16)].getExpandedTypeID(paramInt);
    }
    return -1;
  }
  
  public int getNamespaceType(int paramInt)
  {
    return _adapters[getDTMId(paramInt)].getNamespaceType(paramInt);
  }
  
  public int getNSType(int paramInt)
  {
    return _adapters[getDTMId(paramInt)].getNSType(paramInt);
  }
  
  public int getParent(int paramInt)
  {
    if (paramInt == -1) {
      return -1;
    }
    return _adapters[(paramInt >>> 16)].getParent(paramInt);
  }
  
  public int getAttributeNode(int paramInt1, int paramInt2)
  {
    if (paramInt2 == -1) {
      return -1;
    }
    return _adapters[(paramInt2 >>> 16)].getAttributeNode(paramInt1, paramInt2);
  }
  
  public String getNodeName(int paramInt)
  {
    if (paramInt == -1) {
      return "";
    }
    return _adapters[(paramInt >>> 16)].getNodeName(paramInt);
  }
  
  public String getNodeNameX(int paramInt)
  {
    if (paramInt == -1) {
      return "";
    }
    return _adapters[(paramInt >>> 16)].getNodeNameX(paramInt);
  }
  
  public String getNamespaceName(int paramInt)
  {
    if (paramInt == -1) {
      return "";
    }
    return _adapters[(paramInt >>> 16)].getNamespaceName(paramInt);
  }
  
  public String getStringValueX(int paramInt)
  {
    if (paramInt == -1) {
      return "";
    }
    return _adapters[(paramInt >>> 16)].getStringValueX(paramInt);
  }
  
  public void copy(int paramInt, SerializationHandler paramSerializationHandler)
    throws TransletException
  {
    if (paramInt != -1) {
      _adapters[(paramInt >>> 16)].copy(paramInt, paramSerializationHandler);
    }
  }
  
  public void copy(DTMAxisIterator paramDTMAxisIterator, SerializationHandler paramSerializationHandler)
    throws TransletException
  {
    int i;
    while ((i = paramDTMAxisIterator.next()) != -1) {
      _adapters[(i >>> 16)].copy(i, paramSerializationHandler);
    }
  }
  
  public String shallowCopy(int paramInt, SerializationHandler paramSerializationHandler)
    throws TransletException
  {
    if (paramInt == -1) {
      return "";
    }
    return _adapters[(paramInt >>> 16)].shallowCopy(paramInt, paramSerializationHandler);
  }
  
  public boolean lessThan(int paramInt1, int paramInt2)
  {
    if (paramInt1 == -1) {
      return true;
    }
    if (paramInt2 == -1) {
      return false;
    }
    int i = getDTMId(paramInt1);
    int j = getDTMId(paramInt2);
    return i < j ? true : i == j ? _adapters[i].lessThan(paramInt1, paramInt2) : false;
  }
  
  public void characters(int paramInt, SerializationHandler paramSerializationHandler)
    throws TransletException
  {
    if (paramInt != -1) {
      _adapters[(paramInt >>> 16)].characters(paramInt, paramSerializationHandler);
    }
  }
  
  public void setFilter(StripFilter paramStripFilter)
  {
    for (int i = 0; i < _free; i++) {
      if (_adapters[i] != null) {
        _adapters[i].setFilter(paramStripFilter);
      }
    }
  }
  
  public Node makeNode(int paramInt)
  {
    if (paramInt == -1) {
      return null;
    }
    return _adapters[getDTMId(paramInt)].makeNode(paramInt);
  }
  
  public Node makeNode(DTMAxisIterator paramDTMAxisIterator)
  {
    return _main.makeNode(paramDTMAxisIterator);
  }
  
  public NodeList makeNodeList(int paramInt)
  {
    if (paramInt == -1) {
      return null;
    }
    return _adapters[getDTMId(paramInt)].makeNodeList(paramInt);
  }
  
  public NodeList makeNodeList(DTMAxisIterator paramDTMAxisIterator)
  {
    int i = paramDTMAxisIterator.next();
    if (i == -1) {
      return new DTMAxisIterNodeList(null, null);
    }
    paramDTMAxisIterator.reset();
    return _adapters[getDTMId(i)].makeNodeList(paramDTMAxisIterator);
  }
  
  public String getLanguage(int paramInt)
  {
    return _adapters[getDTMId(paramInt)].getLanguage(paramInt);
  }
  
  public int getSize()
  {
    int i = 0;
    for (int j = 0; j < _size; j++) {
      i += _adapters[j].getSize();
    }
    return i;
  }
  
  public String getDocumentURI(int paramInt)
  {
    if (paramInt == -1) {
      paramInt = 0;
    }
    return _adapters[(paramInt >>> 16)].getDocumentURI(0);
  }
  
  public boolean isElement(int paramInt)
  {
    if (paramInt == -1) {
      return false;
    }
    return _adapters[(paramInt >>> 16)].isElement(paramInt);
  }
  
  public boolean isAttribute(int paramInt)
  {
    if (paramInt == -1) {
      return false;
    }
    return _adapters[(paramInt >>> 16)].isAttribute(paramInt);
  }
  
  public int getDTMId(int paramInt)
  {
    if (paramInt == -1) {
      return 0;
    }
    for (int i = paramInt >>> 16; (i >= 2) && (_adapters[i] == _adapters[(i - 1)]); i--) {}
    return i;
  }
  
  public DOM getDTM(int paramInt)
  {
    return _adapters[getDTMId(paramInt)];
  }
  
  public int getNodeIdent(int paramInt)
  {
    return _adapters[(paramInt >>> 16)].getNodeIdent(paramInt);
  }
  
  public int getNodeHandle(int paramInt)
  {
    return _main.getNodeHandle(paramInt);
  }
  
  public DOM getResultTreeFrag(int paramInt1, int paramInt2)
  {
    return _main.getResultTreeFrag(paramInt1, paramInt2);
  }
  
  public DOM getResultTreeFrag(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    return _main.getResultTreeFrag(paramInt1, paramInt2, paramBoolean);
  }
  
  public DOM getMain()
  {
    return _main;
  }
  
  public SerializationHandler getOutputDomBuilder()
  {
    return _main.getOutputDomBuilder();
  }
  
  public String lookupNamespace(int paramInt, String paramString)
    throws TransletException
  {
    return _main.lookupNamespace(paramInt, paramString);
  }
  
  public String getUnparsedEntityURI(String paramString)
  {
    return _main.getUnparsedEntityURI(paramString);
  }
  
  public Map<String, Integer> getElementsWithIDs()
  {
    return _main.getElementsWithIDs();
  }
  
  public void release()
  {
    _main.release();
  }
  
  private boolean isMatchingAdapterEntry(DOM paramDOM, DOMAdapter paramDOMAdapter)
  {
    DOM localDOM = paramDOMAdapter.getDOMImpl();
    return (paramDOM == paramDOMAdapter) || (((localDOM instanceof AdaptiveResultTreeImpl)) && ((paramDOM instanceof DOMAdapter)) && (((AdaptiveResultTreeImpl)localDOM).getNestedDOM() == ((DOMAdapter)paramDOM).getDOMImpl()));
  }
  
  public void removeDOMAdapter(DOMAdapter paramDOMAdapter)
  {
    _documents.remove(paramDOMAdapter.getDocumentURI(0));
    DOM localDOM = paramDOMAdapter.getDOMImpl();
    int j;
    int k;
    if ((localDOM instanceof DTMDefaultBase))
    {
      SuballocatedIntVector localSuballocatedIntVector = ((DTMDefaultBase)localDOM).getDTMIDs();
      j = localSuballocatedIntVector.size();
      for (k = 0; k < j; k++) {
        _adapters[(localSuballocatedIntVector.elementAt(k) >>> 16)] = null;
      }
    }
    else
    {
      int i = localDOM.getDocument() >>> 16;
      if ((i > 0) && (i < _adapters.length) && (isMatchingAdapterEntry(_adapters[i], paramDOMAdapter)))
      {
        _adapters[i] = null;
      }
      else
      {
        j = 0;
        for (k = 0; k < _adapters.length; k++) {
          if (isMatchingAdapterEntry(_adapters[i], paramDOMAdapter))
          {
            _adapters[k] = null;
            j = 1;
            break;
          }
        }
      }
    }
  }
  
  private final class AxisIterator
    extends DTMAxisIteratorBase
  {
    private final int _axis;
    private final int _type;
    private DTMAxisIterator _source;
    private int _dtmId = -1;
    
    public AxisIterator(int paramInt1, int paramInt2)
    {
      _axis = paramInt1;
      _type = paramInt2;
    }
    
    public int next()
    {
      if (_source == null) {
        return -1;
      }
      return _source.next();
    }
    
    public void setRestartable(boolean paramBoolean)
    {
      if (_source != null) {
        _source.setRestartable(paramBoolean);
      }
    }
    
    public DTMAxisIterator setStartNode(int paramInt)
    {
      if (paramInt == -1) {
        return this;
      }
      int i = paramInt >>> 16;
      if ((_source == null) || (_dtmId != i)) {
        if (_type == -2) {
          _source = _adapters[i].getAxisIterator(_axis);
        } else if (_axis == 3) {
          _source = _adapters[i].getTypedChildren(_type);
        } else {
          _source = _adapters[i].getTypedAxisIterator(_axis, _type);
        }
      }
      _dtmId = i;
      _source.setStartNode(paramInt);
      return this;
    }
    
    public DTMAxisIterator reset()
    {
      if (_source != null) {
        _source.reset();
      }
      return this;
    }
    
    public int getLast()
    {
      if (_source != null) {
        return _source.getLast();
      }
      return -1;
    }
    
    public int getPosition()
    {
      if (_source != null) {
        return _source.getPosition();
      }
      return -1;
    }
    
    public boolean isReverse()
    {
      return Axis.isReverse(_axis);
    }
    
    public void setMark()
    {
      if (_source != null) {
        _source.setMark();
      }
    }
    
    public void gotoMark()
    {
      if (_source != null) {
        _source.gotoMark();
      }
    }
    
    public DTMAxisIterator cloneIterator()
    {
      AxisIterator localAxisIterator = new AxisIterator(MultiDOM.this, _axis, _type);
      if (_source != null) {
        _source = _source.cloneIterator();
      }
      _dtmId = _dtmId;
      return localAxisIterator;
    }
  }
  
  private final class NodeValueIterator
    extends DTMAxisIteratorBase
  {
    private DTMAxisIterator _source;
    private String _value;
    private boolean _op;
    private final boolean _isReverse;
    private int _returnType = 1;
    
    public NodeValueIterator(DTMAxisIterator paramDTMAxisIterator, int paramInt, String paramString, boolean paramBoolean)
    {
      _source = paramDTMAxisIterator;
      _returnType = paramInt;
      _value = paramString;
      _op = paramBoolean;
      _isReverse = paramDTMAxisIterator.isReverse();
    }
    
    public boolean isReverse()
    {
      return _isReverse;
    }
    
    public DTMAxisIterator cloneIterator()
    {
      try
      {
        NodeValueIterator localNodeValueIterator = (NodeValueIterator)super.clone();
        _source = _source.cloneIterator();
        localNodeValueIterator.setRestartable(false);
        return localNodeValueIterator.reset();
      }
      catch (CloneNotSupportedException localCloneNotSupportedException)
      {
        BasisLibrary.runTimeError("ITERATOR_CLONE_ERR", localCloneNotSupportedException.toString());
      }
      return null;
    }
    
    public void setRestartable(boolean paramBoolean)
    {
      _isRestartable = paramBoolean;
      _source.setRestartable(paramBoolean);
    }
    
    public DTMAxisIterator reset()
    {
      _source.reset();
      return resetPosition();
    }
    
    public int next()
    {
      int i;
      while ((i = _source.next()) != -1)
      {
        String str = getStringValueX(i);
        if (_value.equals(str) == _op)
        {
          if (_returnType == 0) {
            return returnNode(i);
          }
          return returnNode(getParent(i));
        }
      }
      return -1;
    }
    
    public DTMAxisIterator setStartNode(int paramInt)
    {
      if (_isRestartable)
      {
        _source.setStartNode(_startNode = paramInt);
        return resetPosition();
      }
      return this;
    }
    
    public void setMark()
    {
      _source.setMark();
    }
    
    public void gotoMark()
    {
      _source.gotoMark();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\dom\MultiDOM.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */