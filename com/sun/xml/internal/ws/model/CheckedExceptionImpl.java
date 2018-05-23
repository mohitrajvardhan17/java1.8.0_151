package com.sun.xml.internal.ws.model;

import com.sun.xml.internal.bind.api.Bridge;
import com.sun.xml.internal.ws.addressing.WsaActionUtil;
import com.sun.xml.internal.ws.api.model.CheckedException;
import com.sun.xml.internal.ws.api.model.ExceptionType;
import com.sun.xml.internal.ws.api.model.JavaMethod;
import com.sun.xml.internal.ws.spi.db.TypeInfo;
import com.sun.xml.internal.ws.spi.db.XMLBridge;

public final class CheckedExceptionImpl
  implements CheckedException
{
  private final Class exceptionClass;
  private final TypeInfo detail;
  private final ExceptionType exceptionType;
  private final JavaMethodImpl javaMethod;
  private String messageName;
  private String faultAction = "";
  
  public CheckedExceptionImpl(JavaMethodImpl paramJavaMethodImpl, Class paramClass, TypeInfo paramTypeInfo, ExceptionType paramExceptionType)
  {
    detail = paramTypeInfo;
    exceptionType = paramExceptionType;
    exceptionClass = paramClass;
    javaMethod = paramJavaMethodImpl;
  }
  
  public AbstractSEIModelImpl getOwner()
  {
    return javaMethod.owner;
  }
  
  public JavaMethod getParent()
  {
    return javaMethod;
  }
  
  public Class getExceptionClass()
  {
    return exceptionClass;
  }
  
  public Class getDetailBean()
  {
    return (Class)detail.type;
  }
  
  /**
   * @deprecated
   */
  public Bridge getBridge()
  {
    return null;
  }
  
  public XMLBridge getBond()
  {
    return getOwner().getXMLBridge(detail);
  }
  
  public TypeInfo getDetailType()
  {
    return detail;
  }
  
  public ExceptionType getExceptionType()
  {
    return exceptionType;
  }
  
  public String getMessageName()
  {
    return messageName;
  }
  
  public void setMessageName(String paramString)
  {
    messageName = paramString;
  }
  
  public String getFaultAction()
  {
    return faultAction;
  }
  
  public void setFaultAction(String paramString)
  {
    faultAction = paramString;
  }
  
  public String getDefaultFaultAction()
  {
    return WsaActionUtil.getDefaultFaultAction(javaMethod, this);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\model\CheckedExceptionImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */