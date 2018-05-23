package com.sun.org.apache.xpath.internal.operations;

import com.sun.org.apache.xalan.internal.res.XSLMessages;
import com.sun.org.apache.xml.internal.utils.PrefixResolver;
import com.sun.org.apache.xml.internal.utils.QName;
import com.sun.org.apache.xml.internal.utils.WrappedRuntimeException;
import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.ExpressionOwner;
import com.sun.org.apache.xpath.internal.VariableStack;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.XPathVisitor;
import com.sun.org.apache.xpath.internal.axes.PathComponent;
import com.sun.org.apache.xpath.internal.objects.XNodeSet;
import com.sun.org.apache.xpath.internal.objects.XObject;
import java.util.Vector;
import javax.xml.transform.TransformerException;

public class Variable
  extends Expression
  implements PathComponent
{
  static final long serialVersionUID = -4334975375609297049L;
  private boolean m_fixUpWasCalled = false;
  protected QName m_qname;
  protected int m_index;
  protected boolean m_isGlobal = false;
  static final String PSUEDOVARNAMESPACE = "http://xml.apache.org/xalan/psuedovar";
  
  public Variable() {}
  
  public void setIndex(int paramInt)
  {
    m_index = paramInt;
  }
  
  public int getIndex()
  {
    return m_index;
  }
  
  public void setIsGlobal(boolean paramBoolean)
  {
    m_isGlobal = paramBoolean;
  }
  
  public boolean getGlobal()
  {
    return m_isGlobal;
  }
  
  public void fixupVariables(Vector paramVector, int paramInt)
  {
    m_fixUpWasCalled = true;
    int i = paramVector.size();
    for (int j = paramVector.size() - 1; j >= 0; j--)
    {
      localObject = (QName)paramVector.elementAt(j);
      if (((QName)localObject).equals(m_qname))
      {
        if (j < paramInt)
        {
          m_isGlobal = true;
          m_index = j;
        }
        else
        {
          m_index = (j - paramInt);
        }
        return;
      }
    }
    String str = XSLMessages.createXPATHMessage("ER_COULD_NOT_FIND_VAR", new Object[] { m_qname.toString() });
    Object localObject = new TransformerException(str, this);
    throw new WrappedRuntimeException((Exception)localObject);
  }
  
  public void setQName(QName paramQName)
  {
    m_qname = paramQName;
  }
  
  public QName getQName()
  {
    return m_qname;
  }
  
  public XObject execute(XPathContext paramXPathContext)
    throws TransformerException
  {
    return execute(paramXPathContext, false);
  }
  
  public XObject execute(XPathContext paramXPathContext, boolean paramBoolean)
    throws TransformerException
  {
    PrefixResolver localPrefixResolver = paramXPathContext.getNamespaceContext();
    Object localObject;
    if (m_fixUpWasCalled)
    {
      if (m_isGlobal) {
        localObject = paramXPathContext.getVarStack().getGlobalVariable(paramXPathContext, m_index, paramBoolean);
      } else {
        localObject = paramXPathContext.getVarStack().getLocalVariable(paramXPathContext, m_index, paramBoolean);
      }
    }
    else {
      localObject = paramXPathContext.getVarStack().getVariableOrParam(paramXPathContext, m_qname);
    }
    if (null == localObject)
    {
      warn(paramXPathContext, "WG_ILLEGAL_VARIABLE_REFERENCE", new Object[] { m_qname.getLocalPart() });
      localObject = new XNodeSet(paramXPathContext.getDTMManager());
    }
    return (XObject)localObject;
  }
  
  public boolean isStableNumber()
  {
    return true;
  }
  
  public int getAnalysisBits()
  {
    return 67108864;
  }
  
  public void callVisitors(ExpressionOwner paramExpressionOwner, XPathVisitor paramXPathVisitor)
  {
    paramXPathVisitor.visitVariableRef(paramExpressionOwner, this);
  }
  
  public boolean deepEquals(Expression paramExpression)
  {
    if (!isSameClass(paramExpression)) {
      return false;
    }
    return m_qname.equals(m_qname);
  }
  
  public boolean isPsuedoVarRef()
  {
    String str = m_qname.getNamespaceURI();
    return (null != str) && (str.equals("http://xml.apache.org/xalan/psuedovar")) && (m_qname.getLocalName().startsWith("#"));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\operations\Variable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */