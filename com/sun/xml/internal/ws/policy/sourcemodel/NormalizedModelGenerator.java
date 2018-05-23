package com.sun.xml.internal.ws.policy.sourcemodel;

import com.sun.xml.internal.ws.policy.AssertionSet;
import com.sun.xml.internal.ws.policy.NestedPolicy;
import com.sun.xml.internal.ws.policy.Policy;
import com.sun.xml.internal.ws.policy.PolicyAssertion;
import com.sun.xml.internal.ws.policy.PolicyException;
import com.sun.xml.internal.ws.policy.privateutil.LocalizationMessages;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;
import java.util.Iterator;

class NormalizedModelGenerator
  extends PolicyModelGenerator
{
  private static final PolicyLogger LOGGER = PolicyLogger.getLogger(NormalizedModelGenerator.class);
  private final PolicyModelGenerator.PolicySourceModelCreator sourceModelCreator;
  
  NormalizedModelGenerator(PolicyModelGenerator.PolicySourceModelCreator paramPolicySourceModelCreator)
  {
    sourceModelCreator = paramPolicySourceModelCreator;
  }
  
  public PolicySourceModel translate(Policy paramPolicy)
    throws PolicyException
  {
    LOGGER.entering(new Object[] { paramPolicy });
    PolicySourceModel localPolicySourceModel = null;
    if (paramPolicy == null)
    {
      LOGGER.fine(LocalizationMessages.WSP_0047_POLICY_IS_NULL_RETURNING());
    }
    else
    {
      localPolicySourceModel = sourceModelCreator.create(paramPolicy);
      ModelNode localModelNode1 = localPolicySourceModel.getRootNode();
      ModelNode localModelNode2 = localModelNode1.createChildExactlyOneNode();
      Iterator localIterator1 = paramPolicy.iterator();
      while (localIterator1.hasNext())
      {
        AssertionSet localAssertionSet = (AssertionSet)localIterator1.next();
        ModelNode localModelNode3 = localModelNode2.createChildAllNode();
        Iterator localIterator2 = localAssertionSet.iterator();
        while (localIterator2.hasNext())
        {
          PolicyAssertion localPolicyAssertion = (PolicyAssertion)localIterator2.next();
          AssertionData localAssertionData = AssertionData.createAssertionData(localPolicyAssertion.getName(), localPolicyAssertion.getValue(), localPolicyAssertion.getAttributes(), localPolicyAssertion.isOptional(), localPolicyAssertion.isIgnorable());
          ModelNode localModelNode4 = localModelNode3.createChildAssertionNode(localAssertionData);
          if (localPolicyAssertion.hasNestedPolicy()) {
            translate(localModelNode4, localPolicyAssertion.getNestedPolicy());
          }
          if (localPolicyAssertion.hasParameters()) {
            translate(localModelNode4, localPolicyAssertion.getParametersIterator());
          }
        }
      }
    }
    LOGGER.exiting(localPolicySourceModel);
    return localPolicySourceModel;
  }
  
  protected ModelNode translate(ModelNode paramModelNode, NestedPolicy paramNestedPolicy)
  {
    ModelNode localModelNode1 = paramModelNode.createChildPolicyNode();
    ModelNode localModelNode2 = localModelNode1.createChildExactlyOneNode();
    AssertionSet localAssertionSet = paramNestedPolicy.getAssertionSet();
    ModelNode localModelNode3 = localModelNode2.createChildAllNode();
    translate(localModelNode3, localAssertionSet);
    return localModelNode1;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\policy\sourcemodel\NormalizedModelGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */