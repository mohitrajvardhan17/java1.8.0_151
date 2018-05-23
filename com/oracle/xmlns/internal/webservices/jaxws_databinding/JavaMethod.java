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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="", propOrder={"methodAnnotation", "javaParams"})
@XmlRootElement(name="java-method")
public class JavaMethod
{
  @XmlElementRefs({@javax.xml.bind.annotation.XmlElementRef(name="web-endpoint", namespace="http://xmlns.oracle.com/webservices/jaxws-databinding", type=XmlWebEndpoint.class, required=false), @javax.xml.bind.annotation.XmlElementRef(name="oneway", namespace="http://xmlns.oracle.com/webservices/jaxws-databinding", type=XmlOneway.class, required=false), @javax.xml.bind.annotation.XmlElementRef(name="action", namespace="http://xmlns.oracle.com/webservices/jaxws-databinding", type=XmlAction.class, required=false), @javax.xml.bind.annotation.XmlElementRef(name="soap-binding", namespace="http://xmlns.oracle.com/webservices/jaxws-databinding", type=XmlSOAPBinding.class, required=false), @javax.xml.bind.annotation.XmlElementRef(name="web-result", namespace="http://xmlns.oracle.com/webservices/jaxws-databinding", type=XmlWebResult.class, required=false), @javax.xml.bind.annotation.XmlElementRef(name="web-method", namespace="http://xmlns.oracle.com/webservices/jaxws-databinding", type=XmlWebMethod.class, required=false)})
  @XmlAnyElement
  protected List<Object> methodAnnotation;
  @XmlElement(name="java-params")
  protected JavaParams javaParams;
  @XmlAttribute(name="name", required=true)
  protected String name;
  @XmlAnyAttribute
  private Map<QName, String> otherAttributes = new HashMap();
  
  public JavaMethod() {}
  
  public List<Object> getMethodAnnotation()
  {
    if (methodAnnotation == null) {
      methodAnnotation = new ArrayList();
    }
    return methodAnnotation;
  }
  
  public JavaParams getJavaParams()
  {
    return javaParams;
  }
  
  public void setJavaParams(JavaParams paramJavaParams)
  {
    javaParams = paramJavaParams;
  }
  
  public String getName()
  {
    return name;
  }
  
  public void setName(String paramString)
  {
    name = paramString;
  }
  
  public Map<QName, String> getOtherAttributes()
  {
    return otherAttributes;
  }
  
  @XmlAccessorType(XmlAccessType.FIELD)
  @XmlType(name="", propOrder={"javaParam"})
  public static class JavaParams
  {
    @XmlElement(name="java-param", required=true)
    protected List<JavaParam> javaParam;
    
    public JavaParams() {}
    
    public List<JavaParam> getJavaParam()
    {
      if (javaParam == null) {
        javaParam = new ArrayList();
      }
      return javaParam;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\oracle\xmlns\internal\webservices\jaxws_databinding\JavaMethod.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */