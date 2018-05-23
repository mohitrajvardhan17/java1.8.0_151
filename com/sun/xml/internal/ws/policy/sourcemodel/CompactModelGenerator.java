package com.sun.xml.internal.ws.policy.sourcemodel;

import com.sun.xml.internal.ws.policy.AssertionSet;
import com.sun.xml.internal.ws.policy.NestedPolicy;
import com.sun.xml.internal.ws.policy.Policy;
import com.sun.xml.internal.ws.policy.PolicyAssertion;
import com.sun.xml.internal.ws.policy.PolicyException;
import com.sun.xml.internal.ws.policy.privateutil.LocalizationMessages;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;
import java.util.Iterator;

class CompactModelGenerator
  extends PolicyModelGenerator
{
  private static final PolicyLogger LOGGER = PolicyLogger.getLogger(CompactModelGenerator.class);
  private final PolicyModelGenerator.PolicySourceModelCreator sourceModelCreator;
  
  CompactModelGenerator(PolicyModelGenerator.PolicySourceModelCreator paramPolicySourceModelCreator)
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
      int i = paramPolicy.getNumberOfAssertionSets();
      if (i > 1) {
        localModelNode1 = localModelNode1.createChildExactlyOneNode();
      }
      ModelNode localModelNode2 = localModelNode1;
      Iterator localIterator1 = paramPolicy.iterator();
      while (localIterator1.hasNext())
      {
        AssertionSet localAssertionSet = (AssertionSet)localIterator1.next();
        if (i > 1) {
          localModelNode2 = localModelNode1.createChildAllNode();
        }
        Iterator localIterator2 = localAssertionSet.iterator();
        while (localIterator2.hasNext())
        {
          PolicyAssertion localPolicyAssertion = (PolicyAssertion)localIterator2.next();
          AssertionData localAssertionData = AssertionData.createAssertionData(localPolicyAssertion.getName(), localPolicyAssertion.getValue(), localPolicyAssertion.getAttributes(), localPolicyAssertion.isOptional(), localPolicyAssertion.isIgnorable());
          ModelNode localModelNode3 = localModelNode2.createChildAssertionNode(localAssertionData);
          if (localPolicyAssertion.hasNestedPolicy()) {
            translate(localModelNode3, localPolicyAssertion.getNestedPolicy());
          }
          if (localPolicyAssertion.hasParameters()) {
            translate(localModelNode3, localPolicyAssertion.getParametersIterator());
          }
        }
      }
    }
    LOGGER.exiting(localPolicySourceModel);
    return localPolicySourceModel;
  }
  
  protected ModelNode translate(ModelNode paramModelNode, NestedPolicy paramNestedPolicy)
  {
    ModelNode localModelNode = paramModelNode.createChildPolicyNode();
    AssertionSet localAssertionSet = paramNestedPolicy.getAssertionSet();
    translate(localModelNode, localAssertionSet);
    return localModelNode;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\policy\sourcemodel\CompactModelGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */