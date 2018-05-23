package com.oracle.xmlns.internal.webservices.jaxws_databinding;

import java.lang.annotation.Annotation;
import javax.jws.WebMethod;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="")
@XmlRootElement(name="web-method")
public class XmlWebMethod
  implements WebMethod
{
  @XmlAttribute(name="action")
  protected String action;
  @XmlAttribute(name="exclude")
  protected Boolean exclude;
  @XmlAttribute(name="operation-name")
  protected String operationName;
  
  public XmlWebMethod() {}
  
  public String getAction()
  {
    if (action == null) {
      return "";
    }
    return action;
  }
  
  public void setAction(String paramString)
  {
    action = paramString;
  }
  
  public boolean isExclude()
  {
    if (exclude == null) {
      return false;
    }
    return exclude.booleanValue();
  }
  
  public void setExclude(Boolean paramBoolean)
  {
    exclude = paramBoolean;
  }
  
  public String getOperationName()
  {
    if (operationName == null) {
      return "";
    }
    return operationName;
  }
  
  public void setOperationName(String paramString)
  {
    operationName = paramString;
  }
  
  public String operationName()
  {
    return Util.nullSafe(operationName);
  }
  
  public String action()
  {
    return Util.nullSafe(action);
  }
  
  public boolean exclude()
  {
    return ((Boolean)Util.nullSafe(exclude, Boolean.valueOf(false))).booleanValue();
  }
  
  public Class<? extends Annotation> annotationType()
  {
    return WebMethod.class;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\oracle\xmlns\internal\webservices\jaxws_databinding\XmlWebMethod.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */