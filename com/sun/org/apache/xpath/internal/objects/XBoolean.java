package com.sun.org.apache.xpath.internal.objects;

import com.sun.org.apache.xml.internal.utils.WrappedRuntimeException;
import javax.xml.transform.TransformerException;

public class XBoolean
  extends XObject
{
  static final long serialVersionUID = -2964933058866100881L;
  public static final XBoolean S_TRUE = new XBooleanStatic(true);
  public static final XBoolean S_FALSE = new XBooleanStatic(false);
  private final boolean m_val;
  
  public XBoolean(boolean paramBoolean)
  {
    m_val = paramBoolean;
  }
  
  public XBoolean(Boolean paramBoolean)
  {
    m_val = paramBoolean.booleanValue();
    setObject(paramBoolean);
  }
  
  public int getType()
  {
    return 1;
  }
  
  public String getTypeString()
  {
    return "#BOOLEAN";
  }
  
  public double num()
  {
    return m_val ? 1.0D : 0.0D;
  }
  
  public boolean bool()
  {
    return m_val;
  }
  
  public String str()
  {
    return m_val ? "true" : "false";
  }
  
  public Object object()
  {
    if (null == m_obj) {
      setObject(new Boolean(m_val));
    }
    return m_obj;
  }
  
  public boolean equals(XObject paramXObject)
  {
    if (paramXObject.getType() == 4) {
      return paramXObject.equals(this);
    }
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xpath\internal\objects\XBoolean.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */