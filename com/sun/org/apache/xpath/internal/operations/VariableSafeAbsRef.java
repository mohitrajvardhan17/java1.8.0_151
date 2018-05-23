package com.sun.org.apache.xpath.internal.operations;

import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.dtm.DTMManager;
import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XNodeSet;
import com.sun.org.apache.xpath.internal.objects.XObject;
import javax.xml.transform.TransformerException;

public class VariableSafeAbsRef
  extends Variable
{
  static final long serialVersionUID = -9174661990819967452L;
  
  public VariableSafeAbsRef() {}
  
  public XObject execute(XPathContext paramXPathContext, boolean paramBoolean)
    throws TransformerException
  {
    XNodeSet localXNodeSet = (XNodeSet)super.execute(paramXPathContext, paramBoolean);
    DTMManager localDTMManager = paramXPathContext.getDTMManager();
    int i = paramXPathContext.getContextNode();
    if (localDTMManager.getDTM(localXNodeSet.getRoot()).getDocument() != localDTMManager.getDTM(i).getDocument())
    {
      Expression localExpression = (Expression)localXNodeSet.getContainedIter();
      localXNodeSet = (XNodeSet)localExpression.asIterator(paramXPathContext, i);
    }
    return localXNodeSet;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\operations\VariableSafeAbsRef.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */