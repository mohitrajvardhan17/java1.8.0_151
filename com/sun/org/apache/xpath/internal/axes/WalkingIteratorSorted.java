package com.sun.org.apache.xpath.internal.axes;

import com.sun.org.apache.xml.internal.utils.PrefixResolver;
import com.sun.org.apache.xpath.internal.compiler.Compiler;
import java.util.Vector;
import javax.xml.transform.TransformerException;

public class WalkingIteratorSorted
  extends WalkingIterator
{
  static final long serialVersionUID = -4512512007542368213L;
  protected boolean m_inNaturalOrderStatic = false;
  
  public WalkingIteratorSorted(PrefixResolver paramPrefixResolver)
  {
    super(paramPrefixResolver);
  }
  
  WalkingIteratorSorted(Compiler paramCompiler, int paramInt1, int paramInt2, boolean paramBoolean)
    throws TransformerException
  {
    super(paramCompiler, paramInt1, paramInt2, paramBoolean);
  }
  
  public boolean isDocOrdered()
  {
    return m_inNaturalOrderStatic;
  }
  
  boolean canBeWalkedInNaturalDocOrderStatic()
  {
    if (null != m_firstWalker)
    {
      AxesWalker localAxesWalker = m_firstWalker;
      int i = -1;
      int j = 1;
      for (int k = 0; null != localAxesWalker; k++)
      {
        int m = localAxesWalker.getAxis();
        if (localAxesWalker.isDocOrdered())
        {
          int n = (m == 3) || (m == 13) || (m == 19) ? 1 : 0;
          if ((n != 0) || (m == -1))
          {
            localAxesWalker = localAxesWalker.getNextWalker();
          }
          else
          {
            int i1 = null == localAxesWalker.getNextWalker() ? 1 : 0;
            return (i1 != 0) && (((localAxesWalker.isDocOrdered()) && ((m == 4) || (m == 5) || (m == 17) || (m == 18))) || (m == 2));
          }
        }
        else
        {
          return false;
        }
      }
      return true;
    }
    return false;
  }
  
  public void fixupVariables(Vector paramVector, int paramInt)
  {
    super.fixupVariables(paramVector, paramInt);
    int i = getAnalysisBits();
    if (WalkerFactory.isNaturalDocOrder(i)) {
      m_inNaturalOrderStatic = true;
    } else {
      m_inNaturalOrderStatic = false;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\axes\WalkingIteratorSorted.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */