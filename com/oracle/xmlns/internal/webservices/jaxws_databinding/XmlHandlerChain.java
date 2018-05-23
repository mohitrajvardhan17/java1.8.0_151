package com.oracle.xmlns.internal.webservices.jaxws_databinding;

import java.lang.annotation.Annotation;
import javax.jws.HandlerChain;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="")
@XmlRootElement(name="handler-chain")
public class XmlHandlerChain
  implements HandlerChain
{
  @XmlAttribute(name="file")
  protected String file;
  
  public XmlHandlerChain() {}
  
  public String getFile()
  {
    return file;
  }
  
  public void setFile(String paramString)
  {
    file = paramString;
  }
  
  public String file()
  {
    return Util.nullSafe(file);
  }
  
  public String name()
  {
    return "";
  }
  
  public Class<? extends Annotation> annotationType()
  {
    return HandlerChain.class;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\oracle\xmlns\internal\webservices\jaxws_databinding\XmlHandlerChain.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */