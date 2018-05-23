package com.sun.org.apache.xpath.internal;

import java.io.PrintStream;
import java.io.PrintWriter;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Node;

public class XPathException
  extends TransformerException
{
  static final long serialVersionUID = 4263549717619045963L;
  Object m_styleNode = null;
  protected Exception m_exception;
  
  public Object getStylesheetNode()
  {
    return m_styleNode;
  }
  
  public void setStylesheetNode(Object paramObject)
  {
    m_styleNode = paramObject;
  }
  
  public XPathException(String paramString, ExpressionNode paramExpressionNode)
  {
    super(paramString);
    setLocator(paramExpressionNode);
    setStylesheetNode(getStylesheetNode(paramExpressionNode));
  }
  
  public XPathException(String paramString)
  {
    super(paramString);
  }
  
  public Node getStylesheetNode(ExpressionNode paramExpressionNode)
  {
    ExpressionNode localExpressionNode = getExpressionOwner(paramExpressionNode);
    if ((null != localExpressionNode) && ((localExpressionNode instanceof Node))) {
      return (Node)localExpressionNode;
    }
    return null;
  }
  
  protected ExpressionNode getExpressionOwner(ExpressionNode paramExpressionNode)
  {
    for (ExpressionNode localExpressionNode = paramExpressionNode.exprGetParent(); (null != localExpressionNode) && ((localExpressionNode instanceof Expression)); localExpressionNode = localExpressionNode.exprGetParent()) {}
    return localExpressionNode;
  }
  
  public XPathException(String paramString, Object paramObject)
  {
    super(paramString);
    m_styleNode = paramObject;
  }
  
  public XPathException(String paramString, Node paramNode, Exception paramException)
  {
    super(paramString);
    m_styleNode = paramNode;
    m_exception = paramException;
  }
  
  public XPathException(String paramString, Exception paramException)
  {
    super(paramString);
    m_exception = paramException;
  }
  
  public void printStackTrace(PrintStream paramPrintStream)
  {
    if (paramPrintStream == null) {
      paramPrintStream = System.err;
    }
    try
    {
      super.printStackTrace(paramPrintStream);
    }
    catch (Exception localException) {}
    Object localObject1 = m_exception;
    for (int i = 0; (i < 10) && (null != localObject1); i++)
    {
      paramPrintStream.println("---------");
      ((Throwable)localObject1).printStackTrace(paramPrintStream);
      if ((localObject1 instanceof TransformerException))
      {
        TransformerException localTransformerException = (TransformerException)localObject1;
        Object localObject2 = localObject1;
        localObject1 = localTransformerException.getException();
        if (localObject2 == localObject1) {
          break;
        }
      }
      else
      {
        localObject1 = null;
      }
    }
  }
  
  public String getMessage()
  {
    Object localObject1 = super.getMessage();
    Object localObject2 = m_exception;
    while (null != localObject2)
    {
      String str = ((Throwable)localObject2).getMessage();
      if (null != str) {
        localObject1 = str;
      }
      if ((localObject2 instanceof TransformerException))
      {
        TransformerException localTransformerException = (TransformerException)localObject2;
        Object localObject3 = localObject2;
        localObject2 = localTransformerException.getException();
        if (localObject3 == localObject2) {
          break;
        }
      }
      else
      {
        localObject2 = null;
      }
    }
    return (String)(null != localObject1 ? localObject1 : "");
  }
  
  public void printStackTrace(PrintWriter paramPrintWriter)
  {
    if (paramPrintWriter == null) {
      paramPrintWriter = new PrintWriter(System.err);
    }
    try
    {
      super.printStackTrace(paramPrintWriter);
    }
    catch (Exception localException1) {}
    int i = 0;
    try
    {
      Throwable.class.getMethod("getCause", (Class[])null);
      i = 1;
    }
    catch (NoSuchMethodException localNoSuchMethodException) {}
    if (i == 0)
    {
      Object localObject1 = m_exception;
      for (int j = 0; (j < 10) && (null != localObject1); j++)
      {
        paramPrintWriter.println("---------");
        try
        {
          ((Throwable)localObject1).printStackTrace(paramPrintWriter);
        }
        catch (Exception localException2)
        {
          paramPrintWriter.println("Could not print stack trace...");
        }
        if ((localObject1 instanceof TransformerException))
        {
          TransformerException localTransformerException = (TransformerException)localObject1;
          Object localObject2 = localObject1;
          localObject1 = localTransformerException.getException();
          if (localObject2 == localObject1)
          {
            localObject1 = null;
            break;
          }
        }
        else
        {
          localObject1 = null;
        }
      }
    }
  }
  
  public Throwable getException()
  {
    return m_exception;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\XPathException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */