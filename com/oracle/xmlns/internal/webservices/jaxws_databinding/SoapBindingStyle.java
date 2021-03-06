package com.oracle.xmlns.internal.webservices.jaxws_databinding;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(name="soap-binding-style")
@XmlEnum
public enum SoapBindingStyle
{
  DOCUMENT,  RPC;
  
  private SoapBindingStyle() {}
  
  public String value()
  {
    return name();
  }
  
  public static SoapBindingStyle fromValue(String paramString)
  {
    return valueOf(paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\oracle\xmlns\internal\webservices\jaxws_databinding\SoapBindingStyle.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */