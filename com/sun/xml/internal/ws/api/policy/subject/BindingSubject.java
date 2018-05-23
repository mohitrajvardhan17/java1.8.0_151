package com.sun.xml.internal.ws.api.policy.subject;

import com.sun.istack.internal.logging.Logger;
import com.sun.xml.internal.ws.resources.BindingApiMessages;
import javax.xml.namespace.QName;

public class BindingSubject
{
  private static final Logger LOGGER = Logger.getLogger(BindingSubject.class);
  private final QName name;
  private final WsdlMessageType messageType;
  private final WsdlNameScope nameScope;
  private final BindingSubject parent;
  
  BindingSubject(QName paramQName, WsdlNameScope paramWsdlNameScope, BindingSubject paramBindingSubject)
  {
    this(paramQName, WsdlMessageType.NO_MESSAGE, paramWsdlNameScope, paramBindingSubject);
  }
  
  BindingSubject(QName paramQName, WsdlMessageType paramWsdlMessageType, WsdlNameScope paramWsdlNameScope, BindingSubject paramBindingSubject)
  {
    name = paramQName;
    messageType = paramWsdlMessageType;
    nameScope = paramWsdlNameScope;
    parent = paramBindingSubject;
  }
  
  public static BindingSubject createBindingSubject(QName paramQName)
  {
    return new BindingSubject(paramQName, WsdlNameScope.ENDPOINT, null);
  }
  
  public static BindingSubject createOperationSubject(QName paramQName1, QName paramQName2)
  {
    BindingSubject localBindingSubject = createBindingSubject(paramQName1);
    return new BindingSubject(paramQName2, WsdlNameScope.OPERATION, localBindingSubject);
  }
  
  public static BindingSubject createInputMessageSubject(QName paramQName1, QName paramQName2, QName paramQName3)
  {
    BindingSubject localBindingSubject = createOperationSubject(paramQName1, paramQName2);
    return new BindingSubject(paramQName3, WsdlMessageType.INPUT, WsdlNameScope.MESSAGE, localBindingSubject);
  }
  
  public static BindingSubject createOutputMessageSubject(QName paramQName1, QName paramQName2, QName paramQName3)
  {
    BindingSubject localBindingSubject = createOperationSubject(paramQName1, paramQName2);
    return new BindingSubject(paramQName3, WsdlMessageType.OUTPUT, WsdlNameScope.MESSAGE, localBindingSubject);
  }
  
  public static BindingSubject createFaultMessageSubject(QName paramQName1, QName paramQName2, QName paramQName3)
  {
    if (paramQName3 == null) {
      throw ((IllegalArgumentException)LOGGER.logSevereException(new IllegalArgumentException(BindingApiMessages.BINDING_API_NO_FAULT_MESSAGE_NAME())));
    }
    BindingSubject localBindingSubject = createOperationSubject(paramQName1, paramQName2);
    return new BindingSubject(paramQName3, WsdlMessageType.FAULT, WsdlNameScope.MESSAGE, localBindingSubject);
  }
  
  public QName getName()
  {
    return name;
  }
  
  public BindingSubject getParent()
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
  
  public boolean isOperationSubject()
  {
    if ((nameScope == WsdlNameScope.OPERATION) && (parent != null)) {
      return parent.isBindingSubject();
    }
    return false;
  }
  
  public boolean isMessageSubject()
  {
    if ((nameScope == WsdlNameScope.MESSAGE) && (parent != null)) {
      return parent.isOperationSubject();
    }
    return false;
  }
  
  public boolean isInputMessageSubject()
  {
    return (isMessageSubject()) && (messageType == WsdlMessageType.INPUT);
  }
  
  public boolean isOutputMessageSubject()
  {
    return (isMessageSubject()) && (messageType == WsdlMessageType.OUTPUT);
  }
  
  public boolean isFaultMessageSubject()
  {
    return (isMessageSubject()) && (messageType == WsdlMessageType.FAULT);
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if ((paramObject == null) || (!(paramObject instanceof BindingSubject))) {
      return false;
    }
    BindingSubject localBindingSubject = (BindingSubject)paramObject;
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
    i = 29 * i + (name == null ? 0 : name.hashCode());
    i = 29 * i + messageType.hashCode();
    i = 29 * i + nameScope.hashCode();
    i = 29 * i + (parent == null ? 0 : parent.hashCode());
    return i;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder("BindingSubject[");
    localStringBuilder.append(name).append(", ").append(messageType);
    localStringBuilder.append(", ").append(nameScope).append(", ").append(parent);
    return "]";
  }
  
  private static enum WsdlMessageType
  {
    NO_MESSAGE,  INPUT,  OUTPUT,  FAULT;
    
    private WsdlMessageType() {}
  }
  
  private static enum WsdlNameScope
  {
    SERVICE,  ENDPOINT,  OPERATION,  MESSAGE;
    
    private WsdlNameScope() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\policy\subject\BindingSubject.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */