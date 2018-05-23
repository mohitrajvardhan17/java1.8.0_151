package com.sun.org.apache.xalan.internal.xsltc.dom;

import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;

public abstract interface CurrentNodeListFilter
{
  public abstract boolean test(int paramInt1, int paramInt2, int paramInt3, int paramInt4, AbstractTranslet paramAbstractTranslet, DTMAxisIterator paramDTMAxisIterator);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\xsltc\dom\CurrentNodeListFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */