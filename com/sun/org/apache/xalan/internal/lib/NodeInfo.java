package com.sun.org.apache.xalan.internal.lib;

import com.sun.org.apache.xalan.internal.extensions.ExpressionContext;
import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.dtm.ref.DTMNodeProxy;
import javax.xml.transform.SourceLocator;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class NodeInfo
{
  public NodeInfo() {}
  
  public static String systemId(ExpressionContext paramExpressionContext)
  {
    Node localNode = paramExpressionContext.getContextNode();
    int i = ((DTMNodeProxy)localNode).getDTMNodeNumber();
    SourceLocator localSourceLocator = ((DTMNodeProxy)localNode).getDTM().getSourceLocatorFor(i);
    if (localSourceLocator != null) {
      return localSourceLocator.getSystemId();
    }
    return null;
  }
  
  public static String systemId(NodeList paramNodeList)
  {
    if ((paramNodeList == null) || (paramNodeList.getLength() == 0)) {
      return null;
    }
    Node localNode = paramNodeList.item(0);
    int i = ((DTMNodeProxy)localNode).getDTMNodeNumber();
    SourceLocator localSourceLocator = ((DTMNodeProxy)localNode).getDTM().getSourceLocatorFor(i);
    if (localSourceLocator != null) {
      return localSourceLocator.getSystemId();
    }
    return null;
  }
  
  public static String publicId(ExpressionContext paramExpressionContext)
  {
    Node localNode = paramExpressionContext.getContextNode();
    int i = ((DTMNodeProxy)localNode).getDTMNodeNumber();
    SourceLocator localSourceLocator = ((DTMNodeProxy)localNode).getDTM().getSourceLocatorFor(i);
    if (localSourceLocator != null) {
      return localSourceLocator.getPublicId();
    }
    return null;
  }
  
  public static String publicId(NodeList paramNodeList)
  {
    if ((paramNodeList == null) || (paramNodeList.getLength() == 0)) {
      return null;
    }
    Node localNode = paramNodeList.item(0);
    int i = ((DTMNodeProxy)localNode).getDTMNodeNumber();
    SourceLocator localSourceLocator = ((DTMNodeProxy)localNode).getDTM().getSourceLocatorFor(i);
    if (localSourceLocator != null) {
      return localSourceLocator.getPublicId();
    }
    return null;
  }
  
  public static int lineNumber(ExpressionContext paramExpressionContext)
  {
    Node localNode = paramExpressionContext.getContextNode();
    int i = ((DTMNodeProxy)localNode).getDTMNodeNumber();
    SourceLocator localSourceLocator = ((DTMNodeProxy)localNode).getDTM().getSourceLocatorFor(i);
    if (localSourceLocator != null) {
      return localSourceLocator.getLineNumber();
    }
    return -1;
  }
  
  public static int lineNumber(NodeList paramNodeList)
  {
    if ((paramNodeList == null) || (paramNodeList.getLength() == 0)) {
      return -1;
    }
    Node localNode = paramNodeList.item(0);
    int i = ((DTMNodeProxy)localNode).getDTMNodeNumber();
    SourceLocator localSourceLocator = ((DTMNodeProxy)localNode).getDTM().getSourceLocatorFor(i);
    if (localSourceLocator != null) {
      return localSourceLocator.getLineNumber();
    }
    return -1;
  }
  
  public static int columnNumber(ExpressionContext paramExpressionContext)
  {
    Node localNode = paramExpressionContext.getContextNode();
    int i = ((DTMNodeProxy)localNode).getDTMNodeNumber();
    SourceLocator localSourceLocator = ((DTMNodeProxy)localNode).getDTM().getSourceLocatorFor(i);
    if (localSourceLocator != null) {
      return localSourceLocator.getColumnNumber();
    }
    return -1;
  }
  
  public static int columnNumber(NodeList paramNodeList)
  {
    if ((paramNodeList == null) || (paramNodeList.getLength() == 0)) {
      return -1;
    }
    Node localNode = paramNodeList.item(0);
    int i = ((DTMNodeProxy)localNode).getDTMNodeNumber();
    SourceLocator localSourceLocator = ((DTMNodeProxy)localNode).getDTM().getSourceLocatorFor(i);
    if (localSourceLocator != null) {
      return localSourceLocator.getColumnNumber();
    }
    return -1;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\lib\NodeInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */