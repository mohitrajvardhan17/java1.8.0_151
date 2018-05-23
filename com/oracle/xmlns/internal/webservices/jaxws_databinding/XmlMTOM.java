package com.oracle.xmlns.internal.webservices.jaxws_databinding;

import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.ws.soap.MTOM;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="")
@XmlRootElement(name="mtom")
public class XmlMTOM
  implements MTOM
{
  @XmlAttribute(name="enabled")
  protected Boolean enabled;
  @XmlAttribute(name="threshold")
  protected Integer threshold;
  
  public XmlMTOM() {}
  
  public boolean isEnabled()
  {
    if (enabled == null) {
      return true;
    }
    return enabled.booleanValue();
  }
  
  public void setEnabled(Boolean paramBoolean)
  {
    enabled = paramBoolean;
  }
  
  public int getThreshold()
  {
    if (threshold == null) {
      return 0;
    }
    return threshold.intValue();
  }
  
  public void setThreshold(Integer paramInteger)
  {
    threshold = paramInteger;
  }
  
  public boolean enabled()
  {
    return ((Boolean)Util.nullSafe(enabled, Boolean.TRUE)).booleanValue();
  }
  
  public int threshold()
  {
    return ((Integer)Util.nullSafe(threshold, Integer.valueOf(0))).intValue();
  }
  
  public Class<? extends Annotation> annotationType()
  {
    return MTOM.class;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\oracle\xmlns\internal\webservices\jaxws_databinding\XmlMTOM.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */