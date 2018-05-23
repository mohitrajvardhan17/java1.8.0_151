package com.oracle.xmlns.internal.webservices.jaxws_databinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="", propOrder={"paramAnnotation"})
@XmlRootElement(name="java-param")
public class JavaParam
{
  @XmlElementRef(name="web-param", namespace="http://xmlns.oracle.com/webservices/jaxws-databinding", type=XmlWebParam.class, required=false)
  @XmlAnyElement
  protected List<Object> paramAnnotation;
  @XmlAttribute(name="java-type")
  protected String javaType;
  @XmlAnyAttribute
  private Map<QName, String> otherAttributes = new HashMap();
  
  public JavaParam() {}
  
  public List<Object> getParamAnnotation()
  {
    if (paramAnnotation == null) {
      paramAnnotation = new ArrayList();
    }
    return paramAnnotation;
  }
  
  public String getJavaType()
  {
    return javaType;
  }
  
  public void setJavaType(String paramString)
  {
    javaType = paramString;
  }
  
  public Map<QName, String> getOtherAttributes()
  {
    return otherAttributes;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\oracle\xmlns\internal\webservices\jaxws_databinding\JavaParam.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */