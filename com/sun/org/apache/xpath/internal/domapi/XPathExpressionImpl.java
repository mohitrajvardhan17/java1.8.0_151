package com.sun.org.apache.xpath.internal.domapi;

import com.sun.org.apache.xpath.internal.XPath;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.res.XPATHMessages;
import javax.xml.transform.TransformerException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.xpath.XPathException;
import org.w3c.dom.xpath.XPathExpression;

class XPathExpressionImpl
  implements XPathExpression
{
  private final XPath m_xpath;
  private final Document m_doc;
  
  XPathExpressionImpl(XPath paramXPath, Document paramDocument)
  {
    m_xpath = paramXPath;
    m_doc = paramDocument;
  }
  
  public Object evaluate(Node paramNode, short paramShort, Object paramObject)
    throws XPathException, DOMException
  {
    if (m_doc != null)
    {
      if ((paramNode != m_doc) && (!paramNode.getOwnerDocument().equals(m_doc)))
      {
        String str = XPATHMessages.createXPATHMessage("ER_WRONG_DOCUMENT", null);
        throw new DOMException((short)4, str);
      }
      int i = paramNode.getNodeType();
      if ((i != 9) && (i != 1) && (i != 2) && (i != 3) && (i != 4) && (i != 8) && (i != 7) && (i != 13))
      {
        localObject2 = XPATHMessages.createXPATHMessage("ER_WRONG_NODETYPE", null);
        throw new DOMException((short)9, (String)localObject2);
      }
    }
    if (!XPathResultImpl.isValidType(paramShort))
    {
      localObject1 = XPATHMessages.createXPATHMessage("ER_INVALID_XPATH_TYPE", new Object[] { new Integer(paramShort) });
      throw new XPathException((short)2, (String)localObject1);
    }
    Object localObject1 = new XPathContext();
    if (null != m_doc) {
      ((XPathContext)localObject1).getDTMHandleFromNode(m_doc);
    }
    Object localObject2 = null;
    try
    {
      localObject2 = m_xpath.execute((XPathContext)localObject1, paramNode, null);
    }
    catch (TransformerException localTransformerException)
    {
      throw new XPathException((short)1, localTransformerException.getMessageAndLocation());
    }
    return new XPathResultImpl(paramShort, (XObject)localObject2, paramNode, m_xpath);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\domapi\XPathExpressionImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */