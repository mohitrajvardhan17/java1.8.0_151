package com.sun.xml.internal.ws.policy.jaxws;

import com.sun.xml.internal.ws.api.policy.AlternativeSelector;
import com.sun.xml.internal.ws.api.policy.PolicyResolver;
import com.sun.xml.internal.ws.api.policy.PolicyResolver.ClientContext;
import com.sun.xml.internal.ws.api.policy.PolicyResolver.ServerContext;
import com.sun.xml.internal.ws.api.policy.ValidationProcessor;
import com.sun.xml.internal.ws.policy.AssertionSet;
import com.sun.xml.internal.ws.policy.EffectivePolicyModifier;
import com.sun.xml.internal.ws.policy.Policy;
import com.sun.xml.internal.ws.policy.PolicyAssertion;
import com.sun.xml.internal.ws.policy.PolicyException;
import com.sun.xml.internal.ws.policy.PolicyMap;
import com.sun.xml.internal.ws.policy.spi.PolicyAssertionValidator.Fitness;
import com.sun.xml.internal.ws.resources.PolicyMessages;
import java.util.Iterator;
import javax.xml.ws.WebServiceException;

public class DefaultPolicyResolver
  implements PolicyResolver
{
  public DefaultPolicyResolver() {}
  
  public PolicyMap resolve(PolicyResolver.ServerContext paramServerContext)
  {
    PolicyMap localPolicyMap = paramServerContext.getPolicyMap();
    if (localPolicyMap != null) {
      validateServerPolicyMap(localPolicyMap);
    }
    return localPolicyMap;
  }
  
  public PolicyMap resolve(PolicyResolver.ClientContext paramClientContext)
  {
    PolicyMap localPolicyMap = paramClientContext.getPolicyMap();
    if (localPolicyMap != null) {
      localPolicyMap = doAlternativeSelection(localPolicyMap);
    }
    return localPolicyMap;
  }
  
  private void validateServerPolicyMap(PolicyMap paramPolicyMap)
  {
    try
    {
      ValidationProcessor localValidationProcessor = ValidationProcessor.getInstance();
      Iterator localIterator1 = paramPolicyMap.iterator();
      while (localIterator1.hasNext())
      {
        Policy localPolicy = (Policy)localIterator1.next();
        Iterator localIterator2 = localPolicy.iterator();
        while (localIterator2.hasNext())
        {
          AssertionSet localAssertionSet = (AssertionSet)localIterator2.next();
          Iterator localIterator3 = localAssertionSet.iterator();
          while (localIterator3.hasNext())
          {
            PolicyAssertion localPolicyAssertion = (PolicyAssertion)localIterator3.next();
            PolicyAssertionValidator.Fitness localFitness = localValidationProcessor.validateServerSide(localPolicyAssertion);
            if (localFitness != PolicyAssertionValidator.Fitness.SUPPORTED) {
              throw new PolicyException(PolicyMessages.WSP_1015_SERVER_SIDE_ASSERTION_VALIDATION_FAILED(localPolicyAssertion.getName(), localFitness));
            }
          }
        }
      }
    }
    catch (PolicyException localPolicyException)
    {
      throw new WebServiceException(localPolicyException);
    }
  }
  
  private PolicyMap doAlternativeSelection(PolicyMap paramPolicyMap)
  {
    EffectivePolicyModifier localEffectivePolicyModifier = EffectivePolicyModifier.createEffectivePolicyModifier();
    localEffectivePolicyModifier.connect(paramPolicyMap);
    try
    {
      AlternativeSelector.doSelection(localEffectivePolicyModifier);
    }
    catch (PolicyException localPolicyException)
    {
      throw new WebServiceException(localPolicyException);
    }
    return paramPolicyMap;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\policy\jaxws\DefaultPolicyResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */