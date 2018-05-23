package com.sun.org.apache.xerces.internal.impl.xs.opti;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.TypeInfo;

public class AttrImpl
  extends NodeImpl
  implements Attr
{
  Element element;
  String value;
  
  public AttrImpl()
  {
    nodeType = 2;
  }
  
  public AttrImpl(Element paramElement, String paramString1, String paramString2, String paramString3, String paramString4, String paramString5)
  {
    super(paramString1, paramString2, paramString3, paramString4, (short)2);
    element = paramElement;
    value = paramString5;
  }
  
  public String getName()
  {
    return rawname;
  }
  
  public boolean getSpecified()
  {
    return true;
  }
  
  public String getValue()
  {
    return value;
  }
  
  public String getNodeValue()
  {
    return getValue();
  }
  
  public Element getOwnerElement()
  {
    return element;
  }
  
  public Document getOwnerDocument()
  {
    return element.getOwnerDocument();
  }
  
  public void setValue(String paramString)
    throws DOMException
  {
    value = paramString;
  }
  
  public boolean isId()
  {
    return false;
  }
  
  public TypeInfo getSchemaTypeInfo()
  {
    return null;
  }
  
  public String toString()
  {
    return getName() + "=\"" + getValue() + "\"";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\opti\AttrImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */