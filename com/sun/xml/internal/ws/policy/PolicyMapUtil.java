package com.sun.xml.internal.ws.policy;

import com.sun.xml.internal.ws.policy.privateutil.LocalizationMessages;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.internal.ws.policy.subject.PolicyMapKeyConverter;
import com.sun.xml.internal.ws.policy.subject.WsdlBindingSubject;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import javax.xml.namespace.QName;

public class PolicyMapUtil
{
  private static final PolicyLogger LOGGER = PolicyLogger.getLogger(PolicyMapUtil.class);
  private static final PolicyMerger MERGER = PolicyMerger.getMerger();
  
  private PolicyMapUtil() {}
  
  public static void rejectAlternatives(PolicyMap paramPolicyMap)
    throws PolicyException
  {
    Iterator localIterator = paramPolicyMap.iterator();
    while (localIterator.hasNext())
    {
      Policy localPolicy = (Policy)localIterator.next();
      if (localPolicy.getNumberOfAssertionSets() > 1) {
        throw ((PolicyException)LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0035_RECONFIGURE_ALTERNATIVES(localPolicy.getIdOrName()))));
      }
    }
  }
  
  public static void insertPolicies(PolicyMap paramPolicyMap, Collection<PolicySubject> paramCollection, QName paramQName1, QName paramQName2)
    throws PolicyException
  {
    LOGGER.entering(new Object[] { paramPolicyMap, paramCollection, paramQName1, paramQName2 });
    HashMap localHashMap = new HashMap();
    Object localObject1 = paramCollection.iterator();
    Object localObject3;
    Object localObject4;
    Object localObject5;
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (PolicySubject)((Iterator)localObject1).next();
      localObject3 = ((PolicySubject)localObject2).getSubject();
      if ((localObject3 instanceof WsdlBindingSubject))
      {
        localObject4 = (WsdlBindingSubject)localObject3;
        localObject5 = new LinkedList();
        ((Collection)localObject5).add(((PolicySubject)localObject2).getEffectivePolicy(MERGER));
        Collection localCollection = (Collection)localHashMap.put(localObject4, localObject5);
        if (localCollection != null) {
          ((Collection)localObject5).addAll(localCollection);
        }
      }
    }
    localObject1 = new PolicyMapKeyConverter(paramQName1, paramQName2);
    Object localObject2 = localHashMap.keySet().iterator();
    while (((Iterator)localObject2).hasNext())
    {
      localObject3 = (WsdlBindingSubject)((Iterator)localObject2).next();
      localObject4 = new PolicySubject(localObject3, (Collection)localHashMap.get(localObject3));
      localObject5 = ((PolicyMapKeyConverter)localObject1).getPolicyMapKey((WsdlBindingSubject)localObject3);
      if (((WsdlBindingSubject)localObject3).isBindingSubject()) {
        paramPolicyMap.putSubject(PolicyMap.ScopeType.ENDPOINT, (PolicyMapKey)localObject5, (PolicySubject)localObject4);
      } else if (((WsdlBindingSubject)localObject3).isBindingOperationSubject()) {
        paramPolicyMap.putSubject(PolicyMap.ScopeType.OPERATION, (PolicyMapKey)localObject5, (PolicySubject)localObject4);
      } else if (((WsdlBindingSubject)localObject3).isBindingMessageSubject()) {
        switch (localObject3.getMessageType())
        {
        case INPUT: 
          paramPolicyMap.putSubject(PolicyMap.ScopeType.INPUT_MESSAGE, (PolicyMapKey)localObject5, (PolicySubject)localObject4);
          break;
        case OUTPUT: 
          paramPolicyMap.putSubject(PolicyMap.ScopeType.OUTPUT_MESSAGE, (PolicyMapKey)localObject5, (PolicySubject)localObject4);
          break;
        case FAULT: 
          paramPolicyMap.putSubject(PolicyMap.ScopeType.FAULT_MESSAGE, (PolicyMapKey)localObject5, (PolicySubject)localObject4);
        }
      }
    }
    LOGGER.exiting();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\policy\PolicyMapUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */