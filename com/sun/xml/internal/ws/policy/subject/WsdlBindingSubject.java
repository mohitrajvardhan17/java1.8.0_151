package com.sun.xml.internal.ws.policy.subject;

import com.sun.xml.internal.ws.policy.privateutil.LocalizationMessages;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;
import javax.xml.namespace.QName;

public class WsdlBindingSubject
{
  private static final PolicyLogger LOGGER = PolicyLogger.getLogger(WsdlBindingSubject.class);
  private final QName name;
  private final WsdlMessageType messageType;
  private final WsdlNameScope nameScope;
  private final WsdlBindingSubject parent;
  
  WsdlBindingSubject(QName paramQName, WsdlNameScope paramWsdlNameScope, WsdlBindingSubject paramWsdlBindingSubject)
  {
    this(paramQName, WsdlMessageType.NO_MESSAGE, paramWsdlNameScope, paramWsdlBindingSubject);
  }
  
  WsdlBindingSubject(QName paramQName, WsdlMessageType paramWsdlMessageType, WsdlNameScope paramWsdlNameScope, WsdlBindingSubject paramWsdlBindingSubject)
  {
    name = paramQName;
    messageType = paramWsdlMessageType;
    nameScope = paramWsdlNameScope;
    parent = paramWsdlBindingSubject;
  }
  
  public static WsdlBindingSubject createBindingSubject(QName paramQName)
  {
    return new WsdlBindingSubject(paramQName, WsdlNameScope.ENDPOINT, null);
  }
  
  public static WsdlBindingSubject createBindingOperationSubject(QName paramQName1, QName paramQName2)
  {
    WsdlBindingSubject localWsdlBindingSubject = createBindingSubject(paramQName1);
    return new WsdlBindingSubject(paramQName2, WsdlNameScope.OPERATION, localWsdlBindingSubject);
  }
  
  public static WsdlBindingSubject createBindingMessageSubject(QName paramQName1, QName paramQName2, QName paramQName3, WsdlMessageType paramWsdlMessageType)
  {
    if (paramWsdlMessageType == null) {
      throw ((IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0083_MESSAGE_TYPE_NULL())));
    }
    if (paramWsdlMessageType == WsdlMessageType.NO_MESSAGE) {
      throw ((IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0084_MESSAGE_TYPE_NO_MESSAGE())));
    }
    if ((paramWsdlMessageType == WsdlMessageType.FAULT) && (paramQName3 == null)) {
      throw ((IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0085_MESSAGE_FAULT_NO_NAME())));
    }
    WsdlBindingSubject localWsdlBindingSubject = createBindingOperationSubject(paramQName1, paramQName2);
    return new WsdlBindingSubject(paramQName3, paramWsdlMessageType, WsdlNameScope.MESSAGE, localWsdlBindingSubject);
  }
  
  public QName getName()
  {
    return name;
  }
  
  public WsdlMessageType getMessageType()
  {
    return messageType;
  }
  
  public WsdlBindingSubject getParent()
  {
    return parent;
  }
  
  public boolean isBindingSubject()
  {
    if (nameScope == WsdlNameScope.ENDPOINT) {
      return parent == null;
    }
    return false;
  }
  
  public boolean isBindingOperationSubject()
  {
    if ((nameScope == WsdlNameScope.OPERATION) && (parent != null)) {
      return parent.isBindingSubject();
    }
    return false;
  }
  
  public boolean isBindingMessageSubject()
  {
    if ((nameScope == WsdlNameScope.MESSAGE) && (parent != null)) {
      return parent.isBindingOperationSubject();
    }
    return false;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if ((paramObject == null) || (!(paramObject instanceof WsdlBindingSubject))) {
      return false;
    }
    WsdlBindingSubject localWsdlBindingSubject = (WsdlBindingSubject)paramObject;
    boolean bool = true;
    bool = (bool) && (name == null ? name == null : name.equals(name));
    bool = (bool) && (messageType.equals(messageType));
    bool = (bool) && (nameScope.equals(nameScope));
    bool = (bool) && (parent == null ? parent == null : parent.equals(parent));
    return bool;
  }
  
  public int hashCode()
  {
    int i = 23;
    i = 31 * i + (name == null ? 0 : name.hashCode());
    i = 31 * i + messageType.hashCode();
    i = 31 * i + nameScope.hashCode();
    i = 31 * i + (parent == null ? 0 : parent.hashCode());
    return i;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder("WsdlBindingSubject[");
    localStringBuilder.append(name).append(", ").append(messageType);
    localStringBuilder.append(", ").append(nameScope).append(", ").append(parent);
    return "]";
  }
  
  public static enum WsdlMessageType
  {
    NO_MESSAGE,  INPUT,  OUTPUT,  FAULT;
    
    private WsdlMessageType() {}
  }
  
  public static enum WsdlNameScope
  {
    SERVICE,  ENDPOINT,  OPERATION,  MESSAGE;
    
    private WsdlNameScope() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\policy\subject\WsdlBindingSubject.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */