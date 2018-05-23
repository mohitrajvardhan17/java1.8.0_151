package com.sun.xml.internal.ws.policy.jaxws;

import com.sun.xml.internal.ws.policy.PolicyException;
import com.sun.xml.internal.ws.policy.PolicyMap;
import com.sun.xml.internal.ws.policy.PolicyMapExtender;
import com.sun.xml.internal.ws.policy.PolicyMapMutator;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

class PolicyMapBuilder
{
  private List<BuilderHandler> policyBuilders = new LinkedList();
  
  PolicyMapBuilder() {}
  
  void registerHandler(BuilderHandler paramBuilderHandler)
  {
    if (null != paramBuilderHandler) {
      policyBuilders.add(paramBuilderHandler);
    }
  }
  
  PolicyMap getPolicyMap(PolicyMapMutator... paramVarArgs)
    throws PolicyException
  {
    return getNewPolicyMap(paramVarArgs);
  }
  
  private PolicyMap getNewPolicyMap(PolicyMapMutator... paramVarArgs)
    throws PolicyException
  {
    HashSet localHashSet = new HashSet();
    PolicyMapExtender localPolicyMapExtender = PolicyMapExtender.createPolicyMapExtender();
    localHashSet.add(localPolicyMapExtender);
    if (null != paramVarArgs) {
      localHashSet.addAll(Arrays.asList(paramVarArgs));
    }
    PolicyMap localPolicyMap = PolicyMap.createPolicyMap(localHashSet);
    Iterator localIterator = policyBuilders.iterator();
    while (localIterator.hasNext())
    {
      BuilderHandler localBuilderHandler = (BuilderHandler)localIterator.next();
      localBuilderHandler.populate(localPolicyMapExtender);
    }
    return localPolicyMap;
  }
  
  void unregisterAll()
  {
    policyBuilders = null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\policy\jaxws\PolicyMapBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */