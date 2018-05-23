package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.Translet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;

public abstract class SingleNodeCounter
  extends NodeCounter
{
  private static final int[] EmptyArray = new int[0];
  DTMAxisIterator _countSiblings = null;
  
  public SingleNodeCounter(Translet paramTranslet, DOM paramDOM, DTMAxisIterator paramDTMAxisIterator)
  {
    super(paramTranslet, paramDOM, paramDTMAxisIterator);
  }
  
  public SingleNodeCounter(Translet paramTranslet, DOM paramDOM, DTMAxisIterator paramDTMAxisIterator, boolean paramBoolean)
  {
    super(paramTranslet, paramDOM, paramDTMAxisIterator, paramBoolean);
  }
  
  public NodeCounter setStartNode(int paramInt)
  {
    _node = paramInt;
    _nodeType = _document.getExpandedTypeID(paramInt);
    _countSiblings = _document.getAxisIterator(12);
    return this;
  }
  
  public String getCounter()
  {
    int i;
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
      i = (int)_value;
    }
    else
    {
      int j = _node;
      i = 0;
      boolean bool = matchesCount(j);
      if (!bool) {
        while (((j = _document.getParent(j)) > -1) && (!matchesCount(j))) {
          if (matchesFrom(j)) {
            j = -1;
          }
        }
      }
      if (j != -1)
      {
        int k = j;
        if ((!bool) && (_hasFrom)) {
          while ((k = _document.getParent(k)) > -1) {
            if (matchesFrom(k)) {
              break;
            }
          }
        }
        if (k != -1)
        {
          _countSiblings.setStartNode(j);
          do
          {
            if (matchesCount(j)) {
              i++;
            }
          } while ((j = _countSiblings.next()) != -1);
          return formatNumbers(i);
        }
      }
      return formatNumbers(EmptyArray);
    }
    return formatNumbers(i);
  }
  
  public static NodeCounter getDefaultNodeCounter(Translet paramTranslet, DOM paramDOM, DTMAxisIterator paramDTMAxisIterator)
  {
    return new DefaultSingleNodeCounter(paramTranslet, paramDOM, paramDTMAxisIterator);
  }
  
  static class DefaultSingleNodeCounter
    extends SingleNodeCounter
  {
    public DefaultSingleNodeCounter(Translet paramTranslet, DOM paramDOM, DTMAxisIterator paramDTMAxisIterator)
    {
      super(paramDOM, paramDTMAxisIterator);
    }
    
    public NodeCounter setStartNode(int paramInt)
    {
      _node = paramInt;
      _nodeType = _document.getExpandedTypeID(paramInt);
      _countSiblings = _document.getTypedAxisIterator(12, _document.getExpandedTypeID(paramInt));
      return this;
    }
    
    public String getCounter()
    {
      int i;
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
        i = (int)_value;
      }
      else
      {
        i = 1;
        _countSiblings.setStartNode(_node);
        int j;
        while ((j = _countSiblings.next()) != -1) {
          i++;
        }
      }
      return formatNumbers(i);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\dom\SingleNodeCounter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */