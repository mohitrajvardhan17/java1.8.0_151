package com.sun.xml.internal.ws.policy;

import com.sun.xml.internal.ws.policy.privateutil.LocalizationMessages;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils.ServiceProvider;
import com.sun.xml.internal.ws.policy.spi.PolicyAssertionValidator;
import com.sun.xml.internal.ws.policy.spi.PolicyAssertionValidator.Fitness;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

public class AssertionValidationProcessor
{
  private static final PolicyLogger LOGGER = PolicyLogger.getLogger(AssertionValidationProcessor.class);
  private final Collection<PolicyAssertionValidator> validators = new LinkedList();
  
  private AssertionValidationProcessor()
    throws PolicyException
  {
    this(null);
  }
  
  protected AssertionValidationProcessor(Collection<PolicyAssertionValidator> paramCollection)
    throws PolicyException
  {
    for (Object localObject2 : (PolicyAssertionValidator[])PolicyUtils.ServiceProvider.load(PolicyAssertionValidator.class)) {
      validators.add(localObject2);
    }
    if (paramCollection != null)
    {
      ??? = paramCollection.iterator();
      while (((Iterator)???).hasNext())
      {
        PolicyAssertionValidator localPolicyAssertionValidator = (PolicyAssertionValidator)((Iterator)???).next();
        validators.add(localPolicyAssertionValidator);
      }
    }
    if (validators.size() == 0) {
      throw ((PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0076_NO_SERVICE_PROVIDERS_FOUND(PolicyAssertionValidator.class.getName()))));
    }
  }
  
  public static AssertionValidationProcessor getInstance()
    throws PolicyException
  {
    return new AssertionValidationProcessor();
  }
  
  public PolicyAssertionValidator.Fitness validateClientSide(PolicyAssertion paramPolicyAssertion)
    throws PolicyException
  {
    PolicyAssertionValidator.Fitness localFitness = PolicyAssertionValidator.Fitness.UNKNOWN;
    Iterator localIterator = validators.iterator();
    while (localIterator.hasNext())
    {
      PolicyAssertionValidator localPolicyAssertionValidator = (PolicyAssertionValidator)localIterator.next();
      localFitness = localFitness.combine(localPolicyAssertionValidator.validateClientSide(paramPolicyAssertion));
      if (localFitness == PolicyAssertionValidator.Fitness.SUPPORTED) {
        break;
      }
    }
    return localFitness;
  }
  
  public PolicyAssertionValidator.Fitness validateServerSide(PolicyAssertion paramPolicyAssertion)
    throws PolicyException
  {
    PolicyAssertionValidator.Fitness localFitness = PolicyAssertionValidator.Fitness.UNKNOWN;
    Iterator localIterator = validators.iterator();
    while (localIterator.hasNext())
    {
      PolicyAssertionValidator localPolicyAssertionValidator = (PolicyAssertionValidator)localIterator.next();
      localFitness = localFitness.combine(localPolicyAssertionValidator.validateServerSide(paramPolicyAssertion));
      if (localFitness == PolicyAssertionValidator.Fitness.SUPPORTED) {
        break;
      }
    }
    return localFitness;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\policy\AssertionValidationProcessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */