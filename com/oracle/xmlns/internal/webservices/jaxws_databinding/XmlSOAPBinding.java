package com.oracle.xmlns.internal.webservices.jaxws_databinding;

import java.lang.annotation.Annotation;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="")
@XmlRootElement(name="soap-binding")
public class XmlSOAPBinding
  implements SOAPBinding
{
  @XmlAttribute(name="style")
  protected SoapBindingStyle style;
  @XmlAttribute(name="use")
  protected SoapBindingUse use;
  @XmlAttribute(name="parameter-style")
  protected SoapBindingParameterStyle parameterStyle;
  
  public XmlSOAPBinding() {}
  
  public SoapBindingStyle getStyle()
  {
    if (style == null) {
      return SoapBindingStyle.DOCUMENT;
    }
    return style;
  }
  
  public void setStyle(SoapBindingStyle paramSoapBindingStyle)
  {
    style = paramSoapBindingStyle;
  }
  
  public SoapBindingUse getUse()
  {
    if (use == null) {
      return SoapBindingUse.LITERAL;
    }
    return use;
  }
  
  public void setUse(SoapBindingUse paramSoapBindingUse)
  {
    use = paramSoapBindingUse;
  }
  
  public SoapBindingParameterStyle getParameterStyle()
  {
    if (parameterStyle == null) {
      return SoapBindingParameterStyle.WRAPPED;
    }
    return parameterStyle;
  }
  
  public void setParameterStyle(SoapBindingParameterStyle paramSoapBindingParameterStyle)
  {
    parameterStyle = paramSoapBindingParameterStyle;
  }
  
  public SOAPBinding.Style style()
  {
    return (SOAPBinding.Style)Util.nullSafe(style, SOAPBinding.Style.DOCUMENT);
  }
  
  public SOAPBinding.Use use()
  {
    return (SOAPBinding.Use)Util.nullSafe(use, SOAPBinding.Use.LITERAL);
  }
  
  public SOAPBinding.ParameterStyle parameterStyle()
  {
    return (SOAPBinding.ParameterStyle)Util.nullSafe(parameterStyle, SOAPBinding.ParameterStyle.WRAPPED);
  }
  
  public Class<? extends Annotation> annotationType()
  {
    return SOAPBinding.class;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\oracle\xmlns\internal\webservices\jaxws_databinding\XmlSOAPBinding.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */