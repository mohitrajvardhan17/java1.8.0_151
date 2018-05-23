package com.oracle.xmlns.internal.webservices.jaxws_databinding;

import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.ws.FaultAction;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="")
@XmlRootElement(name="fault-action")
public class XmlFaultAction
  implements FaultAction
{
  @XmlAttribute(name="className", required=true)
  protected String className;
  @XmlAttribute(name="value")
  protected String value;
  
  public XmlFaultAction() {}
  
  public String getClassName()
  {
    return className;
  }
  
  public void setClassName(String paramString)
  {
    className = paramString;
  }
  
  public String getValue()
  {
    return Util.nullSafe(value);
  }
  
  public void setValue(String paramString)
  {
    value = paramString;
  }
  
  public Class<? extends Exception> className()
  {
    return Util.findClass(className);
  }
  
  public String value()
  {
    return Util.nullSafe(value);
  }
  
  public Class<? extends Annotation> annotationType()
  {
    return FaultAction.class;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\oracle\xmlns\internal\webservices\jaxws_databinding\XmlFaultAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */