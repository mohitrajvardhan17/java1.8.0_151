package com.sun.xml.internal.ws.policy.jaxws;

import com.sun.xml.internal.ws.policy.PolicyException;
import com.sun.xml.internal.ws.policy.PolicyMap;
import com.sun.xml.internal.ws.policy.PolicyMapExtender;
import com.sun.xml.internal.ws.policy.PolicyMapKey;
import com.sun.xml.internal.ws.policy.PolicySubject;
import com.sun.xml.internal.ws.policy.sourcemodel.PolicySourceModel;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import javax.xml.namespace.QName;

final class BuilderHandlerMessageScope
  extends BuilderHandler
{
  private final QName service;
  private final QName port;
  private final QName operation;
  private final QName message;
  private final Scope scope;
  
  BuilderHandlerMessageScope(Collection<String> paramCollection, Map<String, PolicySourceModel> paramMap, Object paramObject, Scope paramScope, QName paramQName1, QName paramQName2, QName paramQName3, QName paramQName4)
  {
    super(paramCollection, paramMap, paramObject);
    service = paramQName1;
    port = paramQName2;
    operation = paramQName3;
    scope = paramScope;
    message = paramQName4;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof BuilderHandlerMessageScope)) {
      return false;
    }
    BuilderHandlerMessageScope localBuilderHandlerMessageScope = (BuilderHandlerMessageScope)paramObject;
    boolean bool = true;
    bool = (bool) && (policySubject == null ? policySubject == null : policySubject.equals(policySubject));
    bool = (bool) && (scope == null ? scope == null : scope.equals(scope));
    bool = (bool) && (message == null ? message == null : message.equals(message));
    if (scope != Scope.FaultMessageScope)
    {
      bool = (bool) && (service == null ? service == null : service.equals(service));
      bool = (bool) && (port == null ? port == null : port.equals(port));
      bool = (bool) && (operation == null ? operation == null : operation.equals(operation));
    }
    return bool;
  }
  
  public int hashCode()
  {
    int i = 19;
    i = 31 * i + (policySubject == null ? 0 : policySubject.hashCode());
    i = 31 * i + (message == null ? 0 : message.hashCode());
    i = 31 * i + (scope == null ? 0 : scope.hashCode());
    if (scope != Scope.FaultMessageScope)
    {
      i = 31 * i + (service == null ? 0 : service.hashCode());
      i = 31 * i + (port == null ? 0 : port.hashCode());
      i = 31 * i + (operation == null ? 0 : operation.hashCode());
    }
    return i;
  }
  
  protected void doPopulate(PolicyMapExtender paramPolicyMapExtender)
    throws PolicyException
  {
    PolicyMapKey localPolicyMapKey;
    if (Scope.FaultMessageScope == scope) {
      localPolicyMapKey = PolicyMap.createWsdlFaultMessageScopeKey(service, port, operation, message);
    } else {
      localPolicyMapKey = PolicyMap.createWsdlMessageScopeKey(service, port, operation);
    }
    Iterator localIterator;
    PolicySubject localPolicySubject;
    if (Scope.InputMessageScope == scope)
    {
      localIterator = getPolicySubjects().iterator();
      while (localIterator.hasNext())
      {
        localPolicySubject = (PolicySubject)localIterator.next();
        paramPolicyMapExtender.putInputMessageSubject(localPolicyMapKey, localPolicySubject);
      }
    }
    else if (Scope.OutputMessageScope == scope)
    {
      localIterator = getPolicySubjects().iterator();
      while (localIterator.hasNext())
      {
        localPolicySubject = (PolicySubject)localIterator.next();
        paramPolicyMapExtender.putOutputMessageSubject(localPolicyMapKey, localPolicySubject);
      }
    }
    else if (Scope.FaultMessageScope == scope)
    {
      localIterator = getPolicySubjects().iterator();
      while (localIterator.hasNext())
      {
        localPolicySubject = (PolicySubject)localIterator.next();
        paramPolicyMapExtender.putFaultMessageSubject(localPolicyMapKey, localPolicySubject);
      }
    }
  }
  
  static enum Scope
  {
    InputMessageScope,  OutputMessageScope,  FaultMessageScope;
    
    private Scope() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\policy\jaxws\BuilderHandlerMessageScope.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */