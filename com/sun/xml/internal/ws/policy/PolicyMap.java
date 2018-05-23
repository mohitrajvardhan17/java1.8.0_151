package com.sun.xml.internal.ws.policy;

import com.sun.xml.internal.ws.policy.privateutil.LocalizationMessages;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.xml.namespace.QName;

public final class PolicyMap
  implements Iterable<Policy>
{
  private static final PolicyLogger LOGGER = PolicyLogger.getLogger(PolicyMap.class);
  private static final PolicyMapKeyHandler serviceKeyHandler = new PolicyMapKeyHandler()
  {
    public boolean areEqual(PolicyMapKey paramAnonymousPolicyMapKey1, PolicyMapKey paramAnonymousPolicyMapKey2)
    {
      return paramAnonymousPolicyMapKey1.getService().equals(paramAnonymousPolicyMapKey2.getService());
    }
    
    public int generateHashCode(PolicyMapKey paramAnonymousPolicyMapKey)
    {
      int i = 17;
      i = 37 * i + paramAnonymousPolicyMapKey.getService().hashCode();
      return i;
    }
  };
  private static final PolicyMapKeyHandler endpointKeyHandler = new PolicyMapKeyHandler()
  {
    public boolean areEqual(PolicyMapKey paramAnonymousPolicyMapKey1, PolicyMapKey paramAnonymousPolicyMapKey2)
    {
      boolean bool = true;
      bool = (bool) && (paramAnonymousPolicyMapKey1.getService().equals(paramAnonymousPolicyMapKey2.getService()));
      bool = (bool) && (paramAnonymousPolicyMapKey1.getPort() == null ? paramAnonymousPolicyMapKey2.getPort() == null : paramAnonymousPolicyMapKey1.getPort().equals(paramAnonymousPolicyMapKey2.getPort()));
      return bool;
    }
    
    public int generateHashCode(PolicyMapKey paramAnonymousPolicyMapKey)
    {
      int i = 17;
      i = 37 * i + paramAnonymousPolicyMapKey.getService().hashCode();
      i = 37 * i + (paramAnonymousPolicyMapKey.getPort() == null ? 0 : paramAnonymousPolicyMapKey.getPort().hashCode());
      return i;
    }
  };
  private static final PolicyMapKeyHandler operationAndInputOutputMessageKeyHandler = new PolicyMapKeyHandler()
  {
    public boolean areEqual(PolicyMapKey paramAnonymousPolicyMapKey1, PolicyMapKey paramAnonymousPolicyMapKey2)
    {
      boolean bool = true;
      bool = (bool) && (paramAnonymousPolicyMapKey1.getService().equals(paramAnonymousPolicyMapKey2.getService()));
      bool = (bool) && (paramAnonymousPolicyMapKey1.getPort() == null ? paramAnonymousPolicyMapKey2.getPort() == null : paramAnonymousPolicyMapKey1.getPort().equals(paramAnonymousPolicyMapKey2.getPort()));
      bool = (bool) && (paramAnonymousPolicyMapKey1.getOperation() == null ? paramAnonymousPolicyMapKey2.getOperation() == null : paramAnonymousPolicyMapKey1.getOperation().equals(paramAnonymousPolicyMapKey2.getOperation()));
      return bool;
    }
    
    public int generateHashCode(PolicyMapKey paramAnonymousPolicyMapKey)
    {
      int i = 17;
      i = 37 * i + paramAnonymousPolicyMapKey.getService().hashCode();
      i = 37 * i + (paramAnonymousPolicyMapKey.getPort() == null ? 0 : paramAnonymousPolicyMapKey.getPort().hashCode());
      i = 37 * i + (paramAnonymousPolicyMapKey.getOperation() == null ? 0 : paramAnonymousPolicyMapKey.getOperation().hashCode());
      return i;
    }
  };
  private static final PolicyMapKeyHandler faultMessageHandler = new PolicyMapKeyHandler()
  {
    public boolean areEqual(PolicyMapKey paramAnonymousPolicyMapKey1, PolicyMapKey paramAnonymousPolicyMapKey2)
    {
      boolean bool = true;
      bool = (bool) && (paramAnonymousPolicyMapKey1.getService().equals(paramAnonymousPolicyMapKey2.getService()));
      bool = (bool) && (paramAnonymousPolicyMapKey1.getPort() == null ? paramAnonymousPolicyMapKey2.getPort() == null : paramAnonymousPolicyMapKey1.getPort().equals(paramAnonymousPolicyMapKey2.getPort()));
      bool = (bool) && (paramAnonymousPolicyMapKey1.getOperation() == null ? paramAnonymousPolicyMapKey2.getOperation() == null : paramAnonymousPolicyMapKey1.getOperation().equals(paramAnonymousPolicyMapKey2.getOperation()));
      bool = (bool) && (paramAnonymousPolicyMapKey1.getFaultMessage() == null ? paramAnonymousPolicyMapKey2.getFaultMessage() == null : paramAnonymousPolicyMapKey1.getFaultMessage().equals(paramAnonymousPolicyMapKey2.getFaultMessage()));
      return bool;
    }
    
    public int generateHashCode(PolicyMapKey paramAnonymousPolicyMapKey)
    {
      int i = 17;
      i = 37 * i + paramAnonymousPolicyMapKey.getService().hashCode();
      i = 37 * i + (paramAnonymousPolicyMapKey.getPort() == null ? 0 : paramAnonymousPolicyMapKey.getPort().hashCode());
      i = 37 * i + (paramAnonymousPolicyMapKey.getOperation() == null ? 0 : paramAnonymousPolicyMapKey.getOperation().hashCode());
      i = 37 * i + (paramAnonymousPolicyMapKey.getFaultMessage() == null ? 0 : paramAnonymousPolicyMapKey.getFaultMessage().hashCode());
      return i;
    }
  };
  private static final PolicyMerger merger = PolicyMerger.getMerger();
  private final ScopeMap serviceMap = new ScopeMap(merger, serviceKeyHandler);
  private final ScopeMap endpointMap = new ScopeMap(merger, endpointKeyHandler);
  private final ScopeMap operationMap = new ScopeMap(merger, operationAndInputOutputMessageKeyHandler);
  private final ScopeMap inputMessageMap = new ScopeMap(merger, operationAndInputOutputMessageKeyHandler);
  private final ScopeMap outputMessageMap = new ScopeMap(merger, operationAndInputOutputMessageKeyHandler);
  private final ScopeMap faultMessageMap = new ScopeMap(merger, faultMessageHandler);
  
  private PolicyMap() {}
  
  public static PolicyMap createPolicyMap(Collection<? extends PolicyMapMutator> paramCollection)
  {
    PolicyMap localPolicyMap = new PolicyMap();
    if ((paramCollection != null) && (!paramCollection.isEmpty()))
    {
      Iterator localIterator = paramCollection.iterator();
      while (localIterator.hasNext())
      {
        PolicyMapMutator localPolicyMapMutator = (PolicyMapMutator)localIterator.next();
        localPolicyMapMutator.connect(localPolicyMap);
      }
    }
    return localPolicyMap;
  }
  
  public Policy getServiceEffectivePolicy(PolicyMapKey paramPolicyMapKey)
    throws PolicyException
  {
    return serviceMap.getEffectivePolicy(paramPolicyMapKey);
  }
  
  public Policy getEndpointEffectivePolicy(PolicyMapKey paramPolicyMapKey)
    throws PolicyException
  {
    return endpointMap.getEffectivePolicy(paramPolicyMapKey);
  }
  
  public Policy getOperationEffectivePolicy(PolicyMapKey paramPolicyMapKey)
    throws PolicyException
  {
    return operationMap.getEffectivePolicy(paramPolicyMapKey);
  }
  
  public Policy getInputMessageEffectivePolicy(PolicyMapKey paramPolicyMapKey)
    throws PolicyException
  {
    return inputMessageMap.getEffectivePolicy(paramPolicyMapKey);
  }
  
  public Policy getOutputMessageEffectivePolicy(PolicyMapKey paramPolicyMapKey)
    throws PolicyException
  {
    return outputMessageMap.getEffectivePolicy(paramPolicyMapKey);
  }
  
  public Policy getFaultMessageEffectivePolicy(PolicyMapKey paramPolicyMapKey)
    throws PolicyException
  {
    return faultMessageMap.getEffectivePolicy(paramPolicyMapKey);
  }
  
  public Collection<PolicyMapKey> getAllServiceScopeKeys()
  {
    return serviceMap.getAllKeys();
  }
  
  public Collection<PolicyMapKey> getAllEndpointScopeKeys()
  {
    return endpointMap.getAllKeys();
  }
  
  public Collection<PolicyMapKey> getAllOperationScopeKeys()
  {
    return operationMap.getAllKeys();
  }
  
  public Collection<PolicyMapKey> getAllInputMessageScopeKeys()
  {
    return inputMessageMap.getAllKeys();
  }
  
  public Collection<PolicyMapKey> getAllOutputMessageScopeKeys()
  {
    return outputMessageMap.getAllKeys();
  }
  
  public Collection<PolicyMapKey> getAllFaultMessageScopeKeys()
  {
    return faultMessageMap.getAllKeys();
  }
  
  void putSubject(ScopeType paramScopeType, PolicyMapKey paramPolicyMapKey, PolicySubject paramPolicySubject)
  {
    switch (paramScopeType)
    {
    case SERVICE: 
      serviceMap.putSubject(paramPolicyMapKey, paramPolicySubject);
      break;
    case ENDPOINT: 
      endpointMap.putSubject(paramPolicyMapKey, paramPolicySubject);
      break;
    case OPERATION: 
      operationMap.putSubject(paramPolicyMapKey, paramPolicySubject);
      break;
    case INPUT_MESSAGE: 
      inputMessageMap.putSubject(paramPolicyMapKey, paramPolicySubject);
      break;
    case OUTPUT_MESSAGE: 
      outputMessageMap.putSubject(paramPolicyMapKey, paramPolicySubject);
      break;
    case FAULT_MESSAGE: 
      faultMessageMap.putSubject(paramPolicyMapKey, paramPolicySubject);
      break;
    default: 
      throw ((IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0002_UNRECOGNIZED_SCOPE_TYPE(paramScopeType))));
    }
  }
  
  void setNewEffectivePolicyForScope(ScopeType paramScopeType, PolicyMapKey paramPolicyMapKey, Policy paramPolicy)
    throws IllegalArgumentException
  {
    if ((paramScopeType == null) || (paramPolicyMapKey == null) || (paramPolicy == null)) {
      throw ((IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0062_INPUT_PARAMS_MUST_NOT_BE_NULL())));
    }
    switch (paramScopeType)
    {
    case SERVICE: 
      serviceMap.setNewEffectivePolicy(paramPolicyMapKey, paramPolicy);
      break;
    case ENDPOINT: 
      endpointMap.setNewEffectivePolicy(paramPolicyMapKey, paramPolicy);
      break;
    case OPERATION: 
      operationMap.setNewEffectivePolicy(paramPolicyMapKey, paramPolicy);
      break;
    case INPUT_MESSAGE: 
      inputMessageMap.setNewEffectivePolicy(paramPolicyMapKey, paramPolicy);
      break;
    case OUTPUT_MESSAGE: 
      outputMessageMap.setNewEffectivePolicy(paramPolicyMapKey, paramPolicy);
      break;
    case FAULT_MESSAGE: 
      faultMessageMap.setNewEffectivePolicy(paramPolicyMapKey, paramPolicy);
      break;
    default: 
      throw ((IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0002_UNRECOGNIZED_SCOPE_TYPE(paramScopeType))));
    }
  }
  
  public Collection<PolicySubject> getPolicySubjects()
  {
    LinkedList localLinkedList = new LinkedList();
    addSubjects(localLinkedList, serviceMap);
    addSubjects(localLinkedList, endpointMap);
    addSubjects(localLinkedList, operationMap);
    addSubjects(localLinkedList, inputMessageMap);
    addSubjects(localLinkedList, outputMessageMap);
    addSubjects(localLinkedList, faultMessageMap);
    return localLinkedList;
  }
  
  public boolean isInputMessageSubject(PolicySubject paramPolicySubject)
  {
    Iterator localIterator = inputMessageMap.getStoredScopes().iterator();
    while (localIterator.hasNext())
    {
      PolicyScope localPolicyScope = (PolicyScope)localIterator.next();
      if (localPolicyScope.getPolicySubjects().contains(paramPolicySubject)) {
        return true;
      }
    }
    return false;
  }
  
  public boolean isOutputMessageSubject(PolicySubject paramPolicySubject)
  {
    Iterator localIterator = outputMessageMap.getStoredScopes().iterator();
    while (localIterator.hasNext())
    {
      PolicyScope localPolicyScope = (PolicyScope)localIterator.next();
      if (localPolicyScope.getPolicySubjects().contains(paramPolicySubject)) {
        return true;
      }
    }
    return false;
  }
  
  public boolean isFaultMessageSubject(PolicySubject paramPolicySubject)
  {
    Iterator localIterator = faultMessageMap.getStoredScopes().iterator();
    while (localIterator.hasNext())
    {
      PolicyScope localPolicyScope = (PolicyScope)localIterator.next();
      if (localPolicyScope.getPolicySubjects().contains(paramPolicySubject)) {
        return true;
      }
    }
    return false;
  }
  
  public boolean isEmpty()
  {
    return (serviceMap.isEmpty()) && (endpointMap.isEmpty()) && (operationMap.isEmpty()) && (inputMessageMap.isEmpty()) && (outputMessageMap.isEmpty()) && (faultMessageMap.isEmpty());
  }
  
  private void addSubjects(Collection<PolicySubject> paramCollection, ScopeMap paramScopeMap)
  {
    Iterator localIterator = paramScopeMap.getStoredScopes().iterator();
    while (localIterator.hasNext())
    {
      PolicyScope localPolicyScope = (PolicyScope)localIterator.next();
      Collection localCollection = localPolicyScope.getPolicySubjects();
      paramCollection.addAll(localCollection);
    }
  }
  
  public static PolicyMapKey createWsdlServiceScopeKey(QName paramQName)
    throws IllegalArgumentException
  {
    if (paramQName == null) {
      throw ((IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0031_SERVICE_PARAM_MUST_NOT_BE_NULL())));
    }
    return new PolicyMapKey(paramQName, null, null, serviceKeyHandler);
  }
  
  public static PolicyMapKey createWsdlEndpointScopeKey(QName paramQName1, QName paramQName2)
    throws IllegalArgumentException
  {
    if ((paramQName1 == null) || (paramQName2 == null)) {
      throw ((IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0033_SERVICE_AND_PORT_PARAM_MUST_NOT_BE_NULL(paramQName1, paramQName2))));
    }
    return new PolicyMapKey(paramQName1, paramQName2, null, endpointKeyHandler);
  }
  
  public static PolicyMapKey createWsdlOperationScopeKey(QName paramQName1, QName paramQName2, QName paramQName3)
    throws IllegalArgumentException
  {
    return createOperationOrInputOutputMessageKey(paramQName1, paramQName2, paramQName3);
  }
  
  public static PolicyMapKey createWsdlMessageScopeKey(QName paramQName1, QName paramQName2, QName paramQName3)
    throws IllegalArgumentException
  {
    return createOperationOrInputOutputMessageKey(paramQName1, paramQName2, paramQName3);
  }
  
  public static PolicyMapKey createWsdlFaultMessageScopeKey(QName paramQName1, QName paramQName2, QName paramQName3, QName paramQName4)
    throws IllegalArgumentException
  {
    if ((paramQName1 == null) || (paramQName2 == null) || (paramQName3 == null) || (paramQName4 == null)) {
      throw ((IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0030_SERVICE_PORT_OPERATION_FAULT_MSG_PARAM_MUST_NOT_BE_NULL(paramQName1, paramQName2, paramQName3, paramQName4))));
    }
    return new PolicyMapKey(paramQName1, paramQName2, paramQName3, paramQName4, faultMessageHandler);
  }
  
  private static PolicyMapKey createOperationOrInputOutputMessageKey(QName paramQName1, QName paramQName2, QName paramQName3)
  {
    if ((paramQName1 == null) || (paramQName2 == null) || (paramQName3 == null)) {
      throw ((IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0029_SERVICE_PORT_OPERATION_PARAM_MUST_NOT_BE_NULL(paramQName1, paramQName2, paramQName3))));
    }
    return new PolicyMapKey(paramQName1, paramQName2, paramQName3, operationAndInputOutputMessageKeyHandler);
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    if (null != serviceMap) {
      localStringBuffer.append("\nServiceMap=").append(serviceMap);
    }
    if (null != endpointMap) {
      localStringBuffer.append("\nEndpointMap=").append(endpointMap);
    }
    if (null != operationMap) {
      localStringBuffer.append("\nOperationMap=").append(operationMap);
    }
    if (null != inputMessageMap) {
      localStringBuffer.append("\nInputMessageMap=").append(inputMessageMap);
    }
    if (null != outputMessageMap) {
      localStringBuffer.append("\nOutputMessageMap=").append(outputMessageMap);
    }
    if (null != faultMessageMap) {
      localStringBuffer.append("\nFaultMessageMap=").append(faultMessageMap);
    }
    return localStringBuffer.toString();
  }
  
  public Iterator<Policy> iterator()
  {
    new Iterator()
    {
      private final Iterator<Iterator<Policy>> mainIterator;
      private Iterator<Policy> currentScopeIterator;
      
      public boolean hasNext()
      {
        while (!currentScopeIterator.hasNext()) {
          if (mainIterator.hasNext()) {
            currentScopeIterator = ((Iterator)mainIterator.next());
          } else {
            return false;
          }
        }
        return true;
      }
      
      public Policy next()
      {
        if (hasNext()) {
          return (Policy)currentScopeIterator.next();
        }
        throw ((NoSuchElementException)PolicyMap.LOGGER.logSevereException(new NoSuchElementException(LocalizationMessages.WSP_0054_NO_MORE_ELEMS_IN_POLICY_MAP())));
      }
      
      public void remove()
      {
        throw ((UnsupportedOperationException)PolicyMap.LOGGER.logSevereException(new UnsupportedOperationException(LocalizationMessages.WSP_0034_REMOVE_OPERATION_NOT_SUPPORTED())));
      }
    };
  }
  
  private static final class ScopeMap
    implements Iterable<Policy>
  {
    private final Map<PolicyMapKey, PolicyScope> internalMap = new HashMap();
    private final PolicyMapKeyHandler scopeKeyHandler;
    private final PolicyMerger merger;
    
    ScopeMap(PolicyMerger paramPolicyMerger, PolicyMapKeyHandler paramPolicyMapKeyHandler)
    {
      merger = paramPolicyMerger;
      scopeKeyHandler = paramPolicyMapKeyHandler;
    }
    
    Policy getEffectivePolicy(PolicyMapKey paramPolicyMapKey)
      throws PolicyException
    {
      PolicyScope localPolicyScope = (PolicyScope)internalMap.get(createLocalCopy(paramPolicyMapKey));
      return localPolicyScope == null ? null : localPolicyScope.getEffectivePolicy(merger);
    }
    
    void putSubject(PolicyMapKey paramPolicyMapKey, PolicySubject paramPolicySubject)
    {
      PolicyMapKey localPolicyMapKey = createLocalCopy(paramPolicyMapKey);
      PolicyScope localPolicyScope = (PolicyScope)internalMap.get(localPolicyMapKey);
      if (localPolicyScope == null)
      {
        LinkedList localLinkedList = new LinkedList();
        localLinkedList.add(paramPolicySubject);
        internalMap.put(localPolicyMapKey, new PolicyScope(localLinkedList));
      }
      else
      {
        localPolicyScope.attach(paramPolicySubject);
      }
    }
    
    void setNewEffectivePolicy(PolicyMapKey paramPolicyMapKey, Policy paramPolicy)
    {
      PolicySubject localPolicySubject = new PolicySubject(paramPolicyMapKey, paramPolicy);
      PolicyMapKey localPolicyMapKey = createLocalCopy(paramPolicyMapKey);
      PolicyScope localPolicyScope = (PolicyScope)internalMap.get(localPolicyMapKey);
      if (localPolicyScope == null)
      {
        LinkedList localLinkedList = new LinkedList();
        localLinkedList.add(localPolicySubject);
        internalMap.put(localPolicyMapKey, new PolicyScope(localLinkedList));
      }
      else
      {
        localPolicyScope.dettachAllSubjects();
        localPolicyScope.attach(localPolicySubject);
      }
    }
    
    Collection<PolicyScope> getStoredScopes()
    {
      return internalMap.values();
    }
    
    Set<PolicyMapKey> getAllKeys()
    {
      return internalMap.keySet();
    }
    
    private PolicyMapKey createLocalCopy(PolicyMapKey paramPolicyMapKey)
    {
      if (paramPolicyMapKey == null) {
        throw ((IllegalArgumentException)PolicyMap.LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0045_POLICY_MAP_KEY_MUST_NOT_BE_NULL())));
      }
      PolicyMapKey localPolicyMapKey = new PolicyMapKey(paramPolicyMapKey);
      localPolicyMapKey.setHandler(scopeKeyHandler);
      return localPolicyMapKey;
    }
    
    public Iterator<Policy> iterator()
    {
      new Iterator()
      {
        private final Iterator<PolicyMapKey> keysIterator = internalMap.keySet().iterator();
        
        public boolean hasNext()
        {
          return keysIterator.hasNext();
        }
        
        public Policy next()
        {
          PolicyMapKey localPolicyMapKey = (PolicyMapKey)keysIterator.next();
          try
          {
            return getEffectivePolicy(localPolicyMapKey);
          }
          catch (PolicyException localPolicyException)
          {
            throw ((IllegalStateException)PolicyMap.LOGGER.logSevereException(new IllegalStateException(LocalizationMessages.WSP_0069_EXCEPTION_WHILE_RETRIEVING_EFFECTIVE_POLICY_FOR_KEY(localPolicyMapKey), localPolicyException)));
          }
        }
        
        public void remove()
        {
          throw ((UnsupportedOperationException)PolicyMap.LOGGER.logSevereException(new UnsupportedOperationException(LocalizationMessages.WSP_0034_REMOVE_OPERATION_NOT_SUPPORTED())));
        }
      };
    }
    
    public boolean isEmpty()
    {
      return internalMap.isEmpty();
    }
    
    public String toString()
    {
      return internalMap.toString();
    }
  }
  
  static enum ScopeType
  {
    SERVICE,  ENDPOINT,  OPERATION,  INPUT_MESSAGE,  OUTPUT_MESSAGE,  FAULT_MESSAGE;
    
    private ScopeType() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\policy\PolicyMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */