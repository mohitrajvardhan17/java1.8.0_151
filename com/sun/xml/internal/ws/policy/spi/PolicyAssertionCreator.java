package com.sun.xml.internal.ws.policy.spi;

import com.sun.xml.internal.ws.policy.AssertionSet;
import com.sun.xml.internal.ws.policy.PolicyAssertion;
import com.sun.xml.internal.ws.policy.sourcemodel.AssertionData;
import java.util.Collection;

public abstract interface PolicyAssertionCreator
{
  public abstract String[] getSupportedDomainNamespaceURIs();
  
  public abstract PolicyAssertion createAssertion(AssertionData paramAssertionData, Collection<PolicyAssertion> paramCollection, AssertionSet paramAssertionSet, PolicyAssertionCreator paramPolicyAssertionCreator)
    throws AssertionCreationException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\policy\spi\PolicyAssertionCreator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */