package com.sun.xml.internal.ws.addressing.v200408;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.namespace.QName;

@XmlRootElement(name="ProblemHeaderQName", namespace="http://schemas.xmlsoap.org/ws/2004/08/addressing")
public class ProblemHeaderQName
{
  @XmlValue
  private QName value;
  
  public ProblemHeaderQName() {}
  
  public ProblemHeaderQName(QName paramQName)
  {
    value = paramQName;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\addressing\v200408\ProblemHeaderQName.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */