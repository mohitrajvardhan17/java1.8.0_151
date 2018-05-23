package com.sun.xml.internal.ws.policy.jaxws;

import com.sun.xml.internal.ws.api.policy.ModelTranslator;
import com.sun.xml.internal.ws.policy.Policy;
import com.sun.xml.internal.ws.policy.PolicyException;
import com.sun.xml.internal.ws.policy.PolicyMapExtender;
import com.sun.xml.internal.ws.policy.PolicySubject;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;
import com.sun.xml.internal.ws.policy.sourcemodel.PolicySourceModel;
import com.sun.xml.internal.ws.resources.PolicyMessages;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

abstract class BuilderHandler
{
  private static final PolicyLogger LOGGER = PolicyLogger.getLogger(BuilderHandler.class);
  Map<String, PolicySourceModel> policyStore;
  Collection<String> policyURIs;
  Object policySubject;
  
  BuilderHandler(Collection<String> paramCollection, Map<String, PolicySourceModel> paramMap, Object paramObject)
  {
    policyStore = paramMap;
    policyURIs = paramCollection;
    policySubject = paramObject;
  }
  
  final void populate(PolicyMapExtender paramPolicyMapExtender)
    throws PolicyException
  {
    if (null == paramPolicyMapExtender) {
      throw ((PolicyException)LOGGER.logSevereException(new PolicyException(PolicyMessages.WSP_1006_POLICY_MAP_EXTENDER_CAN_NOT_BE_NULL())));
    }
    doPopulate(paramPolicyMapExtender);
  }
  
  protected abstract void doPopulate(PolicyMapExtender paramPolicyMapExtender)
    throws PolicyException;
  
  final Collection<Policy> getPolicies()
    throws PolicyException
  {
    if (null == policyURIs) {
      throw ((PolicyException)LOGGER.logSevereException(new PolicyException(PolicyMessages.WSP_1004_POLICY_URIS_CAN_NOT_BE_NULL())));
    }
    if (null == policyStore) {
      throw ((PolicyException)LOGGER.logSevereException(new PolicyException(PolicyMessages.WSP_1010_NO_POLICIES_DEFINED())));
    }
    ArrayList localArrayList = new ArrayList(policyURIs.size());
    Iterator localIterator = policyURIs.iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      PolicySourceModel localPolicySourceModel = (PolicySourceModel)policyStore.get(str);
      if (localPolicySourceModel == null) {
        throw ((PolicyException)LOGGER.logSevereException(new PolicyException(PolicyMessages.WSP_1005_POLICY_REFERENCE_DOES_NOT_EXIST(str))));
      }
      localArrayList.add(ModelTranslator.getTranslator().translate(localPolicySourceModel));
    }
    return localArrayList;
  }
  
  final Collection<PolicySubject> getPolicySubjects()
    throws PolicyException
  {
    Collection localCollection = getPolicies();
    ArrayList localArrayList = new ArrayList(localCollection.size());
    Iterator localIterator = localCollection.iterator();
    while (localIterator.hasNext())
    {
      Policy localPolicy = (Policy)localIterator.next();
      localArrayList.add(new PolicySubject(policySubject, localPolicy));
    }
    return localArrayList;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\policy\jaxws\BuilderHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */