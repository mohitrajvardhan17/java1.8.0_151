package com.sun.org.apache.xpath.internal.axes;

import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.patterns.NodeTest;
import java.util.Vector;
import javax.xml.transform.TransformerException;

public class UnionChildIterator
  extends ChildTestIterator
{
  static final long serialVersionUID = 3500298482193003495L;
  private PredicatedNodeTest[] m_nodeTests = null;
  
  public UnionChildIterator()
  {
    super(null);
  }
  
  public void addNodeTest(PredicatedNodeTest paramPredicatedNodeTest)
  {
    if (null == m_nodeTests)
    {
      m_nodeTests = new PredicatedNodeTest[1];
      m_nodeTests[0] = paramPredicatedNodeTest;
    }
    else
    {
      PredicatedNodeTest[] arrayOfPredicatedNodeTest = m_nodeTests;
      int i = m_nodeTests.length;
      m_nodeTests = new PredicatedNodeTest[i + 1];
      System.arraycopy(arrayOfPredicatedNodeTest, 0, m_nodeTests, 0, i);
      m_nodeTests[i] = paramPredicatedNodeTest;
    }
    paramPredicatedNodeTest.exprSetParent(this);
  }
  
  public void fixupVariables(Vector paramVector, int paramInt)
  {
    super.fixupVariables(paramVector, paramInt);
    if (m_nodeTests != null) {
      for (int i = 0; i < m_nodeTests.length; i++) {
        m_nodeTests[i].fixupVariables(paramVector, paramInt);
      }
    }
  }
  
  public short acceptNode(int paramInt)
  {
    XPathContext localXPathContext = getXPathContext();
    try
    {
      localXPathContext.pushCurrentNode(paramInt);
      for (int i = 0; i < m_nodeTests.length; i++)
      {
        PredicatedNodeTest localPredicatedNodeTest = m_nodeTests[i];
        XObject localXObject = localPredicatedNodeTest.execute(localXPathContext, paramInt);
        if (localXObject != NodeTest.SCORE_NONE)
        {
          short s;
          if (localPredicatedNodeTest.getPredicateCount() > 0)
          {
            if (localPredicatedNodeTest.executePredicates(paramInt, localXPathContext))
            {
              s = 1;
              return s;
            }
          }
          else
          {
            s = 1;
            return s;
          }
        }
      }
    }
    catch (TransformerException localTransformerException)
    {
      throw new RuntimeException(localTransformerException.getMessage());
    }
    finally
    {
      localXPathContext.popCurrentNode();
    }
    return 3;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\axes\UnionChildIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */