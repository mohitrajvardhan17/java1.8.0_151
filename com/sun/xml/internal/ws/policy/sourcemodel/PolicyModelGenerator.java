package com.sun.xml.internal.ws.policy.sourcemodel;

import com.sun.xml.internal.ws.policy.AssertionSet;
import com.sun.xml.internal.ws.policy.NestedPolicy;
import com.sun.xml.internal.ws.policy.Policy;
import com.sun.xml.internal.ws.policy.PolicyAssertion;
import com.sun.xml.internal.ws.policy.PolicyException;
import com.sun.xml.internal.ws.policy.privateutil.LocalizationMessages;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;
import java.util.Iterator;

public abstract class PolicyModelGenerator
{
  private static final PolicyLogger LOGGER = PolicyLogger.getLogger(PolicyModelGenerator.class);
  
  protected PolicyModelGenerator() {}
  
  public static PolicyModelGenerator getGenerator()
  {
    return getNormalizedGenerator(new PolicySourceModelCreator());
  }
  
  protected static PolicyModelGenerator getCompactGenerator(PolicySourceModelCreator paramPolicySourceModelCreator)
  {
    return new CompactModelGenerator(paramPolicySourceModelCreator);
  }
  
  protected static PolicyModelGenerator getNormalizedGenerator(PolicySourceModelCreator paramPolicySourceModelCreator)
  {
    return new NormalizedModelGenerator(paramPolicySourceModelCreator);
  }
  
  public abstract PolicySourceModel translate(Policy paramPolicy)
    throws PolicyException;
  
  protected abstract ModelNode translate(ModelNode paramModelNode, NestedPolicy paramNestedPolicy);
  
  protected void translate(ModelNode paramModelNode, AssertionSet paramAssertionSet)
  {
    Iterator localIterator = paramAssertionSet.iterator();
    while (localIterator.hasNext())
    {
      PolicyAssertion localPolicyAssertion = (PolicyAssertion)localIterator.next();
      AssertionData localAssertionData = AssertionData.createAssertionData(localPolicyAssertion.getName(), localPolicyAssertion.getValue(), localPolicyAssertion.getAttributes(), localPolicyAssertion.isOptional(), localPolicyAssertion.isIgnorable());
      ModelNode localModelNode = paramModelNode.createChildAssertionNode(localAssertionData);
      if (localPolicyAssertion.hasNestedPolicy()) {
        translate(localModelNode, localPolicyAssertion.getNestedPolicy());
      }
      if (localPolicyAssertion.hasParameters()) {
        translate(localModelNode, localPolicyAssertion.getParametersIterator());
      }
    }
  }
  
  protected void translate(ModelNode paramModelNode, Iterator<PolicyAssertion> paramIterator)
  {
    while (paramIterator.hasNext())
    {
      PolicyAssertion localPolicyAssertion = (PolicyAssertion)paramIterator.next();
      AssertionData localAssertionData = AssertionData.createAssertionParameterData(localPolicyAssertion.getName(), localPolicyAssertion.getValue(), localPolicyAssertion.getAttributes());
      ModelNode localModelNode = paramModelNode.createChildAssertionParameterNode(localAssertionData);
      if (localPolicyAssertion.hasNestedPolicy()) {
        throw ((IllegalStateException)LOGGER.logSevereException(new IllegalStateException(LocalizationMessages.WSP_0005_UNEXPECTED_POLICY_ELEMENT_FOUND_IN_ASSERTION_PARAM(localPolicyAssertion))));
      }
      if (localPolicyAssertion.hasNestedAssertions()) {
        translate(localModelNode, localPolicyAssertion.getNestedAssertionsIterator());
      }
    }
  }
  
  protected static class PolicySourceModelCreator
  {
    protected PolicySourceModelCreator() {}
    
    protected PolicySourceModel create(Policy paramPolicy)
    {
      return PolicySourceModel.createPolicySourceModel(paramPolicy.getNamespaceVersion(), paramPolicy.getId(), paramPolicy.getName());
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\policy\sourcemodel\PolicyModelGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */