package com.sun.org.apache.xpath.internal.axes;

import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xpath.internal.compiler.Compiler;
import javax.xml.transform.TransformerException;

public class AttributeIterator
  extends ChildTestIterator
{
  static final long serialVersionUID = -8417986700712229686L;
  
  AttributeIterator(Compiler paramCompiler, int paramInt1, int paramInt2)
    throws TransformerException
  {
    super(paramCompiler, paramInt1, paramInt2);
  }
  
  protected int getNextNode()
  {
    m_lastFetched = (-1 == m_lastFetched ? m_cdtm.getFirstAttribute(m_context) : m_cdtm.getNextAttribute(m_lastFetched));
    return m_lastFetched;
  }
  
  public int getAxis()
  {
    return 2;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\axes\AttributeIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */