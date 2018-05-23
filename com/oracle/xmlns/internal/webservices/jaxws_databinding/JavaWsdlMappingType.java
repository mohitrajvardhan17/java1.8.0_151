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
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="java-wsdl-mapping-type", propOrder={"xmlSchemaMapping", "classAnnotation", "javaMethods"})
public class JavaWsdlMappingType
{
  @XmlElement(name="xml-schema-mapping")
  protected XmlSchemaMapping xmlSchemaMapping;
  @XmlElementRefs({@javax.xml.bind.annotation.XmlElementRef(name="web-service-client", namespace="http://xmlns.oracle.com/webservices/jaxws-databinding", type=XmlWebServiceClient.class, required=false), @javax.xml.bind.annotation.XmlElementRef(name="binding-type", namespace="http://xmlns.oracle.com/webservices/jaxws-databinding", type=XmlBindingType.class, required=false), @javax.xml.bind.annotation.XmlElementRef(name="web-service", namespace="http://xmlns.oracle.com/webservices/jaxws-databinding", type=XmlWebService.class, required=false), @javax.xml.bind.annotation.XmlElementRef(name="web-fault", namespace="http://xmlns.oracle.com/webservices/jaxws-databinding", type=XmlWebFault.class, required=false), @javax.xml.bind.annotation.XmlElementRef(name="service-mode", namespace="http://xmlns.oracle.com/webservices/jaxws-databinding", type=XmlServiceMode.class, required=false), @javax.xml.bind.annotation.XmlElementRef(name="mtom", namespace="http://xmlns.oracle.com/webservices/jaxws-databinding", type=XmlMTOM.class, required=false), @javax.xml.bind.annotation.XmlElementRef(name="handler-chain", namespace="http://xmlns.oracle.com/webservices/jaxws-databinding", type=XmlHandlerChain.class, required=false), @javax.xml.bind.annotation.XmlElementRef(name="soap-binding", namespace="http://xmlns.oracle.com/webservices/jaxws-databinding", type=XmlSOAPBinding.class, required=false)})
  @XmlAnyElement
  protected List<Object> classAnnotation;
  @XmlElement(name="java-methods")
  protected JavaMethods javaMethods;
  @XmlAttribute(name="name")
  protected String name;
  @XmlAttribute(name="java-type-name")
  protected String javaTypeName;
  @XmlAttribute(name="existing-annotations")
  protected ExistingAnnotationsType existingAnnotations;
  @XmlAttribute(name="databinding")
  protected String databinding;
  @XmlAnyAttribute
  private Map<QName, String> otherAttributes = new HashMap();
  
  public JavaWsdlMappingType() {}
  
  public XmlSchemaMapping getXmlSchemaMapping()
  {
    return xmlSchemaMapping;
  }
  
  public void setXmlSchemaMapping(XmlSchemaMapping paramXmlSchemaMapping)
  {
    xmlSchemaMapping = paramXmlSchemaMapping;
  }
  
  public List<Object> getClassAnnotation()
  {
    if (classAnnotation == null) {
      classAnnotation = new ArrayList();
    }
    return classAnnotation;
  }
  
  public JavaMethods getJavaMethods()
  {
    return javaMethods;
  }
  
  public void setJavaMethods(JavaMethods paramJavaMethods)
  {
    javaMethods = paramJavaMethods;
  }
  
  public String getName()
  {
    return name;
  }
  
  public void setName(String paramString)
  {
    name = paramString;
  }
  
  public String getJavaTypeName()
  {
    return javaTypeName;
  }
  
  public void setJavaTypeName(String paramString)
  {
    javaTypeName = paramString;
  }
  
  public ExistingAnnotationsType getExistingAnnotations()
  {
    return existingAnnotations;
  }
  
  public void setExistingAnnotations(ExistingAnnotationsType paramExistingAnnotationsType)
  {
    existingAnnotations = paramExistingAnnotationsType;
  }
  
  public String getDatabinding()
  {
    return databinding;
  }
  
  public void setDatabinding(String paramString)
  {
    databinding = paramString;
  }
  
  public Map<QName, String> getOtherAttributes()
  {
    return otherAttributes;
  }
  
  @XmlAccessorType(XmlAccessType.FIELD)
  @XmlType(name="", propOrder={"javaMethod"})
  public static class JavaMethods
  {
    @XmlElement(name="java-method")
    protected List<JavaMethod> javaMethod;
    
    public JavaMethods() {}
    
    public List<JavaMethod> getJavaMethod()
    {
      if (javaMethod == null) {
        javaMethod = new ArrayList();
      }
      return javaMethod;
    }
  }
  
  @XmlAccessorType(XmlAccessType.FIELD)
  @XmlType(name="", propOrder={"any"})
  public static class XmlSchemaMapping
  {
    @XmlAnyElement(lax=true)
    protected List<Object> any;
    
    public XmlSchemaMapping() {}
    
    public List<Object> getAny()
    {
      if (any == null) {
        any = new ArrayList();
      }
      return any;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\oracle\xmlns\internal\webservices\jaxws_databinding\JavaWsdlMappingType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */