package com.sun.xml.internal.ws.policy.jaxws.spi;

import com.sun.xml.internal.ws.api.WSBinding;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.policy.PolicyException;
import com.sun.xml.internal.ws.policy.PolicyMap;
import com.sun.xml.internal.ws.policy.PolicySubject;
import java.util.Collection;

public abstract interface PolicyMapConfigurator
{
  public abstract Collection<PolicySubject> update(PolicyMap paramPolicyMap, SEIModel paramSEIModel, WSBinding paramWSBinding)
    throws PolicyException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\policy\jaxws\spi\PolicyMapConfigurator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */