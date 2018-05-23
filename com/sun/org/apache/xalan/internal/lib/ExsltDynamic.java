package com.sun.org.apache.xalan.internal.lib;

import com.sun.org.apache.xalan.internal.extensions.ExpressionContext;
import com.sun.org.apache.xalan.internal.res.XSLMessages;
import com.sun.org.apache.xpath.internal.NodeSet;
import com.sun.org.apache.xpath.internal.NodeSetDTM;
import com.sun.org.apache.xpath.internal.XPath;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.XPathContext.XPathExpressionContext;
import com.sun.org.apache.xpath.internal.objects.XBoolean;
import com.sun.org.apache.xpath.internal.objects.XNodeSet;
import com.sun.org.apache.xpath.internal.objects.XNumber;
import com.sun.org.apache.xpath.internal.objects.XObject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXNotSupportedException;

public class ExsltDynamic
  extends ExsltBase
{
  public static final String EXSL_URI = "http://exslt.org/common";
  
  public ExsltDynamic() {}
  
  public static double max(ExpressionContext paramExpressionContext, NodeList paramNodeList, String paramString)
    throws SAXNotSupportedException
  {
    XPathContext localXPathContext = null;
    if ((paramExpressionContext instanceof XPathContext.XPathExpressionContext)) {
      localXPathContext = ((XPathContext.XPathExpressionContext)paramExpressionContext).getXPathContext();
    } else {
      throw new SAXNotSupportedException(XSLMessages.createMessage("ER_INVALID_CONTEXT_PASSED", new Object[] { paramExpressionContext }));
    }
    if ((paramString == null) || (paramString.length() == 0)) {
      return NaN.0D;
    }
    NodeSetDTM localNodeSetDTM = new NodeSetDTM(paramNodeList, localXPathContext);
    localXPathContext.pushContextNodeList(localNodeSetDTM);
    double d1 = -1.7976931348623157E308D;
    for (int i = 0; i < localNodeSetDTM.getLength(); i++)
    {
      int j = localNodeSetDTM.item(i);
      localXPathContext.pushCurrentNode(j);
      double d2 = 0.0D;
      try
      {
        XPath localXPath = new XPath(paramString, localXPathContext.getSAXLocator(), localXPathContext.getNamespaceContext(), 0);
        d2 = localXPath.execute(localXPathContext, j, localXPathContext.getNamespaceContext()).num();
      }
      catch (TransformerException localTransformerException)
      {
        localXPathContext.popCurrentNode();
        localXPathContext.popContextNodeList();
        return NaN.0D;
      }
      localXPathContext.popCurrentNode();
      if (d2 > d1) {
        d1 = d2;
      }
    }
    localXPathContext.popContextNodeList();
    return d1;
  }
  
  public static double min(ExpressionContext paramExpressionContext, NodeList paramNodeList, String paramString)
    throws SAXNotSupportedException
  {
    XPathContext localXPathContext = null;
    if ((paramExpressionContext instanceof XPathContext.XPathExpressionContext)) {
      localXPathContext = ((XPathContext.XPathExpressionContext)paramExpressionContext).getXPathContext();
    } else {
      throw new SAXNotSupportedException(XSLMessages.createMessage("ER_INVALID_CONTEXT_PASSED", new Object[] { paramExpressionContext }));
    }
    if ((paramString == null) || (paramString.length() == 0)) {
      return NaN.0D;
    }
    NodeSetDTM localNodeSetDTM = new NodeSetDTM(paramNodeList, localXPathContext);
    localXPathContext.pushContextNodeList(localNodeSetDTM);
    double d1 = Double.MAX_VALUE;
    for (int i = 0; i < paramNodeList.getLength(); i++)
    {
      int j = localNodeSetDTM.item(i);
      localXPathContext.pushCurrentNode(j);
      double d2 = 0.0D;
      try
      {
        XPath localXPath = new XPath(paramString, localXPathContext.getSAXLocator(), localXPathContext.getNamespaceContext(), 0);
        d2 = localXPath.execute(localXPathContext, j, localXPathContext.getNamespaceContext()).num();
      }
      catch (TransformerException localTransformerException)
      {
        localXPathContext.popCurrentNode();
        localXPathContext.popContextNodeList();
        return NaN.0D;
      }
      localXPathContext.popCurrentNode();
      if (d2 < d1) {
        d1 = d2;
      }
    }
    localXPathContext.popContextNodeList();
    return d1;
  }
  
  public static double sum(ExpressionContext paramExpressionContext, NodeList paramNodeList, String paramString)
    throws SAXNotSupportedException
  {
    XPathContext localXPathContext = null;
    if ((paramExpressionContext instanceof XPathContext.XPathExpressionContext)) {
      localXPathContext = ((XPathContext.XPathExpressionContext)paramExpressionContext).getXPathContext();
    } else {
      throw new SAXNotSupportedException(XSLMessages.createMessage("ER_INVALID_CONTEXT_PASSED", new Object[] { paramExpressionContext }));
    }
    if ((paramString == null) || (paramString.length() == 0)) {
      return NaN.0D;
    }
    NodeSetDTM localNodeSetDTM = new NodeSetDTM(paramNodeList, localXPathContext);
    localXPathContext.pushContextNodeList(localNodeSetDTM);
    double d1 = 0.0D;
    for (int i = 0; i < paramNodeList.getLength(); i++)
    {
      int j = localNodeSetDTM.item(i);
      localXPathContext.pushCurrentNode(j);
      double d2 = 0.0D;
      try
      {
        XPath localXPath = new XPath(paramString, localXPathContext.getSAXLocator(), localXPathContext.getNamespaceContext(), 0);
        d2 = localXPath.execute(localXPathContext, j, localXPathContext.getNamespaceContext()).num();
      }
      catch (TransformerException localTransformerException)
      {
        localXPathContext.popCurrentNode();
        localXPathContext.popContextNodeList();
        return NaN.0D;
      }
      localXPathContext.popCurrentNode();
      d1 += d2;
    }
    localXPathContext.popContextNodeList();
    return d1;
  }
  
  public static NodeList map(ExpressionContext paramExpressionContext, NodeList paramNodeList, String paramString)
    throws SAXNotSupportedException
  {
    XPathContext localXPathContext = null;
    Document localDocument = null;
    if ((paramExpressionContext instanceof XPathContext.XPathExpressionContext)) {
      localXPathContext = ((XPathContext.XPathExpressionContext)paramExpressionContext).getXPathContext();
    } else {
      throw new SAXNotSupportedException(XSLMessages.createMessage("ER_INVALID_CONTEXT_PASSED", new Object[] { paramExpressionContext }));
    }
    if ((paramString == null) || (paramString.length() == 0)) {
      return new NodeSet();
    }
    NodeSetDTM localNodeSetDTM = new NodeSetDTM(paramNodeList, localXPathContext);
    localXPathContext.pushContextNodeList(localNodeSetDTM);
    NodeSet localNodeSet = new NodeSet();
    localNodeSet.setShouldCacheNodes(true);
    for (int i = 0; i < paramNodeList.getLength(); i++)
    {
      int j = localNodeSetDTM.item(i);
      localXPathContext.pushCurrentNode(j);
      XObject localXObject = null;
      try
      {
        XPath localXPath = new XPath(paramString, localXPathContext.getSAXLocator(), localXPathContext.getNamespaceContext(), 0);
        localXObject = localXPath.execute(localXPathContext, j, localXPathContext.getNamespaceContext());
        Object localObject1;
        if ((localXObject instanceof XNodeSet))
        {
          localObject1 = null;
          localObject1 = ((XNodeSet)localXObject).nodelist();
          for (int k = 0; k < ((NodeList)localObject1).getLength(); k++)
          {
            Node localNode = ((NodeList)localObject1).item(k);
            if (!localNodeSet.contains(localNode)) {
              localNodeSet.addNode(localNode);
            }
          }
        }
        else
        {
          if (localDocument == null)
          {
            localObject1 = DocumentBuilderFactory.newInstance();
            ((DocumentBuilderFactory)localObject1).setNamespaceAware(true);
            localObject2 = ((DocumentBuilderFactory)localObject1).newDocumentBuilder();
            localDocument = ((DocumentBuilder)localObject2).newDocument();
          }
          localObject1 = null;
          if ((localXObject instanceof XNumber)) {
            localObject1 = localDocument.createElementNS("http://exslt.org/common", "exsl:number");
          } else if ((localXObject instanceof XBoolean)) {
            localObject1 = localDocument.createElementNS("http://exslt.org/common", "exsl:boolean");
          } else {
            localObject1 = localDocument.createElementNS("http://exslt.org/common", "exsl:string");
          }
          Object localObject2 = localDocument.createTextNode(localXObject.str());
          ((Element)localObject1).appendChild((Node)localObject2);
          localNodeSet.addNode((Node)localObject1);
        }
      }
      catch (Exception localException)
      {
        localXPathContext.popCurrentNode();
        localXPathContext.popContextNodeList();
        return new NodeSet();
      }
      localXPathContext.popCurrentNode();
    }
    localXPathContext.popContextNodeList();
    return localNodeSet;
  }
  
  public static XObject evaluate(ExpressionContext paramExpressionContext, String paramString)
    throws SAXNotSupportedException
  {
    if ((paramExpressionContext instanceof XPathContext.XPathExpressionContext))
    {
      XPathContext localXPathContext = null;
      try
      {
        localXPathContext = ((XPathContext.XPathExpressionContext)paramExpressionContext).getXPathContext();
        XPath localXPath = new XPath(paramString, localXPathContext.getSAXLocator(), localXPathContext.getNamespaceContext(), 0);
        return localXPath.execute(localXPathContext, paramExpressionContext.getContextNode(), localXPathContext.getNamespaceContext());
      }
      catch (TransformerException localTransformerException)
      {
        return new XNodeSet(localXPathContext.getDTMManager());
      }
    }
    throw new SAXNotSupportedException(XSLMessages.createMessage("ER_INVALID_CONTEXT_PASSED", new Object[] { paramExpressionContext }));
  }
  
  public static NodeList closure(ExpressionContext paramExpressionContext, NodeList paramNodeList, String paramString)
    throws SAXNotSupportedException
  {
    XPathContext localXPathContext = null;
    if ((paramExpressionContext instanceof XPathContext.XPathExpressionContext)) {
      localXPathContext = ((XPathContext.XPathExpressionContext)paramExpressionContext).getXPathContext();
    } else {
      throw new SAXNotSupportedException(XSLMessages.createMessage("ER_INVALID_CONTEXT_PASSED", new Object[] { paramExpressionContext }));
    }
    if ((paramString == null) || (paramString.length() == 0)) {
      return new NodeSet();
    }
    NodeSet localNodeSet1 = new NodeSet();
    localNodeSet1.setShouldCacheNodes(true);
    Object localObject = paramNodeList;
    do
    {
      NodeSet localNodeSet2 = new NodeSet();
      NodeSetDTM localNodeSetDTM = new NodeSetDTM((NodeList)localObject, localXPathContext);
      localXPathContext.pushContextNodeList(localNodeSetDTM);
      for (int i = 0; i < ((NodeList)localObject).getLength(); i++)
      {
        int j = localNodeSetDTM.item(i);
        localXPathContext.pushCurrentNode(j);
        XObject localXObject = null;
        try
        {
          XPath localXPath = new XPath(paramString, localXPathContext.getSAXLocator(), localXPathContext.getNamespaceContext(), 0);
          localXObject = localXPath.execute(localXPathContext, j, localXPathContext.getNamespaceContext());
          if ((localXObject instanceof XNodeSet))
          {
            NodeList localNodeList = null;
            localNodeList = ((XNodeSet)localXObject).nodelist();
            for (int k = 0; k < localNodeList.getLength(); k++)
            {
              Node localNode2 = localNodeList.item(k);
              if (!localNodeSet2.contains(localNode2)) {
                localNodeSet2.addNode(localNode2);
              }
            }
          }
          else
          {
            localXPathContext.popCurrentNode();
            localXPathContext.popContextNodeList();
            return new NodeSet();
          }
        }
        catch (TransformerException localTransformerException)
        {
          localXPathContext.popCurrentNode();
          localXPathContext.popContextNodeList();
          return new NodeSet();
        }
        localXPathContext.popCurrentNode();
      }
      localXPathContext.popContextNodeList();
      localObject = localNodeSet2;
      for (i = 0; i < ((NodeList)localObject).getLength(); i++)
      {
        Node localNode1 = ((NodeList)localObject).item(i);
        if (!localNodeSet1.contains(localNode1)) {
          localNodeSet1.addNode(localNode1);
        }
      }
    } while (((NodeList)localObject).getLength() > 0);
    return localNodeSet1;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xalan\internal\lib\ExsltDynamic.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */