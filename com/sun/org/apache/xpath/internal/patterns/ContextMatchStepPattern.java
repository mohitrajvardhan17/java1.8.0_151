package com.sun.org.apache.xpath.internal.patterns;

import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.dtm.DTMAxisTraverser;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.axes.WalkerFactory;
import com.sun.org.apache.xpath.internal.objects.XObject;
import javax.xml.transform.TransformerException;

public class ContextMatchStepPattern
  extends StepPattern
{
  static final long serialVersionUID = -1888092779313211942L;
  
  public ContextMatchStepPattern(int paramInt1, int paramInt2)
  {
    super(-1, paramInt1, paramInt2);
  }
  
  public XObject execute(XPathContext paramXPathContext)
    throws TransformerException
  {
    if (paramXPathContext.getIteratorRoot() == paramXPathContext.getCurrentNode()) {
      return getStaticScore();
    }
    return SCORE_NONE;
  }
  
  public XObject executeRelativePathPattern(XPathContext paramXPathContext, StepPattern paramStepPattern)
    throws TransformerException
  {
    Object localObject1 = NodeTest.SCORE_NONE;
    int i = paramXPathContext.getCurrentNode();
    DTM localDTM = paramXPathContext.getDTM(i);
    if (null != localDTM)
    {
      int j = paramXPathContext.getCurrentNode();
      int k = m_axis;
      boolean bool = WalkerFactory.isDownwardAxisOfMany(k);
      int m = localDTM.getNodeType(paramXPathContext.getIteratorRoot()) == 2 ? 1 : 0;
      if ((11 == k) && (m != 0)) {
        k = 15;
      }
      DTMAxisTraverser localDTMAxisTraverser1 = localDTM.getAxisTraverser(k);
      for (int n = localDTMAxisTraverser1.first(i); -1 != n; n = localDTMAxisTraverser1.next(i, n)) {
        try
        {
          paramXPathContext.pushCurrentNode(n);
          localObject1 = execute(paramXPathContext);
          if (localObject1 != NodeTest.SCORE_NONE)
          {
            if (executePredicates(paramXPathContext, localDTM, i))
            {
              Object localObject2 = localObject1;
              return (XObject)localObject2;
            }
            localObject1 = NodeTest.SCORE_NONE;
          }
          if ((bool) && (m != 0) && (1 == localDTM.getNodeType(n)))
          {
            int i1 = 2;
            for (int i2 = 0; i2 < 2; i2++)
            {
              DTMAxisTraverser localDTMAxisTraverser2 = localDTM.getAxisTraverser(i1);
              for (int i3 = localDTMAxisTraverser2.first(n); -1 != i3; i3 = localDTMAxisTraverser2.next(n, i3)) {
                try
                {
                  paramXPathContext.pushCurrentNode(i3);
                  localObject1 = execute(paramXPathContext);
                  if ((localObject1 != NodeTest.SCORE_NONE) && (localObject1 != NodeTest.SCORE_NONE))
                  {
                    Object localObject3 = localObject1;
                    return (XObject)localObject3;
                  }
                }
                finally {}
              }
              i1 = 9;
            }
          }
        }
        finally
        {
          paramXPathContext.popCurrentNode();
        }
      }
    }
    return (XObject)localObject1;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\patterns\ContextMatchStepPattern.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */