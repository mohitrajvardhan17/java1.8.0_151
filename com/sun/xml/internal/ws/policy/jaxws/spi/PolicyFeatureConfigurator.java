package com.sun.xml.internal.ws.policy.jaxws.spi;

import com.sun.xml.internal.ws.policy.PolicyException;
import com.sun.xml.internal.ws.policy.PolicyMap;
import com.sun.xml.internal.ws.policy.PolicyMapKey;
import java.util.Collection;
import javax.xml.ws.WebServiceFeature;

public abstract interface PolicyFeatureConfigurator
{
  public abstract Collection<WebServiceFeature> getFeatures(PolicyMapKey paramPolicyMapKey, PolicyMap paramPolicyMap)
    throws PolicyException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\policy\jaxws\spi\PolicyFeatureConfigurator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */