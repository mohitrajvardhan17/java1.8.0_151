package com.sun.xml.internal.ws.wsdl.writer.document;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.annotation.XmlElement;
import javax.xml.namespace.QName;

@XmlElement("binding")
public abstract interface Binding
  extends TypedXmlWriter, StartWithExtensionsType
{
  @XmlAttribute
  public abstract Binding type(QName paramQName);
  
  @XmlAttribute
  public abstract Binding name(String paramString);
  
  @XmlElement
  public abstract BindingOperationType operation();
  
  @XmlElement(value="binding", ns="http://schemas.xmlsoap.org/wsdl/soap/")
  public abstract com.sun.xml.internal.ws.wsdl.writer.document.soap.SOAPBinding soapBinding();
  
  @XmlElement(value="binding", ns="http://schemas.xmlsoap.org/wsdl/soap12/")
  public abstract com.sun.xml.internal.ws.wsdl.writer.document.soap12.SOAPBinding soap12Binding();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\wsdl\writer\document\Binding.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */