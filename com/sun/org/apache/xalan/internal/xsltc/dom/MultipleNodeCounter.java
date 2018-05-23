package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.Translet;
import com.sun.org.apache.xalan.internal.xsltc.util.IntegerArray;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;

public abstract class MultipleNodeCounter
  extends NodeCounter
{
  private DTMAxisIterator _precSiblings = null;
  
  public MultipleNodeCounter(Translet paramTranslet, DOM paramDOM, DTMAxisIterator paramDTMAxisIterator)
  {
    super(paramTranslet, paramDOM, paramDTMAxisIterator);
  }
  
  public MultipleNodeCounter(Translet paramTranslet, DOM paramDOM, DTMAxisIterator paramDTMAxisIterator, boolean paramBoolean)
  {
    super(paramTranslet, paramDOM, paramDTMAxisIterator, paramBoolean);
  }
  
  public NodeCounter setStartNode(int paramInt)
  {
    _node = paramInt;
    _nodeType = _document.getExpandedTypeID(paramInt);
    _precSiblings = _document.getAxisIterator(12);
    return this;
  }
  
  public String getCounter()
  {
    if (_value != -2.147483648E9D)
    {
      if (_value == 0.0D) {
        return "0";
      }
      if (Double.isNaN(_value)) {
        return "NaN";
      }
      if ((_value < 0.0D) && (Double.isInfinite(_value))) {
        return "-Infinity";
      }
      if (Double.isInfinite(_value)) {
        return "Infinity";
      }
      return formatNumbers((int)_value);
    }
    IntegerArray localIntegerArray = new IntegerArray();
    int i = _node;
    localIntegerArray.add(i);
    while (((i = _document.getParent(i)) > -1) && (!matchesFrom(i))) {
      localIntegerArray.add(i);
    }
    int j = localIntegerArray.cardinality();
    int[] arrayOfInt = new int[j];
    for (int k = 0; k < j; k++) {
      arrayOfInt[k] = Integer.MIN_VALUE;
    }
    k = 0;
    int m = j - 1;
    while (m >= 0)
    {
      int n = arrayOfInt[k];
      int i1 = localIntegerArray.at(m);
      if (matchesCount(i1))
      {
        _precSiblings.setStartNode(i1);
        while ((i = _precSiblings.next()) != -1) {
          if (matchesCount(i)) {
            arrayOfInt[k] = (arrayOfInt[k] == Integer.MIN_VALUE ? 1 : arrayOfInt[k] + 1);
          }
        }
        arrayOfInt[k] = (arrayOfInt[k] == Integer.MIN_VALUE ? 1 : arrayOfInt[k] + 1);
      }
      m--;
      k++;
    }
    return formatNumbers(arrayOfInt);
  }
  
  public static NodeCounter getDefaultNodeCounter(Translet paramTranslet, DOM paramDOM, DTMAxisIterator paramDTMAxisIterator)
  {
    return new DefaultMultipleNodeCounter(paramTranslet, paramDOM, paramDTMAxisIterator);
  }
  
  static class DefaultMultipleNodeCounter
    extends MultipleNodeCounter
  {
    public DefaultMultipleNodeCounter(Translet paramTranslet, DOM paramDOM, DTMAxisIterator paramDTMAxisIterator)
    {
      super(paramDOM, paramDTMAxisIterator);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\dom\MultipleNodeCounter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */