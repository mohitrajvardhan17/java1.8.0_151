package com.sun.xml.internal.ws.policy;

import com.sun.xml.internal.ws.policy.privateutil.LocalizationMessages;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;
import javax.xml.namespace.QName;

public final class PolicyMapKey
{
  private static final PolicyLogger LOGGER = PolicyLogger.getLogger(PolicyMapKey.class);
  private final QName service;
  private final QName port;
  private final QName operation;
  private final QName faultMessage;
  private PolicyMapKeyHandler handler;
  
  PolicyMapKey(QName paramQName1, QName paramQName2, QName paramQName3, PolicyMapKeyHandler paramPolicyMapKeyHandler)
  {
    this(paramQName1, paramQName2, paramQName3, null, paramPolicyMapKeyHandler);
  }
  
  PolicyMapKey(QName paramQName1, QName paramQName2, QName paramQName3, QName paramQName4, PolicyMapKeyHandler paramPolicyMapKeyHandler)
  {
    if (paramPolicyMapKeyHandler == null) {
      throw ((IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0046_POLICY_MAP_KEY_HANDLER_NOT_SET())));
    }
    service = paramQName1;
    port = paramQName2;
    operation = paramQName3;
    faultMessage = paramQName4;
    handler = paramPolicyMapKeyHandler;
  }
  
  PolicyMapKey(PolicyMapKey paramPolicyMapKey)
  {
    service = service;
    port = port;
    operation = operation;
    faultMessage = faultMessage;
    handler = handler;
  }
  
  public QName getOperation()
  {
    return operation;
  }
  
  public QName getPort()
  {
    return port;
  }
  
  public QName getService()
  {
    return service;
  }
  
  void setHandler(PolicyMapKeyHandler paramPolicyMapKeyHandler)
  {
    if (paramPolicyMapKeyHandler == null) {
      throw ((IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0046_POLICY_MAP_KEY_HANDLER_NOT_SET())));
    }
    handler = paramPolicyMapKeyHandler;
  }
  
  public QName getFaultMessage()
  {
    return faultMessage;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (paramObject == null) {
      return false;
    }
    if ((paramObject instanceof PolicyMapKey)) {
      return handler.areEqual(this, (PolicyMapKey)paramObject);
    }
    return false;
  }
  
  public int hashCode()
  {
    return handler.generateHashCode(this);
  }
  
  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer("PolicyMapKey(");
    localStringBuffer.append(service).append(", ").append(port).append(", ").append(operation).append(", ").append(faultMessage);
    return ")";
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\policy\PolicyMapKey.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */