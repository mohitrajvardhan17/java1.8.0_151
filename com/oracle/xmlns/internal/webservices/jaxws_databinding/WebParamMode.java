package com.oracle.xmlns.internal.webservices.jaxws_databinding;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(name="web-param-mode")
@XmlEnum
public enum WebParamMode
{
  IN,  OUT,  INOUT;
  
  private WebParamMode() {}
  
  public String value()
  {
    return name();
  }
  
  public static WebParamMode fromValue(String paramString)
  {
    return valueOf(paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\oracle\xmlns\internal\webservices\jaxws_databinding\WebParamMode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */