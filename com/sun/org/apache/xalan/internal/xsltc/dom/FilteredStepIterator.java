package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;

public final class FilteredStepIterator
  extends StepIterator
{
  private Filter _filter;
  
  public FilteredStepIterator(DTMAxisIterator paramDTMAxisIterator1, DTMAxisIterator paramDTMAxisIterator2, Filter paramFilter)
  {
    super(paramDTMAxisIterator1, paramDTMAxisIterator2);
    _filter = paramFilter;
  }
  
  public int next()
  {
    int i;
    while ((i = super.next()) != -1) {
      if (_filter.test(i)) {
        return returnNode(i);
      }
    }
    return i;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\dom\FilteredStepIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */