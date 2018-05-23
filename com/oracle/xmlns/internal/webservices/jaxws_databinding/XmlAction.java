package com.oracle.xmlns.internal.webservices.jaxws_databinding;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.ws.Action;
import javax.xml.ws.FaultAction;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="", propOrder={"faultAction"})
@XmlRootElement(name="action")
public class XmlAction
  implements Action
{
  @XmlElement(name="fault-action")
  protected List<XmlFaultAction> faultAction;
  @XmlAttribute(name="input")
  protected String input;
  @XmlAttribute(name="output")
  protected String output;
  
  public XmlAction() {}
  
  public List<XmlFaultAction> getFaultAction()
  {
    if (faultAction == null) {
      faultAction = new ArrayList();
    }
    return faultAction;
  }
  
  public String getInput()
  {
    return Util.nullSafe(input);
  }
  
  public void setInput(String paramString)
  {
    input = paramString;
  }
  
  public String getOutput()
  {
    return Util.nullSafe(output);
  }
  
  public void setOutput(String paramString)
  {
    output = paramString;
  }
  
  public String input()
  {
    return Util.nullSafe(input);
  }
  
  public String output()
  {
    return Util.nullSafe(output);
  }
  
  public FaultAction[] fault()
  {
    return new FaultAction[0];
  }
  
  public Class<? extends Annotation> annotationType()
  {
    return Action.class;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\oracle\xmlns\internal\webservices\jaxws_databinding\XmlAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */