package com.sun.xml.internal.ws.fault;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="CodeType", namespace="http://www.w3.org/2003/05/soap-envelope", propOrder={"Value", "Subcode"})
class CodeType
{
  @XmlTransient
  private static final String ns = "http://www.w3.org/2003/05/soap-envelope";
  @XmlElement(namespace="http://www.w3.org/2003/05/soap-envelope")
  private QName Value;
  @XmlElement(namespace="http://www.w3.org/2003/05/soap-envelope")
  private SubcodeType Subcode;
  
  CodeType(QName paramQName)
  {
    Value = paramQName;
  }
  
  CodeType() {}
  
  QName getValue()
  {
    return Value;
  }
  
  SubcodeType getSubcode()
  {
    return Subcode;
  }
  
  void setSubcode(SubcodeType paramSubcodeType)
  {
    Subcode = paramSubcodeType;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\fault\CodeType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */