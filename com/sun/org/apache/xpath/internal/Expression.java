package com.sun.org.apache.xpath.internal;

import com.sun.org.apache.xalan.internal.res.XSLMessages;
import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.dtm.DTMIterator;
import com.sun.org.apache.xml.internal.utils.XMLString;
import com.sun.org.apache.xpath.internal.objects.XNodeSet;
import com.sun.org.apache.xpath.internal.objects.XObject;
import java.io.Serializable;
import java.util.Vector;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public abstract class Expression
  implements Serializable, ExpressionNode, XPathVisitable
{
  static final long serialVersionUID = 565665869777906902L;
  private ExpressionNode m_parent;
  
  public Expression() {}
  
  public boolean canTraverseOutsideSubtree()
  {
    return false;
  }
  
  public XObject execute(XPathContext paramXPathContext, int paramInt)
    throws TransformerException
  {
    return execute(paramXPathContext);
  }
  
  public XObject execute(XPathContext paramXPathContext, int paramInt1, DTM paramDTM, int paramInt2)
    throws TransformerException
  {
    return execute(paramXPathContext);
  }
  
  public abstract XObject execute(XPathContext paramXPathContext)
    throws TransformerException;
  
  public XObject execute(XPathContext paramXPathContext, boolean paramBoolean)
    throws TransformerException
  {
    return execute(paramXPathContext);
  }
  
  public double num(XPathContext paramXPathContext)
    throws TransformerException
  {
    return execute(paramXPathContext).num();
  }
  
  public boolean bool(XPathContext paramXPathContext)
    throws TransformerException
  {
    return execute(paramXPathContext).bool();
  }
  
  public XMLString xstr(XPathContext paramXPathContext)
    throws TransformerException
  {
    return execute(paramXPathContext).xstr();
  }
  
  public boolean isNodesetExpr()
  {
    return false;
  }
  
  public int asNode(XPathContext paramXPathContext)
    throws TransformerException
  {
    DTMIterator localDTMIterator = execute(paramXPathContext).iter();
    return localDTMIterator.nextNode();
  }
  
  public DTMIterator asIterator(XPathContext paramXPathContext, int paramInt)
    throws TransformerException
  {
    try
    {
      paramXPathContext.pushCurrentNodeAndExpression(paramInt, paramInt);
      DTMIterator localDTMIterator = execute(paramXPathContext).iter();
      return localDTMIterator;
    }
    finally
    {
      paramXPathContext.popCurrentNodeAndExpression();
    }
  }
  
  public DTMIterator asIteratorRaw(XPathContext paramXPathContext, int paramInt)
    throws TransformerException
  {
    try
    {
      paramXPathContext.pushCurrentNodeAndExpression(paramInt, paramInt);
      XNodeSet localXNodeSet = (XNodeSet)execute(paramXPathContext);
      DTMIterator localDTMIterator = localXNodeSet.iterRaw();
      return localDTMIterator;
    }
    finally
    {
      paramXPathContext.popCurrentNodeAndExpression();
    }
  }
  
  public void executeCharsToContentHandler(XPathContext paramXPathContext, ContentHandler paramContentHandler)
    throws TransformerException, SAXException
  {
    XObject localXObject = execute(paramXPathContext);
    localXObject.dispatchCharactersEvents(paramContentHandler);
    localXObject.detach();
  }
  
  public boolean isStableNumber()
  {
    return false;
  }
  
  public abstract void fixupVariables(Vector paramVector, int paramInt);
  
  public abstract boolean deepEquals(Expression paramExpression);
  
  protected final boolean isSameClass(Expression paramExpression)
  {
    if (null == paramExpression) {
      return false;
    }
    return getClass() == paramExpression.getClass();
  }
  
  public void warn(XPathContext paramXPathContext, String paramString, Object[] paramArrayOfObject)
    throws TransformerException
  {
    String str = XSLMessages.createXPATHWarning(paramString, paramArrayOfObject);
    if (null != paramXPathContext)
    {
      ErrorListener localErrorListener = paramXPathContext.getErrorListener();
      localErrorListener.warning(new TransformerException(str, paramXPathContext.getSAXLocator()));
    }
  }
  
  public void assertion(boolean paramBoolean, String paramString)
  {
    if (!paramBoolean)
    {
      String str = XSLMessages.createXPATHMessage("ER_INCORRECT_PROGRAMMER_ASSERTION", new Object[] { paramString });
      throw new RuntimeException(str);
    }
  }
  
  public void error(XPathContext paramXPathContext, String paramString, Object[] paramArrayOfObject)
    throws TransformerException
  {
    String str = XSLMessages.createXPATHMessage(paramString, paramArrayOfObject);
    if (null != paramXPathContext)
    {
      ErrorListener localErrorListener = paramXPathContext.getErrorListener();
      TransformerException localTransformerException = new TransformerException(str, this);
      localErrorListener.fatalError(localTransformerException);
    }
  }
  
  public ExpressionNode getExpressionOwner()
  {
    for (ExpressionNode localExpressionNode = exprGetParent(); (null != localExpressionNode) && ((localExpressionNode instanceof Expression)); localExpressionNode = localExpressionNode.exprGetParent()) {}
    return localExpressionNode;
  }
  
  public void exprSetParent(ExpressionNode paramExpressionNode)
  {
    assertion(paramExpressionNode != this, "Can not parent an expression to itself!");
    m_parent = paramExpressionNode;
  }
  
  public ExpressionNode exprGetParent()
  {
    return m_parent;
  }
  
  public void exprAddChild(ExpressionNode paramExpressionNode, int paramInt)
  {
    assertion(false, "exprAddChild method not implemented!");
  }
  
  public ExpressionNode exprGetChild(int paramInt)
  {
    return null;
  }
  
  public int exprGetNumChildren()
  {
    return 0;
  }
  
  public String getPublicId()
  {
    if (null == m_parent) {
      return null;
    }
    return m_parent.getPublicId();
  }
  
  public String getSystemId()
  {
    if (null == m_parent) {
      return null;
    }
    return m_parent.getSystemId();
  }
  
  public int getLineNumber()
  {
    if (null == m_parent) {
      return 0;
    }
    return m_parent.getLineNumber();
  }
  
  public int getColumnNumber()
  {
    if (null == m_parent) {
      return 0;
    }
    return m_parent.getColumnNumber();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\Expression.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */