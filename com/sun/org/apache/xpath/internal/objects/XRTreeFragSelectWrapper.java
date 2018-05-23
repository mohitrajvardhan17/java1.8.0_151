package com.sun.org.apache.xpath.internal.objects;

import com.sun.org.apache.xalan.internal.res.XSLMessages;
import com.sun.org.apache.xml.internal.dtm.DTMIterator;
import com.sun.org.apache.xml.internal.utils.XMLString;
import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.XPathContext;
import java.util.Vector;
import javax.xml.transform.TransformerException;

public class XRTreeFragSelectWrapper
  extends XRTreeFrag
  implements Cloneable
{
  static final long serialVersionUID = -6526177905590461251L;
  
  public XRTreeFragSelectWrapper(Expression paramExpression)
  {
    super(paramExpression);
  }
  
  public void fixupVariables(Vector paramVector, int paramInt)
  {
    ((Expression)m_obj).fixupVariables(paramVector, paramInt);
  }
  
  public XObject execute(XPathContext paramXPathContext)
    throws TransformerException
  {
    XObject localXObject = ((Expression)m_obj).execute(paramXPathContext);
    localXObject.allowDetachToRelease(m_allowRelease);
    if (localXObject.getType() == 3) {
      return localXObject;
    }
    return new XString(localXObject.str());
  }
  
  public void detach()
  {
    throw new RuntimeException(XSLMessages.createXPATHMessage("ER_DETACH_NOT_SUPPORTED_XRTREEFRAGSELECTWRAPPER", null));
  }
  
  public double num()
    throws TransformerException
  {
    throw new RuntimeException(XSLMessages.createXPATHMessage("ER_NUM_NOT_SUPPORTED_XRTREEFRAGSELECTWRAPPER", null));
  }
  
  public XMLString xstr()
  {
    throw new RuntimeException(XSLMessages.createXPATHMessage("ER_XSTR_NOT_SUPPORTED_XRTREEFRAGSELECTWRAPPER", null));
  }
  
  public String str()
  {
    throw new RuntimeException(XSLMessages.createXPATHMessage("ER_STR_NOT_SUPPORTED_XRTREEFRAGSELECTWRAPPER", null));
  }
  
  public int getType()
  {
    return 3;
  }
  
  public int rtf()
  {
    throw new RuntimeException(XSLMessages.createXPATHMessage("ER_RTF_NOT_SUPPORTED_XRTREEFRAGSELECTWRAPPER", null));
  }
  
  public DTMIterator asNodeIterator()
  {
    throw new RuntimeException(XSLMessages.createXPATHMessage("ER_RTF_NOT_SUPPORTED_XRTREEFRAGSELECTWRAPPER", null));
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\objects\XRTreeFragSelectWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */