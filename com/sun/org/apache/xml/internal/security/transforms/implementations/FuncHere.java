package com.sun.org.apache.xml.internal.security.transforms.implementations;

import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.security.utils.I18n;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import com.sun.org.apache.xpath.internal.NodeSetDTM;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.functions.Function;
import com.sun.org.apache.xpath.internal.objects.XNodeSet;
import com.sun.org.apache.xpath.internal.objects.XObject;
import java.util.Vector;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Node;

public class FuncHere
  extends Function
{
  private static final long serialVersionUID = 1L;
  
  public FuncHere() {}
  
  public XObject execute(XPathContext paramXPathContext)
    throws TransformerException
  {
    Node localNode = (Node)paramXPathContext.getOwnerObject();
    if (localNode == null) {
      return null;
    }
    int i = paramXPathContext.getDTMHandleFromNode(localNode);
    int j = paramXPathContext.getCurrentNode();
    DTM localDTM = paramXPathContext.getDTM(j);
    int k = localDTM.getDocument();
    if (-1 == k) {
      error(paramXPathContext, "ER_CONTEXT_HAS_NO_OWNERDOC", null);
    }
    Object localObject1 = XMLUtils.getOwnerDocument(localDTM.getNode(j));
    Object localObject2 = XMLUtils.getOwnerDocument(localNode);
    if (localObject1 != localObject2) {
      throw new TransformerException(I18n.translate("xpath.funcHere.documentsDiffer"));
    }
    localObject1 = new XNodeSet(paramXPathContext.getDTMManager());
    localObject2 = ((XNodeSet)localObject1).mutableNodeset();
    int m = -1;
    switch (localDTM.getNodeType(i))
    {
    case 2: 
    case 7: 
      m = i;
      ((NodeSetDTM)localObject2).addNode(m);
      break;
    case 3: 
      m = localDTM.getParent(i);
      ((NodeSetDTM)localObject2).addNode(m);
      break;
    }
    ((NodeSetDTM)localObject2).detach();
    return (XObject)localObject1;
  }
  
  public void fixupVariables(Vector paramVector, int paramInt) {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\transforms\implementations\FuncHere.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */