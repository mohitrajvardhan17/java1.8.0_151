package com.sun.xml.internal.ws.policy.sourcemodel;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public final class PolicySourceModelContext
{
  Map<URI, PolicySourceModel> policyModels;
  
  private PolicySourceModelContext() {}
  
  private Map<URI, PolicySourceModel> getModels()
  {
    if (null == policyModels) {
      policyModels = new HashMap();
    }
    return policyModels;
  }
  
  public void addModel(URI paramURI, PolicySourceModel paramPolicySourceModel)
  {
    getModels().put(paramURI, paramPolicySourceModel);
  }
  
  public static PolicySourceModelContext createContext()
  {
    return new PolicySourceModelContext();
  }
  
  public boolean containsModel(URI paramURI)
  {
    return getModels().containsKey(paramURI);
  }
  
  PolicySourceModel retrieveModel(URI paramURI)
  {
    return (PolicySourceModel)getModels().get(paramURI);
  }
  
  PolicySourceModel retrieveModel(URI paramURI1, URI paramURI2, String paramString)
  {
    throw new UnsupportedOperationException();
  }
  
  public String toString()
  {
    return "PolicySourceModelContext: policyModels = " + policyModels;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\policy\sourcemodel\PolicySourceModelContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */