package com.sun.xml.internal.ws.addressing;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="ProblemAction", namespace="http://www.w3.org/2005/08/addressing")
public class ProblemAction
{
  @XmlElement(name="Action", namespace="http://www.w3.org/2005/08/addressing")
  private String action;
  @XmlElement(name="SoapAction", namespace="http://www.w3.org/2005/08/addressing")
  private String soapAction;
  
  public ProblemAction() {}
  
  public ProblemAction(String paramString)
  {
    action = paramString;
  }
  
  public ProblemAction(String paramString1, String paramString2)
  {
    action = paramString1;
    soapAction = paramString2;
  }
  
  public String getAction()
  {
    return action;
  }
  
  public String getSoapAction()
  {
    return soapAction;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\addressing\ProblemAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */