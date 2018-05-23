package com.sun.org.apache.xalan.internal.lib;

import com.sun.org.apache.xalan.internal.extensions.ExpressionContext;
import com.sun.org.apache.xml.internal.dtm.DTMIterator;
import com.sun.org.apache.xml.internal.dtm.ref.DTMNodeIterator;
import com.sun.org.apache.xpath.internal.NodeSet;
import com.sun.org.apache.xpath.internal.axes.RTFIterator;

public class ExsltCommon
{
  public ExsltCommon() {}
  
  public static String objectType(Object paramObject)
  {
    if ((paramObject instanceof String)) {
      return "string";
    }
    if ((paramObject instanceof Boolean)) {
      return "boolean";
    }
    if ((paramObject instanceof Number)) {
      return "number";
    }
    if ((paramObject instanceof DTMNodeIterator))
    {
      DTMIterator localDTMIterator = ((DTMNodeIterator)paramObject).getDTMIterator();
      if ((localDTMIterator instanceof RTFIterator)) {
        return "RTF";
      }
      return "node-set";
    }
    return "unknown";
  }
  
  public static NodeSet nodeSet(ExpressionContext paramExpressionContext, Object paramObject)
  {
    return Extensions.nodeset(paramExpressionContext, paramObject);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\lib\ExsltCommon.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */