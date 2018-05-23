package com.sun.xml.internal.ws.policy.subject;

import com.sun.xml.internal.ws.policy.PolicyMap;
import com.sun.xml.internal.ws.policy.PolicyMapKey;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;
import javax.xml.namespace.QName;

public class PolicyMapKeyConverter
{
  private static final PolicyLogger LOGGER = PolicyLogger.getLogger(PolicyMapKeyConverter.class);
  private final QName serviceName;
  private final QName portName;
  
  public PolicyMapKeyConverter(QName paramQName1, QName paramQName2)
  {
    serviceName = paramQName1;
    portName = paramQName2;
  }
  
  public PolicyMapKey getPolicyMapKey(WsdlBindingSubject paramWsdlBindingSubject)
  {
    LOGGER.entering(new Object[] { paramWsdlBindingSubject });
    PolicyMapKey localPolicyMapKey = null;
    if (paramWsdlBindingSubject.isBindingSubject()) {
      localPolicyMapKey = PolicyMap.createWsdlEndpointScopeKey(serviceName, portName);
    } else if (paramWsdlBindingSubject.isBindingOperationSubject()) {
      localPolicyMapKey = PolicyMap.createWsdlOperationScopeKey(serviceName, portName, paramWsdlBindingSubject.getName());
    } else if (paramWsdlBindingSubject.isBindingMessageSubject()) {
      if (paramWsdlBindingSubject.getMessageType() == WsdlBindingSubject.WsdlMessageType.FAULT) {
        localPolicyMapKey = PolicyMap.createWsdlFaultMessageScopeKey(serviceName, portName, paramWsdlBindingSubject.getParent().getName(), paramWsdlBindingSubject.getName());
      } else {
        localPolicyMapKey = PolicyMap.createWsdlMessageScopeKey(serviceName, portName, paramWsdlBindingSubject.getParent().getName());
      }
    }
    LOGGER.exiting(localPolicyMapKey);
    return localPolicyMapKey;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\policy\subject\PolicyMapKeyConverter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */