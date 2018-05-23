package com.sun.org.apache.xpath.internal.functions;

import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.dtm.DTMIterator;
import com.sun.org.apache.xml.internal.utils.StringVector;
import com.sun.org.apache.xml.internal.utils.XMLString;
import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.NodeSetDTM;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XNodeSet;
import com.sun.org.apache.xpath.internal.objects.XObject;
import java.util.StringTokenizer;
import javax.xml.transform.TransformerException;

public class FuncId
  extends FunctionOneArg
{
  static final long serialVersionUID = 8930573966143567310L;
  
  public FuncId() {}
  
  private StringVector getNodesByID(XPathContext paramXPathContext, int paramInt, String paramString, StringVector paramStringVector, NodeSetDTM paramNodeSetDTM, boolean paramBoolean)
  {
    if (null != paramString)
    {
      String str = null;
      StringTokenizer localStringTokenizer = new StringTokenizer(paramString);
      boolean bool = localStringTokenizer.hasMoreTokens();
      DTM localDTM = paramXPathContext.getDTM(paramInt);
      while (bool)
      {
        str = localStringTokenizer.nextToken();
        bool = localStringTokenizer.hasMoreTokens();
        if ((null != paramStringVector) && (paramStringVector.contains(str)))
        {
          str = null;
        }
        else
        {
          int i = localDTM.getElementById(str);
          if (-1 != i) {
            paramNodeSetDTM.addNodeInDocOrder(i, paramXPathContext);
          }
          if ((null != str) && ((bool) || (paramBoolean)))
          {
            if (null == paramStringVector) {
              paramStringVector = new StringVector();
            }
            paramStringVector.addElement(str);
          }
        }
      }
    }
    return paramStringVector;
  }
  
  public XObject execute(XPathContext paramXPathContext)
    throws TransformerException
  {
    int i = paramXPathContext.getCurrentNode();
    DTM localDTM1 = paramXPathContext.getDTM(i);
    int j = localDTM1.getDocument();
    if (-1 == j) {
      error(paramXPathContext, "ER_CONTEXT_HAS_NO_OWNERDOC", null);
    }
    XObject localXObject = m_arg0.execute(paramXPathContext);
    int k = localXObject.getType();
    XNodeSet localXNodeSet = new XNodeSet(paramXPathContext.getDTMManager());
    NodeSetDTM localNodeSetDTM = localXNodeSet.mutableNodeset();
    Object localObject;
    if (4 == k)
    {
      localObject = localXObject.iter();
      StringVector localStringVector = null;
      int m = ((DTMIterator)localObject).nextNode();
      while (-1 != m)
      {
        DTM localDTM2 = ((DTMIterator)localObject).getDTM(m);
        String str = localDTM2.getStringValue(m).toString();
        m = ((DTMIterator)localObject).nextNode();
        localStringVector = getNodesByID(paramXPathContext, j, str, localStringVector, localNodeSetDTM, -1 != m);
      }
    }
    else
    {
      if (-1 == k) {
        return localXNodeSet;
      }
      localObject = localXObject.str();
      getNodesByID(paramXPathContext, j, (String)localObject, null, localNodeSetDTM, false);
    }
    return localXNodeSet;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\functions\FuncId.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */