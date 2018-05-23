package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.DOMEnhancedForDTM;
import com.sun.org.apache.xalan.internal.xsltc.StripFilter;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import java.util.Map;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public final class DOMAdapter
  implements DOM
{
  private DOMEnhancedForDTM _enhancedDOM;
  private DOM _dom;
  private String[] _namesArray;
  private String[] _urisArray;
  private int[] _typesArray;
  private String[] _namespaceArray;
  private short[] _mapping = null;
  private int[] _reverse = null;
  private short[] _NSmapping = null;
  private short[] _NSreverse = null;
  private int _multiDOMMask;
  
  public DOMAdapter(DOM paramDOM, String[] paramArrayOfString1, String[] paramArrayOfString2, int[] paramArrayOfInt, String[] paramArrayOfString3)
  {
    if ((paramDOM instanceof DOMEnhancedForDTM)) {
      _enhancedDOM = ((DOMEnhancedForDTM)paramDOM);
    }
    _dom = paramDOM;
    _namesArray = paramArrayOfString1;
    _urisArray = paramArrayOfString2;
    _typesArray = paramArrayOfInt;
    _namespaceArray = paramArrayOfString3;
  }
  
  public void setupMapping(String[] paramArrayOfString1, String[] paramArrayOfString2, int[] paramArrayOfInt, String[] paramArrayOfString3)
  {
    _namesArray = paramArrayOfString1;
    _urisArray = paramArrayOfString2;
    _typesArray = paramArrayOfInt;
    _namespaceArray = paramArrayOfString3;
  }
  
  public String[] getNamesArray()
  {
    return _namesArray;
  }
  
  public String[] getUrisArray()
  {
    return _urisArray;
  }
  
  public int[] getTypesArray()
  {
    return _typesArray;
  }
  
  public String[] getNamespaceArray()
  {
    return _namespaceArray;
  }
  
  public DOM getDOMImpl()
  {
    return _dom;
  }
  
  private short[] getMapping()
  {
    if ((_mapping == null) && (_enhancedDOM != null)) {
      _mapping = _enhancedDOM.getMapping(_namesArray, _urisArray, _typesArray);
    }
    return _mapping;
  }
  
  private int[] getReverse()
  {
    if ((_reverse == null) && (_enhancedDOM != null)) {
      _reverse = _enhancedDOM.getReverseMapping(_namesArray, _urisArray, _typesArray);
    }
    return _reverse;
  }
  
  private short[] getNSMapping()
  {
    if ((_NSmapping == null) && (_enhancedDOM != null)) {
      _NSmapping = _enhancedDOM.getNamespaceMapping(_namespaceArray);
    }
    return _NSmapping;
  }
  
  private short[] getNSReverse()
  {
    if ((_NSreverse == null) && (_enhancedDOM != null)) {
      _NSreverse = _enhancedDOM.getReverseNamespaceMapping(_namespaceArray);
    }
    return _NSreverse;
  }
  
  public DTMAxisIterator getIterator()
  {
    return _dom.getIterator();
  }
  
  public String getStringValue()
  {
    return _dom.getStringValue();
  }
  
  public DTMAxisIterator getChildren(int paramInt)
  {
    if (_enhancedDOM != null) {
      return _enhancedDOM.getChildren(paramInt);
    }
    DTMAxisIterator localDTMAxisIterator = _dom.getChildren(paramInt);
    return localDTMAxisIterator.setStartNode(paramInt);
  }
  
  public void setFilter(StripFilter paramStripFilter) {}
  
  public DTMAxisIterator getTypedChildren(int paramInt)
  {
    int[] arrayOfInt = getReverse();
    if (_enhancedDOM != null) {
      return _enhancedDOM.getTypedChildren(arrayOfInt[paramInt]);
    }
    return _dom.getTypedChildren(paramInt);
  }
  
  public DTMAxisIterator getNamespaceAxisIterator(int paramInt1, int paramInt2)
  {
    return _dom.getNamespaceAxisIterator(paramInt1, getNSReverse()[paramInt2]);
  }
  
  public DTMAxisIterator getAxisIterator(int paramInt)
  {
    if (_enhancedDOM != null) {
      return _enhancedDOM.getAxisIterator(paramInt);
    }
    return _dom.getAxisIterator(paramInt);
  }
  
  public DTMAxisIterator getTypedAxisIterator(int paramInt1, int paramInt2)
  {
    int[] arrayOfInt = getReverse();
    if (_enhancedDOM != null) {
      return _enhancedDOM.getTypedAxisIterator(paramInt1, arrayOfInt[paramInt2]);
    }
    return _dom.getTypedAxisIterator(paramInt1, paramInt2);
  }
  
  public int getMultiDOMMask()
  {
    return _multiDOMMask;
  }
  
  public void setMultiDOMMask(int paramInt)
  {
    _multiDOMMask = paramInt;
  }
  
  public DTMAxisIterator getNthDescendant(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    return _dom.getNthDescendant(getReverse()[paramInt1], paramInt2, paramBoolean);
  }
  
  public DTMAxisIterator getNodeValueIterator(DTMAxisIterator paramDTMAxisIterator, int paramInt, String paramString, boolean paramBoolean)
  {
    return _dom.getNodeValueIterator(paramDTMAxisIterator, paramInt, paramString, paramBoolean);
  }
  
  public DTMAxisIterator orderNodes(DTMAxisIterator paramDTMAxisIterator, int paramInt)
  {
    return _dom.orderNodes(paramDTMAxisIterator, paramInt);
  }
  
  public int getExpandedTypeID(int paramInt)
  {
    short[] arrayOfShort = getMapping();
    int i;
    if (_enhancedDOM != null) {
      i = arrayOfShort[_enhancedDOM.getExpandedTypeID2(paramInt)];
    } else if (null != arrayOfShort) {
      i = arrayOfShort[_dom.getExpandedTypeID(paramInt)];
    } else {
      i = _dom.getExpandedTypeID(paramInt);
    }
    return i;
  }
  
  public int getNamespaceType(int paramInt)
  {
    return getNSMapping()[_dom.getNSType(paramInt)];
  }
  
  public int getNSType(int paramInt)
  {
    return _dom.getNSType(paramInt);
  }
  
  public int getParent(int paramInt)
  {
    return _dom.getParent(paramInt);
  }
  
  public int getAttributeNode(int paramInt1, int paramInt2)
  {
    return _dom.getAttributeNode(getReverse()[paramInt1], paramInt2);
  }
  
  public String getNodeName(int paramInt)
  {
    if (paramInt == -1) {
      return "";
    }
    return _dom.getNodeName(paramInt);
  }
  
  public String getNodeNameX(int paramInt)
  {
    if (paramInt == -1) {
      return "";
    }
    return _dom.getNodeNameX(paramInt);
  }
  
  public String getNamespaceName(int paramInt)
  {
    if (paramInt == -1) {
      return "";
    }
    return _dom.getNamespaceName(paramInt);
  }
  
  public String getStringValueX(int paramInt)
  {
    if (_enhancedDOM != null) {
      return _enhancedDOM.getStringValueX(paramInt);
    }
    if (paramInt == -1) {
      return "";
    }
    return _dom.getStringValueX(paramInt);
  }
  
  public void copy(int paramInt, SerializationHandler paramSerializationHandler)
    throws TransletException
  {
    _dom.copy(paramInt, paramSerializationHandler);
  }
  
  public void copy(DTMAxisIterator paramDTMAxisIterator, SerializationHandler paramSerializationHandler)
    throws TransletException
  {
    _dom.copy(paramDTMAxisIterator, paramSerializationHandler);
  }
  
  public String shallowCopy(int paramInt, SerializationHandler paramSerializationHandler)
    throws TransletException
  {
    if (_enhancedDOM != null) {
      return _enhancedDOM.shallowCopy(paramInt, paramSerializationHandler);
    }
    return _dom.shallowCopy(paramInt, paramSerializationHandler);
  }
  
  public boolean lessThan(int paramInt1, int paramInt2)
  {
    return _dom.lessThan(paramInt1, paramInt2);
  }
  
  public void characters(int paramInt, SerializationHandler paramSerializationHandler)
    throws TransletException
  {
    if (_enhancedDOM != null) {
      _enhancedDOM.characters(paramInt, paramSerializationHandler);
    } else {
      _dom.characters(paramInt, paramSerializationHandler);
    }
  }
  
  public Node makeNode(int paramInt)
  {
    return _dom.makeNode(paramInt);
  }
  
  public Node makeNode(DTMAxisIterator paramDTMAxisIterator)
  {
    return _dom.makeNode(paramDTMAxisIterator);
  }
  
  public NodeList makeNodeList(int paramInt)
  {
    return _dom.makeNodeList(paramInt);
  }
  
  public NodeList makeNodeList(DTMAxisIterator paramDTMAxisIterator)
  {
    return _dom.makeNodeList(paramDTMAxisIterator);
  }
  
  public String getLanguage(int paramInt)
  {
    return _dom.getLanguage(paramInt);
  }
  
  public int getSize()
  {
    return _dom.getSize();
  }
  
  public void setDocumentURI(String paramString)
  {
    if (_enhancedDOM != null) {
      _enhancedDOM.setDocumentURI(paramString);
    }
  }
  
  public String getDocumentURI()
  {
    if (_enhancedDOM != null) {
      return _enhancedDOM.getDocumentURI();
    }
    return "";
  }
  
  public String getDocumentURI(int paramInt)
  {
    return _dom.getDocumentURI(paramInt);
  }
  
  public int getDocument()
  {
    return _dom.getDocument();
  }
  
  public boolean isElement(int paramInt)
  {
    return _dom.isElement(paramInt);
  }
  
  public boolean isAttribute(int paramInt)
  {
    return _dom.isAttribute(paramInt);
  }
  
  public int getNodeIdent(int paramInt)
  {
    return _dom.getNodeIdent(paramInt);
  }
  
  public int getNodeHandle(int paramInt)
  {
    return _dom.getNodeHandle(paramInt);
  }
  
  public DOM getResultTreeFrag(int paramInt1, int paramInt2)
  {
    if (_enhancedDOM != null) {
      return _enhancedDOM.getResultTreeFrag(paramInt1, paramInt2);
    }
    return _dom.getResultTreeFrag(paramInt1, paramInt2);
  }
  
  public DOM getResultTreeFrag(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    if (_enhancedDOM != null) {
      return _enhancedDOM.getResultTreeFrag(paramInt1, paramInt2, paramBoolean);
    }
    return _dom.getResultTreeFrag(paramInt1, paramInt2, paramBoolean);
  }
  
  public SerializationHandler getOutputDomBuilder()
  {
    return _dom.getOutputDomBuilder();
  }
  
  public String lookupNamespace(int paramInt, String paramString)
    throws TransletException
  {
    return _dom.lookupNamespace(paramInt, paramString);
  }
  
  public String getUnparsedEntityURI(String paramString)
  {
    return _dom.getUnparsedEntityURI(paramString);
  }
  
  public Map<String, Integer> getElementsWithIDs()
  {
    return _dom.getElementsWithIDs();
  }
  
  public void release()
  {
    _dom.release();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\dom\DOMAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */