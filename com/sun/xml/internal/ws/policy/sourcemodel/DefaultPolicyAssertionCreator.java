package com.sun.xml.internal.ws.policy.sourcemodel;

import com.sun.xml.internal.ws.policy.AssertionSet;
import com.sun.xml.internal.ws.policy.PolicyAssertion;
import com.sun.xml.internal.ws.policy.spi.AssertionCreationException;
import com.sun.xml.internal.ws.policy.spi.PolicyAssertionCreator;
import java.util.Collection;

class DefaultPolicyAssertionCreator
  implements PolicyAssertionCreator
{
  DefaultPolicyAssertionCreator() {}
  
  public String[] getSupportedDomainNamespaceURIs()
  {
    return null;
  }
  
  public PolicyAssertion createAssertion(AssertionData paramAssertionData, Collection<PolicyAssertion> paramCollection, AssertionSet paramAssertionSet, PolicyAssertionCreator paramPolicyAssertionCreator)
    throws AssertionCreationException
  {
    return new DefaultPolicyAssertion(paramAssertionData, paramCollection, paramAssertionSet);
  }
  
  private static final class DefaultPolicyAssertion
    extends PolicyAssertion
  {
    DefaultPolicyAssertion(AssertionData paramAssertionData, Collection<PolicyAssertion> paramCollection, AssertionSet paramAssertionSet)
    {
      super(paramCollection, paramAssertionSet);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\policy\sourcemodel\DefaultPolicyAssertionCreator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */