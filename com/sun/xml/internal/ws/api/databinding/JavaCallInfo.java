package com.sun.xml.internal.ws.api.databinding;

import java.lang.reflect.Method;

public class JavaCallInfo
  implements com.oracle.webservices.internal.api.databinding.JavaCallInfo
{
  private Method method;
  private Object[] parameters;
  private Object returnValue;
  private Throwable exception;
  
  public JavaCallInfo() {}
  
  public JavaCallInfo(Method paramMethod, Object[] paramArrayOfObject)
  {
    method = paramMethod;
    parameters = paramArrayOfObject;
  }
  
  public Method getMethod()
  {
    return method;
  }
  
  public void setMethod(Method paramMethod)
  {
    method = paramMethod;
  }
  
  public Object[] getParameters()
  {
    return parameters;
  }
  
  public void setParameters(Object[] paramArrayOfObject)
  {
    parameters = paramArrayOfObject;
  }
  
  public Object getReturnValue()
  {
    return returnValue;
  }
  
  public void setReturnValue(Object paramObject)
  {
    returnValue = paramObject;
  }
  
  public Throwable getException()
  {
    return exception;
  }
  
  public void setException(Throwable paramThrowable)
  {
    exception = paramThrowable;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\databinding\JavaCallInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */