package com.sun.xml.internal.ws.policy.sourcemodel;

import com.sun.xml.internal.ws.policy.PolicyException;

public abstract class PolicyModelUnmarshaller
{
  private static final PolicyModelUnmarshaller xmlUnmarshaller = new XmlPolicyModelUnmarshaller();
  
  PolicyModelUnmarshaller() {}
  
  public abstract PolicySourceModel unmarshalModel(Object paramObject)
    throws PolicyException;
  
  public static PolicyModelUnmarshaller getXmlUnmarshaller()
  {
    return xmlUnmarshaller;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\policy\sourcemodel\PolicyModelUnmarshaller.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */