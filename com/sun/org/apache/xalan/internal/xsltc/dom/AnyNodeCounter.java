package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.Translet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;

public abstract class AnyNodeCounter
  extends NodeCounter
{
  public AnyNodeCounter(Translet paramTranslet, DOM paramDOM, DTMAxisIterator paramDTMAxisIterator)
  {
    super(paramTranslet, paramDOM, paramDTMAxisIterator);
  }
  
  public AnyNodeCounter(Translet paramTranslet, DOM paramDOM, DTMAxisIterator paramDTMAxisIterator, boolean paramBoolean)
  {
    super(paramTranslet, paramDOM, paramDTMAxisIterator, paramBoolean);
  }
  
  public NodeCounter setStartNode(int paramInt)
  {
    _node = paramInt;
    _nodeType = _document.getExpandedTypeID(paramInt);
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
    int j = _node;
    int k = _document.getDocument();
    int i = 0;
    while ((j >= k) && (!matchesFrom(j)))
    {
      if (matchesCount(j)) {
        i++;
      }
      j--;
    }
    return formatNumbers(i);
  }
  
  public static NodeCounter getDefaultNodeCounter(Translet paramTranslet, DOM paramDOM, DTMAxisIterator paramDTMAxisIterator)
  {
    return new DefaultAnyNodeCounter(paramTranslet, paramDOM, paramDTMAxisIterator);
  }
  
  static class DefaultAnyNodeCounter
    extends AnyNodeCounter
  {
    public DefaultAnyNodeCounter(Translet paramTranslet, DOM paramDOM, DTMAxisIterator paramDTMAxisIterator)
    {
      super(paramDOM, paramDTMAxisIterator);
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
        int k = _document.getExpandedTypeID(_node);
        int m = _document.getDocument();
        while (j >= 0)
        {
          if (k == _document.getExpandedTypeID(j)) {
            i++;
          }
          if (j == m) {
            break;
          }
          j--;
        }
      }
      return formatNumbers(i);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\dom\AnyNodeCounter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */