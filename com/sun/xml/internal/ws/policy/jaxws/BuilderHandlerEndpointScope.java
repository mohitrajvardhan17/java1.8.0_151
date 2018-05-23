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

final class BuilderHandlerEndpointScope
  extends BuilderHandler
{
  private final QName service;
  private final QName port;
  
  BuilderHandlerEndpointScope(Collection<String> paramCollection, Map<String, PolicySourceModel> paramMap, Object paramObject, QName paramQName1, QName paramQName2)
  {
    super(paramCollection, paramMap, paramObject);
    service = paramQName1;
    port = paramQName2;
  }
  
  protected void doPopulate(PolicyMapExtender paramPolicyMapExtender)
    throws PolicyException
  {
    PolicyMapKey localPolicyMapKey = PolicyMap.createWsdlEndpointScopeKey(service, port);
    Iterator localIterator = getPolicySubjects().iterator();
    while (localIterator.hasNext())
    {
      PolicySubject localPolicySubject = (PolicySubject)localIterator.next();
      paramPolicyMapExtender.putEndpointSubject(localPolicyMapKey, localPolicySubject);
    }
  }
  
  public String toString()
  {
    return service.toString() + ":" + port.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\policy\jaxws\BuilderHandlerEndpointScope.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */