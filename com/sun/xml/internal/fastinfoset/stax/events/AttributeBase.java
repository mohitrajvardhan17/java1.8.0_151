package com.sun.xml.internal.fastinfoset.stax.events;

import javax.xml.namespace.QName;
import javax.xml.stream.events.Attribute;

public class AttributeBase
  extends EventBase
  implements Attribute
{
  private QName _QName;
  private String _value;
  private String _attributeType = null;
  private boolean _specified = false;
  
  public AttributeBase()
  {
    super(10);
  }
  
  public AttributeBase(String paramString1, String paramString2)
  {
    super(10);
    _QName = new QName(paramString1);
    _value = paramString2;
  }
  
  public AttributeBase(QName paramQName, String paramString)
  {
    _QName = paramQName;
    _value = paramString;
  }
  
  public AttributeBase(String paramString1, String paramString2, String paramString3)
  {
    this(paramString1, null, paramString2, paramString3, null);
  }
  
  public AttributeBase(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5)
  {
    if (paramString1 == null) {
      paramString1 = "";
    }
    _QName = new QName(paramString2, paramString3, paramString1);
    _value = paramString4;
    _attributeType = (paramString5 == null ? "CDATA" : paramString5);
  }
  
  public void setName(QName paramQName)
  {
    _QName = paramQName;
  }
  
  public QName getName()
  {
    return _QName;
  }
  
  public void setValue(String paramString)
  {
    _value = paramString;
  }
  
  public String getLocalName()
  {
    return _QName.getLocalPart();
  }
  
  public String getValue()
  {
    return _value;
  }
  
  public void setAttributeType(String paramString)
  {
    _attributeType = paramString;
  }
  
  public String getDTDType()
  {
    return _attributeType;
  }
  
  public boolean isSpecified()
  {
    return _specified;
  }
  
  public void setSpecified(boolean paramBoolean)
  {
    _specified = paramBoolean;
  }
  
  public String toString()
  {
    String str = _QName.getPrefix();
    if (!Util.isEmptyString(str)) {
      return str + ":" + _QName.getLocalPart() + "='" + _value + "'";
    }
    return _QName.getLocalPart() + "='" + _value + "'";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\fastinfoset\stax\events\AttributeBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */