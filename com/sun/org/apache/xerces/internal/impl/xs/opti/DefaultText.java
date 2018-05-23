package com.sun.org.apache.xerces.internal.impl.xs.opti;

import org.w3c.dom.DOMException;
import org.w3c.dom.Text;

public class DefaultText
  extends NodeImpl
  implements Text
{
  public DefaultText() {}
  
  public String getData()
    throws DOMException
  {
    return null;
  }
  
  public void setData(String paramString)
    throws DOMException
  {
    throw new DOMException((short)9, "Method not supported");
  }
  
  public int getLength()
  {
    return 0;
  }
  
  public String substringData(int paramInt1, int paramInt2)
    throws DOMException
  {
    throw new DOMException((short)9, "Method not supported");
  }
  
  public void appendData(String paramString)
    throws DOMException
  {
    throw new DOMException((short)9, "Method not supported");
  }
  
  public void insertData(int paramInt, String paramString)
    throws DOMException
  {
    throw new DOMException((short)9, "Method not supported");
  }
  
  public void deleteData(int paramInt1, int paramInt2)
    throws DOMException
  {
    throw new DOMException((short)9, "Method not supported");
  }
  
  public void replaceData(int paramInt1, int paramInt2, String paramString)
    throws DOMException
  {
    throw new DOMException((short)9, "Method not supported");
  }
  
  public Text splitText(int paramInt)
    throws DOMException
  {
    throw new DOMException((short)9, "Method not supported");
  }
  
  public boolean isElementContentWhitespace()
  {
    throw new DOMException((short)9, "Method not supported");
  }
  
  public String getWholeText()
  {
    throw new DOMException((short)9, "Method not supported");
  }
  
  public Text replaceWholeText(String paramString)
    throws DOMException
  {
    throw new DOMException((short)9, "Method not supported");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\opti\DefaultText.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */