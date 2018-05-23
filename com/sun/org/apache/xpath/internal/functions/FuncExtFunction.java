package com.sun.org.apache.xpath.internal.functions;

import com.sun.org.apache.xalan.internal.res.XSLMessages;
import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.ExpressionNode;
import com.sun.org.apache.xpath.internal.ExpressionOwner;
import com.sun.org.apache.xpath.internal.ExtensionsProvider;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.XPathVisitor;
import com.sun.org.apache.xpath.internal.objects.XNull;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.org.apache.xpath.internal.res.XPATHMessages;
import java.util.Vector;
import javax.xml.transform.TransformerException;

public class FuncExtFunction
  extends Function
{
  static final long serialVersionUID = 5196115554693708718L;
  String m_namespace;
  String m_extensionName;
  Object m_methodKey;
  Vector m_argVec = new Vector();
  
  public void fixupVariables(Vector paramVector, int paramInt)
  {
    if (null != m_argVec)
    {
      int i = m_argVec.size();
      for (int j = 0; j < i; j++)
      {
        Expression localExpression = (Expression)m_argVec.elementAt(j);
        localExpression.fixupVariables(paramVector, paramInt);
      }
    }
  }
  
  public String getNamespace()
  {
    return m_namespace;
  }
  
  public String getFunctionName()
  {
    return m_extensionName;
  }
  
  public Object getMethodKey()
  {
    return m_methodKey;
  }
  
  public Expression getArg(int paramInt)
  {
    if ((paramInt >= 0) && (paramInt < m_argVec.size())) {
      return (Expression)m_argVec.elementAt(paramInt);
    }
    return null;
  }
  
  public int getArgCount()
  {
    return m_argVec.size();
  }
  
  public FuncExtFunction(String paramString1, String paramString2, Object paramObject)
  {
    m_namespace = paramString1;
    m_extensionName = paramString2;
    m_methodKey = paramObject;
  }
  
  public XObject execute(XPathContext paramXPathContext)
    throws TransformerException
  {
    if (paramXPathContext.isSecureProcessing()) {
      throw new TransformerException(XPATHMessages.createXPATHMessage("ER_EXTENSION_FUNCTION_CANNOT_BE_INVOKED", new Object[] { toString() }));
    }
    Vector localVector = new Vector();
    int i = m_argVec.size();
    for (int j = 0; j < i; j++)
    {
      localObject2 = (Expression)m_argVec.elementAt(j);
      XObject localXObject = ((Expression)localObject2).execute(paramXPathContext);
      localXObject.allowDetachToRelease(false);
      localVector.addElement(localXObject);
    }
    ExtensionsProvider localExtensionsProvider = (ExtensionsProvider)paramXPathContext.getOwnerObject();
    Object localObject2 = localExtensionsProvider.extFunction(this, localVector);
    Object localObject1;
    if (null != localObject2) {
      localObject1 = XObject.create(localObject2, paramXPathContext);
    } else {
      localObject1 = new XNull();
    }
    return (XObject)localObject1;
  }
  
  public void setArg(Expression paramExpression, int paramInt)
    throws WrongNumberArgsException
  {
    m_argVec.addElement(paramExpression);
    paramExpression.exprSetParent(this);
  }
  
  public void checkNumberArgs(int paramInt)
    throws WrongNumberArgsException
  {}
  
  public void callArgVisitors(XPathVisitor paramXPathVisitor)
  {
    for (int i = 0; i < m_argVec.size(); i++)
    {
      Expression localExpression = (Expression)m_argVec.elementAt(i);
      localExpression.callVisitors(new ArgExtOwner(localExpression), paramXPathVisitor);
    }
  }
  
  public void exprSetParent(ExpressionNode paramExpressionNode)
  {
    super.exprSetParent(paramExpressionNode);
    int i = m_argVec.size();
    for (int j = 0; j < i; j++)
    {
      Expression localExpression = (Expression)m_argVec.elementAt(j);
      localExpression.exprSetParent(paramExpressionNode);
    }
  }
  
  protected void reportWrongNumberArgs()
    throws WrongNumberArgsException
  {
    String str = XSLMessages.createXPATHMessage("ER_INCORRECT_PROGRAMMER_ASSERTION", new Object[] { "Programmer's assertion:  the method FunctionMultiArgs.reportWrongNumberArgs() should never be called." });
    throw new RuntimeException(str);
  }
  
  public String toString()
  {
    if ((m_namespace != null) && (m_namespace.length() > 0)) {
      return "{" + m_namespace + "}" + m_extensionName;
    }
    return m_extensionName;
  }
  
  class ArgExtOwner
    implements ExpressionOwner
  {
    Expression m_exp;
    
    ArgExtOwner(Expression paramExpression)
    {
      m_exp = paramExpression;
    }
    
    public Expression getExpression()
    {
      return m_exp;
    }
    
    public void setExpression(Expression paramExpression)
    {
      paramExpression.exprSetParent(FuncExtFunction.this);
      m_exp = paramExpression;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\functions\FuncExtFunction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */