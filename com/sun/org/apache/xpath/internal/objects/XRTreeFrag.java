package com.sun.org.apache.xpath.internal.objects;

import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.dtm.DTMIterator;
import com.sun.org.apache.xml.internal.dtm.ref.DTMNodeIterator;
import com.sun.org.apache.xml.internal.dtm.ref.DTMNodeList;
import com.sun.org.apache.xml.internal.utils.FastStringBuffer;
import com.sun.org.apache.xml.internal.utils.WrappedRuntimeException;
import com.sun.org.apache.xml.internal.utils.XMLString;
import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.ExpressionNode;
import com.sun.org.apache.xpath.internal.NodeSetDTM;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.axes.RTFIterator;
import javax.xml.transform.TransformerException;
import org.w3c.dom.NodeList;

public class XRTreeFrag
  extends XObject
  implements Cloneable
{
  static final long serialVersionUID = -3201553822254911567L;
  private DTMXRTreeFrag m_DTMXRTreeFrag;
  private int m_dtmRoot = -1;
  protected boolean m_allowRelease = false;
  private XMLString m_xmlStr = null;
  
  public XRTreeFrag(int paramInt, XPathContext paramXPathContext, ExpressionNode paramExpressionNode)
  {
    super(null);
    exprSetParent(paramExpressionNode);
    initDTM(paramInt, paramXPathContext);
  }
  
  public XRTreeFrag(int paramInt, XPathContext paramXPathContext)
  {
    super(null);
    initDTM(paramInt, paramXPathContext);
  }
  
  private final void initDTM(int paramInt, XPathContext paramXPathContext)
  {
    m_dtmRoot = paramInt;
    DTM localDTM = paramXPathContext.getDTM(paramInt);
    if (localDTM != null) {
      m_DTMXRTreeFrag = paramXPathContext.getDTMXRTreeFrag(paramXPathContext.getDTMIdentity(localDTM));
    }
  }
  
  public Object object()
  {
    if (m_DTMXRTreeFrag.getXPathContext() != null) {
      return new DTMNodeIterator(new NodeSetDTM(m_dtmRoot, m_DTMXRTreeFrag.getXPathContext().getDTMManager()));
    }
    return super.object();
  }
  
  public XRTreeFrag(Expression paramExpression)
  {
    super(paramExpression);
  }
  
  public void allowDetachToRelease(boolean paramBoolean)
  {
    m_allowRelease = paramBoolean;
  }
  
  public void detach()
  {
    if (m_allowRelease)
    {
      m_DTMXRTreeFrag.destruct();
      setObject(null);
    }
  }
  
  public int getType()
  {
    return 5;
  }
  
  public String getTypeString()
  {
    return "#RTREEFRAG";
  }
  
  public double num()
    throws TransformerException
  {
    XMLString localXMLString = xstr();
    return localXMLString.toDouble();
  }
  
  public boolean bool()
  {
    return true;
  }
  
  public XMLString xstr()
  {
    if (null == m_xmlStr) {
      m_xmlStr = m_DTMXRTreeFrag.getDTM().getStringValue(m_dtmRoot);
    }
    return m_xmlStr;
  }
  
  public void appendToFsb(FastStringBuffer paramFastStringBuffer)
  {
    XString localXString = (XString)xstr();
    localXString.appendToFsb(paramFastStringBuffer);
  }
  
  public String str()
  {
    String str = m_DTMXRTreeFrag.getDTM().getStringValue(m_dtmRoot).toString();
    return null == str ? "" : str;
  }
  
  public int rtf()
  {
    return m_dtmRoot;
  }
  
  public DTMIterator asNodeIterator()
  {
    return new RTFIterator(m_dtmRoot, m_DTMXRTreeFrag.getXPathContext().getDTMManager());
  }
  
  public NodeList convertToNodeset()
  {
    if ((m_obj instanceof NodeList)) {
      return (NodeList)m_obj;
    }
    return new DTMNodeList(asNodeIterator());
  }
  
  public boolean equals(XObject paramXObject)
  {
    try
    {
      if (4 == paramXObject.getType()) {
        return paramXObject.equals(this);
      }
      if (1 == paramXObject.getType()) {
        return bool() == paramXObject.bool();
      }
      if (2 == paramXObject.getType()) {
        return num() == paramXObject.num();
      }
      if (4 == paramXObject.getType()) {
        return xstr().equals(paramXObject.xstr());
      }
      if (3 == paramXObject.getType()) {
        return xstr().equals(paramXObject.xstr());
      }
      if (5 == paramXObject.getType()) {
        return xstr().equals(paramXObject.xstr());
      }
      return super.equals(paramXObject);
    }
    catch (TransformerException localTransformerException)
    {
      throw new WrappedRuntimeException(localTransformerException);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\objects\XRTreeFrag.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */