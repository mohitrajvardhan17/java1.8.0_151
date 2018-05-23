package com.sun.org.apache.xpath.internal;

import com.sun.org.apache.xml.internal.utils.QName;
import com.sun.org.apache.xpath.internal.objects.XObject;
import java.util.Objects;

public class Arg
{
  private QName m_qname;
  private XObject m_val;
  private String m_expression;
  private boolean m_isFromWithParam;
  private boolean m_isVisible;
  
  public final QName getQName()
  {
    return m_qname;
  }
  
  public final void setQName(QName paramQName)
  {
    m_qname = paramQName;
  }
  
  public final XObject getVal()
  {
    return m_val;
  }
  
  public final void setVal(XObject paramXObject)
  {
    m_val = paramXObject;
  }
  
  public void detach()
  {
    if (null != m_val)
    {
      m_val.allowDetachToRelease(true);
      m_val.detach();
    }
  }
  
  public String getExpression()
  {
    return m_expression;
  }
  
  public void setExpression(String paramString)
  {
    m_expression = paramString;
  }
  
  public boolean isFromWithParam()
  {
    return m_isFromWithParam;
  }
  
  public boolean isVisible()
  {
    return m_isVisible;
  }
  
  public void setIsVisible(boolean paramBoolean)
  {
    m_isVisible = paramBoolean;
  }
  
  public Arg()
  {
    m_qname = new QName("");
    m_val = null;
    m_expression = null;
    m_isVisible = true;
    m_isFromWithParam = false;
  }
  
  public Arg(QName paramQName, String paramString, boolean paramBoolean)
  {
    m_qname = paramQName;
    m_val = null;
    m_expression = paramString;
    m_isFromWithParam = paramBoolean;
    m_isVisible = (!paramBoolean);
  }
  
  public Arg(QName paramQName, XObject paramXObject)
  {
    m_qname = paramQName;
    m_val = paramXObject;
    m_isVisible = true;
    m_isFromWithParam = false;
    m_expression = null;
  }
  
  public int hashCode()
  {
    return Objects.hashCode(m_qname);
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof QName)) {
      return m_qname.equals(paramObject);
    }
    return super.equals(paramObject);
  }
  
  public Arg(QName paramQName, XObject paramXObject, boolean paramBoolean)
  {
    m_qname = paramQName;
    m_val = paramXObject;
    m_isFromWithParam = paramBoolean;
    m_isVisible = (!paramBoolean);
    m_expression = null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\Arg.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */