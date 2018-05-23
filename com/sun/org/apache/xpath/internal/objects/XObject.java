package com.sun.org.apache.xpath.internal.objects;

import com.sun.org.apache.xalan.internal.res.XSLMessages;
import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xml.internal.dtm.DTMIterator;
import com.sun.org.apache.xml.internal.utils.FastStringBuffer;
import com.sun.org.apache.xml.internal.utils.XMLString;
import com.sun.org.apache.xml.internal.utils.XMLStringFactory;
import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.ExpressionOwner;
import com.sun.org.apache.xpath.internal.NodeSetDTM;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.XPathException;
import com.sun.org.apache.xpath.internal.XPathVisitor;
import java.io.Serializable;
import java.util.Vector;
import javax.xml.transform.TransformerException;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.NodeIterator;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class XObject
  extends Expression
  implements Serializable, Cloneable
{
  static final long serialVersionUID = -821887098985662951L;
  protected Object m_obj;
  public static final int CLASS_NULL = -1;
  public static final int CLASS_UNKNOWN = 0;
  public static final int CLASS_BOOLEAN = 1;
  public static final int CLASS_NUMBER = 2;
  public static final int CLASS_STRING = 3;
  public static final int CLASS_NODESET = 4;
  public static final int CLASS_RTREEFRAG = 5;
  public static final int CLASS_UNRESOLVEDVARIABLE = 600;
  
  public XObject() {}
  
  public XObject(Object paramObject)
  {
    setObject(paramObject);
  }
  
  protected void setObject(Object paramObject)
  {
    m_obj = paramObject;
  }
  
  public XObject execute(XPathContext paramXPathContext)
    throws TransformerException
  {
    return this;
  }
  
  public void allowDetachToRelease(boolean paramBoolean) {}
  
  public void detach() {}
  
  public void destruct()
  {
    if (null != m_obj)
    {
      allowDetachToRelease(true);
      detach();
      setObject(null);
    }
  }
  
  public void reset() {}
  
  public void dispatchCharactersEvents(ContentHandler paramContentHandler)
    throws SAXException
  {
    xstr().dispatchCharactersEvents(paramContentHandler);
  }
  
  public static XObject create(Object paramObject)
  {
    return XObjectFactory.create(paramObject);
  }
  
  public static XObject create(Object paramObject, XPathContext paramXPathContext)
  {
    return XObjectFactory.create(paramObject, paramXPathContext);
  }
  
  public int getType()
  {
    return 0;
  }
  
  public String getTypeString()
  {
    return "#UNKNOWN (" + object().getClass().getName() + ")";
  }
  
  public double num()
    throws TransformerException
  {
    error("ER_CANT_CONVERT_TO_NUMBER", new Object[] { getTypeString() });
    return 0.0D;
  }
  
  public double numWithSideEffects()
    throws TransformerException
  {
    return num();
  }
  
  public boolean bool()
    throws TransformerException
  {
    error("ER_CANT_CONVERT_TO_NUMBER", new Object[] { getTypeString() });
    return false;
  }
  
  public boolean boolWithSideEffects()
    throws TransformerException
  {
    return bool();
  }
  
  public XMLString xstr()
  {
    return XMLStringFactoryImpl.getFactory().newstr(str());
  }
  
  public String str()
  {
    return m_obj != null ? m_obj.toString() : "";
  }
  
  public String toString()
  {
    return str();
  }
  
  public int rtf(XPathContext paramXPathContext)
  {
    int i = rtf();
    if (-1 == i)
    {
      DTM localDTM = paramXPathContext.createDocumentFragment();
      localDTM.appendTextChild(str());
      i = localDTM.getDocument();
    }
    return i;
  }
  
  public DocumentFragment rtree(XPathContext paramXPathContext)
  {
    DocumentFragment localDocumentFragment = null;
    int i = rtf();
    DTM localDTM;
    if (-1 == i)
    {
      localDTM = paramXPathContext.createDocumentFragment();
      localDTM.appendTextChild(str());
      localDocumentFragment = (DocumentFragment)localDTM.getNode(localDTM.getDocument());
    }
    else
    {
      localDTM = paramXPathContext.getDTM(i);
      localDocumentFragment = (DocumentFragment)localDTM.getNode(localDTM.getDocument());
    }
    return localDocumentFragment;
  }
  
  public DocumentFragment rtree()
  {
    return null;
  }
  
  public int rtf()
  {
    return -1;
  }
  
  public Object object()
  {
    return m_obj;
  }
  
  public DTMIterator iter()
    throws TransformerException
  {
    error("ER_CANT_CONVERT_TO_NODELIST", new Object[] { getTypeString() });
    return null;
  }
  
  public XObject getFresh()
  {
    return this;
  }
  
  public NodeIterator nodeset()
    throws TransformerException
  {
    error("ER_CANT_CONVERT_TO_NODELIST", new Object[] { getTypeString() });
    return null;
  }
  
  public NodeList nodelist()
    throws TransformerException
  {
    error("ER_CANT_CONVERT_TO_NODELIST", new Object[] { getTypeString() });
    return null;
  }
  
  public NodeSetDTM mutableNodeset()
    throws TransformerException
  {
    error("ER_CANT_CONVERT_TO_MUTABLENODELIST", new Object[] { getTypeString() });
    return (NodeSetDTM)m_obj;
  }
  
  public Object castToType(int paramInt, XPathContext paramXPathContext)
    throws TransformerException
  {
    Object localObject;
    switch (paramInt)
    {
    case 3: 
      localObject = str();
      break;
    case 2: 
      localObject = new Double(num());
      break;
    case 4: 
      localObject = iter();
      break;
    case 1: 
      localObject = new Boolean(bool());
      break;
    case 0: 
      localObject = m_obj;
      break;
    default: 
      error("ER_CANT_CONVERT_TO_TYPE", new Object[] { getTypeString(), Integer.toString(paramInt) });
      localObject = null;
    }
    return localObject;
  }
  
  public boolean lessThan(XObject paramXObject)
    throws TransformerException
  {
    if (paramXObject.getType() == 4) {
      return paramXObject.greaterThan(this);
    }
    return num() < paramXObject.num();
  }
  
  public boolean lessThanOrEqual(XObject paramXObject)
    throws TransformerException
  {
    if (paramXObject.getType() == 4) {
      return paramXObject.greaterThanOrEqual(this);
    }
    return num() <= paramXObject.num();
  }
  
  public boolean greaterThan(XObject paramXObject)
    throws TransformerException
  {
    if (paramXObject.getType() == 4) {
      return paramXObject.lessThan(this);
    }
    return num() > paramXObject.num();
  }
  
  public boolean greaterThanOrEqual(XObject paramXObject)
    throws TransformerException
  {
    if (paramXObject.getType() == 4) {
      return paramXObject.lessThanOrEqual(this);
    }
    return num() >= paramXObject.num();
  }
  
  public boolean equals(XObject paramXObject)
  {
    if (paramXObject.getType() == 4) {
      return paramXObject.equals(this);
    }
    if (null != m_obj) {
      return m_obj.equals(m_obj);
    }
    return m_obj == null;
  }
  
  public boolean notEquals(XObject paramXObject)
    throws TransformerException
  {
    if (paramXObject.getType() == 4) {
      return paramXObject.notEquals(this);
    }
    return !equals(paramXObject);
  }
  
  protected void error(String paramString)
    throws TransformerException
  {
    error(paramString, null);
  }
  
  protected void error(String paramString, Object[] paramArrayOfObject)
    throws TransformerException
  {
    String str = XSLMessages.createXPATHMessage(paramString, paramArrayOfObject);
    throw new XPathException(str, this);
  }
  
  public void fixupVariables(Vector paramVector, int paramInt) {}
  
  public void appendToFsb(FastStringBuffer paramFastStringBuffer)
  {
    paramFastStringBuffer.append(str());
  }
  
  public void callVisitors(ExpressionOwner paramExpressionOwner, XPathVisitor paramXPathVisitor)
  {
    assertion(false, "callVisitors should not be called for this object!!!");
  }
  
  public boolean deepEquals(Expression paramExpression)
  {
    if (!isSameClass(paramExpression)) {
      return false;
    }
    return equals((XObject)paramExpression);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\objects\XObject.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */