package com.sun.org.apache.xpath.internal.objects;

import com.sun.org.apache.xml.internal.utils.WrappedRuntimeException;
import javax.xml.transform.TransformerException;

public class XBooleanStatic
  extends XBoolean
{
  static final long serialVersionUID = -8064147275772687409L;
  private final boolean m_val;
  
  public XBooleanStatic(boolean paramBoolean)
  {
    super(paramBoolean);
    m_val = paramBoolean;
  }
  
  public boolean equals(XObject paramXObject)
  {
    try
    {
      return m_val == paramXObject.bool();
    }
    catch (TransformerException localTransformerException)
    {
      throw new WrappedRuntimeException(localTransformerException);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\objects\XBooleanStatic.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */